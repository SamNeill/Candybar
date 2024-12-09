package candybar.lib.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

import com.amazon.device.iap.PurchasingListener;
import com.amazon.device.iap.PurchasingService;
import com.amazon.device.iap.model.ProductDataResponse;
import com.amazon.device.iap.model.PurchaseResponse;
import com.amazon.device.iap.model.PurchaseUpdatesResponse;
import com.amazon.device.iap.model.Receipt;
import com.amazon.device.iap.model.UserDataResponse;
import com.amazon.device.iap.model.ProductType;
import com.amazon.device.iap.model.Product;
import com.danimahardhika.android.helpers.core.utils.LogUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import candybar.lib.items.InAppBilling;
import candybar.lib.items.Purchase;
import candybar.lib.preferences.Preferences;

public class AmazonBillingProcessor implements BillingProcessor, PurchasingListener {
    private static final String TAG = "AmazonBillingProcessor";
    private final Context context;
    private QueryProductsCallback productsCallback;
    private QueryPurchasesCallback purchasesCallback;
    private ConsumeCallback consumeCallback;
    private AcknowledgeCallback acknowledgeCallback;
    
    public AmazonBillingProcessor(Context context) {
        this.context = context;
        PurchasingService.registerListener(context, this);
        
        // Enable Amazon IAP service
        try {
            // Get the public key from the app's License class
            Class<?> licenseClass = Class.forName("com.candybar.dev.licenses.License");
            java.lang.reflect.Method getKeyMethod = licenseClass.getMethod("getAmazonPublicKey");
            String publicKey = (String) getKeyMethod.invoke(null);
            
            // Enable Amazon IAP service
            context.getPackageManager().setApplicationEnabledSetting(
                "com.amazon.venezia",
                android.content.pm.PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                0);
        } catch (Exception e) {
            Log.e(TAG, "Error setting up Amazon IAP: " + e.getMessage());
        }
    }

    @Override
    public void init() {
        PurchasingService.getUserData();
    }

    @Override
    public void destroy() {
        // Nothing to clean up
    }

    @Override
    public void queryProducts(List<String> productIds, QueryProductsCallback callback) {
        this.productsCallback = callback;
        Set<String> skuSet = new HashSet<>(productIds);
        PurchasingService.getProductData(skuSet);
    }

    @Override
    public void launchBillingFlow(Activity activity, String productId) {
        PurchasingService.purchase(productId);
    }

    @Override
    public void consumePurchase(String purchaseToken, ConsumeCallback callback) {
        // Amazon handles consumables automatically
        if (callback != null) {
            callback.onSuccess();
        }
    }

    @Override
    public void acknowledgePurchase(String purchaseToken, AcknowledgeCallback callback) {
        // Amazon handles acknowledgment automatically
        if (callback != null) {
            callback.onSuccess();
        }
    }

    @Override
    public void queryPurchases(QueryPurchasesCallback callback) {
        this.purchasesCallback = callback;
        PurchasingService.getPurchaseUpdates(true);
    }

    @Override
    public void onProductDataResponse(ProductDataResponse response) {
        switch (response.getRequestStatus()) {
            case SUCCESSFUL:
                if (productsCallback != null) {
                    Map<String, Product> productData = response.getProductData();
                    List<String> products = new ArrayList<>();
                    for (Product product : productData.values()) {
                        String sku = product.getSku();
                        
                        // Check if product is in License settings
                        if (Preferences.get(context).getInAppBillingType() == InAppBilling.DONATE) {
                            String[] donationIds = License.getDonationProductsId();
                            if (Arrays.asList(donationIds).contains(sku)) {
                                products.add(sku);
                            }
                        } else {
                            String[] premiumIds = License.getPremiumRequestProductsId();
                            if (Arrays.asList(premiumIds).contains(sku)) {
                                products.add(sku);
                            }
                        }
                    }
                    productsCallback.onSuccess(products);
                }
                break;
            
            case FAILED:
            case NOT_SUPPORTED:
                if (productsCallback != null) {
                    productsCallback.onError("Failed to get product data");
                }
                break;
        }
    }

    @Override
    public void onPurchaseResponse(PurchaseResponse response) {
        switch (response.getRequestStatus()) {
            case SUCCESSFUL:
                Receipt receipt = response.getReceipt();
                if (purchasesCallback != null) {
                    Purchase purchase = new Purchase.Builder()
                        .setOrderId(receipt.getReceiptId())
                        .setPurchaseToken(receipt.getReceiptId())
                        .setPurchaseTime(receipt.getPurchaseDate().getTime())
                        .setProducts(List.of(receipt.getSku()))
                        .setAcknowledged(true)
                        .setPurchaseState(1) // 1 for PURCHASED
                        .build();
                        
                    purchasesCallback.onSuccess(List.of(purchase));
                }
                break;
            case FAILED:
            case INVALID_SKU:
                if (purchasesCallback != null) {
                    purchasesCallback.onError("Purchase failed");
                }
                // Reset billing type on failure
                Preferences.get(context).setInAppBillingType(-1);
                break;
        }
    }

    @Override
    public void onPurchaseUpdatesResponse(PurchaseUpdatesResponse response) {
        switch (response.getRequestStatus()) {
            case SUCCESSFUL:
                if (purchasesCallback != null) {
                    List<Purchase> purchases = new ArrayList<>();
                    for (Receipt receipt : response.getReceipts()) {
                        Purchase purchase = new Purchase.Builder()
                            .setOrderId(receipt.getReceiptId())
                            .setPurchaseToken(receipt.getReceiptId())
                            .setPurchaseTime(receipt.getPurchaseDate().getTime())
                            .setProducts(List.of(receipt.getSku()))
                            .setAcknowledged(true)
                            .setPurchaseState(1)
                            .build();
                        purchases.add(purchase);
                        
                        // Update premium status if we find an entitled purchase
                        if (receipt.getProductType() == ProductType.ENTITLED) {
                            Preferences.get(context).setPremiumRequest(true);
                            Preferences.get(context).setPremiumRequestProductId(receipt.getSku());
                        }
                    }
                    purchasesCallback.onSuccess(purchases);
                }
                break;
            case FAILED:
                if (purchasesCallback != null) {
                    purchasesCallback.onError("Failed to get purchases");
                }
                break;
        }
    }

    @Override
    public void onUserDataResponse(UserDataResponse response) {
        switch (response.getRequestStatus()) {
            case SUCCESSFUL:
                // User data successfully retrieved
                break;
            case NOT_SUPPORTED:
            case FAILED:
                Log.e(TAG, "Failed to get user data");
                break;
        }
    }
} 
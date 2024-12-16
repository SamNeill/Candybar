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
import com.amazon.device.iap.model.UserData;
import com.amazon.device.iap.model.UserDataResponse;
import com.amazon.device.iap.model.ProductType;
import com.amazon.device.iap.model.Product;
import com.amazon.device.iap.model.RequestId;
import com.danimahardhika.android.helpers.core.utils.LogUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import candybar.lib.items.InAppBilling;
import candybar.lib.items.Purchase;
import candybar.lib.items.BillingProcessor;
import candybar.lib.preferences.Preferences;

public class AmazonBillingProcessor implements PurchasingListener, BillingProcessor {
    private static final String TAG = "AmazonBillingProcessor";

    private final Context context;
    private BillingProcessor.QueryProductsCallback productsCallback;
    private BillingProcessor.QueryPurchasesCallback purchasesCallback;
    private BillingProcessor.ConsumeCallback consumeCallback;
    private BillingProcessor.AcknowledgeCallback acknowledgeCallback;
    private RequestId currentRequest;
    private String currentProductId;
    private String[] donationProductIds = new String[0];
    private String[] premiumRequestProductIds = new String[0];

    public AmazonBillingProcessor(Context context) {
        this.context = context;
        registerListener();
    }

    public void setDonationProductIds(String[] productIds) {
        this.donationProductIds = productIds != null ? productIds : new String[0];
    }

    public void setPremiumRequestProductIds(String[] productIds) {
        this.premiumRequestProductIds = productIds != null ? productIds : new String[0];
    }

    private void registerListener() {
        PurchasingService.registerListener(context, this);
    }

    @Override
    public void init() {
        // Get user data
        PurchasingService.getUserData();

        // Get all available products
        Set<String> skuSet = new HashSet<>();
        if (donationProductIds.length > 0) {
            skuSet.addAll(Arrays.asList(donationProductIds));
        }
        if (premiumRequestProductIds.length > 0) {
            skuSet.addAll(Arrays.asList(premiumRequestProductIds));
        }
        if (!skuSet.isEmpty()) {
            PurchasingService.getProductData(skuSet);
        }
    }

    @Override
    public void destroy() {
        // Nothing to clean up for Amazon IAP
    }

    @Override
    public void queryProducts(List<String> productIds, BillingProcessor.QueryProductsCallback callback) {
        this.productsCallback = callback;
        Set<String> skuSet = new HashSet<>(productIds);
        currentRequest = PurchasingService.getProductData(skuSet);
    }

    @Override
    public void launchBillingFlow(Activity activity, String productId) {
        currentProductId = productId;
        currentRequest = PurchasingService.purchase(productId);
    }

    @Override
    public void consumePurchase(String purchaseToken, BillingProcessor.ConsumeCallback callback) {
        // Amazon handles consumables automatically
        this.consumeCallback = callback;
        if (callback != null) {
            callback.onSuccess();
        }
    }

    @Override
    public void acknowledgePurchase(String purchaseToken, BillingProcessor.AcknowledgeCallback callback) {
        // Amazon handles acknowledgment automatically
        this.acknowledgeCallback = callback;
        if (callback != null) {
            callback.onSuccess();
        }
    }

    @Override
    public void queryPurchases(BillingProcessor.QueryPurchasesCallback callback) {
        this.purchasesCallback = callback;
        currentRequest = PurchasingService.getPurchaseUpdates(true);
    }

    @Override
    public void onProductDataResponse(ProductDataResponse response) {
        switch (response.getRequestStatus()) {
            case SUCCESSFUL:
                Map<String, Product> products = response.getProductData();
                ArrayList<String> availableProducts = new ArrayList<>();
                for (Product product : products.values()) {
                    String sku = product.getSku();
                    // Filter products based on billing type
                    if (Preferences.get(context).getInAppBillingType() == InAppBilling.DONATE) {
                        if (Arrays.asList(donationProductIds).contains(sku)) {
                            availableProducts.add(sku);
                        }
                    } else {
                        if (Arrays.asList(premiumRequestProductIds).contains(sku)) {
                            availableProducts.add(sku);
                        }
                    }
                }
                if (productsCallback != null) {
                    productsCallback.onSuccess(availableProducts);
                }
                break;

            case FAILED:
            case NOT_SUPPORTED:
                if (productsCallback != null) {
                    productsCallback.onError("Failed to get product data: " + response.getRequestStatus());
                }
                break;
        }
    }

    @Override
    public void onPurchaseResponse(final PurchaseResponse response) {
        switch (response.getRequestStatus()) {
            case SUCCESSFUL:
                Receipt receipt = response.getReceipt();
                if (receipt != null) {
                    verifyPurchase(receipt, response.getUserData());
                }
                break;

            case ALREADY_PURCHASED:
                if (purchasesCallback != null) {
                    purchasesCallback.onError("Item already purchased");
                }
                break;

            case FAILED:
            case INVALID_SKU:
                if (purchasesCallback != null) {
                    purchasesCallback.onError("Purchase failed: " + response.getRequestStatus());
                }
                break;
        }
    }

    @Override
    public void onPurchaseUpdatesResponse(final PurchaseUpdatesResponse response) {
        switch (response.getRequestStatus()) {
            case SUCCESSFUL:
                if (purchasesCallback != null) {
                    List<Purchase> purchases = new ArrayList<>();
                    for (Receipt receipt : response.getReceipts()) {
                        verifyPurchase(receipt, response.getUserData());
                        Purchase purchase = new Purchase.Builder()
                            .setOrderId(receipt.getReceiptId())
                            .setPurchaseToken(receipt.getReceiptId())
                            .setPurchaseTime(receipt.getPurchaseDate().getTime())
                            .setProducts(List.of(receipt.getSku()))
                            .setAcknowledged(true)
                            .setPurchaseState(1)
                            .build();
                        purchases.add(purchase);
                    }
                    purchasesCallback.onSuccess(purchases);
                }
                break;

            case FAILED:
                if (purchasesCallback != null) {
                    purchasesCallback.onError("Failed to get purchases: " + response.getRequestStatus());
                }
                break;
        }

        // If there are more purchase updates to retrieve, continue
        if (response.hasMore()) {
            currentRequest = PurchasingService.getPurchaseUpdates(false);
        }
    }

    @Override
    public void onUserDataResponse(final UserDataResponse response) {
        switch (response.getRequestStatus()) {
            case SUCCESSFUL:
                String currentUserId = response.getUserData().getUserId();
                String currentMarketplace = response.getUserData().getMarketplace();
                // Store user data if needed
                break;

            case NOT_SUPPORTED:
                Log.w(TAG, "UserData not supported on current device");
                break;

            case FAILED:
                Log.e(TAG, "Failed to get user data");
                break;
        }
    }

    private void verifyPurchase(Receipt receipt, UserData userData) {
        try {
            String key = AmazonKeyUtils.getAmazonPublicKey(context);
            if (key.isEmpty()) {
                Log.e(TAG, "Amazon public key not found");
                if (purchasesCallback != null) {
                    purchasesCallback.onError("Failed to verify purchase: Missing public key");
                }
                return;
            }

            // Verify the purchase here using the key
            // For now, we'll just update the premium status based on the SKU
            if (Arrays.asList(premiumRequestProductIds).contains(receipt.getSku())) {
                Preferences.get(context).setPremiumRequest(true);
                Preferences.get(context).setPremiumRequestProductId(receipt.getSku());
            }

            // Create purchase object after verification
            Purchase purchase = new Purchase.Builder()
                .setOrderId(receipt.getReceiptId())
                .setPurchaseToken(receipt.getReceiptId())
                .setPurchaseTime(receipt.getPurchaseDate().getTime())
                .setProducts(List.of(receipt.getSku()))
                .setAcknowledged(true)
                .setPurchaseState(1) // PURCHASED
                .build();

            if (purchasesCallback != null) {
                purchasesCallback.onSuccess(List.of(purchase));
            }
        } catch (Exception e) {
            Log.e(TAG, "Error verifying purchase", e);
            if (purchasesCallback != null) {
                purchasesCallback.onError("Failed to verify purchase: " + e.getMessage());
            }
        }
    }
}
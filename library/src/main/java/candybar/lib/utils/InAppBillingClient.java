package candybar.lib.utils;

import android.content.Context;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.danimahardhika.android.helpers.core.utils.LogUtil;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;

import candybar.lib.applications.CandyBarApplication;
import candybar.lib.items.InAppBilling;
import candybar.lib.items.Purchase;
import candybar.lib.items.BillingResult;
import candybar.lib.preferences.Preferences;
import candybar.lib.utils.listeners.InAppBillingListener;
import candybar.lib.utils.AmazonBillingProcessor;
import candybar.lib.utils.License;

public class InAppBillingClient {
    private final Context mContext;
    private final BillingProcessor billingProcessor;
    private boolean mIsInitialized = false;

    private static WeakReference<InAppBillingClient> mInAppBilling;

    public static final String INAPP_TYPE = "inapp";
    public static final QueryParams INAPP_PARAMS = new QueryParams(INAPP_TYPE);

    public static class QueryParams {
        private final String type;

        public QueryParams(String type) {
            this.type = type;
        }

        public String getType() {
            return type;
        }
    }

    private InAppBillingClient(Context context) {
        mContext = context;
        
        // Only initialize billing if enabled
        if (License.isInAppBillingEnabled()) {
            billingProcessor = new AmazonBillingProcessor(context);
            
            // Initialize with License settings
            if (License.isLicenseCheckerEnabled()) {
                String[] premiumRequestProductsId = License.getPremiumRequestProductsId();
                int[] premiumRequestProductsCount = License.getPremiumRequestProductsCount();
                String[] donationProductsId = License.getDonationProductsId();
            }
        } else {
            billingProcessor = null;
        }
    }

    public static InAppBillingClient get(@NonNull Context context) {
        if (mInAppBilling == null || mInAppBilling.get() == null) {
            mInAppBilling = new WeakReference<>(new InAppBillingClient(context));
        }
        return mInAppBilling.get();
    }

    public void init() {
        if (billingProcessor != null) {
            billingProcessor.init();
        }
    }

    public void destroy() {
        if (billingProcessor != null) {
            billingProcessor.destroy();
        }
        mIsInitialized = false;
    }

    public BillingProcessor getProcessor() {
        return billingProcessor;
    }

    public void checkForUnprocessedPurchases() {
        if (billingProcessor != null) {
            billingProcessor.queryPurchases(new BillingProcessor.QueryPurchasesCallback() {
                @Override
                public void onSuccess(List<Purchase> purchases) {
                    if (purchases != null && !purchases.isEmpty()) {
                        try {
                            ((InAppBillingListener) mContext).onProcessPurchase(purchases.get(0));
                            for (Purchase purchase : purchases) {
                                CandyBarApplication.getConfiguration().getAnalyticsHandler().logEvent(
                                        "purchase",
                                        new HashMap<String, Object>() {{
                                            put("order_id", purchase.getOrderId());
                                            put("order_timestamp", purchase.getPurchaseTime());
                                            put("order_status", purchase.getPurchaseState());
                                            put("products", String.join(",", purchase.getProducts()));
                                            put("token", purchase.getPurchaseToken());
                                        }}
                                );
                            }
                        } catch (Exception ignored) {
                        }
                    }
                }

                @Override
                public void onError(String error) {
                    LogUtil.e("Failed to query purchases: " + error);
                    if (error.equals("SERVICE_DISCONNECTED")) {
                        mIsInitialized = false;
                    }

                    if (Preferences.get(mContext).getInAppBillingType() == InAppBilling.PREMIUM_REQUEST) {
                        Preferences.get(mContext).setPremiumRequestCount(0);
                        Preferences.get(mContext).setPremiumRequestTotal(0);
                    }
                    Preferences.get(mContext).setInAppBillingType(-1);
                }
            });
        }
    }

    public void queryPurchasesAsync(QueryParams params, PurchaseQueryCallback callback) {
        if (billingProcessor != null) {
            billingProcessor.queryPurchases(new BillingProcessor.QueryPurchasesCallback() {
                @Override
                public void onSuccess(List<Purchase> purchases) {
                    callback.onResult(new BillingResult.Builder()
                            .setResponseCode(BillingResult.OK)
                            .build(), purchases);
                }

                @Override
                public void onError(String error) {
                    callback.onResult(new BillingResult.Builder()
                            .setResponseCode(BillingResult.ERROR)
                            .setDebugMessage(error)
                            .build(), null);
                }
            });
        }
    }

    public interface PurchaseQueryCallback {
        void onResult(BillingResult billingResult, List<Purchase> purchases);
    }
}

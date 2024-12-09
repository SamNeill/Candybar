package candybar.lib.utils;

import android.app.Activity;
import android.content.Context;

import java.util.List;

import candybar.lib.items.Purchase;

public interface BillingProcessor {
    void init();
    void destroy();
    void queryProducts(List<String> productIds, QueryProductsCallback callback);
    void launchBillingFlow(Activity activity, String productId);
    void consumePurchase(String purchaseToken, ConsumeCallback callback);
    void acknowledgePurchase(String purchaseToken, AcknowledgeCallback callback);
    void queryPurchases(QueryPurchasesCallback callback);
    
    interface QueryProductsCallback {
        void onSuccess(List<String> products);
        void onError(String error);
    }
    
    interface ConsumeCallback {
        void onSuccess();
        void onError(String error); 
    }
    
    interface AcknowledgeCallback {
        void onSuccess();
        void onError(String error);
    }
    
    interface QueryPurchasesCallback {
        void onSuccess(List<Purchase> purchases);
        void onError(String error);
    }
} 
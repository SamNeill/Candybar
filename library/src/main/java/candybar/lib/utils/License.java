package candybar.lib.utils;

import android.content.Context;
import android.content.res.Resources;

public class License {
    /*
     * License Checker
     * Enable or disable license checker
     * Set license key for premium apps from Google Play Console
     */
    private static final boolean LICENSE_CHECKER_ENABLED = true;
    private static final String LICENSE_KEY = "YOUR_LICENSE_KEY";

    /*
     * Amazon In-App Purchase Key
     */
    private static final String AMAZON_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA";

    /*
     * In-App Billing Preferences
     */
    private static final boolean ENABLE_IN_APP_BILLING = true;
    private static final boolean ENABLE_PREMIUM_REQUEST = true;
    private static final boolean ENABLE_RESTORE_PURCHASES = false;
    private static final boolean ENABLE_DONATION = true;
    private static final int PREMIUM_REQUEST_LIMIT = 5;
    private static final boolean RESET_PREMIUM_REQUEST_LIMIT = true;

    private static Context mContext;

    public static void init(Context context) {
        mContext = context.getApplicationContext();
    }

    public static String[] getPremiumRequestProductsId() {
        if (mContext == null) return new String[0];
        Resources res = mContext.getResources();
        int arrayId = res.getIdentifier("premium_request_products", "array", mContext.getPackageName());
        return arrayId != 0 ? res.getStringArray(arrayId) : new String[0];
    }

    public static int[] getPremiumRequestProductsCount() {
        if (mContext == null) return new int[0];
        Resources res = mContext.getResources();
        int arrayId = res.getIdentifier("premium_request_counts", "array", mContext.getPackageName());
        return arrayId != 0 ? res.getIntArray(arrayId) : new int[0];
    }

    public static String[] getDonationProductsId() {
        if (mContext == null) return new String[0];
        Resources res = mContext.getResources();
        int arrayId = res.getIdentifier("donation_products", "array", mContext.getPackageName());
        return arrayId != 0 ? res.getStringArray(arrayId) : new String[0];
    }

    public static String getLicenseKey() {
        return LICENSE_KEY;
    }

    public static String getAmazonKey() {
        return AMAZON_KEY;
    }

    public static boolean isLicenseCheckerEnabled() {
        return LICENSE_CHECKER_ENABLED;
    }

    // In-App Billing Preferences Getters
    public static boolean isInAppBillingEnabled() {
        return ENABLE_IN_APP_BILLING;
    }

    public static boolean isPremiumRequestEnabled() {
        return ENABLE_PREMIUM_REQUEST;
    }

    public static boolean isRestorePurchasesEnabled() {
        return ENABLE_RESTORE_PURCHASES;
    }

    public static boolean isDonationEnabled() {
        return ENABLE_DONATION;
    }

    public static int getPremiumRequestLimit() {
        return PREMIUM_REQUEST_LIMIT;
    }

    public static boolean isResetPremiumRequestLimit() {
        return RESET_PREMIUM_REQUEST_LIMIT;
    }
}
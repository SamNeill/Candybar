package com.candybar.dev.licenses;

public class License {
    /*
     * License Checker
     * Enable or disable license checker
     * Set license key for premium apps from Amazon Developer Console
     */
    private static final boolean LICENSE_CHECKER_ENABLED = false;  // Set to true if you want to enable license checker
    private static final String LICENSE_KEY = "YOUR_AMAZON_LICENSE_KEY"; // Your Amazon license key

    /*
     * Amazon In-App Purchase Public Key
     * Get this key from Amazon Developer Console -> Your App -> In-App Items -> Security Profile
     */
    private static final String AMAZON_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA"; // Replace with your actual key

    private static final byte[] RANDOM_STRING = new byte[] {
        // Your random string bytes for license checking
        (byte) 0x28, (byte) 0xF0, (byte) 0x2D, (byte) 0x91, (byte) 0xA4, (byte) 0x8F
    };

    /*
     * Premium Request
     * Set premium request products
     * Product ID from Amazon Developer Console
     */
    private static final String[] PREMIUM_REQUEST_PRODUCTS = {
        "1_20_icon_request",  // 20 icons for $1.99
        "1_30_icon_request",  // 30 icons for $2.99  
        "1_40_icon_request",  // 40 icons for $3.99
        "1_50_icon_request"   // 50 icons for $4.99
    };

    private static final int[] PREMIUM_REQUEST_COUNTS = {
        20,  // For 1_20_icon_request
        30,  // For 1_30_icon_request
        40,  // For 1_40_icon_request
        50   // For 1_50_icon_request
    };

    /*
     * Donation
     * Set donation products
     * Product ID from Amazon Developer Console 
     */
    private static final String[] DONATION_PRODUCTS = {
        "coffee",      // $0.99 - Buy me a coffee
        "breakfast",   // $1.99 - Buy me breakfast
        "lunch",       // $4.99 - Buy me lunch
        "dinner"       // $9.99 - Buy me dinner
    };

    /*
     * In-App Billing Preferences
     */
    private static final boolean ENABLE_IN_APP_BILLING = true;     // Enable/disable in-app purchases
    private static final boolean ENABLE_PREMIUM_REQUEST = true;    // Enable/disable premium requests
    private static final boolean ENABLE_RESTORE_PURCHASES = false; // Enable/disable restore purchases button
    private static final boolean ENABLE_DONATION = true;          // Enable/disable donation
    private static final int PREMIUM_REQUEST_LIMIT = 5;           // Free request limit
    private static final boolean RESET_PREMIUM_REQUEST_LIMIT = true; // Reset request limit on update

    public static String[] getPremiumRequestProductsId() {
        return PREMIUM_REQUEST_PRODUCTS;
    }

    public static int[] getPremiumRequestProductsCount() {
        return PREMIUM_REQUEST_COUNTS;
    }

    public static String[] getDonationProductsId() {
        return DONATION_PRODUCTS;
    }

    public static String getLicenseKey() {
        return LICENSE_KEY;
    }

    public static String getAmazonPublicKey() {
        return AMAZON_PUBLIC_KEY;
    }

    public static byte[] getRandomString() {
        return RANDOM_STRING;
    }

    public static boolean isLicenseCheckerEnabled() {
        return LICENSE_CHECKER_ENABLED;
    }

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

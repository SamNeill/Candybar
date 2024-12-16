package com.candybar.dev.licenses;

import android.content.Context;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

public class License {
    /*
     * Amazon In-App Purchase Public Key
     * The key is stored in assets/amazon_public_key.pem
     */
    private static String amazonPublicKey = null;

    /*
     * Premium Request
     * Set premium request products
     * Product ID from Amazon Developer Console
     * Format: premium_request_[count]
     */
    private static final String[] PREMIUM_REQUEST_PRODUCTS = {
        "premium_request_20",  // 20 icons for $1.99
        "premium_request_30",  // 30 icons for $2.99  
        "premium_request_40",  // 40 icons for $3.99
        "premium_request_50"   // 50 icons for $4.99
    };

    private static final int[] PREMIUM_REQUEST_COUNTS = {
        20,  // For premium_request_20
        30,  // For premium_request_30
        40,  // For premium_request_40
        50   // For premium_request_50
    };

    /*
     * Donation
     * Set donation products
     * Product ID from Amazon Developer Console
     * Format: donation_[type]
     */
    private static final String[] DONATION_PRODUCTS = {
        "donation_coffee",     // $0.99 - Buy me a coffee
        "donation_breakfast",  // $1.99 - Buy me breakfast
        "donation_lunch",      // $4.99 - Buy me lunch
        "donation_dinner"      // $9.99 - Buy me dinner
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

    public static boolean isLicenseCheckerEnabled() {
        // License checking is not used with Amazon IAP
        return false;
    }

    public static String getLicenseKey() {
        // Not used with Amazon IAP
        return "";
    }

    public static byte[] getRandomString() {
        // Not used with Amazon IAP
        return new byte[0];
    }

    public static String getAmazonPublicKey(Context context) {
        if (amazonPublicKey == null) {
            try {
                InputStream inputStream = context.getAssets().open("amazon_public_key.pem");
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder key = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    if (!line.contains("-----BEGIN PUBLIC KEY-----") && !line.contains("-----END PUBLIC KEY-----")) {
                        key.append(line);
                    }
                }
                reader.close();
                amazonPublicKey = key.toString();
            } catch (IOException e) {
                e.printStackTrace();
                return "";
            }
        }
        return amazonPublicKey;
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

    public static String[] getPremiumRequestProductsId() {
        return PREMIUM_REQUEST_PRODUCTS;
    }

    public static int[] getPremiumRequestProductsCount() {
        return PREMIUM_REQUEST_COUNTS;
    }

    public static String[] getDonationProductsId() {
        return DONATION_PRODUCTS;
    }
}

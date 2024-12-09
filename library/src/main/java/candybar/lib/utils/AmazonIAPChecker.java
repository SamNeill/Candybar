package candybar.lib.utils;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;

public class AmazonIAPChecker {
    
    public static boolean isAmazonIAPAvailable(Context context) {
        try {
            context.getPackageManager().getPackageInfo("com.amazon.venezia", 0);
            return true;
        } catch (NameNotFoundException e) {
            return false;
        }
    }
} 
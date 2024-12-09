package com.candybar.dev.utils;

import android.util.Log;
import java.lang.reflect.Method;

public class DashboardUtils {
    public static boolean safeCheckFeature(Object feature) {
        try {
            if (feature == null) return false;
            // Assuming the interface method is "a()"
            Method method = feature.getClass().getMethod("a");
            return (Boolean) method.invoke(feature);
        } catch (Exception e) {
            Log.e("DashboardUtils", "Error checking feature", e);
            return false;
        }
    }
} 
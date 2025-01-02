package com.candybar.dev.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class PrivacyPolicyLoader {
    private static final String TAG = "PrivacyPolicyLoader";
    private static final String APP_PRIVACY_PATH = "privacy_policy.html";
    private static final String LIB_PRIVACY_PATH = "candybar/privacy_policy.html";

    public static String loadPrivacyPolicy(Context context) {
        String privacyHtml = loadFromAppAssets(context);
        if (privacyHtml == null || privacyHtml.isEmpty()) {
            privacyHtml = loadFromLibraryAssets(context);
        }
        return privacyHtml != null ? privacyHtml : "";
    }

    private static String loadFromAppAssets(Context context) {
        return loadAssetFile(context, APP_PRIVACY_PATH);
    }

    private static String loadFromLibraryAssets(Context context) {
        return loadAssetFile(context, LIB_PRIVACY_PATH);
    }

    private static String loadAssetFile(Context context, String path) {
        AssetManager assetManager = context.getAssets();
        try (InputStream is = assetManager.open(path);
             BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line).append('\n');
            }
            return stringBuilder.toString();
        } catch (IOException e) {
            Log.d(TAG, "Could not load privacy policy from path: " + path);
            return null;
        }
    }
} 
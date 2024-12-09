package com.candybar.dev.activities;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;

import com.candybar.dev.BuildConfig;
import com.candybar.dev.licenses.License;
import candybar.lib.activities.CandyBarMainActivity;

public class MainActivity extends CandyBarMainActivity {

    @NonNull
    @Override
    public ActivityConfiguration onInit() {
        return new ActivityConfiguration()
                .setLicenseCheckerEnabled(License.isLicenseCheckerEnabled())
                .setLicenseKey(License.getLicenseKey())
                .setRandomString(License.getRandomString())
                .setDonationProductsId(License.getDonationProductsId())
                .setPremiumRequestProducts(License.getPremiumRequestProductsId(), License.getPremiumRequestProductsCount());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            Log.d("MainActivity", "Starting MainActivity onCreate");
            
            Log.d("MainActivity", "Dashboard initialization");
            
        } catch (Exception e) {
            Log.e("MainActivity", "Error in onCreate", e);
            throw e;
        }
    }
}

package candybar.lib.helpers;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;

import com.google.android.material.navigation.NavigationView;

import candybar.lib.R;
import candybar.lib.applications.CandyBarApplication;
import candybar.lib.items.Home;
import candybar.lib.preferences.Preferences;

/*
 * CandyBar - Material Dashboard
 *
 * Copyright (c) 2014-2016 Dani Mahardhika
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

public class NavigationViewHelper {

    public static void initApply(@NonNull NavigationView navigationView) {
        Context context = navigationView.getContext();
        MenuItem menuItem = navigationView.getMenu().findItem(R.id.navigation_view_apply);
        if (menuItem == null) return;

        if (context.getResources().getBoolean(R.bool.enable_apply)) {
            menuItem.setVisible(true);
        } else {
            menuItem.setVisible(false);
        }
    }

    public static void initIconRequest(@NonNull NavigationView navigationView) {
        Context context = navigationView.getContext();
        MenuItem menuItem = navigationView.getMenu().findItem(R.id.navigation_view_request);
        if (menuItem == null) return;

        if (context.getResources().getBoolean(R.bool.enable_icon_request)) {
            menuItem.setVisible(true);
        } else {
            menuItem.setVisible(false);
        }
    }

    public static void initWallpapers(@NonNull NavigationView navigationView) {
        Context context = navigationView.getContext();
        MenuItem menuItem = navigationView.getMenu().findItem(R.id.navigation_view_wallpapers);
        if (menuItem == null) return;

        // Hide wallpapers section if wallpaper_json is empty
        String wallpaperJson = context.getResources().getString(R.string.wallpaper_json);
        if (wallpaperJson.isEmpty()) {
            menuItem.setVisible(false);
            return;
        }

        if (WallpaperHelper.getWallpaperType(context) == WallpaperHelper.EXTERNAL_APP) {
            menuItem.setVisible(context.getResources().getBoolean(R.bool.enable_wallpapers));
        } else {
            menuItem.setVisible(true);
        }
    }

    public static void initPresets(@NonNull NavigationView navigationView) {
        Context context = navigationView.getContext();
        MenuItem menuItem = navigationView.getMenu().findItem(R.id.navigation_view_kustom);
        if (menuItem == null) return;

        if (context.getResources().getBoolean(R.bool.enable_kustom)) {
            menuItem.setVisible(true);
        } else {
            menuItem.setVisible(false);
        }
    }

    public static int getHomePosition() {
        return 0;
    }

    public static int getApplyPosition() {
        return 1;
    }

    public static int getIconsPosition() {
        return 2;
    }

    public static int getRequestPosition() {
        return 3;
    }

    public static int getWallpapersPosition() {
        return 4;
    }

    public static int getPresetPosition() {
        return 5;
    }

    public static int getFaqsPosition() {
        return 6;
    }

    public static int getAboutPosition() {
        return 7;
    }

    public static int getSettingsPosition() {
        return 8;
    }

    public static int getKustomPosition() {
        return 9;
    }
}

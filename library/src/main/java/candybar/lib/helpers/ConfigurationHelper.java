package candybar.lib.helpers;

import static com.danimahardhika.android.helpers.core.DrawableHelper.get;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.provider.Settings;
import android.provider.Settings.Global;
import android.provider.Settings.System;
import android.provider.Settings.Secure;

import androidx.annotation.NonNull;
import androidx.appcompat.graphics.drawable.DrawerArrowDrawable;

import com.danimahardhika.android.helpers.core.ColorHelper;

import candybar.lib.R;
import candybar.lib.applications.CandyBarApplication;

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

public class ConfigurationHelper {

    public static Drawable getNavigationIcon(@NonNull Context context, @NonNull CandyBarApplication.NavigationIcon navigationIcon) {
        Drawable drawable;
        // Always use accent color for navigation icon
        int iconColor = ColorHelper.getAttributeColor(context, R.attr.cb_colorAccent);

        switch (navigationIcon) {
            case DEFAULT:
                DrawerArrowDrawable drawerArrowDrawable = new DrawerArrowDrawable(context);
                drawerArrowDrawable.setColor(iconColor);
                return drawerArrowDrawable;
            case STYLE_1:
                drawable = get(context, R.drawable.ic_toolbar_navigation);
                break;
            case STYLE_2:
                drawable = get(context, R.drawable.ic_toolbar_navigation_2);
                break;
            case STYLE_3:
                drawable = get(context, R.drawable.ic_toolbar_navigation_3);
                break;
            case STYLE_4:
                drawable = get(context, R.drawable.ic_toolbar_navigation_4);
                break;
            default:
                drawable = get(context, R.drawable.ic_toolbar_navigation);
                break;
        }

        drawable.setTint(iconColor);
        return drawable;
    }

    public static int getSocialIconColor(@NonNull Context context, @NonNull CandyBarApplication.IconColor iconColor) {
        if (iconColor == CandyBarApplication.IconColor.ACCENT) {
            return ColorHelper.getAttributeColor(context, com.google.android.material.R.attr.colorSecondary);
        }
        return ColorHelper.getAttributeColor(context, android.R.attr.textColorPrimary);
    }

    public static String getTermsAndConditionsLink(@NonNull Context context) {
        try {
            return context.getString(context.getResources()
                    .getIdentifier("terms_and_conditions_link", "string", context.getPackageName()));
        } catch (Exception e) {
            return "";
        }
    }
}

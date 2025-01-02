package candybar.lib.helpers;

import static com.danimahardhika.android.helpers.core.UnitHelper.toDp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.danimahardhika.android.helpers.core.ColorHelper;
import com.danimahardhika.android.helpers.core.utils.LogUtil;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;
import com.getkeepsafe.taptargetview.TapTargetView;

import candybar.lib.R;
import candybar.lib.activities.CandyBarMainActivity;
import candybar.lib.adapters.HomeAdapter;
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

public class TapIntroHelper {

    public interface Listener {
        void onFinish();
    }

    @SuppressLint("StringFormatInvalid")
    public static void showHomeIntros(@NonNull Context context, @Nullable RecyclerView recyclerView,
                                      @Nullable StaggeredGridLayoutManager manager, int position) {
        if (Preferences.get(context).isTimeToShowHomeIntro()) {
            AppCompatActivity activity = (AppCompatActivity) context;
            boolean isBottomNav = ((CandyBarMainActivity) activity).isBottomNavigationEnabled();

            Toolbar toolbar = activity.findViewById(R.id.toolbar);

            new Handler().postDelayed(() -> {
                try {
                    int titleColor = ColorHelper.getAttributeColor(context, R.attr.cb_colorAccent);
                    int descriptionColor = ColorHelper.getAttributeColor(context, R.attr.cb_colorAccent);
                    int circleColorInner = ColorHelper.getAttributeColor(context, R.attr.cb_cardBackground);
                    int circleColorOuter = ColorHelper.setColorAlpha(
                            ColorHelper.getAttributeColor(context, R.attr.cb_cardBackground),
                            0.3f
                    );

                    TapTargetSequence tapTargetSequence = new TapTargetSequence(activity);
                    tapTargetSequence.continueOnCancel(true);

                    Typeface title = TypefaceHelper.getMedium(context);

                    if (!isBottomNav && toolbar != null) {
                        TapTarget tapTarget = TapTarget.forToolbarNavigationIcon(toolbar,
                                        context.getResources().getString(R.string.tap_intro_home_navigation),
                                        context.getResources().getString(R.string.tap_intro_home_navigation_desc))
                                .titleTextColorInt(titleColor)
                                .descriptionTextColorInt(descriptionColor)
                                .targetCircleColorInt(circleColorInner)
                                .cancelable(true)
                                .tintTarget(false)
                                .dimColor(android.R.color.black)
                                .drawShadow(Preferences.get(context).isTapIntroShadowEnabled());

                        if (circleColorOuter != 0) {
                            tapTarget.outerCircleColorInt(circleColorOuter);
                        }

                        if (title != null) {
                            tapTarget.textTypeface(title);
                        }

                        tapTargetSequence.target(tapTarget);
                    }

                    if (recyclerView != null) {
                        HomeAdapter adapter = (HomeAdapter) recyclerView.getAdapter();
                        if (adapter != null) {
                            if (context.getResources().getBoolean(R.bool.enable_apply)) {
                                if (position >= 0 && position < adapter.getItemCount()) {
                                    RecyclerView.ViewHolder holder = recyclerView.findViewHolderForAdapterPosition(position);
                                    if (holder != null) {
                                        View view = holder.itemView;
                                        float circleScale = 60.0f / context.getResources().getInteger(R.integer.tap_intro_circle_scale_percent);
                                        float targetRadius = (toDp(context, view.getMeasuredWidth()) - 40f) * circleScale;

                                        String desc = context.getResources().getString(R.string.tap_intro_home_apply_desc,
                                                context.getResources().getString(R.string.app_name));
                                        TapTarget tapTarget = TapTarget.forView(view,
                                                        context.getResources().getString(R.string.tap_intro_home_apply),
                                                        desc)
                                                .titleTextColorInt(titleColor)
                                                .descriptionTextColorInt(descriptionColor)
                                                .targetCircleColorInt(circleColorInner)
                                                .targetRadius((int) targetRadius)
                                                .cancelable(true)
                                                .tintTarget(false)
                                                .dimColor(android.R.color.black)
                                                .drawShadow(Preferences.get(context).isTapIntroShadowEnabled());

                                        if (circleColorOuter != 0) {
                                            tapTarget.outerCircleColorInt(circleColorOuter);
                                        }

                                        if (title != null) {
                                            tapTarget.textTypeface(title);
                                        }

                                        tapTargetSequence.target(tapTarget);
                                    }
                                }
                            }
                        }
                    }

                    tapTargetSequence.listener(new TapTargetSequence.Listener() {
                        @Override
                        public void onSequenceFinish() {
                            Preferences.get(context).setTimeToShowHomeIntro(false);
                        }

                        @Override
                        public void onSequenceStep(TapTarget tapTarget, boolean b) {
                            if (manager != null && position >= 0) {
                                manager.scrollToPosition(position);
                            }
                        }

                        @Override
                        public void onSequenceCanceled(TapTarget lastTarget) {
                            Preferences.get(context).setTimeToShowHomeIntro(false);
                        }
                    });
                    tapTargetSequence.start();
                } catch (Exception e) {
                    LogUtil.e(Log.getStackTraceString(e));
                }
            }, 100);
        }
    }

    public static void showIconsIntro(@NonNull Context context) {
        if (Preferences.get(context).isTimeToShowIconsIntro()) {
            AppCompatActivity activity = (AppCompatActivity) context;

            Toolbar toolbar = activity.findViewById(R.id.toolbar);
            if (toolbar == null) return;

            new Handler().postDelayed(() -> {
                try {
                    int titleColor = ColorHelper.getAttributeColor(context, R.attr.cb_colorAccent);
                    int descriptionColor = ColorHelper.getAttributeColor(context, R.attr.cb_colorAccent);
                    int circleColorInner = ColorHelper.getAttributeColor(context, R.attr.cb_cardBackground);
                    int circleColorOuter = ColorHelper.setColorAlpha(
                            ColorHelper.getAttributeColor(context, R.attr.cb_cardBackground),
                            0.3f
                    );

                    Typeface title = TypefaceHelper.getMedium(context);

                    TapTarget tapTarget = TapTarget.forToolbarMenuItem(toolbar, R.id.menu_search,
                                    context.getResources().getString(R.string.tap_intro_icons_search),
                                    context.getResources().getString(R.string.tap_intro_icons_search_desc))
                            .titleTextColorInt(titleColor)
                            .descriptionTextColorInt(descriptionColor)
                            .targetCircleColorInt(circleColorInner)
                            .cancelable(true)
                            .tintTarget(false)
                            .dimColor(android.R.color.black)
                            .targetRadius(40)
                            .drawShadow(Preferences.get(context).isTapIntroShadowEnabled());

                    if (circleColorOuter != 0) {
                        tapTarget.outerCircleColorInt(circleColorOuter);
                    }

                    if (title != null) {
                        tapTarget.textTypeface(title);
                    }

                    TapTargetView.showFor(activity, tapTarget,
                            new TapTargetView.Listener() {
                                @Override
                                public void onTargetDismissed(TapTargetView view, boolean userInitiated) {
                                    super.onTargetDismissed(view, userInitiated);
                                    Preferences.get(context).setTimeToShowIconsIntro(false);
                                }

                                @Override
                                public void onOuterCircleClick(TapTargetView view) {
                                    super.onOuterCircleClick(view);
                                    view.dismiss(true);
                                }

                                @Override
                                public void onTargetClick(TapTargetView view) {
                                    super.onTargetClick(view);
                                    view.dismiss(true);
                                }
                            });
                } catch (Exception e) {
                    LogUtil.e(Log.getStackTraceString(e));
                }
            }, 100);
        }
    }

    public static void showRequestIntro(@NonNull Context context, @Nullable RecyclerView recyclerView, @Nullable Listener listener) {
        if (Preferences.get(context).isTimeToShowRequestIntro()) {
            AppCompatActivity activity = (AppCompatActivity) context;

            int requestOrientation = context.getResources().getConfiguration().orientation ==
                    Configuration.ORIENTATION_PORTRAIT ?
                    ActivityInfo.SCREEN_ORIENTATION_PORTRAIT :
                    ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
            activity.setRequestedOrientation(requestOrientation);

            Toolbar toolbar = activity.findViewById(R.id.toolbar);

            new Handler().postDelayed(() -> {
                try {
                    // Get colors based on current theme
                    int titleColor;
                    int descriptionColor;
                    int circleColorInner;
                    int circleColorOuter;

                    if (Preferences.get(context).isMaterialYou()) {
                        // Use Material You colors
                        titleColor = ColorHelper.getAttributeColor(context, android.R.attr.colorPrimary);
                        descriptionColor = ColorHelper.getAttributeColor(context, android.R.attr.colorPrimary);
                        circleColorInner = ColorHelper.getAttributeColor(context, com.google.android.material.R.attr.colorSurface);
                        circleColorOuter = ColorHelper.setColorAlpha(
                                ColorHelper.getAttributeColor(context, com.google.android.material.R.attr.colorSurface),
                                0.3f
                        );
                    } else {
                        // Use theme accent colors
                        titleColor = ColorHelper.getAttributeColor(context, R.attr.cb_colorAccent);
                        descriptionColor = ColorHelper.getAttributeColor(context, R.attr.cb_colorAccent);
                        circleColorInner = ColorHelper.getAttributeColor(context, R.attr.cb_cardBackground);
                        circleColorOuter = ColorHelper.setColorAlpha(
                                ColorHelper.getAttributeColor(context, R.attr.cb_cardBackground),
                                0.3f
                        );
                    }

                    TapTargetSequence tapTargetSequence = new TapTargetSequence(activity);
                    tapTargetSequence.continueOnCancel(true);

                    Typeface title = TypefaceHelper.getMedium(context);

                    if (recyclerView != null) {
                        int position = 0;
                        if (Preferences.get(context).isPremiumRequestEnabled())
                            position += 1;

                        if (recyclerView.getAdapter() != null) {
                            if (position < recyclerView.getAdapter().getItemCount()) {
                                RecyclerView.ViewHolder holder = recyclerView.findViewHolderForAdapterPosition(position);

                                if (holder != null) {
                                    View view = holder.itemView.findViewById(R.id.checkbox);
                                    if (view != null) {
                                        float circleScale = 40.0f / context.getResources().getInteger(R.integer.tap_intro_circle_scale_percent);
                                        float targetRadius = (toDp(context, view.getMeasuredWidth()) - 5f) * circleScale;

                                        TapTarget tapTarget = TapTarget.forView(view,
                                                        context.getResources().getString(R.string.tap_intro_request_select),
                                                        context.getResources().getString(R.string.tap_intro_request_select_desc))
                                                .titleTextColorInt(titleColor)
                                                .descriptionTextColorInt(descriptionColor)
                                                .targetCircleColorInt(circleColorInner)
                                                .targetRadius((int) targetRadius)
                                                .cancelable(true)
                                                .tintTarget(false)
                                                .dimColor(android.R.color.black)
                                                .drawShadow(Preferences.get(context).isTapIntroShadowEnabled());

                                        if (circleColorOuter != 0) {
                                            tapTarget.outerCircleColorInt(circleColorOuter);
                                        }

                                        if (title != null) {
                                            tapTarget.textTypeface(title);
                                        }

                                        tapTargetSequence.target(tapTarget);
                                    }
                                }
                            }
                        }
                    }

                    if (toolbar != null) {
                        // Add search intro before select all
                        MenuItem searchMenuItem = toolbar.getMenu().findItem(R.id.menu_search);
                        if (searchMenuItem != null) {
                            TapTarget searchTarget = TapTarget.forToolbarMenuItem(toolbar, R.id.menu_search,
                                            context.getResources().getString(R.string.tap_intro_request_search),
                                            context.getResources().getString(R.string.tap_intro_request_search_desc))
                                    .titleTextColorInt(titleColor)
                                    .descriptionTextColorInt(descriptionColor)
                                    .targetCircleColorInt(circleColorInner)
                                    .cancelable(true)
                                    .tintTarget(false)
                                    .dimColor(android.R.color.black)
                                    .drawShadow(Preferences.get(context).isTapIntroShadowEnabled());

                            if (circleColorOuter != 0) {
                                searchTarget.outerCircleColorInt(circleColorOuter);
                            }

                            if (title != null) {
                                searchTarget.textTypeface(title);
                            }

                            tapTargetSequence.target(searchTarget);
                        }

                        TapTarget tapTarget = TapTarget.forToolbarMenuItem(toolbar, R.id.menu_select_all,
                                        context.getResources().getString(R.string.tap_intro_request_select_all),
                                        context.getResources().getString(R.string.tap_intro_request_select_all_desc))
                                .titleTextColorInt(titleColor)
                                .descriptionTextColorInt(descriptionColor)
                                .targetCircleColorInt(circleColorInner)
                                .cancelable(true)
                                .tintTarget(false)
                                .dimColor(android.R.color.black)
                                .drawShadow(Preferences.get(context).isTapIntroShadowEnabled());

                        if (circleColorOuter != 0) {
                            tapTarget.outerCircleColorInt(circleColorOuter);
                        }

                        if (title != null) {
                            tapTarget.textTypeface(title);
                        }

                        tapTargetSequence.target(tapTarget);
                    }

                    View fab = activity.findViewById(R.id.fab);
                    if (fab != null) {
                        float circleScale = 50.0f / context.getResources().getInteger(R.integer.tap_intro_circle_scale_percent);
                        float targetRadius = (toDp(context, fab.getMeasuredWidth()) - 10f) * circleScale;

                        TapTarget tapTarget = TapTarget.forView(fab,
                                        context.getResources().getString(R.string.tap_intro_request_send),
                                        context.getResources().getString(R.string.tap_intro_request_send_desc))
                                .titleTextColorInt(titleColor)
                                .descriptionTextColorInt(descriptionColor)
                                .targetCircleColorInt(circleColorInner)
                                .targetRadius((int) targetRadius)
                                .tintTarget(false)
                                .cancelable(true)
                                .dimColor(android.R.color.black)
                                .drawShadow(Preferences.get(context).isTapIntroShadowEnabled());

                        if (circleColorOuter != 0) {
                            tapTarget.outerCircleColorInt(circleColorOuter);
                        }

                        if (title != null) {
                            tapTarget.textTypeface(title);
                        }

                        tapTargetSequence.target(tapTarget);
                    }

                    if (Preferences.get(context).isPremiumRequestEnabled()) {
                        if (!Preferences.get(context).isPremiumRequest()) {
                            if (recyclerView != null) {
                                int position = 0;

                                if (recyclerView.getAdapter() != null) {
                                    if (position < recyclerView.getAdapter().getItemCount()) {
                                        RecyclerView.ViewHolder holder = recyclerView.findViewHolderForAdapterPosition(position);

                                        if (holder != null) {
                                            View view = holder.itemView.findViewById(R.id.buy);
                                            if (view != null) {
                                                float circleScale = 45.0f / context.getResources().getInteger(R.integer.tap_intro_circle_scale_percent);
                                                float targetRadius = (toDp(context, view.getMeasuredWidth()) - 15f) * circleScale;

                                                TapTarget tapTarget = TapTarget.forView(view,
                                                                context.getResources().getString(R.string.tap_intro_request_premium),
                                                                context.getResources().getString(R.string.tap_intro_request_premium_desc))
                                                        .titleTextColorInt(titleColor)
                                                        .descriptionTextColorInt(descriptionColor)
                                                        .targetCircleColorInt(circleColorInner)
                                                        .targetRadius((int) targetRadius)
                                                        .tintTarget(false)
                                                        .cancelable(true)
                                                        .dimColor(android.R.color.black)
                                                        .drawShadow(Preferences.get(context).isTapIntroShadowEnabled());

                                                if (circleColorOuter != 0) {
                                                    tapTarget.outerCircleColorInt(circleColorOuter);
                                                }

                                                if (title != null) {
                                                    tapTarget.textTypeface(title);
                                                }

                                                tapTargetSequence.target(tapTarget);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    tapTargetSequence.listener(new TapTargetSequence.Listener() {
                        @Override
                        public void onSequenceFinish() {
                            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
                            Preferences.get(context).setTimeToShowRequestIntro(false);
                            Preferences.get(context).setTimeToShowRequestSearchIntro(false);
                            if (listener != null) {
                                listener.onFinish();
                            }
                        }

                        @Override
                        public void onSequenceStep(TapTarget lastTarget, boolean targetClicked) {
                            //Do nothing
                        }

                        @Override
                        public void onSequenceCanceled(TapTarget lastTarget) {
                            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
                            Preferences.get(context).setTimeToShowRequestIntro(false);
                            Preferences.get(context).setTimeToShowRequestSearchIntro(false);
                            if (listener != null) {
                                listener.onFinish();
                            }
                        }
                    });
                    tapTargetSequence.start();
                } catch (Exception e) {
                    LogUtil.e(Log.getStackTraceString(e));
                }
            }, 100);
        } else if (listener != null) {
            listener.onFinish();
        }
    }

    @SuppressLint("StringFormatInvalid")
    public static void showWallpapersIntro(@NonNull Context context, @Nullable RecyclerView recyclerView) {
        if (Preferences.get(context).isTimeToShowWallpapersIntro()) {
            AppCompatActivity activity = (AppCompatActivity) context;

            if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }

            new Handler().postDelayed(() -> {
                try {
                    // Get colors based on current theme
                    int titleColor;
                    int descriptionColor;
                    int circleColorInner;
                    int circleColorOuter;

                    if (Preferences.get(context).isMaterialYou()) {
                        // Use theme accent colors
                        titleColor = ColorHelper.getAttributeColor(context, R.attr.cb_colorAccent);
                        descriptionColor = ColorHelper.getAttributeColor(context, R.attr.cb_colorAccent);
                        circleColorInner = ColorHelper.getAttributeColor(context, R.attr.cb_cardBackground);
                        circleColorOuter = ColorHelper.setColorAlpha(
                                ColorHelper.getAttributeColor(context, R.attr.cb_cardBackground),
                                0.3f
                        );
                    } else {
                        // Use theme accent colors
                        titleColor = ColorHelper.getAttributeColor(context, R.attr.cb_colorAccent);
                        descriptionColor = ColorHelper.getAttributeColor(context, R.attr.cb_colorAccent);
                        circleColorInner = ColorHelper.getAttributeColor(context, R.attr.cb_cardBackground);
                        circleColorOuter = ColorHelper.setColorAlpha(
                                ColorHelper.getAttributeColor(context, R.attr.cb_cardBackground),
                                0.3f
                        );
                    }

                    if (recyclerView != null) {
                        TapTargetSequence tapTargetSequence = new TapTargetSequence(activity);
                        tapTargetSequence.continueOnCancel(true);

                        int position = 0;

                        if (recyclerView.getAdapter() == null) return;

                        if (position < recyclerView.getAdapter().getItemCount()) {
                            RecyclerView.ViewHolder holder = recyclerView.findViewHolderForAdapterPosition(position);
                            if (holder == null) return;

                            View view = holder.itemView.findViewById(R.id.image);
                            if (view != null) {
                                float circleScale = 45.0f / context.getResources().getInteger(R.integer.tap_intro_circle_scale_percent);
                                float targetRadius = (toDp(context, view.getMeasuredWidth()) - 15f) * circleScale;

                                Typeface title = TypefaceHelper.getMedium(context);

                                String desc = context.getResources().getString(R.string.tap_intro_wallpapers_option_desc,
                                        context.getResources().getBoolean(R.bool.enable_wallpaper_download) ?
                                                context.getResources().getString(R.string.tap_intro_wallpapers_option_desc_download) : "");

                                TapTarget tapTarget = TapTarget.forView(view,
                                                context.getResources().getString(R.string.tap_intro_wallpapers_option),
                                                desc)
                                        .titleTextColorInt(titleColor)
                                        .descriptionTextColorInt(descriptionColor)
                                        .targetCircleColorInt(circleColorInner)
                                        .targetRadius((int) targetRadius)
                                        .tintTarget(false)
                                        .cancelable(true)
                                        .dimColor(android.R.color.black)
                                        .drawShadow(Preferences.get(context).isTapIntroShadowEnabled());

                                TapTarget tapTarget1 = TapTarget.forView(view,
                                                context.getResources().getString(R.string.tap_intro_wallpapers_preview),
                                                context.getResources().getString(R.string.tap_intro_wallpapers_preview_desc))
                                        .titleTextColorInt(titleColor)
                                        .descriptionTextColorInt(descriptionColor)
                                        .targetCircleColorInt(circleColorInner)
                                        .targetRadius((int) targetRadius)
                                        .tintTarget(false)
                                        .cancelable(true)
                                        .dimColor(android.R.color.black)
                                        .drawShadow(Preferences.get(context).isTapIntroShadowEnabled());

                                if (circleColorOuter != 0) {
                                    tapTarget.outerCircleColorInt(circleColorOuter);
                                    tapTarget1.outerCircleColorInt(circleColorOuter);
                                }

                                if (title != null) {
                                    tapTarget.textTypeface(title);
                                    tapTarget1.textTypeface(title);
                                }

                                tapTargetSequence.target(tapTarget);
                                tapTargetSequence.target(tapTarget1);

                                tapTargetSequence.listener(new TapTargetSequence.Listener() {
                                    @Override
                                    public void onSequenceFinish() {
                                        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
                                        Preferences.get(context).setTimeToShowWallpapersIntro(false);
                                    }

                                    @Override
                                    public void onSequenceStep(TapTarget tapTarget, boolean b) {
                                        // Do nothing
                                    }

                                    @Override
                                    public void onSequenceCanceled(TapTarget lastTarget) {
                                        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
                                        Preferences.get(context).setTimeToShowWallpapersIntro(false);
                                    }
                                });
                                tapTargetSequence.start();
                            }
                        }
                    }
                } catch (Exception e) {
                    LogUtil.e(Log.getStackTraceString(e));
                }
            }, 200);
        }
    }

    public static void showWallpaperPreviewIntro(@NonNull Context context, @ColorInt int color) {
        if (Preferences.get(context).isTimeToShowWallpaperPreviewIntro()) {
            AppCompatActivity activity = (AppCompatActivity) context;

            View rootView = activity.findViewById(R.id.rootview);
            if (rootView == null) return;

            new Handler().postDelayed(() -> {
                try {
                    // Get colors based on current theme
                    int titleColor;
                    int descriptionColor;
                    int circleColorInner;
                    int circleColorOuter;

                    // Use theme accent colors
                    titleColor = ColorHelper.getAttributeColor(activity, R.attr.cb_colorAccent);
                    descriptionColor = ColorHelper.getAttributeColor(activity, R.attr.cb_colorAccent);
                    circleColorInner = ColorHelper.getAttributeColor(activity, R.attr.cb_cardBackground);
                    circleColorOuter = ColorHelper.setColorAlpha(
                            ColorHelper.getAttributeColor(activity, R.attr.cb_cardBackground),
                            0.3f
                    );

                    TapTargetSequence tapTargetSequence = new TapTargetSequence(activity);
                    tapTargetSequence.continueOnCancel(true);

                    Typeface title = TypefaceHelper.getMedium(context);

                    View apply = rootView.findViewById(R.id.menu_apply);
                    View save = rootView.findViewById(R.id.menu_save);

                    // Apply button target
                    if (apply != null) {
                        float applyScale = 45.0f / context.getResources().getInteger(R.integer.tap_intro_circle_scale_percent);
                        float applyRadius = (toDp(context, apply.getMeasuredWidth()) - 15f) * applyScale;

                        TapTarget tapTarget = TapTarget.forView(apply,
                                        context.getResources().getString(R.string.tap_intro_wallpaper_preview_apply),
                                        context.getResources().getString(R.string.tap_intro_wallpaper_preview_apply_desc))
                                .titleTextColorInt(titleColor)
                                .descriptionTextColorInt(descriptionColor)
                                .targetCircleColorInt(circleColorInner)
                                .outerCircleColorInt(circleColorOuter)
                                .targetRadius((int) applyRadius)
                                .cancelable(true)
                                .dimColor(android.R.color.black)
                                .drawShadow(Preferences.get(context).isTapIntroShadowEnabled());

                        if (title != null) {
                            tapTarget.textTypeface(title);
                        }

                        tapTargetSequence.target(tapTarget);
                    }

                    // Save button target
                    if (save != null && context.getResources().getBoolean(R.bool.enable_wallpaper_download)) {
                        float saveScale = 45.0f / context.getResources().getInteger(R.integer.tap_intro_circle_scale_percent);
                        float saveRadius = (toDp(context, save.getMeasuredWidth()) - 15f) * saveScale;

                        TapTarget tapTarget = TapTarget.forView(save,
                                        context.getResources().getString(R.string.tap_intro_wallpaper_preview_save),
                                        context.getResources().getString(R.string.tap_intro_wallpaper_preview_save_desc))
                                .titleTextColorInt(titleColor)
                                .descriptionTextColorInt(descriptionColor)
                                .targetCircleColorInt(circleColorInner)
                                .outerCircleColorInt(circleColorOuter)
                                .targetRadius((int) saveRadius)
                                .cancelable(true)
                                .dimColor(android.R.color.black)
                                .drawShadow(Preferences.get(context).isTapIntroShadowEnabled());

                        if (title != null) {
                            tapTarget.textTypeface(title);
                        }

                        tapTargetSequence.target(tapTarget);
                    }

                    tapTargetSequence.listener(new TapTargetSequence.Listener() {
                        @Override
                        public void onSequenceFinish() {
                            Preferences.get(context).setTimeToShowWallpaperPreviewIntro(false);
                        }

                        @Override
                        public void onSequenceStep(TapTarget lastTarget, boolean targetClicked) {
                            // Do nothing
                        }

                        @Override
                        public void onSequenceCanceled(TapTarget lastTarget) {
                            // Still mark as shown even if canceled
                            Preferences.get(context).setTimeToShowWallpaperPreviewIntro(false);
                        }
                    });
                    tapTargetSequence.start();
                } catch (Exception e) {
                    LogUtil.e(Log.getStackTraceString(e));
                }
            }, 100);
        }
    }

    public static void showHomeApplyIntro(@NonNull Activity activity, @Nullable RecyclerView recyclerView,
                                          @Nullable RecyclerView.LayoutManager manager, int applyIndex) {
        if (!Preferences.get(activity).isTimeToShowHomeIntro()) return;
        if (recyclerView == null) return;
        if (manager == null) return;
        if (applyIndex < 0) return;

        new Handler().postDelayed(() -> {
            try {
                TapTargetSequence tapTargetSequence = new TapTargetSequence(activity);
                tapTargetSequence.continueOnCancel(true);

                // Find the apply view
                RecyclerView.ViewHolder holder = recyclerView.findViewHolderForAdapterPosition(applyIndex);
                if (holder != null) {
                    View apply = holder.itemView;

                    int titleColor = ColorHelper.getAttributeColor(activity, R.attr.cb_colorAccent);
                    int descriptionColor = ColorHelper.getAttributeColor(activity, R.attr.cb_colorAccent);
                    int circleColorInner = ColorHelper.getAttributeColor(activity, R.attr.cb_cardBackground);
                    int circleColorOuter = ColorHelper.setColorAlpha(
                            ColorHelper.getAttributeColor(activity, R.attr.cb_cardBackground),
                            0.3f
                    );

                    float circleScale = 60.0f / activity.getResources().getInteger(R.integer.tap_intro_circle_scale_percent);
                    float targetRadius = (toDp(activity, apply.getMeasuredWidth()) - 40f) * circleScale;

                    String desc = activity.getResources().getString(R.string.tap_intro_home_apply_desc,
                            activity.getResources().getString(R.string.app_name));

                    tapTargetSequence.target(TapTarget.forView(apply,
                                    activity.getResources().getString(R.string.tap_intro_home_apply),
                                    desc)
                            .titleTextColorInt(titleColor)
                            .descriptionTextColorInt(descriptionColor)
                            .targetCircleColorInt(circleColorInner)
                            .outerCircleColorInt(circleColorOuter)
                            .targetRadius((int) targetRadius)
                            .tintTarget(false)
                            .cancelable(true)
                            .dimColor(android.R.color.black)
                            .drawShadow(Preferences.get(activity).isTapIntroShadowEnabled()));

                    tapTargetSequence.listener(new TapTargetSequence.Listener() {
                        @Override
                        public void onSequenceFinish() {
                            Preferences.get(activity).setTimeToShowHomeIntro(false);
                        }

                        @Override
                        public void onSequenceStep(TapTarget lastTarget, boolean targetClicked) {
                            // Do nothing
                        }

                        @Override
                        public void onSequenceCanceled(TapTarget lastTarget) {
                            // Still mark as shown even if canceled
                            Preferences.get(activity).setTimeToShowHomeIntro(false);
                        }
                    });

                    tapTargetSequence.start();
                }
            } catch (Exception e) {
                LogUtil.e(Log.getStackTraceString(e));
            }
        }, 100);
    }

    public static void showRequestSearchIntro(@NonNull Context context, @NonNull View searchView) {
        AppCompatActivity activity = (AppCompatActivity) context;

        try {
            int titleColor = ColorHelper.getAttributeColor(context, R.attr.cb_colorAccent);
            int descriptionColor = ColorHelper.getAttributeColor(context, R.attr.cb_colorAccent);
            int circleColorInner = ColorHelper.getAttributeColor(context, R.attr.cb_cardBackground);
            int circleColorOuter = ColorHelper.setColorAlpha(
                    ColorHelper.getAttributeColor(context, R.attr.cb_cardBackground),
                    0.3f
            );

            Typeface title = TypefaceHelper.getMedium(context);

            float circleScale = 45.0f / context.getResources().getInteger(R.integer.tap_intro_circle_scale_percent);
            float targetRadius = (toDp(context, searchView.getMeasuredWidth()) - 15f) * circleScale;

            TapTarget tapTarget = TapTarget.forView(searchView,
                            context.getResources().getString(R.string.tap_intro_request_search),
                            context.getResources().getString(R.string.tap_intro_request_search_desc))
                    .titleTextColorInt(titleColor)
                    .descriptionTextColorInt(descriptionColor)
                    .targetCircleColorInt(circleColorInner)
                    .outerCircleColorInt(circleColorOuter)
                    .targetRadius((int) targetRadius)
                    .cancelable(true)
                    .tintTarget(false)
                    .dimColor(android.R.color.black)
                    .drawShadow(Preferences.get(context).isTapIntroShadowEnabled());

            if (title != null) {
                tapTarget.textTypeface(title);
            }

            if (!activity.isFinishing()) {
                TapTargetView.showFor(activity, tapTarget,
                        new TapTargetView.Listener() {
                            @Override
                            public void onTargetDismissed(TapTargetView view, boolean userInitiated) {
                                super.onTargetDismissed(view, userInitiated);
                                Preferences.get(context).setTimeToShowRequestSearchIntro(false);
                            }

                            @Override
                            public void onOuterCircleClick(TapTargetView view) {
                                super.onOuterCircleClick(view);
                                view.dismiss(true);
                            }

                            @Override
                            public void onTargetClick(TapTargetView view) {
                                super.onTargetClick(view);
                                view.dismiss(true);
                            }
                        });
            }
        } catch (Exception e) {
            LogUtil.e(Log.getStackTraceString(e));
        }
    }
}

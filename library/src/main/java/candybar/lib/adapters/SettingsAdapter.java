package candybar.lib.adapters;

import static candybar.lib.items.Setting.Type.MATERIAL_YOU;
import static candybar.lib.items.Setting.Type.NOTIFICATIONS;
import static candybar.lib.items.Setting.Type.NAVIGATION_VIEW_STYLE;
import static candybar.lib.items.Setting.Type.PURE_BLACK;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.danimahardhika.android.helpers.core.ColorHelper;
import com.danimahardhika.android.helpers.core.DrawableHelper;
import com.danimahardhika.android.helpers.core.FileHelper;
import com.danimahardhika.android.helpers.core.utils.LogUtil;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetView;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.io.File;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;

import candybar.lib.R;
import candybar.lib.activities.CandyBarMainActivity;
import candybar.lib.applications.CandyBarApplication;
import candybar.lib.databases.Database;
import candybar.lib.fragments.SettingsFragment;
import candybar.lib.fragments.dialog.ChangelogFragment;
import candybar.lib.fragments.dialog.LanguagesFragment;
import candybar.lib.fragments.dialog.ThemeChooserFragment;
import candybar.lib.helpers.ReportBugsHelper;
import candybar.lib.helpers.TypefaceHelper;
import candybar.lib.items.Setting;
import candybar.lib.preferences.Preferences;
import candybar.lib.tasks.IconRequestTask;
import candybar.lib.helpers.ToastHelper;
import candybar.lib.helpers.TapIntroHelper;
import candybar.lib.helpers.ThemeHelper;
import candybar.lib.utils.listeners.InAppBillingListener;

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

public class SettingsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context mContext;
    private final List<Setting> mSettings;

    private static final int TYPE_CONTENT = 0;
    private static final int TYPE_FOOTER = 1;

    public SettingsAdapter(@NonNull Context context, @NonNull List<Setting> settings) {
        mContext = context;
        mSettings = settings;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_CONTENT) {
            View view = LayoutInflater.from(mContext).inflate(
                    R.layout.fragment_settings_item_list, parent, false);
            return new ContentViewHolder(view);
        }

        View view = LayoutInflater.from(mContext).inflate(
                R.layout.fragment_settings_item_footer, parent, false);
        return new FooterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == TYPE_CONTENT) {
            ContentViewHolder contentViewHolder = (ContentViewHolder) holder;
            Setting setting = mSettings.get(position);

            if (setting.getTitle().length() == 0) {
                contentViewHolder.title.setVisibility(View.GONE);
                contentViewHolder.divider.setVisibility(View.GONE);
                contentViewHolder.container.setVisibility(View.VISIBLE);

                contentViewHolder.subtitle.setText(setting.getSubtitle());
                setTextColorForOldAndroid(contentViewHolder.subtitle);

                if (setting.getContent().length() == 0) {
                    contentViewHolder.content.setVisibility(View.GONE);
                } else {
                    contentViewHolder.content.setText(setting.getContent());
                    contentViewHolder.content.setVisibility(View.VISIBLE);
                    setTextColorForOldAndroid(contentViewHolder.content);
                }

                if (setting.getFooter().length() == 0) {
                    contentViewHolder.footer.setVisibility(View.GONE);
                } else {
                    contentViewHolder.footer.setText(setting.getFooter());
                    contentViewHolder.footer.setVisibility(View.VISIBLE);
                }
            } else {
                contentViewHolder.container.setVisibility(View.GONE);
                contentViewHolder.title.setVisibility(View.VISIBLE);
                contentViewHolder.title.setText(setting.getTitle());
                setTextColorForOldAndroid(contentViewHolder.title);

                if (position > 0) {
                    contentViewHolder.divider.setVisibility(View.VISIBLE);
                } else {
                    contentViewHolder.divider.setVisibility(View.GONE);
                }

                if (setting.getIcon() != -1) {
                    int color = Build.VERSION.SDK_INT <= Build.VERSION_CODES.R ? 
                            (isDarkMode() ? mContext.getResources().getColor(android.R.color.white) 
                                        : mContext.getResources().getColor(android.R.color.black)) :
                            ColorHelper.getAttributeColor(mContext, R.attr.cb_colorAccent);
                    contentViewHolder.title.setCompoundDrawablesWithIntrinsicBounds(
                            DrawableHelper.getTintedDrawable(mContext, setting.getIcon(), color),
                            null, null, null);
                }
            }

            if (setting.getType() == MATERIAL_YOU || setting.getType() == NOTIFICATIONS || 
                setting.getType() == NAVIGATION_VIEW_STYLE || setting.getType() == PURE_BLACK) {
                contentViewHolder.switchView.setVisibility(View.VISIBLE);
                contentViewHolder.container.setClickable(false);
                int pad = contentViewHolder.container.getPaddingLeft();
                contentViewHolder.container.setPadding(pad, 0, pad, 0);
                contentViewHolder.updateSwitchAppearance();

                if (contentViewHolder.switchView instanceof CompoundButton) {
                    CompoundButton switchButton = (CompoundButton) contentViewHolder.switchView;
                    if (setting.getType() == MATERIAL_YOU) {
                        switchButton.setChecked(Preferences.get(mContext).isMaterialYou());
                    } else if (setting.getType() == PURE_BLACK) {
                        switchButton.setChecked(Preferences.get(mContext).isPureBlack());
                        switchButton.setEnabled(ThemeHelper.isDarkTheme(mContext));
                    } else if (setting.getType() == NOTIFICATIONS) {
                        boolean isPermissionGranted = Build.VERSION.SDK_INT < Build.VERSION_CODES.O || NotificationManagerCompat.from(mContext).areNotificationsEnabled();
                        switchButton.setChecked(Preferences.get(mContext).isNotificationsEnabled() && isPermissionGranted);
                        pad = contentViewHolder.container.getPaddingLeft();
                        contentViewHolder.container.setPadding(pad, pad, pad, 0);
                    } else if (setting.getType() == NAVIGATION_VIEW_STYLE) {
                        boolean isBottomNav = Preferences.get(mContext).getNavigationViewStyle() 
                            == CandyBarApplication.NavigationViewStyle.BOTTOM_NAVIGATION;
                        switchButton.setChecked(isBottomNav);
                    }
                }

                // Show navigation intro if needed
                if (setting.getType() == NAVIGATION_VIEW_STYLE && 
                    Preferences.get(mContext).isTimeToShowNavigationViewIntro()) {
                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
                        // Store a reference to the RecyclerView
                        RecyclerView recyclerView = (RecyclerView) contentViewHolder.itemView.getParent();
                        
                        TapTargetView.showFor((AppCompatActivity) mContext, 
                            TapTarget.forView(contentViewHolder.switchView,
                                mContext.getResources().getString(R.string.tap_intro_navigation_view),
                                mContext.getResources().getString(R.string.tap_intro_navigation_view_desc))
                            .titleTextColorInt(ColorHelper.getAttributeColor(mContext, R.attr.cb_colorAccent))
                            .descriptionTextColorInt(ColorHelper.getAttributeColor(mContext, R.attr.cb_colorAccent))
                            .targetCircleColorInt(ColorHelper.getAttributeColor(mContext, R.attr.cb_cardBackground))
                            .outerCircleColorInt(ColorHelper.setColorAlpha(
                                ColorHelper.getAttributeColor(mContext, R.attr.cb_cardBackground),
                                0.3f))
                            .cancelable(true)
                            .tintTarget(false)
                            .dimColor(android.R.color.black)
                            .drawShadow(Preferences.get(mContext).isTapIntroShadowEnabled()),
                            new TapTargetView.Listener() {
                                @Override
                                public void onTargetDismissed(TapTargetView view, boolean userInitiated) {
                                    super.onTargetDismissed(view, userInitiated);
                                    Preferences.get(mContext).setTimeToShowNavigationViewIntro(false);
                                    
                                    // Show black theme tutorial after navigation tutorial is dismissed
                                    if (ThemeHelper.isDarkTheme(mContext) && 
                                        Preferences.get(mContext).isTimeToShowBlackThemeIntro()) {
                                        // Find the black theme switch view
                                        for (int i = 0; i < mSettings.size(); i++) {
                                            if (mSettings.get(i).getType() == PURE_BLACK) {
                                                RecyclerView.ViewHolder holder = recyclerView.findViewHolderForAdapterPosition(i);
                                                if (holder instanceof ContentViewHolder) {
                                                    ContentViewHolder blackThemeHolder = (ContentViewHolder) holder;
                                                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
                                                        TapIntroHelper.showBlackThemeIntro(mContext, blackThemeHolder.switchView);
                                                    }, 100);
                                                }
                                                break;
                                            }
                                        }
                                    }
                                }

                                @Override
                                public void onTargetClick(TapTargetView view) {
                                    super.onTargetClick(view);
                                    view.dismiss(true);
                                }
                            });
                    }, 100);
                }
            } else {
                contentViewHolder.switchView.setVisibility(View.GONE);
                contentViewHolder.container.setClickable(true);
            }
        }
    }

    @Override
    public int getItemCount() {
        return mSettings.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == getItemCount() - 1) return TYPE_FOOTER;
        return TYPE_CONTENT;
    }

    private class ContentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final TextView title;
        private final TextView subtitle;
        private final TextView content;
        private final TextView footer;
        private final LinearLayout container;
        private final View divider;
        private View switchView;
        private final Context mContext;
        private CompoundButton.OnCheckedChangeListener switchListener;

        ContentViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            subtitle = itemView.findViewById(R.id.subtitle);
            content = itemView.findViewById(R.id.content);
            footer = itemView.findViewById(R.id.footer);
            divider = itemView.findViewById(R.id.divider);
            container = itemView.findViewById(R.id.container);
            switchView = itemView.findViewById(R.id.switch_key);
            mContext = itemView.getContext();
            
            // Set footer text color to accent
            footer.setTextColor(ColorHelper.getAttributeColor(mContext, R.attr.cb_colorAccent));
            
            // Store the switch listener
            if (switchView instanceof CompoundButton) {
                switchListener = (buttonView, isChecked) -> {
                    int position = getBindingAdapterPosition();
                    if (position < 0) return;
                    Setting setting = mSettings.get(position);
                    
                    switch (setting.getType()) {
                        case MATERIAL_YOU:
                            if (isChecked != Preferences.get(mContext).isMaterialYou()) {
                                Preferences.get(mContext).setMaterialYou(isChecked);
                                notifyDataSetChanged();
                                ((Activity) mContext).recreate();
                            }
                            break;
                        case PURE_BLACK:
                            if (isChecked != Preferences.get(mContext).isPureBlack() && ThemeHelper.isDarkTheme(mContext)) {
                                Preferences.get(mContext).setPureBlack(isChecked);
                                notifyDataSetChanged();
                                ((Activity) mContext).recreate();
                            }
                            break;
                        case NOTIFICATIONS:
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && !NotificationManagerCompat.from(mContext).areNotificationsEnabled()) {
                                ((CompoundButton)switchView).setChecked(false);
                                Intent settingsIntent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                        .putExtra(Settings.EXTRA_APP_PACKAGE, mContext.getPackageName());
                                mContext.startActivity(settingsIntent);
                                break;
                            }
                            if (isChecked != Preferences.get(mContext).isNotificationsEnabled()) {
                                Preferences.get(mContext).setNotificationsEnabled(isChecked);
                                CandyBarApplication.Configuration.NotificationHandler handler = CandyBarApplication.getConfiguration().getNotificationHandler();
                                if (handler != null) {
                                    handler.setMode(isChecked);
                                }
                            }
                            break;
                        case NAVIGATION_VIEW_STYLE:
                            if (isChecked != (Preferences.get(mContext).getNavigationViewStyle() 
                                == CandyBarApplication.NavigationViewStyle.BOTTOM_NAVIGATION)) {
                                CandyBarApplication.NavigationViewStyle newStyle = isChecked ? 
                                    CandyBarApplication.NavigationViewStyle.BOTTOM_NAVIGATION :
                                    CandyBarApplication.NavigationViewStyle.MINI_DRAWER;
                                
                                Preferences.get(mContext).setNavigationViewStyle(newStyle);
                                
                                Activity activity = (Activity) mContext;
                                Intent intent = activity.getIntent();
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                activity.finish();
                                activity.startActivity(intent);
                            }
                            break;
                    }
                };
                ((CompoundButton)switchView).setOnCheckedChangeListener(switchListener);
            }

            container.setOnClickListener(this);
        }

        private void updateSwitchAppearance() {
            boolean isMaterialYou = Preferences.get(mContext).isMaterialYou();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                ViewGroup parent = (ViewGroup) switchView.getParent();
                if (parent == null) return;
                
                int index = parent.indexOfChild(switchView);
                boolean isChecked = switchView instanceof CompoundButton && ((CompoundButton)switchView).isChecked();
                boolean isEnabled = switchView.isEnabled();
                
                // Always recreate the switch to ensure consistent styling
                View oldSwitch = switchView;
                if (isMaterialYou) {
                    Context context = new ContextThemeWrapper(mContext, R.style.Widget_Material3_MaterialSwitch);
                    MaterialSwitch newSwitch = new MaterialSwitch(context);
                    newSwitch.setLayoutParams(new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT));
                    newSwitch.setChecked(isChecked);
                    newSwitch.setEnabled(isEnabled);
                    newSwitch.setOnCheckedChangeListener(switchListener);
                    
                    // Set Material You colors
                    newSwitch.setThumbTintList(ColorStateList.valueOf(
                        ContextCompat.getColor(mContext, R.color.material_you_switch_thumb)));
                    newSwitch.setTrackTintList(ColorStateList.valueOf(
                        ContextCompat.getColor(mContext, R.color.material_you_switch_track)));
                    
                    parent.removeView(oldSwitch);
                    parent.addView(newSwitch, index);
                    switchView = newSwitch;
                } else {
                    Context context = new ContextThemeWrapper(mContext, R.style.Widget_Material2_MaterialSwitch);
                    SwitchMaterial newSwitch = new SwitchMaterial(context);
                    newSwitch.setLayoutParams(new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT));
                    newSwitch.setChecked(isChecked);
                    newSwitch.setEnabled(isEnabled);
                    newSwitch.setOnCheckedChangeListener(switchListener);
                    
                    // Set Material 2 colors
                    TypedValue typedValue = new TypedValue();
                    mContext.getTheme().resolveAttribute(R.attr.cb_colorAccent, typedValue, true);
                    int accentColor = typedValue.data;
                    int trackColorChecked = ColorHelper.setColorAlpha(accentColor, 0.3f);
                    
                    int[][] states = new int[][] {
                        new int[] { android.R.attr.state_checked },
                        new int[] { -android.R.attr.state_checked }
                    };
                    int[] thumbColors = new int[] { 
                        accentColor,      // Checked: accent color
                        0xFFFFFFFF       // Unchecked: white
                    };
                    
                    // Use accent color with reduced alpha for track in Pure Black theme
                    boolean isPureBlack = ThemeHelper.isPureBlackEnabled(mContext);
                    int[] trackColors = new int[] { 
                        trackColorChecked,  // Checked: semi-transparent accent
                        isPureBlack ? ColorHelper.setColorAlpha(accentColor, 0.12f) : 0x61000000  // Unchecked: accent color with 12% alpha for Pure Black, default grey for others
                    };
                    
                    newSwitch.setThumbTintList(new ColorStateList(states, thumbColors));
                    newSwitch.setTrackTintList(new ColorStateList(states, trackColors));
                    
                    parent.removeView(oldSwitch);
                    parent.addView(newSwitch, index);
                    switchView = newSwitch;
                }
            } else if (switchView instanceof SwitchMaterial) {
                SwitchMaterial switchMaterial = (SwitchMaterial) switchView;
                int accentColor = ColorHelper.getAttributeColor(mContext, R.attr.cb_colorAccent);
                
                int[][] states = new int[][] {
                    new int[] { android.R.attr.state_checked },
                    new int[] { -android.R.attr.state_checked }
                };
                int[] thumbColors = new int[] { 
                    accentColor,      // Checked: accent color
                    0xFFFFFFFF       // Unchecked: white
                };
                
                // Use accent color with reduced alpha for track
                boolean isPureBlack = ThemeHelper.isPureBlackEnabled(mContext);
                int[] trackColors = new int[] { 
                    ColorHelper.setColorAlpha(accentColor, 0.3f),  // Checked: semi-transparent accent
                    isPureBlack ? ColorHelper.setColorAlpha(accentColor, 0.12f) : 0x61000000  // Unchecked: accent color with 12% alpha for Pure Black, default grey for others
                };
                
                switchMaterial.setThumbTintList(new ColorStateList(states, thumbColors));
                switchMaterial.setTrackTintList(new ColorStateList(states, trackColors));
            }
        }

        void bind(Setting setting) {
            title.setText(setting.getTitle());
            subtitle.setText(setting.getSubtitle());
            content.setText(setting.getContent());

            if (setting.getType() == MATERIAL_YOU || setting.getType() == NOTIFICATIONS || 
                setting.getType() == NAVIGATION_VIEW_STYLE || setting.getType() == PURE_BLACK) {
                switchView.setVisibility(View.VISIBLE);
                container.setClickable(false);
                int pad = container.getPaddingLeft();
                container.setPadding(pad, 0, pad, 0);
                updateSwitchAppearance();
            } else {
                switchView.setVisibility(View.GONE);
                container.setClickable(true);
            }

            if (switchView instanceof CompoundButton) {
                CompoundButton switchButton = (CompoundButton)switchView;
                if (setting.getType() == MATERIAL_YOU) {
                    switchButton.setChecked(Preferences.get(mContext).isMaterialYou());
                    updateSwitchAppearance();
                } else if (setting.getType() == PURE_BLACK) {
                    switchButton.setChecked(Preferences.get(mContext).isPureBlack());
                    switchButton.setEnabled(ThemeHelper.isDarkTheme(mContext));
                    updateSwitchAppearance();
                } else if (setting.getType() == NOTIFICATIONS) {
                    boolean isPermissionGranted = Build.VERSION.SDK_INT < Build.VERSION_CODES.O || NotificationManagerCompat.from(mContext).areNotificationsEnabled();
                    switchButton.setChecked(Preferences.get(mContext).isNotificationsEnabled() && isPermissionGranted);
                    updateSwitchAppearance();
                } else if (setting.getType() == NAVIGATION_VIEW_STYLE) {
                    boolean isBottomNav = Preferences.get(mContext).getNavigationViewStyle() 
                        == CandyBarApplication.NavigationViewStyle.BOTTOM_NAVIGATION;
                    switchButton.setChecked(isBottomNav);
                    updateSwitchAppearance();
                }
            }
        }

        @Override
        public void onClick(View view) {
            int id = view.getId();
            if (id == R.id.container) {
                int position = getBindingAdapterPosition();

                if (position < 0 || position > mSettings.size()) return;

                Setting setting = mSettings.get(position);
                switch (setting.getType()) {
                    case CACHE:
                        CandyBarApplication.getConfiguration().getAnalyticsHandler().logEvent(
                                "click",
                                new HashMap<String, Object>() {{
                                    put("section", "settings");
                                    put("action", "open_dialog");
                                    put("item", "clear_cache");
                                }}
                        );
                        new MaterialDialog.Builder(mContext)
                                .backgroundColor(ColorHelper.getAttributeColor(mContext, R.attr.cb_cardBackground))
                                .typeface(TypefaceHelper.getMedium(mContext), TypefaceHelper.getRegular(mContext))
                                .content(R.string.pref_data_cache_clear_dialog)
                                .positiveText(R.string.clear)
                                .negativeText(android.R.string.cancel)
                                .positiveColor(ColorHelper.getAttributeColor(mContext, R.attr.cb_colorAccent))
                                .negativeColor(ColorHelper.getAttributeColor(mContext, R.attr.cb_colorAccent))
                                .contentColor(ColorHelper.getAttributeColor(mContext, R.attr.cb_primaryText))
                                .onPositive((dialog, which) -> {
                                    CandyBarApplication.getConfiguration().getAnalyticsHandler().logEvent(
                                            "click",
                                            new HashMap<String, Object>() {{
                                                put("section", "settings");
                                                put("action", "confirm");
                                                put("item", "clear_cache");
                                            }}
                                    );
                                    try {
                                        File cache = mContext.getCacheDir();
                                        FileHelper.clearDirectory(cache);

                                        double size = (double) FileHelper.getDirectorySize(cache) / FileHelper.MB;
                                        NumberFormat formatter = new DecimalFormat("#0.00");

                                        setting.setFooter(mContext.getResources().getString(
                                                R.string.pref_data_cache_size, formatter.format(size) + " MB"));
                                        notifyItemChanged(position);

                                        ToastHelper.show(mContext, R.string.pref_data_cache_cleared,
                                                Toast.LENGTH_LONG);
                                    } catch (Exception e) {
                                        LogUtil.e(Log.getStackTraceString(e));
                                    }
                                })
                                .onNegative(((dialog, which) -> {
                                    CandyBarApplication.getConfiguration().getAnalyticsHandler().logEvent(
                                            "click",
                                            new HashMap<String, Object>() {{
                                                put("section", "settings");
                                                put("action", "cancel");
                                                put("item", "clear_cache");
                                            }}
                                    );
                                }))
                                .show();
                        break;
                    case ICON_REQUEST:
                        CandyBarApplication.getConfiguration().getAnalyticsHandler().logEvent(
                                "click",
                                new HashMap<String, Object>() {{
                                    put("section", "settings");
                                    put("action", "open_dialog");
                                    put("item", "clear_icon_request_data");
                                }}
                        );
                        new MaterialDialog.Builder(mContext)
                                .backgroundColor(ColorHelper.getAttributeColor(mContext, R.attr.cb_cardBackground))
                                .typeface(TypefaceHelper.getMedium(mContext), TypefaceHelper.getRegular(mContext))
                                .content(R.string.pref_data_request_clear_dialog)
                                .positiveText(R.string.clear)
                                .negativeText(android.R.string.cancel)
                                .positiveColor(ColorHelper.getAttributeColor(mContext, R.attr.cb_colorAccent))
                                .negativeColor(ColorHelper.getAttributeColor(mContext, R.attr.cb_colorAccent))
                                .contentColor(ColorHelper.getAttributeColor(mContext, R.attr.cb_primaryText))
                                .onPositive((dialog, which) -> {
                                    CandyBarApplication.getConfiguration().getAnalyticsHandler().logEvent(
                                            "click",
                                            new HashMap<String, Object>() {{
                                                put("section", "settings");
                                                put("action", "confirm");
                                                put("item", "clear_icon_request_data");
                                            }}
                                    );
                                    Database.get(mContext).deleteIconRequestData();

                                    CandyBarMainActivity.sMissedApps = null;
                                    new IconRequestTask(mContext).executeOnThreadPool();

                                    ToastHelper.show(mContext, R.string.pref_data_request_cleared,
                                            Toast.LENGTH_LONG);
                                })
                                .onNegative(((dialog, which) -> {
                                    CandyBarApplication.getConfiguration().getAnalyticsHandler().logEvent(
                                            "click",
                                            new HashMap<String, Object>() {{
                                                put("section", "settings");
                                                put("action", "cancel");
                                                put("item", "clear_icon_request_data");
                                            }}
                                    );
                                }))
                                .show();
                        break;
                    case RESTORE:
                        CandyBarApplication.getConfiguration().getAnalyticsHandler().logEvent(
                                "click",
                                new HashMap<String, Object>() {{
                                    put("section", "settings");
                                    put("item", "restore_purchase_data");
                                    put("action", "confirm_without_dialog");
                                }}
                        );
                        new MaterialDialog.Builder(mContext)
                                .backgroundColor(ColorHelper.getAttributeColor(mContext, R.attr.cb_cardBackground))
                                .typeface(TypefaceHelper.getMedium(mContext), TypefaceHelper.getRegular(mContext))
                                .content(R.string.pref_premium_request_restore_desc)
                                .positiveText(R.string.pref_premium_request_restore)
                                .negativeText(android.R.string.cancel)
                                .positiveColor(ColorHelper.getAttributeColor(mContext, R.attr.cb_colorAccent))
                                .negativeColor(ColorHelper.getAttributeColor(mContext, R.attr.cb_colorAccent))
                                .contentColor(ColorHelper.getAttributeColor(mContext, R.attr.cb_primaryText))
                                .onPositive((dialog, which) -> {
                                    try {
                                        LogUtil.d("Attempting to restore purchases...");
                                        if (mContext == null) {
                                            throw new IllegalStateException("Context is null");
                                        }
                                        if (!(mContext instanceof InAppBillingListener)) {
                                            LogUtil.e("Context class: " + mContext.getClass().getName());
                                            throw new IllegalStateException("Activity must implement InAppBillingListener");
                                        }
                                        InAppBillingListener listener = (InAppBillingListener) mContext;
                                        LogUtil.d("Calling onRestorePurchases...");
                                        listener.onRestorePurchases();
                                    } catch (Exception e) {
                                        LogUtil.e("Failed to restore purchases: " + e.getMessage());
                                        LogUtil.e("Stack trace: " + Log.getStackTraceString(e));
                                        ToastHelper.show(mContext, R.string.pref_premium_request_restore_empty, Toast.LENGTH_LONG);
                                    }
                                })
                                .onNegative((dialog, which) -> {
                                    CandyBarApplication.getConfiguration().getAnalyticsHandler().logEvent(
                                            "click",
                                            new HashMap<String, Object>() {{
                                                put("section", "settings");
                                                put("action", "cancel");
                                                put("item", "restore_purchase_data");
                                            }}
                                    );
                                })
                                .show();
                        break;
                    case PREMIUM_REQUEST:
                        CandyBarApplication.getConfiguration().getAnalyticsHandler().logEvent(
                                "click",
                                new HashMap<String, Object>() {{
                                    put("section", "settings");
                                    put("item", "rebuild_premium_request");
                                    put("action", "confirm_without_dialog");
                                }}
                        );
                        FragmentManager fm = ((AppCompatActivity) mContext).getSupportFragmentManager();
                        if (fm == null) return;

                        Fragment fragment = fm.findFragmentByTag("SettingsFragment");
                        if (fragment == null) return;

                        if (fragment instanceof SettingsFragment) {
                            ((SettingsFragment) fragment).rebuildPremiumRequest();
                        }
                        break;
                    case THEME:
                        CandyBarApplication.getConfiguration().getAnalyticsHandler().logEvent(
                                "click",
                                new HashMap<String, Object>() {{
                                    put("section", "settings");
                                    put("item", "change_theme");
                                    put("action", "open_dialog");
                                }}
                        );
                        ThemeChooserFragment.showThemeChooser(((AppCompatActivity) mContext).getSupportFragmentManager());
                        break;
                    case LANGUAGE:
                        CandyBarApplication.getConfiguration().getAnalyticsHandler().logEvent(
                                "click",
                                new HashMap<String, Object>() {{
                                    put("section", "settings");
                                    put("item", "change_language");
                                    put("action", "open_dialog");
                                }}
                        );
                        LanguagesFragment.showLanguageChooser(((AppCompatActivity) mContext).getSupportFragmentManager());
                        break;
                    case REPORT_BUGS:
                        CandyBarApplication.getConfiguration().getAnalyticsHandler().logEvent(
                                "click",
                                new HashMap<String, Object>() {{
                                    put("section", "settings");
                                    put("item", "report_bugs");
                                    put("action", "open_dialog");
                                }}
                        );
                        ReportBugsHelper.prepareReportBugs(mContext);
                        break;
                    case CHANGELOG:
                        CandyBarApplication.getConfiguration().getAnalyticsHandler().logEvent(
                                "click",
                                new HashMap<String, Object>() {{
                                    put("section", "settings");
                                    put("item", "changelog");
                                    put("action", "open_dialog");
                                }}
                        );
                        ChangelogFragment.showChangelog(((AppCompatActivity) mContext).getSupportFragmentManager(), () -> {});
                        break;
                    case RESET_TUTORIAL:
                        CandyBarApplication.getConfiguration().getAnalyticsHandler().logEvent(
                                "click",
                                new HashMap<String, Object>() {{
                                    put("section", "settings");
                                    put("item", "reset_tutorial");
                                    put("action", "confirm_without_dialog");
                                }}
                        );
                        // Set tutorial flags first
                        Preferences.get(mContext).setTimeToShowHomeIntro(true);
                        Preferences.get(mContext).setTimeToShowIconsIntro(true);
                        Preferences.get(mContext).setTimeToShowRequestIntro(true);
                        Preferences.get(mContext).setTimeToShowWallpapersIntro(true);
                        Preferences.get(mContext).setTimeToShowWallpaperPreviewIntro(true);
                        Preferences.get(mContext).setTimeToShowRequestSearchIntro(true);
                        Preferences.get(mContext).setTimeToShowNavigationViewIntro(true);
                        Preferences.get(mContext).setTimeToShowBlackThemeIntro(true);
                        
                        // Set intro reset last to trigger the home fragment to show intro
                        Preferences.get(mContext).setIntroReset(true);

                        ToastHelper.show(mContext, R.string.pref_others_reset_tutorial_reset, Toast.LENGTH_LONG);
                        break;
                }
            }
        }
    }

    private class FooterViewHolder extends RecyclerView.ViewHolder {

        FooterViewHolder(View itemView) {
            super(itemView);
            if (!Preferences.get(mContext).isCardShadowEnabled()) {
                View shadow = itemView.findViewById(R.id.shadow);
                shadow.setVisibility(View.GONE);
            }
        }
    }

    private boolean isDarkMode() {
        int nightModeFlags = mContext.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        return nightModeFlags == Configuration.UI_MODE_NIGHT_YES;
    }

    private void setTextColorForOldAndroid(TextView textView) {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.R) {
            int color = isDarkMode() ? 
                mContext.getResources().getColor(android.R.color.white) :
                mContext.getResources().getColor(android.R.color.black);
            textView.setTextColor(color);
            // Also update drawable tint if there are any
            if (textView.getCompoundDrawables()[0] != null) {
                textView.getCompoundDrawables()[0].setTint(color);
            }
        }
    }
}

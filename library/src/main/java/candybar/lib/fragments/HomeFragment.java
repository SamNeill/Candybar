package candybar.lib.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import candybar.lib.R;
import candybar.lib.activities.CandyBarMainActivity;
import candybar.lib.adapters.HomeAdapter;
import candybar.lib.applications.CandyBarApplication;
import candybar.lib.helpers.TapIntroHelper;
import candybar.lib.helpers.WallpaperHelper;
import candybar.lib.items.Home;
import candybar.lib.items.Icon;
import candybar.lib.preferences.Preferences;
import candybar.lib.tasks.IconsLoaderTask;
import candybar.lib.utils.listeners.HomeListener;
import com.afollestad.materialdialogs.MaterialDialog;
import com.danimahardhika.android.helpers.core.ColorHelper;
import candybar.lib.helpers.TypefaceHelper;
import candybar.lib.helpers.IconsHelper;
import candybar.lib.helpers.RequestHelper;

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

public class HomeFragment extends Fragment implements HomeListener {

    public static final String TAG = "home";  // Define constant tag

    private RecyclerView mRecyclerView;
    private StaggeredGridLayoutManager mManager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        mRecyclerView = view.findViewById(R.id.recyclerview);

        if (!Preferences.get(requireActivity()).isToolbarShadowEnabled()) {
            View shadow = view.findViewById(R.id.shadow);
            if (shadow != null) shadow.setVisibility(View.GONE);
        }

        // ViewHelper.addBottomPadding(mRecyclerView);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        CandyBarApplication.getConfiguration().getAnalyticsHandler().logEvent(
                "view",
                new HashMap<String, Object>() {{ put("section", "home"); }}
        );

        mManager = new StaggeredGridLayoutManager(
                requireActivity().getResources().getInteger(R.integer.home_column_count),
                StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        // Initialize home items
        initHome();

        // Skip showing intro here - let it be handled by onHomeIntroInit
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        HomeAdapter adapter = (HomeAdapter) mRecyclerView.getAdapter();
        if (adapter != null) adapter.setOrientation(newConfig.orientation);
    }

    @Override
    public void onHomeDataUpdated(Home home) {
        if (mRecyclerView == null) return;
        if (mRecyclerView.getAdapter() == null) return;

        HomeAdapter adapter = (HomeAdapter) mRecyclerView.getAdapter();
        
        // Always update icon count if automatic counting is enabled
        if (CandyBarApplication.getConfiguration().isAutomaticIconsCountEnabled()) {
            int index = adapter.getIconsIndex();
            if (index >= 0 && index < adapter.getItemCount()) {
                Home iconHome = adapter.getItem(index);
                if (iconHome != null) {
                    iconHome.setTitle(String.valueOf(CandyBarMainActivity.sIconsCount));
                    iconHome.setLoading(false);
                    adapter.notifyItemChanged(index);

                    // Load random icon when icons are loaded
                    if (requireActivity().getResources().getBoolean(R.bool.show_random_icon) 
                        && !CandyBarMainActivity.sSections.isEmpty()) {
                        mRecyclerView.post(() -> {
                            try {
                                Random random = new Random();
                                int sectionIndex = random.nextInt(CandyBarMainActivity.sSections.size());
                                List<Icon> icons = CandyBarMainActivity.sSections.get(sectionIndex).getIcons();
                                if (!icons.isEmpty()) {
                                    int iconIndex = random.nextInt(icons.size());
                                    Icon icon = icons.get(iconIndex);

                                    BitmapFactory.Options options = new BitmapFactory.Options();
                                    options.inJustDecodeBounds = true;
                                    BitmapFactory.decodeResource(requireActivity().getResources(),
                                            icon.getRes(), options);

                                    String iconDimension = "";
                                    if (options.outWidth > 0 && options.outHeight > 0) {
                                        iconDimension = requireActivity().getResources().getString(R.string.home_icon_dimension,
                                                options.outWidth + " x " + options.outHeight);
                                    }

                                    Home randomIconHome = new Home(
                                            icon.getRes(),
                                            icon.getTitle(),
                                            iconDimension,
                                            Home.Type.DIMENSION,
                                            false);
                                    
                                    // Update or add the random icon
                                    int dimensionsIndex = adapter.getDimensionsIndex();
                                    CandyBarMainActivity.sHomeIcon = randomIconHome;
                                    if (dimensionsIndex >= 0) {
                                        adapter.notifyItemChanged(dimensionsIndex);
                                    } else {
                                        adapter.addNewContent(randomIconHome);
                                    }
                                }
                            } catch (Exception e) {
                                Log.e("HomeFragment", "Failed to load random icon", e);
                            }
                        });
                    }
                }
            }
        }

        // Handle random icon preview if provided from elsewhere
        if (home != null && home.getType() == Home.Type.DIMENSION) {
            int dimensionsIndex = adapter.getDimensionsIndex();
            if (dimensionsIndex < 0 && requireActivity().getResources().getBoolean(R.bool.show_random_icon)) {
                adapter.addNewContent(home);
            }
        }
    }

    @Override
    public void onHomeIntroInit() {
        Log.d("CandyBar", "onHomeIntroInit called in HomeFragment");
        // This method is called from ChangelogFragment after changelog is dismissed
        // or when intro is reset from settings
        if (getResources().getBoolean(R.bool.show_intro)) {
            Log.d("CandyBar", "show_intro is true, showing intro");
            showIntro();
            if (Preferences.get(requireActivity()).isIntroReset()) {
                Preferences.get(requireActivity()).setIntroReset(false);
            }
        } else {
            Log.d("CandyBar", "show_intro is false");
        }
    }

    // Helper method to show the appropriate intro
    private void showIntro() {
        Log.d("CandyBar", "showIntro called");
        boolean isBottomNav = ((CandyBarMainActivity) requireActivity()).isBottomNavigationEnabled();
        HomeAdapter adapter = (HomeAdapter) mRecyclerView.getAdapter();

        if (isBottomNav) {
            // For bottom navigation, only show apply intro
            TapIntroHelper.showHomeApplyIntro(requireActivity(),
                    mRecyclerView, mManager,
                    adapter.getApplyIndex());
        } else {
            // For sidebar navigation, show full intro
            TapIntroHelper.showHomeIntros(requireActivity(),
                    mRecyclerView, mManager,
                    adapter.getApplyIndex());
        }
    }

    @SuppressLint("StringFormatInvalid")
    private void initHome() {
        List<Home> homes = new ArrayList<>();
        final Resources resources = requireActivity().getResources();
        boolean isBottomNav = ((CandyBarMainActivity) requireActivity()).isBottomNavigationEnabled();

        if (resources.getBoolean(R.bool.enable_apply)) {
            homes.add(new Home(
                    R.drawable.ic_toolbar_apply_launcher,
                    resources.getString(R.string.home_apply_icon_pack,
                            resources.getString(R.string.app_name)),
                    "",
                    Home.Type.APPLY,
                    false));
        }

        if (resources.getBoolean(R.bool.enable_donation)) {
            homes.add(new Home(
                    R.drawable.ic_toolbar_donate,
                    resources.getString(R.string.home_donate),
                    resources.getString(R.string.home_donate_desc),
                    Home.Type.DONATE,
                    false));
        }

        // Show Kustom section in home when using bottom navigation
        if (isBottomNav && resources.getBoolean(R.bool.enable_kustom) &&
                CandyBarApplication.getConfiguration().hasKustomFolders()) {
            Log.d("CandyBar", "Adding Kustom section to home");
            homes.add(new Home(
                    R.drawable.ic_toolbar_presets,
                    resources.getString(R.string.navigation_view_kustom),
                    resources.getString(R.string.home_kustom_desc, resources.getString(R.string.app_name)),
                    Home.Type.KUSTOM,
                    false));
        }

        // Show icons count in home for both navigation styles
        boolean isLoading = CandyBarApplication.getConfiguration().isAutomaticIconsCountEnabled() && CandyBarMainActivity.sIconsCount == 0;
        homes.add(new Home(
                -1,
                CandyBarApplication.getConfiguration().isAutomaticIconsCountEnabled() ?
                        String.valueOf(CandyBarMainActivity.sIconsCount) :
                        String.valueOf(CandyBarApplication.getConfiguration().getCustomIconsCount()),
                resources.getString(R.string.home_icons),
                Home.Type.ICONS,
                isLoading));

        if (CandyBarMainActivity.sHomeIcon != null && requireActivity().getResources().getBoolean(R.bool.show_random_icon)) {
            homes.add(CandyBarMainActivity.sHomeIcon);
        }

        HomeAdapter adapter = new HomeAdapter(requireActivity(), homes,
                resources.getConfiguration().orientation);
        mRecyclerView.setAdapter(adapter);

        // Start icon loading and icon request loading if needed
        if (isLoading) {
            new Thread(() -> {
                try {
                    // Load icons
                    IconsHelper.loadIcons(requireActivity(), true);
                    
                    // Generate random icon
                    Home randomIconHome = null;
                    if (requireActivity().getResources().getBoolean(R.bool.show_random_icon) 
                        && !CandyBarMainActivity.sSections.isEmpty()) {
                        Random random = new Random();
                        int sectionIndex = random.nextInt(CandyBarMainActivity.sSections.size());
                        List<Icon> icons = CandyBarMainActivity.sSections.get(sectionIndex).getIcons();
                        if (!icons.isEmpty()) {
                            int iconIndex = random.nextInt(icons.size());
                            Icon icon = icons.get(iconIndex);

                            BitmapFactory.Options options = new BitmapFactory.Options();
                            options.inJustDecodeBounds = true;
                            BitmapFactory.decodeResource(requireActivity().getResources(),
                                    icon.getRes(), options);

                            String iconDimension = "";
                            if (options.outWidth > 0 && options.outHeight > 0) {
                                iconDimension = requireActivity().getResources().getString(R.string.home_icon_dimension,
                                        options.outWidth + " x " + options.outHeight);
                            }

                            randomIconHome = new Home(
                                    icon.getRes(),
                                    icon.getTitle(),
                                    iconDimension,
                                    Home.Type.DIMENSION,
                                    false);
                            CandyBarMainActivity.sHomeIcon = randomIconHome;
                        }
                    }

                    // Load icon request data
                    Intent intent = new Intent(Intent.ACTION_MAIN);
                    intent.addCategory(Intent.CATEGORY_LAUNCHER);
                    List<ResolveInfo> installedApps = requireActivity().getPackageManager().queryIntentActivities(
                            intent, PackageManager.GET_RESOLVED_FILTER);
                    CandyBarMainActivity.sInstalledAppsCount = installedApps.size();
                    CandyBarMainActivity.sMissedApps = RequestHelper.getMissingApps(requireActivity());

                    // Update UI on main thread
                    final Home finalRandomIconHome = randomIconHome;
                    requireActivity().runOnUiThread(() -> {
                        // Update icon count
                        int index = adapter.getIconsIndex();
                        if (index >= 0 && index < adapter.getItemCount()) {
                            Home iconHome = adapter.getItem(index);
                            if (iconHome != null) {
                                iconHome.setTitle(String.valueOf(CandyBarMainActivity.sIconsCount));
                                iconHome.setLoading(false);
                                adapter.notifyItemChanged(index);
                            }
                        }

                        // Add random icon if generated
                        if (finalRandomIconHome != null) {
                            adapter.addNewContent(finalRandomIconHome);
                        }

                        // Update icon request counts
                        adapter.notifyDataSetChanged();
                    });
                } catch (Exception e) {
                    Log.e("HomeFragment", "Failed to load icons", e);
                }
            }).start();
        }

        // By default `onHomeIntroInit` is called by the ChangelogFragment,
        // so that the intro starts after the changelog dialog is dismissed
        // But when intros are reset using the settings, there's no changelog
        // that would call `onHomeIntroInit`, so in that case we are calling
        // it from here
        if (Preferences.get(requireActivity()).isIntroReset()) {
            onHomeIntroInit();
            Preferences.get(requireActivity()).setIntroReset(false);
        }
    }

    public void resetWallpapersCount() {
        if (WallpaperHelper.getWallpaperType(requireActivity()) == WallpaperHelper.CLOUD_WALLPAPERS) {
            if (mRecyclerView == null) return;
            if (mRecyclerView.getAdapter() == null) return;

            RecyclerView.Adapter<?> adapter = mRecyclerView.getAdapter();
            if (adapter.getItemCount() > 8) {
                //Probably the original adapter already modified
                adapter.notifyDataSetChanged();
                return;
            }

            if (adapter instanceof HomeAdapter) {
                HomeAdapter homeAdapter = (HomeAdapter) adapter;
                int index = homeAdapter.getWallpapersIndex();
                if (index >= 0 && index < adapter.getItemCount()) {
                    adapter.notifyItemChanged(index);
                }
            }
        }
    }

    private void showDonateDialog() {
        new MaterialDialog.Builder(requireActivity())
                .backgroundColor(ColorHelper.getAttributeColor(requireActivity(), R.attr.cb_cardBackground))
                .title(R.string.navigation_view_donate)
                .content(R.string.donation_success)
                .positiveText(R.string.donate)
                .negativeText(R.string.close)
                .positiveColor(ColorHelper.getAttributeColor(requireActivity(), R.attr.cb_colorAccent))
                .negativeColor(ColorHelper.getAttributeColor(requireActivity(), R.attr.cb_colorAccent))
                .titleColor(ColorHelper.getAttributeColor(requireActivity(), R.attr.cb_primaryText))
                .contentColor(ColorHelper.getAttributeColor(requireActivity(), R.attr.cb_primaryText))
                .typeface(TypefaceHelper.getMedium(requireActivity()), TypefaceHelper.getRegular(requireActivity()))
                .onPositive((dialog, which) -> {
                    // ... rest of the code ...
                })
                .show();
    }
}

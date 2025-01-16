package candybar.lib.fragments;

import static candybar.lib.helpers.DrawableHelper.getPackageIcon;
import static candybar.lib.helpers.DrawableHelper.getReqIconBase64;
import static candybar.lib.helpers.ViewHelper.setFastScrollColor;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.interpolator.view.animation.LinearOutSlowInInterpolator;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.Purchase;
import com.danimahardhika.android.helpers.animation.AnimationHelper;
import com.danimahardhika.android.helpers.core.ColorHelper;
import com.danimahardhika.android.helpers.core.DrawableHelper;
import com.danimahardhika.android.helpers.core.FileHelper;
import com.danimahardhika.android.helpers.core.SoftKeyboardHelper;
import com.danimahardhika.android.helpers.core.ViewHelper;
import com.danimahardhika.android.helpers.core.utils.LogUtil;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.pluscubed.recyclerfastscroll.RecyclerFastScroller;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

import candybar.lib.R;
import candybar.lib.activities.CandyBarMainActivity;
import candybar.lib.adapters.RequestAdapter;
import candybar.lib.applications.CandyBarApplication;
import candybar.lib.databases.Database;
import candybar.lib.fragments.dialog.IntentChooserFragment;
import candybar.lib.fragments.dialog.RequestConsentDialog;
import candybar.lib.helpers.IconsHelper;
import candybar.lib.helpers.RequestHelper;
import candybar.lib.helpers.TapIntroHelper;
import candybar.lib.helpers.TypefaceHelper;
import candybar.lib.items.Request;
import candybar.lib.preferences.Preferences;
import candybar.lib.utils.AsyncTaskBase;
import candybar.lib.utils.InAppBillingClient;
import candybar.lib.utils.listeners.InAppBillingListener;
import candybar.lib.utils.listeners.RequestListener;
import candybar.lib.helpers.ToastHelper;

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

public class RequestFragment extends Fragment implements View.OnClickListener {

    private RecyclerView mRecyclerView;
    private RecyclerFastScroller mFastScroll;
    private FloatingActionButton mFab;
    private ProgressBar mProgress;
    private RequestAdapter mAdapter;
    private StaggeredGridLayoutManager mManager;
    private AsyncTaskBase mAsyncTask;
    private MenuItem mMenuItem;

    public static List<Integer> sSelectedRequests;

    private boolean noEmailClientError = false;

    public RequestAdapter getAdapter() {
        return mAdapter;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_request, container, false);
        mRecyclerView = view.findViewById(R.id.request_list);
        mFab = view.findViewById(R.id.fab);
        mFastScroll = view.findViewById(R.id.fastscroll);
        mProgress = view.findViewById(R.id.progress);

        ViewCompat.setOnApplyWindowInsetsListener(mFab, (v, insets) -> {
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            params.bottomMargin = insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom + 50;
            return WindowInsetsCompat.CONSUMED;
        });

        if (!Preferences.get(requireActivity()).isToolbarShadowEnabled()) {
            View shadow = view.findViewById(R.id.shadow);
            if (shadow != null) shadow.setVisibility(View.GONE);
        }
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        CandyBarApplication.getConfiguration().getAnalyticsHandler().logEvent(
                "view",
                new HashMap<String, Object>() {{ put("section", "icon_request"); }}
        );

        setHasOptionsMenu(true);
        resetRecyclerViewPadding(getResources().getConfiguration().orientation);

        // Set accent color for back arrow
        if (getActivity() != null) {
            androidx.appcompat.widget.Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
            if (toolbar != null && toolbar.getNavigationIcon() != null) {
                toolbar.getNavigationIcon().setTint(ColorHelper.getAttributeColor(requireContext(), R.attr.cb_colorAccent));
            }
        }

        mProgress.getIndeterminateDrawable().setColorFilter(
                ColorHelper.getAttributeColor(getActivity(), com.google.android.material.R.attr.colorSecondary),
                PorterDuff.Mode.SRC_IN);

        int color = ColorHelper.getAttributeColor(getActivity(), android.R.attr.textColorPrimary);
        Drawable tintedDrawable = ResourcesCompat.getDrawable(requireActivity().getResources(), R.drawable.ic_fab_send, null);
        assert tintedDrawable != null;
        tintedDrawable.mutate().setColorFilter(color, PorterDuff.Mode.SRC_IN);
        mFab.setImageDrawable(tintedDrawable);
        mFab.setOnClickListener(this);
        mFab.show();

        if (!Preferences.get(requireActivity()).isFabShadowEnabled()) {
            mFab.setCompatElevation(0f);
        }

        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        itemAnimator.setChangeDuration(0);
        mRecyclerView.setItemAnimator(itemAnimator);
        mManager = new StaggeredGridLayoutManager(
                requireActivity().getResources().getInteger(R.integer.request_column_count),
                StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mManager);

        setFastScrollColor(mFastScroll);
        mFastScroll.attachRecyclerView(mRecyclerView);

        mAsyncTask = new MissingAppsLoader().execute();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh the UI when returning from email client
        if (RequestFragment.sSelectedRequests != null && mAdapter != null) {
            for (Integer pos : RequestFragment.sSelectedRequests) {
                if (pos < CandyBarMainActivity.sMissedApps.size()) {
                    Request request = CandyBarMainActivity.sMissedApps.get(pos);
                    request.setRequested(true);
                    // Add to database
                    Database.get(requireActivity()).addRequest(null, request);
                }
            }
            // Notify adapter of full dataset change to properly refresh UI
            mAdapter.notifyDataSetChanged();
            // Clear selected requests after updating
            RequestFragment.sSelectedRequests = null;
            // Reset selection UI
            if (mMenuItem != null) mMenuItem.setIcon(R.drawable.ic_toolbar_select_all);
            mAdapter.resetSelectedItems();
        }
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        resetRecyclerViewPadding(newConfig.orientation);
        if (mAsyncTask != null) return;

        int[] positions = mManager.findFirstVisibleItemPositions(null);

        SparseBooleanArray selectedItems = mAdapter.getSelectedItemsArray();
        ViewHelper.resetSpanCount(mRecyclerView,
                requireActivity().getResources().getInteger(R.integer.request_column_count));

        mAdapter = new RequestAdapter(requireActivity(),
                CandyBarMainActivity.sMissedApps,
                mManager.getSpanCount());
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setSelectedItemsArray(selectedItems);

        if (positions.length > 0)
            mRecyclerView.scrollToPosition(positions[0]);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_request_search, menu);
        MenuItem search = menu.findItem(R.id.menu_search);
        MenuItem selectAll = menu.findItem(R.id.menu_select_all);
        View searchView = search.getActionView();

        // Set initial accent color for select all icon
        Drawable selectAllIcon = selectAll.getIcon();
        if (selectAllIcon != null) {
            selectAllIcon.setTint(ColorHelper.getAttributeColor(requireContext(), R.attr.cb_colorAccent));
        }

        // Set accent color for search icon
        Drawable searchIcon = search.getIcon();
        if (searchIcon != null) {
            searchIcon.setTint(ColorHelper.getAttributeColor(requireContext(), R.attr.cb_colorAccent));
        }

        EditText searchInput = searchView.findViewById(R.id.search_input);
        View clearQueryButton = searchView.findViewById(R.id.clear_query_button);

        searchInput.setHint(R.string.search_apps);
        searchInput.setTextColor(ColorHelper.getAttributeColor(requireActivity(), android.R.attr.textColorPrimary));

        // Set accent color for clear button
        int accentColor = ColorHelper.getAttributeColor(requireActivity(), R.attr.cb_colorAccent);
        if (clearQueryButton instanceof ImageButton) {
            ((ImageButton) clearQueryButton).setColorFilter(accentColor);
        }

        search.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                searchInput.requestFocus();
                if (getActivity() != null) {
                    View bottomNavigation = getActivity().findViewById(R.id.bottom_navigation);
                    if (bottomNavigation != null) {
                        bottomNavigation.setVisibility(View.GONE);
                    }
                }
                mFab.hide();
                if (mAdapter != null) {
                    mAdapter.setSearchMode(true);
                }
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    if (getActivity() != null && !getActivity().isFinishing()) {
                        SoftKeyboardHelper.openKeyboard(getActivity());
                    }
                }, 200);
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                searchInput.setText("");
                SoftKeyboardHelper.closeKeyboard(requireActivity());
                if (getActivity() != null) {
                    CandyBarMainActivity activity = (CandyBarMainActivity) getActivity();
                    View bottomNavigation = activity.findViewById(R.id.bottom_navigation);
                    if (bottomNavigation != null && activity.isBottomNavigationEnabled()) {
                        bottomNavigation.setVisibility(View.VISIBLE);
                    }
                }
                if (mAdapter != null) {
                    mAdapter.setSearchMode(false);
                }
                mFab.show();
                return true;
            }
        });

        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString();
                if (mAdapter != null) {
                    mAdapter.search(query);
                    if (mAdapter.getSelectedItemsSize() > 0) {
                        mFab.show();
                    } else {
                        mFab.hide();
                    }
                }
                clearQueryButton.setVisibility(query.isEmpty() ? View.GONE : View.VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        clearQueryButton.setOnClickListener(view -> searchInput.setText(""));

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onDestroy() {
        if (mAsyncTask != null) {
            mAsyncTask.cancel(true);
        }
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_select_all) {
            mMenuItem = item;
            if (mAdapter == null) return false;
            if (mAdapter.selectAll()) {
                Drawable icon = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_toolbar_select_all_selected, null);
                if (icon != null) {
                    icon.setTint(ColorHelper.getAttributeColor(requireContext(), R.attr.cb_colorAccent));
                    item.setIcon(icon);
                }
                return true;
            }

            Drawable icon = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_toolbar_select_all, null);
            if (icon != null) {
                icon.setTint(ColorHelper.getAttributeColor(requireContext(), R.attr.cb_colorAccent));
                item.setIcon(icon);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.fab) {
            if (mAdapter == null) return;

            int selected = mAdapter.getSelectedItemsSize();
            if (selected > 0) {
                // Check consent first
                if (!Preferences.get(requireActivity()).isRequestConsentAccepted()) {
                    RequestConsentDialog.show(requireActivity());
                    return;
                }

                boolean requestLimit = getResources().getBoolean(
                        R.bool.enable_icon_request_limit);
                boolean iconRequest = getResources().getBoolean(
                        R.bool.enable_icon_request);
                boolean premiumRequest = getResources().getBoolean(
                        R.bool.enable_premium_request);

                if (Preferences.get(requireActivity()).isPremiumRequest()) {
                    int count = Preferences.get(requireActivity()).getPremiumRequestCount();
                    if (selected > count) {
                        RequestHelper.showPremiumRequestLimitDialog(requireActivity(), selected);
                        return;
                    }

                    if (!RequestHelper.isReadyToSendPremiumRequest(requireActivity())) return;

                    try {
                        InAppBillingListener listener = (InAppBillingListener) requireActivity();
                        listener.onInAppBillingRequest();
                    } catch (Exception ignored) {
                    }
                    return;
                }

                if (!iconRequest && premiumRequest) {
                    RequestHelper.showPremiumRequestRequired(requireActivity());
                    return;
                }

                if (requestLimit) {
                    int limit = requireActivity().getResources().getInteger(R.integer.icon_request_limit);
                    int used = Preferences.get(requireActivity()).getRegularRequestUsed();
                    if (selected > (limit - used)) {
                        showLimitDialog();
                        return;
                    }
                }

                if ((requireActivity().getResources().getBoolean(R.bool.json_check_before_request)) &&
                        (CandyBarApplication.getConfiguration().getConfigHandler().configJson(requireActivity()).length() != 0)) {
                    mAsyncTask = new CheckConfig().executeOnThreadPool();
                } else {
                    mAsyncTask = new RequestLoader().executeOnThreadPool();
                }

            } else {
                ToastHelper.show(getActivity(), R.string.request_not_selected,
                        Toast.LENGTH_LONG);
            }
        }
    }

    private void resetRecyclerViewPadding(int orientation) {
        if (mRecyclerView == null) return;

        int padding = 0;
        boolean tabletMode = getResources().getBoolean(com.danimahardhika.android.helpers.core.R.bool.android_helpers_tablet_mode);
        if (tabletMode || orientation == Configuration.ORIENTATION_LANDSCAPE) {
            padding = requireActivity().getResources().getDimensionPixelSize(R.dimen.content_padding);

            if (CandyBarApplication.getConfiguration().getRequestStyle() == CandyBarApplication.Style.PORTRAIT_FLAT_LANDSCAPE_FLAT) {
                padding = requireActivity().getResources().getDimensionPixelSize(R.dimen.card_margin);
            }
        }

        int size = requireActivity().getResources().getDimensionPixelSize(R.dimen.fab_size);
        int marginGlobal = requireActivity().getResources().getDimensionPixelSize(R.dimen.fab_margin_global);

        mRecyclerView.setPadding(padding, padding, 0, size + (marginGlobal * 2));
    }

    public void prepareRequest() {
        if (mAsyncTask != null) return;

        mAsyncTask = new RequestLoader().executeOnThreadPool();
    }

    public void refreshIconRequest() {
        if (mAdapter == null) {
            RequestFragment.sSelectedRequests = null;
            return;
        }

        if (RequestFragment.sSelectedRequests == null) {
            mAdapter.notifyItemChanged(0);
            return;
        }

        for (Integer integer : RequestFragment.sSelectedRequests) {
            mAdapter.setRequested(integer, true);
        }

        mAdapter.notifyDataSetChanged();
        RequestFragment.sSelectedRequests = null;
    }

    private class MissingAppsLoader extends AsyncTaskBase {

        private List<Request> requests;

        @Override
        protected void preRun() {
            if (CandyBarMainActivity.sMissedApps == null) {
                mProgress.setVisibility(View.VISIBLE);
            }
        }

        @Override
        protected boolean run() {
            if (!isCancelled()) {
                try {
                    Thread.sleep(1);
                    if (CandyBarMainActivity.sMissedApps == null) {
                        CandyBarMainActivity.sMissedApps = RequestHelper.getMissingApps(requireActivity());
                    }

                    requests = CandyBarMainActivity.sMissedApps;
                    return true;
                } catch (Exception e) {
                    LogUtil.e(Log.getStackTraceString(e));
                    return false;
                }
            }
            return false;
        }

        @Override
        protected void postRun(boolean ok) {
            if (getActivity() == null) return;
            if (getActivity().isFinishing()) return;

            mAsyncTask = null;
            mProgress.setVisibility(View.GONE);

            if (ok) {
                setHasOptionsMenu(true);
                mAdapter = new RequestAdapter(getActivity(),
                        requests, mManager.getSpanCount());
                mRecyclerView.setAdapter(mAdapter);

                AnimationHelper.show(mFab)
                        .interpolator(new LinearOutSlowInInterpolator())
                        .start();

                if (getActivity().getResources().getBoolean(R.bool.show_intro)) {
                    TapIntroHelper.showRequestIntro(getActivity(), mRecyclerView, new TapIntroHelper.Listener() {
                        @Override
                        public void onFinish() {
                            // Show consent dialog after tutorial
                            if (!Preferences.get(requireActivity()).isRequestConsentAccepted()) {
                                RequestConsentDialog.show(requireActivity());
                            }
                        }
                    });
                }
            } else {
                mRecyclerView.setAdapter(null);
                ToastHelper.show(getActivity(), R.string.request_appfilter_failed, Toast.LENGTH_LONG);
            }
        }
    }

    private class RequestLoader extends AsyncTaskBase {

        private MaterialDialog dialog;
        private boolean isPacific;
        private String pacificApiKey;
        private boolean isCustom;
        private boolean isPremium;
        private String errorMessage;

        @Override
        protected void preRun() {
            if (Preferences.get(requireActivity()).isPremiumRequest()) {
                isPremium = true;
                isCustom = RequestHelper.isPremiumCustomEnabled(requireActivity());
                isPacific = RequestHelper.isPremiumPacificEnabled(requireActivity());
                pacificApiKey = RequestHelper.getPremiumPacificApiKey(requireActivity());
            } else {
                isPremium = false;
                isCustom = RequestHelper.isRegularCustomEnabled(requireActivity());
                isPacific = RequestHelper.isRegularPacificEnabled(requireActivity());
                pacificApiKey = RequestHelper.getRegularPacificApiKey(requireActivity());
            }

            dialog = new MaterialDialog.Builder(requireActivity())
                    .typeface(TypefaceHelper.getMedium(requireActivity()), TypefaceHelper.getRegular(requireActivity()))
                    .content(R.string.request_building)
                    .cancelable(false)
                    .canceledOnTouchOutside(false)
                    .progress(true, 0)
                    .progressIndeterminateStyle(true)
                    .backgroundColor(ColorHelper.getAttributeColor(requireActivity(), R.attr.cb_cardBackground))
                    .build();

            dialog.show();

            // Set progress bar color to accent color
            if (dialog.getProgressBar() != null) {
                dialog.getProgressBar().getIndeterminateDrawable().setColorFilter(
                        ColorHelper.getAttributeColor(requireActivity(), R.attr.cb_colorAccent),
                        PorterDuff.Mode.SRC_IN);
            }
        }

        @Override
        protected boolean run() {
            if (!isCancelled()) {
                try {
                    Thread.sleep(2);

                    RequestFragment.sSelectedRequests = mAdapter.getSelectedItems();
                    List<Request> requests = mAdapter.getSelectedApps();

                    File directory = requireActivity().getCacheDir();
                    List<String> files = new ArrayList<>();

                    for (Request request : requests) {
                        Drawable drawable = getPackageIcon(requireActivity(), request.getActivity());
                        String icon = IconsHelper.saveIcon(files, directory, drawable,
                                isPacific ? request.getPackageName() : RequestHelper.fixNameForRequest(request.getName()),
                                request::setFileName);
                        if (icon != null) files.add(icon);
                        if (isCustom) {
                            request.setIconBase64(getReqIconBase64(drawable));
                        }
                    }

                    if (isPacific) {
                        errorMessage = RequestHelper.sendPacificRequest(requests, files, directory, pacificApiKey);
                        if (errorMessage == null) {
                            for (Request request : requests) {
                                Database.get(requireActivity()).addRequest(null, request);
                            }
                        }
                        return errorMessage == null;
                    } else if (isCustom) {
                        errorMessage = RequestHelper.sendCustomRequest(requests, isPremium);
                        if (errorMessage == null) {
                            for (Request request : requests) {
                                Database.get(requireActivity()).addRequest(null, request);
                            }
                        }
                        return errorMessage == null;
                    } else {
                        boolean nonMailingAppSend = getResources().getBoolean(R.bool.enable_non_mail_app_request);
                        Intent intent;

                        if (!nonMailingAppSend) {
                            intent = new Intent(Intent.ACTION_SENDTO);
                            intent.setData(Uri.parse("mailto:"));
                        } else {
                            intent = new Intent(Intent.ACTION_SEND);
                            intent.setType("application/zip");
                        }

                        List<ResolveInfo> resolveInfos = requireActivity().getPackageManager()
                                .queryIntentActivities(intent, 0);
                        if (resolveInfos.size() == 0) {
                            noEmailClientError = true;
                            return false;
                        }

                        // Clear all cache files before generating new ones
                        File cacheDir = requireActivity().getCacheDir();
                        File[] cacheFiles = cacheDir.listFiles((dir, name) ->
                                name.endsWith(".xml") || name.endsWith(".zip"));
                        if (cacheFiles != null) {
                            for (File file : cacheFiles) {
                                if (!file.delete()) {
                                    LogUtil.e("Failed to delete cache file: " + file.getName());
                                }
                            }
                        }

                        if (Preferences.get(requireActivity()).isPremiumRequest()) {
                            AtomicBoolean hasDetailsLoaded = new AtomicBoolean(false);
                            CountDownLatch doneSignal = new CountDownLatch(1);

                            InAppBillingClient.get(requireActivity()).getClient().queryPurchasesAsync(
                                    InAppBillingClient.INAPP_PARAMS, (billingResult, purchases) -> {
                                        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                                            String premiumRequestProductId = Preferences.get(requireActivity()).getPremiumRequestProductId();
                                            for (Purchase purchase : purchases) {
                                                if (purchase.getProducts().contains(premiumRequestProductId)) {
                                                    CandyBarApplication.sRequestProperty = new Request.Property(null,
                                                            purchase.getOrderId(), premiumRequestProductId);
                                                    hasDetailsLoaded.set(true);
                                                    break;
                                                } else {
                                                    LogUtil.e("Unable to find premiumRequestProductId in the Products");
                                                }
                                            }
                                        } else {
                                            LogUtil.e("Failed to load purchase data. Response Code: " + billingResult.getResponseCode());
                                        }

                                        doneSignal.countDown();
                                    });

                            doneSignal.await();

                            if (!hasDetailsLoaded.get()) return false;
                        }

                        File appFilter = RequestHelper.buildXml(requireActivity(), requests, RequestHelper.XmlType.APPFILTER);
                        File appMap = RequestHelper.buildXml(requireActivity(), requests, RequestHelper.XmlType.APPMAP);
                        File themeResources = RequestHelper.buildXml(requireActivity(), requests, RequestHelper.XmlType.THEME_RESOURCES);

                        if (appFilter != null) files.add(appFilter.toString());

                        if (appMap != null) files.add(appMap.toString());

                        if (themeResources != null) files.add(themeResources.toString());

                        CandyBarApplication.sZipPath = FileHelper.createZip(files, new File(directory.toString(),
                                RequestHelper.getGeneratedZipName(RequestHelper.ZIP)));

                        // Copy zip file to Documents folder in app-specific directory
                        if (CandyBarApplication.sZipPath != null) {
                            File sourceZip = new File(CandyBarApplication.sZipPath);
                            if (sourceZip.exists()) {
                                try {
                                    String appName = requireActivity().getString(requireActivity().getApplicationInfo().labelRes);
                                    String folderPath = Environment.DIRECTORY_DOCUMENTS + File.separator + appName;

                                    // Delete existing zip files in the app's Documents folder
                                    ContentResolver resolver = requireActivity().getContentResolver();
                                    Uri externalUri = MediaStore.Files.getContentUri("external");
                                    String selection = MediaStore.MediaColumns.RELATIVE_PATH + "=? AND " +
                                            MediaStore.MediaColumns.MIME_TYPE + "=?";
                                    String[] selectionArgs = new String[]{folderPath + File.separator, "application/zip"};

                                    Cursor cursor = resolver.query(externalUri, new String[]{MediaStore.MediaColumns._ID},
                                            selection, selectionArgs, null);
                                    if (cursor != null) {
                                        while (cursor.moveToNext()) {
                                            long id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID));
                                            Uri deleteUri = ContentUris.withAppendedId(externalUri, id);
                                            resolver.delete(deleteUri, null, null);
                                        }
                                        cursor.close();
                                    }

                                    // Copy new zip file
                                    ContentValues values = new ContentValues();
                                    values.put(MediaStore.MediaColumns.DISPLAY_NAME, sourceZip.getName());
                                    values.put(MediaStore.MediaColumns.MIME_TYPE, "application/zip");
                                    values.put(MediaStore.MediaColumns.RELATIVE_PATH, folderPath);

                                    Uri targetUri = resolver.insert(externalUri, values);

                                    if (targetUri != null) {
                                        OutputStream out = resolver.openOutputStream(targetUri);
                                        if (out != null) {
                                            FileInputStream in = new FileInputStream(sourceZip);
                                            byte[] buffer = new byte[1024];
                                            int len;
                                            while ((len = in.read(buffer)) > 0) {
                                                out.write(buffer, 0, len);
                                            }
                                            in.close();
                                            out.close();
                                        }
                                    }
                                } catch (Exception e) {
                                    LogUtil.e("Failed to copy zip to Documents: " + e.getMessage());
                                }
                            }
                        }
                    }
                    return true;
                } catch (RuntimeException | InterruptedException e) {
                    LogUtil.e(Log.getStackTraceString(e));
                    return false;
                }
            }
            return false;
        }

        @Override
        protected void postRun(boolean ok) {
            if (getActivity() == null) return;
            if (getActivity().isFinishing()) return;

            dialog.dismiss();
            mAsyncTask = null;
            dialog = null;

            if (ok) {
                if (isPacific || isCustom) {
                    // Update UI immediately after successful request
                    if (mAdapter != null && RequestFragment.sSelectedRequests != null) {
                        for (Integer pos : RequestFragment.sSelectedRequests) {
                            if (pos < CandyBarMainActivity.sMissedApps.size()) {
                                Request request = CandyBarMainActivity.sMissedApps.get(pos);
                                request.setRequested(true);
                                mAdapter.notifyItemChanged(pos);
                            }
                        }
                    }
                    int toastText = isPacific ? R.string.request_pacific_success : R.string.request_custom_success;
                    ToastHelper.show(getActivity(), toastText, Toast.LENGTH_LONG);
                } else {
                    // For email requests, mark as requested only after the email is sent
                    IntentChooserFragment.showIntentChooserDialog(getActivity().getSupportFragmentManager(),
                            IntentChooserFragment.ICON_REQUEST);
                }
                mAdapter.resetSelectedItems();
                if (mMenuItem != null) mMenuItem.setIcon(R.drawable.ic_toolbar_select_all);
            } else {
                if (isPacific || isCustom) {
                    int content = isPacific ? R.string.request_pacific_error : R.string.request_custom_error;
                    new MaterialDialog.Builder(getActivity())
                            .typeface(TypefaceHelper.getMedium(getActivity()), TypefaceHelper.getRegular(getActivity()))
                            .content(content, "\"" + errorMessage + "\"")
                            .cancelable(true)
                            .canceledOnTouchOutside(false)
                            .positiveText(R.string.close)
                            .build()
                            .show();
                } else if (noEmailClientError) {
                    ToastHelper.show(getActivity(), R.string.no_email_app,
                            Toast.LENGTH_LONG);
                } else {
                    ToastHelper.show(getActivity(), R.string.request_build_failed,
                            Toast.LENGTH_LONG);
                }
            }
        }
    }

    public class CheckConfig extends AsyncTaskBase {

        private MaterialDialog dialog;
        private boolean canRequest = true;
        private String updateUrl;

        @Override
        protected void preRun() {
            dialog = new MaterialDialog.Builder(requireActivity())
                    .typeface(TypefaceHelper.getMedium(requireActivity()), TypefaceHelper.getRegular(requireActivity()))
                    .content(R.string.request_fetching_data)
                    .cancelable(false)
                    .canceledOnTouchOutside(false)
                    .progress(true, 0)
                    .progressIndeterminateStyle(true)
                    .build();

            dialog.show();
        }

        @Override
        protected boolean run() {
            if (!isCancelled()) {
                String configJsonUrl = CandyBarApplication.getConfiguration().getConfigHandler().configJson(requireActivity());
                URLConnection urlConnection;
                BufferedReader bufferedReader = null;

                try {
                    urlConnection = new URL(configJsonUrl).openConnection();
                    bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

                    String line;
                    StringBuilder stringBuilder = new StringBuilder();
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line);
                    }

                    PackageInfo packageInfo = requireActivity().getPackageManager()
                            .getPackageInfo(requireActivity().getPackageName(), 0);
                    JSONObject configJson = new JSONObject(stringBuilder.toString());
                    if (configJson.isNull("url")) {
                        // Default to Play Store
                        updateUrl = "https://play.google.com/store/apps/details?id=" + packageInfo.packageName;
                    } else {
                        updateUrl = configJson.getString("url");
                    }

                    JSONObject disableRequestObj = configJson.getJSONObject("disableRequest");
                    long disableRequestBelow = disableRequestObj.optLong("below", 0);
                    String disableRequestOn = disableRequestObj.optString("on", "");
                    long appVersionCode = Build.VERSION.SDK_INT >= Build.VERSION_CODES.P
                            ? packageInfo.getLongVersionCode() : packageInfo.versionCode;

                    if ((appVersionCode < disableRequestBelow) ||
                            disableRequestOn.matches(".*\\b" + appVersionCode + "\\b.*")) {
                        canRequest = false;
                    }

                    return true;
                } catch (Exception ex) {
                    LogUtil.e("Error loading Configuration JSON " + Log.getStackTraceString(ex));
                } finally {
                    if (bufferedReader != null) {
                        try {
                            bufferedReader.close();
                        } catch (IOException e) {
                            LogUtil.e(Log.getStackTraceString(e));
                        }
                    }
                }
            }
            return false;
        }

        @Override
        protected void postRun(boolean ok) {
            dialog.dismiss();
            dialog = null;

            if (ok) {
                if (!canRequest) {
                    new MaterialDialog.Builder(requireActivity())
                            .typeface(TypefaceHelper.getMedium(requireActivity()), TypefaceHelper.getRegular(requireActivity()))
                            .content(R.string.request_app_disabled)
                            .negativeText(R.string.close)
                            .positiveText(R.string.update)
                            .onPositive(((dialog, which) -> {
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(updateUrl));
                                intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                                requireActivity().startActivity(intent);
                            }))
                            .cancelable(false)
                            .canceledOnTouchOutside(false)
                            .build()
                            .show();

                    mAdapter.resetSelectedItems();
                    if (mMenuItem != null) mMenuItem.setIcon(R.drawable.ic_toolbar_select_all);
                } else {
                    mAsyncTask = new RequestLoader().executeOnThreadPool();
                }
            } else {
                new MaterialDialog.Builder(requireActivity())
                        .typeface(TypefaceHelper.getMedium(requireActivity()), TypefaceHelper.getRegular(requireActivity()))
                        .content(R.string.unable_to_load_config)
                        .canceledOnTouchOutside(false)
                        .positiveText(R.string.close)
                        .build()
                        .show();
            }
        }
    }

    private void showLimitDialog() {
        if (!Preferences.get(requireActivity()).isRegularRequestLimit()) return;

        int limit = requireActivity().getResources().getInteger(R.integer.icon_request_limit);
        int used = Preferences.get(requireActivity()).getRegularRequestUsed();

        new MaterialDialog.Builder(requireActivity())
                .backgroundColor(ColorHelper.getAttributeColor(requireActivity(), R.attr.cb_cardBackground))
                .title(R.string.request_limit_title)
                .content(String.format(requireActivity().getResources().getString(
                        R.string.request_limit_content), (limit - used)))
                .positiveText(R.string.close)
                .positiveColor(ColorHelper.getAttributeColor(requireActivity(), R.attr.cb_colorAccent))
                .titleColor(ColorHelper.getAttributeColor(requireActivity(), R.attr.cb_primaryText))
                .contentColor(ColorHelper.getAttributeColor(requireActivity(), R.attr.cb_primaryText))
                .typeface(TypefaceHelper.getMedium(requireActivity()), TypefaceHelper.getRegular(requireActivity()))
                .show();
    }
}

package candybar.lib.activities;

import static candybar.lib.helpers.DrawableHelper.getDrawableId;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.URLUtil;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.graphics.drawable.DrawerArrowDrawable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.ConsumeParams;
import com.android.billingclient.api.Purchase;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.danimahardhika.android.helpers.core.ColorHelper;
import com.danimahardhika.android.helpers.core.DrawableHelper;
import com.danimahardhika.android.helpers.core.FileHelper;
import com.danimahardhika.android.helpers.core.SoftKeyboardHelper;
import com.danimahardhika.android.helpers.core.utils.LogUtil;
import com.danimahardhika.android.helpers.license.LicenseHelper;
import com.danimahardhika.android.helpers.permission.PermissionCode;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import candybar.lib.R;
import candybar.lib.applications.CandyBarApplication;
import candybar.lib.databases.Database;
import candybar.lib.fragments.AboutFragment;
import candybar.lib.fragments.ApplyFragment;
import candybar.lib.fragments.FAQsFragment;
import candybar.lib.fragments.HomeFragment;
import candybar.lib.fragments.IconsBaseFragment;
import candybar.lib.fragments.PresetsFragment;
import candybar.lib.fragments.RequestFragment;
import candybar.lib.fragments.SettingsFragment;
import candybar.lib.fragments.WallpapersFragment;
import candybar.lib.fragments.dialog.ChangelogFragment;
import candybar.lib.fragments.dialog.InAppBillingFragment;
import candybar.lib.fragments.dialog.IntentChooserFragment;
import candybar.lib.fragments.dialog.PrivacyPolicyDialog;
import candybar.lib.fragments.dialog.RequestConsentDialog;
import candybar.lib.helpers.ConfigurationHelper;
import candybar.lib.helpers.IntentHelper;
import candybar.lib.helpers.JsonHelper;
import candybar.lib.helpers.LicenseCallbackHelper;
import candybar.lib.helpers.LocaleHelper;
import candybar.lib.helpers.NavigationViewHelper;
import candybar.lib.helpers.RequestHelper;
import candybar.lib.helpers.ThemeHelper;
import candybar.lib.helpers.TypefaceHelper;
import candybar.lib.helpers.WallpaperHelper;
import candybar.lib.items.Home;
import candybar.lib.items.Icon;
import candybar.lib.items.InAppBilling;
import candybar.lib.items.Request;
import candybar.lib.items.Wallpaper;
import candybar.lib.preferences.Preferences;
import candybar.lib.services.CandyBarService;
import candybar.lib.tasks.IconRequestTask;
import candybar.lib.tasks.IconsLoaderTask;
import candybar.lib.tasks.WallpaperThumbPreloaderTask;
import candybar.lib.utils.CandyBarGlideModule;
import candybar.lib.utils.Extras;
import candybar.lib.utils.InAppBillingClient;
import candybar.lib.utils.listeners.InAppBillingListener;
import candybar.lib.utils.listeners.RequestListener;
import candybar.lib.utils.listeners.SearchListener;
import candybar.lib.utils.listeners.WallpapersListener;
import candybar.lib.utils.views.HeaderView;
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

public abstract class CandyBarMainActivity extends AppCompatActivity implements
        ActivityCompat.OnRequestPermissionsResultCallback, RequestListener, InAppBillingListener,
        SearchListener, WallpapersListener {

    private TextView mToolbarTitle;
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private BottomNavigationView mBottomNavigationView;

    private Extras.Tag mFragmentTag;
    private int mPosition, mLastPosition;
    private int mPreviousPosition = -1;
    private ActionBarDrawerToggle mDrawerToggle;
    private FragmentManager mFragManager;
    private LicenseHelper mLicenseHelper;

    private boolean mIsMenuVisible = true;
    private boolean prevIsDarkTheme;
    private boolean isBottomNavigation;

    public static List<Request> sMissedApps;
    public static List<Icon> sSections;
    public static Home sHomeIcon;
    public static int sInstalledAppsCount;
    public static int sIconsCount;

    private ActivityConfiguration mConfig;

    private Handler mTimesVisitedHandler;
    private Runnable mTimesVisitedRunnable;

    private static final int NOTIFICATION_PERMISSION_CODE = 10;

    @NonNull
    public abstract ActivityConfiguration onInit();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        final boolean isMaterialYou = Preferences.get(this).isMaterialYou();
        final boolean isPureBlack = Preferences.get(this).isPureBlack();
        final int nightMode;
        switch (Preferences.get(this).getTheme()) {
            case LIGHT:
                nightMode = AppCompatDelegate.MODE_NIGHT_NO;
                break;
            case DARK:
                nightMode = AppCompatDelegate.MODE_NIGHT_YES;
                break;
            default:
                nightMode = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
                break;
        }
        AppCompatDelegate.setDefaultNightMode(nightMode);

        LocaleHelper.setLocale(this);
        super.onCreate(savedInstanceState);
        
        // Apply the correct theme based on settings
        if (ThemeHelper.isDarkTheme(this)) {
            if (isPureBlack) {
                if (isMaterialYou && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    setTheme(R.style.CandyBar_Theme_App_MaterialYou_PureBlack);
                } else {
                    setTheme(R.style.CandyBar_Theme_App_PureBlack);
                }
            } else if (isMaterialYou && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                setTheme(R.style.CandyBar_Theme_App_MaterialYou);
            } else {
                setTheme(R.style.CandyBar_Theme_App_DayNight);
            }
        } else {
            if (isMaterialYou && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                setTheme(R.style.CandyBar_Theme_App_MaterialYou);
            } else {
                setTheme(R.style.CandyBar_Theme_App_DayNight);
            }
        }

        setContentView(R.layout.activity_main);

        // Show privacy policy dialog before anything else
        PrivacyPolicyDialog.show(this);

        // Initialize views
        mToolbarTitle = findViewById(R.id.toolbar_title);
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mNavigationView = findViewById(R.id.navigation_view);
        mBottomNavigationView = findViewById(R.id.bottom_navigation);

        // Initialize toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.R) {
            // For Android 11 and below, use explicit light/dark themes
            boolean isDarkMode = (getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK)
                    == Configuration.UI_MODE_NIGHT_YES;
            toolbar.setPopupTheme(isDarkMode ? 
                (isPureBlack ? R.style.CandyBar_PopupMenu_PureBlack : R.style.CandyBar_PopupMenu_Dark) : 
                R.style.CandyBar_PopupMenu_Light);
        } else {
            // For Android 12+, use Material You or day/night themes
            toolbar.setPopupTheme(ThemeHelper.isDarkTheme(this) && isPureBlack ? 
                R.style.CandyBar_PopupMenu_PureBlack : 
                (isMaterialYou ? R.style.CandyBar_Theme_App_MaterialYou : R.style.CandyBar_Theme_App_DayNight));
        }
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        // Set window flags for transparent system bars
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        getWindow().setNavigationBarColor(Color.TRANSPARENT);

        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().clearFlags(
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION | WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        // Set status bar appearance based on theme
        boolean isLightTheme = (getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK)
                == Configuration.UI_MODE_NIGHT_NO;
        if (isLightTheme) {
            int systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                systemUiVisibility |= View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
            }
            getWindow().getDecorView().setSystemUiVisibility(systemUiVisibility);
        }

        mFragManager = getSupportFragmentManager();

        isBottomNavigation = CandyBarApplication.getConfiguration().getNavigationViewStyle()
                == CandyBarApplication.NavigationViewStyle.BOTTOM_NAVIGATION;

        // Hide navigation icon and lock drawer when using bottom navigation
        if (isBottomNavigation) {
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                getSupportActionBar().setHomeButtonEnabled(false);
            }
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            mNavigationView.setVisibility(View.GONE);
            mBottomNavigationView.setVisibility(View.VISIBLE);
            initBottomNavigation();
        } else {
            mNavigationView.setVisibility(View.VISIBLE);
            mBottomNavigationView.setVisibility(View.GONE);
            initNavigationView(toolbar);
            initNavigationViewHeader();
        }

        ViewCompat.setOnApplyWindowInsetsListener(toolbar, (v, insets) -> {
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            params.topMargin = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top;
            findViewById(R.id.inset_padding).getLayoutParams().height = params.topMargin;
            return WindowInsetsCompat.CONSUMED;
        });

        try {
            startService(new Intent(this, CandyBarService.class));
        } catch (IllegalStateException e) {
            LogUtil.e("Unable to start CandyBarService. App is probably running in background.");
        }

        //Todo: wait until google fix the issue, then enable wallpaper crop again on API 26+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Preferences.get(this).setCropWallpaper(false);
        }

        mConfig = onInit();
        InAppBillingClient.get(this).init();

        mPosition = mLastPosition = 0;
        if (savedInstanceState != null) {
            mPosition = mLastPosition = savedInstanceState.getInt(Extras.EXTRA_POSITION, 0);
            onSearchExpanded(false);
        }

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            int position = bundle.getInt(Extras.EXTRA_POSITION, -1);
            if (position >= 0 && position < 6) {
                mPosition = mLastPosition = position;
            }
        }

        IntentHelper.sAction = IntentHelper.getAction(getIntent());
        if (IntentHelper.sAction == IntentHelper.ACTION_DEFAULT) {
            setFragment(getFragment(mPosition));
        } else {
            setFragment(getActionFragment(IntentHelper.sAction));
        }

        checkWallpapers();
        new WallpaperThumbPreloaderTask(this).execute();
        new IconRequestTask(this).executeOnThreadPool();
        new IconsLoaderTask(this).execute();

        /*
        The below code does this
        #1. If new version - set `firstRun` to `true`
        #2. If `firstRun` equals `true`, run the following steps
            #X. License check
                - Enabled: Run check, when completed run #Y
                - Disabled: Run #Y
            #Y. Reset icon request limit, clear cache and show changelog
        */

        if (Preferences.get(this).isNewVersion()) {
            // Check licenses on new version
            Preferences.get(this).setFirstRun(true);
        }

        final Runnable askNotificationPermission = () -> {
            final Runnable showToast = () -> {
                Toast.makeText(this, getResources().getString(R.string.permission_notification_denied_1), Toast.LENGTH_LONG).show();
                Toast.makeText(this, getResources().getString(R.string.permission_notification_denied_2), Toast.LENGTH_LONG).show();
            };

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && CandyBarApplication.getConfiguration().isNotificationEnabled()) {
                if (NotificationManagerCompat.from(this).areNotificationsEnabled()) {
                    CandyBarApplication.getConfiguration().getNotificationHandler().setMode(Preferences.get(this).isNotificationsEnabled());
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        int permissionState = ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS);
                        if (permissionState != PackageManager.PERMISSION_GRANTED) {
                            if (shouldShowRequestPermissionRationale(android.Manifest.permission.POST_NOTIFICATIONS)) {
                                showToast.run();
                            } else {
                                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, NOTIFICATION_PERMISSION_CODE);
                            }
                        }
                    } else {
                        showToast.run();
                    }
                }
            }
        };

        final Runnable onNewVersion = () -> {
            ChangelogFragment.showChangelog(mFragManager, askNotificationPermission);
            File cache = getCacheDir();
            FileHelper.clearDirectory(cache);
        };

        if (Preferences.get(this).isFirstRun()) {
            final Runnable checkLicenseIfEnabled = () -> {
                final Runnable onAllChecksCompleted = () -> {
                    Preferences.get(this).setFirstRun(false);
                    onNewVersion.run();
                };

                if (mConfig.isLicenseCheckerEnabled()) {
                    mLicenseHelper = new LicenseHelper(this);
                    mLicenseHelper.run(mConfig.getLicenseKey(), mConfig.getRandomString(),
                            new LicenseCallbackHelper(this, onAllChecksCompleted));
                } else {
                    onAllChecksCompleted.run();
                }
            };

            checkLicenseIfEnabled.run();

            return;
        }

        if (mConfig.isLicenseCheckerEnabled() && !Preferences.get(this).isLicensed()) {
            finish();
        }

        if (getResources().getBoolean(R.bool.enable_in_app_review)) {
            int timesVisited = Preferences.get(this).getTimesVisited();
            int afterVisits = getResources().getInteger(R.integer.in_app_review_after_visits);
            int nextReviewVisitIdx = Preferences.get(this).getNextReviewVisit();

            if (timesVisited == afterVisits || (timesVisited > afterVisits && timesVisited == nextReviewVisitIdx)) {
                ReviewManager manager = ReviewManagerFactory.create(this);
                Task<ReviewInfo> request = manager.requestReviewFlow();
                request.addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        ReviewInfo reviewInfo = task.getResult();
                        manager.launchReviewFlow(this, reviewInfo);

                        Preferences.get(this).setNextReviewVisit(timesVisited + 3);
                        // We are scheduling next review to be on 3rd visit from the current visit
                    } else {
                        LogUtil.e(Log.getStackTraceString(task.getException()));
                    }
                });
            }

            mTimesVisitedHandler = new Handler(Looper.getMainLooper());
            mTimesVisitedRunnable = () -> Preferences.get(this).setTimesVisited(timesVisited + 1);
            mTimesVisitedHandler.postDelayed(mTimesVisitedRunnable, getResources().getInteger(R.integer.in_app_review_visit_time) * 1000L);
        }

        askNotificationPermission.run();

        // Show privacy policy dialog
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (!isBottomNavigation) {
            mDrawerToggle.syncState();
        }
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (prevIsDarkTheme != ThemeHelper.isDarkTheme(this)) {
            recreate();
            return;
        }
        LocaleHelper.setLocale(this);
        if (mIsMenuVisible && !isBottomNavigation) {
            mDrawerToggle.onConfigurationChanged(newConfig);
        }
        invalidateOptionsMenu(); // Refresh overflow menu
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        LocaleHelper.setLocale(newBase);
        super.attachBaseContext(newBase);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        int action = IntentHelper.getAction(intent);
        if (action != IntentHelper.ACTION_DEFAULT)
            setFragment(getActionFragment(action));
        super.onNewIntent(intent);
    }

    @Override
    protected void onResume() {
        RequestHelper.checkPiracyApp(this);
        IntentHelper.sAction = IntentHelper.getAction(getIntent());
        super.onResume();
        InAppBillingClient.get(this).checkForUnprocessedPurchases();
    }

    @Override
    protected void onDestroy() {
        InAppBillingClient.get(this).destroy();

        if (mLicenseHelper != null) {
            mLicenseHelper.destroy();
        }

        CandyBarMainActivity.sMissedApps = null;
        CandyBarMainActivity.sHomeIcon = null;
        stopService(new Intent(this, CandyBarService.class));
        Database.get(this.getApplicationContext()).closeDatabase();
        if (mTimesVisitedHandler != null) {
            mTimesVisitedHandler.removeCallbacks(mTimesVisitedRunnable);
        }
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(Extras.EXTRA_POSITION, mPosition);
        Database.get(this.getApplicationContext()).closeDatabase();
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        if (isBottomNavigation) {
            Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.container);

            // Check if current fragment is RequestFragment and has selected items
            if (currentFragment instanceof RequestFragment) {
                RequestFragment requestFragment = (RequestFragment) currentFragment;
                if (requestFragment.getAdapter() != null && requestFragment.getAdapter().getSelectedItemsSize() > 0) {
                    requestFragment.getAdapter().resetSelectedItems();
                    return;
                }
            }

            // Check if current fragment is from overflow menu (excluding Presets/Kustom)
            if (currentFragment instanceof FAQsFragment ||
                    currentFragment instanceof SettingsFragment ||
                    currentFragment instanceof AboutFragment) {

                // Show bottom navigation when returning from these fragments
                if (mBottomNavigationView != null) {
                    mBottomNavigationView.setVisibility(View.VISIBLE);
                }

                // For fragments that need to go home first (like RequestFragment)
                Fragment previousFragment = getFragment(mPreviousPosition);
                if (previousFragment instanceof RequestFragment) {
                    // First go to home
                    setFragment(new HomeFragment());
                    // Then delay the navigation to the actual target
                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
                        mPosition = mPreviousPosition;
                        setFragment(previousFragment);
                        mPreviousPosition = -1;
                    }, 100);
                    return;
                }

                // For other fragments, return to previous position directly
                if (mPreviousPosition != -1) {
                    mPosition = mPreviousPosition;
                    setFragment(getFragment(mPosition));
                    mPreviousPosition = -1;
                    return;
                } else {
                    // If no previous position stored, go to home
                    mPosition = 0;
                    mFragmentTag = Extras.Tag.HOME;
                    setFragment(getFragment(mPosition));
                    return;
                }
            } else if (currentFragment instanceof HomeFragment) {
                // If we're in Home, let the system handle back (exit app)
                super.onBackPressed();
                return;
            } else {
                // For any other fragment (including Presets/Kustom), go back to Home
                mPosition = 0;
                mFragmentTag = Extras.Tag.HOME;
                setFragment(getFragment(mPosition));
            }
        } else {
            // Handle back press for sidebar navigation
            if (mFragManager.getBackStackEntryCount() > 0) {
                clearBackStack();
                return;
            }

            if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
                mDrawerLayout.closeDrawers();
                return;
            }

            Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.container);
            // Check if current fragment is RequestFragment and has selected items
            if (currentFragment instanceof RequestFragment) {
                RequestFragment requestFragment = (RequestFragment) currentFragment;
                if (requestFragment.getAdapter() != null && requestFragment.getAdapter().getSelectedItemsSize() > 0) {
                    requestFragment.getAdapter().resetSelectedItems();
                    return;
                }
            }

            if (currentFragment instanceof HomeFragment) {
                // If we're in Home, exit app
                super.onBackPressed();
            } else {
                // If we're in any other section, go to Home first
                mPosition = 0;
                mFragmentTag = Extras.Tag.HOME;
                setFragment(getFragment(mPosition));
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PermissionCode.STORAGE) {
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                recreate();
                return;
            }
            ToastHelper.show(this, R.string.permission_storage_denied, Toast.LENGTH_LONG);
        }
        if (requestCode == NOTIFICATION_PERMISSION_CODE) {
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                CandyBarApplication.getConfiguration().getNotificationHandler()
                        .setMode(Preferences.get(this).isNotificationsEnabled());
            } else {
                ToastHelper.show(this, getResources().getString(R.string.permission_notification_denied_1), Toast.LENGTH_LONG);
                ToastHelper.show(this, getResources().getString(R.string.permission_notification_denied_2), Toast.LENGTH_LONG);
            }
        }
    }

    @Override
    public void onPiracyAppChecked(boolean isPiracyAppInstalled) {
        MenuItem menuItem = mNavigationView.getMenu().findItem(R.id.navigation_view_request);
        if (menuItem != null) {
            menuItem.setVisible(getResources().getBoolean(
                    R.bool.enable_icon_request) || !isPiracyAppInstalled);
        }
    }

    @Override
    public void onRequestSelected(int count) {
        if (mFragmentTag == Extras.Tag.REQUEST) {
            String title = getResources().getString(R.string.navigation_view_request);
            if (count > 0) title += " (" + count + ")";
            mToolbarTitle.setText(title);
        }
    }

    @Override
    public void onBuyPremiumRequest() {
        if (Preferences.get(this).isPremiumRequest()) {
            RequestHelper.showPremiumRequestStillAvailable(this);
            return;
        }

        if (this.getResources().getBoolean(R.bool.enable_restore_purchases)) {
            CountDownLatch doneSignal = new CountDownLatch(1);
            AtomicBoolean doesProductIdExist = new AtomicBoolean(false);
            InAppBillingClient.get(this.getApplicationContext()).getClient()
                    .queryPurchasesAsync(InAppBillingClient.INAPP_PARAMS, (billingResult, purchases) -> {
                        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                            for (Purchase purchase : purchases) {
                                for (String premiumRequestProductId : mConfig.getPremiumRequestProductsId()) {
                                    if (purchase.getProducts().contains(premiumRequestProductId)) {
                                        doesProductIdExist.set(true);
                                        break;
                                    }
                                }
                            }
                        } else {
                            LogUtil.e("Failed to query purchases. Response Code: " + billingResult.getResponseCode());
                        }

                        doneSignal.countDown();
                    });

            try {
                doneSignal.await();
            } catch (InterruptedException e) {
                LogUtil.e(Log.getStackTraceString(e));
            }

            if (doesProductIdExist.get()) {
                RequestHelper.showPremiumRequestExist(this);
                return;
            }
        }

        InAppBillingFragment.showInAppBillingDialog(getSupportFragmentManager(),
                InAppBilling.PREMIUM_REQUEST,
                mConfig.getLicenseKey(),
                mConfig.getPremiumRequestProductsId(),
                mConfig.getPremiumRequestProductsCount());
    }

    @Override
    public void onRequestBuilt(Intent intent, int type) {
        if (type == IntentChooserFragment.ICON_REQUEST) {
            if (RequestFragment.sSelectedRequests == null)
                return;

            if (Preferences.get(this).isPremiumRequest()) {
                int count = Preferences.get(this).getPremiumRequestCount() - RequestFragment.sSelectedRequests.size();
                Preferences.get(this).setPremiumRequestCount(count);
                if (count == 0) {
                    AtomicReference<List<Purchase>> purchases = new AtomicReference<>();
                    CountDownLatch queryDoneSignal = new CountDownLatch(1);

                    InAppBillingClient.get(this).getClient()
                            .queryPurchasesAsync(InAppBillingClient.INAPP_PARAMS, (billingResult, aPurchases) -> {
                                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                                    purchases.set(aPurchases);
                                } else {
                                    LogUtil.e("Failed to load purchase data. Response Code: " + billingResult.getResponseCode());
                                }
                                queryDoneSignal.countDown();
                            });

                    try {
                        queryDoneSignal.await();
                    } catch (InterruptedException e) {
                        LogUtil.e(Log.getStackTraceString(e));
                    }

                    AtomicBoolean isConsumeSuccess = new AtomicBoolean(false);
                    if (purchases.get() != null) {
                        String premiumRequestProductId = Preferences.get(this).getPremiumRequestProductId();
                        for (Purchase purchase : purchases.get()) {
                            if (purchase.getProducts().contains(premiumRequestProductId)) {
                                CountDownLatch consumeDoneSignal = new CountDownLatch(1);
                                InAppBillingClient.get(this).getClient().consumeAsync(
                                        ConsumeParams.newBuilder()
                                                .setPurchaseToken(purchase.getPurchaseToken())
                                                .build(),
                                        (billingResult, s) -> {
                                            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                                                isConsumeSuccess.set(true);
                                            } else {
                                                LogUtil.e("Failed to consume premium request product. Response Code: " + billingResult.getResponseCode());
                                            }
                                            consumeDoneSignal.countDown();
                                        }
                                );
                                try {
                                    consumeDoneSignal.await();
                                } catch (InterruptedException e) {
                                    LogUtil.e(Log.getStackTraceString(e));
                                }
                                break;
                            }
                        }
                    }

                    if (isConsumeSuccess.get()) {
                        Preferences.get(this).setPremiumRequest(false);
                        Preferences.get(this).setPremiumRequestProductId("");
                    } else {
                        RequestHelper.showPremiumRequestConsumeFailed(this);
                        return;
                    }
                }
            } else {
                if (getResources().getBoolean(R.bool.enable_icon_request_limit)) {
                    int used = Preferences.get(this).getRegularRequestUsed();
                    Preferences.get(this).setRegularRequestUsed((used + RequestFragment.sSelectedRequests.size()));
                }
            }

            if (mFragmentTag == Extras.Tag.REQUEST) {
                RequestFragment fragment = (RequestFragment) mFragManager.findFragmentByTag(Extras.Tag.REQUEST.value);
                if (fragment != null) fragment.refreshIconRequest();
            }
        }

        if (intent != null) {
            try {
                startActivity(intent);
            } catch (IllegalArgumentException e) {
                startActivity(Intent.createChooser(intent,
                        getResources().getString(R.string.app_client)));
            }
        }

        CandyBarApplication.sRequestProperty = null;
        CandyBarApplication.sZipPath = null;
    }

    @Override
    public void onRestorePurchases() {
        LogUtil.d("Starting purchase query...");
        InAppBillingClient billingClient = InAppBillingClient.get(this);
        if (billingClient == null) {
            LogUtil.e("Billing client is null");
            ToastHelper.show(this, R.string.pref_premium_request_restore_empty, Toast.LENGTH_LONG);
            return;
        }
        LogUtil.d("Got billing client, querying purchases...");
        billingClient.getClient()
                .queryPurchasesAsync(InAppBillingClient.INAPP_PARAMS, (billingResult, purchases) -> {
                    LogUtil.d("Purchase query completed. Response code: " + billingResult.getResponseCode());
                    if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                        List<String> productIds = new ArrayList<>();
                        if (purchases != null) {
                            LogUtil.d("Found " + purchases.size() + " purchases");
                            for (Purchase purchase : purchases) {
                                productIds.add(purchase.getProducts().get(0));
                            }
                        } else {
                            LogUtil.d("No purchases found");
                        }
                        runOnUiThread(() -> {
                            Fragment currentFragment = mFragManager.findFragmentById(R.id.container);
                            if (currentFragment instanceof SettingsFragment) {
                                LogUtil.d("Restoring purchases in SettingsFragment");
                                ((SettingsFragment) currentFragment).restorePurchases(productIds,
                                        mConfig.getPremiumRequestProductsId(),
                                        mConfig.getPremiumRequestProductsCount());
                            } else {
                                LogUtil.e("SettingsFragment not found or not current fragment");
                                ToastHelper.show(this, R.string.pref_premium_request_restore_empty, Toast.LENGTH_LONG);
                            }
                        });
                    } else {
                        LogUtil.e("Failed to load purchase data. Response Code: " + billingResult.getResponseCode() 
                                + " Debug Message: " + billingResult.getDebugMessage());
                        runOnUiThread(() -> {
                            ToastHelper.show(this, R.string.pref_premium_request_restore_empty, Toast.LENGTH_LONG);
                        });
                    }
                });
    }

    public void onError(String error) {
        LogUtil.e("Failed to load purchase data: " + error);
    }

    @Override
    public void onProcessPurchase(Purchase purchase) {
        if (purchase.getPurchaseState() != Purchase.PurchaseState.PURCHASED) {
            return;
        }

        if (Preferences.get(this).getInAppBillingType() == InAppBilling.DONATE) {
            InAppBillingClient.get(this).getClient().consumeAsync(
                    ConsumeParams.newBuilder()
                            .setPurchaseToken(purchase.getPurchaseToken())
                            .build(),
                    (billingResult, s) -> {
                        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                            Preferences.get(this).setInAppBillingType(-1);
                            runOnUiThread(() -> new MaterialDialog.Builder(this)
                                    .typeface(TypefaceHelper.getMedium(this), TypefaceHelper.getRegular(this))
                                    .title(R.string.navigation_view_donate)
                                    .content(R.string.donation_success)
                                    .positiveText(R.string.close)
                                    .show());
                        } else {
                            LogUtil.e("Failed to consume donation product. Response Code: " + billingResult.getResponseCode());
                        }
                    }
            );
        } else if (Preferences.get(this).getInAppBillingType() == InAppBilling.PREMIUM_REQUEST) {
            if (!purchase.isAcknowledged()) {
                InAppBillingClient.get(this).getClient().acknowledgePurchase(
                        AcknowledgePurchaseParams.newBuilder()
                                .setPurchaseToken(purchase.getPurchaseToken())
                                .build(),
                        (billingResult) -> {
                            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                                Preferences.get(this).setPremiumRequest(true);
                                Preferences.get(this).setPremiumRequestProductId(purchase.getProducts().get(0));
                                Preferences.get(this).setInAppBillingType(-1);

                                // Delete old premium purchase history
                                Database.get(this).deletePremiumRequests();

                                this.runOnUiThread(() -> {
                                    if (mFragmentTag == Extras.Tag.REQUEST) {
                                        RequestFragment fragment = (RequestFragment) mFragManager.findFragmentByTag(Extras.Tag.REQUEST.value);
                                        if (fragment != null) fragment.refreshIconRequest();
                                    }
                                });
                            } else {
                                LogUtil.e("Failed to acknowledge purchase. Response Code: " + billingResult.getResponseCode());
                            }
                        }
                );
            }
        }
    }

    @Override
    public void onInAppBillingSelected(int type, InAppBilling product) {
        Preferences.get(this).setInAppBillingType(type);

        if (type == InAppBilling.PREMIUM_REQUEST) {
            Preferences.get(this).setPremiumRequestCount(product.getProductCount());
            Preferences.get(this).setPremiumRequestTotal(product.getProductCount());
        }

        List<BillingFlowParams.ProductDetailsParams> productDetailsParamsList = new ArrayList<>();
        productDetailsParamsList.add(BillingFlowParams.ProductDetailsParams.newBuilder()
                .setProductDetails(product.getProductDetails())
                .build());

        InAppBillingClient.get(this).getClient().launchBillingFlow(this,
                BillingFlowParams.newBuilder()
                        .setProductDetailsParamsList(productDetailsParamsList)
                        .build());
    }

    @Override
    public void onInAppBillingRequest() {
        if (mFragmentTag == Extras.Tag.REQUEST) {
            RequestFragment fragment = (RequestFragment) mFragManager.findFragmentByTag(Extras.TAG_REQUEST);
            if (fragment != null) fragment.prepareRequest();
        }
    }

    @Override
    public void onWallpapersChecked(int wallpaperCount) {
        Preferences.get(this).setAvailableWallpapersCount(wallpaperCount);

        if (mFragmentTag == Extras.Tag.HOME) {
            HomeFragment fragment = (HomeFragment) mFragManager.findFragmentByTag(Extras.TAG_HOME);
            if (fragment != null) fragment.resetWallpapersCount();
        }
    }

    @Override
    public void onSearchExpanded(boolean expand) {
        Toolbar toolbar = findViewById(R.id.toolbar);
        mIsMenuVisible = !expand;

        if (expand) {
            int color = ColorHelper.getAttributeColor(this, R.attr.cb_toolbarIcon);
            toolbar.setNavigationIcon(DrawableHelper.getTintedDrawable(
                    this, R.drawable.ic_toolbar_back, color));

            // Only handle bottom navigation visibility when using bottom navigation mode
            if (isBottomNavigation && mBottomNavigationView != null) {
                mBottomNavigationView.setVisibility(View.GONE);
            }
        } else {
            SoftKeyboardHelper.closeKeyboard(this);
            ColorHelper.setStatusBarColor(this, Color.TRANSPARENT, true);

            if (!isBottomNavigation) {
                // Only show navigation drawer icon if using side navigation
                if (CandyBarApplication.getConfiguration().getNavigationIcon() == CandyBarApplication.NavigationIcon.DEFAULT) {
                    mDrawerToggle.setDrawerArrowDrawable(new DrawerArrowDrawable(this));
                } else {
                    toolbar.setNavigationIcon(ConfigurationHelper.getNavigationIcon(this,
                            CandyBarApplication.getConfiguration().getNavigationIcon()));
                }

                toolbar.setNavigationOnClickListener(view ->
                        mDrawerLayout.openDrawer(GravityCompat.START));
            } else if (mBottomNavigationView != null) {
                mBottomNavigationView.setVisibility(View.VISIBLE);
            }
        }

        mDrawerLayout.setDrawerLockMode(expand || isBottomNavigation ?
                DrawerLayout.LOCK_MODE_LOCKED_CLOSED : DrawerLayout.LOCK_MODE_UNLOCKED);
        supportInvalidateOptionsMenu();
    }

    public void showSupportDevelopmentDialog() {
        InAppBillingFragment.showInAppBillingDialog(mFragManager,
                InAppBilling.DONATE,
                mConfig.getLicenseKey(),
                mConfig.getDonationProductsId(),
                null);
    }

    private void initNavigationView(Toolbar toolbar) {
        mDrawerToggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, null, R.string.txt_open, R.string.txt_close) {

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if (CandyBarApplication.getConfiguration().getNavigationIcon() == CandyBarApplication.NavigationIcon.DEFAULT) {
                    mDrawerToggle.setDrawerArrowDrawable(new DrawerArrowDrawable(CandyBarMainActivity.this));
                }
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                selectPosition(mPosition);
                toolbar.setNavigationIcon(ConfigurationHelper.getNavigationIcon(CandyBarMainActivity.this,
                        CandyBarApplication.getConfiguration().getNavigationIcon()));
            }

            @Override
            public void onDrawerStateChanged(int newState) {
                super.onDrawerStateChanged(newState);
                if (newState == DrawerLayout.STATE_DRAGGING) {
                    toolbar.setNavigationIcon(ConfigurationHelper.getNavigationIcon(CandyBarMainActivity.this,
                            CandyBarApplication.getConfiguration().getNavigationIcon()));
                }
            }
        };
        mDrawerToggle.setDrawerIndicatorEnabled(false);
        toolbar.setNavigationIcon(ConfigurationHelper.getNavigationIcon(this,
                CandyBarApplication.getConfiguration().getNavigationIcon()));
        toolbar.setNavigationOnClickListener(view ->
                mDrawerLayout.openDrawer(GravityCompat.START));

        // Set navigation icon color based on Android version and theme
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.R) {
            boolean isDarkTheme = (getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK)
                    == Configuration.UI_MODE_NIGHT_YES;
            if (isDarkTheme) {
                int accentColor = ColorHelper.getAttributeColor(this, R.attr.cb_colorAccent);
                if (toolbar.getNavigationIcon() != null) {
                    toolbar.getNavigationIcon().setTint(accentColor);
                }
            }
        }

        if (CandyBarApplication.getConfiguration().getNavigationIcon() == CandyBarApplication.NavigationIcon.DEFAULT) {
            DrawerArrowDrawable drawerArrowDrawable = new DrawerArrowDrawable(this);
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.R) {
                boolean isDarkTheme = (getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK)
                        == Configuration.UI_MODE_NIGHT_YES;
                if (isDarkTheme) {
                    drawerArrowDrawable.setColor(ColorHelper.getAttributeColor(this, R.attr.cb_colorAccent));
                } else {
                    drawerArrowDrawable.setColor(ColorHelper.getAttributeColor(this, R.attr.cb_toolbarIcon));
                }
            } else {
                drawerArrowDrawable.setColor(ColorHelper.getAttributeColor(this, R.attr.cb_toolbarIcon));
            }
            drawerArrowDrawable.setSpinEnabled(true);
            mDrawerToggle.setDrawerArrowDrawable(drawerArrowDrawable);
        } else {
            mDrawerToggle.setDrawerIndicatorEnabled(false);
            toolbar.setNavigationIcon(ConfigurationHelper.getNavigationIcon(this,
                    CandyBarApplication.getConfiguration().getNavigationIcon()));
            toolbar.setNavigationOnClickListener(view ->
                    mDrawerLayout.openDrawer(GravityCompat.START));
        }

        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        mDrawerLayout.addDrawerListener(mDrawerToggle);
        mDrawerLayout.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                // Always show navigation icon in side navigation mode
                if (toolbar != null) {
                    toolbar.setNavigationIcon(ConfigurationHelper.getNavigationIcon(CandyBarMainActivity.this,
                            CandyBarApplication.getConfiguration().getNavigationIcon()));
                }
            }
        });

        NavigationViewHelper.initApply(mNavigationView);
        NavigationViewHelper.initIconRequest(mNavigationView);
        NavigationViewHelper.initWallpapers(mNavigationView);
        NavigationViewHelper.initKustom(mNavigationView);

        ColorStateList itemStateList = ContextCompat.getColorStateList(this,
                R.color.navigation_view_item_highlight);
        mNavigationView.setItemTextColor(itemStateList);
        mNavigationView.setItemIconTintList(itemStateList);
//        Drawable background = ContextCompat.getDrawable(this,
//                ThemeHelper.isDarkTheme(this) ?
//                        R.drawable.navigation_view_item_background_dark :
//                        R.drawable.navigation_view_item_background);
//        mNavigationView.setItemBackground(background);
        mNavigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.navigation_view_home) mPosition = Extras.Tag.HOME.idx;
            else if (id == R.id.navigation_view_apply) mPosition = Extras.Tag.APPLY.idx;
            else if (id == R.id.navigation_view_icons) mPosition = Extras.Tag.ICONS.idx;
            else if (id == R.id.navigation_view_request) mPosition = Extras.Tag.REQUEST.idx;
            else if (id == R.id.navigation_view_wallpapers) mPosition = Extras.Tag.WALLPAPERS.idx;
            else if (id == R.id.navigation_view_kustom) mPosition = Extras.Tag.PRESETS.idx;
            else if (id == R.id.navigation_view_settings) mPosition = Extras.Tag.SETTINGS.idx;
            else if (id == R.id.navigation_view_faqs) mPosition = Extras.Tag.FAQS.idx;
            else if (id == R.id.navigation_view_about) mPosition = Extras.Tag.ABOUT.idx;

            item.setChecked(true);
            mDrawerLayout.closeDrawers();
            return true;
        });
    }

    private void initNavigationViewHeader() {
        if (CandyBarApplication.getConfiguration().getNavigationViewHeader() == CandyBarApplication.NavigationViewHeader.NONE) {
            mNavigationView.removeHeaderView(mNavigationView.getHeaderView(0));
            return;
        }

        String titleText = getResources().getString(R.string.navigation_view_header_title);
        View header = mNavigationView.getHeaderView(0);
        HeaderView image = header.findViewById(R.id.header_image);
        LinearLayout container = header.findViewById(R.id.header_title_container);
        TextView title = header.findViewById(R.id.header_title);
        TextView version = header.findViewById(R.id.header_version);

        // Load the header image
        String imageResource = getResources().getString(R.string.navigation_view_header);
        if (imageResource.length() > 0) {
            int resId = getResources().getIdentifier(imageResource, "drawable", getPackageName());
            if (resId != 0) {
                // Check if nav_head exists and set appropriate drawable
                boolean useLauncherIcon = getResources().getBoolean(R.bool.use_launcher_icon_for_nav_header);
                int navHeadResId = getResources().getIdentifier("nav_head", "drawable", getPackageName());
                
                // If nav_head doesn't exist or flag is true, use nav_head_mini (which contains launcher icon layout)
                if (useLauncherIcon || navHeadResId == 0) {
                    image.setImageResource(resId); // resId already points to nav_head_mini
                } else {
                    // Use nav_head drawable
                    image.setImageResource(navHeadResId);
                }
            }
        }

        if (CandyBarApplication.getConfiguration().getNavigationViewHeader() == CandyBarApplication.NavigationViewHeader.MINI) {
            image.setRatio(16, 9);
        }

        if (titleText.length() == 0) {
            container.setVisibility(View.GONE);
        } else {
            title.setText(titleText);
            try {
                PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                String versionText = packageInfo.versionName + "(" + packageInfo.versionCode + ")";
                version.setText(versionText);
            } catch (Exception ignored) {
            }
        }
    }

    private void checkWallpapers() {
        // Skip wallpaper check if wallpaper_json is empty
        String wallpaperJson = getResources().getString(R.string.wallpaper_json);
        if (wallpaperJson.isEmpty()) {
            return;
        }

        if (Preferences.get(this).isConnectedToNetwork()) {
            new Thread(() -> {
                try {
                    if (WallpaperHelper.getWallpaperType(this) != WallpaperHelper.CLOUD_WALLPAPERS)
                        return;

                    InputStream stream = WallpaperHelper.getJSONStream(this);

                    if (stream != null) {
                        List<?> list = JsonHelper.parseList(stream);
                        if (list == null) return;

                        List<Wallpaper> wallpapers = new ArrayList<>();
                        for (int i = 0; i < list.size(); i++) {
                            Wallpaper wallpaper = JsonHelper.getWallpaper(list.get(i));
                            if (wallpaper != null) {
                                if (!wallpapers.contains(wallpaper)) {
                                    wallpapers.add(wallpaper);
                                } else {
                                    LogUtil.e("Duplicate wallpaper found: " + wallpaper.getURL());
                                }
                            }
                        }

                        this.runOnUiThread(() -> onWallpapersChecked(wallpapers.size()));
                    }
                } catch (IOException e) {
                    LogUtil.e(Log.getStackTraceString(e));
                }
            }).start();
        }

        int size = Preferences.get(this).getAvailableWallpapersCount();
        if (size > 0) {
            onWallpapersChecked(size);
        }
    }

    private void clearBackStack() {
        if (mFragManager.getBackStackEntryCount() > 0) {
            mFragManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            onSearchExpanded(false);
        }
    }

    public void selectPosition(int position) {
        // Get wallpaper_json and kustom status once
        String wallpaperJson = getResources().getString(R.string.wallpaper_json);
        boolean isWallpaperHidden = wallpaperJson.isEmpty();
        boolean isKustomHidden = !getResources().getBoolean(R.bool.enable_kustom);

        // Adjust position if sections are hidden
        if (isWallpaperHidden && position >= 4) {
            position = position - 1;
        }
        if (isKustomHidden && position >= (isWallpaperHidden ? 4 : 5)) {
            position = position - 1;
        }

        if (position == 3) { // Request position
            if (!getResources().getBoolean(R.bool.enable_icon_request) &&
                    getResources().getBoolean(R.bool.enable_premium_request)) {
                if (!Preferences.get(this).isPremiumRequestEnabled())
                    return;

                if (!Preferences.get(this).isPremiumRequest()) {
                    mPosition = mLastPosition;
                    mNavigationView.getMenu().getItem(mPosition).setChecked(true);
                    onBuyPremiumRequest();
                    return;
                }
            }
        }

        if (position == 4 && !isWallpaperHidden) { // Wallpapers position (only if not hidden)
            if (WallpaperHelper.getWallpaperType(this) == WallpaperHelper.EXTERNAL_APP) {
                mPosition = mLastPosition;
                mNavigationView.getMenu().getItem(mPosition).setChecked(true);
                WallpaperHelper.launchExternalApp(CandyBarMainActivity.this);
                return;
            }
        }

        // Always set fragment when clicking a main section, even if position is the same
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.container);
        if (currentFragment instanceof HomeFragment || position != mLastPosition) {
            mLastPosition = mPosition = position;
            setFragment(getFragment(position));
        }
    }

    private void setFragment(Fragment fragment) {
        if (fragment == null) return;
        clearBackStack();

        FragmentTransaction ft = mFragManager.beginTransaction()
                .replace(R.id.container, fragment, fragment.getClass().getSimpleName());
        try {
            ft.commit();
        } catch (Exception e) {
            ft.commitAllowingStateLoss();
        }

        // Show back arrow for specific fragments in bottom navigation
        if (isBottomNavigation) {
            if (fragment instanceof IconsBaseFragment ||
                    fragment instanceof WallpapersFragment ||
                    fragment instanceof ApplyFragment ||
                    fragment instanceof RequestFragment ||
                    fragment instanceof PresetsFragment) {

                // Show back arrow in toolbar
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                    getSupportActionBar().setHomeButtonEnabled(true);

                    Toolbar toolbar = findViewById(R.id.toolbar);
                    if (toolbar != null) {
                        int color = ColorHelper.getAttributeColor(this, R.attr.cb_toolbarIcon);
                        toolbar.setNavigationIcon(DrawableHelper.getTintedDrawable(
                                this, R.drawable.ic_toolbar_back, color));
                        toolbar.setNavigationOnClickListener(view -> onBackPressed());
                    }
                }
            } else if (fragment instanceof FAQsFragment ||
                    fragment instanceof SettingsFragment ||
                    fragment instanceof AboutFragment) {
                // Keep existing behavior for these fragments
                if (mBottomNavigationView != null) {
                    mBottomNavigationView.setVisibility(View.GONE);
                }

                if (getSupportActionBar() != null) {
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                    getSupportActionBar().setHomeButtonEnabled(true);

                    Toolbar toolbar = findViewById(R.id.toolbar);
                    if (toolbar != null) {
                        int color = ColorHelper.getAttributeColor(this, R.attr.cb_toolbarIcon);
                        toolbar.setNavigationIcon(DrawableHelper.getTintedDrawable(
                                this, R.drawable.ic_toolbar_back, color));
                        toolbar.setNavigationOnClickListener(view -> onBackPressed());
                    }
                }
            } else {
                // Hide back arrow for other fragments (like Home)
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                    getSupportActionBar().setHomeButtonEnabled(false);
                }
                if (mBottomNavigationView != null) {
                    mBottomNavigationView.setVisibility(View.VISIBLE);
                }
            }
        }

        // Update toolbar title based on fragment
        String title = "";
        int menuItemId = -1;

        if (fragment instanceof HomeFragment) {
            title = getResources().getString(R.string.navigation_view_home);
            menuItemId = R.id.navigation_view_home;
            mFragmentTag = Extras.Tag.HOME;
        } else if (fragment instanceof ApplyFragment) {
            title = getResources().getString(R.string.navigation_view_apply);
            menuItemId = R.id.navigation_view_apply;
            mFragmentTag = Extras.Tag.APPLY;
        } else if (fragment instanceof IconsBaseFragment) {
            title = getResources().getString(R.string.navigation_view_icons);
            menuItemId = R.id.navigation_view_icons;
            mFragmentTag = Extras.Tag.ICONS;
        } else if (fragment instanceof RequestFragment) {
            title = getResources().getString(R.string.navigation_view_request);
            menuItemId = R.id.navigation_view_request;
            mFragmentTag = Extras.Tag.REQUEST;
        } else if (fragment instanceof WallpapersFragment) {
            title = getResources().getString(R.string.navigation_view_wallpapers);
            menuItemId = R.id.navigation_view_wallpapers;
            mFragmentTag = Extras.Tag.WALLPAPERS;
        } else if (fragment instanceof PresetsFragment) {
            title = getResources().getString(R.string.navigation_view_kustom);
            menuItemId = R.id.navigation_view_kustom;
            mFragmentTag = Extras.Tag.PRESETS;
        } else if (fragment instanceof FAQsFragment) {
            title = getResources().getString(R.string.navigation_view_faqs);
            mFragmentTag = Extras.Tag.FAQS;
        } else if (fragment instanceof SettingsFragment) {
            title = getResources().getString(R.string.navigation_view_settings);
            mFragmentTag = Extras.Tag.SETTINGS;
        } else if (fragment instanceof AboutFragment) {
            title = getResources().getString(R.string.navigation_view_about);
            mFragmentTag = Extras.Tag.ABOUT;
        }

        if (!title.isEmpty() && mToolbarTitle != null) {
            mToolbarTitle.setText(title);
        }

        // Update navigation drawer selection if using sidebar navigation
        if (!isBottomNavigation && menuItemId != -1 && mNavigationView != null) {
            MenuItem menuItem = mNavigationView.getMenu().findItem(menuItemId);
            if (menuItem != null) {
                menuItem.setChecked(true);
            }
        }

        // Update bottom navigation selection without triggering listener
        if (isBottomNavigation && menuItemId != -1 && mBottomNavigationView != null) {
            mBottomNavigationView.setOnItemSelectedListener(null);
            mBottomNavigationView.setSelectedItemId(menuItemId);
            initBottomNavigation(); // Restore the listener
        }
    }

    private Fragment getFragment(int position) {
        // Get wallpaper_json and kustom status once
        String wallpaperJson = getResources().getString(R.string.wallpaper_json);
        boolean isWallpaperHidden = wallpaperJson.isEmpty();
        boolean isKustomHidden = !getResources().getBoolean(R.bool.enable_kustom);

        mFragmentTag = Extras.Tag.HOME;
        if (position == 0) {
            mFragmentTag = Extras.Tag.HOME;
            return new HomeFragment();
        } else if (position == 1) {
            mFragmentTag = Extras.Tag.APPLY;
            return new ApplyFragment();
        } else if (position == 2) {
            mFragmentTag = Extras.Tag.ICONS;
            return new IconsBaseFragment();
        } else if (position == 3) {
            mFragmentTag = Extras.Tag.REQUEST;
            return new RequestFragment();
        } else if (position == 4 && !isWallpaperHidden) {
            mFragmentTag = Extras.Tag.WALLPAPERS;
            return new WallpapersFragment();
        } else if ((position == 4 && isWallpaperHidden && !isKustomHidden) ||
                (position == 5 && !isWallpaperHidden && !isKustomHidden)) {
            mFragmentTag = Extras.Tag.PRESETS;
            return new PresetsFragment();
        } else if ((position == 4 && isWallpaperHidden && isKustomHidden) ||
                (position == 5 && ((isWallpaperHidden && !isKustomHidden) || (!isWallpaperHidden && isKustomHidden))) ||
                (position == 6 && !isWallpaperHidden && !isKustomHidden)) {
            mFragmentTag = Extras.Tag.SETTINGS;
            return new SettingsFragment();
        } else if ((position == 5 && isWallpaperHidden && isKustomHidden) ||
                (position == 6 && ((isWallpaperHidden && !isKustomHidden) || (!isWallpaperHidden && isKustomHidden))) ||
                (position == 7 && !isWallpaperHidden && !isKustomHidden)) {
            mFragmentTag = Extras.Tag.FAQS;
            return new FAQsFragment();
        } else if ((position == 6 && isWallpaperHidden && isKustomHidden) ||
                (position == 7 && ((isWallpaperHidden && !isKustomHidden) || (!isWallpaperHidden && isKustomHidden))) ||
                (position == 8 && !isWallpaperHidden && !isKustomHidden)) {
            mFragmentTag = Extras.Tag.ABOUT;
            return new AboutFragment();
        }
        return new HomeFragment();
    }

    private Fragment getActionFragment(int action) {
        switch (action) {
            case IntentHelper.ICON_PICKER:
            case IntentHelper.IMAGE_PICKER:
                mPosition = mLastPosition = (mFragmentTag = Extras.Tag.ICONS).idx;
                return new IconsBaseFragment();
            case IntentHelper.WALLPAPER_PICKER:
                if (WallpaperHelper.getWallpaperType(this) == WallpaperHelper.CLOUD_WALLPAPERS) {
                    mPosition = mLastPosition = (mFragmentTag = Extras.Tag.WALLPAPERS).idx;
                    return new WallpapersFragment();
                }
            default:
                mPosition = mLastPosition = (mFragmentTag = Extras.Tag.HOME).idx;
                return new HomeFragment();
        }
    }

    public boolean isBottomNavigationEnabled() {
        return isBottomNavigation;
    }

    public static class ActivityConfiguration {

        private boolean mIsLicenseCheckerEnabled;
        private byte[] mRandomString;
        private String mLicenseKey;
        private String[] mDonationProductsId;
        private String[] mPremiumRequestProductsId;
        private int[] mPremiumRequestProductsCount;

        public ActivityConfiguration setLicenseCheckerEnabled(boolean enabled) {
            mIsLicenseCheckerEnabled = enabled;
            return this;
        }

        public ActivityConfiguration setRandomString(@NonNull byte[] randomString) {
            mRandomString = randomString;
            return this;
        }

        public ActivityConfiguration setLicenseKey(@NonNull String licenseKey) {
            mLicenseKey = licenseKey;
            return this;
        }

        public ActivityConfiguration setDonationProductsId(@NonNull String[] productsId) {
            mDonationProductsId = productsId;
            return this;
        }

        public ActivityConfiguration setPremiumRequestProducts(@NonNull String[] ids, @NonNull int[] counts) {
            mPremiumRequestProductsId = ids;
            mPremiumRequestProductsCount = counts;
            return this;
        }

        public boolean isLicenseCheckerEnabled() {
            return mIsLicenseCheckerEnabled;
        }

        public byte[] getRandomString() {
            return mRandomString;
        }

        public String getLicenseKey() {
            return mLicenseKey;
        }

        public String[] getDonationProductsId() {
            return mDonationProductsId;
        }

        public String[] getPremiumRequestProductsId() {
            return mPremiumRequestProductsId;
        }

        public int[] getPremiumRequestProductsCount() {
            return mPremiumRequestProductsCount;
        }
    }

    private void initHomeFragment() {
        Fragment fragment = new HomeFragment();
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction()
                .replace(R.id.container, fragment, HomeFragment.TAG);
        try {
            ft.commit();
        } catch (Exception e) {
            ft.commitAllowingStateLoss();
        }
    }

    private void initBottomNavigation() {
        mBottomNavigationView = findViewById(R.id.bottom_navigation);
        if (mBottomNavigationView == null) return;

        // If using sidebar navigation, keep bottom navigation permanently hidden
        if (!isBottomNavigation) {
            mBottomNavigationView.setVisibility(View.GONE);
            return;
        }

        // Hide wallpapers menu item if wallpaper_json is empty
        String wallpaperJson = getResources().getString(R.string.wallpaper_json);
        if (wallpaperJson.isEmpty()) {
            MenuItem wallpapersItem = mBottomNavigationView.getMenu().findItem(R.id.navigation_view_wallpapers);
            if (wallpapersItem != null) {
                wallpapersItem.setVisible(false);
            }
        }

        // Get theme colors
        TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(R.attr.cb_colorAccent, typedValue, true);
        int accentColor = typedValue.data;

        // Get card background color from theme
        getTheme().resolveAttribute(R.attr.cb_cardBackground, typedValue, true);
        int cardBackground = typedValue.data;

        // Get colors based on theme
        boolean isLightTheme = (getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK)
                == Configuration.UI_MODE_NIGHT_NO;
        int unselectedColor = isLightTheme ?
                Color.parseColor("#99000000") : // 60% black for light theme
                Color.parseColor("#80FFFFFF"); // 50% white for dark theme

        int[][] states = new int[][] {
                new int[] { android.R.attr.state_checked }, // checked
                new int[] { -android.R.attr.state_checked }  // unchecked
        };

        int[] colors = new int[] {
                accentColor, // checked color (accent)
                unselectedColor // unchecked color
        };

        ColorStateList colorStateList = new ColorStateList(states, colors);

        // Create semi-transparent version of accent color for indicator background
        int semiTransparentAccent = Color.argb(64, Color.red(accentColor),
                Color.green(accentColor),
                Color.blue(accentColor));

        ColorStateList backgroundColorStateList = new ColorStateList(
                new int[][] {
                        new int[] { android.R.attr.state_selected },
                        new int[] { android.R.attr.state_checked },
                        new int[] { }
                },
                new int[] {
                        semiTransparentAccent,
                        semiTransparentAccent,
                        Color.TRANSPARENT
                }
        );

        mBottomNavigationView.setItemIconTintList(colorStateList);
        mBottomNavigationView.setItemTextColor(colorStateList);

        boolean isMaterialYou = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
                Preferences.get(this).isMaterialYou();
        boolean isDarkTheme = (getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK)
                == Configuration.UI_MODE_NIGHT_YES;
        boolean isPureBlack = Preferences.get(this).isPureBlack();

        if (isMaterialYou && isDarkTheme) {
            if (isPureBlack) {
                mBottomNavigationView.setBackgroundColor(Color.BLACK);
            } else {
                int systemNeutral2_800 = getResources().getColor(android.R.color.system_neutral2_800);
                mBottomNavigationView.setBackgroundColor(systemNeutral2_800);
            }
        } else if (!isDarkTheme && !isMaterialYou) {
            int mintColor = Color.parseColor("#A5E6C8");
            mBottomNavigationView.setBackgroundColor(mintColor);
        } else {
            mBottomNavigationView.setBackgroundColor(cardBackground);
        }

        if (isMaterialYou) {
            mBottomNavigationView.setItemActiveIndicatorColor(backgroundColorStateList);
            mBottomNavigationView.setItemActiveIndicatorEnabled(true);
        } else {
            mBottomNavigationView.setItemActiveIndicatorEnabled(false);
        }

        mBottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.navigation_view_home) {
                mPosition = 0;
                setFragment(getFragment(mPosition));
                return true;
            } else if (id == R.id.navigation_view_icons) {
                mPosition = 2;
                setFragment(getFragment(mPosition));
                return true;
            } else if (id == R.id.navigation_view_wallpapers) {
                mPosition = 4;
                setFragment(getFragment(mPosition));
                return true;
            } else if (id == R.id.navigation_view_apply) {
                mPosition = 1;
                setFragment(getFragment(mPosition));
                return true;
            } else if (id == R.id.navigation_view_request) {
                mPosition = 3;
                setFragment(getFragment(mPosition));
                return true;
            }
            return false;
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_overflow, menu);

        // Only hide overflow menu items in sidebar mode
        // Keep search and other action buttons visible
        for (int i = 0; i < menu.size(); i++) {
            MenuItem item = menu.getItem(i);
            if (item.getItemId() == R.id.menu_changelog ||
                    item.getItemId() == R.id.navigation_view_faqs ||
                    item.getItemId() == R.id.navigation_view_settings ||
                    item.getItemId() == R.id.navigation_view_about ||
                    item.getItemId() == R.id.navigation_view_kustom) {
                item.setVisible(isBottomNavigation);
            }
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // Update menu item visibility based on navigation mode
        // Only hide overflow menu items in sidebar mode
        for (int i = 0; i < menu.size(); i++) {
            MenuItem item = menu.getItem(i);
            if (item.getItemId() == R.id.menu_changelog ||
                    item.getItemId() == R.id.navigation_view_faqs ||
                    item.getItemId() == R.id.navigation_view_settings ||
                    item.getItemId() == R.id.navigation_view_about ||
                    item.getItemId() == R.id.navigation_view_kustom) {
                item.setVisible(isBottomNavigation);
            }
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_changelog) {
            ChangelogFragment.showChangelog(mFragManager, null);
            return true;
        }

        Fragment fragment = null;
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.container);

        if (id == R.id.navigation_view_faqs ||
                id == R.id.navigation_view_settings ||
                id == R.id.navigation_view_about) {
            // Store previous position before navigating to overflow menu item
            // Note: Removed Kustom from here as it's a main section
            mPreviousPosition = mPosition;
        }

        if (id == R.id.navigation_view_faqs) {
            fragment = new FAQsFragment();
        } else if (id == R.id.navigation_view_settings) {
            fragment = new SettingsFragment();
        } else if (id == R.id.navigation_view_about) {
            fragment = new AboutFragment();
        } else if (id == R.id.navigation_view_kustom) {
            // Handle Kustom like a main section
            mPosition = Extras.Tag.PRESETS.idx;
            fragment = new PresetsFragment();
        }

        if (fragment != null) {
            // Check if current fragment has active search and collapse it
            Toolbar toolbar = findViewById(R.id.toolbar);
            if (toolbar != null && toolbar.getMenu() != null) {
                MenuItem searchItem = toolbar.getMenu().findItem(R.id.menu_search);
                if (searchItem != null && searchItem.isActionViewExpanded()) {
                    // Force close keyboard first
                    SoftKeyboardHelper.closeKeyboard(this);

                    // Collapse search view
                    searchItem.collapseActionView();

                    // Restore bottom navigation if using bottom navigation mode
                    if (isBottomNavigation && mBottomNavigationView != null) {
                        mBottomNavigationView.setVisibility(View.VISIBLE);
                    }

                    // For fragments that need to go home first (like RequestFragment)
                    if (currentFragment instanceof RequestFragment) {
                        final Fragment finalFragment = fragment;
                        setFragment(new HomeFragment());
                        // Delay the next fragment transition slightly to allow proper cleanup
                        new Handler(Looper.getMainLooper()).postDelayed(() -> setFragment(finalFragment), 100);
                        return true;
                    }
                }
            }
            setFragment(fragment);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

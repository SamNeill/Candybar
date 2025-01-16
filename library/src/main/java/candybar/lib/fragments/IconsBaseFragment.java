package candybar.lib.fragments;

import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.interpolator.view.animation.LinearOutSlowInInterpolator;
import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.danimahardhika.android.helpers.animation.AnimationHelper;
import com.danimahardhika.android.helpers.core.ColorHelper;
import com.danimahardhika.android.helpers.core.utils.LogUtil;
import com.danimahardhika.android.helpers.core.SoftKeyboardHelper;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.lang.reflect.Field;
import java.util.List;

import candybar.lib.R;
import candybar.lib.activities.CandyBarMainActivity;
import candybar.lib.applications.CandyBarApplication;
import candybar.lib.fragments.dialog.IconShapeChooserFragment;
import candybar.lib.helpers.IconsHelper;
import candybar.lib.helpers.TapIntroHelper;
import candybar.lib.items.Icon;
import candybar.lib.preferences.Preferences;
import candybar.lib.utils.AsyncTaskBase;
import candybar.lib.utils.listeners.SearchListener;

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

public class IconsBaseFragment extends Fragment {

    private ViewPager2 mPager;
    private ProgressBar mProgress;
    private TabLayout mTabLayout;

    private AsyncTaskBase mGetIcons;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_icons_base, container, false);
        mTabLayout = view.findViewById(R.id.tab);
        mPager = view.findViewById(R.id.pager);
        mProgress = view.findViewById(R.id.progress);
        initTabs();
        mPager.setOffscreenPageLimit(2);
        // Reduce sensitivity of ViewPager
        try {
            Field recyclerViewField = ViewPager2.class.getDeclaredField("mRecyclerView");
            recyclerViewField.setAccessible(true);
            RecyclerView recyclerView = (RecyclerView) recyclerViewField.get(mPager);
            Field touchSlopField = RecyclerView.class.getDeclaredField("mTouchSlop");
            touchSlopField.setAccessible(true);
            int touchSlop = (int) touchSlopField.get(recyclerView);
            touchSlopField.set(recyclerView, touchSlop * 3);
        } catch (Exception e) {
            LogUtil.d(Log.getStackTraceString(e));
        }
        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        LogUtil.d("IconsBaseFragment: onCreateOptionsMenu");
        inflater.inflate(R.menu.menu_icons_search, menu);
        MenuItem search = menu.findItem(R.id.menu_search);
        MenuItem iconShape = menu.findItem(R.id.menu_icon_shape);

        // Set accent color for search and icon shape icons
        if (search.getIcon() != null) {
            search.getIcon().setTint(ColorHelper.getAttributeColor(requireContext(), R.attr.cb_colorAccent));
        }
        if (iconShape.getIcon() != null) {
            iconShape.getIcon().setTint(ColorHelper.getAttributeColor(requireContext(), R.attr.cb_colorAccent));
        }

        search.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                LogUtil.d("IconsBaseFragment: onMenuItemActionExpand - start");
                Activity activity = getActivity();
                if (activity == null || !(activity instanceof CandyBarMainActivity)) {
                    LogUtil.d("IconsBaseFragment: onMenuItemActionExpand - invalid activity");
                    return false;
                }

                CandyBarMainActivity mainActivity = (CandyBarMainActivity) activity;
                FragmentManager fm = mainActivity.getSupportFragmentManager();
                if (fm == null) {
                    LogUtil.d("IconsBaseFragment: onMenuItemActionExpand - null fragment manager");
                    return false;
                }

                LogUtil.d("IconsBaseFragment: onMenuItemActionExpand - disabling options menu");
                setHasOptionsMenu(false);
                View view = mainActivity.findViewById(R.id.shadow);
                if (view != null) view.animate().translationY(-mTabLayout.getHeight())
                        .setDuration(200).start();
                mTabLayout.animate().translationY(-mTabLayout.getHeight()).setDuration(200)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(android.animation.Animator animation) {
                                super.onAnimationEnd(animation);
                                LogUtil.d("IconsBaseFragment: onMenuItemActionExpand - animation end");
                                if (mainActivity.isFinishing()) {
                                    LogUtil.d("IconsBaseFragment: onMenuItemActionExpand - activity finishing");
                                    return;
                                }

                                Fragment prev = fm.findFragmentByTag("home");
                                if (prev != null) {
                                    LogUtil.d("IconsBaseFragment: onMenuItemActionExpand - home fragment exists");
                                    return;
                                }

                                PagerIconsAdapter adapter = (PagerIconsAdapter) mPager.getAdapter();
                                if (adapter == null) {
                                    LogUtil.d("IconsBaseFragment: onMenuItemActionExpand - null adapter");
                                    return;
                                }

                                LogUtil.d("IconsBaseFragment: onMenuItemActionExpand - notifying search expanded");
                                ((SearchListener) mainActivity).onSearchExpanded(true);

                                LogUtil.d("IconsBaseFragment: onMenuItemActionExpand - adding search fragment");
                                FragmentTransaction ft = fm.beginTransaction()
                                        .replace(R.id.container, new IconsSearchFragment(), IconsSearchFragment.TAG)
                                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                                        .addToBackStack(null);

                                try {
                                    ft.commit();
                                } catch (Exception e) {
                                    LogUtil.e("IconsBaseFragment: onMenuItemActionExpand - commit error: " + e.getMessage());
                                    ft.commitAllowingStateLoss();
                                }
                            }
                        }).start();

                LogUtil.d("IconsBaseFragment: onMenuItemActionExpand - complete");
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                LogUtil.d("IconsBaseFragment: onMenuItemActionCollapse - start");
                Activity activity = getActivity();
                if (activity == null || !(activity instanceof CandyBarMainActivity)) {
                    LogUtil.d("IconsBaseFragment: onMenuItemActionCollapse - invalid activity");
                    return true;
                }

                CandyBarMainActivity mainActivity = (CandyBarMainActivity) activity;

                LogUtil.d("IconsBaseFragment: onMenuItemActionCollapse - closing keyboard");
                SoftKeyboardHelper.closeKeyboard(mainActivity);

                LogUtil.d("IconsBaseFragment: onMenuItemActionCollapse - restoring UI elements");
                View view = mainActivity.findViewById(R.id.shadow);
                if (view != null) view.animate().translationY(0).setDuration(200).start();
                mTabLayout.animate().translationY(0).setDuration(200)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(android.animation.Animator animation) {
                                super.onAnimationEnd(animation);
                                LogUtil.d("IconsBaseFragment: onMenuItemActionCollapse - animation end");
                                if (mainActivity.isFinishing()) {
                                    LogUtil.d("IconsBaseFragment: onMenuItemActionCollapse - activity finishing");
                                    return;
                                }

                                FragmentManager fm = mainActivity.getSupportFragmentManager();
                                Fragment prev = fm.findFragmentByTag("home");
                                if (prev != null) {
                                    LogUtil.d("IconsBaseFragment: onMenuItemActionCollapse - home fragment exists");
                                    return;
                                }

                                PagerIconsAdapter adapter = (PagerIconsAdapter) mPager.getAdapter();
                                if (adapter == null) {
                                    LogUtil.d("IconsBaseFragment: onMenuItemActionCollapse - null adapter");
                                    return;
                                }

                                LogUtil.d("IconsBaseFragment: onMenuItemActionCollapse - notifying search collapsed");
                                ((SearchListener) mainActivity).onSearchExpanded(false);

                                LogUtil.d("IconsBaseFragment: onMenuItemActionCollapse - restoring bottom navigation");
                                View bottomNavigation = mainActivity.findViewById(R.id.bottom_navigation);
                                if (bottomNavigation != null && mainActivity.isBottomNavigationEnabled()) {
                                    bottomNavigation.setVisibility(View.VISIBLE);
                                }

                                LogUtil.d("IconsBaseFragment: onMenuItemActionCollapse - returning to icons fragment");
                                setHasOptionsMenu(true); // Restore options menu before fragment transaction
                                FragmentTransaction ft = fm.beginTransaction()
                                        .replace(R.id.container, IconsFragment.newInstance(mPager.getCurrentItem()))
                                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);

                                try {
                                    ft.commit();
                                } catch (Exception e) {
                                    LogUtil.e("IconsBaseFragment: onMenuItemActionCollapse - commit error: " + e.getMessage());
                                    ft.commitAllowingStateLoss();
                                }
                            }
                        }).start();

                LogUtil.d("IconsBaseFragment: onMenuItemActionCollapse - complete");
                return true;
            }
        });

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O ||
                !requireActivity().getResources().getBoolean(R.bool.includes_adaptive_icons) ||
                !requireActivity().getResources().getBoolean(R.bool.show_icon_shape)) {
            iconShape.setVisible(false);
        }

        iconShape.setOnMenuItemClickListener(menuItem -> {
            IconShapeChooserFragment.showIconShapeChooser(requireActivity().getSupportFragmentManager());
            return false;
        });
    }

    @Override
    public void onDestroy() {
        if (mGetIcons != null) {
            mGetIcons.cancel(true);
        }
        Activity activity = getActivity();
        if (activity != null) Glide.get(activity).clearMemory();
        super.onDestroy();
    }

    private void initTabs() {
        AnimationHelper.slideDownIn(mTabLayout)
                .interpolator(new LinearOutSlowInInterpolator())
                .callback(new AnimationHelper.Callback() {
                    @Override
                    public void onAnimationStart() {
                    }

                    @Override
                    public void onAnimationEnd() {
                        if (getActivity() == null) return;

                        if (Preferences.get(getActivity()).isToolbarShadowEnabled()) {
                            AnimationHelper.fade(getActivity().findViewById(R.id.shadow)).start();
                        }

                        // Only show pill background for Material You theme
                        boolean isMaterialYou = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
                                Preferences.get(getActivity()).isMaterialYou();

                        if (isMaterialYou) {
                            for (int i = 0; i < mTabLayout.getTabCount(); i++) {
                                TabLayout.Tab tab = mTabLayout.getTabAt(i);
                                if (tab != null && tab.getCustomView() != null) {
                                    tab.getCustomView().setBackgroundResource(R.drawable.tab_background);
                                }
                            }
                        }

                        mGetIcons = new IconsLoader().execute();
                    }
                })
                .start();
    }

    private class IconsLoader extends AsyncTaskBase {
        @Override
        protected void preRun() {
            if (CandyBarMainActivity.sSections == null) {
                mProgress.setVisibility(View.VISIBLE);
            }
        }

        @Override
        protected boolean run() {
            if (!isCancelled()) {
                try {
                    Thread.sleep(1);
                    IconsHelper.loadIcons(requireActivity(), true);
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

            mGetIcons = null;
            mProgress.setVisibility(View.GONE);
            if (ok) {
                setHasOptionsMenu(true);

                PagerIconsAdapter adapter = new PagerIconsAdapter(
                        getChildFragmentManager(), getLifecycle(), CandyBarMainActivity.sSections);
                mPager.setAdapter(adapter);

                new TabLayoutMediator(mTabLayout, mPager, (tab, position) -> {
                }).attach();
                mPager.setCurrentItem(0);

                new TabTypefaceChanger().executeOnThreadPool();

                if (getActivity().getResources().getBoolean(R.bool.show_intro)) {
                    TapIntroHelper.showIconsIntro(getActivity());
                }
            } else {
                Toast.makeText(getActivity(), R.string.icons_load_failed,
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    private class TabTypefaceChanger extends AsyncTaskBase {

        PagerIconsAdapter adapter;

        @Override
        protected void preRun() {
            adapter = (PagerIconsAdapter) mPager.getAdapter();
        }

        @Override
        protected boolean run() {
            if (!isCancelled()) {
                try {
                    Thread.sleep(1);
                    for (int i = 0; i < adapter.getItemCount(); i++) {
                        int j = i;
                        runOnUiThread(() -> {
                            if (getActivity() == null) return;
                            if (getActivity().isFinishing()) return;
                            if (mTabLayout == null) return;

                            if (j < mTabLayout.getTabCount()) {
                                TabLayout.Tab tab = mTabLayout.getTabAt(j);
                                if (tab != null) {
                                    if (j == 0) {
                                        tab.setCustomView(R.layout.fragment_icons_base_tab);
                                        tab.setText(adapter.getPageTitle(j));
                                    } else if (j < adapter.getItemCount()) {
                                        tab.setCustomView(R.layout.fragment_icons_base_tab);
                                        tab.setText(adapter.getPageTitle(j));
                                    }
                                }
                            }
                        });
                    }
                    return true;
                } catch (Exception ignored) {
                    return false;
                }
            }
            return false;
        }
    }

    private static class PagerIconsAdapter extends FragmentStateAdapter {

        private final List<Icon> mIcons;

        PagerIconsAdapter(@NonNull FragmentManager fm, @NonNull Lifecycle lifecycle,
                          @NonNull List<Icon> icons) {
            super(fm, lifecycle);
            mIcons = icons;
        }

        public CharSequence getPageTitle(int position) {
            String title = mIcons.get(position).getTitle();
            if (CandyBarApplication.getConfiguration().isShowTabIconsCount()) {
                title += " (" + mIcons.get(position).getIcons().size() + ")";
            }
            return title;
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            return IconsFragment.newInstance(position);
        }

        @Override
        public int getItemCount() {
            return mIcons.size();
        }

        public List<Icon> getIcons() {
            return mIcons;
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Set accent color for back arrow
        if (getActivity() != null) {
            androidx.appcompat.widget.Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
            if (toolbar != null && toolbar.getNavigationIcon() != null) {
                toolbar.getNavigationIcon().setTint(ColorHelper.getAttributeColor(requireContext(), R.attr.cb_colorAccent));
            }
        }

        mTabLayout = view.findViewById(R.id.tab);
        mPager = view.findViewById(R.id.pager);
        mProgress = view.findViewById(R.id.progress);
        initTabs();
    }
}

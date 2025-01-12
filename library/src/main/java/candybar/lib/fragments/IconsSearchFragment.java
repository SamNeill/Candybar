package candybar.lib.fragments;

import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.danimahardhika.android.helpers.core.ColorHelper;
import com.danimahardhika.android.helpers.core.SoftKeyboardHelper;
import com.danimahardhika.android.helpers.core.ViewHelper;
import com.danimahardhika.android.helpers.core.utils.LogUtil;
import com.pluscubed.recyclerfastscroll.RecyclerFastScroller;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import candybar.lib.R;
import candybar.lib.activities.CandyBarMainActivity;
import candybar.lib.adapters.IconsAdapter;
import candybar.lib.applications.CandyBarApplication;
import candybar.lib.fragments.dialog.IconShapeChooserFragment;
import candybar.lib.helpers.IconsHelper;
import candybar.lib.items.Icon;
import candybar.lib.utils.AlphanumComparator;
import candybar.lib.utils.AsyncTaskBase;
import candybar.lib.utils.listeners.SearchListener;

import static candybar.lib.helpers.ViewHelper.setFastScrollColor;

public class IconsSearchFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private RecyclerFastScroller mFastScroll;
    private TextView mSearchResult;
    private EditText mSearchInput;
    private final Fragment mFragment = this;

    private IconsAdapter mAdapter;
    private AsyncTaskBase mAsyncTask;

    public static final String TAG = "icons_search";

    private static WeakReference<IconsAdapter> currentAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_icons_search, container, false);
        mRecyclerView = view.findViewById(R.id.icons_grid);
        mFastScroll = view.findViewById(R.id.fastscroll);
        mSearchResult = view.findViewById(R.id.search_result);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LogUtil.d("IconsSearchFragment: onViewCreated");

        CandyBarApplication.getConfiguration().getAnalyticsHandler().logEvent(
                "view",
                new HashMap<String, Object>() {{ put("section", "icons_search"); }}
        );

        Activity activity = getActivity();
        if (activity instanceof CandyBarMainActivity) {
            CandyBarMainActivity mainActivity = (CandyBarMainActivity) activity;
            // Hide bottom navigation when search fragment is created
            View bottomNavigation = mainActivity.findViewById(R.id.bottom_navigation);
            if (bottomNavigation != null) {
                bottomNavigation.setVisibility(View.GONE);
            }
        }

        setHasOptionsMenu(true);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(),
                requireActivity().getResources().getInteger(R.integer.icons_column_count)));

        setFastScrollColor(mFastScroll);
        mFastScroll.attachRecyclerView(mRecyclerView);
        mAsyncTask = new IconsLoader().execute();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        LogUtil.d("IconsSearchFragment: onCreateOptionsMenu");
        inflater.inflate(R.menu.menu_icons_search, menu);
        MenuItem search = menu.findItem(R.id.menu_search);
        MenuItem iconShape = menu.findItem(R.id.menu_icon_shape);
        View searchView = search.getActionView();

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O ||
                !requireActivity().getResources().getBoolean(R.bool.includes_adaptive_icons)) {
            iconShape.setVisible(false);
        } else {
            searchView.findViewById(R.id.container).setPadding(0, 0, 0, 0);
        }

        View clearQueryButton = searchView.findViewById(R.id.clear_query_button);
        mSearchInput = searchView.findViewById(R.id.search_input);
        mSearchInput.setHint(R.string.search_icon);
        mSearchInput.setTextColor(ColorHelper.getAttributeColor(requireActivity(), android.R.attr.textColorPrimary));
        
        // Set accent color for clear button
        int accentColor = ColorHelper.getAttributeColor(requireActivity(), R.attr.cb_colorAccent);
        if (clearQueryButton instanceof ImageButton) {
            ((ImageButton) clearQueryButton).setColorFilter(accentColor);
        }

        search.expandActionView();
        mSearchInput.requestFocus();

        search.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                LogUtil.d("IconsSearchFragment: onMenuItemActionExpand");
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                LogUtil.d("IconsSearchFragment: onMenuItemActionCollapse - start");
                Activity activity = getActivity();
                if (activity == null || !(activity instanceof CandyBarMainActivity)) {
                    LogUtil.d("IconsSearchFragment: onMenuItemActionCollapse - invalid activity");
                    return true;
                }

                CandyBarMainActivity mainActivity = (CandyBarMainActivity) activity;
                if (mainActivity.isFinishing()) {
                    LogUtil.d("IconsSearchFragment: onMenuItemActionCollapse - activity finishing");
                    return true;
                }

                // First close keyboard and clear search
                LogUtil.d("IconsSearchFragment: onMenuItemActionCollapse - closing keyboard");
                SoftKeyboardHelper.closeKeyboard(mainActivity);
                mSearchInput.setText("");

                // Notify search state change immediately
                LogUtil.d("IconsSearchFragment: onMenuItemActionCollapse - notifying search state");
                ((SearchListener) mainActivity).onSearchExpanded(false);

                // Restore bottom navigation if needed
                View bottomNavigation = mainActivity.findViewById(R.id.bottom_navigation);
                if (bottomNavigation != null && mainActivity.isBottomNavigationEnabled()) {
                    LogUtil.d("IconsSearchFragment: onMenuItemActionCollapse - restoring bottom navigation");
                    bottomNavigation.setVisibility(View.VISIBLE);
                }

                // Return to icons fragment
                LogUtil.d("IconsSearchFragment: onMenuItemActionCollapse - popping back stack");
                FragmentManager fm = mainActivity.getSupportFragmentManager();
                setHasOptionsMenu(false); // Prevent menu recreation during pop
                fm.popBackStack();

                LogUtil.d("IconsSearchFragment: onMenuItemActionCollapse - complete");
                return true;
            }
        });

        mSearchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String query = charSequence.toString();
                LogUtil.d("IconsSearchFragment: onTextChanged - query: " + query);
                filterSearch(query);
                clearQueryButton.setVisibility(query.contentEquals("") ? View.GONE : View.VISIBLE);
            }
        });

        clearQueryButton.setOnClickListener(view -> mSearchInput.setText(""));

        iconShape.setOnMenuItemClickListener(menuItem -> {
            IconShapeChooserFragment.showIconShapeChooser(requireActivity().getSupportFragmentManager());
            return false;
        });
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        ViewHelper.resetSpanCount(mRecyclerView, requireActivity().getResources().getInteger(R.integer.icons_column_count));
    }

    @Override
    public void onDestroy() {
        LogUtil.d("IconsSearchFragment: onDestroy");
        if (mAsyncTask != null) {
            mAsyncTask.cancel(true);
        }
        Activity activity = getActivity();
        if (activity != null) {
            SoftKeyboardHelper.closeKeyboard(activity);
            Glide.get(activity).clearMemory();
        }
        currentAdapter = null;
        super.onDestroy();
    }

    private void filterSearch(String query) {
        try {
            mAdapter.search(query);
            if (mAdapter.getItemCount() == 0) {
                String text = requireActivity().getResources().getString(R.string.search_noresult, query);
                mSearchResult.setText(text);
                mSearchResult.setVisibility(View.VISIBLE);
            } else mSearchResult.setVisibility(View.GONE);
        } catch (Exception e) {
            LogUtil.e(Log.getStackTraceString(e));
        }
    }

    public static void reloadIcons() {
        if (currentAdapter != null && currentAdapter.get() != null)
            currentAdapter.get().reloadIcons();
    }

    private class IconsLoader extends AsyncTaskBase {

        private Set<Icon> iconSet;
        private List<Icon> iconList;
        private Set<String> excludedCategories;

        @Override
        protected void preRun() {
            iconSet = new HashSet<>();
            String[] exCategories = CandyBarApplication.getConfiguration().getExcludedCategoryForSearch();
            excludedCategories = exCategories != null ? new HashSet<>(Arrays.asList(exCategories)) : new HashSet<>();
        }

        @Override
        protected boolean run() {
            if (!isCancelled()) {
                try {
                    Thread.sleep(1);

                    IconsHelper.loadIcons(requireActivity(), false);

                    for (Icon icon : CandyBarMainActivity.sSections) {
                        boolean isExlcuded = excludedCategories.contains(icon.getTitle());
                        String allIconsTabTitle = CandyBarApplication.getConfiguration().getTabAllIconsTitle();
                        if (CandyBarApplication.getConfiguration().isShowTabAllIcons()) {
                            if (!icon.getTitle().equals(allIconsTabTitle) && !isExlcuded) {
                                iconSet.addAll(icon.getIcons());
                            }
                        } else if (!isExlcuded) {
                            iconSet.addAll(icon.getIcons());
                        }
                    }

                    iconList = new ArrayList<>(iconSet);

                    // Sort them in lowercase
                    Collections.sort(iconList, new AlphanumComparator() {
                        @Override
                        public int compare(Object o1, Object o2) {
                            String s1 = ((Icon) o1).getTitle().toLowerCase().trim();
                            String s2 = ((Icon) o2).getTitle().toLowerCase().trim();
                            return super.compare(s1, s2);
                        }
                    });

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
            if (ok) {
                mAdapter = new IconsAdapter(getActivity(), iconList, mFragment, false);
                currentAdapter = new WeakReference<>(mAdapter);
                mRecyclerView.setAdapter(mAdapter);
                filterSearch("");
                mSearchInput.requestFocus();
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    if (getActivity() != null && !getActivity().isFinishing()) {
                        SoftKeyboardHelper.openKeyboard(getActivity());
                    }
                }, 200);
            } else {
                // Unable to load all icons
                Toast.makeText(getActivity(), R.string.icons_load_failed,
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        LogUtil.d("IconsSearchFragment: onPause");
        // Ensure keyboard is closed when fragment is paused
        Activity activity = getActivity();
        if (activity != null) {
            SoftKeyboardHelper.closeKeyboard(activity);
        }
    }
}

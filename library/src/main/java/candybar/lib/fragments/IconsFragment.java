package candybar.lib.fragments;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import me.zhanghai.android.fastscroll.FastScrollerBuilder;

import com.danimahardhika.android.helpers.core.ColorHelper;
import com.danimahardhika.android.helpers.core.DrawableHelper;
import com.danimahardhika.android.helpers.core.ViewHelper;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import candybar.lib.R;
import candybar.lib.activities.CandyBarMainActivity;
import candybar.lib.adapters.IconsAdapter;
import candybar.lib.applications.CandyBarApplication;
import candybar.lib.databases.Database;
import candybar.lib.items.Icon;

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
 * distributed under the License is distributed on an "AS IS" BASIS,.
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

public class IconsFragment extends Fragment {

    private View mNoBookmarksFoundView;
    private RecyclerView mRecyclerView;
    private IconsAdapter mAdapter;

    private List<Icon> mIcons;
    private boolean isBookmarksFragment;
    private boolean prevIsEmpty = false;

    private static final String INDEX = "index";

    private static final List<WeakReference<IconsAdapter>> iconsAdapters = new ArrayList<>();
    private static WeakReference<IconsFragment> bookmarksIconFragment = new WeakReference<>(null);

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_icons, container, false);
        mNoBookmarksFoundView = view.findViewById(R.id.no_bookmarks_found_container);
        mRecyclerView = view.findViewById(R.id.icons_grid);
        return view;
    }

    public static IconsFragment newInstance(int index) {
        IconsFragment fragment = new IconsFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(INDEX, index);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mIcons = new ArrayList<>();
        int index = requireArguments().getInt(INDEX);
        if (CandyBarMainActivity.sSections != null) {
            mIcons = CandyBarMainActivity.sSections.get(index).getIcons();
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        CandyBarApplication.getConfiguration().getAnalyticsHandler().logEvent(
                "view",
                new HashMap<String, Object>() {{ put("section", "icons"); }}
        );

        setupViewVisibility();

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(),
                requireActivity().getResources().getInteger(R.integer.icons_column_count)));

        mAdapter = new IconsAdapter(requireActivity(), mIcons, this, false);
        mRecyclerView.setAdapter(mAdapter);
        iconsAdapters.add(new WeakReference<>(mAdapter));

        int accentColor = ColorHelper.getAttributeColor(requireActivity(), R.attr.cb_colorAccent);
        int trackColor = Color.argb(
            (int) (255 * 0.2f),
            Color.red(accentColor),
            Color.green(accentColor),
            Color.blue(accentColor)
        );
        
        new FastScrollerBuilder(mRecyclerView)
                .useMd2Style()
                .setThumbDrawable(DrawableHelper.getTintedDrawable(requireActivity(), 
                    me.zhanghai.android.fastscroll.R.drawable.afs_md2_thumb, 
                    accentColor))
                .setTrackDrawable(DrawableHelper.getTintedDrawable(requireActivity(),
                    me.zhanghai.android.fastscroll.R.drawable.afs_md2_track,
                    trackColor))
                .setPopupStyle(popupView -> {
                    popupView.setBackground(DrawableHelper.getTintedDrawable(requireActivity(),
                        R.drawable.fastscroll_popup_background,
                        accentColor));
                    if (popupView instanceof TextView) {
                        TextView textView = (TextView) popupView;
                        textView.setTextColor(Color.WHITE);
                        textView.setTextSize(36); // Increased text size
                        textView.setGravity(android.view.Gravity.CENTER);
                        int verticalPadding = (int) TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_DIP,
                            8,
                            Resources.getSystem().getDisplayMetrics()
                        );
                        int leftPadding = (int) TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_DIP,
                            4,  // Less padding on the left
                            Resources.getSystem().getDisplayMetrics()
                        );
                        int rightPadding = (int) TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_DIP,
                            12,  // More padding on the right
                            Resources.getSystem().getDisplayMetrics()
                        );
                        textView.setPadding(leftPadding, verticalPadding, rightPadding, verticalPadding);
                        
                        // Center the popup using view properties
                        textView.setY(0);
                        textView.setElevation(10f);
                        textView.post(() -> {
                            int thumbHeight = mRecyclerView.getHeight() / mRecyclerView.getAdapter().getItemCount();
                            textView.setY(-textView.getHeight() / 2f + thumbHeight / 2f);
                        });
                    }
                })
                .build();
    }

    private void setupViewVisibility() {
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        ViewHelper.resetSpanCount(mRecyclerView,
                requireActivity().getResources().getInteger(R.integer.icons_column_count));
    }

    public void refreshBookmarks() {
        if (isBookmarksFragment && isAdded()) {
            mIcons = Database.get(requireActivity()).getBookmarkedIcons(requireActivity());
            mAdapter.setIcons(mIcons);
            setupViewVisibility();
        }
    }

    public static void reloadIcons() {
        for (WeakReference<IconsAdapter> adapterRef : iconsAdapters) {
            if (adapterRef.get() != null) adapterRef.get().reloadIcons();
        }
    }

    public static void reloadBookmarks() {
        if (bookmarksIconFragment.get() != null) {
            bookmarksIconFragment.get().refreshBookmarks();
        }
    }
}

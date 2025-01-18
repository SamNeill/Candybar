package candybar.lib.adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.widget.CompoundButtonCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.danimahardhika.android.helpers.core.ColorHelper;
import com.danimahardhika.android.helpers.core.DrawableHelper;
import com.danimahardhika.android.helpers.core.utils.LogUtil;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import candybar.lib.R;
import candybar.lib.activities.CandyBarMainActivity;
import candybar.lib.applications.CandyBarApplication;
import candybar.lib.helpers.TypefaceHelper;
import candybar.lib.items.Request;
import candybar.lib.preferences.Preferences;
import candybar.lib.utils.CandyBarGlideModule;
import candybar.lib.utils.listeners.RequestListener;

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

public class RequestAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context mContext;
    private final List<Request> mRequests;
    private final List<Request> mRequestsAll;
    private final List<Request> mFilteredRequests;
    private SparseBooleanArray mSelectedItems;

    private final int mTextColorSecondary;
    private final int mTextColorAccent;
    private boolean mSelectedAll = false;

    private final boolean mShowShadow;
    private final boolean mShowPremiumRequest;
    private final boolean mShowRegularRequestLimit;
    private final int mSelectionHighlightColor;

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_CONTENT = 1;
    private static final int TYPE_FOOTER = 2;

    private boolean mIsSearchMode = false;

    public RequestAdapter(@NonNull Context context, @NonNull List<Request> requests, int spanCount) {
        mContext = context;
        mRequests = requests;
        mRequestsAll = new ArrayList<>(requests);
        mFilteredRequests = new ArrayList<>(requests);
        mTextColorSecondary = ColorHelper.getAttributeColor(mContext,
                android.R.attr.textColorSecondary);
        mTextColorAccent = ColorHelper.getAttributeColor(mContext, com.google.android.material.R.attr.colorSecondary);
        
        int accentColor = ColorHelper.getAttributeColor(mContext, R.attr.cb_colorAccent);
        mSelectionHighlightColor = Color.argb(40,
                Color.red(accentColor),
                Color.green(accentColor),
                Color.blue(accentColor));
                
        mSelectedItems = new SparseBooleanArray();

        mShowShadow = (spanCount == 1);
        mShowPremiumRequest = Preferences.get(mContext).isPremiumRequestEnabled();
        mShowRegularRequestLimit = Preferences.get(mContext).isRegularRequestLimit();
    }

    public void search(String query) {
        mFilteredRequests.clear();
        if (query.isEmpty()) {
            mFilteredRequests.addAll(mRequests);
        } else {
            String lowercaseQuery = query.toLowerCase().trim();
            for (Request request : mRequests) {
                String appName = request.getName().toLowerCase();
                if (appName.contains(lowercaseQuery)) {
                    mFilteredRequests.add(request);
                }
            }
        }
        if (mIsSearchMode) {
            if (getSelectedItemsSize() > 0) {
                showSearchModeFab();
            } else {
                hideSearchModeFab();
            }
        }
        notifyDataSetChanged();
    }

    private int getOriginalPosition(Request request) {
        for (int i = 0; i < mRequests.size(); i++) {
            if (mRequests.get(i).getActivity().equals(request.getActivity())) {
                return i;
            }
        }
        return -1;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            View view = LayoutInflater.from(mContext).inflate(
                    R.layout.fragment_request_item_header, parent, false);

            StaggeredGridLayoutManager.LayoutParams params = getLayoutParams(view);
            if (params != null) params.setFullSpan(false);
            return new HeaderViewHolder(view);
        } else if (viewType == TYPE_CONTENT) {
            View view = LayoutInflater.from(mContext).inflate(
                    R.layout.fragment_request_item_list, parent, false);

            StaggeredGridLayoutManager.LayoutParams params = getLayoutParams(view);
            if (params != null) params.setFullSpan(false);
            return new ContentViewHolder(view);
        }

        View view = LayoutInflater.from(mContext).inflate(
                R.layout.fragment_request_item_footer, parent, false);

        StaggeredGridLayoutManager.LayoutParams params = getLayoutParams(view);
        if (params != null) params.setFullSpan(true);
        return new FooterViewHolder(view);
    }

    @Override
    public void onViewRecycled(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewRecycled(holder);
        if (holder.getItemViewType() == TYPE_CONTENT) {
            ContentViewHolder contentViewHolder = (ContentViewHolder) holder;
            contentViewHolder.content.setTextColor(mTextColorSecondary);

            if (mShowShadow) {
                contentViewHolder.divider.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == TYPE_HEADER) {
            HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
            if (mShowPremiumRequest) {
                if (Preferences.get(mContext).isPremiumRequest()) {
                    headerViewHolder.button.setVisibility(View.GONE);
                    headerViewHolder.premContent.setVisibility(View.GONE);
                    headerViewHolder.premContainer.setVisibility(View.VISIBLE);

                    int total = Preferences.get(mContext).getPremiumRequestTotal();
                    int available = Preferences.get(mContext).getPremiumRequestCount();

                    headerViewHolder.premTotal.setText(mContext.getResources().getString(
                            R.string.premium_request_count, total));
                    headerViewHolder.premAvailable.setText(mContext.getResources().getString(
                            R.string.premium_request_available, available));
                    headerViewHolder.premUsed.setText(mContext.getResources().getString(
                            R.string.premium_request_used, (total - available)));

                    headerViewHolder.premProgress.setMax(total);
                    headerViewHolder.premProgress.setProgress(available);
                    headerViewHolder.premProgress.getProgressDrawable().setColorFilter(
                            ColorHelper.getAttributeColor(mContext, R.attr.cb_colorAccent),
                            PorterDuff.Mode.SRC_IN);
                } else {
                    headerViewHolder.button.setVisibility(View.VISIBLE);
                    headerViewHolder.premContent.setVisibility(View.VISIBLE);
                    headerViewHolder.premContainer.setVisibility(View.GONE);
                }
            } else {
                headerViewHolder.premWholeContainer.setVisibility(View.GONE);
            }

            if (mShowRegularRequestLimit) {
                int total = mContext.getResources().getInteger(R.integer.icon_request_limit);
                int used = Preferences.get(mContext).getRegularRequestUsed();
                int available = total - used;

                headerViewHolder.regTotal.setText(mContext.getResources().getString(
                        R.string.regular_request_count, total));
                headerViewHolder.regAvailable.setText(mContext.getResources().getString(
                        R.string.regular_request_available, available));
                headerViewHolder.regUsed.setText(mContext.getResources().getString(
                        R.string.regular_request_used, used));

                headerViewHolder.regProgress.setMax(total);
                headerViewHolder.regProgress.setProgress(available);
                headerViewHolder.regProgress.getProgressDrawable().setColorFilter(
                        ColorHelper.getAttributeColor(mContext, R.attr.cb_colorAccent),
                        PorterDuff.Mode.SRC_IN);
            } else {
                headerViewHolder.regWholeContainer.setVisibility(View.GONE);
            }
        } else if (holder.getItemViewType() == TYPE_CONTENT) {
            int finalPosition = position;
            if (mShowPremiumRequest || mShowRegularRequestLimit) finalPosition -= 1;
            ContentViewHolder contentViewHolder = (ContentViewHolder) holder;

            Request request = mFilteredRequests.get(finalPosition);
            int originalPosition = getOriginalPosition(request);
            
            // Set checkbox state based on request state
            int tintColor;
            if (request.isRequested() && !mContext.getResources().getBoolean(R.bool.enable_icon_request_multiple)) {
                contentViewHolder.checkbox.setEnabled(false);
                contentViewHolder.checkbox.setAlpha(0.3f);
                tintColor = Color.parseColor("#808080"); // Grey color for already requested icons
            } else {
                contentViewHolder.checkbox.setEnabled(true);
                contentViewHolder.checkbox.setAlpha(1.0f);
                tintColor = ColorHelper.getAttributeColor(mContext, R.attr.cb_colorAccent);
            }
            CompoundButtonCompat.setButtonTintList(contentViewHolder.checkbox, 
                ColorStateList.valueOf(tintColor));

            contentViewHolder.title.setText(request.getName());
            contentViewHolder.infoIcon.setVisibility(View.GONE);

            if (mSelectedItems.get(originalPosition, false)) {
                // Selected state
                contentViewHolder.container.setBackgroundColor(mSelectionHighlightColor);
                contentViewHolder.title.setTextColor(ColorHelper.getAttributeColor(mContext, R.attr.cb_colorAccent));
                if (!request.isRequested()) {
                    contentViewHolder.content.setTextColor(ColorHelper.getAttributeColor(mContext, R.attr.cb_colorAccent));
                }
            } else {
                // Unselected state
                contentViewHolder.container.setBackgroundColor(Color.TRANSPARENT);
                contentViewHolder.title.setTextColor(ColorHelper.getAttributeColor(mContext, android.R.attr.textColorPrimary));
                
                // Set content text color based on request state
                if (request.isRequested()) {
                    contentViewHolder.content.setTextColor(ColorHelper.getAttributeColor(mContext, R.attr.cb_colorAccent));
                    contentViewHolder.content.setText(mContext.getResources().getString(R.string.request_already_requested));
                } else if (request.isAvailableForRequest()) {
                    contentViewHolder.content.setTextColor(ColorHelper.getAttributeColor(mContext, android.R.attr.textColorPrimary));
                    contentViewHolder.content.setText(mContext.getResources().getString(R.string.request_not_requested));
                } else {
                    contentViewHolder.content.setTextColor(mTextColorSecondary);
                    contentViewHolder.content.setText(mContext.getResources().getString(R.string.request_not_available));
                }
            }

            // Handle opacity based on request state
            if (request.isRequested() && !mContext.getResources().getBoolean(R.bool.enable_icon_request_multiple)) {
                contentViewHolder.content.setAlpha(1f);
                contentViewHolder.title.setAlpha(1f);
                contentViewHolder.icon.setAlpha(1f);
                contentViewHolder.checkbox.setEnabled(false);
            } else if (!request.isAvailableForRequest()) {
                contentViewHolder.content.setAlpha(0.5f);
                contentViewHolder.title.setAlpha(0.5f);
                contentViewHolder.icon.setAlpha(0.5f);
                contentViewHolder.checkbox.setEnabled(false);
            } else {
                contentViewHolder.content.setAlpha(1f);
                contentViewHolder.title.setAlpha(1f);
                contentViewHolder.icon.setAlpha(1f);
                contentViewHolder.checkbox.setEnabled(true);
            }

            // Handle info text
            if (!request.getInfoText().isEmpty()) {
                contentViewHolder.infoIcon.setVisibility(View.VISIBLE);
                contentViewHolder.infoIcon.setImageDrawable(AppCompatResources.getDrawable(mContext, R.drawable.ic_drawer_about));
                contentViewHolder.infoIcon.setColorFilter(mTextColorSecondary);
                contentViewHolder.infoIcon.setOnClickListener(v -> new MaterialDialog.Builder(mContext)
                        .typeface(TypefaceHelper.getMedium(mContext), TypefaceHelper.getRegular(mContext))
                        .title(request.getName())
                        .content(request.getInfoText())
                        .positiveText(android.R.string.yes)
                        .show());
            }

            if (CandyBarGlideModule.isValidContextForGlide(mContext)) {
                Glide.with(mContext)
                        .load("package://" + request.getActivity())
                        .override(272)
                        .transition(DrawableTransitionOptions.withCrossFade(300))
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .into(contentViewHolder.icon);
            }

            contentViewHolder.checkbox.setChecked(mSelectedItems.get(originalPosition, false));

            if (finalPosition == (mFilteredRequests.size() - 1) && mShowShadow) {
                contentViewHolder.divider.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public int getItemCount() {
        int size = mFilteredRequests.size();
        if (mShowPremiumRequest || mShowRegularRequestLimit) size += 1;
        if (mShowShadow) size += 1;
        return size;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0 && (mShowPremiumRequest || mShowRegularRequestLimit)) return TYPE_HEADER;
        if (position == (getItemCount() - 1) && mShowShadow) return TYPE_FOOTER;
        return TYPE_CONTENT;
    }

    private class HeaderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final TextView premContent;
        private final TextView premTotal;
        private final TextView premAvailable;
        private final TextView premUsed;
        private final Button button;
        private final LinearLayout premContainer;
        private final LinearLayout premWholeContainer;
        private final ProgressBar premProgress;

        private final TextView regTotal;
        private final TextView regAvailable;
        private final TextView regUsed;
        private final LinearLayout regWholeContainer;
        private final ProgressBar regProgress;

        HeaderViewHolder(View itemView) {
            super(itemView);
            TextView premTitle = itemView.findViewById(R.id.premium_request_title);
            premContent = itemView.findViewById(R.id.premium_request_content);
            button = itemView.findViewById(R.id.buy);

            premWholeContainer = itemView.findViewById(R.id.premium_request_container);
            premContainer = itemView.findViewById(R.id.premium_request);
            premTotal = itemView.findViewById(R.id.premium_request_total);
            premAvailable = itemView.findViewById(R.id.premium_request_available);
            premUsed = itemView.findViewById(R.id.premium_request_used);
            premProgress = itemView.findViewById(R.id.premium_request_progress);


            TextView regTitle = itemView.findViewById(R.id.regular_request_title);
            TextView regContent = itemView.findViewById(R.id.regular_request_content);
            regWholeContainer = itemView.findViewById(R.id.regular_request_container);
            LinearLayout regContainer = itemView.findViewById(R.id.regular_request);
            regTotal = itemView.findViewById(R.id.regular_request_total);
            regAvailable = itemView.findViewById(R.id.regular_request_available);
            regUsed = itemView.findViewById(R.id.regular_request_used);
            regProgress = itemView.findViewById(R.id.regular_request_progress);

            MaterialCardView card = itemView.findViewById(R.id.card);
            if (CandyBarApplication.getConfiguration().getRequestStyle() == CandyBarApplication.Style.PORTRAIT_FLAT_LANDSCAPE_FLAT &&
                    card != null) {
                if (card.getLayoutParams() instanceof StaggeredGridLayoutManager.LayoutParams) {
                    card.setRadius(0f);
                    card.setUseCompatPadding(false);
                    int margin = mContext.getResources().getDimensionPixelSize(R.dimen.card_margin);
                    StaggeredGridLayoutManager.LayoutParams params = (StaggeredGridLayoutManager.LayoutParams) card.getLayoutParams();
                    params.setMargins(0, 0, margin, margin);
                    params.setMarginEnd(margin);
                }
            }

            if (mContext.getResources().getBoolean(R.bool.use_flat_card) && card != null) {
                card.setStrokeWidth(mContext.getResources().getDimensionPixelSize(R.dimen.card_stroke_width));
                card.setCardElevation(0);
                card.setUseCompatPadding(false);
                int marginTop = mContext.getResources().getDimensionPixelSize(R.dimen.card_margin_top);
                int marginLeft = mContext.getResources().getDimensionPixelSize(R.dimen.card_margin_left);
                int marginRight = mContext.getResources().getDimensionPixelSize(R.dimen.card_margin_right);
                int marginBottom = mContext.getResources().getDimensionPixelSize(R.dimen.card_margin_bottom);
                StaggeredGridLayoutManager.LayoutParams params = (StaggeredGridLayoutManager.LayoutParams) card.getLayoutParams();
                params.setMargins(marginLeft, marginTop, marginRight, marginBottom);
            }

            if (!Preferences.get(mContext).isCardShadowEnabled()) {
                if (card != null) card.setCardElevation(0);
            }

            int padding = mContext.getResources().getDimensionPixelSize(R.dimen.content_margin) + mContext.getResources().getDimensionPixelSize(R.dimen.icon_size_small);
            premContent.setPadding(padding, 0, 0, 0);
            premContainer.setPadding(padding, 0, padding, 0);

            regContent.setPadding(padding, 0, 0, 0);
            regContainer.setPadding(padding, 0, padding, 0);

            int color = ColorHelper.getAttributeColor(mContext, android.R.attr.textColorPrimary);
            premTitle.setCompoundDrawablesWithIntrinsicBounds(
                    DrawableHelper.getTintedDrawable(mContext,
                            R.drawable.ic_toolbar_premium_request, color),
                    null, null, null);

            regTitle.setCompoundDrawablesWithIntrinsicBounds(
                    DrawableHelper.getTintedDrawable(mContext,
                            R.drawable.ic_toolbar_icon_request, color),
                    null, null, null);

            int primary = ColorHelper.getAttributeColor(mContext, androidx.appcompat.R.attr.colorPrimary);
            int accent = ColorHelper.getAttributeColor(mContext, com.google.android.material.R.attr.colorSecondary);
            button.setTextColor(ColorHelper.getAttributeColor(mContext, android.R.attr.textColorPrimary));

            premProgress.getProgressDrawable().setColorFilter(accent, PorterDuff.Mode.SRC_IN);
            regProgress.getProgressDrawable().setColorFilter(accent, PorterDuff.Mode.SRC_IN);

            button.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int id = view.getId();
            if (id == R.id.buy) {
                RequestListener listener = (RequestListener) mContext;
                listener.onBuyPremiumRequest();
            }
        }
    }

    public interface ToggleResultListener {
        void onPositiveResult();
        void onNegativeResult();
    }

    private class ContentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        private final TextView title;
        private final TextView content;
        private final ImageView icon;
        private final CheckBox checkbox;
        private final ImageView infoIcon;
        private final View divider;
        private final LinearLayout container;

        ContentViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.name);
            content = itemView.findViewById(R.id.requested);
            icon = itemView.findViewById(R.id.icon);
            checkbox = itemView.findViewById(R.id.checkbox);
            infoIcon = itemView.findViewById(R.id.requestedInfoIcon);
            container = itemView.findViewById(R.id.container);
            divider = itemView.findViewById(R.id.divider);

            container.setOnClickListener(this);
            container.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int id = view.getId();
            if (id == R.id.container) {
                int position = mShowPremiumRequest || mShowRegularRequestLimit ?
                        getBindingAdapterPosition() - 1 : getBindingAdapterPosition();
                toggleSelection(position, new ToggleResultListener() {
                    @Override public void onPositiveResult() {
                        checkbox.toggle();
                        // Update background and text colors immediately after selection
                        if (checkbox.isChecked()) {
                            container.setBackgroundColor(mSelectionHighlightColor);
                            title.setTextColor(ColorHelper.getAttributeColor(mContext, R.attr.cb_colorAccent));
                            if (!mFilteredRequests.get(getBindingAdapterPosition() - 1).isRequested()) {
                                content.setTextColor(ColorHelper.getAttributeColor(mContext, R.attr.cb_colorAccent));
                            }
                        } else {
                            container.setBackgroundColor(Color.TRANSPARENT);
                            title.setTextColor(ColorHelper.getAttributeColor(mContext, android.R.attr.textColorPrimary));
                            if (!mFilteredRequests.get(getBindingAdapterPosition() - 1).isRequested()) {
                                content.setTextColor(ColorHelper.getAttributeColor(mContext, android.R.attr.textColorPrimary));
                            }
                        }
                        try {
                            RequestListener listener = (RequestListener) mContext;
                            listener.onRequestSelected(getSelectedItemsSize());
                        } catch (Exception ignored) {
                        }
                    }
                    @Override public void onNegativeResult() { /* Do nothing */ }
                });
            }
        }

        @Override
        public boolean onLongClick(View view) {
            int id = view.getId();
            if (id == R.id.container) {
                int position = mShowPremiumRequest || mShowRegularRequestLimit ?
                        getBindingAdapterPosition() - 1 : getBindingAdapterPosition();
                toggleSelection(position, new ToggleResultListener() {
                    @Override public void onPositiveResult() {
                        checkbox.toggle();
                        // Update background and text colors immediately after selection
                        if (checkbox.isChecked()) {
                            container.setBackgroundColor(mSelectionHighlightColor);
                            title.setTextColor(ColorHelper.getAttributeColor(mContext, R.attr.cb_colorAccent));
                            if (!mFilteredRequests.get(getBindingAdapterPosition() - 1).isRequested()) {
                                content.setTextColor(ColorHelper.getAttributeColor(mContext, R.attr.cb_colorAccent));
                            }
                        } else {
                            container.setBackgroundColor(Color.TRANSPARENT);
                            title.setTextColor(ColorHelper.getAttributeColor(mContext, android.R.attr.textColorPrimary));
                            if (!mFilteredRequests.get(getBindingAdapterPosition() - 1).isRequested()) {
                                content.setTextColor(ColorHelper.getAttributeColor(mContext, android.R.attr.textColorPrimary));
                            }
                        }
                        try {
                            RequestListener listener = (RequestListener) mContext;
                            listener.onRequestSelected(getSelectedItemsSize());
                        } catch (Exception ignored) {
                        }
                    }
                    @Override public void onNegativeResult() { /* Do nothing */ }
                });
                return true;
            }
            return false;
        }
    }

    private class FooterViewHolder extends RecyclerView.ViewHolder {

        FooterViewHolder(View itemView) {
            super(itemView);
            View shadow = itemView.findViewById(R.id.shadow);
            if (!Preferences.get(mContext).isCardShadowEnabled()) {
                shadow.setVisibility(View.GONE);
            }
        }
    }

    @Nullable
    private StaggeredGridLayoutManager.LayoutParams getLayoutParams(@Nullable View view) {
        if (view != null) {
            try {
                return (StaggeredGridLayoutManager.LayoutParams) view.getLayoutParams();
            } catch (Exception e) {
                LogUtil.d(Log.getStackTraceString(e));
            }
        }
        return null;
    }

    private void notifySelectionChanged() {
        try {
            RequestListener listener = (RequestListener) mContext;
            listener.onRequestSelected(getSelectedItemsSize());
        } catch (Exception ignored) {
        }
    }

    private void hideSearchModeFab() {
        try {
            if (mContext instanceof CandyBarMainActivity) {
                FloatingActionButton fab = ((CandyBarMainActivity) mContext).findViewById(R.id.fab);
                if (fab != null) {
                    fab.hide();
                }
            }
        } catch (Exception ignored) {
        }
    }

    private void showSearchModeFab() {
        try {
            if (mContext instanceof CandyBarMainActivity) {
                FloatingActionButton fab = ((CandyBarMainActivity) mContext).findViewById(R.id.fab);
                if (fab != null) {
                    fab.show();
                }
            }
        } catch (Exception ignored) {
        }
    }

    public void toggleSelection(int position, ToggleResultListener toggleListener) {
        if (position >= 0 && position < mFilteredRequests.size()) {
            Request request = mFilteredRequests.get(position);
            int originalPosition = getOriginalPosition(request);

            boolean isSelected = mSelectedItems.get(originalPosition, false);
            boolean isRequested = request.isRequested();
            boolean isDuplicateRequestAllowed = mContext.getResources().getBoolean(R.bool.enable_icon_request_multiple);

            if (isSelected) {
                mSelectedItems.delete(originalPosition);
                toggleListener.onPositiveResult();
                notifySelectionChanged();
                if (mIsSearchMode) {
                    if (getSelectedItemsSize() > 0) {
                        showSearchModeFab();
                    } else {
                        hideSearchModeFab();
                    }
                } else {
                    updateNormalModeFabVisibility();
                }
            } else if (isRequested) {
                if (isDuplicateRequestAllowed) {
                    new MaterialDialog.Builder(mContext)
                            .typeface(TypefaceHelper.getMedium(mContext), TypefaceHelper.getRegular(mContext))
                            .title(R.string.request_already_requested)
                            .content(R.string.request_requested_possible)
                            .cancelable(false)
                            .canceledOnTouchOutside(false)
                            .negativeText(R.string.request_requested_button_cancel)
                            .onNegative((dialog, which) -> toggleListener.onNegativeResult())
                            .positiveText(R.string.request_requested_button_confirm)
                            .onPositive((dialog, which) -> {
                                mSelectedItems.put(originalPosition, true);
                                toggleListener.onPositiveResult();
                                if (mIsSearchMode) {
                                    if (getSelectedItemsSize() > 0) {
                                        showSearchModeFab();
                                    } else {
                                        hideSearchModeFab();
                                    }
                                } else {
                                    updateNormalModeFabVisibility();
                                }
                            })
                            .show();
                } else {
                    toggleListener.onNegativeResult();
                    new MaterialDialog.Builder(mContext)
                            .typeface(TypefaceHelper.getMedium(mContext), TypefaceHelper.getRegular(mContext))
                            .title(R.string.request_not_available)
                            .content(R.string.request_requested)
                            .negativeText(R.string.request_requested_button_cancel)
                            .backgroundColorAttr(R.attr.cb_cardBackground)
                            .negativeColor(ColorHelper.getAttributeColor(mContext, R.attr.cb_colorAccent))
                            .show();
                }
            } else if (!request.isAvailableForRequest()) {
                toggleListener.onNegativeResult();
                if (!request.getInfoText().isEmpty()) {
                    new MaterialDialog.Builder(mContext)
                            .typeface(TypefaceHelper.getMedium(mContext), TypefaceHelper.getRegular(mContext))
                            .title(mContext.getResources().getString(R.string.request_not_available))
                            .content(request.getInfoText())
                            .positiveText(android.R.string.yes)
                            .show();
                }
            } else {
                mSelectedItems.put(originalPosition, true);
                toggleListener.onPositiveResult();
                if (mIsSearchMode) {
                    if (getSelectedItemsSize() > 0) {
                        showSearchModeFab();
                    } else {
                        hideSearchModeFab();
                    }
                } else {
                    updateNormalModeFabVisibility();
                }
            }
        } else {
            toggleListener.onNegativeResult();
        }
    }

    public boolean selectAll() {
        if (mSelectedAll) {
            mSelectedAll = false;
            mSelectedItems.clear();
            notifyDataSetChanged();
            try {
                RequestListener listener = (RequestListener) mContext;
                listener.onRequestSelected(getSelectedItemsSize());
            } catch (Exception ignored) {
            }
            if (mIsSearchMode) {
                if (getSelectedItemsSize() > 0) {
                    showSearchModeFab();
                } else {
                    hideSearchModeFab();
                }
            } else {
                updateNormalModeFabVisibility();
            }
            return false;
        }

        mSelectedItems.clear();
        for (int i = 0; i < mFilteredRequests.size(); i++) {
            Request request = mFilteredRequests.get(i);
            if (!request.isRequested() && request.isAvailableForRequest()) {
                int originalPosition = getOriginalPosition(request);
                if (originalPosition != -1) {
                    mSelectedItems.put(originalPosition, true);
                }
            }
        }
        mSelectedAll = mSelectedItems.size() > 0;
        notifyDataSetChanged();

        try {
            RequestListener listener = (RequestListener) mContext;
            listener.onRequestSelected(getSelectedItemsSize());
        } catch (Exception ignored) {
        }
        if (mIsSearchMode) {
            if (getSelectedItemsSize() > 0) {
                showSearchModeFab();
            } else {
                hideSearchModeFab();
            }
        } else {
            updateNormalModeFabVisibility();
        }
        return mSelectedAll;
    }

    public void setRequested(int position, boolean requested) {
        if (position >= 0 && position < mRequests.size()) {
            mRequests.get(position).setRequested(requested);
            // Update filtered list if it contains this item
            for (Request request : mFilteredRequests) {
                if (request.getActivity().equals(mRequests.get(position).getActivity())) {
                    request.setRequested(requested);
                    break;
                }
            }
        }
    }

    public int getSelectedItemsSize() {
        return mSelectedItems.size();
    }

    public List<Integer> getSelectedItems() {
        List<Integer> selected = new ArrayList<>();
        for (int i = 0; i < mSelectedItems.size(); i++) {
            selected.add(mSelectedItems.keyAt(i));
        }
        return selected;
    }

    public SparseBooleanArray getSelectedItemsArray() {
        return mSelectedItems;
    }

    public void setSelectedItemsArray(SparseBooleanArray selectedItems) {
        mSelectedItems = selectedItems;
        notifyDataSetChanged();
    }

    public void resetSelectedItems() {
        mSelectedAll = false;
        mSelectedItems.clear();
        try {
            RequestListener listener = (RequestListener) mContext;
            listener.onRequestSelected(getSelectedItemsSize());
        } catch (Exception ignored) {
        }
        if (mIsSearchMode) {
            if (getSelectedItemsSize() > 0) {
                showSearchModeFab();
            } else {
                hideSearchModeFab();
            }
        } else {
            updateNormalModeFabVisibility();
        }
        notifyDataSetChanged();
    }

    public List<Request> getSelectedApps() {
        List<Request> items = new ArrayList<>(mSelectedItems.size());
        for (int i = 0; i < mSelectedItems.size(); i++) {
            int position = mSelectedItems.keyAt(i);
            if (position >= 0 && position < mFilteredRequests.size()) {
                Request request = mFilteredRequests.get(position);
                items.add(request);
            }
        }
        return items;
    }

    public boolean isContainsRequested() {
        List<Request> requests = getSelectedApps();
        boolean requested = false;
        for (int i = 0; i < requests.size(); i++) {
            if (requests.get(i).isRequested()) {
                requested = true;
                break;
            }
        }
        return requested;
    }

    private void updateNormalModeFabVisibility() {
        try {
            if (mContext instanceof CandyBarMainActivity) {
                FloatingActionButton fab = ((CandyBarMainActivity) mContext).findViewById(R.id.fab);
                if (fab != null) {
                    fab.show();
                }
            }
        } catch (Exception ignored) {
        }
    }

    public void setSearchMode(boolean searchMode) {
        mIsSearchMode = searchMode;
        if (mIsSearchMode) {
            if (getSelectedItemsSize() > 0) {
                showSearchModeFab();
            } else {
                hideSearchModeFab();
            }
        } else {
            updateNormalModeFabVisibility();
        }
    }
}

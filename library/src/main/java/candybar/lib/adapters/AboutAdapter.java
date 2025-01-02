package candybar.lib.adapters;

import static candybar.lib.helpers.DrawableHelper.getDrawableId;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;
import androidx.core.text.HtmlCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.danimahardhika.android.helpers.core.ColorHelper;
import com.danimahardhika.android.helpers.core.DrawableHelper;
import com.danimahardhika.android.helpers.core.utils.LogUtil;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.HashMap;

import candybar.lib.BuildConfig;
import candybar.lib.R;
import candybar.lib.applications.CandyBarApplication;
import candybar.lib.fragments.dialog.CreditsFragment;
import candybar.lib.fragments.dialog.LicensesFragment;
import candybar.lib.fragments.dialog.PrivacyPolicyDialog;
import candybar.lib.helpers.ConfigurationHelper;
import candybar.lib.preferences.Preferences;
import candybar.lib.utils.CandyBarGlideModule;

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

public class AboutAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context mContext;

    private int mItemCount;

    private static final int TYPE_HEADER = 0;

    public AboutAdapter(@NonNull Context context, int spanCount) {
        mContext = context;
        mItemCount = 1;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(
                R.layout.fragment_about_item_header, parent, false);
        return new HeaderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == TYPE_HEADER) {
            HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;

            String imageUri = mContext.getString(R.string.about_image);

            if (ColorHelper.isValidColor(imageUri)) {
                headerViewHolder.image.setBackgroundColor(Color.parseColor(imageUri));
            } else {
                if (!URLUtil.isValidUrl(imageUri)) {
                    imageUri = "drawable://" + getDrawableId(imageUri);
                }

                if (CandyBarGlideModule.isValidContextForGlide(mContext)) {
                    Glide.with(mContext)
                            .load(imageUri)
                            .transition(DrawableTransitionOptions.withCrossFade(300))
                            .skipMemoryCache(true)
                            .diskCacheStrategy(imageUri.contains("drawable://")
                                    ? DiskCacheStrategy.NONE
                                    : DiskCacheStrategy.RESOURCE)
                            .into(headerViewHolder.image);
                }
            }

            String profileUri = mContext.getResources().getString(R.string.about_profile_image);
            if (!URLUtil.isValidUrl(profileUri)) {
                profileUri = "drawable://" + getDrawableId(profileUri);
            }

            if (CandyBarGlideModule.isValidContextForGlide(mContext)) {
                Glide.with(mContext)
                        .load(profileUri)
                        .skipMemoryCache(true)
                        .diskCacheStrategy(profileUri.contains("drawable://")
                                ? DiskCacheStrategy.NONE
                                : DiskCacheStrategy.RESOURCE)
                        .into(headerViewHolder.profile);
            }
        }
    }

    @Override
    public int getItemCount() {
        return mItemCount;
    }

    @Override
    public int getItemViewType(int position) {
        return TYPE_HEADER;
    }

    private class HeaderViewHolder extends RecyclerView.ViewHolder {

        private final ImageView image;
        private final ImageView profile;

        HeaderViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            profile = itemView.findViewById(R.id.profile);
            TextView subtitle = itemView.findViewById(R.id.subtitle);
            RecyclerView recyclerView = itemView.findViewById(R.id.recyclerview);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
            recyclerView.setHasFixedSize(true);

            int topMargin = mContext.getResources().getDimensionPixelSize(R.dimen.content_margin) * 4;
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) recyclerView.getLayoutParams();
            params.topMargin = topMargin;
            params.gravity = Gravity.END;
            recyclerView.setLayoutParams(params);

            String[] urls = mContext.getResources().getStringArray(R.array.about_social_links);
            if (urls.length == 0) {
                recyclerView.setVisibility(View.GONE);

                subtitle.setPadding(
                        subtitle.getPaddingLeft(),
                        subtitle.getPaddingTop(),
                        subtitle.getPaddingRight(),
                        subtitle.getPaddingBottom() + mContext.getResources().getDimensionPixelSize(R.dimen.content_margin));
            } else {
                if (recyclerView.getLayoutParams() instanceof LinearLayout.LayoutParams) {
                    params = (LinearLayout.LayoutParams) recyclerView.getLayoutParams();
                    if (urls.length < 7) {
                        params.width = LinearLayout.LayoutParams.WRAP_CONTENT;
                        params.gravity = Gravity.END;
                        recyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
                    }
                    params.topMargin = topMargin;
                    recyclerView.setLayoutParams(params);
                }
                recyclerView.setAdapter(new AboutSocialAdapter(mContext, urls));
            }

            subtitle.setText(HtmlCompat.fromHtml(
                    mContext.getResources().getString(R.string.about_desc), HtmlCompat.FROM_HTML_MODE_COMPACT));
            subtitle.setMovementMethod(LinkMovementMethod.getInstance());
            subtitle.setTextColor(ColorHelper.getAttributeColor(mContext, R.attr.cb_primaryText));

            MaterialCardView privacyPolicyButton = itemView.findViewById(R.id.privacy_policy_button);
            String privacyLink = mContext.getResources().getString(R.string.privacy_policy_link);
            
            if (privacyLink == null || privacyLink.trim().isEmpty()) {
                privacyPolicyButton.setVisibility(View.GONE);
            } else {
                privacyPolicyButton.setVisibility(View.VISIBLE);
                privacyPolicyButton.setOnClickListener(v -> {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(privacyLink));
                    try {
                        mContext.startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        Toast.makeText(mContext, 
                            R.string.no_browser, 
                            Toast.LENGTH_LONG).show();
                    }
                });
            }

            MaterialCardView termsButton = itemView.findViewById(R.id.terms_conditions_button);
            String termsLink = ConfigurationHelper.getTermsAndConditionsLink(mContext);
            
            if (termsLink == null || termsLink.trim().isEmpty()) {
                termsButton.setVisibility(View.GONE);
            } else {
                termsButton.setVisibility(View.VISIBLE);
                termsButton.setOnClickListener(v -> {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(termsLink));
                    try {
                        mContext.startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        Toast.makeText(mContext, 
                            R.string.no_browser, 
                            Toast.LENGTH_LONG).show();
                    }
                });
            }
        }
    }

    private class SubItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final TextView title;

        SubItemHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.privacy_policy_title);
            title.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int id = view.getId();
            if (id == R.id.privacy_policy_title) {
                String privacyLink = mContext.getResources().getString(R.string.privacy_policy_link);
                if (privacyLink != null && !privacyLink.trim().isEmpty()) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(privacyLink));
                    try {
                        mContext.startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        Toast.makeText(mContext, 
                            R.string.no_browser, 
                            Toast.LENGTH_LONG).show();
                    }
                }
            }
        }
    }
}

package candybar.lib.adapters;

import static candybar.lib.helpers.DrawableHelper.getDrawableId;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
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
import android.content.res.Configuration;

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
import candybar.lib.fragments.dialog.ChangelogFragment;
import candybar.lib.helpers.ViewHelper;
import candybar.lib.utils.ImageConfig;
import candybar.lib.utils.views.HeaderView;

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
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
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

            // Set title text
            TextView title = headerViewHolder.itemView.findViewById(R.id.title);
            if (title != null) {
                title.setText(mContext.getResources().getString(R.string.about_title));
                setTextColorForOldAndroid(title);
            }

            // Set version text and app icon
            if (headerViewHolder.appVersion != null) {
                try {
                    String versionName = mContext.getPackageManager()
                            .getPackageInfo(mContext.getPackageName(), 0).versionName;
                    int versionCode = mContext.getPackageManager()
                            .getPackageInfo(mContext.getPackageName(), 0).versionCode;
                    headerViewHolder.appVersion.setText(versionName + " (" + versionCode + ")");
                    setTextColorForOldAndroid(headerViewHolder.appVersion);

                    // Load app icon
                    ImageView appIcon = headerViewHolder.itemView.findViewById(R.id.app_icon);
                    if (appIcon != null) {
                        try {
                            appIcon.setImageDrawable(mContext.getPackageManager().getApplicationIcon(mContext.getPackageName()));
                        } catch (Exception e) {
                            appIcon.setVisibility(View.GONE);
                        }
                    }
                } catch (Exception e) {
                    headerViewHolder.appVersion.setVisibility(View.GONE);
                }
            }

            // Set description text with HTML formatting
            if (headerViewHolder.description != null) {
                int linkColor = ColorHelper.getAttributeColor(mContext, R.attr.cb_colorAccent);
                String colorHex = String.format("#%06X", (0xFFFFFF & linkColor));
                String description = String.format(mContext.getString(R.string.candybar_description), colorHex);
                
                headerViewHolder.description.setText(HtmlCompat.fromHtml(
                    description,
                    HtmlCompat.FROM_HTML_MODE_LEGACY));
                headerViewHolder.description.setMovementMethod(LinkMovementMethod.getInstance());
                headerViewHolder.description.setLinkTextColor(linkColor);
                setTextColorForOldAndroid(headerViewHolder.description);
            }

            // Set special thanks text with HTML formatting
            if (headerViewHolder.specialThanks != null) {
                int linkColor = ColorHelper.getAttributeColor(mContext, R.attr.cb_colorAccent);
                String colorHex = String.format("#%06X", (0xFFFFFF & linkColor));
                String specialThanks = String.format(mContext.getString(R.string.candybar_special_thanks), colorHex);
                
                headerViewHolder.specialThanks.setText(HtmlCompat.fromHtml(
                    specialThanks,
                    HtmlCompat.FROM_HTML_MODE_LEGACY));
                headerViewHolder.specialThanks.setMovementMethod(LinkMovementMethod.getInstance());
                headerViewHolder.specialThanks.setLinkTextColor(linkColor);
                setTextColorForOldAndroid(headerViewHolder.specialThanks);
            }

            // Set up GitHub button click listeners
            if (headerViewHolder.originalGithubButton != null) {
                headerViewHolder.originalGithubButton.setOnClickListener(v -> {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/danimahardhika/candybar-library"));
                    mContext.startActivity(intent);
                });
            }

            if (headerViewHolder.currentGithubButton != null) {
                headerViewHolder.currentGithubButton.setOnClickListener(v -> {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/zixpo/candybar"));
                    mContext.startActivity(intent);
                });
            }

            // Set up Sam's section
            TextView samText = headerViewHolder.itemView.findViewById(R.id.candybar_by_sam_text);
            MaterialCardView samSummerCard = headerViewHolder.itemView.findViewById(R.id.sam_summer_card);
            if (samSummerCard != null) {
                // Navigate through the nested structure to find the description TextView
                View outerContent = samSummerCard.getChildAt(0);
                if (outerContent instanceof ViewGroup) {
                    ViewGroup outerGroup = (ViewGroup) outerContent;
                    // Look for the inner MaterialCardView that contains the description
                    for (int i = 0; i < outerGroup.getChildCount(); i++) {
                        View child = outerGroup.getChildAt(i);
                        if (child instanceof MaterialCardView) {
                            // Found the inner card, now look for the TextView
                            View innerContent = ((MaterialCardView) child).getChildAt(0);
                            if (innerContent instanceof TextView) {
                                TextView descriptionText = (TextView) innerContent;
                                if (descriptionText.getText().toString().equals(mContext.getString(R.string.sam_summer_description))) {
                                    setTextColorForOldAndroid(descriptionText);
                                    break;
                                }
                            }
                        }
                    }
                }
            }

            if (samText != null) {
                int linkColor = ColorHelper.getAttributeColor(mContext, R.attr.cb_colorAccent);
                String colorHex = String.format("#%06X", (0xFFFFFF & linkColor));
                String text = String.format(mContext.getString(R.string.candybar_by_sam), colorHex);
                
                samText.setText(HtmlCompat.fromHtml(
                    text,
                    HtmlCompat.FROM_HTML_MODE_LEGACY));
                samText.setMovementMethod(LinkMovementMethod.getInstance());
                samText.setLinkTextColor(linkColor);
                setTextColorForOldAndroid(samText);
            }

            // Set up Sam's GitHub button click listener
            MaterialCardView samGithubButton = headerViewHolder.itemView.findViewById(R.id.sam_github_button);
            if (samGithubButton != null) {
                samGithubButton.setOnClickListener(v -> {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/SamNeill"));
                    try {
                        mContext.startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        Toast.makeText(mContext, R.string.no_browser, Toast.LENGTH_LONG).show();
                    }
                });
            }

            // Set up version card click listener
            MaterialCardView versionCard = headerViewHolder.itemView.findViewById(R.id.version_card);
            if (versionCard != null) {
                versionCard.setOnClickListener(v -> {
                    if (mContext instanceof AppCompatActivity) {
                        ChangelogFragment.showChangelog(
                            ((AppCompatActivity) mContext).getSupportFragmentManager(),
                            () -> {}
                        );
                    }
                });
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
        private final MaterialCardView originalGithubButton;
        private final MaterialCardView currentGithubButton;
        private final TextView appVersion;
        private final TextView description;
        private final TextView specialThanks;
        private final MaterialCardView privacyPolicyButton;
        private final MaterialCardView termsButton;
        private final MaterialCardView versionCard;

        HeaderViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            profile = itemView.findViewById(R.id.profile);
            originalGithubButton = itemView.findViewById(R.id.original_github_button);
            currentGithubButton = itemView.findViewById(R.id.current_github_button);
            appVersion = itemView.findViewById(R.id.app_version);
            description = itemView.findViewById(R.id.candybar_description);
            specialThanks = itemView.findViewById(R.id.special_thanks);
            privacyPolicyButton = itemView.findViewById(R.id.privacy_policy_button);
            termsButton = itemView.findViewById(R.id.terms_conditions_button);
            versionCard = itemView.findViewById(R.id.version_card);

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

            if (privacyPolicyButton != null) {
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
            }

            if (termsButton != null) {
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

    private boolean isDarkMode() {
        int nightModeFlags = mContext.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        return nightModeFlags == Configuration.UI_MODE_NIGHT_YES;
    }

    private void setTextColorForOldAndroid(TextView textView) {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.R) {
            int color = isDarkMode() ? 
                mContext.getResources().getColor(android.R.color.white) :
                mContext.getResources().getColor(android.R.color.black);
            textView.setHighlightColor(Color.TRANSPARENT); // Remove highlight on click
            textView.setTextColor(color);
            // Also update drawable tint if there are any
            if (textView.getCompoundDrawables()[0] != null) {
                textView.getCompoundDrawables()[0].setTint(color);
            }
        }
    }
}

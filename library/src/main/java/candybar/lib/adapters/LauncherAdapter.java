package candybar.lib.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions;
import com.danimahardhika.android.helpers.core.utils.LogUtil;

import java.util.List;

import candybar.lib.R;
import candybar.lib.activities.CandyBarMainActivity;
import candybar.lib.applications.CandyBarApplication;
import candybar.lib.helpers.LauncherHelper;
import candybar.lib.items.Icon;
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

public class LauncherAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context mContext;
    private final List<Icon> mLaunchers;

    public static final int TYPE_HEADER = 0;
    public static final int TYPE_CONTENT = 1;
    public static final int TYPE_FOOTER = 2;

    public LauncherAdapter(@NonNull Context context, @NonNull List<Icon> launchers) {
        mContext = context;
        mLaunchers = launchers;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = null;
        if (viewType == TYPE_HEADER) {
            view = LayoutInflater.from(mContext).inflate(
                    R.layout.fragment_apply_item_header, parent, false);
        } else if (viewType == TYPE_CONTENT) {
            view = LayoutInflater.from(mContext).inflate(
                    R.layout.fragment_apply_item_grid, parent, false);
        } else if (viewType == TYPE_FOOTER) {
            view = LayoutInflater.from(mContext).inflate(
                    R.layout.fragment_apply_item_footer, parent, false);

            return new FooterViewHolder(view);
        }
        return new ViewHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == TYPE_HEADER) {
            ((ViewHolder) holder).name.setText(mLaunchers.get(position).getTitle());
        } else if (holder.getItemViewType() == TYPE_CONTENT) {
            bindLauncher((ViewHolder) holder, position);
        }
    }

    private void bindLauncher(ViewHolder holder, int position) {
        Icon launcher = mLaunchers.get(position);
        holder.name.setText(launcher.getTitle());

        // Load launcher icon
        Glide.with(mContext)
            .load("drawable://" + launcher.getRes())
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            .into(holder.icon);
    }

    @Override
    public int getItemCount() {
        return mLaunchers.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == getFirstHeaderPosition() || position == getLastHeaderPosition() || position == getMiddleHeaderPosition()) {
            return TYPE_HEADER;
        }
        if (position == getItemCount() - 1) return TYPE_FOOTER;
        return TYPE_CONTENT;
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView name;
        private ImageView icon;

        ViewHolder(View itemView, int viewType) {
            super(itemView);
            if (viewType == TYPE_HEADER) {
                name = itemView.findViewById(R.id.name);
            } else if (viewType == TYPE_CONTENT) {
                icon = itemView.findViewById(R.id.icon);
                name = itemView.findViewById(R.id.name);
                LinearLayout container = itemView.findViewById(R.id.container);
                container.setOnClickListener(this);
            }
        }

        private boolean isPackageInstalled(Context context, String packageName) {
            try {
                PackageInfo packageInfo = context.getPackageManager().getPackageInfo(
                        packageName, PackageManager.GET_ACTIVITIES);
                return packageInfo != null;
            } catch (Exception e) {
                return false;
            }
        }

        @SuppressLint("StringFormatInvalid")
        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            if (position < 0 || position > mLaunchers.size()) return;

            Icon launcher = mLaunchers.get(position);
            if (launcher.getPackageName() == null) return;

            if (!isPackageInstalled(mContext, launcher.getPackageName())) {
                if (mContext instanceof CandyBarMainActivity) {
                    ((CandyBarMainActivity) mContext).showLauncherNotInstalledDialog(launcher.getTitle());
                }
                return;
            }

            try {
                LauncherHelper.apply(mContext, launcher.getPackageName(), launcher.getTitle());
            } catch (Exception e) {
                LogUtil.e(Log.getStackTraceString(e));
            }
        }
    }

    class FooterViewHolder extends RecyclerView.ViewHolder {

        FooterViewHolder(View itemView) {
            super(itemView);
            if (!Preferences.get(mContext).isCardShadowEnabled()) {
                View shadow = itemView.findViewById(R.id.shadow);
                shadow.setVisibility(View.GONE);
            }
        }
    }

    public int getFirstHeaderPosition() {
        return mLaunchers.indexOf(new Icon(
                mContext.getResources().getString(R.string.apply_installed), -1, null));
    }

    public int getMiddleHeaderPosition() {
        return mLaunchers.indexOf(new Icon(
                mContext.getResources().getString(R.string.apply_installed_launchers), -3, null));
    }

    public int getLastHeaderPosition() {
        return mLaunchers.indexOf(new Icon(
                mContext.getResources().getString(R.string.apply_supported), -2, null));
    }
}

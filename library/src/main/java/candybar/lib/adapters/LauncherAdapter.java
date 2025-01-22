package candybar.lib.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.shape.CornerFamily;
import com.google.android.material.shape.ShapeAppearanceModel;

import java.util.HashMap;
import java.util.List;

import candybar.lib.R;
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
    private int mColumnCount;

    public static final int TYPE_CONTENT = 1;

    public LauncherAdapter(@NonNull Context context, @NonNull List<Icon> launchers) {
        mContext = context;
        mLaunchers = launchers;
        mColumnCount = context.getResources().getInteger(R.integer.apply_column_count);
    }

    public List<Icon> getLaunchers() {
        return mLaunchers;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Use different layout for older Android versions
        int layoutRes = Build.VERSION.SDK_INT >= Build.VERSION_CODES.R ? 
                R.layout.fragment_apply_item_grid : 
                R.layout.fragment_apply_item_grid_legacy;
        View view = LayoutInflater.from(mContext).inflate(layoutRes, parent, false);
        return new ViewHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Icon launcher = mLaunchers.get(position);
        ViewHolder contentViewHolder = (ViewHolder) holder;
        contentViewHolder.name.setText(launcher.getTitle());

        // Make icon container background transparent for Android 11 and below in dark theme
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.R) {
            boolean isDarkTheme = (mContext.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK)
                    == Configuration.UI_MODE_NIGHT_YES;
            if (isDarkTheme) {
                contentViewHolder.icon.setBackgroundColor(Color.TRANSPARENT);
            }
        }

        // Hide bottom line for last row
        int lastRowFirstPosition = mLaunchers.size() - (mLaunchers.size() % mColumnCount == 0 ? mColumnCount : mLaunchers.size() % mColumnCount);
        View bottomLine = contentViewHolder.itemView.findViewById(R.id.bottom_line);
        if (position >= lastRowFirstPosition) {
            bottomLine.setVisibility(View.GONE);
        } else {
            bottomLine.setVisibility(View.VISIBLE);
        }

        // Apply shape appearance programmatically for newer Android versions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && contentViewHolder.icon instanceof ShapeableImageView) {
            try {
                ShapeableImageView shapeableIcon = (ShapeableImageView) contentViewHolder.icon;
                float cornerSize = 32f * mContext.getResources().getDisplayMetrics().density;
                ShapeAppearanceModel shapeAppearanceModel = ShapeAppearanceModel.builder()
                        .setAllCorners(CornerFamily.ROUNDED, cornerSize)
                        .build();
                shapeableIcon.setShapeAppearanceModel(shapeAppearanceModel);
            } catch (Exception ignored) {
                // Fallback if shapeAppearance fails
            }
        }

        try {
            PackageManager pm = mContext.getPackageManager();
            pm.getPackageInfo(launcher.getPackageName(), PackageManager.GET_ACTIVITIES);
            // Package is installed - show in color
            Glide.with(mContext)
                    .load("drawable://" + launcher.getRes())
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(contentViewHolder.icon);
        } catch (Exception e) {
            // Package not installed - show in grayscale
            Glide.with(mContext)
                    .asBitmap()
                    .load("drawable://" + launcher.getRes())
                    .transform(new CandyBarGlideModule.GrayscaleTransformation())
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(contentViewHolder.icon);
        }
    }

    @Override
    public int getItemCount() {
        return mLaunchers.size();
    }

    @Override
    public int getItemViewType(int position) {
        return TYPE_CONTENT;
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView name;
        private ImageView icon;

        ViewHolder(View itemView, int viewType) {
            super(itemView);
            icon = itemView.findViewById(R.id.icon);
            name = itemView.findViewById(R.id.name);
            LinearLayout container = itemView.findViewById(R.id.container);
            container.setOnClickListener(this);
        }

        @SuppressLint("StringFormatInvalid")
        @Override
        public void onClick(View view) {
            int id = view.getId();
            int position = getBindingAdapterPosition();
            if (id == R.id.container) {
                if (position < 0 || position > getItemCount()) return;
                try {
                    LauncherHelper.apply(mContext,
                            mLaunchers.get(position).getPackageName(),
                            mLaunchers.get(position).getTitle());
                } catch (Exception e) {
                    Toast.makeText(mContext, mContext.getResources().getString(
                                    R.string.apply_launch_failed, mLaunchers.get(position).getTitle()),
                            Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}

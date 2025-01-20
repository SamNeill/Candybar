package candybar.lib.adapters.dialog;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.core.widget.CompoundButtonCompat;

import java.util.ArrayList;
import java.util.List;

import candybar.lib.R;
import candybar.lib.fragments.dialog.IconShapeChooserFragment;
import candybar.lib.items.IconShape;
import candybar.lib.helpers.ColorHelper;

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

public class IconShapeAdapter extends BaseAdapter {
    private final Context mContext;
    private final List<IconShape> mShapes;
    private final int mSelectedIndex;
    private final List<ViewHolder> mHolders;

    public IconShapeAdapter(@NonNull Context context, @NonNull List<IconShape> shapes, int selectedIndex) {
        mContext = context;
        mShapes = shapes;
        mSelectedIndex = selectedIndex;
        mHolders = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return mShapes.size();
    }

    @Override
    public IconShape getItem(int position) {
        return mShapes.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        IconShapeAdapter.ViewHolder holder;

        if (view == null) {
            view = View.inflate(mContext, R.layout.fragment_inapp_dialog_item_list, null);
            holder = new ViewHolder(view);
            view.setTag(holder);
            mHolders.add(holder);
        } else {
            holder = (IconShapeAdapter.ViewHolder) view.getTag();
        }

        // Set accent color for radio button
        int accentColor = ColorHelper.getAttributeColor(mContext, R.attr.cb_colorAccent);
        int uncheckedColor = Color.argb(
            (int) (255 * 0.7f),
            Color.red(accentColor),
            Color.green(accentColor),
            Color.blue(accentColor)
        );
        ColorStateList colorStateList = new ColorStateList(
                new int[][]{
                        new int[]{-android.R.attr.state_checked},
                        new int[]{android.R.attr.state_checked}
                },
                new int[]{
                        uncheckedColor,
                        accentColor
                }
        );
        CompoundButtonCompat.setButtonTintList(holder.radio, colorStateList);

        holder.radio.setChecked(mSelectedIndex == position);
        holder.name.setText(mShapes.get(position).getName());

        holder.container.setOnClickListener(v -> {
            for (ViewHolder aHolder : mHolders) {
                if (aHolder != holder) aHolder.radio.setChecked(false);
            }
            holder.radio.setChecked(true);

            FragmentManager fm = ((AppCompatActivity) mContext).getSupportFragmentManager();
            if (fm == null) return;

            Fragment fragment = fm.findFragmentByTag(IconShapeChooserFragment.TAG);
            if (fragment == null) return;

            if (fragment instanceof IconShapeChooserFragment) {
                ((IconShapeChooserFragment) fragment).setShape(mShapes.get(position).getShape());
            }
        });

        return view;
    }

    private static class ViewHolder {
        private final RadioButton radio;
        private final TextView name;
        private final LinearLayout container;

        ViewHolder(View view) {
            radio = view.findViewById(R.id.radio);
            name = view.findViewById(R.id.name);
            container = view.findViewById(R.id.container);
        }
    }
}

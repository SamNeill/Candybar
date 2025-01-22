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
import androidx.core.widget.CompoundButtonCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import java.util.List;

import candybar.lib.R;
import candybar.lib.fragments.dialog.LanguagesFragment;
import candybar.lib.helpers.ColorHelper;
import candybar.lib.items.Language;

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

public class LanguagesAdapter extends BaseAdapter {

    private final Context mContext;
    private final List<Language> mLanguages;
    private final int mSelectedIndex;

    public LanguagesAdapter(@NonNull Context context, @NonNull List<Language> languages, int selectedIndex) {
        mContext = context;
        mLanguages = languages;
        mSelectedIndex = selectedIndex;
    }

    @Override
    public int getCount() {
        return mLanguages.size();
    }

    @Override
    public Language getItem(int position) {
        return mLanguages.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if (view == null) {
            view = View.inflate(mContext, R.layout.fragment_inapp_dialog_item_list, null);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        holder.radio.setChecked(mSelectedIndex == position);
        holder.name.setText(mLanguages.get(position).getName());

        // Fix text color for dark theme
        if (android.os.Build.VERSION.SDK_INT <= android.os.Build.VERSION_CODES.R) {
            boolean isDarkMode = (mContext.getResources().getConfiguration().uiMode 
                & android.content.res.Configuration.UI_MODE_NIGHT_MASK) 
                == android.content.res.Configuration.UI_MODE_NIGHT_YES;
            holder.name.setTextColor(isDarkMode ? android.graphics.Color.WHITE : android.graphics.Color.BLACK);
        }

        // Set radio button colors programmatically
        int accentColor = ColorHelper.getAttributeColor(mContext, R.attr.cb_colorAccent);
        int uncheckedColor = Color.argb(128, // 50% opacity
                Color.red(accentColor),
                Color.green(accentColor),
                Color.blue(accentColor));
        
        ColorStateList colorStateList = new ColorStateList(
            new int[][] {
                new int[] { android.R.attr.state_checked },
                new int[] { -android.R.attr.state_checked }
            },
            new int[] {
                accentColor,  // Checked state
                uncheckedColor   // Unchecked state
            }
        );
        CompoundButtonCompat.setButtonTintList(holder.radio, colorStateList);

        holder.container.setOnClickListener(v -> {
            FragmentManager fm = ((AppCompatActivity) mContext).getSupportFragmentManager();
            if (fm == null) return;

            Fragment fragment = fm.findFragmentByTag(LanguagesFragment.TAG);
            if (fragment == null) return;

            if (fragment instanceof LanguagesFragment) {
                ((LanguagesFragment) fragment).setLanguage(mLanguages.get(position).getLocale());
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

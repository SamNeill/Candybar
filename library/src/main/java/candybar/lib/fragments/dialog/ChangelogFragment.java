package candybar.lib.fragments.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.fragment.app.FragmentActivity;

import com.afollestad.materialdialogs.MaterialDialog;

import candybar.lib.R;
import candybar.lib.adapters.dialog.ChangelogAdapter;
import candybar.lib.helpers.TypefaceHelper;
import candybar.lib.preferences.Preferences;
import candybar.lib.utils.listeners.HomeListener;
import candybar.lib.helpers.ColorHelper;
import candybar.lib.fragments.HomeFragment;

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

public class ChangelogFragment extends DialogFragment {

    private static final String TAG = "candybar.dialog.changelog";
    private Runnable onPositive;
    private boolean mIsNewVersion;
    private boolean mIntroShown = false;

    private static ChangelogFragment newInstance(Runnable onPositive) {
        return new ChangelogFragment(onPositive);
    }

    ChangelogFragment(Runnable onPositive) {
        super();
        this.onPositive = onPositive;
    }

    public static void showChangelog(FragmentManager fm, Runnable onPositive) {
        if (fm.getFragments().isEmpty()) return;
        Context context = fm.getFragments().get(0).requireContext();
        
        if (!Preferences.get(context).isPrivacyPolicyAccepted()) {
            return;
        }
        
        FragmentTransaction ft = fm.beginTransaction();
        Fragment prev = fm.findFragmentByTag(TAG);
        if (prev != null) {
            ft.remove(prev);
        }

        try {
            DialogFragment dialog = ChangelogFragment.newInstance(onPositive);
            dialog.show(ft, TAG);
        } catch (IllegalArgumentException | IllegalStateException ignored) {
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        MaterialDialog dialog = new MaterialDialog.Builder(requireActivity())
                .typeface(TypefaceHelper.getMedium(requireActivity()), TypefaceHelper.getRegular(requireActivity()))
                .customView(R.layout.fragment_changelog, false)
                .positiveText(R.string.close)
                .backgroundColorAttr(R.attr.cb_cardBackground)
                .positiveColor(ColorHelper.getAttributeColor(requireActivity(), R.attr.cb_colorAccent))
                .onPositive((d, which) -> {
                    if (this.onPositive != null) {
                        this.onPositive.run();
                    }
                })
                .build();

        ListView changelogList = (ListView) dialog.findViewById(R.id.changelog_list);
        TextView changelogDate = (TextView) dialog.findViewById(R.id.changelog_date);
        TextView changelogVersion = (TextView) dialog.findViewById(R.id.changelog_version);

        Activity activity = requireActivity();
        try {
            String version = activity.getPackageManager().getPackageInfo(
                    activity.getPackageName(), 0).versionName;
            if (version != null && version.length() > 0) {
                changelogVersion.setText(activity.getResources().getString(
                        R.string.changelog_version));
                changelogVersion.append(" " + version);
            }
        } catch (Exception ignored) {
        }

        String date = activity.getResources().getString(R.string.changelog_date);
        if (date.length() > 0) changelogDate.setText(date);
        else changelogDate.setVisibility(View.GONE);

        String[] changelog = activity.getResources().getStringArray(R.array.changelog);
        changelogList.setAdapter(new ChangelogAdapter(requireActivity(), changelog));

        return dialog;
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        Log.d("CandyBar", "Changelog dismissed, checking for intro");
        if (mIntroShown) {
            Log.d("CandyBar", "Intro already shown, skipping");
            super.onDismiss(dialog);
            return;
        }
        if (getActivity() != null) {
            Log.d("CandyBar", "Activity is available");
            FragmentManager fm = getActivity().getSupportFragmentManager();
            Log.d("CandyBar", "Fragment manager fragments count: " + fm.getFragments().size());
            for (Fragment f : fm.getFragments()) {
                Log.d("CandyBar", "Found fragment: " + f.getClass().getSimpleName() + 
                    " with tag: " + f.getTag());
            }
            Fragment fragment = getActivity().getSupportFragmentManager()
                    .findFragmentByTag(HomeFragment.TAG);
            Log.d("CandyBar", "Found home fragment: " + (fragment != null));
            if (fragment != null && fragment instanceof HomeListener) {
                Log.d("CandyBar", "Home fragment is HomeListener");
                if (Preferences.get(getActivity()).isTimeToShowHomeIntro()) {
                    Log.d("CandyBar", "Time to show intro is true, calling onHomeIntroInit");
                    ((HomeListener) fragment).onHomeIntroInit();
                    mIntroShown = true;
                } else {
                    Log.d("CandyBar", "Time to show intro is false");
                }
            } else {
                Log.d("CandyBar", "Home fragment is not HomeListener");
            }
        }
        super.onDismiss(dialog);
    }
}

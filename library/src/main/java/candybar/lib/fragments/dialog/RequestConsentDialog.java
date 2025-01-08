package candybar.lib.fragments.dialog;

import android.app.Dialog;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.afollestad.materialdialogs.MaterialDialog;

import candybar.lib.R;
import candybar.lib.activities.CandyBarMainActivity;
import candybar.lib.fragments.HomeFragment;
import candybar.lib.preferences.Preferences;
import candybar.lib.utils.listeners.RequestListener;
import candybar.lib.helpers.TypefaceHelper;
import candybar.lib.helpers.ColorHelper;

public class RequestConsentDialog extends DialogFragment {

    private static final String TAG = "request_consent_dialog";

    public static void show(FragmentActivity activity) {
        if (activity == null || activity.isFinishing()) {
            return;
        }

        // Don't show if already accepted
        if (Preferences.get(activity).isRequestConsentAccepted()) {
            return;
        }

        FragmentManager fm = activity.getSupportFragmentManager();
        Fragment fragment = fm.findFragmentByTag(TAG);
        if (fragment == null) {
            fragment = new RequestConsentDialog();
            fm.beginTransaction()
                    .add(fragment, TAG)
                    .commit();
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity())
                .inflate(R.layout.dialog_request_consent, null);

        View btnAccept = view.findViewById(R.id.btn_accept);
        View btnDeny = view.findViewById(R.id.btn_deny);

        btnAccept.setOnClickListener(v -> {
            Preferences.get(requireContext()).setRequestConsent(true);
            dismiss();
        });
        
        btnDeny.setOnClickListener(v -> {
            if (getActivity() instanceof CandyBarMainActivity) {
                // First dismiss the dialog
                dismiss();
                // Then trigger back press to return to home
                ((CandyBarMainActivity) getActivity()).onBackPressed();
            } else {
                dismiss();
            }
        });

        MaterialDialog dialog = new MaterialDialog.Builder(requireActivity())
                .typeface(TypefaceHelper.getMedium(requireActivity()), TypefaceHelper.getRegular(requireActivity()))
                .customView(view, false)
                .cancelable(false)
                .canceledOnTouchOutside(false)
                .backgroundColorAttr(R.attr.cb_cardBackground)
                .build();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            Window window = dialog.getWindow();
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                window.setLayout((int)(getResources().getDisplayMetrics().widthPixels * 0.5),
                               WindowManager.LayoutParams.WRAP_CONTENT);
            } else {
                window.setLayout((int)(getResources().getDisplayMetrics().widthPixels * 0.85),
                               WindowManager.LayoutParams.WRAP_CONTENT);
            }
            
            // Center the dialog
            window.setGravity(android.view.Gravity.CENTER);
        }

        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
        }
    }
}

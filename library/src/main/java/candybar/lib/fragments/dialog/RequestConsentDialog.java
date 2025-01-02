package candybar.lib.fragments.dialog;

import android.app.Dialog;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import candybar.lib.R;
import candybar.lib.activities.CandyBarMainActivity;
import candybar.lib.fragments.HomeFragment;
import candybar.lib.preferences.Preferences;
import candybar.lib.utils.listeners.RequestListener;

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

        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setView(view);

        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(
                    new ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
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

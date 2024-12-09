package candybar.lib.fragments.dialog;

import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import candybar.lib.R;
import candybar.lib.fragments.HomeFragment;
import candybar.lib.helpers.TypefaceHelper;
import candybar.lib.preferences.Preferences;

public class RequestConsentDialog extends DialogFragment {

    private static final String TAG = "request_consent_dialog";

    public static void show(FragmentActivity activity) {
        if (activity == null || activity.isFinishing()) {
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

        TextView title = view.findViewById(R.id.title);
        TextView content = view.findViewById(R.id.content);
        View btnAccept = view.findViewById(R.id.btn_accept);
        View btnDeny = view.findViewById(R.id.btn_deny);

        title.setTypeface(TypefaceHelper.getMedium(requireActivity()));
        content.setTypeface(TypefaceHelper.getRegular(requireActivity()));

        btnAccept.setOnClickListener(v -> {
            Preferences.get(requireContext()).setRequestConsentAccepted(true);
            dismiss();
        });
        
        btnDeny.setOnClickListener(v -> {
            // Navigate to home fragment
            requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, new HomeFragment())
                .commit();
            dismiss();
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
}

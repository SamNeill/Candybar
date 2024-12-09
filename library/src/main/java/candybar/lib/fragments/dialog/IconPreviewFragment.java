package candybar.lib.fragments.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import candybar.lib.R;
import candybar.lib.items.Icon;

public class IconPreviewFragment extends DialogFragment {

    private static final String TAG = "candybar.dialog.icon.preview";
    private static final String ICON = "icon";

    private Icon mIcon;

    public static IconPreviewFragment newInstance(Icon icon) {
        IconPreviewFragment fragment = new IconPreviewFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(ICON, icon);
        fragment.setArguments(bundle);
        return fragment;
    }

    public static void showIconPreview(@NonNull FragmentManager fm, String title, int res, String drawableName) {
        Icon icon = new Icon(drawableName, "", res);
        icon.setTitle(title);
        IconPreviewFragment.newInstance(icon).show(fm, TAG);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        MaterialAlertDialogBuilder dialog = new MaterialAlertDialogBuilder(requireActivity());

        View view = View.inflate(requireActivity(), R.layout.fragment_icon_preview_dialog, null);
        ImageView icon = view.findViewById(R.id.icon);
        TextView name = view.findViewById(R.id.name);
        TextView closeButton = view.findViewById(R.id.close_button);

        if (savedInstanceState != null) {
            mIcon = savedInstanceState.getParcelable(ICON);
        }

        if (getArguments() != null) {
            mIcon = getArguments().getParcelable(ICON);
        }

        name.setText(mIcon.getTitle());
        name.setVisibility(View.VISIBLE);

        // Make icon square
        int size = getResources().getDimensionPixelSize(R.dimen.icon_preview_size);
        icon.getLayoutParams().width = size;
        icon.getLayoutParams().height = size;

        Glide.with(requireActivity())
                .load("drawable://" + mIcon.getRes())
                .transition(DrawableTransitionOptions.withCrossFade(300))
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(icon);

        // Add close button functionality
        closeButton.setOnClickListener(v -> dismiss());

        dialog.setView(view);
        return dialog.create();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putParcelable(ICON, mIcon);
        super.onSaveInstanceState(outState);
    }
}

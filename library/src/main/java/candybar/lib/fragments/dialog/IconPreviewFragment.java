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

        // Initial zoom animation for preview
        icon.setScaleX(0f);
        icon.setScaleY(0f);
        icon.setAlpha(0f);
        
        icon.animate()
                .scaleX(1f)
                .scaleY(1f)
                .alpha(1f)
                .setDuration(300)
                .setInterpolator(new android.view.animation.AccelerateDecelerateInterpolator())
                .start();

        // Setup random parameters for click animation
        java.util.Random random = new java.util.Random();
        float randomScale = 0.2f + random.nextFloat() * 0.8f;
        float randomRotation = random.nextFloat() * 720f - 360f;
        int randomDuration = 300 + random.nextInt(300);
        final int animationType = random.nextInt(6);
        
        // Random interpolator for click animation
        android.view.animation.Interpolator randomInterpolator;
        switch(random.nextInt(6)) {
            case 0:
                randomInterpolator = new android.view.animation.BounceInterpolator();
                break;
            case 1:
                randomInterpolator = new android.view.animation.AnticipateOvershootInterpolator();
                break;
            case 2:
                randomInterpolator = new android.view.animation.AccelerateDecelerateInterpolator();
                break;
            case 3:
                randomInterpolator = new android.view.animation.AnticipateInterpolator();
                break;
            case 4:
                randomInterpolator = new android.view.animation.OvershootInterpolator();
                break;
            default:
                randomInterpolator = new android.view.animation.DecelerateInterpolator();
                break;
        }

        // Add click animation
        icon.setOnClickListener(v -> {
            v.animate().cancel(); // Cancel any ongoing animation
            
            switch(animationType) {
                case 0: // Scale with rotation
                    v.animate()
                        .scaleX(randomScale)
                        .scaleY(randomScale)
                        .rotation(randomRotation)
                        .setDuration(randomDuration)
                        .setInterpolator(randomInterpolator)
                        .withEndAction(() -> {
                            v.animate()
                                .scaleX(1f)
                                .scaleY(1f)
                                .rotation(0f)
                                .setDuration(randomDuration)
                                .setInterpolator(randomInterpolator)
                                .start();
                        })
                        .start();
                    break;
                case 1: // Fade with scale
                    v.animate()
                        .alpha(0f)
                        .scaleX(randomScale)
                        .scaleY(randomScale)
                        .setDuration(randomDuration)
                        .setInterpolator(randomInterpolator)
                        .withEndAction(() -> {
                            v.animate()
                                .alpha(1f)
                                .scaleX(1f)
                                .scaleY(1f)
                                .setDuration(randomDuration)
                                .setInterpolator(randomInterpolator)
                                .start();
                        })
                        .start();
                    break;
                case 2: // Multi-rotation
                    v.animate()
                        .rotationX(randomRotation)
                        .rotationY(randomRotation)
                        .rotation(randomRotation)
                        .setDuration(randomDuration)
                        .setInterpolator(randomInterpolator)
                        .withEndAction(() -> {
                            v.animate()
                                .rotationX(0f)
                                .rotationY(0f)
                                .rotation(0f)
                                .setDuration(randomDuration)
                                .setInterpolator(randomInterpolator)
                                .start();
                        })
                        .start();
                    break;
                case 3: // Scale only
                    v.animate()
                        .scaleX(0f)
                        .scaleY(0f)
                        .setDuration(randomDuration)
                        .setInterpolator(randomInterpolator)
                        .withEndAction(() -> {
                            v.animate()
                                .scaleX(1f)
                                .scaleY(1f)
                                .setDuration(randomDuration)
                                .setInterpolator(randomInterpolator)
                                .start();
                        })
                        .start();
                    break;
                case 4: // Flip
                    v.animate()
                        .scaleX(0f)
                        .rotation(randomRotation)
                        .setDuration(randomDuration)
                        .setInterpolator(randomInterpolator)
                        .withEndAction(() -> {
                            v.animate()
                                .scaleX(1f)
                                .rotation(0f)
                                .setDuration(randomDuration)
                                .setInterpolator(randomInterpolator)
                                .start();
                        })
                        .start();
                    break;
                default: // Random transform
                    v.animate()
                        .scaleX(randomScale)
                        .scaleY(randomScale)
                        .rotation(randomRotation)
                        .alpha(0.5f)
                        .translationX(random.nextFloat() * 100f - 50f)
                        .translationY(random.nextFloat() * 100f - 50f)
                        .setDuration(randomDuration)
                        .setInterpolator(randomInterpolator)
                        .withEndAction(() -> {
                            v.animate()
                                .scaleX(1f)
                                .scaleY(1f)
                                .rotation(0f)
                                .alpha(1f)
                                .translationX(0f)
                                .translationY(0f)
                                .setDuration(randomDuration)
                                .setInterpolator(randomInterpolator)
                                .start();
                        })
                        .start();
                    break;
            }
        });

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

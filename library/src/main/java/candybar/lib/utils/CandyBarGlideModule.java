package candybar.lib.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.module.AppGlideModule;

import java.security.MessageDigest;

@GlideModule
public final class CandyBarGlideModule extends AppGlideModule {
    @Override
    public void registerComponents(@NonNull Context context, @NonNull Glide glide, Registry registry) {
        registry.prepend(String.class, Bitmap.class, new CommonModelLoaderFactory(context));
    }

    // Kindly provided by @farhan on GitHub
    // https://github.com/bumptech/glide/issues/1484#issuecomment-365625087
    public static boolean isValidContextForGlide(final Context context) {
        if (context == null) {
            return false;
        }
        if (context instanceof Activity) {
            final Activity activity = (Activity) context;
            return !activity.isDestroyed() && !activity.isFinishing();
        }
        return true;
    }

    public static class GrayscaleTransformation extends BitmapTransformation {
        private static final String ID = "candybar.lib.utils.GrayscaleTransformation";
        private static final byte[] ID_BYTES = ID.getBytes(CHARSET);

        @Override
        protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
            int width = toTransform.getWidth();
            int height = toTransform.getHeight();

            Bitmap.Config config = toTransform.getConfig() != null ? toTransform.getConfig() : Bitmap.Config.ARGB_8888;
            Bitmap bitmap = pool.get(width, height, config);

            Canvas canvas = new Canvas(bitmap);
            ColorMatrix saturation = new ColorMatrix();
            saturation.setSaturation(0f);
            Paint paint = new Paint();
            paint.setColorFilter(new ColorMatrixColorFilter(saturation));
            canvas.drawBitmap(toTransform, 0, 0, paint);

            return bitmap;
        }

        @Override
        public void updateDiskCacheKey(MessageDigest messageDigest) {
            messageDigest.update(ID_BYTES);
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof GrayscaleTransformation;
        }

        @Override
        public int hashCode() {
            return ID.hashCode();
        }
    }
}

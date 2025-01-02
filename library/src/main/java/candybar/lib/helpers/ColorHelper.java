package candybar.lib.helpers;

import android.content.Context;
import android.util.TypedValue;

import androidx.annotation.AttrRes;

public class ColorHelper {
    
    public static int getAttributeColor(Context context, @AttrRes int attr) {
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(attr, typedValue, true);
        return typedValue.data;
    }
} 
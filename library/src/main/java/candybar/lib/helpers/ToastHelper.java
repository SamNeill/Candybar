package candybar.lib.helpers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;

import candybar.lib.R;

public class ToastHelper {
    public static void show(Context context, String message) {
        show(context, message, Toast.LENGTH_SHORT);
    }

    public static void show(Context context, int messageResId) {
        show(context, context.getString(messageResId), Toast.LENGTH_SHORT);
    }

    public static void show(Context context, int messageResId, int duration) {
        show(context, context.getString(messageResId), duration);
    }

    public static void show(Context context, String message, int duration) {
        View layout = LayoutInflater.from(context).inflate(R.layout.toast_custom, null);
        CardView cardView = layout.findViewById(R.id.toast_card);
        TextView text = layout.findViewById(R.id.toast_text);
        text.setText(message);

        Toast toast = new Toast(context);
        toast.setDuration(duration);
        toast.setView(layout);
        toast.show();
    }
} 
package candybar.lib.helpers;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;

import candybar.lib.R;
import candybar.lib.applications.CandyBarApplication;
import candybar.lib.items.Theme;
import candybar.lib.preferences.Preferences;

public class ThemeHelper {
    public static Theme getDefaultTheme(Context context) {
        try {
            return Theme.valueOf(context.getResources().getString(R.string.default_theme).toUpperCase());
        } catch (Exception e) {
            return Theme.AUTO;
        }
    }

    public static boolean isDarkTheme(Context context) {
        boolean isThemingEnabled = CandyBarApplication.getConfiguration().isDashboardThemingEnabled();
        if (!isThemingEnabled) return getDefaultTheme(context) == Theme.DARK;

        Theme currentTheme = Preferences.get(context).getTheme();
        if (currentTheme == Theme.AUTO) {
            switch (context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) {
                case Configuration.UI_MODE_NIGHT_YES:
                    return true;
                case Configuration.UI_MODE_NIGHT_NO:
                    return false;
            }
        }

        return currentTheme == Theme.DARK;
    }

    public static boolean isPureBlackEnabled(Context context) {
        boolean isDark = isDarkTheme(context);
        return isDark && Preferences.get(context).isPureBlack();
    }

    public static int getThemeRes(Context context) {
        if (isDarkTheme(context)) {
            boolean isPureBlack = Preferences.get(context).isPureBlack();
            boolean isMaterialYou = Preferences.get(context).isMaterialYou() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S;

            if (isPureBlack) {
                if (isMaterialYou) {
                    return R.style.CandyBar_Theme_App_MaterialYou_PureBlack;
                }
                return R.style.CandyBar_Theme_App_PureBlack;
            }
            
            if (isMaterialYou) {
                return R.style.CandyBar_Theme_App_MaterialYou;
            }
            return R.style.CandyBar_Theme_App_DayNight;
        }
        return R.style.CandyBar_Theme_App_DayNight;
    }
}
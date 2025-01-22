package candybar.lib.fragments.dialog;

import android.app.Dialog;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.afollestad.materialdialogs.MaterialDialog;

import java.util.Locale;

import candybar.lib.R;
import candybar.lib.fragments.HomeFragment;
import candybar.lib.preferences.Preferences;
import candybar.lib.utils.listeners.HomeListener;
import candybar.lib.helpers.TypefaceHelper;
import candybar.lib.helpers.ColorHelper;

public class PrivacyPolicyDialog extends DialogFragment {

    private static final String TAG = "privacy_policy_dialog";

    public static void show(FragmentActivity activity) {
        show(activity, true);
    }

    public static void show(FragmentActivity activity, boolean checkPreferences) {
        if (activity == null || activity.isFinishing()) {
            return;
        }

        if (checkPreferences) {
            Preferences preferences = Preferences.get(activity);
            // Don't show dialog during configuration changes (like theme changes)
            if (activity.isChangingConfigurations()) {
                return;
            }
            
            // Only show dialog on first launch or when privacy is explicitly reset
            if (preferences.isPrivacyPolicyAccepted()) {
                return;
            }
        }

        FragmentManager fm = activity.getSupportFragmentManager();
        Fragment fragment = fm.findFragmentByTag(TAG);
        if (fragment == null) {
            fragment = new PrivacyPolicyDialog();
            fm.beginTransaction()
                    .add(fragment, TAG)
                    .commit();
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity())
                .inflate(R.layout.fragment_privacy_policy, null);

        WebView webView = view.findViewById(R.id.webview);
        View btnAccept = view.findViewById(R.id.btn_accept);
        View btnDeny = view.findViewById(R.id.btn_deny);

        // Get theme colors
        String textColorPrimaryHex;
        String textColorSecondaryHex;
        String accentColorHex;

        if (android.os.Build.VERSION.SDK_INT <= android.os.Build.VERSION_CODES.R) {
            // For Android 11 and below, manually set colors based on theme
            int nightMode = requireContext().getResources().getConfiguration().uiMode & 
                    Configuration.UI_MODE_NIGHT_MASK;
            boolean isDarkTheme = nightMode == Configuration.UI_MODE_NIGHT_YES;
            
            int textColorPrimary = isDarkTheme ? 
                    requireContext().getResources().getColor(android.R.color.white) :
                    requireContext().getResources().getColor(android.R.color.black);
            int textColorSecondary = isDarkTheme ? 
                    requireContext().getResources().getColor(android.R.color.white) :
                    requireContext().getResources().getColor(android.R.color.black);
                    
            // Get accent color from theme
            TypedValue typedValue = new TypedValue();
            requireContext().getTheme().resolveAttribute(R.attr.cb_colorAccent, typedValue, true);
            int accentColor = typedValue.data;
                    
            textColorPrimaryHex = String.format("#%06X", (0xFFFFFF & textColorPrimary));
            textColorSecondaryHex = String.format("#%06X", (0xFFFFFF & textColorSecondary));
            accentColorHex = String.format("#%06X", (0xFFFFFF & accentColor));
        } else {
            // For Android 12+, use theme attributes
            TypedValue typedValue = new TypedValue();
            requireContext().getTheme().resolveAttribute(android.R.attr.textColorPrimary, typedValue, true);
            int textColorPrimary = typedValue.data;

            requireContext().getTheme().resolveAttribute(android.R.attr.textColorSecondary, typedValue, true);
            int textColorSecondary = typedValue.data;

            requireContext().getTheme().resolveAttribute(R.attr.cb_colorAccent, typedValue, true);
            int accentColor = typedValue.data;

            textColorPrimaryHex = String.format("#%06X", (0xFFFFFF & textColorPrimary));
            textColorSecondaryHex = String.format("#%06X", (0xFFFFFF & textColorSecondary));
            accentColorHex = String.format("#%06X", (0xFFFFFF & accentColor));
        }

        // Configure WebView
        webView.setBackgroundColor(0x00000000); // Transparent background
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(false);
        webView.getSettings().setBuiltInZoomControls(false);
        webView.getSettings().setDisplayZoomControls(false);
        webView.setVerticalScrollBarEnabled(false);

        // Set text styles based on app theme
        String css = String.format(
            "body { " +
            "   color: %s !important; " +
            "   font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell, sans-serif !important; " +
            "   font-size: 14px !important; " +
            "   line-height: 1.5 !important; " +
            "} " +
            "h1, h2 { " +
            "   color: %s !important; " +
            "   font-size: 18px !important; " +
            "   font-weight: 500 !important; " +
            "   margin-top: 16px !important; " +
            "   margin-bottom: 8px !important; " +
            "} " +
            "h3, h4, h5, h6 { " +
            "   color: %s !important; " +
            "   font-size: 16px !important; " +
            "   font-weight: 500 !important; " +
            "   margin-top: 12px !important; " +
            "   margin-bottom: 8px !important; " +
            "} " +
            "p, li { " +
            "   color: %s !important; " +
            "   font-size: 14px !important; " +
            "   line-height: 1.5 !important; " +
            "   margin-bottom: 8px !important; " +
            "} " +
            "strong { " +
            "   color: %s !important; " +
            "   font-weight: 500 !important; " +
            "} " +
            "a { " +
            "   color: %s !important; " +
            "   text-decoration: none !important; " +
            "} " +
            "a[href^='mailto:'] { " +
            "   color: %s !important; " +
            "} ",
            textColorSecondaryHex, // body text
            textColorPrimaryHex,   // h1, h2
            textColorPrimaryHex,   // h3-h6
            textColorSecondaryHex, // p, li
            textColorPrimaryHex,   // strong
            textColorSecondaryHex, // regular links
            accentColorHex         // email links
        );

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                String js = String.format(
                    "document.documentElement.style.setProperty('--text-color', '%s');" +
                    "document.documentElement.style.setProperty('--accent-color', '%s');" +
                    "document.body.style.padding = '0px';" +
                    "document.body.style.margin = '0px';" +
                    "var style = document.createElement('style');" +
                    "style.type = 'text/css';" +
                    "style.innerHTML = '%s';" +
                    "document.head.appendChild(style);",
                    textColorSecondaryHex,
                    accentColorHex,
                    css.replace("'", "\\'")
                );
                webView.evaluateJavascript(js, null);
            }
        });

        // Get current locale
        Locale systemLocale;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            systemLocale = Resources.getSystem().getConfiguration().getLocales().get(0);
        } else {
            systemLocale = Resources.getSystem().getConfiguration().locale;
        }
        Log.d(TAG, "System locale: " + systemLocale.getLanguage() + "_" + systemLocale.getCountry());

        // Get app locale
        Locale appLocale;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            appLocale = requireContext().getResources().getConfiguration().getLocales().get(0);
        } else {
            appLocale = requireContext().getResources().getConfiguration().locale;
        }
        Log.d(TAG, "App locale: " + appLocale.getLanguage() + "_" + appLocale.getCountry());

        // Check if either system or app language is Urdu (Pakistan)
        boolean isSystemUrdu = systemLocale.getLanguage().equals("ur") && systemLocale.getCountry().equals("PK");
        boolean isAppUrdu = appLocale.getLanguage().equals("ur") && appLocale.getCountry().equals("PK");
        boolean isUrdu = isSystemUrdu || isAppUrdu;

        // Check if either system or app language is Chinese (Simplified)
        boolean isSystemChinese = systemLocale.getLanguage().equals("zh") && systemLocale.getCountry().equals("CN");
        boolean isAppChinese = appLocale.getLanguage().equals("zh") && appLocale.getCountry().equals("CN");
        boolean isChinese = isSystemChinese || isAppChinese;

        // Check if either system or app language is Arabic (Saudi Arabia)
        boolean isSystemArabic = systemLocale.getLanguage().equals("ar") && systemLocale.getCountry().equals("SA");
        boolean isAppArabic = appLocale.getLanguage().equals("ar") && appLocale.getCountry().equals("SA");
        boolean isArabic = isSystemArabic || isAppArabic;

        // Check if either system or app language is Afrikaans (South Africa)
        boolean isSystemAfrikaans = systemLocale.getLanguage().equals("af") && systemLocale.getCountry().equals("ZA");
        boolean isAppAfrikaans = appLocale.getLanguage().equals("af") && appLocale.getCountry().equals("ZA");
        boolean isAfrikaans = isSystemAfrikaans || isAppAfrikaans;

        // Check if either system or app language is Bulgarian (Bulgaria)
        boolean isSystemBulgarian = systemLocale.getLanguage().equals("bg") && systemLocale.getCountry().equals("BG");
        boolean isAppBulgarian = appLocale.getLanguage().equals("bg") && appLocale.getCountry().equals("BG");
        boolean isBulgarian = isSystemBulgarian || isAppBulgarian;

        // Check if either system or app language is Bengali (India)
        boolean isSystemBengali = systemLocale.getLanguage().equals("bn") && systemLocale.getCountry().equals("IN");
        boolean isAppBengali = appLocale.getLanguage().equals("bn") && appLocale.getCountry().equals("IN");
        boolean isBengali = isSystemBengali || isAppBengali;

        // Check if either system or app language is Catalan (Spain)
        boolean isSystemCatalan = systemLocale.getLanguage().equals("ca") && systemLocale.getCountry().equals("ES");
        boolean isAppCatalan = appLocale.getLanguage().equals("ca") && appLocale.getCountry().equals("ES");
        boolean isCatalan = isSystemCatalan || isAppCatalan;

        // Check if either system or app language is Czech (Czech Republic)
        boolean isSystemCzech = systemLocale.getLanguage().equals("cs") && systemLocale.getCountry().equals("CZ");
        boolean isAppCzech = appLocale.getLanguage().equals("cs") && appLocale.getCountry().equals("CZ");
        boolean isCzech = isSystemCzech || isAppCzech;

        // Check if either system or app language is Danish (Denmark)
        boolean isSystemDanish = systemLocale.getLanguage().equals("da") && systemLocale.getCountry().equals("DK");
        boolean isAppDanish = appLocale.getLanguage().equals("da") && appLocale.getCountry().equals("DK");
        boolean isDanish = isSystemDanish || isAppDanish;

        // Check if either system or app language is German (Germany)
        boolean isSystemGerman = systemLocale.getLanguage().equals("de") && systemLocale.getCountry().equals("DE");
        boolean isAppGerman = appLocale.getLanguage().equals("de") && appLocale.getCountry().equals("DE");
        boolean isGerman = isSystemGerman || isAppGerman;

        // Check if either system or app language is Greek (Greece)
        boolean isSystemGreek = systemLocale.getLanguage().equals("el") && systemLocale.getCountry().equals("GR");
        boolean isAppGreek = appLocale.getLanguage().equals("el") && appLocale.getCountry().equals("GR");
        boolean isGreek = isSystemGreek || isAppGreek;

        // Check if either system or app language is Spanish (Spain)
        boolean isSystemSpanish = systemLocale.getLanguage().equals("es") && systemLocale.getCountry().equals("ES");
        boolean isAppSpanish = appLocale.getLanguage().equals("es") && appLocale.getCountry().equals("ES");
        boolean isSpanish = isSystemSpanish || isAppSpanish;

        // Check if either system or app language is Finnish (Finland)
        boolean isSystemFinnish = systemLocale.getLanguage().equals("fi") && systemLocale.getCountry().equals("FI");
        boolean isAppFinnish = appLocale.getLanguage().equals("fi") && appLocale.getCountry().equals("FI");
        boolean isFinnish = isSystemFinnish || isAppFinnish;

        // Check if either system or app language is French (France)
        boolean isSystemFrench = systemLocale.getLanguage().equals("fr") && systemLocale.getCountry().equals("FR");
        boolean isAppFrench = appLocale.getLanguage().equals("fr") && appLocale.getCountry().equals("FR");
        boolean isFrench = isSystemFrench || isAppFrench;

        // Check if either system or app language is Hindi (India)
        boolean isSystemHindi = systemLocale.getLanguage().equals("hi") && systemLocale.getCountry().equals("IN");
        boolean isAppHindi = appLocale.getLanguage().equals("hi") && appLocale.getCountry().equals("IN");
        boolean isHindi = isSystemHindi || isAppHindi;

        // Check if either system or app language is Hungarian (Hungary)
        boolean isSystemHungarian = systemLocale.getLanguage().equals("hu") && systemLocale.getCountry().equals("HU");
        boolean isAppHungarian = appLocale.getLanguage().equals("hu") && appLocale.getCountry().equals("HU");
        boolean isHungarian = isSystemHungarian || isAppHungarian;

        // Check if either system or app language is Indonesian (Indonesia)
        boolean isSystemIndonesian = systemLocale.getLanguage().equals("in") && systemLocale.getCountry().equals("ID");
        boolean isAppIndonesian = appLocale.getLanguage().equals("in") && appLocale.getCountry().equals("ID");
        boolean isIndonesian = isSystemIndonesian || isAppIndonesian;

        // Check if either system or app language is Italian (Italy)
        boolean isSystemItalian = systemLocale.getLanguage().equals("it") && systemLocale.getCountry().equals("IT");
        boolean isAppItalian = appLocale.getLanguage().equals("it") && appLocale.getCountry().equals("IT");
        boolean isItalian = isSystemItalian || isAppItalian;

        // Check if either system or app language is Hebrew (Israel)
        boolean isSystemHebrew = systemLocale.getLanguage().equals("iw") && systemLocale.getCountry().equals("IL");
        boolean isAppHebrew = appLocale.getLanguage().equals("iw") && appLocale.getCountry().equals("IL");
        boolean isHebrew = isSystemHebrew || isAppHebrew;

        // Check if either system or app language is Japanese (Japan)
        boolean isSystemJapanese = systemLocale.getLanguage().equals("ja") && systemLocale.getCountry().equals("JP");
        boolean isAppJapanese = appLocale.getLanguage().equals("ja") && appLocale.getCountry().equals("JP");
        boolean isJapanese = isSystemJapanese || isAppJapanese;

        // Check if either system or app language is Korean (Korea)
        boolean isSystemKorean = systemLocale.getLanguage().equals("ko") && systemLocale.getCountry().equals("KR");
        boolean isAppKorean = appLocale.getLanguage().equals("ko") && appLocale.getCountry().equals("KR");
        boolean isKorean = isSystemKorean || isAppKorean;

        // Check if either system or app language is Nepali (Nepal)
        boolean isSystemNepali = systemLocale.getLanguage().equals("ne") && systemLocale.getCountry().equals("NP");
        boolean isAppNepali = appLocale.getLanguage().equals("ne") && appLocale.getCountry().equals("NP");
        boolean isNepali = isSystemNepali || isAppNepali;

        // Check if either system or app language is Romanian (Romania)
        boolean isSystemRomanian = systemLocale.getLanguage().equals("ro") && systemLocale.getCountry().equals("RO");
        boolean isAppRomanian = appLocale.getLanguage().equals("ro") && appLocale.getCountry().equals("RO");
        boolean isRomanian = isSystemRomanian || isAppRomanian;

        // Check if either system or app language is Russian (Russia)
        boolean isSystemRussian = systemLocale.getLanguage().equals("ru") && systemLocale.getCountry().equals("RU");
        boolean isAppRussian = appLocale.getLanguage().equals("ru") && appLocale.getCountry().equals("RU");
        boolean isRussian = isSystemRussian || isAppRussian;

        // Check if either system or app language is Serbian (Serbia)
        boolean isSystemSerbian = systemLocale.getLanguage().equals("sr") && systemLocale.getCountry().equals("SP");
        boolean isAppSerbian = appLocale.getLanguage().equals("sr") && appLocale.getCountry().equals("SP");
        boolean isSerbian = isSystemSerbian || isAppSerbian;

        // Check if either system or app language is Swedish (Sweden)
        boolean isSystemSwedish = systemLocale.getLanguage().equals("sv") && systemLocale.getCountry().equals("SE");
        boolean isAppSwedish = appLocale.getLanguage().equals("sv") && appLocale.getCountry().equals("SE");
        boolean isSwedish = isSystemSwedish || isAppSwedish;

        // Check if either system or app language is Tamil (India)
        boolean isSystemTamil = systemLocale.getLanguage().equals("ta") && systemLocale.getCountry().equals("IN");
        boolean isAppTamil = appLocale.getLanguage().equals("ta") && appLocale.getCountry().equals("IN");
        boolean isTamil = isSystemTamil || isAppTamil;

        // Check if either system or app language is Thai (Thailand)
        boolean isSystemThai = systemLocale.getLanguage().equals("th") && systemLocale.getCountry().equals("TH");
        boolean isAppThai = appLocale.getLanguage().equals("th") && appLocale.getCountry().equals("TH");
        boolean isThai = isSystemThai || isAppThai;

        // Check if either system or app language is Turkish (Turkey)
        boolean isSystemTurkish = systemLocale.getLanguage().equals("tr") && systemLocale.getCountry().equals("TR");
        boolean isAppTurkish = appLocale.getLanguage().equals("tr") && appLocale.getCountry().equals("TR");
        boolean isTurkish = isSystemTurkish || isAppTurkish;

        // Check if either system or app language is Ukrainian (Ukraine)
        boolean isSystemUkrainian = systemLocale.getLanguage().equals("uk") && systemLocale.getCountry().equals("UA");
        boolean isAppUkrainian = appLocale.getLanguage().equals("uk") && appLocale.getCountry().equals("UA");
        boolean isUkrainian = isSystemUkrainian || isAppUkrainian;

        // Check if either system or app language is Vietnamese (Vietnam)
        boolean isSystemVietnamese = systemLocale.getLanguage().equals("vi") && systemLocale.getCountry().equals("VN");
        boolean isAppVietnamese = appLocale.getLanguage().equals("vi") && appLocale.getCountry().equals("VN");
        boolean isVietnamese = isSystemVietnamese || isAppVietnamese;

        // Check if either system or app language is Chinese Traditional (Taiwan)
        boolean isSystemChineseTraditional = systemLocale.getLanguage().equals("zh") && systemLocale.getCountry().equals("TW");
        boolean isAppChineseTraditional = appLocale.getLanguage().equals("zh") && appLocale.getCountry().equals("TW");
        boolean isChineseTraditional = isSystemChineseTraditional || isAppChineseTraditional;

        // Check if either system or app language is Kazakh (Kazakhstan)
        boolean isSystemKazakh = systemLocale.getLanguage().equals("kk") && systemLocale.getCountry().equals("KZ");
        boolean isAppKazakh = appLocale.getLanguage().equals("kk") && appLocale.getCountry().equals("KZ");
        boolean isKazakh = isSystemKazakh || isAppKazakh;

        // Check if either system or app language is Tagalog (Philippines)
        boolean isSystemTagalog = systemLocale.getLanguage().equals("tl") && systemLocale.getCountry().equals("PH");
        boolean isAppTagalog = appLocale.getLanguage().equals("tl") && appLocale.getCountry().equals("PH");
        boolean isTagalog = isSystemTagalog || isAppTagalog;

        // Check if either system or app language is Slovak (Slovakia)
        boolean isSystemSlovak = systemLocale.getLanguage().equals("sk") && systemLocale.getCountry().equals("SK");
        boolean isAppSlovak = appLocale.getLanguage().equals("sk") && appLocale.getCountry().equals("SK");
        boolean isSlovak = isSystemSlovak || isAppSlovak;

        // Check if either system or app language is Dutch (Netherlands)
        boolean isSystemDutch = systemLocale.getLanguage().equals("nl") && systemLocale.getCountry().equals("NL");
        boolean isAppDutch = appLocale.getLanguage().equals("nl") && appLocale.getCountry().equals("NL");
        boolean isDutch = isSystemDutch || isAppDutch;

        // Check if either system or app language is Norwegian (Norway)
        boolean isSystemNorwegian = systemLocale.getLanguage().equals("no") && systemLocale.getCountry().equals("NO");
        boolean isAppNorwegian = appLocale.getLanguage().equals("no") && appLocale.getCountry().equals("NO");
        boolean isNorwegian = isSystemNorwegian || isAppNorwegian;

        // Check if either system or app language is Polish (Poland)
        boolean isSystemPolish = systemLocale.getLanguage().equals("pl") && systemLocale.getCountry().equals("PL");
        boolean isAppPolish = appLocale.getLanguage().equals("pl") && appLocale.getCountry().equals("PL");
        boolean isPolish = isSystemPolish || isAppPolish;

        // Check if either system or app language is Portuguese (Brazil)
        boolean isSystemPortugueseBR = systemLocale.getLanguage().equals("pt") && systemLocale.getCountry().equals("BR");
        boolean isAppPortugueseBR = appLocale.getLanguage().equals("pt") && appLocale.getCountry().equals("BR");
        boolean isPortugueseBR = isSystemPortugueseBR || isAppPortugueseBR;

        // Check if either system or app language is Portuguese (Portugal)
        boolean isSystemPortuguesePT = systemLocale.getLanguage().equals("pt") && systemLocale.getCountry().equals("PT");
        boolean isAppPortuguesePT = appLocale.getLanguage().equals("pt") && appLocale.getCountry().equals("PT");
        boolean isPortuguesePT = isSystemPortuguesePT || isAppPortuguesePT;

        Log.d(TAG, "Is System Urdu: " + isSystemUrdu);
        Log.d(TAG, "Is App Urdu: " + isAppUrdu);
        Log.d(TAG, "Using Urdu: " + isUrdu);
        Log.d(TAG, "Is System Chinese: " + isSystemChinese);
        Log.d(TAG, "Is App Chinese: " + isAppChinese);
        Log.d(TAG, "Using Chinese: " + isChinese);
        Log.d(TAG, "Is System Arabic: " + isSystemArabic);
        Log.d(TAG, "Is App Arabic: " + isAppArabic);
        Log.d(TAG, "Using Arabic: " + isArabic);
        Log.d(TAG, "Is System Afrikaans: " + isSystemAfrikaans);
        Log.d(TAG, "Is App Afrikaans: " + isAppAfrikaans);
        Log.d(TAG, "Using Afrikaans: " + isAfrikaans);
        Log.d(TAG, "Is System Bulgarian: " + isSystemBulgarian);
        Log.d(TAG, "Is App Bulgarian: " + isAppBulgarian);
        Log.d(TAG, "Using Bulgarian: " + isBulgarian);
        Log.d(TAG, "Is System Bengali: " + isSystemBengali);
        Log.d(TAG, "Is App Bengali: " + isAppBengali);
        Log.d(TAG, "Using Bengali: " + isBengali);
        Log.d(TAG, "Is System Catalan: " + isSystemCatalan);
        Log.d(TAG, "Is App Catalan: " + isAppCatalan);
        Log.d(TAG, "Using Catalan: " + isCatalan);
        Log.d(TAG, "Is System Czech: " + isSystemCzech);
        Log.d(TAG, "Is App Czech: " + isAppCzech);
        Log.d(TAG, "Using Czech: " + isCzech);
        Log.d(TAG, "Is System Danish: " + isSystemDanish);
        Log.d(TAG, "Is App Danish: " + isAppDanish);
        Log.d(TAG, "Using Danish: " + isDanish);
        Log.d(TAG, "Is System German: " + isSystemGerman);
        Log.d(TAG, "Is App German: " + isAppGerman);
        Log.d(TAG, "Using German: " + isGerman);
        Log.d(TAG, "Is System Greek: " + isSystemGreek);
        Log.d(TAG, "Is App Greek: " + isAppGreek);
        Log.d(TAG, "Using Greek: " + isGreek);
        Log.d(TAG, "Is System Spanish: " + isSystemSpanish);
        Log.d(TAG, "Is App Spanish: " + isAppSpanish);
        Log.d(TAG, "Using Spanish: " + isSpanish);
        Log.d(TAG, "Is System Finnish: " + isSystemFinnish);
        Log.d(TAG, "Is App Finnish: " + isAppFinnish);
        Log.d(TAG, "Using Finnish: " + isFinnish);
        Log.d(TAG, "Is System French: " + isSystemFrench);
        Log.d(TAG, "Is App French: " + isAppFrench);
        Log.d(TAG, "Using French: " + isFrench);
        Log.d(TAG, "Is System Hindi: " + isSystemHindi);
        Log.d(TAG, "Is App Hindi: " + isAppHindi);
        Log.d(TAG, "Using Hindi: " + isHindi);
        Log.d(TAG, "Is System Hungarian: " + isSystemHungarian);
        Log.d(TAG, "Is App Hungarian: " + isAppHungarian);
        Log.d(TAG, "Using Hungarian: " + isHungarian);
        Log.d(TAG, "Is System Indonesian: " + isSystemIndonesian);
        Log.d(TAG, "Is App Indonesian: " + isAppIndonesian);
        Log.d(TAG, "Using Indonesian: " + isIndonesian);
        Log.d(TAG, "Is System Italian: " + isSystemItalian);
        Log.d(TAG, "Is App Italian: " + isAppItalian);
        Log.d(TAG, "Using Italian: " + isItalian);
        Log.d(TAG, "Is System Hebrew: " + isSystemHebrew);
        Log.d(TAG, "Is App Hebrew: " + isAppHebrew);
        Log.d(TAG, "Using Hebrew: " + isHebrew);
        Log.d(TAG, "Is System Japanese: " + isSystemJapanese);
        Log.d(TAG, "Is App Japanese: " + isAppJapanese);
        Log.d(TAG, "Using Japanese: " + isJapanese);
        Log.d(TAG, "Is System Korean: " + isSystemKorean);
        Log.d(TAG, "Is App Korean: " + isAppKorean);
        Log.d(TAG, "Using Korean: " + isKorean);
        Log.d(TAG, "Is System Nepali: " + isSystemNepali);
        Log.d(TAG, "Is App Nepali: " + isAppNepali);
        Log.d(TAG, "Using Nepali: " + isNepali);
        Log.d(TAG, "Is System Romanian: " + isSystemRomanian);
        Log.d(TAG, "Is App Romanian: " + isAppRomanian);
        Log.d(TAG, "Using Romanian: " + isRomanian);
        Log.d(TAG, "Is System Russian: " + isSystemRussian);
        Log.d(TAG, "Is App Russian: " + isAppRussian);
        Log.d(TAG, "Using Russian: " + isRussian);
        Log.d(TAG, "Is System Serbian: " + isSystemSerbian);
        Log.d(TAG, "Is App Serbian: " + isAppSerbian);
        Log.d(TAG, "Using Serbian: " + isSerbian);
        Log.d(TAG, "Is System Swedish: " + isSystemSwedish);
        Log.d(TAG, "Is App Swedish: " + isAppSwedish);
        Log.d(TAG, "Using Swedish: " + isSwedish);
        Log.d(TAG, "Is System Tamil: " + isSystemTamil);
        Log.d(TAG, "Is App Tamil: " + isAppTamil);
        Log.d(TAG, "Using Tamil: " + isTamil);
        Log.d(TAG, "Is System Thai: " + isSystemThai);
        Log.d(TAG, "Is App Thai: " + isAppThai);
        Log.d(TAG, "Using Thai: " + isThai);
        Log.d(TAG, "Is System Turkish: " + isSystemTurkish);
        Log.d(TAG, "Is App Turkish: " + isAppTurkish);
        Log.d(TAG, "Using Turkish: " + isTurkish);
        Log.d(TAG, "Is System Ukrainian: " + isSystemUkrainian);
        Log.d(TAG, "Is App Ukrainian: " + isAppUkrainian);
        Log.d(TAG, "Using Ukrainian: " + isUkrainian);
        Log.d(TAG, "Is System Vietnamese: " + isSystemVietnamese);
        Log.d(TAG, "Is App Vietnamese: " + isAppVietnamese);
        Log.d(TAG, "Using Vietnamese: " + isVietnamese);
        Log.d(TAG, "Is System Chinese Traditional: " + isSystemChineseTraditional);
        Log.d(TAG, "Is App Chinese Traditional: " + isAppChineseTraditional);
        Log.d(TAG, "Using Chinese Traditional: " + isChineseTraditional);
        Log.d(TAG, "Is System Kazakh: " + isSystemKazakh);
        Log.d(TAG, "Is App Kazakh: " + isAppKazakh);
        Log.d(TAG, "Using Kazakh: " + isKazakh);
        Log.d(TAG, "Is System Tagalog: " + isSystemTagalog);
        Log.d(TAG, "Is App Tagalog: " + isAppTagalog);
        Log.d(TAG, "Using Tagalog: " + isTagalog);
        Log.d(TAG, "Is System Slovak: " + isSystemSlovak);
        Log.d(TAG, "Is App Slovak: " + isAppSlovak);
        Log.d(TAG, "Using Slovak: " + isSlovak);
        Log.d(TAG, "Is System Dutch: " + isSystemDutch);
        Log.d(TAG, "Is App Dutch: " + isAppDutch);
        Log.d(TAG, "Using Dutch: " + isDutch);
        Log.d(TAG, "Is System Norwegian: " + isSystemNorwegian);
        Log.d(TAG, "Is App Norwegian: " + isAppNorwegian);
        Log.d(TAG, "Using Norwegian: " + isNorwegian);
        Log.d(TAG, "Is System Polish: " + isSystemPolish);
        Log.d(TAG, "Is App Polish: " + isAppPolish);
        Log.d(TAG, "Using Polish: " + isPolish);
        Log.d(TAG, "Is System Portuguese (Brazil): " + isSystemPortugueseBR);
        Log.d(TAG, "Is App Portuguese (Brazil): " + isAppPortugueseBR);
        Log.d(TAG, "Using Portuguese (Brazil): " + isPortugueseBR);
        Log.d(TAG, "Is System Portuguese (Portugal): " + isSystemPortuguesePT);
        Log.d(TAG, "Is App Portuguese (Portugal): " + isAppPortuguesePT);
        Log.d(TAG, "Using Portuguese (Portugal): " + isPortuguesePT);

        // Load the appropriate privacy policy HTML
        String htmlPath;
        if (isUrdu) {
            htmlPath = "file:///android_asset/privacy/privacy_policy_ur-rPK.html";
            Log.d(TAG, "Loading Urdu privacy policy: " + htmlPath);
        } else if (isChinese) {
            htmlPath = "file:///android_asset/privacy/privacy_policy_zh-rCN.html";
            Log.d(TAG, "Loading Chinese privacy policy: " + htmlPath);
        } else if (isArabic) {
            htmlPath = "file:///android_asset/privacy/privacy_policy_ar-rSA.html";
            Log.d(TAG, "Loading Arabic privacy policy: " + htmlPath);
        } else if (isAfrikaans) {
            htmlPath = "file:///android_asset/privacy/privacy_policy_af-rZA.html";
            Log.d(TAG, "Loading Afrikaans privacy policy: " + htmlPath);
        } else if (isBulgarian) {
            htmlPath = "file:///android_asset/privacy/privacy_policy_bg-rBG.html";
            Log.d(TAG, "Loading Bulgarian privacy policy: " + htmlPath);
        } else if (isBengali) {
            htmlPath = "file:///android_asset/privacy/privacy_policy_bn-rIN.html";
            Log.d(TAG, "Loading Bengali privacy policy: " + htmlPath);
        } else if (isCatalan) {
            htmlPath = "file:///android_asset/privacy/privacy_policy_ca-rES.html";
            Log.d(TAG, "Loading Catalan privacy policy: " + htmlPath);
        } else if (isCzech) {
            htmlPath = "file:///android_asset/privacy/privacy_policy_cs-rCZ.html";
            Log.d(TAG, "Loading Czech privacy policy: " + htmlPath);
        } else if (isDanish) {
            htmlPath = "file:///android_asset/privacy/privacy_policy_da-rDK.html";
            Log.d(TAG, "Loading Danish privacy policy: " + htmlPath);
        } else if (isGerman) {
            htmlPath = "file:///android_asset/privacy/privacy_policy_de-rDE.html";
            Log.d(TAG, "Loading German privacy policy: " + htmlPath);
        } else if (isGreek) {
            htmlPath = "file:///android_asset/privacy/privacy_policy_el-rGR.html";
            Log.d(TAG, "Loading Greek privacy policy: " + htmlPath);
        } else if (isSpanish) {
            htmlPath = "file:///android_asset/privacy/privacy_policy_es-rES.html";
            Log.d(TAG, "Loading Spanish privacy policy: " + htmlPath);
        } else if (isFinnish) {
            htmlPath = "file:///android_asset/privacy/privacy_policy_fi-rFI.html";
            Log.d(TAG, "Loading Finnish privacy policy: " + htmlPath);
        } else if (isFrench) {
            htmlPath = "file:///android_asset/privacy/privacy_policy_fr-rFR.html";
            Log.d(TAG, "Loading French privacy policy: " + htmlPath);
        } else if (isHindi) {
            htmlPath = "file:///android_asset/privacy/privacy_policy_hi-rIN.html";
            Log.d(TAG, "Loading Hindi privacy policy: " + htmlPath);
        } else if (isHungarian) {
            htmlPath = "file:///android_asset/privacy/privacy_policy_hu-rHU.html";
            Log.d(TAG, "Loading Hungarian privacy policy: " + htmlPath);
        } else if (isIndonesian) {
            htmlPath = "file:///android_asset/privacy/privacy_policy_in-rID.html";
            Log.d(TAG, "Loading Indonesian privacy policy: " + htmlPath);
        } else if (isItalian) {
            htmlPath = "file:///android_asset/privacy/privacy_policy_it-rIT.html";
            Log.d(TAG, "Loading Italian privacy policy: " + htmlPath);
        } else if (isHebrew) {
            htmlPath = "file:///android_asset/privacy/privacy_policy_iw-rIL.html";
            Log.d(TAG, "Loading Hebrew privacy policy: " + htmlPath);
        } else if (isJapanese) {
            htmlPath = "file:///android_asset/privacy/privacy_policy_ja-rJP.html";
            Log.d(TAG, "Loading Japanese privacy policy: " + htmlPath);
        } else if (isKorean) {
            htmlPath = "file:///android_asset/privacy/privacy_policy_ko-rKR.html";
            Log.d(TAG, "Loading Korean privacy policy: " + htmlPath);
        } else if (isNepali) {
            htmlPath = "file:///android_asset/privacy/privacy_policy_ne-rNP.html";
            Log.d(TAG, "Loading Nepali privacy policy: " + htmlPath);
        } else if (isRomanian) {
            htmlPath = "file:///android_asset/privacy/privacy_policy_ro-rRO.html";
            Log.d(TAG, "Loading Romanian privacy policy: " + htmlPath);
        } else if (isRussian) {
            htmlPath = "file:///android_asset/privacy/privacy_policy_ru-rRU.html";
            Log.d(TAG, "Loading Russian privacy policy: " + htmlPath);
        } else if (isSerbian) {
            htmlPath = "file:///android_asset/privacy/privacy_policy_sr-rSP.html";
            Log.d(TAG, "Loading Serbian privacy policy: " + htmlPath);
        } else if (isSwedish) {
            htmlPath = "file:///android_asset/privacy/privacy_policy_sv-rSE.html";
            Log.d(TAG, "Loading Swedish privacy policy: " + htmlPath);
        } else if (isTamil) {
            htmlPath = "file:///android_asset/privacy/privacy_policy_ta-rIN.html";
            Log.d(TAG, "Loading Tamil privacy policy: " + htmlPath);
        } else if (isThai) {
            htmlPath = "file:///android_asset/privacy/privacy_policy_th-rTH.html";
            Log.d(TAG, "Loading Thai privacy policy: " + htmlPath);
        } else if (isTurkish) {
            htmlPath = "file:///android_asset/privacy/privacy_policy_tr-rTR.html";
            Log.d(TAG, "Loading Turkish privacy policy: " + htmlPath);
        } else if (isUkrainian) {
            htmlPath = "file:///android_asset/privacy/privacy_policy_uk-rUA.html";
            Log.d(TAG, "Loading Ukrainian privacy policy: " + htmlPath);
        } else if (isVietnamese) {
            htmlPath = "file:///android_asset/privacy/privacy_policy_vi-rVN.html";
            Log.d(TAG, "Loading Vietnamese privacy policy: " + htmlPath);
        } else if (isChineseTraditional) {
            htmlPath = "file:///android_asset/privacy/privacy_policy_zh-rTW.html";
            Log.d(TAG, "Loading Chinese Traditional privacy policy: " + htmlPath);
        } else if (isKazakh) {
            htmlPath = "file:///android_asset/privacy/privacy_policy_kk-rKZ.html";
            Log.d(TAG, "Loading Kazakh privacy policy: " + htmlPath);
        } else if (isTagalog) {
            htmlPath = "file:///android_asset/privacy/privacy_policy_tl-rPH.html";
            Log.d(TAG, "Loading Tagalog privacy policy: " + htmlPath);
        } else if (isSlovak) {
            htmlPath = "file:///android_asset/privacy/privacy_policy_sk-rSK.html";
            Log.d(TAG, "Loading Slovak privacy policy: " + htmlPath);
        } else if (isDutch) {
            htmlPath = "file:///android_asset/privacy/privacy_policy_nl-rNL.html";
            Log.d(TAG, "Loading Dutch privacy policy: " + htmlPath);
        } else if (isNorwegian) {
            htmlPath = "file:///android_asset/privacy/privacy_policy_no-rNO.html";
            Log.d(TAG, "Loading Norwegian privacy policy: " + htmlPath);
        } else if (isPolish) {
            htmlPath = "file:///android_asset/privacy/privacy_policy_pl-rPL.html";
            Log.d(TAG, "Loading Polish privacy policy: " + htmlPath);
        } else if (isPortugueseBR) {
            htmlPath = "file:///android_asset/privacy/privacy_policy_pt-rBR.html";
            Log.d(TAG, "Loading Portuguese (Brazil) privacy policy: " + htmlPath);
        } else if (isPortuguesePT) {
            htmlPath = "file:///android_asset/privacy/privacy_policy_pt-rPT.html";
            Log.d(TAG, "Loading Portuguese (Portugal) privacy policy: " + htmlPath);
        } else {
            htmlPath = "file:///android_asset/privacy/privacy_policy.html";
            Log.d(TAG, "Loading English privacy policy: " + htmlPath);
        }
        webView.loadUrl(htmlPath);

        btnAccept.setOnClickListener(v -> {
            Preferences.get(requireContext()).setPrivacyPolicyAccepted(true);
            FragmentActivity activity = requireActivity();
            Log.d("CandyBar", "Privacy Policy accepted, showing changelog next");
            
            // Initialize home fragment first
            Fragment homeFragment = new HomeFragment();
            activity.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, homeFragment, HomeFragment.TAG)
                    .commitNow(); // Use commitNow to ensure fragment is added immediately
            
            // Now show changelog
            ChangelogFragment.showChangelog(activity.getSupportFragmentManager(), () -> {
                Log.d("CandyBar", "Changelog closed, attempting to show intro");
            });
            dismiss();
        });
        
        btnDeny.setOnClickListener(v -> {
            requireActivity().finishAffinity();
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
            
            // Set dialog size based on orientation
            Window window = dialog.getWindow();
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                window.setLayout((int)(getResources().getDisplayMetrics().widthPixels * 0.85),
                               (int)(getResources().getDisplayMetrics().heightPixels * 0.99));
            } else {
                window.setLayout((int)(getResources().getDisplayMetrics().widthPixels * 0.85),
                               WindowManager.LayoutParams.WRAP_CONTENT);
            }
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

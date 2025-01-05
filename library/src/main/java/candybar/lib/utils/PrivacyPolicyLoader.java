package candybar.lib.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

public class PrivacyPolicyLoader {
    private static final String TAG = "PrivacyPolicyLoader";
    private static final String APP_PRIVACY_PATH = "privacy/privacy_policy.html";
    private static final String LIB_PRIVACY_PATH = "candybar/privacy/privacy_policy.html";

    public static String loadPrivacyPolicy(Context context) {
        // Get system locale
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
            appLocale = context.getResources().getConfiguration().getLocales().get(0);
        } else {
            appLocale = context.getResources().getConfiguration().locale;
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

        // Try to load language-specific version first
        String privacyHtml = null;
        if (isUrdu) {
            Log.d(TAG, "Attempting to load Urdu privacy policy");
            privacyHtml = loadFromAppAssets(context, "privacy/privacy_policy_ur-rPK.html");
            if (privacyHtml == null || privacyHtml.isEmpty()) {
                Log.d(TAG, "Urdu privacy policy not found in app assets, trying library assets");
                privacyHtml = loadFromLibraryAssets(context, "privacy/privacy_policy_ur-rPK.html");
            }
        } else if (isChinese) {
            Log.d(TAG, "Attempting to load Chinese privacy policy");
            privacyHtml = loadFromAppAssets(context, "privacy/privacy_policy_zh-rCN.html");
            if (privacyHtml == null || privacyHtml.isEmpty()) {
                Log.d(TAG, "Chinese privacy policy not found in app assets, trying library assets");
                privacyHtml = loadFromLibraryAssets(context, "privacy/privacy_policy_zh-rCN.html");
            }
        } else if (isArabic) {
            Log.d(TAG, "Attempting to load Arabic privacy policy");
            privacyHtml = loadFromAppAssets(context, "privacy/privacy_policy_ar-rSA.html");
            if (privacyHtml == null || privacyHtml.isEmpty()) {
                Log.d(TAG, "Arabic privacy policy not found in app assets, trying library assets");
                privacyHtml = loadFromLibraryAssets(context, "privacy/privacy_policy_ar-rSA.html");
            }
        } else if (isAfrikaans) {
            Log.d(TAG, "Attempting to load Afrikaans privacy policy");
            privacyHtml = loadFromAppAssets(context, "privacy/privacy_policy_af-rZA.html");
            if (privacyHtml == null || privacyHtml.isEmpty()) {
                Log.d(TAG, "Afrikaans privacy policy not found in app assets, trying library assets");
                privacyHtml = loadFromLibraryAssets(context, "privacy/privacy_policy_af-rZA.html");
            }
        } else if (isBulgarian) {
            Log.d(TAG, "Attempting to load Bulgarian privacy policy");
            privacyHtml = loadFromAppAssets(context, "privacy/privacy_policy_bg-rBG.html");
            if (privacyHtml == null || privacyHtml.isEmpty()) {
                Log.d(TAG, "Bulgarian privacy policy not found in app assets, trying library assets");
                privacyHtml = loadFromLibraryAssets(context, "privacy/privacy_policy_bg-rBG.html");
            }
        } else if (isBengali) {
            Log.d(TAG, "Attempting to load Bengali privacy policy");
            privacyHtml = loadFromAppAssets(context, "privacy/privacy_policy_bn-rIN.html");
            if (privacyHtml == null || privacyHtml.isEmpty()) {
                Log.d(TAG, "Bengali privacy policy not found in app assets, trying library assets");
                privacyHtml = loadFromLibraryAssets(context, "privacy/privacy_policy_bn-rIN.html");
            }
        } else if (isCatalan) {
            Log.d(TAG, "Attempting to load Catalan privacy policy");
            privacyHtml = loadFromAppAssets(context, "privacy/privacy_policy_ca-rES.html");
            if (privacyHtml == null || privacyHtml.isEmpty()) {
                Log.d(TAG, "Catalan privacy policy not found in app assets, trying library assets");
                privacyHtml = loadFromLibraryAssets(context, "privacy/privacy_policy_ca-rES.html");
            }
        } else if (isCzech) {
            Log.d(TAG, "Attempting to load Czech privacy policy");
            privacyHtml = loadFromAppAssets(context, "privacy/privacy_policy_cs-rCZ.html");
            if (privacyHtml == null || privacyHtml.isEmpty()) {
                Log.d(TAG, "Czech privacy policy not found in app assets, trying library assets");
                privacyHtml = loadFromLibraryAssets(context, "privacy/privacy_policy_cs-rCZ.html");
            }
        } else if (isDanish) {
            Log.d(TAG, "Attempting to load Danish privacy policy");
            privacyHtml = loadFromAppAssets(context, "privacy/privacy_policy_da-rDK.html");
            if (privacyHtml == null || privacyHtml.isEmpty()) {
                Log.d(TAG, "Danish privacy policy not found in app assets, trying library assets");
                privacyHtml = loadFromLibraryAssets(context, "privacy/privacy_policy_da-rDK.html");
            }
        } else if (isGerman) {
            Log.d(TAG, "Attempting to load German privacy policy");
            privacyHtml = loadFromAppAssets(context, "privacy/privacy_policy_de-rDE.html");
            if (privacyHtml == null || privacyHtml.isEmpty()) {
                Log.d(TAG, "German privacy policy not found in app assets, trying library assets");
                privacyHtml = loadFromLibraryAssets(context, "privacy/privacy_policy_de-rDE.html");
            }
        } else if (isGreek) {
            Log.d(TAG, "Attempting to load Greek privacy policy");
            privacyHtml = loadFromAppAssets(context, "privacy/privacy_policy_el-rGR.html");
            if (privacyHtml == null || privacyHtml.isEmpty()) {
                Log.d(TAG, "Greek privacy policy not found in app assets, trying library assets");
                privacyHtml = loadFromLibraryAssets(context, "privacy/privacy_policy_el-rGR.html");
            }
        } else if (isSpanish) {
            Log.d(TAG, "Attempting to load Spanish privacy policy");
            privacyHtml = loadFromAppAssets(context, "privacy/privacy_policy_es-rES.html");
            if (privacyHtml == null || privacyHtml.isEmpty()) {
                Log.d(TAG, "Spanish privacy policy not found in app assets, trying library assets");
                privacyHtml = loadFromLibraryAssets(context, "privacy/privacy_policy_es-rES.html");
            }
        } else if (isFinnish) {
            Log.d(TAG, "Attempting to load Finnish privacy policy");
            privacyHtml = loadFromAppAssets(context, "privacy/privacy_policy_fi-rFI.html");
            if (privacyHtml == null || privacyHtml.isEmpty()) {
                Log.d(TAG, "Finnish privacy policy not found in app assets, trying library assets");
                privacyHtml = loadFromLibraryAssets(context, "privacy/privacy_policy_fi-rFI.html");
            }
        } else if (isFrench) {
            Log.d(TAG, "Attempting to load French privacy policy");
            privacyHtml = loadFromAppAssets(context, "privacy/privacy_policy_fr-rFR.html");
            if (privacyHtml == null || privacyHtml.isEmpty()) {
                Log.d(TAG, "French privacy policy not found in app assets, trying library assets");
                privacyHtml = loadFromLibraryAssets(context, "privacy/privacy_policy_fr-rFR.html");
            }
        } else if (isHindi) {
            Log.d(TAG, "Attempting to load Hindi privacy policy");
            privacyHtml = loadFromAppAssets(context, "privacy/privacy_policy_hi-rIN.html");
            if (privacyHtml == null || privacyHtml.isEmpty()) {
                Log.d(TAG, "Hindi privacy policy not found in app assets, trying library assets");
                privacyHtml = loadFromLibraryAssets(context, "privacy/privacy_policy_hi-rIN.html");
            }
        } else if (isHungarian) {
            Log.d(TAG, "Attempting to load Hungarian privacy policy");
            privacyHtml = loadFromAppAssets(context, "privacy/privacy_policy_hu-rHU.html");
            if (privacyHtml == null || privacyHtml.isEmpty()) {
                Log.d(TAG, "Hungarian privacy policy not found in app assets, trying library assets");
                privacyHtml = loadFromLibraryAssets(context, "privacy/privacy_policy_hu-rHU.html");
            }
        } else if (isIndonesian) {
            Log.d(TAG, "Attempting to load Indonesian privacy policy");
            privacyHtml = loadFromAppAssets(context, "privacy/privacy_policy_in-rID.html");
            if (privacyHtml == null || privacyHtml.isEmpty()) {
                Log.d(TAG, "Indonesian privacy policy not found in app assets, trying library assets");
                privacyHtml = loadFromLibraryAssets(context, "privacy/privacy_policy_in-rID.html");
            }
        } else if (isItalian) {
            Log.d(TAG, "Attempting to load Italian privacy policy");
            privacyHtml = loadFromAppAssets(context, "privacy/privacy_policy_it-rIT.html");
            if (privacyHtml == null || privacyHtml.isEmpty()) {
                Log.d(TAG, "Italian privacy policy not found in app assets, trying library assets");
                privacyHtml = loadFromLibraryAssets(context, "privacy/privacy_policy_it-rIT.html");
            }
        } else if (isHebrew) {
            Log.d(TAG, "Attempting to load Hebrew privacy policy");
            privacyHtml = loadFromAppAssets(context, "privacy/privacy_policy_iw-rIL.html");
            if (privacyHtml == null || privacyHtml.isEmpty()) {
                Log.d(TAG, "Hebrew privacy policy not found in app assets, trying library assets");
                privacyHtml = loadFromLibraryAssets(context, "privacy/privacy_policy_iw-rIL.html");
            }
        } else if (isJapanese) {
            Log.d(TAG, "Attempting to load Japanese privacy policy");
            privacyHtml = loadFromAppAssets(context, "privacy/privacy_policy_ja-rJP.html");
            if (privacyHtml == null || privacyHtml.isEmpty()) {
                Log.d(TAG, "Japanese privacy policy not found in app assets, trying library assets");
                privacyHtml = loadFromLibraryAssets(context, "privacy/privacy_policy_ja-rJP.html");
            }
        } else if (isKorean) {
            Log.d(TAG, "Attempting to load Korean privacy policy");
            privacyHtml = loadFromAppAssets(context, "privacy/privacy_policy_ko-rKR.html");
            if (privacyHtml == null || privacyHtml.isEmpty()) {
                Log.d(TAG, "Korean privacy policy not found in app assets, trying library assets");
                privacyHtml = loadFromLibraryAssets(context, "privacy/privacy_policy_ko-rKR.html");
            }
        } else if (isNepali) {
            Log.d(TAG, "Attempting to load Nepali privacy policy");
            privacyHtml = loadFromAppAssets(context, "privacy/privacy_policy_ne-rNP.html");
            if (privacyHtml == null || privacyHtml.isEmpty()) {
                Log.d(TAG, "Nepali privacy policy not found in app assets, trying library assets");
                privacyHtml = loadFromLibraryAssets(context, "privacy/privacy_policy_ne-rNP.html");
            }
        } else if (isRomanian) {
            Log.d(TAG, "Attempting to load Romanian privacy policy");
            privacyHtml = loadFromAppAssets(context, "privacy/privacy_policy_ro-rRO.html");
            if (privacyHtml == null || privacyHtml.isEmpty()) {
                Log.d(TAG, "Romanian privacy policy not found in app assets, trying library assets");
                privacyHtml = loadFromLibraryAssets(context, "privacy/privacy_policy_ro-rRO.html");
            }
        } else if (isRussian) {
            Log.d(TAG, "Attempting to load Russian privacy policy");
            privacyHtml = loadFromAppAssets(context, "privacy/privacy_policy_ru-rRU.html");
            if (privacyHtml == null || privacyHtml.isEmpty()) {
                Log.d(TAG, "Russian privacy policy not found in app assets, trying library assets");
                privacyHtml = loadFromLibraryAssets(context, "privacy/privacy_policy_ru-rRU.html");
            }
        } else if (isSerbian) {
            Log.d(TAG, "Attempting to load Serbian privacy policy");
            privacyHtml = loadFromAppAssets(context, "privacy/privacy_policy_sr-rSP.html");
            if (privacyHtml == null || privacyHtml.isEmpty()) {
                Log.d(TAG, "Serbian privacy policy not found in app assets, trying library assets");
                privacyHtml = loadFromLibraryAssets(context, "privacy/privacy_policy_sr-rSP.html");
            }
        } else if (isSwedish) {
            Log.d(TAG, "Attempting to load Swedish privacy policy");
            privacyHtml = loadFromAppAssets(context, "privacy/privacy_policy_sv-rSE.html");
            if (privacyHtml == null || privacyHtml.isEmpty()) {
                Log.d(TAG, "Swedish privacy policy not found in app assets, trying library assets");
                privacyHtml = loadFromLibraryAssets(context, "privacy/privacy_policy_sv-rSE.html");
            }
        } else if (isTamil) {
            Log.d(TAG, "Attempting to load Tamil privacy policy");
            privacyHtml = loadFromAppAssets(context, "privacy/privacy_policy_ta-rIN.html");
            if (privacyHtml == null || privacyHtml.isEmpty()) {
                Log.d(TAG, "Tamil privacy policy not found in app assets, trying library assets");
                privacyHtml = loadFromLibraryAssets(context, "privacy/privacy_policy_ta-rIN.html");
            }
        } else if (isThai) {
            Log.d(TAG, "Attempting to load Thai privacy policy");
            privacyHtml = loadFromAppAssets(context, "privacy/privacy_policy_th-rTH.html");
            if (privacyHtml == null || privacyHtml.isEmpty()) {
                Log.d(TAG, "Thai privacy policy not found in app assets, trying library assets");
                privacyHtml = loadFromLibraryAssets(context, "privacy/privacy_policy_th-rTH.html");
            }
        } else if (isTurkish) {
            Log.d(TAG, "Attempting to load Turkish privacy policy");
            privacyHtml = loadFromAppAssets(context, "privacy/privacy_policy_tr-rTR.html");
            if (privacyHtml == null || privacyHtml.isEmpty()) {
                Log.d(TAG, "Turkish privacy policy not found in app assets, trying library assets");
                privacyHtml = loadFromLibraryAssets(context, "privacy/privacy_policy_tr-rTR.html");
            }
        } else if (isUkrainian) {
            Log.d(TAG, "Attempting to load Ukrainian privacy policy");
            privacyHtml = loadFromAppAssets(context, "privacy/privacy_policy_uk-rUA.html");
            if (privacyHtml == null || privacyHtml.isEmpty()) {
                Log.d(TAG, "Ukrainian privacy policy not found in app assets, trying library assets");
                privacyHtml = loadFromLibraryAssets(context, "privacy/privacy_policy_uk-rUA.html");
            }
        } else if (isVietnamese) {
            Log.d(TAG, "Attempting to load Vietnamese privacy policy");
            privacyHtml = loadFromAppAssets(context, "privacy/privacy_policy_vi-rVN.html");
            if (privacyHtml == null || privacyHtml.isEmpty()) {
                Log.d(TAG, "Vietnamese privacy policy not found in app assets, trying library assets");
                privacyHtml = loadFromLibraryAssets(context, "privacy/privacy_policy_vi-rVN.html");
            }
        } else if (isChineseTraditional) {
            Log.d(TAG, "Attempting to load Chinese Traditional privacy policy");
            privacyHtml = loadFromAppAssets(context, "privacy/privacy_policy_zh-rTW.html");
            if (privacyHtml == null || privacyHtml.isEmpty()) {
                Log.d(TAG, "Chinese Traditional privacy policy not found in app assets, trying library assets");
                privacyHtml = loadFromLibraryAssets(context, "privacy/privacy_policy_zh-rTW.html");
            }
        } else if (isKazakh) {
            Log.d(TAG, "Attempting to load Kazakh privacy policy");
            privacyHtml = loadFromAppAssets(context, "privacy/privacy_policy_kk-rKZ.html");
            if (privacyHtml == null || privacyHtml.isEmpty()) {
                Log.d(TAG, "Kazakh privacy policy not found in app assets, trying library assets");
                privacyHtml = loadFromLibraryAssets(context, "privacy/privacy_policy_kk-rKZ.html");
            }
        } else if (isTagalog) {
            Log.d(TAG, "Attempting to load Tagalog privacy policy");
            privacyHtml = loadFromAppAssets(context, "privacy/privacy_policy_tl-rPH.html");
            if (privacyHtml == null || privacyHtml.isEmpty()) {
                Log.d(TAG, "Tagalog privacy policy not found in app assets, trying library assets");
                privacyHtml = loadFromLibraryAssets(context, "privacy/privacy_policy_tl-rPH.html");
            }
        } else if (isSlovak) {
            Log.d(TAG, "Attempting to load Slovak privacy policy");
            privacyHtml = loadFromAppAssets(context, "privacy/privacy_policy_sk-rSK.html");
            if (privacyHtml == null || privacyHtml.isEmpty()) {
                Log.d(TAG, "Slovak privacy policy not found in app assets, trying library assets");
                privacyHtml = loadFromLibraryAssets(context, "privacy/privacy_policy_sk-rSK.html");
            }
        } else if (isDutch) {
            Log.d(TAG, "Attempting to load Dutch privacy policy");
            privacyHtml = loadFromAppAssets(context, "privacy/privacy_policy_nl-rNL.html");
            if (privacyHtml == null || privacyHtml.isEmpty()) {
                Log.d(TAG, "Dutch privacy policy not found in app assets, trying library assets");
                privacyHtml = loadFromLibraryAssets(context, "privacy/privacy_policy_nl-rNL.html");
            }
        } else if (isNorwegian) {
            Log.d(TAG, "Attempting to load Norwegian privacy policy");
            privacyHtml = loadFromAppAssets(context, "privacy/privacy_policy_no-rNO.html");
            if (privacyHtml == null || privacyHtml.isEmpty()) {
                Log.d(TAG, "Norwegian privacy policy not found in app assets, trying library assets");
                privacyHtml = loadFromLibraryAssets(context, "privacy/privacy_policy_no-rNO.html");
            }
        } else if (isPolish) {
            Log.d(TAG, "Attempting to load Polish privacy policy");
            privacyHtml = loadFromAppAssets(context, "privacy/privacy_policy_pl-rPL.html");
            if (privacyHtml == null || privacyHtml.isEmpty()) {
                Log.d(TAG, "Polish privacy policy not found in app assets, trying library assets");
                privacyHtml = loadFromLibraryAssets(context, "privacy/privacy_policy_pl-rPL.html");
            }
        } else if (isPortugueseBR) {
            Log.d(TAG, "Attempting to load Portuguese (Brazil) privacy policy");
            privacyHtml = loadFromAppAssets(context, "privacy/privacy_policy_pt-rBR.html");
            if (privacyHtml == null || privacyHtml.isEmpty()) {
                Log.d(TAG, "Portuguese (Brazil) privacy policy not found in app assets, trying library assets");
                privacyHtml = loadFromLibraryAssets(context, "privacy/privacy_policy_pt-rBR.html");
            }
        } else if (isPortuguesePT) {
            Log.d(TAG, "Attempting to load Portuguese (Portugal) privacy policy");
            privacyHtml = loadFromAppAssets(context, "privacy/privacy_policy_pt-rPT.html");
            if (privacyHtml == null || privacyHtml.isEmpty()) {
                Log.d(TAG, "Portuguese (Portugal) privacy policy not found in app assets, trying library assets");
                privacyHtml = loadFromLibraryAssets(context, "privacy/privacy_policy_pt-rPT.html");
            }
        }

        // Fall back to default English version if needed
        if (privacyHtml == null || privacyHtml.isEmpty()) {
            Log.d(TAG, "Loading default English privacy policy");
            privacyHtml = loadFromAppAssets(context, APP_PRIVACY_PATH);
            if (privacyHtml == null || privacyHtml.isEmpty()) {
                privacyHtml = loadFromLibraryAssets(context, LIB_PRIVACY_PATH);
            }
        }

        return privacyHtml != null ? privacyHtml : "";
    }

    private static String loadFromAppAssets(Context context, String filename) {
        Log.d(TAG, "Trying to load from app assets: " + filename);
        return loadAssetFile(context, filename);
    }

    private static String loadFromLibraryAssets(Context context, String filename) {
        Log.d(TAG, "Trying to load from library assets: candybar/" + filename);
        return loadAssetFile(context, "candybar/" + filename);
    }

    private static String loadAssetFile(Context context, String filename) {
        AssetManager assetManager = context.getAssets();
        try (InputStream is = assetManager.open(filename);
             BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
            return content.toString();
        } catch (IOException e) {
            Log.d(TAG, "Error loading " + filename + ": " + e.getMessage());
            return null;
        }
    }
} 
package candybar.lib.activities;

import android.os.Bundle;
import android.webkit.WebView;
import androidx.appcompat.app.AppCompatActivity;

import candybar.lib.utils.PrivacyPolicyLoader;

public class PrivacyPolicyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        WebView webView = new WebView(this);
        setContentView(webView);

        String privacyHtml = PrivacyPolicyLoader.loadPrivacyPolicy(this);
        webView.loadDataWithBaseURL(null, privacyHtml, "text/html", "UTF-8", null);
    }
} 
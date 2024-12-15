package candybar.lib.utils;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class AmazonKeyUtils {
    private static final String TAG = "AmazonKeyUtils";

    public static String getAmazonPublicKey(Context context) {
        try {
            InputStream inputStream = context.getAssets().open("amazon_public_key.pem");
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder key = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.contains("-----BEGIN PUBLIC KEY-----") && !line.contains("-----END PUBLIC KEY-----")) {
                    key.append(line);
                }
            }
            reader.close();
            return key.toString();
        } catch (IOException e) {
            Log.e(TAG, "Error reading Amazon public key", e);
            return null;
        }
    }
}

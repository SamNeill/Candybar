package candybar.lib.services;

import android.content.Intent;
import android.net.Uri;

import com.google.android.apps.muzei.api.provider.MuzeiArtProvider;
import com.google.android.apps.muzei.api.provider.Artwork;

import java.io.InputStream;

public class CandyBarMuzeiService extends MuzeiArtProvider {

    @Override
    public void onLoadRequested(boolean initial) {
        // Handle loading artwork
    }

    @Override
    public InputStream openFile(Artwork artwork) {
        // Return input stream for artwork
        return null;
    }
}

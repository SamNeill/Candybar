package candybar.lib.utils;

/*
 * CandyBar - Material Dashboard
 *
 * Copyright (c) 2014-2016 Dani Mahardhika
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.content.Context;
import android.widget.Toast;

public abstract class Extras {

    public enum Tag {
        HOME(0),
        APPLY(1),
        ICONS(2),
        REQUEST(3),
        WALLPAPERS(4),
        PRESETS(5),
        SETTINGS(6),
        FAQS(7),
        ABOUT(8);

        public final int idx;

        Tag(int idx) {
            this.idx = idx;
        }

        public String value = name().toLowerCase();
    }

    public static final String EXTRA_POSITION = "position";
    public static final String EXTRA_SIZE = "size";
    public static final String EXTRA_URL = "url";
    public static final String EXTRA_IMAGE = "image";
    public static final String EXTRA_RESUMED = "resumed";

    public enum Error {
        APPFILTER_NULL,
        DATABASE_ERROR,
        INSTALLED_APPS_NULL,
        ICON_REQUEST_NULL,
        ICON_REQUEST_PROPERTY_NULL,
        ICON_REQUEST_PROPERTY_COMPONENT_NULL;

        public String getMessage() {
            switch (this) {
                case APPFILTER_NULL:
                    return "Error: Unable to read appfilter.xml";
                case DATABASE_ERROR:
                    return "Error: Unable to read database";
                case INSTALLED_APPS_NULL:
                    return "Error: Unable to collect installed apps";
                case ICON_REQUEST_NULL:
                    return "Error: Icon request is null";
                case ICON_REQUEST_PROPERTY_NULL:
                    return "Error: Icon request property is null";
                case ICON_REQUEST_PROPERTY_COMPONENT_NULL:
                    return "Error: Email client component is null";
                default:
                    return "Error: Unknown";
            }
        }

        public void showToast(Context context) {
            if (context == null) return;
            Toast.makeText(context, getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}

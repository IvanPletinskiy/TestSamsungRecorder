package com.sec.android.app.voicenote.provider;

import android.graphics.Typeface;
import java.util.Hashtable;

public class TypefaceProvider {
    private static final String ROBOTO_CONDENSED_BOLD = "roboto_condensed_bold";
    private static final String ROBOTO_CONDENSED_REGULAR = "roboto_condensed_regular";
    private static final String SAMSUNG_NEO_NUM_3L = "/system/fonts/SamsungNeoNum-3L.ttf";
    private static final String TAG = "TypefaceProvider";
    private static Hashtable<String, Typeface> fontCache = new Hashtable<>();

    public static Typeface getFromFile() {
        return getFromFile(SAMSUNG_NEO_NUM_3L);
    }

    public static Typeface getFromFile(String str) {
        Typeface typeface = fontCache.get(str);
        if (typeface == null) {
            Log.m26i(TAG, "create typeface from file - name : " + str);
            try {
                typeface = Typeface.createFromFile(str);
                fontCache.put(str, typeface);
            } catch (Exception unused) {
                return Typeface.DEFAULT;
            }
        }
        return typeface;
    }

    public static void clearAll() {
        fontCache.clear();
    }

    public static Typeface getRobotoCondensedBoldFont() {
        Typeface typeface = fontCache.get(ROBOTO_CONDENSED_BOLD);
        if (typeface != null) {
            return typeface;
        }
        try {
            Typeface create = Typeface.create("sec-roboto-condensed", 1);
            fontCache.put(ROBOTO_CONDENSED_BOLD, create);
            return create;
        } catch (Exception unused) {
            return Typeface.DEFAULT_BOLD;
        }
    }

    public static Typeface getRobotoCondensedRegularFont() {
        Typeface typeface = fontCache.get(ROBOTO_CONDENSED_REGULAR);
        if (typeface != null) {
            return typeface;
        }
        try {
            Typeface create = Typeface.create("sans-serif", 0);
            fontCache.put(ROBOTO_CONDENSED_REGULAR, create);
            return create;
        } catch (Exception unused) {
            return Typeface.DEFAULT;
        }
    }
}

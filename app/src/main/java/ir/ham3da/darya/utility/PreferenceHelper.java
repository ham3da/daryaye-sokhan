package ir.ham3da.darya.utility;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import java.util.Map;

public class PreferenceHelper
{
    private static final String PREF_NAME = "ir.ham3da.darya_preferences";

    private final SharedPreferences mPrefs;

    public PreferenceHelper(Context context) {
        mPrefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public Map<String, ?> getAllItems() {
        return mPrefs.getAll();
    }

    public String getKey(String key, String def) {
        return mPrefs.getString(key, def);
    }

    public Boolean getKey(String key, Boolean def) {
        return mPrefs.getBoolean(key, def);
    }

    public int getKey(String key, int def) {
        return mPrefs.getInt(key, def);
    }

    public float getKey(String key, float def) {
        return mPrefs.getFloat(key, def);
    }

    public void setKey(String key, String value) {
        mPrefs.edit().putString(key, value).apply();
    }

    public void setKey(String key, Boolean value) {
        mPrefs.edit().putBoolean(key, value).apply();
    }

    public void setKey(String key, int value) {
        mPrefs.edit().putInt(key, value).apply();
    }

    public void setKey(String key, float value) {
        mPrefs.edit().putFloat(key, value).apply();
    }


}
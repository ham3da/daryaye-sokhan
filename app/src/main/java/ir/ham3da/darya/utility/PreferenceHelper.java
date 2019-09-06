package ir.ham3da.darya.utility;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import java.util.Map;

public class PreferenceHelper
{
    private final SharedPreferences mPrefs;

    public PreferenceHelper(Context context) {
        mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public  Map<String, ?> getAllItems()
    {
        Map<String, ?> map = mPrefs.getAll();
        return map;
    }

    public String getKey(String key, String def) {
        String str = mPrefs.getString(key, def);
        return str;
    }

    public Boolean getKey(String key, Boolean def ) {
        Boolean res = mPrefs.getBoolean(key, def);
        return res;
    }

    public int getKey(String key, int def ) {
        int res = mPrefs.getInt(key, def);
        return res;
    }

    public float getKey(String key, float def ) {
        float res = mPrefs.getFloat(key, def);
        return res;
    }

    public void setKey(String key, String value)
    {
        SharedPreferences.Editor mEditor = mPrefs.edit();
        mEditor.putString(key, value);
        mEditor.apply();
        mEditor.commit();
    }

    public void setKey(String key, Boolean value)
    {
        SharedPreferences.Editor mEditor = mPrefs.edit();
        mEditor.putBoolean(key, value);
        mEditor.apply();
        mEditor.commit();
    }

    public void setKey(String key, int value)
    {
        SharedPreferences.Editor mEditor = mPrefs.edit();
        mEditor.putInt(key, value);
        mEditor.apply();
        mEditor.commit();
    }


    public void setKey(String key, float value)
    {
        SharedPreferences.Editor mEditor = mPrefs.edit();
        mEditor.putFloat(key, value);
        mEditor.apply();
        mEditor.commit();
    }

}
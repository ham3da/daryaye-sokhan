package ir.ham3da.darya;

import android.app.Application;
import android.content.Context;
import android.util.Log;


import androidx.appcompat.app.AppCompatDelegate;

import com.google.firebase.analytics.FirebaseAnalytics;

import ir.ham3da.darya.utility.AppSettings;
import ir.ham3da.darya.utility.SetLanguage;

public class App extends Application
{


    private boolean updatePoetList, updateFavList = false;
    private FirebaseAnalytics mFirebaseAnalytics;

    public boolean getUpdatePoetList()
    {
        return updatePoetList;
    }

    public void setUpdatePoetList(boolean status)
    {
        updatePoetList = status;
    }

    public boolean getUpdateFavList()
    {
        return updateFavList;
    }

    public void setUpdateFavList(boolean status)
    {
        updateFavList = status;
    }

    static {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
    }

    @Override
    public void onCreate() {
        //SetLanguage.wrap(this);
        super.onCreate();
        AppSettings.Init(this);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

    }
}
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


    private boolean updatePoetList, updateFavList, Adviewd = false, updateRandPoemNotify;
    private int loginMethod = 1;

    /**
     * get Login Method
     * @return int 1 => Login as Guest, 2 => login with google , 0 => not set
     */
    public int getLoginMethod()
    {
        return this.loginMethod;
    }

    /**
     * set Login Method
     * @param loginMethodParam int 1 => Login as Guest, 2 => login with google
     */
    public void setLoginMethod(int loginMethodParam)
    {
        this.loginMethod = loginMethodParam;
    }

    public boolean getUpdateRandPoemNotify()
    {
        return updateRandPoemNotify;
    }

    public void setUpdateRandPoemNotify(boolean updateRandPoemNotify)
    {
        this.updateRandPoemNotify = updateRandPoemNotify;
    }



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

    public void setAdviewd(boolean adviewd)
    {
        Adviewd = adviewd;
    }
    public boolean getAdviewd()
    {
        return  Adviewd;
    }

    static {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
    }

    @Override
    public void onCreate() {
        //SetLanguage.wrap(this);
        super.onCreate();
        AppSettings.Init(this);
        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

    }
}
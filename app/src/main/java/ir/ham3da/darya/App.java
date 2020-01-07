package ir.ham3da.darya;

import android.app.Application;
import android.content.Context;


import com.google.firebase.analytics.FirebaseAnalytics;

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



    @Override
    public void onCreate() {
       // SetLanguage.wrap(this);
        super.onCreate();
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

    }
}
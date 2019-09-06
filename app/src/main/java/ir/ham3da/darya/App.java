package ir.ham3da.darya;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;

import androidx.annotation.NonNull;

import java.util.Locale;

import ir.ham3da.darya.utility.LangSettingList;
import ir.ham3da.darya.utility.SetLanguage;

public class App extends Application {
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(SetLanguage.wrap(base));
    }
}
package ir.ham3da.darya.utility;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import java.util.Locale;

import android.content.ContextWrapper;
import android.util.DisplayMetrics;

public class SetLanguage extends ContextWrapper {

    public SetLanguage(Context base) {
        super(base);
    }

    public static ContextWrapper wrap(Context context) {

        AppSettings.Init(context);
        LangSettingList langSetting = AppSettings.getLangSettingList(context);
        String language = langSetting.getTag();
        String country = langSetting.getCountry();

        Locale locale = new Locale(language, country);
        Locale.setDefault(locale);

        Resources resources = context.getResources();

        Configuration config = context.getResources().getConfiguration();

        config.setLayoutDirection(locale);
        DisplayMetrics dm = resources.getDisplayMetrics();

        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.N){
            config.setLocale(locale);
           Context context2 = context.createConfigurationContext(config);
            return new SetLanguage(context2);
        }
        else
        {
          config.locale = new Locale(language, country);
          resources.updateConfiguration(config, dm);
            return new SetLanguage(context);
        }


    }

}
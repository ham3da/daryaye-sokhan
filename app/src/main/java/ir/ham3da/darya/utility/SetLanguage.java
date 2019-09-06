package ir.ham3da.darya.utility;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.util.DisplayMetrics;

import java.util.Locale;

public class SetLanguage {

    public static Context wrap(Context context) {
        try {
            AppSettings.Init(context);
            LangSettingList langSetting = AppSettings.getLangSettingList(context);

            Locale locale = new Locale(langSetting.getTag(), langSetting.getCountry());
            Locale.setDefault(locale);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                return updateResourcesLocale(context, locale);
            } else {
                return updateResourcesLocaleLegacy(context, locale);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return context;
        }

    }

    private static Context updateResourcesLocale(Context context, Locale locale) {
        Configuration configuration = context.getResources().getConfiguration();
        configuration.setLocale(locale);
        return context.createConfigurationContext(configuration);
    }

    @SuppressWarnings("deprecation")
    private static Context updateResourcesLocaleLegacy(Context context, Locale locale) {
        Resources res = context.getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration config = res.getConfiguration();
        config.locale = locale;
        config.setLayoutDirection(locale);
        res.updateConfiguration(config, dm);
        context.createConfigurationContext(config);
        return context;
    }

    @SuppressWarnings("deprecation")
    public Locale getCurrentLocale(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return context.getResources().getConfiguration().getLocales().get(0);
        } else {
            //noinspection deprecation

            return context.getResources().getConfiguration().locale;
        }
    }
}
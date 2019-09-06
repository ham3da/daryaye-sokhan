package ir.ham3da.darya;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import ir.ham3da.darya.adaptors.AdapterSocialList;
import ir.ham3da.darya.ganjoor.GanjoorDbBrowser;
import ir.ham3da.darya.ganjoor.GanjoorPoet;
import ir.ham3da.darya.utility.AppSettings;
import ir.ham3da.darya.utility.CustomProgress;
import ir.ham3da.darya.utility.LangSettingList;
import ir.ham3da.darya.utility.LinkItem;
import ir.ham3da.darya.utility.SetLanguage;

public class ActivitySettings extends AppCompatActivity {


    static Preference  pref_op_db;
//    static Preference  pref_rand_poem;
    int currentLocalIndex;
    GanjoorDbBrowser GanjoorDbBrowser1;

    public void setRandomPoemText() {

    }


    public static String getRandomPoetsName(GanjoorDbBrowser GanjoorDbBrowser1) {
        List<String> names = new ArrayList<>();
        String randomSelectedPoets = AppSettings.getRandomSelectedPoets();

        List<GanjoorPoet> poets = GanjoorDbBrowser1.getPoets(randomSelectedPoets);
        for (GanjoorPoet poet : poets) {
            names.add(poet._Name);
        }
        String names2 = TextUtils.join(" - ", names);
        return names2;
    }


    public static String getRandomPoetsNameFromCat(GanjoorDbBrowser GanjoorDbBrowser1) {
        List<String> names = new ArrayList<>();
        String randomSelectedCategories = AppSettings.getRandomSelectedCategories();

        List<GanjoorPoet> poets = GanjoorDbBrowser1.getPoetsFromCat(randomSelectedCategories);
        for (GanjoorPoet poet : poets) {
            names.add(poet._Name);
        }
        String names2 = TextUtils.join(" - ", names);
        return names2;
    }


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(SetLanguage.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.settings_activity);

        AppSettings.Init(this);
        LangSettingList langSetting = AppSettings.getLangSettingList(this);
        currentLocalIndex = langSetting.getId();
        GanjoorDbBrowser1 = new GanjoorDbBrowser(this);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment())
                .commit();


        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        setTitle(R.string.action_settings);


    }



    public static class SettingsFragment extends PreferenceFragmentCompat {


        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);

            GanjoorDbBrowser GanjoorDbBrowser1 = new GanjoorDbBrowser(getContext());

            EditTextPreference prefTextSize = (EditTextPreference) findPreference("TextSize");

            final float textSize = AppSettings.getTextSize();


            if (prefTextSize != null) {

                String textSizeStr = String.format(Locale.getDefault(), "%.0f", textSize);

                prefTextSize.setTitle(prefTextSize.getTitle() + " (" + textSizeStr + ")");

                prefTextSize.setOnBindEditTextListener(new EditTextPreference.OnBindEditTextListener() {
                    @Override
                    public void onBindEditText(@NonNull EditText editText) {
                        editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                        editText.setTextDirection(View.TEXT_DIRECTION_LTR);
                        editText.selectAll();
                    }
                });
            }

            final ActivitySettings activitySettings = (ActivitySettings) getActivity();

            LangSettingList LangSettingList1 = AppSettings.getLangSettingList(getContext());

            Preference preferenceLang = (Preference) findPreference("langSettingList");

            if (preferenceLang != null)
            {
                preferenceLang.setTitle(preferenceLang.getTitle() + " (" + LangSettingList1.getText() + ")");

                preferenceLang.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {

                        activitySettings.openLangDailog(preference);
                        return false;
                    }
                });

            }


//            pref_rand_poem = (Preference) findPreference("randomPoem");
//
//            String des = getString(R.string.current_scope);
//            des += " " + getRandomPoetsNameFromCat(GanjoorDbBrowser1) + "";
//            pref_rand_poem.setSummary(des);
//
//            final RandomPoetDialog randomPoetDialog = new RandomPoetDialog(getContext());
//
//            pref_rand_poem.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
//                @Override
//                public boolean onPreferenceClick(Preference preference) {
//
//                    randomPoetDialog.ShowLimitsDialog(true);
//                    return true;
//                }
//            });

            pref_op_db = (Preference) findPreference("optimize_db");
            pref_op_db.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference)
                {
                    activitySettings.optimizeDatabase();
                    return true;
                }
            });


        }


    }


    @Override
    protected void onPause() {
        super.onPause();

    }

    protected void openLangDailog(Preference preference)
    {

        ArrayList<LinkItem> links = new ArrayList<>();

        links.add(new LinkItem(0, getString(R.string.persian), "", R.drawable.ic_iran));
        links.add(new LinkItem(1, getString(R.string.english), "", R.drawable.ic_united_states));

        final Dialog dialog = new Dialog(preference.getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_listview);

        dialog.setCancelable(true);


        TextView dlg_title = (TextView) dialog.findViewById(R.id.dlg_title);
        ImageView dialog_icon = (ImageView) dialog.findViewById(R.id.dialog_icon);

        dlg_title.setText(getString(R.string.change_lang));
        dialog_icon.setImageResource(R.drawable.ic_language_black_24dp);

        ListView listView = (ListView) dialog.findViewById(R.id.listView);

        AdapterSocialList adapterSocialList = new AdapterSocialList(links, this, R.color.transparent);

        listView.setAdapter(adapterSocialList);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                dialog.dismiss();
                AppSettings.saveLanguageSettings(position);
                if (currentLocalIndex != position) {
                    currentLocalIndex = position;

                    recreate();
                }
            }
        });

        Button okBtn = (Button) dialog.findViewById(R.id.okBtn);

        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
            }
        });

       dialog.show();

    }


    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Bungee.slideDown(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.setting_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        if (android.R.id.home == id) {
            finish();
            Bungee.slideDown(this);
        }
        return super.onOptionsItemSelected(item);
    }

    protected void optimizeDatabase()
    {
        final CustomProgress customProgressDlg = new CustomProgress(this);
        customProgressDlg.showProgress(getString(R.string.optimize_db), getString(R.string.please_wait2) , false, false, true);


        new Thread(new Runnable() {
            public void run() {
                try
                {
                    GanjoorDbBrowser1.Vacum();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                customProgressDlg.dismiss();
            }
        }).start();
    }

    public void setRandomPoetText()//برای آینده
    {
//        GanjoorDbBrowser GanjoorDbBrowser1 = new GanjoorDbBrowser(this);
//        String des = getString(R.string.current_scope);
//        des += " " + getRandomPoetsNameFromCat(GanjoorDbBrowser1) + "";
//        pref_rand_poem.setSummary(des);
    }

}
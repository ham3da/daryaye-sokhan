package ir.ham3da.darya;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

import ir.ham3da.darya.ganjoor.GanjoorAudioInfo;
import ir.ham3da.darya.ganjoor.GanjoorDbBrowser;
import ir.ham3da.darya.ganjoor.GanjoorPoem;
import ir.ham3da.darya.admob.MainAdMobFragment;
import ir.ham3da.darya.notification.AlarmNotificationReceiver;
import ir.ham3da.darya.notification.PoemService;
import ir.ham3da.darya.ui.main.MainFavoritesFragment;
import ir.ham3da.darya.ui.main.MainPoetsFragment;
import ir.ham3da.darya.utility.AppSettings;
import ir.ham3da.darya.utility.CustomProgress;
import ir.ham3da.darya.utility.LangSettingList;
import ir.ham3da.darya.utility.MyDialogs;
import ir.ham3da.darya.utility.PreferenceHelper;
import ir.ham3da.darya.utility.SerializableNotify;
import ir.ham3da.darya.utility.SetLanguage;
import ir.ham3da.darya.utility.UpdateApp;
import ir.ham3da.darya.utility.UtilFunctions;


public class ActivityMain extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener
{

    public int booksCount = 0;
    MainActivityUtil mainActivityUtil1;
    UtilFunctions UtilFunctions1;
    MyDialogs MyDialogs1;
    String TAG = "ActivityMain";
    DrawerLayout drawer;
    int currentLocalIndex;

    // boolean doLoading;
    //Handler progressBarHandler = new Handler();

    @Override
    protected void attachBaseContext(Context newBase)
    {
        Context newContext = SetLanguage.wrap(newBase);
        super.attachBaseContext(newContext);
    }


    @Override
    public void applyOverrideConfiguration(Configuration overrideConfiguration)
    {
        if (overrideConfiguration != null)
        {
            int uiMode = overrideConfiguration.uiMode;
            overrideConfiguration.setTo(getBaseContext().getResources().getConfiguration());
            overrideConfiguration.uiMode = uiMode;
        }
        super.applyOverrideConfiguration(overrideConfiguration);
    }

    AdRequest adRequest;
    int rnd_poem_id;
    String findStr;
    int vOrder;

    @Override
    protected void onNewIntent(Intent intent)
    {
        super.onNewIntent(intent);

        Bundle extras = intent.getExtras();
        if (extras != null)
        {
            if (extras.containsKey("serializableNotifyVerse"))
            {
                SerializableNotify serializableNotify = (SerializableNotify) extras.getSerializable("serializableNotifyVerse");
                assert serializableNotify != null;
                rnd_poem_id = serializableNotify.getRnd_poem_id();
                findStr = serializableNotify.getFindStr();
                vOrder = serializableNotify.getvOrder();
                extras.putSerializable("serializableNotifyVerse", null);
            }
            showPoem(rnd_poem_id, findStr, vOrder);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);

        interstitial = new InterstitialAd(this);
        interstitial.setAdUnitId(getString(R.string.interstitial_ad_unit_id));
        adRequest = new AdRequest.Builder().build();
        setAdListen();

        UtilFunctions.changeTheme(this);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O)
        {
            SetLanguage.wrap(this);
        }

        // setTheme(R.style.LightTheme);
        setContentView(R.layout.activity_main);

        LangSettingList langSetting = AppSettings.getLangSettingList(this);
        currentLocalIndex = langSetting.getId();

        mainActivityUtil1 = new MainActivityUtil(this);
        UtilFunctions1 = new UtilFunctions(this);
        MyDialogs1 = new MyDialogs(this);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        String DB_PATH = AppSettings.getDatabasePath(this);
        Log.e(TAG, "DB_PATH1: " + DB_PATH);
        boolean exist_db = MainActivityUtil.checkExists(DB_PATH);
        if (exist_db)
        {
            loadPager();
        }
        else
        {
            mainActivityUtil1.extractGangoorDB(DB_PATH);
        }
        Log.e(TAG, "DB_PATH2: " + AppSettings.getAppFolderPath());
        progress_bar_dlg = findViewById(R.id.progressBar_loader);

        UpdateApp update = new UpdateApp(this);
        update.initUpdate();

    }

    public void LoadDBFirstTime(CustomProgress dlg1)
    {
        GanjoorDbBrowser GanjoorDbBrowser1 = new GanjoorDbBrowser(this);
        dlg1.dismiss();
        loadPager();
    }

    @StringRes
    private static final int[] TAB_TITLES = new int[]{R.string.tab_poets, R.string.tab_favorites, R.string.easy_donating};

    public void loadPager()
    {
        ViewPager2 viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(new FragmentStateAdapter(this)
        {
            @Override
            public int getItemCount()
            {
                return TAB_TITLES.length;
            }

            @NonNull
            @Override
            public Fragment createFragment(int position)
            {
                Log.e(TAG, "createFragment2: " + position);
                switch (position)
                {
                    case 0:
                    default:
                        return new MainPoetsFragment();
                    case 1:
                        return new MainFavoritesFragment();
                    case 2:
                        return new MainAdMobFragment();
                }
            }
        });
        new TabLayoutMediator(findViewById(R.id.tabs), viewPager,
                (tab, position) -> tab.setText(getString(TAB_TITLES[position]))).attach();


        Bundle extras = getIntent().getExtras();
        if (extras != null)
        {

            if (extras.containsKey("serializableNotifyVerse"))
            {
                SerializableNotify serializableNotify = (SerializableNotify) extras.getSerializable("serializableNotifyVerse");
                assert serializableNotify != null;
                rnd_poem_id = serializableNotify.getRnd_poem_id();
                findStr = serializableNotify.getFindStr();
                vOrder = serializableNotify.getvOrder();
                extras.putSerializable("serializableNotifyVerse", null);
            }

            Log.e(TAG, "getExtras: ok " + findStr);
        }

        if (rnd_poem_id > 0)
        {
            showPoem(rnd_poem_id, findStr, vOrder);
        }
        else
        {
            showNotify();
        }


    }

    public void showNotify()
    {

        boolean notifyed = false;
        String notify_title, notify_url, notify_url_text, notify_text;
        PreferenceHelper preferenceHelper = new PreferenceHelper(getApplicationContext());
        notify_text = preferenceHelper.getKey("notify_text", "");

        if (!notify_text.isEmpty())
        {

            notify_title = preferenceHelper.getKey("notify_title", "");
            notify_url = preferenceHelper.getKey("notify_url", "");
            notify_url_text = preferenceHelper.getKey("MyUrlText", "");
            notifyed = true;
            MyDialogs1.showNotify(notify_text, notify_title, notify_url, notify_url_text);

        }


        if (!notifyed)
        {

            if (getIntent().getExtras() != null)
            {

//                for (String key : getIntent().getExtras().keySet())
//                {
//                    Log.e(TAG, key + ": " + getIntent().getExtras().get(key));
//                }

                if (getIntent().getExtras().containsKey("Package") && getIntent().getExtras().getString("Package", "").equals(getPackageName()))
                {

                    notify_text = getIntent().getExtras().getString("Text", "");
                    notify_title = getIntent().getExtras().getString("Title", "");
                    notify_url = getIntent().getExtras().getString("MyUrl", "");
                    notify_url_text = getIntent().getExtras().getString("MyUrlText", "");

                    MyDialogs1.showNotify(notify_text, notify_title, notify_url, notify_url_text);

                }
            }

        }
    }

    @Override
    public void onBackPressed()
    {
        if (drawer.isDrawerOpen(GravityCompat.START))
        {
            drawer.closeDrawer(GravityCompat.START);
        }
        else
        {
            App globalVariable = (App) getApplicationContext();

            if (globalVariable.getAdviewd())
            {
                globalVariable.setAdviewd(false);
                super.onBackPressed();
            }
            else
            {
                askExitAd();
            }

        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        GanjoorDbBrowser GanjoorDbBrowser1 = new GanjoorDbBrowser(this);
        Intent intent;
        int id = item.getItemId();
        switch (id)
        {
            case R.id.action_search:
                intent = new Intent(this, ActivitySearch.class);
                this.startActivity(intent);
                Bungee.card(this);
                break;
            case R.id.action_add_colection:
                ShowCollectionAct();
                break;

            case R.id.action_random_poem:

                try
                {

                    String randomSelectedCategories = AppSettings.getRandomSelectedCategories();

                    GanjoorPoem poem = GanjoorDbBrowser1.getPoemRandom(randomSelectedCategories);

                    if (poem != null)
                    {

                        intent = new Intent(ActivityMain.this, ActivityPoem.class);
                        intent.putExtra("poem_id", poem._ID);
                        startActivity(intent);
                        Bungee.spin(this);
                    }
                    else
                    {
                        Toast.makeText(this, R.string.nothing_found, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception ex)
                {
                    Log.e(TAG, "getPoemRandom: " + ex.getMessage());
                }


                break;
        }
        return super.onOptionsItemSelected(item);
    }


    public void ShowCollectionAct()
    {

        if (UtilFunctions.isNetworkConnected(this))
        {
            Intent intent = new Intent(this, ActivityCollection.class);
            this.startActivity(intent);
            Bungee.card(this);
        }
        else
        {
            MyDialogs1.ShowWarningMessage(getString(R.string.internet_failed));
        }
    }


    private void showPoem(int poem_id)
    {
        Log.e(TAG, "rnd_poem_id: " + poem_id);
        Intent intent = new Intent(ActivityMain.this, ActivityPoem.class);
        intent.putExtra("poem_id", poem_id);
        startActivity(intent);
        Bungee.card(ActivityMain.this);
    }


    private void showPoem(int poem_id, String findStr, int vOrder)
    {
        Intent intent = new Intent(ActivityMain.this, ActivityPoem.class);
        intent.putExtra("poem_id", poem_id);
        intent.putExtra("from_search", true);
        intent.putExtra("findStr", findStr);
        intent.putExtra("vOrder", vOrder);


        startActivity(intent);
        Bungee.card(ActivityMain.this);
    }


    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem)
    {
        final int id = menuItem.getItemId();
        Handler handler = new Handler(Looper.getMainLooper());

        handler.postDelayed(() -> {
            Intent intent;
            switch (id)
            {
                case R.id.collections:
                    ShowCollectionAct();
                    break;

                case R.id.nav_last_read:

                    int getLastPoemIdVisited = AppSettings.getLastPoemIdVisited();
                    if (getLastPoemIdVisited > 0)
                    {
                        showPoem(getLastPoemIdVisited);
                    }
                    else
                    {
                        Toast.makeText(ActivityMain.this, R.string.nothing_found, Toast.LENGTH_LONG).show();
                    }

                    break;

                case R.id.nav_social_networks:
                    MyDialogs1.socialNetworks();
                    break;


                case R.id.nav_help:
                    MyDialogs1.showHelp();
                    break;

                case R.id.nav_setting:

                    intent = new Intent(ActivityMain.this, ActivitySettings.class);
                    startActivity(intent);
                    Bungee.card(ActivityMain.this);

                    break;
                case R.id.nav_about:

                    MyDialogs1.showAbout();

                    break;
                case R.id.nav_rating:
                    UtilFunctions1.gotoRating();

                    break;
                case R.id.nav_contact_us:

                    MyDialogs1.ShowContactUs();

                    break;
                case R.id.nav_share:

                    UtilFunctions1.shareApp();

                    break;
                case R.id.nav_policy:

                    MyDialogs1.showPolicy();
                    break;

//                case R.id.nav_donate:
//                    UtilFunctions1.openUrl("https://ham3da.ir/darya-donate/");
//                    break;


                case R.id.nav_poem_game:
                    intent = new Intent(ActivityMain.this, ActivityPuzzle.class);
                    intent.putExtra("parentCate", 0);
                    startActivity(intent);
                    Bungee.card(ActivityMain.this);
                    break;


            }
        }, 300);


        // Handle navigation view item clicks here.

        if (drawer.isDrawerOpen(GravityCompat.START))
        {

            drawer.closeDrawer(GravityCompat.START);
        }
        return true;

    }

    public void restartApp()
    {
        Intent intent = new Intent(getBaseContext(), ActivityMain.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }


    /**
     * Set Count of Poets And Books in textview
     */
    public void setCountPoetsAndBooks()
    {
        try
        {
            GanjoorDbBrowser GanjoorDbBrowser1 = new GanjoorDbBrowser(this);
            int getPoetsCount = GanjoorDbBrowser1.getPoetsCount();
            int getBooksCount = booksCount;
            NavigationView navigationView = findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(this);
            View hview = navigationView.getHeaderView(0);
            TextView textView_all_count = hview.findViewById(R.id.textView_all_count);
            String str_count_all_word = String.format(Locale.getDefault(), getString(R.string.nav_header_subtitle), getBooksCount, getPoetsCount);
            textView_all_count.setText(str_count_all_word);

        } catch (Exception ex)
        {
            Log.e(TAG, "setCountPoetsAndBooks: " + ex.getMessage());
        }


    }


    @Override
    protected void onResume()
    {
        super.onResume();

        LangSettingList langSetting = AppSettings.getLangSettingList(this);

        if (currentLocalIndex != langSetting.getId())
        {
            currentLocalIndex = langSetting.getId();
            recreate();
        }

    }

    InterstitialAd interstitial;


    private void setAdListen()
    {
        interstitial.setAdListener(new AdListener()
        {

            @Override
            public void onAdLoaded()
            {
                super.onAdLoaded();
                progress_bar_dlg.setVisibility(View.GONE);
                if (interstitial.isLoaded())
                {
                    interstitial.show();
                }
                else
                {
                    Toast.makeText(ActivityMain.this, getString(R.string.admob_not_load), Toast.LENGTH_SHORT).show();
                    finish();
                }

            }


            @Override
            public void onAdOpened()
            {
                super.onAdOpened();

            }

            @Override
            public void onAdClosed()
            {
                super.onAdClosed();
                finish();
            }

            @Override
            public void onAdFailedToLoad(LoadAdError loadAdError)
            {
                super.onAdFailedToLoad(loadAdError);
                progress_bar_dlg.setVisibility(View.GONE);
                Log.e("interstitial", "onAdFailedToLoad: " + loadAdError.getMessage());
                Toast.makeText(ActivityMain.this, getString(R.string.admob_not_load), Toast.LENGTH_SHORT).show();

                finish();
            }

        });
    }

    ProgressBar progress_bar_dlg;

    private void askExitAd()
    {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setIcon(R.drawable.ic_baseline_favorite_border_24);

        dialog.setCancelable(false);
        dialog.setTitle(R.string.easy_donating);
        dialog.setMessage(R.string.ad_exit_text);
        dialog.setPositiveButton(R.string.view_admob, (dialog1, id) -> {
            progress_bar_dlg.setVisibility(View.VISIBLE);
            if (interstitial.isLoaded())
            {
                interstitial.show();
            }
            else
            {
                interstitial.loadAd(adRequest);
            }

        }).setNegativeButton(R.string.close, (dialog12, which) -> finish());

        final AlertDialog alert = dialog.create();
        alert.show();
    }

    public void startPoemAlarm()
    {
        if (AppSettings.checkRandomNotifyIsActive())
        {
            if (!PoemService.getIsRunning())
            {
                Intent i = new Intent(this, PoemService.class);
                startService(i);
            }
        }

    }

}
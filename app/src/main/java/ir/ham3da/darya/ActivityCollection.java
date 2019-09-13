package ir.ham3da.darya;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;


import android.content.Context;

import android.os.AsyncTask;
import android.os.Bundle;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;


import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayInputStream;

import java.io.IOException;
import java.io.InputStream;

import java.nio.charset.StandardCharsets;

import java.util.LinkedList;
import java.util.List;

import ir.ham3da.darya.ganjoor.GDBList;
import ir.ham3da.darya.ganjoor.GanjoorDbBrowser;
import ir.ham3da.darya.utility.AppSettings;
import ir.ham3da.darya.utility.DownloadGDBTask;
import ir.ham3da.darya.adaptors.GDBListAdaptor;
import ir.ham3da.darya.adaptors.ScheduleGDB;
import ir.ham3da.darya.utility.DownloadFromUrl;
import ir.ham3da.darya.utility.MyDialogs;
import ir.ham3da.darya.utility.SetLanguage;
import ir.ham3da.darya.utility.UtilFunctions;


public class ActivityCollection extends AppCompatActivity {

    GanjoorDbBrowser _DbBrowser;
    public GDBListAdaptor GDBListAdaptor1;
    RecyclerView recyclerViewCollection;
    SwipeRefreshLayout simpleSwipeRefreshLayout;
    MyDialogs MyDialogs1;
    String dlPath;


    public void StartDownload(GDBListAdaptor.ViewHolder holder, ScheduleGDB ScheduleGDB1) {

        if (UtilFunctions.isNetworkConnected(this)) {
            DownloadGDBTask downloadGDBTask = new DownloadGDBTask(holder, this, ScheduleGDB1);
            downloadGDBTask.execute(ScheduleGDB1._URL);

        } else {
            MyDialogs1.ShowWarningMessage(getString(R.string.internet_failed));
        }

    }


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(SetLanguage.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_collection);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        setTitle(R.string.collections);

        recyclerViewCollection = (RecyclerView) findViewById(R.id.RecyclerViewCollection);
        _DbBrowser = new GanjoorDbBrowser(this);

        simpleSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.simpleSwipeRefreshLayout);

        simpleSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadItems();
            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerViewCollection.setLayoutManager(linearLayoutManager);

        if (UtilFunctions.isNetworkConnected(this))
        {
            loadItems();
        } else {
            finish();
            Toast.makeText(this, getString(R.string.internet_failed), Toast.LENGTH_SHORT).show();
        }

        dlPath = AppSettings.getDownloadPath(this);
        MyDialogs1 = new MyDialogs(this);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Bungee.slideDown(this); //fire the slide left animation
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            Bungee.slideDown(this);
        }
        return super.onOptionsItemSelected(item);
    }


    private void loadItems() {
        DownloadXmlTask downloadXmlTask = new DownloadXmlTask();
        downloadXmlTask.execute(_Lists_Url[0]);
    }


    /**
     * نام فایلهای مجموعه های پیش فرض
     */
    private final static String[] _Lists_Url = new String[]{
            "http://i.ganjoor.net/android/androidgdbs.xml"
            /*
            "sitegdbs.xml",
            "newgdbs.xml",
            "programgdbs.xml"*/
    };


    private GDBList _MixedList = null;


    private void SetLists(String XMLString) {

        if (XMLString != null && !XMLString.isEmpty()) {

            InputStream InputStream1 = new ByteArrayInputStream(XMLString.getBytes(StandardCharsets.UTF_8));

            List<GDBList> lists = new LinkedList<GDBList>();

            GDBList list = null;
            try {
                list = GDBList.Build(0, InputStream1, _DbBrowser);
                if (list != null) {
                    lists.add(list);
                }
            } catch (IOException e) {

                Log.e("IOException", "SetLists err: " + e.getMessage());
                //  e.printStackTrace();
            } catch (XmlPullParserException e) {

                Log.e("XmlPullParserException", "SetLists err: " + e.getMessage());
                // e.printStackTrace();
            }
            catch (Exception ex)
            {
                Log.e("Exception", "SetLists err: " + ex.getMessage());
            }

            _MixedList = GDBList.Mix(lists);


        } else {
            Toast.makeText(this, getString(R.string.nothing_found), Toast.LENGTH_SHORT).show();
        }


        if (_MixedList != null) {
            showGDBList(_MixedList);
        }

    }

    /**
     * نمایش لیست مجموعه های قابل دریافت
     *
     * @param list GDBList
     */
    protected void showGDBList(GDBList list) {

        if (GDBListAdaptor1 != null) {
            GDBListAdaptor1.notifyDataSetChanged();
        } else {
            GDBListAdaptor1 = new GDBListAdaptor(list, this);
            recyclerViewCollection.setAdapter(GDBListAdaptor1);
        }
        recyclerViewCollection.scrollTo(0, 0);
    }


    private class DownloadXmlTask extends AsyncTask<String, Void, String> {


        // onPreExecute called before the doInBackgroud start for display
        // progress dialog.
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            if (_MixedList != null) {
                _MixedList = null;
            }

            if (GDBListAdaptor1 != null) {
                GDBListAdaptor1.notifyDataSetChanged();
            }

            if (!simpleSwipeRefreshLayout.isRefreshing()) {
                simpleSwipeRefreshLayout.setRefreshing(true);
            }
        }

        @Override
        protected String doInBackground(String... urls) {

            try {
                return DownloadFromUrl.downloadDataFromUrl(urls[0]);
            } catch (Exception e) {
                Log.e("DownloadFromUrl", "doInBackground: "+e.getMessage());
                return null;
            }
        }

        // onPostExecute displays the results of the doInBackgroud and also we
        // can hide progress dialog.
        @Override
        protected void onPostExecute(String result) {

            if (simpleSwipeRefreshLayout.isRefreshing()) {
                simpleSwipeRefreshLayout.setRefreshing(false);
            }
            SetLists(result);
        }
    }


    public void ShowSuccessToast() {
        Toast.makeText(this, getString(R.string.success_add), Toast.LENGTH_SHORT).show();
    }

//Downloader
}

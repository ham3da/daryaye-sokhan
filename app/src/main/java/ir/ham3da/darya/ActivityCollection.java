package ir.ham3da.darya;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;


import android.content.Context;

import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;


import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.URLUtil;
import android.widget.SearchView;

import android.widget.TextView;
import android.widget.Toast;


import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayInputStream;

import java.io.IOException;
import java.io.InputStream;

import java.nio.charset.StandardCharsets;

import java.util.LinkedList;
import java.util.List;

import ir.ham3da.darya.ganjoor.GDBInfo;
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

    SearchView searchView;
    boolean searchViewHasFocus = false;

    String TAG = "ActivityCollection";

    private GDBList _MixedList = null;


    public int DlIndex = 0;
    public void StartDownloadALL()
    {

        if (_MixedList != null)
        {

            if(_MixedList._Items.size() > DlIndex) {
                GDBListAdaptor.ViewHolder viewHolder = (GDBListAdaptor.ViewHolder) recyclerViewCollection.findViewHolderForAdapterPosition(DlIndex);

                GDBInfo GDBInfo1 =  _MixedList._Items.get(DlIndex);

                 boolean poetInstalled = GDBInfo1._Exist;
                 boolean updateAvailable = GDBInfo1._UpdateAvailable;

                String fileName = URLUtil.guessFileName(GDBInfo1._DownloadUrl, null, null);
                ScheduleGDB scheduleGDB;
                if (poetInstalled)
                {
                    if(updateAvailable) {
                        scheduleGDB = new ScheduleGDB(DlIndex, GDBInfo1._PoetID, GDBInfo1._CatName, GDBInfo1._DownloadUrl, fileName, GDBInfo1._PubDateString + "|" + GDBInfo1._FileSizeInByte, true);
                        StartDownload(viewHolder, scheduleGDB, true);
                    }
                    else
                    {
                        DlIndex++;
                        StartDownloadALL();
                    }
                }
                else
                {
                    scheduleGDB = new ScheduleGDB(DlIndex, GDBInfo1._PoetID, GDBInfo1._CatName, GDBInfo1._DownloadUrl, fileName, GDBInfo1._PubDateString + "|" + GDBInfo1._FileSizeInByte, false);
                    StartDownload(viewHolder, scheduleGDB, true);

                }


            }
        }

    }


    public void StartDownload(GDBListAdaptor.ViewHolder holder, ScheduleGDB ScheduleGDB1, boolean downloadAll) {

        if (UtilFunctions.isNetworkConnected(this)) {
            DownloadGDBTask downloadGDBTask = new DownloadGDBTask(holder, this, ScheduleGDB1, downloadAll);
            downloadGDBTask.execute(ScheduleGDB1._URL);

        } else {
            MyDialogs1.ShowWarningMessage(getString(R.string.internet_failed));
        }

    }


    private void reloadRecycleView() {
        if (UtilFunctions.isNetworkConnected(this)) {
            loadItems();
        } else {
            finish();
            Toast.makeText(this, getString(R.string.internet_failed), Toast.LENGTH_SHORT).show();
        }
    }


    private void searchInRecycleView(String findStr) {
        if (!findStr.isEmpty()) {
            if (_MixedList != null) {

                GDBList _MixedListSearch = new GDBList(_MixedList);
                _MixedListSearch._Items.clear();

                int Index = 0;
                for (GDBInfo gdbInfo : _MixedList._Items) {
                    if (gdbInfo._CatName.contains(findStr)) {
                        Index++;
                        gdbInfo._Index = Index;
                        _MixedListSearch._Items.add(gdbInfo);
                    }
                }
                showGDBList(_MixedListSearch);
            }
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

        recyclerViewCollection = findViewById(R.id.RecyclerViewCollection);
        _DbBrowser = new GanjoorDbBrowser(this);

        simpleSwipeRefreshLayout = findViewById(R.id.simpleSwipeRefreshLayout);

        simpleSwipeRefreshLayout.setOnRefreshListener(this::loadItems);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerViewCollection.setLayoutManager(linearLayoutManager);

        reloadRecycleView();

        dlPath = AppSettings.getDownloadPath(this);
        MyDialogs1 = new MyDialogs(this);
    }


    @Override
    public void onBackPressed() {
        if (searchViewHasFocus) {
            searchView.setIconified(true);
        } else {
            super.onBackPressed();
            Bungee.slideDown(this); //fire the slide left animation
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.collection_menu, menu);

        MenuItem action_search = menu.findItem(R.id.action_search);

        searchView = (SearchView) action_search.getActionView();

        searchView.setQueryHint(getString(R.string.enter_poet_name));

        try {
            Typeface typeface = ResourcesCompat.getFont(this, R.font.iran_sans_mobile_light);
            int id = searchView.getContext()
                    .getResources()
                    .getIdentifier("android:id/search_src_text", null, null);
            TextView textView = searchView.findViewById(id);
            textView.setTypeface(typeface);
        } catch (Exception ex) {
            Log.e(TAG, "msg: " + ex.getMessage());
        }


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Toast like print
//                Toast.makeText(ActivityCollection.this, "SearchOnQueryTextSubmit: " + query, Toast.LENGTH_SHORT).show();
//                if (!searchView.isIconified()) {
//                    searchView.setIconified(true);
//                }
//                action_search.collapseActionView();

                searchInRecycleView(query.trim());
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }

        });

        searchView.setOnQueryTextFocusChangeListener((v, hasFocus) -> searchViewHasFocus = hasFocus);


        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                if (searchViewHasFocus) {
                    searchView.setIconified(true);
                } else {
                    super.onBackPressed();
                    Bungee.slideDown(this); //fire the slide left animation
                }
                break;
            case R.id.download_all:
                DlIndex = 0;
                StartDownloadALL();
                break;
            case R.id.reload_all:
                reloadRecycleView();
                break;


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


    private void SetLists(String XMLString) {

        if (XMLString != null && !XMLString.isEmpty()) {

            InputStream InputStream1 = new ByteArrayInputStream(XMLString.getBytes(StandardCharsets.UTF_8));

            List<GDBList> lists = new LinkedList<>();

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
            } catch (Exception ex) {
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


        GDBListAdaptor1 = new GDBListAdaptor(list, this);
        recyclerViewCollection.setAdapter(GDBListAdaptor1);
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
                Log.e("DownloadFromUrl", "doInBackground: " + e.getMessage());
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


    public void ShowSuccessDownloadToast(boolean DownloadAll)
    {
        if(DownloadAll){
            if( DlIndex+1 == _MixedList._Items.size())
            {
                Toast.makeText(this, getString(R.string.all_collectons_added), Toast.LENGTH_SHORT).show();
            }
        }
        else
        {
        Toast.makeText(this, getString(R.string.success_add), Toast.LENGTH_SHORT).show();
        }
    }

    public void DownloadFailToast() {
        Toast.makeText(this, getString(R.string.download_failed), Toast.LENGTH_SHORT).show();
    }
//Downloader
}

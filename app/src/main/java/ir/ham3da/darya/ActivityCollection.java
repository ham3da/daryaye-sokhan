package ir.ham3da.darya;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;


import android.app.Dialog;
import android.content.Context;

import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;


import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SearchView;

import android.widget.TextView;
import android.widget.Toast;


import com.downloader.Error;
import com.downloader.OnDownloadListener;
import com.downloader.PRDownloader;
import com.downloader.PRDownloaderConfig;
import com.downloader.request.DownloadRequest;
import com.downloader.request.DownloadRequestBuilder;

import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayInputStream;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import java.nio.charset.StandardCharsets;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import ir.ham3da.darya.adaptors.ScheduleAudio;
import ir.ham3da.darya.ganjoor.GDBInfo;
import ir.ham3da.darya.ganjoor.GDBList;
import ir.ham3da.darya.ganjoor.GanjoorAudioInfo;
import ir.ham3da.darya.ganjoor.GanjoorDbBrowser;
import ir.ham3da.darya.utility.AppSettings;
import ir.ham3da.darya.adaptors.GDBListAdaptor;
import ir.ham3da.darya.adaptors.ScheduleGDB;
import ir.ham3da.darya.utility.DownloadFromUrl;

import ir.ham3da.darya.utility.MyDialogs;
import ir.ham3da.darya.utility.SetLanguage;
import ir.ham3da.darya.utility.UtilFunctions;


public class ActivityCollection extends AppCompatActivity
{

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
    int sumDownloaded = 0;

    RelativeLayout download_RelativeLayout;

    ImageButton cancel_downloads;
    TextView progress_text;
    TextView progress_description;
    TextView progress_text1;
    TextView progress_text2;
    ProgressBar progress_bar;


    List<ScheduleGDB> scheduleGDBList;

    ActionMode actionMode;


    public void showActionbar()
    {
        if (actionMode == null)
        {
            actionMode = startActionMode(callback);
        }

    }

    public void setActionbarTitle(String title)
    {
        if (actionMode != null)
        {
            actionMode.setTitle(title);
        }

    }

    private ActionMode.Callback callback = new ActionMode.Callback()
    {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu)
        {
            mode.getMenuInflater().inflate(R.menu.audio_collection_menu, menu);
            return true;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item)
        {
            switch (item.getItemId())
            {
                case R.id.action_select_all:
                    if (GDBListAdaptor1 != null)
                    {
                        if (GDBListAdaptor1.checkAnyBookIsSelected())
                        {
                            GDBListAdaptor1.selectAllItem(false);
                            item.setIcon(R.drawable.ic_outline_library_add_check_24);
                        }
                        else
                        {
                            GDBListAdaptor1.selectAllItem(true);
                            item.setIcon(R.drawable.ic_baseline_library_add_check_24_fill);
                        }

                    }
                    return true;
                case R.id.action_dl:
                    downloadMarkedBook();
                    return true;

                case R.id.action_delete:
                    deleteMarkedBook();
                    return true;
                default:
                    return false;
            }

        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu)
        {
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode)
        {
            actionMode = null;

        }
    };

    private void reloadRecycleView()
    {
        if (UtilFunctions.isNetworkConnected(this))
        {
            loadItems();
        }
        else
        {
            finish();
            Toast.makeText(this, getString(R.string.internet_failed), Toast.LENGTH_SHORT).show();
        }
    }


    private void searchInRecycleView(String findStr)
    {
        if (!findStr.isEmpty())
        {
            if (_MixedList != null)
            {

                GDBList _MixedListSearch = new GDBList(_MixedList);
                _MixedListSearch._Items.clear();

                int Index = 0;
                for (GDBInfo gdbInfo : _MixedList._Items)
                {
                    if (gdbInfo._CatName.contains(findStr))
                    {
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
    protected void attachBaseContext(Context newBase)
    {
        super.attachBaseContext(SetLanguage.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        UtilFunctions.changeTheme(this, true);

        setContentView(R.layout.activity_collection);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        setTitle(R.string.collections);

        recyclerViewCollection = findViewById(R.id.RecyclerViewCollection);
        _DbBrowser = new GanjoorDbBrowser(this);

        simpleSwipeRefreshLayout = findViewById(R.id.simpleSwipeRefreshLayout);

        simpleSwipeRefreshLayout.setOnRefreshListener(this::loadItems);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerViewCollection.setLayoutManager(linearLayoutManager);


        download_RelativeLayout = findViewById(R.id.download_RelativeLayout);
        if(download_RelativeLayout.getVisibility()== View.VISIBLE)
        {
            download_RelativeLayout.setVisibility(View.GONE);
        }

        cancel_downloads = findViewById(R.id.cancel_downloads);
        progress_text = findViewById(R.id.progress_text);
        progress_description = findViewById(R.id.progress_description);
        progress_text1 = findViewById(R.id.progress_text1);
        progress_text2 = findViewById(R.id.progress_text2);
        progress_bar = findViewById(R.id.progress_bar);

        reloadRecycleView();


        dlPath = AppSettings.getDownloadPath(this);
        MyDialogs1 = new MyDialogs(this);

        PRDownloaderConfig config = PRDownloaderConfig.newBuilder()
                .setDatabaseEnabled(true)
                .build();
        PRDownloader.initialize(getApplicationContext(), config);
    }

    private void downloadMarkedBook()
    {
        scheduleGDBList = new ArrayList<>();
        for (int i = 0; i < GDBListAdaptor1.getItemCount(); i++)
        {
            if ( ( GDBListAdaptor1.isSelected(i) && !GDBListAdaptor1.getBookExist(i) ) || (GDBListAdaptor1.isSelected(i) && GDBListAdaptor1.getBookUpdateAvailable(i)) )
            {
                ScheduleGDB scheduleBook = GDBListAdaptor1.getScheduleBook(i);
                scheduleGDBList.add(scheduleBook);
            }
        }
        sumDownloaded = 0;
        downloadBooks(scheduleGDBList);

    }

    public void downloadBooks(List<ScheduleGDB> scheduleBookList1)
    {
        int total_downloads = scheduleBookList1.size();
        if (total_downloads > 0)
        {
            if (download_RelativeLayout.getVisibility() != View.VISIBLE)
            {
                download_RelativeLayout.setVisibility(View.VISIBLE);
            }

            progress_bar.setProgress(0);
            progress_text.setText("");
            progress_description.setText("");
            progress_text1.setText("");
            progress_text2.setText("");

            cancel_downloads.setOnClickListener(v -> {
                PRDownloader.cancelAll();
                download_RelativeLayout.setVisibility(View.GONE);
            });

            if (total_downloads > sumDownloaded)
            {
                ScheduleGDB scheduleGDB = scheduleBookList1.get(sumDownloaded);
                sumDownloaded++;

                String fileName = scheduleGDB._FileName;
                String des = String.format(Locale.getDefault(), "%d", sumDownloaded) + " / " + String.format(Locale.getDefault(), "%d", total_downloads);

                DownloadRequestBuilder downloadRequestBuilder = PRDownloader.download(scheduleGDB._URL, dlPath, scheduleGDB._FileName);
                int finalSum = sumDownloaded;
                DownloadRequest downloadRequest = downloadRequestBuilder.build();
                downloadRequest.setDownloadId(scheduleGDB._Pos);
                downloadRequest.setOnProgressListener(progress ->
                {
                    int percent = (int) Math.round(((double) progress.currentBytes / (double) progress.totalBytes) * 100);
                    if (percent > 0)
                    {
                        String formatBytesCopied = getString(R.string.file_received) + " " + android.text.format.Formatter.formatFileSize(ActivityCollection.this, progress.currentBytes);
                        String formatFileLength = getString(R.string.file_size) + " " + android.text.format.Formatter.formatFileSize(ActivityCollection.this, progress.totalBytes);
                        String progressText = formatBytesCopied + " / " + formatFileLength;

                        progress_bar.setProgress(percent);
                        String percentStr = String.format(Locale.getDefault(), "%d", percent) + " %";
                        progress_text.setText(percentStr);
                        progress_description.setText(des);
                        progress_text1.setText(formatBytesCopied);
                        progress_text2.setText(formatFileLength);
                    }
                })
                        .start(new OnDownloadListener()
                        {
                            @Override
                            public void onDownloadComplete()
                            {
                                File file = new File(dlPath + "/" + fileName);
                                if (file.exists())
                                {
                                    if (scheduleGDB._DoUpdate)
                                    {
                                        _DbBrowser.DeletePoet(scheduleGDB._PoetID);
                                    }
                                    boolean imported = _DbBrowser.ImportGdb(dlPath + "/" + fileName, scheduleGDB._Update_info);
                                    if (imported)
                                    {
                                        GDBListAdaptor1.notifyNewImported(scheduleGDB._Pos, scheduleGDB._PoetID);
                                        try
                                        {
                                            boolean delete = file.delete();
                                        } catch (Exception ex)
                                        {
                                            Log.e(TAG, "onPostExecute: " + ex.getMessage());
                                        }

                                        App globalVariable = (App) getApplicationContext();
                                        globalVariable.setUpdatePoetList(true);

                                    }

                                    downloadBooks(scheduleGDBList);
                                }

                                if (finalSum >= total_downloads)
                                {
                                    boolean multi_dl = false;
                                    if (total_downloads > 1)
                                    {
                                        multi_dl = true;
                                    }
                                    GDBListAdaptor1.notifyDataSetChanged();
                                    ShowSuccessDownloadToast(multi_dl);
                                    download_RelativeLayout.setVisibility(View.GONE);


                                }
                            }

                            @Override
                            public void onError(Error error)
                            {
                                Log.e("DownloadAudioTask", "ResponseCode: " +
                                        error.getResponseCode() + ", get ServerError Message: " + error.getServerErrorMessage() +
                                        ", get Connection Exception:" + error.getConnectionException().getMessage());
                                DownloadFailToast();
                                PRDownloader.cancel(scheduleGDB._Pos);
                                download_RelativeLayout.setVisibility(View.GONE);
                                GDBListAdaptor1.notifyDataSetChanged();
                            }
                        });
            }

        }
    }

    private void deleteMarkedBook()
    {
        if (!GDBListAdaptor1.checkAnyBookIsSelected())
        {
            return;
        }

        List<GDBInfo> gdbInfos = new ArrayList<>();

        for (int i = 0; i < GDBListAdaptor1.getItemCount(); i++)
        {
            if (GDBListAdaptor1.isSelected(i) && GDBListAdaptor1.getBookExist(i))
            {
                GDBInfo gdbInfo = GDBListAdaptor1.getGanjoorBookInfo(i);
                gdbInfos.add(gdbInfo);
            }
        }

        MyDialogs MyDialogs1 = new MyDialogs(this);

        String ques = getString(R.string.delete_all_au);
        final Dialog yesNoDialog = MyDialogs1.YesNoDialog(ques, getDrawable(R.drawable.ic_delete_white_24dp), true);
        Button noBtn = yesNoDialog.findViewById(R.id.noBtn);
        noBtn.setOnClickListener(view -> yesNoDialog.dismiss());

        Button yesBtn = yesNoDialog.findViewById(R.id.yesBtn);
        yesBtn.setOnClickListener(view ->
        {
            yesNoDialog.dismiss();
            DeleteBookFilesTask deleteBookFilesTask = new DeleteBookFilesTask();
            GDBInfo[] ganjoorBookInfoListArray = gdbInfos.toArray(new GDBInfo[0]);
            deleteBookFilesTask.execute(ganjoorBookInfoListArray);

        });
        yesNoDialog.show();

    }

    private class DeleteBookFilesTask extends AsyncTask<GDBInfo, Integer, Integer>
    {

        public DeleteBookFilesTask()
        {
            cancel_downloads.setOnClickListener(v -> {
                this.cancel(true);
            });
        }

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();

            download_RelativeLayout.setVisibility(View.VISIBLE);
            progress_bar.setProgress(0);
            progress_text.setText("");
            progress_description.setText("");
            progress_text1.setText("");
            progress_text2.setText("");
        }

        @Override
        protected Integer doInBackground(GDBInfo... gdbInfos)
        {
            try
            {
                int count = gdbInfos.length;
                for (int i = 0; i < count; i++)
                {
                    GDBInfo gdbInfo = gdbInfos[i];
                    GDBListAdaptor1.deleteItemMarked(gdbInfo);
                    publishProgress(i, count);
                }
                return 1;
            } catch (Exception ex)
            {
                ex.printStackTrace();
                Log.e("xml_Dl", "doInBackground: " + ex.getMessage());
                return -1;
            }
        }


        @Override
        protected void onProgressUpdate(Integer... values)
        {
            super.onProgressUpdate(values);
            int total = values[1];
            int current = values[0];
            int percent = (int) Math.round(((double) current / (double) total) * 100);
            if (percent > 0)
            {
                progress_bar.setProgress(percent);
            }

            String percentStr = String.format(Locale.getDefault(), "%d", percent) + " %";
            progress_text.setText(percentStr);
            progress_description.setText(R.string.deleting);
            progress_text1.setText("");
            progress_text2.setText("");
        }


        @Override
        protected void onPostExecute(Integer result)
        {
            download_RelativeLayout.setVisibility(View.GONE);
            GDBListAdaptor1.notifyDataSetChanged();

        }
    }


    @Override
    public void onBackPressed()
    {
        if (searchViewHasFocus)
        {
            searchView.setIconified(true);
        }
        else
        {
            super.onBackPressed();
            Bungee.slideDown(this); //fire the slide left animation
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.collection_menu, menu);

        MenuItem action_search = menu.findItem(R.id.action_search);

        searchView = (SearchView) action_search.getActionView();

        searchView.setQueryHint(getString(R.string.enter_poet_name));

        try
        {
            Typeface typeface = ResourcesCompat.getFont(this, R.font.iran_sans_mobile_light);
            int id = searchView.getContext()
                    .getResources()
                    .getIdentifier("android:id/search_src_text", null, null);
            TextView textView = searchView.findViewById(id);
            textView.setTypeface(typeface);
        } catch (Exception ex)
        {
            Log.e(TAG, "msg: " + ex.getMessage());
        }


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener()
        {
            @Override
            public boolean onQueryTextSubmit(String query)
            {
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
            public boolean onQueryTextChange(String s)
            {
                return false;
            }

        });

        searchView.setOnQueryTextFocusChangeListener((v, hasFocus) -> searchViewHasFocus = hasFocus);


        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {

        int id = item.getItemId();

        switch (id)
        {
            case android.R.id.home:
                if (searchViewHasFocus)
                {
                    searchView.setIconified(true);
                }
                else
                {
                    super.onBackPressed();
                    Bungee.slideDown(this); //fire the slide left animation
                }
                break;
            case R.id.reload_all:
                reloadRecycleView();
                break;

        }


        return super.onOptionsItemSelected(item);
    }


    private void loadItems()
    {
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


    private Integer SetLists(String XMLString)
    {
        int res = 0;
        if (XMLString != null && !XMLString.isEmpty())
        {

            InputStream InputStream1 = new ByteArrayInputStream(XMLString.getBytes(StandardCharsets.UTF_8));

            List<GDBList> lists = new LinkedList<>();

            GDBList list = null;
            try
            {
                list = GDBList.Build(0, InputStream1, _DbBrowser);
                if (list != null)
                {
                    lists.add(list);
                }
            } catch (IOException e)
            {

                Log.e("IOException", "SetLists err: " + e.getMessage());
                //  e.printStackTrace();
            } catch (XmlPullParserException e)
            {

                Log.e("XmlPullParserException", "SetLists err: " + e.getMessage());
                // e.printStackTrace();
            } catch (Exception ex)
            {
                Log.e("Exception", "SetLists err: " + ex.getMessage());
            }

            _MixedList = GDBList.Mix(lists);
        }
        else
        {
            Log.e("SetLists_Error", "SetLists err: " + getString(R.string.nothing_found));
        }


        if (_MixedList != null)
        {
            return 1;
        }
        else
        {
            return 0;

        }

    }

    /**
     * Display a list of downloadable collections
     *
     * @param list GDBList
     */
    protected void showGDBList(GDBList list)
    {
        GDBListAdaptor1 = new GDBListAdaptor(list, this);
        recyclerViewCollection.setAdapter(GDBListAdaptor1);
        recyclerViewCollection.scrollToPosition(0);
    }


    private class DownloadXmlTask extends AsyncTask<String, Integer, Integer>
    {


        // onPreExecute called before the doInBackgroud start for display
        // progress dialog.
        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();

            if (_MixedList != null)
            {
                _MixedList = null;
            }

            if (GDBListAdaptor1 != null)
            {
                GDBListAdaptor1.notifyDataSetChanged();
            }

            if (!simpleSwipeRefreshLayout.isRefreshing())
            {
                simpleSwipeRefreshLayout.setRefreshing(true);
            }
        }

        @Override
        protected Integer doInBackground(String... urls)
        {

            try
            {
                String result = DownloadFromUrl.downloadDataFromUrl(urls[0], false);
                int res2 = SetLists(result);
                publishProgress(100);
                return res2;
            } catch (Exception e)
            {
                Log.e("DownloadFromUrl", "doInBackground: " + e.getMessage());
                return -1;
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values)
        {
            super.onProgressUpdate(values);

        }

        @Override
        protected void onPostExecute(Integer result)
        {

            if (simpleSwipeRefreshLayout.isRefreshing())
            {
                simpleSwipeRefreshLayout.setRefreshing(false);
            }
            if (result == 1)
            {
                showGDBList(_MixedList);
            }
            else if(result == -1)
            {
                Toast.makeText(getBaseContext(), getString(R.string.err_list_audio), Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(ActivityCollection.this, getString(R.string.nothing_found), Toast.LENGTH_SHORT).show();
            }
        }
    }


    public void ShowSuccessDownloadToast(boolean DownloadAll)
    {
        if (DownloadAll)
        {
            Toast.makeText(this, getString(R.string.selected_collectons_added), Toast.LENGTH_SHORT).show();

        }
        else
        {
            Toast.makeText(this, getString(R.string.success_add), Toast.LENGTH_SHORT).show();
        }
    }

    public void DownloadFailToast()
    {
        Toast.makeText(this, getString(R.string.download_failed), Toast.LENGTH_SHORT).show();
    }
//Downloader
}

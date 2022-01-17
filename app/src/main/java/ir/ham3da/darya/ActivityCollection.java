package ir.ham3da.darya;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.format.Formatter;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

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

import ir.ham3da.darya.adaptors.GDBListAdaptor;
import ir.ham3da.darya.adaptors.ScheduleGDB;
import ir.ham3da.darya.ganjoor.GDBInfo;
import ir.ham3da.darya.ganjoor.GDBList;
import ir.ham3da.darya.ganjoor.GanjoorDbBrowser;
import ir.ham3da.darya.utility.AppSettings;


import ir.ham3da.darya.utility.DownloadFromUrl;
import ir.ham3da.darya.utility.MyDialogs;
import ir.ham3da.darya.utility.SetLanguage;
import ir.ham3da.darya.utility.UtilFunctions;


import com.downloader.Error;

import com.downloader.OnDownloadListener;

import com.downloader.OnProgressListener;
import com.downloader.PRDownloader;
import com.downloader.PRDownloaderConfig;

import com.downloader.Progress;
import com.downloader.request.DownloadRequest;
import com.downloader.request.DownloadRequestBuilder;


public class ActivityCollection extends AppCompatActivity
{

    GanjoorDbBrowser _DbBrowser;
    GDBListAdaptor GDBListAdaptor1;
    RecyclerView recyclerViewCollection;
    SwipeRefreshLayout simpleSwipeRefreshLayout;
    MyDialogs MyDialogs1;
    String dlPath;
    SearchView searchView;
    boolean searchViewHasFocus = false;
    String TAG = "ir.ham3da.darya.ActivityCollection";
    private GDBList _MixedList = null;
    public int DlIndex = 0;
    public int sumDownloaded = 0;

    ImageButton cancel_downloads;
    TextView progress_text;
    TextView progress_description;
    TextView progress_text1;
    TextView progress_text2;
    ProgressBar progress_bar;
    List<ScheduleGDB> scheduleGDBList;
    ActionMode actionMode;

    /**
     * نام فایلهای مجموعه های پیش فرض
     */
    private final String[] _Lists_Url = {"http://i.ganjoor.net/android/androidgdbs.xml"};

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

    private final ActionMode.Callback callback = new ActionMode.Callback()
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


        RelativeLayout downloadRelativelayout = findViewById(R.id.download_RelativeLayout);

        if (downloadRelativelayout.isShown())
        {
            downloadRelativelayout.setVisibility(View.GONE);
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
            if (GDBListAdaptor1.isSelected(i) && !GDBListAdaptor1.getBookExist(i)
                    || GDBListAdaptor1.isSelected(i) && GDBListAdaptor1.getBookUpdateAvailable(i))
            {
                ScheduleGDB scheduleBook = GDBListAdaptor1.getScheduleBook(i);
                scheduleGDBList.add(scheduleBook);
            }
        }
        sumDownloaded = 0;
        downloadBooks(scheduleGDBList);
    }

    public void showSuccessDownloadToast(Boolean DownloadAll)
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

    public void downloadFailToast()
    {
        Toast.makeText(this, getString(R.string.download_failed), Toast.LENGTH_SHORT).show();
    }

    public void downloadBooks(List<ScheduleGDB> scheduleBookList1)
    {
        RelativeLayout downloadRelatively = findViewById(R.id.download_RelativeLayout);

        int totalDownloads = scheduleBookList1.size();
        if (totalDownloads > 0)
        {
            if (downloadRelatively.getVisibility() != View.VISIBLE)
            {
                downloadRelatively.setVisibility(View.VISIBLE);
            }
            progress_bar.setProgress(0);
            progress_text.setText("");
            progress_description.setText("");
            progress_text1.setText("");
            progress_text2.setText("");
            cancel_downloads.setOnClickListener(v -> {
                        PRDownloader.cancelAll();
                        downloadRelatively.setVisibility(View.GONE);
                    }
            );

            if (totalDownloads > sumDownloaded)
            {
                ScheduleGDB scheduleGDB = scheduleBookList1.get(sumDownloaded);
                sumDownloaded++;
                String fileName = scheduleGDB._FileName;
                String des = String.format(Locale.getDefault(), "%d", sumDownloaded) + " / " + String.format(Locale.getDefault(), "%d", totalDownloads);

                String dl_ulr = scheduleGDB._URL;

                DownloadRequestBuilder downloadRequestBuilder = PRDownloader.download(dl_ulr, dlPath, scheduleGDB._FileName);
                int finalSum = sumDownloaded;
                DownloadRequest downloadRequest = downloadRequestBuilder.build();
                downloadRequest.setDownloadId(scheduleGDB._Pos);

                downloadRequest.setOnProgressListener(progress -> {
                    int percent = (int) Math.round(((double) progress.currentBytes / (double) progress.totalBytes) * 100);
                    if (percent > 0)
                    {
                        String formatBytesCopied = getString(R.string.file_received) + " " + Formatter.formatFileSize(ActivityCollection.this, progress.currentBytes);

                        String formatFileLength = getString(R.string.file_size) + " " + Formatter.formatFileSize(ActivityCollection.this, progress.totalBytes);
                        String progressText = formatBytesCopied + " / " + formatFileLength;
                        progress_bar.setProgress(percent);
                        String percentStr = String.format(Locale.getDefault(), "%d", percent) + " %";
                        progress_text.setText(percentStr);
                        progress_description.setText(des);
                        progress_text1.setText(formatBytesCopied);
                        progress_text2.setText(formatFileLength);
                    }
                }).start(new OnDownloadListener()
                {
                    @Override
                    public void onDownloadComplete()
                    {
                        String filePath = dlPath + "/" + fileName;

                        File file = new File(filePath);
                        if (file.exists())
                        {
                            if (scheduleGDB._DoUpdate)
                            {
                                _DbBrowser.DeletePoet(scheduleGDB._PoetID);
                            }
                            boolean imported = _DbBrowser.ImportGdb(filePath, scheduleGDB._Update_info);
                            if (imported)
                            {
                                GDBListAdaptor1.notifyNewImported(scheduleGDB._Pos, scheduleGDB._PoetID);
                                try
                                {
                                    file.delete();
                                } catch (Exception ex)
                                {
                                    Log.e(TAG, "onPostExecute: " + ex.getMessage());
                                }
                                App globalVariable = (App) getApplicationContext();
                                globalVariable.setUpdatePoetList(true);
                            }
                            downloadBooks(scheduleGDBList);
                        }
                        if (finalSum >= totalDownloads)
                        {
                            boolean multi_dl = false;
                            if (totalDownloads > 1)
                            {
                                multi_dl = true;
                            }
                            GDBListAdaptor1.notifyDataSetChanged();
                            showSuccessDownloadToast(multi_dl);
                            downloadRelatively.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onError(Error error)
                    {
                        Log.e("DownloadAudioTask", "ResponseCode: " +
                                error.getResponseCode() + ", get ServerError Message: " + error.getServerErrorMessage() +
                                ", get Connection Exception:" + error.getConnectionException().getMessage());

                        downloadFailToast();
                        PRDownloader.cancel(scheduleGDB._Pos);
                        downloadRelatively.setVisibility(View.GONE);
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
        Dialog yesNoDialog = MyDialogs1.YesNoDialog(ques, ContextCompat.getDrawable(this, R.drawable.ic_delete_white_24dp), true);
        Button noBtn = yesNoDialog.findViewById(R.id.noBtn);
        noBtn.setOnClickListener(v -> {
                    yesNoDialog.dismiss();
                }
        );
        Button yesBtn = yesNoDialog.findViewById(R.id.yesBtn);
        yesBtn.setOnClickListener(v -> {
                    yesNoDialog.dismiss();

                    GDBInfo[] ganjoorBookInfoListArray = gdbInfos.toArray(new GDBInfo[0]);

                    DeleteBookFilesTask deleteBookFilesTask = new DeleteBookFilesTask(ganjoorBookInfoListArray);
                    deleteBookFilesTask.execute();
                }
        );


        yesNoDialog.show();
    }


    private class DeleteBookFilesTask
    {

        GDBInfo[] gdbInfoArrayList;
        Thread deleteThread;
        Handler mainHandler = new Handler(Looper.getMainLooper());

        public DeleteBookFilesTask(GDBInfo[] ganjoorBookInfo1)
        {
            gdbInfoArrayList = ganjoorBookInfo1;
            cancel_downloads.setOnClickListener(v -> {
                this.deleteThread.interrupt();
            });
        }


        public void execute()
        {
            RelativeLayout downloadRelatively = findViewById(R.id.download_RelativeLayout);

            downloadRelatively.setVisibility(View.VISIBLE);
            progress_bar.setProgress(0);
            progress_text.setText("");
            progress_description.setText("");
            progress_text1.setText("");
            progress_text2.setText("");
            deleteThread = new Thread(this::doInBackground);
            deleteThread.start();


        }

        private void doInBackground()
        {
            try
            {
                int count = gdbInfoArrayList.length;
                for (int i = 0; i < count; i++)
                {
                    GDBInfo gdbInfo = gdbInfoArrayList[i];
                    GDBListAdaptor1.deleteItemMarked(gdbInfo);
                    publishProgress(i + 1, count);
                }
                completed();
            } catch (Exception ex)
            {
                Log.e("xml_Dl", "doInBackground: " + ex.getMessage());
            }

        }

        private void publishProgress(int total, int current)
        {

            mainHandler.post(() -> {
                if (progress_bar != null)
                {
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
            });

        }

        private void completed()
        {
            runOnUiThread(() -> {
                RelativeLayout downloadRelativeLayout = findViewById(R.id.download_RelativeLayout);
                downloadRelativeLayout.setVisibility(View.GONE);
                GDBListAdaptor1.notifyDataSetChanged();
            });

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
            int id = searchView.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
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
                searchInRecycleView(query.trim());
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText)
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
        if (android.R.id.home == id)
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
        else if(R.id.reload_all == id)
        {
            reloadRecycleView();
        }
        return super.onOptionsItemSelected(item);
    }


    private void loadItems()
    {
        DownloadXmlTask downloadXmlTask = new DownloadXmlTask(_Lists_Url[0]);
        downloadXmlTask.execute();


    }

    private Integer SetLists(String XMLString)
    {
        //val res = 0
        if (XMLString != null && !XMLString.isEmpty())
        {
            InputStream InputStream1 = new ByteArrayInputStream(XMLString.getBytes(StandardCharsets.UTF_8));

            List<GDBList> lists = new LinkedList<>();
            GDBList list;
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


    private class DownloadXmlTask
    {

        private String[] dlUrls;
        Thread downloadThread;
        Handler mainHandler = new Handler(Looper.getMainLooper());

        public DownloadXmlTask(String... urls)
        {
            dlUrls = urls;
        }


        // progress dialog.

        public void execute()
        {

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

            downloadThread = new Thread(() -> doInBackground(dlUrls));
            downloadThread.start();

        }

        private void doInBackground(String[] urls)
        {
            try
            {
                String result = DownloadFromUrl.downloadDataFromUrl(urls[0], false);
                SetLists(result);
                complete(1);

            } catch (Exception ex)
            {
                Log.e("DownloadFromUrl", "doInBackground: " + ex.getMessage());
                complete(-1);
            }
        }

        private void complete(int result)
        {
            runOnUiThread(() -> {
                if (simpleSwipeRefreshLayout.isRefreshing())
                {
                    simpleSwipeRefreshLayout.setRefreshing(false);
                }
                if (result == 1)
                {
                    showGDBList(_MixedList);
                }
                else if (result == -1)
                {
                    Toast.makeText(ActivityCollection.this, getString(R.string.err_list_file), Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(ActivityCollection.this,
                            getString(R.string.nothing_found), Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

}
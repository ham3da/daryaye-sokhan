package ir.ham3da.darya;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;


import java.io.File;

import java.io.IOException;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ir.ham3da.darya.adaptors.AdaptorAudio;

import ir.ham3da.darya.adaptors.ScheduleAudio;

import ir.ham3da.darya.ganjoor.GanjoorDbBrowser;
import ir.ham3da.darya.ganjoor.GanjoorPoet;
import ir.ham3da.darya.tools.PermissionMediaType;
import ir.ham3da.darya.utility.AppSettings;


import ir.ham3da.darya.ganjoor.GanjoorAudioInfo;
import ir.ham3da.darya.utility.MyDialogs;
import ir.ham3da.darya.utility.PoemAudio;
import ir.ham3da.darya.utility.SetLanguage;
import ir.ham3da.darya.utility.UtilFunctions;


import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.downloader.Error;

import com.downloader.OnDownloadListener;

import com.downloader.PRDownloader;
import com.downloader.PRDownloaderConfig;

import com.downloader.request.DownloadRequest;
import com.downloader.request.DownloadRequestBuilder;

import ir.ham3da.darya.utility.EndlessRecyclerViewScrollListener;

public class ActivityAudioCollection extends AppCompatActivity
{

    private static final String TAG = "ActivityAudioCollection";
    GanjoorDbBrowser _DbBrowser;
    RecyclerView recycler_audio;
    SwipeRefreshLayout simpleSwipeRefreshLayout;
    MyDialogs MyDialogs1;
    public AdaptorAudio adaptorAudio;
    List<PoemAudio> existAudioList;
    List<GanjoorAudioInfo> listXmlItems;
    int poem_id, poet_id, dl_type, cate_id, page_number;
    int page_size = 100;
    boolean UseCaches;
    TextView no_item_textview;
    String dl_path;
    ActionMode actionMode;

    List<ScheduleAudio> scheduleAudioList;
    RelativeLayout download_RelativeLayout;

    ImageButton cancel_downloads;
    TextView progress_text;
    TextView progress_description;
    TextView progress_text1;
    TextView progress_text2;
    ProgressBar progress_bar;
    String mDataSource = "";

    private MediaPlayer mPlayer;

    private EndlessRecyclerViewScrollListener scrollListener;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        UtilFunctions.changeTheme(this, true);
        setContentView(R.layout.activity_audio_collection);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        setTitle(R.string.download_declaim);

        _DbBrowser = new GanjoorDbBrowser(this);
        recycler_audio = findViewById(R.id.recycler_audio);
        no_item_textview = findViewById(R.id.no_item_textview);
        download_RelativeLayout = findViewById(R.id.download_RelativeLayout);

        cancel_downloads = findViewById(R.id.cancel_downloads);
        progress_text = findViewById(R.id.progress_text);
        progress_description = findViewById(R.id.progress_description);
        progress_text1 = findViewById(R.id.progress_text1);
        progress_text2 = findViewById(R.id.progress_text2);
        progress_bar = findViewById(R.id.progress_bar);


        AppSettings.Init(this);
        dl_path = AppSettings.getAudioDownloadPath(this);

        poem_id = getIntent().getIntExtra("poem_id", 0);
        poet_id = getIntent().getIntExtra("poet_id", -1);
        cate_id = getIntent().getIntExtra("cate_id", -1);
        dl_type = getIntent().getIntExtra("dl_type", 1);
        page_number = 0;

        UseCaches = (dl_type == GanjoorAudioInfo.DOWNLOAD_POET_POEMS || dl_type == GanjoorAudioInfo.DOWNLOAD_CATE_POEMS);

        simpleSwipeRefreshLayout = findViewById(R.id.simpleSwipeRefreshLayout);


        simpleSwipeRefreshLayout.setOnRefreshListener(this::reloadItems);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recycler_audio.setLayoutManager(linearLayoutManager);

        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager)
        {
            @Override
            public void onLoadMore(final int page, int totalItemsCount, RecyclerView view)
            {
                //Log.e("onLoadMore", "onLoadMore: " + totalItemsCount);
                if (totalItemsCount >= page_size)
                {
                    loadItems();
                }

            }
        };
        recycler_audio.addOnScrollListener(scrollListener);

        if (UtilFunctions.isNetworkConnected(this))
        {
            loadItems();
        }
        else
        {
            finish();
            Toast.makeText(this, getString(R.string.internet_failed), Toast.LENGTH_SHORT).show();
        }
        MyDialogs1 = new MyDialogs(this);

        PRDownloaderConfig config = PRDownloaderConfig.newBuilder()
                .setDatabaseEnabled(true)
                .build();
        PRDownloader.initialize(getApplicationContext(), config);
    }

    private void reloadItems()
    {
        this.listXmlItems.clear();
        adaptorAudio.notifyDataSetChanged(); // or notifyItemRangeRemoved
        scrollListener.resetState();
        loadItems();
    }

    private void downloadMarkedAudio()
    {
        scheduleAudioList = new ArrayList<>();
        for (int i = 0; i < adaptorAudio.getItemCount(); i++)
        {
            if (adaptorAudio.isSelected(i) && !adaptorAudio.getAudioExist(i))
            {
                ScheduleAudio scheduleAudio = adaptorAudio.getScheduleAudio(i);
                scheduleAudioList.add(scheduleAudio);
            }
        }
        sumDownloaded = 0;
        downloadAudios(scheduleAudioList);

    }

    public boolean requestAudioFocus(AudioManager.OnAudioFocusChangeListener focusChangeListener, int streamType, int audioFocusGain)
    {
        int r;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            r = audioManager.requestAudioFocus(
                    new AudioFocusRequest.Builder(audioFocusGain)
                            .setAudioAttributes(
                                    new AudioAttributes.Builder()
                                            .setLegacyStreamType(streamType)
                                            .build())
                            .setOnAudioFocusChangeListener(focusChangeListener)
                            .build());
        }
        else
        {
            //deprecation
            r = audioManager.requestAudioFocus(focusChangeListener, streamType, audioFocusGain);
        }

        return r == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
    }

    AudioManager audioManager;

    AudioManager.OnAudioFocusChangeListener afChangeListener =
            focusChange -> {
                Log.e(TAG, "onAudioFocusChange: " + focusChange);
                switch (focusChange)
                {
                    case AudioManager.AUDIOFOCUS_LOSS:
                    case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                        pauseAudio();
                        break;
                    case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                        // Lower the volume, keep playing
                        break;
                    case AudioManager.AUDIOFOCUS_GAIN:
                        // Your app has been granted audio focus again
                        // Raise volume to normal, restart playback if necessary
                        break;
                }

            };

    public void pauseAudio()
    {
        if (mPlayer != null)
        {
            if (mPlayer.isPlaying())
            {
                Log.e(TAG, "pauseAudio: true");
                mPlayer.pause();
            }
        }
    }

    private void SetupAudio()
    {
        try
        {
            if (audioManager == null)
            {
                audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                requestAudioFocus(afChangeListener, AudioManager.STREAM_VOICE_CALL, AudioManager.AUDIOFOCUS_GAIN);
            }
        } catch (Exception ex)
        {
            Log.e(TAG, "SetupAudio: " + ex.getMessage());
        }
    }

    public void playAudioThread(GanjoorAudioInfo audioInfo)
    {
        Toast.makeText(this, R.string.please_wait, Toast.LENGTH_SHORT).show();
        Runnable thread = () -> playAudio(audioInfo);
        Thread t = new Thread(thread);
        t.start();
    }

    public void playAudio(GanjoorAudioInfo audioInfo)
    {
        SetupAudio();
        String filePath;

        String fileName = audioInfo.audio_fchecksum + ".mp3";

        if (audioInfo.exist)
        {
            filePath = dl_path + "/" + fileName;
        }
        else
        {
            filePath = audioInfo.audio_mp3;
        }

        try
        {
            if (!mDataSource.equals(filePath) || mPlayer == null)
            {

                if (mPlayer != null)
                {
                    mPlayer.stop();
                }
                mPlayer = new MediaPlayer();
                mPlayer.setDataSource(filePath);
                mDataSource = filePath;
                mPlayer.prepare();
                mPlayer.setOnPreparedListener(mp -> {
                });
                mPlayer.setOnErrorListener((mp, what, extra) ->
                {
                    Log.e(TAG, "setOnErrorListener, extra: " + extra + " what: " + what);
                    try
                    {
                        mPlayer.stop();
                    } catch (IllegalStateException e)
                    {
                        Log.e(TAG, "IllegalStateException " + e.getMessage());
                    }
                    mPlayer = null;
                    return true;
                });
            }
            mPlayer.start();

        } catch (IOException ex)
        {
            Log.e("mPlayer", "IOException: " + ex.getMessage());
        } catch (Exception e)
        {
            Log.e("mPlayer", "Exception: " + e.getMessage());
        }
    }

    public boolean isPlaying()
    {
        return mPlayer.isPlaying();
    }

    private void deleteMarkedAudio()
    {
        if (!adaptorAudio.checkAnyAudioIsSelected())
        {
            return;
        }

        List<GanjoorAudioInfo> ganjoorAudioInfoList2 = new ArrayList<>();

        for (int i = 0; i < adaptorAudio.getItemCount(); i++)
        {
            if (adaptorAudio.isSelected(i) && adaptorAudio.getAudioExist(i))
            {
                GanjoorAudioInfo ganjoorAudioInfo = adaptorAudio.getGanjoorAudioInfo(i);
                ganjoorAudioInfoList2.add(ganjoorAudioInfo);
            }
        }

        MyDialogs MyDialogs1 = new MyDialogs(this);

        String ques = getString(R.string.delete_all_au);
        final Dialog yesNoDialog = MyDialogs1.YesNoDialog(ques, ContextCompat.getDrawable(this, R.drawable.ic_delete_white_24dp), true);
        Button noBtn = yesNoDialog.findViewById(R.id.noBtn);
        noBtn.setOnClickListener(view -> yesNoDialog.dismiss());

        Button yesBtn = yesNoDialog.findViewById(R.id.yesBtn);
        yesBtn.setOnClickListener(view ->
        {
            yesNoDialog.dismiss();
            GanjoorAudioInfo[] ganjoorAudioInfoListArray = ganjoorAudioInfoList2.toArray(new GanjoorAudioInfo[0]);
            DeleteAudioFilesTask deleteAudioFilesTask = new DeleteAudioFilesTask(ganjoorAudioInfoListArray);
            deleteAudioFilesTask.execute();

        });
        yesNoDialog.show();

    }

    int sumDownloaded = 0;

    public void downloadAudios(List<ScheduleAudio> scheduleAudioList1)
    {
        int total_downloads = scheduleAudioList1.size();
        if (total_downloads > 0)
        {
            progress_bar.setProgress(0);
            progress_text.setText("0");
            progress_description.setText(R.string.starting_download);
            progress_text1.setText("...");
            progress_text2.setText("...");

            if (download_RelativeLayout.getVisibility() != View.VISIBLE)
            {
                download_RelativeLayout.setVisibility(View.VISIBLE);
            }


            cancel_downloads.setOnClickListener(v -> {
                PRDownloader.cancelAll();
                download_RelativeLayout.setVisibility(View.GONE);
            });

            if (total_downloads > sumDownloaded)
            {
                ScheduleAudio scheduleAudio = scheduleAudioList1.get(sumDownloaded);
                sumDownloaded++;

                String fileName = scheduleAudio._FileName;
                String des = String.format(Locale.getDefault(), "%d", sumDownloaded) + " / " + String.format(Locale.getDefault(), "%d", total_downloads);

                String dl_audio_ulr = scheduleAudio._URL;
                DownloadRequestBuilder downloadRequestBuilder = PRDownloader.download(dl_audio_ulr, dl_path, scheduleAudio._FileName);
                int finalSum = sumDownloaded;
                DownloadRequest downloadRequest = downloadRequestBuilder.build();
                downloadRequest.setDownloadId(scheduleAudio._Pos);
                downloadRequest.setOnProgressListener(progress ->
                {
                    int percent = (int) Math.round(((double) progress.currentBytes / (double) progress.totalBytes) * 100);
                    if (percent > 0)
                    {
                        String formatBytesCopied = getString(R.string.file_received) + " " + android.text.format.Formatter.formatFileSize(ActivityAudioCollection.this, progress.currentBytes);
                        String formatFileLength = getString(R.string.file_size) + " " + android.text.format.Formatter.formatFileSize(ActivityAudioCollection.this, progress.totalBytes);
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
                                File file = new File(dl_path + "/" + fileName);
                                if (file.exists())
                                {
                                    adaptorAudio.notifyNewDownloaded(scheduleAudio._Pos);
                                    downloadAudios(scheduleAudioList);
                                }

                                if (finalSum >= total_downloads)
                                {
                                    adaptorAudio.notifyDataSetChanged();
                                    ShowSuccessToast();
                                    download_RelativeLayout.setVisibility(View.GONE);
                                }
                            }

                            @Override
                            public void onError(Error error)
                            {
                                error.getConnectionException().printStackTrace();
                                Log.e("DownloadAudioTask", "ResponseCode: " +
                                        error.getResponseCode() + ", get ServerError Message: " + error.getServerErrorMessage() +
                                        ", get Connection Exception:" + error.getConnectionException().getMessage());
                                Log.e(TAG, "dl_audio_ulr: " + dl_audio_ulr);
                                DownloadFailToast();
                                PRDownloader.cancel(scheduleAudio._Pos);
                                download_RelativeLayout.setVisibility(View.GONE);
                                adaptorAudio.notifyDataSetChanged();
                            }
                        });
            }

        }
    }

    @Override
    protected void attachBaseContext(Context newBase)
    {
        super.attachBaseContext(SetLanguage.wrap(newBase));
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        Bungee.slideDown(this); //fire the slide left animation
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        int id = item.getItemId();
        if (id == android.R.id.home)
        {
            finish();
            Bungee.slideDown(this);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 2)
        {
            Log.d(TAG, "Write  External storage");
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                Log.v(TAG, "Permission: " + permissions[0] + " was " + grantResults[0]);
                downloadMarkedAudio();
            }
        }
    }

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
                    if (adaptorAudio != null)
                    {
                        if (adaptorAudio.checkAnyAudioIsSelected())
                        {
                            adaptorAudio.selectAllItem(false);
                            item.setIcon(R.drawable.ic_outline_library_add_check_24);
                        }
                        else
                        {
                            adaptorAudio.selectAllItem(true);
                            item.setIcon(R.drawable.ic_baseline_library_add_check_24_fill);
                        }

                    }
                    return true;
                case R.id.action_dl:
                    if (UtilFunctions.isWriteStoragePermissionGranted(ActivityAudioCollection.this, PermissionMediaType.AUDIO))
                    {
                        downloadMarkedAudio();
                    }
                    return true;

                case R.id.action_delete:
                    deleteMarkedAudio();
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

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        if (mPlayer != null)
        {
            if (mPlayer.isPlaying())
            {
                mPlayer.stop();
            }
            mPlayer.release();
        }
        mPlayer = null;
    }

    public String getAudioListUrl(int type)
    {
        String url = "";
        switch (type)
        {
            case GanjoorAudioInfo.DOWNLOAD_POEM:
                url = "https://ganjgah.ir/api/ganjoor/poem/" + this.poem_id + "/recitations";
                break;
            case GanjoorAudioInfo.DOWNLOAD_POET_POEMS:

                url = "https://ganjgah.ir/api/audio/published?PageNumber=" + this.page_number + "&PageSize=" + this.page_size + "&poetId=" + this.poet_id + "&catId=0";
                break;

            case GanjoorAudioInfo.DOWNLOAD_CATE_POEMS:
                url = "https://ganjgah.ir/api/audio/published?PageNumber=" + this.page_number + "&PageSize=" + this.page_size + "&poetId=0&catId=" + this.cate_id;
                break;
        }
        return url;
    }


    public void loadItems()
    {

        if (dl_type == GanjoorAudioInfo.DOWNLOAD_POEM)
        {
            existAudioList = _DbBrowser.getPoemAudios(poem_id);
        }

        this.page_number = calc_page_num();
        Log.e(TAG, " this.page_number: " + this.page_number);
        downloadJsonPoemAudios(getAudioListUrl(dl_type));
    }

    private int calc_page_num()
    {
        if (can_load_new_page())
        {
            double newPage = ((double) listXmlItems.size() / page_size) + 1;
            int newPage2 = (int) Math.ceil(newPage);
            return newPage2;
        }
        else
        {
            return 1;
        }
    }

    private boolean can_load_new_page()
    {
        return (listXmlItems != null && listXmlItems.size() >= page_size);
    }

    /**
     * check Audio file is Exist
     *
     * @param ganjoorAudioInfo ganjoorAudioInfo
     * @return boolean
     */
    private boolean checkExistAudio(GanjoorAudioInfo ganjoorAudioInfo)
    {
        boolean result = false;
        boolean existInDatabase = _DbBrowser.IsSoundExist(ganjoorAudioInfo.audio_fchecksum);
        boolean fileExist = _DbBrowser.checkAudioFileExist(ganjoorAudioInfo.audio_fchecksum, dl_path);

        if (existInDatabase)
        {
            if (fileExist)
            {
                result = true;
            }
            else
            {
                _DbBrowser.deleteSound(ganjoorAudioInfo.audio_fchecksum);
                result = false;
            }

        }
        else
        {
            if (fileExist)
            {
                reAddSoundData(ganjoorAudioInfo);
                result = true;
            }
        }

        return result;
    }


    private void reAddSoundData(GanjoorAudioInfo ganjoorAudioInfo)
    {
        if (!_DbBrowser.IsSoundExist(ganjoorAudioInfo.audio_fchecksum))
        {
            _DbBrowser.addToSound(ganjoorAudioInfo);
        }
    }


    private void downloadJsonPoemAudios(String jsonUrl)
    {

        if (!simpleSwipeRefreshLayout.isRefreshing())
        {
            simpleSwipeRefreshLayout.setRefreshing(true);
        }

        StringRequest request = new StringRequest(jsonUrl, string ->
        {
            parseAudioListJsonData(string);
            completedJsonDownload(1);
        }, volleyError -> {
            //dialog.dismiss();
            if (simpleSwipeRefreshLayout.isRefreshing())
            {
                simpleSwipeRefreshLayout.setRefreshing(false);
            }
            Toast.makeText(getBaseContext(), getString(R.string.err_list_audio), Toast.LENGTH_SHORT).show();
        });


        RequestQueue rQueue = Volley.newRequestQueue(ActivityAudioCollection.this);
        rQueue.add(request);
    }

    private void parseAudioListJsonData(String JsonString)
    {
        try
        {
            JSONArray jsonArray = new JSONArray(JsonString);
            if (jsonArray.length() > 0)
            {
                List<GanjoorAudioInfo> ganjoorAudioInfos1 = new ArrayList<>();
                int new_index = 0;
                if (listXmlItems != null)
                {
                    new_index = listXmlItems.size();
                }

                for (int i = 0; i < jsonArray.length(); i++)
                {
                    int audio_post_ID = jsonArray.getJSONObject(i).getInt("poemId");
                    int audio_order = jsonArray.getJSONObject(i).getInt("id");

                    String audio_xml = jsonArray.getJSONObject(i).getString("xmlText");

                    String audio_mp3 = jsonArray.getJSONObject(i).getString("mp3Url");
                    String audio_title = jsonArray.getJSONObject(i).getString("audioTitle");
                    String audio_artist = jsonArray.getJSONObject(i).getString("audioArtist");
                    String audio_artist_url = jsonArray.getJSONObject(i).getString("audioArtistUrl");
                    String audio_src = jsonArray.getJSONObject(i).getString("audioSrc");
                    String audio_src_url = jsonArray.getJSONObject(i).getString("audioSrcUrl");
                    String audio_guid = jsonArray.getJSONObject(i).getString("legacyAudioGuid");
                    String audio_fchecksum = jsonArray.getJSONObject(i).getString("mp3FileCheckSum");

                    int audio_mp3bsize = jsonArray.getJSONObject(i).getInt("mp3SizeInBytes");
                    int audio_oggbsize = 0;

                    Date audio_date = null;

                    String audio_date_str = jsonArray.getJSONObject(i).getString("publishDate");
                    SimpleDateFormat sfd = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                    try
                    {
                        audio_date = sfd.parse(audio_date_str);
                    } catch (ParseException e)
                    {
                        e.printStackTrace();
                    }

                    new_index++;
                    GanjoorAudioInfo ganjoorAudioInfo1 = new GanjoorAudioInfo(audio_post_ID, audio_order,
                            audio_xml, null, audio_mp3, audio_title, audio_artist,
                            audio_artist_url, audio_src, audio_src_url, audio_guid,
                            audio_fchecksum, audio_mp3bsize, audio_oggbsize, audio_date, false,
                            new_index, false);

                    int get_poet_id, get_cate_id;

                    switch (dl_type)
                    {
                        case GanjoorAudioInfo.DOWNLOAD_POET_POEMS:
                            get_poet_id = _DbBrowser.getPoemPoetId(audio_post_ID);
                            if (get_poet_id == poet_id)
                            {
                                ganjoorAudioInfo1.exist = checkExistAudio(ganjoorAudioInfo1);
                                ganjoorAudioInfos1.add(ganjoorAudioInfo1);
                            }
                            break;

                        case GanjoorAudioInfo.DOWNLOAD_CATE_POEMS:
                            get_cate_id = _DbBrowser.getPoemCateId(audio_post_ID);
                            if (get_cate_id == cate_id)
                            {
                                ganjoorAudioInfo1.exist = checkExistAudio(ganjoorAudioInfo1);
                                ganjoorAudioInfos1.add(ganjoorAudioInfo1);
                            }
                            break;
                        case GanjoorAudioInfo.DOWNLOAD_POEM:
                        default:
                            ganjoorAudioInfo1.exist = checkExistAudio(ganjoorAudioInfo1);
                            ganjoorAudioInfos1.add(ganjoorAudioInfo1);
                    }
                }


                no_item_textview.setVisibility(View.GONE);

                if (listXmlItems == null)
                {
                    Log.e(TAG, "parseAudioListJsonData: listXmlItems is null");
                    listXmlItems = ganjoorAudioInfos1;
                    GanjoorPoet ganjoorPoet = null;
                    if (dl_type == GanjoorAudioInfo.DOWNLOAD_POET_POEMS)
                    {
                        ganjoorPoet = _DbBrowser.getPoet(poet_id);
                    }
                    adaptorAudio = new AdaptorAudio(listXmlItems, this, dl_type, ganjoorPoet);
                    recycler_audio.setAdapter(adaptorAudio);
                    recycler_audio.scrollToPosition(0);
                }
                else
                {
                    Log.e(TAG, "parseAudioListJsonData: listXmlItems is not null");
                    listXmlItems.addAll(ganjoorAudioInfos1);
                    this.adaptorAudio.notifyItemRangeInserted(this.listXmlItems.size(), this.listXmlItems.size() - 1);
                }


            }
            else
            {
                Toast.makeText(this, getString(R.string.nothing_found), Toast.LENGTH_SHORT).show();
            }


        } catch (JSONException e)
        {
            e.printStackTrace();
            Toast.makeText(getBaseContext(), getString(R.string.err_list_audio), Toast.LENGTH_SHORT).show();


        }


    }

    protected void completedJsonDownload(Integer result)
    {
        Log.e(TAG, "completedJsonDownload: " + result);
        if (simpleSwipeRefreshLayout.isRefreshing())
        {
            simpleSwipeRefreshLayout.setRefreshing(false);
        }

    }


    public void ShowSuccessToast()
    {
        Toast.makeText(this, getString(R.string.download_success), Toast.LENGTH_SHORT).show();
    }

    public void DownloadFailToast()
    {
        Toast.makeText(this, getString(R.string.download_failed), Toast.LENGTH_SHORT).show();
    }


    private class DeleteAudioFilesTask
    {

        GanjoorAudioInfo[] ganjoorAudioInfos;
        Thread deleteThread;
        Handler mainHandler = new Handler(Looper.getMainLooper());

        public DeleteAudioFilesTask(GanjoorAudioInfo[] ganjoorAudioInfos1)
        {
            ganjoorAudioInfos = ganjoorAudioInfos1;
            cancel_downloads.setOnClickListener(v -> {
                this.deleteThread.interrupt();
            });
        }


        protected void execute()
        {
            download_RelativeLayout.setVisibility(View.VISIBLE);
            progress_bar.setProgress(0);
            progress_text.setText("");
            progress_description.setText("");
            progress_text1.setText("");
            progress_text2.setText("");

            deleteThread = new Thread(() -> doInBackground(ganjoorAudioInfos));
            deleteThread.start();

        }


        protected void doInBackground(GanjoorAudioInfo... ganjoorAudioInfos1)
        {
            try
            {
                int count = ganjoorAudioInfos1.length;
                for (int i = 0; i < count; i++)
                {
                    GanjoorAudioInfo scheduleAudio = ganjoorAudioInfos1[i];
                    adaptorAudio.deleteItemMarked(scheduleAudio);
                    publishProgress(i, count);
                }
                completed();

            } catch (Exception ex)
            {
                ex.printStackTrace();
                Log.e("xml_Dl", "doInBackground: " + ex.getMessage());
            }
        }


        protected void publishProgress(int total, int current)
        {
            //super.onProgressUpdate(values);

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


        protected void completed()
        {
            runOnUiThread(() -> {
                download_RelativeLayout.setVisibility(View.GONE);
                adaptorAudio.notifyDataSetChanged();
            });


        }

    }

}

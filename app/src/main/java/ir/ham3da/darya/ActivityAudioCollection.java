package ir.ham3da.darya;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.io.IOUtil;
import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayInputStream;
import java.io.File;

import java.io.IOException;
import java.io.InputStream;

import java.nio.charset.StandardCharsets;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import ir.ham3da.darya.adaptors.AdaptorAudio;

import ir.ham3da.darya.adaptors.ScheduleAudio;
import ir.ham3da.darya.ganjoor.GanjoorDbBrowser;
import ir.ham3da.darya.ganjoor.GanjoorPoet;
import ir.ham3da.darya.utility.AppSettings;
import ir.ham3da.darya.utility.AudioXmlParser;


import ir.ham3da.darya.ganjoor.GanjoorAudioInfo;
import ir.ham3da.darya.utility.DownloadFromUrl;
import ir.ham3da.darya.utility.MyDialogs;
import ir.ham3da.darya.utility.PoemAudio;
import ir.ham3da.darya.utility.SetLanguage;
import ir.ham3da.darya.utility.UtilFunctions;


import com.downloader.Error;

import com.downloader.OnDownloadListener;

import com.downloader.PRDownloader;
import com.downloader.PRDownloaderConfig;

import com.downloader.request.DownloadRequest;
import com.downloader.request.DownloadRequestBuilder;


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
    int poem_id, poet_id, dl_type, cate_id;
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

        UseCaches = (dl_type == GanjoorAudioInfo.DOWNLOAD_POET_POEMS  || dl_type == GanjoorAudioInfo.DOWNLOAD_CATE_POEMS);

        simpleSwipeRefreshLayout = findViewById(R.id.simpleSwipeRefreshLayout);


        simpleSwipeRefreshLayout.setOnRefreshListener(this::loadItems);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recycler_audio.setLayoutManager(linearLayoutManager);
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
                        pauseAudio();
                        break;

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
        final Dialog yesNoDialog = MyDialogs1.YesNoDialog(ques, getDrawable(R.drawable.ic_delete_white_24dp), true);
        Button noBtn = yesNoDialog.findViewById(R.id.noBtn);
        noBtn.setOnClickListener(view -> yesNoDialog.dismiss());

        Button yesBtn = yesNoDialog.findViewById(R.id.yesBtn);
        yesBtn.setOnClickListener(view ->
        {
            yesNoDialog.dismiss();
            DeleteAudioFilesTask deleteAudioFilesTask = new DeleteAudioFilesTask();
            GanjoorAudioInfo[] ganjoorAudioInfoListArray = ganjoorAudioInfoList2.toArray(new GanjoorAudioInfo[0]);
            deleteAudioFilesTask.execute(ganjoorAudioInfoListArray);

        });
        yesNoDialog.show();

    }

    int sumDownloaded = 0;

    public void downloadAudios(List<ScheduleAudio> scheduleAudioList1)
    {
        int total_downloads = scheduleAudioList1.size();
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
                ScheduleAudio scheduleAudio = scheduleAudioList1.get(sumDownloaded);
                sumDownloaded++;

                String fileName = scheduleAudio._FileName;
                String des = String.format(Locale.getDefault(), "%d", sumDownloaded) + " / " + String.format(Locale.getDefault(), "%d", total_downloads);

                DownloadRequestBuilder downloadRequestBuilder = PRDownloader.download(scheduleAudio._URL, dl_path, scheduleAudio._FileName);
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
                                Log.e("DownloadAudioTask", "ResponseCode: " +
                                        error.getResponseCode() + ", get ServerError Message: " + error.getServerErrorMessage() +
                                        ", get Connection Exception:" + error.getConnectionException().getMessage());
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

    public boolean isWriteStoragePermissionGranted()
    {
        if (Build.VERSION.SDK_INT >= 23)
        {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED)
            {
                Log.v(TAG, "Permission is granted2");
                return true;
            }
            else
            {

                Log.v(TAG, "Permission is revoked2");
                String[] permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
                ActivityCompat.requestPermissions(this, permissions, 2);
                return false;
            }
        }
        else
        { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG, "Permission is granted2");
            return true;
        }
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
                    if (isWriteStoragePermissionGranted())
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
        if (type == GanjoorAudioInfo.DOWNLOAD_POEM)
        {
            return "http://a.ganjoor.net/?p=" + this.poem_id;
        }
        else
        {
            return "http://a.ganjoor.net";
        }
    }


    public void loadItems()
    {

        if (dl_type == GanjoorAudioInfo.DOWNLOAD_POEM)
        {
            existAudioList = _DbBrowser.getPoemAudios(poem_id);
        }

        DownloadXmlTask downloadXmlTask = new DownloadXmlTask();
        downloadXmlTask.execute(getAudioListUrl(dl_type));
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
        if (dl_type == GanjoorAudioInfo.DOWNLOAD_POEM)
        {
            for (PoemAudio PoemAudio1 : existAudioList)
            {
                if (PoemAudio1.poemID == ganjoorAudioInfo.audio_post_ID && ganjoorAudioInfo.audio_fchecksum.equals(PoemAudio1.fchksum))
                {
                    result = true;
                    break;
                }
            }
        }
        else
        {
            result = _DbBrowser.checkAudioFileExist(ganjoorAudioInfo.audio_fchecksum, dl_path);
        }

        return result;
    }


    private Integer SetLists(String XMLString)
    {
        int res = 0;
        if (XMLString != null && !XMLString.isEmpty())
        {
            // String dl_path = AppSettings.getAudioDownloadPath(this);
            InputStream InputStream1 = new ByteArrayInputStream(XMLString.getBytes(StandardCharsets.UTF_8));
            AudioXmlParser audioXmlParser = new AudioXmlParser(this);
            List<GanjoorAudioInfo> newAudioList = new ArrayList<>();
            List<GanjoorAudioInfo> newAudioList2 = new ArrayList<>();

            try
            {
                newAudioList = audioXmlParser.parseXML(InputStream1);
                int get_poet_id, get_cate_id;
                int new_index = 0;

                for (GanjoorAudioInfo audioInfo : newAudioList)
                {
                    // String dl_fileName = URLUtil.guessFileName(audioInfo.audio_mp3, null, null);
                    switch (dl_type)
                    {
                        case GanjoorAudioInfo.DOWNLOAD_POET_POEMS:
                            get_poet_id = _DbBrowser.getPoemPoetId(audioInfo.audio_post_ID);
                            if (get_poet_id == poet_id)
                            {
                                new_index++;
                                audioInfo.Index = new_index;
                                audioInfo.exist = checkExistAudio(audioInfo);
                                newAudioList2.add(audioInfo);
                            }
                            break;

                        case GanjoorAudioInfo.DOWNLOAD_CATE_POEMS:
                            get_cate_id = _DbBrowser.getPoemCateId(audioInfo.audio_post_ID);
                            if(get_cate_id == cate_id)
                            {
                                new_index++;
                                audioInfo.Index = new_index;
                                audioInfo.exist = checkExistAudio(audioInfo);
                                newAudioList2.add(audioInfo);
                            }
                            break;
                        case GanjoorAudioInfo.DOWNLOAD_POEM:
                        default:
                            audioInfo.exist = checkExistAudio(audioInfo);
                            newAudioList2.add(audioInfo);
                    }

                }

                if (newAudioList2.size() > 0)
                {
                    no_item_textview.setVisibility(View.GONE);
                    listXmlItems = newAudioList2;
                    if (adaptorAudio != null)
                    {
                        //adaptorAudio.notifyDataSetChanged();
                        res = 2;
                    }
                    else
                    {
                        GanjoorPoet ganjoorPoet = null;
                        if (dl_type == GanjoorAudioInfo.DOWNLOAD_POET_POEMS)
                        {
                            ganjoorPoet = _DbBrowser.getPoet(poet_id);
                        }


                        adaptorAudio = new AdaptorAudio(listXmlItems, this, dl_type, ganjoorPoet);
                        //recycler_audio.setAdapter(adaptorAudio);
                        res = 1;
                    }

                    //recycler_audio.scrollToPosition(0);

                }
                else
                {
                    no_item_textview.setVisibility(View.VISIBLE);
                    Toast.makeText(this, getString(R.string.nothing_found), Toast.LENGTH_SHORT).show();
                    res = 0;
                }

            } catch (IOException e)
            {

                Log.e("IOException", "parseXML err: " + e.getMessage());
                res = -1;

            } catch (XmlPullParserException e)
            {

                Log.e("XmlPullParserException", "parseXML err: " + e.getMessage());
                res = -2;
            } catch (Exception ex)
            {
                Log.e("Exception", "parseXML err: " + ex.getMessage());
                res = -3;
            }

        }
        return res;
    }


    public void ShowSuccessToast()
    {
        Toast.makeText(this, getString(R.string.download_success), Toast.LENGTH_SHORT).show();
    }

    public void DownloadFailToast()
    {
        Toast.makeText(this, getString(R.string.download_failed), Toast.LENGTH_SHORT).show();
    }

    private class DeleteAudioFilesTask extends AsyncTask<GanjoorAudioInfo, Integer, Integer>
    {


        public DeleteAudioFilesTask()
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
        protected Integer doInBackground(GanjoorAudioInfo... ganjoorAudioInfos)
        {
            try
            {
                int count = ganjoorAudioInfos.length;
                for (int i = 0; i < count; i++)
                {

                    GanjoorAudioInfo scheduleAudio = ganjoorAudioInfos[i];
                    adaptorAudio.deleteItemMarked(scheduleAudio);
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
            adaptorAudio.notifyDataSetChanged();

        }
    }

    private class DownloadXmlTask extends AsyncTask<String, Integer, Integer>
    {

        // onPreExecute called before the doInBackgroud start for display
        // progress dialog.
        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();

            if (adaptorAudio != null)
            {
                adaptorAudio.notifyDataSetChanged();
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
                String res = DownloadFromUrl.downloadDataFromUrl(urls[0], UseCaches);
                int res2 = SetLists(res);
                publishProgress(100);
                return res2;
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

        }

        // onPostExecute displays the results of the doInBackgroud and also we
        // can hide progress dialog.
        @Override
        protected void onPostExecute(Integer result)
        {
            if (simpleSwipeRefreshLayout.isRefreshing())
            {
                simpleSwipeRefreshLayout.setRefreshing(false);
            }

            if (result == 1)
            {
                recycler_audio.setAdapter(adaptorAudio);
                recycler_audio.scrollToPosition(0);
            }
            else if (result == 2)
            {
                adaptorAudio.notifyDataSetChanged();
                recycler_audio.scrollToPosition(0);
            }
            else if(result == -1)
            {
                Toast.makeText(getBaseContext(), getString(R.string.err_list_audio), Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(getBaseContext(), getString(R.string.nothing_found), Toast.LENGTH_SHORT).show();
            }
        }
    }

}

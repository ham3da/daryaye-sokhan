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
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import ir.ham3da.darya.adaptors.AdaptorAudio;
import ir.ham3da.darya.adaptors.ScheduleAudio;
import ir.ham3da.darya.ganjoor.GanjoorDbBrowser;
import ir.ham3da.darya.utility.AppSettings;
import ir.ham3da.darya.utility.AudioXmlParser;
import ir.ham3da.darya.utility.DownloadAudioTask;

import ir.ham3da.darya.ganjoor.GanjoorAudioInfo;
import ir.ham3da.darya.utility.MyDialogs;
import ir.ham3da.darya.utility.PoemAudio;
import ir.ham3da.darya.utility.SetLanguage;
import ir.ham3da.darya.utility.UtilFunctions;
import ir.ham3da.darya.utility.DownloadFromUrl;

public class ActivityAudioCollection extends AppCompatActivity {

    GanjoorDbBrowser _DbBrowser;

    RecyclerView recycler_audio;

    SwipeRefreshLayout simpleSwipeRefreshLayout;
    MyDialogs MyDialogs1;

    public AdaptorAudio adaptorAudio;
    List<PoemAudio> existAudioList;

    List<GanjoorAudioInfo> listXmlItems;

    int poem_id;

    TextView no_item_textview;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(SetLanguage.wrap(newBase));
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

        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            Bungee.slideDown(this);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_collection);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        setTitle(R.string.download_declaim);

        _DbBrowser = new GanjoorDbBrowser(this);
        recycler_audio = findViewById(R.id.recycler_audio);
        no_item_textview = findViewById(R.id.no_item_textview);

        AppSettings.Init(this);

        poem_id = getIntent().getIntExtra("poem_id", 0);

        simpleSwipeRefreshLayout = findViewById(R.id.simpleSwipeRefreshLayout);

        simpleSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadItems();
            }
        });


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recycler_audio.setLayoutManager(linearLayoutManager);


        if (UtilFunctions.isNetworkConnected(this)) {
            loadItems();
        } else {
            finish();
            Toast.makeText(this, getString(R.string.internet_failed), Toast.LENGTH_SHORT).show();
        }

        MyDialogs1 = new MyDialogs(this);

    }

    public String getAudioListUrl() {
        return "http://a.ganjoor.net/?p=" + this.poem_id;
    }

    public void loadItems()
    {
        existAudioList = _DbBrowser.getPoemAudios(poem_id);
        DownloadXmlTask downloadXmlTask = new DownloadXmlTask();
        downloadXmlTask.execute(getAudioListUrl());
    }


    private class DownloadXmlTask extends AsyncTask<String, Void, String> {

        // onPreExecute called before the doInBackgroud start for display
        // progress dialog.
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            if (adaptorAudio != null) {
                adaptorAudio.notifyDataSetChanged();
            }

            if (!simpleSwipeRefreshLayout.isRefreshing()) {
                simpleSwipeRefreshLayout.setRefreshing(true);
            }
        }

        @Override
        protected String doInBackground(String... urls) {

            try {
                return DownloadFromUrl.downloadDataFromUrl(urls[0]);
            } catch (Exception ex)
            {
                ex.printStackTrace();
                Log.e("xml_Dl", "doInBackground: "+ex.getMessage() );
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



private  boolean checkExistAudio(GanjoorAudioInfo ganjoorAudioInfo)
{
    boolean result = false;

    for(PoemAudio PoemAudio1 : existAudioList)
    {
      if( PoemAudio1.poemID == ganjoorAudioInfo.audio_post_ID && ganjoorAudioInfo.audio_fchecksum.equals(PoemAudio1.fchksum))
      {
          result =true;
          break;
      }

    }

    return result;
}


    private void SetLists(String XMLString)
    {
        if (XMLString != null && !XMLString.isEmpty()) {

           // String dl_path = AppSettings.getAudioDownloadPath(this);

            InputStream InputStream1 = new ByteArrayInputStream(XMLString.getBytes(StandardCharsets.UTF_8));

            AudioXmlParser audioXmlParser = new AudioXmlParser(this);
            List<GanjoorAudioInfo> newAudioList = new ArrayList<>();
            List<GanjoorAudioInfo> newAudioList2 = new ArrayList<>();

            try {

                newAudioList = audioXmlParser.parseXML(InputStream1);

                for( GanjoorAudioInfo audioInfo : newAudioList)
                {
                   // String dl_fileName = URLUtil.guessFileName(audioInfo.audio_mp3, null, null);

                  if(checkExistAudio(audioInfo))
                  {
                      audioInfo.exist = true;
                      newAudioList2.add(audioInfo);
                  }
                  else {
                      audioInfo.exist = false;
                      newAudioList2.add(audioInfo);
                  }
                }

                if (newAudioList2.size() > 0)
                {
                    no_item_textview.setVisibility(View.GONE);
                    listXmlItems = newAudioList2;
                    if (adaptorAudio != null) {
                        adaptorAudio.notifyDataSetChanged();
                    } else {
                        adaptorAudio = new AdaptorAudio(listXmlItems, this);
                        recycler_audio.setAdapter(adaptorAudio);
                    }
                    recycler_audio.scrollTo(0, 0);
                } else {

                    no_item_textview.setVisibility(View.VISIBLE);
                    Toast.makeText(this, getString(R.string.nothing_found), Toast.LENGTH_SHORT).show();
                }

            } catch (IOException e) {

                Log.e("IOException", "parseXML err: " + e.getMessage());

            } catch (XmlPullParserException e) {

                Log.e("XmlPullParserException", "parseXML err: " + e.getMessage());
            }
            catch (Exception ex)
            {
                Log.e("Exception", "parseXML err: " + ex.getMessage());
            }
        }
    }

    public void StartDownload(AdaptorAudio.ViewHolder holder, ScheduleAudio scheduleAudio) {

        if (UtilFunctions.isNetworkConnected(this)) {
            DownloadAudioTask downloadAudioTask = new DownloadAudioTask(holder, this, scheduleAudio);
            downloadAudioTask.execute(scheduleAudio._URL);

        } else {
            MyDialogs1.ShowWarningMessage(getString(R.string.internet_failed));
        }

    }

    public void ShowSuccessToast() {
        Toast.makeText(this, getString(R.string.download_success), Toast.LENGTH_SHORT).show();
    }
    public void DownloadFailToast() {
        Toast.makeText(this, getString(R.string.download_failed), Toast.LENGTH_SHORT).show();
    }

}

package ir.ham3da.darya;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;


import java.io.IOException;
import java.util.List;

import ir.ham3da.darya.adaptors.PoemVerseRecycleAdaptor;
import ir.ham3da.darya.ganjoor.GanjoorCat;
import ir.ham3da.darya.ganjoor.GanjoorDbBrowser;
import ir.ham3da.darya.ganjoor.GanjoorPoem;
import ir.ham3da.darya.ganjoor.GanjoorPoet;
import ir.ham3da.darya.ganjoor.GanjoorVerse;
import ir.ham3da.darya.ganjoor.GanjoorVerseB;
import ir.ham3da.darya.utility.AppSettings;
import ir.ham3da.darya.utility.MyDialogs;
import ir.ham3da.darya.utility.MyTouchListener;
import ir.ham3da.darya.utility.PoemAudio;
import ir.ham3da.darya.utility.SetLanguage;
import ir.ham3da.darya.utility.UtilFunctions;

public class ActivityPoem extends AppCompatActivity {

    int poem_id = 0;
    int vOrder;

    String cate_url;

    MyDialogs MyDialogs1;

    GanjoorDbBrowser GanjoorDbBrowser1;

    GanjoorCat GanjoorCat1, baseCategory;
    GanjoorPoet GanjoorPoet1;
    GanjoorPoem GanjoorPoem1;

    List<GanjoorVerseB> verseArrangement;

    private RecyclerView recyclerView;

    PoemVerseRecycleAdaptor adapter;
    List<GanjoorVerse> verseList;

    private AppBarLayout appBarLayout;

    CollapsingToolbarLayout toolbarLayout;

    UtilFunctions UtilFunctions1;

    FloatingActionButton fab;

    private String TAG = ActivityPoem.class.getSimpleName();
    float initialX, initialY;

    Animation animation;

    Boolean indexingStatus, from_search;

    String findStr;

    Context mContext;
    RelativeLayout audio_player_bar;
    SeekBar audio_seekbar;

    ImageView play_audio, audio_more_option, audio_close;
    List<PoemAudio> existAudioList;

    private MediaPlayer mPlayer;

    private static int oTime = 0, sTime = 0, eTime = 0;

    boolean MEDIA_IS_LOADED = false;
    private boolean AUDIO_DOWNLOAD_SHOW = false;

    private Handler hdlr = new Handler();


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("poem_id", poem_id);

    }

    @Override
    protected void onPause()
    {
        super.onPause();

    }

    @Override
    protected void onResume()
    {
        super.onResume();

        if (AUDIO_DOWNLOAD_SHOW && poem_id > 0) {
            AUDIO_DOWNLOAD_SHOW = false;
            existAudioList = GanjoorDbBrowser1.getPoemAudios(poem_id);
            if (existAudioList.size() > 0 && audio_player_bar.getVisibility() != View.VISIBLE) {
                audio_player_bar.setVisibility(View.VISIBLE);
            }

        }
    }

    public boolean requestAudioFocus(AudioManager.OnAudioFocusChangeListener focusChangeListener, int streamType, int audioFocusGain) {
        int r;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            r = audioManager.requestAudioFocus(
                    new AudioFocusRequest.Builder(audioFocusGain)
                            .setAudioAttributes(
                                    new AudioAttributes.Builder()
                                            .setLegacyStreamType(streamType)
                                            .build())
                            .setOnAudioFocusChangeListener(focusChangeListener)
                            .build());
        } else {
            //deprecation
            r = audioManager.requestAudioFocus(focusChangeListener, streamType, audioFocusGain);
        }

        return r == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
    }

    AudioManager audioManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        UtilFunctions.changeTheme(this);
        setContentView(R.layout.activity_poem);

        Toolbar toolbar = findViewById(R.id.toolbar_poem);
        setSupportActionBar(toolbar);
        fab = findViewById(R.id.fab_poem);

        mContext = this;


        UtilFunctions1 = new UtilFunctions(this);
        GanjoorDbBrowser1 = new GanjoorDbBrowser(this);
        MyDialogs1 = new MyDialogs(this);

        if (savedInstanceState != null) {
            poem_id = savedInstanceState.getInt("poem_id", 0);
        }
        else {
            poem_id = getIntent().getIntExtra("poem_id", 0);
        }

        vOrder = getIntent().getIntExtra("vOrder", 0);

        from_search = getIntent().getBooleanExtra("from_search", false);
        findStr = getIntent().getStringExtra("findStr");

        findStr = (findStr == null) ? "" : findStr;

        try {
            GanjoorPoem1 = GanjoorDbBrowser1.getPoem(poem_id);
            GanjoorCat1 = GanjoorDbBrowser1.getCat(GanjoorPoem1._CatID);
            GanjoorPoet1 = GanjoorDbBrowser1.getPoet(GanjoorCat1._PoetID);
            baseCategory = GanjoorDbBrowser1.getBaseCategory(GanjoorCat1._ID);
            verseList = GanjoorDbBrowser1.getVerses(poem_id);

            verseArrangement = UtilFunctions1.VerseArrangement(verseList);

            appBarLayout = findViewById(R.id.app_bar);
            toolbarLayout = findViewById(R.id.poem_toolbar_layout);
            UtilFunctions1.setupToolbarLayout(toolbarLayout, true);
            toolbarLayout.setExpandedTitleColor(Color.TRANSPARENT);


            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
            }
            TextView category_title = this.findViewById(R.id.category_title);
            TextView poem_title = this.findViewById(R.id.poem_title);
            TextView poet_name = this.findViewById(R.id.poet_name);
            TextView book_name = this.findViewById(R.id.book_name);

            if(GanjoorPoet1._CatID == GanjoorPoem1._CatID)
            {
                category_title.setText(R.string.poetry_collection);
                if (baseCategory != null)
                {
                    book_name.setText(R.string.poetry_collection);
                }

            }
            else {
                category_title.setText(GanjoorCat1._Text);
                if (baseCategory != null)
                {
                    book_name.setText(baseCategory._Text);
                }
            }

            poem_title.setText(GanjoorPoem1._Title);
            toolbarLayout.setTitle(GanjoorPoem1._Title);
            poet_name.setText(GanjoorPoet1._Name);


            checkIsFavorite();
            indexingStatus = AppSettings.getVerseListIndexStatus();
            recyclerView = findViewById(R.id.poem_recycler_view);
            adapter = new PoemVerseRecycleAdaptor(verseArrangement, GanjoorPoet1, this, indexingStatus, findStr);

            recyclerView.setAdapter(adapter);

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(linearLayoutManager);

            recyclerView.addOnItemTouchListener(new MyTouchListener(this,
                    recyclerView,
                    new MyTouchListener.OnTouchActionListener() {
                        @Override
                        public void onLeftSwipe(View view, int position) {
                            loadNextPreviousPoem(false);
                        }

                        @Override
                        public void onRightSwipe(View view, int position) {
                            loadNextPreviousPoem(true);

                        }

                        @Override
                        public void onClick(View view, int position) {

                            // Toast.makeText(mContext, ""+ view.getClass().getName() , Toast.LENGTH_SHORT).show();
                        }
                    }));

            if (!findStr.isEmpty()) {
                appBarLayout.setExpanded(false, true);

                int rowIndex = adapter.getFirstLineContainFindStr(vOrder);
                recyclerView.smoothScrollToPosition(rowIndex);

            }

            activityCreate();
            AppSettings.setLastPoemIdVisited(poem_id);


        } catch (Exception ex) {
            ex.printStackTrace();
            Toast.makeText(this, R.string.nothing_found, Toast.LENGTH_LONG).show();
            finish();
        }

    }

    private void activityCreate() {
        final ImageView verse_more_option = findViewById(R.id.verse_more_option);

        final PopupMenu popup = new PopupMenu(verse_more_option.getContext(), verse_more_option);
        popup.inflate(R.menu.poem_action_btn);

        popup.setOnMenuItemClickListener(item -> {
            Intent intent;
            switch (item.getItemId()) {

                case R.id.next_poem:
                    loadNextPreviousPoem(true);
                    break;
                case R.id.previous_poem:
                    loadNextPreviousPoem(false);
                    break;
                case R.id.verse_select_all:
                    adapter.selectAllItem(true);
                    break;
                case R.id.verse_un_select_all:
                    adapter.selectAllItem(false);
                    break;
                case R.id.verse_share:
                    adapter.shareVerses();
                    break;

                case R.id.verse_share_as_img:
                    sharePoemAsImage();
                    break;


                case R.id.verse_copy:
                    adapter.copyVerses();
                    break;

                case R.id.verse_declaim:


                    if (MEDIA_IS_LOADED) {
                        if (mPlayer.isPlaying()) {
                            mPlayer.pause();
                            mPlayer.stop();
                            MEDIA_IS_LOADED = false;

                            play_audio.setImageDrawable(getDrawable(R.drawable.ic_play_arrow_black_24dp));
                        }
                    }
                    AUDIO_DOWNLOAD_SHOW = true;
                    intent = new Intent(mContext, ActivityAudioCollection.class);
                    intent.putExtra("poem_id", poem_id);
                    startActivity(intent);
                    Bungee.slideUp(mContext);
                    break;
            }
            return false;
        });


        verse_more_option.setOnClickListener(v -> {
            //displaying the popup
            popup.show();
        });


        fab.setOnClickListener(view -> {
            FloatingActionButton fabObj = (FloatingActionButton) view;

            if (GanjoorPoem1._Faved) {
                GanjoorDbBrowser1.removeFromFavorites(poem_id);
                GanjoorPoem1._Faved = false;
            } else {
                GanjoorDbBrowser1.addToFavorites(poem_id);
                GanjoorPoem1._Faved = true;
            }
            checkIsFavorite();

        });

        audio_player_bar = findViewById(R.id.audio_player_bar);
        audio_close = findViewById(R.id.audio_close);
        play_audio = findViewById(R.id.play_audio);
        audio_seekbar = findViewById(R.id.audio_seekbar);

        audio_more_option = findViewById(R.id.audio_more_option);

        audio_close.setOnClickListener(view -> {
            audio_player_bar.setVisibility(View.GONE);
            pauseAudio();

        });


        audio_more_option.setOnClickListener(this::showAudioList);

        play_audio.setOnClickListener(view -> {


            if (MEDIA_IS_LOADED) {
                if (mPlayer.isPlaying()) {
                    mPlayer.pause();
                    play_audio.setImageDrawable(getDrawable(R.drawable.ic_play_arrow_black_24dp));
                } else {
                    mPlayer.start();
                    play_audio.setImageDrawable(getDrawable(R.drawable.ic_pause_black_24dp));
                }
            } else {
                PlayFirsAudio();
            }

        });

        // Set a change listener for seek bar
        audio_seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (mPlayer != null && b) {
                    mPlayer.seekTo(i);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }

    private void sharePoemAsImage() {
        try {
            List<String> stringList = adapter.getListOfVerses();
            String stringLineSeparated = TextUtils.join(System.lineSeparator(), stringList);

            Intent intent = new Intent(this, ActivityImageEdit.class);

            intent.putExtra("poemText", stringLineSeparated);
            intent.putExtra("poetName", "«" + GanjoorPoet1._Name + "»");
            startActivity(intent);
            Bungee.slideUp(this);
        } catch (Exception ex) {
            ex.printStackTrace();
            Toast.makeText(this, getString(R.string.error_occurred), Toast.LENGTH_SHORT).show();
        }

    }

    private void PlayFirsAudio() {
        existAudioList = GanjoorDbBrowser1.getPoemAudios(poem_id);
        if (existAudioList.size() > 0) {
            PoemAudio poemAudio = existAudioList.get(0);
            mPlayer = new MediaPlayer();
            mPlayer.setOnCompletionListener(mediaPlayer -> play_audio.setImageDrawable(getDrawable(R.drawable.ic_play_arrow_black_24dp)));
            PlayAudio(poemAudio);
        } else {
            ShowAudioCollection();
        }
    }

    private void PlayAudio(PoemAudio poemAudio)
    {
        SetupAudio();

        try {


            if (MEDIA_IS_LOADED && mPlayer != null) {
                mPlayer.stop();
            }
            mPlayer = new MediaPlayer();

            mPlayer.setOnCompletionListener(mediaPlayer -> play_audio.setImageDrawable(getDrawable(R.drawable.ic_play_arrow_black_24dp)));

            oTime = 0;
            mPlayer.setDataSource(poemAudio.filePath);
            mPlayer.prepare();
            mPlayer.start();
            play_audio.setImageDrawable(getDrawable(R.drawable.ic_pause_black_24dp));
            eTime = mPlayer.getDuration();
            sTime = mPlayer.getCurrentPosition();

            if (oTime == 0) {
                audio_seekbar.setMax(eTime);
                oTime = 1;
            }

            audio_seekbar.setProgress(sTime);
            MEDIA_IS_LOADED = true;

            hdlr.postDelayed(UpdateSongTime, 100);

        } catch (IOException ex) {
            Log.e("mPlayer", "IOException: " + ex.getMessage());
        } catch (Exception e) {
            Log.e("mPlayer", "Exception: " + e.getMessage());
        }
    }

    private void showAudioList(View view) {
        existAudioList = GanjoorDbBrowser1.getPoemAudios(poem_id);

        if (existAudioList.size() > 0) {
// setup the alert builder
            AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
            builder.setTitle(R.string.declaims);
// add a list
            String[] items = new String[existAudioList.size()];
            for (int i = 0; i < existAudioList.size(); i++) {
                items[i] = existAudioList.get(i).description;
            }

            builder.setIcon(R.drawable.ic_playlist_play_gray_24dp);
            builder.setItems(items, (dialog, which) -> {
                PoemAudio poemAudio = existAudioList.get(which);
                PlayAudio(poemAudio);

            });

// create and show the alert dialog
            AlertDialog dialog = builder.create();
            dialog.show();
        } else {
            Toast.makeText(this, getString(R.string.nothing_found), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(SetLanguage.wrap(newBase));
    }

    private void loadNextPreviousPoem(boolean next) {

        GanjoorPoem getGanjoorPoem;
        if (next) {
            getGanjoorPoem = GanjoorDbBrowser1.getNextPoem(GanjoorPoem1);
        } else {
            getGanjoorPoem = GanjoorDbBrowser1.getPrevPoem(GanjoorPoem1);
        }

        if (getGanjoorPoem != null) {
            if (MEDIA_IS_LOADED) {
                hdlr.removeCallbacks(UpdateSongTime);
                mPlayer.stop();
                play_audio.setImageDrawable(getDrawable(R.drawable.ic_play_arrow_black_24dp));
                MEDIA_IS_LOADED = false;
                audio_seekbar.setProgress(0);

            }

            AppSettings.setLastPoemIdVisited(getGanjoorPoem._ID);

            GanjoorPoem1 = getGanjoorPoem;
            GanjoorCat1 = GanjoorDbBrowser1.getCat(GanjoorPoem1._CatID);
            GanjoorPoet1 = GanjoorDbBrowser1.getPoet(GanjoorCat1._PoetID);

            baseCategory = GanjoorDbBrowser1.getBaseCategory(GanjoorCat1._ID);
            poem_id = GanjoorPoem1._ID;
            verseList = GanjoorDbBrowser1.getVerses(poem_id);

            verseArrangement = UtilFunctions1.VerseArrangement(verseList);

            findStr = "";
            adapter.setFindStr(findStr);

            adapter.setGanjoorVerseList(verseArrangement);

            TextView category_title = this.findViewById(R.id.category_title);
            TextView poem_title = this.findViewById(R.id.poem_title);

            TextView poet_name = this.findViewById(R.id.poet_name);
            TextView book_name = this.findViewById(R.id.book_name);


            if(GanjoorPoet1._CatID == GanjoorPoem1._CatID)
            {
                category_title.setText(R.string.poetry_collection);
            }
            else {
                category_title.setText(GanjoorCat1._Text);
            }


            toolbarLayout.setTitle(GanjoorPoem1._Title);

            poem_title.setText(GanjoorPoem1._Title);

            recyclerView.smoothScrollToPosition(0);

            recyclerView.startLayoutAnimation();

            checkIsFavorite();


        } else {
            if (next) {
                Toast.makeText(this, getString(R.string.end_of_list), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.begin_of_list), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void checkIsFavorite() {
        if (GanjoorPoem1._Faved) {
            fab.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(mContext, R.color.mark_fav)));

        } else {

            fab.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(mContext, R.color.white)));

        }
        animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.zoom_in);
        fab.startAnimation(animation);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Bungee.slideDown(this); //fire the slide left animation
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_poem, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        Intent intent;

        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                finish();
                Bungee.slideDown(this); //fire the slide left animation
                break;
            case R.id.action_share:
                adapter.shareVerses();
                break;
            case R.id.action_play:
                showAudioBar();
                break;


        }
        return super.onOptionsItemSelected(item);
    }

    private void showAudioBar() {
        if (audio_player_bar.getVisibility() != View.VISIBLE) {
            existAudioList = GanjoorDbBrowser1.getPoemAudios(poem_id);
            if (!MEDIA_IS_LOADED) {
                if (existAudioList.size() > 0) {
                    audio_player_bar.setVisibility(View.VISIBLE);

                    PoemAudio poemAudio = existAudioList.get(0);
                    mPlayer = new MediaPlayer();
                    mPlayer.setOnCompletionListener(mediaPlayer -> play_audio.setImageDrawable(getDrawable(R.drawable.ic_play_arrow_black_24dp)));

                    PlayAudio(poemAudio);
                    audio_player_bar.setVisibility(View.VISIBLE);
                } else {
                    ShowAudioCollection();
                }
            } else {
                if (mPlayer != null && !mPlayer.isPlaying()) {
                    audio_player_bar.setVisibility(View.VISIBLE);
                    mPlayer.start();
                    play_audio.setImageDrawable(getDrawable(R.drawable.ic_pause_black_24dp));
                }
            }

        }
    }


    private void ShowAudioCollection() {

        MyDialogs MyDialogs1 = new MyDialogs(mContext);
        final Dialog yesNoDialog = MyDialogs1.YesNoDialog(getString(R.string.no_donload_this_item), mContext.getDrawable(R.drawable.ic_cloud_download_white_24dp), true);

        Button noBtn = yesNoDialog.findViewById(R.id.noBtn);
        noBtn.setOnClickListener(view -> yesNoDialog.dismiss());

        Button yesBtn = yesNoDialog.findViewById(R.id.yesBtn);
        yesBtn.setOnClickListener(view -> {
            AUDIO_DOWNLOAD_SHOW = true;
            yesNoDialog.dismiss();
            pauseAudio();
            Intent intent = new Intent(mContext, ActivityAudioCollection.class);
            intent.putExtra("poem_id", poem_id);
            startActivity(intent);
            Bungee.slideUp(mContext);
        });
        yesNoDialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        hdlr.removeCallbacks(UpdateSongTime);
        if (MEDIA_IS_LOADED) {
            mPlayer.stop();
            mPlayer.release();
        }
        mPlayer = null;
    }


    private Runnable UpdateSongTime = new Runnable() {
        @Override
        public void run() {

            sTime = mPlayer.getCurrentPosition();
            audio_seekbar.setProgress(sTime);
            hdlr.postDelayed(this, 100);

        }
    };

    private void pauseAudio()
    {

        if (MEDIA_IS_LOADED && mPlayer != null)
        {
            if (mPlayer.isPlaying())
            {
                Log.e(TAG, "pauseAudio: true");
                mPlayer.pause();
                play_audio.setImageDrawable(getDrawable(R.drawable.ic_play_arrow_black_24dp));
            }
        }
    }

    private void setAudioVolume(float volume) {
        if (MEDIA_IS_LOADED && mPlayer != null) {
            if (mPlayer.isPlaying()) {
                mPlayer.setVolume(volume, volume);
            }
        }
    }

    private  void SetupAudio()
    {
        try {

            if(audioManager == null) {
                audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                requestAudioFocus(afChangeListener, AudioManager.STREAM_VOICE_CALL, AudioManager.AUDIOFOCUS_GAIN);
            }

        } catch (Exception ex) {
            Log.e(TAG, "SetupAudio: " + ex.getMessage());
        }
    }

    AudioManager.OnAudioFocusChangeListener afChangeListener =
            focusChange -> {
               Log.e(TAG, "onAudioFocusChange: " + focusChange);
                switch (focusChange) {
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


}

package ir.ham3da.darya;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd;
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAdLoadCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import ir.ham3da.darya.ganjoor.GanjoorDbBrowser;
import ir.ham3da.darya.ganjoor.GanjoorPoet;
import ir.ham3da.darya.ganjoor.GanjoorVerse;
import ir.ham3da.darya.utility.AppFontManager;
import ir.ham3da.darya.utility.AppSettings;
import ir.ham3da.darya.utility.MyDialogs;
import ir.ham3da.darya.utility.RateType;
import ir.ham3da.darya.utility.SetLanguage;
import ir.ham3da.darya.utility.UtilFunctions;

public class ActivityPuzzle extends AppCompatActivity implements OnUserEarnedRewardListener
{

    UtilFunctions UtilFunctions1;
    GanjoorDbBrowser GanjoorDbBrowser1;
    EditText editText_response;
    TextView textViewVerse, textViewShowResult, textview_plus_rate, textview_negative_rate;
    int parentCate;
    List<GanjoorVerse> verseList;
    String validResponse, vreseStringWithLine;
    ImageButton puzzle_new, puzzle_info, puzzle_check;
    MyDialogs MyDialogs1;
    String TAG = "ActivityPuzzle";
    ProgressBar progress_bar;
    MediaPlayer mp;
    boolean isAnswered = false;
    InputMethodManager inputManager;

   private RewardedInterstitialAd rewardedInterstitialAd ;

    int lastIncorrectAnsCount = 0;

   RelativeLayout relativeLayout2, ad_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UtilFunctions.changeTheme(this, true);
        setContentView(R.layout.activity_puzzle);
        AppSettings.Init(this);
        lastIncorrectAnsCount = AppSettings.getIncorrectAnsCount();

        progress_bar = findViewById(R.id.progress_bar);
        relativeLayout2 = findViewById(R.id.relativeLayout2);
        ad_layout =  findViewById(R.id.ad_layout);

        UtilFunctions1 = new UtilFunctions(this);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        GanjoorDbBrowser1 = new GanjoorDbBrowser(this);

        GanjoorDbBrowser1.createRateTable();

        setTitle(R.string.dont_forget_poetry);

        MyDialogs1 = new MyDialogs(this);

        editText_response = findViewById(R.id.your_response);
        textViewVerse = findViewById(R.id.textViewVerse);
        textViewShowResult = findViewById(R.id.show_result);
        textview_plus_rate = findViewById(R.id.textview_plus_rate);
        textview_negative_rate = findViewById(R.id.textview_negative_rate);


        puzzle_new = findViewById(R.id.puzzle_new);

        puzzle_new.setOnClickListener(v ->
        {
            editText_response.setText("");
            puzzle_check.setImageTintList(ContextCompat.getColorStateList(this, R.color.black));
            generateNewVerse();
        });
        puzzle_new.setOnLongClickListener(v -> {
            Toast.makeText(this, R.string.new1, Toast.LENGTH_SHORT).show();
            return true;
        });


        puzzle_info = findViewById(R.id.puzzle_info);
        puzzle_info.setOnLongClickListener(v -> {
            Toast.makeText(this, R.string.source, Toast.LENGTH_SHORT).show();
            return true;
        });

        puzzle_info.setOnClickListener(v -> {

            GanjoorVerse ganjoorVerse = verseList.get(0);
            int poem_id = ganjoorVerse._PoemID;

            String poemTree = GanjoorDbBrowser1.getPoemTree(poem_id);

            String html = "<strong>" + getString(R.string.source) + ": </strong>" + poemTree;
            MyDialogs1.ShowMessage(html, R.drawable.ic_help_24px);

        });

        puzzle_check = findViewById(R.id.puzzle_check);
        puzzle_check.setOnLongClickListener(v -> {
            Toast.makeText(this, R.string.check_answer, Toast.LENGTH_SHORT).show();
            return true;
        });

        puzzle_check.setOnClickListener(v ->
        {
            if (!isAnswered)
            {
                boolean check = checkAnswer(validResponse, editText_response.getText().toString());
                if (check)
                {
                    puzzle_check.setImageTintList(ContextCompat.getColorStateList(this, R.color.green_color_picker));
                    //play ok
                    if (!AppSettings.getGameSoundMute())
                    {
                        try
                        {
                            mp = MediaPlayer.create(getApplicationContext(), R.raw.valid2);
                            mp.start();
                        }
                        catch (Exception exception)
                        {
                            exception.printStackTrace();
                        }


                    }

                    textViewShowResult.setText(R.string.is_correct);
                    textViewShowResult.setTextColor(ContextCompat.getColor(this, R.color.green_color_picker));
                    GanjoorDbBrowser1.saveRate(1, RateType.PLUAS_RATE_COL, verseList.get(1)._PoemID, verseList.get(1)._Order);

                } else {

                    puzzle_check.setImageTintList(ContextCompat.getColorStateList(this,R.color.red500));
                    //play warning
                    if (!AppSettings.getGameSoundMute())
                    {
                        try
                        {
                            mp = MediaPlayer.create(getApplicationContext(), R.raw.error3);
                            mp.start();
                        }
                        catch (Exception exception)
                        {
                            exception.printStackTrace();
                        }

                    }

                    String str = getString(R.string.is_incorrect);
                    String html = str + "<br>" + getString(R.string.correct_answer) + " <strong>" + validResponse + "</strong>";

                    textViewShowResult.setText(UtilFunctions.fromHtml(html), TextView.BufferType.SPANNABLE);
                    textViewShowResult.setTextColor(ContextCompat.getColor(this, R.color.red500));

                    GanjoorDbBrowser1.saveRate(1, RateType.NAGATIVE_RATE_COL, verseList.get(1)._PoemID, verseList.get(1)._Order);
                    lastIncorrectAnsCount = lastIncorrectAnsCount + 1;
                    AppSettings.setIncorrectAnsCount(lastIncorrectAnsCount);

                    if(lastIncorrectAnsCount >=  AppSettings.default_max_incorrect_ans)
                    {
                       // showRewardAlert();
                        checkAllowedToPlay();
                    }

                }

                isAnswered = true;
                editText_response.setEnabled(false);
                loadPoints();
            }

        });


        Button btn_view_ad = findViewById(R.id.btn_view_ad);

        btn_view_ad.setOnClickListener(v -> {
            displayRewardedAd();
        });


        editText_response.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                puzzle_check.performClick();
                return true;
            }
            return false;
        });

        if (savedInstanceState != null) {
            parentCate = savedInstanceState.getInt("parentCate", 0);
        } else {
            parentCate = getIntent().getIntExtra("parentCate", 0);
        }
        generateNewVerse();

        if (Locale.getDefault().getLanguage().equals("fa")) {
            AppFontManager.setFont(this, textview_plus_rate, 0);
            AppFontManager.setFont(this, textview_negative_rate, 0);
        }

        loadPoints();

        createAndLoadRewardedAd(getString(R.string.reward_ad_unit_id), false);
        checkAllowedToPlay();
    }

    private void  checkAllowedToPlay()
    {
        if(lastIncorrectAnsCount >= 3 )
        {
            relativeLayout2.setAlpha(0.7f);
            relativeLayout2.setEnabled(false);
            textViewVerse.setEnabled(false);
            ad_layout.setVisibility(View.VISIBLE);

        }
        else
        {
            relativeLayout2.setEnabled(true);
            textViewVerse.setEnabled(true);
            relativeLayout2.setAlpha(1f);
            ad_layout.setVisibility(View.GONE);
        }
    }

    private void setProgressbarVisible(int visibility)
    {
        progress_bar.setVisibility(visibility);
    }

    public void createAndLoadRewardedAd(String adUnitId, boolean showAd)
    {

        setProgressbarVisible(View.VISIBLE);

        FullScreenContentCallback fullScreenContentCallback= new FullScreenContentCallback()
        {
            @Override
            public void onAdShowedFullScreenContent() {
                // Called when ad is shown.
                Log.d(TAG, "Ad was shown.");
                rewardedInterstitialAd = null;
                setProgressbarVisible(View.INVISIBLE);

            }

            @Override
            public void onAdFailedToShowFullScreenContent(AdError adError) {
                // Called when ad fails to show.
                Log.d(TAG, "onAdFailedToShowFullScreenContent: "+adError.getMessage());
                setProgressbarVisible(View.INVISIBLE);
            }

            @Override
            public void onAdDismissedFullScreenContent() {
                // Called when ad is dismissed.
                // Don't forget to set the ad reference to null so you
                // don't show the ad a second time.
                Log.d(TAG, "Ad was dismissed.");
                setProgressbarVisible(View.INVISIBLE);
            }
        };

        RewardedInterstitialAdLoadCallback rewardedInterstitialAdLoadCallback =  new RewardedInterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(RewardedInterstitialAd ad)
            {
                setProgressbarVisible(View.INVISIBLE);
                rewardedInterstitialAd = ad;
                Log.e(TAG, "onAdLoaded");
                rewardedInterstitialAd.setFullScreenContentCallback(fullScreenContentCallback);

                if(showAd)
                {
                    rewardedInterstitialAd.show(ActivityPuzzle.this,ActivityPuzzle.this);
                }
                Log.d(TAG, "onAdLoaded");


            }
            @Override
            public void onAdFailedToLoad(LoadAdError loadAdError) {
                Log.e(TAG, "onAdFailedToLoad: "+ loadAdError.getMessage());
                setRewardForFailed();
                setProgressbarVisible(View.INVISIBLE);
            }
        };


        AdRequest adRequest = new AdRequest.Builder().build();
        // Use the test ad unit ID to load an ad.
        RewardedInterstitialAd.load(ActivityPuzzle.this, adUnitId,
                adRequest,  rewardedInterstitialAdLoadCallback);
    }

    public void displayRewardedAd()
    {
        if (UtilFunctions.isNetworkConnected(this))
        {

            if (rewardedInterstitialAd != null)
            {
                rewardedInterstitialAd.show(ActivityPuzzle.this,ActivityPuzzle.this);

            }
            else
            {
                createAndLoadRewardedAd(getString(R.string.reward_ad_unit_id), true);
            }
        }
        else
        {
            Toast.makeText(this, getString(R.string.internet_failed), Toast.LENGTH_SHORT).show();
        }

    }


    private void setRewardForFailed()
    {
        if(lastIncorrectAnsCount >= AppSettings.default_max_incorrect_ans)
        {
            AppSettings.setIncorrectAnsCount(0);
            lastIncorrectAnsCount = 0;
            Toast.makeText(this, R.string.no_ads2, Toast.LENGTH_SHORT).show();
            rewardedInterstitialAd = null;
            checkAllowedToPlay();
        }
    }



    @Override
    public void onUserEarnedReward(@NonNull RewardItem rewardItem)
    {
        Log.e(TAG, "displayRewardedAd: "+rewardItem.getAmount() );
        AppSettings.setIncorrectAnsCount(0);
        lastIncorrectAnsCount = 0;
        Toast.makeText(this, R.string.congratulations, Toast.LENGTH_SHORT).show();
        checkAllowedToPlay();
    }


    private void loadPoints() {
        RateType rateType = GanjoorDbBrowser1.getRates();
        if (rateType != null) {

            String plus = String.format(Locale.getDefault(), "%d", rateType.plusRate);
            String negative = String.format(Locale.getDefault(), "%d", rateType.negativeRate);

            textview_plus_rate.setText(plus);
            textview_negative_rate.setText(negative);
        }
    }

    /**
     * check Answer Is Valid or No
     *
     * @param validAns Valid Answer
     * @param userAns  User Answer
     * @return boolean
     */
    private boolean checkAnswer(String validAns, String userAns) {

        validAns = validAns.replace("ك", "ک");
        validAns = validAns.replace("ي", "ی");
        validAns = validAns.replace("‌", " ");
        validAns = validAns.replace("...", "");

        userAns = userAns.replace("ك", "ک");
        userAns = userAns.replace("ي", "ی");
        userAns = userAns.replace("‌", " ");
        userAns = userAns.replace("...", "");

        return validAns.trim().equals(userAns.trim());
    }


    private void generateNewVerse() {

        textViewVerse.setText(R.string.loadingVers);
        textViewShowResult.setText("");
        progress_bar.setVisibility(View.VISIBLE);
        progress_bar.setIndeterminate(true);


        Runnable task1 = () ->
        {
            NewVerseAsyncTask newVerseAsyncTask = new NewVerseAsyncTask();
            newVerseAsyncTask.execute();
        };
        new Handler(Looper.getMainLooper()).postDelayed(task1, 1000);

    }



    private class NewVerseAsyncTask {



        protected void execute() {

            if (progress_bar.getVisibility() != View.VISIBLE)
            {
                progress_bar.setVisibility(View.VISIBLE);
                progress_bar.setIndeterminate(true);
            }

            textViewShowResult.setText("");
            editText_response.setText("");
            puzzle_new.setEnabled(false);
            puzzle_info.setEnabled(false);
            puzzle_check.setEnabled(false);

            Thread verseThread = new Thread(this::doInBackground);
            verseThread.start();
        }

        protected void doInBackground() {

            try {
                if (parentCate > 0) {
                    verseList = GanjoorDbBrowser1.getRandomPoemPuzzle(parentCate);
                } else {

                    GanjoorPoet rndPoet = GanjoorDbBrowser1.getRandomPoet();
                    verseList = GanjoorDbBrowser1.getRandomPoemPuzzle(rndPoet._CatID);
                }

                List<String> allVerse = new ArrayList<>();
                for (GanjoorVerse ganjoorVerse : verseList) {
                    allVerse.add(ganjoorVerse._Text.trim());
                }

                int rand = UtilFunctions.getRandomNumberInRange(0, allVerse.size() - 1);
                if (allVerse.get(rand).trim().length() <= 0 || allVerse.get(rand).equals("□")) {

                    for (int i = 0; i < allVerse.size(); i++) {
                        if (allVerse.get(i).length() > 0 && !allVerse.get(i).equals("□")) {
                            rand = i;
                            break;
                        }
                    }
                }

                //Log.e("APL", "rand: " + rand);
                String randLine = allVerse.get(rand);
                int wordRand = 0;

                String[] verseWords = randLine.split(" ");
                if (verseWords.length > 1) {
                    wordRand = UtilFunctions.getRandomNumberInRange(0, verseWords.length - 1);
                    validResponse = verseWords[wordRand];
                } else {
                    validResponse = verseWords[wordRand];
                }

                verseWords[wordRand] = "<font color=\"red\">.....</font>";
                String newWords = TextUtils.join(" ", verseWords);
                allVerse.set(rand, newWords);

                StringBuilder stringBuilder = new StringBuilder();

                vreseStringWithLine = TextUtils.join("<br>", allVerse);

                complete();

                Log.e("APL", "validResponse: " + validResponse);
            } catch (Exception ex) {
                ex.printStackTrace();
                complete();
            }

        }

        // onPostExecute displays the results of the doInBackgroud and also we
        // can hide progress dialog.
        protected void complete() {

            runOnUiThread(() -> {
                progress_bar.setVisibility(View.INVISIBLE);

                if(vreseStringWithLine != null) {
                    textViewVerse.setText(UtilFunctions.fromHtml(vreseStringWithLine), TextView.BufferType.SPANNABLE);
                }

                isAnswered = false;
                editText_response.setEnabled(true);
                editText_response.requestFocus();
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
                inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

                puzzle_new.setEnabled(true);
                puzzle_info.setEnabled(true);
                puzzle_check.setEnabled(true);
            });


        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Bungee.slideDown(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.puzzle_menu, menu);
        MenuItem item = menu.findItem(R.id.audio_mute);
        if (AppSettings.getGameSoundMute()) {
            item.setIcon(R.drawable.ic_volume_off_wihte_24dp);
        } else {

            item.setIcon(R.drawable.ic_volume_up_withe_24dp);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:

                finish();
                Bungee.slideDown(this);

                break;
            case R.id.action_refresh:
                puzzle_new.performClick();

                break;

            case R.id.audio_mute:
                if (AppSettings.getGameSoundMute()) {
                    AppSettings.setGameSoundMute(false);
                    item.setIcon(R.drawable.ic_volume_up_withe_24dp);
                } else {
                    AppSettings.setGameSoundMute(true);
                    item.setIcon(R.drawable.ic_volume_off_wihte_24dp);
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(SetLanguage.wrap(newBase));
    }

    @Override
    protected void onDestroy() {
        if (mp != null) {
            mp.release();
        }
        if(rewardedInterstitialAd != null)
        {
            rewardedInterstitialAd = null;
        }

        super.onDestroy();
    }
}

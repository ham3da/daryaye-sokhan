package ir.ham3da.darya;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.appbar.CollapsingToolbarLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ir.ham3da.darya.adaptors.SearchCustomAdapter;
import ir.ham3da.darya.ganjoor.GanjoorDbBrowser;
import ir.ham3da.darya.ganjoor.GanjoorPoet;
import ir.ham3da.darya.ganjoor.SearchResult;
import ir.ham3da.darya.utility.AppSettings;
import ir.ham3da.darya.utility.EndlessRecyclerViewScrollListener;
import ir.ham3da.darya.utility.SetLanguage;
import ir.ham3da.darya.utility.UtilFunctions;

public class ActivitySearch extends AppCompatActivity {
    UtilFunctions UtilFunctions1;
    CollapsingToolbarLayout toolbarLayout;
    SearchLimitsDialog searchLimitsDialog;
    GanjoorDbBrowser GanjoorDbBrowser1;

    List<SearchResult> searchResults1;

    EditText editTextSearch;
    int offset = 0;
    int per_page = 100;

    public int resCount = 0;

    private RecyclerView search_recycler_view;
    SearchCustomAdapter adapter;

    String strToFind;

    ProgressBar progressBar_loader;

    private EndlessRecyclerViewScrollListener scrollListener;
    InputMethodManager inputManager;
    private static final int REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UtilFunctions.changeTheme(this);
        setContentView(R.layout.activity_search);
        progressBar_loader = findViewById(R.id.progressBar_loader);

        UtilFunctions1 = new UtilFunctions(this);
        GanjoorDbBrowser1 = new GanjoorDbBrowser(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbarLayout = findViewById(R.id.toolbar_layout);

        editTextSearch = findViewById(R.id.editTextSearch);

        UtilFunctions1.setupToolbarLayout(toolbarLayout, false);

        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        searchLimitsDialog = new SearchLimitsDialog(this);
        setSearchLimitsText();

        editTextSearch.requestFocus();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);


        searchResults1 = new ArrayList<>();
        search_recycler_view = findViewById(R.id.search_recycler_view);

        adapter = new SearchCustomAdapter(searchResults1, ActivitySearch.this);

        search_recycler_view.setAdapter(adapter);


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        search_recycler_view.setLayoutManager(linearLayoutManager);


        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(final int page, int totalItemsCount, RecyclerView view) {
                //Log.e("onLoadMore", "getResCount err: " + resCount);
                //if (resCount > searchResults1.size()) {

                    searchForPhraseInDB(strToFind, false);
               // }
            }
        };

        search_recycler_view.addOnScrollListener(scrollListener);

        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();
        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
                String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
                if (sharedText != null) {
                    editTextSearch.setText(sharedText);
                    onClickFindBtn(editTextSearch);
                }
            }
        }
        editTextSearch.setOnKeyListener((v, keyCode, event) -> {
            if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                onClickFindBtn(v);
                return true;
            }
            return false;
        });

    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(SetLanguage.wrap(newBase));
    }

    public void setSearchLimitsText() {
        TextView search_limits_text = findViewById(R.id.search_limits_text);
        String poetName = getString(R.string.all_poets);
        String srcLimit = getString(R.string.search_in);

        int searchSelectedPoetId = AppSettings.getSearchSelectedPoet();
        if (searchSelectedPoetId > 0) {
            GanjoorPoet poet = GanjoorDbBrowser1.getPoet(searchSelectedPoetId);
            if (poet != null) {
                poetName = poet._Name;
            }
        }

        String text = srcLimit + "&nbsp;&nbsp;<b><font color=\"#FFFF00\">" + poetName + "</font></b>";

        search_limits_text.setText(UtilFunctions.fromHtml(text), TextView.BufferType.NORMAL);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Bungee.slideDown(this); //fire the slide left animation
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                finish();
                Bungee.slideDown(this);
                break;
            case  R.id.action_setting:
                searchLimitsDialog.ShowLimitsDialog(true);
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    public void onClickSettingBtn(final View vobj) {
        searchLimitsDialog.ShowLimitsDialog(true);
    }

    public void onClickFindBtn(final View vobj) {
        strToFind = editTextSearch.getText().toString();
        searchForPhraseInDB(strToFind.trim(), true);

    }


    public void voiceButton(View view) {
        //Trigger the RecognizerIntent intent//
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        try {
            startActivityForResult(intent, REQUEST_CODE);
        } catch (ActivityNotFoundException a) {

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQUEST_CODE: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    String resStr = result.get(0);
                    if (!resStr.trim().isEmpty()) {
                        strToFind = resStr.trim();
                        editTextSearch.setText(strToFind);
                        searchForPhraseInDB(strToFind.trim(), true);
                    }

                }
                break;
            }

        }
    }

    /**
     * @param findSTR    searching phrase
     * @param new_status boolean new search or Continue the previous search
     */

    protected void searchForPhraseInDB(String findSTR, boolean new_status) {




        if (findSTR.trim().isEmpty()) {
            return;
        }
        inputManager.hideSoftInputFromWindow(editTextSearch.getWindowToken(), 0);

        int poet_id = AppSettings.getSearchSelectedPoet();
        String bookIds = AppSettings.getSearchBooksAsString();

        if (new_status) {
            offset = 0;
            this.searchResults1.clear();
            adapter.notifyDataSetChanged(); // or notifyItemRangeRemoved
            scrollListener.resetState();
        } else {
            offset = this.searchResults1.size();
        }

        //Log.e("offset", "offset: " + offset);
        SearchWordAsyncTask searchWordAsyncTask1 = new SearchWordAsyncTask(
                findSTR,
                offset,
                offset,
                per_page,
                this,
                GanjoorDbBrowser1,
                new_status);

        searchWordAsyncTask1.outputSearchResult = this.searchResults1;
        searchWordAsyncTask1.execute();
    }
}

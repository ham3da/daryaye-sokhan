package ir.ham3da.darya;

import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ir.ham3da.darya.adaptors.AdapterSong;
import ir.ham3da.darya.ganjoor.GanjoorSongInfo;
import ir.ham3da.darya.utility.SetLanguage;
import ir.ham3da.darya.utility.UtilFunctions;

public class ActivitySongCollection extends AppCompatActivity {
    RecyclerView recyclerView;
    SwipeRefreshLayout swipeRefreshLayout;
    TextView no_item_textview;
    AdapterSong adapterSong;
    List<GanjoorSongInfo> songList = new ArrayList<>();
    int poemId;
    String poem_title;
    private Toolbar toolbar;
    UtilFunctions UtilFunctions1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        UtilFunctions.changeTheme(this);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            SetLanguage.wrap(this);
        }


        setContentView(R.layout.activity_audio_collection);
        UtilFunctions1 = new UtilFunctions(this);
        UtilFunctions1.setBackBackPressed(ActivitySongCollection.this);

        poem_title = getIntent().getStringExtra("poem_title");

        toolbar = findViewById(R.id.toolbar_audio);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle( getString(R.string.songs) + " «"+ poem_title+"»");
        }
        toolbar.setTitle( getString(R.string.songs) + " «"+ poem_title+"»");

        poemId = getIntent().getIntExtra("poem_id", 0);

        recyclerView = findViewById(R.id.recycler_audio);
        swipeRefreshLayout = findViewById(R.id.simpleSwipeRefreshLayout);
        no_item_textview = findViewById(R.id.no_item_textview);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapterSong = new AdapterSong(songList, this);
        recyclerView.setAdapter(adapterSong);

        swipeRefreshLayout.setOnRefreshListener(this::loadSongs);

        loadSongs();
    }

    private void loadSongs() {
        swipeRefreshLayout.setRefreshing(true);
        String url = "https://api.ganjoor.net/api/ganjoor/poem/" + poemId + "/songs?approved=true";

        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONArray array = new JSONArray(response);
                        songList.clear();
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject obj = array.getJSONObject(i);
                            GanjoorSongInfo song = new GanjoorSongInfo();
                            song.id = obj.getInt("id");
                            song.poemId = obj.getInt("poemId");
                            song.artistName = obj.getString("artistName");
                            song.artistUrl = obj.getString("artistUrl");
                            song.albumName = obj.getString("albumName");
                            song.albumUrl = obj.getString("albumUrl");
                            song.trackName = obj.getString("trackName");
                            song.trackUrl = obj.getString("trackUrl");
                            songList.add(song);
                        }
                        adapterSong.notifyDataSetChanged();
                        no_item_textview.setText(R.string.no_song_this_item);
                        no_item_textview.setVisibility(songList.isEmpty() ? View.VISIBLE : View.GONE);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, R.string.get_song_error, Toast.LENGTH_SHORT).show();
                    }
                    swipeRefreshLayout.setRefreshing(false);
                },
                error -> {
                    swipeRefreshLayout.setRefreshing(false);
                    Toast.makeText(this, R.string.get_song_error, Toast.LENGTH_SHORT).show();
                });

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }


    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        SetLanguage.wrap(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.setting_menu, menu);
        return true;
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
}
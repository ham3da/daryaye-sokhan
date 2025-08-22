package ir.ham3da.darya;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ir.ham3da.darya.adaptors.PoemImageAdapter;
import ir.ham3da.darya.ganjoor.PoemImage;
import ir.ham3da.darya.utility.SetLanguage;
import ir.ham3da.darya.utility.UtilFunctions;

public class ActivityPoemImages extends AppCompatActivity {

    private ViewPager2 viewPager;


    private PoemImageAdapter adapter;
    private List<PoemImage> imageList = new ArrayList<>();

    private int poemId = 0;
    private String poem_title;

    private Toolbar toolbar;
    UtilFunctions UtilFunctions1;
    ProgressBar progressBar;
    TextView no_item_textview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UtilFunctions.changeTheme(this);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            SetLanguage.wrap(this);
        }

        setContentView(R.layout.activity_poem_images);


        UtilFunctions1 = new UtilFunctions(this);
        UtilFunctions1.setBackBackPressed(ActivityPoemImages.this);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getString(R.string.Manuscripts) + " «"+ poem_title+"»");

        }

        poem_title = getIntent().getStringExtra("poem_title");

        toolbar.setTitle( getString(R.string.Manuscripts) + " «"+ poem_title+"»");

        viewPager = findViewById(R.id.viewPager);

        progressBar = findViewById(R.id.progressBar);
        viewPager = findViewById(R.id.viewPager);
        no_item_textview = findViewById(R.id.no_item_textview);


        adapter = new PoemImageAdapter(this, imageList);
        viewPager.setAdapter(adapter);
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                adapter.setCurrentPosition(position);
            }
        });



        poemId = getIntent().getIntExtra("poem_id", 0);
        fetchPoemImages(poemId);
    }

    private void fetchPoemImages(int poemId_var) {

        progressBar.setVisibility(View.VISIBLE);
        viewPager.setVisibility(View.GONE);

        String url = "https://api.ganjoor.net/api/ganjoor/poem/" + poemId_var + "/images";

        RequestQueue queue = Volley.newRequestQueue(this);
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    imageList.clear();
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject obj = response.getJSONObject(i);
                            PoemImage image = new PoemImage();
                            image.id = obj.getString("id");
                            image.imageOrder = obj.getInt("imageOrder");
                            image.thumbnailImageUrl = obj.getString("thumbnailImageUrl");
                            image.targetPageUrl = obj.getString("targetPageUrl");
                            image.altText = obj.getString("altText");
                            image.isTextOriginalSource = obj.getBoolean("isTextOriginalSource");

                            imageList.add(image);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    adapter.notifyDataSetChanged();
                    progressBar.setVisibility(View.GONE);


                    if (imageList.isEmpty()) {

                        no_item_textview.setVisibility(View.VISIBLE);
                        Toast.makeText(this, R.string.no_item, Toast.LENGTH_SHORT).show();
                    } else {
                        viewPager.setVisibility(View.VISIBLE);
                        no_item_textview.setVisibility(View.GONE);
                    }

                },
                error -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, R.string.get_images_error, Toast.LENGTH_SHORT).show();
                }
        );

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
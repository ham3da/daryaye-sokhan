package ir.ham3da.darya;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import ir.ham3da.darya.adaptors.CategoryRecycleAdaptor;
import ir.ham3da.darya.adaptors.PoemsRecycleAdaptor;
import ir.ham3da.darya.ganjoor.CateWithPoem;
import ir.ham3da.darya.ganjoor.GanjoorAudioInfo;
import ir.ham3da.darya.ganjoor.GanjoorCat;
import ir.ham3da.darya.ganjoor.GanjoorDbBrowser;
import ir.ham3da.darya.ganjoor.GanjoorPoem;
import ir.ham3da.darya.ganjoor.GanjoorPoet;
import ir.ham3da.darya.utility.AppSettings;
import ir.ham3da.darya.utility.SetLanguage;
import ir.ham3da.darya.utility.UtilFunctions;


public class ActivityCate extends AppCompatActivity {
    int poet_id = 0, cate_id = 0;
    String cate_url ;

    GanjoorDbBrowser GanjoorDbBrowser1;
    GanjoorCat GanjoorCat1;
    GanjoorPoet GanjoorPoet1;

    RecyclerView recyclerView;

    RecyclerView.Adapter adapter;
    List<GanjoorCat> cateList;
    List<GanjoorPoem> poemList;

    List<CateWithPoem> cateWithPoemList;

    CollapsingToolbarLayout toolbarLayout;

    UtilFunctions UtilFunctions1;
    Menu menuAct;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        UtilFunctions.changeTheme(this);
        setContentView(R.layout.activity_cate);

        Toolbar toolbar = findViewById(R.id.toolbar_cate);
        setSupportActionBar(toolbar);

        UtilFunctions1 = new UtilFunctions(this);

        FloatingActionButton fab = findViewById(R.id.fab);

        fab.setOnClickListener(v ->
        {
            Intent  intent = new Intent(this, ActivityPuzzle.class);
            intent.putExtra("parentCate", cate_id);
            startActivity(intent);
            Bungee.card(this);
        }
        );


        fab.setOnLongClickListener(v -> {
            Toast.makeText(v.getContext(), R.string.dont_forget_poetry, Toast.LENGTH_SHORT).show();
            return true;
        });

        toolbarLayout = findViewById(R.id.toolbar_layout);
        UtilFunctions1.setupToolbarLayout(toolbarLayout, true);

        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        cate_id = getIntent().getIntExtra("cate_id", 0);
        boolean fromCate = getIntent().getBooleanExtra("fromCate", false);

        GanjoorDbBrowser1 = new GanjoorDbBrowser(this);

        GanjoorCat1 = GanjoorDbBrowser1.getCat(cate_id);
        poet_id = GanjoorCat1._PoetID;
        cate_url = GanjoorCat1._Url;

        GanjoorPoet1 = GanjoorDbBrowser1.getPoet(poet_id);
        TextView poet_name = this.findViewById(R.id.poet_name);
        if(fromCate)
        {
            GanjoorCat parentCate = GanjoorDbBrowser1.getCat(GanjoorCat1._ParentID);
            poet_name.setText(parentCate._Text);
            toolbarLayout.setTitle(GanjoorCat1._Text);
        }
        else
        {
            poet_name.setText(GanjoorPoet1._Name);
            if(GanjoorPoet1._CatID == GanjoorCat1._ID)
            {

                toolbarLayout.setTitle(getString(R.string.poetry_collection));
            }
            else {
                toolbarLayout.setTitle(GanjoorCat1._Text);
            }

        }


        recyclerView = findViewById(R.id.cate_recycler_view);
        //
        int subCatsCount = GanjoorDbBrowser1.getSubCatsCount(cate_id);


        if (subCatsCount > 0)
        {
            cateList = GanjoorDbBrowser1.getSubCats(cate_id);
            cateWithPoemList = new ArrayList<>();
            if(GanjoorPoet1._CatID != GanjoorCat1._ID) {
                for (GanjoorCat cate1 : cateList) {
                    CateWithPoem cateWithPoem = new CateWithPoem(cate1._ID, cate1._PoetID, cate1._Text, cate1._ParentID, cate1._Url, 0, CateWithPoem.TYPE_CATEGORY, false, "");
                    cateWithPoemList.add(cateWithPoem);
                }
            }

            poemList = GanjoorDbBrowser1.getPoems(cate_id, true);
            if(poemList.size() > 0)
            {
                for (GanjoorPoem poem1: poemList)
                {
                    CateWithPoem cateWithPoem = new CateWithPoem(poem1._ID, poet_id, poem1._Title, poem1._CatID, poem1._Url, 0, CateWithPoem.TYPE_POEM, poem1._Faved, poem1._FirstVerse);
                    cateWithPoemList.add(cateWithPoem);
                }
            }

            adapter = new CategoryRecycleAdaptor(cateWithPoemList, this);

        }
        else
        {
            poemList = GanjoorDbBrowser1.getPoems(cate_id, true);
            adapter = new PoemsRecycleAdaptor(poemList, this);
        }

        recyclerView.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);



    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(SetLanguage.wrap(newBase));
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        Bungee.slideDown(this); //fire the slide left animation
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.cate_menu, menu);

        MenuItem item = menu.findItem(R.id.action_download_declaim);
        if(poemList != null && poemList.size() > 0)
        {
            item.setVisible(true);
        }
        else
        {
            item.setVisible(false);
        }

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        Intent intent ;
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                finish();
                Bungee.slideDown(this);
                break;
            case R.id.action_search:

                intent = new Intent(this, ActivitySearch.class);
                ActivityCate.this.startActivity(intent);
                Bungee.card(ActivityCate.this);
                break;
            case R.id.action_download_declaim:

                intent = new Intent(this, ActivityAudioCollection.class);
                intent.putExtra("poem_id", 0);
                intent.putExtra("dl_type", GanjoorAudioInfo.DOWNLOAD_CATE_POEMS);
                intent.putExtra("poet_id", poet_id);
                intent.putExtra("cate_id", cate_id);

                startActivity(intent);
                Bungee.slideUp(this);
                break;



        }
        return super.onOptionsItemSelected(item);
    }
}

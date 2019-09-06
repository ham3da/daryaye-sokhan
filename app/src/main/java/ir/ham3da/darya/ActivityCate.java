package ir.ham3da.darya;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import ir.ham3da.darya.adaptors.CateRecycleAdaptor;
import ir.ham3da.darya.adaptors.PoemsRecycleAdaptor;
import ir.ham3da.darya.ganjoor.GanjoorCat;
import ir.ham3da.darya.ganjoor.GanjoorDbBrowser;
import ir.ham3da.darya.ganjoor.GanjoorPoem;
import ir.ham3da.darya.ganjoor.GanjoorPoet;
import ir.ham3da.darya.utility.SetLanguage;
import ir.ham3da.darya.utility.UtilFunctions;


public class ActivityCate extends AppCompatActivity {
    int poet_id = 0, cate_id = 0;
    String cate_url ;

    GanjoorDbBrowser GanjoorDbBrowser1;
    GanjoorCat GanjoorCat1;
    GanjoorPoet GanjoorPoet1;
    GanjoorPoem GanjoorPoem1;

    RecyclerView recyclerView;

    RecyclerView.Adapter adapter;
    List<GanjoorCat> cateList;
    List<GanjoorPoem> poemList;

    CollapsingToolbarLayout toolbarLayout;

    UtilFunctions UtilFunctions1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cate);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_cate);
        setSupportActionBar(toolbar);

        UtilFunctions1 = new UtilFunctions(this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!cate_url.isEmpty())
                {
                    UtilFunctions1.openUrl(cate_url);
                }
                else
                {
                    Toast.makeText(view.getContext(), R.string.url_empty, Toast.LENGTH_SHORT).show();
                }
            }
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

        if (cate_url.isEmpty())
        {
            fab.setEnabled(false);

        }

        GanjoorPoet1 = GanjoorDbBrowser1.getPoet(poet_id);
        TextView poet_name = (TextView) this.findViewById(R.id.poet_name);
        if(fromCate)
        {
            GanjoorCat parentCate = GanjoorDbBrowser1.getCat(GanjoorCat1._ParentID);
            poet_name.setText(parentCate._Text);
        }
        else
        {
            poet_name.setText(GanjoorPoet1._Name);
        }

        toolbarLayout.setTitle(GanjoorCat1._Text);
        recyclerView = (RecyclerView) findViewById(R.id.cate_recycler_view);
        cateList = GanjoorDbBrowser1.getSubCats(cate_id);
        if (cateList.size() > 0) {
            adapter = new CateRecycleAdaptor(cateList, this);
        } else {
            poemList = GanjoorDbBrowser1.getPoems(cate_id);
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
            case R.id.action_search:
                Intent intent = new Intent(ActivityCate.this, ActivitySearch.class);
                ActivityCate.this.startActivity(intent);
                Bungee.card(ActivityCate.this);
                break;

        }
        return super.onOptionsItemSelected(item);
    }
}

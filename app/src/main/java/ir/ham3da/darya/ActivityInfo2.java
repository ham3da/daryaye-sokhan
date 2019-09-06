package ir.ham3da.darya;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.text.Layout;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import ir.ham3da.darya.utility.AppSettings;
import ir.ham3da.darya.utility.SetLanguage;
import ir.ham3da.darya.utility.UtilFunctions;

public class ActivityInfo2 extends AppCompatActivity {


    UtilFunctions UtilFunctions1;
    float textSize;
    CollapsingToolbarLayout toolbarLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_info);
        setSupportActionBar(toolbar);

       ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        AppSettings.Init(this);
        textSize = AppSettings.getTextSize();

        UtilFunctions1 = new UtilFunctions(this);

        String title1 = getIntent().getStringExtra("title1");
        String title2 = getIntent().getStringExtra("title2");
        final String text = getIntent().getStringExtra("text");

        TextView text_box_long = (TextView) this.findViewById(R.id.text);

        TextView title2_box1  = (TextView) this.findViewById(R.id.title2);
        ImageView info_avatar1 =  this.findViewById(R.id.info_avatar1);


        if(Build.VERSION.SDK_INT >=  Build.VERSION_CODES.O) {
            text_box_long.setJustificationMode(Layout.JUSTIFICATION_MODE_INTER_WORD);
        }

        //text_box_long.setText(text);
        if (!text.isEmpty()) {
            text_box_long.setText(UtilFunctions.fromHtml(text), TextView.BufferType.SPANNABLE);
        }


        text_box_long.setTextSize(textSize);
        //text_box_long.setGravity(Gravity.START|Gravity.END);



        if(title2.isEmpty())
        {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) info_avatar1.getLayoutParams();
            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
            params.setMargins(0, 0, 0, 0);

            title2_box1.setVisibility(View.GONE);

        }
        else {
            title2_box1.setText(title2);
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UtilFunctions1.shareText(text);
            }
        });

        toolbarLayout = findViewById(R.id.toolbar_layout);
        UtilFunctions1.setupToolbarLayout(toolbarLayout, false);

        toolbarLayout.setTitle(title1);
    }

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
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.info2, menu);
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

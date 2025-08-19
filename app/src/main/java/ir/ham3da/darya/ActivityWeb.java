package ir.ham3da.darya;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import ir.ham3da.darya.utility.SetLanguage;
import ir.ham3da.darya.utility.UtilFunctions;

public class ActivityWeb extends AppCompatActivity {

    WebView webView2;
    ProgressBar progress_bar_dlg;

    private Toolbar toolbar;
    UtilFunctions UtilFunctions1;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UtilFunctions.changeTheme(this);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            SetLanguage.wrap(this);
        }
        setContentView(R.layout.activity_web);
        String title1 = getIntent().getStringExtra("title");
        UtilFunctions1 = new UtilFunctions(this);
        UtilFunctions1.setBackBackPressed(ActivityWeb.this);

        toolbar = findViewById(R.id.web_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(title1);
        }
        App globalVariable = (App) getApplicationContext();
        progress_bar_dlg = findViewById(R.id.progressBar_loader);
        webView2 = findViewById(R.id.webView2);


        String text = getIntent().getStringExtra("text");

        boolean fromUrl = getIntent().getBooleanExtra("fromUrl", false);

        //setTitle(title1);
        if(fromUrl )
        {
            webView2.setWebViewClient(new WebViewClient(){
                @Override
                public void onPageStarted(WebView view, String url, Bitmap favicon)
                {


                    super.onPageStarted(view, url, favicon);
                    Log.d("webView2", "onPageStarted: with url " + url);
                    progress_bar_dlg.setVisibility(View.VISIBLE);
                }

                @Override
                public void onPageFinished(WebView view, String url)
                {
                    super.onPageFinished(view, url);
                    Log.d("webView2", "onPageFinished: for " + url);
                    progress_bar_dlg.setVisibility(View.GONE);
                }

                @Override
                public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request)
                {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(request.getUrl().toString()));
                    startActivity(browserIntent);
                    return  true;
                }

                @Override
                public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error)
                {
                    Log.d("webView2", "onReceivedError: " + error);
                    super.onReceivedError(view, request, error);
                }
            });


            String url = getIntent().getStringExtra("url");
            webView2.getSettings().setJavaScriptEnabled(true);
            webView2.loadUrl(url);

            globalVariable.setAdviewd(true);
        }
        else
        {
            webView2.loadDataWithBaseURL("file:///android_res/", text, "text/html", "UTF-8", null);
        }

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

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(SetLanguage.wrap(newBase));
    }
}

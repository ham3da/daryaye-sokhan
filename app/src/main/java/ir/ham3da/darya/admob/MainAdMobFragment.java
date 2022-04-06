package ir.ham3da.darya.admob;


import android.content.Context;
import android.content.Intent;

import android.os.Bundle;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.Locale;

import ir.ham3da.darya.ActivityDonateCrypto;
import ir.ham3da.darya.ActivityMain;
import ir.ham3da.darya.ActivityWeb;
import ir.ham3da.darya.App;
import ir.ham3da.darya.Bungee;
import ir.ham3da.darya.MainActivityUtil;
import ir.ham3da.darya.R;

import ir.ham3da.darya.utility.AppSettings;
import ir.ham3da.darya.utility.UtilFunctions;

public class MainAdMobFragment extends Fragment
{

    private MainActivityUtil mainActivityUtil1;
    Context mContext;
    Button btn_view_ad;

    //    InterstitialAd mInterstitialAd;
    ProgressBar progress_bar;
    String TAG = "MainAdMobFragment";
    UtilFunctions UtilFunctions1;

    public MainAdMobFragment()
    {

    }

    @Override
    public void onResume()
    {
        super.onResume();
    }

    @Override
    public void onPause()
    {
        super.onPause();

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View root = inflater.inflate(R.layout.fragment_admob, container, false);

        mContext = getContext();
        AppSettings.Init(mContext);

        String CurrentLang = Locale.getDefault().getLanguage();
        UtilFunctions1 = new UtilFunctions(mContext);

        btn_view_ad = root.findViewById(R.id.btn_view_ad);
        progress_bar = root.findViewById(R.id.progress_bar);

        Button btn_donate_github = root.findViewById(R.id.btn_donate_github);
        Button btn_donate_collections = root.findViewById(R.id.btn_donate_collections);

        Button btn_donate_crypto = root.findViewById(R.id.btn_donate_crypto);

        View rv_pay = root.findViewById(R.id.rv_pay);

//        if (UtilFunctions.isGooglePlayVersion())
//        {
//            rv_pay.setVisibility(View.VISIBLE);
//        }
//        else
//        {
//            rv_pay.setVisibility(View.GONE);
//        }

        rv_pay.setVisibility(View.GONE);
        btn_view_ad.setOnClickListener(v ->
        {
            // displayInterstitial();
            displayCustomAdWeb();
        });

        btn_donate_github.setOnClickListener(v -> {
            UtilFunctions1.openUrl(getString(R.string.git_url));
        });

        btn_donate_collections.setOnClickListener(v ->
        {
            UtilFunctions1.openUrl(getString(R.string.new_collection_url));

        });

        btn_donate_crypto.setOnClickListener(v ->
        {
            Intent intent = new Intent(mContext, ActivityDonateCrypto.class);
            startActivity(intent);
            Bungee.card(mContext);
        });


        return root;
    }


    public void displayCustomAdWeb()
    {
        UtilFunctions1.openUrl(getString(R.string.our_products_url));
//        Intent intent = new Intent(mContext, ActivityWeb.class);
//        intent.putExtra("title", getString(R.string.our_products));
//        intent.putExtra("fromUrl", true);
//        intent.putExtra("url",  getString(R.string.our_products_url));
//
//        startActivity(intent);
//        Bungee.card(mContext);
    }


}

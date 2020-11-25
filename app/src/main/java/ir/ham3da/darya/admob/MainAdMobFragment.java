package ir.ham3da.darya.admob;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.LoadAdError;

import ir.ham3da.darya.App;
import ir.ham3da.darya.MainActivityUtil;
import ir.ham3da.darya.R;

import ir.ham3da.darya.utility.AppSettings;

public class MainAdMobFragment extends Fragment
{

    private MainActivityUtil mainActivityUtil1;
    Context mContext;
    private int resCount = 0;
    Button btn_view_ad;

    InterstitialAd interstitial;
    ProgressBar progress_bar;


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

        final FragmentActivity fragmentActivity = getActivity();
        mContext = getContext();
        AppSettings.Init(mContext);

        // Prepare the Interstitial Ad
        interstitial = new InterstitialAd(mContext);
        // Insert the Ad Unit ID
        Log.e("interstitial", getString(R.string.interstitial_ad_unit_id));
        interstitial.setAdUnitId(getString(R.string.interstitial_ad_unit_id));
        // Request for Ads
        AdRequest adRequest = new AdRequest.Builder()
                .build();


        btn_view_ad = root.findViewById(R.id.btn_view_ad);
        progress_bar = root.findViewById(R.id.progress_bar);

        btn_view_ad.setOnClickListener(v ->
        {
            progress_bar.setVisibility(View.VISIBLE);
            interstitial.loadAd(adRequest);
            // Load ads into Interstitial Ads

        });


        // Prepare an Interstitial Ad Listener
        interstitial.setAdListener(new AdListener()
        {

            @Override
            public void onAdLoaded()
            {
                Log.e("interstitial", "onAdLoaded: ");
                progress_bar.setVisibility(View.INVISIBLE);
                displayInterstitial();
                super.onAdLoaded();
            }

            @Override
            public void onAdOpened()
            {
                super.onAdOpened();
            }

            @Override
            public void onAdClosed()
            {
                Toast.makeText(getContext(), getString(R.string.thanks_a_lot), Toast.LENGTH_SHORT).show();
                App globalVariable = (App) getActivity().getApplicationContext();
                globalVariable.setAdviewd(true);
                super.onAdClosed();
            }

            @Override
            public void onAdFailedToLoad(LoadAdError loadAdError)
            {
                super.onAdFailedToLoad(loadAdError);
                progress_bar.setVisibility(View.INVISIBLE);
                Log.e("interstitial", "onAdFailedToLoad: "+loadAdError.getMessage());
                Toast.makeText(getContext(), getString(R.string.admob_not_load), Toast.LENGTH_SHORT).show();
            }

        });
        return root;
    }


    public void displayInterstitial()
    {
        // If Ads are loaded, show Interstitial else show nothing.
        if (interstitial.isLoaded())
        {
            interstitial.show();
        }
        else
        {
            Toast.makeText(getContext(), getString(R.string.admob_not_load), Toast.LENGTH_SHORT).show();
        }
    }

}

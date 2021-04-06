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

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

import ir.ham3da.darya.ActivityMain;
import ir.ham3da.darya.App;
import ir.ham3da.darya.MainActivityUtil;
import ir.ham3da.darya.R;

import ir.ham3da.darya.utility.AppSettings;
import ir.ham3da.darya.utility.UtilFunctions;

public class MainAdMobFragment extends Fragment
{

    private MainActivityUtil mainActivityUtil1;
    Context mContext;
    Button btn_view_ad;

    InterstitialAd mInterstitialAd;
    ProgressBar progress_bar;
    String TAG = "MainAdMobFragment";


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

        AdmobInterstitialInit(false);

        btn_view_ad = root.findViewById(R.id.btn_view_ad);
        progress_bar = root.findViewById(R.id.progress_bar);

        btn_view_ad.setOnClickListener(v ->
        {
            displayInterstitial();
        });

        return root;
    }

    private void AdmobInterstitialInit(boolean requestShow)
    {

        FullScreenContentCallback fullScreenContentCallback = new FullScreenContentCallback()
        {
            @Override
            public void onAdDismissedFullScreenContent()
            {
                mInterstitialAd = null;
                // Proceed to the next level.
                Toast.makeText(mContext, getString(R.string.thanks_a_lot), Toast.LENGTH_SHORT).show();
                App globalVariable = (App) getActivity().getApplicationContext();
                globalVariable.setAdviewd(true);

            }

            @Override
            public void onAdShowedFullScreenContent()
            {
                super.onAdShowedFullScreenContent();
            }

            @Override
            public void onAdFailedToShowFullScreenContent(AdError adError)
            {
                Log.e(TAG, "onAdFailedToShowFullScreenContent: " + adError.getMessage());
                super.onAdFailedToShowFullScreenContent(adError);
                Toast.makeText(mContext, getString(R.string.admob_not_load), Toast.LENGTH_SHORT).show();
            }
        };

        String mAdunitID = getString(R.string.interstitial_ad_unit_id);

        InterstitialAd.load(
                mContext,
                mAdunitID,
                new AdRequest.Builder().build(),
                new InterstitialAdLoadCallback()
                {

                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd ad)
                    {
                        if(progress_bar.getVisibility() == View.VISIBLE)
                        {
                            progress_bar.setVisibility(View.INVISIBLE);
                        }

                        mInterstitialAd = ad;
                        mInterstitialAd.setFullScreenContentCallback(fullScreenContentCallback);
                        if (requestShow)
                        {
                            mInterstitialAd.show(getActivity());
                        }
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError adError)
                    {
                        Log.e(TAG, "onAdFailedToLoad: " + adError.getMessage());
                        progress_bar.setVisibility(View.INVISIBLE);
                        Toast.makeText(mContext, getString(R.string.admob_not_load), Toast.LENGTH_SHORT).show();
                    }
                });

    }


    public void displayInterstitial()
    {
        if (UtilFunctions.isNetworkConnected(mContext))
        {

            if (mInterstitialAd != null)
            {
                mInterstitialAd.show(getActivity());
            }
            else
            {
                progress_bar.setVisibility(View.VISIBLE);
                AdmobInterstitialInit(true);
            }
        }
        else
        {
            Toast.makeText(mContext, getString(R.string.internet_failed), Toast.LENGTH_SHORT).show();
        }

    }

}

package ir.ham3da.darya.ad;


import android.content.Context;

import android.os.Bundle;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.Locale;

import ir.ham3da.darya.MainActivityUtil;
import ir.ham3da.darya.R;

import ir.ham3da.darya.utility.AppSettings;
import ir.ham3da.darya.utility.UtilFunctions;

public class MainAdFragment extends Fragment
{

    private MainActivityUtil mainActivityUtil1;
    Context mContext;
    Button btn_view_ad;

    String TAG = "MainAdFragment";
    UtilFunctions UtilFunctions1;

    public MainAdFragment()
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
        View root = inflater.inflate(R.layout.fragment_ad, container, false);

        mContext = getContext();
        AppSettings.Init(mContext);

        String CurrentLang = Locale.getDefault().getLanguage();
        UtilFunctions1 = new UtilFunctions(mContext);

        btn_view_ad = root.findViewById(R.id.btn_view_ad);

        Button btn_donate_github = root.findViewById(R.id.btn_donate_github);
        Button btn_donate_collections = root.findViewById(R.id.btn_donate_collections);


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

       return root;
    }


    public void displayCustomAdWeb()
    {
        UtilFunctions1.openUrl(getString(R.string.our_products_url));
    }


}

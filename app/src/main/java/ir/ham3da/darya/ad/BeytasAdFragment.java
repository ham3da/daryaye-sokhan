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

public class BeytasAdFragment extends Fragment
{

    private MainActivityUtil mainActivityUtil1;
    Context mContext;
    Button btn_view_ad;

    String TAG = "BeytasAdFragment";
    UtilFunctions UtilFunctions1;

    public BeytasAdFragment()
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
        View root = inflater.inflate(R.layout.fragment_beytas, container, false);

        mContext = getContext();
        AppSettings.Init(mContext);

        String CurrentLang = Locale.getDefault().getLanguage();
        UtilFunctions1 = new UtilFunctions(mContext);

        btn_view_ad = root.findViewById(R.id.btn_view_ad);

        btn_view_ad.setOnClickListener(v ->
        {
            if(UtilFunctions.isBazaarVersion()) {
                UtilFunctions1.openUrl(getString(R.string.beytas_url));
            }
            else
            {
                UtilFunctions1.openUrl(getString(R.string.beytas_play_url));
            }
        });
       return root;
    }


    public void displayCustomAdWeb()
    {
        UtilFunctions1.openUrl(getString(R.string.our_products_url));
    }


}

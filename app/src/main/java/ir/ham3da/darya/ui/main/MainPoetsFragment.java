package ir.ham3da.darya.ui.main;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import ir.ham3da.darya.adaptors.AdapterPoetsExpand;
import ir.ham3da.darya.ganjoor.GanjoorDbBrowser;
import ir.ham3da.darya.utility.AppSettings;
import ir.ham3da.darya.adaptors.PoetAndBooksAdapter;
import ir.ham3da.darya.ActivityMain;
import ir.ham3da.darya.MainActivityUtil;
import ir.ham3da.darya.R;

public class MainPoetsFragment extends Fragment {
    RecyclerView recycler;
    AdapterPoetsExpand adapter;
    MainActivityUtil mainActivityUtil1;
    private GanjoorDbBrowser GanjoorDbBrowser1;
    private Context mContext;
    SwipeRefreshLayout swipeRefreshLayout1;

    String TAG = "MainPoetsFragment";

    int poetCounts = 0;

    public MainPoetsFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_poets_list, container, false);

        recycler = (RecyclerView) root.findViewById(R.id.main_recycler);

        final FragmentActivity fragmentActivity = getActivity();

        mContext = getContext();

        AppSettings.Init(mContext);

        String DB_PATH = AppSettings.getDatabasePath(mContext);


        swipeRefreshLayout1 = (SwipeRefreshLayout) root.findViewById(R.id.simpleSwipeRefreshLayout1);

        // String DB_PATH = AppSettings.getDatabasePath( mContext );
        //  mainActivityUtil1 = new MainActivityUtil(mContext);

        swipeRefreshLayout1.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadPoetsAndBooks();
                swipeRefreshLayout1.setRefreshing(false);
            }
        });


        loadPoetsAndBooks();

        return root;

    }



    /**
     * Load Poets And their Books in recycleview
     */
    public void loadPoetsAndBooks() {

        try {

            adapter = new AdapterPoetsExpand(this.getContext(), MainPoetsFragment.this);
            adapter.setMode(PoetAndBooksAdapter.MODE_ACCORDION);

            recycler.setLayoutManager(new LinearLayoutManager(this.getContext()));

            recycler.setAdapter(adapter);

            ActivityMain activityMain = ((ActivityMain) getActivity());

            if (activityMain!= null) {
                activityMain.setCountPoetsAndBooks();
            }

            GanjoorDbBrowser GanjoorDbBrowser1 = new GanjoorDbBrowser(mContext);
            poetCounts = GanjoorDbBrowser1.getPoetsCount();

        } catch (Exception ex) {
            Log.e(TAG, "loadPoetsAndBooks err: " + ex.getMessage());
        }

    }

    public void refreshList(final int position) {

        try {
            swipeRefreshLayout1.setRefreshing(true);

            swipeRefreshLayout1.postDelayed(new Runnable() {
                @Override
                public void run() {
                    reloadRecycleViewData(position);
                }
            }, 1000);


        } catch (Exception ex) {
            Log.e("refreshList", "err: " + ex.getMessage());
        }

    }

    private void reloadRecycleViewData(int position) {

        loadPoetsAndBooks();
        if (adapter.getItemCount() >= position) {
            recycler.smoothScrollToPosition(position);
        }
        swipeRefreshLayout1.setRefreshing(false);
    }

    @Override
    public void onResume() {
        super.onResume();

        ActivityMain activityMain = ((ActivityMain) getActivity());

        if (activityMain!= null && activityMain.DOWNLOADEDING_NEW_POET) {
            activityMain.DOWNLOADEDING_NEW_POET = false;
            GanjoorDbBrowser1 = new GanjoorDbBrowser(mContext);
            if (poetCounts != GanjoorDbBrowser1.getPoetsCount()) {

                refreshList(0);
            }
        }


    }

    @Override
    public void onPause() {

        super.onPause();

    }

//
//    public void LoadDBFirstTime(CustomProgress dlg1) {
//        GanjoorDbBrowser GanjoorDbBrowser1 = new GanjoorDbBrowser(this.getContext());
//        dlg1.dismiss();
//        loadPoetsAndBooks();
//    }

}

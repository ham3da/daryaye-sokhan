package ir.ham3da.darya.ui.main;

import android.content.Context;
import android.os.Bundle;

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

import java.util.ArrayList;
import java.util.List;

import ir.ham3da.darya.App;
import ir.ham3da.darya.utility.AppSettings;
import ir.ham3da.darya.utility.EndlessRecyclerViewScrollListener;
import ir.ham3da.darya.ganjoor.FavoritesPoem;
import ir.ham3da.darya.ganjoor.GanjoorDbBrowser;
import ir.ham3da.darya.MainActivityUtil;
import ir.ham3da.darya.R;
import ir.ham3da.darya.adaptors.FavoritesAdaptor;

public class MainFavoritesFragment extends Fragment {

    private RecyclerView fav_recycler;
    private FavoritesAdaptor adapter;
    private MainActivityUtil mainActivityUtil1;
    Context mContext;
    SwipeRefreshLayout swipeRefreshLayout;

    private int offset = 0;
    private int per_page = 100;
    private int resCount = 0;
    private GanjoorDbBrowser GanjoorDbBrowser1;

    private List<FavoritesPoem> favoritesPoemList;
    EndlessRecyclerViewScrollListener scrollListener;

    public MainFavoritesFragment() {

    }

    @Override
    public void onResume() {
        super.onResume();

        App globalVariable = (App) mContext.getApplicationContext();
        if (resCount != GanjoorDbBrowser1.getFavoritesCount() || globalVariable.getUpdateFavList()) {
            refreshFavorites();
            globalVariable.setUpdateFavList(false);
        }


    }

    @Override
    public void onPause() {
        super.onPause();

    }
private void checkAdapterIsEmpty()
{
    if(favoritesPoemList.isEmpty()) {
        FavoritesPoem emptyItem = new FavoritesPoem(0, 0, getString(R.string.empty_favorites), "", false,
                "", "", "", 0);
        emptyItem.isEmptyItem = true;
        favoritesPoemList.add(emptyItem);
    }
}
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        View root = inflater.inflate(R.layout.fragment_favorites, container, false);
        fav_recycler = root.findViewById(R.id.fav_recycler);
        final FragmentActivity fragmentActivity = getActivity();
        mContext = getContext();
        AppSettings.Init(mContext);

        String DB_PATH = AppSettings.getDatabasePath(mContext);
        if (MainActivityUtil.checkExists(DB_PATH)) {

            GanjoorDbBrowser1 = new GanjoorDbBrowser(mContext);

            swipeRefreshLayout = root.findViewById(R.id.simpleSwipeRefreshLayout);

            // mainActivityUtil1 = new MainActivityUtil(mContext);

            resCount = GanjoorDbBrowser1.getFavoritesCount();

            favoritesPoemList = GanjoorDbBrowser1.getFavoritesPoems(false, offset, per_page, 0);
            checkAdapterIsEmpty();
            adapter = new FavoritesAdaptor(favoritesPoemList, mContext);

            fav_recycler.setAdapter(adapter);

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
            fav_recycler.setLayoutManager(linearLayoutManager);

            scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
                @Override
                public void onLoadMore(final int page, int totalItemsCount, RecyclerView view) {

                    if (resCount > favoritesPoemList.size()) {
                        loadFavorites();
                    }
                }
            };

            fav_recycler.addOnScrollListener(scrollListener);
            swipeRefreshLayout.setOnRefreshListener(this::refreshFavorites);
        }

        return root;
    }


    public void refreshFavorites() {
        offset = 0;

        if (favoritesPoemList != null) {
            favoritesPoemList.clear();
        } else {
            favoritesPoemList = new ArrayList<>();
        }
        resCount = GanjoorDbBrowser1.getFavoritesCount();
        List<FavoritesPoem> newItems = GanjoorDbBrowser1.getFavoritesPoems(false, offset, per_page, 0); // بارگذاری صفحه اول
        favoritesPoemList.addAll(newItems);

        checkAdapterIsEmpty();

        if (adapter == null) {
            adapter = new FavoritesAdaptor(favoritesPoemList, mContext);
            fav_recycler.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }

        if (scrollListener != null) {
            scrollListener.resetState();
        }

        if (swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
        }


    }

    /**
     * Load Poets And their Books in recycleview
     */
    public void loadFavorites() {

        int currentSize = favoritesPoemList.size();
        offset = currentSize;
        List<FavoritesPoem> favoritesPoemList2 = GanjoorDbBrowser1.getFavoritesPoems(false, offset, per_page, 0); // در اینجا offset سوم در getFavoritesPoems احتمالا باید 0 باشد اگر فقط برای limit query است
        if (!favoritesPoemList2.isEmpty()) {
            favoritesPoemList.addAll(favoritesPoemList2);
            adapter.notifyItemRangeInserted(currentSize, favoritesPoemList2.size());
        }
        else {
            checkAdapterIsEmpty();
            adapter.notifyItemInserted(0);
        }

    }


}

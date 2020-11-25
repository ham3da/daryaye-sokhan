package ir.ham3da.darya;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import ir.ham3da.darya.adaptors.SearchCustomAdapter;
import ir.ham3da.darya.ganjoor.GanjoorDbBrowser;
import ir.ham3da.darya.ganjoor.SearchResult;
import ir.ham3da.darya.utility.AppSettings;

public class SearchWordAsyncTask {
    String mTAG = "srcAsyncTask";
    private final String findStr;
    private final int offset;
    private final int per_page;
    private final int start_index;

    private final boolean new_status;

    public List<SearchResult> outputSearchResult;

    SearchCustomAdapter adapter;
    private final GanjoorDbBrowser GanjoorDbBrowser1;

    ActivitySearch activitySearch;
    private final ProgressBar ProgressBar1;

    public SearchWordAsyncTask(String findStr , int offset, int start_index, int per_page , Context context, GanjoorDbBrowser ganjoorDbBrowser, boolean new_status)
    {
        this.findStr = findStr;
        this.offset = offset;
        this.start_index = start_index;
        this.per_page = per_page;
        this.activitySearch = (ActivitySearch) context;
        this.GanjoorDbBrowser1 = ganjoorDbBrowser;
        this.ProgressBar1 = this.activitySearch.progressBar_loader;
        this.new_status = new_status;
        this.adapter = this.activitySearch.adapter;
    }

    protected void execute()
    {
        ProgressBar1.setVisibility(View.VISIBLE);

        Thread searchingThread = new Thread(this::doInBackground);
        searchingThread.start();
    }


    protected void doInBackground() {

        List<SearchResult> searchResults = new ArrayList<>();
        try {


            int poet_id = AppSettings.getSearchSelectedPoet();
            String bookIds = AppSettings.getSearchBooksAsString();//پیاده سازی نشده

            searchResults = GanjoorDbBrowser1.searchForPhrase(this.findStr, poet_id, "", offset, per_page, start_index);

            complete(searchResults);

        } catch (Exception e) {

            Log.e(this.mTAG, "SRC_doInBackground: " + e.getMessage());
            complete(searchResults);
        }
    }

    protected void complete(List<SearchResult> tempSearchResult)
    {
        activitySearch.runOnUiThread(() -> {
            ProgressBar1.setVisibility(View.GONE);
            if (tempSearchResult.size() > 0)
            {
                this.outputSearchResult.addAll(tempSearchResult);

                this.adapter.notifyItemRangeInserted(this.outputSearchResult.size(), this.outputSearchResult.size() - 1);
                this.adapter.setFindStr(this.findStr);

                if(this.new_status)
                {
                    int res_count = activitySearch.resCount;
                    this.adapter.notifyDataSetChanged(); // or notifyItemRangeRemoved

                }
            }
            else
            {
                if(this.new_status) {
                    Toast.makeText(activitySearch, R.string.nothing_found, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

}
package ir.ham3da.darya;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import ir.ham3da.darya.adaptors.SearchCustomAdapter;
import ir.ham3da.darya.ganjoor.GanjoorDbBrowser;
import ir.ham3da.darya.ganjoor.SearchResult;
import ir.ham3da.darya.utility.AppSettings;

public class SearchWordAsyncTask extends AsyncTask<Void, Void, Void> {
    private String mTAG = "srcAsyncTask";
    private String findStr;
    private int offset;
    private int per_page;
    private int start_index;

    private boolean new_status;

    public List<SearchResult> outputSearchResult;
    private List<SearchResult> tempSearchResult;

    private SearchCustomAdapter adapter;
    private GanjoorDbBrowser GanjoorDbBrowser1;

    private Context mContext;
    private ActivitySearch activitySearch;
    private ProgressBar ProgressBar1;

    public SearchWordAsyncTask(String findStr , int offset, int start_index, int per_page , Context context, GanjoorDbBrowser ganjoorDbBrowser, boolean new_status)
    {
        this.findStr = findStr;
        this.offset = offset;
        this.start_index = start_index;
        this.per_page = per_page;
        this.mContext = context;
        this.activitySearch = (ActivitySearch) mContext;
        this.GanjoorDbBrowser1 = ganjoorDbBrowser;
        this.ProgressBar1 = this.activitySearch.progressBar_loader;
        this.new_status = new_status;
        this.adapter = this.activitySearch.adapter;
    }


    @Override
    protected void onPreExecute()
    {
        ProgressBar1.setVisibility(View.VISIBLE);
    }

    @Override
    protected Void doInBackground(Void... arg) {

       try {

            this.tempSearchResult = new ArrayList<>();
            int poet_id = AppSettings.getSearchSelectedPoet();
            String bookIds = AppSettings.getSearchBooksAsString();//پیاده سازی نشده

            this.tempSearchResult = GanjoorDbBrowser1.searchForPhrase(this.findStr, poet_id, "", offset, per_page, start_index);

        } catch (Exception e) {
            Log.e(this.mTAG, "SRC_doInBackground: " + e.getMessage());
        }

        return null;
    }

    protected void onPostExecute(Void a)
    {
        ProgressBar1.setVisibility(View.GONE);
        if (this.tempSearchResult.size() > 0)
        {
            this.outputSearchResult.addAll(tempSearchResult);

            this.adapter.notifyItemRangeInserted(this.outputSearchResult.size(), this.outputSearchResult.size() - 1);
            this.adapter.setFindStr(this.findStr);

            if(this.new_status)
            {
                int res_count = activitySearch.resCount;
               // String str_found = String.format(Locale.getDefault(), "%d ", res_count) + mContext.getString(R.string.items_found);
                this.adapter.notifyDataSetChanged(); // or notifyItemRangeRemoved
               // Toast.makeText(activitySearch, str_found, Toast.LENGTH_SHORT).show();
            }
        }
        else
        {
            if(this.new_status) {
                Toast.makeText(activitySearch, R.string.nothing_found, Toast.LENGTH_SHORT).show();
            }
        }
    }

}
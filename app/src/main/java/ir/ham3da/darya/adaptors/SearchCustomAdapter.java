package ir.ham3da.darya.adaptors;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Locale;

import ir.ham3da.darya.utility.AppSettings;
import ir.ham3da.darya.Bungee;
import ir.ham3da.darya.ActivityPoem;
import ir.ham3da.darya.R;
import ir.ham3da.darya.ganjoor.SearchResult;
import ir.ham3da.darya.utility.UtilFunctions;

public class SearchCustomAdapter extends RecyclerView.Adapter<SearchCustomAdapter.ViewHolder> {

    private List<SearchResult> searchList;
    private Context context1;
    private boolean indexingStatus;

    public String findStr;

    public SearchCustomAdapter(List<SearchResult> searchResult1, Context mCtx)
    {
        this.searchList = searchResult1;
        this.context1 = mCtx;
        this.indexingStatus = AppSettings.getVerseListIndexStatus();
    }


    public void setFindStr(String str)
    {
        findStr = str;
    }

    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_search_result, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final SearchCustomAdapter.ViewHolder holder, int position) {
        final SearchResult srcResult = searchList.get(position);

        String verseStr = srcResult._Text.trim();

        if(!findStr.isEmpty())
        {
            String findStrHtml = "<font color=\"#f44336\">" + findStr + "</font>";
            verseStr = verseStr.replace(findStr, findStrHtml);
        }

//        if(indexingStatus)
//        {
            verseStr = String.format(Locale.getDefault(), "%d. ", srcResult._Index) + verseStr;
//        }
        holder.src_verse.setText(UtilFunctions.fromHtml(verseStr), TextView.BufferType.NORMAL);

        String referenceText  =  srcResult._CatTree+  " > " +srcResult._PoemTitle;
        holder.src_verse_reference.setText(referenceText);

        holder.srcCardView.setOnClickListener(view -> {
            Intent intent = new Intent(view.getContext(), ActivityPoem.class);
            intent.putExtra("poem_id", srcResult._PoemID);
            intent.putExtra("from_search", true);
            intent.putExtra("findStr", findStr);
            intent.putExtra("vOrder", srcResult._Order);

            context1.startActivity(intent);
            Bungee.card(context1);
        });
    }


    @Override
    public int getItemCount() {

        return searchList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView src_verse;
        public TextView src_verse_reference;
        public CardView srcCardView;

        public ViewHolder(View itemView) {
            super(itemView);

            src_verse = itemView.findViewById(R.id.src_verse);
            src_verse_reference = itemView.findViewById(R.id.src_verse_reference);
            srcCardView = itemView.findViewById(R.id.srcCardView);
        }
    }
}

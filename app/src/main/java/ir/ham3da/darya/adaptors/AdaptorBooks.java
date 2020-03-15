package ir.ham3da.darya.adaptors;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ir.ham3da.darya.ganjoor.GanjoorCat;
import ir.ham3da.darya.R;

public class AdaptorBooks extends RecyclerView.Adapter<AdaptorBooks.ViewHolder> {


    private List<GanjoorCat> GanjoorCatList;
    private Context context1;

    public AdaptorBooks(List<GanjoorCat> ganjoorCatList, Context context) {
        this.GanjoorCatList = ganjoorCatList;
        this.context1 = context;
    }


    @Override
    public AdaptorBooks.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_book_search, parent, false);
        return new AdaptorBooks.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(AdaptorBooks.ViewHolder holder, final int position) {
        final GanjoorCat GanjoorCat1 = GanjoorCatList.get(position);
        holder.book_name.setText(GanjoorCat1._Text);
        final int cate_id = GanjoorCat1._ID;


        final AdaptorBooks.ViewHolder finalHolder = holder;

        finalHolder.bookCardView.setSelected(GanjoorCat1._Selected);


        holder.bookCardView.setOnClickListener(v -> {

            if (finalHolder.bookCardView.isSelected()) {
                GanjoorCat1._Selected = false;
                finalHolder.bookCardView.setSelected(false);
                GanjoorCatList.get(position)._Selected = false;

            } else {
                GanjoorCat1._Selected = true;

                GanjoorCatList.get(position)._Selected = true;

                finalHolder.bookCardView.setSelected(true);
            }
        });


    }


    private List<Integer> getListOfCatID() {
        List<Integer> catListId = new ArrayList<>();
        for (GanjoorCat ganjoorCat : GanjoorCatList) {
            catListId.add(ganjoorCat._ID);
        }
        return catListId;
    }

    /**
     * Select or UnSelect All
     *
     * @param isSelectedAll
     */
    public void selectAllItem(boolean isSelectedAll) {
        try {

            for (GanjoorCat ganjoorCat : GanjoorCatList) {
                ganjoorCat._Selected = isSelectedAll;
            }
            this.notifyDataSetChanged();

        } catch (Exception e) {
            Log.w("selectAllItem", "Exception: " + e.getMessage());
        }
    }

    public List<Integer> getSelectedBookIds() {
        List<Integer> booIDsList = new ArrayList<>();

        for (GanjoorCat ganjoorCat : GanjoorCatList) {
            if (ganjoorCat._Selected) {
                booIDsList.add(ganjoorCat._ID);
            }
        }

        return booIDsList;
    }

    public void setSelectedBookIds(List<Integer> bookIDsList) {

        boolean notify = false;

        List<Integer> getListOfCatID = getListOfCatID();

        if (bookIDsList.size() > 0) {
            for (int i = 0; i < bookIDsList.size(); i++) {
                int index = getListOfCatID.indexOf(bookIDsList.get(i));
                if(index >= 0) {
                    GanjoorCat ganjoorCat = GanjoorCatList.get(index);

                    ganjoorCat._Selected = true;
                    notify = true;
                }

            }

            if (notify) {
                this.notifyDataSetChanged();
            }
        }
    }

    @Override
    public int getItemCount() {
        return GanjoorCatList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView book_name;
        public CardView bookCardView;

        public ViewHolder(View itemView) {
            super(itemView);

            book_name = itemView.findViewById(R.id.book_name);
            bookCardView = itemView.findViewById(R.id.bookCardView);

        }
    }


}

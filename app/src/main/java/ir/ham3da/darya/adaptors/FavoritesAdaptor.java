package ir.ham3da.darya.adaptors;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Locale;

import ir.ham3da.darya.utility.AppSettings;
import ir.ham3da.darya.Bungee;
import ir.ham3da.darya.ganjoor.FavoritesPoem;
import ir.ham3da.darya.ganjoor.GanjoorDbBrowser;
import ir.ham3da.darya.utility.MyDialogs;
import ir.ham3da.darya.ActivityPoem;
import ir.ham3da.darya.R;


public class FavoritesAdaptor extends RecyclerView.Adapter<FavoritesAdaptor.ViewHolder> {

    private List<FavoritesPoem> favoriteList;
    private Context context1;
    GanjoorDbBrowser GanjoorDbBrowser1;
    private float textSize;

    public FavoritesAdaptor(List<FavoritesPoem> favoriteList1, Context mCtx)
    {
        this.favoriteList = favoriteList1;
        this.context1 = mCtx;
       this.GanjoorDbBrowser1 = new GanjoorDbBrowser(this.context1);
        AppSettings.Init( this.context1 );
        this.textSize = AppSettings.getTextSize();
    }


    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_favorites, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final FavoritesAdaptor.ViewHolder holder, int position) {
        final FavoritesPoem favoriteList1 = favoriteList.get(position);
        final int finalPosition = position;

        String title =  String.format(Locale.getDefault(), "%d. ", favoriteList1._Index) + favoriteList1._Title;

        holder.fav_title.setText(title);

        String referenceText  =  favoriteList1._CatTree;
        holder.fav_reference.setText(referenceText);

        holder.favCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                browsePoem(view.getContext(),favoriteList1._ID );
            }
        });

        holder.moreOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMenu(view, favoriteList1._ID, finalPosition );

            }
        });
    }

    public void browsePoem(Context context, int poemId)
    {
        Intent intent = new Intent(context, ActivityPoem.class);
        intent.putExtra("poem_id", poemId);
        context1.startActivity(intent);
        Bungee.card(context1);
    }

    public void reIndexItems()
    {
        int index = 0;

        for (FavoritesPoem favoritesPoem :favoriteList)
        {
            index++;
            favoritesPoem._Index =index;
        }
    }

    @Override
    public int getItemCount() {

        return favoriteList.size();
    }

    private void deleteItem(final int favPoemId, final int position)
    {

        MyDialogs MyDialogs1 = new MyDialogs(context1);

        FavoritesPoem favoriteList1 = favoriteList.get(position);
        String ques = String.format(context1.getString(R.string.fav_delete_ques), "<b>"+favoriteList1._Title+"</b>");

        final Dialog yesNoDialog = MyDialogs1.YesNoDialog(ques , context1.getDrawable(R.drawable.ic_delete_white_24dp) , true);

        Button noBtn = (Button) yesNoDialog.findViewById(R.id.noBtn);
        noBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                yesNoDialog.dismiss();
            }
        });

        Button yesBtn = (Button) yesNoDialog.findViewById(R.id.yesBtn);
        yesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                yesNoDialog.dismiss();
                GanjoorDbBrowser1.removeFromFavorites(favPoemId);
                favoriteList.remove(position);
                reIndexItems();
                notifyDataSetChanged();

            }
        });
        yesNoDialog.show();
    }

    public void showMenu(final View view, final int favPoemId, final int position)
    {
        //creating a popup menu
        PopupMenu popup = new PopupMenu(view.getContext(), view);
        //inflating menu from xml resource
        popup.inflate(R.menu.favorite_option_menu);

        //adding click listener
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {

                    case R.id.fav_delete:
                        //handle menu2 click
                        deleteItem(favPoemId, position);
                        break;
                    case R.id.fav_view:
                        browsePoem(view.getContext(), favPoemId);
                        break;
                }
                return false;
            }
        });

        //displaying the popup
        popup.show();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {

        public TextView fav_title;
        public TextView fav_reference;
        public ImageButton moreOptions;
        public CardView favCardView;


        public ViewHolder(View itemView)
        {
            super(itemView);

            fav_title = (TextView) itemView.findViewById(R.id.fav_title);
            fav_reference = (TextView) itemView.findViewById(R.id.fav_reference);
            moreOptions = (ImageButton) itemView.findViewById(R.id.moreOptions);
            favCardView = (CardView) itemView.findViewById(R.id.favCardView);

            fav_title.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
            fav_reference.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
        }
    }
}

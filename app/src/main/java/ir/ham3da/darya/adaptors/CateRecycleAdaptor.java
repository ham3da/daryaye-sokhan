package ir.ham3da.darya.adaptors;

import android.content.Context;
import android.content.Intent;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ir.ham3da.darya.utility.AppSettings;
import ir.ham3da.darya.Bungee;
import ir.ham3da.darya.ActivityCate;
import ir.ham3da.darya.ganjoor.GanjoorCat;
import ir.ham3da.darya.R;

public class CateRecycleAdaptor extends RecyclerView.Adapter<CateRecycleAdaptor.ViewHolder> {

    private List<GanjoorCat> GanjoorCatList;
    private Context context1;
    private float textSize;

    public CateRecycleAdaptor(List<GanjoorCat> GanjoorCatList, Context context) {
        this.GanjoorCatList = GanjoorCatList;
        this.context1 = context;
        AppSettings.Init(context);
        this.textSize = AppSettings.getTextSize();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cate, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CateRecycleAdaptor.ViewHolder holder, int position)
    {
        GanjoorCat GanjoorCat1 = GanjoorCatList.get(position);

        holder.item_text.setText(GanjoorCat1._Text);
        final String text = GanjoorCat1._Text;

        final  int cate_id = GanjoorCat1._ID;

        //holder.avatar_item.
        holder.cateCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(v.getContext(), ActivityCate.class);
                intent.putExtra("cate_id", cate_id);
                intent.putExtra("fromCate", true);
                context1.startActivity(intent);
                Bungee.card(context1);
            }
        });
    }


    @Override
    public int getItemCount()
    {
        return GanjoorCatList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView item_text;
        public ImageView avatar_item;
        public CardView cateCardView;

        public ViewHolder(View itemView)
        {
            super(itemView);

            item_text = (TextView) itemView.findViewById(R.id.item_text);
            avatar_item = (ImageView) itemView.findViewById(R.id.avatar_item);
            cateCardView = (CardView) itemView.findViewById(R.id.cateCardView);

            item_text.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);

        }
    }
}
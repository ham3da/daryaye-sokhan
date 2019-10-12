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

import ir.ham3da.darya.ActivityCate;
import ir.ham3da.darya.ActivityPoem;
import ir.ham3da.darya.Bungee;
import ir.ham3da.darya.R;
import ir.ham3da.darya.ganjoor.CateWithPoem;
import ir.ham3da.darya.ganjoor.GanjoorCat;
import ir.ham3da.darya.utility.AppFontManager;
import ir.ham3da.darya.utility.AppSettings;

public class CategoryRecycleAdaptor extends RecyclerView.Adapter<CategoryRecycleAdaptor.ViewHolder> {

    private List<CateWithPoem> CateWithPoemList;
    private Context context1;
    private float textSize;
    int fontId;

    public CategoryRecycleAdaptor(List<CateWithPoem>cateWithPoemList, Context context) {
        this.CateWithPoemList = cateWithPoemList;
        this.context1 = context;
        AppSettings.Init(context);
        this.textSize = AppSettings.getTextSize();
        fontId = AppSettings.getPoemsFont();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cate, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryRecycleAdaptor.ViewHolder holder, int position)
    {
        CateWithPoem GanjoorCat1 = CateWithPoemList.get(position);

        holder.item_text.setText(GanjoorCat1._Text);
        final String text = GanjoorCat1._Text;

        final  int item_id = GanjoorCat1._ID;
        final  int item_type = GanjoorCat1._Type;

        //holder.avatar_item.
        holder.cateCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {

                Intent intent;

                if(item_type == CateWithPoem.TYPE_CATEGORY) {
                    intent = new Intent(context1, ActivityCate.class);
                    intent.putExtra("cate_id", item_id);
                    intent.putExtra("fromCate", true);
                }
                else{
                    intent = new Intent(context1, ActivityPoem.class);
                    intent.putExtra("poem_id", item_id);
                }
                context1.startActivity(intent);
                Bungee.card(context1);
            }
        });
    }


    @Override
    public int getItemCount()
    {
        return CateWithPoemList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView item_text;
        public ImageView avatar_item;
        public CardView cateCardView;

        public ViewHolder(View itemView)
        {
            super(itemView);

            item_text = itemView.findViewById(R.id.item_text);
            avatar_item = itemView.findViewById(R.id.avatar_item);
            cateCardView = itemView.findViewById(R.id.cateCardView);

            item_text.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);

            AppFontManager.setFont(context1, item_text, fontId);

        }
    }
}

package ir.ham3da.darya.adaptors;

import android.content.Context;
import android.content.Intent;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ir.ham3da.darya.utility.AppFontManager;
import ir.ham3da.darya.utility.AppSettings;
import ir.ham3da.darya.Bungee;
import ir.ham3da.darya.ganjoor.GanjoorPoem;
import ir.ham3da.darya.ActivityPoem;
import ir.ham3da.darya.R;

public class PoemsRecycleAdaptor extends RecyclerView.Adapter<PoemsRecycleAdaptor.ViewHolder>
{

    private List<GanjoorPoem> GanjoorPoemList;
    private Context context1;

    private float textSize;
    int fontId;

    public PoemsRecycleAdaptor(List<GanjoorPoem> GanjoorPoemList, Context context)
    {
        this.GanjoorPoemList = GanjoorPoemList;
        this.context1 = context;
        AppSettings.Init(context);
        this.textSize = AppSettings.getTextSize();
        fontId = AppSettings.getPoemsFont();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cate, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(PoemsRecycleAdaptor.ViewHolder holder, int position)
    {
        GanjoorPoem GanjoorPoem1 = GanjoorPoemList.get(position);

        holder.item_text.setText(GanjoorPoem1._Title);

        final int poem_id = GanjoorPoem1._ID;

        if (!GanjoorPoem1._FirstVerse.isEmpty())
        {
            holder.item_first_verse.setText(GanjoorPoem1._FirstVerse);
            holder.item_first_verse.setVisibility(View.VISIBLE);
        }
        else
        {
            holder.item_first_verse.setVisibility(View.GONE);
        }

        //Run Poem activity
        holder.cateCardView.setOnClickListener(v -> {

            Intent intent = new Intent(v.getContext(), ActivityPoem.class);
            intent.putExtra("poem_id", poem_id);
            context1.startActivity(intent);
            Bungee.card(context1);
        });
    }


    @Override
    public int getItemCount()
    {
        return GanjoorPoemList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder
    {

        public TextView item_text;
        public TextView item_first_verse;
        public ImageView avatar_item;
        public LinearLayout cateCardView;

        public ViewHolder(View itemView)
        {
            super(itemView);

            item_text = itemView.findViewById(R.id.item_text);
            item_first_verse = itemView.findViewById(R.id.item_first_verse);
            avatar_item = itemView.findViewById(R.id.avatar_item);

            cateCardView = itemView.findViewById(R.id.cateCardView);

            item_text.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
            item_first_verse.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);

            AppFontManager.setFont(context1, item_first_verse, fontId);
            AppFontManager.setFont(context1, item_text, fontId);

        }
    }
}
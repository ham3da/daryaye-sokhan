
package ir.ham3da.darya.adaptors;

        import android.content.Context;
        import android.content.Intent;
        import android.util.TypedValue;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.ImageView;
        import android.widget.TextView;

        import androidx.cardview.widget.CardView;
        import androidx.recyclerview.widget.RecyclerView;

        import java.util.List;

        import ir.ham3da.darya.utility.AppFontManager;
        import ir.ham3da.darya.utility.AppSettings;
        import ir.ham3da.darya.Bungee;
        import ir.ham3da.darya.ganjoor.GanjoorPoem;
        import ir.ham3da.darya.ActivityPoem;
        import ir.ham3da.darya.R;

public class PoemsRecycleAdaptor extends RecyclerView.Adapter<PoemsRecycleAdaptor.ViewHolder> {

    private List<GanjoorPoem> GanjoorPoemList;
    private Context context1;

    private float textSize;
    int fontId;
    public PoemsRecycleAdaptor(List<GanjoorPoem> GanjoorPoemList, Context context) {
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

        //Run Poem activity
        holder.cateCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(v.getContext(), ActivityPoem.class);
                intent.putExtra("poem_id", poem_id);
                context1.startActivity(intent);
                Bungee.card(context1);
            }
        });
    }


    @Override
    public int getItemCount()
    {
        return GanjoorPoemList.size();
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
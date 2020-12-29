package ir.ham3da.darya.adaptors;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import ir.ham3da.darya.R;
import ir.ham3da.darya.utility.ListItemClickListener;
import ir.ham3da.darya.utility.UtilFunctions;


public class BackGroundAdapter extends RecyclerView.Adapter<BackGroundAdapter.ViewHolder>
{

    List<BackGroundItem> backGroundItemList;
    Context context;

    private ListItemClickListener itemClickListener;

    public BackGroundAdapter(Context context, List<BackGroundItem> allNewspaperList)
    {
        this.context = context;
        backGroundItemList = allNewspaperList;
    }

    public void setItemClickListener(ListItemClickListener itemClickListener)
    {
        this.itemClickListener = itemClickListener;
    }


    @Override
    public int getItemViewType(int position)
    {
        return super.getItemViewType(position);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view;

        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image, parent, false);


        return new ViewHolder(view, viewType, itemClickListener);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {

        public ImageView imgPost;
        public ConstraintLayout mCardView;
        public ListItemClickListener itemClickListener;


        public ViewHolder(View itemView, int viewType, ListItemClickListener itemClickListener)
        {
            super(itemView);

            this.itemClickListener = itemClickListener;
            mCardView = itemView.findViewById(R.id.card_view_top);
            imgPost = itemView.findViewById(R.id.post_img);
            mCardView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view)
        {
            if (itemClickListener != null)
            {
                itemClickListener.onItemClick(getLayoutPosition(), view);
            }
        }
    }

    @Override
    public int getItemCount()
    {
        return (null != backGroundItemList ? backGroundItemList.size() : 0);

    }


    @Override
    public void onBindViewHolder(BackGroundAdapter.ViewHolder mainHolder, int position)
    {
        final BackGroundItem model = backGroundItemList.get(position);
        mainHolder.imgPost.setImageResource(model.getResIDSmall());
        mainHolder.imgPost.setScaleType(ImageView.ScaleType.CENTER_CROP);

    }
}

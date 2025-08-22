package ir.ham3da.darya.adaptors;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;
import java.util.Locale;

import ir.ham3da.darya.R;
import ir.ham3da.darya.ganjoor.PoemImage;
import ir.ham3da.darya.utility.UtilFunctions;

public class PoemImageAdapter extends RecyclerView.Adapter<PoemImageAdapter.ImageViewHolder> {

    private List<PoemImage> imageList;
    private Context context;

    public PoemImageAdapter(Context context, List<PoemImage> imageList) {
        this.context = context;
        this.imageList = imageList;
    }



    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_poem_image, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        PoemImage image = imageList.get(position);

        Glide.with(context)
                .load(image.thumbnailImageUrl)
                .placeholder(R.drawable.placeholder)
                .into(holder.imageView);

        holder.imageView.setOnClickListener(v -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(image.targetPageUrl));
            context.startActivity(browserIntent);
        });

        holder.shareButton.setOnClickListener(v -> {
            String shareText = "📜 "+context.getString(R.string.Manuscript)+":\n" + image.altText + "\n\n "+context.getString(R.string.view_on)+":\n" + image.targetPageUrl;
            UtilFunctions.shareText(context, shareText);
        });

        holder.urlButton.setOnClickListener(v -> {
            UtilFunctions.openUrl(context, image.targetPageUrl);
        });

        holder.imageTitle.setText(image.altText);
        String counter_text = String.format(Locale.getDefault(), "%d / %d", position + 1, imageList.size()) ;

        holder.imageCounter.setText(counter_text);


    }

    private int currentPosition = 0;

    public void setCurrentPosition(int position) {
        currentPosition = position;
        notifyItemChanged(position);
    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView imageTitle, imageCounter;
        ImageButton shareButton, urlButton;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.poemImageView);
            imageTitle = itemView.findViewById(R.id.imageTitle);
            shareButton = itemView.findViewById(R.id.shareButton);
            urlButton = itemView.findViewById(R.id.urlButton);
            imageCounter = itemView.findViewById(R.id.imageCounter);

        }
    }
}
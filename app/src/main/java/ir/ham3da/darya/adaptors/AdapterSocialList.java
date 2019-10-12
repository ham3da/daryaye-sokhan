package ir.ham3da.darya.adaptors;

import android.content.Context;
import android.content.res.ColorStateList;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.widget.ImageViewCompat;

import java.util.ArrayList;

import ir.ham3da.darya.R;
import ir.ham3da.darya.utility.AppFontManager;
import ir.ham3da.darya.utility.LinkItem;

public class AdapterSocialList extends ArrayAdapter<LinkItem> implements View.OnClickListener{

    private ArrayList<LinkItem> dataSet;
    Context mContext;
    Integer iconColor;
    public  Boolean ChangeFont = false;

    // View lookup cache
    private static class ViewHolder {
        TextView item_text;
        ImageView icon_image;
    }

    public AdapterSocialList(ArrayList<LinkItem> data, Context context, @Nullable Integer iconColor) {
        super(context, R.layout.dialog_list_item, data);
        this.dataSet = data;
        this.mContext=context;

        this.iconColor = iconColor;

    }

    @Override
    public void onClick(View v) {

        int position=(Integer) v.getTag();

        Object object= getItem(position);
        LinkItem dataModel=(LinkItem)object;
    }

    private int lastPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        LinkItem dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.dialog_list_item, parent, false);

            viewHolder.item_text = convertView.findViewById(R.id.item_text);
            viewHolder.icon_image = convertView.findViewById(R.id.icon_image);


            if(this.iconColor != null)
            {
                viewHolder.icon_image.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(mContext, iconColor)));
            }


            result = convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }


        lastPosition = position;

        viewHolder.item_text.setText(dataModel.Text);
        if(ChangeFont)
        {
            AppFontManager.setFont(mContext, viewHolder.item_text, dataModel.ID);
        }

        viewHolder.icon_image.setImageResource(dataModel.Icon);

        // Return the completed view to render on screen
        return convertView;
    }
}
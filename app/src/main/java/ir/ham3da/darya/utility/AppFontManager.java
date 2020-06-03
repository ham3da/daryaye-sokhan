package ir.ham3da.darya.utility;

import android.content.Context;
import android.graphics.Typeface;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;

import java.util.ArrayList;

import ir.ham3da.darya.R;

public class AppFontManager {


    private static final int IRANIAN_SANS_APP_FONT = 0;
    private static final int SHABNAM_APP_FONT = 1;
    private static final int SAHEL_APP_FONT = 2;
    private static final int TANHA_APP_FONT = 3;
    private static final int VAZIR_APP_FONT = 4;
    private static final int NASKH_APP_FONT = 5;

    public AppFontManager() {

    }


    public static ArrayList<LinkItem> getFontsList(Context context) {
        ArrayList<LinkItem> links = new ArrayList<>();

        links.add(new LinkItem(0, context.getString(R.string.iransans), "", R.drawable.ic_text_fields_black_24dp));
        links.add(new LinkItem(1, context.getString(R.string.shabnam), "", R.drawable.ic_text_fields_black_24dp));
        links.add(new LinkItem(2, context.getString(R.string.sahel), "", R.drawable.ic_text_fields_black_24dp));
        links.add(new LinkItem(3, context.getString(R.string.tanha), "", R.drawable.ic_text_fields_black_24dp));
        links.add(new LinkItem(4, context.getString(R.string.vazir), "", R.drawable.ic_text_fields_black_24dp));
        links.add(new LinkItem(5, context.getString(R.string.droid_naskh), "", R.drawable.ic_text_fields_black_24dp));
        return links;
    }

    public static String getFontName(Context context, int fontId)
    {
        ArrayList<LinkItem> links = getFontsList(context);

        if (fontId < links.size())
        {
            return  links.get(fontId).Text;
        }
        else
        {
            return links.get(0).Text;
        }


    }

    public static Typeface getTypeface(Context context, int fontId) {
        Typeface font;

        switch (fontId) {

            case SHABNAM_APP_FONT:
                font = ResourcesCompat.getFont(context, R.font.shabnam_light_fa);
                break;
            case SAHEL_APP_FONT:
                font = ResourcesCompat.getFont(context, R.font.sahel_fd);
                break;
            case TANHA_APP_FONT:
                font = ResourcesCompat.getFont(context, R.font.tanha_fa);
                break;

            case VAZIR_APP_FONT:
                font = ResourcesCompat.getFont(context, R.font.vazir_light_fa);
                break;

            case NASKH_APP_FONT:
                font = ResourcesCompat.getFont(context, R.font.droid_naskh_regular);
                break;
            default:
                font = ResourcesCompat.getFont(context, R.font.iran_sans_mobile_light_fa);
                break;
        }
        return font;
    }


    /**
     * @param context
     * @param textView
     */
    public static void setFont(Context context, TextView textView, int fontId) {

        Typeface font;

        switch (fontId) {

            case SHABNAM_APP_FONT:
                font = ResourcesCompat.getFont(context, R.font.shabnam_light_fa);
                Typeface.create(font, Typeface.NORMAL);
                textView.setTypeface(font);
                break;

            case SAHEL_APP_FONT:
                font = ResourcesCompat.getFont(context, R.font.sahel_fd);
                Typeface.create(font, Typeface.NORMAL);
                textView.setTypeface(font);
                break;

            case IRANIAN_SANS_APP_FONT:
                font = ResourcesCompat.getFont(context, R.font.iran_sans_mobile_light_fa);
                Typeface.create(font, Typeface.NORMAL);
                textView.setTypeface(font);
                break;

            case TANHA_APP_FONT:
                font = ResourcesCompat.getFont(context, R.font.tanha_fa);
                Typeface.create(font, Typeface.NORMAL);
                textView.setTypeface(font);
                break;
            case VAZIR_APP_FONT:
                font = ResourcesCompat.getFont(context, R.font.vazir_light_fa);
                Typeface.create(font, Typeface.NORMAL);
                textView.setTypeface(font);
                break;
            case NASKH_APP_FONT:
                font = ResourcesCompat.getFont(context, R.font.droid_naskh_regular);
                Typeface.create(font, Typeface.NORMAL);
                textView.setTypeface(font);
                break;
        }
    }
}
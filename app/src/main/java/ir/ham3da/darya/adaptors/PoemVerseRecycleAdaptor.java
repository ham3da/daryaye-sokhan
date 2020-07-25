package ir.ham3da.darya.adaptors;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import ir.ham3da.darya.utility.AppFontManager;
import ir.ham3da.darya.utility.AppSettings;
import ir.ham3da.darya.ganjoor.GanjoorPoet;
import ir.ham3da.darya.ganjoor.GanjoorVerseB;
import ir.ham3da.darya.R;
import ir.ham3da.darya.utility.UtilFunctions;

public class PoemVerseRecycleAdaptor extends RecyclerView.Adapter<PoemVerseRecycleAdaptor.ViewHolder> {

    private List<GanjoorVerseB> GanjoorVerseList1;
    private Context context1;
    private String findStr;

    GanjoorPoet ganjoorPoet;
    UtilFunctions utilFunctions;
    Boolean indexingStatus;
    float textSize;
    String signature;
    int fontId;

    public PoemVerseRecycleAdaptor(List<GanjoorVerseB> GanjoorVerseList, GanjoorPoet poet, Context context, boolean IndexingStatus, String FindStr) {
        this.GanjoorVerseList1 = GanjoorVerseList;
        this.context1 = context;
        this.ganjoorPoet = poet;
        utilFunctions = new UtilFunctions(context);
        this.indexingStatus = IndexingStatus;
        this.findStr = FindStr;

        AppSettings.Init(context);
        this.textSize = AppSettings.getTextSize();
        signature =  AppSettings.getSignature();
        fontId = AppSettings.getPoemsFont();

    }

    public int getFirstLineContainFindStr(int vorder) {
        int index = 0;
        if (vorder > 0) {

            for (int i = 0; i < GanjoorVerseList1.size(); i++) {
                index = i;
                GanjoorVerseB verseB = GanjoorVerseList1.get(i);
                if (verseB._Vorder1 == vorder || verseB._Vorder2 == vorder) {
                    break;
                }
            }
        }
        return index;
    }

    public void setGanjoorVerseList(List<GanjoorVerseB> GanjoorVerseList) {
        if (!this.GanjoorVerseList1.isEmpty()) {
            this.GanjoorVerseList1.clear();
        }

        this.GanjoorVerseList1 = GanjoorVerseList;
        this.notifyDataSetChanged();
    }

    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_verse, parent, false);
        return new ViewHolder(v);
    }

    /**
     * @param inputString    Input string
     * @param strToHighlight String To Highlight
     * @return String Highlighted string or null
     */
    private String setHtmlHighlight(String inputString, String strToHighlight) {
        if (!findStr.trim().isEmpty()) {
            String findStrHtml = "<font color=\"#f44336\">" + strToHighlight + "</font>";
            return inputString.replace(strToHighlight, findStrHtml);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull PoemVerseRecycleAdaptor.ViewHolder holder, int position) {

        final GanjoorVerseB GanjoorVerse1 = this.GanjoorVerseList1.get(position);
        final PoemVerseRecycleAdaptor.ViewHolder finalHolder = holder;


        String text1 = GanjoorVerse1._Text1.trim();
        if (!findStr.isEmpty()) {
            if (text1.contains(findStr.trim())) {
                text1 = setHtmlHighlight(text1, findStr.trim());
            }
        }

        //Verse 1
        if (this.indexingStatus) {

            if (GanjoorVerse1._Index != -1) {
                String verseIndexStr = String.format(Locale.getDefault(), "%d", GanjoorVerse1._Index);
                text1 = verseIndexStr + ". " + text1;
            }
        }
        holder.verse_text1.setText(UtilFunctions.fromHtml(text1), TextView.BufferType.NORMAL); //Verse 1
        if (GanjoorVerse1._Centered)
        {
            holder.verse_text1.setGravity(Gravity.CENTER_HORIZONTAL);
        }
        else
        {
            if (GanjoorVerse1._Text1.equals("□")) {
                holder.verse_text1.setGravity(Gravity.LEFT);
            } else {
                holder.verse_text1.setGravity(Gravity.RIGHT);
            }
        }

        if (!GanjoorVerse1._Text2.isEmpty())
        {
            if (holder.verse_text2.getVisibility() == View.GONE) {
                holder.verse_text2.setVisibility(View.VISIBLE);
            }
            String text2 = GanjoorVerse1._Text2.trim();
            if (!findStr.isEmpty()) {
                if (text2.contains(findStr.trim())) {
                    text2 = setHtmlHighlight(text2, findStr.trim());
                }
            }
            holder.verse_text2.setText(UtilFunctions.fromHtml(text2), TextView.BufferType.NORMAL);//Verse 2

            if (GanjoorVerse1._Centered) {
                holder.verse_text2.setGravity(Gravity.CENTER_HORIZONTAL);
            } else {
                holder.verse_text2.setGravity(Gravity.LEFT);
            }

        } else {
            holder.verse_text2.setVisibility(View.GONE);
        }

        final int poemID = GanjoorVerse1._PoemID;

        finalHolder.versCardView.setSelected(GanjoorVerse1._Selected);
        holder.versCardView.setOnClickListener(v -> {
            if (finalHolder.versCardView.isSelected()) {
                GanjoorVerse1._Selected = false;
                finalHolder.versCardView.setSelected(false);

            } else {
                GanjoorVerse1._Selected = true;
                finalHolder.versCardView.setSelected(true);
            }
        });
    }

    /**
     * get ListOf Verses to Share or Copy and ...
     *
     * @return List<String> List Of Verses
     */
    public List<String> getListOfVerses() {
        List<String> stringList = new ArrayList<>();
        if (checkAnyVerseIsSelected()) {
            for (GanjoorVerseB verseB : GanjoorVerseList1) {
                if (verseB._Selected) {
                    stringList.add(verseB._Text1.trim());
                    if (!verseB._Text2.isEmpty()) {
                        stringList.add(verseB._Text2.trim());
                        stringList.add("");
                    }
                }
            }

        } else {
            for (GanjoorVerseB verseB : GanjoorVerseList1) {
                stringList.add(verseB._Text1.trim());
                if (!verseB._Text2.isEmpty()) {
                    stringList.add(verseB._Text2.trim());
                    stringList.add("");
                }
            }
        }
        if (!stringList.get(stringList.size() - 1).isEmpty()) {
            stringList.add("");
        }
        return stringList;
    }

    public void setFindStr(String findStr) {
        this.findStr = findStr;
    }

    public void copyVerses() {
        List<String> stringList = getListOfVerses();
        stringList.add("✏ «" + this.ganjoorPoet._Name + "»");
        String stringLineSeparated = TextUtils.join(System.lineSeparator(), stringList);
        utilFunctions.copyText(stringLineSeparated);
    }


    public void shareVerses() {
        List<String> stringList = getListOfVerses();
        if(!signature.isEmpty()) {
            stringList.add("«" + signature + "»");
        }
        stringList.add("✏ «" + this.ganjoorPoet._Name + "»");
        String stringLineSeparated = TextUtils.join(System.lineSeparator(), stringList);
        utilFunctions.shareText(stringLineSeparated);

    }

    private boolean checkAnyVerseIsSelected() {
        boolean isSelected = false;
        for (GanjoorVerseB verseB : GanjoorVerseList1) {
            if (verseB._Selected) {
                isSelected = true;
                break;
            }
        }
        return isSelected;
    }

    public void selectAllItem(boolean isSelectedAll) {
        try {

            for (GanjoorVerseB verseB : GanjoorVerseList1) {
                verseB._Selected = isSelectedAll;
            }
            this.notifyDataSetChanged();

        } catch (Exception e) {
            Log.w("selectAllItem", "Exception: " + e.getMessage());
        }
    }

    /**
     * fading Animation
     *
     * @param view  view object
     * @param inOut 1=> in, 2 => out
     */
    public void fadingAnimation(View view, int inOut) {

        if (inOut == 1) {
            Animation startFadeOutAnimation = AnimationUtils.loadAnimation(context1.getApplicationContext(), R.anim.fade_in_animation);
            view.startAnimation(startFadeOutAnimation);
        } else {
            Animation startFadeOutAnimation = AnimationUtils.loadAnimation(context1.getApplicationContext(), R.anim.fade_out_animation);
            view.startAnimation(startFadeOutAnimation);
        }

    }

    @Override
    public int getItemCount() {
        return this.GanjoorVerseList1.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        public LinearLayout versCardView;
        public RelativeLayout verse_layout;
        public TextView verse_text1, verse_text2;
        // public CheckBox CheckBox1;


        public ViewHolder(View itemView) {
            super(itemView);

            verse_text1 = itemView.findViewById(R.id.verse_text1);
            verse_text2 = itemView.findViewById(R.id.verse_text2);

            verse_text1.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
            verse_text2.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);


            AppFontManager.setFont(context1, verse_text1, fontId);
            AppFontManager.setFont(context1, verse_text2, fontId);

            versCardView = itemView.findViewById(R.id.versCardView);
            // CheckBox1 = (CheckBox) itemView.findViewById(R.id.verse_check_box);
            verse_layout = itemView.findViewById(R.id.verse_layout);


        }
    }
}
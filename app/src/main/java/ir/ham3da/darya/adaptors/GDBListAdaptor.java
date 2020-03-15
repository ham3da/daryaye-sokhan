package ir.ham3da.darya.adaptors;

import android.app.Dialog;
import android.content.Context;
import android.opengl.Visibility;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Locale;

import ir.ham3da.darya.ActivityCollection;
import ir.ham3da.darya.App;
import ir.ham3da.darya.utility.AppSettings;
import ir.ham3da.darya.ganjoor.GDBInfo;
import ir.ham3da.darya.ganjoor.GDBList;
import ir.ham3da.darya.ganjoor.GanjoorDbBrowser;
import ir.ham3da.darya.ganjoor.GanjoorPoet;
import ir.ham3da.darya.R;
import ir.ham3da.darya.utility.MyDialogs;

public class GDBListAdaptor extends RecyclerView.Adapter<GDBListAdaptor.ViewHolder> {

    private GDBList gDBList;
    private Context context1;
    GanjoorDbBrowser GanjoorDbBrowser1;

    ActivityCollection activityCollection;
    private float textSize;


    public GDBListAdaptor(GDBList GDBList1, Context mCtx) {
        this.gDBList = GDBList1;
        this.context1 = mCtx;
        this.GanjoorDbBrowser1 = new GanjoorDbBrowser(this.context1);
        AppSettings.Init(this.context1);
        this.textSize = AppSettings.getTextSize();

        activityCollection = (ActivityCollection) context1;
    }

    @Override
    @NonNull
    public GDBListAdaptor.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_collection, parent, false);

        return new GDBListAdaptor.ViewHolder(v);
    }


    @Override
    public void onBindViewHolder(@NonNull final GDBListAdaptor.ViewHolder holder, int position) {
        final GDBInfo GDBInfo1 = gDBList._Items.get(position);
        final int finalPosition = position;


        String title = String.format(Locale.getDefault(), "%d. ", GDBInfo1._Index) + GDBInfo1._CatName;
        String poetInfo = title + " - " + android.text.format.Formatter.formatShortFileSize(context1, GDBInfo1._FileSizeInByte);


        holder.poet_name.setText(poetInfo);

        final boolean poetInstalled = GDBInfo1._Exist;
        final boolean updateAvailable = GDBInfo1._UpdateAvailable;


        if (poetInstalled) {
            holder.imageButton_dl.setImageResource(R.drawable.ic_check_square_outlined_01);

            if (updateAvailable) {
                holder.imageButton_update.setVisibility(View.VISIBLE);
            } else {
                holder.imageButton_update.setVisibility(View.GONE);
            }


        } else {
            holder.imageButton_dl.setImageResource(R.drawable.ic_file_download_black_24dp);
            holder.imageButton_update.setVisibility(View.GONE);
        }

        holder.imageButton_dl.setOnClickListener(v -> {
            if (poetInstalled) {
                //delete poet
                deleteItem(GDBInfo1._PoetID, finalPosition);

            } else {
                //install poet
                String fileName = URLUtil.guessFileName(GDBInfo1._DownloadUrl, null, null);
                ScheduleGDB scheduleGDB = new ScheduleGDB(finalPosition, GDBInfo1._PoetID, GDBInfo1._CatName, GDBInfo1._DownloadUrl, fileName, GDBInfo1._PubDateString + "|" + GDBInfo1._FileSizeInByte, false);
                activityCollection.StartDownload(holder, scheduleGDB, false);
            }

        });

        holder.imageButton_update.setOnClickListener(v -> {
                    if (poetInstalled) {
                        String fileName = URLUtil.guessFileName(GDBInfo1._DownloadUrl, null, null);
                        ScheduleGDB scheduleGDB = new ScheduleGDB(finalPosition, GDBInfo1._PoetID, GDBInfo1._CatName, GDBInfo1._DownloadUrl, fileName, GDBInfo1._PubDateString + "|" + GDBInfo1._FileSizeInByte, true);
                        activityCollection.StartDownload(holder, scheduleGDB, false);
                    }
                }
        );


        holder.collectionCardView.setOnClickListener(v ->
        {
            if(holder.imageButton_update.getVisibility() == View.VISIBLE)
            {
                holder.imageButton_update.performClick();
            }
            else
            {
                if (!poetInstalled) {
                    //install poet
                    String fileName = URLUtil.guessFileName(GDBInfo1._DownloadUrl, null, null);
                    ScheduleGDB scheduleGDB = new ScheduleGDB(finalPosition, GDBInfo1._PoetID, GDBInfo1._CatName, GDBInfo1._DownloadUrl, fileName, GDBInfo1._PubDateString + "|" + GDBInfo1._FileSizeInByte, false);
                    activityCollection.StartDownload(holder, scheduleGDB, false);
                }
            }


        });
    }

    public void notifyNewImported(int position, int PoetID, boolean DownloadAll) {
        if (GanjoorDbBrowser1.getPoet(PoetID) != null) {
            gDBList._Items.get(position)._Exist = true;
            gDBList._Items.get(position)._UpdateAvailable = false;

            notifyItemChanged(position);

            if (DownloadAll) {

                activityCollection.DlIndex++;
                activityCollection.StartDownloadALL();
            }
        }
    }

    private void deleteItem(final int poetId, final int position) {

        MyDialogs MyDialogs1 = new MyDialogs(context1);

        String ques = String.format(context1.getString(R.string.poet_delete_ques), "<b>" + this.gDBList._Items.get(position)._CatName + "</b>");
        final Dialog yesNoDialog = MyDialogs1.YesNoDialog(ques, context1.getDrawable(R.drawable.ic_delete_white_24dp), true);
        Button noBtn = yesNoDialog.findViewById(R.id.noBtn);
        noBtn.setOnClickListener(view -> yesNoDialog.dismiss());

        Button yesBtn = yesNoDialog.findViewById(R.id.yesBtn);
        yesBtn.setOnClickListener(view -> {
            try {
                GanjoorPoet ganjoorPoet = GanjoorDbBrowser1.getPoet(poetId);
                GanjoorDbBrowser1.DeletePoet(ganjoorPoet);

                App globalVariable = (App) context1.getApplicationContext();
                globalVariable.setUpdatePoetList(true);

                gDBList._Items.get(position)._Exist = false;

                notifyItemChanged(position);

            } catch (Exception ex) {
                Log.e("delete_gdb", "err: " + ex.getMessage());
            }

            yesNoDialog.dismiss();

        });
        yesNoDialog.show();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {

        return gDBList._Items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {


        public TextView poet_name;
        public ImageButton imageButton_dl, imageButton_update;

        public CardView collectionCardView;


        public ViewHolder(View itemView) {
            super(itemView);

            poet_name = itemView.findViewById(R.id.poet_name);
            imageButton_dl = itemView.findViewById(R.id.imageButton_dl);
            imageButton_update = itemView.findViewById(R.id.imageButton_update);

            collectionCardView = itemView.findViewById(R.id.collectionCardView);
            poet_name.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);

        }
    }
}

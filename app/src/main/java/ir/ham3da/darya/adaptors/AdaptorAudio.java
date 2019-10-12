package ir.ham3da.darya.adaptors;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.List;

import ir.ham3da.darya.utility.AppSettings;
import ir.ham3da.darya.ActivityAudioCollection;
import ir.ham3da.darya.ganjoor.GanjoorDbBrowser;
import ir.ham3da.darya.R;
import ir.ham3da.darya.ganjoor.GanjoorAudioInfo;
import ir.ham3da.darya.utility.MyDialogs;

public class AdaptorAudio extends RecyclerView.Adapter<AdaptorAudio.ViewHolder> {
    private List<GanjoorAudioInfo> mainAudioList;
    Context context1;
    private float textSize;
    GanjoorDbBrowser GanjoorDbBrowser1;

    ActivityAudioCollection activityAudioCollection;

    public AdaptorAudio(List<GanjoorAudioInfo> audioList, Context mCtx) {

        this.context1 = mCtx;
        this.mainAudioList = audioList;

        AppSettings.Init(this.context1);
        this.textSize = AppSettings.getTextSize();
        this.GanjoorDbBrowser1 = new GanjoorDbBrowser(this.context1);
        activityAudioCollection = (ActivityAudioCollection) this.context1;
    }


    @Override
    @NonNull
    public AdaptorAudio.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_audio, parent, false);

        return new AdaptorAudio.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final AdaptorAudio.ViewHolder holder, final int position) {

        final GanjoorAudioInfo audioInfo = mainAudioList.get(position);


        String nameAudio = audioInfo.audio_artist +" - "+android.text.format.Formatter.formatShortFileSize(context1, audioInfo.audio_mp3bsize) ;

        holder.audio_name.setText(nameAudio);

        if (audioInfo.exist) {
            holder.imageButton_dl.setImageResource(R.drawable.ic_check_square_outlined_01);
            holder.imageButton_dl.setBackground(null);

            //  holder.imageButton_play.setVisibility(View.VISIBLE);

        } else {
            holder.imageButton_dl.setImageResource(R.drawable.ic_file_download_black_24dp);
            holder.imageButton_dl.setBackground(context1.getDrawable(R.drawable.download_btn_forground));

            // holder.imageButton_play.setVisibility(View.GONE);
        }

        holder.imageButton_dl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (audioInfo.exist) {
                    //delete audio
                    deleteItem(audioInfo, position);
                } else {
                    //download audio
                    String fileName = audioInfo.audio_fchecksum+".mp3";  //URLUtil.guessFileName(dl_url, null, null);

                    ScheduleAudio scheduleAudio = new ScheduleAudio(position, audioInfo.audio_post_ID, audioInfo.audio_mp3, fileName);
                    activityAudioCollection.StartDownload(holder, scheduleAudio);

                }
            }
        });


    }

    public void notifyNewDownloaded(int position) {

        final GanjoorAudioInfo audioInfo = mainAudioList.get(position);
        if (GanjoorDbBrowser1.addToSound(audioInfo) != -1) {
            mainAudioList.get(position).exist = true;
            notifyItemChanged(position);
        }
    }

    private void deleteItem(final GanjoorAudioInfo audioInfo, final int position) {

        MyDialogs MyDialogs1 = new MyDialogs(context1);

        String ques = String.format(context1.getString(R.string.poet_delete_poem_au), "<b>" + audioInfo.audio_artist + "</b>");
        final Dialog yesNoDialog = MyDialogs1.YesNoDialog(ques, context1.getDrawable(R.drawable.ic_delete_white_24dp), true);
        Button noBtn = yesNoDialog.findViewById(R.id.noBtn);
        noBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                yesNoDialog.dismiss();
            }
        });

        Button yesBtn = yesNoDialog.findViewById(R.id.yesBtn);
        yesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    String dl_path = AppSettings.getAudioDownloadPath(context1);
                    File auFile = new File(audioInfo.audio_mp3);
                    if (auFile.exists()) {
                        auFile.delete();
                    }

                    GanjoorDbBrowser1.deleteSound(audioInfo.audio_post_ID, audioInfo.audio_order);
                    mainAudioList.get(position).exist = false;
                    notifyItemChanged(position);
                } catch (Exception ex) {
                    Log.e("deleteAudio", "err: " + ex.getMessage());
                }
                yesNoDialog.dismiss();

            }
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

        return mainAudioList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ProgressBar circle_progress_bar;
        public TextView audio_name;
        public ImageButton imageButton_dl, imageButton_play;

        public ProgressBar horizontal_progress_bar;
        public CardView audioCardView;


        public ViewHolder(View itemView) {
            super(itemView);

            circle_progress_bar = itemView.findViewById(R.id.circle_progress_bar);
            audio_name = itemView.findViewById(R.id.audio_name);

            imageButton_dl = itemView.findViewById(R.id.imageButton_dl);
            imageButton_play = itemView.findViewById(R.id.imageButton_play);

            horizontal_progress_bar = itemView.findViewById(R.id.horizontal_progress_bar);
            audioCardView = itemView.findViewById(R.id.audioCardView);

            audio_name.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);

        }
    }

}

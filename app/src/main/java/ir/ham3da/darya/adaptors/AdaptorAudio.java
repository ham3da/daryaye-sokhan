package ir.ham3da.darya.adaptors;

import android.app.Dialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.List;
import java.util.Locale;

import ir.ham3da.darya.ganjoor.GanjoorPoem;
import ir.ham3da.darya.ganjoor.GanjoorPoet;
import ir.ham3da.darya.ganjoor.GanjoorVerseB;
import ir.ham3da.darya.utility.AppSettings;
import ir.ham3da.darya.ActivityAudioCollection;
import ir.ham3da.darya.ganjoor.GanjoorDbBrowser;
import ir.ham3da.darya.R;
import ir.ham3da.darya.ganjoor.GanjoorAudioInfo;
import ir.ham3da.darya.utility.MyDialogs;

public class AdaptorAudio extends RecyclerView.Adapter<AdaptorAudio.ViewHolder>
{
    private List<GanjoorAudioInfo> mainAudioList;
    Context context1;
    private float textSize;
    GanjoorDbBrowser GanjoorDbBrowser1;

    ActivityAudioCollection activityAudioCollection;

    private int dl_type;
    GanjoorPoet ganjoorPoet;

    public AdaptorAudio(List<GanjoorAudioInfo> audioList, Context mCtx, int dl_type, GanjoorPoet poet)
    {

        this.context1 = mCtx;
        this.mainAudioList = audioList;

        AppSettings.Init(this.context1);
        this.textSize = AppSettings.getTextSize();
        this.GanjoorDbBrowser1 = new GanjoorDbBrowser(this.context1);
        activityAudioCollection = (ActivityAudioCollection) this.context1;
        this.dl_type = dl_type;
        this.ganjoorPoet = poet;
    }


    @Override
    @NonNull
    public AdaptorAudio.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_audio, parent, false);
        return new AdaptorAudio.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final AdaptorAudio.ViewHolder holder, final int position)
    {

        final GanjoorAudioInfo audioInfo = mainAudioList.get(position);
        holder.index = audioInfo.Index;
        String IndexStr = String.format(Locale.getDefault(), "%d", audioInfo.Index);
        String nameAudio;
        if (this.dl_type == GanjoorAudioInfo.DOWNLOAD_POET_POEMS || this.dl_type == GanjoorAudioInfo.DOWNLOAD_CATE_POEMS)
        {
            GanjoorPoem ganjoorPoem = GanjoorDbBrowser1.getPoem(audioInfo.audio_post_ID);
            nameAudio = IndexStr + ". " + ganjoorPoem._Title + " - " + android.text.format.Formatter.formatShortFileSize(context1, audioInfo.audio_mp3bsize);
            nameAudio += System.lineSeparator() + context1.getString(R.string.declaimer) + ": " + audioInfo.audio_artist;
        }
        else
        {
            nameAudio = IndexStr + ". " + audioInfo.audio_artist + " - " + android.text.format.Formatter.formatShortFileSize(context1, audioInfo.audio_mp3bsize);
        }

        holder.audio_name.setText(nameAudio);
        if (audioInfo.exist)
        {
            holder.audio_name.setTextColor(ContextCompat.getColor(context1, R.color.text_dl_marked));
            holder.avatar_audio.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(context1, R.color.icon_dl_marked)));
        }
        else
        {
            holder.audio_name.setTextColor(ContextCompat.getColor(context1, R.color.textColor));
            holder.avatar_audio.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(context1, R.color.icon_dl)));
        }

        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            audioInfo.Selected = isChecked;

            int index = mainAudioList.indexOf(audioInfo);
           // mainAudioList.get(index).Selected  = isChecked;
            activityAudioCollection.showActionbar();
            setCheckedCount();
        });


        holder.imageButton_play.setOnClickListener(v ->
        {

           activityAudioCollection.playAudioThread(audioInfo);

        });

        holder.imageButton_pause.setOnClickListener(v -> {
            activityAudioCollection.pauseAudio();
        });


        holder.checkBox.setChecked(audioInfo.Selected);
        holder.audioCardView.setOnClickListener(v -> {
            if (holder.checkBox.isChecked())
            {
                holder.checkBox.setChecked(false);
            }
            else
            {
                holder.checkBox.setChecked(true);
            }

        });

    }



    public GanjoorAudioInfo getGanjoorAudioInfo(int auIndex)
    {
        return mainAudioList.get(auIndex);
    }

    public ScheduleAudio getScheduleAudio(int auIndex)
    {
        GanjoorAudioInfo audioInfo = mainAudioList.get(auIndex);
        String fileName = audioInfo.audio_fchecksum + ".mp3";
        return new ScheduleAudio(auIndex, audioInfo.audio_post_ID, audioInfo.audio_mp3, fileName);
    }


    public boolean getAudioExist(int auIndex)
    {
        GanjoorAudioInfo audioInfo = mainAudioList.get(auIndex);
        return audioInfo.exist;
    }

    public boolean isSelected(int auIndex)
    {
        GanjoorAudioInfo audioInfo = mainAudioList.get(auIndex);
        return audioInfo.Selected;
    }


    public void setCheckedCount()
    {
        int count = getCheckedCount();
        String selectedCount = String.format(Locale.getDefault(), "%d", count);
        activityAudioCollection.setActionbarTitle(selectedCount);
    }


    public boolean checkAnyAudioIsSelected()
    {
        boolean isSelected = false;
        for (int i = 0; i < this.mainAudioList.size(); i++)
        {
            GanjoorAudioInfo audioInfo = mainAudioList.get(i);
            if (audioInfo.Selected)
            {
                isSelected = true;
                break;
            }
        }
        return isSelected;
    }

    public void selectAllItem(boolean isSelectedAll)
    {
        try
        {
            for (int i = 0; i < this.mainAudioList.size(); i++)
            {
                GanjoorAudioInfo audioInfo = mainAudioList.get(i);
                audioInfo.Selected = isSelectedAll;
            }
            this.notifyDataSetChanged();

            if (isSelectedAll)
            {
                String selectedCount = String.format(Locale.getDefault(), "%d", this.mainAudioList.size());
                activityAudioCollection.setActionbarTitle(selectedCount);
            }
            else
            {
                activityAudioCollection.setActionbarTitle(String.format(Locale.getDefault(), "%d", 0));
            }


        } catch (Exception e)
        {
            Log.w("selectAllItem", "Exception: " + e.getMessage());
        }
    }


    public void notifyNewDownloaded(int position)
    {

        final GanjoorAudioInfo audioInfo = mainAudioList.get(position);
        if (GanjoorDbBrowser1.addToSound(audioInfo) != -1)
        {
            mainAudioList.get(position).exist = true;
            mainAudioList.get(position).Selected = false;
           // notifyItemChanged(position);
        }
    }

    public void deleteItemMarked(GanjoorAudioInfo audioInfo)
    {
        String dl_path = AppSettings.getAudioDownloadPath(context1);
        File auFile = new File(dl_path + "/" + audioInfo.audio_fchecksum + ".mp3");
        if (audioInfo.Selected)
        {
            if (auFile.exists())
            {
                boolean delete = auFile.delete();
            }
            GanjoorDbBrowser1.deleteSound(audioInfo.audio_post_ID, audioInfo.audio_order);
            int pos = mainAudioList.indexOf(audioInfo);
            mainAudioList.get(pos).exist = false;
            mainAudioList.get(pos).Selected = false;
        }
    }

    private void deleteItem(final GanjoorAudioInfo audioInfo, final int position)
    {

        MyDialogs MyDialogs1 = new MyDialogs(context1);

        String ques = String.format(context1.getString(R.string.poet_delete_poem_au), "<b>" + audioInfo.audio_artist + "</b>");
        final Dialog yesNoDialog = MyDialogs1.YesNoDialog(ques, ContextCompat.getDrawable(context1, R.drawable.ic_delete_white_24dp), true);
        Button noBtn = yesNoDialog.findViewById(R.id.noBtn);
        noBtn.setOnClickListener(view -> yesNoDialog.dismiss());

        Button yesBtn = yesNoDialog.findViewById(R.id.yesBtn);
        yesBtn.setOnClickListener(view -> {

            try
            {
                String dl_path = AppSettings.getAudioDownloadPath(context1);
                File auFile;
                auFile = new File(audioInfo.audio_mp3);
                if (auFile.exists())
                {
                    final boolean delete = auFile.delete();
                }

                GanjoorDbBrowser1.deleteSound(audioInfo.audio_post_ID, audioInfo.audio_order);
                mainAudioList.get(position).exist = false;
                notifyItemChanged(position);
            } catch (Exception ex)
            {
                Log.e("deleteAudio", "err: " + ex.getMessage());
            }
            yesNoDialog.dismiss();

        });
        yesNoDialog.show();


    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public int getItemViewType(int position)
    {
        return position;
    }

    @Override
    public int getItemCount()
    {
        return mainAudioList.size();
    }

    public int getCheckedCount()
    {
        if (this.mainAudioList != null)
        {
            int countCk = 0;
            for (int i = 0; i < this.mainAudioList.size(); i++)
            {
                GanjoorAudioInfo audioInfo = mainAudioList.get(i);
                if (audioInfo.Selected)
                {
                    countCk++;
                }
            }
            return countCk;
        }
        else
        {
            return 0;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {

        // public ProgressBar circle_progress_bar;
        public TextView audio_name;
        public ImageButton imageButton_play, imageButton_pause;
        public CheckBox checkBox;

        public ProgressBar progress_bar;
        public LinearLayout audioCardView;
        public int index;
        public ImageView avatar_audio;

        public ViewHolder(View itemView)
        {
            super(itemView);

            audio_name = itemView.findViewById(R.id.audio_name);

            avatar_audio = itemView.findViewById(R.id.avatar_audio);
            imageButton_play = itemView.findViewById(R.id.imageButton_play);
            imageButton_pause = itemView.findViewById(R.id.imageButton_pause);

            progress_bar = itemView.findViewById(R.id.progress_bar);
            audioCardView = itemView.findViewById(R.id.audioCardView);

            checkBox = itemView.findViewById(R.id.checkBox);

            audio_name.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);

        }
    }

}

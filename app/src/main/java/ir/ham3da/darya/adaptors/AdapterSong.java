package ir.ham3da.darya.adaptors;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ir.ham3da.darya.R;
import ir.ham3da.darya.ganjoor.GanjoorSongInfo;

public class AdapterSong extends RecyclerView.Adapter<AdapterSong.ViewHolder> {
    private final List<GanjoorSongInfo> songList;
    private final Context context;

    public AdapterSong(List<GanjoorSongInfo> songList, Context context) {
        this.songList = songList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_audio, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        GanjoorSongInfo song = songList.get(position);
        String title = song.trackName + " - " + song.albumName + " - " + song.artistName;
        holder.audio_name.setText(title);

        holder.imageButton_play.setOnClickListener(v -> {
            Toast.makeText(context, R.string.opening_play_link, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(song.trackUrl));
            context.startActivity(intent);
        });


        holder.imageButton_pause.setVisibility(View.GONE);
        holder.checkBox.setVisibility(View.GONE);
        holder.progress_bar.setVisibility(View.GONE);

    }

    @Override
    public int getItemCount() {
        return songList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView audio_name;
        ImageButton imageButton_play, imageButton_pause;
        CheckBox checkBox;
        ProgressBar progress_bar;
        ImageView avatar_audio;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            audio_name = itemView.findViewById(R.id.audio_name);
            imageButton_pause = itemView.findViewById(R.id.imageButton_pause);
            imageButton_play = itemView.findViewById(R.id.imageButton_play);
            imageButton_play.setImageResource(R.drawable.baseline_open_in_browser_24);
            checkBox = itemView.findViewById(R.id.checkBox);
            progress_bar = itemView.findViewById(R.id.progress_bar);

            avatar_audio = itemView.findViewById(R.id.avatar_audio);
            avatar_audio.setImageResource(R.drawable.baseline_music_note_24);
        }
    }
}
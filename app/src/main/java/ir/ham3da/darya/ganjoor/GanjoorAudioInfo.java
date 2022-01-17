package ir.ham3da.darya.ganjoor;


import java.util.Date;

public class GanjoorAudioInfo {

    public int Index;
    public int audio_post_ID;
    public int audio_order;

    public String audio_xml;
    public String audio_ogg;
    public String audio_mp3;
    public String audio_title;
    public String audio_artist;
    public String audio_artist_url;
    public String audio_src;
    public String audio_src_url;
    public String audio_guid;
    public String audio_fchecksum;

    public int audio_mp3bsize;
    public int audio_oggbsize;

    public Date audio_date;

    public boolean exist;
    public static final int DOWNLOAD_POEM = 1;
    public static final int DOWNLOAD_POET_POEMS= 2;
    public static final int DOWNLOAD_ALL_POEM = 3;
    public static final int DOWNLOAD_CATE_POEMS= 4;


    public Boolean Selected;



    public GanjoorAudioInfo(int audio_post_ID, int audio_order, String audio_xml,
                            String audio_ogg, String audio_mp3, String audio_title, String audio_artist,
                            String audio_artist_url, String audio_src, String audio_src_url, String audio_guid,
                            String audio_fchecksum, int audio_mp3bsize, int audio_oggbsize, Date audio_date,
                            boolean exist, int index, boolean selected) {
        this.audio_post_ID = audio_post_ID;
        this.audio_order = audio_order;

        this.audio_xml = audio_xml;

        this.audio_ogg = audio_ogg;
        this.audio_mp3 = audio_mp3;
        this.audio_title = audio_title;
        this.audio_artist = audio_artist;
        this.audio_artist_url = audio_artist_url;
        this.audio_src = audio_src;
        this.audio_src_url = audio_src_url;
        this.audio_guid = audio_guid;
        this.audio_fchecksum = audio_fchecksum;

        this.audio_mp3bsize = audio_mp3bsize;
        this.audio_oggbsize = audio_oggbsize;

        this.audio_date = audio_date;
        this.exist = exist;
        this.Index = index;
        this.Selected = selected;
    }
}

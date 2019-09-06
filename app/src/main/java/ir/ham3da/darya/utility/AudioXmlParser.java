package ir.ham3da.darya.utility;

import android.content.Context;
import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ir.ham3da.darya.ganjoor.GanjoorAudioInfo;

public class AudioXmlParser {

    Context mContext;
    public AudioXmlParser(Context context)
    {
        mContext = context;
    }


    public List<GanjoorAudioInfo> getAudioList(InputStream XmlStrim) throws XmlPullParserException, IOException
    {

        List<GanjoorAudioInfo> audioList = new ArrayList<>();
        audioList = parseXML(XmlStrim);

        return audioList;
    }

    private static final String ns = null;

    public List<GanjoorAudioInfo> parseXML(InputStream in) throws XmlPullParserException, IOException {

        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            return readItems(parser);
        }
        catch (Exception ex)
        {
            Log.e("parseXML", "err: " + ex.getMessage() );
            in.close();
            return null;
        }

    }

    private List<GanjoorAudioInfo> readItems(XmlPullParser parser) throws XmlPullParserException, IOException {
        List<GanjoorAudioInfo> entries = new ArrayList<>();
        parser.require(XmlPullParser.START_TAG, ns, "DesktopGanjoorPoemAudioList");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("PoemAudio")) {
                entries.add(readEntry(parser));
            } else {
                skip(parser);
            }
        }
        return entries;
    }


    private GanjoorAudioInfo readEntry(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "PoemAudio");


        int audio_post_ID = 0;
        int audio_order = 0;

        String audio_xml = null;
        String audio_ogg = null;
        String audio_mp3 = null;
        String audio_title = null;
        String audio_artist = null;
        String audio_artist_url = null;
        String audio_src = null;
        String audio_src_url = null;
        String audio_guid = null;
        String audio_fchecksum = null;

        int audio_mp3bsize = 0;
        int audio_oggbsize = 0;

        Date audio_date = null;

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();

            switch (name) {
                case "audio_post_ID":
                    audio_post_ID = readItemInt(parser, name);
                    break;
                case "audio_order":
                    audio_order = readItemInt(parser, name);
                    break;

                case "audio_xml":
                    audio_xml = readItemString(parser, name);
                    break;

                case "audio_ogg":
                    audio_ogg = readItemString(parser, name);
                    break;

                case "audio_mp3":
                    audio_mp3 = readItemString(parser, name);
                    break;

                case "audio_title":
                    audio_title = readItemString(parser, name);
                    break;

                case "audio_artist":
                    audio_artist = readItemString(parser, name);
                    break;

                case "audio_artist_url":
                    audio_artist_url = readItemString(parser, name);
                    break;

                case "audio_src":
                    audio_src = readItemString(parser, name);
                    break;

                case "audio_src_url":
                    audio_src_url = readItemString(parser, name);
                    break;

                case "audio_guid":
                    audio_guid = readItemString(parser, name);
                    break;

                case "audio_fchecksum":
                    audio_fchecksum = readItemString(parser, name);
                    break;

                case "audio_mp3bsize":
                    audio_mp3bsize = readItemInt(parser, name);
                    break;

                case "audio_oggbsize":
                    audio_oggbsize = readItemInt(parser, name);
                    break;

                case "audio_date":

                    String audio_date_str = readItemString(parser, name);
                    SimpleDateFormat sfd = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                    try {
                        audio_date = sfd.parse(audio_date_str);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    break;

                default:
                    skip(parser);

            }


        }
        return new GanjoorAudioInfo(audio_post_ID, audio_order, audio_xml,
                audio_ogg, audio_mp3, audio_title, audio_artist,
                audio_artist_url, audio_src, audio_src_url, audio_guid,
                audio_fchecksum, audio_mp3bsize, audio_oggbsize, audio_date, false);
    }


    private String readItemString(XmlPullParser parser, String TagName) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, TagName);
        String value = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, TagName);
        return value;
    }

    private int readItemInt(XmlPullParser parser, String TagName) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, TagName);
        int value = Integer.valueOf(readText(parser));
        parser.require(XmlPullParser.END_TAG, ns, TagName);
        return value;
    }


    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }
}

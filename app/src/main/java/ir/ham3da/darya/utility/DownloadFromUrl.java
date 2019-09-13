package ir.ham3da.darya.utility;

import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadFromUrl
{
    private static String TAG = "DownloadFromUrl";

    public static boolean isOnline(Context ctx)
    {
        return  UtilFunctions.isNetworkConnected(ctx);
    }
    // Given a URL, establishes an HttpUrlConnection and retrieves
    // the web page content as a InputStream, which it returns as
    // a string.
    public static String downloadDataFromUrl(String urlStr) throws IOException {
        InputStream inputStream1 = null;

        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(10000);
        conn.setConnectTimeout(15000);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);

        // Starts the query
        conn.connect(); // calling the web address

        int response = conn.getResponseCode();

        inputStream1 = conn.getInputStream();
        // Convert the InputStream into a string
        String contentAsString = readInputStream(inputStream1);
        conn.disconnect();
        return contentAsString;

        // Makes sure that the InputStream is closed after the app is
        // finished using it.
    }

    // Reads an InputStream and converts it to a String.
    public static String readInputStream(InputStream stream) throws IOException {
        int n = 0;
        char[] buffer = new char[1024 * 4];
        InputStreamReader reader = new InputStreamReader(stream, "UTF8");
        StringWriter writer = new StringWriter();
        while (-1 != (n = reader.read(buffer))) {
            writer.write(buffer, 0, n);
        }
        reader.close();
        return writer.toString();
    }
}
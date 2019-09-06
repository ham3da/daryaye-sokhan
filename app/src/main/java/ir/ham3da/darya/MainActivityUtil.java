package ir.ham3da.darya;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import ir.ham3da.darya.utility.AppSettings;
import ir.ham3da.darya.utility.CopyDatabaseTask;

public class MainActivityUtil
{
    private Context mCtx;
    private ClipboardManager myClipboard;
    private ClipData myClip;
    private int booksCount = 0;
    /**
     * google play = 0, cafebazaar = 1, myket = 2 ,samsung apps =3
     */
    private int Store = 0;

    public MainActivityUtil(Context mCtx) {

        this.mCtx = mCtx;
    }


    public static boolean checkExists(String filePath) {
        File DbFile = new File(filePath);
        return DbFile.exists();
    }

    public void extractGangoorDB(String filePath) {
        boolean check_exists = checkExists(filePath);
        if (!check_exists) {

            try {

                InputStream fileToCopy = mCtx.getAssets().open(AppSettings.getDatabaseName());
                CopyDatabaseTask copyDatabaseTask1 = new CopyDatabaseTask(mCtx, fileToCopy, filePath);
                copyDatabaseTask1.execute(filePath);
            } catch (IOException e) {
                Log.e("ham3da_library", "extractGanjoor_db err: " + e.getMessage());
            }


        }
    }


}

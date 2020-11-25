package ir.ham3da.darya.utility;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import ir.ham3da.darya.ActivityMain;
import ir.ham3da.darya.R;

public class CopyDatabaseTask
{
    private final Context context1;
    private final CustomProgress customProgressDlg;
    private final String  destFile;
    private final InputStream sourceFile;
    Handler mainHandler = new Handler(Looper.getMainLooper());
    public CopyDatabaseTask(Context contextActivityMain, InputStream sourceFile, String destFile)
    {
        this.context1 = contextActivityMain;
        this.customProgressDlg = new CustomProgress(context1);
        this.destFile = destFile;
        this.sourceFile = sourceFile;

    }

    public void Execute() {

        this.customProgressDlg.showProgress(this.context1.getString(R.string.prepar_database), "0 %" , false, false, false);
        this.customProgressDlg.setProgress(0);
        Thread copyingThread = new Thread(this::doInBackground);
        copyingThread.start();

       // new Handler().postDelayed(this::doInBackground, 500);
    }

    /**
     * Copy file in background thread
     * */

    protected void doInBackground()
    {

        long totalBytesCopied = 0;
        try {

           String dbName = AppSettings.getDatabaseName();

            SQLiteDatabase db = context1.openOrCreateDatabase(dbName, Context.MODE_PRIVATE, null);
            db.close();

            InputStream mInput = this.sourceFile;
            OutputStream mOutput = new FileOutputStream(this.destFile);

            byte[] mBuffer = new byte[1000000];
            int mLength;

            long expectedBytes = mInput.available();
            while ((mLength = mInput.read(mBuffer)) > 0) {
                totalBytesCopied += mLength;
                mOutput.write(mBuffer, 0, mLength);

                int progress = (int) Math.round(((double) totalBytesCopied / (double) expectedBytes) * 100);
                if(progress > 0)
                {
                    publishProgress(progress);
                }

            }
            mOutput.flush();
            mOutput.close();
            mInput.close();
            completed(100);
        }
        catch (IOException e)
        {
            this.customProgressDlg.dismiss();
            String exception = UtilFunctions.getStackTrace(e);
            Log.e("doInBackground", exception );
        }
    }

    protected void publishProgress(Integer progress)
    {
        Log.e("copy", "publishProgress: "+ progress);
        if(customProgressDlg != null)
        {
            mainHandler.post(() -> customProgressDlg.setProgress(progress));
        }

    }

    /**
     * After completing background task
     *
     * **/

    protected void completed(Integer result)
    {

        //### Load Poets List
        ActivityMain activityMain = (ActivityMain) this.context1;
        activityMain.runOnUiThread(() -> activityMain.LoadDBFirstTime(customProgressDlg));

    }


}
package ir.ham3da.darya.utility;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;

import ir.ham3da.darya.ActivityMain;
import ir.ham3da.darya.R;
import ir.ham3da.darya.utility.AppSettings;
import ir.ham3da.darya.utility.CustomProgress;
import ir.ham3da.darya.utility.UtilFunctions;

public class CopyDatabaseTask extends AsyncTask<String, Integer, Long>
{
    private WeakReference<ActivityMain> activityMain;

    private Context context1;
    private CustomProgress customProgressDlg;
    private String  destFile;
    private InputStream sourceFile;

    public CopyDatabaseTask(Context contextActivityMain, InputStream sourceFile, String destFile)
    {
        this.context1 = contextActivityMain;
        this.customProgressDlg = new CustomProgress(context1);
        this.destFile = destFile;
        this.sourceFile = sourceFile;

    }

    /**
     * Before starting background thread
     * Show Progress Bar Dialog
     * */
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        this.customProgressDlg.showProgress(this.context1.getString(R.string.prepar_database), "0 %" , false, false, false);
        this.customProgressDlg.setProgress(0);
    }

    /**
     * Copy file in background thread
     * */
    @Override
    protected Long doInBackground(String... files)
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
                int progress = (int) Math.round(((double) totalBytesCopied / (double) expectedBytes) * 100);
                if(progress > 0) {
                    publishProgress(progress);
                }
                mOutput.write(mBuffer, 0, mLength);
            }
            mOutput.flush();
            mOutput.close();
            mInput.close();
        }
        catch (IOException e)
        {
            this.customProgressDlg.dismiss();
            String exception = UtilFunctions.getStackTrace(e);
            Log.e("doInBackground", exception );


        }
        return totalBytesCopied;
    }

    /**
     * Updating progress bar
     * */
    @Override
    protected void onProgressUpdate(Integer... progress)
    {
        this.customProgressDlg.setProgress(progress[0]);
    }

    /**
     * After completing background task
     *
     * **/
    @Override
    protected void onPostExecute(Long result)
    {
        super.onPostExecute(result);
        //### Load Poets List
        ActivityMain activityMain = (ActivityMain) this.context1;

//        MainPoetsFragment fragment = new MainPoetsFragment();
//        MainPoetsFragment currentFragment = (MainPoetsFragment)((AppCompatActivity) context).getSupportFragmentManager().findFragmentByTag(this.fragmentTag);
//
        activityMain.LoadDBFirstTime(this.customProgressDlg);
    }


}
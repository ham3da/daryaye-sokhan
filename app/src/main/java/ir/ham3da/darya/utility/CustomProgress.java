package ir.ham3da.darya.utility;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Locale;

import ir.ham3da.darya.R;

public class CustomProgress {

    private Dialog mDialog;

    public CustomProgress(Context context)
    {
        this.mDialog = new Dialog(context);
    }

    /**
     *
     * @param vId View id
     * @return View
     */
    public View getView(int vId)
    {
        return  mDialog.findViewById(vId);
    }

    /**
     *
     * @param title title
     * @param message message to show
     * @param cancelable cancelable
     * @param hideProgress hideProgress
     */
    public void showProgress(String title, String message, boolean cancelable, boolean hideProgress, boolean indeterminate)
    {
        // no tile for the dialog
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.setContentView(R.layout.prograss_bar_dialog);
        ProgressBar  mProgressBar = mDialog.findViewById(R.id.progress_bar);
        TextView progressText = mDialog.findViewById(R.id.progress_text);
        TextView progress_title = mDialog.findViewById(R.id.progress_title);

        progress_title.setText(title);
        progressText.setText(message);
        mProgressBar.setProgress(0);

        progress_title.setVisibility(View.VISIBLE);
        progressText.setVisibility(View.VISIBLE);

        if(hideProgress)
        {
            mProgressBar.setVisibility(View.INVISIBLE);
        }
        else
        {
            mProgressBar.setVisibility(View.VISIBLE);
            mProgressBar.setIndeterminate(indeterminate);
        }

        // you can change or add this line according to your need
        mDialog.setCancelable(cancelable);
        mDialog.setCanceledOnTouchOutside(cancelable);
        mDialog.show();
    }

    public void setProgress(int percent)
    {
        TextView progressText = this.mDialog.findViewById(R.id.progress_text);
        String percentStr = String.format(Locale.getDefault(), "%d", percent)+ " %";
        progressText.setText(percentStr);
        ProgressBar  mProgressBar = this.mDialog.findViewById(R.id.progress_bar);
        mProgressBar.setProgress(percent);
    }

    public void setProgress(int percent, String Text1, String Text2, String Description )
    {
        TextView progressText = mDialog.findViewById(R.id.progress_text);
        TextView progress_description = mDialog.findViewById(R.id.progress_description);
        TextView progressText1 = mDialog.findViewById(R.id.progress_text1);
        TextView progressText2 = mDialog.findViewById(R.id.progress_text2);

        String percentStr = String.format(Locale.getDefault(), "%d", percent)+ " %";
        progressText.setText(percentStr);

        if(!Text1.isEmpty())
        {
            progressText1.setText(Text1);
        }

        if(!Text2.isEmpty())
        {
            progressText2.setText(Text2);
        }

        if(!Description.isEmpty())
        {
            progress_description.setText(Description);
        }

        ProgressBar  mProgressBar = mDialog.findViewById(R.id.progress_bar);
        mProgressBar.setProgress(percent);
    }

    public void setProgress(int percent, String Text1, String Text2 )
    {
        TextView progressText = mDialog.findViewById(R.id.progress_text);
        TextView progressText1 = mDialog.findViewById(R.id.progress_text1);
        TextView progressText2 = mDialog.findViewById(R.id.progress_text2);

        String percentStr = String.format(Locale.getDefault(), "%d", percent)+ " %";
        progressText.setText(percentStr);

        if(!Text1.isEmpty())
        {
            progressText1.setText(Text1);
        }

        if(!Text2.isEmpty())
        {
            progressText2.setText(Text2);
        }

        ProgressBar  mProgressBar = mDialog.findViewById(R.id.progress_bar);
        mProgressBar.setProgress(percent);
    }


    public void dismiss()
    {
        if (mDialog != null) {
            mDialog.dismiss();
            mDialog = null;
        }
    }
}
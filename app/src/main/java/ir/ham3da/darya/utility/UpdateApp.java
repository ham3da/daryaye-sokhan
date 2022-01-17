package ir.ham3da.darya.utility;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;

import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.Task;

import ir.ham3da.darya.R;

public class UpdateApp
{
    Context mContext;
    private static final String TAG = "UpdateApp";

    public UpdateApp(Context context) {
        mContext = context;
    }

    public void initUpdate()
    {

        if(UtilFunctions.isGooglePlayVersion())
        {
            checkGooglePlayForUpdate();
        }
    }
    private void checkGooglePlayForUpdate()
    {

        AppUpdateManager appUpdateManager = AppUpdateManagerFactory.create(mContext);
        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();

        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo ->
        {
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE))
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle(R.string.update);
                builder.setMessage(R.string.update_app_des);
                builder.setPositiveButton(
                        R.string.update, (dialog, which) -> {
                            final String appPackageName = mContext.getPackageName();
                            try
                            {
                                mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                            } catch (ActivityNotFoundException anfe)
                            {
                                mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                            }
                        });
                builder.setNegativeButton(R.string.cancel, (dialog, which) -> {dialog.dismiss();} );
                builder.setCancelable(false);
                builder.show();
            }
        });
        appUpdateInfoTask.addOnFailureListener(e -> Log.e(TAG, "onFailure: "+e.getMessage() ));

    }
}
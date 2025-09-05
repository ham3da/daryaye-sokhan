package ir.ham3da.darya.utility;
import android.content.Context;
import android.util.Log;
import androidx.appcompat.app.AlertDialog;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import ir.ham3da.darya.BuildConfig;

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
           checkGithubForUpdate();
    }

    private void showUpdateDialog(String versionName, String changelog, String downloadUrl) {
        new AlertDialog.Builder(mContext)
                .setTitle(R.string.update_title)
                .setMessage(mContext.getString(R.string.changes)+"\n\n" + changelog)
                .setPositiveButton(R.string.update, (dialog, which) -> {
                    UtilFunctions.openUrl(mContext, downloadUrl);
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }


    private void checkGithubForUpdate()
    {

        String url = "https://raw.githubusercontent.com/ham3da/daryaye-sokhan/refs/heads/master/version.json";

        RequestQueue queue = Volley.newRequestQueue(mContext);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        int latestCode = response.getInt("versionCode");
                        String latestName = response.getString("versionName");
                        JSONArray changelogArray = response.getJSONArray("changelog");
                        String downloadUrl = response.getString("downloadUrl");

                        int currentCode = BuildConfig.VERSION_CODE;
                        Log.i(TAG, "latestCode: "+latestCode);
                        if (latestCode > currentCode) {
                            StringBuilder changelogBuilder = new StringBuilder();
                            for (int i = 0; i < changelogArray.length(); i++) {
                                changelogBuilder.append("• ").append(changelogArray.getString(i)).append("\n");
                            }
                            showUpdateDialog(latestName, changelogBuilder.toString(), downloadUrl);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Log.e(TAG, "Error checking new version", error)
        );

        queue.add(request);

    }
}
package ir.ham3da.darya;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;

import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;

import java.util.List;

import android.widget.AdapterView.OnItemSelectedListener;

import androidx.recyclerview.widget.LinearLayoutManager;

import ir.ham3da.darya.adaptors.PoetSpinnerAdapter;
import ir.ham3da.darya.ganjoor.GanjoorDbBrowser;
import ir.ham3da.darya.ganjoor.GanjoorPoet;
import ir.ham3da.darya.utility.AppSettings;

public class SearchLimitsDialog {

    private Dialog mDialog;
    private Spinner poetSpinner;

    private GanjoorPoet SelectedPoet;

    ActivitySearch activitySearch;

    public int selectedPoetID = 0;
    private Context mainContext;

    public SearchLimitsDialog(Context context)
    {
        activitySearch = (ActivitySearch) context;
        mainContext = context;
    }

    /**
     * Show search limits dialog
     * @param cancelable boolean
     */
    public void ShowLimitsDialog(boolean cancelable) {

        this.mDialog = new Dialog(mainContext);


        this.mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.mDialog.setContentView(R.layout.search_limits);

        this.poetSpinner = this.mDialog.findViewById(R.id.poet_list_spinner);
        Button okBtn = this.mDialog.findViewById(R.id.okBtn);
        Button cancelBtn = this.mDialog.findViewById(R.id.cancelBtn);


        final GanjoorDbBrowser GanjoorDbBrowser1 = new GanjoorDbBrowser(mainContext);
        List<GanjoorPoet> poetList = GanjoorDbBrowser1.getPoets();

        GanjoorPoet GanjoorPoet1 = new GanjoorPoet(-1, mainContext.getString(R.string.all), -1, "", "");
        poetList.add(0, GanjoorPoet1);

        PoetSpinnerAdapter adapter = new PoetSpinnerAdapter(mainContext, poetList);
        poetSpinner.setAdapter(adapter);

        mDialog.setCancelable(cancelable);
        mDialog.setCanceledOnTouchOutside(cancelable);
        mDialog.show();


        okBtn.setOnClickListener(view -> {
            //save settings and exit
            AppSettings.setSearchSelectedPoet(selectedPoetID);
            activitySearch.setSearchLimitsText();
            dismiss();
        });

        cancelBtn.setOnClickListener(view -> dismiss());

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mainContext);


        poetSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                SelectedPoet = (GanjoorPoet) adapterView.getItemAtPosition(i);

                selectedPoetID = SelectedPoet._ID;

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        int savedPoetId = AppSettings.getSearchSelectedPoet();

        for (int i = 0; i < poetList.size(); i++) {
            GanjoorPoet poet = poetList.get(i);
            if (poet._ID == savedPoetId) {
                poetSpinner.setSelection(i);
                break;
            }
        }

    }

    /**
     * Dismiss dialog
     */
    public void dismiss() {

        if (mDialog != null) {
            mDialog.dismiss();
            mDialog = null;
        }
    }

}

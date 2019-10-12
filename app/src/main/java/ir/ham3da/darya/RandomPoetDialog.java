package ir.ham3da.darya;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import java.util.List;
import ir.ham3da.darya.adaptors.PoetSpinnerAdapter;
import ir.ham3da.darya.ganjoor.GanjoorCat;
import ir.ham3da.darya.ganjoor.GanjoorDbBrowser;
import ir.ham3da.darya.ganjoor.GanjoorPoet;
import ir.ham3da.darya.utility.AppSettings;



public class RandomPoetDialog {

    private Dialog mDialog;
    private Spinner poetSpinner;

    private GanjoorPoet SelectedPoet;

    ActivitySettings activitySettings;

    public int selectedPoetCatID = 0;
    private Context mainContext;


    public RandomPoetDialog(Context context)
    {
        activitySettings = (ActivitySettings) context;
        mainContext = context;
    }

    /**
     * Show search limits dialog
     * @param cancelable boolean
     */
    public void ShowLimitsDialog(boolean cancelable) {

        try {
            this.mDialog = new Dialog(mainContext);


            this.mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            this.mDialog.setContentView(R.layout.search_limits);

            this.poetSpinner = this.mDialog.findViewById(R.id.poet_list_spinner);
            Button okBtn = this.mDialog.findViewById(R.id.okBtn);
            Button cancelBtn = this.mDialog.findViewById(R.id.cancelBtn);

           TextView progress_title = this.mDialog.findViewById(R.id.progress_title);
            progress_title.setText(R.string.random_poetry_scope);

            final GanjoorDbBrowser GanjoorDbBrowser1 = new GanjoorDbBrowser(mainContext);
            List<GanjoorPoet> poetList = GanjoorDbBrowser1.getPoets();

            GanjoorPoet GanjoorPoet1 = new GanjoorPoet(-1, mainContext.getString(R.string.all), -1, "", "");
            poetList.add(0, GanjoorPoet1);

            PoetSpinnerAdapter adapter = new PoetSpinnerAdapter(mainContext, poetList);
            poetSpinner.setAdapter(adapter);

            mDialog.setCancelable(cancelable);
            mDialog.setCanceledOnTouchOutside(cancelable);
            mDialog.show();


            okBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //save settings and exit
                    AppSettings.setRandomSelectedCategories(String.valueOf(selectedPoetCatID));
                    activitySettings.setRandomPoetText();
                    dismiss();
                }
            });

            cancelBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dismiss();
                }
            });

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mainContext);


            poetSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                    SelectedPoet = (GanjoorPoet) adapterView.getItemAtPosition(i);
                    selectedPoetCatID = SelectedPoet._CatID;

                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });


            String savedPoetId = AppSettings.getRandomSelectedCategories();
            GanjoorCat cat = GanjoorDbBrowser1.getCat(Integer.valueOf(savedPoetId));

            int p_id = 0;
            if (cat != null) {
                p_id = cat._PoetID;
            }

            for (int i = 0; i < poetList.size(); i++) {
                GanjoorPoet poet = poetList.get(i);
                if (poet._ID == p_id) {
                    poetSpinner.setSelection(i);
                    break;
                }
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
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

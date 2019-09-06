package ir.ham3da.darya.utility;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;


import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;


import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import ir.ham3da.darya.BuildConfig;
import ir.ham3da.darya.R;
import ir.ham3da.darya.adaptors.AdapterSocialList;


public class MyDialogs {
    public int TopColorRes = R.color.teal;
    public int ButtonsColorRes = R.color.darkDeepOrange;
    public int Icon = R.drawable.ic_star_border_white_36dp;
    private Context context;

    public String price;
    public String app_name;
    public String version = BuildConfig.VERSION_NAME;


    public MyDialogs(Context mCtx) {
        this.context = mCtx;
        price = mCtx.getString(R.string.free);
        app_name = mCtx.getString(R.string.app_name);
    }

    public void showMessage(int title, String message) {

        AlertDialog alertDialog = new AlertDialog.Builder(this.context).create();
        alertDialog.setTitle(title);
        alertDialog.setIcon(this.Icon);
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, this.context.getText(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        alertDialog.show();

    }

    public String developer() {
        return this.context.getString(R.string.developer_name);
    }


    public void socialNetworks() {

        final ArrayList<LinkItem> links = new ArrayList<>();

        links.add(new LinkItem(0, context.getString(R.string.app_telegram_channel), "https://t.me/daryaye_sokhan", R.drawable.ic_telegram));
        links.add(new LinkItem(1, context.getString(R.string.app_instagram), "https://www.instagram.com/daryaye_sokhan/", R.drawable.ic_instagram));

// setup the alert builder

        String title = this.context.getString(R.string.app_social_networks);
        String appMame = this.context.getString(R.string.app_name);


        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_listview);

        dialog.setCancelable(true);


        TextView dlg_title = (TextView) dialog.findViewById(R.id.dlg_title);
        ImageView dialog_icon = (ImageView) dialog.findViewById(R.id.dialog_icon);

        dlg_title.setText(String.format(title, appMame));
        dialog_icon.setImageResource(R.drawable.ic_group);


        Button okBtn = (Button) dialog.findViewById(R.id.okBtn);
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
            }
        });

        ListView listView = (ListView) dialog.findViewById(R.id.listView);

        AdapterSocialList adapterSocialList = new AdapterSocialList(links, context, null);
        final UtilFunctions utilFunctions = new UtilFunctions(context);

        listView.setAdapter(adapterSocialList);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                LinkItem link = links.get(position);

                utilFunctions.openUrl(link.URL);
                dialog.dismiss();

            }
        });
        dialog.show();


    }


    public void ShowContactUs() {
        final UtilFunctions utilFunctions = new UtilFunctions(this.context);

        final ArrayList<LinkItem> links = new ArrayList<>();

        links.add(new LinkItem(0, context.getString(R.string.whatsApp), "", R.drawable.ic_whatsapp_logo));
        links.add(new LinkItem(1, context.getString(R.string.telegram), "", R.drawable.ic_telegram));
        links.add(new LinkItem(2, context.getString(R.string.email), "", R.drawable.ic_email_24px));

        String title = this.context.getString(R.string.app_social_networks);
        String appMame = this.context.getString(R.string.app_name);


        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_listview);

        dialog.setCancelable(true);


        TextView dlg_title = (TextView) dialog.findViewById(R.id.dlg_title);
        ImageView dialog_icon = (ImageView) dialog.findViewById(R.id.dialog_icon);

        dlg_title.setText(this.context.getString(R.string.contact_us));
        dialog_icon.setImageResource(R.drawable.ic_email_24px);


        Button okBtn = (Button) dialog.findViewById(R.id.okBtn);
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
            }
        });

        ListView listView = (ListView) dialog.findViewById(R.id.listView);

        AdapterSocialList adapterSocialList = new AdapterSocialList(links, context, null);

        listView.setAdapter(adapterSocialList);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                switch (position) {
                    case 0:
                        utilFunctions.openWhatsApp();
                        break;
                    case 1:
                        utilFunctions.openMyTelegram();
                        break;
                    case 2:
                        utilFunctions.openEmail();
                        break;
                }
                dialog.dismiss();

            }
        });
        dialog.show();
    }


    public void showHelp() {
        try {
            float textSize = AppSettings.getTextSize();
            String title = this.context.getString(R.string.help);

            String CurrentLang = Locale.getDefault().getLanguage();
            String file_name = "help_" + CurrentLang + ".htm";
            InputStream stream = context.getAssets().open(file_name);

            int size = stream.available();
            byte[] buffer = new byte[size];
            stream.read(buffer);
            stream.close();

            String text = new String(buffer);

            text = text.replace("$fontSize", textSize + "px");

            ShowWebDialog(title, text, R.drawable.ic_help_24px);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    public void showPolicy() {
        try {
            float textSize = AppSettings.getTextSize();
            String title = this.context.getString(R.string.about);

            String CurrentLang = Locale.getDefault().getLanguage();
            String file_name = "pr_po_" + CurrentLang + ".htm";
            InputStream stream = context.getAssets().open(file_name);

            int size = stream.available();
            byte[] buffer = new byte[size];
            stream.read(buffer);
            stream.close();

            String text = new String(buffer);

            text = text.replace("$version", version);
            text = text.replace("$name", context.getString(R.string.app_name));

            text = text.replace("$fontSize", textSize + "px");

            ShowWebDialog(title, text, R.drawable.ic_security_black_24dp);

        } catch (Exception ex) {
            Log.e("about", "showAbout: " + ex.getMessage());
        }
    }


    public void showAbout()
    {

        try {
            float textSize = AppSettings.getTextSize();
            String title = this.context.getString(R.string.about);

            String CurrentLang = Locale.getDefault().getLanguage();
            String file_name = "about_" + CurrentLang + ".htm";
            InputStream stream = context.getAssets().open(file_name);

            int size = stream.available();
            byte[] buffer = new byte[size];
            stream.read(buffer);
            stream.close();

            String text = new String(buffer);

            text = text.replace("$version", version);
            text = text.replace("$name", context.getString(R.string.app_name));

            text = text.replace("$fontSize", textSize + "px");

            ShowWebDialog(title, text, R.drawable.ic_info_24px);

        } catch (Exception ex) {
            Log.e("about", "showAbout: " + ex.getMessage());
        }

    }


    public void ShowWebDialog(String title, String text, int icon) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_webview);

        dialog.setCancelable(true);


        TextView dlg_title = (TextView) dialog.findViewById(R.id.dlg_title);
        ImageView dialog_icon = (ImageView) dialog.findViewById(R.id.dialog_icon);

        dlg_title.setText(title);
        dialog_icon.setImageResource(icon);


        Button okBtn = (Button) dialog.findViewById(R.id.okBtn);

        WebView webView = (WebView) dialog.findViewById(R.id.webView);

        webView.loadDataWithBaseURL("file:///android_asset/", text, "text/html", "UTF-8", null);
        dialog.show();

        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

    }


    public void ShowMessage(String message, int icon) {

        final Dialog mDialog = new Dialog(context);
        // no tile for the dialog
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.setContentView(R.layout.dialog_message);

        TextView dialog_text = (TextView) mDialog.findViewById(R.id.dialog_text);

        ImageView dialog_icon = (ImageView) mDialog.findViewById(R.id.dialog_icon);

        dialog_icon.setImageDrawable(context.getDrawable(icon));

        dialog_text.setText(UtilFunctions.fromHtml(message), TextView.BufferType.SPANNABLE);

        Button okBtn = (Button) mDialog.findViewById(R.id.okBtn);
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.dismiss();
            }
        });

        mDialog.setCancelable(true);
        mDialog.setCanceledOnTouchOutside(true);
        mDialog.show();
    }

    public void ShowWarningMessage(String message) {
        ShowMessage(message, R.drawable.ic_warning_white_24dp);
    }


    public Dialog YesNoDialog(String message, Drawable drawableIcon, boolean cancelAble) {


        Dialog mDialog = new Dialog(context);
        // no tile for the dialog
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.setContentView(R.layout.dialog_yes_no);

        TextView dialog_text = (TextView) mDialog.findViewById(R.id.dialog_text);

        ImageView dialog_icon = (ImageView) mDialog.findViewById(R.id.dialog_icon);

        dialog_icon.setImageDrawable(drawableIcon);


        dialog_text.setText(UtilFunctions.fromHtml(message), TextView.BufferType.SPANNABLE);
        mDialog.setCancelable(cancelAble);
        mDialog.setCanceledOnTouchOutside(cancelAble);

        return mDialog;
    }

}
package ir.ham3da.darya.utility;

import ir.ham3da.darya.R;
import ir.ham3da.darya.utility.PreferenceHelper;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

import androidx.core.content.res.ResourcesCompat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ir.ham3da.darya.utility.LangSettingList;
import ir.ham3da.darya.utility.UtilFunctions;

/**
 * @author Hamid Reza and Javad Ahshamian
 * App Settings Calss
 */
public class AppSettings {


    /**
     * Class Initialization:
     * قبل از استفاده از کلاس حتماً باید این متد با مقدار غیر null برای پارامتر ورودی ارسال شود
     *
     * @param context نمونۀ کلاس Activity
     */
    public static void Init(Context context) {
        if (PreferenceManager1 == null) {
            PreferenceManager1 = new PreferenceHelper(context);
        }
        if (_typeface == null) {

            Typeface font = ResourcesCompat.getFont(context, R.font.iran_sans_mobile_light);
            Typeface.create(font, Typeface.NORMAL);
            setApplicationTypeface(font);
        }
    }

    public static String getXmlSting(Context context) {
        StringBuilder aBuffer = new StringBuilder();

        File XMLFile = new File(context.getApplicationInfo().dataDir
                + "/shared_prefs/" + context.getPackageName() + "_preferences.xml");
        try {
            FileInputStream fIn = new FileInputStream(XMLFile);
            BufferedReader myReader = new BufferedReader(new InputStreamReader(fIn));
            String aDataRow = "";
            while ((aDataRow = myReader.readLine()) != null) {
                aBuffer.append(aDataRow);
            }
            myReader.close();
        } catch (Exception ex) {
            Log.e("getXmlSting", "err: " + ex.getMessage());
        }

        return aBuffer.toString();
    }

    public static String getSignature() {
        if (PreferenceManager1 == null) {
            return "";
        }
        return PreferenceManager1.getKey("signature", "");

    }

    public static String getDatabaseName() {

        return "ganjoor.s3db";
    }

    /**
     * Get Database Path(ganjoor.s3db)
     *
     * @param context Context
     * @return String Database Path
     */
    public static String getDatabasePath(Context context) {

        File db_file = new File(context.getExternalFilesDir("databases"), "ganjoor.s3db");
        String defaultDLDir = db_file.getAbsolutePath();
        if (!db_file.exists()) {
            File db_path = context.getExternalFilesDir("databases");
            db_path.mkdirs();
        }

        //String DB_PATH = context.getDatabasePath("ganjoor.s3db").getAbsolutePath();

        String DB_PATH = db_file.getAbsolutePath();
        if (PreferenceManager1 == null) {
            return null;
        }
        return PreferenceManager1.getKey("dbpath", DB_PATH);
    }

    /**
     * ذخیرۀ مقدار مسیر دیتابیس ganjoor.s3db
     *
     * @param Value مسیر دیتابیس ganjoor.s3db
     */
    public static void setDatabasePath(String Value) {
        if (PreferenceManager1 == null) {
            return;
        }
        PreferenceManager1.setKey("dbpath", Value);
    }

    /**
     * LastPoemIdVisited
     * شناسۀ رکورد آخرین شعری که کاربر داشته آن را می دیده
     */
    public static int getLastPoemIdVisited() {
        if (PreferenceManager1 == null) {
            return 0;
        }
        return PreferenceManager1.getKey("lastpoemvisited", 0);

    }


    public static void setPoemsFont(int Value) {
        if (PreferenceManager1 == null) {
            return;
        }
        PreferenceManager1.setKey("poemFont", Value);
    }

    /**
     * Get Poems Font id
     *
     * @return int def is 0
     */
    public static int getPoemsFont() {
        if (PreferenceManager1 == null) {
            return 0;
        }
        return PreferenceManager1.getKey("poemFont", 0);
    }


    /**
     * ذخیرۀ شناسۀ رکورد آخرین شعری که کاربر داشته آن را می دیده
     *
     * @param Value شناسۀ رکورد آخرین شعری که کاربر داشته آن را می دیده
     */
    public static void setLastPoemIdVisited(int Value) {
        if (PreferenceManager1 == null) {
            return;
        }
        PreferenceManager1.setKey("lastpoemvisited", Value);
    }

    /**
     * شناسۀ رکورد آخرین بخشی که کاربر داشته آن را می دیده
     */
    public static int getLastCatIdVisited() {
        if (PreferenceManager1 == null) {
            return 0;
        }
        return PreferenceManager1.getKey("lastcatvisited", 0);

    }

    /**
     * ذخیرۀ شناسۀ رکورد آخرین بخشی که کاربر داشته آن را می دیده
     *
     * @param Value شناسۀ رکورد آخرین بخشی که کاربر داشته آن را می دیده
     */
    public static void setLastCatIdVisited(int Value) {
        if (PreferenceManager1 == null) {
            return;
        }
        PreferenceManager1.setKey("lastcatvisited", Value);
    }

    /**
     * Audio Downloads Path
     *
     * @param context Context
     * @return String path
     */
    public static String getAudioDownloadPath(Context context) {
        if (PreferenceManager1 == null) {
            return null;
        }

        File dl_path = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
        String defaultDLDir = dl_path.getAbsolutePath();

        if (!dl_path.exists()) {
            dl_path.mkdir();
        }

        return PreferenceManager1.getKey("audiodwnldpath", defaultDLDir);
    }


    /**
     * Audio Download Path
     * ذخیرۀ مقدار مسیر فایلهای دریافتی
     *
     * @param Value مسیر فایلهای دریافتی
     */
    public static void setAudioDownloadPath(String Value) {
        if (PreferenceManager1 == null) {
            return;
        }

        PreferenceManager1.setKey("audiodwnldpath", Value);
    }

    /**
     * Database Download Path
     *
     * @return مسیر ذخیرۀ فایلهای دریافتی
     */
    public static String getDownloadPath(Context context) {
        if (PreferenceManager1 == null) {
            return null;
        }

        String defaultDLDir = context.getApplicationInfo().dataDir + "/downloads";
        File dir = new File(defaultDLDir);

        if (!dir.exists()) {
            dir.mkdir();
            setDownloadPath(defaultDLDir);
        }

        return PreferenceManager1.getKey("dwnldpath", defaultDLDir);
    }

    /**
     * Database Download Path
     * ذخیرۀ مقدار مسیر فایلهای دریافتی
     *
     * @param Value مسیر فایلهای دریافتی
     */
    public static void setDownloadPath(String Value) {
        if (PreferenceManager1 == null)
            return;
        PreferenceManager1.setKey("dwnldpath", Value);
    }

    /**
     * در دریافت مجموعه ها از داونلود منیجر اندروید استفاده شود یا خیر
     *
     * @return
     */
    public static Boolean getUseAndroidDownloadManager() {
        if (PreferenceManager1 == null)
            return false;
        return PreferenceManager1.getKey("usedownloadmanager", true);
    }

    /**
     * ذخیرۀ آپشن استفاده از داونلود منیجر اندروید
     *
     * @param Value
     */
    public static void setUseAndroidDownloadManager(Boolean Value) {
        if (PreferenceManager1 == null)
            return;
        PreferenceManager1.setKey("usedownloadmanager", Value);
    }


    /**
     * Get Poet List Indexing Status
     *
     * @return boolean
     */
    public static boolean getPoetListIndexStatus() {
        if (PreferenceManager1 == null) {
            return true;
        }
        return PreferenceManager1.getKey("PoetListIndexStatus", false);
    }


    /**
     * Set the Verse numbering Status
     *
     * @param Value Boolean value
     */
    public static void setVerseListIndexStatus(Boolean Value) {
        if (PreferenceManager1 == null)
            return;
        PreferenceManager1.setKey("VerseListIndexStatus", Value);
    }


    /**
     * Get the Verse numbering Status
     *
     * @return boolean default i false
     */
    public static boolean getVerseListIndexStatus() {
        if (PreferenceManager1 == null) {
            return false;
        }
        return PreferenceManager1.getKey("VerseListIndexStatus", false);
    }


    /**
     * Set Category List Index Status
     *
     * @param Value Boolean value
     */
    public static void setCateListIndexStatus(Boolean Value) {
        if (PreferenceManager1 == null)
            return;
        PreferenceManager1.setKey("VerseListIndexStatus", Value);
    }


    /**
     * Get Category List Indexing Status
     *
     * @return boolean
     */
    public static boolean getCateListIndexStatus() {
        if (PreferenceManager1 == null) {
            return true;
        }
        return PreferenceManager1.getKey("CateListIndexStatus", false);
    }


    /**
     * Set Poet List Index Status
     *
     * @param Value Boolean value
     */
    public static void setPoetListIndexStatus(Boolean Value) {
        if (PreferenceManager1 == null)
            return;
        PreferenceManager1.setKey("PoetListIndexStatus", Value);
    }


    /**
     * Save Language
     *
     * @param langIndex language Index
     */
    public static void saveLanguageSettings(int langIndex) {
        if (PreferenceManager1 == null)
            return;

        PreferenceManager1.setKey("langSettingList", langIndex);
    }


    /**
     * Get Category List Indexing Status
     *
     * @return boolean
     */
    public static LangSettingList getLangSettingList(Context context) {
        List<LangSettingList> LangSettingLists = UtilFunctions.getLanguageList(context);


        LangSettingList langSettingList = new LangSettingList(0, context.getString(R.string.persian), "fa", "IR");
        if (PreferenceManager1 == null) {
            return langSettingList;
        }
        int langSetting = PreferenceManager1.getKey("langSettingList", 0);

        langSettingList = LangSettingLists.get(langSetting);

        Log.e("Lang", "Lang: " + langSettingList.getText());

        return langSettingList;

    }

    public static float getTextSize() {
        if (PreferenceManager1 == null) {
            return 14;
        }

        return Float.valueOf(PreferenceManager1.getKey("TextSize", "14"));
    }

    /**
     * فونت نمایش متون
     */
    public static void setApplicationTypeface(Typeface typeface) {

        _typeface = typeface;
    }

    public static Typeface getApplicationTypeface() {
        return _typeface;
    }


    /**
     * Save Poet ids that selected in Random s limits dialog
     *
     * @param Poet_Ids String Comma Separated
     */
    public static void setRandomSelectedPoet(String Poet_Ids) {
        if (PreferenceManager1 != null) {
            String strPoets = "";
            PreferenceManager1.setKey("randomSelectedPoet", Poet_Ids);
        }
    }

    /**
     * Get Poet id that selected in Random poem limits dialog
     *
     * @return int default is -1
     */
    public static String getRandomSelectedPoets() {
        if (PreferenceManager1 == null) {
            return "2";
        }
        return PreferenceManager1.getKey("randomSelectedPoet", "2");
    }


    /**
     * Save Category ids that selected in Random poem limits dialog
     *
     * @param Cat_Ids String Comma Separated
     */
    public static void setRandomSelectedCategories(String Cat_Ids) {
        if (PreferenceManager1 != null) {
            PreferenceManager1.setKey("randomSelectedCat", Cat_Ids);
        }
    }

    /**
     * Get Category id that selected in Random poem limits dialog
     *
     * @return int default is -1
     */
    public static String getRandomSelectedCategories() {
        if (PreferenceManager1 == null) {
            return "24";
        }
        return "24";
        // return PreferenceManager1.getKey("randomSelectedCat", "24"); برای آینده
    }


    /**
     * Save Poet id that selected in search limits dialog
     *
     * @param Value int
     */
    public static void setSearchSelectedPoet(int Value) {
        if (PreferenceManager1 != null) {
            PreferenceManager1.setKey("searchPoet", Value);
        }
    }


    /**
     * Get Poet id that selected in search limits dialog from
     *
     * @return int default is -1
     */
    public static int getSearchSelectedPoet() {
        if (PreferenceManager1 == null) {
            return -1;
        }
        return PreferenceManager1.getKey("searchPoet", -1);
    }

    /**
     * Save book ids that selected in search limits dialog
     *
     * @param bookList List of boo ids
     */
    public static void setSearchSelectedBooks(List bookList) {
        if (PreferenceManager1 != null) {

            String strBookList = "";
            if (!bookList.isEmpty()) {
                strBookList = TextUtils.join(",", bookList);
            }

            PreferenceManager1.setKey("searchBooks", strBookList);
        }
    }

    /**
     * Get Book ids that selected in search limits dialog from
     *
     * @return String Comma Separated ids or empty string
     */
    public static String getSearchBooksAsString() {
        if (PreferenceManager1 == null) {
            return "";
        }
        return PreferenceManager1.getKey("searchBooks", "");
    }

    /**
     * Get Book ids that selected in search limits dialog from
     *
     * @return List<Integer> List of ids or empty string
     */
    public static List<Integer> getSearchBooksAsList() {
        List<Integer> bookListIDS = new ArrayList<>();
        if (PreferenceManager1 == null) {
            return bookListIDS;
        }

        String strBooksList = PreferenceManager1.getKey("searchBooks", "");

        if (!strBooksList.isEmpty()) {

            String[] arrayBookIds = strBooksList.split(",");
            for (String bookID : arrayBookIds) {
                bookListIDS.add(Integer.parseInt(bookID));
            }
        }

        return bookListIDS;
    }


    //Internals:
    /**
     * ذخیره و بازیابی تنظیمات
     */
    private static PreferenceHelper PreferenceManager1 = null;

    /**
     * فونت نمایش متون
     * فونت نمایش متن، رفرنسی از آن جهت تنظیم فونت کنترلهایی که بعدا اضافه می شوند نگهداری می شود
     */
    private static Typeface _typeface = null;


    /**
     * Folder to save image by Photo editor
     *
     * @return
     */
    public static String getImageFolderPath() {

        String dirPath = MediaStore.Images.Media.EXTERNAL_CONTENT_URI.getPath() + File.separator + "Darya";

        File dir = new File(dirPath);
        if (!dir.exists()) {
            dir.mkdir();
        }
        return dirPath;
    }

}

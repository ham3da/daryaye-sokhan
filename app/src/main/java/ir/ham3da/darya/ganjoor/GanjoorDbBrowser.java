/**
 *
 */
package ir.ham3da.darya.ganjoor;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import android.text.TextUtils;
import android.util.Log;

import ir.ham3da.darya.ActivitySearch;
import ir.ham3da.darya.utility.AppSettings;
import ir.ham3da.darya.utility.PoemAudio;
import ir.ham3da.darya.utility.RateType;

/**
 * @author Hamid Reza
 * کلاس اصلی استخراج و در صورت نیاز ذخیرۀ اطلاعات در دیتابیس گنجور رومیزی
 */
public class GanjoorDbBrowser {

    String TAG = "GanjoorDbBrowser";
    private Context mContext;

    /**
     * سازندۀ بدون پارامتر
     *
     */
    public GanjoorDbBrowser(Context context) {
        mContext = context;
        AppSettings.Init(context);
        //mimicking .NET Path.Combine:
        String dbPath = AppSettings.getDatabasePath(context);
        OpenDatbase(dbPath);
    }

    /**
     * سازندۀ با پارامتر مسیر دیتابیس
     * @param dbPath مسیر دیتابیس
     */
    public GanjoorDbBrowser(Context context, String dbPath) {

        OpenDatbase(dbPath);
    }


    /**
     * دیتابیس برنامه
     */
    private SQLiteDatabase _db = null;
    /**
     * مسیر فرستاده شده برای دیتابیس برنامه
     */
    private String _dbPath = "";
    /**
     *Last Error exception
     */
    public String _LastError = null;

    /**
     * Open Ganjoor Database
     * @param dbPath Database path
     * @return true if succeeds
     */
    public Boolean OpenDatbase(String dbPath) {
        _dbPath = dbPath;
        //if(!getDatabaseFileExists())
        //	return false;
        try {
            _db = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READWRITE);

            CreateIndexSetOnDb(_db); //Create Index In Database

        } catch (Exception exp) {
            _LastError = exp.toString();
        }

        return getIsConnected();
    }

    /**
     * Reopen previous opened database Database
     * @return نتیجه
     */
    public Boolean OpenDatbase() {
        if (_dbPath.isEmpty()) {
            return false;
        }
        return OpenDatbase(_dbPath);
    }

    /**
     * Close Database
     */
    public void CloseDatabase() {
        if (getIsConnected()) {
            _db.close();
            _db = null;
        }
    }


    /**
     * Create an empty Ganjoor database
     * @param fileName File path
     * @param failIfExists Skip if it is Exists.
     * @return The result of the action
     */
    public Boolean CreateNewPoemDatabase(String fileName, Boolean failIfExists) {
        if (failIfExists) {
            File f = new File(fileName);
            if (f.exists())
                return false;
        }

        File dbPath = new File(fileName).getParentFile();
        try {
            boolean res = dbPath.mkdirs();
        } catch (Exception exp) {
            exp.printStackTrace();
            return false;
        }
        dbPath = new File(fileName);
        if (dbPath.isDirectory()) {
            boolean res = dbPath.delete();
        }

        SQLiteDatabase newDb;
        try {
            newDb = SQLiteDatabase.openDatabase(dbPath.getAbsoluteFile().toString(), null, SQLiteDatabase.CREATE_IF_NECESSARY);
        } catch (Exception exp) {
            exp.printStackTrace();
            return false;
        }
        Boolean result = CreateEmptyDB(newDb);
        newDb.close();
        return result;

    }

    /**
     * Create Ganjoor Database Tables
     * @param newDb Destination database
     * @return The result of the action
     */
    private Boolean CreateEmptyDB(SQLiteDatabase newDb) {
        String sql = "CREATE TABLE [cat] ([id] INTEGER  PRIMARY KEY NOT NULL,[poet_id] INTEGER  NULL,[text] NVARCHAR(100)  NULL,[parent_id] INTEGER  NULL,[url] NVARCHAR(255)  NULL);";
        try {
            newDb.execSQL(sql);
        } catch (SQLException exp) {
            return false;
        }

        sql = "CREATE TABLE poem (id INTEGER PRIMARY KEY, cat_id INTEGER, title NVARCHAR(255), url NVARCHAR(255));";
        try {
            newDb.execSQL(sql);
        } catch (SQLException exp) {
            return false;
        }

        sql = "CREATE TABLE [poet] ([id] INTEGER  PRIMARY KEY NOT NULL,[name] NVARCHAR(20)  NULL,[cat_id] INTEGER  NULL  NULL, [description] TEXT, [update_info] VARCHAR(50));";
        try {
            newDb.execSQL(sql);
        } catch (SQLException exp) {
            return false;
        }

        sql = "CREATE TABLE [verse] ([poem_id] INTEGER NULL,[vorder] INTEGER  NULL,[position] INTEGER  NULL,[text] TEXT  NULL);";
        try {
            newDb.execSQL(sql);
        } catch (SQLException exp) {
            return false;
        }

        CreateIndexSetOnDb(newDb);

        return true;
    }


    public boolean createRateTable() {
        if (getIsConnected()) {
            String sql = "CREATE TABLE IF NOT EXISTS [Points] ([id] INTEGER Primary Key AUTOINCREMENT," +
                    "[plus_rate] INTEGER DEFAULT 0,[negative_rate] INTEGER  DEFAULT 0,[poem_id] INTEGER  DEFAULT 0, [verse_order] INTEGER  DEFAULT 0);";
            try {
                _db.execSQL(sql);
                return true;
            } catch (SQLException exp) {
                return false;
            }
        } else {
            return false;
        }

    }

    public boolean checkRateExist(int id) {
        if (getIsConnected()) {
            try {
                String countQuery1 = "SELECT  Count(*) FROM Points";
                Cursor cursor_count = _db.rawQuery(countQuery1, null);
                cursor_count.moveToFirst();
                int count = cursor_count.getInt(0);
                cursor_count.close();
                return count == 0;
            } catch (Exception ex) {
                Log.e("checkRateExist", "err: " + ex.getMessage());
                return false;
            }
        } else {
            return false;
        }

    }


    public void saveRate(int rate, String rateType,int poem_id, int verse_id) {

        if (getIsConnected()) {

            try {
                ContentValues contentValues = new ContentValues();
                contentValues.put(rateType, rate);
                contentValues.put("poem_id", poem_id);
                contentValues.put("verse_order", verse_id);
                _db.insert("Points", null, contentValues);

            } catch (Exception ex) {
                Log.e("saveRate", "err: " + ex.getMessage());
            }
        }

    }


    public RateType getRates()
    {
        if (getIsConnected()) {
            try {
                String countQuery1 = "SELECT  Sum(plus_rate), Sum(negative_rate) FROM Points";
                Cursor cursor_count = _db.rawQuery(countQuery1, null);
                cursor_count.moveToFirst();
                int plus_rate = cursor_count.getInt(0);
                int negative_rate = cursor_count.getInt(1);
                cursor_count.close();
                return new RateType(plus_rate, negative_rate);
            } catch (Exception ex) {
                Log.e("getRates", "err: " + ex.getMessage());
                return null;
            }
        } else {
            return null;
        }
    }


    /**
     * Create indexes
     * @param db Input database
     * @return The result of the action
     */
    private Boolean CreateIndexSetOnDb(SQLiteDatabase db) {

        String sql = "CREATE UNIQUE INDEX IF NOT EXISTS idx_poem_catid ON poem(id ASC, cat_id ASC);";
        try {
            db.execSQL(sql);
        } catch (SQLException exp) {
            return false;
        }
        sql = "CREATE INDEX IF NOT EXISTS poem_cid ON poem(cat_id ASC);";
        try {
            db.execSQL(sql);
        } catch (SQLException exp) {
            return false;
        }
        sql = "CREATE UNIQUE INDEX IF NOT EXISTS verse_pid ON verse(poem_id ASC);";
        try {
            db.execSQL(sql);
        } catch (SQLException exp) {
            return false;
        }

        sql = "CREATE UNIQUE INDEX IF NOT EXISTS cat_pid ON cat(parent_id ASC);";
        try {
            db.execSQL(sql);
        } catch (SQLException exp) {
            return false;
        }

        return true;
    }


    /**
     * @return Is the database file exists on the specified path?
     */
    public Boolean getDatabaseFileExists() {
        if (getIsConnected()) {
            return true;
        }
        File f = new File(_dbPath);
        return f.exists();
    }


    private List<Integer> allSubCategory;

//    public GanjoorVerse getVerseRandom()
//    {
//        if (getIsConnected()) {
//
//            Cursor cursor = _db.rawQuery("SELECT p.id, p.cat_id, p.title, p.url,  p.url as urlFake, " +
//                    "(SELECT  Count(*) FROM fav Where poem_id=p.id) AS favCount FROM poem p " +
//                    "WHERE  ORDER BY RANDOM() Limit 1", null);
//            if (cursor.moveToFirst()) {
//                GanjoorPoem GanjoorPoem1 = new GanjoorPoem(
//                        cursor.getInt(IDX_POEM_ID),
//                        cursor.getInt(IDX_POEM_CATID),
//                        cursor.getString(IDX_POEM_TITLE),
//                        cursor.getString(IDX_POEM_URL),
//                        cursor.getInt(IDX_POEM_FAV) != 0,
//                        ""
//                );
//                cursor.close();
//                return GanjoorPoem1;
//            }
//            cursor.close();
//        }
//        return null;
//
//
//    }


    public List<GanjoorVerse> getRandomPoemPuzzle() {

        List<GanjoorVerse> ganjoorVerseList = new ArrayList<>();

        if (getIsConnected()) {

            Cursor cursor = _db.rawQuery(
                    "SELECT  v.poem_id, v.vorder, v.position, v.text, (Select Count(*) From verse Where poem_id=v.poem_id ) As verseCount From [verse] v " +
                            "WHERE (v.position != -1) AND (length(trim(v.text)) > 1) AND((v.vorder % 2) == 1) AND (verseCount > v.vorder ) " +
                            "ORDER BY RANDOM() Limit 1",
                    null);
            if (cursor.moveToFirst()) {
                GanjoorVerse GanjoorVerse1 = new GanjoorVerse(
                        cursor.getInt(IDX_VERSE_POEMID),
                        cursor.getInt(IDX_VERSE_ORDER),
                        cursor.getInt(IDX_VERSE_POSITION),
                        cursor.getString(IDX_VERSE_TEXT)
                );
                cursor.close();

                //boolean isEven = ( GanjoorVerse1._Order % 2 ) == 0; // Zoj ?

                ganjoorVerseList.add(GanjoorVerse1);
                GanjoorVerse GanjoorVerse2 = getNextVerse(GanjoorVerse1._PoemID, GanjoorVerse1._Order);
                if (GanjoorVerse2 != null) {
                    ganjoorVerseList.add(GanjoorVerse2);
                }

            }
            cursor.close();

            return ganjoorVerseList;
        }
        return null;
    }





    public List<GanjoorVerse> getRandomVersePuzzle(int poem_id) {
        List<GanjoorVerse> ganjoorVerseList = new ArrayList<>();
        if (getIsConnected()) {

            Cursor cursor = _db.rawQuery(
                    "SELECT v.poem_id, v.vorder, v.position, v.text, (Select Count(*) From verse Where poem_id=v.poem_id ) As verseCount From [verse] v " +
                            "INNER JOIN [poem] p ON v.poem_id = p.id " +
                            "INNER JOIN [cat] c ON c.id = p.cat_id " +
                            "WHERE (v.position != -1) AND (length(trim(v.text)) > 1) AND ((v.vorder % 2) == 1) AND (verseCount > v.vorder ) AND (v.poem_id=" + poem_id + ") ORDER BY RANDOM() Limit 1",
                    null);
            if (cursor.moveToFirst()) {
                GanjoorVerse GanjoorVerse1 = new GanjoorVerse(
                        cursor.getInt(IDX_VERSE_POEMID),
                        cursor.getInt(IDX_VERSE_ORDER),
                        cursor.getInt(IDX_VERSE_POSITION),
                        cursor.getString(IDX_VERSE_TEXT)
                );
                cursor.close();

                ganjoorVerseList.add(GanjoorVerse1);

                GanjoorVerse GanjoorVerse2 = getNextVerse(GanjoorVerse1._PoemID, GanjoorVerse1._Order);
                if (GanjoorVerse2 != null) {
                    ganjoorVerseList.add(GanjoorVerse2);

                    if(GanjoorVerse1._Position == GanjoorVerse.POSITION_SINGLE)
                    {
                        GanjoorVerse GanjoorVerse3 = getNextVerse(GanjoorVerse1._PoemID, GanjoorVerse2._Order);
                        if (GanjoorVerse3 != null) {
                            ganjoorVerseList.add(GanjoorVerse3);

                            GanjoorVerse GanjoorVerse4 = getNextVerse(GanjoorVerse1._PoemID, GanjoorVerse3._Order);
                            if (GanjoorVerse4 != null) {
                                ganjoorVerseList.add(GanjoorVerse4);


                            }
                        }
                    }

                }
                return ganjoorVerseList;
            }
            cursor.close();
        }
        return null;
    }


    /**
     *
     * @param parentCate parent category id
     * @return List<GanjoorVerse>
     */
    public List<GanjoorVerse> getRandomPoemPuzzle(int parentCate) {

        List<GanjoorVerse> ganjoorVerseList = new ArrayList<>();
        List<Integer> cateList = getAllSubCategories(parentCate);
        cateList.add(parentCate);

        if (allSubCategory.size() > 0) {
            cateList.addAll(allSubCategory);
        }
        String CommaSpIds = TextUtils.join(",", allSubCategory);

        GanjoorPoem poem = getPoemRandom(CommaSpIds);

        ganjoorVerseList = getRandomVersePuzzle(poem._ID);


        return ganjoorVerseList;
    }


    public List<Integer> getAllSubCategories(int parentCate) {
        allSubCategory = new ArrayList<>();
        getAllSubCategory(parentCate);
        return allSubCategory;
    }

    private void getAllSubCategory(int parentCate) {
        List<GanjoorCat> getSubCats = getSubCats(parentCate);

        if (getSubCats.size() > 0) {
            for (GanjoorCat cate : getSubCats) {
                allSubCategory.add(cate._ID);
                getAllSubCategory(cate._ID);
            }
        }
    }

    /**
     * Are we connected to the database or not?
     * @return true if yes
     */
    public Boolean getIsConnected() {
        return _db != null;
    }

    private static final int IDX_POET_ID = 0;
    private static final int IDX_POET_NAME = 1;
    private static final int IDX_POET_CATID = 2;
    private static final int IDX_POET_BIO = 3;
    private static final int IDX_POET_UPDATE = 4;


    /**
     * get Count of Poems by cat id
     * @return int
     */
    public int getPoemsCount(int cat_id) {
        // Log.e(TAG, "cat_id: "+cat_id);
        if (getIsConnected()) {
            try {
                String countQuery1 = "SELECT  Count(*) FROM poem Where cat_id='" + cat_id + "'";
                Cursor cursor_count = _db.rawQuery(countQuery1, null);
                cursor_count.moveToFirst();
                int count = cursor_count.getInt(0);
                cursor_count.close();
                return count;
            } catch (Exception ex) {
                Log.e("getPoetsCount", "err: " + ex.getMessage());
                return 0;
            }
        } else {
            return 0;
        }

    }

    /**
     * get Count of Poets
     * @return int
     */
    public int getPoetsCount() {
        if (getIsConnected()) {
            try {
                String countQuery1 = "SELECT  Count(*) FROM poet";
                Cursor cursor_count = _db.rawQuery(countQuery1, null);
                cursor_count.moveToFirst();
                int count = cursor_count.getInt(0);
                cursor_count.close();
                return count;
            } catch (Exception ex) {
                Log.e("getPoetsCount", "err: " + ex.getMessage());
                return 0;
            }
        } else {
            return 0;
        }

    }


    /**
     * @param CommaSpIds Comma Separated cat ids
     * @return GanjoorPoem Poem
     */
    public GanjoorPoem getPoemRandom(String CommaSpIds) {
        if (getIsConnected()) {

            Cursor cursor = _db.rawQuery("SELECT p.id, p.cat_id, p.title, p.url,  p.url as urlFake, " +
                    "(SELECT  Count(*) FROM fav Where poem_id=p.id) AS favCount FROM poem p " +
                    "WHERE cat_id IN(" + CommaSpIds + ") ORDER BY RANDOM() Limit 1", null);
            if (cursor.moveToFirst()) {
                GanjoorPoem GanjoorPoem1 = new GanjoorPoem(
                        cursor.getInt(IDX_POEM_ID),
                        cursor.getInt(IDX_POEM_CATID),
                        cursor.getString(IDX_POEM_TITLE),
                        cursor.getString(IDX_POEM_URL),
                        cursor.getInt(IDX_POEM_FAV) != 0,
                        ""
                );
                cursor.close();
                return GanjoorPoem1;
            }
            cursor.close();
        }
        return null;
    }


    /**
     * Get list of Poets
     * @param CommaSpIds Comma Separated cat ids
     * @return List<GanjoorPoet> list of Poets
     */
    public List<GanjoorPoet> getPoetsFromCat(String CommaSpIds) {
        LinkedList<GanjoorPoet> poets = new LinkedList<GanjoorPoet>();
        if (getIsConnected()) {
            String query = "Select poet.id,  poet.name, poet.cat_id, poet.description, poet.update_info From cat " +
                    "INNER JOIN poet ON cat.poet_id = poet.id Where cat.id IN(" + CommaSpIds + ") Group By poet.id Order By poet.name";
            Cursor cursor = _db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                do {
                    poets.add(
                            new GanjoorPoet(
                                    cursor.getInt(IDX_POET_ID),
                                    cursor.getString(IDX_POET_NAME),
                                    cursor.getInt(IDX_POET_CATID),
                                    cursor.getString(IDX_POET_BIO),
                                    cursor.getString(IDX_POET_UPDATE)
                            )
                    );

                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return poets;
    }

    /**
     * Get list of Poets
     * @param CommaSpIds Comma Separated ids
     * @return List<GanjoorPoet> list of Poets
     */
    public List<GanjoorPoet> getPoets(String CommaSpIds) {
        LinkedList<GanjoorPoet> poets = new LinkedList<GanjoorPoet>();
        if (getIsConnected()) {
            String query = "SELECT id, name, cat_id, description, update_info FROM poet WHERE id IN (" + CommaSpIds + ") Order By name";
            Cursor cursor = _db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                do {
                    poets.add(
                            new GanjoorPoet(
                                    cursor.getInt(IDX_POET_ID),
                                    cursor.getString(IDX_POET_NAME),
                                    cursor.getInt(IDX_POET_CATID),
                                    cursor.getString(IDX_POET_BIO),
                                    cursor.getString(IDX_POET_UPDATE)

                            )
                    );

                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return poets;
    }

    /**
     * Get list of Poets
     * @return List<GanjoorPoet> list of Poets
     */
    public List<GanjoorPoet> getPoets() {
        LinkedList<GanjoorPoet> poets = new LinkedList<GanjoorPoet>();
        if (getIsConnected()) {
            Cursor cursor = _db.query("poet", new String[]{"id", "name", "cat_id", "description", "update_info"}, null, null, null, null, "name");
            if (cursor.moveToFirst()) {
                do {
                    poets.add(
                            new GanjoorPoet(
                                    cursor.getInt(IDX_POET_ID),
                                    cursor.getString(IDX_POET_NAME),
                                    cursor.getInt(IDX_POET_CATID),
                                    cursor.getString(IDX_POET_BIO),
                                    cursor.getString(IDX_POET_UPDATE)
                            )
                    );

                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return poets;
    }


    public GanjoorPoet getPoet(int PoetId) {
        if (getIsConnected()) {
            Cursor cursor = _db.query("poet", new String[]{"id", "name", "cat_id", "description", "update_info"}, "id = " + PoetId, null, null, null, "name");
            if (cursor.moveToFirst()) {
                GanjoorPoet GanjoorPoet1 = new GanjoorPoet(
                        cursor.getInt(IDX_POET_ID),
                        cursor.getString(IDX_POET_NAME),
                        cursor.getInt(IDX_POET_CATID),
                        cursor.getString(IDX_POET_BIO),
                        cursor.getString(IDX_POET_UPDATE)
                );

                cursor.close();
                return GanjoorPoet1;
            }
            cursor.close();
        }
        return null;
    }

    private static final int IDX_CAT_ID = 0;
    private static final int IDX_CAT_POETID = 1;
    private static final int IDX_CAT_TEXT = 2;
    private static final int IDX_CAT_PARENTID = 3;
    private static final int IDX_CAT_URL = 4;

    /**
     * استخراج اطلاعات یک بخش از روی شناسۀ رکورد آن
     * @param CatId شناسۀ رکورد متناظر
     * @return اگر بخش موجود نباشد یا به دیتابیس متصل نباشیم null
     */
    public GanjoorCat getCat(int CatId) {
        if (getIsConnected()) {
            Cursor cursor = _db.query("cat", new String[]{"id", "poet_id", "text", "parent_id", "url"}, "id = " + CatId, null, null, null, "id", "1");
            if (cursor.moveToFirst()) {
                GanjoorCat GanjoorCat1 = new GanjoorCat(
                        cursor.getInt(IDX_CAT_ID),
                        cursor.getInt(IDX_CAT_POETID),
                        cursor.getString(IDX_CAT_TEXT),
                        cursor.getInt(IDX_CAT_PARENTID),
                        cursor.getString(IDX_CAT_URL)
                );

                cursor.close();
                return GanjoorCat1;
            }
            cursor.close();

        }
        return null;
    }

    /**
     * فهرست زیربخشهای یک بخش
     * @param CatId شناسۀ رکورد بخش
     * @return در صورت عدم اتصال به دیتابیس یک لیست خالی باز می گردد
     */
    public List<GanjoorCat> getSubCats(int CatId) {
        LinkedList<GanjoorCat> cats = new LinkedList<GanjoorCat>();
        if (getIsConnected()) {
            Cursor cursor = _db.query("cat", new String[]{"id", "poet_id", "text", "parent_id", "url"}, "parent_id = " + CatId, null, null, null, "id");
            if (cursor.moveToFirst()) {
                do {
                    cats.add(
                            new GanjoorCat(
                                    cursor.getInt(IDX_CAT_ID),
                                    cursor.getInt(IDX_CAT_POETID),
                                    cursor.getString(IDX_CAT_TEXT),
                                    cursor.getInt(IDX_CAT_PARENTID),
                                    cursor.getString(IDX_CAT_URL)
                            )
                    );

                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return cats;
    }

    /**
     * Get Sub Categories Count
     * @param CatId int category id
     * @return int
     */
    public int getSubCatsCount(int CatId) {

        if (getIsConnected()) {
            try {

                String countQuery1 = String.format(Locale.ENGLISH, "SELECT  Count(*) FROM cat Where parent_id=%d", CatId);
                Cursor cursor_count = _db.rawQuery(countQuery1, null);
                cursor_count.moveToFirst();
                int count = cursor_count.getInt(0);
                cursor_count.close();
                return count;
            } catch (Exception ex) {
                Log.e("getPoetsCount", "err: " + ex.getMessage());
                return 0;
            }
        } else {
            return 0;
        }

    }


    private static final int IDX_POEM_ID = 0;
    private static final int IDX_POEM_CATID = 1;
    private static final int IDX_POEM_TITLE = 2;
    private static final int IDX_POEM_URL = 3;
    private static final int IDX_POEM_FIRSTVERSE = 4;
    private static final int IDX_POEM_FAV = 5;


    public int getFavoritesCount() {
        if (getIsConnected()) {
            try {
                String countQuery1 = "SELECT  Count(*) FROM fav";
                Cursor cursor_count = _db.rawQuery(countQuery1, null);
                cursor_count.moveToFirst();
                int count = cursor_count.getInt(0);
                cursor_count.close();
                return count;
            } catch (Exception ex) {
                Log.e("getPoetsCount", "err: " + ex.getMessage());
                return 0;
            }
        } else {
            return 0;
        }

    }

    public GanjoorPoet getRandomPoet() {
        if (getIsConnected()) {
            Cursor cursor = _db.rawQuery("Select id, name, cat_id, description From poet ORDER BY RANDOM()", null);
            if (cursor.moveToFirst()) {
                GanjoorPoet GanjoorPoet1 = new GanjoorPoet(
                        cursor.getInt(IDX_POET_ID),
                        cursor.getString(IDX_POET_NAME),
                        cursor.getInt(IDX_POET_CATID),
                        cursor.getString(IDX_POET_BIO),
                        ""
                );

                cursor.close();
                return GanjoorPoet1;
            }
            cursor.close();
        }
        return null;
    }

    public String getPoemTree(int poem_id) {

        if (getIsConnected()) {
            Cursor cursor;

            cursor = _db.rawQuery("SELECT pm.id, pm.cat_id, pm.title, pt.name AS poetName, (with parent_tree AS (" +
                            "Select id, parent_id, text " +
                            "From cat " +
                            "Where id = pm.cat_id " +
                            "union All " +
                            "Select c.id, c.parent_id, c.text " +
                            "From cat c " +
                            "JOIN parent_tree parent ON c.id = parent.parent_id " +
                            "AND c.id != c.parent_id " +
                            ")" +
                            "Select group_concat(cate_tree.text, ' > ') AS commaTree From (SELECT * From parent_tree ORDER BY id ASC) cate_tree) cate_tree " +
                            "From poem pm " +
                            "INNER JOIN [cat] c2 ON c2.id = pm.cat_id " +
                            "INNER JOIN [poet] pt ON pt.id = c2.poet_id " +
                            "Where pm.id=" + poem_id
                    , null);

            if (cursor.moveToFirst()) {
                String cate_tree = cursor.getString(4);
                return cate_tree;
            }
        }
        return null;
    }

    public List<FavoritesPoem> getFavoritesPoems(Boolean IncludeFirstVerse, int offset, int limit, int startIndex) {

        LinkedList<FavoritesPoem> poems = new LinkedList<FavoritesPoem>();
        if (getIsConnected()) {
            Cursor cursor;

            String limitStr = String.format(Locale.ENGLISH, "Limit %d, %d", offset, limit);

            if (IncludeFirstVerse) {

                cursor = _db.rawQuery("SELECT f.poem_id, pm.cat_id, pm.title, pm.url, v.text, pt.name AS poetName, (with parent_tree AS (" +
                        "Select id, parent_id, text " +
                        "From cat " +
                        "Where id = pm.cat_id " +
                        "union All " +
                        "Select c.id, c.parent_id, c.text " +
                        "From cat c " +
                        "JOIN parent_tree parent ON c.id = parent.parent_id " +
                        "AND c.id != c.parent_id " +
                        ")" +
                        "Select group_concat(cate_tree.text, ' > ') AS commaTree From (SELECT * From parent_tree ORDER BY id ASC) cate_tree) cate_tree " +
                        "From fav f " +
                        "INNER JOIN [poem] pm ON pm.id = f.poem_id " +
                        "INNER JOIN [cat] c2 ON c2.id = pm.cat_id " +
                        "INNER JOIN [poet] pt ON pt.id = c2.poet_id " +
                        "INNER JOIN [verse] v ON pm.id = v.poem_id " +
                        "WHERE v.vorder = 1 ORDER BY f.pos " + limitStr, null);
            } else {


                cursor = _db.rawQuery("SELECT f.poem_id, pm.cat_id, pm.title, pm.url, pm.title as text2, pt.name AS poetName, (with parent_tree AS (" +
                        "Select id, parent_id, text " +
                        "From cat " +
                        "Where id = pm.cat_id " +
                        "union All " +
                        "Select c.id, c.parent_id, c.text " +
                        "From cat c " +
                        "JOIN parent_tree parent ON c.id = parent.parent_id " +
                        "AND c.id != c.parent_id " +
                        ")" +
                        "Select group_concat(cate_tree.text, ' > ') AS commaTree From (SELECT * From parent_tree ORDER BY id ASC) cate_tree) cate_tree " +
                        "From fav f " +
                        "INNER JOIN [poem] pm ON pm.id = f.poem_id " +
                        "INNER JOIN [cat] c2 ON c2.id = pm.cat_id " +
                        "INNER JOIN [poet] pt ON pt.id = c2.poet_id " +
                        "ORDER BY f.pos " + limitStr, null);

            }

            int index = startIndex;
            if (cursor.moveToFirst()) {
                do {
                    index++;
                    poems.add(
                            new FavoritesPoem(
                                    cursor.getInt(IDX_POEM_ID),
                                    cursor.getInt(IDX_POEM_CATID),
                                    cursor.getString(IDX_POEM_TITLE),
                                    cursor.getString(IDX_POEM_URL),
                                    true,
                                    IncludeFirstVerse ? cursor.getString(IDX_POEM_FIRSTVERSE) : "",
                                    cursor.getString(5),
                                    cursor.getString(6),
                                    index
                            )
                    );

                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return poems;
    }


    public List<PoemAudio> getPoemAudios(int poem_id) {
        List<PoemAudio> poemAudios = new ArrayList<>();
        if (getIsConnected()) {
            Cursor cursor = _db.query("poemsnd",
                    new String[]{"poem_id", "id", "filepath", "description",
                            "dnldurl", "isdirect", "syncguid", "fchksum", "isuploaded"},
                    "poem_id = " + poem_id, null, null, null, "id");
            if (cursor.moveToFirst()) {
                do {
                    poemAudios.add(
                            new PoemAudio(
                                    cursor.getInt(0),
                                    cursor.getInt(1),
                                    cursor.getString(2),
                                    cursor.getString(3),
                                    cursor.getString(4),
                                    cursor.getInt(5) != 0,
                                    cursor.getString(6),
                                    cursor.getString(7),
                                    cursor.getInt(8) != 0
                            )
                    );

                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return poemAudios;

    }

    /**
     * فهرست شعرهای یک بخش
     * @param CatId شناسۀ رکورد بخش
     * @return در صورت عدم اتصال به دیتابیس یک لیست خالی باز می گردد
     */
    public List<GanjoorPoem> getPoems(int CatId) {
        return getPoems(CatId, false);
    }

    /**
     * فهرست شعرهای یک بخش
     * @param CatId شناسۀ رکورد بخش
     * @param IncludeFirstVerse مصرع اول هم گنجانده شود
     * @return
     */
    public List<GanjoorPoem> getPoems(int CatId, Boolean IncludeFirstVerse) {
        LinkedList<GanjoorPoem> poems = new LinkedList<GanjoorPoem>();
        if (getIsConnected()) {
            Cursor cursor;
            if (IncludeFirstVerse) {
                //این کوئری شعرهای بدون مصرع را نادیده می گیرد
                // هر چند به نظر نمی رسد این ایراد مهمی در برنامۀ نمایش شعر ایجاد کند
                // اما برای حالتهایی مثل ویرایشگر شعر ایجاد اشکال خواهد کرد
                // راه دیگر استفاده از دو کوئری مجزا برای شعرها و مصرع اول آنها است که در
                // گنجور رومیزی استفاده شده که این ایراد را ندارد
                // اما مسلما کندتر است.

                cursor = _db.rawQuery("SELECT p.id, p.cat_id, p.title, p.url, v.text, (SELECT  Count(*) FROM fav Where poem_id=p.id) AS favCount FROM poem p " +
                        "INNER JOIN verse v ON p.id = v.poem_id " +
                        "WHERE v.vorder = 1 AND p.cat_id = " + CatId + " ORDER BY p.id", null);
            } else {
                cursor = _db.rawQuery("SELECT p.id, p.cat_id, p.title, p.url,  p.url as urlFake, (SELECT  Count(*) FROM fav Where poem_id=p.id) AS favCount FROM poem p " +
                        "WHERE p.cat_id = " + CatId + " ORDER BY p.id", null);

            }

            if (cursor.moveToFirst()) {
                do {
                    poems.add(
                            new GanjoorPoem(
                                    cursor.getInt(IDX_POEM_ID),
                                    cursor.getInt(IDX_POEM_CATID),
                                    cursor.getString(IDX_POEM_TITLE),
                                    cursor.getString(IDX_POEM_URL),
                                    cursor.getInt(IDX_POEM_FAV) != 0,
                                    IncludeFirstVerse ? cursor.getString(IDX_POEM_FIRSTVERSE) : ""
                            )
                    );

                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return poems;
    }

    /**
     * @param PoemId شناسۀ رکورد شعر
     * @return اطلاعات شعر
     */
    public GanjoorPoem getPoem(int PoemId) {
        if (getIsConnected()) {

            Cursor cursor = _db.rawQuery("SELECT p.id, p.cat_id, p.title, p.url,  p.url as urlFake, (SELECT  Count(*) FROM fav Where poem_id=p.id) AS favCount FROM poem p " +
                    "WHERE p.id = " + PoemId + " ORDER BY p.id Limit 1", null);
            if (cursor.moveToFirst()) {
                GanjoorPoem GanjoorPoem1 = new GanjoorPoem(
                        cursor.getInt(IDX_POEM_ID),
                        cursor.getInt(IDX_POEM_CATID),
                        cursor.getString(IDX_POEM_TITLE),
                        cursor.getString(IDX_POEM_URL),
                        cursor.getInt(IDX_POEM_FAV) != 0,
                        ""
                );
                cursor.close();
                return GanjoorPoem1;
            }
            cursor.close();
        }
        return null;
    }

    /**
     * Check  Poem is added to favorites
     * @param PoemId شناسۀ رکورد شعر
     * @return false!
     */
    public Boolean IsPoemFaved(int PoemId) {
        if (getIsConnected()) {

            try {
                String countQuery1 = "SELECT  Count(*) AS favCount FROM fav Where poem_id=" + PoemId;

                Cursor cursor = _db.rawQuery(countQuery1, null);
                cursor.moveToFirst();
                int count = cursor.getInt(0);
                cursor.close();
                return count != 0;
            } catch (Exception ex) {
                Log.e("MaxFavOrder", "err: " + ex.getMessage());
                return false;
            }
        }
        return false;
    }

    public Boolean IsVerseFaved(int PoemId, int VerseID) {
        if (getIsConnected()) {

            String countQuery1 = "SELECT  Count(*) AS favCount FROM fav Where poem_id=" + PoemId + " AND verse_id=" + VerseID;

            Cursor cursor = _db.rawQuery(countQuery1, null);
            cursor.moveToFirst();
            int count = cursor.getInt(0);
            cursor.close();
            return count != 0;
        }
        return false;
    }

    private int MaxFavOrder() {
        if (getIsConnected()) {
            try {
                String countQuery1 = "SELECT  MAX(pos) FROM fav";
                Cursor cursor = _db.rawQuery(countQuery1, null);
                cursor.moveToFirst();
                int max = cursor.getInt(0);
                cursor.close();
                return max;
            } catch (Exception ex) {
                Log.e("MaxFavOrder", "err: " + ex.getMessage());
                return -1;
            }
        }
        return -1;
    }

    public Boolean ToggleFav(int PoemID, int VerseID) {
        boolean faved = VerseID == -1 ? IsPoemFaved(PoemID) : IsVerseFaved(PoemID, VerseID);
        if (faved) {
            removeFromFavorites(PoemID, VerseID);
            return false;
        } else {
            addToFavorites(PoemID, VerseID);
            return true;
        }
    }


    public long addToSound(GanjoorAudioInfo audioInfo) {

        if (getIsConnected()) {

            try {
                String dl_path = AppSettings.getAudioDownloadPath(mContext);
                // String fileName = URLUtil.guessFileName(audioInfo.audio_mp3, null, null);
                ContentValues contentValues = new ContentValues();
                contentValues.put("poem_id", audioInfo.audio_post_ID);
                contentValues.put("id", audioInfo.audio_order);
                contentValues.put("filepath", dl_path + "/" + audioInfo.audio_fchecksum + ".mp3");
                contentValues.put("description", audioInfo.audio_artist);
                contentValues.put("syncguid", audioInfo.audio_guid);
                contentValues.put("fchksum", audioInfo.audio_fchecksum);
                contentValues.put("isuploaded", 0);

                return _db.insert("poemsnd", null, contentValues);

            } catch (Exception ex) {
                Log.e("addToSound", "err: " + ex.getMessage());
                return -1;

            }
        } else {
            return -1;
        }
    }


    public void deleteSound(int PoemID, int id) {
        if (getIsConnected()) {

            try {
                _db.delete("poemsnd", "poem_id = ? AND id = ?", new String[]{String.valueOf(PoemID), String.valueOf(id)});
            } catch (Exception ex) {
                Log.e("addToSound", "err: " + ex.getMessage());
            }
        }
    }

    public void addToFavorites(int PoemId) {
        addToFavorites(PoemId, -1);
    }

    public void addToFavorites(int PoemId, int VerseID) {
        if (getIsConnected()) {

            try {
                ContentValues contentValues = new ContentValues();
                contentValues.put("poem_id", PoemId);
                contentValues.put("verse_id", VerseID);
                contentValues.put("pos", (MaxFavOrder() + 1));

                long res = _db.insert("fav", null, contentValues);
            } catch (Exception ex) {
                Log.e("addToFavorites", "err: " + ex.getMessage());
            }
        }
    }


    public void removeFromFavorites(int PoemID) {
        removeFromFavorites(PoemID, -1);
    }

    public void removeFromFavorites(int PoemID, int VerseID) {
        if (getIsConnected()) {
            if (VerseID != -1) {
                _db.delete("fav", "poem_id = ? AND verse_id = ?", new String[]{String.valueOf(PoemID), String.valueOf(VerseID)});
            } else {
                _db.delete("fav", "poem_id = ?", new String[]{String.valueOf(PoemID)});
            }
        }
    }


    /**
     * Check Poem is MultiPart (Refrain)
     * @param PoemId Poem id
     * @return Boolean
     */
    public Boolean IsPoemMultiPart(int PoemId) {
        Cursor cursor = _db.query("verse", new String[]{"position"}, "poem_id = " + PoemId + " AND (position = 2 OR position = 3)", null, null, null, null, "1");
        boolean res = cursor.moveToFirst();
        cursor.close();
        return res;
    }

    private static final int IDX_VERSE_POEMID = 0;
    private static final int IDX_VERSE_ORDER = 1;
    private static final int IDX_VERSE_POSITION = 2;
    private static final int IDX_VERSE_TEXT = 3;

    /**
     * Verses of Poem
     * @param PoemId Poem ID
     * @return List<GanjoorVerse> List of Verses
     */
    public List<GanjoorVerse> getVerses(int PoemId) {
        LinkedList<GanjoorVerse> verses = new LinkedList<GanjoorVerse>();
        if (getIsConnected()) {
            Cursor cursor = _db.query("verse", new String[]{"poem_id", "vorder", "position", "text"}, "poem_id = " + PoemId, null, null, null, "vorder");
            if (cursor.moveToFirst()) {
                do {
                    verses.add(
                            new GanjoorVerse(
                                    cursor.getInt(IDX_VERSE_POEMID),
                                    cursor.getInt(IDX_VERSE_ORDER),
                                    cursor.getInt(IDX_VERSE_POSITION),
                                    cursor.getString(IDX_VERSE_TEXT)
                            )
                    );

                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return verses;
    }

    /**
     * Get the Next Verse of current verse
     * @param Verse current verse
     * @return GanjoorVerse Verse
     */
    public GanjoorVerse getNextVerse(GanjoorVerse Verse) {
        if (Verse == null)
            return null;
        return getNextVerse(Verse._PoemID, Verse._Order);
    }

    /**
     * Get the Next Verse of current verse
     * @param PoemId Poem ID
     * @param VerseOrder Verse Order
     * @return GanjoorVerse Verse or null
     */
    public GanjoorVerse getNextVerse(int PoemId, int VerseOrder) {
        if (getIsConnected()) {

            Cursor cursor = _db.query("verse", new String[]{"poem_id", "vorder", "position", "text"}, "poem_id = " + PoemId + " AND vorder = " + (VerseOrder + 1), null, null, null, "vorder", "1");
            if (cursor.moveToFirst()) {
                GanjoorVerse GanjoorVerse1 = new GanjoorVerse(
                        cursor.getInt(IDX_VERSE_POEMID),
                        cursor.getInt(IDX_VERSE_ORDER),
                        cursor.getInt(IDX_VERSE_POSITION),
                        cursor.getString(IDX_VERSE_TEXT)
                );
                cursor.close();
                return GanjoorVerse1;
            }
            cursor.close();
        }
        return null;
    }

    /**
     * Get the next poem
     * @param Poem Current GanjoorPoem
     * @return Next poem or null
     */
    public GanjoorPoem getNextPoem(GanjoorPoem Poem) {
        if (Poem == null) {
            return null;
        }
        return getNextPoem(Poem._ID, Poem._CatID);
    }

    /**
     *Get the next poem
     * @param PoemId Poem ID
     * @param CatId Cat ID
     * @return GanjoorPoem next poem or null
     */
    public GanjoorPoem getNextPoem(int PoemId, int CatId) {
        return getRelPoem(PoemId, CatId, true);
    }


    /**
     * شعر قبلی شعر در یک بخش را بر می گرداند
     * @param PoemId شناسۀ رکورد شعر
     * @param CatId شناسۀ رکورد بخش
     * @return شعر قبلی یا null
     * @todo: add a method to getPrevPoem without specifying CatId
     */
    public GanjoorPoem getPrevPoem(int PoemId, int CatId) {
        return getRelPoem(PoemId, CatId, false);
    }

    /**
     * شعر قبلی شعر در یک بخش را بر می گرداند
     * @param Poem اطلاعات شعر فعلی
     * @return شعر قبلی یا null
     */
    public GanjoorPoem getPrevPoem(GanjoorPoem Poem) {
        if (Poem == null) {
            return null;
        }
        return getPrevPoem(Poem._ID, Poem._CatID);
    }

    /**
     * شعر بعدی یا قبلی یک شعر در یک بخش را بر می گرداند
     * @param PoemId شناسۀّ رکورد شعر
     * @param CatId شناسۀ بخش
     * @param NextOne بعدی را برگرداند یا قبلی را
     * @return شعر بعدی یا قبلی یا null
     */
    private GanjoorPoem getRelPoem(int PoemId, int CatId, Boolean NextOne) {
        if (getIsConnected()) {
            String cmpOperator = NextOne ? ">" : "<";
            String orderClause = NextOne ? "p.id" : "p.id DESC";

            GanjoorPoem GanjoorPoem1;

            Cursor cursor = _db.rawQuery("SELECT p.id, p.cat_id, p.title, p.url,  p.url as urlFake, " +
                    "(SELECT  Count(*) FROM fav Where poem_id=p.id) AS favCount FROM poem p " +
                    "WHERE p.cat_id = " + CatId + " AND p.id " + cmpOperator + " " + PoemId + " ORDER BY " + orderClause + " Limit 1", null);

            if (cursor.moveToFirst()) {
                GanjoorPoem1 = new GanjoorPoem(
                        cursor.getInt(IDX_POEM_ID),
                        cursor.getInt(IDX_POEM_CATID),
                        cursor.getString(IDX_POEM_TITLE),
                        cursor.getString(IDX_POEM_URL),
                        cursor.getInt(IDX_POEM_FAV) != 0,
                        ""
                );
                cursor.close();
                return GanjoorPoem1;
            }

            cursor.close();
        }

        return null;

    }

    /**
     * شاعر بعدی را بر اساس حروف الفبا برمی گرداند
     * @param Poet  اطلاعات رکورد شاعر فعلی
     * @return اطلاعات شاعر بعدی یا null
     */
    public GanjoorPoet getNextPoet(GanjoorPoet Poet) {
        return getRelPoet(Poet, true);
    }

    /**
     * شاعر قبلی را بر اساس حروف الفبا برمی گرداند
     * @param Poet  اطلاعات رکورد شاعر فعلی
     * @return اطلاعات شاعر بعدی یا null
     */
    public GanjoorPoet getPrevPoet(GanjoorPoet Poet) {
        return getRelPoet(Poet, false);
    }

    /**
     * شاعر بعدی یا قبلی یک شاعر را بر می گرداند
     * @param Poet اطلاعات رکورد شاعر
     * @param NextOne بعدی یا قبلی
     * @return اطلاعات شاعر بعدی یا قبلی یا null
     */
    private GanjoorPoet getRelPoet(GanjoorPoet Poet, Boolean NextOne) {
        if (getIsConnected()) {
            String cmpOperator = NextOne ? "> '" : "< '";
            String orderClause = NextOne ? "name" : "name DESC";
            Cursor cursor = _db.query("poet", new String[]{"id", "name", "cat_id", "description", "update_info"}, "name " + cmpOperator + Poet._Name + "'", null, null, null, orderClause, "1");
            if (cursor.moveToFirst()) {
                GanjoorPoet GanjoorPoet1 = new GanjoorPoet(
                        cursor.getInt(IDX_POET_ID),
                        cursor.getString(IDX_POET_NAME),
                        cursor.getInt(IDX_POET_CATID),
                        cursor.getString(IDX_POET_BIO),
                        cursor.getString(IDX_POET_UPDATE)
                );

                cursor.close();
                return GanjoorPoet1;
            }

        }
        return null;
    }

    /**
     *
     * @return بخش بعدی در بخشهای شاعر
     */
    public GanjoorCat getNextCat(GanjoorCat Cat) {
        return getRelCat(Cat, true);
    }

    /**
     * بخش قبلی در بخشهای شاعر
     */
    public GanjoorCat getPrevCat(GanjoorCat Cat) {
        return getRelCat(Cat, false);
    }

    /**
     *
     * @param Cat بخش فعلی
     * @param NextOne
     * @return بخش بعدی یا قبلی در بخشهای شاعر را بر می گرداند
     */
    private GanjoorCat getRelCat(GanjoorCat Cat, Boolean NextOne) {
        if (getIsConnected()) {
            String cmpOperator = NextOne ? ">" : "<";
            String orderClause = NextOne ? "id" : "id DESC";
            Cursor cursor = _db.query("cat", new String[]{"id", "poet_id", "text", "parent_id", "url"}, "poet_id = " + Cat._PoetID + " AND parent_id = " + Cat._ParentID + " AND id " + cmpOperator + " " + Cat._ID, null, null, null, orderClause, "1");

            if (cursor.moveToFirst()) {
                GanjoorCat GanjoorCat1 = new GanjoorCat(
                        cursor.getInt(IDX_CAT_ID),
                        cursor.getInt(IDX_CAT_POETID),
                        cursor.getString(IDX_CAT_TEXT),
                        cursor.getInt(IDX_CAT_PARENTID),
                        cursor.getString(IDX_CAT_URL)
                );
                cursor.close();
                return GanjoorCat1;

            } else {
                GanjoorCat ParentCat = getCat(Cat._ParentID);
                if (ParentCat != null) {
                    return getRelCat(ParentCat, NextOne);
                }
            }
            cursor.close();
        }
        return null;
    }

    /**
     * کپی استریم ورودی به استریم خروجی
     * @param in
     * @param out
     * @throws IOException
     */
    private static void copyInputStream(InputStream in, OutputStream out)
            throws IOException {
        byte[] buffer = new byte[1024];
        int len;

        while ((len = in.read(buffer)) >= 0)
            out.write(buffer, 0, len);

        in.close();
        out.close();
    }

    /**
     * فایل gdb را که پسوندش ممکن است zip باشد به دیتابیس اضافه می کند
     * @param fileName مسیر کامل فایل
     * @return true if succeeds
     */
    public Boolean ImportGdb(String fileName, String updateInfo) {
        if (!fileName.toLowerCase().endsWith(".gdb")) {
            File f = new File(fileName);
            try {
                ZipFile zipFile = new ZipFile(f);
                Enumeration<?> entries = zipFile.entries();

                while (entries.hasMoreElements()) {
                    ZipEntry entry = (ZipEntry) entries.nextElement();

                    if (entry.getName().endsWith(".gdb")) {
                        File gdbFile = new File(new File(new File(fileName).getParent()), entry.getName());
                        copyInputStream(zipFile.getInputStream(entry),
                                new BufferedOutputStream(new FileOutputStream(gdbFile.toString())));

                        Boolean result = ImportDbFastUnsafe(gdbFile.toString(), updateInfo);
                        gdbFile.delete();
                        return result;
                    }

                }

                zipFile.close();
            } catch (ZipException e) {

                Log.e(TAG, "ImportGdb: " + e.getMessage());

                return ImportDbFastUnsafe(fileName, updateInfo);
            } catch (IOException e) {
                Log.e(TAG, "ImportGdb io: " + e.getMessage());
                e.printStackTrace();
            }
        }
        return ImportDbFastUnsafe(fileName, updateInfo);
    }

    /**
     * نمونۀ سادۀ متد ImportDb که چکها و تغییر شناسه ها را در صورت
     * تکراری بودن شناسه ها انجام نمی دهد
     * @param dbPath فایل ورودی
     * @return نتیجۀ کل عملیات
     */
    public Boolean ImportDbFastUnsafe(String dbPath, String updateInfo) {
        try {
            if (!getIsConnected()) {
                return false;
            }
            GanjoorDbBrowser gdbOpener = new GanjoorDbBrowser(this.mContext, dbPath);
            if (!gdbOpener.getIsConnected()) {
                this._LastError = gdbOpener._LastError;
                return false;
            }

            //آپگرید دیتابیسهای قدیمی
            gdbOpener.UpgradeOldDbs();

            //یک چک مقدماتی و البته ناکافی
            List<GanjoorPoet> gdbPoets = gdbOpener.getPoets();
            for (GanjoorPoet gdbPoet : gdbPoets) {
                if (this.getPoet(gdbPoet._ID) != null) {
                    this._LastError = "مجموعۀ ورودی شامل شاعرانی است که شناسۀ آنها با شناسۀ شاعران موجود همسان است";
                    return false;
                }
            }

            Cursor cursor;
            String sql;
            Boolean bResult = true;
            try {
                _db.beginTransaction();

                //کپی cat
                cursor = gdbOpener._db.query("cat", new String[]{"id", "poet_id", "text", "parent_id", "url"}, null, null, null, null, null);
                if (cursor.moveToFirst()) {
                    do {
                        sql = String.format(Locale.ENGLISH, "INSERT INTO cat (id, poet_id, text, parent_id, url) VALUES (%d, %d, \"%s\", %d, \"%s\");",
                                cursor.getInt(IDX_CAT_ID),
                                cursor.getInt(IDX_CAT_POETID),
                                cursor.getString(IDX_CAT_TEXT),
                                cursor.getInt(IDX_CAT_PARENTID),
                                cursor.getString(IDX_CAT_URL)
                        );
                        _db.execSQL(sql);
                    } while (cursor.moveToNext());
                }
                cursor.close();
                //کپی poet
                cursor = gdbOpener._db.query("poet", new String[]{"id", "name", "cat_id", "description"}, null, null, null, null, null);
                if (cursor.moveToFirst()) {
                    do {
                        sql = String.format(Locale.ENGLISH, "INSERT INTO poet (id, name, cat_id, description, update_info) VALUES (%d, \"%s\", %d, \"%s\", \"%s\");",
                                cursor.getInt(IDX_POET_ID),
                                cursor.getString(IDX_POET_NAME),
                                cursor.getInt(IDX_POET_CATID),
                                cursor.getString(IDX_POET_BIO),
                                updateInfo
                        );
                        _db.execSQL(sql);
                    } while (cursor.moveToNext());
                }
                cursor.close();
                //کپی poem
                cursor = gdbOpener._db.query("poem", new String[]{"id", "cat_id", "title", "url"}, null, null, null, null, null);
                if (cursor.moveToFirst()) {
                    do {
                        sql = String.format(Locale.ENGLISH, "INSERT INTO poem (id, cat_id, title, url) VALUES (%d, %d, \"%s\", \"%s\");",
                                cursor.getInt(IDX_POEM_ID),
                                cursor.getInt(IDX_POEM_CATID),
                                cursor.getString(IDX_POEM_TITLE),
                                cursor.getString(IDX_POEM_URL)
                        );
                        _db.execSQL(sql);
                    } while (cursor.moveToNext());
                }
                cursor.close();
                //کپی verse
                cursor = gdbOpener._db.query("verse", new String[]{"poem_id", "vorder", "position", "text"}, null, null, null, null, null);
                if (cursor.moveToFirst()) {
                    do {
                        sql = String.format(Locale.ENGLISH, "INSERT INTO verse (poem_id, vorder, position, text) VALUES (%d, %d, %d, \"%s\");",
                                cursor.getInt(IDX_VERSE_POEMID),
                                cursor.getInt(IDX_VERSE_ORDER),
                                cursor.getInt(IDX_VERSE_POSITION),
                                cursor.getString(IDX_VERSE_TEXT).replace("\"", "\"\"")
                        );
                        _db.execSQL(sql);
                    } while (cursor.moveToNext());
                }
                cursor.close();

                _db.setTransactionSuccessful();
            } catch (Exception expData) {
                Log.e(TAG, "ImportDbFastUnsafe: " + expData.getMessage());
                bResult = false;
            } finally {
                _db.endTransaction();
            }


            gdbOpener.CloseDatabase();

            return bResult;
        } catch (Exception exp) {
            exp.printStackTrace();
            return false;
        }
    }

    //ورژن گذاری و پشتیبانی از دیتابیسهای قدیمی
    private static final int DatabaseVersion = 3;

    //تبدیل دیتابیسهای قدیمی تر
    //تمام مراحل آپگرید دیتابیسهای قدیمی از ورژن دات نت
    // به اینجا منتقل نشده، فقط مواردی که ایجاد مشکل بحرانی می کنند اضاففه شده اند.
    private void UpgradeOldDbs() {
        //اگر جدول poet سه تا فیلد داشته باشد یعنی فیلد
        //description را ندارد و باید آن را اضافه کنیم
        try {
            Cursor cursor = _db.rawQuery("PRAGMA table_info('poet')", null);
            int n = 0;
            if (cursor.moveToFirst()) {
                n++;
                while (cursor.moveToNext()) {
                    n++;
                }
            }
            cursor.close();
            if (n == 3) {

                _db.execSQL("ALTER TABLE poet ADD description TEXT");

                if (tableExists("gver")) {
                    _db.execSQL("DELETE FROM gver");
                    _db.execSQL("INSERT INTO gver (curver) VALUES (" + DatabaseVersion + ")");
                }

            } else if (n == 4) {

                _db.execSQL("ALTER TABLE poet ADD update_info VARCHAR(50)");
                if (tableExists("gver")) {
                    _db.execSQL("INSERT INTO gver (curver) VALUES (" + DatabaseVersion + ")");
                }

            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }


    }

    /**
     *
     * @param tableName String table Name
     * @return boolean
     */
    public boolean tableExists(String tableName) {
        if (getIsConnected()) {

            if (tableName == null) {
                return false;
            }
            Cursor cursor = _db.rawQuery("SELECT COUNT(*) FROM sqlite_master WHERE type = ? AND name = ?", new String[]{"table", tableName});
            if (!cursor.moveToFirst()) {
                cursor.close();
                return false;
            }
            int count = cursor.getInt(0);
            cursor.close();
            return count > 0;
        } else {
            return false;

        }
    }

    /**
     * یک بخش را به همراه تمام زیر بخشها و شعرهای متعلقه حذف می کند
     * @param Cat بخشی که باید حذف شود
     */
    public void DeleteCat(GanjoorCat Cat) {
        if (Cat == null)
            return;
        List<GanjoorCat> subCats = getSubCats(Cat._ID);
        for (GanjoorCat subCat : subCats) {
            DeleteCat(subCat);
        }
        String sql = String.format(Locale.ENGLISH, "DELETE FROM verse WHERE poem_id IN (SELECT id FROM poem WHERE cat_id=%d);", Cat._ID);
        _db.execSQL(sql);
        sql = String.format(Locale.ENGLISH, "DELETE FROM poem WHERE cat_id=%d;", Cat._ID);
        _db.execSQL(sql);
        sql = String.format(Locale.ENGLISH, "DELETE FROM cat WHERE id=%d;", Cat._ID);
        _db.execSQL(sql);


    }


    /**
     * آثار شاعر را از دیتابیس حذف می کند
     * @param Poet شاعری که باید آثارش حذف شود
     */
    public void DeletePoet(GanjoorPoet Poet) {
        DeleteCat(getCat(Poet._CatID));
        String sql = String.format(Locale.ENGLISH, "DELETE FROM poet WHERE id=%d;", Poet._ID);
        _db.execSQL(sql);
    }

    /**
     * VACUUM database (optimize database)
     */
    public void Vacum() {
        if (getIsConnected()) {
            _db.execSQL("VACUUM");
        }

    }


    /**
     * آثار شاعر را از دیتابیس حذف می کند
     * @param poet_id poet id
     */
    public void DeletePoet(int poet_id) {
        GanjoorPoet Poet = getPoet(poet_id);
        DeleteCat(getCat(Poet._CatID));
        String sql = String.format(Locale.ENGLISH, "DELETE FROM poet WHERE id=%d;", Poet._ID);
        _db.execSQL(sql);
    }


    /**
     * Get Top Category(Book) of this category
     * @param cate_id int
     * @return GanjoorCat
     */
    public GanjoorCat getBaseCategory(int cate_id) {

        GanjoorCat cate1 = getCat(cate_id);

        GanjoorCat cate2 = cate1;

        int pid = cate1._ParentID;
        if (pid > 0) {
            while (pid > 0) {
                cate1 = getCat(pid);
                if (cate1._ParentID == 0) {
                    break;
                } else {
                    cate2 = cate1;
                    pid = cate1._ParentID;
                }
            }

            return cate2;
        } else {
            return cate2;
        }
    }


    /**
     * Search in Verses
     * @param phrase phrase for
     * @param poet_id int poet id
     * @param book_ids string comma separated cate id
     * @param offset int start of search pos
     * @param limit int limit
     * @return List<SearchResult> List of SearchResult class
     */
    public List<SearchResult> searchForPhrase(String phrase, int poet_id, String book_ids,
                                              int offset, int limit, int startIndex) {


        List<Integer> newBookIds = new ArrayList<>();
        List<Integer> newBookIdsTemp = new ArrayList<>();

        ActivitySearch activitySearch = (ActivitySearch) mContext;

        List<SearchResult> findedVerse = new ArrayList<>();
        String srcQuery;

        String limitStr = String.format(Locale.ENGLISH, "Limit %d, %d", offset, limit);
        srcQuery = "SELECT v.poem_id, v.vorder, v.position, v.text, pm.title, c2.poet_id, pm.cat_id, pt.name AS poetName, (with parent_tree AS (" +
                "Select id, parent_id, text " +
                "From cat " +
                "Where id = pm.cat_id " +
                "union All " +
                "Select c.id, c.parent_id, c.text " +
                "From cat c " +
                "JOIN parent_tree parent ON c.id = parent.parent_id " +
                "AND c.id != c.parent_id " +
                ") " +
                "Select group_concat(cate_tree.text, ' > ') AS commaTree From (SELECT * From parent_tree ORDER BY id ASC) cate_tree) cate_tree " +
                "From [verse] v "
                + "INNER JOIN [poem] pm ON v.poem_id = pm.id "
                + "INNER JOIN [cat] c2 ON c2.id = pm.cat_id "
                + "INNER JOIN [poet] pt ON pt.id = c2.poet_id "
                + "Where v.text like '%" + phrase + "%' ";

        String countQuery1 = "Select Count(verse.poem_id) AS resCount From verse "
                + "INNER JOIN [poem] pm ON verse.poem_id = pm.id "
                + "INNER JOIN [cat] c2 ON c2.id = pm.cat_id "
                + "INNER JOIN [poet] pt ON pt.id = c2.poet_id "
                + "Where verse.text like '%" + phrase + "%' ";


        if (poet_id <= 0) {
            srcQuery += limitStr;
        } else {
            String srcQueryPoet = String.format(Locale.ENGLISH, "AND (c2.poet_id = %d) ", poet_id);
            srcQuery += srcQueryPoet;
            countQuery1 += srcQueryPoet;
            srcQuery += limitStr;
        }

        // Cursor cursor_count = _db.rawQuery(countQuery1, null);
        // cursor_count.moveToFirst();

        // activitySearch.resCount = cursor_count.getInt(0);
        // cursor_count.close();

        Cursor cursor = _db.rawQuery(srcQuery, null);

        int index = startIndex;
        if (cursor.moveToFirst()) {
            do {
                index++;
                findedVerse.add(
                        new SearchResult(
                                cursor.getInt(IDX_VERSE_POEMID),
                                cursor.getInt(IDX_VERSE_ORDER),
                                cursor.getInt(IDX_VERSE_POSITION),
                                cursor.getString(IDX_VERSE_TEXT),
                                cursor.getString(4),
                                cursor.getInt(5),
                                cursor.getInt(6),
                                cursor.getString(7),
                                cursor.getString(8),
                                index
                        )
                );

            } while (cursor.moveToNext());
        }

        cursor.close();

        return findedVerse;
    }


    /**
     * Search in Verses
     * @param phrase phrase for
     * @param poet_id int poet id
     * @param book_ids string comma separated cate id
     * @param offset int start of search pos
     * @param limit int limit
     * @return List<SearchResult> List of SearchResult class
     */
    public List<SearchResult> searchForPhrase2(String phrase, int poet_id, String book_ids,
                                               int offset, int limit, int startIndex) {


        List<Integer> newBookIds = new ArrayList<>();
        List<Integer> newBookIdsTemp = new ArrayList<>();

        ActivitySearch activitySearch = (ActivitySearch) mContext;

        List<SearchResult> findedVerse = new ArrayList<>();

        String srcQuery = "";


        String limitStr = String.format(Locale.ENGLISH, "Limit %d, %d", offset, limit);

        String countQuery1 = "Select Count(v.poem_id) AS resCount From [verse] v "
                + "INNER JOIN [poem] pm ON v.poem_id = pm.id "
                + "INNER JOIN [cat] c2 ON c2.id = pm.cat_id "
                + "INNER JOIN [poet] pt ON pt.id = c2.poet_id "
                + "Where v.text like '%" + phrase + "%' ";

        if (poet_id <= 0) {


            srcQuery = "SELECT * FROM verse " +
                    "INNER JOIN [poem] pm ON verse.poem_id = pm.id " +
                    "INNER JOIN [cat] c2 ON c2.id = pm.cat_id " +
                    "INNER JOIN [poet] pt ON pt.id = c2.poet_id " +
                    "WHERE verse.text Like '%" + phrase + "%' ";

            srcQuery += limitStr;

        } else {

            // String srcQueryPoet = String.format(Locale.ENGLISH, "AND (c2.poet_id = %d) ", poet_id);

            srcQuery = "SELECT * FROM verse " +
                    "INNER JOIN [poem] pm ON verse.poem_id = pm.id " +
                    "INNER JOIN [cat] c2 ON c2.id = pm.cat_id " +
                    "INNER JOIN [poet] pt ON pt.id = c2.poet_id " +
                    "WHERE (verse.text Like '%" + phrase + "%') AND (c2.poet_id=" + poet_id + ") ";


            srcQuery += limitStr;
            countQuery1 += " AND (c2.poet_id=" + poet_id + ")";
        }

        Cursor cursor_count = _db.rawQuery(countQuery1, null);
        cursor_count.moveToFirst();

        activitySearch.resCount = cursor_count.getInt(0);
        cursor_count.close();


        Cursor cursor = _db.rawQuery(srcQuery, null);

        int index = startIndex;
        if (cursor.moveToFirst()) {
            do {

                int poem_id = cursor.getInt(IDX_VERSE_POEMID);

                index++;
                findedVerse.add(
                        new SearchResult(
                                cursor.getInt(IDX_VERSE_POEMID),
                                cursor.getInt(IDX_VERSE_ORDER),
                                cursor.getInt(IDX_VERSE_POSITION),
                                cursor.getString(IDX_VERSE_TEXT),
                                "",
                                0,
                                0,
                                "",
                                "",
                                index
                        )
                );

            } while (cursor.moveToNext());
        }
        cursor.close();
        return findedVerse;
    }
}

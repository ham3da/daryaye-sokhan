/**
 *
 */
package ir.ham3da.darya.ganjoor;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

/**
 * @author Hamid Reza
 * پردازش لیستهای دریافت مجموعه ها را با این کلاس انجام می دهم
 */
public class GDBList {

    /**
     * سازنده را مخفی نگه می داریم تا ساخته شدن نمونه کاملاً در کنترل خودمان باشد
     * و بتوانیم به ازای فایلهای مشکلدار ورودی به راحتی
     * null
     * برگردانیم
     */
    private GDBList() {
        _Items = new LinkedList<GDBInfo>();
    }

    /**
     * سازندۀ کپی کنندۀ کلاس
     * @param source منبع
     */
    public GDBList(GDBList source) {
        this._ListUrl = source._ListUrl;
        _Items = new LinkedList<GDBInfo>();
        for (GDBInfo Item : source._Items) {
            _Items.add(new GDBInfo(Item));
        }
    }

    private static boolean checkInstalled(int poetID, List<GanjoorPoet> exsistPoets) {
        boolean res = false;
        for (GanjoorPoet ganjoorPoet : exsistPoets) {

            if (ganjoorPoet._ID == poetID) {
                res = true;
                break;
            }

        }
        return res;

    }

    /**
     *
     * @param poetID poet ID
     * @param exsistPoets Poets list
     * @param dateAndSize data And Size as Separated string by |
     * @return boolean true if update available
     */
    public static boolean CheckUpdateAvailable(int poetID, List<GanjoorPoet> exsistPoets, String dateAndSize) {
        boolean res = false;
        for (GanjoorPoet ganjoorPoet : exsistPoets) {
            if (ganjoorPoet._ID == poetID) {

                String currentDateAndSize = ganjoorPoet._Update_info;

                if (!currentDateAndSize.equals(dateAndSize)) {
                    res = true;
                }
                break;
            }

        }
        return res;
    }


    /**
     * متد پردازش و ساخت نمونه
     * @param nListId به لیستهای ورودی یک شناسۀ یکتا نسبت می دهیم تا بعدا بتوانیم در صورت مخلوط کردن آنها آنها را ردگیری و فیلتر کنیم
     * @param inputStream فایل ورودی
     * @param db یک نمونه از دیتابیس جهت حذف شاعران موجود
     * @return نمونه ایجاد شده
     * @throws IOException
     * @throws XmlPullParserException
     */
    public static GDBList Build(int nListId, InputStream inputStream, GanjoorDbBrowser db) throws IOException, XmlPullParserException {


        List<GanjoorPoet> poets = db.getPoets();

        GDBList result = new GDBList();

        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();

        factory.setNamespaceAware(true);
        XmlPullParser xpp = factory.newPullParser();
        xpp.setInput(inputStream, null);
        int eventType = xpp.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_TAG) {
                String tagName = xpp.getName();
                if (tagName.equals("gdb")) {
                    GDBInfo gdbInfo = GDBInfo.BuildFromXmlPullParser(nListId, xpp, eventType);
                    if (gdbInfo != null) {

                        gdbInfo._Exist = checkInstalled(gdbInfo._PoetID, poets);

                        if (gdbInfo._Exist) {

                            String dateAndSize = gdbInfo._PubDateString   + "|" + gdbInfo._FileSizeInByte;
                            gdbInfo._UpdateAvailable = CheckUpdateAvailable(gdbInfo._PoetID, poets, dateAndSize);
                        }
                        // if(db.getPoet(gdbInfo._PoetID) == null){
                        result._Items.add(gdbInfo);
                        // }
                    }
                }
            }
            eventType = xpp.next();
        }

        inputStream.close();
        if (result._Items.size() == 0)
            return null;
        return result;
    }

    /**
     *
     * @param lists لیستهای ورودی
     * @return مخلوط شده لیستهای ورودی با یکی کردن موارد تکراری
     */
    public static GDBList Mix(List<GDBList> lists) {
        if (lists == null)
            return null;
        if (lists.size() > 0) {
            GDBList result = new GDBList(lists.get(0));
            for (int i = 1; i < lists.size(); i++) {
                GDBList list = lists.get(i);
                for (GDBInfo Item : list._Items) {
                    int idx = result.FindSimilarIndex(Item);
                    if (idx == -1) {
                        result._Items.add(Item);
                    } else {
                        if (result._Items.get(idx)._PubDate.compareTo(Item._PubDate) < 0) {
                            GDBInfo repItem = new GDBInfo(Item);
                            result._Items.set(idx, repItem);
                        }
                    }
                }

            }
            return result;
        }
        return null;
    }

    /**
     * جستجوی موارد مشابه
     * @param inputItem
     * @return اندیس جدیدترین مورد مشابه
     */
    public int FindSimilarIndex(GDBInfo inputItem) {
        int CatId = inputItem._CatID;
        int idx = -1;
        for (int i = 0; i < this._Items.size(); i++) {
            if (this._Items.get(i)._CatID == CatId) {
                if (idx == -1) {
                    idx = i;
                } else {
                    if (this._Items.get(idx)._PubDate.compareTo(this._Items.get(i)._PubDate) < 0) {
                        idx = i;
                    }
                }
            }
        }
        return idx;
    }

    /**
     * نشانی لیست
     */
    public String _ListUrl;

    /**
     * لیست آیتمها
     */
    public List<GDBInfo> _Items;


}

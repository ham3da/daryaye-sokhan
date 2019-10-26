package ir.ham3da.darya.ganjoor;

public class CateWithPoem
{

    public static final int TYPE_CATEGORY = 1;
    public static final int TYPE_POEM = 2;

    /**
     * Default initialization: Creates an instance of a class with input values
     *
     * @param ID
     * @param PoetID
     * @param Text
     * @param ParentID
     * @param UrlAddress
     * @param StartPoem
     * @param itemType
     * @param isFave
     * @param FirstVerse
     */
    public CateWithPoem(int ID, int PoetID, String Text, int ParentID, String UrlAddress, int StartPoem, int itemType, boolean isFave, String FirstVerse) {
        _ID = ID;
        _PoetID = PoetID;
        _Text = Text;
        _ParentID = ParentID;
        _Url = UrlAddress;
        _StartPoem = StartPoem;
        _Type = itemType;
        _Faved = isFave;
        _FirstVerse = FirstVerse;
    }


    /**
     * @summary record id
     * @database-field id
     */
    public int _ID;
    /**
     * @summary record poet id
     * @database-field poet_id
     */
    public int _PoetID;
    /**
     * @summary record text
     * @database-field text
     */
    public String _Text;


    /**
     * متن اولین خط یا مصرع شعر
     */
    public String _FirstVerse;

    /**
     * @summary شناسۀ رکورد بخش والد
     * id از رکورد دیگری از جدول cat
     * اگر صفر باشد یعنی مربوط به بخشهای ریشه (شاعران) است
     * @database-field parent_id
     */
    public int _ParentID;
    /**
     * @summary نشانی بخش در سایت گنجور ganjoor.net
     * @database-field url
     */
    public String _Url;
    /**
     * در صورتی که تعداد شعرهای بخش بیشتر از «حداکثر تعداد عنوانها در فهرست اشعار یک بخش» باشد مقدار غیر صفر برای این
     * فیلد نشان می دهد فهرست اشعار باید نه از ابتدا که از این عدد به بعد تا همان حداکثر نمایش داده شود.
     */
    public int _StartPoem;

    /**
     * item type : 1 = category, 2 = poem
     */
    public int _Type;

    /**
     * آیا کل شعر نشانه گذاری شده یا خیر (وضعیت دکمۀ نشانه گذاری - حذف نشانه از روی این فیلد تعیین می شود)
     */
    public Boolean _Faved;


}

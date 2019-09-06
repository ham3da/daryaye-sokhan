/**
 * 
 */
package ir.ham3da.darya.ganjoor;

/**
 * @author Hamid Reza, Javad
 * @database-table cat
 * @summary Category info
 */
public class GanjoorCat {
    /**
     * Default initialization: Creates an instance of a class with input values
     * @param ID
     * @param PoetID
     * @param Text
     * @param ParentID
     * @param Url
     * @param StartPoem
     */
    public GanjoorCat(int ID, int PoetID, String Text, int ParentID, String Url, int StartPoem)
    {
        _ID = ID;
        _PoetID = PoetID;
        _Text = Text;
        _ParentID = ParentID;
        _Url = Url;
        _StartPoem = StartPoem;
    }	

    /**
     * Default initialization: Creates an instance of a class with input values
     * @param ID
     * @param PoetID
     * @param Text
     * @param ParentID
     * @param Url
     */
    public GanjoorCat(int ID, int PoetID, String Text, int ParentID, String Url)
    {
    	this(ID, PoetID, Text, ParentID, Url, 0);
    }

    /**
     * Default initialization: Creates an instance of a class with input values
     * @param baseCat
     * @param StartPoem
     */
    public GanjoorCat(GanjoorCat baseCat, int StartPoem)
    {
    	this(baseCat._ID, baseCat._PoetID, baseCat._Text, baseCat._ParentID, baseCat._Url, StartPoem);
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
     * Select Category
     * Used in search limits dialog
     */
    public boolean _Selected;

}

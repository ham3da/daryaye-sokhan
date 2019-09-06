/**
 * 
 */
package ir.ham3da.darya.ganjoor;

/**
 * @author Hamid Reza
 * @database-table poem
 * @summary اطلاعات شعر : مصاریع یا خطوط شعر با لیستی از نمونه های کلاس GanjoorVerse که _PoemId همه آنها برابر _ID نمونۀ این کلاس است مشخص می شود.
 */
public class GanjoorPoem {	
	/**
	 * @database-field id
	 * @summary شناسۀ رکورد شعر
	 */		
    public int _ID;
	/**
	 * @database-field cat_id
	 * @summary شناسۀ رکورد بخشی که شعر به آن تعلق دارد
	 */	    
    public int _CatID;
	/**
	 * @database-field title
	 * @summary عنوان شعر
	 */	    
    public String _Title;
	/**
	 * @database-field url
	 * @summary نشانی شعر در سایت گنجور ganjoor.net
	 */	    
    public String _Url;
	/**
	 * در نمایش شعر در صورتی که قرار است متنی در کل شعر برجسته و متفاوت نشان داده شود مقدار این متن در این فیلد نگهداری می شود
	 */	    
    public String _HighlightText;
	/**
	 * آیا کل شعر نشانه گذاری شده یا خیر (وضعیت دکمۀ نشانه گذاری - حذف نشانه از روی این فیلد تعیین می شود)
	 */	    
    public Boolean _Faved;
    
    /**
     * متن اولین خط یا مصرع شعر
     */
    public String _FirstVerse;

	/**
	 * سازندۀ پیش فرض : یک نمونه از کلاس را با مقادیر ورودی می سازد
	 */    
    public GanjoorPoem(int ID, int CatID, String Title, String Url, Boolean Faved)        
    {    
    	this(ID, CatID, Title, Url, Faved, "", "");
    }
    
	/**
	 * سازندۀ پیش فرض : یک نمونه از کلاس را با مقادیر ورودی می سازد
	 */    
    public GanjoorPoem(int ID, int CatID, String Title, String Url, Boolean Faved, String FirstVerse)        
    {    
    	this(ID, CatID, Title, Url, Faved, "", FirstVerse);
    }
    
    
	/**
	 * سازندۀ پیش فرض : یک نمونه از کلاس را با مقادیر ورودی می سازد
	 */    
    public GanjoorPoem(int ID, int CatID, String Title, String Url, Boolean Faved, String HighlightText, String FirstVerse)
    {
        _ID = ID;
        _CatID = CatID;
        _Title = Title;
        _Url = Url;
        _Faved = Faved;
        _HighlightText = HighlightText;
        _FirstVerse = FirstVerse;
    }

}

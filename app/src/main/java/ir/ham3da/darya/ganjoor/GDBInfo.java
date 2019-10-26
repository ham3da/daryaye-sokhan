package ir.ham3da.darya.ganjoor;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

/**
 * @author Hamid Reza
 * مشخصات مجموعه های قابل دریافت از اینترنت
 */
public class GDBInfo {
    /**
     * سازندۀ آن را private تعریف کرده ایم
     * از بیرون کلاس آن را با استفاده از متد
     * BuildFromXmlPullParser
     * می سازیم
     */
    private GDBInfo(int nListId){
        _GDBListIdSet = new int[1];
        _GDBListIdSet[0] = nListId;
    }

    /**
     * سازندۀ کپی کنندۀ ورودی
     * @param source ورودی
     */
    public GDBInfo(GDBInfo source)
    {
        _CatName 			= source._CatName;
        _PoetID 			= source._PoetID;
        _CatID 			= source._CatID;
        _DownloadUrl 		= source._DownloadUrl;
        _BlogUrl 			= source._BlogUrl;
        _FileExt 			= source._FileExt;
        _ImageUrl 			= source._ImageUrl;
        _FileSizeInByte 	= source._FileSizeInByte;
        _LowestPoemID 		= source._LowestPoemID;
        _PubDate 			= source._PubDate;

        _GDBListIdSet		= new int[source._GDBListIdSet.length];
        for(int i=0; i<source._GDBListIdSet.length; i++){
            _GDBListIdSet[i] = source._GDBListIdSet[i];
        }

        _UpdateAvailable = source._UpdateAvailable;
        _Exist = source._Exist;
        _PubDateString = source._PubDateString;
        _Index = source._Index;

    }

    /**
     * ایجاد یک نمونه از کلاس
     * GDBInfo
     * با پارس xml
     *
     * نمونۀ بلوکی که باید این متد پارس کند:
     *
     *	 <gdb>
     *	    <CatName>فردوسی</CatName>
     *	    <PoetID>4</PoetID>
     *	    <CatID>32</CatID>
     *	    <DownloadUrl>http://sourceforge.net/projects/ganjoor/files/gdb/frdvsi.zip</DownloadUrl>
     *	    <FileExt>.zip</FileExt>
     *	    <ImageUrl>http://ganjoor.sourceforge.net/saaghar/4.png</ImageUrl>
     *	    <FileSizeInByte>2784409</FileSizeInByte>
     *	    <LowestPoemID>1321</LowestPoemID>
     *	    <PubDate>2011-09-24</PubDate>
     *	  </gdb>
     *
     * @param nListId به لیستهای ورودی یک شناسۀ یکتا نسبت می دهیم تا بعدا بتوانیم در صورت مخلوط کردن آنها آنها را ردگیری و فیلتر کنیم
     * @param xpp قبل از فراخوانی این متد باید xml باز شده باشد و به اولین تگ gdb رسید باشیم.
     * @param eventType xpp.getEventType() یا  xpp.next() خروجی آخرین
     * @return ساخته شده GDBInfo نمونه
     * @throws XmlPullParserException
     * @throws IOException
     */
    public static GDBInfo BuildFromXmlPullParser(int nListId, XmlPullParser xpp, int eventType ) throws XmlPullParserException, IOException{
        //
		/*
		 * نمونۀ بلوکی که باید این متد پارس کند:
		 *
		 <gdb>
		    <CatName>فردوسی</CatName>
		    <PoetID>4</PoetID>
		    <CatID>32</CatID>
		    <DownloadUrl>http://sourceforge.net/projects/ganjoor/files/gdb/frdvsi.zip</DownloadUrl>
		    <FileExt>.zip</FileExt>
		    <ImageUrl>http://ganjoor.sourceforge.net/saaghar/4.png</ImageUrl>
		    <FileSizeInByte>2784409</FileSizeInByte>
		    <LowestPoemID>1321</LowestPoemID>
		    <PubDate>2011-09-24</PubDate>
		  </gdb>

		  * البته لزوماً همۀ تگها وجود ندارند

		 */
        if(eventType == XmlPullParser.START_TAG){
            String gdbTagName =  xpp.getName();
            if(gdbTagName.equals("gdb")){

                //تگ باز gdb:

                GDBInfo result = new GDBInfo(nListId);//نتیجه را در این متغیر نگه می داریم

                do
                {
                    eventType = xpp.next();
                    //اتفاق بعدی که می افتد می تواند بسته شدن تگ باز gdb باشد یا باز شدن یک تگ بچۀ جدید

                    if(eventType == XmlPullParser.END_TAG){
                        String closingTagName = xpp.getName();
                        if(closingTagName.equals("gdb")){
                            return result;
                        }
                    }
                    else
                    if(eventType == XmlPullParser.START_TAG){
                        String tagName = xpp.getName();
                        String tagValue = xpp.nextText();

                        if(tagName.equals("CatName")){
                            result._CatName =  tagValue;
                        }
                        else
                        if(tagName.equals("PoetID")){
                            result._PoetID =  Integer.parseInt(tagValue);
                        }
                        else
                        if(tagName.equals("CatID")){
                            result._CatID = Integer.parseInt(tagValue);
                        }
                        else
                        if(tagName.equals("DownloadUrl")){
                            result._DownloadUrl = tagValue;
                        }
                        else
                        if(tagName.equals("FileExt")){
                            result._FileExt = tagValue;
                        }
                        else
                        if(tagName.equals("ImageUrl")){
                            result._ImageUrl = tagValue;
                        }
                        else
                        if(tagName.equals("FileSizeInByte")){
                            result._FileSizeInByte = Integer.parseInt(tagValue);
                        }
                        else
                        if(tagName.equals("LowestPoemID")){
                            result._LowestPoemID = Integer.parseInt(tagValue);
                        }
                        else
                        if(tagName.equals("PubDate"))
                        {

                            result._PubDateString = tagValue;

                            SimpleDateFormat dateFormat  = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                            try {
                                result._PubDate = dateFormat.parse(tagValue);
                            } catch (ParseException e) {
                                 e.printStackTrace();
                            }
                        }
                    }
                }
                while(eventType != XmlPullParser.END_DOCUMENT);
            }
        }
        return null;
    }
    /**
     * شاعر/بخش
     */
    public String _CatName = null;

    /**
     * شناسهٔ شاعر
     */
    public int _PoetID = 0;

    /**
     * شناسهٔ بخش
     */
    public int _CatID = 0;

    /**
     * نشانی دریافت
     */
    public String _DownloadUrl = null;

    /**
     * نشانی توضیحات
     */
    public String _BlogUrl = null;

    /**
     * پسوند
     */
    public String _FileExt = null;

    /**
     * نشانی تصویر
     */
    public String _ImageUrl = null;

    /**
     * اندازه
     */
    public int _FileSizeInByte = 0;

    /**
     * اولین شعر
     */
    public int _LowestPoemID = 0;

    /**
     * تاریخ انتشار
     */
    public Date _PubDate = null;

    public String _PubDateString;

    /**
     * شناسۀ لیست
     * این فیلد را اضافه بر ورژن
     * .NET
     * در نظر می گیرم چون قصد دارم در ورژن اندروید لیستها را با هم مخلوط کنم
     * و البته بعدا امکان فیلتر کردن بر اساس آنها را در اختیار داشته باشم
     */
    public int[] _GDBListIdSet;


    /**
     * آیا شاعر  از قبل در دیتابیس وجود دارد یا خیر ؟
     */
    public boolean _Exist = false;

    /**
     * آیا آپدیت جدیدی برای اشعار این شاعر موجود است؟
     */
    public boolean _UpdateAvailable = false;

    public int _Index;

}

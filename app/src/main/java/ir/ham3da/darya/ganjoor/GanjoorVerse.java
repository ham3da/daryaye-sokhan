/**
 *
 */
package ir.ham3da.darya.ganjoor;

/**
 * @author Hamid Reza
 * @database-table verse
 * @summary اطلاعات مصرعهای اشعار، همینطور خطوط شعر نیمایی یا آزاد یا پاراگرافهای نثر در این ساختار نگهداری می شود.
 */
public class GanjoorVerse {

    /**
     * @summary سازندۀ پیش فرض
     */
    public GanjoorVerse(int PoemID, int Order, int Position, String Text) {
        _PoemID = PoemID;
        _Order = Order;
        _Position = Position;
        _Text = Text;
    }

    /**
     * @database-field poem_id
     * @summary شناسۀ شعر مرتبط
     */
    public int _PoemID;

    /**
     * @database-field vorder
     * @summary ترتیب مصرع در کل شعر بدون توجه به _Position
     */
    public int _Order;

    /**
     *  مقادیر ساختار enum VersePosition در نگارش .NET
     * ثابتهای تعریف شده نوع مصرع را نشان می دهند
     * فیلد position در جدول verse یکی از این مقادیر را دارد.
     */

    /**
     *  مصرع اول
     */
    public static final int POSTION_RIGHT = 0;

    /**
     * مصرع دوم
     */
    public static final int POSITION_LEFT = 1;

    /**
     *  مصرع اول یا تنهای ابیات ترجیع یا ترکیب
     */
    public static final int POSITION_CENTEREDVERSE1 = 2;

    /**
     *  مصرع دوم ابیات ترجیع یا ترکیب
     */
    public static final int POSITION_CENTEREDVERSE2 = 3;

    /**
     * مصرعهای شعرهای نیمایی یا آزاد
     */
    public static final int POSITION_SINGLE = 4;

    /**
     * نثر
     */
    public static final int POSITION_PARAGRAPH = -1;


    /**
     * @database-field position
     * @summary یکی از مقادیر ثابتهای POSITION_*
     */
    public int _Position;

    /**
     * @database-field text
     * @summary متن مصرع
     */
    public String _Text;

}

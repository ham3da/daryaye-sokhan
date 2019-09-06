package ir.ham3da.darya.ganjoor;

public class SearchResult {


    public SearchResult(int PoemID, int Order, int Position, String Text, String PoemTitle, int PoetID, int PoemCatID, String PoetName, String CatTree, int Index) {
        _PoemID = PoemID;
        _Order = Order;
        _Position = Position;
        _Text = Text;

        _PoemTitle = PoemTitle;
        _PoetID = PoetID;
        _PoemCatID = PoemCatID;
        _PoetName = PoetName;
        _CatTree = CatTree;
        _Index = Index;
    }

    /**
     * Index from 1 to result count
     */
    public int _Index;

    /**
     * Poem Title
     */
    public String _PoemTitle;

    /**
     * Poet id
     */
    public int _PoetID;

    /**
     * Poem Category ID
     */
    public int _PoemCatID;

    /**
     * Poet Name
     */
    public String _PoetName;

    /**
     * Category name of found verse as tree
     */
    public String _CatTree;

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
     * First line
     * مصرع اول
     */
    public static final int POSTION_RIGHT = 0;

    /**
     * Second line
     * مصرع دوم
     */
    public static final int POSITION_LEFT = 1;

    /**
     * Refrain first line
     * مصرع اول یا تنهای ابیات ترجیع یا ترکیب
     */
    public static final int POSITION_CENTEREDVERSE1 = 2;

    /**
     * Refrain second line
     * مصرع دوم ابیات ترجیع یا ترکیب
     */
    public static final int POSITION_CENTEREDVERSE2 = 3;

    /**
     * New Poem(Nimaei)
     */
    public static final int POSITION_SINGLE = 4;

    /**
     * Prose
     * نثر
     */
    public static final int POSITION_PARAGRAPH = -1;


    /**
     * @database-field position
     * @summary یکی از مقادیر ثابتهای POSITION_*
     */
    public int _Position;

    /**
     * Text of verse
     *
     * @database-field text
     * @summary متن مصرع
     */
    public String _Text;
}


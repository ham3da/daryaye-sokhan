package ir.ham3da.darya.adaptors;

public class ScheduleGDB
{
    public int _Pos;
    public int _PoetID;
    public String _URL;
    public String _FileName;
    public String _Update_info;
    public boolean _DoUpdate;
    public String _CateName;

    public ScheduleGDB(int Pos, int PoetID, String CateName, String URL, String FileName,  String Update_info, boolean DoUpdate)
    {
        _Pos = Pos;
        _PoetID = PoetID;
        _URL = URL;
        _FileName = FileName;
        _Update_info = Update_info;
        _DoUpdate = DoUpdate;
        _CateName = CateName;
    }

}

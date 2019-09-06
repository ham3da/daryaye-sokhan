package ir.ham3da.darya.ganjoor;

public class GanjoorVerseB {

    public int _PoemID;
    public int _Position;
    public int _Index;
    public int _Vorder1;
    public int _Vorder2;
    public String _Text1;
    public String _Text2;
    public Boolean _Centered;
    public Boolean _Selected;


    public GanjoorVerseB(int PoemID, int Position, String Text1, String Text2, boolean Centered, boolean Selected, int Index, int Vorder1, int Vorder2) {

        _PoemID = PoemID;
        _Position = Position;
        _Text1 = Text1;
        _Text2 = Text2;
        _Centered = Centered;
        _Selected = Selected;
        _Index = Index;
        _Vorder1 = Vorder1;
        _Vorder2 = Vorder2;

    }


}

package ir.ham3da.darya.ganjoor;

import ir.ham3da.darya.ganjoor.GanjoorPoem;

public class FavoritesPoem  extends GanjoorPoem
{


    public FavoritesPoem(int ID, int CatID, String Title, String Url, Boolean Faved, String FirstVerse, String PoetName, String CatTree, int Index)
    {
        super( ID,  CatID,  Title,  Url, Faved, FirstVerse);

        _PoetName = PoetName;
        _CatTree = CatTree;
        _Index = Index;
    }

    /**
     * Index from 1 to result count
     */
    public int _Index;
    /**
     * Poet Name
     */
    public String _PoetName;

    /**
     * Category name of found verse as tree
     */
    public String _CatTree;

}

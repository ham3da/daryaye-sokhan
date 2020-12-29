package ir.ham3da.darya.adaptors;

import android.content.Context;
import android.util.Log;

import ir.ham3da.darya.utility.UtilFunctions;

public class BackGroundItem
{
    Context mContext;

    public BackGroundItem(Context context)
    {
        mContext = context;
    }

    private int id;

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }


    public int getResIDSmall()
    {
        return UtilFunctions.getResID(mContext, "sm_darya_" + this.id);
    }
    public int getResIDBig()
    {
        return UtilFunctions.getResID(mContext, "darya_" + this.id);
    }

}
package ir.ham3da.darya.utility;


import java.io.Serializable;

public class SerializableNotify implements Serializable
{

    int rnd_poem_id;
    String findStr ;
    int vOrder;


    public SerializableNotify()
    {
    }

    public SerializableNotify(int pid, String findStr, int vOrder) {
        this.rnd_poem_id = pid;
        this.findStr = findStr;
        this.vOrder = vOrder;
    }

    public int getRnd_poem_id()
    {
        return rnd_poem_id;
    }

    public int getvOrder()
    {
        return vOrder;
    }

    public String getFindStr()
    {
        return findStr;
    }

    public void setFindStr(String findStr)
    {
        this.findStr = findStr;
    }

    public void setRnd_poem_id(int rnd_poem_id)
    {
        this.rnd_poem_id = rnd_poem_id;
    }

    public void setvOrder(int vOrder)
    {
        this.vOrder = vOrder;
    }
}

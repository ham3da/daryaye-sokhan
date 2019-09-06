package ir.ham3da.darya.utility;

import java.io.Serializable;

public class BackupSettingItem  implements Serializable
{
    String itemName;
    String itemValue;
    public BackupSettingItem(String Name, String Value) {
        super();
        this.itemName = Name;
        this.itemValue = Value;
    }

    @Override
    public String toString() {
        return "BackupSettingItem [itemName=" + itemName + ", itemValue=" + itemValue + "]";
    }
}

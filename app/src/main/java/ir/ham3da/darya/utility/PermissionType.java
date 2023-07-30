package ir.ham3da.darya.utility;

public class PermissionType
{
    private String Permission;
    private Integer requestCode;

    public PermissionType()
    {

    }
    public PermissionType(String PermissionParam, Integer requestCodeParam)
    {
        this.Permission = PermissionParam;
        this.requestCode = requestCodeParam;
    }

    public void setPermission(String permission)
    {
        Permission = permission;
    }

    public void setRequestCode(Integer requestCode)
    {
        this.requestCode = requestCode;
    }

    public Integer getRequestCode()
    {
        return requestCode;
    }

    public String getPermission()
    {
        return Permission;
    }
}

package ir.ham3da.darya.utility;

public class PoemAudio {
    public int poemID;
    public int id;
    public String filePath;
    public String description;
    public String dnldurl;
    public boolean isdirect;
    public String syncguid;
    public String fchksum;
    public boolean isuploaded;

    public PoemAudio(int poemID, int id, String filePath, String description, String dnldurl, boolean isdirect, String syncguid, String fchksum, boolean isuploaded) {

        this.poemID = poemID;
        this.id = id;
        this.filePath = filePath;
        this.description = description;
        this.dnldurl = dnldurl;
        this.isdirect = isdirect;
        this.syncguid = syncguid;
        this.fchksum = fchksum;
        this.isuploaded = isuploaded;
    }

    public PoemAudio(String filePath)
    {
        this.filePath = filePath;
    }

}

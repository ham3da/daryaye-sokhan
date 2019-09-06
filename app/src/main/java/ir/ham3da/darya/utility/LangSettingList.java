package ir.ham3da.darya.utility;

public class LangSettingList
{
    private int id;
    private String text;
    private String Tag;
    private String Country;
    public int index;

    /**
     *
     * @param id
     * @param text
     * @param tag
     */
    public LangSettingList(int id, String text, String tag)
    {
        this.id = id;;
        this.text = text;
        this.Tag = tag;
    }

    /**
     *
     * @param id
     * @param text
     * @param tag
     * @param country
     */
    public LangSettingList(int id, String text, String tag, String country)
    {
        this.id = id;;
        this.text = text;
        this.Tag = tag;
        this.Country = country;
    }

    public String getTag() {
        return Tag;
    }

    public String getText() {
        return text;
    }

    public String getCountry() {
        return Country;
    }
    //getters
    public int getId() {
        return id;
    }


}
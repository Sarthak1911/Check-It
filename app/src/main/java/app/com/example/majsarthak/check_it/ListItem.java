package app.com.example.majsarthak.check_it;


class ListItem {

    private int id;
    private String name = null;
    private Boolean checked = false;
    private String date;

    ListItem(int id, String name, Boolean checked, String date)
    {
        this.id = id;
        this.name = name;
        this.checked = checked;
        this.date = date;
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }



    Boolean isChecked()
    {
        return checked;
    }

    void setChecked(Boolean checked)
    {
        this.checked = checked;
    }

    String getDate()
    {
        return this.date;
    }

    public void setDate(String date)
    {
        this.date = date;
    }
}



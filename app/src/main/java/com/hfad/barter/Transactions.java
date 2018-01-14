package com.hfad.barter;

public class Transactions {

    String id;
    String lend;
    String itemname;
    String LBname;
    String description;
    String datetime;
    String favorite;

    public Transactions(String id,String lend, String itemname, String LBname, String description, String datetime,
                        String favorite ) {
        this.id = id;
        this.lend = lend;
        this.itemname = itemname;
        this.LBname = LBname;
        this.description = description;
        this.datetime = datetime;
        this.favorite = favorite;
    }

    public String getID(){ return id;}

    public String getLend() {
        return lend;
    }

    public void setLend(String lend) {
        this.lend = lend;
    }

    public String getItemname() {
        return itemname;
    }

    public void setItemname(String itemname) {
        this.itemname = itemname;
    }

    public String getLBname() {
        return LBname;
    }

    public void setLBname(String LBname) {
        this.LBname = LBname;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFavorite() {
        return favorite;
    }

    public void setFavorite(String favorite) {
        this.favorite = favorite;
    }


}

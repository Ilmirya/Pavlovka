package com.example.pavlovka.Classes;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RecordFromFoldersGet {
   /* @SerializedName("name")
    @Expose
    public String name;

    @SerializedName("id")
    @Expose
    public String id;

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }*/
   @SerializedName("data")
   @Expose
   public String data;

    @SerializedName("children")
    @Expose
    public String[] children;

    public String getData() {
        return data;
    }

    public String[] getChildren() {
        return children;
    }
}


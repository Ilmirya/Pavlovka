package com.example.pavlovka.Classes;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FoldersGet {
    //----------------------------Set null

    //---------------------------Get

    @SerializedName("tube")
    @Expose
    public RecordFromFoldersGet root;

    public RecordFromFoldersGet getRoot() {
        return root;
    }
}


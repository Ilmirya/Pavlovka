package com.example.pavlovka.Classes.RowCache2And3;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RowFromRowCache {

    @SerializedName("id")
    @Expose
    public String id;

    @SerializedName("name")
    @Expose
    public String name;

    @SerializedName("pname")
    @Expose
    public String pname;

    @SerializedName("indication")
    @Expose
    public String indication;

    @SerializedName("indicationUnitMeasurement")
    @Expose
    public String indicationUnitMeasurement;

    @SerializedName("controllerData")
    @Expose
    public String controllerData;

    @SerializedName("event")
    @Expose
    public String event;

    @SerializedName("value")
    @Expose
    public String value;

    @SerializedName("description")
    @Expose
    public String description;



    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPname() {
        return pname;
    }

    public String getIndication() {
        return indication;
    }

    public String getIndicationUnitMeasurement() {
        return indicationUnitMeasurement;
    }

    public String getControllerData() {
        return controllerData;
    }

    public String getEvent() {
        return event;
    }

    public String getValue() {
        return value;
    }

    public String getDescription() {
        return description;
    }

}

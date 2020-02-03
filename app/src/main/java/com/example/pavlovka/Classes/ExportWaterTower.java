package com.example.pavlovka.Classes;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ExportWaterTower {
    // ---------------------Set
    @SerializedName("objectId")
    @Expose
    public String objectId;

    public void setWaterTower(String objectId) {
        this.objectId = objectId;
    }
    // ---------------------Get
    @SerializedName("max")
    @Expose
    public String max;

    @SerializedName("min")
    @Expose
    public String min;

    @SerializedName("interval")
    @Expose
    public String interval;

    @SerializedName("criticalMax")
    @Expose
    public String criticalMax ;

    @SerializedName("criticalMin")
    @Expose
    public String criticalMin ;

    @SerializedName("controlMode")
    @Expose
    public String controlMode ;

    public String getMax() {
        return max;
    }
    public String getMin() {
        return min;
    }

    public String getCriticalMin() {
        return criticalMin;
    }

    public String getCriticalMax() {
        return criticalMax;
    }

    public String getInterval() {
        return interval;
    }

    public String getControlMode() {
        return controlMode;
    }

}


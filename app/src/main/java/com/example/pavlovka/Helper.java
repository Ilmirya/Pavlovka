package com.example.pavlovka;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.example.pavlovka.POJO.QueryFromDatabase.RecordsFromQueryDB;

import java.util.ArrayList;

public class Helper {
    public static String getMetaData(Context context, String name) {
        try {
            ApplicationInfo ai = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = ai.metaData;
            return bundle.getString(name);
        } catch (PackageManager.NameNotFoundException e) {
        }
        return null;
    }
    public static RecordsFromQueryDB GetLastRecordByType(RecordsFromQueryDB[] records, String type){
        ArrayList<RecordsFromQueryDB> recordsLocal= new ArrayList<>();

        try{
            for(RecordsFromQueryDB rec : records){
                if(rec.s1.equals(type)){
                    recordsLocal.add(rec);
                }
            }
            return recordsLocal.get(recordsLocal.size() - 1);
        }
        catch(Exception ex){}
        return  null;
    }
    public static ArrayList<RecordsFromQueryDB> GetRecordsByType(RecordsFromQueryDB[] records, String type){
        ArrayList<RecordsFromQueryDB> recordsLocal= new ArrayList<>();
        try{
            for(RecordsFromQueryDB rec : records){
                if(rec.s1.equals(type)){
                    recordsLocal.add(rec);
                }
            }
            return recordsLocal;
        }
        catch (Exception ex){}
        return null;
    }
    public static String GetS2FromRec(RecordsFromQueryDB record){
        try{
            return record.getS2();
        }
        catch (Exception ex){}
        return "";
    }
    public static String GetD1FromRec(RecordsFromQueryDB record){
        try{
            return record.getD1s();
        }
        catch (Exception ex){}
        return "";
    }
}

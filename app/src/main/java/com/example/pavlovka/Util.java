package com.example.pavlovka;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

/**
 * Created by Nirmal Dhara on 12-07-2015.
 */
public class Util {
    public static String getProperty(String key, Context context) throws IOException {
        return getProperties(context,"config").getProperty(key);
    }
    public static String getPropertyOrSetDefaultValue(String key, String value, Context context) throws IOException {
        String valueTmp = getProperty(key, context);
        if(valueTmp == null){
            setPropertyConfig(key, value, context);
            valueTmp = getProperty(key, context);
        }
        return valueTmp;
    }
    public static Properties getProperties(Context context, String fileName) throws IOException{
        Properties properties = new Properties();
        FileInputStream fileInputStream = new FileInputStream(context.getFilesDir()+"/"+ fileName + ".properties");
        properties.load(fileInputStream);
        return properties;
    }
    public static void setProperty(String key, String value, Context context, String fileName) throws IOException {
        if(value == null) return;
        Properties properties = new Properties();
        ArrayList<String> listKeys = new ArrayList<>();
        try {
            Properties properties1 = getProperties(context, fileName);
            Enumeration<String> enumerationStr = (Enumeration<String>) properties1.propertyNames();
            while (enumerationStr.hasMoreElements()){
                listKeys.add(enumerationStr.nextElement());
            }

            properties = properties1;
        } catch (IOException e) {
            e.printStackTrace();
            new File(context.getFilesDir(), fileName + ".properties");
        }
        if(listKeys.size() < 50){
            properties.setProperty(key,value);
        }
        else{
            Date dtTmp = new Date();
            String strKey = "";
            for(String keyTmp : listKeys){
                Date dtKey = new Date();
                DateFormat formatter = new SimpleDateFormat("dd.MM.yy HH:mm:ss");
                try {
                    dtKey = formatter.parse(keyTmp.split("#")[1]);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if(dtTmp.after(dtKey)){
                    strKey = keyTmp;
                    dtTmp = dtKey;
                }
            }
            properties.remove(strKey);
            properties.setProperty(key,value);
        }
        FileOutputStream fileOutputStream = new FileOutputStream(context.getFilesDir()+"/"+ fileName + ".properties");
        properties.store(fileOutputStream,null);

    }
    public static void setPropertyConfig(String key, String value, Context context) throws IOException {
        if(value == null) return;
        Properties properties = new Properties();
        try {
            Properties properties1 = getProperties(context, "config");
            properties = properties1;
        } catch (IOException e) {
            e.printStackTrace();
            new File(context.getFilesDir(), "config.properties");
        }
        properties.setProperty(key,value);
        FileOutputStream fileOutputStream = new FileOutputStream(context.getFilesDir()+"/config.properties");
        properties.store(fileOutputStream,null);
    }
    public static void logsInfo(String value, Context context){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yy HH:mm:ss");
        String key = simpleDateFormat.format(new Date());
        try {
            setProperty("I#" + key, value, context, "logs");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void logsError(String value, Context context){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yy HH:mm:ss");
        String key = simpleDateFormat.format(new Date());
        try {
            setProperty("E#" + key, value, context, "logs");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
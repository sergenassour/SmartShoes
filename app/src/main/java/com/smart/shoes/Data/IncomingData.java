package com.smart.shoes.Data;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.util.Log;

import com.smart.shoes.Database.Database;
import com.smart.shoes.Models.SensorModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class IncomingData {
    Context context;
    Database database;
    public IncomingData(Context context) {
        this.context = context;
        database=new Database(context);
    }

    public void parseData(String incomingJsonData){
        Log.d("Data From Device",incomingJsonData);
         // after parse initialize the following values

              database.clearDatabase();

        // insert some values in the database
           SensorModel sm1=new SensorModel("1","2","fit","00:00:5",getCurrentDate());
           database.addInstance(sm1);setData(sm1);

        SensorModel sm2=new SensorModel("1","3","fit","00:00:5",getCurrentDate());
        database.addInstance(sm2);setData(sm2);
        SensorModel sm3=new SensorModel("2","4","tired","00:00:5",getCurrentDate());
        database.addInstance(sm3);setData(sm3);
        SensorModel sm4=new SensorModel("3","7","fit","00:00:5",getCurrentDate());
        database.addInstance(sm4);setData(sm4);
        SensorModel sm5=new SensorModel("15","13","fit","00:00:5",getCurrentDate());
        database.addInstance(sm5);setData(sm5);
        SensorModel sm6=new SensorModel("5","20","normal","00:00:5",getCurrentDate());
        database.addInstance(sm6);setData(sm6);
        SensorModel sm7=new SensorModel("10","12","fit","00:00:5",getCurrentDate());
        database.addInstance(sm7);setData(sm7);
        SensorModel sm8=new SensorModel("7","2","fit","00:00:5",getCurrentDate());
        database.addInstance(sm8);setData(sm8);
        SensorModel sm9=new SensorModel("8","3","fit","00:00:5",getCurrentDate());
        database.addInstance(sm9);setData(sm9);
        SensorModel sm10=new SensorModel("5","7","tired","00:00:5",getCurrentDate());
        database.addInstance(sm10);setData(sm10);
    }
    public void setData(SensorModel sm){
        SharedPreferences sh=context.getSharedPreferences("sensorsFile", MODE_PRIVATE);
        SharedPreferences.Editor myEdit = sh.edit();
        myEdit.putString("speed",sm.getSpeed());
        myEdit.putString("distance",sm.getDistance());
        myEdit.putString("emgSignal",sm.getEmgSignal());
        myEdit.putString("time",sm.getTime());
        myEdit.apply();
    }

    public String getSpeed(){
        SharedPreferences sh=context.getSharedPreferences("sensorsFile", MODE_PRIVATE);
        if(!sh.contains("speed")){
            SharedPreferences.Editor myEdit = sh.edit();
            myEdit.putString("speed","");
            myEdit.apply();
        }
        return sh.getString("speed","");
    }
    public String getDistance(){
        SharedPreferences sh=context.getSharedPreferences("sensorsFile", MODE_PRIVATE);
        if(!sh.contains("distance")){
            SharedPreferences.Editor myEdit = sh.edit();
            myEdit.putString("distance","");
            myEdit.apply();
        }
        return sh.getString("distance","");
    }

    public String getEmgSignal(){
        SharedPreferences sh=context.getSharedPreferences("sensorsFile", MODE_PRIVATE);
        if(!sh.contains("emgSignal")){
            SharedPreferences.Editor myEdit = sh.edit();
            myEdit.putString("emgSignal","");
            myEdit.apply();
        }
        return sh.getString("emgSignal","");
    }
    public String getTime(){
        SharedPreferences sh=context.getSharedPreferences("sensorsFile", MODE_PRIVATE);
        if(!sh.contains("time")){
            SharedPreferences.Editor myEdit = sh.edit();
            myEdit.putString("time","");
            myEdit.apply();
        }
        return sh.getString("time","");
    }
    private String getCurrentDate() {
        return  new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
    }
}

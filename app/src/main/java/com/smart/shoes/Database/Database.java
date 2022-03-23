package com.smart.shoes.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.smart.shoes.Models.SensorModel;

import java.util.ArrayList;
import java.util.List;


public class Database extends SQLiteOpenHelper {

    private Context context;
    private static final String DATABASE_NAME = "sensorsData.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_SENSOR = "table_sensor";

    private static final String INSTANCE_ID = "instance_id";
    private static final String SPEED = "speed";
    private static final String DISTANCE = "distance";
    private static final String EMG_SIGNAL = "emg_signal";
    private static final String TIME= "time";
    private static final String DATE= "date";



    public Database(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {



        String query = "CREATE TABLE " + TABLE_SENSOR +
                " (" + INSTANCE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                SPEED + " TEXT, " +
                DISTANCE + " TEXT, " +
                EMG_SIGNAL + " TEXT, " +
                TIME + " TEXT, " +
                DATE + " TEXT);";




        db.execSQL(query);


    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SENSOR);
        onCreate(db);
    }


    public boolean addInstance(SensorModel sensorModel){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(SPEED, sensorModel.getSpeed());
        contentValues.put(DISTANCE,sensorModel.getDistance());
        contentValues.put(EMG_SIGNAL,sensorModel.getEmgSignal());
        contentValues.put(TIME,sensorModel.getTime());
        contentValues.put(DATE,sensorModel.getDate());

        long result = db.insert(TABLE_SENSOR,null, contentValues );
        return result != -1;
    }


    public List<SensorModel> readAllInstances(){
        String query = "SELECT * FROM " + TABLE_SENSOR;
        SQLiteDatabase db = this.getReadableDatabase();
        List<SensorModel> sensorModels=new ArrayList<>();
        Cursor cursor = null;
        if(db != null){
            cursor = db.rawQuery(query, null);
            if(cursor.moveToFirst()){
                do {
                    SensorModel sm=new SensorModel();
                    sm.setSpeed(cursor.getString(1));
                    sm.setDistance(cursor.getString(2));
                    sm.setEmgSignal(cursor.getString(3));
                    sm.setTime(cursor.getString(4));
                    sm.setDate(cursor.getString(5));
                    sensorModels.add(sm);
                }while (cursor.moveToNext());

            }
        }
        return sensorModels;
    }
    public void clearDatabase(){
        SQLiteDatabase db=this.getWritableDatabase();
        db.delete(TABLE_SENSOR, null, null);
        db.close();
    }
}

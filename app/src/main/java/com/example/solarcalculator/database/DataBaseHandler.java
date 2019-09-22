package com.example.solarcalculator.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.solarcalculator.Models.LocationModels;

import java.util.ArrayList;

public class DataBaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "Locations";
    private static final String TABLE_LOCATIONS = "SavedLocations";

    private static String CREATE_TABLE = "CREATE TABLE " + TABLE_LOCATIONS + " (id INTEGER PRIMARY KEY AUTOINCREMENT," +
            " latitude TEXT, longitude TEXT, place TEXT, sunrise TEXT, sunset TEXT," +
            " moonrise TEXT, moonset TEXT)";


    public DataBaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public void saveLocation(LocationModels model) {

        try {
            SQLiteDatabase database = getWritableDatabase();
            ContentValues values = new ContentValues();

            values.put("latitude", model.getLatitude());
            values.put("longitude", model.getLongitude());
            values.put("place", model.getLocation());
            values.put("sunrise", model.getSunrise());
            values.put("sunset", model.getSunset());
            values.put("moonrise", model.getMoonrise());
            values.put("moonset", model.getMoonset());

            Log.e("loc "+ model.getLocation()," Sunrise "+model.getSunrise()+" Sunset "+model.getSunset());

            database.insert(TABLE_LOCATIONS, null, values);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        Log.e("Location ", "Saved");

    }

    public ArrayList<LocationModels> retrieveValues(){

        ArrayList<LocationModels> locationModels = new ArrayList<>();
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor =null;

        try{
            cursor = database.rawQuery("SELECT * FROM SavedLocations",null);
            if (cursor !=null){
                if (cursor.moveToFirst()){
                    do{
                        LocationModels models = new LocationModels();
                        String latitude = cursor.getString(1);
                        models.setLatitude(latitude);
                        String longitude = cursor.getString(2);
                        models.setLongitude(longitude);
                        String location = cursor.getString(3);
                        models.setLocation(location);
                        String sunrise = cursor.getString(4);
                        models.setSunrise(sunrise);
                        String sunset = cursor.getString(5);
                        models.setSunset(sunset);
                       // String moonrise = cursor.getString(6);
                       // models.setMoonrise(moonrise);
                        //String moonset = cursor.getString(7);
                        //models.setMoonset(moonset);

                        Log.e("loc "+ latitude," Sunrise "+sunrise+" Sunset "+sunset);
                        locationModels.add(models);


                    }while (cursor.moveToNext());
                }
            }



        }catch (Exception ex){
            ex.printStackTrace();
        }

        return locationModels;

    }


}

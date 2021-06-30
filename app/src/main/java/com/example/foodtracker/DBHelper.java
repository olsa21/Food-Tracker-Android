package com.example.foodtracker;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

public class DBHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "foodtracker.db";

    public DBHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //Hier werden die Tabellen erstellt
        db.execSQL(DBContract.createTableFood);
        db.execSQL(DBContract.createTableLog);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DBContract.deleteEntries);
        onCreate(db);
    }

    public String buildSqlFoodInsert(JSONObject product) {
        String sql = "";
        try {
            JSONObject nutrition = product.getJSONObject("nutriments");

            sql = "INSERT OR IGNORE INTO food VALUES (" +//Ignoriere wenn Produkt bereits in Tabelle
                    product.getString("ean") + ", \"" +
                    product.getString("brands") + "\", \"" +
                    product.getString("product_name_de") + "\", " +
                    nutrition.getString("energy-kcal_100g") + ", " +
                    nutrition.getString("fat_100g") + ", " +
                    nutrition.getString("saturated-fat_100g") + ", " +
                    nutrition.getString("carbohydrates_100g") + ", " +
                    nutrition.getString("sugars_100g") + ", " +
                    nutrition.getString("proteins_100g") + ", " +
                    nutrition.getString("salt_100g") + ");";

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return sql;
    }

    public String buildSqlLogInsert(String ean, float menge) {
        String sql = "INSERT INTO log VALUES (null," + ean + ", " + menge + ", datetime('now','localtime'));";
        return sql;
    }
}

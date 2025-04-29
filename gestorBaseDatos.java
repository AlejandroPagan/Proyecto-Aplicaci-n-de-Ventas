package com.example.aplicacinaselab02;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class gestorBaseDatos extends SQLiteOpenHelper {
    public gestorBaseDatos(@Nullable Context context) {
        super(context, "tablaVentas", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE IF NOT EXISTS ventas_comerciales (" +
                "id_comercial INTEGER PRIMARY KEY, " +
                "nombre TEXT, " +
                "media_2024 INTEGER, " +
                "enero INTEGER, " +
                "febrero INTEGER, " +
                "marzo INTEGER," +
                "abril INTEGER," +
                "mayo INTEGER,"+
                "junio INTEGER,"+
                "julio INTEGER,"+
                "agosto INTEGER,"+
                "septiembre INTEGER,"+
                "octubre INTEGER,"+
                "noviembre INTEGER,"+
                "diciembre INTEGER)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
    public void insertarVenta(int id, String nombre, int media2024, int enero, int febrero, int marzo) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("id_comercial", id);
        values.put("nombre", nombre);
        values.put("media_2024", media2024);
        values.put("enero", enero);
        values.put("febrero", febrero);
        values.put("marzo", marzo);

        db.insert("ventas_comerciales", null, values);
    }

}

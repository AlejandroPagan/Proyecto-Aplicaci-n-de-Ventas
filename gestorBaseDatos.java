package com.example.aplicacinaselab02;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class gestorBaseDatos extends SQLiteOpenHelper {

    public gestorBaseDatos(@Nullable Context context) {
        super(context, "tablaVentas", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        crearTablaVentas(db);
        insertarValoresIniciales(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Si actualizas la base de datos, puedes hacer un DROP aquí y llamar a onCreate
    }

    public void insertarOVentasAcumuladas(int idComercial, String nombre, String mes, int ventasNuevas) {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT " + mes + " FROM ventas_comerciales WHERE id_comercial = ?", new String[]{String.valueOf(idComercial)});

        if (cursor.moveToFirst()) {
            int ventasActuales = cursor.getInt(0);
            int ventasTotales = ventasActuales + ventasNuevas;

            ContentValues valores = new ContentValues();
            valores.put(mes, ventasTotales);

            db.update("ventas_comerciales", valores, "id_comercial = ?", new String[]{String.valueOf(idComercial)});
        } else {
            ContentValues valores = new ContentValues();
            valores.put("id_comercial", idComercial);
            valores.put("nombre", nombre);
            valores.put(mes, ventasNuevas);
            db.insert("ventas_comerciales", null, valores);
        }

        cursor.close();
    }

    public void vaciarTabla() {
        SQLiteDatabase db = this.getWritableDatabase();

        // Eliminar la tabla si existe
        db.execSQL("DROP TABLE IF EXISTS ventas_comerciales");

        // Crear tabla y valores iniciales sin llamar a onCreate()
        crearTablaVentas(db);
        insertarValoresIniciales(db);
    }

    // Métodos privados para evitar duplicación de código
    private void crearTablaVentas(SQLiteDatabase db) {
        String createTable = "CREATE TABLE ventas_comerciales (" +
                "id_comercial INTEGER PRIMARY KEY, " +
                "nombre TEXT, " +
                "media_2024 INTEGER, " +
                "enero INTEGER, " +
                "febrero INTEGER, " +
                "marzo INTEGER," +
                "abril INTEGER," +
                "mayo INTEGER," +
                "junio INTEGER," +
                "julio INTEGER," +
                "agosto INTEGER," +
                "septiembre INTEGER," +
                "octubre INTEGER," +
                "noviembre INTEGER," +
                "diciembre INTEGER)";
        db.execSQL(createTable);
    }

    private void insertarValoresIniciales(SQLiteDatabase db) {
        String insertValues = "INSERT INTO ventas_comerciales (id_comercial, nombre, media_2024, enero, febrero, marzo, abril, mayo, junio, julio, agosto, septiembre, octubre, noviembre, diciembre) " +
                "VALUES (1, 'TV', 0, 24, 8, 30, 0, 0, 0, 0, 0, 0, 0, 0, 0), " +
                "(2, 'RV', 0, 206, 68, 50, 0, 0, 0, 0, 0, 0, 0, 0, 0), " +
                "(3, 'PV', 0, 15, 14, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0),"+
                "(4, 'AUSENTES', 0, 15, 14, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0)";
        db.execSQL(insertValues);
    }

    // Método extra si necesitas insertar un comercial vacío
    private void insertarComercialInicial(SQLiteDatabase db, int id, String nombre) {
        ContentValues valores = new ContentValues();
        valores.put("id_comercial", id);
        valores.put("nombre", nombre);
        valores.put("media_2024", 0);
        valores.put("enero", 0);
        valores.put("febrero", 0);
        valores.put("marzo", 0);
        valores.put("abril", 0);
        valores.put("mayo", 0);
        valores.put("junio", 0);
        valores.put("julio", 0);
        valores.put("agosto", 0);
        valores.put("septiembre", 0);
        valores.put("octubre", 0);
        valores.put("noviembre", 0);
        valores.put("diciembre", 0);
        db.insert("ventas_comerciales", null, valores);
    }
}

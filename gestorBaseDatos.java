package com.example.aplicacinaselab02;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

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

    public boolean existeComercial(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        boolean existe = false;
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT id_comercial FROM ventas_comerciales WHERE id_comercial = ?", new String[]{String.valueOf(id)});
            existe = cursor.moveToFirst();
        } catch (Exception e) {
            e.printStackTrace(); // Para ver errores en Logcat
        } finally {
            if (cursor != null) cursor.close();
        }
        return existe;
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

    public void moverVentasAausentes(int idComercial) {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor columnasCursor = db.rawQuery("PRAGMA table_info(ventas_comerciales)", null);
        List<String> columnas = new ArrayList<>();
        while (columnasCursor.moveToNext()) {
            String nombreColumna = columnasCursor.getString(columnasCursor.getColumnIndexOrThrow("name"));
            if (!nombreColumna.equals("id_comercial") && !nombreColumna.equals("nombre")) {
                columnas.add(nombreColumna);
            }
        }
        columnasCursor.close();

        Cursor cursorComercial = db.rawQuery("SELECT * FROM ventas_comerciales WHERE id_comercial = ?", new String[]{String.valueOf(idComercial)});
        if (!cursorComercial.moveToFirst()) {
            cursorComercial.close();
            return;
        }

        Cursor cursorAusentes = db.rawQuery("SELECT * FROM ventas_comerciales WHERE nombre = ?", new String[]{"AUSENTES"});
        if (!cursorAusentes.moveToFirst()) {
            cursorAusentes.close();
            cursorComercial.close();
            return;
        }

        ContentValues valoresActualizados = new ContentValues();

        for (String columna : columnas) {
            int valorComercial = cursorComercial.getInt(cursorComercial.getColumnIndexOrThrow(columna));
            int valorAusente = cursorAusentes.getInt(cursorAusentes.getColumnIndexOrThrow(columna));
            valoresActualizados.put(columna, valorComercial + valorAusente);
        }

        db.update("ventas_comerciales", valoresActualizados, "nombre = ?", new String[]{"AUSENTES"});

        cursorComercial.close();
        cursorAusentes.close();
    }

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
                "(3, 'PV', 0, 15, 14, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0)," +
                "(4, 'AUSENTES', 0, 15, 14, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0)";
        db.execSQL(insertValues);
    }
    public boolean insertarNuevoComercial(int id, String nombre) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues valores = new ContentValues();
        valores.put("id_comercial", id);
        valores.put("nombre", nombre);

        // Inicializamos todas las columnas de meses a 0
        String[] meses = {"media_2024", "enero", "febrero", "marzo", "abril", "mayo", "junio",
                "julio", "agosto", "septiembre", "octubre", "noviembre", "diciembre"};
        for (String mes : meses) {
            valores.put(mes, 0);
        }

        long resultado = db.insert("ventas_comerciales", null, valores);
        return resultado != -1;
    }
    public int obtenerVentasDeComercial(int id, String mes) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        int ventas = 0;

        try {
            cursor = db.rawQuery("SELECT " + mes + " FROM ventas_comerciales WHERE id_comercial = ?", new String[]{String.valueOf(id)});
            if (cursor.moveToFirst()) {
                ventas = cursor.getInt(0);
            }
        } catch (Exception e) {
            e.printStackTrace(); // útil para ver errores en Logcat si el mes no es válido
        } finally {
            if (cursor != null) cursor.close();
        }

        return ventas;
    }

    public void actualizarVentas(int id, String mes, int nuevasVentas) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues valores = new ContentValues();
        valores.put(mes, nuevasVentas);
        db.update("ventas_comerciales", valores, "id_comercial = ?", new String[]{String.valueOf(id)});
    }

}


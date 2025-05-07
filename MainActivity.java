package com.example.aplicacinaselab02;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private BarChart barChart;
    private Button btnActualizar,btnVentaEspecial;
    private TextView totalVentas;
    private TableLayout table;
    private TextView mesMasVentas;
    private Intent intent;

    private gestorBaseDatos db;

    private final String[] MESES = {
            "enero", "febrero", "marzo", "abril", "mayo",
            "junio", "julio", "agosto", "septiembre", "octubre",
            "noviembre", "diciembre"
    };

    private ActivityResultLauncher<Intent> activityResultLauncher;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mesMasVentas = findViewById(R.id.mesMasVentas);
        barChart = findViewById(R.id.barChart);
        btnActualizar = findViewById(R.id.btnActualizar);
        btnVentaEspecial= findViewById(R.id.btnVentaEspecial);
        totalVentas = findViewById(R.id.totalVentas);
        table = findViewById(R.id.tablero_main);

        db = new gestorBaseDatos(this);

        // Cargar datos reales desde la base de datos
        cargarDatosDesdeBaseDeDatos();

        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        cargarDatosDesdeBaseDeDatos(); // Recargar datos reales al volver
                    }
                }
        );

        btnActualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AgregarVentasActivity.class);
                activityResultLauncher.launch(intent);
            }
        });

        btnVentaEspecial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AgregarVentasEspeciales2.class);
                activityResultLauncher.launch(intent);
            }
        });
    }

    // Método para cargar todos los datos desde la base de datos
    private void cargarDatosDesdeBaseDeDatos() {
        table.removeAllViews(); // Limpia la tabla
// Encabezado dinámico
        TableRow header = new TableRow(this);
        header.setBackgroundColor(0xFFCCCCCC); // Gris claro

        String[] columnas = {"NUMID", "NOMBRE", "ENERO", "FEBRERO", "MARZO", "ABRIL", "MAYO", "JUNIO",
                "JULIO", "AGOSTO", "SEPTIEMBRE", "OCTUBRE", "NOVIEMBRE", "DICIEMBRE","ELIMINAR"};

        for (String col : columnas) {
            TextView celda = crearCelda(col);
            celda.setTypeface(null, android.graphics.Typeface.BOLD);
            header.addView(celda);
        }

        table.addView(header);

        List<BarEntry> entradasGrafico = new ArrayList<>();
        int[] ventasMensuales = new int[12];
        int total = 0;

        // Realiza la consulta para obtener todos los datos de ventas_comerciales
        Cursor cursor = db.getReadableDatabase().rawQuery("SELECT * FROM ventas_comerciales", null);
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id_comercial"));
                String nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre"));
                TableRow row = new TableRow(this);
                row.addView(crearCelda(String.valueOf(id)));
                row.addView(crearCelda(nombre));


                // Agregar las ventas por mes a la fila y actualizar las ventas mensuales
                for (int i = 0; i < MESES.length; i++) {
                    int ventas = cursor.getInt(cursor.getColumnIndexOrThrow(MESES[i]));
                    row.addView(crearCelda(String.valueOf(ventas)));

                    // Acumular ventas mensuales y el total
                    ventasMensuales[i] += ventas;
                    total += ventas;
                }

                // Añadir la fila de datos a la tabla
                table.addView(row);
                Button btnEliminar = new Button(this);
                btnEliminar.setText("🗑️");
                btnEliminar.setBackgroundColor(Color.TRANSPARENT);
                btnEliminar.setPadding(6, 6, 6, 6);
                btnEliminar.setTextColor(Color.RED);

// Necesitas final para usar en el OnClick
                final int idFinal = id;

                btnEliminar.setOnClickListener(v -> {
                    // Crear el AlertDialog
                                db.getWritableDatabase().delete("ventas_comerciales", "id_comercial = ?", new String[]{String.valueOf(idFinal)});
                                cargarDatosDesdeBaseDeDatos(); // Recargar la tabla actualizada
                });


                row.addView(btnEliminar);

            } while (cursor.moveToNext());
        }
        cursor.close();

        // Crear las entradas para el gráfico de barras con las ventas mensuales
        for (int i = 0; i < ventasMensuales.length; i++) {
            entradasGrafico.add(new BarEntry(i, ventasMensuales[i]));
        }

        // Encontrar el mes con el máximo de ventas
        int mesMax = 0;
        int maxVentas = 0;
        for (int i = 0; i < ventasMensuales.length; i++) {
            if (ventasMensuales[i] > maxVentas) {
                maxVentas = ventasMensuales[i];
                mesMax = i;
            }
        }

        // Mostrar el mes con las ventas más altas
        mesMasVentas.setText(capitalizar(MESES[mesMax]));

        // Actualizar el total de ventas
        totalVentas.setText(String.valueOf(total));

        // Mostrar el gráfico con las ventas mensuales
        mostrarGrafico(entradasGrafico);


    }


    // Método para crear celdas de la tabla
    private TextView crearCelda(String texto) {
        TextView cell = new TextView(this);
        cell.setText(texto);
        cell.setPadding(6, 6, 6, 6);
        cell.setGravity(Gravity.CENTER);
        cell.setTextColor(getResources().getColor(android.R.color.black));
        return cell;
    }

    // Mostrar gráfica con datos proporcionados
    private void mostrarGrafico(List<BarEntry> entradas) {
        BarDataSet dataSet = new BarDataSet(entradas, "Ventas por mes");
        dataSet.setColor(getResources().getColor(R.color.black));
        BarData data = new BarData(dataSet);
        data.setBarWidth(0.9f);

        barChart.setData(data);
        barChart.setFitBars(true);

        Description description = new Description();
        description.setText("Ventas 2025");
        barChart.setDescription(description);
        barChart.invalidate();
    }
    private String capitalizar(String texto) {
        if (texto == null || texto.isEmpty()) return texto;
        return texto.substring(0, 1).toUpperCase() + texto.substring(1);
    }
}




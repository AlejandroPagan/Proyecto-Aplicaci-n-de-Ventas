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
import android.widget.HorizontalScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;


import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private BarChart barChart;
    private Button btnActualizar,btnVentaEspecial;
    private TextView totalVentas;
    private TableLayout tablaCabecera, tablaCuerpo;

    private TextView mesMasVentas;
    private Intent intent;

    private gestorBaseDatos db;

    private final String[] MESES = {
            "enero", "febrero", "marzo", "abril", "mayo",
            "junio", "julio", "agosto", "septiembre", "octubre",
            "noviembre", "diciembre"
    };
    private final String[] MESES_CORTOS = {
            "ene", "feb", "mar", "abr", "may",
            "jun", "jul", "ago", "sep", "oct",
            "nov", "dic"
    };

    private ActivityResultLauncher<Intent> activityResultLauncher;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        mesMasVentas = findViewById(R.id.mesMasVentas);
        barChart = findViewById(R.id.barChart);
        btnActualizar = findViewById(R.id.btnActualizar);
        btnVentaEspecial= findViewById(R.id.btnVentaEspecial);
        totalVentas = findViewById(R.id.totalVentas);
        tablaCabecera = findViewById(R.id.tabla_cabecera);
        tablaCuerpo = findViewById(R.id.tablero_main);
        HorizontalScrollView headerScroll = findViewById(R.id.horizontalScrollViewHeader);
        HorizontalScrollView bodyScroll = findViewById(R.id.horizontalScrollViewBody);

// Sincroniza el desplazamiento horizontal
        headerScroll.setOnScrollChangeListener((v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            bodyScroll.scrollTo(scrollX, 0);
        });

        bodyScroll.setOnScrollChangeListener((v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            headerScroll.scrollTo(scrollX, 0);
        });


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
        tablaCabecera.removeAllViews(); // Limpia la cabecera
        tablaCuerpo.removeAllViews();   // Limpia el cuerpo

// Encabezado dinámico
        TableRow header = new TableRow(this);
        header.setBackgroundColor(0xFFCCCCCC); // Gris claro

        String[] columnas = {"NUMID", "NOMBRE", "ENERO", "FEBRERO", "MARZO", "ABRIL", "MAYO", "JUNIO",
                "JULIO", "AGOSTO", "SEPTI", "OCTUB", "NOVIEM", "DICIEM","ELIMINAR"};

        for (String col : columnas) {
            TextView celda = crearCelda(col);
            celda.setTypeface(null, android.graphics.Typeface.BOLD);
            header.addView(celda);
        }

        tablaCabecera.addView(header);


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
                if (id > 4) {
                    Button btnEliminar = new Button(this);
                    btnEliminar.setText("🗑️");
                    btnEliminar.setBackgroundColor(Color.TRANSPARENT);
                    btnEliminar.setPadding(6, 6, 6, 6);
                    btnEliminar.setTextColor(Color.RED);

                    final int idFinal = id;
                    final String nombreFinal = nombre;

                    btnEliminar.setOnClickListener(v -> {
                        new AlertDialog.Builder(this)
                                .setTitle("Confirmar eliminación")
                                .setMessage(Html.fromHtml("¿Estás seguro de que deseas eliminar al comercial <b>" + nombreFinal + "</b> con ID <b>" + idFinal + "</b>?"))
                                .setPositiveButton("Sí", (dialog, which) -> {
                                    db.moverVentasAausentes(idFinal);
                                    db.getWritableDatabase().delete("ventas_comerciales", "id_comercial = ?", new String[]{String.valueOf(idFinal)});
                                    cargarDatosDesdeBaseDeDatos();
                                })
                                .setNegativeButton("Cancelar", null)
                                .show();
                    });

                    row.addView(btnEliminar);
                } else {
                    // Para mantener la alineación en la tabla (una celda vacía en lugar del botón)
                    TextView celdaVacia = crearCelda(""); // O puedes usar un espacio o guion si lo prefieres
                    row.addView(celdaVacia);
                }

                tablaCuerpo.addView(row);

            } while (cursor.moveToNext());
        }
        cursor.close();
        igualarAnchoColumnas(tablaCabecera, tablaCuerpo);
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

        // Altura fija (por ejemplo 48dp convertidos a píxeles)
        int alturaFija = (int) (32 * getResources().getDisplayMetrics().density); // 48dp
        cell.setHeight(alturaFija);

        return cell;
    }


    // Mostrar gráfica con datos proporcionados
    private void mostrarGrafico(List<BarEntry> entradas) {
        // Crear el dataset con color naranja
        BarDataSet dataSet = new BarDataSet(entradas, "Ventas por mes");
        dataSet.setColor(Color.parseColor("#FFA500")); // Naranja
        dataSet.setValueTextColor(Color.WHITE); // Números blancos encima de cada barra
        dataSet.setValueTextSize(12f);

        // Crear BarData
        BarData data = new BarData(dataSet);
        data.setBarWidth(0.9f);

        // Aplicar los datos al gráfico
        barChart.setData(data);
        barChart.setFitBars(true);

        // Fondo oscuro
        barChart.setBackgroundColor(Color.parseColor("#121212")); // gris oscuro o negro
        barChart.setDrawGridBackground(false); // quita fondo de cuadros si los tuviera

        // Descripción en blanco
        Description description = new Description();
        description.setText("Ventas 2025");
        description.setTextColor(Color.WHITE);
        barChart.setDescription(description);

        // Ejes en blanco
        barChart.getAxisLeft().setTextColor(Color.WHITE);
        barChart.getAxisRight().setTextColor(Color.WHITE);
        barChart.getXAxis().setTextColor(Color.WHITE);

        // Ejes: líneas blancas
        barChart.getAxisLeft().setAxisLineColor(Color.WHITE);
        barChart.getAxisRight().setAxisLineColor(Color.WHITE);
        barChart.getXAxis().setAxisLineColor(Color.WHITE);

        // Leyenda en blanco
        barChart.getLegend().setTextColor(Color.WHITE);
        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM); // Las etiquetas se muestran en la parte inferior
        xAxis.setGranularity(1f); // Para asegurarse de que cada barra tiene una etiqueta
        xAxis.setValueFormatter(new IndexAxisValueFormatter(MESES_CORTOS)); // Usar las primeras 3 letras de cada mes

        // Reducir el tamaño de las etiquetas si es necesario
        xAxis.setLabelRotationAngle(-45f); // Rotar ligeramente las etiquetas para que no se sobrepongan
        xAxis.setLabelCount(12);

        // Forzar redibujado
        barChart.invalidate();
    }

    private String capitalizar(String texto) {
        if (texto == null || texto.isEmpty()) return texto;
        return texto.substring(0, 1).toUpperCase() + texto.substring(1);
    }
    private void igualarAnchoColumnas(TableLayout cabecera, TableLayout contenido) {
        int columnas = cabecera.getChildCount() > 0 ?
                ((TableRow) cabecera.getChildAt(0)).getChildCount() : 0;

        for (int col = 0; col < columnas; col++) {
            int maxAncho = 0;

            // Obtener ancho máximo entre cabecera y contenido
            for (int i = 0; i < contenido.getChildCount(); i++) {
                TableRow fila = (TableRow) contenido.getChildAt(i);
                TextView celda = (TextView) fila.getChildAt(col);
                celda.measure(0, 0);
                maxAncho = Math.max(maxAncho, celda.getMeasuredWidth());
            }

            TableRow filaCabecera = (TableRow) cabecera.getChildAt(0);
            TextView celdaCabecera = (TextView) filaCabecera.getChildAt(col);
            celdaCabecera.measure(0, 0);
            maxAncho = Math.max(maxAncho, celdaCabecera.getMeasuredWidth());

            // Aplicar el ancho a todas las celdas de esa columna
            for (int i = 0; i < contenido.getChildCount(); i++) {
                TableRow fila = (TableRow) contenido.getChildAt(i);
                TextView celda = (TextView) fila.getChildAt(col);
                celda.setWidth(maxAncho);
            }

            celdaCabecera.setWidth(maxAncho);
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Infla el menú que has creado
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Verifica si el ítem seleccionado es "Añadir Comercial"
        if (item.getItemId() == R.id.item_agregar_comercial) {
            // Crear la Intent para abrir la actividad AgregarComercialActivity
            Intent intent = new Intent(MainActivity.this, AgregarComercialActivity.class);
            activityResultLauncher.launch(intent);
            return true;
        }
        if (item.getItemId() == R.id.id_boton_menu_eliminar_ventas) {
            // Crear la Intent para abrir la actividad AgregarComercialActivity
            Intent intent = new Intent(MainActivity.this, EliminarVentasComerciales.class);
            activityResultLauncher.launch(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
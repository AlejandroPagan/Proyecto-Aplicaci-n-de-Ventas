package com.example.aplicacinaselab02;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private BarChart barChart;
    private Button btnActualizar;
    private TextView totalVentas;
    private TableLayout table;

    // Lanzador de actividad para recibir resultados
    private ActivityResultLauncher<Intent> activityResultLauncher;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        barChart = findViewById(R.id.barChart);
        btnActualizar = findViewById(R.id.btnActualizar);
        totalVentas = findViewById(R.id.totalVentas);
        table = findViewById(R.id.tablero_main);

        mostrarGrafico();

        // Configurar lanzador para recibir resultados de la actividad AgregarVentasActivity
        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            ArrayList<String> ventas = data.getStringArrayListExtra("ventas");
                            // Actualizar la tabla con los nuevos datos
                            updateTable(ventas);
                        }
                    }
                }
        );

        // Configurar el botón para abrir la actividad AgregarVentasActivity
        btnActualizar.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AgregarVentasActivity.class);
            activityResultLauncher.launch(intent);
        });
    }

    // Método para agregar los datos de ventas a la tabla de la actividad principal
    private void updateTable(ArrayList<String> ventas) {
        for (String venta : ventas) {
            String[] datos = venta.split(",");
            String comercial = datos[0];
            int ventasMes = Integer.parseInt(datos[1]);

            // Aquí buscamos la fila correspondiente al comercial
            TableRow row = new TableRow(this);

            // Creación de los TextViews de cada columna
            TextView tvComercial = new TextView(this);
            tvComercial.setText(comercial);
            tvComercial.setPadding(6, 6, 6, 6);
            tvComercial.setGravity(Gravity.CENTER);
            tvComercial.setTextColor(getResources().getColor(android.R.color.black));

            TextView tvVentas = new TextView(this);
            tvVentas.setText(String.valueOf(ventasMes));
            tvVentas.setPadding(6, 6, 6, 6);
            tvVentas.setGravity(Gravity.CENTER);
            tvVentas.setTextColor(getResources().getColor(android.R.color.black));

            // Añadimos las celdas a la fila
            row.addView(tvComercial);
            row.addView(tvVentas);

            // Añadimos la fila a la tabla
            table.addView(row);
        }
    }

    // Método para agregar un comercial a la tabla de ventas
    private void agregarComercial(String codigo, String nombre, int enero, int febrero, int marzo, int s14, int s15) {
        TableRow row = new TableRow(this);

        row.addView(crearCelda(codigo));
        row.addView(crearCelda(nombre));
        row.addView(crearCelda(String.valueOf(enero)));
        row.addView(crearCelda(String.valueOf(febrero)));
        row.addView(crearCelda(String.valueOf(marzo)));
        row.addView(crearCelda(String.valueOf(s14)));
        row.addView(crearCelda(String.valueOf(s15)));

        table.addView(row);
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

    // Método para mostrar el gráfico de ventas
    private void mostrarGrafico() {
        Random random = new Random();
        List<BarEntry> entradas = new ArrayList<>();
        String[] meses = {"Ene", "Feb", "Mar", "Abr", "May","Jun","Jul","Ago","Sep","Oct","Nov","Dic"};
        int total = 0;

        for (int i = 0; i < meses.length; i++) {
            int ventas = 10 + random.nextInt(50);
            entradas.add(new BarEntry(i, ventas));
            total += ventas;
        }

        totalVentas.setText("Total ventas: " + total);

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
}



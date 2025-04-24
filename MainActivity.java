package com.example.aplicacinaselab02;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

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

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        barChart = findViewById(R.id.barChart);
        btnActualizar = findViewById(R.id.btnActualizar);
        totalVentas = findViewById(R.id.totalVentas);

        mostrarGrafico();

        btnActualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarGrafico();
            }
        });
    }
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

    private TextView crearCelda(String texto) {
        TextView cell = new TextView(this);
        cell.setText(texto);
        cell.setPadding(6, 6, 6, 6);
        cell.setGravity(Gravity.CENTER);
        cell.setTextColor(Color.BLACK);
        return cell;
    }
    private void mostrarGrafico() {
        Random random = new Random();
        List<BarEntry> entradas = new ArrayList<>();
        String[] meses = {"Ene", "Feb", "Mar", "Abr", "May"};
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

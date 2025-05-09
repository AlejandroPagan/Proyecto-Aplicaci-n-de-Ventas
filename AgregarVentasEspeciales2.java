package com.example.aplicacinaselab02;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

public class AgregarVentasEspeciales2 extends AppCompatActivity {

    private EditText etCantidadVentas;
    private Button btnGuardar, btnBorrar,btnMes;
    private TextView tvMesActual;
    private TableLayout tablaVentas;
    private RadioGroup rgTiposComercial;
    private gestorBaseDatos db;

    private String mesActual;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_ventas_especiales2);

        // Ya no usamos estos campos porque se seleccionan automáticamente
        rgTiposComercial = findViewById(R.id.rgTiposComercial);
        etCantidadVentas = findViewById(R.id.etCantidadVentas);
        btnGuardar = findViewById(R.id.btnGuardar);
        btnBorrar = findViewById(R.id.btnVolverVenta);
        btnMes = findViewById(R.id.btnMes);
        tvMesActual = findViewById(R.id.tvMesActual);
        tablaVentas = findViewById(R.id.tablaVentas);


        mesActual = getMonthKey(Calendar.getInstance().get(Calendar.MONTH));
        tvMesActual.setText("Mes actual: " + capitalizar(mesActual));

        db = new gestorBaseDatos(this);

        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardarVentas();
                setResult(RESULT_OK);

            }
        });

        btnBorrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_OK);
                finish();
            }
        });
        btnMes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String[] meses = {
                        "enero", "febrero", "marzo", "abril", "mayo", "junio",
                        "julio", "agosto", "septiembre", "octubre", "noviembre", "diciembre"
                };

                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(AgregarVentasEspeciales2.this);
                builder.setTitle("Selecciona un mes")
                        .setSingleChoiceItems(meses, -1, null)
                        .setPositiveButton("Aceptar", (dialog, whichButton) -> {
                            android.app.AlertDialog alertDialog = (android.app.AlertDialog) dialog;
                            int selectedPosition = alertDialog.getListView().getCheckedItemPosition();
                            if (selectedPosition >= 0) {
                                mesActual = meses[selectedPosition]; // Cambia el mes de guardado
                                tvMesActual.setText("Mes seleccionado: " + capitalizar(mesActual));
                            }
                        })
                        .setNegativeButton("Cancelar", null)
                        .show();
            }
        });
    }

    private void guardarVentas() {
        // Detectar selección del tipo de comercial
        int selectedId = rgTiposComercial.getCheckedRadioButtonId();
        int id = -1;
        String nombre = "";

        if (selectedId == R.id.rbTeleventa) {
            id = 1;
            nombre = "TV";
        } else if (selectedId == R.id.rbRenovacion) {
            id = 2;
            nombre = "RV";
        } else if (selectedId == R.id.rbPrivada) {
            id = 3;
            nombre = "PV"; // <- CORRECTO
        }
        else {
            Toast.makeText(this, "Selecciona un tipo de comercial", Toast.LENGTH_SHORT).show();
            return;
        }

        String cantidadStr = etCantidadVentas.getText().toString().trim();
        if (cantidadStr.isEmpty()) {
            Toast.makeText(this, "Por favor, introduce la cantidad de ventas", Toast.LENGTH_SHORT).show();
            return;
        }

        int cantidad = Integer.parseInt(cantidadStr);
        db.insertarOVentasAcumuladas(id, nombre, mesActual, cantidad);

        Toast.makeText(this, "Ventas guardadas correctamente", Toast.LENGTH_SHORT).show();

        // Limpiar el formulario:
        etCantidadVentas.setText("");
        rgTiposComercial.clearCheck();
        setResult(RESULT_OK);
    }


    private String getMonthKey(int month) {
        String[] keys = {"enero", "febrero", "marzo", "abril", "mayo", "junio",
                "julio", "agosto", "septiembre", "octubre", "noviembre", "diciembre"};
        return keys[month];
    }

    private String capitalizar(String texto) {
        if (texto == null || texto.isEmpty()) return texto;
        return texto.substring(0, 1).toUpperCase() + texto.substring(1);
    }
}
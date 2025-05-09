package com.example.aplicacinaselab02;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class AgregarVentasActivity extends AppCompatActivity {

    private EditText etIdComercial, etNombreComercial, etCantidadVentas;
    private Button btnGuardar,btnBorrar,btnMes;
    private TextView tvMesActual;
    private TableLayout tablaVentas;
    private gestorBaseDatos db;

    private String mesActual;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_ventas);

        etIdComercial = findViewById(R.id.etNumeroComercial);
        etNombreComercial = findViewById(R.id.etNombreComercial);
        etCantidadVentas = findViewById(R.id.etCantidadVentas);
        btnGuardar = findViewById(R.id.btnGuardar);
        btnBorrar=findViewById(R.id.btnVolverVenta);
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
            }
        });
        btnMes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String[] meses = {
                        "enero", "febrero", "marzo", "abril", "mayo", "junio",
                        "julio", "agosto", "septiembre", "octubre", "noviembre", "diciembre"
                };

                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(AgregarVentasActivity.this);
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

        btnBorrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_OK);
                finish(); // Vuelve a MainActivity, que puede reaccionar en onActivityResult o launcher
            }
        });
    }

    private void guardarVentas() {
        String idStr = etIdComercial.getText().toString().trim();
        String nombre = etNombreComercial.getText().toString().trim();
        String cantidadStr = etCantidadVentas.getText().toString().trim();

        if (idStr.isEmpty() || cantidadStr.isEmpty()) {
            Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        int id = Integer.parseInt(idStr);
        int cantidad = Integer.parseInt(cantidadStr);

        db.insertarOVentasAcumuladas(id, nombre, mesActual, cantidad);

        Toast.makeText(this, "Ventas guardadas correctamente", Toast.LENGTH_SHORT).show();

        // Volver a MainActivity
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

package com.example.aplicacinaselab02;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;


public class EliminarVentasComerciales extends AppCompatActivity {

    private EditText etIdComercial, etCantidadVentas;
    private Button btnEliminar, btnVolver, btnMes;
    private TextView tvMesActual;
    private TableLayout tablaVentas;
    private gestorBaseDatos db;

    private String mesActual;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_ventas);  // Asegúrate de que esta es la vista correcta

        etIdComercial = findViewById(R.id.etNumeroComercial);
        etCantidadVentas = findViewById(R.id.etCantidadVentas);
        btnEliminar = findViewById(R.id.btnEliminar); // ← Usa el ID del botón en XML
        btnVolver = findViewById(R.id.btnVolverVenta);
        btnMes = findViewById(R.id.btnMes);
        tvMesActual = findViewById(R.id.tvMesActual);
        tablaVentas = findViewById(R.id.tablaVentas);

        db = new gestorBaseDatos(this);

        mesActual = getMonthKey(Calendar.getInstance().get(Calendar.MONTH));
        tvMesActual.setText("Mes actual: " + capitalizar(mesActual));

        btnEliminar.setOnClickListener(v -> eliminarVentas());

        btnMes.setOnClickListener(v -> {
            final String[] meses = {
                    "enero", "febrero", "marzo", "abril", "mayo", "junio",
                    "julio", "agosto", "septiembre", "octubre", "noviembre", "diciembre"
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Selecciona un mes")
                    .setSingleChoiceItems(meses, -1, null)
                    .setPositiveButton("Aceptar", (dialog, whichButton) -> {
                        AlertDialog alertDialog = (AlertDialog) dialog;
                        int selectedPosition = alertDialog.getListView().getCheckedItemPosition();
                        if (selectedPosition >= 0) {
                            mesActual = meses[selectedPosition];
                            tvMesActual.setText("Mes seleccionado: " + capitalizar(mesActual));
                        }
                    })
                    .setNegativeButton("Cancelar", null)
                    .show();
        });

        btnVolver.setOnClickListener(v -> {
            setResult(RESULT_OK);
            finish();
        });
    }

    private void eliminarVentas() {
        String idStr = etIdComercial.getText().toString().trim();
        String cantidadStr = etCantidadVentas.getText().toString().trim();

        if (idStr.isEmpty() || cantidadStr.isEmpty()) {
            Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        int id = Integer.parseInt(idStr);
        int cantidadAEliminar = Integer.parseInt(cantidadStr);

        if (!db.existeComercial(id)) {
            Toast.makeText(this, "El comercial con ID " + id + " no existe.", Toast.LENGTH_SHORT).show();
            return;
        }

        int ventasActuales = db.obtenerVentasDeComercial(id, mesActual);

        if (ventasActuales < cantidadAEliminar) {
            Toast.makeText(this, "No hay suficientes ventas para eliminar", Toast.LENGTH_SHORT).show();
            return;
        }

        int nuevasVentas = ventasActuales - cantidadAEliminar;
        db.actualizarVentas(id, mesActual, nuevasVentas);

        Toast.makeText(this, "Ventas eliminadas correctamente", Toast.LENGTH_SHORT).show();
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

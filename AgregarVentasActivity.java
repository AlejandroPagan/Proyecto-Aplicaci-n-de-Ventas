package com.example.aplicacinaselab02;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class AgregarVentasActivity extends AppCompatActivity {

    private EditText etIdComercial, etCantidadVentas;
    private Button btnGuardar, btnBorrar, btnMes;
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
        etCantidadVentas = findViewById(R.id.etCantidadVentas);
        btnGuardar = findViewById(R.id.btnGuardar);
        btnBorrar = findViewById(R.id.btnVolverVenta);
        btnMes = findViewById(R.id.btnMes);
        tvMesActual = findViewById(R.id.tvMesActual);
        tablaVentas = findViewById(R.id.tablaVentas);

        mesActual = getMonthKey(Calendar.getInstance().get(Calendar.MONTH));
        tvMesActual.setText("Mes actual: " + capitalizar(mesActual));

        db = new gestorBaseDatos(this);

        btnGuardar.setOnClickListener(v -> guardarVentas());

        btnMes.setOnClickListener(v -> {
            final String[] meses = {
                    "enero", "febrero", "marzo", "abril", "mayo", "junio",
                    "julio", "agosto", "septiembre", "octubre", "noviembre", "diciembre"
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(AgregarVentasActivity.this);
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

        btnBorrar.setOnClickListener(v -> {
            setResult(RESULT_OK);
            finish();
        });
    }

    private void guardarVentas() {
        String idStr = etIdComercial.getText().toString().trim();
        String cantidadStr = etCantidadVentas.getText().toString().trim();

        if (idStr.isEmpty() || cantidadStr.isEmpty()) {
            Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        int id = Integer.parseInt(idStr);
        int cantidad = Integer.parseInt(cantidadStr);

        if (!db.existeComercial(id)) {
            mostrarDialogoCrearComercial(id);
            return;
        }

        db.insertarOVentasAcumuladas(id, null, mesActual, cantidad); // null en nombre
        Toast.makeText(this, "Ventas guardadas correctamente", Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
    }

    private void mostrarDialogoCrearComercial(int id) {
        new AlertDialog.Builder(this)
                .setTitle("Comercial no encontrado")
                .setMessage("El comercial con ID " + id + " no existe. ¿Deseas crearlo?")
                .setPositiveButton("Sí", (dialog, which) -> {
                    // Redirigir a una Activity para crear un nuevo comercial
                    // Aquí deberías crear AgregarComercialActivity si aún no existe
                    Intent intent = new Intent(this, AgregarComercialActivity.class);
                    intent.putExtra("id", id);
                    startActivity(intent);
                })
                .setNegativeButton("No", null)
                .show();
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

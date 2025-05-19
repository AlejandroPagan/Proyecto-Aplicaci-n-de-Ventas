package com.example.aplicacinaselab02;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class AgregarComercialActivity extends AppCompatActivity {

    private EditText etIdComercial, etNombreComercial;
    private Button btnGuardar, btnVolverVenta;
    private TextView tvMesActual;
    private gestorBaseDatos db;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_comercial);
        etNombreComercial= findViewById(R.id.etNombreComercial);
        etIdComercial = findViewById(R.id.etNumeroComercial);
        btnGuardar = findViewById(R.id.btnGuardar);
        btnVolverVenta = findViewById(R.id.btnVolverVenta);

        db = new gestorBaseDatos(this);

        btnGuardar.setOnClickListener(v -> guardarComercial());

        btnVolverVenta.setOnClickListener(v -> {
            setResult(RESULT_OK);
            finish();
        });
    }

    private void guardarComercial() {
        String nombreCom = etNombreComercial.getText().toString().trim();
        String idStr = etIdComercial.getText().toString().trim();

        if (idStr.isEmpty() || nombreCom.isEmpty()) {
            Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        int id;
        try {
            id = Integer.parseInt(idStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "ID debe ser un número entero", Toast.LENGTH_SHORT).show();
            return;
        }

        if (db.existeComercial(id)) {
            mostrarDialogoCrearComercial(id);
            return;
        }

        boolean exito = db.insertarNuevoComercial(id, nombreCom);
        if (exito) {
            Toast.makeText(this, "Comercial guardado correctamente", Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);
            finish();
        } else {
            Toast.makeText(this, "Error al guardar el comercial", Toast.LENGTH_SHORT).show();
        }
    }


    private void mostrarDialogoCrearComercial(int id) {
        new AlertDialog.Builder(this)
                .setTitle("Comercial ya existe")
                .setMessage("El comercial con ID " + id + " ya existe. Introduzca un ID que no esté en uso")

                .setNegativeButton("OK", null)
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
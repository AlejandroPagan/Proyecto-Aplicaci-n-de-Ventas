package com.example.aplicacinaselab02;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AgregarVentasActivity extends AppCompatActivity {

    private EditText etComercial, etVentas;
    private Button btnGuardar;
    private TableLayout tablaVentas;
    private TextView tvMesActual;

    // Aquí obtenemos el mes actual
    private String mesActual = getMonthString(Calendar.getInstance().get(Calendar.MONTH));
    private ActivityResultLauncher<Intent> activityResultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_ventas);

        etComercial = findViewById(R.id.etComercial);
        etVentas = findViewById(R.id.etVentas);
        btnGuardar = findViewById(R.id.btnGuardar);
        tablaVentas = findViewById(R.id.tablaVentas);
        tvMesActual = findViewById(R.id.tvMesActual);

        // Mostrar el mes actual
        tvMesActual.setText("Mes actual: " + mesActual);

        // Crear el lanzador para el resultado
        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        // Recibimos los datos de ventas desde MainActivity
                        Intent data = result.getData();
                        if (data != null) {
                            ArrayList<String> ventas = data.getStringArrayListExtra("ventas");
                            // Aquí puedes actualizar la tabla de ventas en MainActivity
                            updateTable(ventas);
                        }
                    }
                }
        );

        // Manejar el botón de guardar
        btnGuardar.setOnClickListener(v -> guardarVentas());
    }

    // Método para guardar las ventas y devolverlas a la actividad principal
    private void guardarVentas() {
        String comercial = etComercial.getText().toString().trim();
        String ventasStr = etVentas.getText().toString().trim();

        if (comercial.isEmpty() || ventasStr.isEmpty()) {
            Toast.makeText(this, "Por favor, ingrese todos los datos", Toast.LENGTH_SHORT).show();
            return;
        }

        int ventas = Integer.parseInt(ventasStr);

        // Crear un Intent para enviar los datos
        Intent resultIntent = new Intent();
        ArrayList<String> ventasList = new ArrayList<>();
        ventasList.add(comercial + "," + ventas);  // Formato: Comercial, ventas

        resultIntent.putStringArrayListExtra("ventas", ventasList);
        setResult(RESULT_OK, resultIntent);

        // Llamar a onActivityResult para enviar los datos
        activityResultLauncher.launch(resultIntent);
    }

    // Método para convertir el número del mes en su nombre correspondiente
    private String getMonthString(int month) {
        String[] meses = {"Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"};
        return meses[month];
    }

    // Actualizar la tabla de ventas con los nuevos datos
    private void updateTable(ArrayList<String> ventas) {
        for (String venta : ventas) {
            String[] datos = venta.split(",");
            String comercial = datos[0];
            int ventasMes = Integer.parseInt(datos[1]);

            // Aquí buscamos la fila correspondiente al comercial
            TableRow row = new TableRow(this);

            // Puedes crear los TextViews de cada columna
            TextView tvComercial = new TextView(this);
            tvComercial.setText(comercial);
            TextView tvVentas = new TextView(this);
            tvVentas.setText(String.valueOf(ventasMes));

            // Añadimos las celdas a la fila
            row.addView(tvComercial);
            row.addView(tvVentas);

            // Añadimos la fila a la tabla
            tablaVentas.addView(row);
        }
    }
}

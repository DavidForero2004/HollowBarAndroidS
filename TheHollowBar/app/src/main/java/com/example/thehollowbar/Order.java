package com.example.thehollowbar;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class Order extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        ImageButton buttonBack = findViewById(R.id.buttonBackOrder);
        TextView txtOrders = findViewById(R.id.txtOrders);
        LinearLayout containerOrderList = findViewById(R.id.containerOrderList);
        Button buttonCreateOrder = findViewById(R.id.buttonCreateOrder);

        // Lógica para volver atrás con confirmación
        buttonBack.setOnClickListener(v -> showExitConfirmationDialog());

        // Lógica para crear un nuevo pedido y navegar a AskForEmployee
        buttonCreateOrder.setOnClickListener(v -> openAskForEmployeeActivity());
    }

    private void openAskForEmployeeActivity() {
        Intent intent = new Intent(this, AskForEmployee.class);
        startActivity(intent);
    }

    private void showExitConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Cerrar Sesión")
                .setMessage("¿Estás seguro de que deseas cerrar sesión?")
                .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Regresa a la actividad de Home
                        Intent intent = new Intent(Order.this, Home.class); // Cambia "Home" por el nombre correcto de tu actividad de inicio
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish(); // Finaliza la actividad actual
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}

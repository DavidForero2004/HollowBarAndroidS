package com.example.thehollowbar;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class LoginEmployee extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_employee);

        ImageButton buttonBack = findViewById(R.id.buttonBackEmployee);
        Button buttonLogin = findViewById(R.id.button); // Asegúrate de que el ID es correcto

        buttonBack.setOnClickListener(v -> {
            onBackPressed();
        });

        // Lógica para ir a la actividad Order
        buttonLogin.setOnClickListener(v -> {
            Intent intent = new Intent(LoginEmployee.this, Order.class);
            startActivity(intent);
        });
    }
}

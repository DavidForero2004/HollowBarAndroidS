package com.example.thehollowbar;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.widget.TextView;
import android.widget.Button;



public class AskFor extends AppCompatActivity {

    private TextView productQuantity;
    private int quantity = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ask_for);

        productQuantity = findViewById(R.id.productQuantity);
        Button buttonAdd = findViewById(R.id.btnAddition);
        Button buttonSubtract = findViewById(R.id.btnSubtraction);

        buttonAdd.setOnClickListener(v -> add());
        buttonSubtract.setOnClickListener(v -> subtract());

    }

    private void add() {
        quantity++;
        updateQuantity();
    }

    private void subtract() {
        if (quantity > 1) {
            quantity--;
        }
        updateQuantity();
    }

    private void updateQuantity() {
        productQuantity.setText(String.valueOf(quantity));
    }
}
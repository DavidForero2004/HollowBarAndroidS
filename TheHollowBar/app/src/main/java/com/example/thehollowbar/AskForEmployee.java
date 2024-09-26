package com.example.thehollowbar;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class AskForEmployee extends AppCompatActivity {

    private final List<String> cartItems = new ArrayList<>();
    private TextView cartItemsTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ask_for_employee);

        ImageButton buttonBackAsk = findViewById(R.id.buttonBackOrder);
        Button buttonCreateOrder = findViewById(R.id.buttonCreateOrder);
        Button buttonAddToCart = findViewById(R.id.buttonAddToCart);
        Spinner productSpinner = findViewById(R.id.productSpinner);
        cartItemsTextView = findViewById(R.id.cartItemsTextView);

        // Configuración del Spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.product_list, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        productSpinner.setAdapter(adapter);

        // Lógica para volver a la actividad anterior
        buttonBackAsk.setOnClickListener(v -> finish());

        // Lógica para agregar producto al carrito
        buttonAddToCart.setOnClickListener(v -> {
            String selectedProduct = productSpinner.getSelectedItem().toString();
            addToCart(selectedProduct);
        });

        // Lógica para crear un nuevo pedido
        buttonCreateOrder.setOnClickListener(v -> Toast.makeText(this, "Pedido creado: " + cartItems.toString(), Toast.LENGTH_SHORT).show());
    }

    private void addToCart(String product) {
        cartItems.add(product);
        updateCartDisplay();
        Toast.makeText(this, product + " agregado al carrito", Toast.LENGTH_SHORT).show();
    }

    private void updateCartDisplay() {
        StringBuilder items = new StringBuilder("Productos en el carrito:\n");
        for (String item : cartItems) {
            items.append("- ").append(item).append("\n");
        }
        cartItemsTextView.setText(items.toString());
    }
}

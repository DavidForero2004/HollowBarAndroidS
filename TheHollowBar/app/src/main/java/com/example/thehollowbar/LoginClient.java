package com.example.thehollowbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.thehollowbar.Interface.OrderService;
import com.example.thehollowbar.models.Order;
import com.example.thehollowbar.network.ApiClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginClient extends AppCompatActivity {

    private OrderService orderService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_client);

        orderService = ApiClient.getClient().create(OrderService.class);

        ImageButton buttonBack = findViewById(R.id.buttonBackClient);
        TextView selectTypeDocument = findViewById(R.id.selectTypeDocument);
        TextView selectTable = findViewById(R.id.selectTable);
        EditText inputNumDocument = findViewById(R.id.inputNumDocument);
        Button buttonNext = findViewById(R.id.buttonNextClient);

        TextView errorTypeDocument = findViewById(R.id.errorTypeDocument);
        TextView errorTable = findViewById(R.id.errorTable);

        final String[] optionsTypeDocument = getResources().getStringArray(R.array.options_array_type_document);
        final String[] optionsTable = getResources().getStringArray(R.array.options_array_table);

        buttonBack.setOnClickListener(v -> onBackPressed());

        selectTypeDocument.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(LoginClient.this);
            builder.setTitle("Tipo de documento");
            builder.setItems(optionsTypeDocument, (dialog, which) -> {
                selectTypeDocument.setText(optionsTypeDocument[which]);
                errorTypeDocument.setVisibility(View.GONE);
            });
            builder.show();
        });

        selectTable.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(LoginClient.this);
            builder.setTitle("Mesa");
            builder.setItems(optionsTable, (dialog, which) -> {
                selectTable.setText(optionsTable[which]);
                errorTable.setVisibility(View.GONE);
            });
            builder.show();
        });

        inputNumDocument.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().isEmpty()) {
                    inputNumDocument.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        buttonNext.setOnClickListener(v -> {
            String documentType = selectTypeDocument.getText().toString();
            String table = selectTable.getText().toString();
            String numDocument = inputNumDocument.getText().toString();

            boolean isValid = true;

            if (documentType.equals("Selecciona una opción")) {
                errorTypeDocument.setText("Por favor, selecciona un tipo de documento.");
                errorTypeDocument.setVisibility(View.VISIBLE);
                isValid = false;
            }

            if (table.equals("Selecciona una opción")) {
                errorTable.setText("Por favor, selecciona una mesa.");
                errorTable.setVisibility(View.VISIBLE);
                isValid = false;
            }

            if (numDocument.isEmpty()) {
                inputNumDocument.setError("El número de documento es obligatorio.");
                isValid = false;
            }

            if (isValid) {
                int numDocumentInt = Integer.parseInt(numDocument);
                int id_table = Integer.parseInt(table);

                Order orderRq = new Order();
                orderRq.setType_document(documentType);
                orderRq.setNum_document(numDocumentInt);
                orderRq.setId_table(id_table);

                try {
                    Call<List<Order>> call = orderService.getOrderClient(orderRq);
                    call.enqueue(new Callback<List<Order>>() {
                        @Override
                        public void onResponse(Call<List<Order>> call, Response<List<Order>> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                Intent intent = new Intent(LoginClient.this, AskFor.class);
                                startActivity(intent);
                            } else {
                                Toast.makeText(LoginClient.this, "Error en la respuesta: " + response.message() + " Código: " + response.code(), Toast.LENGTH_LONG).show();
                                Log.e("API_RESPONSE_ERROR", "Error: " + response.message() + " Código: " + response.code());
                            }
                        }

                        @Override
                        public void onFailure(Call<List<Order>> call, Throwable t) {
                            Toast.makeText(LoginClient.this, "Error de conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
                            Log.e("API_ERROR", "Error: " + t.getMessage());
                        }
                    });
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
        });
    }
}

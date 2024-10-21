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

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.thehollowbar.Interface.OrderService;
import com.example.thehollowbar.models.Order;
import com.example.thehollowbar.models.OrderResponse;
import com.example.thehollowbar.network.ApiClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.google.gson.internal.LinkedTreeMap;

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

                Call<OrderResponse> call = orderService.getOrderClient(orderRq);
                call.enqueue(new Callback<OrderResponse>() {
                    @Override
                    public void onResponse(Call<OrderResponse> call, Response<OrderResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            List<Object> resultList = response.body().getResult();
                            if (!resultList.isEmpty() && resultList.get(0) instanceof List) {
                                List<?> innerList = (List<?>) resultList.get(0);
                                if (!innerList.isEmpty()) {
                                    Log.d("Debug", "Tipo del primer elemento en innerList: " + innerList.get(0).getClass().getName());

                                    List<Order> orderList = new ArrayList<>();
                                    for (Object item : innerList) {
                                        if (item instanceof LinkedTreeMap) {
                                            LinkedTreeMap<?, ?> map = (LinkedTreeMap<?, ?>) item;
                                            Order order = new Order();

                                            order.setId(((Double) map.get("id")).intValue());
                                            order.setType_document((String) map.get("type_document"));
                                            order.setNum_document(((Double) map.get("num_document")).intValue());
                                            order.setId_table(((Double) map.get("id_table")).intValue());
                                            order.setId_status(((Double) map.get("id_status")).intValue());
                                            order.setOrder_date((String) map.get("order_date"));

                                            orderList.add(order);
                                        }
                                    }

                                    if (!orderList.isEmpty()) {
                                        Order orderResponseQ = orderList.get(0);

                                        Log.d("Order Debug", "ID: " + orderResponseQ.getId() + ", Tipo de documento: " + orderResponseQ.getType_document());

                                        /* Intent intent = new Intent(LoginClient.this, AskFor.class);
                                           intent.putExtra("orderId", orderResponseQ.getId());
                                           startActivity(intent); */
                                    } else {
                                        Toast.makeText(LoginClient.this, "No se encontraron órdenes.", Toast.LENGTH_LONG).show();
                                    }
                                } else {
                                    Toast.makeText(LoginClient.this, "La lista interna está vacía.", Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Toast.makeText(LoginClient.this, "Formato de respuesta inesperado.", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(LoginClient.this, "Error en la respuesta: " + response.message(), Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<OrderResponse> call, Throwable t) {
                        Toast.makeText(LoginClient.this, "Fallo en la conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }
}

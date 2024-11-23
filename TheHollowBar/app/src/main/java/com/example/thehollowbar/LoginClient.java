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
import com.example.thehollowbar.Interface.TableService;
import com.example.thehollowbar.models.Order;
import com.example.thehollowbar.models.ResponseWeb;
import com.example.thehollowbar.models.Table;
import com.example.thehollowbar.network.ApiClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.google.gson.internal.LinkedTreeMap;

public class LoginClient extends AppCompatActivity {
    private OrderService orderService;  // Servicio para manejar las órdenes
    private TableService tableService;  // Servicio para manejar las mesas
    private int IdTable = -1;  // Variable para almacenar el ID de la mesa seleccionada

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_client);

        // Crear instancias de los servicios
        orderService = ApiClient.getClient().create(OrderService.class);
        tableService = ApiClient.getClient().create(TableService.class);

        // Referenciar las vistas (botones, textos, campos de entrada)
        ImageButton buttonBack = findViewById(R.id.buttonBackClient);
        TextView selectTypeDocument = findViewById(R.id.selectTypeDocument);
        TextView selectTable = findViewById(R.id.selectTable);
        EditText inputNumDocument = findViewById(R.id.inputNumDocument);
        Button buttonNext = findViewById(R.id.buttonNextClient);

        // Referenciar las vistas de errores
        TextView errorTypeDocument = findViewById(R.id.errorTypeDocument);
        TextView errorTable = findViewById(R.id.errorTable);

        // Obtener las opciones del tipo de documento desde los recursos
        final String[] optionsTypeDocument = getResources().getStringArray(R.array.options_array_type_document);

        // Configurar el botón de regreso para que cierre la actividad cuando se presione
        buttonBack.setOnClickListener(v -> onBackPressed());

        // Configurar la acción de selección del tipo de documento
        selectTypeDocument.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(LoginClient.this);
            builder.setTitle("Tipo de documento");
            builder.setItems(optionsTypeDocument, (dialog, which) -> {
                selectTypeDocument.setText(optionsTypeDocument[which]);  // Actualizar el texto con la opción seleccionada
                errorTypeDocument.setVisibility(View.GONE);  // Ocultar el mensaje de error
            });
            builder.show();
        });

        // Configurar la acción de selección de la mesa
        selectTable.setOnClickListener(v -> {
            // Llamada a la API para obtener la lista de mesas
            Call<ResponseWeb> call = tableService.getTables();
            call.enqueue(new Callback<ResponseWeb>() {
                @Override
                public void onResponse(Call<ResponseWeb> call, Response<ResponseWeb> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        List<Object> resultList = response.body().getResult();

                        // Verificar si la lista de mesas es válida
                        if (!resultList.isEmpty() && resultList.get(0) instanceof List) {
                            List<?> innerList = (List<?>) resultList.get(0);

                            if (!innerList.isEmpty()) {
                                // Procesar la lista de mesas y mostrarlas en un diálogo
                                List<Table> tableList = new ArrayList<>();
                                for (Object item : innerList) {
                                    if (item instanceof LinkedTreeMap) {
                                        LinkedTreeMap<?, ?> map = (LinkedTreeMap<?, ?>) item;
                                        Table table = new Table();
                                        table.setId(((Double) map.get("id")).intValue());
                                        table.setName((String) map.get("name_table"));
                                        table.setId_status(((Double) map.get("id_status")).intValue());
                                        tableList.add(table);
                                    }
                                }

                                if (!tableList.isEmpty()) {
                                    HashMap<String, Integer> tableMap = new HashMap<>();
                                    String[] optionsTable = new String[tableList.size()];

                                    // Convertir la lista de mesas a un array de nombres
                                    for (int i = 0; i < tableList.size(); i++) {
                                        Table table = tableList.get(i);
                                        optionsTable[i] = table.getName();
                                        tableMap.put(table.getName(), table.getId());  // Mapear nombre de mesa a ID
                                    }

                                    // Mostrar las mesas en un diálogo
                                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginClient.this);
                                    builder.setTitle("Selecciona una mesa");
                                    builder.setItems(optionsTable, (dialog, which) -> {
                                        String selectedTableName = optionsTable[which];
                                        selectTable.setText(selectedTableName);  // Actualizar el texto con la mesa seleccionada
                                        errorTable.setVisibility(View.GONE);  // Ocultar el mensaje de error
                                        IdTable = tableMap.get(selectedTableName);  // Guardar el ID de la mesa seleccionada
                                    });
                                    builder.show();
                                } else {
                                    Toast.makeText(LoginClient.this, "La lista de mesas está vacía.", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(LoginClient.this, "La lista interna está vacía.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Log.e("Error", "Formato inesperado en la respuesta.");
                            Toast.makeText(LoginClient.this, "Error en la respuesta del servidor.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.e("API Error", "Error en la respuesta: " + response.message());
                        Toast.makeText(LoginClient.this, "Error al conectarse al servidor.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseWeb> call, Throwable t) {
                    // Manejo de errores en la conexión con la API
                    Toast.makeText(LoginClient.this, "Fallo en la conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        });

        // Configurar el TextWatcher para el campo de número de documento para limpiar el error mientras se escribe
        inputNumDocument.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().isEmpty()) {
                    inputNumDocument.setError(null);  // Limpiar el error si el campo no está vacío
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Configurar la acción del botón "Siguiente" para validar la información
        buttonNext.setOnClickListener(v -> {
            String documentType = selectTypeDocument.getText().toString();
            String numDocument = inputNumDocument.getText().toString();

            boolean isValid = true;

            // Validaciones para los campos de tipo de documento, mesa y número de documento
            if (documentType.equals("Selecciona una opción")) {
                errorTypeDocument.setText("Por favor, selecciona un tipo de documento.");
                errorTypeDocument.setVisibility(View.VISIBLE);
                isValid = false;
            }

            if (IdTable == -1) {
                errorTable.setText("Por favor, selecciona una mesa.");
                errorTable.setVisibility(View.VISIBLE);
                isValid = false;
            }

            if (numDocument.isEmpty()) {
                inputNumDocument.setError("El número de documento es obligatorio.");
                isValid = false;
            }

            // Si todos los campos son válidos, enviar la solicitud
            if (isValid) {
                int numDocumentInt = Integer.parseInt(numDocument);

                // Crear el objeto de la orden con los datos validados
                Order orderRq = new Order();
                orderRq.setType_document(documentType);
                orderRq.setNum_document(numDocumentInt);
                orderRq.setId_table(IdTable);

                // Llamar al servicio para crear una nueva orden
                Call<ResponseWeb> call = orderService.getOrderClient(orderRq);
                call.enqueue(new Callback<ResponseWeb>() {
                    @Override
                    public void onResponse(Call<ResponseWeb> call, Response<ResponseWeb> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            List<Object> resultList = response.body().getResult();
                            if (!resultList.isEmpty() && resultList.get(0) instanceof List) {
                                List<?> innerList = (List<?>) resultList.get(0);
                                if (!innerList.isEmpty()) {
                                    // Procesar la respuesta de la API para crear la orden
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

                                    // Si se encontró una orden, pasar a la siguiente actividad
                                    if (!orderList.isEmpty()) {
                                        Order orderResponseQ = orderList.get(0);

                                        Log.d("Order Debug", "ID: " + orderResponseQ.getId() + ", Tipo de documento: " + orderResponseQ.getType_document());

                                        Intent intent = new Intent(LoginClient.this, AskFor.class);
                                        intent.putExtra("orderId", orderResponseQ.getId());  // Pasar el ID de la orden
                                        startActivity(intent);  // Iniciar la actividad AskFor
                                    } else {
                                        Toast.makeText(LoginClient.this, "No se pudo crear la orden.", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(LoginClient.this, "No se encontró ninguna respuesta válida.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        } else {
                            Toast.makeText(LoginClient.this, "Error al crear la orden.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseWeb> call, Throwable t) {
                        // Manejo de errores en la conexión con la API
                        Toast.makeText(LoginClient.this, "Error en la conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }
}

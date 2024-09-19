package com.example.thehollowbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class LoginClient extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_client);

        ImageButton buttonBack = findViewById(R.id.buttonBackClient);
        TextView selectTypeDocument = findViewById(R.id.selectTypeDocument);
        TextView selectTable = findViewById(R.id.selectTable);
        Button buttonNext = findViewById(R.id.buttonNextClient);

        final String[] optionsTypeDocument = getResources().getStringArray(R.array.options_array_type_document);
        final String[] optionsTable = getResources().getStringArray(R.array.options_array_table);

        buttonBack.setOnClickListener(v -> {
            onBackPressed();
        });

        selectTypeDocument.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(LoginClient.this);
            builder.setTitle("Tipo de documento");
            builder.setItems(optionsTypeDocument, (dialog, which) -> {
                selectTypeDocument.setText(optionsTypeDocument[which]);
            });
            builder.show();
        });

        selectTable.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(LoginClient.this);
            builder.setTitle("Mesa");
            builder.setItems(optionsTable, (dialog, which) -> {
                selectTable.setText(optionsTable[which]);
            });
            builder.show();
        });

        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginClient.this, AskFor.class);
                startActivity(intent);
            }
        });
    }
}
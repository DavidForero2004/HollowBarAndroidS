package com.example.thehollowbar;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.Toast;

public class Home extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Button buttonLogin = findViewById(R.id.buttonLogin);
        Button buttonAsk = findViewById(R.id.buttonAsk);

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(Home.this, v);
                popupMenu.getMenuInflater().inflate(R.menu.popup_menu,popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(item ->   {
                    if (item.getItemId() == R.id.option_login) {
                        Intent intent = new Intent(Home.this, LoginEmployee.class);
                        startActivity(intent);

                        return true;
                    }
                    return false;
                });
                popupMenu.show();
            }
        });

        buttonAsk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home.this, LoginClient.class);
                startActivity(intent);
            }
        });
    }
}

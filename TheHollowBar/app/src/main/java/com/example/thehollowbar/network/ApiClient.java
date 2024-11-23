package com.example.thehollowbar.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static Retrofit retrofit = null;

    public static Retrofit getClient() {
        if (retrofit == null) {
            // Validar si estamos en un emulador o dispositivo físico
            String baseUrl = "http://192.168.43.85:3000/";

            retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        return retrofit;
    }

    // Método para detectar si estamos en un emulador
    private static boolean isEmulator() {
        return android.os.Build.FINGERPRINT.contains("generic");
    }
}

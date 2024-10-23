package com.example.thehollowbar;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.thehollowbar.Interface.UserService;
import com.example.thehollowbar.models.Error;
import com.example.thehollowbar.models.Order;
import com.example.thehollowbar.models.OrderResponse;
import com.example.thehollowbar.models.User;
import com.example.thehollowbar.models.UserResponse;
import com.example.thehollowbar.network.ApiClient;
import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.BeginSignInResult;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.internal.LinkedTreeMap;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Converter;
import retrofit2.Response;

public class LoginEmployee extends AppCompatActivity {
    private static final String TAG = "InicioDeSesion";
    private SignInClient oneTapClient;
    private BeginSignInRequest signInRequest;
    private Button button;
    private static final int REQ_ONE_TAP = 2;
    private boolean showOneTapUI = true;
    private int intentosFailedLogin = 0;
    private static final int MAX_INTENTOS = 5;
    private static final long TIEMPO_BLOQUEO = 30 * 60 * 1000;
    private ActivityResultLauncher<IntentSenderRequest> activityResultLauncher;
    private UserService userService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_employee);

        userService = ApiClient.getClient().create(UserService.class);

        ImageButton buttonBack = findViewById(R.id.buttonBackEmployee);
        Button buttonLogin = findViewById(R.id.loginEmployee);
        EditText inputUser = findViewById(R.id.inputUser);
        EditText inputPassword = findViewById(R.id.inputPassword);

        buttonBack.setOnClickListener(v -> onBackPressed());

        inicializarVistas();
        configurarGoogleSignIn();
        configurarActivityResultLauncher();
        configurarBotonLogin();

        inputUser.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().isEmpty()) {
                    inputUser.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        inputPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().isEmpty()) {
                    inputPassword.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        buttonLogin.setOnClickListener(v -> {
            String user = inputUser.getText().toString();
            String password = inputPassword.getText().toString();

            boolean isValid = true;

            if (user.isEmpty()) {
                inputUser.setError("El correo electronico es obligatorio.");
                isValid = false;
            }

            if (password.isEmpty()) {
                inputPassword.setError("La contraseña es obligatoria.");
                isValid = false;
            }

            if(isValid) {
                User userRq = new User();
                userRq.setEmail(user);
                userRq.setPassword(password);

                Call<UserResponse> call = userService.loginUser(userRq);
                call.enqueue(new Callback<UserResponse>() {
                    @Override
                    public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            String token = response.body().getToken();

                            SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("token", token);
                            editor.apply();

                            Intent intent = new Intent(LoginEmployee.this, Dashboard.class);
                            startActivity(intent);
                            finish();
                        } else {
                            String errorMessage;

                            try {
                                Converter<ResponseBody, Error> converter =
                                        ApiClient.getClient().responseBodyConverter(Error.class, new Annotation[0]);
                                Error errorResponse = converter.convert(response.errorBody());

                                errorMessage = errorResponse.getMsg();

                            } catch (Exception e) {
                                e.printStackTrace();
                                errorMessage = "Error de análisis: " + e.getMessage();
                            }

                            Toast.makeText(LoginEmployee.this, errorMessage, Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<UserResponse> call, Throwable t) {
                        if (t instanceof IOException) {
                            Toast.makeText(LoginEmployee.this, "Error de conexión. Verifica tu conexión a Internet.", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(LoginEmployee.this, "Ocurrió un error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
    }

    private void inicializarVistas() {
        button = findViewById(R.id.loginGoogleEmployee);
        Log.d(TAG, "Vistas inicializadas");
    }

    private void configurarGoogleSignIn() {
        oneTapClient = Identity.getSignInClient(this);

        signInRequest = BeginSignInRequest.builder()
                .setPasswordRequestOptions(BeginSignInRequest.PasswordRequestOptions.builder()
                        .setSupported(true)
                        .build())
                .setGoogleIdTokenRequestOptions(BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                        .setSupported(true)
                        .setServerClientId(getString(R.string.web_client_id))
                        .setFilterByAuthorizedAccounts(false)
                        .build())
                .setAutoSelectEnabled(true)
                .build();
        Log.d(TAG, "Google Sign-In configurado");
    }

    private void configurarActivityResultLauncher() {
        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartIntentSenderForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            try {
                                SignInCredential credential = oneTapClient.getSignInCredentialFromIntent(result.getData());
                                String idToken = credential.getGoogleIdToken();
                                if (idToken != null) {
                                    EditText inputUser = findViewById(R.id.inputUser);

                                    String email = credential.getId();
                                    inputUser.setText(email);
                                    Log.d(TAG, "Google Sign-In exitoso para: " + email);
                                    Toast.makeText(getApplicationContext(), "Email: " + email, Toast.LENGTH_SHORT).show();
//                                    Intent intent = new Intent(LoginEmployee.this, Home.class);
//                                    startActivity(intent);
                                } else {
                                    Log.w(TAG, "No se pudo obtener ID Token");
                                }
                            } catch (ApiException e) {
                                Log.e(TAG, "Error al obtener credenciales: " + e.getStatusCode(), e);
                                manejarErrorLogin(e);
                            }
                        } else {
                            Log.w(TAG, "Google Sign-In cancelado");
                        }
                        button.setEnabled(true);
                    }
                });
        Log.d(TAG, "ActivityResultLauncher configurado");
    }

    private void configurarBotonLogin() {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Botón de inicio de sesión presionado");
                if (intentosFailedLogin >= MAX_INTENTOS) {
                    Toast.makeText(LoginEmployee.this, "Has alcanzado el límite de intentos. Por favor, espera antes de intentar de nuevo.", Toast.LENGTH_LONG).show();
                    return;
                }
                iniciarProcesoLogin();
            }
        });
    }

    private void iniciarProcesoLogin() {
        Log.d(TAG, "Iniciando proceso de login");
        button.setEnabled(false);
        oneTapClient.beginSignIn(signInRequest)
                .addOnSuccessListener(LoginEmployee.this, new OnSuccessListener<BeginSignInResult>() {
                    @Override
                    public void onSuccess(BeginSignInResult result) {
                        Log.d(TAG, "beginSignIn exitoso");
                        IntentSenderRequest intentSenderRequest =
                                new IntentSenderRequest.Builder(result.getPendingIntent().getIntentSender()).build();
                        activityResultLauncher.launch(intentSenderRequest);
                    }
                })

                .addOnFailureListener(LoginEmployee.this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "beginSignIn falló", e);
                        button.setEnabled(true);
                        manejarErrorLogin(e);
                    }
                });
    }

    private void manejarErrorLogin(Exception e) {
        intentosFailedLogin++;
        if (e instanceof ApiException) {

            ApiException apiException = (ApiException) e;
            if (apiException.getStatusCode() == CommonStatusCodes.CANCELED) {
                Log.d(TAG, "One-tap dialog was closed.");
                showOneTapUI = false;
            } else {
                Log.e(TAG, "Error de inicio de sesión: " + apiException.getStatusCode());
                Toast.makeText(LoginEmployee.this, "Error al iniciar sesión: " + getErrorMessage(apiException.getStatusCode()), Toast.LENGTH_SHORT).show();
            }
        } else {
            Log.e(TAG, "Error no específico: " + e.getMessage(), e);
            Toast.makeText(LoginEmployee.this, "Error inesperado: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        if (intentosFailedLogin >= MAX_INTENTOS) {
            Log.w(TAG, "Máximo de intentos alcanzado");
            Toast.makeText(LoginEmployee.this, "Has alcanzado el límite de intentos fallidos. Inténtalo de nuevo más tarde.", Toast.LENGTH_LONG).show();
            button.setEnabled(false);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    button.setEnabled(true);
                    intentosFailedLogin = 0;
                    Log.d(TAG, "Reinicio de intentos de login");
                }
            }, TIEMPO_BLOQUEO);
        }
        if (e instanceof ApiException) {
            ApiException apiException = (ApiException) e;
            Log.e(TAG, "Código de error detallado: " + apiException.getStatusCode());
            Log.e(TAG, "Mensaje de error detallado: " + apiException.getMessage());
        }
    }
    private String getErrorMessage(int statusCode) {
        switch (statusCode) {
            case CommonStatusCodes.API_NOT_CONNECTED:
                return "API no conectada";
            case CommonStatusCodes.DEVELOPER_ERROR:
                return "Error de desarrollador. Verifica la configuración.";
            case CommonStatusCodes.ERROR:
                return "Error desconocido";
            case CommonStatusCodes.INTERNAL_ERROR:
                return "Error interno de Google Play Services";
            case CommonStatusCodes.INVALID_ACCOUNT:
                return "Cuenta inválida";
            case CommonStatusCodes.SIGN_IN_REQUIRED:
                return "Se requiere iniciar sesión";
            default:
                return "Error " + statusCode;
        }
    }
}

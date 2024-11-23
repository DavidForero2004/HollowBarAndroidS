package com.example.thehollowbar;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.widget.Toast;

import com.example.thehollowbar.Interface.UserService;
import com.example.thehollowbar.models.Error;
import com.example.thehollowbar.models.ResponseWeb;
import com.example.thehollowbar.models.User;
import com.example.thehollowbar.models.UserResponse;
import com.example.thehollowbar.network.ApiClient;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.io.IOException;
import java.lang.annotation.Annotation;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Converter;
import retrofit2.Response;

public class LoginEmployee extends AppCompatActivity {
    private UserService userService;
    private FirebaseAuth firebaseAuth;
    private GoogleSignInClient googleSignInClient;
    int RC_SIGN_IN = 1;
    String TAG = "GoogleSignInLoginActivity";
    Button buttonLoginGoogle;
    String accountEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_employee);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);
        firebaseAuth = FirebaseAuth.getInstance();

        userService = ApiClient.getClient().create(UserService.class);

        ImageButton buttonBack = findViewById(R.id.buttonBackEmployee);
        Button buttonLogin = findViewById(R.id.loginEmployee);
        EditText inputUser = findViewById(R.id.inputUser);
        EditText inputPassword = findViewById(R.id.inputPassword);
        buttonLoginGoogle = findViewById(R.id.loginGoogleEmployee);

        buttonBack.setOnClickListener(v -> onBackPressed());

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

        buttonLoginGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });
    }

    private void signIn() {
        Intent signIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            if(task.isSuccessful()) {
                try {
                    GoogleSignInAccount account = task.getResult(ApiException.class);
                    Log.d(TAG, "firebaseAuthWithGoogle:" + account.getEmail());
                    accountEmail = account.getEmail();
                    firebaseAuthWithGoogle(account.getIdToken());
                } catch (ApiException e) {
                    Log.w(TAG, "Google sign in failed", e);
                }
            } else {
                Log.d(TAG,"Error, login no exitoso:" + task.getException().toString());
                Toast.makeText(this, "Ocurrio un error. " + task.getException().toString(), Toast.LENGTH_LONG).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = firebaseAuth.getCurrentUser();

                            if (user != null) {
                                User userRq = new User();
                                userRq.setEmail(accountEmail);

                                Call<UserResponse> call = userService.loginEmail(userRq);
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

                                            firebaseAuth.signOut();
                                            googleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Log.d(TAG, "Se cerro sesión correctamente");
                                                    } else {
                                                        Toast.makeText(getApplicationContext(), "No se pudo cerrar sesión con google", Toast.LENGTH_LONG).show();
                                                    }
                                                }
                                            });

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
                        } else {
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                        }
                    }
                });
    }

    @Override
    protected void onStart() {
        FirebaseUser user = firebaseAuth.getCurrentUser();

        if (user != null) {
            Intent intent = new Intent(LoginEmployee.this, Dashboard.class);
            startActivity(intent);
        }
        super.onStart();
    }
}

package com.example.atacador_firebasefinalact;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class Login extends AppCompatActivity {

    EditText email, password;
    Button login;
    TextView register;
    FirebaseAuth auth;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        login = findViewById(R.id.loginBtn);
        register = findViewById(R.id.register);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userLogin();

            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Login.this, Register.class));
            }
        });

        progressDialog = new ProgressDialog(this);

        auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            startActivity(new Intent(Login.this, MainAct.class));
            finish();
        }
    }
    private void userLogin() {
        String EMAIL = email.getText().toString().trim();
        String PASS = password.getText().toString().trim();

        if (EMAIL.isEmpty()) {
            email.setError("Email is required!");
            email.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(EMAIL).matches()) {
            email.setError("Please provide valid Email!");
            email.requestFocus();
            return;
        }
        if (PASS.isEmpty()) {
            password.setError("Password is required!");
            password.requestFocus();
            return;
        }

        progressDialog.setMessage("Logging in...Please Wait");
        progressDialog.show();

        auth.signInWithEmailAndPassword(EMAIL, PASS)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            startActivity(new Intent(Login.this, MainAct.class));
                            finish();
                            progressDialog.dismiss();

                        } else {
                            try {
                                throw task.getException();
                            }
                            catch (FirebaseAuthInvalidUserException invalidEmail) {
                                email.setError("Email is not registered");
                                email.requestFocus();
                                progressDialog.dismiss();
                            }
                            catch (FirebaseAuthInvalidCredentialsException wrongPassword) {
                                password.setError("Incorrect Password");
                                password.requestFocus();
                                progressDialog.dismiss();
                            } catch (Exception e) {
                                Toast.makeText(Login.this, "Login Failed!", Toast.LENGTH_LONG).show();
                                progressDialog.dismiss();
                            }
                        }
                    }
                });
    }
}
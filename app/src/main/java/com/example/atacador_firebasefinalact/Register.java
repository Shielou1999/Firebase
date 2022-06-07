package com.example.atacador_firebasefinalact;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
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
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.FirebaseDatabase;

public class Register extends AppCompatActivity {

    FirebaseAuth auth;
    TextView login;
    Button register;
    EditText name, emailAdd, pass, confirmPassword;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        auth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);

        login = findViewById(R.id.regLogin);
        register = findViewById(R.id.registerBtn);
        name = findViewById(R.id.username);
        emailAdd = findViewById(R.id.regEmail);
        pass = findViewById(R.id.regPassword);
        confirmPassword = findViewById(R.id.confirmPassword);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submit();
            }
        });
    }

    private void submit() {
        String NAME = name.getText().toString();
        String EMAIL = emailAdd.getText().toString();
        String PASS = pass.getText().toString();
        String CONFIRMPASS = confirmPassword.getText().toString();

        if (EMAIL.isEmpty()) {
            emailAdd.setError("Email is required!");
            emailAdd.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(EMAIL).matches()) {
            emailAdd.setError("Please provide valid Email!");
            emailAdd.requestFocus();
            return;
        }
        if (PASS.isEmpty()) {
            pass.setError("Password is required!");
            pass.requestFocus();
            return;
        }
        if (CONFIRMPASS.isEmpty()) {
            confirmPassword.setError("Confirm your password!");
            confirmPassword.requestFocus();
            return;
        }
        if (!CONFIRMPASS.matches(PASS)) {
            confirmPassword.setError("Password doesn't match!");
            confirmPassword.requestFocus();
            return;
        }

        progressDialog.setMessage("Registering...Please Wait");
        progressDialog.show();


        auth.createUserWithEmailAndPassword(EMAIL, PASS)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            User user = new User(EMAIL, NAME);

                            FirebaseDatabase.getInstance().getReference("users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                progressDialog.dismiss();
                                                Toast.makeText(Register.this, "User successfully registered", Toast.LENGTH_LONG).show();
                                                finish();
                                            } else {
                                                progressDialog.dismiss();
                                                Toast.makeText(Register.this, "Registration Failed", Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                        } else {
                            try {
                                throw task.getException();
                            } catch (FirebaseAuthUserCollisionException existEmail) {
                                emailAdd.setError("Email already exists");
                                emailAdd.requestFocus();
                                progressDialog.dismiss();
                            } catch (Exception e) {
                                progressDialog.dismiss();
                                Toast.makeText(Register.this, "Failed to register! Try Again!", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });
    }
}
package com.example.test;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignIn extends AppCompatActivity {

    private EditText emailEditText, passwordEditText;
    private Button signInButton, signUpButton;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_in);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Initialize UI elements
        emailEditText = findViewById(R.id.emailText);
        passwordEditText = findViewById(R.id.passwordText);
        signInButton = findViewById(R.id.signInButton);
        signUpButton = findViewById(R.id.signUpButton);

        // Set input type for password
        passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        // Sign-In button click listener
        signInButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(SignIn.this, "Please enter email and password", Toast.LENGTH_SHORT).show();
            } else {
                signInUser(email, password);
            }
        });

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onIconClick(v);
            }
        });

        // Sign-Up button click listener
        signUpButton.setOnClickListener(v -> {
            Intent intent = new Intent(SignIn.this, SignUp.class);
            startActivity(intent);
        });
    }

    private void signInUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Get the current user ID
                    String userId = mAuth.getCurrentUser().getUid();
                    DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(userId).child("role");

                    userRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            if (task.isSuccessful()) {
                                if (task.getResult().exists()) {
                                    String userRole = task.getResult().getValue(String.class);

                                    if (userRole != null) {
                                        if (userRole.equals("Admin")) {
                                            Intent intent = new Intent(SignIn.this, AdminHomePage.class);
                                            startActivity(intent);
                                            finish();
                                        } else if (userRole.equals("User")) {
                                            Intent intent = new Intent(SignIn.this, UserHomePage.class);
                                            startActivity(intent);
                                            finish();
                                        } else {
                                            Toast.makeText(SignIn.this, "No valid role found", Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        Toast.makeText(SignIn.this, "Role is not set for this user", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(SignIn.this, "Role does not exist", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(SignIn.this, "Failed to retrieve role", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });


                } else {
                    Toast.makeText(SignIn.this, "Login Failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void onIconClick(View view) {
        // 创建 Intent 以启动 LoginActivity
        Intent intent = new Intent(SignIn.this, SignUp.class);
        startActivity(intent);

    }
}

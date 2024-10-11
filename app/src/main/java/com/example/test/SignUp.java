package com.example.test;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUp extends AppCompatActivity {

    private EditText nameEditText, emailEditText, passwordEditText;

    private TextView emailErrorText, passwordErrorText;

    private Button createAccountButton, signInButton;

    private FirebaseAuth mAuth;

    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up);
        mAuth = FirebaseAuth.getInstance();

        createAccountButton = findViewById(R.id.CreateAccButton);
        signInButton = findViewById(R.id.SignInButton);
        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onIconClick(v);
            }
        });

    }

    private void register() {
        nameEditText = findViewById(R.id.NameEditText);
        emailEditText = findViewById(R.id.EmailEditText);
        passwordEditText = findViewById(R.id.PasswordEditText);

        emailErrorText = findViewById(R.id.EmailErrorText);
        passwordErrorText = findViewById(R.id.PasswordErrorText);

        String name = nameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            if (email.isEmpty()) {
                emailErrorText.setVisibility(View.VISIBLE);
            } else {
                emailErrorText.setVisibility(View.GONE);
            }

            if (password.isEmpty()) {
                passwordErrorText.setVisibility(View.VISIBLE);
            } else {
                passwordErrorText.setVisibility(View.GONE);
            }
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser mUser = mAuth.getCurrentUser();
                            if (mUser != null) {
                                DatabaseReference userRef = FirebaseDatabase.getInstance("https://librarymanagementsystemai-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference();
                                String userId = mUser.getUid();

                                // Set individual fields for the user
                                userRef.child("Users").child(userId).child("name").setValue(name);
                                userRef.child("Users").child(userId).child("email").setValue(email);
                                userRef.child("Users").child(userId).child("role").setValue("User")
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(SignUp.this, "User registered successfully", Toast.LENGTH_SHORT).show();
                                                    startActivity(new Intent(SignUp.this, SignIn.class));
                                                } else {
                                                    Toast.makeText(SignUp.this, "Registration Failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            }
                        } else {
                            Toast.makeText(SignUp.this, "Registration Failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    private void onIconClick(View view) {
        // 创建 Intent 以启动 LoginActivity
        Intent intent = new Intent(SignUp.this, SignIn.class);
        startActivity(intent);

    }
}


package com.airjob.chatappfinal_05_12.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.airjob.chatappfinal_05_12.MainActivity;
import com.airjob.chatappfinal_05_12.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class StartActivity extends AppCompatActivity {

    // Var des widgets
    private Button login, register;

    // Var Firebase
    private FirebaseUser currentUser;
    private FirebaseFirestore db;

    // Initialisation des widgets
    private void init(){
        login = findViewById(R.id.login);
        register = findViewById(R.id.register);
    }

    // Initialisation de FirebaseUser
    private void initFirebase(){
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();
    }

    // Gestion des clics sur les boutons
    private void btnLogin() {
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(StartActivity.this, LoginActivity.class));
            }
        });
    }

    private void btnRegister() {
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(StartActivity.this, RegisterActivity.class));
            }
        });
    }

    // Cycles de vie de l'app
    @Override
    protected void onStart() {
        super.onStart();
        if (currentUser != null){
            startActivity(new Intent(StartActivity.this, MainActivity.class));
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        // Initialisation des widgets
        init();
        // Initialisation de Firebase
        initFirebase();

        // Appel des méthodes pour la gestion des clics sur les boutons
        btnLogin();
        btnRegister();
    }
}
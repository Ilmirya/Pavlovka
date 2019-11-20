package com.example.pavlovka;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class SigninActivity extends AppCompatActivity {

    public EditText etPassword, etLogin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        etLogin = findViewById(R.id.etLogin);
        etPassword = findViewById(R.id.etPassword);

    }
    public void onClickEntry(View view){

    }
}

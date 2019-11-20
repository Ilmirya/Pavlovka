package com.example.pavlovka;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class SigninActivity extends AppCompatActivity {

    public EditText etPassword, etLogin;
    private TextView tvMessageSignin;
    public String sessionId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        etLogin = findViewById(R.id.etLogin);
        etPassword = findViewById(R.id.etPassword);
        tvMessageSignin = findViewById(R.id.tvMessageSignin);

    }
    public String login1, pass1l;
    public void onClickEntry(View view){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String login = etLogin.getText().toString(), password = etPassword.getText().toString();
                login1 = login;
                pass1l = password;
                if(login == "")
                    tvMessageSignin.setText("введите логин");
                else if(password == "")
                    tvMessageSignin.setText("введите пароль");
                else
                    sessionId = ApiQuery.Instance().AuthByLogin(SigninActivity.this, login, password);
                if(sessionId == null || sessionId == ""){
                    tvMessageSignin.setText("неверный логин или пароль");
                }
                else{
                    finish();
                }
            }
        }).start();
    }
}

package com.example.pavlovka;

import androidx.appcompat.app.AppCompatActivity;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;

public class SigninActivity extends AppCompatActivity {

    public EditText etPassword, etLogin;
    private TextView tvMessageSignin;
    public String sessionId;

    Handler handler = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        etLogin = findViewById(R.id.etLogin);
        etPassword = findViewById(R.id.etPassword);
        tvMessageSignin = findViewById(R.id.tvMessageSignin);
    }
    public void onClickEntry(View view){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String login = etLogin.getText().toString(), password = etPassword.getText().toString();
                if(login.equals(""))
                {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            tvMessageSignin.setText("введите логин");
                        }
                    });
                }
                else if(password.equals("")) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            tvMessageSignin.setText("введите пароль");
                        }
                    });
                }
                else {
                    sessionId = ApiQuery.Instance().AuthByLogin(SigninActivity.this, login, password);
                    if(sessionId == null || sessionId == ""){
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                tvMessageSignin.setText("неверный логин или пароль");
                            }
                        });
                    }
                    else{
                        try {
                            Util.setPropertyConfig("login", login, SigninActivity.this);
                            Util.setPropertyConfig("password", password, SigninActivity.this);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Intent answerIntent = new Intent();
                        answerIntent.putExtra("isLoginAndPassword", true);
                        setResult(Const.Session,answerIntent);
                        finish();
                    }
                }
            }
        }).start();
    }
}

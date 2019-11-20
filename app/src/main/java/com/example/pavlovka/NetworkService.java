package com.example.pavlovka;

import java.lang.reflect.Type;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NetworkService {
    private static NetworkService instance;



    private Retrofit retrofit;

    public static NetworkService Instance() {
        if (instance == null) {
            instance = new NetworkService();
        }
        return instance;
    }

    public NetworkService() {
        retrofit = new Retrofit.Builder()
                .baseUrl(Const.MX_URL)
                //.baseUrl(MX_URL_local)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }
    public mxApi getMxApi() {
        return retrofit.create(mxApi.class);
    }
}

package com.example.pavlovka;

import android.content.Context;

import com.example.pavlovka.POJO.Auth.AuthBySession.AuthBySession;
import com.example.pavlovka.POJO.GetSessionidd.BodyFromSession;
import com.example.pavlovka.POJO.GetSessionidd.SessionJson;
import com.example.pavlovka.POJO.Message;
import com.example.pavlovka.POJO.Poll;
import com.example.pavlovka.POJO.QueryFromDatabase.QueryDB;
import com.example.pavlovka.POJO.QueryFromDatabase.RecordsFromQueryDB;
import com.example.pavlovka.POJO.SignalBind;
import com.google.gson.Gson;

import java.io.IOException;
import java.lang.String;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Response;

public class ApiQuery {

    private static ApiQuery instance = new ApiQuery();

    public static ApiQuery Instance() {
        return instance;
    }
    public Gson gson = new Gson();
    public String gSessionId = "";

    private Message Message(String what, Object body){
        Message message = new Message();
        message.setHead(what, gSessionId);
        message.setBody(body);
        return message;
    }
    public String AuthByLogin(Context context){
        SessionJson sessionJson = new SessionJson();
        sessionJson.setWhat("auth-by-login");
        sessionJson.setLoginPassword("admin", "7472ee515fd6610cf741dccee9abef5a");

        String json = gson.toJson(sessionJson);
        Call<SessionJson> call = NetworkService.Instance().getMxApi().getSessionId(json);
        BodyFromSession post = new BodyFromSession();
        try {
            Response<SessionJson> response = call.execute();
            if(!response.isSuccessful()){
                return "";
            }
            post = response.body().getBody();
        } catch (IOException e) {
            Util.logsError(e.getMessage(),context);
        }
        gSessionId = post.getSessionId();
        try {
            Util.setPropertyConfig("sessionId", gSessionId, context);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return post.getSessionId();
    }

    /*
    public String AuthByLogin(Context context){
        GetSessionId getSessionId = new GetSessionId();
        getSessionId.setLoginPassword("admin", "7472ee515fd6610cf741dccee9abef5a");

        String json = gson.toJson(Message("auth-by-login", getSessionId));
        Call<Message> call = NetworkService.Instance().getMxApi().message(json);
        GetSessionId post = new GetSessionId();
        try {
            Response<Message> response = call.execute();
            if(!response.isSuccessful()){
                return "";
            }
            String json1 = gson.toJson(response.body().getBody());
            post = gson.fromJson(json1, GetSessionId.class) ;
        } catch (IOException e) {
            e.printStackTrace();
        }
        gSessionId = post.getSessionId();
        try {
            Util.setPropertyConfig("sessionId", gSessionId, context);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return post.getSessionId();
    }
*/
    public void SignalBind(String connectionId){
        SignalBind signalBind = new SignalBind();
        signalBind.setConnectionId(connectionId);
        String json = gson.toJson(Message("signal-bind",signalBind));
        try {
            NetworkService.Instance().getMxApi().message(json).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Message MessageExecute (String what, Object body){
        Message message = new Message();
        message.setHead(what, gSessionId);
        message.setBody(body);
        String json = gson.toJson(message);
        Message answerMessage = new Message();
        Call<Message> call = NetworkService.Instance().getMxApi().message(json);
        try {
            Response<Message> response = call.execute();
            if(!response.isSuccessful()){
                return null;
            }
            answerMessage = response.body();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return answerMessage;
    }

    public Boolean Poll(String objectId, String cmd, String components){
        Poll poll = new Poll();
        poll.setPoll1(new String[]{objectId}, cmd, components);
        Message answer = MessageExecute("poll", poll);
        if(answer == null) return false;
        String what = answer.getHead().getWhat();
        if(what.equals("poll-accepted")){
            try {
                Thread.sleep(1000 * 3);
                return true;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
    public Boolean Poll1(String objectId, String cmd, String components){
        Poll poll = new Poll();
        poll.setPoll1(new String[]{objectId}, cmd, components);
        String json = gson.toJson(Message("poll", poll));
        Call<Message> call = NetworkService.Instance().getMxApi().message(json);
        try {
            Response<Message> response = call.execute();
            if(!response.isSuccessful()){
                return false;
            }
            String what = response.body().getHead().getWhat();
            if(what.equals("poll-accepted")){
                try {
                    Thread.sleep(1000 * 3);
                    return true;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public String isGetSession(Context context) {
        try {
            gSessionId = Util.getProperty("sessionId", context);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(gSessionId == null || gSessionId.equals("")) return AuthByLogin(context);
        if(AuthBySession(context)){
            return gSessionId;
        }
        return AuthByLogin(context);
    }

    public boolean AuthBySession(Context context)
    {
        if (gSessionId == null || gSessionId.equals("")) return false;
        AuthBySession authBySession = new AuthBySession();
        authBySession.setSessionId(gSessionId);

        String tmp = gson.toJson(Message("auth-by-session", authBySession));
        Call<Message> call = NetworkService.Instance().getMxApi().message(tmp);
        try {
            Response<Message> response = call.execute();
            if(!response.isSuccessful()){
                return false;
            }
            if (response.body().getHead().getWhat().equals("auth-success"))
            {

                String json1 = gson.toJson(response.body().getBody());
                AuthBySession authBySession1 = gson.fromJson(json1, AuthBySession.class) ;
                gSessionId = authBySession1.getSessionId();
                try {
                    Util.setPropertyConfig("sessionId", gSessionId, context);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return true;
            }
        } catch (IOException e) {
            Util.logsError(e.getMessage(), context);
        }
        return false;
    }

    public RecordsFromQueryDB[] QueryFromDatabase(){
        Calendar calStart = Calendar.getInstance();
        calStart.add(Calendar.MINUTE,-25);
        Date dtStart = calStart.getTime();
        Calendar calendarNow = Calendar.getInstance();
        calendarNow.add(Calendar.MINUTE, 20);
        Date dtNow = calendarNow.getTime();

        QueryDB queryDB = new QueryDB();
        queryDB.setQueryDB(new String[]{Const.objectIdUpp}, dtStart, dtNow,"Current");


        String json = gson.toJson(Message("records-get1", queryDB));
        Call<Message> call = NetworkService.Instance().getMxApi().message(json);
        try {
            Response<Message> response = call.execute();
            if(!response.isSuccessful()){
                return null;
            }

            String json1 = gson.toJson(response.body().getBody());
            QueryDB queryDB1 = gson.fromJson(json1, QueryDB.class) ;
            return queryDB1.getRecords();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}

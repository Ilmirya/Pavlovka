package com.example.pavlovka;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;

import com.example.pavlovka.POJO.BodyForSignalR.ListUpdateBody;
import com.example.pavlovka.POJO.Message;
import com.example.pavlovka.POJO.QueryFromDatabase.RecordsFromQueryDB;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import microsoft.aspnet.signalr.client.Connection;
import microsoft.aspnet.signalr.client.ConnectionState;
import microsoft.aspnet.signalr.client.ErrorCallback;
import microsoft.aspnet.signalr.client.MessageReceivedHandler;
import microsoft.aspnet.signalr.client.Platform;
import microsoft.aspnet.signalr.client.http.android.AndroidPlatformComponent;

public class MyService extends Service {
    public Gson gson = new Gson();
    NotificationManager notificationManager;
    private String gSessionId = "";
    public PendingIntent pendingIntent;
    ExecutorService es;
    Timer timer = new Timer();
    Timer timer1 = new Timer();
    static boolean isActivity = false;
    public boolean isEnterInFunc  =false;
    public Connection  connection = null;
    public int countVisit = 0;
    private boolean isPingOk = false;
    private Date dtUppStop;

    private NumberFormat formatDouble = new DecimalFormat("#00.00");
    private double gHeight = -1;
    private int gWls = -1;
    private boolean gIsMotorStart = false;
    private boolean gIsNotConnected = true;
    private boolean gIsAdmin = false;

    private int lastSendNotifId = -1;

    public MyService() {
    }
    public void onCreate() {
        super.onCreate();
        Platform.loadPlatformComponent(new AndroidPlatformComponent());
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        es = Executors.newFixedThreadPool(1);
    }
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
    public int onStartCommand(Intent intent, int flags, int startId) {
        pendingIntent = intent.getParcelableExtra("pendingIntent");
        if(pendingIntent != null){
            MyRun mr = new MyRun();
            es.execute(mr);
            startForeground();
        }
        return super.onStartCommand(intent, flags, startId);
    }
    private void startForeground() {
        Intent notificationIntent = new Intent(this, MainActivity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        Notification notification = new NotificationCompat.Builder(this, "ChannelId") // don't forget create a notification channel first
                .setSmallIcon(R.mipmap.ic_launcher)
                .setOngoing(true)
                .setContentTitle(getString(R.string.app_name))
                .setContentText("Service is running background")
                .setContentIntent(pendingIntent)
                .build();

        startForeground(1, notification);
    }
    class MyRun implements Runnable {
        public void run() {
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    gSessionId = ApiQuery.Instance().isGetSession(MyService.this);
                    if(gSessionId == null || gSessionId == ""){
                        if(gIsNotConnected){
                            Intent intent = new Intent().putExtra("tvHeightWaters", Const.notConnection);
                            try {
                                pendingIntent.send(MyService.this,Const.Error,intent);
                            } catch (PendingIntent.CanceledException e) {
                                e.printStackTrace();
                            }
                            sendNotif(Const.notConnection, "", Const.notifNotConnecion);
                        }
                        gIsNotConnected = true;
                    }else{
                        if ((connection == null) || (connection.getState() != ConnectionState.Connected)||(!isPingOk))
                        {
                            Subscribe();
                        }
                        isPingOk = false;
                        countVisit = 0;
                        gIsNotConnected = false;
                        MainFunction();
                    }
                }
            },0, 1000 * 60 * 5);

            timer1.schedule(new TimerTask() {
                @Override
                public void run() {
                    isEnterInFunc = false;
                }
            },1000, 1000 * 40);
        }
    }
    void sendNotif(String contentText, String contentInfo, int notfiId) {
        if(lastSendNotifId == notfiId) return;
        lastSendNotifId = notfiId;
        Util.logsError(contentText,this);
        if(isActivity) return;
        String CHANNEL_ID = "channel_01";
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            CharSequence name = "channel";
            String Description = "This is my channel";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            mChannel.setDescription(Description);
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.RED);
            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});

            mChannel.setShowBadge(false);
            notificationManager.createNotificationChannel(mChannel);
        }

        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(getString(R.string.app_name))
                .setSound(uri)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setVibrate(new long[]{1000, 1000, 1000, 1000})
                .setContentText(contentText);

        Intent resultIntent = new Intent(this, MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setContentIntent(resultPendingIntent);

        notificationManager.notify(2, builder.build());
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        Intent intent = new Intent().putExtra("tvHeightWaters", Const.notConnection);
        try {
            pendingIntent.send(MyService.this, Const.ClosedService, intent);
        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
        }
        timer.cancel();
        timer1.cancel();
        stopSelf();
    }
    void IsActivity(boolean isact){
        isActivity = isact;
    }

    public void Subscribe()
    {
        try{
            if (connection == null)
            {
                connection = new Connection(Const.MX_URL + "/messageacceptor");

                connection.reconnected(new Runnable() {
                    @Override
                    public void run() {
                        ApiQuery.Instance().SignalBind(connection.getConnectionId(),MyService.this);
                    }
                });

                connection.closed(new Runnable() {
                    @Override
                    public void run() {
                        connection.stop();
                    }
                });
                connection.error(new ErrorCallback() {
                    @Override
                    public void onError(Throwable throwable) {
                        connection.stop();
                    }
                });
                connection.received(new MessageReceivedHandler() {
                    @Override
                    public void onMessageReceived(final JsonElement jsonElement) {
                        ReceivedSignalR(jsonElement);
                    }
                });
            }
            TryToConnect();
        }
        catch (Exception ex){
            Util.logsError(ex.getMessage(),this);
        }
    }
    public void ReceivedSignalR(JsonElement e)
    {
        Message message = gson.fromJson(e, Message.class);;
        Object body = message.getBody();
        String what = message.getHead().getWhat();
        if(what.equals("ping")){
            isPingOk = true;
        }
        else if(what.equals("ListUpdate")){
            String tmp1 = gson.toJson(body);
            ListUpdateBody listUpdateBody = gson.fromJson(tmp1, ListUpdateBody.class) ;
            String[] ids = listUpdateBody.getIds();
            for(String id : ids){
                if(id.equals(Const.objectIdUpp)){
                    try {
                        Thread.sleep(1000 * 3);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                    MainFunction();
                }
            }
        }
    }
    private void TryToConnect()
    {
        try{
            if (connection.getState() == ConnectionState.Connected)
            {
                connection.stop();
            }
            else
            {
                connection.connected(new Runnable() {
                    @Override
                    public void run() {
                        ApiQuery.Instance().SignalBind(connection.getConnectionId(), MyService.this);
                    }
                });
                connection.start();
            }
        }
        catch (Exception ex){
            Util.logsError(ex.getMessage(),this);
        }
    }
    public void MainFunction(){
        if(isEnterInFunc) return;
        countVisit++;
        isEnterInFunc = true;
        RecordsFromQueryDB[] records = ApiQuery.Instance().QueryFromDatabase(this);
        RecordsFromQueryDB recordUpp, recordWls, recordHeight, recordMotorCurrent;
        if(records == null || records.length == 0){
            VerificationMainFunction();
            return;
        }
        recordUpp = Helper.GetLastRecordByType(records,"upp");
        recordWls = Helper.GetLastRecordByType(records, "wls");
        recordHeight = Helper.GetLastRecordByType(records, "высота");
        recordMotorCurrent = Helper.GetLastRecordByType(records, "motorCurrent");

        if(recordUpp == null || recordWls == null || recordHeight == null){
            VerificationMainFunction();
            return;
        }
        sendInActive(records);

        String[] arrUpp = recordUpp.getS2().split("; ");
        boolean isMotorStart = false;
        String strMotor = "насос: ";
        for(String uppTmp : arrUpp){
            String[] arrTmp = uppTmp.split("=");
            if(!arrTmp[0].equals("мотор")) continue;
            strMotor += arrTmp[1].toLowerCase();
            if(arrTmp[1].equals("START")){
                isMotorStart = true;
                dtUppStop = null;
            }
            else {
                isMotorStart = false;
                if(dtUppStop == null){
                    dtUppStop = new Date();
                }
            }
        }

        double motorCurrent = recordMotorCurrent.getD1d();

        double height = recordHeight.getD1d();

        int wls = (int)(recordWls.getD1d());

        Date dtTmp = new Date();
        double WLSmin2 = 9, WLSmax2 = 13.75, proc2 = 20, maxTimeStop = 30;
        boolean isAutoQueryByDiscrepancy = false;
        try{
            maxTimeStop = Double.parseDouble(Util.getPropertyOrSetDefaultValue("maxTimeStop", "30",this));
            proc2 = Double.parseDouble(Util.getPropertyOrSetDefaultValue("Proc2", "20",this));
            WLSmin2 = Double.parseDouble(Util.getPropertyOrSetDefaultValue("WLSmin2", "9",this));
            WLSmax2 = Double.parseDouble(Util.getPropertyOrSetDefaultValue("WLSmax2", "13.75",this));
            isAutoQueryByDiscrepancy = Boolean.parseBoolean(Util.getPropertyOrSetDefaultValue("AutoQueryByDiscrepancy", "false",this));
            gIsAdmin = Boolean.parseBoolean(Util.getPropertyOrSetDefaultValue("isAdmin", "false",this));
        }
        catch (Exception ex){
            ex.fillInStackTrace();
        }

        if(dtUppStop != null){
            if(Helper.getDateDiff(dtUppStop, dtTmp, TimeUnit.MINUTES) > maxTimeStop){
                sendNotif("УПП находится в стопе более чем " + maxTimeStop + " мин", "", Const.notifMaxTimeStop);
            }
        }
        if(isMotorStart && motorCurrent < proc2){
            sendNotif("ток < " + proc2,"", Const.notifTokLessThanProc);
        }
        else if((wls == 0 && height > 12.5)||(wls == 15 && height < 13.5)||(wls < 7 && height > 13.7)||(wls > 1 && height < 12)){
            if(countVisit < 2 && lastSendNotifId != Const.notifMismatchIdWlsAndHeightMoreThanProc){
                if(isAutoQueryByDiscrepancy){
                    if(ApiQuery.Instance().Poll(Const.objectIdWLS, "", "Current", this)){
                        try {
                            Thread.sleep(1000 * 3);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        VerificationMainFunction();
                    }
                }else {
                    VerificationMainFunction();
                }
            }
            return;
        }
        else if(((height >  13.6 || wls >= 7) && isMotorStart) || ((height <  12.4 || wls <= 1) && !isMotorStart) ||
                ((height <  WLSmin2 ) && isMotorStart) || ((height > WLSmax2 || wls == 15) && isMotorStart)){
            VerificationMainFunction();
            return;
        }

        if(gWls == -1 || gHeight == -1 || gWls != wls || gHeight != height || gIsMotorStart != isMotorStart){
            Util.logsInfo("h: " + formatDouble.format(height) + "м; WLS: " + Integer.toBinaryString(wls) + "; " + strMotor,this);
            gWls = wls;
            gIsMotorStart = isMotorStart;
            gHeight = height;
        }

        countVisit--;
    }

    public void sendInActive(RecordsFromQueryDB[] records){
        RecordsFromQueryDB recordUpp = Helper.GetLastRecordByType(records,"upp");
        RecordsFromQueryDB recordWls = Helper.GetLastRecordByType(records, "wls");
        RecordsFromQueryDB recordHeight = Helper.GetLastRecordByType(records, "высота");
        RecordsFromQueryDB recordCurrentPhaseMax = Helper.GetLastRecordByType(records, "currentPhaseMax");
        RecordsFromQueryDB recordLastStartTime = Helper.GetLastRecordByType(records, "lastStartTime");
        RecordsFromQueryDB recordLastStopTime = Helper.GetLastRecordByType(records, "lastStopTime");
        RecordsFromQueryDB recordMotorCurrent = Helper.GetLastRecordByType(records, "motorCurrent");

        if(recordUpp == null || recordWls == null || recordHeight == null){
            return;
        }

        String strMotor = "насос\n", strUppSecondary = "";

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String strLastUpdate = "время обновления: " + simpleDateFormat.format(recordUpp.getDateDt());
        String strTimeDataWls = "время опроса башни: " + recordWls.getS2();
        String strLastStartTime = recordLastStartTime != null ? ("время запуска: "  + recordLastStartTime.getS2()) : "undefined";
        String strlastStopTime = recordLastStopTime != null ? ("время останова: " + recordLastStopTime.getS2()) : "undefined";

        String[] arrUpp = recordUpp.getS2().split("; ");
        for(String uppTmp : arrUpp){
            String[] arrTmp = uppTmp.split("=");
            switch (arrTmp[0]){
                case "мотор":
                    if(arrTmp[1].equals("START")){
                        strMotor += "ЗАПУЩЕН";
                    }
                    else {
                        strMotor += "ОСТАНОВЛЕН";
                    }
                    break;
                case "Auto mode":
                    strUppSecondary += "Auto mode: " + arrTmp[1] + "\n";
                    break;
                case "Fault":
                    strUppSecondary += "Fault: " + arrTmp[1] + "\n";
                    break;
                case "TOR":
                    strUppSecondary += "TOR: " + arrTmp[1] + "\n";
                    break;
                case "ReadySS":
                    strUppSecondary += "ReadySS: " + arrTmp[1] + "\n";
                    break;
                case "DI":
                    strUppSecondary += "DI: " + arrTmp[1] + "\n";
                    break;
            }
        }

        String strUppMain = recordCurrentPhaseMax != null ? ("Макс.ток фаз,A:\n" + recordCurrentPhaseMax.getD1s() + "\n") : "undefined\n";
        strUppMain += recordMotorCurrent != null ? ("% от номинала:\n" + recordMotorCurrent.getD1s()) : "undefined";

        double height = recordHeight.getD1d();

        String strHeight = "\n\nВысота,м: " + formatDouble.format(height) +"\n";

        strHeight += "Заполн,%: " + formatDouble.format(height * 100 / 14)+"\n";

        strHeight += "WLS: " +  Integer.toBinaryString((int)(recordWls.getD1d()));

        Intent intent = new Intent().putExtra("tvMotor", strMotor)
                .putExtra("myProgressBar",(int)(height*100))
                .putExtra("tvHeightWaters", strHeight)
                .putExtra("tvLastStartTime", strLastStartTime)
                .putExtra("tvLastStopTime", strlastStopTime)
                .putExtra("tvUPPMain", strUppMain)
                .putExtra("tvUPPSecondary", strUppSecondary)
                .putExtra("tvLastUpdate", strLastUpdate)
                .putExtra("tvTimeDataWls", strTimeDataWls);
        try {
            pendingIntent.send(MyService.this, Const.Success, intent);
        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
        }
    }

    public void VerificationMainFunction(){
        if(countVisit > 1) return;
        if(!ApiQuery.Instance().Poll(Const.objectIdUpp, "", "Current", this)) return;
        RecordsFromQueryDB[] records = ApiQuery.Instance().QueryFromDatabase(this);
        if(records == null || records.length == 0){
            Intent intent = new Intent().putExtra("tvHeightWaters", "\n\n" + Const.ifDataNull);
            try {
                pendingIntent.send(MyService.this, Const.Error, intent);
            } catch (PendingIntent.CanceledException e) {
                e.printStackTrace();
            }
            sendNotif(Const.ifDataNull, "", Const.notifDataNull);
            return;
        }
        RecordsFromQueryDB recordsUpp = Helper.GetLastRecordByType(records,"upp");
        RecordsFromQueryDB recordsWls = Helper.GetLastRecordByType(records, "wls");
        RecordsFromQueryDB recordsHeight = Helper.GetLastRecordByType(records, "высота");

        if(recordsUpp == null || recordsWls == null || recordsHeight == null){
            Intent intent = new Intent().putExtra("tvHeightWaters", "\n\n" + Const.ifDataNull);
            try {
                pendingIntent.send(MyService.this, Const.Error, intent);
            } catch (PendingIntent.CanceledException e) {
                e.printStackTrace();
            }
            sendNotif(Const.ifDataNull, "", Const.notifDataNull);
            return;
        }
        sendInActive(records);
        String strMotor = "насос: ", contentText;
        String[] arrUpp = recordsUpp.getS2().split("; ");
        boolean isMotorStart = false;
        for(String uppTmp : arrUpp){
            String[] arrTmp = uppTmp.split("=");
            if (!arrTmp[0].equals("мотор")) continue;
            if(arrTmp[1].equals("START")){
                strMotor += "start";
                isMotorStart = true;
            }
            else {
                strMotor += "stop";
                isMotorStart = false;
            }
        }

        double height = recordsHeight.getD1d();
        int wls = (int)(recordsWls.getD1d());

        if(gWls == -1 || gHeight == -1 || gWls != wls || gHeight != height || gIsMotorStart != isMotorStart){
            gWls = wls;
            gHeight = height;
            gIsMotorStart = isMotorStart;
            contentText = "h: " + formatDouble.format(height) + "м; WLS: " + Integer.toBinaryString(wls) + " " + strMotor;
            if((height >  13.6 || wls >= 7) && isMotorStart){
                if((height > 13.75 || wls > 7) && isMotorStart){
                    sendNotif(contentText, "wls>wls_max2&&Start", Const.notifWlsMoreThanWlsmax2_Start);
                }
                else
                    sendNotif(contentText, "wls>wls_max&&Start", Const.notifWlsMoreThanWlsmax_Start);
            } else if((height <  12.4 || wls <= 1) && !isMotorStart){
                    sendNotif(contentText, "wls<wls_min&&Stop", Const.notifWlsLessThanWlsmin_Stop);
            } else if((height <  10 ) && isMotorStart){
                sendNotif(contentText, "wls<wls_min2&&Start", Const.notifWlsLessThanWlsmin2_Start);
            } else if((wls == 0 && height > 12.5)||(wls == 15 && height < 13.5)||(wls < 7 && height > 13.7)||(wls > 1 && height < 12)){
                sendNotif(contentText, "mismatch in wls and height more than proc", Const.notifMismatchIdWlsAndHeightMoreThanProc);
            } else {
                Util.logsInfo("h: " + formatDouble.format(height) + "м; WLS: " + Integer.toBinaryString(wls) + "; " + strMotor,this);
                countVisit--;
            }
        }
    }
}

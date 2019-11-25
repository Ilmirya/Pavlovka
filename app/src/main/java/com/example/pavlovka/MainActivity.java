package com.example.pavlovka;

import androidx.appcompat.app.AppCompatActivity;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import android.view.Menu;
import com.example.pavlovka.Classes.QueryFromDatabase.RecordsFromQueryDB;
import com.google.gson.Gson;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.Date;
import java.util.concurrent.Executor;
import java.nio.ByteBuffer;


public class MainActivity extends AppCompatActivity {
    TextView tvMotor, tvUPPMain, tvUPPSecondary, tvHeightWaters, tvLastStartTime, tvLastStopTime, tvLastUpdate, tvUpp, tvTimeDataWls,tvMonitoringWls;
    public Gson gson = new Gson();

    Handler handler = new Handler();
    public  MyService myService;
    public ProgressBar myProgressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myProgressBar = findViewById(R.id.pbWatersLevel);
        tvMotor = findViewById(R.id.tvMotor);
        tvUPPMain = findViewById(R.id.tvUPPMain);
        tvLastStartTime = findViewById(R.id.tvLastStartTime);
        tvLastStopTime = findViewById(R.id.tvLastStopTime);
        tvUPPSecondary = findViewById(R.id.tvUPPSecondary);
        tvHeightWaters = findViewById(R.id.tvWls);
        tvLastUpdate = findViewById(R.id.tvLastUpdate);
        tvUpp = findViewById(R.id.tvUpp);
        tvTimeDataWls = findViewById(R.id.tvTimeDataWls);
        tvMonitoringWls = findViewById(R.id.tvMonitoringWls);
        String login = "", password = "";

       try {
            login = Util.getProperty("login", this);
            password = Util.getProperty("password", this);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(login == null || password == null || login.equals("") || password.equals("")){
           Intent intentSignin = new Intent(this, SigninActivity.class);
            startActivityForResult(intentSignin, Const.Session);
        }
        else{
           FunctionAtStart();
        }
    }
    private void FunctionAtStart(){
        myService = new MyService();
        PendingIntent pendingIntent = createPendingResult(1, new Intent(),0  );
        Intent intent = new Intent(this, MyService.class).putExtra("pendingIntent", pendingIntent);
        stopService(intent);
        myService.IsActivity(true);
        startService(intent);
        new Thread(new Runnable() {
            @Override
            public void run() {
                TcpIpWls();
            }
        }).start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.CustomizableOptionsActivity:
                Intent intent = new Intent(this, CustomizableOptionsActivity.class);
                startActivity(intent);
                return true;
            case R.id.LogsActivity:
                Intent intent1 = new Intent(this, LogsActivity.class);
                startActivity(intent1);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        myService.IsActivity(false);
    }
    public void onClickWlsCardView(View view){
        sendMessage();
    }
    public void onClickPollCurrent(View view){
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(ApiQuery.Instance().Poll(Const.objectIdUpp,"","Current", MainActivity.this)){
                    MainFunction();
                }
            }
        }).start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == Const.Session){
           FunctionAtStart();
        }
        if(resultCode == Const.ClosedService){

        }
        if (resultCode == Const.Error) {
            tvHeightWaters.setText(data.getStringExtra("tvHeightWaters"));
        }
        if (resultCode == Const.Success) {
            tvHeightWaters.setText(data.getStringExtra("tvHeightWaters"));
            tvLastStartTime.setText(data.getStringExtra("tvLastStartTime"));
            tvLastStopTime.setText(data.getStringExtra("tvLastStopTime"));
            tvMotor.setText(data.getStringExtra("tvMotor"));
            tvUPPMain.setText(data.getStringExtra("tvUPPMain"));
            tvUPPSecondary.setText(data.getStringExtra("tvUPPSecondary"));
            tvLastUpdate.setText(data.getStringExtra("tvLastUpdate"));
            myProgressBar.setProgress(data.getIntExtra("myProgressBar",0));
            tvTimeDataWls.setText(data.getStringExtra("tvTimeDataWls"));
        }
    }

    public Socket socket;
    // while this is true, the server will continue running
    // used to send messages
    private DataOutputStream mBufferOut;
    // used to read messages from the server
    public BufferedReader mBufferIn;
    // sends message received notifications

    Executor es;
    public void sendMessage() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                int[] bytesSendHeight = new int[]{0x10,0x04,0x0B,0x00,0x00,0x02,0x70,0xAE};
                try {
                    for(int i = 0; i < bytesSendHeight.length; i++){
                        mBufferOut.writeByte(bytesSendHeight[i]);
                    }

                    mBufferOut.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                int[] bytesSendWls = new int[]{0x10,0x04,0x01,0x00,0x00,0x01,0x33,0x77};
                try {
                    for(int i = 0; i < bytesSendWls.length; i++){
                        mBufferOut.writeByte(bytesSendWls[i]);
                    }
                    mBufferOut.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    Thread.sleep(1000*5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }
    public void TcpIpWls(){
        int receiveLen;
        byte[] buffer = new byte[20];
        try {
            socket = new Socket(InetAddress.getByName(Const.IpAddressWls), Const.wlsPort);
            mBufferOut = new DataOutputStream(socket.getOutputStream());

            DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
            mBufferIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            float height = 0, pressure;

            int wls;
            NumberFormat formatDouble = new DecimalFormat("#00.00");
            String strTmp = "";

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
            while (true){
                try{
                    receiveLen = dataInputStream.read(buffer);
                    if (receiveLen > 5 && buffer[0] == 0x10 && buffer[1] == 0x04) {
                        if(buffer[2] == 0x04){
                            Date dtNow = new Date();
                            pressure = ByteBuffer.wrap(new byte[]{buffer[5],buffer[6],buffer[3],buffer[4]}).getFloat();
                            height = pressure;
                            strTmp = simpleDateFormat.format(dtNow) + "->" + formatDouble.format(height) + "("+ formatDouble.format(pressure) +")";
                        }
                        if(buffer[2] == 0x02){
                            wls = (int)buffer[4];
                            strTmp += ":" + Integer.toBinaryString(wls);
                        }
                        final String tmp1 = strTmp;
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                tvMonitoringWls.setText(tmp1);
                            }
                        });
                    }
                }
                catch (Exception exc){}
            }

        } catch (Exception exc) {
            Util.logsException(exc.getMessage(),this);
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                Util.logsException(e.getMessage(),this);
            }
        }
    }

    public void MainFunction(){
        RecordsFromQueryDB[] records = ApiQuery.Instance().QueryFromDatabase(this);
        if(records == null || records.length == 0){
            handler.post(new Runnable() {
                @Override
                public void run() {
                    tvHeightWaters.setText("\n\n" + Const.ifDataNull);
                }
            });
            return;
        }
        RecordsFromQueryDB recordsUpp = Helper.GetLastRecordByType(records,"upp");
        RecordsFromQueryDB recordsWls = Helper.GetLastRecordByType(records, "wls");
        RecordsFromQueryDB recordsCurrentPhaseMax = Helper.GetLastRecordByType(records, "currentPhaseMax");
        RecordsFromQueryDB recordsHeight = Helper.GetLastRecordByType(records, "высота");
        RecordsFromQueryDB recordsLastStartTime = Helper.GetLastRecordByType(records, "lastStartTime");
        RecordsFromQueryDB recordsLastStopTime = Helper.GetLastRecordByType(records, "lastStopTime");
        RecordsFromQueryDB recordsMotorCurrent = Helper.GetLastRecordByType(records, "motorCurrent");

        if(recordsUpp == null || recordsWls == null || recordsHeight == null){
            handler.post(new Runnable() {
                @Override
                public void run() {
                    tvHeightWaters.setText("\n\n" + Const.ifDataNull);
                }
            });
            return;
        }

        String strMotor = "насос\n", strUppSecondary = "";

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        final String strLastUpdate = "время обновления: " + simpleDateFormat.format(recordsUpp.getDateDt());
        final String strTimeDataWls = "время опроса башни: " + recordsWls.getS2();

        final String strLastStartTime = recordsLastStartTime != null ? ("время запуска: "  + recordsLastStartTime.getS2()) : "undefined";
        final String strlastStopTime = recordsLastStopTime != null ? ("время останова: " + recordsLastStopTime.getS2()) : "undefined";

        String[] arrUpp = recordsUpp.getS2().split("; ");
        for(String uppTmp : arrUpp){
            String[] arrTmp = uppTmp.split("=");
            if (arrTmp[0].equals("мотор")) {
                if(arrTmp[1].equals("START")){
                    strMotor += "СТАРТ";//"ЗАПУЩЕН";
                }
                else {
                    strMotor += "СТОП";// "ОСТ-ЛЕН";
                }
            }
            else if (arrTmp[0].equals("Auto mode")) {
                strUppSecondary += "Auto mode: " + arrTmp[1] + "\n";
            }
            else if (arrTmp[0].equals("Fault")) {
                strUppSecondary += "Fault: " + arrTmp[1] + "\n";
            }
            else if (arrTmp[0].equals("TOR")) {
                strUppSecondary += "TOR: " + arrTmp[1] + "\n";
            }
            else if (arrTmp[0].equals("ReadySS")) {
                strUppSecondary += "ReadySS: " + arrTmp[1] + "\n";
            }
            else if (arrTmp[0].equals("DI")) {
                strUppSecondary += "DI: " + arrTmp[1] + "\n";
            }
        }
        NumberFormat formatDouble = new DecimalFormat("#00.00");

        String strUppMain = recordsCurrentPhaseMax != null ? ("Макс.ток фаз,A:\n" + recordsCurrentPhaseMax.getD1s() + "\n") : ("undefined\n");
        strUppMain += recordsMotorCurrent != null ? ("% от номинала:\n" + recordsMotorCurrent.getD1s()) : "undefined";

        double height = recordsHeight.getD1d();

        String strHeight = "Высота,м: " + formatDouble.format(height)+"\n";

        double percent = height*100/14;
        strHeight += "Заполн,%: " + formatDouble.format(percent)+"\n";

        int wls = (int)(recordsWls.getD1d());
        strHeight += "WLS: " +  Integer.toBinaryString(wls);

        final double height_f = height;
        final String strHeight_f = strHeight, strMotor_f = strMotor, strUppMain_f = strUppMain, strUppSecondary_f = strUppSecondary;
        handler.post(new Runnable() {
            @Override
            public void run() {
                tvHeightWaters.setText(strHeight_f);
                tvLastStartTime.setText(strLastStartTime);
                tvLastStopTime.setText(strlastStopTime);
                tvMotor.setText(strMotor_f);
                tvUPPMain.setText(strUppMain_f);
                tvUPPSecondary.setText(strUppSecondary_f);
                tvLastUpdate.setText(strLastUpdate);
                myProgressBar.setProgress((int)(height_f*100));
                tvTimeDataWls.setText(strTimeDataWls);
            }
        });

    }

}

package com.example.pavlovka;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;

import java.io.IOException;

public class CustomizableOptionsActivity extends AppCompatActivity {
    private EditText etWLSmin2, etWLSmax2, etProc1, etMaxTimeStop, etProc2;
    private Switch swhAutoQueryByDiscrepancy, swhWlsLessThenWlsminAndStop, swhWlsMoreThenWlsmaxAndStart,
            swhWlsLessThenWLsmin2AndStart, swhWlsMoreThenWlsmax2AndStart, swhProc1, swhMaxTimeStop, swhProc2,swhDataNull,swhNotConnection;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customizable_options);
        assert getSupportActionBar() != null;   //null check
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);   //show back button

        etWLSmin2 = findViewById(R.id.etWLSmin2);
        etWLSmax2 = findViewById(R.id.etWLSmax2);
        etProc1 = findViewById(R.id.etProc1);
        etMaxTimeStop = findViewById(R.id.etMaxTimeStop);
        etProc2 = findViewById(R.id.etProc2);
        swhAutoQueryByDiscrepancy = findViewById(R.id.swhAutoQueryByDiscrepancy);

        swhWlsLessThenWlsminAndStop = findViewById(R.id.swhWlsLessThenWLsminAndStop);
        swhWlsMoreThenWlsmaxAndStart = findViewById(R.id.swhWlsMoreThenWlsmaxAndStart);
        swhWlsLessThenWLsmin2AndStart = findViewById(R.id.swhWlsLessThenWlsmin2AndStart);
        swhWlsMoreThenWlsmax2AndStart = findViewById(R.id.swhWlsMoreThenWlsmax2AndStart);
        swhProc1 = findViewById(R.id.swhProc1);
        swhMaxTimeStop = findViewById(R.id.swhMaxTimeStop);
        swhProc2 = findViewById(R.id.swhProc2);
        swhDataNull = findViewById(R.id.swhDataNull);
        swhNotConnection = findViewById(R.id.swhNotConnection);

        try {
            swhWlsLessThenWlsminAndStop.setChecked(Boolean.parseBoolean(Util.getPropertyOrSetDefaultValue("isWlsLessThenWlsminAndStop", "false",this)));
            swhWlsMoreThenWlsmaxAndStart.setChecked(Boolean.parseBoolean(Util.getPropertyOrSetDefaultValue("isWlsMoreThenWlsmaxAndStart", "false",this)));
            swhWlsLessThenWLsmin2AndStart.setChecked(Boolean.parseBoolean(Util.getPropertyOrSetDefaultValue("isWlsLessThenWLsmin2AndStart", "false",this)));
            swhWlsMoreThenWlsmax2AndStart.setChecked(Boolean.parseBoolean(Util.getPropertyOrSetDefaultValue("isWlsMoreThenWlsmax2AndStart", "false",this)));
            swhProc1.setChecked(Boolean.parseBoolean(Util.getPropertyOrSetDefaultValue("isProc1", "false",this)));
            swhMaxTimeStop.setChecked(Boolean.parseBoolean(Util.getPropertyOrSetDefaultValue("isMaxTimeStop", "false",this)));
            swhProc2.setChecked(Boolean.parseBoolean(Util.getPropertyOrSetDefaultValue("isProc2", "false",this)));
            swhDataNull.setChecked(Boolean.parseBoolean(Util.getPropertyOrSetDefaultValue("isDataNull", "false",this)));
            swhNotConnection.setChecked(Boolean.parseBoolean(Util.getPropertyOrSetDefaultValue("isNotConnection", "false",this)));

            swhAutoQueryByDiscrepancy.setChecked(Boolean.parseBoolean(Util.getPropertyOrSetDefaultValue("AutoQueryByDiscrepancy", "false",this)));
            etWLSmin2.setText(Util.getPropertyOrSetDefaultValue("WLSmin2", "9",this));
            etWLSmax2.setText(Util.getPropertyOrSetDefaultValue("WLSmax2", "13.75",this));
            etProc1.setText(Util.getPropertyOrSetDefaultValue("Proc1", "10",this));
            etMaxTimeStop.setText(Util.getPropertyOrSetDefaultValue("maxTimeStop", "30",this));
            etProc2.setText(Util.getPropertyOrSetDefaultValue("Proc2", "20",this));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
    public void onClickSaveOptions(View view){
        try {
            Util.setPropertyConfig("isWlsLessThenWlsminAndStop", Boolean.toString(swhWlsLessThenWlsminAndStop.isChecked()), this);
            Util.setPropertyConfig("isWlsMoreThenWlsmaxAndStart", Boolean.toString(swhWlsMoreThenWlsmaxAndStart.isChecked()), this);
            Util.setPropertyConfig("isWlsLessThenWLsmin2AndStart", Boolean.toString(swhWlsLessThenWLsmin2AndStart.isChecked()), this);
            Util.setPropertyConfig("isWlsMoreThenWlsmax2AndStart", Boolean.toString(swhWlsMoreThenWlsmax2AndStart.isChecked()), this);
            Util.setPropertyConfig("isProc1", Boolean.toString(swhProc1.isChecked()), this);
            Util.setPropertyConfig("isMaxTimeStop", Boolean.toString(swhMaxTimeStop.isChecked()), this);
            Util.setPropertyConfig("isProc2", Boolean.toString(swhProc2.isChecked()), this);
            Util.setPropertyConfig("isDataNull", Boolean.toString(swhDataNull.isChecked()), this);
            Util.setPropertyConfig("isNotConnection", Boolean.toString(swhNotConnection.isChecked()), this);

            Util.setPropertyConfig("AutoQueryByDiscrepancy", Boolean.toString(swhAutoQueryByDiscrepancy.isChecked()), this);
            Util.setPropertyConfig("WLSmin2", etWLSmin2.getText().toString(),this);
            Util.setPropertyConfig("WLSmax2", etWLSmax2.getText().toString(),this);
            Util.setPropertyConfig("Proc1", etProc1.getText().toString(),this);
            Util.setPropertyConfig("maxTimeStop", etMaxTimeStop.getText().toString(),this);
            Util.setPropertyConfig("Proc2", etProc2.getText().toString(),this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

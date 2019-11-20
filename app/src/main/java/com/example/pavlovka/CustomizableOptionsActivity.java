package com.example.pavlovka;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;

import java.io.IOException;

public class CustomizableOptionsActivity extends AppCompatActivity {

    public EditText etWLSmin2, etWLSmax2, etProc1, etMaxTimeStop, etProc2;
    public Switch switchAutoQueryByDiscrepancy;
    String WLSmin2, WLSmax2, Proc1, maxTimeStop,Proc2;
    public boolean dd;
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
        switchAutoQueryByDiscrepancy = findViewById(R.id.switchAutoQueryByDiscrepancy);
        try {
            switchAutoQueryByDiscrepancy.setChecked(Boolean.parseBoolean(Util.getPropertyOrSetDefaultValue("AutoQueryByDiscrepancy", "false",this)));
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
    boolean dd1;
    String ddtrgt;
    public void onClickSaveOptions(View view){
        try {
            Util.setPropertyConfig("AutoQueryByDiscrepancy", Boolean.toString(switchAutoQueryByDiscrepancy.isChecked()), this);
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

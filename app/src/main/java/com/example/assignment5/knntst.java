package com.example.assignment5;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

public class knntst extends AppCompatActivity {
    private ListView wifiList;
    private WifiManager wifiManager;
    private final int MY_PERMISSIONS_ACCESS_COARSE_LOCATION = 1;
    List<ScanResult> wifiList1;
    ArrayList<model> mylist = new ArrayList<>();
    Button tstb;
    TextView knnE;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_knntst);
        RoomDB rdb= RoomDB.getInstance(getApplicationContext());
        MainDao mainDao = rdb.mainDao();
        wifiList = findViewById(R.id.wifiList211);
        Button buttonScan = findViewById(R.id.scan211);
        tstb = findViewById(R.id.testbtn11);
        knnE = findViewById(R.id.roomNamefindEdit11);
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }
        buttonScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(knntst.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(knntst.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSIONS_ACCESS_COARSE_LOCATION);
                } else {
                    wifiManager.startScan();
                }
            }
        });

        tstb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Integer> rc = mainDao.getRowCount();
                List<MyTable> mt = mainDao.getAll();
                List<model2> mt2 = new ArrayList<>();
                for(int i=0;i<rc.get(0);i++){
                    for(int j=0;j<wifiList1.size();j++){
                        if(wifiList1.get(j).SSID.equals(mt.get(i).ssid)){
                            model2 m2 = new model2();
                            m2.setindx(i);
                            // m2.setvalue( Math.sqrt(Math.pow((wifiList1.get(j).level - mt.get(j).rssi),2)));
                            m2.setvalue( wifiList1.get(j).level - mt.get(j).rssi);
                            mt2.add(m2);
                        }
                    }
                }
                Collections.sort(mt2, new Sortbyvalue());
                if(mt.get(mt2.get(0).indx).roomno == mt.get(mt2.get(1).indx).roomno || mt.get(mt2.get(0).indx).roomno == mt.get(mt2.get(2).indx).roomno  ){
                    knnE.setText(String.valueOf(mt.get(mt2.get(0).indx).roomno));
                }
                else if (mt.get(mt2.get(1).indx).roomno == mt.get(mt2.get(2).indx).roomno){
                    knnE.setText(String.valueOf(mt.get(mt2.get(1).indx).roomno));
                }
                else {
                    knnE.setText(String.valueOf(mt.get(mt2.get(0).indx).roomno));
                    knnE.setTextColor(Color.CYAN);
                }
            }

            class Sortbyvalue implements Comparator<model2>
            {
                public int compare(model2 a, model2 b)
                {
                    return (int)(a.value - b.value);
                }
            }
        });

    }
    @Override
    protected void onPostResume() {
        super.onPostResume();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        registerReceiver(new BroadcastReceiver()
        { @RequiresApi(api = Build.VERSION_CODES.R)
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals(action)) {
                wifiList1 = wifiManager.getScanResults();
                ArrayList<String> deviceList = new ArrayList<>();
                for (ScanResult scanResult : wifiList1) {
                    deviceList.add(scanResult.SSID + "    RSSI   - " +(scanResult.level) );
                    model m = new model();
                    m.setName(scanResult.SSID);
                    m.setvalue(scanResult.level);
                    mylist.add(m);
                }
                ArrayAdapter arrayAdapter = new ArrayAdapter(context, android.R.layout.simple_list_item_1, deviceList.toArray());
                wifiList.setAdapter(arrayAdapter);
            }
        }}, intentFilter);
        getWifi();
    }
    private void getWifi() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(knntst.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(knntst.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSIONS_ACCESS_COARSE_LOCATION);
            } else {
                wifiManager.startScan();
            }
        } else {
            wifiManager.startScan();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_ACCESS_COARSE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    wifiManager.startScan();
                } else {
                    Toast.makeText(knntst.this, "permission required", Toast.LENGTH_SHORT).show();
                    return;
                }
                break;
        }
    }
}
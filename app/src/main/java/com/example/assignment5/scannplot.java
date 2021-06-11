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
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class scannplot extends AppCompatActivity {
    private ListView wifiList;
    private WifiManager wifiManager;
    private final int MY_PERMISSIONS_ACCESS_COARSE_LOCATION = 1;
    List<ScanResult> wifiList1;
    ArrayList<model> mylist = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scannplot);
        wifiList = findViewById(R.id.wifiList);
        Button buttonScan11 = findViewById(R.id.buttonscnplt);
        Button buttonScan12 = findViewById(R.id.buttonplt);
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }
        buttonScan11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(scannplot.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(scannplot.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSIONS_ACCESS_COARSE_LOCATION);
                } else {
                    wifiManager.startScan();
                }
            }
        });
        buttonScan12.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              plotfun();
            }
        });

    }
    public void plotfun(){
        final ArrayList<String> xAxisLabel = new ArrayList<>();
        BarChart barChart = findViewById(R.id.barChart1);
        ArrayList<BarEntry> ev = new ArrayList<>();
        int count=1;
        for (model m:mylist){
            int flag=0;
            for (int i=0;i<xAxisLabel.size();i++){
                if(xAxisLabel.get(i).equals(m.getName())){
                    flag = 1;
                    break;
                }
            }
            if (flag !=1){
                xAxisLabel.add(m.getName());
                ev.add(new BarEntry(count,(100+m.getvalue())));
                count++;}
        }
        BarDataSet barDataSet = new BarDataSet(ev, "RSSI Values");
        barDataSet.setColors(ColorTemplate.VORDIPLOM_COLORS);
        barDataSet.setValueTextColor(Color.BLACK);
        barDataSet.setValueTextSize(16f);
        if(xAxisLabel != null){
            barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(xAxisLabel));
            barChart.getXAxis().setGranularity(1f);
            barChart.getXAxis().setGranularityEnabled(true);
            barChart.getXAxis().setAvoidFirstLastClipping(true);
            barChart.getXAxis().setCenterAxisLabels(true);
        }

        BarData barData = new BarData(barDataSet);
        barChart.setFitBars(true);
        barChart.setData(barData);
        barChart.getDescription().setText("Bar Chart for RSSI");
        barChart.animateY(1000);
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
              //  ArrayAdapter arrayAdapter = new ArrayAdapter(context, android.R.layout.simple_list_item_1, deviceList.toArray());
              //  wifiList.setAdapter(arrayAdapter);
            }
        }}, intentFilter);
        getWifi();
    }
    private void getWifi() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(scannplot.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(scannplot.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSIONS_ACCESS_COARSE_LOCATION);
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
                    Toast.makeText(scannplot.this, "permission required", Toast.LENGTH_SHORT).show();
                    return;
                }
                break;
        }
    }
}
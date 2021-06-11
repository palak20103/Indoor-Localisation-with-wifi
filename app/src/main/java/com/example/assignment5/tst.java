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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

public class tst extends AppCompatActivity {
    private ListView wifiList;
    private WifiManager wifiManager;
    private final int MY_PERMISSIONS_ACCESS_COARSE_LOCATION = 1;
    List<ScanResult> wifiList1;
    ArrayList<model> mylist = new ArrayList<>();
    Button tstb;
    TextView nme;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tst);
        RoomDB rdb= RoomDB.getInstance(getApplicationContext());
        MainDao mainDao = rdb.mainDao();
        wifiList = findViewById(R.id.wifiList21);
        Button buttonScan = findViewById(R.id.scan21);
        tstb = findViewById(R.id.testbtn1);
        nme = findViewById(R.id.roomNamefindEdit1);
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }
        buttonScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(tst.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(tst.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSIONS_ACCESS_COARSE_LOCATION);
                } else {
                    wifiManager.startScan();
                }
            }
        });

        tstb.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {

                List<Integer> rooms1 = mainDao.getroomno1();
                List<Integer> rooms = rooms1.stream().distinct().collect(Collectors.toList());
                int rsize[] = new int[rooms.size()];
                int indx[] = new int[rooms.size()];
                double xx[][] = new double[rooms.size()][wifiList1.size()];
                for (int r : rooms ){
                    int count=0;
                    List<String> s = mainDao.getssid(r);
                    HashSet<String> uniqueWords = new HashSet<String>(s);
                    for(String s1:uniqueWords)
                    {
                        List<Integer> ii = mainDao.getmin(r,s1);
                        List<Integer> iii = mainDao.getmax(r,s1);
                        int cnt=0;
                        for(ScanResult scanResult : wifiList1){
                            if(cnt == wifiList1.size()){cnt=0;}
                            if(scanResult.SSID.equals(s1)){
                                if(scanResult.level >= ii.get(0) && scanResult.level <= iii.get(0)){
                                    count ++;
                                    xx[r-1][cnt]   =   scanResult.level -   (  (ii.get(0) + iii.get(0)) /2  );
                                    cnt++;
                                }
                            }
                        }
                    }
                    for(int j=0;j<wifiList1.size();j++) {
                        double min=xx[0][0];
                        for(int i=0; i<rooms.size() ; i++){
                            if(xx[i][j] < min){
                                min = xx[i][j];
                                indx[i] = i;
                            }
                        }
                    }

                    rsize[r-1]=count;}
                int ans=0,finalans=0;

                for(int i=0; i<rsize.length;i++){
                    if(rsize[i]>ans){
                        ans=rsize[i];
                        finalans=i+1;
                    }
                }
                nme.setText(String.valueOf(finalans));
                nme.setTextColor(Color.YELLOW);
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
            if (ContextCompat.checkSelfPermission(tst.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(tst.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSIONS_ACCESS_COARSE_LOCATION);
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
                    Toast.makeText(tst.this, "permission required", Toast.LENGTH_SHORT).show();
                    return;
                }
                break;
        }
    }
}
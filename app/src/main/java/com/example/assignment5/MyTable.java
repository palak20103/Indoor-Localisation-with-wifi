package com.example.assignment5;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import java.io.Serializable;

@Entity(tableName = "table1")
public class MyTable implements Serializable {
    @PrimaryKey(autoGenerate = true)
    public int ID;
    @ColumnInfo(name = "SSID")
    public String ssid;
    @ColumnInfo(name = "RSSI")
    public int rssi;
    @ColumnInfo(name = "RoomNo")
    public int roomno;
}
package com.example.assignment5;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface MainDao {
    @Insert
    void insert(MyTable table);
    @Query("Select RoomNo from table1")
    List<Integer> getroomno1();
    @Query("Select SSID from table1 where roomno == :r")
    List<String> getssid(int r);
    @Query("Select MIN(RSSI) from table1 where roomno == :r AND ssid == :s")
    List<Integer> getmin(int r,String s);
    @Query("Select MAX(RSSI) from table1 where roomno == :r AND ssid == :s")
    List<Integer> getmax(int r,String s);
    @Query("SELECT COUNT(RSSI) FROM table1")
    List<Integer> getRowCount();
    @Query("SELECT * FROM table1")
    List<MyTable> getAll();
}

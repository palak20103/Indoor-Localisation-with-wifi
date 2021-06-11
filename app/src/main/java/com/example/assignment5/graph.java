package com.example.assignment5;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.io.Serializable;
import java.util.ArrayList;

public class graph extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);
        Intent intent=getIntent();
        final ArrayList<String> xAxisLabel = new ArrayList<>();
        ArrayList<model> mylist = (ArrayList<model>) intent.getSerializableExtra("key");
        BarChart barChart = findViewById(R.id.barChart);
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
        barDataSet.setColors(ColorTemplate.JOYFUL_COLORS);
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
        barChart.animateY(2000);
    }

    @Override
    public void onBackPressed() {
        Intent returnBtn = new Intent(getApplicationContext(),
                MainActivity.class);
        super.onBackPressed();
        startActivity(returnBtn);
    }
}

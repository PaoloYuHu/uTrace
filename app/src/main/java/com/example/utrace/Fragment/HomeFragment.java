package com.example.utrace.Fragment;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.example.utrace.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.util.List;
import java.util.Random;

public class HomeFragment extends Fragment {

    private static final int APP_COLOR = Color.BLACK;
    private static final int BACKGROUND_COLOR = Color.GRAY;
    private static final int PRIMARY_TEXT_COLOR = Color.GREEN;


    private View view;
    private LinearLayout square1;
    private LinearLayout square2;
    private EditText text1_1;
    private EditText text1_2;
    private EditText text2_1;
    private EditText text2_2;
    private BarChart chart1;
    private BarChart chart2;


    public HomeFragment() {}

    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        this.view = inflater.inflate(R.layout.fragment_home, container, false);
        square1 = view.findViewById(R.id.square1);
        square2 = view.findViewById(R.id.square2);
        text1_1 = view.findViewById(R.id.text1_1);
        text1_2 = view.findViewById(R.id.text1_2);
        text2_1 = view.findViewById(R.id.text2_1);
        text2_2 = view.findViewById(R.id.text2_2);
        chart1 = view.findViewById(R.id.chart1);
        chart2 = view.findViewById(R.id.chart2);

        text1_1.setText("CO2 consumata:");
        text2_1.setText("rete consumata:");

        //finche non abbiamo un db riempiamo la applicazione con dati finti
        putSampleData();

        return view;
    }


    private void putSampleData(){

        text1_2.setText("14 tons");
        text2_2.setText("77Mb");
        sampleCharts();
        style();
    }

    private void sampleCharts() {

        List<String> days = List.of(
                "Lun", "Mar", "Mer", "Gio", "Ven", "Sab", "Dom"
        );

        prepareChart(chart1, days);
        prepareChart(chart2, days);
    }

    private void prepareChart(BarChart barChart, List<String> label) {

        Random random = new Random();

        List<BarEntry> entries = List.of(
                new BarEntry(0,random.nextInt(7)),
                new BarEntry(1,random.nextInt(7)),
                new BarEntry(2,random.nextInt(7)),
                new BarEntry(3,random.nextInt(7)),
                new BarEntry(4,random.nextInt(7)),
                new BarEntry(5,random.nextInt(7)),
                new BarEntry(6,random.nextInt(7))
        );

        BarDataSet datasSet = new BarDataSet(entries, "");
        barChart.setData(new BarData(datasSet));

        YAxis yAxis = barChart.getAxisLeft();
        yAxis.setAxisMaximum(10f);
        yAxis.setAxisLineWidth(1.5f);
        yAxis.setLabelCount(label.size());
        yAxis.setAxisLineColor(Color.BLUE);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(label));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);

        barChart.getDescription().setEnabled(false);
        barChart.invalidate();
    }

    private void style(){

        square1.setBackgroundColor(BACKGROUND_COLOR);
        square2.setBackgroundColor(BACKGROUND_COLOR);


        text1_1.setTextColor(PRIMARY_TEXT_COLOR);
        text1_2.setTextColor(PRIMARY_TEXT_COLOR);
        text2_1.setTextColor(PRIMARY_TEXT_COLOR);
        text2_2.setTextColor(PRIMARY_TEXT_COLOR);

        //view.setBackgroundColor(getResources().getColor(PRIMARY_TEXT_COLOR));


    }
}


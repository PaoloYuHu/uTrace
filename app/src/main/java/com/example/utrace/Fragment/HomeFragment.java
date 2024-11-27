package com.example.utrace.Fragment;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.ColorInt;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
    // private static final int BACKGROUND_COLOR = Color.GRAY;
    // private static final int PRIMARY_TEXT_COLOR = Color.GREEN;


    private View view;
    private CardView batteryCard;
    private CardView internetCard;
    private TextView text1_1;
    private TextView text1_2;
    private TextView text2_1;
    private TextView text2_2;
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

        setToolbarTitle("Home");


        batteryCard = view.findViewById(R.id.batteryCard);
        internetCard = view.findViewById(R.id.internetCard);
        text1_1 = view.findViewById(R.id.text1_1);
        text1_2 = view.findViewById(R.id.text1_2);
        text2_1 = view.findViewById(R.id.text2_1);
        text2_2 = view.findViewById(R.id.text2_2);
        chart1 = view.findViewById(R.id.chart1);
        chart2 = view.findViewById(R.id.chart2);

        batteryCard.setOnClickListener(v -> {
            FragmentManager fragmentManager = getParentFragmentManager();  // Use getChildFragmentManager() if in a fragment
            fragmentManager.beginTransaction()
                    .replace(R.id.MainContainer, new BatteryFragment())
                    .addToBackStack(null)
                    .commit();
        });
        internetCard.setOnClickListener(v -> {
            FragmentManager fragmentManager = getParentFragmentManager();  // Use getChildFragmentManager() if in a fragment
            fragmentManager.beginTransaction()
                    .replace(R.id.MainContainer, new AppUsageFragment())
                    .addToBackStack(null)
                    .commit();
                });


        text1_1.setText("Batteria\nConsumi - CO2:");
        text2_1.setText("Rete\nConsumi - CO2:");

        //finche non abbiamo un db riempiamo la applicazione con dati finti
        putSampleData();

        return view;
    }


    private void putSampleData(){

        text1_2.setText("47Watt - 7Kg");
        text2_2.setText("100Mb - 2Kg");
        prepareChart(chart1, Color.GREEN, "Consumo batteria");
        prepareChart(chart2, Color.BLUE, "Consumo rete");
        style();
    }

    private void prepareChart(BarChart chart, @ColorInt int color, String label){

        List<String> days = List.of(
                "Lun", "Mar", "Mer", "Gio", "Ven", "Sab", "Dom"
        );

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

        BarDataSet datasSet = new BarDataSet(entries, label);
        datasSet.setColor(color);
        chart.setData(new BarData(datasSet));

        YAxis yAxis = chart.getAxisLeft();
        yAxis.setAxisMaximum(10f);
        yAxis.setAxisLineWidth(1.5f);
        yAxis.setLabelCount(days.size());

        //chart.getAxisRight() riguarda la colonna dei valori a dx che non ci interessa
        chart.getAxisRight().setDrawLabels(false); //toglie i valori
        chart.getAxisRight().setDrawGridLines(false); // toglie le righe sulla griglia

        XAxis xAxis = chart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(days));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);

        xAxis.setTextColor(Color.BLACK);
        xAxis.setDrawGridLines(false);

        chart.getDescription().setEnabled(false);
        chart.invalidate();

    }


    private void style(){
        //view.setBackgroundColor(getResources().getColor(PRIMARY_TEXT_COLOR));
    }

    private void setToolbarTitle(String title) {
        if (getActivity() != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(title);
        }
    }
}


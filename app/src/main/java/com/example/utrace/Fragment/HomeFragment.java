package com.example.utrace.Fragment;

import static com.example.utrace.utils.Constants.CELLULAR;
import static com.example.utrace.utils.Constants.CarbonGramPerGB;
import static com.example.utrace.utils.Constants.CarbonGramPerWh;
import static com.example.utrace.utils.Constants.DAY;
import static com.example.utrace.utils.Constants.GiB;
import static com.example.utrace.utils.Constants.WIFI;
import static com.example.utrace.utils.Constants.avgWattPerHour;

import android.app.usage.NetworkStats;
import android.app.usage.NetworkStatsManager;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.ColorInt;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
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
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";

    private View view;
    private CardView batteryCard;
    private CardView internetCard;
    private TextView text1_1;
    private TextView text1_2;
    private TextView text2_1;
    private TextView text2_2;
    private BarChart chart1;
    private BarChart chart2;

    List<String> days;
    private float totalScreenTime=0;

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

        batteryCard = view.findViewById(R.id.batteryCard);
        internetCard = view.findViewById(R.id.internetCard);
        text1_1 = view.findViewById(R.id.text1_1);
        text1_2 = view.findViewById(R.id.text1_2);
        text2_1 = view.findViewById(R.id.text2_1);
        text2_2 = view.findViewById(R.id.text2_2);
        chart1 = view.findViewById(R.id.chart1);
        chart2 = view.findViewById(R.id.chart2);

        setToolbarTitle("Home");

        setDates();
        // Ensure that getActivity() is not null for the first boot
        if (getActivity() != null) {
            BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.bottom_navigation);
            // Ensure that bottomNavigationView is not null
            if (bottomNavigationView != null) {
                bottomNavigationView.getMenu().getItem(0).setChecked(true);
            }
        }




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


        text1_1.setText("Consumi 7gg\nBatteria - CO2:");
        text2_1.setText("Consumi 7gg\nRete - CO2:");

        putData();

        return view;
    }


    private void putData(){

        List<BarEntry> usageEntries = fetchScreenTimeData();

        List<BarEntry> dataEntries = new ArrayList<>();
        long lastFetch=fetchNetworkStats(DAY);
        float maxData=lastFetch/GiB;
        dataEntries.add(new BarEntry(6, maxData ));
        for (int i = 1; i < 7; i++) {
            long currentFetch=fetchNetworkStats(DAY*(i+1));
            float currentData = (currentFetch-lastFetch)/GiB;
            dataEntries.add(new BarEntry(6-i,currentData));
            if(currentData>maxData)
                maxData=currentData;
            lastFetch=currentFetch;
        }

        double usageWh= totalScreenTime*avgWattPerHour;
        double carbonWh= usageWh*CarbonGramPerWh;
        text1_2.setText(String.format("%.0fWh - %.2fg", usageWh, carbonWh));

        long dataWeek = fetchNetworkStats(7 * DAY);
        double dataWeekGiB = (double) dataWeek / GiB;
        double carbonData = dataWeekGiB * CarbonGramPerGB;
        text2_2.setText(String.format("%.2fGB - %.1fg", dataWeekGiB, carbonData));

        prepareChart(chart1, Color.GREEN, "Screen Time (Ore)  *con app aperte", usageEntries, (float) Math.ceil(findMaxUsage(usageEntries)));
        prepareChart(chart2, Color.BLUE, "Consumo rete (GB)", dataEntries, (float) Math.ceil(maxData));

    }

    private void prepareChart(BarChart chart, @ColorInt int color, String label, List<BarEntry> entries, float max) {
        BarDataSet dataSet = new BarDataSet(entries, label);
        dataSet.setColor(color);
        BarData data = new BarData(dataSet);

        chart.setData(data);
        chart.getData().notifyDataChanged();  // Notify the chart that the data has changed
        chart.notifyDataSetChanged();  // Refresh the chart

        YAxis yAxis = chart.getAxisLeft();
        yAxis.setAxisMaximum(max);
        yAxis.setAxisLineWidth(1.5f);
        yAxis.setLabelCount(days.size());

        chart.getAxisRight().setDrawLabels(false); // Hide right axis labels
        chart.getAxisRight().setDrawGridLines(false); // Hide right axis grid lines

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


    private void setDates(){
        Calendar calendar = Calendar.getInstance();
        int todayIndex = calendar.get(Calendar.DAY_OF_WEEK);
        List<String> weekdays= List.of(
                "Dom","Lun", "Mar", "Mer", "Gio", "Ven", "Sab"
        );
        List<String> reorderedDays = new ArrayList<>(weekdays);
        Collections.rotate(reorderedDays, -todayIndex);

        reorderedDays.set(reorderedDays.size() - 1, "Oggi");
        days=reorderedDays;
    }

    private long fetchNetworkStats(long selectedTime) {
        NetworkStatsManager networkStatsManager = (NetworkStatsManager) getActivity().getSystemService(getContext().NETWORK_STATS_SERVICE);
        if (networkStatsManager == null) {
            Log.e(TAG, "NetworkStatsManager is null.");
            return 0;
        }

        long startTime = System.currentTimeMillis() - selectedTime;
        long endTime = System.currentTimeMillis();
        long res=0;
        try {
            NetworkStats.Bucket bucket = networkStatsManager.querySummaryForDevice(CELLULAR, null, startTime, endTime);
            res += bucket.getRxBytes();
            res += bucket.getTxBytes();
        } catch (Exception e) {
            Log.e(TAG, "Error querying mobile data usage", e);
        }

        try {
            NetworkStats.Bucket bucket = networkStatsManager.querySummaryForDevice(WIFI, null, startTime, endTime);
            res += bucket.getRxBytes();
            res += bucket.getTxBytes();
        } catch (Exception e) {
            Log.e(TAG, "Error querying Wi-Fi data usage", e);
        }
        return res;
    }



    private List<BarEntry> fetchScreenTimeData() {
        List<BarEntry> entries = new ArrayList<>();
        UsageStatsManager usageStatsManager = (UsageStatsManager) requireContext().getSystemService(Context.USAGE_STATS_SERVICE);
        if (usageStatsManager == null) {
            Log.e(TAG, "UsageStatsManager is null");
            return entries;
        }

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.add(Calendar.DAY_OF_YEAR, -7);

        long[] dailyUsage = new long[7];

        for (int i = 0; i < 7; i++) {
            long dayStartTime = calendar.getTimeInMillis();
            calendar.add(Calendar.DAY_OF_YEAR, 1);
            long dayEndTime = calendar.getTimeInMillis();

            UsageEvents usageEvents = usageStatsManager.queryEvents(dayStartTime, dayEndTime);
            dailyUsage[i] = calculateDailyUsage(usageEvents);
        }

        totalScreenTime = 0;

        for (int i = 0; i < dailyUsage.length; i++) {
            float usageInHours = dailyUsage[i] / (1000f * 60 * 60);
            totalScreenTime += usageInHours;
            entries.add(new BarEntry(i, usageInHours));
        }

        return entries;
    }

    private long calculateDailyUsage(UsageEvents usageEvents) {
        long totalTime = 0;
        UsageEvents.Event event = new UsageEvents.Event();
        long startTime = 0;
        boolean isForeground = false;

        while (usageEvents.hasNextEvent()) {
            usageEvents.getNextEvent(event);

            if (event.getEventType() == UsageEvents.Event.MOVE_TO_FOREGROUND) {
                startTime = event.getTimeStamp();
                isForeground = true;
            } else if (event.getEventType() == UsageEvents.Event.MOVE_TO_BACKGROUND && isForeground) {
                long endTime = event.getTimeStamp();
                totalTime += (endTime - startTime);
                isForeground = false;
            }
        }

        return totalTime;
    }


    private float findMaxUsage(List<BarEntry> entries) {
        float max = 0;
        for (BarEntry entry : entries) {
            if (entry.getY() > max) {
                max = entry.getY();
            }
        }
        return max;
    }


    private void setToolbarTitle(String title) {
        if (getActivity() != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(title);
        }
    }
}





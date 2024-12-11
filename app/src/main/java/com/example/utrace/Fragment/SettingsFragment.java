package com.example.utrace.Fragment;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;



import com.example.utrace.R;


public class SettingsFragment extends Fragment {

    private View view;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setToolbarTitle("Settings");

        view = inflater.inflate(R.layout.fragment_settings, container, false);

        /*
        AnyChartView chartView = view.findViewById(R.id.any_chart_view);
        Cartesian cartesianChart = AnyChart.column();

        List<DataEntry> data = new ArrayList<>();
        data.add(new ValueDataEntry("Jan", 10));
        data.add(new ValueDataEntry("Feb", 20));
        data.add(new ValueDataEntry("Mar", 15));
        data.add(new ValueDataEntry("Apr", 25));

        cartesianChart.data(data);
        chartView.setChart(cartesianChart);
        */

        return view;
    }

    private void setToolbarTitle(String title) {
        if (getActivity() != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(title);
        }
    }
}
package com.example.utrace.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.example.utrace.Activity.MainActivity;
import com.example.utrace.R;


public class SettingsFragment extends Fragment {

    private View view;
    private CardView logoutButton;

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

        logoutButton = view.findViewById(R.id.logoutBt);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences userPref = requireActivity().getSharedPreferences("user", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = userPref.edit();
                editor.clear();
                editor.apply();
                Intent mainIntent = new Intent(getActivity(), MainActivity.class);
                requireActivity().startActivity(mainIntent);
                requireActivity().finish();
            }
        });

        return view;
    }

    private void setToolbarTitle(String title) {
        if (getActivity() != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(title);
        }
    }
}
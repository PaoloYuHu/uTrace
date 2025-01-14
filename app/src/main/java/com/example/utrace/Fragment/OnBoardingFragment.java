package com.example.utrace.Fragment;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.utrace.Activity.LoginAndRegistration;
import com.example.utrace.R;

public class OnBoardingFragment extends Fragment {

    public OnBoardingFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_on_boarding, container, false);

        // Initialize the button
        Button next = view.findViewById(R.id.nextBt);

        // Set an OnClickListener to the button to close the fragment and load the login fragment
        next.setOnClickListener(v -> {
            if (getActivity() != null) {
                ((LoginAndRegistration) getActivity()).loadFragmentLogin();
            }
        });

        return view;
    }
}

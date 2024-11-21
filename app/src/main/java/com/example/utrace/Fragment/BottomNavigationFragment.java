package com.example.utrace.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.utrace.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class BottomNavigationFragment extends Fragment {

    private Fragment homeFragment;
    private Fragment missionsFragment;
    private Fragment usageFragment;
    private Fragment tipsFragment;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bottom_navigation, container, false);

        BottomNavigationView bottomNavigationView = view.findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {


            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;

                int itemId = item.getItemId();
                if (itemId == R.id.navigation_home) {
                    if (homeFragment == null) {
                        homeFragment = new HomeFragment();
                    }
                    selectedFragment = homeFragment;
                } else if (itemId == R.id.navigation_missions) {
                    if (missionsFragment == null) {
                        missionsFragment = new MissionsFragment();
                    }
                    selectedFragment = missionsFragment;
                } else if (itemId == R.id.navigation_usage) {
                    if (usageFragment == null) {
                        usageFragment = new AppUsageFragment();
                    }
                    selectedFragment = usageFragment;
                } else if (itemId == R.id.navigation_tips) {
                    if (tipsFragment == null) {
                        tipsFragment = new TipsFragment();
                    }
                    selectedFragment = tipsFragment;
                }

                if (selectedFragment != null) {
                    FragmentManager fragmentManager = getParentFragmentManager();  // Use getChildFragmentManager() if in a fragment
                    fragmentManager.beginTransaction()
                            .replace(R.id.MainContainer, selectedFragment)
                            .addToBackStack(null)
                            .commit();
                    return true;
                }

                return false;
            }


        });

        return view;
    }
}

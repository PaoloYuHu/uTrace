package com.example.utrace.Activity;

import android.annotation.SuppressLint;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.utrace.R;
import com.example.utrace.databinding.ActivityMainBinding;
import com.example.utrace.Fragment.SettingsFragment;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Apply night mode before setting the content view
        if (loadNightModeState()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        if (!hasUsageStatsPermission()) {
            Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
            startActivity(intent);
        }

        // Initialize user
        manageLoginAndRegistration();

        // Initialize the night mode switch based on the saved state
        @SuppressLint("UseSwitchCompatOrMaterialCode") Switch nightModeSwitch = findViewById(R.id.nightModeSwitch);
        nightModeSwitch.setChecked(loadNightModeState());

        // Set up the switch listener to toggle night mode
        nightModeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                }
                // Save the night mode state
                saveNightModeState(isChecked);
                // Restart the activity to apply the changes
                recreate();
            }
        });

        // Set up click listener for the settings button
        Button settingsButton = findViewById(R.id.settings);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceFragment(new SettingsFragment());
            }
        });
    }

    private boolean hasUsageStatsPermission() {
        UsageStatsManager usageStatsManager = (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);
        long now = System.currentTimeMillis();
        List<UsageStats> stats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, now - 1000 * 60 * 60 * 24, now);
        return stats != null && !stats.isEmpty();
    }

    private void manageLoginAndRegistration() {
        String name = getIntent().getStringExtra("name");
        String id = getIntent().getStringExtra("id");
        boolean localStorageIsEmpty = true;
        if (localStorageIsEmpty) {
            if (name == null && id == null) {
                Intent loginIntent = new Intent(MainActivity.this, LoginAndRegistration.class);
                MainActivity.this.startActivity(loginIntent);
                finish();
            } else {
                // Save the name and id in local storage so it won't ask for login again
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            // Find the LinearLayout
            LinearLayout profileMenu = findViewById(R.id.profile_menu);

            // Toggle visibility
            if (profileMenu.getVisibility() == View.VISIBLE) {
                profileMenu.setVisibility(View.GONE);
            } else {
                profileMenu.setVisibility(View.VISIBLE);
            }

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveNightModeState(boolean nightMode) {
        SharedPreferences sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("NightMode", nightMode);
        editor.apply();
    }

    private boolean loadNightModeState() {
        SharedPreferences sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE);
        return sharedPreferences.getBoolean("NightMode", false);
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.MainContainer, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}

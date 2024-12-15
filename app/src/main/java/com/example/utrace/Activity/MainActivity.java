package com.example.utrace.Activity;

import static com.example.utrace.utils.PermissionUtils.NOTIFICATION_PERMISSION_CODE;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.utrace.Fragment.HomeFragment;
import com.example.utrace.R;
import com.example.utrace.databinding.ActivityMainBinding;
import com.example.utrace.Fragment.SettingsFragment;
import com.example.utrace.notifications.MyNotificationChannel;
import com.example.utrace.notifications.NotificationReceiver;
import com.example.utrace.utils.PermissionUtils;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Calendar;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private String userName;
    private String email;
    private String userId;
    private int points;

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

        //app usage permission
        if (!hasUsageStatsPermission()) {
            Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
            startActivity(intent);
        }
        //notification permission
        if (PermissionUtils.hasNotificationPermission(this)) {
            String mes = "Pianta un albero: Aiuta a combattere il cambiamento climatico assorbendo CO2.";
            scheduleNotification("Green Tip", mes);
        } else {
            PermissionUtils.requestNotificationPermission(this);
        }


        FirebaseApp.initializeApp(this);
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
                LinearLayout profileMenu = findViewById(R.id.profile_menu);
                profileMenu.setVisibility(View.GONE);
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
        String extraName = getIntent().getStringExtra("name");
        String extraEmail = getIntent().getStringExtra("email");
        String extraId = getIntent().getStringExtra("userId");
        int extraPoints = getIntent().getIntExtra("points",0);

        SharedPreferences userPref = getSharedPreferences("user", MODE_PRIVATE);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            if(extraName != null && !extraName.isEmpty()){
                SharedPreferences.Editor editor = userPref.edit();
                userName = extraName;
                email = extraEmail;
                userId = extraId;
                points = extraPoints;
                editor.putString("userName",userName);
                editor.putString("email",email);
                editor.putString("userId",userId);
                editor.putInt("points", points);
                editor.apply();

                Log.d("MainActivity", "Registered user:"+userName);
            }else{
                userName = userPref.getString("userName", "failed");
                email = userPref.getString("email", "");
                userId = userPref.getString("userId", "");
                points = userPref.getInt("points", 0);
                Log.d("MainActivity", "Logged user:"+userName);
            }
        }else{
            Intent loginIntent = new Intent(MainActivity.this, LoginAndRegistration.class);
            MainActivity.this.startActivity(loginIntent);
            finish();
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
            LinearLayout profileMenu = findViewById(R.id.profile_menu);

            // Toggle visibility
            if (profileMenu.getVisibility() == View.VISIBLE) {
                profileMenu.setVisibility(View.GONE);
            } else {
                profileMenu.setVisibility(View.VISIBLE);
                // Imposta le informazioni nei TextView del profilo
                TextView userNameTV = findViewById(R.id.userName);
                userNameTV.setText(userName);
                TextView pointsTV = findViewById(R.id.userScore);
                pointsTV.setText("Points: " + points + "!");
                TextView rankTV = findViewById(R.id.userRanking);
                rankTV.setText("rank da impl");
            }

            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void saveNightModeState(boolean nightMode) {
        SharedPreferences sharedPreferences = getSharedPreferences("modePrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("NightMode", nightMode);
        editor.apply();
    }

    private boolean loadNightModeState() {
        SharedPreferences sharedPreferences = getSharedPreferences("modePrefs", MODE_PRIVATE);
        return sharedPreferences.getBoolean("NightMode", false);
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.MainContainer, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }


    private void scheduleNotification(String title, String message) {
        MyNotificationChannel.createNotificationChannel(this);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.MINUTE, 1);  // Set your desired time

        // Create an intent to trigger the NotificationReceiver
        Intent intent = new Intent(this, NotificationReceiver.class);
        intent.putExtra("notification_title", title);
        intent.putExtra("notification_message", message);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // Set up the notification with the PendingIntent
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        if (alarmManager != null) {
            long alarmTime = calendar.getTimeInMillis();
            long windowLengthMillis = 60000L; // 1 minute window, adjust as needed

            alarmManager.setWindow(AlarmManager.RTC_WAKEUP, alarmTime, windowLengthMillis, pendingIntent);
        }
    }




    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == NOTIFICATION_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                String mes = "Pianta un albero: Aiuta a combattere il cambiamento climatico assorbendo CO2.";
                scheduleNotification("Green Tip", mes);
            } else {
                Log.d("MainActivity", "Notification permission denied"); // Optionally, show a message to the user
                Toast.makeText(this, "Permesso per notifiche rifiutata. Non riceverai notifiche.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
package com.example.utrace.Fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.utrace.R;

public class BatteryFragment extends Fragment {

    private TextView batteryInfo;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_battery, container, false);
        setToolbarTitle("Info Batteria");

        batteryInfo = view.findViewById(R.id.batteryInfo);

        // Register the BroadcastReceiver to listen for battery status changes
        requireActivity().registerReceiver(batteryInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

        return view;
    }

    private final BroadcastReceiver batteryInfoReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Retrieve battery status
            int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
            String statusString = getStatusString(status);

            // Retrieve battery health
            int health = intent.getIntExtra(BatteryManager.EXTRA_HEALTH, -1);
            String healthString = getHealthString(health);

            // Retrieve battery percentage
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            int batteryPct = (int) ((level / (float) scale) * 100);

            // Retrieve battery voltage
            int voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1);

            // Retrieve battery temperature
            int temperature = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1);

            // Retrieve battery technology
            String technology = intent.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY);

            // Retrieve battery charge counter
            BatteryManager batteryManager = (BatteryManager) context.getSystemService(Context.BATTERY_SERVICE);
            int chargeCounter = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER);

            // Retrieve battery capacity using PowerProfile
            int batteryCapacity = getBatteryCapacity(context);

            int cycles = chargeCounter/batteryCapacity;

            // Format the battery information
            String batteryDetails = String.format("Status: %s\nHealth: %s\nPercentage: %d%%\nVoltage: %d mV\nTemperature: %.1f°C\nTechnology: %s\nCicli di ricarica: %d\nCapacity: %d mAh",
                    statusString, healthString, batteryPct, voltage, temperature / 10.0, technology, cycles, batteryCapacity);

            // Display battery information
            batteryInfo.setText(batteryDetails);
        }
    };

    private String getStatusString(int status) {
        switch (status) {
            case BatteryManager.BATTERY_STATUS_CHARGING:
                return "Charging";
            case BatteryManager.BATTERY_STATUS_DISCHARGING:
                return "Discharging";
            case BatteryManager.BATTERY_STATUS_FULL:
                return "Full";
            case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
                return "Not Charging";
            default:
                return "Unknown";
        }
    }

    private String getHealthString(int health) {
        switch (health) {
            case BatteryManager.BATTERY_HEALTH_GOOD:
                return "Good";
            case BatteryManager.BATTERY_HEALTH_OVERHEAT:
                return "Overheat";
            case BatteryManager.BATTERY_HEALTH_DEAD:
                return "Dead";
            case BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE:
                return "Over Voltage";
            case BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE:
                return "Unspecified Failure";
            case BatteryManager.BATTERY_HEALTH_COLD:
                return "Cold";
            default:
                return "Unknown";
        }
    }

    private int getBatteryCapacity(Context context) {
        Object mPowerProfile_;
        double batteryCapacity = 0.0;
        final String POWER_PROFILE_CLASS = "com.android.internal.os.PowerProfile";
        try {
            mPowerProfile_ = Class.forName(POWER_PROFILE_CLASS)
                    .getConstructor(Context.class).newInstance(context);
            batteryCapacity = (Double) Class.forName(POWER_PROFILE_CLASS)
                    .getMethod("getAveragePower", String.class)
                    .invoke(mPowerProfile_, "battery.capacity");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (int) batteryCapacity;  // Convert double to int for compatibility
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        requireActivity().unregisterReceiver(batteryInfoReceiver);
    }

    private void setToolbarTitle(String title) {
        if (getActivity() != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(title);
        }
    }
}

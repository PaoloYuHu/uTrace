package com.example.utrace.Adapter;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.utrace.Model.AppModel;
import com.example.utrace.R;
import com.example.utrace.utils.FormatHelper;

import java.util.List;

public class AppsListAdapter extends RecyclerView.Adapter<AppsListAdapter.AppsListViewHolder> {

    private Activity mActivity;
    private List<AppModel> mAppsList;

    public AppsListAdapter(Activity mActivity, List<AppModel> mAppsList) {
        this.mActivity = mActivity;
        this.mAppsList = mAppsList;
    }

    @NonNull
    @Override
    public AppsListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AppsListViewHolder(LayoutInflater.from(mActivity).inflate(R.layout.app_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull AppsListViewHolder holder, int position) {
        AppModel app = mAppsList.get(position);
        holder.appName.setText(app.getAppName());
        holder.appPackageName.setText(app.getPackageName());
        holder.dataUsage.setText("Down: " + FormatHelper.bytesToString(app.getRxBytes()) + " Up: " + FormatHelper.bytesToString(app.getTxBytes()));

        try {
            Drawable appIcon = mActivity.getPackageManager().getApplicationIcon(app.getPackageName());
            holder.appIcon.setImageDrawable(appIcon);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return mAppsList.size();
    }

    public class AppsListViewHolder extends RecyclerView.ViewHolder {
        private TextView appName, appPackageName, dataUsage;
        private ImageView appIcon;

        public AppsListViewHolder(@NonNull View itemView) {
            super(itemView);
            appName = itemView.findViewById(R.id.app_name);
            appPackageName = itemView.findViewById(R.id.app_package);
            dataUsage = itemView.findViewById(R.id.data_usage);
            appIcon = itemView.findViewById(R.id.app_icon);
        }
    }
}

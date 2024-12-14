package com.example.utrace.Fragment;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.utrace.Adapter.MissionsAdapter;
import com.example.utrace.Model.MissionModel;
import com.example.utrace.R;
import com.example.utrace.db.Mission;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MissionsFragment extends Fragment {

    private static final String TAG = "MissionsFragment";
    private View view;
    private RecyclerView recyclerView;
    private MissionsAdapter myAdapter;
    private List<MissionModel> items = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_missions, container, false);

        setToolbarTitle("Missions");

        // Find the RecyclerView
        recyclerView = view.findViewById(R.id.missions_list);

        fetchMissions();

        // Set up the LayoutManager for the RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        myAdapter = new MissionsAdapter(getContext(),items);
        recyclerView.setAdapter(myAdapter);


        Button button = view.findViewById(R.id.prizes_button);
        button.setOnClickListener(v -> {
            // Manual navigation to PrizesFragment
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.MainContainer, new PrizesFragment())
                    .addToBackStack(null)
                    .commit();
        });

        return view;
    }

    private void fetchMissions() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("missions")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        Log.d(TAG, "Successfully retrieved missions");

                        List<Mission> dbMissions = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Mission mission = document.toObject(Mission.class);
                            if (mission != null) {
                                updateMissionDate(mission, document.getId());
                                dbMissions.add(mission);
                            } else {
                                Log.d(TAG, "Mission is null");
                            }
                        }
                        // Populate items and update adapter
                        items.clear();
                        items.addAll(populateMissionItems(dbMissions));
                        myAdapter.notifyDataSetChanged();
                    } else {
                        Log.d(TAG, "Failed to retrieve missions", task.getException());
                    }
                });
    }

    private List<MissionModel> populateMissionItems(List<Mission> dbMissions) {
        List<MissionModel> items = new ArrayList<>();
        SharedPreferences userPref = requireActivity().getSharedPreferences("user", MODE_PRIVATE);
        Set<String> completedMissionsSet = userPref.getStringSet("completedMissions", new HashSet<>());
        List<String> completedMissionsList = new ArrayList<>(completedMissionsSet);

        Log.d(TAG, "Completed Missions List: " + completedMissionsList);

        for (Mission m : dbMissions) {
            if (!completedMissionsList.contains(m.getId())) {
                items.add(new MissionModel(m.getId(), m.getTitle(), m.getDescription(), m.getRepeat(), m.getType(), R.drawable.utrace_miniature_logo));
            } else {
                Log.d(TAG, "Mission already completed: " + m.getId());
            }
        }
        Log.d(TAG, "Final Items List Size: " + items.size());
        return items;
    }

    private void updateMissionDate(Mission mission, String documentId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Timestamp startDate = mission.getStartDate();
        Date currentDate = new Date();

        if (startDate == null) {
            Log.d(TAG, documentId + " start date is null");
            return;
        }

        Date missionStartDate = resetTimeToMidnight(startDate.toDate());
        Log.d(TAG, "Current Date: " + currentDate);
        Log.d(TAG, "Mission Start Date: " + missionStartDate);

        if (mission.getRepeat().equals("day") && currentDate.after(addDays(missionStartDate, 1))) {
            mission.setStartDate(new Timestamp(resetTimeToMidnight(currentDate)));
            db.collection("missions").document(documentId).set(mission)
                    .addOnSuccessListener(aVoid -> Log.d(TAG, documentId + " daily start date updated"))
                    .addOnFailureListener(e -> Log.d(TAG, "Error updating daily start date for " + documentId, e));
            removeCompleted(documentId);
        } else if (mission.getRepeat().equals("week") && currentDate.after(addDays(missionStartDate, 7))) {
            mission.setStartDate(new Timestamp(resetTimeToMidnight(currentDate)));
            db.collection("missions").document(documentId).set(mission)
                    .addOnSuccessListener(aVoid -> Log.d(TAG, documentId + " weekly start date updated"))
                    .addOnFailureListener(e -> Log.d(TAG, "Error updating weekly start date for " + documentId, e));
            removeCompleted(documentId);
        } else {
            Log.d(TAG, documentId + " start date is current");
        }
    }

    private Date resetTimeToMidnight(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    private Date addDays(Date date, int days) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_YEAR, days);
        return calendar.getTime();
    }

    private void removeCompleted(String id){
        SharedPreferences userPref = requireActivity().getSharedPreferences("user", MODE_PRIVATE);
        Set<String> completedMissionsSet = new HashSet<>(userPref.getStringSet("completedMissions", new HashSet<>()));
        completedMissionsSet.remove(id);
        SharedPreferences.Editor editor = userPref.edit();
        editor.putStringSet("completedMissions", completedMissionsSet);
        editor.apply();
    }

    private void addCompleted(String id){
        SharedPreferences userPref = requireActivity().getSharedPreferences("user", MODE_PRIVATE);
        Set<String> completedMissionsSet = new HashSet<>(userPref.getStringSet("completedMissions", new HashSet<>()));
        completedMissionsSet.add(id);
        SharedPreferences.Editor editor = userPref.edit();
        editor.putStringSet("completedMissions", completedMissionsSet);
        editor.apply();
    }


    private void setToolbarTitle(String title) {
        if (getActivity() != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(title);
        }
    }
}

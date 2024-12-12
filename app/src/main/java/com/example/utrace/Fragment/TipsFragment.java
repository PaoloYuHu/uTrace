package com.example.utrace.Fragment;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.utrace.Model.Tip;
import com.example.utrace.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class TipsFragment extends Fragment {

    private View view;
    private TextView title;
    private TextView tip;
    private Button tipsBtn;
    private List<Tip> tipList;
    private int currentTipIndex = 0;

    public TipsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tipList = new ArrayList<>();
        fetchTipsFromFirestore();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_tips, container, false);

        setToolbarTitle("Green Tips");

        // Initialize views
        title = view.findViewById(R.id.tip_title);
        tip = view.findViewById(R.id.tip);
        tipsBtn = view.findViewById(R.id.next);

        // Set button click listener to update the tip
        tipsBtn.setOnClickListener(v -> changeTip());

        return view;
    }

    private void fetchTipsFromFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("tips")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        tipList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Tip tip = document.toObject(Tip.class);
                            tipList.add(tip);
                        }
                        if (!tipList.isEmpty()) {
                            showTip(0);
                        }
                    } else {
                        Toast.makeText(getContext(), "Errore nel recupero dei tips", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showTip(int index) {
        if (!tipList.isEmpty() && index < tipList.size()) {
            Tip currentTip = tipList.get(index);
            title.setText(currentTip.getTitle());
            tip.setText(currentTip.getTip());
        }
    }

    private void changeTip() {
        // Increment the tip index
        currentTipIndex = (currentTipIndex + 1) % tipList.size();

        // Update the TextView with the next tip
        showTip(currentTipIndex);
    }

    private void setToolbarTitle(String title) {
        if (getActivity() != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(title);
        }
    }
}

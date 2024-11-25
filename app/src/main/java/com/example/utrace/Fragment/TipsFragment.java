package com.example.utrace.Fragment;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.utrace.R;

public class TipsFragment extends Fragment {

    private View view;
    private TextView title;
    private TextView tip;
    private Button tipsBtn;
    private String[] tips;
    private String[] titles;
    private int currentTipIndex = 0;

    public TipsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize tips
        tips = new String[]{
                "Pianta un albero: Aiuta a combattere il cambiamento climatico assorbendo CO2.",
                "Partecipa a iniziative ecologiche: Unisciti a giornate di pulizia o campagne di sensibilizzazione.",
                "Usa energia verde: Passa a fornitori di energia rinnovabile.",
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA     PROVA SCROLL    AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
        };

        // Initialize titles
        titles = new String[]{
             "Fai la tua parte!",
             "Spirito di iniziativa.",
             "Green Energy.",
             "Tip Title"
        };
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

        // Set the first tip
        title.setText(titles[currentTipIndex]);
        tip.setText(tips[currentTipIndex]);

        // Set button click listener to update the tip
        tipsBtn.setOnClickListener(v -> changeTip());

        return view;
    }

    private void changeTip() {
        // Increment the tip index
        currentTipIndex = (currentTipIndex + 1) % tips.length;

        // Update the TextView with the next tip
        title.setText(titles[currentTipIndex]);
        tip.setText(tips[currentTipIndex]);
    }


    private void setToolbarTitle(String title) {
        if (getActivity() != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(title);
        }
    }
}

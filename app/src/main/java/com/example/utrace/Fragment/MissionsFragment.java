package com.example.utrace.Fragment;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.utrace.Adapter.MissionsAdapter;
import com.example.utrace.Model.Mission;
import com.example.utrace.R;

import java.util.ArrayList;
import java.util.List;

public class MissionsFragment extends Fragment {

    private static final String TAG = "MissionsFragment";
    private View view;
    private RecyclerView recyclerView;
    private MissionsAdapter myAdapter;

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

        // Trova la RecyclerView
        recyclerView = view.findViewById(R.id.missions_list);

        // Configura il LayoutManager per la RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Crea i dati da visualizzare
        List<Mission> items = new ArrayList<>();
        items.add(new Mission("Missione 1", "Completa 10 attività.", 20, R.drawable.utrace_miniature_logo));
        items.add(new Mission("Missione 2", "Raggiungi il livello 5.", 30, R.drawable.utrace_miniature_logo));
        items.add(new Mission("Missione 3", "Ottieni un punteggio di 1000.", 40, R.drawable.utrace_miniature_logo));
        items.add(new Mission("Missione 4", "Partecipa a 3 eventi.", 70, R.drawable.utrace_miniature_logo));
        items.add(new Mission("Missione 5", "Invita un amico al gioco.", 90, R.drawable.utrace_miniature_logo));
        items.add(new Mission("Missione 6", "Completa tutte le missioni.", 100, R.drawable.utrace_miniature_logo));
        items.add(new Mission("Missione 7", "Completa tutte le missioni.", 100, R.drawable.utrace_miniature_logo));
        items.add(new Mission("Missione 8", "Completa tutte le missioni.", 100, R.drawable.utrace_miniature_logo));
        items.add(new Mission("Missione 9", "Completa tutte le missioni.", 100, R.drawable.utrace_miniature_logo));
        items.add(new Mission("Missione 10", "Completa tutte le missioni.", 100, R.drawable.utrace_miniature_logo));

        // Inizializza l'Adapter con i dati
        myAdapter = new MissionsAdapter(items);

        // Assegna l'Adapter alla RecyclerView
        recyclerView.setAdapter(myAdapter);

        return view;
    }

    private void setToolbarTitle(String title) {
        if (getActivity() != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(title);
        }
    }
}
package com.example.utrace.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.utrace.Activity.MainActivity;
import com.example.utrace.databinding.FragmentLoginBinding;

import com.example.utrace.R;
import com.example.utrace.db.AuthRepository;
import com.example.utrace.db.UserRepository;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;

public class LoginFragment extends Fragment {

    private FragmentLoginBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentLoginBinding.inflate(inflater, container, false);

        binding.LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserRepository userRepository = new UserRepository();
                AuthRepository authRepository = new AuthRepository();

                String email = binding.editTextEmail.getText().toString();
                String password = binding.editTextPassword.getText().toString();
                if(email.isEmpty() || password.isEmpty()){
                    Toast.makeText(getActivity(), "Completa tutti i campi", Toast.LENGTH_SHORT).show();
                    return;
                }

                authRepository.login(email, password, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = authRepository.getCurrentUser();
                        if (firebaseUser != null) {
                            String userId = firebaseUser.getUid();
                            String fireEmail = firebaseUser.getEmail();
                            userRepository.getUserData(userId, userDataTask -> {
                                if (userDataTask.isSuccessful()) {
                                    DocumentSnapshot document = userDataTask.getResult();
                                    if (document.exists()) {
                                        String retrievedUsername = document.getString("username");
                                        Long retrievedPointsL = document.getLong("points");
                                        int retrievedPoints = retrievedPointsL != null ? retrievedPointsL.intValue() : 0;
                                        SharedPreferences userPref = requireActivity().getSharedPreferences("user", Context.MODE_PRIVATE);
                                        SharedPreferences.Editor editor = userPref.edit();
                                        editor.putString("userName", retrievedUsername);
                                        editor.putString("email", fireEmail);
                                        editor.putInt("points", retrievedPoints);
                                        editor.putString("userId", userId);
                                        editor.apply();

                                        Intent mainIntent = new Intent(getActivity(), MainActivity.class);
                                        mainIntent.putExtra("name", retrievedUsername);
                                        mainIntent.putExtra("email", fireEmail);
                                        mainIntent.putExtra("points", retrievedPoints);
                                        requireActivity().startActivity(mainIntent);
                                        requireActivity().finish();
                                    } else {
                                        Toast.makeText(getActivity(), "Utente non trovato", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(getActivity(), "Errore nel recupero delle informazioni utente: " + userDataTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            Toast.makeText(getActivity(), "Errore nell'ottenere l'utente corrente", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getActivity(), "Login fallito: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        // Gestisci click del bottone per portare al form di registrazione
        binding.RegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Naviga al RegisterFragment
                FragmentManager fragmentManager = getParentFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, new RegistrationFragment());
                fragmentTransaction.addToBackStack(null); // Aggiunge alla backstack
                fragmentTransaction.commit();
            }
        });

        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

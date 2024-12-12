package com.example.utrace.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.utrace.Activity.MainActivity;
import com.example.utrace.R;
import com.example.utrace.databinding.FragmentRegistrationBinding;
import com.example.utrace.db.AuthRepository;
import com.example.utrace.db.UserRepository;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;


public class RegistrationFragment extends Fragment {

    private FragmentRegistrationBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentRegistrationBinding.inflate(inflater, container, false);

        binding.RegisterButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                UserRepository userRepository = new UserRepository();
                AuthRepository authRepository = new AuthRepository();

                String email = binding.editTextEmail.getText().toString();
                String password = binding.editTextPassword.getText().toString();
                String passwordCheck= binding.editTextConfirmPassword.getText().toString();
                String username = binding.editTextUsername.getText().toString();
                int points = 0;
                if(email.isEmpty() || password.isEmpty() || passwordCheck.isEmpty() || username.isEmpty()){
                    Toast.makeText(getActivity(), "Completa tutti i campi", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!password.equals(passwordCheck)){
                    Toast.makeText(getActivity(), "Le password non combaciano", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Creare un nuovo utente
                userRepository.createUser(email, password, username, points, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = authRepository.getCurrentUser();
                        if (firebaseUser != null) {
                            userRepository.saveUserData(firebaseUser, username, points);

                            SharedPreferences userPref = requireActivity().getSharedPreferences("user", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = userPref.edit();
                            editor.putBoolean("isLogged", true);
                            editor.apply();

                            Intent mainIntent = new Intent(getActivity(), MainActivity.class);
                            mainIntent.putExtra("name", username);
                            mainIntent.putExtra("email",email);
                            mainIntent.putExtra("points", 0);
                            requireActivity().startActivity(mainIntent);
                            requireActivity().finish();
                        }
                    } else {
                        Toast.makeText(getActivity(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

        // Gestisci click del bottone per portare al login
        binding.LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Naviga al RegisterFragment
                FragmentManager fragmentManager = getParentFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, new LoginFragment());
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });


        return binding.getRoot();
    }

    public  void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


}
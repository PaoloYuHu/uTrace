package com.example.utrace.Fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.utrace.Activity.LoginAndRegistration;
import com.example.utrace.Activity.MainActivity;
import com.example.utrace.databinding.FragmentLoginBinding;

import com.example.utrace.R;

import java.util.Objects;


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
                // Gestisci il click del bottone di login
                Intent mainIntent = new Intent(getActivity(), MainActivity.class);
                mainIntent.putExtra("name", "ciaone");
                mainIntent.putExtra("id","1234");
                requireActivity().startActivity(mainIntent);
                requireActivity().finish();
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
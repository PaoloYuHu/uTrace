package com.example.utrace.Fragment;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.utrace.Activity.MainActivity;
import com.example.utrace.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangeEmailFragment extends Fragment {

    private View view;

    public static ChangeEmailFragment newInstance(String param1, String param2) {
        ChangeEmailFragment fragment = new ChangeEmailFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_change_email, container, false);

        EditText newEmailText = view.findViewById(R.id.newMail);
        EditText confirmText = view.findViewById(R.id.confirmNewMail);
        Button confirmBt = view.findViewById(R.id.confirmBt);

        confirmBt.setOnClickListener(v -> {
            String newEmail = newEmailText.getText().toString();
            String newEmailC = confirmText.getText().toString();
            if (!newEmail.isEmpty() && !newEmailC.isEmpty() && newEmail.equals(newEmailC)) {
                updateUserEmail(newEmail);
            } else {
                Toast.makeText(requireContext(), "Please enter a valid email", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private void updateUserEmail(String newEmail) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            user.verifyBeforeUpdateEmail(newEmail)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            SharedPreferences userPref = requireActivity().getSharedPreferences("user", MODE_PRIVATE);
                            SharedPreferences.Editor editor = userPref.edit();
                            editor.putString("email",newEmail);
                            editor.apply();
                            Intent mainIntent = new Intent(getActivity(), MainActivity.class);
                            requireActivity().startActivity(mainIntent);
                            requireActivity().finish();
                            Toast.makeText(requireContext(), "Verification email sent to update your email address.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(requireContext(), "Error sending verification email.", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(requireContext(), "User is not authenticated", Toast.LENGTH_SHORT).show();
        }
    }
}

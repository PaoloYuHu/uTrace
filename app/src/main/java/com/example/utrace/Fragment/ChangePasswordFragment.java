package com.example.utrace.Fragment;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.utrace.R;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePasswordFragment extends Fragment {

    private View view;

    public static ChangePasswordFragment newInstance(String param1, String param2) {
        ChangePasswordFragment fragment = new ChangePasswordFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_change_password, container, false);

        EditText currentPasswordText = view.findViewById(R.id.currentPassword);
        EditText newPasswordText = view.findViewById(R.id.newPassword);
        EditText confirmNewPasswordText = view.findViewById(R.id.confirmNewPassword);
        Button updatePasswordButton = view.findViewById(R.id.confirmBt);

        updatePasswordButton.setOnClickListener(v -> {
            String currentPassword = currentPasswordText.getText().toString();
            String newPassword = newPasswordText.getText().toString();
            String confirmNewPassword = confirmNewPasswordText.getText().toString();

            if (!currentPassword.isEmpty() && !newPassword.isEmpty() && !confirmNewPassword.isEmpty() && newPassword.equals(confirmNewPassword)) {
                updateUserPassword(currentPassword, newPassword);
            } else {
                Toast.makeText(requireContext(), "Please enter and confirm a valid password", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private void updateUserPassword(String currentPassword, String newPassword) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            String email = user.getEmail();
            AuthCredential credential = EmailAuthProvider.getCredential(email, currentPassword);

            user.reauthenticate(credential).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    user.updatePassword(newPassword).addOnCompleteListener(updateTask -> {
                        if (updateTask.isSuccessful()) {
                            Toast.makeText(requireContext(), "Password updated successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(requireContext(), "Error updating password", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(requireContext(), "Re-authentication failed", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(requireContext(), "User is not authenticated", Toast.LENGTH_SHORT).show();
        }
    }
}

package com.example.utrace.Fragment;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.util.Log;
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
import com.google.firebase.firestore.FirebaseFirestore;

public class ChangeUserFragment extends Fragment {

    private View view;

    public static ChangeUserFragment newInstance(String param1, String param2) {
        ChangeUserFragment fragment = new ChangeUserFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_change_user, container, false);

        EditText newUsernameText = view.findViewById(R.id.newUsername);
        EditText confirmUsernameText = view.findViewById(R.id.confirmNewUsername);
        Button updateUsernameButton = view.findViewById(R.id.confirmBt);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userId = user.getUid();
        Log.d("Change", "userid" + userId);
        updateUsernameButton.setOnClickListener(v -> {
            String newUsername = newUsernameText.getText().toString();
            String confirmUsername = confirmUsernameText.getText().toString();
            if (!newUsername.isEmpty() && !confirmUsername.isEmpty() && newUsername.equals(confirmUsername)) {
                updateUserUsername(userId, newUsername);
            } else {
                Toast.makeText(requireContext(), "Please enter and confirm a valid username", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private void updateUserUsername(String userId, String newUsername) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(userId)
                .update("username", newUsername)
                .addOnSuccessListener(aVoid -> {
                    SharedPreferences userPref = requireActivity().getSharedPreferences("user", MODE_PRIVATE);
                    SharedPreferences.Editor editor = userPref.edit();
                    editor.putString("userName",newUsername);
                    editor.apply();
                    Intent mainIntent = new Intent(getActivity(), MainActivity.class);
                    requireActivity().startActivity(mainIntent);
                    requireActivity().finish();
                    Toast.makeText(requireContext(), "Username updated successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "Error updating username", Toast.LENGTH_SHORT).show();
                });
    }
}

package com.example.utrace.Fragment;

import static android.content.Context.MODE_PRIVATE;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.utrace.Activity.MainActivity;
import com.example.utrace.R;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseUserMetadata;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;

public class SettingsFragment extends Fragment {

    private static final String TAG = "SettingsFragment";

    private View view;
    private CardView logoutButton;
    private CardView usernameButton;
    private CardView emailButton;
    private CardView passwordButton;
    private CardView inviteButton;
    private CardView deleteButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setToolbarTitle("Settings");

        view = inflater.inflate(R.layout.fragment_settings, container, false);

        setUserCard();

        logoutButton = view.findViewById(R.id.logoutBt);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                SharedPreferences userPref = requireActivity().getSharedPreferences("user", MODE_PRIVATE);
                SharedPreferences.Editor editor = userPref.edit();
                editor.clear();
                editor.apply();
                Intent mainIntent = new Intent(getActivity(), MainActivity.class);
                requireActivity().startActivity(mainIntent);
                requireActivity().finish();
            }
        });

        emailButton = view.findViewById(R.id.emailBt);
        emailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getParentFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.MainContainer, new ChangeEmailFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });

        usernameButton = view.findViewById(R.id.usernameBt);
        usernameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getParentFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.MainContainer, new ChangeUserFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });

        passwordButton = view.findViewById(R.id.passwordBt);
        passwordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getParentFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.MainContainer, new ChangePasswordFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });

        inviteButton = view.findViewById(R.id.inviteBt);
        inviteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                copyToClipboard("Scarica uTrace!!");
            }
        });

        deleteButton = view.findViewById(R.id.deleteBt);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteConfirmationDialog();
            }
        });

        return view;
    }

    private void showDeleteConfirmationDialog() {
        TextView password = view.findViewById(R.id.passwordDelete);
        if(password.getVisibility() == View.GONE){
            password.setVisibility(View.VISIBLE);
            Toast.makeText(requireContext(), "Inserisci password per continuare", Toast.LENGTH_SHORT).show();
        } else {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Delete Account")
                    .setMessage("Are you sure you want to delete your account? This action cannot be undone.")
                    .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            deleteUserAccount();
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        }
    }

    private void deleteUserAccount() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        TextView passwordTV = view.findViewById(R.id.passwordDelete);
        String password = passwordTV.getText().toString();

        if (user != null) {
            String email = user.getEmail();
            if (email != null && !password.isEmpty()) {
                AuthCredential credential = EmailAuthProvider.getCredential(email, password);
                user.reauthenticate(credential).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        user.delete().addOnCompleteListener(deleteTask -> {
                            if (deleteTask.isSuccessful()) {
                                deleteUserFromFirestore(user.getUid());
                            } else {
                                Toast.makeText(requireContext(), "Error deleting user", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        Toast.makeText(requireContext(), "Re-authentication failed", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(requireContext(), "Email or password not provided", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void deleteUserFromFirestore(String userId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(userId).delete().addOnSuccessListener(aVoid -> {
            Toast.makeText(requireContext(), "Account deleted successfully", Toast.LENGTH_SHORT).show();
            SharedPreferences userPref = requireActivity().getSharedPreferences("user", MODE_PRIVATE);
            SharedPreferences.Editor editor = userPref.edit();
            editor.clear();
            editor.apply();
            Intent mainIntent = new Intent(getActivity(), MainActivity.class);
            requireActivity().startActivity(mainIntent);
            requireActivity().finish();
        }).addOnFailureListener(e -> {
            Toast.makeText(requireContext(), "Error deleting user data from Firestore", Toast.LENGTH_SHORT).show();
        });
    }

    private void copyToClipboard(String text) {
        ClipboardManager clipboard = (ClipboardManager) requireContext().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Copied Text", text);
        if (clipboard != null) {
            clipboard.setPrimaryClip(clip);
            Toast.makeText(requireContext(), "Messaggio copiato", Toast.LENGTH_SHORT).show();
        } else {
            Log.e(TAG, "ClipboardManager is null");
        }
    }

    private void setUserCard(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        SharedPreferences userPref = requireActivity().getSharedPreferences("user", MODE_PRIVATE);
        String userName = userPref.getString("userName", "");;
        TextView uUser = view.findViewById(R.id.uUser);
        uUser.setText("Ciao "+ userName + "!");
        TextView uEmail = view.findViewById(R.id.uEmail);
        uEmail.setText("Email: "+ user.getEmail());
        if (user != null) {
            FirebaseUserMetadata metadata = user.getMetadata();
            if (metadata != null) {
                long creationTimestamp = metadata.getCreationTimestamp();
                Date creationDate = new Date(creationTimestamp);
                TextView uCreation = view.findViewById(R.id.uCreation);
                uCreation.setText("Creato: "+ creationDate);
            } else {
                Toast.makeText(requireContext(), "User metadata not available", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show();
        }

    }

    private void setToolbarTitle(String title) {
        if (getActivity() != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(title);
        }
    }
}

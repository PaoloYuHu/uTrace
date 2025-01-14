package com.example.utrace.db;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.HashMap;
import java.util.Map;

public class UserRepository {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    public UserRepository() {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    public void createUser(String email, String password, String username, int points, OnCompleteListener<AuthResult> listener) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(listener);
    }

    public void saveUserData(FirebaseUser firebaseUser, String username, int points) {
        String userId = firebaseUser.getUid();
        Map<String, Object> user = new HashMap<>();
        user.put("username", username);
        user.put("points", points);

        db.collection("users").document(userId)
                .set(user)
                .addOnSuccessListener(aVoid -> {
                    // Informazioni utente salvate con successo
                })
                .addOnFailureListener(e -> {
                    // Errore nel salvataggio delle informazioni utente
                });
    }

    public void getUserData(String userId, OnCompleteListener<DocumentSnapshot> listener) {
        db.collection("users").document(userId)
                .get()
                .addOnCompleteListener(listener);
    }

    // New method to retrieve user ranking
    public void getUserRanking(String userId, RankingCallback callback) {
        db.collection("users")
                .orderBy("points", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        int position = 1;
                        for (DocumentSnapshot document : task.getResult()) {
                            if (document.getId().equals(userId)) {
                                callback.onRankingDetermined(position);
                                return;
                            }
                            position++;
                        }
                        // User not found in the list
                        callback.onRankingDetermined(-1);
                    } else {
                        // Handle failure
                        callback.onRankingDetermined(-1);
                    }
                });
    }

    // Callback interface for ranking result
    public interface RankingCallback {
        void onRankingDetermined(int position);
    }
}


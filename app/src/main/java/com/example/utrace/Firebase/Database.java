package com.example.utrace.Firebase;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Database {

    private final FirebaseFirestore db;

    public Database(){
        this.db = FirebaseFirestore.getInstance();
    }

    public void addUser(String userId, String username, String email, String password){
        Map<String, Object> user = new HashMap<>();
        user.put("username", username);
        user.put("email", email);
        user.put("password", password);

        db.collection("utenti").document(userId)
                .set(user)
                .addOnSuccessListener(a -> System.out.println("aggiunto utente!"))
                .addOnFailureListener(a -> System.out.println("NON aggiunto utente!"));
    }

    public void addSomething(String key, String value){

        System.out.println("provo ad aggiungere qualcosa...");

        db.collection("oggetti").document(key)
                .set(Map.of(key, value))
                .addOnSuccessListener(a -> System.out.println("aggiunto qualcosa!"))
                .addOnFailureListener(a -> System.out.println("NON aggiunto qualcosa!"));

    }

}


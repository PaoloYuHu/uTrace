package com.example.utrace.database;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.core.Context;

import java.util.HashMap;
import java.util.Map;

public class Database {
    private static final String DATABASE_URL = "YOUR_DB_URL"; //TODO qual'é????
    private FirebaseDatabase database;
    private final DatabaseReference tipsRef;

    public Database(){

        FirebaseOptions options = new FirebaseOptions.Builder()
               // .setCredentials(GoogleCredentials.fromStream(getClass().getResourceAsStream("C:\\Users\\alessio\\StudioProjects\\uTrace\\utracefirebase-firebase-adminsdk-48u5z-15a00558b8.json")))
                .setDatabaseUrl(DATABASE_URL)
                .build();


//        FirebaseApp.initializeApp(options);


        database = FirebaseDatabase.getInstance();
        tipsRef = database.getReference(Tables.TIPS);
    }
    public FirebaseDatabase getFirebaseDatabase() {
        return database;
    }

    public void insertTip(Tip tip){

        System.out.println("inserendo una tip...");

        Map<String, Tip> entry = Map.of(tip.getId(), tip);

        tipsRef.setValue(entry).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                System.out.println("Tip saved successfully.");
            } else {
                System.err.println("Failed to save Tip.");

                System.out.println("Tip NOT saved successfully.");
            }
        });
    }
}

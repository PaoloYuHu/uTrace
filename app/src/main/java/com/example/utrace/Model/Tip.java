package com.example.utrace.Model;

public class Tip {
    private String title;
    private String tip;

    public Tip() {
        //Empty constructor required for Firestore
    }

    public Tip(String title, String tip) {
        this.title = title;
        this.tip = tip;
    }

    public String getTitle() {
        return title;
    }

    public String getTip() {
        return tip;
    }
}


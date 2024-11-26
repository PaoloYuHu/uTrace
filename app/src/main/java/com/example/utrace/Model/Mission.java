package com.example.utrace.Model;

public class Mission {
    private String title;
    private String description;
    private int percentage;
    private int icon;

    public Mission(String title, String description, int percentage, int icon) {
        this.title = title;
        this.description = description;
        this.percentage = percentage;
        this.icon = icon;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getPercentage() {
        return percentage;
    }

    public int getIcon() {
        return icon;
    }
}

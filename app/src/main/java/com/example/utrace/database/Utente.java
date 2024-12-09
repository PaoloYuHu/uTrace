package com.example.utrace.database;

import java.util.ArrayList;
import java.util.List;

public class Utente {

    private String id;
    private String username;
    private String email;
    private String password;
    private List<Trofeo> trofei;
    private List<Missione> missioniCompletate;

    //necessario costruttore vuoto per inserire nel db
    Utente(){}


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Trofeo> getTrofei() {
        return trofei;
    }

    public void setTrofei(List<Trofeo> trofei) {
        this.trofei = trofei;
    }

    public List<Missione> getMissioniCompletate() {
        return missioniCompletate;
    }

    public void setMissioniCompletate(List<Missione> missioniCompletate) {
        this.missioniCompletate = missioniCompletate;
    }
}

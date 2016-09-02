package com.turkoid.practiceonejohn.beans;

/**
 * Created by turkoid on 8/11/2016.
 */
public class User {
    private int id;
    private String name;
    private boolean state;

    public User(int id, String name, boolean state) {
        this.id = id;
        this.name = name;
        this.state = state;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }
}

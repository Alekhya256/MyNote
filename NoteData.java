package com.example.myapp;

import java.io.Serializable;

public class NoteData implements Serializable {
    public boolean isFullDisplayed;
    String title;
    String description;
    String id;
    boolean fullDisplayed;
    public NoteData(){

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;

    }

    public boolean isFullDisplayed() {
        return fullDisplayed;
    }

    public void setFullDisplayed(boolean fullDisplayed) {
        this.fullDisplayed = fullDisplayed;
    }
}
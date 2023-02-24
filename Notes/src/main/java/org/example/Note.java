package org.example;

import javafx.scene.paint.Color;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Note {
    private int id;
    private String heading;
    private String note;

    private List<String> hashtags;

    private LocalDateTime date;
    private Color color;

    public Note(int id, String heading, String note, Color color, List<String> hashtags, LocalDateTime date) {
        this.id = id;
        this.heading = heading;
        this.note = note;
        this.color = color;
        this.hashtags = hashtags;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String getHeading() {
        return heading;
    }

    public void setHeading(String heading) {
        this.heading = heading;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public List<String> getHashtags() {
        return hashtags;
    }

    public void setHashtags(List<String> hashtags) {
        this.hashtags = hashtags;
    }
}


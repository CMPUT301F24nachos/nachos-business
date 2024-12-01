package com.example.nachosbusiness.notifications;

import com.google.firebase.Timestamp;

public class Notification {
    private String title;
    private String content;
    private Timestamp timestamp;
    private String pendingIntentData; // For PendingIntent details (e.g., a URI or action)

    // Empty constructor for Firebase
    public Notification() {}

    public Notification(String title, String content, Timestamp timestamp, String pendingIntentData) {
        this.title = title;
        this.content = content;
        this.timestamp = timestamp;
        this.pendingIntentData = pendingIntentData;
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Timestamp getTimestamp() { return timestamp; }
    public void setTimestamp(Timestamp timestamp) { this.timestamp = timestamp; }

    public String getPendingIntentData() { return pendingIntentData; }
    public void setPendingIntentData(String pendingIntentData) { this.pendingIntentData = pendingIntentData; }
}


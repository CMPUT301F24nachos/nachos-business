package com.example.nachosbusiness.notifications;

public class Notification {
    private String title;
    private String content;
    private long timestamp; // Use milliseconds since epoch for sorting and querying
    private String pendingIntentData; // For PendingIntent details (e.g., a URI or action)

    // Empty constructor for Firebase
    public Notification() {}

    public Notification(String title, String content, long timestamp, String pendingIntentData) {
        this.title = title;
        this.content = content;
        this.timestamp = timestamp;
        this.pendingIntentData = pendingIntentData;
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public String getPendingIntentData() { return pendingIntentData; }
    public void setPendingIntentData(String pendingIntentData) { this.pendingIntentData = pendingIntentData; }
}


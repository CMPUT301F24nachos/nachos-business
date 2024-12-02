package com.example.nachosbusiness.notifications;

import com.google.firebase.Timestamp;

/**
 * Represents a notification in the system, including its title, content, timestamp, and data
 * required for a {@link android.app.PendingIntent}.
 */
public class Notification {
    private String title;
    private String content;
    private Timestamp timestamp;
    private String pendingIntentData;

    // Empty constructor for Firebase
    public Notification() {}

    /**
     * Constructs a new Notification with the specified details.
     * @param title             the title of the notification
     * @param content           the content or body of the notification
     * @param timestamp         the time when the notification was created
     * @param pendingIntentData additional data required for the PendingIntent, such as a URI or action
     */
    public Notification(String title, String content, Timestamp timestamp, String pendingIntentData) {
        this.title = title;
        this.content = content;
        this.timestamp = timestamp;
        this.pendingIntentData = pendingIntentData;
    }

    /**
     * Gets the title of the notification.
     * @return the title of the notification
     */
    public String getTitle() { return title; }

    /**
     * Sets the title of the notification.
     * @param title the title to set
     */
    public void setTitle(String title) { this.title = title; }

    /**
     * Gets the content of the notification.
     * @return the content of the notification
     */
    public String getContent() { return content; }

    /**
     * Sets the content of the notification.
     * @param content the content to set
     */
    public void setContent(String content) { this.content = content; }

    /**
     * Gets the timestamp of the notification.
     * @return the timestamp of the notification
     */
    public Timestamp getTimestamp() { return timestamp; }

    /**
     * Sets the timestamp of the notification.
     * @param timestamp the timestamp to set
     */
    public void setTimestamp(Timestamp timestamp) { this.timestamp = timestamp; }

    /**
     * Gets the data required for the PendingIntent.
     * @return the PendingIntent data
     */
    public String getPendingIntentData() { return pendingIntentData; }

    /**
     * Sets the data required for the PendingIntent.
     * @param pendingIntentData the PendingIntent data to set
     */
    public void setPendingIntentData(String pendingIntentData) { this.pendingIntentData = pendingIntentData; }
}


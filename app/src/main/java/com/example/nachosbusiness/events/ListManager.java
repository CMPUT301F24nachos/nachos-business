package com.example.nachosbusiness.events;

import com.example.nachosbusiness.DBManager;
import com.example.nachosbusiness.users.User;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class ListManager {
    private ArrayList<Map<Object, Object>> waitList;
    private ArrayList<User> invitedList;
    private ArrayList<User> acceptedList;
    private ArrayList<User> canceledList;

    private int waitListSpots;

    private String listManagerID;

    private DBManager dbManager;

    /**
     * Constructor for DBManager
     */
    public ListManager() {
    }

    /**
     * Constructor for ListManager with unlimited waitlist spots
     * @param eventID event corresponding to the lists
     */
    public ListManager(String eventID)
    {
        this.listManagerID = eventID;

        this.waitList = new ArrayList<Map<Object, Object>>();
        this.invitedList = new ArrayList<User>();
        this.acceptedList = new ArrayList<User>();
        this.canceledList = new ArrayList<User>();

        this.dbManager = new DBManager("lists");
        dbManager.setEntry(eventID, this);

        this.waitListSpots = -1;
    }

    /**
     * Constructor for ListManager with limited wait list spots
     * @param eventID event corresponding to the lists
     * @param waitListSpots number of wait list spots
     */
    public ListManager(String eventID, int waitListSpots)
    {
        this.listManagerID = UUID.randomUUID().toString();

        this.waitList = new ArrayList<Map<Object, Object>>();
        this.invitedList = new ArrayList<User>();
        this.acceptedList = new ArrayList<User>();
        this.canceledList = new ArrayList<User>();

        this.dbManager = new DBManager("lists");
        dbManager.setEntry(eventID, this);

        this.waitListSpots = waitListSpots;
    }

    /**
     * Adds user to waitlist
     * @param user string of userID to add
     * @return true if successfully added
     */
    public Boolean addToWaitList(User user, GeoPoint geoPoint)
    {
        if ((waitListSpots < 0 || (waitListSpots > 0 && waitList.size() < waitListSpots))) //&& waitList.stream().noneMatch(entry -> entry.containsKey("userID") && Objects.equals(entry.get("userID"), user)))
        {
            Map<Object, Object> userEntry = new HashMap<>();
            userEntry.put("user", user);
            userEntry.put("location", geoPoint);

            waitList.add(userEntry);
            dbManager.getCollectionReference().document(listManagerID).update("waitList", FieldValue.arrayUnion(userEntry));
            return true;
        }
        return false;
    }

    /**
     * Remove user from waitlist
     * @param user user to remove
     * @return true if successfully removed
     */
    public Boolean removeFromWaitList(User user) {
        Map<Object, Object> userEntry = waitList.stream()
                .filter(entry -> entry.containsKey("user"))
                .filter(entry -> {
                    Object userObject = entry.get("user");

                    if (userObject instanceof User) {
                        return Objects.equals(((User) userObject).getAndroid_id(), user.getAndroid_id());
                    }

                    else if (userObject instanceof Map) {
                        Object androidId = ((Map<?, ?>) userObject).get("android_id");
                        return Objects.equals(androidId, user.getAndroid_id());
                    }
                    return false;
                })
                .findFirst()
                .orElse(null);

        if (userEntry != null) {
            waitList.remove(userEntry);
            dbManager.getCollectionReference().document(listManagerID).update("waitList", FieldValue.arrayRemove(userEntry));
            return true;
        }
        return false;
    }

    /**
     * Transfers user from waitlist to invited list
     * @param user user to transfer
     * @return true for successful transfer
     */
    public Boolean moveToInvitedList(User user) {
        Map<Object, Object> userEntry = waitList.stream()
                .filter(entry -> entry.containsKey("userID") && Objects.equals(entry.get("userID"), user.getAndroid_id()))
                .findFirst()
                .orElse(null);
        if (userEntry != null) {
            waitList.remove(userEntry);
            invitedList.add(user);

            dbManager.getCollectionReference().document(listManagerID).update("waitlist", FieldValue.arrayRemove(userEntry));
            dbManager.getCollectionReference().document(listManagerID).update("invitedlist", FieldValue.arrayUnion(user.getAndroid_id()));
            return true;
        }
        return false;
    }

    /**
     * Transfers user from invited list to accepted list
     * @param user user to transfer
     * @return true for successful transfer
     */
    public Boolean moveToAcceptedList(User user)
    {
        if (invitedList.contains(user))
        {
            invitedList.remove(user);
            acceptedList.add(user);
            dbManager.getCollectionReference().document(listManagerID).update("invitedlist", FieldValue.arrayRemove(user.getAndroid_id()));
            dbManager.getCollectionReference().document(listManagerID).update("acceptedlist", FieldValue.arrayUnion(user.getAndroid_id()));
            return true;
        }
        return false;
    }

    /**
     * Transfers user from invited list to canceled list
     * @param user user to transfer
     * @return true for successful transfer
     */
    public Boolean moveToCanceledList(User user)
    {
        if (invitedList.contains(user))
        {
            invitedList.remove(user);
            canceledList.add(user);
            dbManager.getCollectionReference().document(listManagerID).update("invitedlist", FieldValue.arrayRemove(user.getAndroid_id()));
            dbManager.getCollectionReference().document(listManagerID).update("canceledlist", FieldValue.arrayUnion(user.getAndroid_id()));
            return true;
        }
        return false;
    }

    /**
     * Randomly selects a given number of users from the wait list
     * @param count number of users to select
     * @return list of selected users
     */
//    public List<User> sampleWaitList(int count)
//    {
//        Collections.shuffle(waitList);
//
//        List<Map<Object, Object>> selectedEntries = waitList.subList(0, count);
//
//        List<User> selectedUsers = new ArrayList<>();
//        for (Map<Object, Object> entry : selectedEntries) {
//            String userID = (String) entry.get("userID");
//            selectedUsers.add(userID);
//        }
//
//        return selectedUsers;
//    }

    /**
     * Check if the user is in the waitlist
     * @param user userID to query
     * @return true if in waitList
     */
    public boolean inWaitList(User user){
        Map<Object, Object> userEntry = waitList.stream()
                .filter(entry -> entry.containsKey("user"))
                .filter(entry -> {
                    Object userObject = entry.get("user");

                    if (userObject instanceof User) {
                        return Objects.equals(((User) userObject).getAndroid_id(), user.getAndroid_id());
                    }

                    else if (userObject instanceof Map) {
                        Object androidId = ((Map<?, ?>) userObject).get("android_id");
                        return Objects.equals(androidId, user.getAndroid_id());
                    }
                    return false;
                })
                .findFirst()
                .orElse(null);
        return userEntry != null;
    }

    /**
     * Getter for wait list
     * @return wait list
     */
    public ArrayList<Map<Object, Object>> getWaitList() {
        return waitList;
    }

    public void setWaitList(ArrayList<Map<Object, Object>> dBWaitList) {
        this.waitList = dBWaitList;
    }

    /**
     * Getter for invited list
     * @return invited list
     */
    public ArrayList<User> getInvitedList() {
        return invitedList;
    }

    /**
     * Getter for accepted list
     * @return accepted list
     */
    public ArrayList<User> getAcceptedList() {
        return acceptedList;
    }

    /**
     * Getter for cancelled list
     * @return cancelled list
     */
    public ArrayList<User> getCanceledList() {
        return canceledList;
    }

    /**
     * Set the number of available spots in the wishlist
     * @param waitListSpots total number of spots in the wishlist
     */
    public void setWaitListSpots(int waitListSpots) {
        this.waitListSpots = waitListSpots;
    }

    public String getListManagerID() {
        return listManagerID;
    }

    public void setListManagerID(String listManagerID) {
        this.listManagerID = listManagerID;
    }

    public DBManager getDbManager() {
        return dbManager;
    }

    public void setDbManager(DBManager dbManager) {
        this.dbManager = dbManager;
    }
}

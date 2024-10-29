package com.example.nachosbusiness.events;

import com.example.nachosbusiness.DBManager;
import com.example.nachosbusiness.users.User;
import com.google.firebase.firestore.FieldValue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class ListManager {
    private ArrayList<User> waitList;
    private ArrayList<User> invitedList;
    private ArrayList<User> acceptedList;
    private ArrayList<User> canceledList;

    private int waitListSpots;

    private String listManagerID;

    private DBManager dbManager;


    /**
     * Constructor for ListManager with unlimited waitlist spots
     * @param eventID event corresponding to the lists
     */
    public ListManager(String eventID)
    {
        this.listManagerID = eventID;

        this.waitList = new ArrayList<User>();
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

        this.waitList = new ArrayList<User>();
        this.invitedList = new ArrayList<User>();
        this.acceptedList = new ArrayList<User>();
        this.canceledList = new ArrayList<User>();

        this.dbManager = new DBManager("lists");
        dbManager.setEntry(eventID, this);

        this.waitListSpots = waitListSpots;
    }

    /**
     * Adds user to waitlist
     * @param user user to add
     * @return true if successfully added
     */
    public Boolean addToWaitList(User user)
    {
        if ((waitListSpots < 0 || (waitListSpots > 0 && waitList.size() < waitListSpots)) && !waitList.contains(user))
        {
            waitList.add(user);
            dbManager.getCollectionReference().document(listManagerID).update("waitlist", FieldValue.arrayUnion(user.getAndroid_id()));
            return true;
        }
        return false;
    }

    /**
     * Transfers user from waitlist to invited list
     * @param user user to transfer
     * @return true for successful transfer
     */
    public Boolean moveToInvitedList(User user)
    {
        if (waitList.contains(user))
        {
            waitList.remove(user);
            invitedList.add(user);
            dbManager.getCollectionReference().document(listManagerID).update("waitlist", FieldValue.arrayRemove(user.getAndroid_id()));
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
     * Transfers user from wait/invited/accepted list to cancelled list
     * @param user user to transfer
     * @return true for successful transfer
     */
    public Boolean moveToCanceledList(User user)
    {
        if (waitList.contains(user))
        {
            waitList.remove(user);
            canceledList.add(user);
            dbManager.getCollectionReference().document(listManagerID).update("waitlist", FieldValue.arrayRemove(user.getAndroid_id()));
            dbManager.getCollectionReference().document(listManagerID).update("canceledlist", FieldValue.arrayUnion(user.getAndroid_id()));
            return true;
        }
        else if (invitedList.contains(user))
        {
            invitedList.remove(user);
            canceledList.add(user);
            dbManager.getCollectionReference().document(listManagerID).update("invitedlist", FieldValue.arrayRemove(user.getAndroid_id()));
            dbManager.getCollectionReference().document(listManagerID).update("canceledlist", FieldValue.arrayUnion(user.getAndroid_id()));
            return true;
        }
        else if (acceptedList.contains(user))
        {
            acceptedList.remove(user);
            canceledList.add(user);
            dbManager.getCollectionReference().document(listManagerID).update("acceptedlist", FieldValue.arrayRemove(user.getAndroid_id()));
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
    public List<User> sampleWaitList(int count)
    {
        Collections.shuffle(waitList);

        // put them in invite list

        return waitList.subList(0, count-1);
    }

    /**
     * Getter for wait list
     * @return wait list
     */
    public ArrayList<User> getWaitList() {
        return waitList;
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
}

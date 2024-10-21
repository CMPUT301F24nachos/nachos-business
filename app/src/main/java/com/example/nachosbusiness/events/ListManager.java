package com.example.nachosbusiness.events;

import android.widget.Toast;

import com.example.nachosbusiness.users.User;

import java.util.ArrayList;

public class ListManager {
    private ArrayList<User> waitList;
    private ArrayList<User> invitedList;
    private ArrayList<User> acceptedList;
    private ArrayList<User> canceledList;

    private int waitListSpots;


    public ListManager()
    {
        waitList = new ArrayList<User>();
        invitedList = new ArrayList<User>();
        acceptedList = new ArrayList<User>();
        canceledList = new ArrayList<User>();

        waitListSpots = -1;
    }

    public ListManager(int waitListSpots)
    {
        waitList = new ArrayList<User>();
        invitedList = new ArrayList<User>();
        acceptedList = new ArrayList<User>();
        canceledList = new ArrayList<User>();

        this.waitListSpots = waitListSpots;
    }

    public Boolean addToWaitList(User user)
    {
        if ((waitListSpots < 0 || (waitListSpots > 0 && waitList.size() < waitListSpots)) && !waitList.contains(user))
        {
            waitList.add(user);
            return true;
        }
        return false;
    }

    public Boolean moveToInvitedList(User user)
    {
        if (waitList.contains(user))
        {
            invitedList.add(user);
            return true;
        }
        return false;
    }

    public Boolean moveToAcceptedList(User user)
    {
        if (invitedList.contains(user))
        {
            acceptedList.add(user);
            return true;
        }
        return false;
    }

    public Boolean moveToCanceledList(User user)
    {
        if (waitList.contains(user))
        {
            waitList.remove(user);
            canceledList.add(user);
            return true;
        }
        else if (invitedList.contains(user))
        {
            invitedList.remove(user);
            canceledList.add(user);
            return true;
        }
        else if (acceptedList.contains(user))
        {
            acceptedList.remove(user);
            canceledList.add(user);
            return true;
        }
        return false;
    }

    public ArrayList<User> sampleWaitList()
    {
        ArrayList<User> sample;



        return sample;
    }

    private void addToDB(User user, String collectionName)
    {

    }

    private void removeFromDB(User user, String collectionName)
    {

    }

    private void moveBetweenCollection(User user, String sourceCollection, String destinationCollection)
    {

    }


    public ArrayList<User> getWaitList() {
        return waitList;
    }

    public ArrayList<User> getInvitedList() {
        return invitedList;
    }

    public ArrayList<User> getAcceptedList() {
        return acceptedList;
    }

    public ArrayList<User> getCanceledList() {
        return canceledList;
    }
}

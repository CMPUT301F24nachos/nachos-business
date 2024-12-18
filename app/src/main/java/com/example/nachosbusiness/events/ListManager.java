package com.example.nachosbusiness.events;

import com.example.nachosbusiness.DBManager;
import com.example.nachosbusiness.users.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * WaitList holds objects of type User, and GeoPoint. They are of type Object to be able to be stored
 * in FireBase. Using a Map<User, GeoPoint> returns an error and does not allow the map to hold the
 * different types.
 */
public class ListManager {
    private ArrayList<Map<Object, Object>> waitList;
    private ArrayList<User> invitedList;
    private ArrayList<User> acceptedList;
    private ArrayList<User> canceledList;

    private int waitListSpots;

    private boolean testMode = false;

    private String listManagerID;

    private DBManager dbManager;

    /**
     * Empty Constructor for firebase db queries.
     */
    public ListManager(){

    }

    /**
     * Constructor for testing, disables all calls to db and allows core functionality testing. Set
     * testMode to True to bypass db calls.
     */
    public ListManager(int waitListSpots, boolean testMode) {
        this.waitList = new ArrayList<Map<Object, Object>>();
        this.invitedList = new ArrayList<User>();
        this.acceptedList = new ArrayList<User>();
        this.canceledList = new ArrayList<User>();
        this.waitListSpots = waitListSpots;
        this.testMode = testMode;
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
            if (!testMode) {
                dbManager.getCollectionReference().document(listManagerID).update("waitList", FieldValue.arrayUnion(userEntry));
            };
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
            if (!testMode) {
                dbManager.getCollectionReference().document(listManagerID).update("waitList", FieldValue.arrayRemove(userEntry));
            }
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
            invitedList.add(user);

            if (!testMode) {
                dbManager.getCollectionReference().document(listManagerID).update("waitList", FieldValue.arrayRemove(userEntry))
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                dbManager.getCollectionReference().document(listManagerID).update("invitedList", FieldValue.arrayUnion(user));
                            }
                        });
            }
            return true;
        }
        return false;
    }

    /**
     * Transfers user from invited list to accepted list
     * @param user user to transfer
     * @return true for successful transfer
     */
    public Boolean moveToAcceptedList(User user) {
        Object foundEntry = null;

        for (Object entry : invitedList) {
            if (entry instanceof User) {
                if (((User) entry).getAndroid_id().equals(user.getAndroid_id())) {
                    foundEntry = entry;
                    break;
                }
            }
            // Will always be a map type, but i kept the user stuff just incase???
            else if (entry instanceof Map) {
                Map<String, Object> mapEntry = (Map<String, Object>) entry;
                Object androidId = mapEntry.get("android_id");
                if (androidId != null && androidId.equals(user.getAndroid_id())) {
                    foundEntry = entry;
                    break;
                }
            }
        }

        if (foundEntry == null) {
            return false;
        }

        invitedList.remove(foundEntry);

        if (acceptedList == null) {
            acceptedList = new ArrayList<>();
        }
        acceptedList.add(user);

        if (!testMode) {
            dbManager.getCollectionReference().document(listManagerID).update("invitedList", FieldValue.arrayRemove(foundEntry))
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            dbManager.getCollectionReference().document(listManagerID).update("acceptedList", FieldValue.arrayUnion(user));
                        }
                    });
        }
        return true;
    }

    /**
     * Transfers user from invited list to canceled list
     * @param user user to transfer
     * @return true for successful transfer
     */
    public Boolean moveToCanceledList(User user) {
        Object foundEntry = null;

        for (Object entry : invitedList) {
            if (entry instanceof User) {
                if (((User) entry).getAndroid_id().equals(user.getAndroid_id())) {
                    foundEntry = entry;
                    break;
                }
            }
            // Will always be a map type, but i kept the user stuff just incase???
            else if (entry instanceof Map) {
                Map<String, Object> mapEntry = (Map<String, Object>) entry;
                Object androidId = mapEntry.get("android_id");
                if (androidId != null && androidId.equals(user.getAndroid_id())) {
                    foundEntry = entry;
                    break;
                }
            }
        }

        if (foundEntry == null) {
            return false;
        }

        invitedList.remove(foundEntry);

        if (canceledList == null) {
            canceledList = new ArrayList<>();
        }
        canceledList.add(user);

        if (!testMode) {
            dbManager.getCollectionReference().document(listManagerID).update("invitedList", FieldValue.arrayRemove(user))
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            dbManager.getCollectionReference().document(listManagerID).update("canceledList", FieldValue.arrayUnion(user));
                        }
                    });
        }
        return true;
    }

    /**
     * moves all entrants in the invited list to the canceled list
     * @return true if transfer is successful
     */
    public boolean moveAllToCanceledList() {
        canceledList.addAll(invitedList);
        invitedList.clear();

        if (!testMode) {
            dbManager.getCollectionReference().document(listManagerID).update("invitedList", invitedList)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            dbManager.getCollectionReference().document(listManagerID).update("canceledList", canceledList);
                        }
                    });
        }

        return true;
    }


    /**
     * Randomly selects a given number of users from the wait list and moves them to the invited list
     * @param count number of users to select
     * @return list of selected users
     */
    public ArrayList<User> sampleWaitList(int count)
    {
        Collections.shuffle(waitList);

        List<Map<Object, Object>> selectedEntries = waitList.subList(0, count);

        ArrayList<User> selectedUsers = new ArrayList<>();
        for (Map<Object, Object> entry : selectedEntries) {
            Object userObject = entry.get("user");

            if (userObject instanceof User) {
                selectedUsers.add((User) userObject);
                moveToInvitedList((User) userObject);
            } else if (userObject instanceof Map) {
                try {
                    User user;
                    Map<?, ?> userMap = (Map<?, ?>) userObject;
                    user = new User(userMap.get("android_id").toString(), userMap.get("username").toString(), userMap.get("email").toString(), userMap.get("phone").toString());
                    selectedUsers.add(user);
                    moveToInvitedList(user);
                } catch (Exception e) {
                    return null;
                }
            } else {
                return null;
            }
        }
        return selectedUsers;
    }

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
     * sets the listManager and dbManager to be used for the queries
     * @param eventID ID of the event which the listManager belongs to
     */
    public void initializeManagers(String eventID) {
        this.listManagerID = eventID;
        this.dbManager = new DBManager("lists");
        dbManager.setEntry(eventID, this);
    }

    /**
     * Getter for wait list
     * @return wait list
     */
    public ArrayList<Map<Object, Object>> getWaitList() {
        return waitList;
    }

    /**
     * Sets the waitlist in the listmanager
     * @param dBWaitList waitlist from the db
     */
    public void setWaitList(ArrayList<Map<Object, Object>> dBWaitList) {
        this.waitList = dBWaitList;
    }

    /**
     * sets the invitelist in the list manager
     * @param invitedList invite list from db
     */
    public void setInvitedList(ArrayList<User> invitedList) {
        this.invitedList = invitedList;
    }

    /**
     * sets the invitelist in the list manager
     * @param acceptedList accepted list from db
     */
    public void setAcceptedList(ArrayList<User> acceptedList) {
        this.acceptedList = acceptedList;
    }

    /**
     * sets the invitelist in the list manager
     * @param canceledList cancelled list from db
     */
    public void setCanceledList(ArrayList<User> canceledList) {
        this.canceledList = canceledList;
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
    public ArrayList<User> getCanceledList() { return canceledList; }

    /**
     * Set the number of available spots in the wishlist
     * @param waitListSpots total number of spots in the wishlist
     */
    public void setWaitListSpots(int waitListSpots) {
        this.waitListSpots = waitListSpots;
    }

    /**
     * Sets the listManagerID
     * @param listManagerID id of the event
     */
    public void setListManagerID(String listManagerID) {
        this.listManagerID = listManagerID;
    }

    /**
     * Sets the DB Manager for sending items to the DB
     * @param dbManager class of DBManager
     */
    public void setDbManager(DBManager dbManager) {
        this.dbManager = dbManager;
    }


}
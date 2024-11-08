package com.example.nachosbusiness.admin_browse;

import android.util.Log;

import androidx.annotation.Nullable;

import com.example.nachosbusiness.DBManager;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Handler to query the 'entrants' database, which holds all user information
 */
public class ProfileDBManager extends DBManager implements Serializable {
    private static final String TAG = "ProfileDBManager ";


    /**
     * Constructor for ProfileDBManager
     * @param collection string (name of collection)
     */
    public ProfileDBManager (String collection) {
        super(collection);
        this.setCollectionReference("entrants");
    }

    /**
     * Callback interface for receiving a list of profiles
     */

    public interface ProfileCallback {
        void onProfilesReceived(List<Profile> profileList);
    }


    /**
     * Queries the Firebase Db and retrieves all profiles from the entrants collection
     *
     * @param callback  to handle the list of profile objects retrieved.
     *
     */

    public void fetchAllProfiles(ProfileCallback callback) {
        getCollectionReference().addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshots, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.e(TAG, error.toString());
                    return;
                }

                if (querySnapshots != null) {
                    List<Profile> profilesList = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : querySnapshots) {
                        String Username = doc.getString("username");
                        String image = doc.getString("profileImage");
                        String androidID= doc.getString("android_id");
                        String email = doc.getString("email");
                        String phonenum= doc.getString("phone");
                        if (Username != null) {
                            Profile profile = new Profile(Username, image, androidID,email, phonenum);
                            profilesList.add(profile);
                        }
                    }
                    callback.onProfilesReceived(profilesList);
                }
            }
        });
    }
}

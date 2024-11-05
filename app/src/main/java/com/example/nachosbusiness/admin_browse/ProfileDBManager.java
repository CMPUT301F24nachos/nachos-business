package com.example.nachosbusiness;

import android.util.Log;

import androidx.annotation.Nullable;

import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Handler to query the 'entrants' database.
 */
public class ProfileDBManager extends DBManager implements Serializable {
    private static final String TAG = "ProfileDBManager ";

    /**
     * Constructor for EntrantsDbManager
     * @param collection string of collection name
     */
    public ProfileDBManager (String collection) {
        super(collection);
        this.setCollectionReference("entrants"); // Default to "entrants" collection
    }

    // Callback interface to handle retrieved entrants data
    public interface EntrantCallback {
        void onEntrantsReceived(List<Profile> entrantsList);
    }

    /**
     * Query the Firestore DB for all entrants and trigger the callback with a list of entrants' names.
     */
    public void fetchAllEntrants(EntrantCallback callback) {
        getCollectionReference().addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshots, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.e(TAG, error.toString());
                    return;
                }

                if (querySnapshots != null) {
                    List<Profile> entrantsList = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : querySnapshots) {
                        String entrantName = doc.getString("username");
                        String imageUrl = doc.getString("profileImage");// Adjust field name if needed
                        if (entrantName != null) {
                            Profile profile = new Profile(entrantName, imageUrl); // Create Profile object
                            entrantsList.add(profile);
                        }
                    }
                    callback.onEntrantsReceived(entrantsList); // Trigger callback with the list
                }
            }
        });
    }
}

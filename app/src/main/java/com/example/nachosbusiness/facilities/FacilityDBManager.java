package com.example.nachosbusiness.facilities;

import android.util.Log;

import androidx.annotation.Nullable;

import com.example.nachosbusiness.DBManager;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;

/**
 * Handler to query the 'facilities' database. Sets this.facility to be the queried facility.
 */
public class FacilityDBManager extends DBManager implements Serializable{

    Facility facility;
    private static final String TAG = "FacilityDBManager";

    /**
     * Constructor for FacilityDBManager
     */
    public FacilityDBManager(String collection) {
        super(collection);
        this.facility = new Facility();
    }

    /**
     * Setter for the facility
     * @param facility Facility type
     */
    public void setFacility(Facility facility) {
        this.facility = facility;
    }

    /**
     * getter for the facility
     * @return facility of type Facility
     */
    public Facility getFacility() {
        return facility;
    }

    /**
     * Checks if the user associated with an AndroidID has an existing facility in the db
     * @return True if exists
     */
    public Boolean hasFacility() {
        return this.facility.getName() != null;
    }

    public interface FacilityCallback {
        void onFacilityReceived(Facility facility);
    }

    /**
     * Query the firebase db for a facility with document ID = androidID. Updates this.facility if
     * facility exists.
     * @param androidID Android ID of the user to query the db with
     */
    public void queryOrganizerFacility(String androidID, FacilityCallback callback) {
        this.setCollectionReference("facilities");
        this.getCollectionReference().addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshots, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.e(TAG, error.toString());
                    return;
                }
                if (querySnapshots != null) {
                    for (QueryDocumentSnapshot doc : querySnapshots) {
                        if (doc.getId().equals(androidID)) {
                            facility.setName(doc.getString("name"));
                            facility.setLocation(doc.getString("location"));
                            facility.setDesc(doc.getString("desc"));
                            Log.d(TAG, String.format("Facility - androidID %s, facility name %s) fetched", doc.getId(), facility.getName()));

                            callback.onFacilityReceived(facility);
                        }
                    }
                }
            }
        });
    }
}



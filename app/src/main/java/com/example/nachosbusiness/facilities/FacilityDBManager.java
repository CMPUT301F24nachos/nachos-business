package com.example.nachosbusiness.facilities;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.nachosbusiness.DBManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;

/**
 * Handler to query the 'facilities' database.
 */
public class FacilityDBManager extends DBManager implements Serializable{

    Facility facility;
    Boolean hasFacility = false;
    String docId;
    private static final String TAG = "FacilityDBManager";

    /**
     * Constructor for FacilityDBManager
     * @param collection string of collection name
     */
    public FacilityDBManager(String collection) {
        super(collection);
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
    public Boolean getHasFacility() {
        return hasFacility;
    }

    /**
     * Sets if the user associated with an AndroidID has an existing facility in the db
     * @param hasFacility True if exists
     */
    public void setHasFacility(Boolean hasFacility) {
        this.hasFacility = hasFacility;
    }

    /**
     * getter for the Document ID within the 'facilities' db
     * @return String value of the document id on Firebase
     */
    public String getDocId() {
        return docId;
    }

    /**
     * setter for the Document ID within the 'facilities' db
     * @param docId String value of the document id on Firebase
     */
    public void setDocId(String docId) {
        this.docId = docId;
    }

    /**
     * Query the Firebase db for a record where the user's android ID exists in a record in the 'facilities'
     * table. If the user has a record, will update the hasFacility attribute to true, will update the
     * documentID attribute to the corresponding document, will set the facility attribute to the
     * corresponding facility record from the DB.
     * @param androidID Android ID of the user to query the db with
     */
    public void queryOrganizerFacility(String androidID){
        this.getDb().collection("facilities")
                .whereEqualTo("android_id", androidID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        Log.i(TAG, "query complete");
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                setFacility(document.toObject(Facility.class));
                                setHasFacility(true);
                                setDocId(document.getId());
                                Log.i(TAG, "Cached document data: " + document.getData());
                            }
                        } else {
                            Log.e(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
        }

}

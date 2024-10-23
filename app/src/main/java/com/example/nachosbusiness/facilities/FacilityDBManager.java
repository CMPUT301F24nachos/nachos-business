package com.example.nachosbusiness.facilities;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.nachosbusiness.DBManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;

public class FacilityDBManager extends DBManager implements Serializable{

    Facility facility;
    Boolean hasFacility = false;
    String docId;
    private static final String TAG = "FACILITYDB";

    public FacilityDBManager(String collection) {
        super(collection);
    }

    public void setFacility(Facility facility) {
        this.facility = facility;
    }

    public Facility getFacility() {
        return facility;
    }

    public Boolean getHasFacility() {
        return hasFacility;
    }

    public void setHasFacility(Boolean hasFacility) {
        this.hasFacility = hasFacility;
    }

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }

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

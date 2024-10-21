package com.example.nachosbusiness.facilities;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.nachosbusiness.users.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class FacilityManager {

    private FirebaseFirestore db;

    public FacilityManager(String androidID)
    {
        this.db = FirebaseFirestore.getInstance();
    }

    public void addNewFacility(Facility facility){
        CollectionReference facilitiesRef = db.collection("facilities");

        facilitiesRef.add(facility).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                // add to db success
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // add to db failure
            }
        });
    }

//    public boolean queryFacility(String android_id){
//        CollectionReference facilitiesRef = db.collection("facilities");
//        facilitiesRef.whereEqualTo("android_id", android_id)
//                .get()
//                .addOnCompleteListener(task -> {
//                    if (task.isSuccessful()) {
//                        // Check if any document matches the query
//                        if (!task.getResult().isEmpty()) {
//                            return true;
//                        } else {
//                            return false;
//                        }
//                    } else {
//                        return false;
//                        // fail
//                        }
//                    }
//                });
//    }
}

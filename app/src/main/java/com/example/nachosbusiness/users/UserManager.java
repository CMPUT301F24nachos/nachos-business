package com.example.nachosbusiness.users;

import android.net.Uri;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Used to interact with the db for users to add.
 */

public class UserManager {

    private FirebaseFirestore db;

    public UserManager()
    {
        this.db = FirebaseFirestore.getInstance();
    }

    /**
     * Creates a new user and adds the new user to the db
     */
    public void registerUser(String android_id, String username, String email, String phone) {
        User user = new User(android_id, username, email, phone);
        addUser(user);
    }





    /**
     * Adds user to the firestore database
     * @param user user to add
     */
    public void addUser(User user)
    {
        CollectionReference usersRef = db.collection("entrants");

        usersRef.add(user).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
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

    /**
     * Removes user from the firestore database
     * @param user user to delete
     */
    public void deleteUser(User user)
    {
        db.collection("entrants")
                .document(user.getAndroid_id())
                .delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful())
                        {
                            // success
                        }
                        else
                        {
                            // fail
                        }
                    }
                });
    }


}
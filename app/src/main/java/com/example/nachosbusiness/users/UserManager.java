package com.example.nachosbusiness.users;

import android.content.Context;
import android.content.ContentResolver;

import android.provider.Settings.Secure;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class UserManager {

    private FirebaseFirestore db;

    /**
     * Creates a new user and adds the new user to the db
     */
    public void registerUser(String android_id, String username, String email, int phone)
    {
        User user = new User(android_id, username, email, phone);
        addUser(user);
    }

    /**
     * Adds user to the firestore database
     * @param user
     */
    public void addUser(User user)
    {
        db = FirebaseFirestore.getInstance();

        CollectionReference usersRef = db.collection("users");

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
     * @param user
     */
    public void deleteUser(User user)
    {
        db = FirebaseFirestore.getInstance();

        db.collection("users")
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

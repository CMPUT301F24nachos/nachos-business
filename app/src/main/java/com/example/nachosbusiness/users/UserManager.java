package com.example.nachosbusiness.users;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
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

    /**
     * Checks if the user is an admin based on androidID
     * @param androidID the user's androidID
     * @param callback the callback to pass the result
     */
    public void checkIfUserIsAdmin(String androidID, final AdminCallback callback) {
        db.collection("entrants")
                .document(androidID) // Assuming the document ID is the androidID
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document != null && document.exists()) {
                                Boolean adminStatus = document.getBoolean("admin");  // Get the boolean value
                                Log.d("UserManager", "Admin field value: " + adminStatus);  // Log the value
                                if (adminStatus != null && adminStatus) {
                                    callback.onAdminFound(true);  // User is an admin
                                } else {
                                    callback.onAdminFound(false); // User is not an admin
                                }
                            } else {
                                Log.d("UserManager", "No document found for androidID: " + androidID);
                                callback.onAdminFound(false); // No such document or no admin field
                            }
                        } else {
                            Log.e("UserManager", "Error getting documents: ", task.getException());
                            callback.onAdminFound(false); // In case of error, return false
                        }
                    }
                });
    }


    /**
     * Callback interface for admin status
     */
    public interface AdminCallback {
        void onAdminFound(boolean isAdmin);
    }
}




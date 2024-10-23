package com.example.nachosbusiness.users;

import android.content.Context;
import android.net.Uri;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.function.BiConsumer;

public class UserManager {

    private FirebaseFirestore db;

    public UserManager()
    {
        this.db = FirebaseFirestore.getInstance();
    }

    /**
     * Creates a new user and adds the new user to the db
     */
    public void registerUser(String android_id, String username, String email, String phone, Uri profileImage) {
        User user = new User(android_id, username, email, phone, profileImage);
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

    public interface UserCallback {
        void onUserRetrieved(String name, String email, String phone);
        void onUserNotFound();
        void onError(String error);
    }

    public void getUser(String android_id, UserCallback callback) {
        Query query = db.collection("entrants").whereEqualTo("android_id", android_id).limit(1);

        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot querySnapshot = task.getResult();
                if (querySnapshot != null && !querySnapshot.isEmpty()) {
                    DocumentSnapshot document = querySnapshot.getDocuments().get(0);
                    String name = document.getString("name");
                    String email = document.getString("email");
                    String phone = document.getString("phone");

                    callback.onUserRetrieved(name, email, phone);
                } else {
                    callback.onUserNotFound();
                }
            } else {
                // Handle the error
                callback.onError(task.getException() != null ? task.getException().getMessage() : "Unknown error");
            }
        });
    }

}

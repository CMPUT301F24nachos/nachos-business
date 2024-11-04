package com.example.nachosbusiness;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class DBManager {

    private FirebaseFirestore db;
    private CollectionReference collectionReference;

    public DBManager(String collection)
    {
        this.db = FirebaseFirestore.getInstance();
        this.collectionReference = db.collection(collection);
    }

    /**
     * Creates document with auto-generated id and adds object to collection in the firestore database
     * @param o object to add
     */
    public void addEntry(Object o)
    {
        collectionReference.add(o).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
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
     * Creates/updates specified document to set object in collection in the firestore database
     * @param document document to overwrite/create
     * @param o object to set
     */
    public void setEntry(String document, Object o)
    {
        collectionReference.document(document).set(o)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    /**
     * Removes document from the collection in the firestore database
     * @param document document to delete
     */
    public void deleteEntry(String document)
    {
        collectionReference.document(document)
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
     * Creates document with auto-generated id and adds object to a specified collection in the firestore database
     * @param o object to add
     * @param collection firebase collection to add object
     */
    public void addEntry(Object o, String collection)
    {
        db.collection(collection).add(o).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
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
     * Creates/updates specified document to set object in a specified collection in the firestore database
     * @param document document to overwrite/create
     * @param o object to set
     * @param collection firebase collection to set object
     */
    public void setEntry(String document, Object o, String collection)
    {
        db.collection(collection).document(document).set(o)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    /**
     * Removes document from a specified collection in the firestore database
     * @param document document to delete
     * @param collection firebase collection where entry is stored
     */
    public void deleteEntry(String document, String collection)
    {
        db.collection(collection)
                .document(document)
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

    public CollectionReference getCollectionReference() {
        return collectionReference;
    }

    public void setCollectionReference(String collection) {
        this.collectionReference = db.collection(collection);
    }
}

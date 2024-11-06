package com.example.nachosbusiness;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.io.InputStream;


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

    public void getUser(String android_id, EntryRetrievalCallback callback) {
        Query query = db.collection("entrants").whereEqualTo("android_id", android_id).limit(1);

        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot querySnapshot = task.getResult();
                if (querySnapshot != null && !querySnapshot.isEmpty()) {
                    DocumentSnapshot document = querySnapshot.getDocuments().get(0);
                    String name = document.getString("username");
                    String email = document.getString("email");
                    String phone = document.getString("phone");

                    callback.onEntryRetrieved(name, email, phone);
                } else {
                    callback.onEntryNotFound();
                }
            } else {
                callback.onError(task.getException() != null ? task.getException().getMessage() : "Unknown error");
            }
        });
    }

    public static void uploadProfileImage(Context context, String imageName, Uri selectedImageUri) {
        if (selectedImageUri != null) {
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();
            StorageReference profileImagesRef = storageRef.child("profile_images/" + imageName + ".jpg");
            UploadTask uploadTask = profileImagesRef.putFile(selectedImageUri);

            uploadTask.addOnSuccessListener(taskSnapshot -> {
                profileImagesRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    Toast.makeText(context, "Image uploaded successfully!", Toast.LENGTH_SHORT).show();
                });
            }).addOnFailureListener(e -> {
                Toast.makeText(context, "Image upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
        } else {
            Toast.makeText(context, "No image selected to upload.", Toast.LENGTH_SHORT).show();
        }
    }

    public void getProfileImage(String androidId, ImageView imageView, Context context, Runnable onImageLoaded) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference profileImageRef = storageRef.child("profile_images/" + androidId + ".jpg");

        profileImageRef.getDownloadUrl().addOnSuccessListener(uri -> {
            new Thread(() -> {
                try {
                    InputStream inputStream = new java.net.URL(uri.toString()).openStream();
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    ((Activity) context).runOnUiThread(() -> {
                        imageView.setImageBitmap(bitmap);
                        if (onImageLoaded != null) {
                            onImageLoaded.run();  // Execute the callback only if it's provided
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        }).addOnFailureListener(e -> {
            e.printStackTrace();
        });
    }

    public void deleteProfileImage(String androidId, ShowProfile context) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference profileImageRef = storageRef.child("profile_images/" + androidId + ".jpg");

        profileImageRef.delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(context, "Profile image deleted successfully.", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Failed to delete profile image.", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                });
    }

    public void getProfileImageExtra(String androidId, ImageView imageView, Context context, ProfileImageCallback callback) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference profileImageRef = storageRef.child("profile_images/" + androidId + ".jpg");

        profileImageRef.getDownloadUrl().addOnSuccessListener(uri -> {
            new Thread(() -> {
                try {
                    InputStream inputStream = new java.net.URL(uri.toString()).openStream();
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    ((Activity) context).runOnUiThread(() -> {
                        imageView.setImageBitmap(bitmap);
                        if (callback != null) {
                            callback.onImageLoaded(bitmap);
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                    ((Activity) context).runOnUiThread(() -> {
                        if (callback != null) {
                            callback.onImageLoadFailed(e);
                        }
                    });
                }
            }).start();
        }).addOnFailureListener(e -> {
            e.printStackTrace();
            if (callback != null) {
                callback.onImageLoadFailed(e);
            }
        });
    }

    public interface EntryRetrievalCallback {
        void onEntryRetrieved(String name, String email, String phone);
        void onEntryNotFound();
        void onError(String error);
    }

    public interface ProfileImageCallback {
        void onImageLoaded(Bitmap bitmap);
        void onImageLoadFailed(Exception e);
    }

    public CollectionReference getCollectionReference() {
        return collectionReference;
    }

    public void setCollectionReference(String collection) {
        this.collectionReference = db.collection(collection);
    }
}

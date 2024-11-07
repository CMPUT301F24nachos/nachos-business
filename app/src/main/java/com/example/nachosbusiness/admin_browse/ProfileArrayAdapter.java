package com.example.nachosbusiness.admin_browse;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.nachosbusiness.R;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Array Adapter for displaying Profile objects in a custom ListView.
 * Each item in the list shows the profile's image and name, with an edit button.
 */
public class ProfileArrayAdapter extends ArrayAdapter<Profile> {

    private ArrayList<Profile> profiles;
    private Context context;

    /**
     * Constructs a new ProfileArrayAdpater
     *
     * @param context  The current context.
     * @param profiles The list of Profile objects to display.
     */
    public ProfileArrayAdapter(Context context, ArrayList<Profile> profiles) {

        super(context, 0, profiles);
        this.profiles = profiles;
        this.context = context;
    }

    /**
     * Provides a view, inflates a layout for each list item, retrieves the
     *  event at the specified position.
     *
     * @param position    The position of the item within the adapter's data set
     * @param convertView the old view
     * @param parent      parent view.
     * @return View corresponding to the data at the specified position.
     *
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View view = convertView;

        if(view == null){
            view = LayoutInflater.from(context).inflate(R.layout.profile_list, parent,false);
        }

        Profile profile = profiles.get(position);

        ImageView profileImage = view.findViewById(R.id.profile_image);
        TextView profileName = view.findViewById(R.id.profile_name);
        ImageButton editProfile = view.findViewById(R.id.edit);
        profileImage.setImageDrawable(null);
        profileName.setText(profile.getName());

        String androidId = profile.getAndroid_id(); // Make sure this method exists to retrieve the ID
        loadProfileImage(androidId, profileImage);

        return view;
    }

    /**
     * Loads the profile image from Firebase Storage using the attached Android ID
     *
     * @param androidId   The unique Android ID for each user profile.
     * @param imageView   The ImageView to display the profile image.
     */
    public void loadProfileImage(String androidId, ImageView imageView) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference profileImageRef = storageRef.child("profile_images/" + androidId + ".jpg");

        profileImageRef.getDownloadUrl().addOnSuccessListener(uri -> {
            new Thread(() -> {
                try {
                    InputStream inputStream = new java.net.URL(uri.toString()).openStream();
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                    ((Activity) context).runOnUiThread(() -> imageView.setImageBitmap(bitmap));
                } catch (IOException e) {
                    e.printStackTrace();
                    // Handle image loading errors here
                }
            }).start();
        }).addOnFailureListener(e -> {
            e.printStackTrace();
            // Optionally, handle the case where the image could not be retrieved
        });
    }

}
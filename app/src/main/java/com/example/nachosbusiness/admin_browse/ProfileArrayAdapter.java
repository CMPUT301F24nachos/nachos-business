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
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

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
        String Name = profile.getName();
        loadProfileImage(androidId, profileImage, Name);

        editProfile.setOnClickListener(v -> {
            // When the edit button is clicked, open the profile fragment
            openProfileDetailFragment(profile);
        });
        return view;
    }

    /**
     * Loads the profile image from Firebase Storage using the attached Android ID
     *
     * @param androidId   The unique Android ID for each user profile.
     * @param imageView   The ImageView to display the profile image.
     */
    private void loadProfileImage(String androidId, ImageView imageView, String Name) {
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
                    // In case of an error, handle it by showing a default image
                    showDefaultImage(imageView, null, Name);
                }
            }).start();
        }).addOnFailureListener(e -> {
            e.printStackTrace();
            // In case of failure, handle it by showing a default image
            showDefaultImage(imageView, e, Name);
        });
    }

    private void showDefaultImage(ImageView imageView, Exception e, String Name) {
        // Check if the username exists and generate the default image based on it
        // Get the username from the profile (or any other source if needed)
        String username = Name; // You might need to pass this to the method if it's available

        // Generate a default image based on the first character of the username
        char firstChar = Character.toLowerCase(username.charAt(0));
        StorageReference defaultImageRef;

        if ("abcdefghi".indexOf(firstChar) != -1) {
            defaultImageRef = FirebaseStorage.getInstance().getReference().child("profile_images/abcdefghi.jpg");
        } else if ("jklmnopqr".indexOf(firstChar) != -1) {
            defaultImageRef = FirebaseStorage.getInstance().getReference().child("profile_images/jklmnopqr.jpg");
        } else if ("stuvwxyz".indexOf(firstChar) != -1) {
            defaultImageRef = FirebaseStorage.getInstance().getReference().child("profile_images/stuvwxyz.jpg");
        } else {
            // If the username doesn't match any known range, use a fallback default image
            defaultImageRef = FirebaseStorage.getInstance().getReference().child("profile_images/default.jpg");
        }

        defaultImageRef.getDownloadUrl().addOnSuccessListener(defaultUri -> {
            new Thread(() -> {
                try {
                    InputStream inputStream = new java.net.URL(defaultUri.toString()).openStream();
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                    // Set the default image to the ImageView
                    ((Activity) context).runOnUiThread(() -> imageView.setImageBitmap(bitmap));
                } catch (IOException ex) {
                    ex.printStackTrace();
                    ((Activity) context).runOnUiThread(() -> imageView.setImageResource(R.drawable.profile_picture_drawable));
                }
            }).start();
        }).addOnFailureListener(defaultError -> {
            defaultError.printStackTrace();
            // Fallback to a basic default resource image if all else fails
            ((Activity) context).runOnUiThread(() -> imageView.setImageResource(R.drawable.profile_picture_drawable));
        });
    }
    /**
     * Opens the ProfileDetailFragment and with the profile
     * Replaces the current fragment with the ProfileDetailFragment
     *
     * @param profile profile to be displayed
     *
     */
    private void openProfileDetailFragment(Profile profile) {

        ProfileDetailFragment detailFragment = ProfileDetailFragment.newInstance(profile);

        FragmentTransaction transaction = ((FragmentActivity) context).getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.profilefragment_container, detailFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

}
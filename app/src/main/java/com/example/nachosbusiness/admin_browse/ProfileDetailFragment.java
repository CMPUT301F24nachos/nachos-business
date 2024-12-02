package com.example.nachosbusiness.admin_browse;

import static android.view.View.GONE;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.nachosbusiness.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.io.InputStream;

/**
 * Fragment to display and manage details of a user's profile.
 * View profile information, edit the profile, and delete the profile or profile image.
 *
 */
public class ProfileDetailFragment extends Fragment {

    private Profile profile;
    private Boolean ImageGen;



    public ProfileDetailFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if (getArguments() != null) {
            profile = (Profile) getArguments().getSerializable("profile");
        }
    }
    /**
     * Sets up the view.
     * Sets up click listeners for buttons like back, edit, remove profile image, and remove profile.
     *
     * @param view   root view of fragment
     * @param savedInstanceState Saved state for fragment
     */
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageButton backButton = view.findViewById(R.id.back);
        ImageButton editButton = view.findViewById(R.id.edit);
        Button removeProfileImage = view.findViewById(R.id.remove_image);
        Button removeProfile = view.findViewById(R.id.remove_profile);

        ImageView profileImage = view.findViewById(R.id.profile);
        TextView profileName = view.findViewById(R.id.Username);
        TextView email = view.findViewById(R.id.EmailValue);
        TextView phonenum = view.findViewById(R.id.PhoneValue);
        TextView EditMode = view.findViewById(R.id.editmode);
        EditMode.setVisibility(GONE);

        removeProfileImage.setVisibility(GONE);
        removeProfile.setVisibility(GONE);

        // Set up the back button with a confirmation dialog and toast
        backButton.setOnClickListener(v -> {
            new AlertDialog.Builder(getActivity())
                    .setTitle("Return to Profile List")
                    .setMessage("Do you want to go back to the profile list?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // Navigate back to Profile List Fragment
                            // From: https://stackoverflow.com/questions/25350397/android-return-to-previous-fragment-on-back-press
                            assert getFragmentManager() != null;
                            getFragmentManager().popBackStack();  // This pops the current fragment off the back stack
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss(); // Close the dialog without action
                        }
                    })
                    .show();
        });

        // Set up the edit button with a confirmation dialog and toast
        editButton.setOnClickListener(v -> {
            new AlertDialog.Builder(getActivity())
                    .setTitle("Enter Edit Mode")
                    .setMessage("Do you want to enter edit mode?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                            removeProfileImage.setVisibility(View.VISIBLE);
                            removeProfile.setVisibility(View.VISIBLE);
                            EditMode.setVisibility(View.VISIBLE);
                            editButton.setVisibility(GONE);
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss(); // Close the dialog without action
                        }
                    })
                    .show();
        });


        // button to remove profile image
        removeProfileImage.setOnClickListener(v -> {
            new AlertDialog.Builder(getActivity())
                    .setTitle("Remove Profile Image")
                    .setMessage("Do you want to remove the users profile image?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            if(ImageGen){
                                Toast.makeText(getActivity(), "Cannot Remove - Auto Generated", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                removeProfileImageFromFirebase(profile.getAndroid_id());
                            }
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .show();
        });

        // Remove Profile
        removeProfile.setOnClickListener(v -> {
            new AlertDialog.Builder(getActivity())
                    .setTitle("Remove Profile ")
                    .setMessage("Do you want to remove the users profile?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            deleteProfileFromFirestore(profile.getAndroid_id());
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss(); // Close the dialog without action
                        }
                    })
                    .show();
        });
    }

    /**
     * Inflates fragemnt and set profile details
     *
     * @param inflater  LayoutInflater object to inflate the layout.
     * @param container parent view that fragment attaches to
     * @param savedInstanceState Saved state for fragment
     * @return fragment view
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.admin_edit_profile, container, false);


        ImageView profileImage = view.findViewById(R.id.profile);
        TextView profileName = view.findViewById(R.id.Username);
        TextView email = view.findViewById(R.id.EmailValue);
        TextView phonenum = view.findViewById(R.id.PhoneValue);


        if (profile != null) {
            profileName.setText(profile.getName());
            String Name = profile.getName();
            loadProfileImage(profile.getAndroid_id(), profileImage, Name);
            email.setText(profile.getEmail());
            if (profile.getPhonenum() != null && !profile.getPhonenum().isEmpty()) {
                phonenum.setText(profile.getPhonenum());
            } else {
                phonenum.setText("Not Added");
            }
        }

        return view;
    }

    /**
     * Loads the profile image from Firebase Storage
     *
     * @param androidId The unique ID for the profile
     * @param imageView ImageView for profile image
     * @param Name Users username
     */
    private void loadProfileImage(String androidId, ImageView imageView, String Name) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference profileImageRef = storageRef.child("profile_images/" + androidId + ".jpg");

        profileImageRef.getDownloadUrl().addOnSuccessListener(uri -> {
            new Thread(() -> {
                try {
                    InputStream inputStream = new java.net.URL(uri.toString()).openStream();
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    getActivity().runOnUiThread(() -> imageView.setImageBitmap(bitmap));
                    ImageGen = false;
                } catch (IOException e) {
                    e.printStackTrace();
                    // In case of an error, handle it by showing a default image
                    showDefaultImage(imageView, null,Name);
                }
            }).start();
        }).addOnFailureListener(e -> {
            e.printStackTrace();
            // In case of failure, handle it by showing a default image
            showDefaultImage(imageView, e, Name);
        });
    }

    /**
     * Show autogen iamge
     *
     *
     * @param imageView ImageView for profile image
     * @param Name Users username
     */
    private void showDefaultImage(ImageView imageView, Exception e, String Name) {

        String username = Name;
        char firstChar = Character.toLowerCase(username.charAt(0));
        StorageReference defaultImageRef;

        if ("abcdefghi".indexOf(firstChar) != -1) {
            defaultImageRef = FirebaseStorage.getInstance().getReference().child("profile_images/abcdefghi.jpg");
            ImageGen = true;
        } else if ("jklmnopqr".indexOf(firstChar) != -1) {
            defaultImageRef = FirebaseStorage.getInstance().getReference().child("profile_images/jklmnopqr.jpg");
            ImageGen = true;
        } else if ("stuvwxyz".indexOf(firstChar) != -1) {
            defaultImageRef = FirebaseStorage.getInstance().getReference().child("profile_images/stuvwxyz.jpg");
            ImageGen = true;
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
                    getActivity().runOnUiThread(() -> imageView.setImageBitmap(bitmap));
                } catch (IOException ex) {
                    ex.printStackTrace();
                    getActivity().runOnUiThread(() -> imageView.setImageResource(R.drawable.profile_picture_drawable));
                }
            }).start();
        }).addOnFailureListener(defaultError -> {
            defaultError.printStackTrace();
            // Fallback to a basic default resource image if all else fails
            getActivity().runOnUiThread(() -> imageView.setImageResource(R.drawable.profile_picture_drawable));
        });
    }
    /**
     * New ProfileDetailFragment
     *
     * @param profile profile to pass to the fragment.
     * @return A new instance of ProfileDetailFragment with the passed profile
     */

    public static ProfileDetailFragment newInstance(Profile profile) {
        ProfileDetailFragment fragment = new ProfileDetailFragment();

        Bundle args = new Bundle();
        args.putSerializable("profile", profile);  // Put the profile object in the Bundle
        fragment.setArguments(args);  // Set the arguments to the fragment

        return fragment;
    }

    /**
     * Removes the profile image from Firebase Storage (and view)
     *
     * @param androidId Android ID of the profile to remove the image for.
     */
    private void removeProfileImageFromFirebase(String androidId) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference profileImageRef = storageRef.child("profile_images/" + androidId + ".jpg");

        profileImageRef.delete()
                .addOnSuccessListener(aVoid -> {
                    // Success: Image deleted from Firebase Storage
                    Toast.makeText(getActivity(), "Profile image removed", Toast.LENGTH_SHORT).show();
                    // Remove the image from UI as well
                    ImageView profileImage = getView().findViewById(R.id.profile);
                    profileImage.setImageResource(R.drawable.profile_picture_drawable); // Use a default placeholder image
                })
                .addOnFailureListener(e -> {
                    // Failure: Something went wrong
                    Toast.makeText(getActivity(), "Failed to remove profile image", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                });
    }
    /**
     * Deletes the profile
     *
     * @param androidId Android ID of the profile to delete.
     */
    private void deleteProfileFromFirestore(String androidId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("entrants")
                .whereEqualTo("android_id", androidId)  // Find document id based on android id
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (!task.getResult().isEmpty()) {
                            DocumentSnapshot document = task.getResult().getDocuments().get(0);
                            DocumentReference profileRef = db.collection("entrants").document(document.getId());
                            profileRef.delete()
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(getActivity(), "Profile deleted successfully", Toast.LENGTH_SHORT).show();
                                        getFragmentManager().popBackStack();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(getActivity(), "Failed to delete profile", Toast.LENGTH_SHORT).show();
                                    });
                        } else {
                            Toast.makeText(getActivity(), "Profile not found", Toast.LENGTH_SHORT).show();
                        }
                    } else {

                        Toast.makeText(getActivity(), "Error querying profile", Toast.LENGTH_SHORT).show();
                    }
                });
    }





}
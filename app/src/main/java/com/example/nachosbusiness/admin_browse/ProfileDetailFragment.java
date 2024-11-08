package com.example.nachosbusiness.admin_browse;

import static android.view.View.GONE;

import static com.google.firebase.appcheck.internal.util.Logger.TAG;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
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

import com.example.nachosbusiness.DBManager;
import com.example.nachosbusiness.R;
import com.example.nachosbusiness.ShowProfile;
import com.google.firebase.firestore.DocumentReference;
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
                    .setMessage("Do you want to remove ths users profile image?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            removeProfileImageFromFirebase(profile.getAndroid_id());
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss(); // Close the dialog without action
                        }
                    })
                    .show();
        });

        // Remove Profile Image
        removeProfile.setOnClickListener(v -> {
            new AlertDialog.Builder(getActivity())
                    .setTitle("Remove Profile ")
                    .setMessage("Do you want to remove ths users profile?")
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

        View view = inflater.inflate(R.layout.view_profile, container, false);


        ImageView profileImage = view.findViewById(R.id.profile);
        TextView profileName = view.findViewById(R.id.Username);
        TextView email = view.findViewById(R.id.EmailValue);
        TextView phonenum = view.findViewById(R.id.PhoneValue);


        if (profile != null) {
            profileName.setText(profile.getName());
            loadProfileImage(profile.getAndroid_id(), profileImage);
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
     */
    private void loadProfileImage(String androidId, ImageView imageView) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference profileImageRef = storageRef.child("profile_images/" + androidId + ".jpg");

        profileImageRef.getDownloadUrl().addOnSuccessListener(uri -> {
            new Thread(() -> {
                try {
                    InputStream inputStream = new java.net.URL(uri.toString()).openStream();
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    getActivity().runOnUiThread(() -> imageView.setImageBitmap(bitmap));
                } catch (IOException e) {
                    e.printStackTrace();

                }
            }).start();
        }).addOnFailureListener(e -> {
            e.printStackTrace();
            getActivity().runOnUiThread(() -> {
                imageView.setImageResource(R.drawable.profile_picture_drawable); // Placeholder image
                Toast.makeText(getActivity(), "No profile image found", Toast.LENGTH_SHORT).show();
            });
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
        DocumentReference profileRef = db.collection("entrants").document(androidId);  // Assuming profiles is the collection name

        profileRef.delete()
                .addOnSuccessListener(aVoid -> {
                    // Profile successfully deleted
                    Toast.makeText(getActivity(), "Profile deleted successfully", Toast.LENGTH_SHORT).show();

                    // Optionally, navigate back to the profile list fragment
                    getFragmentManager().popBackStack();  // This pops the current fragment off the back stack
                })
                .addOnFailureListener(e -> {
                    // Error occurred while deleting
                    Toast.makeText(getActivity(), "Failed to delete profile", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                });
    }




}

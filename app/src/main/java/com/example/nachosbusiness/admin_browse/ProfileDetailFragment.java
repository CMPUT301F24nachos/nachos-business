package com.example.nachosbusiness.admin_browse;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.nachosbusiness.R;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.io.InputStream;

public class ProfileDetailFragment extends Fragment {

    private Profile profile;

    // Empty constructor is required for fragments
    public ProfileDetailFragment() {
        // Required empty constructor for fragment
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retrieve the Profile from arguments passed using setArguments()
        if (getArguments() != null) {
            profile = (Profile) getArguments().getSerializable("profile");
        }
    }
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageButton backButton = view.findViewById(R.id.back);
        ImageButton editButton = view.findViewById(R.id.edit);

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
                            // Show toast for edit mode
                            Toast.makeText(getActivity(), "Edit Mode", Toast.LENGTH_SHORT).show();

                            // Add your logic here for entering edit mode (if applicable)
                            // For example, enable editing of TextViews, ImageViews, etc.
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.view_profile, container, false);

        // Set profile details to views
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

    // Method to load the profile image from Firebase Storage
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
        });
    }

    // Static method to create an instance of the fragment with the Profile object
    public static ProfileDetailFragment newInstance(Profile profile) {
        ProfileDetailFragment fragment = new ProfileDetailFragment();

        // Create a Bundle and pass the profile object (must be Serializable)
        Bundle args = new Bundle();
        args.putSerializable("profile", profile);  // Put the profile object in the Bundle
        fragment.setArguments(args);  // Set the arguments to the fragment

        return fragment;
    }
}

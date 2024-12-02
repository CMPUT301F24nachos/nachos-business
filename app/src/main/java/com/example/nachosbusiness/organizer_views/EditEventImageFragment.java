package com.example.nachosbusiness.organizer_views;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.nachosbusiness.DBManager;
import com.example.nachosbusiness.R;

import java.io.IOException;

/**
 * A fragment that allows the user to edit the event's image. The user can upload a new image, delete the current one, or cancel the changes.
 * This fragment interacts with the database to fetch and update the event image and provides an interface for selecting and saving the image.
 */
public class EditEventImageFragment extends Fragment {
    private DBManager dbManager;

    // UI elements
    private ImageButton imageButton;
    private ImageView eventImage;
    private ImageButton closeButton;
    private Button saveChangesButton;
    private Button cancelChangesButton;
    private Uri selectedImageUri;
    private boolean isImageMarkedForDeletion = false;
    private boolean isImageMarkedForUpload = false;

    /**
     * Called to inflate the fragment's view. Sets up the layout for the event image editing.
     * 
     * @param inflater The LayoutInflater object to inflate the view.
     * @param container The parent view that the fragment's UI should be attached to.
     * @param savedInstanceState A Bundle containing the previous state of the fragment.
     * @return The root view for the fragment's UI.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the fragment layout
        return inflater.inflate(R.layout.fragment_event_image, container, false);
    }

    /**
     * Called after the view has been created. This method initializes UI elements and sets up listeners for user interactions.
     * It also handles retrieving the event ID and fetching the current event image from the database.
     * 
     * @param view The root view for this fragment.
     * @param savedInstanceState A Bundle containing the previous state of the fragment.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize UI elements
        imageButton = view.findViewById(R.id.imageButton);
        eventImage = view.findViewById(R.id.eventImage);
        closeButton = view.findViewById(R.id.closeButton);
        saveChangesButton = view.findViewById(R.id.signUpButton);
        cancelChangesButton = view.findViewById(R.id.cancelButton);
        dbManager = new DBManager("events");

        String eventId;
        if (getArguments() != null) {
            eventId = getArguments().getString("POSTER_PATH");
        } else {
            eventId = null;
        }

        if (eventId != null) {
            Toast.makeText(requireContext(), "Event ID: " + eventId, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(requireContext(), "Event ID not found", Toast.LENGTH_SHORT).show();
        }

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageChooser();
                isImageMarkedForUpload = true;
            }
        });

        dbManager.getEventImage(eventId, eventImage, requireContext(), () -> {
            eventImage.setVisibility(View.VISIBLE);
            closeButton.setVisibility(View.VISIBLE);
        });

        closeButton.setOnClickListener(v -> {
            eventImage.setVisibility(View.GONE);
            closeButton.setVisibility(View.GONE);
            imageButton.setVisibility(View.VISIBLE);
            isImageMarkedForDeletion = true;
        });

        saveChangesButton.setOnClickListener(v -> {
            if (isImageMarkedForDeletion && eventId != null) {
                dbManager.deleteEventImage(eventId, requireContext());
                isImageMarkedForDeletion = false;
            }

            if (isImageMarkedForUpload) {
                dbManager.uploadEventImage(getContext(), eventId, selectedImageUri);
                isImageMarkedForUpload = false;
            }

            requireActivity().getSupportFragmentManager().popBackStack();
        });

        cancelChangesButton.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().popBackStack();
        });
    }

    /**
     * Activity result launcher for selecting an image from the gallery.
     * Handles the result when the user picks an image, and displays it in the ImageView.
     */
    ActivityResultLauncher<Intent> launchSomeActivity = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null && data.getData() != null) {
                        selectedImageUri = data.getData();
                        try {
                            // Use requireContext() to get the content resolver
                            Bitmap selectedImageBitmap = MediaStore.Images.Media.getBitmap(
                                    requireContext().getContentResolver(), selectedImageUri);

                            eventImage.setImageBitmap(selectedImageBitmap);
                            eventImage.setVisibility(View.VISIBLE);
                            closeButton.setVisibility(View.VISIBLE);

                        } catch (IOException e) {
                            e.printStackTrace();
                            Toast.makeText(requireContext(), "Failed to load image", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });

    /**
     * Opens the image chooser to allow the user to select an image from their device.
     * This method launches an intent to pick an image from the gallery.
     */
    private void imageChooser() {
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        launchSomeActivity.launch(i);
    }
}

package com.example.nachosbusiness.users;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.telephony.PhoneNumberUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.nachosbusiness.DBManager;
import com.example.nachosbusiness.R;

import java.io.IOException;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * A fragment that allows the user to update their profile information, including their username, email, phone number,
 * and profile image. The user can select a new profile image, update their details, and save or cancel the changes.
 * The profile information is validated before being submitted.
 */
public class UpdateProfile extends Fragment {
    private DBManager dbManager;
    private EditText phoneNumber;
    private EditText email;
    private EditText userName;
    private Uri selectedImageUri;
    private ImageView profileImage;
    private ImageButton closeButton;
    private  ImageButton imageButton;
    private boolean isImageMarkedForDeletion = false;
    private boolean isImageMarkedForUpload = false;

    /**
     * Called when the fragment's view is created. This method initializes the UI components and sets up the necessary listeners
     * for user interactions such as saving changes, cancelling, and choosing a profile image.
     * 
     * @param inflater The LayoutInflater object used to inflate the fragment's view.
     * @param container The container (if any) that will hold the fragment's view.
     * @param savedInstanceState A bundle containing the saved instance state of the fragment (if any).
     * @return The inflated view for the fragment.
     */
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.activity_update_profile_final, container, false);
    }

    /**
     * Called after the fragment's view has been created. This method retrieves the data from the arguments bundle,
     * initializes the UI components, and sets up the listeners for saving or cancelling changes, selecting a profile image,
     * and removing the current profile image.
     * 
     * @param view The root view of the fragment.
     * @param savedInstanceState A bundle containing the saved instance state of the fragment (if any).
     */
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button saveButton = view.findViewById(R.id.signUpButton);
        Button cancelButton = view.findViewById(R.id.cancelButton);

        phoneNumber = view.findViewById(R.id.editTextPhone);
        email = view.findViewById(R.id.editTextTextEmailAddress);
        userName = view.findViewById(R.id.editTextUsername);
        profileImage = view.findViewById(R.id.profileImage);
        closeButton = view.findViewById(R.id.closeButton);
        imageButton = view.findViewById(R.id.imageButton);

        dbManager = new DBManager("entrants");
        String android_id = Settings.Secure.getString(requireContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        dbManager.getProfileImage(android_id, profileImage, requireContext(), () -> {
            profileImage.setVisibility(View.VISIBLE);
            closeButton.setVisibility(View.VISIBLE);
        });

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageChooser();
                isImageMarkedForUpload = true;
            }
        });

        closeButton.setOnClickListener(v -> {
            profileImage.setVisibility(View.GONE);
            closeButton.setVisibility(View.GONE);
            selectedImageUri = null;
            isImageMarkedForDeletion = true;
        });

        // Retrieve data from the Bundle
        Bundle arguments = getArguments();
        if (arguments != null) {
            String name = arguments.getString("name");
            String emailAddress = arguments.getString("email");
            String phone = arguments.getString("phone");

            userName.setText(name);
            email.setText(emailAddress);
            phoneNumber.setText(phone);
        }

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isNameValid = isValidUsername(userName, "Username is Required!");
                boolean isEmailValid = isValidEmail(email);
                boolean isPhoneNumber = isValidPhoneNumber(phoneNumber);

                String name = userName.getText().toString().trim();
                String emailAddress = email.getText().toString().trim();
                String phone = phoneNumber.getText().toString().trim();

                if (isNameValid && isEmailValid && isPhoneNumber) {
                    User updatedUser = new User(android_id, name, emailAddress, phone);
                    dbManager.setEntry(android_id, updatedUser);
                    if (isImageMarkedForDeletion) {
                        dbManager.deleteProfileImage(android_id, (ShowProfile) requireContext());
                        isImageMarkedForDeletion = false;
                    }

                    if (isImageMarkedForUpload) {
                        dbManager.uploadProfileImage(getContext(), android_id, selectedImageUri);
                        isImageMarkedForUpload = false;
                    }

                    requireActivity().finish();
                    startActivity(new Intent(requireContext(), ShowProfile.class));
                }
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requireActivity().getSupportFragmentManager().popBackStack();
            }
        });
    }

    /**
     * Verifies that the provided username is not empty and starts with a letter.
     * If the validation fails, a Toast is displayed with an error message.
     * 
     * @param inputText The EditText input field containing the username.
     * @param errorMessage The error message to display if the validation fails.
     * @return True if the username is valid, otherwise False.
     */
    private boolean isValidUsername(EditText inputText, String e){
        String text = Objects.requireNonNull(inputText.getText()).toString().trim();
        if (text.isEmpty()) {
            Toast.makeText(requireContext(), e, Toast.LENGTH_SHORT).show();
            return false;
        } else if (!Character.isLetter(text.charAt(0))) {
            Toast.makeText(requireContext(), "Username must start with a letter.", Toast.LENGTH_SHORT).show();
            return false;

        }
        return true;
    }

    /**
     * Verifies that the provided phone number is valid according to the Canadian phone number format.
     * If the phone number is invalid, a Toast is displayed with an error message.
     * 
     * @param inputText The EditText input field containing the phone number.
     * @return True if the phone number is valid, otherwise False.
     */
    private boolean isValidPhoneNumber(EditText inputText){
        String text = Objects.requireNonNull(inputText.getText()).toString().trim();
        if (!inputText.getText().toString().isEmpty())
        {
            String phone = PhoneNumberUtils.formatNumber(inputText.getText().toString(), "CA");
            if (phone == null)
            {
                Toast.makeText(requireContext(), "Invalid phone number", Toast.LENGTH_LONG).show();
                return false;
            }
        }
        return true;
    }

    /**
     * Verifies that the provided email address is valid according to a regular expression pattern.
     * If the email is invalid, a Toast is displayed with an error message.
     * 
     * @param email The EditText input field containing the email address.
     * @return True if the email is valid, otherwise False.
     */
    private boolean isValidEmail(EditText email) {
        if (email == null || email.getText().toString().trim().isEmpty()) {
            Toast.makeText(requireContext(), "Enter an email", Toast.LENGTH_SHORT).show();
            return false;
        }

        String regexPattern = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        if (!Pattern.compile(regexPattern).matcher(email.getText().toString().trim()).matches()) {
            Toast.makeText(requireContext(), "Invalid email format", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    /**
     * ActivityResultLauncher to handle the result of the image selection activity.
     * This updates the profile image with the selected image and displays it in the profile image view.
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

                            profileImage.setImageBitmap(selectedImageBitmap);
                            profileImage.setVisibility(View.VISIBLE);
                            closeButton.setVisibility(View.VISIBLE);

                        } catch (IOException e) {
                            e.printStackTrace();
                            Toast.makeText(requireContext(), "Failed to load image", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });

    /**
     * Launches an activity to choose an image from the device's storage.
     * The selected image is then displayed in the profile image view.
     */
    private void imageChooser() {
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        launchSomeActivity.launch(i);
    }
}

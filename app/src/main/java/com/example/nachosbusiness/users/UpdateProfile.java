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
 * This fragment is where user's can edit their profile information, update their user profile,
 * or navigate back to the view page. Any changes will be updated in the DB.
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


    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.activity_update_profile_final, container, false);
    }

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
     * Verifies the required fields have values in them. A toast showing the missing value error.
     * @param inputText the TextEditInputText value to check
     * @param e error message that will be shown in the toast
     * @return True if the inputText is not empty, returns False if is empty
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

    private void imageChooser() {
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        launchSomeActivity.launch(i);
    }
}
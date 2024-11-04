package com.example.nachosbusiness;

import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ShowProfile extends AppCompatActivity {

    private DBManager dbManager;
    private TextView userName;
    private TextView email;
    private TextView phoneNumber;
    private ImageView selectedImageUri;
    private Bundle userProfileBundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_update);

        dbManager = new DBManager("entrants");

        userName = findViewById(R.id.user_name);
        email = findViewById(R.id.user_email);
        phoneNumber = findViewById(R.id.user_phone);
        selectedImageUri = findViewById(R.id.profileImage);
        RelativeLayout updateProfileButton = findViewById(R.id.update_profile_button);

        setDefaultValues();

        String android_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        dbManager.getUser(android_id, new DBManager.EntryRetrievalCallback() {
            @Override
            public void onEntryRetrieved(String name, String emailAddress, String phone) {
                userName.setText(name);
                email.setText(emailAddress);
                phoneNumber.setText(phone);

                // Now that we have the user details, fetch and display the profile image
                dbManager.getProfileImage(android_id, selectedImageUri, ShowProfile.this);

                userProfileBundle = new Bundle();
                userProfileBundle.putString("android_id", android_id);
                userProfileBundle.putString("name", name);
                userProfileBundle.putString("email", emailAddress);
                userProfileBundle.putString("phone", phone);
            }

            @Override
            public void onEntryNotFound() {
                userName.setText("User not found");
            }

            @Override
            public void onError(String error) {
                userName.setText("Error fetching user");
            }
        });

        updateProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle update profile logic here
            }
        });
    }

    private void setDefaultValues() {
        String defaultUserName = "Name not set";
        String defaultEmail = "Email not set";
        String defaultPhone = "Phone number not set";

        userName.setText(defaultUserName);
        email.setText(defaultEmail);
        phoneNumber.setText(defaultPhone);
    }
}

package com.example.nachosbusiness.users;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.nachosbusiness.DBManager;
import com.example.nachosbusiness.Dashboard;
import com.example.nachosbusiness.R;

/**
 * This activity displays the user's profile information, including their name, email, phone number, and profile image.
 * Users can navigate to the profile update section or return to the dashboard. The profile update section is implemented
 * as a fragment and is shown within this activity when the user chooses to update their profile.
 * 
 * The activity retrieves the user's profile information from a database using the DBManager and displays it to the user.
 * If the user chooses to update their profile, the corresponding fragment is displayed.
 */
public class ShowProfile extends AppCompatActivity {

    private DBManager dbManager;
    private TextView userName;
    private TextView email;
    private TextView phoneNumber;
    private ImageView profileImage;
    private Bundle userProfileBundle;
    private ImageButton buttonHome;

    /**
     * Called when the activity is first created. This method sets up the layout and initializes all the UI components.
     * It retrieves the user's profile data from the database and displays it, along with the user's profile image.
     * 
     * @param savedInstanceState A Bundle containing the previous state of the activity, if it exists.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_update);

        dbManager = new DBManager("entrants");

        userName = findViewById(R.id.user_name);
        email = findViewById(R.id.user_email);
        phoneNumber = findViewById(R.id.user_phone);
        profileImage = findViewById(R.id.profileImage);
        RelativeLayout updateProfileButton = findViewById(R.id.update_profile_button);
        View fragmentContainer = findViewById(R.id.organizer_events_container); // Reference to fragment container

        buttonHome = findViewById(R.id.button_profile_home);
        // Set the fragment container to GONE initially
        fragmentContainer.setVisibility(View.GONE);

        setDefaultValues();

        String android_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        dbManager.getUser(android_id, new DBManager.EntryRetrievalCallback() {
            @Override
            public void onEntryRetrieved(String name, String emailAddress, String phone) {
                userName.setText(name);
                email.setText(emailAddress);
                phoneNumber.setText(phone);

                // Gets profile image using the username
                dbManager.getProfileImageExtra(android_id, name, profileImage, ShowProfile.this, new DBManager.ProfileImageCallback() {
                    @Override
                    public void onImageLoaded(Bitmap bitmap) {
                        profileImage.setVisibility(View.VISIBLE); // Show image if loaded
                    }

                    @Override
                    public void onImageLoadFailed(Exception e) {
                        profileImage.setVisibility(View.GONE); // Hide image if loading failed
                    }
                });

                // Prepare the Bundle
                userProfileBundle = new Bundle();
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
                navigateToUpdateProfile();
            }
        });

        buttonHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent eventIntent = new Intent(ShowProfile.this, Dashboard.class);
                eventIntent.putExtra("name", userName.getText());
                startActivity(eventIntent);
            }
        });

    }

    /**
     * Navigates to the profile update section by replacing the current view with the UpdateProfile fragment.
     * The user's profile data is passed to the fragment using a Bundle.
     */
    private void navigateToUpdateProfile() {
        if (userProfileBundle == null) {
            Log.e("ShowProfile", "User profile bundle is null.");
            return;
        }

        UpdateProfile updateProfileFragment = new UpdateProfile();
        updateProfileFragment.setArguments(userProfileBundle);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.organizer_events_container, updateProfileFragment)
                .addToBackStack(null)
                .commit();

        findViewById(R.id.organizer_events_container).setVisibility(View.VISIBLE);
    }

    /**
     * Sets default values for the user's profile information in case no data is available.
     * This method is used to populate the profile fields when the data is not yet fetched or unavailable.
     */
    private void setDefaultValues() {
        String defaultUserName = "Name not set";
        String defaultEmail = "Email not set";
        String defaultPhone = "Phone number not set";

        userName.setText(defaultUserName);
        email.setText(defaultEmail);
        phoneNumber.setText(defaultPhone);
    }
}

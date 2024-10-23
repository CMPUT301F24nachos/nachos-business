package com.example.nachosbusiness;

import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.nachosbusiness.users.UserManager;

public class UpdateProfile extends AppCompatActivity {
    private UserManager userManager;
    private TextView userName;
    private TextView email;
    private TextView phoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_update);

        userManager = new UserManager();

        userName = findViewById(R.id.user_name);
        email = findViewById(R.id.user_email);
        phoneNumber = findViewById(R.id.user_phone);
        RelativeLayout updateProfileButton = findViewById(R.id.update_profile_button);

        setDefaultValues();

        String android_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        userManager.getUser(android_id, new UserManager.UserCallback() {
            @Override
            public void onUserRetrieved(String name, String emailAddress, String phone) {
                userName.setText(name);
                email.setText(emailAddress);
                phoneNumber.setText(phone);
            }

            @Override
            public void onUserNotFound() {
            }

            @Override
            public void onError(String error) {
                userName.setText("Error fetching user");

            }
        });

        updateProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Logic to update the profile (if needed)
                Toast.makeText(getApplicationContext(), "Profile Updated!", Toast.LENGTH_SHORT).show();
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

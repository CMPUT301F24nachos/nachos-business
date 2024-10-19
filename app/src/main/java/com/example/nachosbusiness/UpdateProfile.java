package com.example.nachosbusiness;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class UpdateProfile extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_update); // Make sure this matches the XML file name

        // Initialize TextViews from the layout
        TextView userName = findViewById(R.id.john_doe);
        TextView email = findViewById(R.id.john_doe_gm);
        TextView phoneNumber = findViewById(R.id.some_id);

        // Initialize Update Profile button
        Button updateProfileButton = findViewById(R.id.update_profile_button);

        // Click listener for the Update Profile button
        updateProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Profile Updated!", Toast.LENGTH_SHORT).show();
                // Add the update logic here
            }
        });

        // Example usage of the TextViews
        userName.setText("John Doe");
        email.setText("john.doe@gmail.com");
        phoneNumber.setText("+1 (123)-456-7890");
    }
}

package com.example.nachosbusiness;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class UpdateProfile extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_update);

        TextView userName = findViewById(R.id.john_doe);
        TextView email = findViewById(R.id.john_doe_gm);
        TextView phoneNumber = findViewById(R.id.some_id);
        RelativeLayout updateProfileButton = findViewById(R.id.update_profile_button);

        updateProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Profile Updated!", Toast.LENGTH_SHORT).show();
                // Add the update here
            }
        });

//        userName.setText("John Doe");
//        email.setText("john.doe@gmail.com");
//        phoneNumber.setText("+1 (123)-456-7890");
    }
}

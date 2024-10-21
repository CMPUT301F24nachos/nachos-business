package com.example.nachosbusiness.users;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.telephony.PhoneNumberUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.nachosbusiness.R;

import java.io.IOException;
import java.util.regex.Pattern;

public class RegistrationActivity extends AppCompatActivity {
    private UserManager userManager;
    private ImageView imageView;

    // Using OWASP email validation: https://www.baeldung.com/java-email-validation-regex
    private boolean isValidEmail(String email) {
        String regexPattern = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return Pattern.compile(regexPattern).matcher(email).matches();
    }

    // Altered code from https://www.geeksforgeeks.org/how-to-select-an-image-from-gallery-in-android/
    ActivityResultLauncher<Intent> launchSomeActivity = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null && data.getData() != null) {
                        Uri selectedImageUri = data.getData();
                        try {
                            Bitmap selectedImageBitmap = MediaStore.Images.Media.getBitmap(
                                    this.getContentResolver(), selectedImageUri);

                            imageView.setImageBitmap(selectedImageBitmap); // Set image to ImageView
                            imageView.setVisibility(View.VISIBLE); // Make ImageView visible
                        } catch (IOException e) {
                            e.printStackTrace();
                            Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registration);

        userManager = new UserManager();
        imageView = findViewById(R.id.imageView);

        EditText editUsername = findViewById(R.id.editTextText);
        EditText editEmail = findViewById(R.id.editTextTextEmailAddress);
        EditText editPhone = findViewById(R.id.editTextPhone);
        ImageButton profileButton = findViewById(R.id.imageButton);
        Button signUpButton = findViewById(R.id.signUpButton);


        profileButton.setOnClickListener(view -> imageChooser());



        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageChooser();
            }
        });

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username  = editUsername.getText().toString();
                String email = editEmail.getText().toString();
                String phone = PhoneNumberUtils.formatNumber(editPhone.getText().toString(), "+1");

                if (username.isEmpty() || email.isEmpty())
                {
                    Toast.makeText(RegistrationActivity.this, "Please fill out username and email", Toast.LENGTH_LONG).show();
                }
                else if (!isValidEmail(email)) {
                    Toast.makeText(RegistrationActivity.this, "Invalid email format", Toast.LENGTH_LONG).show();
                }
                else if (phone == null || phone.isEmpty())
                {
                    String android_id = Settings.Secure.getString(RegistrationActivity.this.getContentResolver(), Settings.Secure.ANDROID_ID);
                    userManager.registerUser(android_id, username, email);
                }
                else
                {
                    String android_id = Settings.Secure.getString(RegistrationActivity.this.getContentResolver(), Settings.Secure.ANDROID_ID);
                    userManager.registerUser(android_id, username, email, phone);
                }
            }
        });
    }
}

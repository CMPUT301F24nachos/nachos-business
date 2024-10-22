package com.example.nachosbusiness.users;

import android.os.Bundle;
import android.provider.Settings;
import android.telephony.PhoneNumberUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.nachosbusiness.DBManager;
import com.example.nachosbusiness.R;

public class RegistrationActivity extends AppCompatActivity {
    private DBManager dbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registration);

        dbManager = new DBManager("entrants");

        EditText editUsername = findViewById(R.id.editTextText);
        EditText editEmail = findViewById(R.id.editTextTextEmailAddress);
        EditText editPhone = findViewById(R.id.editTextPhone);

        Button signUpButton = findViewById(R.id.signUpButton);

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username  = editUsername.getText().toString();
                String email = editEmail.getText().toString();
                String phone = "";

                if (editPhone.getText().toString().isEmpty())
                {
                    phone = PhoneNumberUtils.formatNumber(editPhone.getText().toString(), "CA");
                    if (phone == null)
                    {
                        Toast.makeText(RegistrationActivity.this, "Invalid phone number", Toast.LENGTH_LONG).show();
                        return;
                    }
                }

                if (username.isEmpty() || email.isEmpty())
                {
                    Toast.makeText(RegistrationActivity.this, "Please fill out username and email", Toast.LENGTH_LONG).show();
                }
                else if (phone.isEmpty())
                {
                    String android_id = Settings.Secure.getString(RegistrationActivity.this.getContentResolver(), Settings.Secure.ANDROID_ID);
                    User user = new User(android_id, username, email);
                    dbManager.addEntry(user);
                }
                else
                {
                    String android_id = Settings.Secure.getString(RegistrationActivity.this.getContentResolver(), Settings.Secure.ANDROID_ID);
                    User user = new User(android_id, username, email, phone);
                    dbManager.addEntry(user);
                }
            }
        });
    }
}

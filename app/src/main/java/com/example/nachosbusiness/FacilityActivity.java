package com.example.nachosbusiness;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;

//TODO: need to check if organizer has a facility, update texts if there is one.
public class FacilityActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.facility);

        Button saveButton = findViewById(R.id.facility_button_save);
        Button cancelButton = findViewById(R.id.facility_button_cancel);

        TextInputEditText facilityName = findViewById(R.id.text_facility_input_name);
        TextInputEditText facilityLocation = findViewById(R.id.text_facility_input_location);
        TextInputEditText facilityDescription = findViewById(R.id.text_facility_input_desc);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isNameValid = validateInput(facilityName, "Facility Name is Required!");
                boolean isLocationValid = validateInput(facilityLocation, "Facility Location is Required!");
                boolean isDescriptionValid = validateInput(facilityDescription, "Facility Description is Required!");

                if (isNameValid && isLocationValid && isDescriptionValid) {
                    saveFacility(facilityName, facilityLocation, facilityDescription);
                    finish();
                }
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    /**
     * Verifies the required fields have values in them. A toast showing the missing value error.
     * @param inputText the TextEditInputText value to check
     * @param e error message that will be shown in the toast
     * @return True if the inputText is not empty, returns False if is empty
     */
    private boolean validateInput(TextInputEditText inputText, String e){
        String text = Objects.requireNonNull(inputText.getText()).toString().trim();
        if (text.isEmpty()) {
            Toast.makeText(getApplicationContext(), e, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

//TODO: Implement firebase code to either update or create a new record
    private void saveFacility(TextInputEditText facilityName,
                              TextInputEditText facilityLocation,
                              TextInputEditText facilityDescription){
        Facility facility = new Facility();
        facility.setName(facilityName.getText().toString());
        facility.setLocation(facilityLocation.getText().toString());
        facility.setInfo(facilityDescription.getText().toString());
    }
}

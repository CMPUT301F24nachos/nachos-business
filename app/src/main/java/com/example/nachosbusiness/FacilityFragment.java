package com.example.nachosbusiness;

import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.nachosbusiness.facilities.Facility;
import com.example.nachosbusiness.facilities.FacilityManager;
import com.example.nachosbusiness.users.RegistrationActivity;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;

//TODO: need to check if organizer has a facility, update texts if there is one.
public class FacilityFragment extends Fragment {

    private FacilityManager facilityManager;
    private String androidID;
    private boolean hasFacility;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        facilityManager = new FacilityManager(androidID);
        androidID = getArguments().getString("androidID");
        //hasFacility = facilityManager.hasFacility();
        return inflater.inflate(R.layout.facility, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button saveButton = view.findViewById(R.id.facility_button_save);
        Button cancelButton = view.findViewById(R.id.facility_button_cancel);

        TextInputEditText facilityName = view.findViewById(R.id.text_facility_input_name);
        TextInputEditText facilityLocation = view.findViewById(R.id.text_facility_input_location);
        TextInputEditText facilityDescription = view.findViewById(R.id.text_facility_input_desc);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isNameValid = validateInput(facilityName, "Facility Name is Required!");
                boolean isLocationValid = validateInput(facilityLocation, "Facility Location is Required!");
                boolean isDescriptionValid = validateInput(facilityDescription, "Facility Description is Required!");

                if (isNameValid && isLocationValid && isDescriptionValid) {
                    if (hasFacility){updateFacility();}
                    else {saveFacility(androidID, facilityName, facilityLocation, facilityDescription);}
                    requireActivity().getSupportFragmentManager().popBackStack();
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
    private boolean validateInput(TextInputEditText inputText, String e){
        String text = Objects.requireNonNull(inputText.getText()).toString().trim();
        if (text.isEmpty()) {
            Toast.makeText(requireContext(), e, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }


    private void updateFacility(){

    }

    private void saveFacility(String androidID,
                              TextInputEditText facilityName,
                              TextInputEditText facilityLocation,
                              TextInputEditText facilityDescription){
        Facility facility = new Facility(androidID,
                facilityName.getText().toString(),
                facilityLocation.getText().toString(),
                facilityDescription.getText().toString()
                );
        facilityManager.addNewFacility(facility);
    }
}

package com.example.nachosbusiness;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.nachosbusiness.facilities.Facility;
import com.example.nachosbusiness.facilities.FacilityDBManager;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;

//TODO: need to check if organizer has a facility, update texts if there is one.
public class FacilityFragment extends Fragment {

    private FacilityDBManager facilityManager;
    private String androidID;
    private Facility facility;
    private Boolean hasFacility;
    private String documentID;
    private static final String TAG = "FACILITY";

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        facilityManager = (FacilityDBManager) getArguments().getSerializable("facilityManager");
        androidID = getArguments().getString("androidID");
        facility = (Facility) getArguments().getSerializable("facility");
        hasFacility = getArguments().getBoolean("hasFacility");
        documentID = getArguments().getString("documentID");
        return inflater.inflate(R.layout.facility, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button saveButton = view.findViewById(R.id.facility_button_save);
        Button cancelButton = view.findViewById(R.id.facility_button_cancel);

        TextInputEditText facilityName = view.findViewById(R.id.text_facility_input_name);
        TextInputEditText facilityLocation = view.findViewById(R.id.text_facility_input_location);
        TextInputEditText facilityDescription = view.findViewById(R.id.text_facility_input_desc);

        facilityName.setText(facility.getName());
        facilityLocation.setText(facility.getLocation());
        facilityDescription.setText(facility.getInfo());

        Log.d("FacilityFragment", "onViewCreated: View is created");

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isNameValid = validateInput(facilityName, "Facility Name is Required!");
                boolean isLocationValid = validateInput(facilityLocation, "Facility Location is Required!");
                boolean isDescriptionValid = validateInput(facilityDescription, "Facility Description is Required!");

                if (isNameValid && isLocationValid && isDescriptionValid) {
                    facility.setAndroid_id(androidID);
                    facility.setName(facilityName.getText().toString());
                    facility.setLocation((facilityLocation.getText().toString()));
                    facility.setInfo(facilityDescription.getText().toString());

                    if (hasFacility){
                        facilityManager.setEntry(documentID, facility, "facilities");
                    }
                    else {
                        facilityManager.addEntry(facility);
                    }
                    facilityManager.queryOrganizerFacility(androidID);
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
}


package com.example.nachosbusiness;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

public class FacilityFragment extends Fragment {

    private FacilityDBManager facilityManager;
    private String androidID;
    private Facility facility;
    private String documentID;
    private static final String TAG = "FACILITY";

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        facilityManager = (FacilityDBManager) getArguments().getSerializable("facilityManager");
        androidID = getArguments().getString("androidID");
        facility = facilityManager.getFacility();
        documentID = facilityManager.getDocId();
        return inflater.inflate(R.layout.facility, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button saveButton = view.findViewById(R.id.facility_button_save);
        Button cancelButton = view.findViewById(R.id.facility_button_cancel);

        TextInputLayout facilityNameLayout = view.findViewById(R.id.text_facility_input_name_container);
        TextInputLayout facilityLocationLayout = view.findViewById(R.id.text_facility_input_location_container);
        TextInputLayout facilityDescriptionLayout = view.findViewById(R.id.text_facility_input_desc_container);

        TextInputEditText facilityName = view.findViewById(R.id.text_facility_input_name);
        TextInputEditText facilityLocation = view.findViewById(R.id.text_facility_input_location);
        TextInputEditText facilityDescription = view.findViewById(R.id.text_facility_input_desc);

        facilityName.setText(facility.getName());
        facilityLocation.setText(facility.getLocation());
        facilityDescription.setText(facility.getInfo());

        addTextWatcher(facilityName, facilityNameLayout, "Facility Name is Required!");
        addTextWatcher(facilityLocation, facilityLocationLayout, "Facility Location is Required!");
        addTextWatcher(facilityDescription, facilityDescriptionLayout, "Facility Description is Required!");

        Log.d("FacilityFragment", "onViewCreated: View is created");

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isNameValid = validateField(facilityName, facilityNameLayout, "Facility Name is Required!");
                boolean isLocationValid = validateField(facilityLocation, facilityLocationLayout, "Facility Location is Required!");
                boolean isDescriptionValid = validateField(facilityDescription, facilityDescriptionLayout, "Facility Description is Required!");

                if (isNameValid && isLocationValid && isDescriptionValid) {
                    facility.setAndroid_id(androidID);
                    facility.setName(facilityName.getText().toString());
                    facility.setLocation((facilityLocation.getText().toString()));
                    facility.setInfo(facilityDescription.getText().toString());

                    if (facilityManager.hasFacility()){
                        facilityManager.setEntry(documentID, facility, "facilities");
                        Toast.makeText(requireContext(),"Saved Changes!", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        facilityManager.addEntry(facility);
                        Toast.makeText(requireContext(),"New Facility Added!", Toast.LENGTH_SHORT).show();
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
     * Adds a TextWatcher to a TextInputEditText
     * @param inputEditText the TextInputEditText to watch
     * @param inputLayout the associated TextInputLayout where the error is displayed
     * @param errorMessage the error message to display when input is invalid
     */
    private void addTextWatcher(TextInputEditText inputEditText, TextInputLayout inputLayout, String errorMessage) {
        inputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // no action to do
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().isEmpty()) {
                    inputLayout.setError(errorMessage);
                } else {
                    inputLayout.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // no action to do
            }
        });
    }

    /**
     * Checks if the input in a TextInputEditText is not empty.
     * If invalid, sets an error message and highlights the input box.
     * @param editText The TextInputEditText to check
     * @param inputLayout The associated TextInputLayout for displaying error
     * @param errorMessage The error message to display if invalid
     * @return True if the input is valid, false otherwise
     */
    private boolean validateField(TextInputEditText editText, TextInputLayout inputLayout, String errorMessage) {
        String inputText = editText.getText().toString().trim();
        if (inputText.isEmpty()) {
            inputLayout.setError(errorMessage);
            return false;
        } else {
            inputLayout.setError(null);
            return true;
        }
    }
}
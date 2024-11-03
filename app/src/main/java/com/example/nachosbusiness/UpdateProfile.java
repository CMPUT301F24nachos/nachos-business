package com.example.nachosbusiness;

import android.content.Context;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.nachosbusiness.users.RegistrationActivity;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;
import java.util.regex.Pattern;

public class UpdateProfile extends Fragment {

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.activity_update_profile_final, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button saveButton = view.findViewById(R.id.signUpButton);
        Button cancelButton = view.findViewById(R.id.cancelButton);

        EditText phoneNumber = view.findViewById(R.id.editTextPhone);
        EditText email = view.findViewById(R.id.editTextTextEmailAddress);
        EditText userName = view.findViewById(R.id.editTextUsername);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isNameValid = isValidUsername(userName, "Username is Required!");
                boolean isEmailValid = isValidEmail(email);
                boolean isPhoneNumber = isValidPhoneNumber(phoneNumber);

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
    private boolean isValidUsername(EditText inputText, String e){
        String text = Objects.requireNonNull(inputText.getText()).toString().trim();
        if (text.isEmpty()) {
            Toast.makeText(requireContext(), e, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean isValidPhoneNumber(EditText inputText){
        String text = Objects.requireNonNull(inputText.getText()).toString().trim();
        if (!inputText.getText().toString().isEmpty())
        {
            String phone = PhoneNumberUtils.formatNumber(inputText.getText().toString(), "CA");
            if (phone == null)
            {
                Toast.makeText(requireContext(), "Invalid phone number", Toast.LENGTH_LONG).show();
                return false;
            }
        }
        return true;
    }

    private boolean isValidEmail(EditText email) {
        if (email == null || email.getText().toString().trim().isEmpty()) {
            Toast.makeText(requireContext(), "Enter an email", Toast.LENGTH_SHORT).show();
            return false;
        }

        String regexPattern = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        if (!Pattern.compile(regexPattern).matcher(email.getText().toString().trim()).matches()) {
            Toast.makeText(requireContext(), "Invalid email format", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}

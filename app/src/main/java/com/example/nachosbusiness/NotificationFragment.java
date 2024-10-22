package com.example.nachosbusiness;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class NotificationFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_notification, container, false);

        setupNotification(view, R.id.notification_item_1, R.id.close_button_1, R.id.claim_button_1, R.id.reject_button_1, null);
        setupNotification(view, R.id.notification_item_2, R.id.close_button_2, null, null, R.id.signup_button);
        setupNotification(view, R.id.notification_item_3, R.id.close_button_3, R.id.claim_button_3, R.id.reject_button_3, null);
        setupNotification(view, R.id.notification_item_4, R.id.close_button_4, R.id.claim_button_4, R.id.reject_button_4, null);
        setupNotification(view, R.id.notification_item_5, R.id.close_button_5, null, null, null);
        setupNotification(view, R.id.notification_item_6, R.id.close_button_6, null, null, null);
        return view;
    }

    private void setupNotification(View view, int notificationLayoutId, int closeButtonId, Integer claimButtonId, Integer rejectButtonId, Integer signUpButtonId) {
        ImageView closeButton = view.findViewById(closeButtonId);
        LinearLayout notificationLayout = view.findViewById(notificationLayoutId);
        closeButton.setOnClickListener(v -> {
            notificationLayout.setVisibility(View.GONE);
        });

        if (claimButtonId != null) {
            Button claimButton = view.findViewById(claimButtonId);
            claimButton.setOnClickListener(v -> {
                Toast.makeText(getContext(), "Claimed!", Toast.LENGTH_SHORT).show();
                // Handle the claim action
            });
        }

        if (rejectButtonId != null) {
            Button rejectButton = view.findViewById(rejectButtonId);
            rejectButton.setOnClickListener(v -> {
                Toast.makeText(getContext(), "Rejected!", Toast.LENGTH_SHORT).show();
                // Handle the reject action
            });
        }

        if (signUpButtonId != null) {
            Button signUpButton = view.findViewById(signUpButtonId);
            signUpButton.setOnClickListener(v -> {
                Toast.makeText(getContext(), "Signed up!", Toast.LENGTH_SHORT).show();
                // Handle the sign-up action
            });
        }
    }
}

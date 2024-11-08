package com.example.nachosbusiness.admin_browse;

import static android.view.View.GONE;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nachosbusiness.DBManager;
import com.example.nachosbusiness.QRUtil;
import androidx.fragment.app.Fragment;

import com.example.nachosbusiness.R;
import com.example.nachosbusiness.events.Event;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.Serializable;
import java.text.SimpleDateFormat;

public class EventDetailFragment extends Fragment {

    private Event event;
    private QRUtil qrUtil = new QRUtil();
    private FirebaseFirestore db;
    private DBManager dbManager;



    public static EventDetailFragment newInstance(com.example.nachosbusiness.events.Event event) {
        EventDetailFragment fragment = new EventDetailFragment();
        Bundle args = new Bundle();
        args.putSerializable("event", (Serializable) event) ; // Pass the event object
        fragment.setArguments(args);
        return fragment;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance(); // This initializes the Firestore instance
        if (getArguments() != null) {
            event = (com.example.nachosbusiness.events.Event) getArguments().getSerializable("event");
        }
        dbManager = new DBManager("events");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.event_detail, container, false);

        ImageButton backButton = view.findViewById(R.id.back);
        ImageButton editButton = view.findViewById(R.id.edit);
        Button removeEvent = view.findViewById(R.id.removeEvent);
        Button removeQR = view.findViewById(R.id.RemoveQR);
        ImageButton removeFacility = view.findViewById(R.id.trash);
        removeQR.setVisibility(GONE);
        removeEvent.setVisibility(GONE);
        removeFacility.setVisibility(GONE);

        backButton.setOnClickListener(v -> {
            new AlertDialog.Builder(getActivity())
                    .setTitle("Return to Admin List")
                    .setMessage("Do you want to go back to the Admin list?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // Navigate back to Profile List Fragment
                            // From: https://stackoverflow.com/questions/25350397/android-return-to-previous-fragment-on-back-press
                            assert getFragmentManager() != null;
                            getFragmentManager().popBackStack();  // This pops the current fragment off the back stack
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss(); // Close the dialog without action
                        }
                    })
                    .show();
        });
        editButton.setOnClickListener(v -> {
            new AlertDialog.Builder(getActivity())
                    .setTitle("Enter Edit Mode")
                    .setMessage("Do you want to enter edit mode?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            removeQR.setVisibility(View.VISIBLE);
                            removeEvent.setVisibility(View.VISIBLE);
                            removeFacility.setVisibility(View.VISIBLE);
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss(); // Close the dialog without action
                        }
                    })
                    .show();
        });
        removeEvent.setOnClickListener(v -> {
            new AlertDialog.Builder(getActivity())
                    .setTitle("Remove Event ")
                    .setMessage("Do you want to remove the event?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // Remove the event from Firestore
                            }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();  // Close the dialog without any action
                        }
                    })
                    .show();
        });


        removeQR.setOnClickListener(v -> {
            new AlertDialog.Builder(getActivity())
                    .setTitle("Remove Hashed QR Data")
                    .setMessage("Do you want to remove the QR?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        if (event != null) {
                            // Reference the specific event document using the eventID
                            DocumentReference eventRef = db.collection("events").document(event.getEventID());

                            // Update the 'qrCode' field to null in the event document
                            eventRef.update("qrCode", null)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(getActivity(), "QR removed successfully.", Toast.LENGTH_SHORT).show();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(getActivity(), "Failed to remove QR: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });
                        }
                    })
                    .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                    .show();
        });


        removeFacility.setOnClickListener(v -> {
            new AlertDialog.Builder(getActivity())
                    .setTitle("Remove Facility")
                    .setMessage("Do you want to remove the Facility?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        if (event != null) {
                           //TO DO Remove Facilty, facilty is a collection wthin events
                        }
                    })
                    .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                    .show();
        });
        if (event != null) {
            Log.d("EventDetailFragment", "Event ID: " + event.getEventID());
        } else {
            Log.d("EventDetailFragment", "Event is null!");
        }
        if (event != null) {
            TextView eventName = view.findViewById(R.id.event_name);
            TextView eventDescription = view.findViewById(R.id.event_description);
//            TextView eventOrganizer = view.findViewById(R.id.event_organizer);
            TextView eventDate = view.findViewById(R.id.event_date);
            ImageView qrCode = view.findViewById(R.id.event_qr_code);
            TextView facilityLocation = view.findViewById(R.id.facility_location);
            TextView facilityName = view.findViewById(R.id.facility_name);

            eventName.setText(event.getName());
            eventDescription.setText(event.getDescription());
//            eventOrganizer.setText(event.getOrganizerID());

            facilityLocation.setText(event.getFacility().getLocation());
            facilityName.setText(event.getFacility().getName());
            if (event.getQrCode() != null) {
                Bitmap qr = qrUtil.generateQRCode(event.getEventID());
                qrUtil.display(qr, qrCode);
            }
            // Format dates
            SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
            String startDate = event.getStartDateTime() != null ?
                    dateFormat.format(event.getStartDateTime().toDate()) : "N/A";
            String endDate = event.getEndDateTime() != null ?
                    dateFormat.format(event.getEndDateTime().toDate()) : "N/A";
            eventDate.setText(startDate + " - " + endDate);
        }

        return view;
    }


}

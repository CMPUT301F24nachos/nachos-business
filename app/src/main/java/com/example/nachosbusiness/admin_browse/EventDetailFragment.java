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
import com.example.nachosbusiness.utils.QRUtil;
import androidx.fragment.app.Fragment;

import com.example.nachosbusiness.R;
import com.example.nachosbusiness.events.Event;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.Serializable;
import java.text.SimpleDateFormat;

public class EventDetailFragment extends Fragment {

    private Event event;
    private QRUtil qrUtil = new QRUtil();
    private FirebaseFirestore db;
    private DBManager dbManager;



    /**
     * A fragment that displays the details of a specific event.
     * Edit or remove the event, the QR code, and the facilites
     *
     */
    public static EventDetailFragment newInstance(com.example.nachosbusiness.events.Event event) {
        EventDetailFragment fragment = new EventDetailFragment();
        Bundle args = new Bundle();
        args.putSerializable("event", (Serializable) event) ; // Pass the event object
        fragment.setArguments(args);
        return fragment;

    }

    /**
     * Creates a new instance of EventDetailFragment
     * @return A new instance of fragment
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance(); // This initializes the Firestore instance
        if (getArguments() != null) {
            event = (com.example.nachosbusiness.events.Event) getArguments().getSerializable("event");
        }
        dbManager = new DBManager("events");
    }


    /**
     * Fragment Creation
     * Retrieves the event passed to the fragment.
     *
     * @param savedInstanceState The saved instance state .
     */
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
        TextView eventName = view.findViewById(R.id.event_name);
        TextView eventDescription = view.findViewById(R.id.event_description);
//            TextView eventOrganizer = view.findViewById(R.id.event_organizer);
        TextView eventDate = view.findViewById(R.id.event_date);
        ImageView qrCode = view.findViewById(R.id.event_qr_code);
        TextView facilityLocation = view.findViewById(R.id.facility_location);
        TextView facilityName = view.findViewById(R.id.facility_name);

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
                    .setTitle("Remove Event")
                    .setMessage("Do you want to remove this event?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        if (event != null) {
                            // Reference the Firestore collection
                            CollectionReference eventsRef = db.collection("events");

                            // Query the collection for a document with the matching eventID field
                            eventsRef.whereEqualTo("eventID", event.getEventID()).limit(1)
                                    .get()
                                    .addOnSuccessListener(querySnapshot -> {
                                        if (!querySnapshot.isEmpty()) {
                                            // Document found, proceed to delete
                                            DocumentSnapshot documentSnapshot = querySnapshot.getDocuments().get(0);
                                            String documentID = documentSnapshot.getId(); // Get the document ID

                                            // Create an instance of DBManager for the "events" collection
                                            DBManager dbManager = new DBManager("events");

                                            // Delete the event document
                                            dbManager.deleteEntry(documentID);

                                            // Optionally, update the UI or give a success message
                                            Toast.makeText(getActivity(), "Event removed successfully.", Toast.LENGTH_SHORT).show();

                                            getActivity().getSupportFragmentManager().popBackStack();
                                        } else {
                                            // If the query doesn't return any documents
                                            Toast.makeText(getActivity(), "Event not found.", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(e -> {
                                        // Handle the error case when querying the collection fails
                                        Toast.makeText(getActivity(), "Error fetching event: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });
                        }
                    })
                    .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                    .show();
        });



        removeQR.setOnClickListener(v -> {
            new AlertDialog.Builder(getActivity())
                    .setTitle("Remove Hashed QR Data")
                    .setMessage("Do you want to remove the QR?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        if (event != null) {
                            String eventID = event.getEventID();
                            Log.d("RemoveQR", "Attempting to remove QR for eventID: " + eventID);

                            // Query Firestore for the event document
                            db.collection("events")
                                    .whereEqualTo("eventID", eventID)
                                    .get()
                                    .addOnSuccessListener(querySnapshot -> {
                                        if (!querySnapshot.isEmpty()) {
                                            // Document found, proceed to update QR code field
                                            DocumentSnapshot documentSnapshot = querySnapshot.getDocuments().get(0);
                                            String documentID = documentSnapshot.getId();

                                            // Update the 'qrCode' field to null in the event document
                                            db.collection("events").document(documentID)
                                                    .update("qrCode", null)
                                                    .addOnSuccessListener(aVoid -> {
                                                        // Hide the QR code ImageView
                                                        qrCode.setVisibility(View.GONE); // Hide the QR code image

                                                        Toast.makeText(getActivity(), "QR removed successfully.", Toast.LENGTH_SHORT).show();
                                                    })
                                                    .addOnFailureListener(e -> {
                                                        Toast.makeText(getActivity(), "Failed to remove QR: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                    });
                                        } else {
                                            // If the query doesn't return any documents
                                            Toast.makeText(getActivity(), "Event not found with eventID: " + eventID, Toast.LENGTH_SHORT).show();
                                            Log.e("RemoveQR", "No document found for eventID: " + eventID);
                                        }
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(getActivity(), "Error fetching event: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        Log.e("RemoveQR", "Error fetching event: ", e);
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
                            String eventID = event.getEventID();
                            Log.d("RemoveFacility", "Attempting to remove Facility for eventID: " + eventID);

                            // Query Firestore for the event document
                            db.collection("events")
                                    .whereEqualTo("eventID", eventID)
                                    .get()
                                    .addOnSuccessListener(querySnapshot -> {
                                        if (!querySnapshot.isEmpty()) {
                                            // Document found, proceed to update facility field
                                            DocumentSnapshot documentSnapshot = querySnapshot.getDocuments().get(0);
                                            String documentID = documentSnapshot.getId();

                                            // Update the 'facility' field to null in the event document
                                            db.collection("events").document(documentID)
                                                    .update("facility", null)
                                                    .addOnSuccessListener(aVoid -> {
                                                        // Update the UI by changing the facility text to "No Facility"

                                                        facilityLocation.setText("No Facility");
                                                        facilityName.setText("No Facility");

                                                        Toast.makeText(getActivity(), "Facility removed successfully.", Toast.LENGTH_SHORT).show();
                                                    })
                                                    .addOnFailureListener(e -> {
                                                        Toast.makeText(getActivity(), "Failed to remove facility: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                        Log.e("RemoveFacility", "Error removing facility: ", e);
                                                    });
                                        } else {
                                            // If no event was found with this eventID
                                            Toast.makeText(getActivity(), "Event not found.", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(getActivity(), "Error querying Firestore: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        Log.e("RemoveFacility", "Error querying Firestore: ", e);
                                    });
                        }
                    })
                    .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                    .show();
        });

        if (event != null) {

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

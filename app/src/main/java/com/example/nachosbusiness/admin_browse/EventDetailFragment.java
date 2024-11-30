package com.example.nachosbusiness.admin_browse;

import static android.view.View.GONE;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.io.InputStream;
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
     */
    public static EventDetailFragment newInstance(Event event) {
        EventDetailFragment fragment = new EventDetailFragment();
        Bundle args = new Bundle();
        args.putSerializable("event", (Serializable) event); // Pass the event object
        fragment.setArguments(args);
        return fragment;

    }

    /**
     * Creates a new instance of EventDetailFragment
     *
     * @return A new instance of fragment
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance(); // This initializes the Firestore instance
        if (getArguments() != null) {
            event = (Event) getArguments().getSerializable("event");
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
        Button removeImage = view.findViewById(R.id.removeImage);
        ImageButton removeFacility = view.findViewById(R.id.trash);
        removeQR.setVisibility(GONE);
        removeEvent.setVisibility(GONE);
        removeFacility.setVisibility(GONE);
        removeImage.setVisibility(GONE);
        TextView EditMode = view.findViewById(R.id.editmode);
        EditMode.setVisibility(GONE);
        TextView eventName = view.findViewById(R.id.event_name);
        TextView eventDescription = view.findViewById(R.id.event_description);
        TextView eventDate = view.findViewById(R.id.event_date);
        ImageView qrCode = view.findViewById(R.id.event_qr_code);
        ImageView eventImage = view.findViewById(R.id.Image);
        TextView facilityLocation = view.findViewById(R.id.facility_location);
        TextView facilityName = view.findViewById(R.id.facility_name);



        backButton.setOnClickListener(v -> {
            new AlertDialog.Builder(getActivity())
                    .setTitle("Return to Admin List")
                    .setMessage("Do you want to go back?")
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
                            removeImage.setVisibility(View.VISIBLE);
                            editButton.setVisibility(GONE);
                            EditMode.setVisibility(View.VISIBLE);

                            if (event.getQrCode() == null) {
                                removeQR.setVisibility(View.GONE);
                            }

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
                        removeEvent();
                    })
                    .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                    .show();
        });


        removeImage.setOnClickListener(v -> {
            new AlertDialog.Builder(getActivity())
                    .setTitle("Remove Event Image")
                    .setMessage("Do you want to remove the event image?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            removeEventImageFromFirebase(event.getEventID());
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
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
                                                        removeQR.setVisibility(View.GONE);
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
                    .setMessage("Removing this facility will delete all events with this facility and the facility itself.Do you want to proceed?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        if (event != null) {
                            String eventID = event.getEventID();
                            String facilityOrganizerID = event.getOrganizerID();

                            db.collection("events")
                                    .whereEqualTo("eventID", eventID)
                                    .get()
                                    .addOnSuccessListener(querySnapshot -> {
                                        if (!querySnapshot.isEmpty()) {
                                            DocumentSnapshot documentSnapshot = querySnapshot.getDocuments().get(0);
                                            String documentID = documentSnapshot.getId();

                                            db.collection("events").document(documentID)
                                                    .update("facility", null)
                                                    .addOnSuccessListener(aVoid -> {
                                                        facilityLocation.setText("No Facility");
                                                        facilityName.setText("No Facility");
                                                        removeFacility.setVisibility(GONE);


                                                        db.collection("facilities").document(facilityOrganizerID)
                                                                .delete()
                                                                .addOnSuccessListener(aVoid1 -> {
                                                                    Toast.makeText(getActivity(), "Facility removed successfully.", Toast.LENGTH_SHORT).show();
                                                                })
                                                                .addOnFailureListener(e -> {
//                                                                    Toast.makeText(getActivity(), "Failed to remove facility document: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                                    Log.e("RemoveFacility", "Error removing facility document: ", e);
                                                                });
                                                        removeEvent();
                                                        deleteEventsForOrganizer(facilityOrganizerID);
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
                                        //Toast.makeText(getActivity(), "Error querying Firestore: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
            loadEventImage(event.getEventID(), eventImage);
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

    /**
     * This is called to remove events form firebase (And the list)
     * Created a function as deleting a facility will also delete an event.
     */
    private void removeEvent() {
        if (event != null) {
            CollectionReference eventsRef = db.collection("events");

            // Query the collection for a document with the matching eventID field
            eventsRef.whereEqualTo("eventID", event.getEventID()).limit(1)
                    .get()
                    .addOnSuccessListener(querySnapshot -> {
                        if (!querySnapshot.isEmpty()) {
                            DocumentSnapshot documentSnapshot = querySnapshot.getDocuments().get(0);
                            String documentID = documentSnapshot.getId(); // Get the document ID
                            DBManager dbManager = new DBManager("events");
                            dbManager.deleteEntry(documentID);
                            Toast.makeText(getActivity(), "Event removed successfully.", Toast.LENGTH_SHORT).show();

                            getActivity().getSupportFragmentManager().popBackStack();
                        } else {

                            Toast.makeText(getActivity(), "Event not found.", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getActivity(), "Error fetching event: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }

    /**
     * This method deletes all events associated with a specific organizerID,
     * except for the event that was already removed.
     */
    private void deleteEventsForOrganizer(String organizerID) {
        // Query for all events with the same organizerID
        db.collection("events")
                .whereEqualTo("organizerID", organizerID)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        for (DocumentSnapshot eventDoc : querySnapshot.getDocuments()) {
                            String eventID = eventDoc.getId();
                            db.collection("events").document(eventID)
                                    .delete()
                                    .addOnSuccessListener(aVoid -> {
                                        Log.d("RemoveFacility", "Event deleted: " + eventID);
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e("RemoveFacility", "Error deleting event: " + e.getMessage());
                                    });
                        }
                    }
                    //Toast.makeText(getActivity(), "All events for this organizer have been deleted.", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Log.e("RemoveFacility", "Error deleting events for organizer: " + e.getMessage());
                });
    }

    private void loadEventImage(String eventId, ImageView imageView) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference eventImageRef = storageRef.child("event_images/" + eventId + ".jpg");

        eventImageRef.getDownloadUrl().addOnSuccessListener(uri -> {
            new Thread(() -> {
                try {
                    InputStream inputStream = new java.net.URL(uri.toString()).openStream();
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    getActivity().runOnUiThread(() -> imageView.setImageBitmap(bitmap));
                } catch (IOException e) {
                    e.printStackTrace();

                }
            }).start();
        }).addOnFailureListener(e -> {
            e.printStackTrace();
            getActivity().runOnUiThread(() -> {
                imageView.setImageResource(R.drawable.emptyevent);
                Toast.makeText(getActivity(), "No event image found", Toast.LENGTH_SHORT).show();
            });
        });
    }

    private void removeEventImageFromFirebase(String eventId) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference eventImageRef = storageRef.child("event_images/" + eventId + ".jpg");

        eventImageRef.delete()
                .addOnSuccessListener(aVoid -> {
                    // Success: Image deleted from Firebase Storage
                    Toast.makeText(getActivity(), "Event image removed", Toast.LENGTH_SHORT).show();
                    // Remove the image from UI as well
                    ImageView profileImage = getView().findViewById(R.id.profile);
                    profileImage.setImageResource(R.drawable.emptyevent); // Use a default placeholder image
                })
                .addOnFailureListener(e -> {
                    // Failure: Something went wrong
                    Toast.makeText(getActivity(), "Failed to remove event image", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                });
    }
}

package com.example.nachosbusiness;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.nachosbusiness.users.User;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class BrowseProfileFragment extends Fragment {

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.browse_profile, container, false);
    }


    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

//        ListView listView = view.findViewById(R.id.profileview);
//
//        // Initialize Firestore
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//        CollectionReference usersRef = db.collection("entrants");
//
//        ArrayList<String> userList = new ArrayList<>();
//
//        // Create an ArrayAdapter to display user data in the ListView
//        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, userList);
//        listView.setAdapter(adapter);
//
//        usersRef.get().addOnCompleteListener(task -> {
//            if (task.isSuccessful()) {
//                for (QueryDocumentSnapshot document : task.getResult()) {
//                    // Convert the document to a User object
//                    User user = document.toObject(User.class);
//
//                    // Add user info (e.g., username) to the list
//                    userList.add(user.getUsername());
//                }
//
//                // Notify the adapter that the data has changed
//                adapter.notifyDataSetChanged();
//            } else {
//                Toast.makeText(getContext(), "Failed to fetch users.", Toast.LENGTH_SHORT).show();
//            }
//        });

        ImageButton eventViewButton = view.findViewById(R.id.eventview);
        eventViewButton.setOnClickListener(v -> {
            new AlertDialog.Builder(getActivity())
                    .setTitle("Switch to Browse Events and Event Images")
                    .setMessage("Do you want to switch to the Event view?")
                    .setPositiveButton("Switch", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(getActivity(), Browse.class);
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton("Back", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .show();
        });

    }
}

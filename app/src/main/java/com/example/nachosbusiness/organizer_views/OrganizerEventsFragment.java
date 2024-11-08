package com.example.nachosbusiness.organizer_views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.nachosbusiness.CreateEventFragment;
import com.example.nachosbusiness.Dashboard;
import com.example.nachosbusiness.R;

public class OrganizerEventsFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.organizer_events, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        ImageButton homeButton = view.findViewById(R.id.button_event_home);
        ImageButton menuButton = view.findViewById(R.id.button_org_event_menu);

        menuButton.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(requireContext(), v);  // Use requireContext() instead of this.getContext()
            popupMenu.getMenuInflater().inflate(R.menu.organizer_event_menu, popupMenu.getMenu());

            popupMenu.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.action_add_event) {
                    //Toast.makeText(requireContext(), "create a new event", Toast.LENGTH_SHORT).show();
                        CreateEventFragment createEventFragment = new CreateEventFragment();
                        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                        transaction.replace(R.id.fragment_container, createEventFragment);
                        transaction.addToBackStack(null);
                        transaction.commit();
                    return true;
                } else if (item.getItemId() == R.id.action_nav_facility) {
                    Toast.makeText(requireContext(), "nav to my facility", Toast.LENGTH_SHORT).show();
                    return true;
                } else {
                    return false;
                }
            });
            popupMenu.show(); // Show the popup menu
        });

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requireActivity().getSupportFragmentManager().popBackStack();
            }
        });
    }
}
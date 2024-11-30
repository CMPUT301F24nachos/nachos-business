package com.example.nachosbusiness.organizer_views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.nachosbusiness.R;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import java.util.List;
import java.util.Map;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    private MapView mapView;
    private GoogleMap googleMap;
    private List<Map<String, Object>> waitlist;  // The waitlist passed from the event

    // Initialize the fragment with waitlist data passed via Bundle
    public void setWaitlist(List<Map<String, Object>> waitlist) {
        this.waitlist = waitlist;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragments_map, container, false);

        // Retrieve the waitlist from the arguments
        if (getArguments() != null) {
            waitlist = (List<Map<String, Object>>) getArguments().getSerializable("waitlist");
        }

        // Initialize the MapView
        mapView = view.findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        return view;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap map) {
        googleMap = map;

        if (waitlist != null) {
            // Loop through the waitlist and add markers for each location
            for (Map<String, Object> waitlistMember : waitlist) {
                // Extract location data
                Map<String, Double> location = (Map<String, Double>) waitlistMember.get("location");
                if (location != null) {
                    double latitude = location.get("latitude");
                    double longitude = location.get("longitude");

                    // Create a LatLng object for the marker
                    LatLng latLng = new LatLng(latitude, longitude);

                    // Add a marker for each waitlist member's location
                    googleMap.addMarker(new MarkerOptions().position(latLng).title("Waitlist Member"));
                }
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}
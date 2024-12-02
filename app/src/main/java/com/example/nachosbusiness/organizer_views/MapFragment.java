package com.example.nachosbusiness.organizer_views;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.nachosbusiness.R;
import com.example.nachosbusiness.events.Event;
import com.example.nachosbusiness.events.ListManager;
import com.example.nachosbusiness.events.ListManagerDBManager;
import com.example.nachosbusiness.users.User;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    private MapView mapView;
    private GoogleMap googleMap;
    private ArrayList<Map<Object, Object>> waitlist;  // The waitlist
    private ArrayList<User> entrants;
    private ListManagerDBManager listManagerDBManager;
    private ListManager listManager;
    private Event event;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragments_map, container, false);

        Bundle bundle  = getArguments();
        assert bundle != null;
        event = (Event) bundle.getSerializable("event");
        assert event != null;

        entrants = new ArrayList<>();
        listManagerDBManager = new ListManagerDBManager();

        loadEntrantsAndWaitlist();

        mapView = view.findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        return view;
    }

    private void loadEntrantsAndWaitlist() {
        listManagerDBManager.queryLists(event.getEventID(), new ListManagerDBManager.ListManagerCallback() {
            @Override
            public void onListManagerReceived(ListManager newListManager) {
                if (newListManager != null) {
                    listManager = newListManager;
                    listManager.initializeManagers(event.getEventID());

                    waitlist = newListManager.getWaitList();

                    Log.d("MapFragment", "Event ID: " + event.getEventID() + " | Waitlist size: " + (waitlist != null ? waitlist.size() : 0));

                    if (googleMap != null && waitlist != null) {
                        addMarkersToMap();
                    }
                } else {
                    Toast.makeText(getActivity(), "Failed to load lists", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onSingleListFound(List<String> eventIDs) {

            }
        });
    }

    private void addMarkersToMap() {
        if (googleMap != null && waitlist != null) {
            for (Map<Object, Object> waitlistMember : waitlist) {

                Object locationObject = waitlistMember.get("location");
                if (locationObject instanceof GeoPoint) {
                    GeoPoint geoPoint = (GeoPoint) locationObject;
                    double latitude = geoPoint.getLatitude();
                    double longitude = geoPoint.getLongitude();

                    LatLng latLng = new LatLng(latitude, longitude);

                    googleMap.addMarker(new MarkerOptions().position(latLng).title("Waitlist Member"));
                } else {
                    Log.e("MapFragment", "Location is not a GeoPoint: " + locationObject);
                }
            }
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap map) {
        googleMap = map;

        if (waitlist != null) {
            addMarkersToMap();
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
}
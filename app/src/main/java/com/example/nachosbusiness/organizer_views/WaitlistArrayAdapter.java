package com.example.nachosbusiness.organizer_views;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.example.nachosbusiness.DBManager;
import com.example.nachosbusiness.R;
import com.example.nachosbusiness.events.Event;
import com.example.nachosbusiness.events.ListManager;
import com.example.nachosbusiness.events.ListManagerDBManager;
import com.example.nachosbusiness.users.User;

import java.util.ArrayList;

/**
 * ArrayAdapter for the waitlist. Displays an entrant's profile image, username, and status in the corresponding event.
 */
public class WaitlistArrayAdapter extends ArrayAdapter<User> {

    private ArrayList<User> users;
    private Context context;
    private Event event;

    /**
     * constructor for WaitlistArrayAdapter
     * @param context context of view
     * @param users the list of users being displayed
     */
    public WaitlistArrayAdapter(Context context, ArrayList<User> users) {
        super(context, 0, users);
        this.context = context;
        this.users = users;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.waitlist_user_content, parent, false);
        }

        User user = users.get(position);

        TextView usernameText = view.findViewById(R.id.username);
        Button userStatusButton = view.findViewById(R.id.user_status);

        usernameText.setText(user.getUsername());
        setUserStatusButton(user, userStatusButton);

        DBManager dbManager = new DBManager("entrants");
        ImageView profileImage = view.findViewById(R.id.waitlist_profile_image);
        dbManager.getProfileImageExtra(user.getAndroid_id(), user.getUsername(), profileImage, getContext(), new DBManager.ProfileImageCallback() {
            @Override
            public void onImageLoaded(Bitmap bitmap) {
                profileImage.setVisibility(View.VISIBLE); // Show image if loaded
            }

            @Override
            public void onImageLoadFailed(Exception e) {
                profileImage.setVisibility(View.GONE); // Hide image if loading failed
            }
        });

        userStatusButton.setOnClickListener(v -> {
            setUserStatusButton(user, userStatusButton);
        });

        return view;
    }

    /**
     * sets event that is currently being displayed
     * @param event event the user is an entrant of
     */
    public void setEvent(Event event) {
        this.event = event;
    }

    /**
     * Sets the style of the user status button based on the user's current status in the displayed event
     * @param user displayed user
     * @param userStatusButton the button to set
     */
    private void setUserStatusButton(User user, Button userStatusButton) {
        ListManagerDBManager listManagerDBManager = new ListManagerDBManager();
        listManagerDBManager.queryEventDetails(event.getEventID(), user.getAndroid_id(), new ListManagerDBManager.EventDetailsCallback() {
            @Override
            public void onEventDetailsReceived(ListManagerDBManager.userStatus status, ListManager newListManager) {
                if (status == ListManagerDBManager.userStatus.INVITELIST)
                {
                    userStatusButton.setVisibility(View.VISIBLE);
                    userStatusButton.setText("Not responded...");
                    userStatusButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.lightButton)));
                }
                else if (status == ListManagerDBManager.userStatus.ACCEPTEDLIST)
                {
                    userStatusButton.setVisibility(View.VISIBLE);
                    userStatusButton.setText("Accepted");
                    userStatusButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.greenButton)));
                }
                else if (status == ListManagerDBManager.userStatus.CANCELLEDLIST)
                {
                    userStatusButton.setVisibility(View.VISIBLE);
                    userStatusButton.setText("Declined");
                    userStatusButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.redButton)));
                }
                else
                {
                    userStatusButton.setVisibility(View.GONE);
                }
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(getContext(), "Could not fetch user status",Toast.LENGTH_SHORT).show();
            }
        });
    }
}

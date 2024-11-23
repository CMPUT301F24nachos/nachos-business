package com.example.nachosbusiness.organizer_views;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.example.nachosbusiness.R;
import com.example.nachosbusiness.events.Event;
import com.example.nachosbusiness.events.ListManager;
import com.example.nachosbusiness.events.ListManagerDBManager;
import com.example.nachosbusiness.users.User;

import java.util.ArrayList;

public class WaitlistArrayAdapter extends ArrayAdapter<User> {

    private ArrayList<User> users;
    private Context context;
    private Event event;

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

        userStatusButton.setOnClickListener(v -> {
            setUserStatusButton(user, userStatusButton);
        });

        return view;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

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

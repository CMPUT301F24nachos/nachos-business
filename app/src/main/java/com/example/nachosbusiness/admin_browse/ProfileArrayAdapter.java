package com.example.nachosbusiness.admin_browse;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.nachosbusiness.DBManager;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.nachosbusiness.R;

import java.util.ArrayList;

/**
 * Array Adapter for displaying Profile objects in a custom ListView.
 * Each item in the list shows the profile's image and name, with an edit button.
 */
public class ProfileArrayAdapter extends ArrayAdapter<Profile> {

    private ArrayList<Profile> profiles;
    private Context context;

    /**
     * Constructs a new ProfileArrayAdpater
     *
     * @param context  The current context.
     * @param profiles The list of Profile objects to display.
     */
    public ProfileArrayAdapter(Context context, ArrayList<Profile> profiles) {

        super(context, 0, profiles);
        this.profiles = profiles;
        this.context = context;
    }

    /**
     * Provides a view, inflates a layout for each list item, retrieves the
     *  event at the specified position.
     *
     * @param position    The position of the item within the adapter's data set
     * @param convertView the old view
     * @param parent      parent view.
     * @return View corresponding to the data at the specified position.
     *
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View view = convertView;

        if(view == null){
            view = LayoutInflater.from(context).inflate(R.layout.profile_list, parent,false);
        }

        Profile profile = profiles.get(position);

        ImageView profileImage = view.findViewById(R.id.profile_image);
        TextView profileName = view.findViewById(R.id.profile_name);
        ImageButton editProfile = view.findViewById(R.id.edit);

        profileName.setText(profile.getName());

        String androidId = profile.getAndroid_id(); // Make sure this method exists to retrieve the ID
        DBManager dbManager = new DBManager("entrants"); // Use your collection name
        dbManager.getProfileImage(androidId, profileImage, context);

        return view;
    }
}
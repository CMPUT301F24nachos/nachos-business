package com.example.nachosbusiness;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.example.nachosbusiness.Profile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class ProfileArrayAdapter extends ArrayAdapter<Profile> {

    private ArrayList<Profile> profiles;
    private Context context;

    public ProfileArrayAdapter(Context context, ArrayList<Profile> profiles) {

        super(context, 0, profiles);
        this.profiles = profiles;
        this.context = context;
    }

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
        ImageButton actionButton = view.findViewById(R.id.edit);

        profileName.setText(profile.getName());


        return view;
    }
}
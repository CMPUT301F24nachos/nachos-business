package com.example.nachosbusiness;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.ImageButton;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;


public class Browse extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.browse_home);


        ImageButton profileViewButton = findViewById(R.id.profileview);
        profileViewButton.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Switch to Browse Profiles and Profile Images")
                    .setMessage("Do you want to switch to the profile view?")
                    .setPositiveButton("Switch", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            switchToProfileFragment();
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

    // Method to switch to the BrowseProfileFragment
    //Adapted from: https://stackoverflow.com/questions/23212162/how-to-move-from-one-fragment-to-another-fragment-on-click-of-an-imageview-in-an
    private void switchToProfileFragment() {
        BrowseProfileFragment profileFragment = new BrowseProfileFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.browse_home_container, profileFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
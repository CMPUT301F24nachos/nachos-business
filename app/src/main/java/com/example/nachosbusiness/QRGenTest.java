package com.example.nachosbusiness;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class QRGenTest extends Fragment {
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.qrtest, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String eventID = "testID1234";
        ImageView qrCodeImageView = view.findViewById(R.id.qr_code_image_view);

        QRUtil q = new QRUtil();
        Bitmap b = q.generateQRCode(eventID);
        q.display(b, qrCodeImageView);
        String hashedData = q.hashQRCodeData(eventID);
        System.out.println("Hashed QR Code Data: " + hashedData);
    }



}


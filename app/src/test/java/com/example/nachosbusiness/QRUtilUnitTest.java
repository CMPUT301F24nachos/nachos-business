package com.example.nachosbusiness;

import static org.junit.Assert.assertEquals;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
class QRUtilUnitTest {

    @Test
    public void testQRHash() {
        QRUtil qrUtil = new QRUtil();
        String testEventId = "12345";
        String hash = qrUtil.hashQRCodeData(testEventId);
        assertEquals(hash, "3d5a8d3b135992bf76f41954a450de371f177d3b033914494eaed4ecc7baa5c5");
    }

}



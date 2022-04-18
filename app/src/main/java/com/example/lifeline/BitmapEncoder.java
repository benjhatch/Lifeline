package com.example.lifeline;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

public class BitmapEncoder {
    public static String encodeBitmap(Bitmap image) {
        if (image == null)
            return null;
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG,100, stream);
        byte [] b=stream.toByteArray();
        String temp= Base64.encodeToString(b, Base64.DEFAULT);
        if(temp==null)
        {
            return null;
        }
        else
            return temp;
    }

    public static Bitmap decodeBitmap(String profilePic) {
        try {
            byte[] encodeByte = Base64.decode(profilePic, Base64.DEFAULT);
            Bitmap bitmap= BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            if(bitmap==null)
            {
                return null;
            }
            else
            {
                return bitmap;
            }

        } catch(Exception e) {
            e.getMessage();
            return null;
        }
    }
}

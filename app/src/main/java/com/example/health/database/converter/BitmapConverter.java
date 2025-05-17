// BitmapConverter.java
package com.example.health.database.converter;

import androidx.room.TypeConverter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class BitmapConverter {
    @TypeConverter
    public static byte[] bitmapToByteArray(Bitmap bitmap) {
        if (bitmap == null) return null;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        return outputStream.toByteArray();
    }

    @TypeConverter
    public static Bitmap byteArrayToBitmap(byte[] byteArray) {
        if (byteArray == null || byteArray.length == 0) return null;
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
    }
}
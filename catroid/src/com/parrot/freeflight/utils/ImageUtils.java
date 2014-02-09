
package com.parrot.freeflight.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.FloatMath;
import android.util.Log;

public final class ImageUtils
{
    private static final String TAG = ImageUtils.class.getSimpleName();


    private ImageUtils()
    {}


    public static Bitmap decodeBitmapFromFile(String file, int width, int height)
    {
        if (width <= 0 || height <= 0) { return BitmapFactory.decodeFile(file); }

        BitmapFactory.Options options = new BitmapFactory.Options();

        options.inJustDecodeBounds = true;

        Bitmap bit = BitmapFactory.decodeFile(file, options);

        int h = (int) FloatMath.ceil(options.outHeight / (float) height);
        int w = (int) FloatMath.ceil(options.outWidth / (float) width);

        if (h > 1 || w > 1) {
            if (h > w) {
                options.inSampleSize = h;
            } else {
                options.inSampleSize = w;
            }
        }

        options.inJustDecodeBounds = false;

        bit = BitmapFactory.decodeFile(file, options);

        return bit;
    }


    public static boolean saveBitmap(File filename, Bitmap bitmap)
    {
        boolean result = false;

        FileOutputStream out = null;
        try {
            out = new FileOutputStream(filename);

            result = bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
        } catch (FileNotFoundException e) {
            Log.w(TAG, e.toString());
        } finally {
            if (out != null) {
                try {
                    out.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return result;
    }
}

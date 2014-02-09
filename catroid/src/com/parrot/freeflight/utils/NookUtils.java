package com.parrot.freeflight.utils;

import java.io.File;

import android.os.Build;
import android.os.Environment;

public final class NookUtils
{
    private NookUtils()
    {

    }

    public static boolean isNook()
    {
        boolean result = false;

        if (Build.BRAND.equalsIgnoreCase("nook")) {
            // Build.MANUFACTURER; //Barnes&Noble
            result = true;
        }

        return result;
    }

    public static File getMediaFolder()
    {
        File result = Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);

        if (!FileUtils.isExtStorgAvailable()) {

            result = new File("/mnt/media/");
        }

        return result;
    }
}

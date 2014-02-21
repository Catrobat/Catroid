package org.catrobat.catroid.utils;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.hardware.Camera;

/**
 * Created by bernd on 2/21/14.
 */
public class LEDUtil {

    private static boolean lightON = false;

    private static Camera cam = null;

    public LEDUtil(Activity activity) {
        boolean hasCamera = activity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
        boolean hasLED =    activity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);

        if (!hasCamera || !hasLED) {
            // what now?
        } else {
            // supi
        }
    }

    public boolean getLEDValue() {
        return lightON;
    }

    public static void setLEDValue(boolean val) {
        if ( val ) {
            ledON();
        } else {
            ledOFF();
        }
    }

    private static void ledON() {
        if (lightON == true)
            return;

        Thread lightThread = new Thread( new Runnable() {
            @Override
            public void run() {

                cam = Camera.open();
                if (cam != null) {
                    Camera.Parameters params = cam.getParameters();
                    params.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);

                    try {
                        cam.setParameters(params);
                        cam.startPreview();
                        lightON = true;
                    } catch (Exception e) {
                        // TODO: toast message
                    }
                } else {
                    // TODO: toast message
                }
            }
        });
        lightThread.start();
    }

    private static void ledOFF() {
        if (lightON == false)
            return;

        Thread lightThread = new Thread( new Runnable() {
            @Override
            public void run() {
                if (cam != null) {
                    cam.stopPreview();
                    cam.release();
                    cam = null;
                    lightON = false;
                } else {
                    // TODO: toast message
                }
            }
        });
        lightThread.start();
    }
}

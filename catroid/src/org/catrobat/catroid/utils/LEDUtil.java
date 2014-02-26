package org.catrobat.catroid.utils;

import android.app.Activity;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Build;
import android.util.Log;

/**
 * Created by bernd on 2/21/14.
 */
public class LEDUtil {

    private static final String LOG_TAG = "LED ";
    private static boolean lightON = false;
    private static Camera cam = null;
    private static boolean previousLightOn = false;

    private LEDUtil() {

    }

    public boolean getLEDValue() {
        return lightON;
    }

    public static void setLEDValue(boolean val) {
        if (val) {
            ledON();
        } else {
            ledOFF();
        }
    }

    private static void ledON() {
        if (lightON == true)
            return;

        Thread lightThread = new Thread(new Runnable() {
            @Override
            public void run() {
                cam = Camera.open();


                if (cam != null) {

                    Camera.Parameters params = cam.getParameters();
                    params.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);

                    try {
                        if (android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD){
                            cam.setPreviewTexture(new SurfaceTexture(0));
                        }

                        cam.setParameters(params);
                        cam.startPreview();
                        lightON = true;
                        Log.d(LOG_TAG, "led is on");
                    } catch (Exception e) {
                        // TODO: toast message

                        Log.d(LOG_TAG, e.getMessage());
                    }
                } else {
                    // TODO: toast message
                    Log.d(LOG_TAG, "cam is null :(");
                }
            }
        });
        lightThread.start();

    }

    public static void ledOFF() {
        if (lightON == false)
            return;

        Thread lightThread = new Thread(new Runnable() {
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

    public static void pauseLed() {
        if (lightON == true) {
            ledOFF();
            previousLightOn = true;
        } else {
            previousLightOn = false;
        }
    }

    public static void resumeLed() {
        Log.d(LOG_TAG, "resume led");
        setLEDValue(previousLightOn);
    }
}

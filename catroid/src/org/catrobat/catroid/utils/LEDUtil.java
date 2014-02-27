package org.catrobat.catroid.utils;

import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Build;
import android.util.Log;

import java.util.concurrent.Semaphore;


/**
 * Created by bernd on 2/21/14.
 */
public class LEDUtil {

    private static final String LOG_TAG = "LEDUtil::";
    private static Camera cam = null;

    private static boolean lightON = false;
    private static boolean previousLightOn = false;
    private static boolean ledValue = false;
    private static boolean paused = false;

    private static boolean lightThreadActive = true;
    private static Semaphore lightThreadSemaphore = new Semaphore(1);
    private static Thread lightThread = new Thread(new Runnable() {

        @Override
        public void run() {
            while (lightThreadActive) {
                try {
                    lightThreadSemaphore.acquire();
                    setLED(ledValue);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
            lightThreadSemaphore.release();
        }
    });


    public static boolean getLEDValue() {
        return ledValue;
    }

    public static void setLEDValue(boolean val) {
        ledValue = val;
        lightThreadSemaphore.release();
    }

    public static void pauseLED() {
        Log.d(LOG_TAG, "pauseLED");
        if (!paused) {
            paused = true;
            if (lightON == true) {
                setLEDValue(false);
                previousLightOn = true;
            } else {
                previousLightOn = false;
            }
        }
    }

    public static void resumeLED() {
        Log.d(LOG_TAG, "resumeLED()");
        setLEDValue(previousLightOn);
        paused = false;
    }

    public LEDUtil() {
    }

    public static void activateLEDThread() {
        Log.d(LOG_TAG, "activateLEDThread()");

        if (!lightThread.isAlive()) {
            try {
                // thread has to start in waiting state
                lightThreadSemaphore.acquire();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            lightThread.setName("lightThread");
            lightThread.start();
        }
    }

    private synchronized static void setLED(boolean ledValue) {
        Log.d(LOG_TAG, "setLED()");
        if (ledValue)
            ledON();
        else
            ledOFF();
    }

    private synchronized static void ledON() {
        if (lightON == true)
            return;

        Log.d(LOG_TAG, "ledON()");
        try {
            cam = Camera.open();

            if (cam != null) {
                Camera.Parameters params = cam.getParameters();
                if (params != null) {
                    params.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);

                    if (android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD_MR1) {
                        cam.setPreviewTexture(new SurfaceTexture(0));
                    }

                    cam.setParameters(params);
                    cam.startPreview();
                    lightON = true;
                }
            }
        } catch (Exception e) {
            // TODO: Toast message
            Log.d(LOG_TAG, e.getMessage());
        }
    }

    private synchronized static void ledOFF() {
        if (lightON == false)
            return;

        Log.d(LOG_TAG, "ledOFF()");
        try {
            if (cam != null) {
                cam.stopPreview();
                cam.release();
                cam = null;
                lightON = false;
            }
        } catch (Exception e) {
            // TODO: Toast message
            Log.d(LOG_TAG, e.getMessage());
        }
    }
}

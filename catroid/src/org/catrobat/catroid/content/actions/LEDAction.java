package org.catrobat.catroid.content.actions;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.hardware.Camera;
//import android.widget.Toast;
//import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;
import android.os.Bundle;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Sprite;

/**
 * Created by bernd on 2/20/14.
 */
public class LEDAction extends Activity {

    private Sprite sprite;

    private Camera cam;
    private boolean ledOn;

    public LEDAction() {
        cam = null;
        ledOn = false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_script);
        boolean hasCamera = getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
        boolean hasFlash = getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);

        if ( !hasCamera && !hasFlash ) {
            // gibts problem
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (ledOn) {
            lightON();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (ledOn) {
            lightOFF();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (ledOn) {
            lightOFF();
        }
    }

    public void setSprite( Sprite sprite ) {
        this.sprite = sprite;
    }

    public void illuminate( boolean val ) {
        Thread thrd = new Thread( new Runnable() {
            @Override
            public void run() {
                if ( val ) {
                    lightON();
                } else {
                    lightOFF();
                }
            }
        });

        thrd.start();


    }

    private synchronized void lightON() {
        // switch led on
        cam = cam.open();
        if ( cam != null ) {
            Camera.Parameters params = cam.getParameters();
            params.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);

            try
            {
                cam.setParameters(params);
                cam.startPreview();
            } catch (Exception e) {
                //Toast.makeText( this, "turn light on exception", Toast.LENGTH_SHORT ).show();
            }

        } else {
            //Toast.makeText( this, "no led available", Toast.LENGTH_SHORT ).show();
        }
    }

    private synchronized void lightOFF() {
        // switch led off
        if ( cam != null ) {
            cam.stopPreview();
            cam.release();
            cam = null;
        } else {
            //Toast.makeText( this, "no led available", Toast.LENGTH_SHORT ).show();
        }
    }
}

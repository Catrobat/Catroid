package at.tugraz.ist.catroid.stage;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.io.sound.SoundManager;
import at.tugraz.ist.catroid.utils.Utils;

public class StageActivity extends Activity {

    public static SurfaceView sage;
    protected boolean isWaiting = false;
    private SoundManager soundManager;
    private StageManager sageManager;

    // public static boolean mDoNextCommands = true;
    private boolean stagePlaying = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Utils.checkForSdCard(this)) {
            Window window = getWindow();
            window.requestFeature(Window.FEATURE_NO_TITLE);
            window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

            setContentView(R.layout.stage);
            sage = (SurfaceView) findViewById(R.id.stageView);

            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            soundManager = SoundManager.getInstance();
            sageManager = new StageManager(this);
            sageManager.start();
            stagePlaying = true;
        }

    }

    public boolean onTouchEvent(MotionEvent event) {
        Log.i("StageActivity", "Number of pointers " + event.getPointerCount() + " action code: " + event.getAction() + " coordinates: x: "
                + event.getX((int) event.getPointerCount() - 1) + " y: " + event.getY((int) event.getPointerCount() - 1));
        // for the first pointer we get MotionEvent.ACTION_DOWN
        if (event.getAction() == MotionEvent.ACTION_DOWN)
            processOnTouch((int) event.getX(), (int) event.getY());
        // if we have a second pointer we also get
        // MotionEvent.ACTION_POINTER_2_DOWN
        if (event.getAction() == MotionEvent.ACTION_POINTER_2_DOWN)
            processOnTouch((int) event.getX(1), (int) event.getY(1));

        return true;
    }

    public void processOnTouch(int coordX, int coordY) {
        coordX = coordX + sage.getTop();
        coordY = coordY + sage.getLeft();

        sageManager.processOnTouch(coordX, coordY);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.stage_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.stagemenuStart:
            pauseOrContinue();
            break;
        case R.id.stagemenuConstructionSite:
            toMainActivity();
            break;
        }
        return true;
    }

    protected void onStop() {
        super.onStop();
        soundManager.pause();
        sageManager.pause(false);
        stagePlaying = false;
    }

    protected void onRestart() {
        super.onRestart();
        sageManager.resume();
        soundManager.resume();
        stagePlaying = true;
    }

    protected void onDestroy() {
        super.onDestroy();
        soundManager.clear();
    }

    public void onBackPressed() {
        finish();
    }

    private void toMainActivity() {
        finish();
    }

    private void pauseOrContinue() {
        if (stagePlaying) {
            sageManager.pause(true);
            soundManager.pause();
            stagePlaying = false;

        } else {
            sageManager.resume();
            soundManager.resume();
            stagePlaying = true;
        }
    }
}

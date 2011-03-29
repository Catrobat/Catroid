package at.tugraz.ist.catroid.stage;

import java.util.ArrayList;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.constructionSite.content.ProjectManager;
import at.tugraz.ist.catroid.content.sprite.Sprite;

public class StageManager {
    private Activity activity;
    protected ArrayList<Sprite> spriteList;
    private Boolean spritesChanged;
    private IDraw draw;
    private boolean isPaused;

    private Handler mHandler = new Handler();
    private Runnable runnable = new Runnable() {
        public void run() {
            for (Sprite sprite : spriteList) {
                if (sprite.getToDraw() == true) {
                    spritesChanged = true;
                    sprite.setToDraw(false);
                }
            }
            if (spritesChanged) {
                spritesChanged = !drawSprites();
            }

            if (!isPaused)
                mHandler.postDelayed(this, 33);
        }
    };

    public int getMaxZValue() {
        return ProjectManager.getInstance().getCurrentProject().getMaxZValue();
    }

    public StageManager(Activity activity) {

        spriteList = (ArrayList<Sprite>) ProjectManager.getInstance().getCurrentProject().getSpriteList();
        this.activity = activity;

        spritesChanged = true;
        draw = new CanvasDraw();

        for (Sprite sprite: spriteList) {
            sprite.startScripts();
        }
    }

    public boolean drawSprites() {
        return draw.draw();
    }

    public void processOnTouch(int coordX, int coordY) {
        for(Sprite sprite : spriteList){
            sprite.processOnTouch(coordX, coordY);
        }
    }

    public void pause(boolean drawScreen) {
        for (Sprite sprite : spriteList) {
            sprite.pause();
        }

        if (drawScreen) {
            Bitmap pauseBitmap = BitmapFactory.decodeResource(activity.getResources(), R.drawable.paused_cat);
            draw.drawPauseScreen(pauseBitmap);
            mHandler.removeCallbacks(runnable);
            spritesChanged = true;
        }

        isPaused = true;
    }

    public void resume() {
        for(Sprite sprite : spriteList){
            sprite.resume();
        }
        isPaused = false;
        spritesChanged = true;
        runnable.run();
    }

    public void start() {
        isPaused = false;
        runnable.run();
    }
}

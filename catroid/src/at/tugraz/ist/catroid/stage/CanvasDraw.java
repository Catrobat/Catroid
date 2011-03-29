package at.tugraz.ist.catroid.stage;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import at.tugraz.ist.catroid.constructionSite.content.ProjectManager;
import at.tugraz.ist.catroid.content.sprite.Costume;
import at.tugraz.ist.catroid.content.sprite.Sprite;
import at.tugraz.ist.catroid.utils.ImageEditing;

/**
 * 
 * Draws DrawObjects into a canvas.
 */
public class CanvasDraw implements IDraw {
    private Canvas canvas = null;
    private SurfaceView srufaceView;
    private Paint whitePaint;
    private SurfaceHolder holder;

    // TODO destroy surface somewhere!

    public CanvasDraw() {
        super();
        srufaceView = StageActivity.sage;
        holder = srufaceView.getHolder();
        whitePaint = new Paint();
        whitePaint.setStyle(Paint.Style.FILL);
        whitePaint.setColor(Color.WHITE);
    }

    public synchronized boolean draw() {
        canvas = holder.lockCanvas();
        try {
            if (canvas == null)
                throw new Exception();
            // we want to start with a white rectangle
            canvas.drawRect(new Rect(0, 0, canvas.getWidth(), canvas.getHeight()), whitePaint);
            Log.v("CanvasDraw", "drawSprites");
            for (Sprite sprite : ProjectManager.getInstance().getCurrentProject().getSpriteList()) {
                if(!sprite.isVisible()){
                    continue;
                }
                if (sprite.getCurrentCostume() != null && sprite.getCurrentCostume().getBitmap() != null) {
                    Costume tempCostume = sprite.getCurrentCostume();
                    canvas.drawBitmap(tempCostume.getBitmap(), tempCostume.getDrawPositionX(), tempCostume.getDrawPositionY(), null);
                    sprite.setToDraw(false);
                }
            }
            holder.unlockCanvasAndPost(canvas);
            return true;

        } catch (Exception e) {
            Log.v("CanvasDraw", "Exception");
            return false;
        }
    } 

    public synchronized void drawPauseScreen(Bitmap pauseBitmap) {
        Paint greyPaint = new Paint();
        greyPaint.setStyle(Paint.Style.FILL);
        greyPaint.setColor(Color.DKGRAY);
        canvas = holder.lockCanvas();
        if (canvas != null) {
            canvas.drawRect(new Rect(0, 0, canvas.getWidth(), canvas.getHeight()), greyPaint);
            if (pauseBitmap != null) {
                Bitmap scaledPauseBitmap = ImageEditing.scaleBitmap(pauseBitmap,
                        ((float) canvas.getWidth() / 2f) / (float) pauseBitmap.getWidth(), false);
                int posX = canvas.getWidth() / 2 - scaledPauseBitmap.getWidth() / 2;
                int posY = canvas.getHeight() / 2 - scaledPauseBitmap.getHeight() / 2;
                canvas.drawBitmap(scaledPauseBitmap, posX, posY, null);
            }
        }
        holder.unlockCanvasAndPost(canvas);

    }

}

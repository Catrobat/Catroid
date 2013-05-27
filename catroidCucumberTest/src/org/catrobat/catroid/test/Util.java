package org.catrobat.catroid.test;

import android.content.Context;
import android.graphics.Point;
import android.view.Display;
import android.view.WindowManager;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Sprite;

import static junit.framework.Assert.fail;

public final class Util {
    private Util() {
    }

    public static Sprite findSprite(Project project, String name) {
        for (Sprite sprite : project.getSpriteList()) {
            if (sprite.getName().equals(name)) {
                return sprite;
            }
        }
        fail(String.format("Sprite not found '%s'", name));
        return null;
    }

    public static Point libgdxToScreenCoordinates(Context context, float x, float y) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
//        Log.d(CucumberInstrumentation.TAG, String.format("center: [%d/%d]", size.x / 2, size.y / 2));
        Point point = new Point();
        point.x = Math.round((size.x / 2f) + x);
        point.y = Math.round((size.y / 2f) + y);
//        Log.d(CucumberInstrumentation.TAG, String.format("coords: [%d/%d]", point.x, point.y));
        return point;
    }
}

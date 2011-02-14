package at.tugraz.ist.catroid.test.content.brick;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


import android.test.InstrumentationTestCase;
import at.tugraz.ist.catroid.content.brick.gui.SetCostumeBrick;
import at.tugraz.ist.catroid.content.sprite.Sprite;
import at.tugraz.ist.catroid.stage.StageActivity;
import at.tugraz.ist.catroid.test.R;

public class SetCostumeBrickTest extends InstrumentationTestCase{
    
    private static final int IMAGE_FILE_ID = R.raw.icon;
    private File testImage;
    
    public void testSetCostume() throws IOException{
        BufferedInputStream inputStream = new BufferedInputStream(getInstrumentation().getContext().getResources().openRawResource(IMAGE_FILE_ID));
        testImage = File.createTempFile("testImage", ".png");
        BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(testImage), 4147);
        StageActivity.SCREEN_HEIGHT = 200;
        StageActivity.SCREEN_WIDTH = 200;
        
        byte[] buffer = new byte[4147];
        int length = 0;
        while ((length = inputStream.read(buffer)) > 0) {
            outputStream.write(buffer, 0, length);
        }
        inputStream.close();
        outputStream.flush();
        outputStream.close();
        
        Sprite sprite = new Sprite("new sprite");
        SetCostumeBrick setCostumeBrick = new SetCostumeBrick(sprite,testImage.getAbsolutePath());
        assertNull("current Costume is not null", sprite.getCurrentCostume());
        setCostumeBrick.execute();
        assertEquals("Width of loaded bitmap is not the same as width of original image", 72, sprite.getCurrentCostume().getBitmap().getWidth());
        assertEquals("Height of loaded bitmap is not the same as height of original image", 72, sprite.getCurrentCostume().getBitmap().getHeight());
    }

}

package at.tugraz.ist.catroid.test.content.brick;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.test.InstrumentationTestCase;
import at.tugraz.ist.catroid.content.brick.gui.SetCostumeBrick;
import at.tugraz.ist.catroid.content.sprite.Sprite;
import at.tugraz.ist.catroid.stage.StageActivity;
import at.tugraz.ist.catroid.test.R;

public class SetCostumeBrickTest extends InstrumentationTestCase {
    
    private static final int IMAGE_FILE_ID = R.raw.icon;
    private static final int fileSize = 4147;
    private File testImage;
    
    public void testSetCostume() throws IOException {
    	
        testImage = new File("mnt/sdcard/catroid/testImage.png");
        
        if (!testImage.exists())
        {
	        InputStream in   = getInstrumentation().getContext().getResources().openRawResource(IMAGE_FILE_ID);
	        OutputStream out = new BufferedOutputStream(new FileOutputStream(testImage), fileSize);
	        
	        byte[] buffer = new byte[fileSize];
	        int length = 0;
	        while ((length = in.read(buffer)) > 0) {
	            out.write(buffer, 0, length);
	        }
	        
	        in.close();
	        out.flush();
	        out.close();
        }
        
        StageActivity.SCREEN_HEIGHT = 200;
        StageActivity.SCREEN_WIDTH  = 200;
        
        Sprite sprite = new Sprite("new sprite");
        SetCostumeBrick setCostumeBrick = new SetCostumeBrick(sprite);
        setCostumeBrick.setCostume(testImage.getAbsolutePath());
        assertNull("current Costume is not null (should not be set)", sprite.getCurrentCostume());
        assertEquals("the new Costume is not in the costumeList of the sprite", 72, sprite.getCostumeList().get(0).getBitmap().getWidth());
        assertEquals("the new Costume is not in the costumeList of the sprite", 72, sprite.getCostumeList().get(0).getBitmap().getHeight());
        setCostumeBrick.execute(); //now setting current costume
        assertEquals("Width of loaded bitmap is not the same as width of original image", 72, sprite.getCurrentCostume().getBitmap().getWidth());
        assertEquals("Height of loaded bitmap is not the same as height of original image", 72, sprite.getCurrentCostume().getBitmap().getHeight());
    }

}

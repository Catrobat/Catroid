package at.tugraz.ist.catroid.test.content.brick;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.graphics.BitmapFactory;
import android.test.InstrumentationTestCase;
import at.tugraz.ist.catroid.Values;
import at.tugraz.ist.catroid.content.brick.SetCostumeBrick;
import at.tugraz.ist.catroid.content.sprite.Sprite;
import at.tugraz.ist.catroid.test.R;

public class SetCostumeBrickTest extends InstrumentationTestCase {
    
    private static final int IMAGE_FILE_ID = R.raw.icon;
    
    private File testImage;
    int width;
    int height;

    @Override
    protected void setUp() throws Exception {
        final int fileSize = 4147;
        final String imagePath = "/mnt/sdcard/catroid/testImage.png"; 
        testImage = new File(imagePath);
        if(!testImage.exists()) {
            testImage.createNewFile();
        }
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
        
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, o);

        width = o.outWidth;
        height = o.outHeight;
    }
    
    @Override
    protected void tearDown() throws Exception {
        if(testImage != null && testImage.exists()){
            testImage.delete();
        }
    }
    
    public void testSetCostume() throws IOException {
        
        Values.SCREEN_HEIGHT = 200;
        Values.SCREEN_WIDTH = 200;
        
        Sprite sprite = new Sprite("new sprite");
        SetCostumeBrick setCostumeBrick = new SetCostumeBrick(sprite);
        setCostumeBrick.setCostume(testImage.getAbsolutePath());
        assertNull("current Costume is not null (should not be set)", sprite.getCurrentCostume());
        
        assertEquals("the new Costume is not in the costumeList of the sprite", width,  sprite.getCostumeList().get(0).getBitmap().getWidth());
        assertEquals("the new Costume is not in the costumeList of the sprite", height, sprite.getCostumeList().get(0).getBitmap().getHeight());
        setCostumeBrick.execute(); //now setting current costume
        assertEquals("Width of loaded bitmap is not the same as width of original image",   width,  sprite.getCurrentCostume().getBitmap().getWidth());
        assertEquals("Height of loaded bitmap is not the same as height of original image", height, sprite.getCurrentCostume().getBitmap().getHeight());
    }

}

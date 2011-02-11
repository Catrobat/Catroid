/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010  Catroid development team 
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package at.tugraz.ist.catroid.test.io;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;

import android.test.AndroidTestCase;
import android.util.Log;
import at.tugraz.ist.catroid.content.brick.ScaleCostumeBrickBase;
import at.tugraz.ist.catroid.content.brick.gui.ComeToFrontBrick;
import at.tugraz.ist.catroid.content.brick.gui.HideBrick;
import at.tugraz.ist.catroid.content.brick.gui.PlaceAtBrick;
import at.tugraz.ist.catroid.content.brick.gui.ScaleCostumeBrick;
import at.tugraz.ist.catroid.content.brick.gui.ShowBrick;
import at.tugraz.ist.catroid.content.entities.SoundInfo;
import at.tugraz.ist.catroid.content.project.Project;
import at.tugraz.ist.catroid.content.script.Script;
import at.tugraz.ist.catroid.content.sprite.Sprite;
import at.tugraz.ist.catroid.io.StorageHandler;
import at.tugraz.ist.catroid.stage.StageActivity;
import at.tugraz.ist.catroid.utils.filesystem.MediaFileLoader;

public class StorageHandlerTest extends AndroidTestCase {
	public StageActivity stageActivity = new StageActivity();
	public StorageHandler storageHandler = StorageHandler.getInstance(stageActivity);
	
    public void testSerializeProject() {
        
        int xPosition = 457;
        int yPosition = 598;
        int scaleValue = 80;
        
        Project project     = new Project("testProject");
        Sprite firstSprite  = new Sprite("first");
        Sprite secondSprite = new Sprite("second");
        Sprite thirdSprite  = new Sprite("third");
        Sprite fourthSprite = new Sprite("fourth");
        Script testScript   = new Script();
        Script otherScript  = new Script();
        HideBrick hideBrick = new HideBrick(firstSprite);
        ShowBrick showBrick = new ShowBrick(firstSprite);
        ScaleCostumeBrick scaleCostumeBrick = new ScaleCostumeBrick(secondSprite, scaleValue);
        ComeToFrontBrick comeToFrontBrick   = new ComeToFrontBrick(firstSprite, null);
        PlaceAtBrick placeAtBrick           = new PlaceAtBrick(secondSprite, xPosition, yPosition);
        
        //adding Bricks: ----------------
        testScript.addBrick(hideBrick);
        testScript.addBrick(showBrick);
        testScript.addBrick(scaleCostumeBrick);
        testScript.addBrick(comeToFrontBrick);

        otherScript.addBrick(placeAtBrick); //secondSprite
        otherScript.setPaused(true);
        //-------------------------------

        firstSprite.getScriptList().add(testScript);
        secondSprite.getScriptList().add(otherScript);

        project.addSprite(firstSprite);
        project.addSprite(secondSprite);
        project.addSprite(thirdSprite);
        project.addSprite(fourthSprite);
        
        storageHandler.saveProject(project);
        
        Project loadedProject = storageHandler.loadProject("testProject");


        ArrayList<Sprite> preSpriteList  = (ArrayList<Sprite>) project.getSpriteList();
        ArrayList<Sprite> postSpriteList = (ArrayList<Sprite>) loadedProject.getSpriteList();

        
        assertEquals("First sprite does not match after deserialization",  preSpriteList.get(0).getName(), postSpriteList.get(0).getName());
        assertEquals("Second sprite does not match after deserialization", preSpriteList.get(1).getName(), postSpriteList.get(1).getName());
        assertEquals("Third sprite does not match after deserialization",  preSpriteList.get(2).getName(), postSpriteList.get(2).getName());
        assertEquals("Fourth sprite does not match after deserialization", preSpriteList.get(3).getName(), postSpriteList.get(3).getName());
        assertEquals("Fifth sprite does not match after deserialization",  preSpriteList.get(4).getName(), postSpriteList.get(4).getName());

        assertEquals("Title missmatch after deserialization", project.getProjectTitle(), loadedProject.getProjectTitle());
        
        assertEquals("Scale was not deserialized right",     scaleValue, ((ScaleCostumeBrick)(postSpriteList.get(1).getScriptList().get(0).getBrickList().get(2))).getScale());
        // TODO: Why does PlaceAtBrick have getters all of a sudden, shouldn't the Sprite hold all that information?
        assertEquals("XPosition was not deserialized right", xPosition, ((PlaceAtBrick) (postSpriteList.get(2).getScriptList().get(0).getBrickList().get(0))).getXPosition());
        assertEquals("YPosition was not deserialized right", yPosition, ((PlaceAtBrick) (postSpriteList.get(2).getScriptList().get(0).getBrickList().get(0))).getYPosition());
        
        assertEquals("isTouchScript should not be set in script", preSpriteList.get(1).getScriptList().get(0).isTouchScript(),postSpriteList.get(1).getScriptList().get(0).isTouchScript());
        assertFalse("paused should not be set in script",getPaused(preSpriteList.get(1).getScriptList().get(0)));

        assertEquals("paused should be set in script", getPaused(preSpriteList.get(2).getScriptList().get(0)),getPaused(postSpriteList.get(2).getScriptList().get(0)));

    }

    // TODO: might aswell just have a getter? Or get info from sprite?
//    private double getScale(ScaleCostumeBrick brick) {
//        Field field = null;
//        double scale = 0.0;
//        try {
//            field = ScaleCostumeBrickBase.class.getDeclaredField("scale");
//            field.setAccessible(true);
//            scale = (Double) field.get(brick);
//        } catch (Exception e) {}
//        return scale;
//    }

//	@SuppressWarnings("unchecked")
//	private int getXPosition(PlaceAtBrick brick) {
//        Field field = null;
//        int xPos = 0;
//        try {
//            field = PlaceAtBrickBase.class.getDeclaredField("xPosition");
//            field.setAccessible(true);
//            xPos = ((PrimitiveWrapper<Integer>) field.get(brick)).getValue();
//        } catch (Exception e) {}
//        return xPos;
//    }
//
//    private int getYPosition(PlaceAtBrick brick) {
//        Field field = null;
//        int yPos = 0;
//        try {
//            field = PlaceAtBrickBase.class.getDeclaredField("yPosition");
//            field.setAccessible(true);
//            yPos = (Integer) field.get(brick);
//        } catch (Exception e) {}
//        return yPos;
//    }
    
    private boolean getPaused(Script script) {
        Field field = null;
        boolean paused = false;
        try {
            field = Script.class.getDeclaredField("paused");
            field.setAccessible(true);
            paused = (Boolean) field.get(script);
        } catch (Exception e) {}
        return paused;
    }
    
    public void testLoadSoundContent(){
//		storageHandler.loadSoundContent();
//		ArrayList<SoundInfo> content =  storageHandler.getSoundContent();
//		File file;
//		
//		Log.d("TEST", "number of sound files: "+content.size());
//		assertNotNull( content);
//		
//		for(int i = 0; i < content.size(); i++){
//			file = new File(content.get(i).getPath());
//			assertTrue(file.exists());
//			
//			Log.d("TEST", content.get(i).getTitle());
//			Log.d("TEST", content.get(i).getId()+"");
//		}
	}
}

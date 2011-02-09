package at.tugraz.ist.catroid.test.io;


import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;

import android.test.AndroidTestCase;
import at.tugraz.ist.catroid.content.brick.Brick;
import at.tugraz.ist.catroid.content.brick.ComeToFrontBrick;
import at.tugraz.ist.catroid.content.brick.HideBrick;
import at.tugraz.ist.catroid.content.brick.PlaceAtBrick;
import at.tugraz.ist.catroid.content.brick.ScaleCostumeBrick;
import at.tugraz.ist.catroid.content.brick.ShowBrick;
import at.tugraz.ist.catroid.content.project.Project;
import at.tugraz.ist.catroid.content.script.Script;
import at.tugraz.ist.catroid.content.sprite.Sprite;
import at.tugraz.ist.catroid.io.StorageHandler;
import at.tugraz.ist.catroid.stage.StageActivity;


public class StorageHandlerTest extends AndroidTestCase {
	public StageActivity stageActivity = new StageActivity();
	public StorageHandler storageHandler = StorageHandler.getInstance(stageActivity);
	
    public void testSerializeProject() {
        
        int xPosition = 457;
        int yPosition = 598;
        double scaleValue = 2.4;
        
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
        
        // TODO: check if Scripts are equal, comparing object is obviously wrong
        assertEquals("Scale was not deserialized right",getScale((ScaleCostumeBrick)(postSpriteList.get(1).getScriptList().get(0).getBrickList().get(2))),scaleValue);
        assertEquals("XPosition was not deserialized right",getXPosition((PlaceAtBrick)(postSpriteList.get(2).getScriptList().get(0).getBrickList().get(0))),xPosition);
        assertEquals("YPosition was not deserialized right",getYPosition((PlaceAtBrick)(postSpriteList.get(2).getScriptList().get(0).getBrickList().get(0))),yPosition);
    }

	
    @SuppressWarnings("unchecked")
    private double getScale(ScaleCostumeBrick brick){
        Field field = null;
        double scale = 0.0;
        try {
            field = ScaleCostumeBrick.class.getDeclaredField("scale");
        } catch (Exception e) {
            e.printStackTrace();
        } 
        field.setAccessible(true);
        try {
            scale = (Double) field.get(brick);
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return scale;
    }
    
    @SuppressWarnings("unchecked")
    private int getXPosition(PlaceAtBrick brick){
        Field field = null;
        int xPos = 0;
        try {
            field = PlaceAtBrick.class.getDeclaredField("xPosition");
        } catch (Exception e) {
            e.printStackTrace();
        } 
        field.setAccessible(true);
        try {
            xPos = (Integer) field.get(brick);
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return xPos;
    }
    
    @SuppressWarnings("unchecked")
    private int getYPosition(PlaceAtBrick brick){
        Field field = null;
        int yPos = 0;
        try {
            field = PlaceAtBrick.class.getDeclaredField("yPosition");
        } catch (Exception e) {
            e.printStackTrace();
        } 
        field.setAccessible(true);
        try {
            yPos = (Integer) field.get(brick);
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return yPos;
    }
}

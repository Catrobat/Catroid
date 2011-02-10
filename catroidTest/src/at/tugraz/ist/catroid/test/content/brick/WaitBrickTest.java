package at.tugraz.ist.catroid.test.content.brick;

import android.test.AndroidTestCase;
import at.tugraz.ist.catroid.content.brick.HideBrick;
import at.tugraz.ist.catroid.content.brick.ShowBrick;
import at.tugraz.ist.catroid.content.brick.WaitBrick;
import at.tugraz.ist.catroid.content.script.Script;
import at.tugraz.ist.catroid.content.sprite.Sprite;

public class WaitBrickTest extends AndroidTestCase {
	
	public void testWait() {
		Sprite testSprite = new Sprite("testSprite");
		Script testScript = new Script();
		HideBrick hideBrick = new HideBrick(testSprite);
		WaitBrick waitBrick = new WaitBrick(3000);
        ShowBrick showBrick = new ShowBrick(testSprite);
        
        testScript.addBrick(hideBrick);
        testScript.addBrick(waitBrick);
        testScript.addBrick(showBrick);
        

        testSprite.getScriptList().add(testScript);
        
        testSprite.startScripts();
        
        
        try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		assertFalse("Unexpected visibility of testSprite", testSprite.isVisible());
		
		try {
			Thread.sleep(2010);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
        assertTrue("Unexpected visibility of testSprite", testSprite.isVisible());  
	}
	
	public void testPauseUnPause() {
		Sprite testSprite = new Sprite("testSprite");
		Script testScript = new Script();
		HideBrick hideBrick = new HideBrick(testSprite);
		WaitBrick waitBrick = new WaitBrick(10000);
        ShowBrick showBrick = new ShowBrick(testSprite);
        
        testScript.addBrick(hideBrick);
        testScript.addBrick(waitBrick);
        testScript.addBrick(showBrick);

        testSprite.getScriptList().add(testScript);
        
        testSprite.startScripts();
        
        try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		assertFalse("Unexpected visibility of testSprite", testSprite.isVisible());
		
        testSprite.pause();
        try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
        assertFalse("Unexpected visibility of testSprite", testSprite.isVisible());
        
        testSprite.unpause();

        try {
			Thread.sleep(6990);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		assertFalse("Unexpected visibility of testSprite", testSprite.isVisible());
     
        try {
			Thread.sleep(20);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
        assertTrue("Unexpected visibility of testSprite", testSprite.isVisible());  
	}
}

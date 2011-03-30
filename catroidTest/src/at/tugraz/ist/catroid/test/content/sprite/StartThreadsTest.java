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
package at.tugraz.ist.catroid.test.content.sprite;

import android.test.AndroidTestCase;
import at.tugraz.ist.catroid.content.brick.HideBrick;
import at.tugraz.ist.catroid.content.brick.IfTouchedBrick;
import at.tugraz.ist.catroid.content.brick.ScaleCostumeBrick;
import at.tugraz.ist.catroid.content.brick.ShowBrick;
import at.tugraz.ist.catroid.content.brick.WaitBrick;
import at.tugraz.ist.catroid.content.script.Script;
import at.tugraz.ist.catroid.content.sprite.Sprite;

public class StartThreadsTest extends AndroidTestCase {
    
    public void testStartThreads() {
        double scale = 300;
        Sprite testSprite = new Sprite("testSprite");
        Script testScript = new Script();
        HideBrick hideBrick = new HideBrick(testSprite);
        ScaleCostumeBrick scaleCostumeBrick = new ScaleCostumeBrick(testSprite, scale);
        
        testScript.addBrick(hideBrick);
        testScript.addBrick(scaleCostumeBrick);
        testSprite.getScriptList().add(testScript);
        
        testSprite.startScripts();
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertFalse("Sprite is not hidden", testSprite.isVisible());
        assertEquals("the scale is not as expected",scale, testSprite.getScale());
    }
    
    public void testResumeThreads(){
        Sprite testSprite = new Sprite("testSprite");
        Script testScript = new Script();
        HideBrick hideBrick = new HideBrick(testSprite);
        WaitBrick waitBrick = new WaitBrick(testSprite, 400);
        ShowBrick showBrick = new ShowBrick(testSprite);
        
        testScript.addBrick(hideBrick);
        testScript.addBrick(waitBrick);
        testScript.addBrick(showBrick);
        testSprite.getScriptList().add(testScript);
        
        testSprite.startScripts();
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        testSprite.pause();
        assertFalse("Sprite is not hidden", testSprite.isVisible());
        testSprite.resume();
        try {
            Thread.sleep(400);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertTrue("Sprite is hidden", testSprite.isVisible());
        
        testScript.getBrickList().clear();
        testScript.addBrick(hideBrick);
        testSprite.startScripts();
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertTrue("Sprite is hidden - should not be because this script shouldnt be executed", testSprite.isVisible());
    }
    
    public void testStartTouchScripts() {
        Sprite testSprite = new Sprite("testSprite");
        Script testScript = new Script();
        Script touchScript = new Script();
        HideBrick hideBrick = new HideBrick(testSprite);
        
        IfTouchedBrick touchedBrick2 = new IfTouchedBrick(testSprite, touchScript);
        IfTouchedBrick touchedBrick = (IfTouchedBrick) touchedBrick2.clone();
        ShowBrick showBrick = new ShowBrick(testSprite);
        
        testScript.addBrick(hideBrick);
        touchScript.addBrick(touchedBrick);
        touchScript.addBrick(showBrick);
        
        testSprite.getScriptList().add(testScript);
        testSprite.getScriptList().add(touchScript);
        
        System.out.println("Touchscript: " + touchScript.isTouchScript());

        testSprite.startScripts();
        
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        assertFalse("Sprite is visible", testSprite.isVisible());
        
        testSprite.processOnTouch(0, 0);
        
        try {
            Thread.sleep(250);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        assertTrue("Sprite is not visible", testSprite.isVisible());
        
        touchScript.getBrickList().clear();
        touchScript.addBrick(hideBrick);
        
        testSprite.processOnTouch(0, 0);
        
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        assertFalse("Sprite is visible", testSprite.isVisible());
    }
}

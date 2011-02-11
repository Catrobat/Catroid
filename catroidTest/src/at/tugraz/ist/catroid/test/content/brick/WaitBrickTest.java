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
package at.tugraz.ist.catroid.test.content.brick;

import android.test.AndroidTestCase;
import at.tugraz.ist.catroid.content.brick.gui.HideBrick;
import at.tugraz.ist.catroid.content.brick.gui.ShowBrick;
import at.tugraz.ist.catroid.content.brick.gui.WaitBrick;
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
        
        testSprite.resume();

        try {
			Thread.sleep(6900);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		assertFalse("Unexpected visibility of testSprite", testSprite.isVisible());
     
        try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
        assertTrue("Unexpected visibility of testSprite", testSprite.isVisible());  
	}
}

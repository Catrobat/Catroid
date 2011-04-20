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

import java.io.IOException;
import java.util.ArrayList;

import android.content.pm.PackageManager.NameNotFoundException;
import android.test.AndroidTestCase;
import at.tugraz.ist.catroid.content.Project;
import at.tugraz.ist.catroid.content.Script;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.content.bricks.ComeToFrontBrick;
import at.tugraz.ist.catroid.content.bricks.HideBrick;
import at.tugraz.ist.catroid.content.bricks.PlaceAtBrick;
import at.tugraz.ist.catroid.content.bricks.ScaleCostumeBrick;
import at.tugraz.ist.catroid.content.bricks.ShowBrick;
import at.tugraz.ist.catroid.io.StorageHandler;

public class StorageHandlerTest extends AndroidTestCase {
    private StorageHandler storageHandler;
    
    public StorageHandlerTest() throws IOException {
        storageHandler = StorageHandler.getInstance();
    }

    public void testSerializeProject() throws NameNotFoundException {

        int xPosition = 457;
        int yPosition = 598;
        double scaleValue = 0.8;

        Project project = new Project(getContext(), "testProject");
        Sprite firstSprite = new Sprite("first");
        Sprite secondSprite = new Sprite("second");
        Sprite thirdSprite = new Sprite("third");
        Sprite fourthSprite = new Sprite("fourth");
        Script testScript = new Script("testScript", firstSprite);
        Script otherScript = new Script("otherScript", secondSprite);
        HideBrick hideBrick = new HideBrick(firstSprite);
        ShowBrick showBrick = new ShowBrick(firstSprite);
        ScaleCostumeBrick scaleCostumeBrick = new ScaleCostumeBrick(secondSprite, scaleValue);
        ComeToFrontBrick comeToFrontBrick = new ComeToFrontBrick(firstSprite);
        PlaceAtBrick placeAtBrick = new PlaceAtBrick(secondSprite, xPosition, yPosition);

        // adding Bricks: ----------------
        testScript.addBrick(hideBrick);
        testScript.addBrick(showBrick);
        testScript.addBrick(scaleCostumeBrick);
        testScript.addBrick(comeToFrontBrick);

        otherScript.addBrick(placeAtBrick); // secondSprite
        otherScript.setPaused(true);
        // -------------------------------

        firstSprite.getScriptList().add(testScript);
        secondSprite.getScriptList().add(otherScript);

        project.addSprite(firstSprite);
        project.addSprite(secondSprite);
        project.addSprite(thirdSprite);
        project.addSprite(fourthSprite);

        storageHandler.saveProject(project);

        Project loadedProject = storageHandler.loadProject("testProject");

        ArrayList<Sprite> preSpriteList = (ArrayList<Sprite>) project.getSpriteList();
        ArrayList<Sprite> postSpriteList = (ArrayList<Sprite>) loadedProject.getSpriteList();

        // Test sprite names:
        assertEquals("First sprite does not match after deserialization", preSpriteList.get(0).getName(),
                postSpriteList.get(0).getName());
        assertEquals("Second sprite does not match after deserialization", preSpriteList.get(1).getName(),
                postSpriteList.get(1).getName());
        assertEquals("Third sprite does not match after deserialization", preSpriteList.get(2).getName(),
                postSpriteList.get(2).getName());
        assertEquals("Fourth sprite does not match after deserialization", preSpriteList.get(3).getName(),
                postSpriteList.get(3).getName());
        assertEquals("Fifth sprite does not match after deserialization", preSpriteList.get(4).getName(),
                postSpriteList.get(4).getName());

        // Test project name:
        assertEquals("Title missmatch after deserialization", project.getName(), loadedProject.getName());

        // Test random brick values
        assertEquals("Scale was not deserialized right", scaleValue, ((ScaleCostumeBrick) (postSpriteList.get(1)
                .getScriptList().get(0).getBrickList().get(2))).getScale());
        assertEquals("XPosition was not deserialized right", xPosition, ((PlaceAtBrick) (postSpriteList.get(2)
                .getScriptList().get(0).getBrickList().get(0))).getXPosition());
        assertEquals("YPosition was not deserialized right", yPosition, ((PlaceAtBrick) (postSpriteList.get(2)
                .getScriptList().get(0).getBrickList().get(0))).getYPosition());

        assertEquals("isTouchScript should not be set in script", preSpriteList.get(1).getScriptList().get(0)
                .isTouchScript(), postSpriteList.get(1).getScriptList().get(0).isTouchScript());
        assertFalse("paused should not be set in script", preSpriteList.get(1).getScriptList().get(0).isPaused());

        // Test script value
        assertEquals("paused should be set in script", preSpriteList.get(2).getScriptList().get(0).isPaused(),
                postSpriteList.get(2).getScriptList().get(0).isPaused());
        
        // Test version codes and names
        final int preVersionCode = project.getVersionCode();
        final int postVersionCode = loadedProject.getVersionCode();
        assertEquals("Version codes are not equal", preVersionCode, postVersionCode);
        
        final String preVersionName = project.getVersionName();
        final String postVersionName = loadedProject.getVersionName();
        assertEquals("Version names are not equal", preVersionName, postVersionName);
    }
    
    
}

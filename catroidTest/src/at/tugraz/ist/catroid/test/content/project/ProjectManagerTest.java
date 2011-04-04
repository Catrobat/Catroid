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
package at.tugraz.ist.catroid.test.content.project;

import java.io.File;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.test.AndroidTestCase;
import at.tugraz.ist.catroid.constructionSite.content.ProjectManager;
import at.tugraz.ist.catroid.content.brick.SetCostumeBrick;
import at.tugraz.ist.catroid.content.script.Script;
import at.tugraz.ist.catroid.content.sprite.Sprite;
import at.tugraz.ist.catroid.utils.UtilFile;

public class ProjectManagerTest extends AndroidTestCase {

    String projectNameOne = "Ulumulu";
    String scriptNameOne = "Ulukai";
    String scriptNameTwo = "Ulukai2";
    String spriteNameOne = "Zuul";
    String spriteNameTwo = "Zuuul";

    @Override
    public void tearDown() {
        File directory = new File("/sdcard/catroid/" + projectNameOne);
        if (directory.exists()) {
            UtilFile.deleteDirectory(directory);
        }
    }

    public void testBasicFunctions() throws NameNotFoundException {

        ProjectManager manager = ProjectManager.getInstance();
        assertNull("there is a current sprite set", manager.getCurrentSprite());
        assertNull("there is a current script set", manager.getCurrentScript());

        Context context = getContext().createPackageContext("at.tugraz.ist.catroid", Context.CONTEXT_IGNORE_SECURITY);
        manager.initializeNewProject(projectNameOne, context);
        assertNotNull("no current project set", manager.getCurrentProject());
        assertEquals("The Projectname is not " + projectNameOne, projectNameOne, manager.getCurrentProject().getName());

        Sprite sprite = new Sprite(spriteNameOne);
        manager.addSprite(sprite);
        manager.setCurrentSprite(sprite);

        assertNotNull("no current sprite set", manager.getCurrentSprite());
        assertEquals("The Srpitename is not " + spriteNameOne, spriteNameOne, manager.getCurrentSprite().getName());

        Script script = new Script(scriptNameOne, sprite);
        manager.addScript(script);
        manager.setCurrentScript(script);

        assertNotNull("no current script set", manager.getCurrentScript());
        assertEquals("The Srpitename is not " + scriptNameOne, scriptNameOne, manager.getCurrentScript().getName());

        //loadProject ----------------------------------------

        manager.loadProject(projectNameOne, context);
        assertNotNull("no current project set", manager.getCurrentProject());
        assertEquals("The Projectname is not " + projectNameOne, projectNameOne, manager.getCurrentProject().getName());
        assertNull("there is a current sprite set", manager.getCurrentSprite());
        assertNull("there is a current script set", manager.getCurrentScript());

        //resetProject ---------------------------------------

        manager.addSprite(sprite);
        manager.setCurrentSprite(sprite);
        manager.addScript(script);
        manager.setCurrentScript(script);

        manager.resetProject(context);

        assertNull("there is a current sprite set", manager.getCurrentSprite());
        assertNull("there is a current script set", manager.getCurrentScript());

        //addSprite

        Sprite sprite2 = new Sprite(spriteNameTwo);
        manager.addSprite(sprite2);
        assertTrue("Sprite not in current Project", manager.getCurrentProject().getSpriteList().contains(sprite2));
        
        //addScript
        
        manager.setCurrentSprite(sprite2);
        Script script2 = new Script(scriptNameTwo, sprite2);
        manager.addScript(script2);
        assertTrue("Script not in current Sprite", manager.getCurrentSprite().getScriptList().contains(script2));

        //addBrick

        manager.setCurrentScript(script2);
        SetCostumeBrick brick = new SetCostumeBrick(sprite2);
        manager.addBrick(brick);
        assertTrue("Brick not in current Script", manager.getCurrentScript().getBrickList().contains(brick));

        //move brick already tested

	}
}

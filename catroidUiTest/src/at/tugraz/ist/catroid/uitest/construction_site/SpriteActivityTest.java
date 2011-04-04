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

package at.tugraz.ist.catroid.uitest.construction_site;

import java.io.File;
import java.io.IOException;

import android.content.pm.PackageManager.NameNotFoundException;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.ListView;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.content.brick.ComeToFrontBrick;
import at.tugraz.ist.catroid.content.brick.HideBrick;
import at.tugraz.ist.catroid.content.brick.PlaceAtBrick;
import at.tugraz.ist.catroid.content.brick.ScaleCostumeBrick;
import at.tugraz.ist.catroid.content.brick.ShowBrick;
import at.tugraz.ist.catroid.content.project.Project;
import at.tugraz.ist.catroid.content.script.Script;
import at.tugraz.ist.catroid.content.sprite.Sprite;
import at.tugraz.ist.catroid.io.StorageHandler;
import at.tugraz.ist.catroid.ui.MainMenuActivity;
import at.tugraz.ist.catroid.utils.UtilFile;

import com.jayway.android.robotium.solo.Solo;

public class SpriteActivityTest extends ActivityInstrumentationTestCase2<MainMenuActivity> {
	private Solo solo;
    private String testProject = "testProject";


	public SpriteActivityTest() {
		super("at.tugraz.ist.catroid.ui", MainMenuActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		solo = new Solo(getInstrumentation(), getActivity());
	}

	@Override
	public void tearDown() throws Exception {
		try {
			solo.finalize();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		getActivity().finish();
		
        File directory = new File("/sdcard/catroid/" + testProject);
		UtilFile.deleteDirectory(directory);
		
		super.tearDown();
	}
	
	public void testMainMenuButton() {
		solo.clickOnButton(getActivity().getString(R.string.resume));
		solo.clickInList(0);
		
		solo.clickOnButton(solo.getCurrentActivity().getString(R.string.main_menu));
		String title = solo.getText(0).getText().toString();
        assertEquals("MainMenuActivity is not showing!", getActivity().getString(R.string.main_menu), title);
	}
	
	public void createTestProject(String projectName) throws IOException, NameNotFoundException {
		StorageHandler storageHandler = StorageHandler.getInstance();
		
		int xPosition = 457;
        int yPosition = 598;
        double scaleValue = 0.8;

        Project project = new Project(getActivity(), projectName);
        Sprite firstSprite = new Sprite("cat");
        Sprite secondSprite = new Sprite("dog");
        Sprite thirdSprite = new Sprite("horse");
        Sprite fourthSprite = new Sprite("pig");
        Script testScript = new Script();
        Script otherScript = new Script();
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
        testScript.setName("testScript");
        
        otherScript.addBrick(placeAtBrick); // secondSprite
        otherScript.setPaused(true);
        otherScript.setName("otherScript");
        // -------------------------------

        firstSprite.getScriptList().add(testScript);
        secondSprite.getScriptList().add(otherScript);

        project.addSprite(firstSprite);
        project.addSprite(secondSprite);
        project.addSprite(thirdSprite);
        project.addSprite(fourthSprite);

        storageHandler.saveProject(project);
		
	}

}

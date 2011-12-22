/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010-2011 The Catroid Team
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://www.catroid.org/catroid_license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *   
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package at.tugraz.ist.catroid.uitest.content;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.json.JSONException;

import android.test.ActivityInstrumentationTestCase2;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.common.Consts;
import at.tugraz.ist.catroid.common.CostumeData;
import at.tugraz.ist.catroid.content.Project;
import at.tugraz.ist.catroid.content.Script;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.content.StartScript;
import at.tugraz.ist.catroid.content.bricks.SetCostumeBrick;
import at.tugraz.ist.catroid.ui.ScriptTabActivity;
import at.tugraz.ist.catroid.uitest.util.XMLValidationUtil;
import at.tugraz.ist.catroid.utils.Utils;

import com.jayway.android.robotium.solo.Solo;

public class EmptyBrickSpinnersTest extends ActivityInstrumentationTestCase2<ScriptTabActivity> {

	private Solo solo;
	private String testProjectName = "xmlTestProjectName";
	private String costumeDataName = "blubb";

	public EmptyBrickSpinnersTest() {
		super("at.tugraz.ist.catroid", ScriptTabActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		createSpinnerProject();
		solo = new Solo(getInstrumentation(), getActivity());
		super.setUp();
	}

	@Override
	public void tearDown() throws Exception {
		try {
			solo.finalize();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		getActivity().finish();

		super.tearDown();
	}

	public void testBricksWithEmptySpinner() throws IllegalArgumentException, IllegalAccessException,
			InvocationTargetException, IOException, JSONException {

		assertTrue("text " + costumeDataName + " not found", solo.searchText(costumeDataName));
		solo.clickOnText(costumeDataName);
		solo.clickOnText(getActivity().getString(R.string.broadcast_nothing_selected));

		// go back that the project xml is saved
		solo.goBack();

		String projectXMLPath = Utils.buildPath(Consts.DEFAULT_ROOT, testProjectName, testProjectName
				+ Consts.PROJECT_EXTENTION);
		XMLValidationUtil.sendProjectXMLToServerForValidating(projectXMLPath);
	}

	private void createSpinnerProject() {

		Project project = new Project(null, testProjectName);
		project.setDeviceData();
		Sprite sprite = new Sprite("testSprite");
		Script startScript = new StartScript(sprite);
		sprite.addScript(startScript);
		project.addSprite(sprite);

		CostumeData dummyCostumeData = new CostumeData();
		dummyCostumeData.setCostumeName(costumeDataName);
		sprite.getCostumeDataList().add(dummyCostumeData);

		SetCostumeBrick setCostumeBrick = new SetCostumeBrick(sprite);
		setCostumeBrick.setCostume(dummyCostumeData);
		startScript.addBrick(setCostumeBrick);

		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(sprite);
		ProjectManager.getInstance().setCurrentScript(startScript);

	}

}

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
package at.tugraz.ist.catroid.test.content;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;

import org.json.JSONException;

import android.test.AndroidTestCase;
import android.util.Log;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.common.Constants;
import at.tugraz.ist.catroid.content.BroadcastScript;
import at.tugraz.ist.catroid.content.Project;
import at.tugraz.ist.catroid.content.Script;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.content.StartScript;
import at.tugraz.ist.catroid.content.WhenScript;
import at.tugraz.ist.catroid.content.bricks.Brick;
import at.tugraz.ist.catroid.content.bricks.BroadcastReceiverBrick;
import at.tugraz.ist.catroid.content.bricks.WhenBrick;
import at.tugraz.ist.catroid.content.bricks.WhenStartedBrick;
import at.tugraz.ist.catroid.io.StorageHandler;
import at.tugraz.ist.catroid.test.utils.TestUtils;
import at.tugraz.ist.catroid.test.utils.XMLValidationUtil;
import at.tugraz.ist.catroid.ui.dialogs.AddBrickDialog;
import at.tugraz.ist.catroid.utils.UtilFile;

public class XMLValidatingTest extends AndroidTestCase {
	private String testProjectName = "xmlTestProjectName";

	public XMLValidatingTest() throws IOException {
	}

	@Override
	public void tearDown() throws Exception {
		TestUtils.clearProject(testProjectName);
		super.tearDown();
	}

	@Override
	public void setUp() {
		TestUtils.clearProject(testProjectName);
	}

	@SuppressWarnings("unchecked")
	public void testSerializeProjectWithAllBricks() throws IllegalArgumentException, IllegalAccessException,
			InvocationTargetException, IOException, JSONException {

		File projectDirectory = new File(Constants.DEFAULT_ROOT + "/" + testProjectName);
		if (projectDirectory.exists()) {
			UtilFile.deleteDirectory(projectDirectory);
		}

		Project project = new Project(getContext(), testProjectName);
		Sprite sprite = new Sprite("testSprite");
		Script startScript = new StartScript(sprite);
		Script whenScript = new WhenScript(sprite);
		Script broadcastScript = new BroadcastScript(sprite);
		sprite.addScript(startScript);
		sprite.addScript(whenScript);
		sprite.addScript(broadcastScript);
		project.addSprite(sprite);

		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(sprite);
		ProjectManager.getInstance().setCurrentScript(startScript);

		Method[] methods = AddBrickDialog.class.getDeclaredMethods();
		HashMap<String, List<Brick>> brickMap = null;
		for (Method method : methods) {
			if (method.getName().equalsIgnoreCase("setupBrickMap")) {
				method.setAccessible(true);
				brickMap = (HashMap<String, List<Brick>>) method.invoke(null, sprite, getContext());
				break;
			}
		}

		for (List<Brick> brickList : brickMap.values()) {
			for (Brick brick : brickList) {
				if (brick.getClass().equals(WhenBrick.class) || brick.getClass().equals(WhenStartedBrick.class)
						|| brick.getClass().equals(BroadcastReceiverBrick.class)) {
					Log.i("XMLValidationtest", "These bricks are not in the new schema");
				} else {
					startScript.addBrick(brick);
				}
			}
		}

		assertTrue("no bricks added to the start script", startScript.getBrickList().size() > 0);
		StorageHandler.getInstance().saveProject(project);

		XMLValidationUtil.sendProjectXMLToServerForValidating(project);
	}

}

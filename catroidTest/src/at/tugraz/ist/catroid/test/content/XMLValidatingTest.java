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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.test.AndroidTestCase;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.common.Consts;
import at.tugraz.ist.catroid.content.BroadcastScript;
import at.tugraz.ist.catroid.content.Project;
import at.tugraz.ist.catroid.content.Script;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.content.StartScript;
import at.tugraz.ist.catroid.content.WhenScript;
import at.tugraz.ist.catroid.content.bricks.Brick;
import at.tugraz.ist.catroid.io.StorageHandler;
import at.tugraz.ist.catroid.test.utils.TestUtils;
import at.tugraz.ist.catroid.ui.dialogs.AddBrickDialog;
import at.tugraz.ist.catroid.utils.UtilFile;
import at.tugraz.ist.catroid.utils.Utils;
import at.tugraz.ist.catroid.web.ConnectionWrapper;

public class XMLValidatingTest extends AndroidTestCase {
	private static final String XML_VALIDATING_URL = "http://catroidtestserver.ist.tugraz.at/tests/catroid/validateXml.php";
	private String testProjectName = "xmlTestProjectName";

	public XMLValidatingTest() throws IOException {
	}

	@Override
	public void tearDown() {
		TestUtils.clearProject(testProjectName);
	}

	@Override
	public void setUp() {
		TestUtils.clearProject(testProjectName);
	}

	@SuppressWarnings("unchecked")
	public void testSerializeProjectWithAllBricks() throws IllegalArgumentException, IllegalAccessException,
			InvocationTargetException, IOException, JSONException {

		File projectDirectory = new File(Consts.DEFAULT_ROOT + "/" + testProjectName);
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
				startScript.addBrick(brick);
			}
		}

		StorageHandler.getInstance().saveProject(project);

		sendCreatedXMLToServerForValidating(project);
	}

	private void sendCreatedXMLToServerForValidating(Project project) throws IOException, JSONException {
		String projectXMLPath = Utils.buildPath(Consts.DEFAULT_ROOT, project.getName(), project.getName()
				+ Consts.PROJECT_EXTENTION);

		String xmlContent = readTextFile(projectXMLPath);

		HashMap<String, String> postValues = new HashMap<String, String>();
		postValues.put("xmlToValidate", xmlContent);

		ConnectionWrapper connection = new ConnectionWrapper();
		String responce = connection.doHttpPost(XML_VALIDATING_URL, postValues);

		JSONObject jsonResponce = new JSONObject(responce);
		boolean valid = jsonResponce.getBoolean("valid");
		String message = jsonResponce.optString("message");

		assertTrue(message + " For error information, copy the xml to the res directory "
				+ "in the catroidFileTest Project and run the file tests", valid);
	}

	private String readTextFile(String fullPathFilename) throws IOException {
		StringBuffer contents = new StringBuffer();
		BufferedReader reader = null;

		reader = new BufferedReader(new FileReader(fullPathFilename));
		String text = null;

		while ((text = reader.readLine()) != null) {
			contents.append(text).append(System.getProperty("line.separator"));
		}
		reader.close();

		return contents.toString();

	}

}

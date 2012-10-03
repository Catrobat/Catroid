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
package at.tugraz.ist.catroid.uitest.content.brick;

import android.test.ActivityInstrumentationTestCase2;
import at.tugraz.ist.catroid.ui.ScriptTabActivity;
import at.tugraz.ist.catroid.uitest.util.UiTestUtils;

import com.jayway.android.robotium.solo.Solo;

public class SpeakBrickTest extends ActivityInstrumentationTestCase2<ScriptTabActivity> {

	private Solo solo;

	//	private Project project;

	//	private SpeakBrick speakBrick;
	//	private String testString = "test";
	//	private String testString2 = "";

	public SpeakBrickTest() {
		super(ScriptTabActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		//createProject();
		solo = new Solo(getInstrumentation(), getActivity());
	}

	@Override
	public void tearDown() throws Exception {
		UiTestUtils.goBackToHome(getInstrumentation());
		solo.finishOpenedActivities();
		UiTestUtils.clearAllUtilTestProjects();
		super.tearDown();
		solo = null;
	}

	/*
	 * @Smoke
	 * public void testSpeakBrick() {
	 * int childrenCount = getActivity().getAdapter().getChildCountFromLastGroup();
	 * int groupCount = getActivity().getAdapter().getGroupCount();
	 * 
	 * assertEquals("Incorrect number of bricks.", 2, solo.getCurrentListViews().get(0).getChildCount());
	 * assertEquals("Incorrect number of bricks.", 1, childrenCount);
	 * 
	 * ArrayList<Brick> projectBrickList = project.getSpriteList().get(0).getScript(0).getBrickList();
	 * assertEquals("Incorrect number of bricks.", 1, projectBrickList.size());
	 * 
	 * assertEquals("Wrong Brick instance.", projectBrickList.get(0),
	 * getActivity().getAdapter().getChild(groupCount - 1, 0));
	 * assertNotNull("TextView does not exist.",
	 * solo.getText(getActivity().getString(at.tugraz.ist.catroid.R.string.brick_speak)));
	 * 
	 * solo.clickOnEditText(0);
	 * solo.enterText(0, testString);
	 * solo.clickOnButton(0);
	 * solo.sleep(300);
	 * 
	 * String text = UiTestUtils.getPrivateField("text", speakBrick).toString();
	 * 
	 * assertEquals("Wrong text in field.", testString, text);
	 * 
	 * solo.clickOnEditText(0);
	 * solo.enterText(0, "");
	 * solo.clickOnButton(0);
	 * solo.sleep(300);
	 * 
	 * text = UiTestUtils.getPrivateField("text", speakBrick).toString();
	 * 
	 * assertEquals("Wrong text in field.", "", text);
	 * 
	 * solo.clickOnEditText(0);
	 * solo.enterText(0, testString2);
	 * solo.clickOnButton(0);
	 * solo.sleep(900);
	 * 
	 * text = UiTestUtils.getPrivateField("text", speakBrick).toString();
	 * 
	 * assertEquals("Wrong text in field.", testString2, text);
	 * 
	 * }
	 * 
	 * private void createProject() {
	 * project = new Project(null, "testProject");
	 * Sprite sprite = new Sprite("cat");
	 * Script script = new StartScript("script", sprite);
	 * speakBrick = new SpeakBrick(sprite, null);
	 * script.addBrick(speakBrick);
	 * 
	 * sprite.addScript(script);
	 * project.addSprite(sprite);
	 * 
	 * ProjectManager.getInstance().setProject(project);
	 * ProjectManager.getInstance().setCurrentSprite(sprite);
	 * ProjectManager.getInstance().setCurrentScript(script);
	 * testString2 = getInstrumentation().getContext().getString(at.tugraz.ist.catroid.R.string.);
	 * 
	 * }
	 */
}

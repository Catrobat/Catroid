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
import java.util.ArrayList;

import android.content.pm.PackageManager.NameNotFoundException;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.ListView;
import android.widget.TextView;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.constructionSite.content.ProjectManager;
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

public class MainMenuActivityTest extends ActivityInstrumentationTestCase2<MainMenuActivity> {
	private Solo solo;
	private String testProject = "testProject";
	private String testProject2 = "testProject2";
	private String testProject3 = "testProject3";

	public MainMenuActivityTest() {
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

		File directory2 = new File("/sdcard/catroid/" + testProject2);
		UtilFile.deleteDirectory(directory2);

		File directory3 = new File("/sdcard/catroid/" + testProject3);
		UtilFile.deleteDirectory(directory3);

		super.tearDown();
	}

	public void testCreateNewProject() throws InterruptedException {
		File directory = new File("/sdcard/catroid/" + testProject);
		UtilFile.deleteDirectory(directory);
		assertFalse("testProject was not deleted!", directory.exists());

		solo.clickOnButton(getActivity().getString(R.string.new_project));
		solo.clickOnEditText(0);
		solo.enterText(0, testProject);
		solo.goBack();
		solo.clickOnButton(getActivity().getString(R.string.new_project_dialog_button));
		Thread.sleep(2000);

		File file = new File("/sdcard/catroid/testProject/" + testProject + ".spf");
		assertTrue(testProject + " was not created!", file.exists());
	}

	public void testCreateNewProjectWithSpecialCharacters() throws InterruptedException {
		final String projectNameWithSpecialCharacters = "Hey, look, I'm special! ?äöüß<>";

		solo.clickOnButton(getActivity().getString(R.string.new_project));
		solo.clickOnEditText(0);
		solo.enterText(0, projectNameWithSpecialCharacters);
		solo.goBack();
		solo.clickOnButton(getActivity().getString(R.string.new_project_dialog_button));
		Thread.sleep(1000);

		assertEquals("Project name with special characters was not set properly", ProjectManager.getInstance().getCurrentProject().getName(), projectNameWithSpecialCharacters);
		
		File directory = new File("/sdcard/catroid/" + projectNameWithSpecialCharacters);
		UtilFile.deleteDirectory(directory);
	}

	public void testLoadProject() throws IOException, NameNotFoundException, InterruptedException {
		File directory = new File("/sdcard/catroid/" + testProject2);
		UtilFile.deleteDirectory(directory);
		assertFalse(testProject2 + " was not deleted!", directory.exists());

		createTestProject(testProject2);

		solo.clickOnButton(getActivity().getString(R.string.load_project));
		solo.clickOnText(testProject2);
		ListView spritesList = (ListView) solo.getCurrentActivity().findViewById(R.id.spriteListView);
		Sprite first = (Sprite) spritesList.getItemAtPosition(1);
		assertEquals("Sprite at index 1 is not \"cat\"!", "cat", first.getName());
		Sprite second = (Sprite) spritesList.getItemAtPosition(2);
		assertEquals("Sprite at index 2 is not \"dog\"!", "dog", second.getName());
		Sprite third = (Sprite) spritesList.getItemAtPosition(3);
		assertEquals("Sprite at index 3 is not \"horse\"!", "horse", third.getName());
		Sprite fourth = (Sprite) spritesList.getItemAtPosition(4);
		assertEquals("Sprite at index 4 is not \"pig\"!", "pig", fourth.getName());
		solo.goBack();
		TextView currentProject = (TextView) getActivity().findViewById(R.id.currentProjectNameTextView);
		assertEquals("Current project is not testProject2!", getActivity().getString(R.string.current_project) + " " + testProject2, currentProject.getText());
	}

	public void testResume() throws NameNotFoundException, IOException {
		File directory = new File("/sdcard/catroid/" + testProject3);
		UtilFile.deleteDirectory(directory);
		assertFalse(testProject3 + " was not deleted!", directory.exists());

		createTestProject(testProject3);

		solo.clickOnButton(getActivity().getString(R.string.load_project));
		solo.clickOnText(testProject3);
		solo.goBack();

		solo.clickOnButton(getActivity().getString(R.string.resume));

		TextView projectTitle = (TextView) solo.getCurrentActivity().findViewById(R.id.projectTitleTextView);

		assertEquals("Project title is not " + testProject3, getActivity().getString(R.string.project_name) + " " + testProject3, projectTitle.getText());

		ListView spritesList = (ListView) solo.getCurrentActivity().findViewById(R.id.spriteListView);
		Sprite first = (Sprite) spritesList.getItemAtPosition(1);
		assertEquals("Sprite at index 1 is not \"cat\"!", "cat", first.getName());
		Sprite second = (Sprite) spritesList.getItemAtPosition(2);
		assertEquals("Sprite at index 2 is not \"dog\"!", "dog", second.getName());
		Sprite third = (Sprite) spritesList.getItemAtPosition(3);
		assertEquals("Sprite at index 3 is not \"horse\"!", "horse", third.getName());
		Sprite fourth = (Sprite) spritesList.getItemAtPosition(4);
		assertEquals("Sprite at index 4 is not \"pig\"!", "pig", fourth.getName());
	}

	public void testAboutCatroid() {
		solo.clickOnButton(getActivity().getString(R.string.about));
		ArrayList<TextView> textViewList = solo.getCurrentTextViews(null);

		assertEquals("Title is not correct!", getActivity().getString(R.string.about_title), textViewList.get(0).getText().toString());
		assertEquals("About text not correct!", getActivity().getString(R.string.about_text), textViewList.get(1).getText().toString());
		assertEquals("Link text is not correct!", getActivity().getString(R.string.about_link_text), textViewList.get(2).getText().toString());
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
		ComeToFrontBrick comeToFrontBrick = new ComeToFrontBrick(firstSprite, null);
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

	}

}

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
package at.tugraz.ist.catroid.uitest.ui;

import java.io.File;
import java.util.ArrayList;

import android.graphics.Bitmap;
import android.test.ActivityInstrumentationTestCase2;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.common.CostumeData;
import at.tugraz.ist.catroid.common.SoundInfo;
import at.tugraz.ist.catroid.content.Project;
import at.tugraz.ist.catroid.content.Script;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.content.StartScript;
import at.tugraz.ist.catroid.content.WhenScript;
import at.tugraz.ist.catroid.content.bricks.Brick;
import at.tugraz.ist.catroid.content.bricks.ChangeXByBrick;
import at.tugraz.ist.catroid.content.bricks.SetXBrick;
import at.tugraz.ist.catroid.content.bricks.SetYBrick;
import at.tugraz.ist.catroid.ui.MainMenuActivity;
import at.tugraz.ist.catroid.uitest.util.UiTestUtils;

import com.jayway.android.robotium.solo.Solo;

public class ProjectActivityTest extends ActivityInstrumentationTestCase2<MainMenuActivity> {
	private Solo solo;

	public ProjectActivityTest() {
		super("at.tugraz.ist.catroid", MainMenuActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		solo = new Solo(getInstrumentation(), getActivity());
		UiTestUtils.createEmptyProject();
	}

	@Override
	public void tearDown() throws Exception {
		ProjectManager.getInstance().deleteCurrentProject();

		try {
			solo.finalize();
		} catch (Throwable e) {
			e.printStackTrace();
		}

		getActivity().finish();
		UiTestUtils.clearAllUtilTestProjects();
		super.tearDown();
	}

	private void addNewSprite(String spriteName) {
		solo.sleep(500);
		UiTestUtils.clickOnLinearLayout(solo, R.id.btn_action_add_button);

		solo.sleep(200);
		EditText addNewSpriteEditText = solo.getEditText(0);
		//check if hint is set
		assertEquals("Not the proper hint set",
				getActivity().getString(R.string.new_sprite_dialog_default_sprite_name), addNewSpriteEditText.getHint());
		assertEquals("There should no text be set", "", addNewSpriteEditText.getText().toString());
		solo.sleep(100);
		solo.enterText(0, spriteName);
		solo.setActivityOrientation(Solo.LANDSCAPE);
		assertTrue("EditText field got cleared after changing orientation", solo.searchText(spriteName));
		solo.sleep(600);
		solo.setActivityOrientation(Solo.PORTRAIT);
		//solo.goBack();
		solo.clickOnButton(0);
		solo.sleep(100);
	}

	public void testBackgroundSprite() {
		String sometext = "something" + System.currentTimeMillis();
		solo.clickOnText(getActivity().getString(R.string.new_project));

		solo.setActivityOrientation(Solo.PORTRAIT);
		solo.sleep(300);
		solo.clearEditText(0);
		solo.enterText(0, sometext);
		solo.setActivityOrientation(Solo.LANDSCAPE);

		assertTrue("EditText field got cleared after changing orientation", solo.searchText(sometext));
		solo.sleep(600);
		solo.setActivityOrientation(Solo.PORTRAIT);
		//solo.goBack();
		solo.clickOnButton(0);
		solo.sleep(100);

		assertTrue("Wrong name for background sprite!",
				solo.searchText(solo.getCurrentActivity().getString(R.string.background)));
		solo.clickLongOnText(solo.getCurrentActivity().getString(R.string.background));
		assertFalse("Found delete option for background sprite",
				solo.searchText(solo.getCurrentActivity().getString(R.string.delete)));
	}

	public void testAddNewSprite() {
		final String spriteName = "testSprite";
		solo.clickOnButton(getActivity().getString(R.string.current_project_button));
		addNewSprite(spriteName);

		solo.sleep(300);

		ListView spritesList = (ListView) solo.getCurrentActivity().findViewById(android.R.id.list);
		Sprite secondSprite = (Sprite) spritesList.getItemAtPosition(1);
		assertEquals("Sprite at index 1 is not " + spriteName, spriteName, secondSprite.getName());
		assertTrue("Sprite is not in current Project", ProjectManager.getInstance().getCurrentProject().getSpriteList()
				.contains(secondSprite));

		final String spriteName2 = "anotherTestSprite";
		addNewSprite(spriteName2);
		spritesList = (ListView) solo.getCurrentActivity().findViewById(android.R.id.list);
		Sprite thirdSprite = (Sprite) spritesList.getItemAtPosition(2);
		assertEquals("Sprite at index 2 is not " + spriteName2, spriteName2, thirdSprite.getName());
		assertTrue("Sprite is not in current Project", ProjectManager.getInstance().getCurrentProject().getSpriteList()
				.contains(thirdSprite));
		assertTrue("Sprite not shown in Adapter", solo.searchText(spriteName2));
	}

	public void testContextMenu() {
		solo.clickOnButton(getActivity().getString(R.string.current_project_button));
		// Create sprites manually so we're able to check for equality
		final String spriteName = "foo";
		final String spriteName2 = "bar";

		addNewSprite(spriteName);
		addNewSprite(spriteName2);

		solo.sleep(500);

		// Rename sprite
		final String newSpriteName = "baz";
		solo.clickLongOnText(spriteName);
		solo.clickOnText(getActivity().getString(R.string.rename));
		solo.sleep(50);

		solo.clearEditText(0);
		UiTestUtils.enterText(solo, 0, newSpriteName);
		solo.setActivityOrientation(Solo.LANDSCAPE);
		solo.sleep(500);
		solo.setActivityOrientation(Solo.PORTRAIT);
		//solo.goBack();
		solo.clickOnButton(0);
		solo.sleep(50);

		ListView spritesList = (ListView) solo.getCurrentActivity().findViewById(android.R.id.list);
		Sprite sprite = (Sprite) spritesList.getItemAtPosition(1);
		assertEquals("Sprite on position wasn't renamed correctly", newSpriteName, sprite.getName());

		// Delete sprite
		solo.clickLongOnText(newSpriteName);
		solo.clickOnText(getActivity().getString(R.string.delete));

		// Dialog is handled asynchronously, so we need to wait a while for it to finish
		solo.sleep(1000);

		assertFalse("Sprite is still in Project", ProjectManager.getInstance().getCurrentProject().getSpriteList()
				.contains(sprite));
		assertFalse("Sprite is still in Project", solo.searchText(newSpriteName));

		spritesList = (ListView) solo.getCurrentActivity().findViewById(android.R.id.list);
		Sprite sprite2 = (Sprite) spritesList.getItemAtPosition(1);
		assertEquals("Subsequent sprite was not moved up after predecessor's deletion", spriteName2, sprite2.getName());
	}

	public void testMainMenuButton() {
		solo.clickOnButton(getActivity().getString(R.string.current_project_button));

		UiTestUtils.clickOnLinearLayout(solo, R.id.btn_action_home);

		solo.waitForActivity(MainMenuActivity.class.getSimpleName());

		boolean buttonFound = solo.getView(R.id.btn_home) != null ? true : false;
		assertTrue("Main menu is not visible", buttonFound);
	}

	public void testChangeOrientation() {
		String spriteName = "testSprite";

		solo.clickOnButton(getActivity().getString(R.string.current_project_button));
		solo.setActivityOrientation(Solo.LANDSCAPE);
		solo.sleep(500);
		solo.setActivityOrientation(Solo.PORTRAIT);

		solo.sleep(500);

		addNewSprite(spriteName);
		solo.clickLongOnText(spriteName); //opening context menu
		solo.setActivityOrientation(Solo.LANDSCAPE);
		assertTrue(
				"Context menu dialog not visible after changing orientation",
				solo.searchText(getActivity().getString(R.string.rename))
						&& solo.searchText(getActivity().getString(R.string.delete)));

		String testText = "testText";
		solo.sleep(600);
		solo.setActivityOrientation(Solo.PORTRAIT);
		solo.sleep(400);
		solo.clickOnText(getActivity().getString(R.string.rename));
		solo.sleep(600);
		solo.clearEditText(0);
		solo.enterText(0, testText);
		solo.setActivityOrientation(Solo.LANDSCAPE);
		solo.sleep(600);
		assertTrue("Dialog is not visible after orientation change",
				solo.searchText(getActivity().getString(R.string.ok)));
		assertTrue("EditText field got cleared after changing orientation", solo.searchText(testText));
		solo.setActivityOrientation(Solo.PORTRAIT);
		solo.sleep(600);
		//solo.goBack();
		solo.clickOnButton(0);
		solo.sleep(100);
		assertTrue("Sprite wasnt renamed", solo.searchText(testText));
	}

	public void testCheckMaxTextLines() {
		String spriteName = "poor poor poor poor poor poor poor poor me me me me me me";
		int expectedLineCount = 1;
		solo.clickOnButton(getActivity().getString(R.string.current_project_button));
		addNewSprite(spriteName);
		TextView textView = solo.getText(9);
		assertEquals("linecount is wrong - ellipsize failed", expectedLineCount, textView.getLineCount());
		solo.clickLongOnText(spriteName);
		expectedLineCount = 3;
		TextView textView2 = solo.getText(0);
		assertEquals("linecount is wrong", expectedLineCount, textView2.getLineCount());
	}

	public void testNewSpriteDialog() {

		ProjectManager projectManager = ProjectManager.getInstance();
		String spriteName1 = "sprite1";
		String spriteName2 = "sprite2";
		solo.clickOnButton(getActivity().getString(R.string.current_project_button));

		openNewSpriteDialog();
		solo.setActivityOrientation(Solo.PORTRAIT);
		solo.sleep(300);
		UiTestUtils.enterText(solo, 0, spriteName1);
		solo.setActivityOrientation(Solo.LANDSCAPE);
		solo.sleep(300);
		solo.setActivityOrientation(Solo.PORTRAIT);
		assertTrue("EditText field got cleared after changing orientation", solo.searchText(spriteName1));
		solo.sleep(300);
		//solo.goBack();
		solo.clickOnButton(0);
		solo.sleep(300);

		assertTrue("Sprite not successfully added", projectManager.spriteExists(spriteName1));

		openNewSpriteDialog();
		UiTestUtils.enterText(solo, 0, spriteName2);
		sendKeys(KeyEvent.KEYCODE_ENTER);

		solo.sleep(800);

		assertTrue("Sprite not successfully added", projectManager.spriteExists(spriteName2));

	}

	public void testNewSpriteDialogErrorMessages() {
		ProjectManager projectManager = ProjectManager.getInstance();
		String spriteName = "spriteError";
		solo.clickOnButton(getActivity().getString(R.string.current_project_button));

		openNewSpriteDialog();
		UiTestUtils.enterText(solo, 0, spriteName);
		solo.sleep(200);
		solo.setActivityOrientation(Solo.LANDSCAPE);
		assertTrue("EditText field got cleared after changing orientation", solo.searchText(spriteName));
		solo.sleep(600);
		solo.setActivityOrientation(Solo.PORTRAIT);
		//solo.goBack();
		solo.clickOnButton(0);

		solo.sleep(800);

		assertTrue("Sprite not successfully added", projectManager.spriteExists(spriteName));

		//trying to add sprite which already exists:
		openNewSpriteDialog();
		UiTestUtils.enterText(solo, 0, spriteName);
		solo.setActivityOrientation(Solo.LANDSCAPE);
		assertTrue("EditText field got cleared after changing orientation", solo.searchText(spriteName));
		solo.sleep(600);
		solo.setActivityOrientation(Solo.PORTRAIT);
		//solo.goBack();
		solo.clickOnButton(0);

		assertTrue("ErrorMessage not visible",
				solo.searchText(getActivity().getString(R.string.spritename_already_exists)));
		solo.clickOnButton(getActivity().getString(R.string.close));

		solo.sleep(200);

		sendKeys(KeyEvent.KEYCODE_ENTER);
		assertTrue("ErrorMessage not visible",
				solo.searchText(getActivity().getString(R.string.spritename_already_exists)));
		solo.sleep(200);
		solo.clickOnButton(getActivity().getString(R.string.close));

		//trying to add sprite without name ("")

		UiTestUtils.enterText(solo, 0, "");
		sendKeys(KeyEvent.KEYCODE_ENTER);
		assertTrue("ErrorMessage not visible", solo.searchText(getActivity().getString(R.string.spritename_invalid)));
		solo.clickOnButton(getActivity().getString(R.string.close));

		solo.sleep(200);
		solo.setActivityOrientation(Solo.LANDSCAPE);
		solo.sleep(600);
		solo.setActivityOrientation(Solo.PORTRAIT);
		solo.clickOnButton(0);
		assertTrue("not in NewSpriteDialog", solo.searchText(getActivity().getString(R.string.new_sprite_dialog_title)));
	}

	public void testRenameSpriteDialog() {
		String spriteName = "spriteRename";
		String spriteName2 = "spriteRename2";
		solo.clickOnButton(getActivity().getString(R.string.current_project_button));
		addNewSprite(spriteName);
		addNewSprite(spriteName2);

		//trying to rename sprite to name which already exists:
		//------------ OK Button:
		openRenameSpriteDialog(spriteName);
		UiTestUtils.enterText(solo, 0, spriteName2);
		solo.setActivityOrientation(Solo.LANDSCAPE);
		assertTrue("EditText field got cleared after changing orientation", solo.searchText(spriteName));
		solo.sleep(600);
		solo.setActivityOrientation(Solo.PORTRAIT);
		//solo.goBack();
		solo.clickOnButton(0);

		solo.sleep(200);
		assertTrue("ErrorMessage not visible",
				solo.searchText(getActivity().getString(R.string.spritename_already_exists)));
		solo.clickOnButton(getActivity().getString(R.string.close));
		assertTrue("RenameSpriteDialog not visible",
				solo.searchText(getActivity().getString(R.string.rename_sprite_dialog)));

		//------------ Enter Key:
		solo.sleep(200);
		sendKeys(KeyEvent.KEYCODE_ENTER);
		solo.sleep(200);
		assertTrue("ErrorMessage not visible",
				solo.searchText(getActivity().getString(R.string.spritename_already_exists)));
		solo.clickOnButton(getActivity().getString(R.string.close));
		solo.sleep(100);

		//trying to rename sprite to ""
		//------------ OK Button:
		UiTestUtils.enterText(solo, 0, "");
		sendKeys(KeyEvent.KEYCODE_ENTER);
		assertTrue("ErrorMessage not visible", solo.searchText(getActivity().getString(R.string.spritename_invalid)));
		solo.clickOnButton(getActivity().getString(R.string.close));

		solo.sleep(200);
		solo.clickOnButton(0);
		assertTrue("not in RenameSpriteDialog", solo.searchText(getActivity().getString(R.string.rename_sprite_dialog)));

	}

	public void testDivider() {
		String spriteName = "Sprite1";
		String spriteName2 = "Sprite2";
		String spriteName3 = "Sprite3";
		solo.clickOnButton(getActivity().getString(R.string.current_project_button));
		addNewSprite(spriteName);
		addNewSprite(spriteName2);
		addNewSprite(spriteName3);

		assertTrue("ListView divider should be null", solo.getCurrentListViews().get(0).getDivider() == null);
		assertTrue("Listview dividerheight should be 0", solo.getCurrentListViews().get(0).getDividerHeight() == 0);

		int dividerID = R.id.sprite_divider;
		int currentViewID;
		boolean isBackground = true;
		Bitmap viewBitmap;
		int pixelColor;
		int colorDivider;

		for (View viewToTest : solo.getCurrentViews()) {
			currentViewID = viewToTest.getId();
			if (dividerID == currentViewID) {
				viewToTest.buildDrawingCache();
				viewBitmap = viewToTest.getDrawingCache();
				if (isBackground) {
					pixelColor = viewBitmap.getPixel(1, 3);
					viewToTest.destroyDrawingCache();
					assertTrue("Background divider should have 4px height", viewToTest.getHeight() == 4);
					colorDivider = solo.getCurrentActivity().getResources().getColor(R.color.gray);
					assertEquals("Divider color for background should be gray", pixelColor, colorDivider);
					isBackground = false;
				} else {
					pixelColor = viewBitmap.getPixel(1, 1);
					viewToTest.destroyDrawingCache();
					assertTrue("Normal Sprite divider should have 2px height", viewToTest.getHeight() == 2);
					colorDivider = solo.getCurrentActivity().getResources().getColor(R.color.egg_yellow);
					assertEquals("Divider color for normal sprite should be eggyellow", pixelColor, colorDivider);
				}
			}
		}

	}

	public void testSpriteListDetails() {
		solo.clickOnButton(getActivity().getString(R.string.current_project_button));
		createProject();

		Sprite sprite = ProjectManager.getInstance().getCurrentSprite();
		int scriptCount = sprite.getNumberOfScripts();
		int brickCount = sprite.getNumberOfBricks();
		int costumeCount = sprite.getCostumeDataList().size();
		int soundCount = sprite.getSoundList().size();

		String scriptCountString = ((TextView) solo.getView(R.id.textView_number_of_scripts)).getText().toString();
		int scriptCountActual = Integer.parseInt(scriptCountString.substring(scriptCountString.lastIndexOf(' ') + 1));
		assertEquals("Displayed wrong number of scripts", scriptCount, scriptCountActual);

		String brickCountString = ((TextView) solo.getView(R.id.textView_number_of_bricks)).getText().toString();
		int brickCountActual = Integer.parseInt(brickCountString.substring(brickCountString.lastIndexOf(' ') + 1));
		int brickCountExpected = scriptCount + brickCount;
		assertEquals("Displayed the wrong number of bricks", brickCountExpected, brickCountActual);

		String costumeCountString = ((TextView) solo.getView(R.id.textView_number_of_costumes)).getText().toString();
		int costumeCountActual = Integer
				.parseInt(costumeCountString.substring(costumeCountString.lastIndexOf(' ') + 1));
		assertEquals("Displayed wrong number of costumes", costumeCount, costumeCountActual);

		String soundCountString = ((TextView) solo.getView(R.id.textView_number_of_sounds)).getText().toString();
		int soundCountActual = Integer.parseInt(soundCountString.substring(soundCountString.lastIndexOf(' ') + 1));
		assertEquals("Displayed wrong number of sound", soundCount, soundCountActual);
	}

	private void openNewSpriteDialog() {
		solo.sleep(200);
		UiTestUtils.clickOnLinearLayout(solo, R.id.btn_action_add_button);
		solo.sleep(50);
	}

	private void openRenameSpriteDialog(String spriteName) {
		solo.sleep(200);
		solo.clickLongOnText(spriteName);
		solo.sleep(2500);
		solo.clickInList(1);
		//solo.clickOnText(getActivity().getString(R.string.rename));
		solo.sleep(50);
	}

	private void createProject() {
		Project project = new Project(null, UiTestUtils.PROJECTNAME1);

		Sprite spriteCat = new Sprite("cat");
		Script startScriptCat = new StartScript(spriteCat);
		Script scriptTappedCat = new WhenScript(spriteCat);
		Brick setXBrick = new SetXBrick(spriteCat, 50);
		Brick setYBrick = new SetYBrick(spriteCat, 50);
		Brick changeXBrick = new ChangeXByBrick(spriteCat, 50);
		startScriptCat.addBrick(setYBrick);
		startScriptCat.addBrick(setXBrick);
		scriptTappedCat.addBrick(changeXBrick);

		spriteCat.addScript(startScriptCat);
		spriteCat.addScript(scriptTappedCat);
		project.addSprite(spriteCat);

		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(spriteCat);
		ProjectManager.getInstance().setCurrentScript(startScriptCat);

		File imageFile = UiTestUtils.saveFileToProject(project.getName(), "catroid_sunglasses.png",
				at.tugraz.ist.catroid.uitest.R.drawable.catroid_sunglasses, getActivity(), UiTestUtils.FileTypes.IMAGE);

		ProjectManager projectManager = ProjectManager.getInstance();
		ArrayList<CostumeData> costumeDataList = projectManager.getCurrentSprite().getCostumeDataList();
		CostumeData costumeData = new CostumeData();
		costumeData.setCostumeFilename(imageFile.getName());
		costumeData.setCostumeName("Catroid sun");
		costumeDataList.add(costumeData);
		projectManager.fileChecksumContainer.addChecksum(costumeData.getChecksum(), costumeData.getAbsolutePath());

		File soundFile = UiTestUtils.saveFileToProject(project.getName(), "longsound.mp3",
				at.tugraz.ist.catroid.uitest.R.raw.longsound, getInstrumentation().getContext(),
				UiTestUtils.FileTypes.SOUND);
		SoundInfo soundInfo = new SoundInfo();
		soundInfo.setSoundFileName(soundFile.getName());
		soundInfo.setTitle("longsound");

		ArrayList<SoundInfo> soundInfoList = ProjectManager.getInstance().getCurrentSprite().getSoundList();
		soundInfoList.add(soundInfo);
		ProjectManager.getInstance().fileChecksumContainer.addChecksum(soundInfo.getChecksum(),
				soundInfo.getAbsolutePath());
	}
}

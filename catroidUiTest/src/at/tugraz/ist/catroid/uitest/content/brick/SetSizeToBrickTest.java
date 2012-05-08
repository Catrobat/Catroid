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

import java.io.File;
import java.util.ArrayList;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.Smoke;
import android.util.DisplayMetrics;
import android.util.Log;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.common.Constants;
import at.tugraz.ist.catroid.common.CostumeData;
import at.tugraz.ist.catroid.common.Values;
import at.tugraz.ist.catroid.content.Project;
import at.tugraz.ist.catroid.content.Script;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.content.StartScript;
import at.tugraz.ist.catroid.content.bricks.Brick;
import at.tugraz.ist.catroid.content.bricks.SetCostumeBrick;
import at.tugraz.ist.catroid.content.bricks.SetSizeToBrick;
import at.tugraz.ist.catroid.stage.StageActivity;
import at.tugraz.ist.catroid.stage.StageListener;
import at.tugraz.ist.catroid.ui.ScriptActivity;
import at.tugraz.ist.catroid.ui.ScriptTabActivity;
import at.tugraz.ist.catroid.uitest.util.UiTestUtils;
import at.tugraz.ist.catroid.utils.UtilFile;

import com.jayway.android.robotium.solo.Solo;

public class SetSizeToBrickTest extends ActivityInstrumentationTestCase2<ScriptTabActivity> {
	private static final String TAG = SetSizeToBrickTest.class.getSimpleName();
	private String projectName = "SetSizeToBrickTestProject";
	private Solo solo;
	private Project project;
	private SetSizeToBrick setSizeToBrick;
	private SetCostumeBrick setCostumeBrick;
	private int imageRawId = at.tugraz.ist.catroid.uitest.R.raw.red_quad;
	private final int screenWidth = 480;
	private final int screenHeight = 800;

	public SetSizeToBrickTest() {
		super("at.tugraz.ist.catroid", ScriptTabActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		createProject();
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

		File directory = new File(Constants.DEFAULT_ROOT + "/" + projectName);
		if (directory.exists()) {
			UtilFile.deleteDirectory(directory);
		}
		UiTestUtils.clearAllUtilTestProjects();
		super.tearDown();
	}

	@Smoke
	public void testSetSizeToBrick() {
		int childrenCount = ((ScriptActivity) getActivity().getCurrentActivity()).getAdapter()
				.getChildCountFromLastGroup();
		int groupCount = ((ScriptActivity) getActivity().getCurrentActivity()).getAdapter().getGroupCount();
		assertEquals("Incorrect number of bricks.", 3, solo.getCurrentListViews().get(0).getChildCount());
		assertEquals("Incorrect number of bricks.", 2, childrenCount);

		ArrayList<Brick> projectBrickList = project.getSpriteList().get(0).getScript(0).getBrickList();
		assertEquals("Incorrect number of bricks.", 2, projectBrickList.size());

		assertEquals("Wrong Brick instance.", projectBrickList.get(0), ((ScriptActivity) getActivity()
				.getCurrentActivity()).getAdapter().getChild(groupCount - 1, 0));
		assertNotNull("TextView does not exist", solo.getText(getActivity().getString(R.string.brick_set_size_to)));

		double newSize = 200;

		UiTestUtils.clickEnterClose(solo, 0, newSize + "");

		solo.sleep(500);

		double size = (Double) UiTestUtils.getPrivateField("size", setSizeToBrick);
		assertEquals("Wrong text in field", newSize, size);
		assertEquals("Text not updated", newSize, Double.parseDouble(solo.getEditText(0).getText().toString()));

		// -------------------------------------------------------------------------------------------------------------
		DisplayMetrics displayMetrics = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

		Values.SCREEN_WIDTH = displayMetrics.widthPixels;
		Values.SCREEN_HEIGHT = displayMetrics.heightPixels;

		UiTestUtils.clickOnLinearLayout(solo, R.id.btn_action_play);

		solo.assertCurrentActivity("Not in stage", StageActivity.class);

		solo.sleep(1500);

		solo.goBack();
		solo.clickOnText(getActivity().getString(R.string.stagemenu_screenshot));
		solo.sleep(50);

		assertTrue("Successful screenshot Toast not found!",
				solo.searchText(getActivity().getString(R.string.notification_screenshot_ok)));

		solo.clickOnText(getActivity().getString(R.string.resume_current_project));

		// -------------------------------------------------------------------------------------------------------------
		Bitmap screenshot = BitmapFactory.decodeFile(Constants.DEFAULT_ROOT + "/" + projectName + "/"
				+ StageListener.SCREENSHOT_FILE_NAME);

		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		Bitmap blackQuad = BitmapFactory.decodeFile(setCostumeBrick.getImagePath());
		int blackQuadHeight = blackQuad.getHeight();
		int blackQuadWidth = blackQuad.getWidth();
		Log.v(TAG, "black_quad.png x: " + blackQuadHeight + " y: " + blackQuadWidth);
		Log.v(TAG, "Screen height: " + Values.SCREEN_HEIGHT + " width: " + Values.SCREEN_WIDTH);

		Log.v(TAG, (Values.SCREEN_WIDTH / 2) + (blackQuadHeight / 2) + 5 + "");
		Log.v(TAG, (Values.SCREEN_HEIGHT / 2) + (blackQuadWidth / 2) + 5 + "");

		int colorInsideSizedQuad = screenshot.getPixel((Values.SCREEN_WIDTH / 2) + (blackQuadWidth / 2) + 5,
				(Values.SCREEN_HEIGHT / 2) + (blackQuadHeight / 2) + 5);
		int colorOutsideSizedQuad = screenshot.getPixel(Values.SCREEN_WIDTH / 2 + blackQuadWidth + 10,
				Values.SCREEN_HEIGHT / 2 + blackQuadHeight + 10);

		assertEquals("Image was not scaled up even though SetSizeTo was exectuted before!", Color.RED,
				colorInsideSizedQuad);
		assertEquals("Wrong stage background color!", Color.WHITE, colorOutsideSizedQuad);
	}

	public void testResizeInputField() {
		UiTestUtils.clickOnLinearLayout(solo, R.id.btn_action_home);
		solo.sleep(200);
		solo.clickOnText(getActivity().getString(R.string.current_project_button));
		createTestProject();
		solo.clickOnText(solo.getCurrentListViews().get(0).getItemAtPosition(0).toString());
		solo.sleep(100);

		UiTestUtils.testDoubleEditText(solo, 0, 1.0, 60, true);
		UiTestUtils.testDoubleEditText(solo, 0, 100.55, 60, true);
		UiTestUtils.testDoubleEditText(solo, 0, -0.1, 60, true);
		UiTestUtils.testDoubleEditText(solo, 0, 1000.55, 60, false);
	}

	private void createProject() {

		Values.SCREEN_HEIGHT = screenHeight;
		Values.SCREEN_WIDTH = screenWidth;

		project = new Project(null, projectName);
		Sprite sprite = new Sprite("cat");
		Script script = new StartScript(sprite);
		setSizeToBrick = new SetSizeToBrick(sprite, 100);
		setCostumeBrick = new SetCostumeBrick(sprite);

		script.addBrick(setSizeToBrick);
		script.addBrick(setCostumeBrick);

		sprite.addScript(script);
		project.addSprite(sprite);

		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(sprite);
		ProjectManager.getInstance().setCurrentScript(script);
		ProjectManager.getInstance().saveProject();

		File image = UiTestUtils.saveFileToProject(projectName, "black_quad.png", imageRawId, getInstrumentation()
				.getContext(), UiTestUtils.FileTypes.IMAGE);
		Log.v(TAG, image.getName());
		CostumeData costumeData = new CostumeData();
		costumeData.setCostumeFilename(image.getName());
		costumeData.setCostumeName("image");
		setCostumeBrick.setCostume(costumeData);
		sprite.getCostumeDataList().add(costumeData);
		ProjectManager.getInstance().fileChecksumContainer.addChecksum(costumeData.getChecksum(),
				image.getAbsolutePath());
		ProjectManager.getInstance().saveProject();
	}

	private void createTestProject() {
		project = new Project(null, UiTestUtils.DEFAULT_TEST_PROJECT_NAME);
		Sprite sprite = new Sprite("cat");
		Script script = new StartScript(sprite);
		setSizeToBrick = new SetSizeToBrick(sprite, 0);
		script.addBrick(setSizeToBrick);

		sprite.addScript(script);
		project.addSprite(sprite);

		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(sprite);
		ProjectManager.getInstance().setCurrentScript(script);
	}
}

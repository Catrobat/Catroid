/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2013 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://developer.catrobat.org/license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *  
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.uitest.content.brick;

import java.io.File;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.common.Values;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.SetLookBrick;
import org.catrobat.catroid.content.bricks.SetSizeToBrick;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.stage.StageListener;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.uitest.util.UiTestUtils;
import org.catrobat.catroid.utils.UtilFile;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.Smoke;
import android.util.DisplayMetrics;
import android.util.Log;

import com.jayway.android.robotium.solo.Solo;

public class SetSizeToBrickTest extends ActivityInstrumentationTestCase2<MainMenuActivity> {
	private static final int SCREEN_WIDTH = 480;
	private static final int SCREEN_HEIGHT = 800;

	private static final String TAG = SetSizeToBrickTest.class.getSimpleName();

	private String projectName = "SetSizeToBrickTestProject";
	private Solo solo;
	private Project project;
	private SetSizeToBrick setSizeToBrick;
	private SetLookBrick setLookBrick;
	private int imageRawId = org.catrobat.catroid.uitest.R.raw.red_quad;

	public SetSizeToBrickTest() {
		super(MainMenuActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		createProject();
		solo = new Solo(getInstrumentation(), getActivity());
		UiTestUtils.prepareStageForTest();
		UiTestUtils.getIntoScriptActivityFromMainMenu(solo, 2);
	}

	@Override
	public void tearDown() throws Exception {
		UiTestUtils.goBackToHome(getInstrumentation());
		solo.finishOpenedActivities();
		File directory = new File(Constants.DEFAULT_ROOT + "/" + projectName);
		if (directory.exists()) {
			UtilFile.deleteDirectory(directory);
		}
		UiTestUtils.clearAllUtilTestProjects();
		super.tearDown();
		solo = null;
	}

	@Smoke
	public void testSetSizeToBrick() {
		double newSize = 200;

		UiTestUtils.testBrickWithFormulaEditor(solo, 0, 1, newSize, "size", setSizeToBrick);

		DisplayMetrics displayMetrics = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

		Values.SCREEN_WIDTH = displayMetrics.widthPixels;
		Values.SCREEN_HEIGHT = displayMetrics.heightPixels;

		UiTestUtils.clickOnBottomBar(solo, R.id.button_play);
		solo.waitForActivity(StageActivity.class.getSimpleName());

		solo.assertCurrentActivity("Not in stage", StageActivity.class);

		solo.goBack();
		solo.clickOnText(solo.getString(R.string.stage_dialog_screenshot));

		assertTrue("Successful screenshot Toast not found!",
				solo.searchText(solo.getString(R.string.notification_screenshot_ok)));

		solo.clickOnText(solo.getString(R.string.stage_dialog_resume));

		// -------------------------------------------------------------------------------------------------------------
		Bitmap screenshot = BitmapFactory.decodeFile(Constants.DEFAULT_ROOT + "/" + projectName + "/"
				+ StageListener.SCREENSHOT_MANUAL_FILE_NAME);

		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		Bitmap blackQuad = BitmapFactory.decodeFile(setLookBrick.getImagePath());
		int blackQuadHeight = blackQuad.getHeight();
		int blackQuadWidth = blackQuad.getWidth();
		Log.v(TAG, "black_quad.png x: " + blackQuadHeight + " y: " + blackQuadWidth);
		Log.v(TAG, "Screenshot height: " + Values.SCREEN_WIDTH + " width: " + Values.SCREEN_WIDTH);

		Log.v(TAG, (Values.SCREEN_WIDTH / 2) + (blackQuadHeight / 2) + 5 + "");

		//Two times width, because of the quadratically screenshots
		int colorInsideSizedQuad = screenshot.getPixel((Values.SCREEN_WIDTH / 2) + (blackQuadWidth / 2) + 5,
				(Values.SCREEN_WIDTH / 2) + (blackQuadHeight / 2) + 5);
		int colorOutsideSizedQuad = screenshot.getPixel(Values.SCREEN_WIDTH / 2 + blackQuadWidth + 10,
				Values.SCREEN_WIDTH / 2 + blackQuadHeight + 10);

		assertEquals("Image was not scaled up even though SetSizeTo was exectuted before!", Color.RED,
				colorInsideSizedQuad);
		assertEquals("Wrong stage background color!", Color.WHITE, colorOutsideSizedQuad);
	}

	private void createProject() {
		Values.SCREEN_HEIGHT = SCREEN_HEIGHT;
		Values.SCREEN_WIDTH = SCREEN_WIDTH;

		project = new Project(getActivity(), projectName);
		Sprite sprite = new Sprite("cat");
		Script script = new StartScript(sprite);
		setSizeToBrick = new SetSizeToBrick(sprite, 100);
		setLookBrick = new SetLookBrick(sprite);

		script.addBrick(setSizeToBrick);
		script.addBrick(setLookBrick);

		sprite.addScript(script);
		project.addSprite(sprite);

		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(sprite);
		ProjectManager.getInstance().setCurrentScript(script);
		StorageHandler.getInstance().saveProject(project);

		File image = UiTestUtils.saveFileToProject(projectName, "black_quad.png", imageRawId, getInstrumentation()
				.getContext(), UiTestUtils.FileTypes.IMAGE);
		Log.v(TAG, image.getName());
		LookData lookData = new LookData();
		lookData.setLookFilename(image.getName());
		lookData.setLookName("image");
		setLookBrick.setLook(lookData);
		sprite.getLookDataList().add(lookData);
		ProjectManager.getInstance().getFileChecksumContainer()
				.addChecksum(lookData.getChecksum(), image.getAbsolutePath());
		StorageHandler.getInstance().saveProject(project);
	}

}

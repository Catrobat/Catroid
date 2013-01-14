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
import java.util.ArrayList;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.CostumeData;
import org.catrobat.catroid.common.Values;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.SetCostumeBrick;
import org.catrobat.catroid.content.bricks.SetSizeToBrick;
import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.stage.StageListener;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.ui.adapter.BrickAdapter;
import org.catrobat.catroid.uitest.util.UiTestUtils;
import org.catrobat.catroid.utils.UtilFile;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.Smoke;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.ListView;

import com.jayway.android.robotium.solo.Solo;

public class SetSizeToBrickTest extends ActivityInstrumentationTestCase2<MainMenuActivity> {
	private static final int SCREEN_WIDTH = 480;
	private static final int SCREEN_HEIGHT = 800;

	private static final String TAG = SetSizeToBrickTest.class.getSimpleName();

	private String projectName = "SetSizeToBrickTestProject";
	private Solo solo;
	private Project project;
	private SetSizeToBrick setSizeToBrick;
	private SetCostumeBrick setCostumeBrick;
	private int imageRawId = org.catrobat.catroid.uitest.R.raw.red_quad;

	public SetSizeToBrickTest() {
		super(MainMenuActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		createProject();
		solo = new Solo(getInstrumentation(), getActivity());
		UiTestUtils.getIntoScriptActivityFromMainMenu(solo);
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
		ListView dragDropListView = UiTestUtils.getScriptListView(solo);
		BrickAdapter adapter = (BrickAdapter) dragDropListView.getAdapter();

		int childrenCount = adapter.getChildCountFromLastGroup();
		int groupCount = adapter.getScriptCount();

		assertEquals("Incorrect number of bricks.", 3 + 1, dragDropListView.getChildCount()); // don't forget the footer
		assertEquals("Incorrect number of bricks.", 2, childrenCount);

		ArrayList<Brick> projectBrickList = project.getSpriteList().get(0).getScript(0).getBrickList();
		assertEquals("Incorrect number of bricks.", 2, projectBrickList.size());

		assertEquals("Wrong Brick instance.", projectBrickList.get(0).getClass().getSimpleName(),
				adapter.getChild(groupCount - 1, 0).getClass().getSimpleName());
		assertNotNull("TextView does not exist", solo.getText(solo.getString(R.string.brick_set_size_to)));

		double newSize = 200;

		UiTestUtils.clickEnterClose(solo, 0, newSize + "");

		double currentSize = (Double) UiTestUtils.getPrivateField("size", setSizeToBrick);
		assertEquals("Wrong text in field", newSize, currentSize);
		assertEquals("Text not updated", newSize, Double.parseDouble(solo.getEditText(0).getText().toString()));

		// -------------------------------------------------------------------------------------------------------------
		DisplayMetrics displayMetrics = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

		Values.SCREEN_WIDTH = displayMetrics.widthPixels;
		Values.SCREEN_HEIGHT = displayMetrics.heightPixels;

		UiTestUtils.clickOnBottomBar(solo, R.id.button_play);

		solo.assertCurrentActivity("Not in stage", StageActivity.class);

		solo.goBack();
		solo.clickOnText(solo.getString(R.string.stage_dialog_screenshot));

		assertTrue("Successful screenshot Toast not found!",
				solo.searchText(solo.getString(R.string.notification_screenshot_ok)));

		solo.clickOnText(solo.getString(R.string.stage_dialog_resume));

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

	private void createProject() {
		Values.SCREEN_HEIGHT = SCREEN_HEIGHT;
		Values.SCREEN_WIDTH = SCREEN_WIDTH;

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
		ProjectManager.getInstance().getFileChecksumContainer()
				.addChecksum(costumeData.getChecksum(), image.getAbsolutePath());
		ProjectManager.getInstance().saveProject();
	}

}

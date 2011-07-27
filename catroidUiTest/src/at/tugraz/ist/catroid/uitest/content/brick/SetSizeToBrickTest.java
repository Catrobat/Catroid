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
import at.tugraz.ist.catroid.common.Consts;
import at.tugraz.ist.catroid.common.Values;
import at.tugraz.ist.catroid.content.Project;
import at.tugraz.ist.catroid.content.Script;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.content.StartScript;
import at.tugraz.ist.catroid.content.bricks.Brick;
import at.tugraz.ist.catroid.content.bricks.SetCostumeBrick;
import at.tugraz.ist.catroid.content.bricks.SetSizeToBrick;
import at.tugraz.ist.catroid.stage.StageActivity;
import at.tugraz.ist.catroid.ui.ScriptActivity;
import at.tugraz.ist.catroid.uitest.util.UiTestUtils;

import com.jayway.android.robotium.solo.Solo;

public class SetSizeToBrickTest extends ActivityInstrumentationTestCase2<ScriptActivity> {
	private static final String TAG = SetSizeToBrickTest.class.getSimpleName();
	private String projectName = "SetSizeToBrickTestProject";
	private Solo solo;
	private Project project;
	private SetSizeToBrick setSizeToBrick;
	private SetCostumeBrick setCostumeBrick;
	private int imageRawId = at.tugraz.ist.catroid.uitest.R.raw.black_quad;

	public SetSizeToBrickTest() {
		super("at.tugraz.ist.catroid", ScriptActivity.class);
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
		super.tearDown();
	}

	@Smoke
	public void testSetSizeToBrick() {
		int childrenCount = getActivity().getAdapter().getChildCountFromLastGroup();
		int groupCount = getActivity().getAdapter().getGroupCount();
		assertEquals("Incorrect number of bricks.", 3, solo.getCurrentListViews().get(0).getChildCount());
		assertEquals("Incorrect number of bricks.", 2, childrenCount);

		ArrayList<Brick> projectBrickList = project.getSpriteList().get(0).getScript(0).getBrickList();
		assertEquals("Incorrect number of bricks.", 2, projectBrickList.size());

		assertEquals("Wrong Brick instance.", projectBrickList.get(0),
				getActivity().getAdapter().getChild(groupCount - 1, 0));
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

		UiTestUtils.clickOnImageButton(solo, R.id.btn_action_play);

		solo.assertCurrentActivity("Not in stage", StageActivity.class);

		solo.sleep(1500);

		solo.clickOnScreen(Values.SCREEN_WIDTH, 0);

		assertTrue("Successful screenshot Toast not found!",
				solo.searchText(getActivity().getString(R.string.notification_screenshot_ok)));

		// -------------------------------------------------------------------------------------------------------------
		Bitmap screenshot = BitmapFactory.decodeFile(Consts.DEFAULT_ROOT + "/" + projectName + "/"
				+ Consts.SCREENSHOT_FILE_NAME);

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

		assertEquals("Image was not scaled up even though SetSizeTo was exectuted before!", Color.BLACK,
				colorInsideSizedQuad);
		assertEquals("Wrong stage background color!", Color.WHITE, colorOutsideSizedQuad);
	}

	private void createProject() {
		project = new Project(null, projectName);
		Sprite sprite = new Sprite("cat");
		Script script = new StartScript("script", sprite);
		setSizeToBrick = new SetSizeToBrick(sprite, 100);
		setCostumeBrick = new SetCostumeBrick(sprite);

		script.addBrick(setSizeToBrick);
		script.addBrick(setCostumeBrick);

		sprite.addScript(script);
		project.addSprite(sprite);

		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(sprite);
		ProjectManager.getInstance().setCurrentScript(script);

		File image = UiTestUtils.saveFileToProject(projectName, "black_quad.png", imageRawId, getInstrumentation()
				.getContext(), UiTestUtils.TYPE_IMAGE_FILE);
		Log.v(TAG, image.getName());
		setCostumeBrick.setCostume(image.getName());
	}

}
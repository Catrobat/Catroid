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
import java.util.Vector;

import android.graphics.Point;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.Smoke;
import android.util.DisplayMetrics;
import android.util.Log;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.common.Values;
import at.tugraz.ist.catroid.content.Project;
import at.tugraz.ist.catroid.content.Script;
import at.tugraz.ist.catroid.content.SpeechBubble;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.content.StartScript;
import at.tugraz.ist.catroid.content.bricks.Brick;
import at.tugraz.ist.catroid.content.bricks.PlaceAtBrick;
import at.tugraz.ist.catroid.content.bricks.SayBrick;
import at.tugraz.ist.catroid.content.bricks.SetCostumeBrick;
import at.tugraz.ist.catroid.stage.StageActivity;
import at.tugraz.ist.catroid.ui.ScriptActivity;
import at.tugraz.ist.catroid.uitest.util.UiTestUtils;

import com.jayway.android.robotium.solo.Solo;

// TODO: create different Tests for differnt SpeechBubblePosition and Sizes (4-6)

public class SpeechBubblesTest extends ActivityInstrumentationTestCase2<ScriptActivity> {
	private static final String TAG = SpeechBubblesTest.class.getSimpleName();

	private String projectName2 = "ProjectName2";
	private String projectName3 = "ProjectName3";

	private Project project2;
	private Project project3;

	//	private Sprite sprite1;
	private Sprite sprite2;
	private Sprite sprite3;

	//	private Script script1;
	private Script script2;
	private Script script3;

	//	private PlaceAtBrick placeAtBrick1;
	private PlaceAtBrick placeAtBrick2;
	private PlaceAtBrick placeAtBrick3;

	//	private SetCostumeBrick setCostumeBrick1;
	private SetCostumeBrick setCostumeBrick2;
	private SetCostumeBrick setCostumeBrick3;

	//	private SayBrick sayBrick1;
	private SayBrick sayBrick2;
	private SayBrick sayBrick3;

	//	private String text1;
	private String text2;
	private String text3;

	private Solo solo;

	private int imageRawId = at.tugraz.ist.catroid.uitest.R.raw.testobject01;

	public SpeechBubblesTest() {
		super("at.tugraz.ist.catroid", ScriptActivity.class);
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
	public void testBasicFuntionality01() {
		String projectName1 = "ProjectName1";
		Project project1 = new Project(null, projectName1);
		Sprite sprite1 = new Sprite("cat");
		Script script1 = new StartScript("script", sprite1);
		PlaceAtBrick placeAtBrick1 = new PlaceAtBrick(sprite1, 20, 20);
		SetCostumeBrick setCostumeBrick1 = new SetCostumeBrick(sprite1);
		String text1 = "Fortschritt durch Catroid!";
		SayBrick sayBrick1 = new SayBrick(sprite1, text1);

		script1.addBrick(setCostumeBrick1);
		script1.addBrick(placeAtBrick1);
		script1.addBrick(sayBrick1);

		sprite1.addScript(script1);
		project1.addSprite(sprite1);

		ProjectManager.getInstance().setProject(project1);
		ProjectManager.getInstance().setCurrentSprite(sprite1);
		ProjectManager.getInstance().setCurrentScript(script1);

		File image = UiTestUtils.saveFileToProject(projectName1, "black_quad.png", imageRawId, getInstrumentation()
				.getContext(), UiTestUtils.TYPE_IMAGE_FILE);
		Log.v(TAG, image.getName());
		setCostumeBrick1.setCostume(image.getName());
		SpeechBubble.setVisualMode(true);
		// -------------------------------------------------------------------------------------------------------------
		solo = new Solo(getInstrumentation(), getActivity());

		int childrenCount = getActivity().getAdapter().getChildCountFromLastGroup();
		assertEquals("Incorrect number of bricks.", 4, solo.getCurrentListViews().get(0).getChildCount());
		assertEquals("Incorrect number of bricks.", 3, childrenCount);
		ArrayList<Brick> projectBrickList = project1.getSpriteList().get(0).getScript(0).getBrickList();
		assertEquals("Incorrect number of bricks.", 3, projectBrickList.size());
		solo.sleep(500);
		DisplayMetrics displayMetrics = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		Values.SCREEN_WIDTH = displayMetrics.widthPixels;
		Values.SCREEN_HEIGHT = displayMetrics.heightPixels;
		UiTestUtils.clickOnImageButton(solo, R.id.btn_action_play);
		solo.assertCurrentActivity("Not in stage", StageActivity.class);
		solo.sleep(1500);
		// -------------------------------------------------------------------------------------------------------------

		SpeechBubble speechBubble = sprite1.getBubble();

		@SuppressWarnings("unchecked")
		Vector<String> textGrid = (Vector<String>) UiTestUtils.getPrivateField("textGrid", speechBubble);
		String completeText = "";
		for (int textIndex = 0; textIndex < textGrid.size(); textIndex++) {
			completeText += textGrid.elementAt(textIndex);
		}
		assertEquals("Displayed text is incomplete.", text1, completeText);
		Point speechBubblePos = (Point) UiTestUtils.getPrivateField("position", speechBubble);
		float speechBubbleWidth = (Float) UiTestUtils.getPrivateField("speechBubblePicWidth", speechBubble);
		float speechBubbleHeight = (Float) UiTestUtils.getPrivateField("speechBubblePicHeight", speechBubble);
		Log.v("SHOW.A.POINT", speechBubblePos.toString());
		Log.v("SHOW.A.WITH", "" + speechBubbleWidth);
		Log.v("SHOW.A.HEIGHT", "" + speechBubbleHeight);
		// TODO: getBitmap Mainview -> cache enable, load cache in bitmap
		// TODO: assert Red Boarder points (4x)

		solo.sleep(1500);
		// -------------------------------------------------------------------------------------------------------------
	}

	@Smoke
	public void testBasicFuntionality02() {
		createProject2();
		solo = new Solo(getInstrumentation(), getActivity());

		int childrenCount = getActivity().getAdapter().getChildCountFromLastGroup();
		assertEquals("Incorrect number of bricks.", 4, solo.getCurrentListViews().get(0).getChildCount());
		assertEquals("Incorrect number of bricks.", 3, childrenCount);
		ArrayList<Brick> projectBrickList = project2.getSpriteList().get(0).getScript(0).getBrickList();
		assertEquals("Incorrect number of bricks.", 3, projectBrickList.size());
		solo.sleep(500);
		DisplayMetrics displayMetrics = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		Values.SCREEN_WIDTH = displayMetrics.widthPixels;
		Values.SCREEN_HEIGHT = displayMetrics.heightPixels;
		UiTestUtils.clickOnImageButton(solo, R.id.btn_action_play);
		solo.assertCurrentActivity("Not in stage", StageActivity.class);
		solo.sleep(1500);
		// -------------------------------------------------------------------------------------------------------------

		SpeechBubble speechBubble = sprite2.getBubble();

		@SuppressWarnings("unchecked")
		Vector<String> textGrid = (Vector<String>) UiTestUtils.getPrivateField("textGrid", speechBubble);
		String completeText = "";
		for (int textIndex = 0; textIndex < textGrid.size(); textIndex++) {
			completeText += textGrid.elementAt(textIndex);
		}
		assertEquals("Displayed text is incomplete.", text2, completeText);
		solo.sleep(1500);
		// -------------------------------------------------------------------------------------------------------------

	}

	@Smoke
	public void testBasicFuntionality03() {
		createProject3();
		solo = new Solo(getInstrumentation(), getActivity());

		int childrenCount = getActivity().getAdapter().getChildCountFromLastGroup();
		assertEquals("Incorrect number of bricks.", 4, solo.getCurrentListViews().get(0).getChildCount());
		assertEquals("Incorrect number of bricks.", 3, childrenCount);
		ArrayList<Brick> projectBrickList = project3.getSpriteList().get(0).getScript(0).getBrickList();
		assertEquals("Incorrect number of bricks.", 3, projectBrickList.size());
		solo.sleep(500);
		DisplayMetrics displayMetrics = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		Values.SCREEN_WIDTH = displayMetrics.widthPixels;
		Values.SCREEN_HEIGHT = displayMetrics.heightPixels;
		UiTestUtils.clickOnImageButton(solo, R.id.btn_action_play);
		solo.assertCurrentActivity("Not in stage", StageActivity.class);
		solo.sleep(1500);
		// -------------------------------------------------------------------------------------------------------------

		SpeechBubble speechBubble = sprite3.getBubble();

		@SuppressWarnings("unchecked")
		Vector<String> textGrid = (Vector<String>) UiTestUtils.getPrivateField("textGrid", speechBubble);
		String completeText = "";
		for (int textIndex = 0; textIndex < textGrid.size(); textIndex++) {
			completeText += textGrid.elementAt(textIndex);
		}
		assertEquals("Displayed text is incomplete.", text3, completeText);
		solo.sleep(1500);
		// -------------------------------------------------------------------------------------------------------------
	}

	private void createProject2() {
		project2 = new Project(null, projectName2);
		Sprite sprite2 = new Sprite("cat");
		Script script2 = new StartScript("script", sprite2);
		placeAtBrick2 = new PlaceAtBrick(sprite2, 900, -300);
		setCostumeBrick2 = new SetCostumeBrick(sprite2);

		sayBrick2 = new SayBrick(sprite2, "Whatever");

		script2.addBrick(setCostumeBrick2);
		script2.addBrick(placeAtBrick2);
		script2.addBrick(sayBrick2);

		sprite2.addScript(script2);
		project2.addSprite(sprite2);

		ProjectManager.getInstance().setProject(project2);
		ProjectManager.getInstance().setCurrentSprite(sprite2);
		ProjectManager.getInstance().setCurrentScript(script2);

		File image = UiTestUtils.saveFileToProject(projectName2, "black_quad.png", imageRawId, getInstrumentation()
				.getContext(), UiTestUtils.TYPE_IMAGE_FILE);
		Log.v(TAG, image.getName());
		setCostumeBrick2.setCostume(image.getName());
	}

	private void createProject3() {
		project3 = new Project(null, projectName3);
		Sprite sprite3 = new Sprite("cat");
		Script script3 = new StartScript("script", sprite3);
		placeAtBrick3 = new PlaceAtBrick(sprite3, 900, 900);
		setCostumeBrick3 = new SetCostumeBrick(sprite3);
		sayBrick3 = new SayBrick(sprite3, "No more Catroids!");

		script3.addBrick(setCostumeBrick3);
		script3.addBrick(placeAtBrick3);
		script3.addBrick(sayBrick3);

		sprite3.addScript(script3);
		project3.addSprite(sprite3);

		ProjectManager.getInstance().setProject(project3);
		ProjectManager.getInstance().setCurrentSprite(sprite3);
		ProjectManager.getInstance().setCurrentScript(script3);

		File image = UiTestUtils.saveFileToProject(projectName3, "black_quad.png", imageRawId, getInstrumentation()
				.getContext(), UiTestUtils.TYPE_IMAGE_FILE);
		Log.v(TAG, image.getName());
		setCostumeBrick3.setCostume(image.getName());

	}

}
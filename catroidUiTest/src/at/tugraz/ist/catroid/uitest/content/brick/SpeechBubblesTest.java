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

import android.graphics.Point;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.Smoke;
import android.util.DisplayMetrics;
import android.util.Log;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.common.Values;
import at.tugraz.ist.catroid.content.Project;
import at.tugraz.ist.catroid.content.Script;
import at.tugraz.ist.catroid.content.SpeechBubble;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.content.StartScript;
import at.tugraz.ist.catroid.content.TapScript;
import at.tugraz.ist.catroid.content.bricks.ComeToFrontBrick;
import at.tugraz.ist.catroid.content.bricks.HideBrick;
import at.tugraz.ist.catroid.content.bricks.PlaceAtBrick;
import at.tugraz.ist.catroid.content.bricks.SayBrick;
import at.tugraz.ist.catroid.content.bricks.SetCostumeBrick;
import at.tugraz.ist.catroid.content.bricks.SetSizeToBrick;
import at.tugraz.ist.catroid.io.StorageHandler;
import at.tugraz.ist.catroid.ui.MainMenuActivity;
import at.tugraz.ist.catroid.uitest.util.UiTestUtils;

import com.jayway.android.robotium.solo.Solo;

public class SpeechBubblesTest extends ActivityInstrumentationTestCase2<MainMenuActivity> {
	private static final String TAG = SpeechBubblesTest.class.getSimpleName();
	private StorageHandler storageHandler;
	private Solo solo;
	private int imageRawId = at.tugraz.ist.catroid.uitest.R.raw.black_quad;
	private final String projectName1 = UiTestUtils.PROJECTNAME1;

	public SpeechBubblesTest() {
		super("at.tugraz.ist.catroid", MainMenuActivity.class);
		storageHandler = StorageHandler.getInstance();
	}

	@Override
	public void setUp() throws Exception {
		UiTestUtils.clearAllUtilTestProjects();

		solo = new Solo(getInstrumentation(), getActivity());
		super.setUp();
	}

	@Override
	public void tearDown() throws Exception {
		try {
			solo.finalize();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		UiTestUtils.clearAllUtilTestProjects();

		getActivity().finish();
		super.tearDown();
	}

	@Smoke
	public void testNoClickFunctionality() {
		String projectName = "project2";
		Sprite firstSprite = new Sprite("sprite1");
		Script startScript1 = new StartScript("start1", firstSprite);
		Script touchScript1 = new TapScript("script1", firstSprite);
		SetCostumeBrick setCostumeBrick = new SetCostumeBrick(firstSprite);
		String speechBubbleText = "Click on this SpeechBubble!";
		startScript1.addBrick(setCostumeBrick);
		touchScript1.addBrick(new HideBrick(firstSprite));
		firstSprite.addScript(startScript1);
		firstSprite.addScript(touchScript1);
		Sprite secondSprite = new Sprite("sprite2");
		Script startScript2 = new StartScript("start2", secondSprite);
		Script touchScript2 = new TapScript("script2", secondSprite);
		SetCostumeBrick setCostumeBrick2 = new SetCostumeBrick(secondSprite);
		SayBrick sayBrick2 = new SayBrick(secondSprite, speechBubbleText);
		startScript2.addBrick(setCostumeBrick2);
		startScript2.addBrick(sayBrick2);
		startScript2.addBrick(new PlaceAtBrick(secondSprite, -400, -250));
		touchScript2.addBrick(new SetSizeToBrick(secondSprite, 200));
		touchScript2.addBrick(new ComeToFrontBrick(secondSprite));
		secondSprite.addScript(startScript2);
		secondSprite.addScript(touchScript2);
		ArrayList<Sprite> spriteList = new ArrayList<Sprite>();
		spriteList.add(firstSprite);
		spriteList.add(secondSprite);
		Project project4 = UiTestUtils.createProject(projectName, spriteList, getActivity());
		File image = UiTestUtils.saveFileToProject(projectName, "black_quad.png", imageRawId, getInstrumentation()
				.getContext(), UiTestUtils.TYPE_IMAGE_FILE);
		Log.v(TAG, image.getName());
		setCostumeBrick.setCostume(image.getName());
		setCostumeBrick2.setCostume(image.getName());
		storageHandler.saveProject(project4);
		DisplayMetrics displayMetrics = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		Values.SCREEN_WIDTH = displayMetrics.widthPixels;
		Values.SCREEN_HEIGHT = displayMetrics.heightPixels;
		UiTestUtils.clickOnImageButton(solo, R.id.btn_action_play);
		solo.sleep(5000);
		// -------------------------------------------------------------------------------------------------------------
		SpeechBubble speechBubble = spriteList.get(0).getBubble();

		Log.v(TAG, speechBubble.toString());
		Point position = (Point) UiTestUtils.getPrivateField("position", speechBubble);
		float speechBubblePicHeight = (Float) UiTestUtils.getPrivateField("speechBubblePicHeight", speechBubble);
		float speechBubblePicWidth = (Float) UiTestUtils.getPrivateField("speechBubblePicWidth", speechBubble);
		Log.v(TAG, position.toString());
		Log.v(TAG, "H: " + speechBubblePicHeight + ", W:" + speechBubblePicWidth);
		int clickWidth = Values.SCREEN_WIDTH / 2;
		int clickHeight = Values.SCREEN_HEIGHT / 2;
		Log.v(TAG, "click: " + clickWidth + " " + clickHeight);
		solo.clickOnScreen(clickWidth, clickHeight);
		solo.sleep(5000);
		boolean visible = (Boolean) UiTestUtils.getPrivateField("isVisible", firstSprite);
		assertEquals("SpeechBubble is clickable.", false, visible);
		// -------------------------------------------------------------------------------------------------------------
	}

	@Smoke
	public void testBasicPosition() {

		Sprite sprite = new Sprite("sprite");
		Script startScript = new StartScript("startscript", sprite);
		SetCostumeBrick setCostumeBrick = new SetCostumeBrick(sprite);
		String speechBubbleText = "AB CDE";
		SayBrick sayBrick = new SayBrick(sprite, speechBubbleText);
		startScript.addBrick(setCostumeBrick);
		startScript.addBrick(new ComeToFrontBrick(sprite));
		startScript.addBrick(sayBrick);
		sprite.addScript(startScript);
		ArrayList<Sprite> spriteList = new ArrayList<Sprite>();
		spriteList.add(sprite);
		Project project = UiTestUtils.createProject(projectName1, spriteList, getActivity());
		File image = UiTestUtils.saveFileToProject(projectName1, "black_quad.png", imageRawId, getInstrumentation()
				.getContext(), UiTestUtils.TYPE_IMAGE_FILE);
		setCostumeBrick.setCostume(image.getName());
		storageHandler.saveProject(project);
		DisplayMetrics displayMetrics = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		Values.SCREEN_WIDTH = displayMetrics.widthPixels;
		Values.SCREEN_HEIGHT = displayMetrics.heightPixels;
		UiTestUtils.clickOnImageButton(solo, R.id.btn_action_play);
		solo.sleep(5000);
		// -------------------------------------------------------------------------------------------------------------
		SpeechBubble speechBubble = spriteList.get(0).getBubble();

		Log.v(TAG, speechBubble.toString());
		Point position = (Point) UiTestUtils.getPrivateField("position", speechBubble);
		float speechBubblePicHeight = (Float) UiTestUtils.getPrivateField("speechBubblePicHeight", speechBubble);
		float speechBubblePicWidth = (Float) UiTestUtils.getPrivateField("speechBubblePicWidth", speechBubble);
		Log.v(TAG, position.toString());
		Log.v(TAG, "H: " + speechBubblePicHeight + ", W:" + speechBubblePicWidth);
		solo.sleep(1500);

		// -------------------------------------------------------------------------------------------------------------
	}

	public void testFlipPosition() {

		Sprite sprite = new Sprite("sprite");
		Script startScript = new StartScript("startscript", sprite);
		SetCostumeBrick setCostumeBrick = new SetCostumeBrick(sprite);
		String speechBubbleText = "AB CDE";
		SayBrick sayBrick = new SayBrick(sprite, speechBubbleText);
		startScript.addBrick(setCostumeBrick);
		startScript.addBrick(new ComeToFrontBrick(sprite));
		startScript.addBrick(sayBrick);
		sprite.addScript(startScript);
		ArrayList<Sprite> spriteList = new ArrayList<Sprite>();
		spriteList.add(sprite);
		Project project = UiTestUtils.createProject(projectName1, spriteList, getActivity());
		File image = UiTestUtils.saveFileToProject(projectName1, "black_quad.png", imageRawId, getInstrumentation()
				.getContext(), UiTestUtils.TYPE_IMAGE_FILE);
		setCostumeBrick.setCostume(image.getName());
		storageHandler.saveProject(project);
		DisplayMetrics displayMetrics = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		Values.SCREEN_WIDTH = displayMetrics.widthPixels;
		Values.SCREEN_HEIGHT = displayMetrics.heightPixels;
		UiTestUtils.clickOnImageButton(solo, R.id.btn_action_play);
		solo.sleep(5000);
		// -------------------------------------------------------------------------------------------------------------
		SpeechBubble speechBubble = spriteList.get(0).getBubble();

		Log.v(TAG, speechBubble.toString());
		Point position = (Point) UiTestUtils.getPrivateField("position", speechBubble);
		float speechBubblePicHeight = (Float) UiTestUtils.getPrivateField("speechBubblePicHeight", speechBubble);
		float speechBubblePicWidth = (Float) UiTestUtils.getPrivateField("speechBubblePicWidth", speechBubble);
		Log.v(TAG, position.toString());
		Log.v(TAG, "H: " + speechBubblePicHeight + ", W:" + speechBubblePicWidth);
		solo.sleep(1500);

		// -------------------------------------------------------------------------------------------------------------
	}

	public void testFlipTranslatePosition() {

		Sprite sprite = new Sprite("sprite");
		Script startScript = new StartScript("startscript", sprite);
		SetCostumeBrick setCostumeBrick = new SetCostumeBrick(sprite);
		String speechBubbleText = "AB CDE";
		SayBrick sayBrick = new SayBrick(sprite, speechBubbleText);
		startScript.addBrick(setCostumeBrick);
		startScript.addBrick(new ComeToFrontBrick(sprite));
		startScript.addBrick(sayBrick);
		sprite.addScript(startScript);
		ArrayList<Sprite> spriteList = new ArrayList<Sprite>();
		spriteList.add(sprite);
		Project project = UiTestUtils.createProject(projectName1, spriteList, getActivity());
		File image = UiTestUtils.saveFileToProject(projectName1, "black_quad.png", imageRawId, getInstrumentation()
				.getContext(), UiTestUtils.TYPE_IMAGE_FILE);
		setCostumeBrick.setCostume(image.getName());
		storageHandler.saveProject(project);
		DisplayMetrics displayMetrics = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		Values.SCREEN_WIDTH = displayMetrics.widthPixels;
		Values.SCREEN_HEIGHT = displayMetrics.heightPixels;
		UiTestUtils.clickOnImageButton(solo, R.id.btn_action_play);
		solo.sleep(5000);
		// -------------------------------------------------------------------------------------------------------------
		SpeechBubble speechBubble = spriteList.get(0).getBubble();

		Log.v(TAG, speechBubble.toString());
		Point position = (Point) UiTestUtils.getPrivateField("position", speechBubble);
		float speechBubblePicHeight = (Float) UiTestUtils.getPrivateField("speechBubblePicHeight", speechBubble);
		float speechBubblePicWidth = (Float) UiTestUtils.getPrivateField("speechBubblePicWidth", speechBubble);
		Log.v(TAG, position.toString());
		Log.v(TAG, "H: " + speechBubblePicHeight + ", W:" + speechBubblePicWidth);
		solo.sleep(1500);

		// -------------------------------------------------------------------------------------------------------------
	}

	public void changeBubbleType() {

		Sprite sprite = new Sprite("sprite");
		Script startScript = new StartScript("startscript", sprite);
		SetCostumeBrick setCostumeBrick = new SetCostumeBrick(sprite);
		String speechBubbleText = "AB CDE";
		SayBrick sayBrick = new SayBrick(sprite, speechBubbleText);
		startScript.addBrick(setCostumeBrick);
		startScript.addBrick(new ComeToFrontBrick(sprite));
		startScript.addBrick(sayBrick);
		sprite.addScript(startScript);
		ArrayList<Sprite> spriteList = new ArrayList<Sprite>();
		spriteList.add(sprite);
		Project project = UiTestUtils.createProject(projectName1, spriteList, getActivity());
		File image = UiTestUtils.saveFileToProject(projectName1, "black_quad.png", imageRawId, getInstrumentation()
				.getContext(), UiTestUtils.TYPE_IMAGE_FILE);
		setCostumeBrick.setCostume(image.getName());
		storageHandler.saveProject(project);
		DisplayMetrics displayMetrics = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		Values.SCREEN_WIDTH = displayMetrics.widthPixels;
		Values.SCREEN_HEIGHT = displayMetrics.heightPixels;
		UiTestUtils.clickOnImageButton(solo, R.id.btn_action_play);
		solo.sleep(5000);
		// -------------------------------------------------------------------------------------------------------------
		SpeechBubble speechBubble = spriteList.get(0).getBubble();

		Log.v(TAG, speechBubble.toString());
		Point position = (Point) UiTestUtils.getPrivateField("position", speechBubble);
		float speechBubblePicHeight = (Float) UiTestUtils.getPrivateField("speechBubblePicHeight", speechBubble);
		float speechBubblePicWidth = (Float) UiTestUtils.getPrivateField("speechBubblePicWidth", speechBubble);
		Log.v(TAG, position.toString());
		Log.v(TAG, "H: " + speechBubblePicHeight + ", W:" + speechBubblePicWidth);
		solo.sleep(1500);

		// -------------------------------------------------------------------------------------------------------------
	}

}
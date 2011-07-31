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
package at.tugraz.ist.catroid.uitest.stage;

import java.io.File;
import java.util.ArrayList;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.common.Consts;
import at.tugraz.ist.catroid.common.CostumeData;
import at.tugraz.ist.catroid.common.SoundInfo;
import at.tugraz.ist.catroid.common.Values;
import at.tugraz.ist.catroid.content.Costume;
import at.tugraz.ist.catroid.content.Project;
import at.tugraz.ist.catroid.content.Script;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.content.StartScript;
import at.tugraz.ist.catroid.content.TapScript;
import at.tugraz.ist.catroid.content.bricks.ComeToFrontBrick;
import at.tugraz.ist.catroid.content.bricks.GoNStepsBackBrick;
import at.tugraz.ist.catroid.content.bricks.HideBrick;
import at.tugraz.ist.catroid.content.bricks.PlaceAtBrick;
import at.tugraz.ist.catroid.content.bricks.PlaySoundBrick;
import at.tugraz.ist.catroid.content.bricks.SetCostumeBrick;
import at.tugraz.ist.catroid.content.bricks.SetSizeToBrick;
import at.tugraz.ist.catroid.content.bricks.WaitBrick;
import at.tugraz.ist.catroid.io.SoundManager;
import at.tugraz.ist.catroid.io.StorageHandler;
import at.tugraz.ist.catroid.stage.StageActivity;
import at.tugraz.ist.catroid.ui.MainMenuActivity;
import at.tugraz.ist.catroid.uitest.util.UiTestUtils;
import at.tugraz.ist.catroid.utils.UtilFile;

import com.jayway.android.robotium.solo.Solo;

public class StageTest extends ActivityInstrumentationTestCase2<MainMenuActivity> {
	private Solo solo;
	private StorageHandler storageHandler;
	private final static String TAG = "StageUITest";
	private final String projectName = UiTestUtils.PROJECTNAME1;

	private File image1;
	private File image2;
	private File soundFile;
	private String imageName1 = "image1";
	private String imageName2 = "image2";

	private int placeAt = 400;

	private int image1Width;
	private int image2Width;
	private int image1Height;
	private int image2Height;
	private final int ATTEMPTS = 3;

	private static final int IMAGE_FILE_ID = at.tugraz.ist.catroid.uitest.R.raw.icon;
	private static final int IMAGE_FILE_ID2 = at.tugraz.ist.catroid.uitest.R.raw.icon2;
	private static final int SOUND_FILE_ID = at.tugraz.ist.catroid.uitest.R.raw.testsoundui;

	public StageTest() {
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

		if (image1 != null && image1.exists()) {
			image1.delete();
		}
		if (image2 != null && image2.exists()) {
			image2.delete();
		}

		if (soundFile != null) {
			soundFile.delete();
		}

		UiTestUtils.clearAllUtilTestProjects();

		getActivity().finish();
		super.tearDown();
	}

	public void testStageFromLandscapeOrientation() {
		createTestproject(projectName);
		solo.setActivityOrientation(Solo.LANDSCAPE);
		solo.sleep(5000);
		UiTestUtils.clickOnImageButton(solo, R.id.btn_action_play);
		solo.waitForActivity(StageActivity.class.getName(), 1000);
		solo.sleep(5000);
		assertTrue("Wrong orientation! Screen height: " + Values.SCREEN_HEIGHT + ", Screen width: "
				+ Values.SCREEN_WIDTH, Values.SCREEN_HEIGHT > Values.SCREEN_WIDTH);
	}

	public void testClickOnPictureAndChangeCostume() {
		createTestproject(projectName);

		Log.v(TAG, "image1: " + image1.getAbsolutePath() + " " + image1Width + " " + image1Height);
		Log.v(TAG, "image2: " + image2.getAbsolutePath() + " " + image2Width + " " + image2Height);
		UiTestUtils.clickOnImageButton(solo, R.id.btn_action_play);

		Costume costume = ProjectManager.getInstance().getCurrentProject().getSpriteList().get(1).getCostume();
		solo.sleep(5000);
		assertEquals("image1 is not set", image1Width, costume.getImageWidth());
		assertEquals("image1 is not set", image1Height, costume.getImageHeight());
		solo.clickOnScreen(Values.SCREEN_WIDTH / 2, Values.SCREEN_HEIGHT / 2);

		solo.sleep(2000);
		costume = ProjectManager.getInstance().getCurrentProject().getSpriteList().get(1).getCostume();
		assertEquals("image2 is not set", image2Width, costume.getImageWidth());
		assertEquals("image2 is not set", image2Height, costume.getImageHeight());
	}

	public void testRunScript() {
		createTestProject2(projectName);

		Log.v(TAG, "image1: " + image1.getAbsolutePath() + " " + image1Width + " " + image1Height);

		UiTestUtils.clickOnImageButton(solo, R.id.btn_action_play);

		solo.sleep(5000);
		Costume costume = ProjectManager.getInstance().getCurrentProject().getSpriteList().get(1).getCostume();
		solo.sleep(2000);
		assertEquals("A wrong image is set", this.image2Width, costume.getImageWidth());
		assertEquals("A wrong image is set", this.image2Height, costume.getImageHeight());

		solo.clickOnScreen(Values.SCREEN_WIDTH / 2, Values.SCREEN_HEIGHT / 2); // click in the middle

		solo.sleep(3000);
		costume = ProjectManager.getInstance().getCurrentProject().getSpriteList().get(1).getCostume();
		assertEquals("Image size not set correctly", (image1Width / 2), costume.getImageWidth());

		solo.sleep(2500);
		costume = ProjectManager.getInstance().getCurrentProject().getSpriteList().get(1).getCostume();
		assertEquals("Image size not set correctly", (image1Width), costume.getImageWidth());

		int drawPositionX = Math.round(((Values.SCREEN_WIDTH / (2f * Consts.MAX_REL_COORDINATES)) * placeAt)
				+ Values.SCREEN_WIDTH / 2f);
		drawPositionX = drawPositionX - image1Width / 2;
		assertEquals("Image was not positioned correctly", drawPositionX, costume.getDrawPositionX());
	}

	public void testClickAroundPicture() {
		createTestproject(projectName);
		UiTestUtils.clickOnImageButton(solo, R.id.btn_action_play);

		Costume costume = ProjectManager.getInstance().getCurrentProject().getSpriteList().get(1).getCostume();
		solo.sleep(5000);
		assertEquals("image1 is not set ", image1Width, costume.getImageWidth());
		assertEquals("image1 is not set ", image1Height, costume.getImageHeight());

		Log.v(TAG, "image1: " + image1.getAbsolutePath() + " " + image1Width + " " + image1Height);

		int clickWidth = (Values.SCREEN_WIDTH - costume.getBitmap().getWidth()) / 2 - 4;
		int clickHeight = Values.SCREEN_HEIGHT / 2;
		Log.v(TAG, "click: " + clickWidth + " " + clickHeight);
		solo.clickOnScreen(clickWidth, clickHeight);

		clickWidth = (Values.SCREEN_WIDTH / 2);
		clickHeight = (Values.SCREEN_HEIGHT - costume.getBitmap().getHeight()) / 2 - 4;
		Log.v(TAG, "click: " + clickWidth + " " + clickHeight);
		solo.clickOnScreen(clickWidth, clickHeight);

		clickWidth = (Values.SCREEN_WIDTH / 2);
		clickHeight = ((Values.SCREEN_HEIGHT - costume.getBitmap().getHeight()) / 2) + costume.getBitmap().getHeight()
				+ 4;
		Log.v(TAG, "click: " + clickWidth + " " + clickHeight);
		solo.clickOnScreen(clickWidth, clickHeight);

		clickWidth = ((Values.SCREEN_WIDTH - costume.getBitmap().getWidth()) / 2) + costume.getBitmap().getWidth() + 4;
		clickHeight = (Values.SCREEN_HEIGHT / 2);
		Log.v(TAG, "click: " + clickWidth + " " + clickHeight);
		solo.clickOnScreen(clickWidth, clickHeight);

		solo.sleep(1000);
		costume = ProjectManager.getInstance().getCurrentProject().getSpriteList().get(1).getCostume();
		solo.sleep(2000);
		assertEquals("image1 is not set ", image1Width, costume.getImageWidth());
		assertEquals("image1 is not set ", image1Height, costume.getImageHeight());

		solo.clickOnScreen(Values.SCREEN_WIDTH / 2, Values.SCREEN_HEIGHT / 2);
		solo.sleep(1000);
		costume = ProjectManager.getInstance().getCurrentProject().getSpriteList().get(1).getCostume();
		solo.sleep(2000);
		assertEquals("image2 is not set ", image2Width, costume.getImageWidth());
		assertEquals("image2 is not set ", image2Height, costume.getImageHeight());
	}

	public void testClickImageBoundaries() {
		createTestproject(projectName);
		UiTestUtils.clickOnImageButton(solo, R.id.btn_action_play);

		Costume costume = ProjectManager.getInstance().getCurrentProject().getSpriteList().get(1).getCostume();
		solo.sleep(5000);
		int costumeWidth = costume.getBitmap().getWidth();
		int costumeHeight = costume.getBitmap().getHeight();
		int clickWidth = (Values.SCREEN_WIDTH - costumeWidth) / 2 + 4;
		int clickHeight = Values.SCREEN_HEIGHT / 2;
		Log.v(TAG, "click: " + clickWidth + " " + clickHeight);
		clickOnScreenAndReturn(clickWidth, clickHeight, image2Width, image2Height);

		clickWidth = (Values.SCREEN_WIDTH / 2);
		clickHeight = (Values.SCREEN_HEIGHT - costumeHeight) / 2 + 18;
		Log.v(TAG, "click: " + clickWidth + " " + clickHeight + " SCREEN:" + Values.SCREEN_WIDTH + " "
				+ Values.SCREEN_HEIGHT);
		clickOnScreenAndReturn(clickWidth, clickHeight, image2Width, image2Height);

		clickWidth = (Values.SCREEN_WIDTH / 2 + 15);
		clickHeight = ((Values.SCREEN_HEIGHT - costumeHeight) / 2) + costumeHeight - 4;
		Log.v(TAG, "click: " + clickWidth + " " + clickHeight);
		clickOnScreenAndReturn(clickWidth, clickHeight, image2Width, image2Height);

		clickWidth = ((Values.SCREEN_WIDTH - costumeWidth) / 2) + costumeWidth - 4;
		clickHeight = (Values.SCREEN_HEIGHT / 2);
		Log.v(TAG, "click: " + clickWidth + " " + clickHeight);
		clickOnScreenAndReturn(clickWidth, clickHeight, image2Width, image2Height);

	}

	public void testProjectFileChangesInStage() {
		// it is not allowed for the project file to change when in stage
		// add another test when you add new stage buttons
		createTestproject(projectName);
		File projectFileBeforeStage = new File(Consts.DEFAULT_ROOT + "/" + projectName + "/" + projectName
				+ Consts.PROJECT_EXTENTION);

		UiTestUtils.clickOnImageButton(solo, R.id.btn_action_play);

		solo.clickOnScreen(Values.SCREEN_WIDTH / 2, Values.SCREEN_HEIGHT / 2);
		solo.goBack();
		solo.clickOnButton(0);
		solo.clickInList(1);

		File projectFileAfterStage = new File(Consts.DEFAULT_ROOT + "/" + projectName + "/" + projectName
				+ Consts.PROJECT_EXTENTION);

		assertEquals("Project file changed!", projectFileBeforeStage.hashCode(), projectFileAfterStage.hashCode());
	}

	public void testPlayPauseHomeButton() {
		double size = 50.0;

		Project project = new Project(getActivity(), projectName);
		Sprite sprite = new Sprite("testSprite");
		Script script = new StartScript("script", sprite);
		WaitBrick waitBrick = new WaitBrick(sprite, 5000);
		SetSizeToBrick setSizeToBrick = new SetSizeToBrick(sprite, size);

		script.addBrick(waitBrick);
		script.addBrick(setSizeToBrick);
		sprite.addScript(script);
		project.addSprite(sprite);

		storageHandler.saveProject(project);
		ProjectManager.getInstance().setProject(project);

		UiTestUtils.clickOnImageButton(solo, R.id.btn_action_play);
		solo.sleep(3000);
		assertEquals("Unexpected sprite size", 100.0, sprite.getSize());
		solo.pressMenuItem(1);
		solo.sleep(6000);
		solo.pressMenuItem(1);
		assertEquals("Unexpected sprite size", 100.0, sprite.getSize());
		solo.sleep(4000);
		assertEquals("Unexpected sprite size", size, sprite.getSize());
	}

	public void testZValue() {
		createTestProject3(this.projectName);
		UiTestUtils.clickOnImageButton(solo, R.id.btn_action_play);

		solo.sleep(5000);
		solo.clickOnScreen(Values.SCREEN_WIDTH / 2, Values.SCREEN_HEIGHT / 2);
		solo.sleep(1000);
		Costume costume = ProjectManager.getInstance().getCurrentProject().getSpriteList().get(2).getCostume();

		assertEquals("costume has wrong width --> touch worked on it", image2Width, costume.getImageWidth());
		assertEquals("costume has wrong height --> touch worked on it", image2Height, costume.getImageHeight());

		//		solo.sleep(3000);
		solo.clickOnScreen(Values.SCREEN_WIDTH / 2, Values.SCREEN_HEIGHT / 2);
		solo.sleep(3000);
		assertEquals("costume has wrong width", image2Width * 2, costume.getBitmap().getWidth());
		assertEquals("costume has wrong height", image2Height * 2, costume.getBitmap().getHeight());
	}

	public void testMediaPlayerPlaying() {
		this.createTestProjectWithSound();
		MediaPlayer mediaPlayer = SoundManager.getInstance().getMediaPlayer();
		solo.sleep(800);
		UiTestUtils.clickOnImageButton(solo, R.id.btn_action_play);
		solo.sleep(5000);
		solo.clickOnScreen(Values.SCREEN_WIDTH / 2, Values.SCREEN_HEIGHT / 2);
		solo.sleep(250);
		int count = 0;
		while (true) {
			if (mediaPlayer.isPlaying()) {
				break;
			}
			solo.sleep(500);
			count++;
			if (count >= ATTEMPTS) {
				fail("MediaPlayer is not playing");
			}
		}

	}

	public void testMediaPlayerPause() {
		MediaPlayer mediaPlayer = SoundManager.getInstance().getMediaPlayer();

		this.createTestProjectWithSound();

		UiTestUtils.clickOnImageButton(solo, R.id.btn_action_play);
		solo.sleep(5000);
		solo.clickOnScreen(Values.SCREEN_WIDTH / 2, Values.SCREEN_HEIGHT / 2);
		solo.pressMenuItem(1);
		solo.sleep(500);
		assertFalse("Media player is playing while pausing", mediaPlayer.isPlaying());
		solo.sleep(1000);
		solo.pressMenuItem(1);
		int count = 0;
		while (true) {
			if (mediaPlayer.isPlaying()) {
				break;
			}
			solo.sleep(20);
			count++;
			if (count >= ATTEMPTS) {
				fail("Media player is not playing after pause");
			}
		}
	}

	public void testMediaPlayerNotPlayingAfterPause() {
		MediaPlayer mediaPlayer = SoundManager.getInstance().getMediaPlayer();

		this.createTestProjectWithSound();
		UiTestUtils.clickOnImageButton(solo, R.id.btn_action_play);
		solo.sleep(5000);
		solo.pressMenuItem(1);
		solo.sleep(1000);
		solo.pressMenuItem(1);
		assertFalse("Media Player is playing", mediaPlayer.isPlaying());
	}

	public void testClickOnHiddenSprite() {
		createTestProject4(projectName);
		UiTestUtils.clickOnImageButton(solo, R.id.btn_action_play);

		solo.sleep(500);
		solo.clickOnScreen(Values.SCREEN_WIDTH / 2, Values.SCREEN_HEIGHT / 2);
		solo.sleep(1000);

		Sprite sprite = ProjectManager.getInstance().getCurrentProject().getSpriteList().get(1);
		assertEquals("Unexpected sprite size", 100.0, sprite.getSize());
	}

	public void testCanvas() {
		Sprite sprite = new Sprite("sprite1");
		Script script = new StartScript("script1", sprite);
		sprite.addScript(script);
		SetCostumeBrick setCostumeBrick = new SetCostumeBrick(sprite);
		script.addBrick(setCostumeBrick);
		ArrayList<Sprite> spriteList = new ArrayList<Sprite>();
		spriteList.add(sprite);
		UiTestUtils.createProject(projectName, spriteList, getActivity());
		File image = UiTestUtils.saveFileToProject(projectName, imageName1,
				at.tugraz.ist.catroid.uitest.R.raw.red_quad, getInstrumentation().getContext(),
				UiTestUtils.TYPE_IMAGE_FILE);
		setImageMemberProperties(image);
		CostumeData costumeData = new CostumeData();
		costumeData.setCostumeFilename(image.getName());
		costumeData.setCostumeName("image");
		setCostumeBrick.setCostume(costumeData);
		solo.sleep(100);
		UiTestUtils.clickOnImageButton(solo, R.id.btn_action_play);
		solo.clickOnScreen(Values.SCREEN_WIDTH, 0); //save thumbnail
		solo.sleep(5000);

		//File file = new File(Consts.DEFAULT_ROOT + "/" + projectName + "/" + Consts.SCREENSHOT_FILE_NAME);
		Bitmap bitmap = BitmapFactory.decodeFile(Consts.DEFAULT_ROOT + "/" + projectName + "/"
				+ Consts.SCREENSHOT_FILE_NAME);

		int borderWidth = ((Values.SCREEN_WIDTH / 2) + 100 / 2);
		int borderHeight = ((Values.SCREEN_HEIGHT / 2) + 100 / 2);
		int startWidth = ((Values.SCREEN_WIDTH - 100) / 2);
		int startHeight = ((Values.SCREEN_HEIGHT - 100) / 2);

		for (int i = startWidth; i < borderWidth; i++) {
			for (int j = startHeight; j < borderHeight; j++) {
				assertEquals("pixel is not red", Color.RED, bitmap.getPixel(i, j));
				//Log.v(TAG, "in TEST " + i + " " + j);
			}
		}

		for (int j = startHeight; j < borderHeight; j++) {
			assertEquals("pixel is not white", Color.WHITE, bitmap.getPixel(startWidth - 1, j));
			//Log.v(TAG, "in TEST2 " + (startWidth - 1) + " " + j);
		}

		for (int j = startHeight; j < borderHeight; j++) {
			assertEquals("pixel is not white", Color.WHITE, bitmap.getPixel(borderWidth, j));
			//Log.v(TAG, "in TEST3 " + borderWidth + " " + j);
		}

		for (int i = startWidth; i < borderWidth; i++) {
			assertEquals("pixel is not white", Color.WHITE, bitmap.getPixel(i, startHeight - 1));
			//Log.v(TAG, "in TEST4 " + i + " " + (startHeight - 1));
		}

		for (int i = startWidth; i < borderWidth; i++) {
			assertEquals("pixel is not white", Color.WHITE, bitmap.getPixel(i, borderHeight));
			//Log.v(TAG, "in TEST5 " + i + " " + borderHeight);
		}

	}

	public void clickOnScreenAndReturn(int x, int y, int expectedWidth, int expectedHeight) {
		solo.clickOnScreen(x, y);
		solo.sleep(1000);
		Costume costume = ProjectManager.getInstance().getCurrentProject().getSpriteList().get(1).getCostume();
		solo.sleep(500);
		assertEquals("Unexpected image width", expectedWidth, costume.getImageWidth());
		assertEquals("Unexpected image height", expectedHeight, costume.getImageHeight());

		solo.goBack();
		solo.sleep(2000);
		UiTestUtils.clickOnImageButton(solo, R.id.btn_action_play);
		solo.sleep(5000);
	}

	public void createTestproject(String projectName) {

		//creating sprites for project:
		Sprite firstSprite = new Sprite("sprite1");
		Script startScript = new StartScript("script1", firstSprite);
		Script touchScript = new TapScript("script2", firstSprite);

		SetCostumeBrick setCostumeBrick = new SetCostumeBrick(firstSprite);
		SetCostumeBrick setCostumeBrick2 = new SetCostumeBrick(firstSprite);

		startScript.addBrick(setCostumeBrick);
		touchScript.addBrick(setCostumeBrick2);
		firstSprite.addScript(startScript);
		firstSprite.addScript(touchScript);

		ArrayList<Sprite> spriteList = new ArrayList<Sprite>();
		spriteList.add(firstSprite);
		Project project = UiTestUtils.createProject(projectName, spriteList, getActivity());

		image1 = UiTestUtils.saveFileToProject(projectName, imageName1, IMAGE_FILE_ID, getInstrumentation()
				.getContext(), UiTestUtils.TYPE_IMAGE_FILE);
		image2 = UiTestUtils.saveFileToProject(projectName, imageName2, IMAGE_FILE_ID2, getInstrumentation()
				.getContext(), UiTestUtils.TYPE_IMAGE_FILE);
		setImageMemberProperties(image1);
		setImageMemberProperties(image2);
		CostumeData costumeData = new CostumeData();
		costumeData.setCostumeFilename(image1.getName());
		costumeData.setCostumeName("image1");
		setCostumeBrick.setCostume(costumeData);
		costumeData = new CostumeData();
		costumeData.setCostumeFilename(image2.getName());
		costumeData.setCostumeName("image2");
		setCostumeBrick2.setCostume(costumeData);

		storageHandler.saveProject(project);
	}

	public void createTestProject2(String projectName) {

		//creating sprites for project:
		Sprite firstSprite = new Sprite("sprite1");
		Script startScript = new StartScript("startscript", firstSprite);
		Script touchScript = new TapScript("script2", firstSprite);

		SetCostumeBrick setCostumeBrick = new SetCostumeBrick(firstSprite);
		SetCostumeBrick setCostumeBrick2 = new SetCostumeBrick(firstSprite);

		SetSizeToBrick setSizeToBrick = new SetSizeToBrick(firstSprite, 50);
		SetSizeToBrick setSizeToBrick2 = new SetSizeToBrick(firstSprite, 100);
		WaitBrick waitBrick = new WaitBrick(firstSprite, 2000);
		PlaceAtBrick placeAtBrick = new PlaceAtBrick(firstSprite, placeAt, placeAt);

		startScript.addBrick(setCostumeBrick2);
		touchScript.addBrick(setCostumeBrick);
		touchScript.addBrick(setSizeToBrick);
		touchScript.addBrick(waitBrick);
		touchScript.addBrick(setSizeToBrick2);
		touchScript.addBrick(placeAtBrick);
		firstSprite.addScript(startScript);
		firstSprite.addScript(touchScript);

		ArrayList<Sprite> spriteList = new ArrayList<Sprite>();
		spriteList.add(firstSprite);
		Project project = UiTestUtils.createProject(projectName, spriteList, getActivity());

		image1 = UiTestUtils.saveFileToProject(projectName, imageName1, IMAGE_FILE_ID, getInstrumentation()
				.getContext(), UiTestUtils.TYPE_IMAGE_FILE);
		image2 = UiTestUtils.saveFileToProject(projectName, imageName2, IMAGE_FILE_ID2, getInstrumentation()
				.getContext(), UiTestUtils.TYPE_IMAGE_FILE);
		setImageMemberProperties(image1);
		setImageMemberProperties(image2);
		CostumeData costumeData = new CostumeData();
		costumeData.setCostumeFilename(image1.getName());
		costumeData.setCostumeName("image1");
		setCostumeBrick.setCostume(costumeData);
		costumeData = new CostumeData();
		costumeData.setCostumeFilename(image2.getName());
		costumeData.setCostumeName("image2");
		setCostumeBrick2.setCostume(costumeData);

		storageHandler.saveProject(project);
	}

	public void createTestProject3(String projectName) {

		//creating sprites for project:

		// sprite1 --------------------------------
		Sprite firstSprite = new Sprite("sprite1");
		Script startScript1 = new StartScript("start1", firstSprite);
		Script touchScript1 = new TapScript("script1", firstSprite);
		// creating bricks:
		SetCostumeBrick setCostumeBrick = new SetCostumeBrick(firstSprite);

		// adding bricks:
		startScript1.addBrick(setCostumeBrick);
		startScript1.addBrick(new ComeToFrontBrick(firstSprite));
		touchScript1.addBrick(new GoNStepsBackBrick(firstSprite, 2));
		firstSprite.addScript(startScript1);
		firstSprite.addScript(touchScript1);

		// sprite2 --------------------------------
		Sprite secondSprite = new Sprite("sprite2");
		Script startScript2 = new StartScript("start2", secondSprite);
		Script touchScript2 = new TapScript("script2", secondSprite);
		// creating bricks:
		SetCostumeBrick setCostumeBrick2 = new SetCostumeBrick(secondSprite);
		// adding bricks:
		startScript2.addBrick(setCostumeBrick2);
		touchScript2.addBrick(new SetSizeToBrick(secondSprite, 200));

		secondSprite.addScript(startScript2);
		secondSprite.addScript(touchScript2);

		ArrayList<Sprite> spriteList = new ArrayList<Sprite>();
		spriteList.add(firstSprite);
		spriteList.add(secondSprite);
		Project project = UiTestUtils.createProject(projectName, spriteList, getActivity());

		image1 = UiTestUtils.saveFileToProject(projectName, imageName1, IMAGE_FILE_ID, getInstrumentation()
				.getContext(), UiTestUtils.TYPE_IMAGE_FILE);
		image2 = UiTestUtils.saveFileToProject(projectName, imageName2, IMAGE_FILE_ID2, getInstrumentation()
				.getContext(), UiTestUtils.TYPE_IMAGE_FILE);
		setImageMemberProperties(image1);
		setImageMemberProperties(image2);
		CostumeData costumeData = new CostumeData();
		costumeData.setCostumeFilename(image1.getName());
		costumeData.setCostumeName("image1");
		setCostumeBrick.setCostume(costumeData);
		costumeData = new CostumeData();
		costumeData.setCostumeFilename(image2.getName());
		costumeData.setCostumeName("image2");
		setCostumeBrick2.setCostume(costumeData);

		storageHandler.saveProject(project);
	}

	public void createTestProject4(String projectName) {

		//creating sprites for project:
		Sprite firstSprite = new Sprite("sprite1");
		Script startScript = new StartScript("startscript", firstSprite);
		Script touchScript = new TapScript("touchscript", firstSprite);

		SetCostumeBrick setCostumeBrick = new SetCostumeBrick(firstSprite);

		SetSizeToBrick setSizeToBrick = new SetSizeToBrick(firstSprite, 50);
		HideBrick hideBrick = new HideBrick(firstSprite);

		startScript.addBrick(setCostumeBrick);
		startScript.addBrick(hideBrick);
		touchScript.addBrick(setSizeToBrick);
		firstSprite.addScript(startScript);
		firstSprite.addScript(touchScript);

		ArrayList<Sprite> spriteList = new ArrayList<Sprite>();
		spriteList.add(firstSprite);
		Project project = UiTestUtils.createProject(projectName, spriteList, getActivity());

		image1 = UiTestUtils.saveFileToProject(projectName, imageName1, IMAGE_FILE_ID, getInstrumentation()
				.getContext(), UiTestUtils.TYPE_IMAGE_FILE);
		setImageMemberProperties(image1);
		CostumeData costumeData = new CostumeData();
		costumeData.setCostumeFilename(image1.getName());
		costumeData.setCostumeName("image1");
		setCostumeBrick.setCostume(costumeData);

		storageHandler.saveProject(project);
	}

	public void createTestProjectWithSound() {

		//creating sprites for project:
		Sprite firstSprite = new Sprite("sprite1");
		Script startScript = new StartScript("startscript", firstSprite);
		Script touchScript = new TapScript("touchscript", firstSprite);

		SetCostumeBrick setCostumeBrick = new SetCostumeBrick(firstSprite);
		PlaySoundBrick playSoundBrick = new PlaySoundBrick(firstSprite);
		SetSizeToBrick setSizeToBrick = new SetSizeToBrick(firstSprite, 50);

		startScript.addBrick(setCostumeBrick);
		touchScript.addBrick(setSizeToBrick);
		touchScript.addBrick(playSoundBrick);

		firstSprite.addScript(startScript);
		firstSprite.addScript(touchScript);

		ArrayList<Sprite> spriteList = new ArrayList<Sprite>();
		spriteList.add(firstSprite);
		Project project = UiTestUtils.createProject(projectName, spriteList, getActivity());

		soundFile = UiTestUtils.saveFileToProject(projectName, "soundfile.mp3", SOUND_FILE_ID, getInstrumentation()
				.getContext(), UiTestUtils.TYPE_SOUND_FILE);

		image1 = UiTestUtils.saveFileToProject(projectName, imageName1, IMAGE_FILE_ID, getInstrumentation()
				.getContext(), UiTestUtils.TYPE_IMAGE_FILE);
		setImageMemberProperties(image1);

		CostumeData costumeData = new CostumeData();
		costumeData.setCostumeFilename(image1.getName());
		costumeData.setCostumeName("image1");
		setCostumeBrick.setCostume(costumeData);

		SoundInfo soundInfo = new SoundInfo();
		soundInfo.setSoundFileName(soundFile.getName());
		soundInfo.setTitle(soundFile.getName());
		playSoundBrick.setSoundInfo(soundInfo);

		storageHandler.saveProject(project);
	}

	private void setImageMemberProperties(File image) {
		BitmapFactory.Options o = new BitmapFactory.Options();
		o.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(image.getAbsolutePath(), o);

		if (image.getName().equalsIgnoreCase(imageName1)) {
			image1Width = o.outWidth;
			image1Height = o.outHeight;
		} else {
			image2Width = o.outWidth;
			image2Height = o.outHeight;
		}
	}
}

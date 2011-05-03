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
import java.io.IOException;
import java.util.ArrayList;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.test.ActivityInstrumentationTestCase2;
import at.tugraz.ist.catroid.Consts;
import at.tugraz.ist.catroid.Values;
import at.tugraz.ist.catroid.constructionSite.content.ProjectManager;
import at.tugraz.ist.catroid.content.Costume;
import at.tugraz.ist.catroid.content.Project;
import at.tugraz.ist.catroid.content.Script;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.content.bricks.ComeToFrontBrick;
import at.tugraz.ist.catroid.content.bricks.GoNStepsBackBrick;
import at.tugraz.ist.catroid.content.bricks.HideBrick;
import at.tugraz.ist.catroid.content.bricks.PlaceAtBrick;
import at.tugraz.ist.catroid.content.bricks.PlaySoundBrick;
import at.tugraz.ist.catroid.content.bricks.ScaleCostumeBrick;
import at.tugraz.ist.catroid.content.bricks.SetCostumeBrick;
import at.tugraz.ist.catroid.content.bricks.WaitBrick;
import at.tugraz.ist.catroid.io.SoundManager;
import at.tugraz.ist.catroid.io.StorageHandler;
import at.tugraz.ist.catroid.ui.MainMenuActivity;
import at.tugraz.ist.catroid.uitest.R;
import at.tugraz.ist.catroid.uitest.util.Utils;

import com.jayway.android.robotium.solo.Solo;

public class StageTest extends ActivityInstrumentationTestCase2<MainMenuActivity> {
	private Solo solo;
	private StorageHandler storageHandler;
	private final String projectName = "project1";

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

	private static final int IMAGE_FILE_ID = R.raw.icon;
	private static final int IMAGE_FILE_ID2 = R.raw.icon2;
	private static final int SOUND_FILE_ID = R.raw.testsound;

	public StageTest() {
		super("at.tugraz.ist.catroid", MainMenuActivity.class);
		storageHandler = StorageHandler.getInstance();
	}

	@Override
	public void setUp() throws Exception {
		Utils.clearProject(projectName);

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

		Utils.clearProject(projectName);

		getActivity().finish();
		super.tearDown();
	}

	public void testClickOnPictureAndChangeCostume() throws IOException, InterruptedException {
		createTestproject(projectName);

		System.out.println("image1: " + image1.getAbsolutePath() + " " + image1Width + " " + image1Height);
		System.out.println("image2: " + image2.getAbsolutePath() + " " + image2Width + " " + image2Height);
		solo.clickOnButton(1); // this is the stage //change it when you mess with the buttons

		Thread.sleep(2000);
		Costume costume = ProjectManager.getInstance().getCurrentProject().getSpriteList().get(1).getCostume();
		assertEquals("image1 is not set ", (Integer) image1Width, costume.getImageWidthHeight().first);
		assertEquals("image1 is not set ", (Integer) image1Height, costume.getImageWidthHeight().second);
		solo.clickOnScreen(Values.SCREEN_WIDTH / 2, Values.SCREEN_HEIGHT / 2);

		Thread.sleep(1000);
		costume = ProjectManager.getInstance().getCurrentProject().getSpriteList().get(1).getCostume();
		assertEquals("image2 is not set ", (Integer) image2Width, costume.getImageWidthHeight().first);
		assertEquals("image2 is not set ", (Integer) image2Height, costume.getImageWidthHeight().second);
	}

	public void testRunScript() throws IOException, InterruptedException {
		createTestProject2(projectName);

		System.out.println("image1: " + image1.getAbsolutePath() + " " + image1Width + " " + image1Height);

		solo.clickOnButton(1);
		Thread.sleep(2000);
		Costume costume = ProjectManager.getInstance().getCurrentProject().getSpriteList().get(1).getCostume();
		assertEquals("a wrong image is set", (Integer) this.image2Width, costume.getImageWidthHeight().first);
		assertEquals("a wrong image is set", (Integer) this.image2Height, costume.getImageWidthHeight().second);

		solo.clickOnScreen(Values.SCREEN_WIDTH / 2, Values.SCREEN_HEIGHT / 2); // click in se middle

		Thread.sleep(1500);
		costume = ProjectManager.getInstance().getCurrentProject().getSpriteList().get(1).getCostume();
		assertEquals("image not right scaled", (Integer) (image1Width / 2), costume.getImageWidthHeight().first);

		Thread.sleep(2500);
		costume = ProjectManager.getInstance().getCurrentProject().getSpriteList().get(1).getCostume();
		assertEquals("image not right scaled", (Integer) (image1Width), costume.getImageWidthHeight().first);

		int drawPositionX = Math.round(((Values.SCREEN_WIDTH / (2f * Consts.MAX_REL_COORDINATES)) * placeAt)
				+ Values.SCREEN_WIDTH / 2f);
		drawPositionX = drawPositionX - image1Width / 2;
		assertEquals("image was not set right", drawPositionX, costume.getDrawPositionX());
	}

	public void testClickAroundPicture() throws IOException, InterruptedException {
		createTestproject(projectName);
		solo.clickOnButton(1);

		Thread.sleep(2000);
		Costume costume = ProjectManager.getInstance().getCurrentProject().getSpriteList().get(1).getCostume();
		assertEquals("image1 is not set ", (Integer) image1Width, costume.getImageWidthHeight().first);
		assertEquals("image1 is not set ", (Integer) image1Height, costume.getImageWidthHeight().second);

		System.out.println("image1: " + image1.getAbsolutePath() + " " + image1Width + " " + image1Height);

		int clickWidth = (Values.SCREEN_WIDTH - costume.getBitmap().getWidth()) / 2 - 4;
		int clickHeight = Values.SCREEN_HEIGHT / 2;
		System.out.println("click: " + clickWidth + " " + clickHeight);
		solo.clickOnScreen(clickWidth, clickHeight);

		clickWidth = (Values.SCREEN_WIDTH / 2);
		clickHeight = (Values.SCREEN_HEIGHT - costume.getBitmap().getHeight()) / 2 - 4;
		System.out.println("click: " + clickWidth + " " + clickHeight);
		solo.clickOnScreen(clickWidth, clickHeight);

		clickWidth = (Values.SCREEN_WIDTH / 2);
		clickHeight = ((Values.SCREEN_HEIGHT - costume.getBitmap().getHeight()) / 2) + costume.getBitmap().getHeight()
				+ 4;
		System.out.println("click: " + clickWidth + " " + clickHeight);
		solo.clickOnScreen(clickWidth, clickHeight);

		clickWidth = ((Values.SCREEN_WIDTH - costume.getBitmap().getWidth()) / 2) + costume.getBitmap().getWidth() + 4;
		clickHeight = (Values.SCREEN_HEIGHT / 2);
		System.out.println("click: " + clickWidth + " " + clickHeight);
		solo.clickOnScreen(clickWidth, clickHeight);

		Thread.sleep(1000);
		costume = ProjectManager.getInstance().getCurrentProject().getSpriteList().get(1).getCostume();
		assertEquals("image1 is not set ", (Integer) image1Width, costume.getImageWidthHeight().first);
		assertEquals("image1 is not set ", (Integer) image1Height, costume.getImageWidthHeight().second);

		solo.clickOnScreen(Values.SCREEN_WIDTH / 2, Values.SCREEN_HEIGHT / 2);
		Thread.sleep(1000);
		costume = ProjectManager.getInstance().getCurrentProject().getSpriteList().get(1).getCostume();
		assertEquals("image2 is not set ", (Integer) image2Width, costume.getImageWidthHeight().first);
		assertEquals("image2 is not set ", (Integer) image2Height, costume.getImageWidthHeight().second);
	}

	public void testClickImageBoundaries() throws IOException, InterruptedException {
		createTestproject(projectName);
		solo.clickOnButton(1);
		Thread.sleep(2000);
		Costume costume = ProjectManager.getInstance().getCurrentProject().getSpriteList().get(1).getCostume();
		int costumeWidth = costume.getBitmap().getWidth();
		int costumeHeight = costume.getBitmap().getHeight();
		int clickWidth = (Values.SCREEN_WIDTH - costumeWidth) / 2 + 4;
		int clickHeight = Values.SCREEN_HEIGHT / 2;
		System.out.println("click: " + clickWidth + " " + clickHeight);
		clickOnScreenAndReturn(clickWidth, clickHeight, image2Width, image2Height);

		clickWidth = (Values.SCREEN_WIDTH / 2);
		clickHeight = (Values.SCREEN_HEIGHT - costumeHeight) / 2 + 18;
		System.out.println("click: " + clickWidth + " " + clickHeight + " SCREEN:" + Values.SCREEN_WIDTH + " "
				+ Values.SCREEN_HEIGHT);
		clickOnScreenAndReturn(clickWidth, clickHeight, image2Width, image2Height);

		clickWidth = (Values.SCREEN_WIDTH / 2 + 15);
		clickHeight = ((Values.SCREEN_HEIGHT - costumeHeight) / 2) + costumeHeight
				- 4;
		System.out.println("click: " + clickWidth + " " + clickHeight);
		clickOnScreenAndReturn(clickWidth, clickHeight, image2Width, image2Height);

		clickWidth = ((Values.SCREEN_WIDTH - costumeWidth) / 2) + costumeWidth - 4;
		clickHeight = (Values.SCREEN_HEIGHT / 2);
		System.out.println("click: " + clickWidth + " " + clickHeight);
		clickOnScreenAndReturn(clickWidth, clickHeight, image2Width, image2Height);

	}

	public void testSpfChangesInStage() throws IOException {
		// it is not allowed for the .spf file to change when in stage
		// add another test when you add new stage buttons
		createTestproject(projectName);
		File mySpfFile = new File(Consts.DEFAULT_ROOT + projectName + "/" + projectName + Consts.PROJECT_EXTENTION);

		solo.clickOnButton(1);
		solo.clickOnScreen(Values.SCREEN_WIDTH / 2, Values.SCREEN_HEIGHT / 2);
		solo.goBack();
		solo.clickOnButton(0);
		solo.clickInList(1);

		File mySpfFile2 = new File(Consts.DEFAULT_ROOT + projectName + "/" + projectName + Consts.PROJECT_EXTENTION);

		assertEquals("spf File changed!", mySpfFile.hashCode(), mySpfFile2.hashCode());
	}

	public void testPlayPauseHomeButton() throws IOException, InterruptedException {
		double scale = 50.0;

		Project project = new Project(getActivity(), projectName);
		Sprite sprite = new Sprite("testSprite");
		Script script = new Script("script", sprite);
		WaitBrick waitBrick = new WaitBrick(sprite, 4000);
		ScaleCostumeBrick scaleCostumeBrick = new ScaleCostumeBrick(sprite, scale);

		script.getBrickList().add(waitBrick);
		script.getBrickList().add(scaleCostumeBrick);
		sprite.getScriptList().add(script);
		project.getSpriteList().add(sprite);

		storageHandler.saveProject(project);
		ProjectManager.getInstance().setProject(project);

		solo.clickOnButton(1);
		solo.pressMenuItem(1);
		Thread.sleep(5000);
		solo.pressMenuItem(1);
		assertEquals(100.0, sprite.getScale());
		Thread.sleep(4000);
		assertEquals(scale, sprite.getScale());
	}

	public void testZValue() throws IOException, InterruptedException {
		createTestProject3(this.projectName);
		solo.clickOnButton(1);

		Thread.sleep(1000);
		solo.clickOnScreen(Values.SCREEN_WIDTH / 2, Values.SCREEN_HEIGHT / 2);
		Thread.sleep(1000);
		Costume costume = ProjectManager.getInstance().getCurrentProject().getSpriteList().get(2).getCostume();

		assertEquals("costume has wrong width --> touch worked on it", (Integer) image2Width,
				costume.getImageWidthHeight().first);
		assertEquals("costume has wrong height --> touch worked on it", (Integer) image2Height,
				costume.getImageWidthHeight().second);

		solo.clickOnScreen(Values.SCREEN_WIDTH / 2, Values.SCREEN_HEIGHT / 2);
		Thread.sleep(500);
		assertEquals("costume has wrong width", image2Width * 2, costume.getBitmap().getWidth());
		assertEquals("costume has wrong height", image2Height * 2, costume.getBitmap().getHeight());
	}

	public void testMediaPlayerPlaying() throws InterruptedException, IOException {

		MediaPlayer mediaPlayer = SoundManager.getInstance().getMediaPlayer();

		this.createTestProjectWithSound();
		solo.clickOnButton(1);
		solo.clickOnScreen(Values.SCREEN_WIDTH / 2, Values.SCREEN_HEIGHT / 2);
		Thread.sleep(200);
		assertTrue("Media player is not playing", mediaPlayer.isPlaying());
	}

	public void testMediaPlayerPause() throws IOException, InterruptedException {
		MediaPlayer mediaPlayer = SoundManager.getInstance().getMediaPlayer();

		this.createTestProjectWithSound();

		solo.clickOnButton(1);
		solo.clickOnScreen(Values.SCREEN_WIDTH / 2, Values.SCREEN_HEIGHT / 2);
		solo.pressMenuItem(1);
		Thread.sleep(1000);
		solo.pressMenuItem(1);
		Thread.sleep(50);
		assertTrue("Media player is not playing after pause", mediaPlayer.isPlaying());
	}

	public void testMediaPlayerNotPlayingAfterPause() throws IOException, InterruptedException {
		MediaPlayer mediaPlayer = SoundManager.getInstance().getMediaPlayer();

		this.createTestProjectWithSound();
		solo.clickOnButton(1);
		solo.pressMenuItem(1);
		Thread.sleep(1000);
		solo.pressMenuItem(1);
		assertFalse("Media Player is playing", mediaPlayer.isPlaying());
	}

	public void testClickOnHiddenSprite() throws IOException, InterruptedException {
		createTestProject4(projectName);
		solo.clickOnButton(1);
		Thread.sleep(500);
		solo.clickOnScreen(Values.SCREEN_WIDTH / 2, Values.SCREEN_HEIGHT / 2);
		Thread.sleep(1000);

		Sprite sprite = ProjectManager.getInstance().getCurrentProject().getSpriteList().get(1);
		assertEquals(100.0, sprite.getScale());
	}

	public void testCanvas() {
		Sprite sprite = new Sprite("sprite1");
		Script script = new Script("script1", sprite);
		sprite.getScriptList().add(script);
		SetCostumeBrick setCostumeBrick = new SetCostumeBrick(sprite);
		script.addBrick(setCostumeBrick);
		ArrayList<Sprite> spriteList = new ArrayList<Sprite>();
		spriteList.add(sprite);
		Utils.createProject(projectName, spriteList, getActivity());
		File image = Utils.saveFileToProject(projectName, imageName1, R.raw.red_quad, getInstrumentation()
					.getContext(), 0);
		setImageMemberProperties(image);
		setCostumeBrick.setCostume(image.getName());

		solo.clickOnButton(1);
		solo.clickOnScreen(Values.SCREEN_WIDTH, 0); //save thumbnail

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
				//System.out.println("in TEST " + i + " " + j);
			}
		}

		for (int j = startHeight; j < borderHeight; j++) {
			assertEquals("pixel is not white", Color.WHITE, bitmap.getPixel(startWidth - 1, j));
			//System.out.println("in TEST2 " + (startWidth - 1) + " " + j);
		}

		for (int j = startHeight; j < borderHeight; j++) {
			assertEquals("pixel is not white", Color.WHITE, bitmap.getPixel(borderWidth, j));
			//System.out.println("in TEST3 " + borderWidth + " " + j);
		}

		for (int i = startWidth; i < borderWidth; i++) {
			assertEquals("pixel is not white", Color.WHITE, bitmap.getPixel(i, startHeight - 1));
			//System.out.println("in TEST4 " + i + " " + (startHeight - 1));
		}

		for (int i = startWidth; i < borderWidth; i++) {
			assertEquals("pixel is not white", Color.WHITE, bitmap.getPixel(i, borderHeight));
			//System.out.println("in TEST5 " + i + " " + borderHeight);
		}

	}

	public void clickOnScreenAndReturn(int x, int y, int expectedWidth, int expectedHeight) throws InterruptedException {
		solo.clickOnScreen(x, y);
		Thread.sleep(1000);
		Costume costume = ProjectManager.getInstance().getCurrentProject().getSpriteList().get(1).getCostume();
		assertEquals((Integer) expectedWidth, costume.getImageWidthHeight().first);
		assertEquals((Integer) expectedHeight, costume.getImageWidthHeight().second);

		solo.goBack();
		solo.clickOnButton(1);
	}

	public void createTestproject(String projectName) {

		//creating sprites for project:
		Sprite firstSprite = new Sprite("sprite1");
		Script testScript = new Script("script1", firstSprite);
		Script touchScript = new Script("script2", firstSprite);
		touchScript.setTouchScript(true);

		SetCostumeBrick setCostumeBrick = new SetCostumeBrick(firstSprite);
		SetCostumeBrick setCostumeBrick2 = new SetCostumeBrick(firstSprite);

		testScript.addBrick(setCostumeBrick);
		touchScript.addBrick(setCostumeBrick2);
		firstSprite.getScriptList().add(testScript);
		firstSprite.getScriptList().add(touchScript);

		ArrayList<Sprite> spriteList = new ArrayList<Sprite>();
		spriteList.add(firstSprite);
		Project project = Utils.createProject(projectName, spriteList, getActivity());

		image1 = Utils.saveFileToProject(projectName, imageName1, IMAGE_FILE_ID, getInstrumentation()
				.getContext(), 0);
		image2 = Utils.saveFileToProject(projectName, imageName2, IMAGE_FILE_ID2, getInstrumentation()
				.getContext(), 0);
		setImageMemberProperties(image1);
		setImageMemberProperties(image2);
		setCostumeBrick.setCostume(image1.getName());
		setCostumeBrick2.setCostume(image2.getName());

		storageHandler.saveProject(project);
	}

	public void createTestProject2(String projectName) {

		//creating sprites for project:
		Sprite firstSprite = new Sprite("sprite1");
		Script startScript = new Script("startscript", firstSprite);
		Script touchScript = new Script("script2", firstSprite);
		touchScript.setTouchScript(true);

		SetCostumeBrick setCostumeBrick = new SetCostumeBrick(firstSprite);
		SetCostumeBrick setCostumeBrick2 = new SetCostumeBrick(firstSprite);

		ScaleCostumeBrick scaleCostumeBrick = new ScaleCostumeBrick(firstSprite, 50);
		ScaleCostumeBrick scaleCostumeBrick2 = new ScaleCostumeBrick(firstSprite, 100);
		WaitBrick waitBrick = new WaitBrick(firstSprite, 2000);
		PlaceAtBrick placeAt = new PlaceAtBrick(firstSprite, this.placeAt, this.placeAt);

		startScript.addBrick(setCostumeBrick2);
		touchScript.addBrick(setCostumeBrick);
		touchScript.addBrick(scaleCostumeBrick);
		touchScript.addBrick(waitBrick);
		touchScript.addBrick(scaleCostumeBrick2);
		touchScript.addBrick(placeAt);
		firstSprite.getScriptList().add(startScript);
		firstSprite.getScriptList().add(touchScript);

		ArrayList<Sprite> spriteList = new ArrayList<Sprite>();
		spriteList.add(firstSprite);
		Project project = Utils.createProject(projectName, spriteList, getActivity());

		image1 = Utils.saveFileToProject(projectName, imageName1, IMAGE_FILE_ID, getInstrumentation()
				.getContext(), 0);
		image2 = Utils.saveFileToProject(projectName, imageName2, IMAGE_FILE_ID2, getInstrumentation()
				.getContext(), 0);
		setImageMemberProperties(image1);
		setImageMemberProperties(image2);
		setCostumeBrick.setCostume(image1.getName());
		setCostumeBrick2.setCostume(image2.getName());

		storageHandler.saveProject(project);
	}

	public void createTestProject3(String projectName) {

		//creating sprites for project:

		// sprite1 --------------------------------
		Sprite firstSprite = new Sprite("sprite1");
		Script startScript1 = new Script("start1", firstSprite);
		Script touchScript1 = new Script("script1", firstSprite);
		touchScript1.setTouchScript(true);
		// creating bricks:
		SetCostumeBrick setCostumeBrick = new SetCostumeBrick(firstSprite);

		// adding bricks:
		startScript1.addBrick(setCostumeBrick);
		startScript1.addBrick(new ComeToFrontBrick(firstSprite));
		touchScript1.addBrick(new GoNStepsBackBrick(firstSprite, 2));
		firstSprite.getScriptList().add(startScript1);
		firstSprite.getScriptList().add(touchScript1);

		// sprite2 --------------------------------
		Sprite secondSprite = new Sprite("sprite2");
		Script startScript2 = new Script("start2", secondSprite);
		Script touchScript2 = new Script("script2", secondSprite);
		touchScript2.setTouchScript(true);
		// creating bricks:
		SetCostumeBrick setCostumeBrick2 = new SetCostumeBrick(secondSprite);
		// adding bricks:
		startScript2.addBrick(setCostumeBrick2);
		touchScript2.addBrick(new ScaleCostumeBrick(secondSprite, 200));

		secondSprite.getScriptList().add(startScript2);
		secondSprite.getScriptList().add(touchScript2);

		ArrayList<Sprite> spriteList = new ArrayList<Sprite>();
		spriteList.add(firstSprite);
		spriteList.add(secondSprite);
		Project project = Utils.createProject(projectName, spriteList, getActivity());

		image1 = Utils.saveFileToProject(projectName, imageName1, IMAGE_FILE_ID, getInstrumentation()
				.getContext(), 0);
		image2 = Utils.saveFileToProject(projectName, imageName2, IMAGE_FILE_ID2, getInstrumentation()
				.getContext(), 0);
		setImageMemberProperties(image1);
		setImageMemberProperties(image2);
		setCostumeBrick.setCostume(image1.getName());
		setCostumeBrick2.setCostume(image2.getName());

		storageHandler.saveProject(project);
	}

	public void createTestProject4(String projectName) throws IOException {

		//creating sprites for project:
		Sprite firstSprite = new Sprite("sprite1");
		Script startScript = new Script("startscript", firstSprite);
		Script touchScript = new Script("touchscript", firstSprite);
		touchScript.setTouchScript(true);

		SetCostumeBrick setCostumeBrick = new SetCostumeBrick(firstSprite);

		ScaleCostumeBrick scaleCostumeBrick = new ScaleCostumeBrick(firstSprite, 50);
		HideBrick hideBrick = new HideBrick(firstSprite);

		startScript.addBrick(setCostumeBrick);
		startScript.addBrick(hideBrick);
		touchScript.addBrick(scaleCostumeBrick);
		firstSprite.getScriptList().add(startScript);
		firstSprite.getScriptList().add(touchScript);

		ArrayList<Sprite> spriteList = new ArrayList<Sprite>();
		spriteList.add(firstSprite);
		Project project = Utils.createProject(projectName, spriteList, getActivity());

		image1 = Utils.saveFileToProject(projectName, imageName1, IMAGE_FILE_ID, getInstrumentation()
				.getContext(), 0);
		setImageMemberProperties(image1);
		setCostumeBrick.setCostume(image1.getName());

		storageHandler.saveProject(project);
	}

	public void createTestProjectWithSound() throws IOException {

		//creating sprites for project:
		Sprite firstSprite = new Sprite("sprite1");
		Script startScript = new Script("startscript", firstSprite);
		Script touchScript = new Script("touchscript", firstSprite);
		touchScript.setTouchScript(true);

		SetCostumeBrick setCostumeBrick = new SetCostumeBrick(firstSprite);
		PlaySoundBrick playSoundBrick = new PlaySoundBrick(firstSprite);
		ScaleCostumeBrick scaleCostumeBrick = new ScaleCostumeBrick(firstSprite, 50);

		startScript.addBrick(setCostumeBrick);
		touchScript.addBrick(scaleCostumeBrick);
		touchScript.addBrick(playSoundBrick);

		firstSprite.getScriptList().add(startScript);
		firstSprite.getScriptList().add(touchScript);

		ArrayList<Sprite> spriteList = new ArrayList<Sprite>();
		spriteList.add(firstSprite);
		Project project = Utils.createProject(projectName, spriteList, getActivity());

		soundFile = Utils.saveFileToProject(projectName, "soundfile.mp3", SOUND_FILE_ID, getInstrumentation()
				.getContext(), 1);

		image1 = Utils.saveFileToProject(projectName, imageName1, IMAGE_FILE_ID, getInstrumentation()
				.getContext(), 0);
		setImageMemberProperties(image1);
		setCostumeBrick.setCostume(image1.getName());
		playSoundBrick.setPathToSoundfile(soundFile.getName());

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

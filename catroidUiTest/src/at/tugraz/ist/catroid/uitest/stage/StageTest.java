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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.graphics.BitmapFactory;
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
import at.tugraz.ist.catroid.utils.UtilFile;

import com.jayway.android.robotium.solo.Solo;

public class StageTest extends ActivityInstrumentationTestCase2<MainMenuActivity> {
	private Solo solo;
	final String projectName = "project1";
	final String dummyName = "dummy";

	File image1;
	File image2;
	String imageName1 = "image1";
	String imageName2 = "image2";
	int placeAt = 400;

	int image1Width;
	int image2Width;
	int image1Height;
	int image2Height;

	private static final int IMAGE_FILE_ID = R.raw.icon;
	private static final int IMAGE_FILE_ID2 = R.raw.icon2;

	private static final int SOUND_FILE_ID = R.raw.testsound;
	private File soundFile;

	public StageTest() {
		super("at.tugraz.ist.catroid", MainMenuActivity.class);
	}

	@Override
	public void setUp() throws Exception {

		File directory = new File("/sdcard/catroid/" + projectName);
		UtilFile.deleteDirectory(directory);

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

		File directory = new File("/sdcard/catroid/" + projectName);
		UtilFile.deleteDirectory(directory);

		if (soundFile != null) {
			soundFile.delete();
		}

		getActivity().finish();
		super.tearDown();
	}

	public void testClickOnPictureAndChangeCostume() throws IOException, InterruptedException {
		createTestProject1(projectName);

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
		assertEquals("some image is set", (Integer) this.image2Width, costume.getImageWidthHeight().first);
		assertEquals("some image is set", (Integer) this.image2Height, costume.getImageWidthHeight().second);

		solo.clickOnScreen(Values.SCREEN_WIDTH / 2, Values.SCREEN_HEIGHT / 2); // click in se middle

		Thread.sleep(1000);
		costume = ProjectManager.getInstance().getCurrentProject().getSpriteList().get(1).getCostume();
		assertEquals("image not right scaled", (Integer) (image1Width / 2), costume.getImageWidthHeight().first);

		Thread.sleep(3000);
		costume = ProjectManager.getInstance().getCurrentProject().getSpriteList().get(1).getCostume();
		assertEquals("image not right scaled", (Integer) (image1Width), costume.getImageWidthHeight().first);

		int drawPositionX = Math.round(((Values.SCREEN_WIDTH / (2f * Consts.MAX_REL_COORDINATES)) * placeAt)
				+ Values.SCREEN_WIDTH / 2f);
		drawPositionX = drawPositionX - image1Width / 2;
		assertEquals("image was not set right", drawPositionX, costume.getDrawPositionX());
	}

	public void testClickAroundPicture() throws IOException, InterruptedException {
		createTestProject1(projectName);
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
		createTestProject1(projectName);
		solo.clickOnButton(1);
		Thread.sleep(2000);
		Costume costume = ProjectManager.getInstance().getCurrentProject().getSpriteList().get(1).getCostume();
		int clickWidth = (Values.SCREEN_WIDTH - costume.getBitmap().getWidth()) / 2 + 4;
		int clickHeight = Values.SCREEN_HEIGHT / 2;
		System.out.println("click: " + clickWidth + " " + clickHeight);
		clickOnScreenAndReturn(clickWidth, clickHeight, image2Width, image2Height);

		clickWidth = (Values.SCREEN_WIDTH / 2);
		clickHeight = (Values.SCREEN_HEIGHT - costume.getBitmap().getHeight()) / 2 + 4;
		System.out.println("click: " + clickWidth + " " + clickHeight);
		clickOnScreenAndReturn(clickWidth, clickHeight, image2Width, image2Height);

		clickWidth = (Values.SCREEN_WIDTH / 2);
		clickHeight = ((Values.SCREEN_HEIGHT - costume.getBitmap().getHeight()) / 2) + costume.getBitmap().getHeight()
				- 4;
		System.out.println("click: " + clickWidth + " " + clickHeight);
		clickOnScreenAndReturn(clickWidth, clickHeight, image2Width, image2Height);

		clickWidth = ((Values.SCREEN_WIDTH - costume.getBitmap().getWidth()) / 2) + costume.getBitmap().getWidth() - 4;
		clickHeight = (Values.SCREEN_HEIGHT / 2);
		System.out.println("click: " + clickWidth + " " + clickHeight);
		clickOnScreenAndReturn(clickWidth, clickHeight, image2Width, image2Height);

	}

	public void testSpfChangesInStage() throws IOException {
		// it is not allowed for the .spf file to change when in stage
		// add another test when you add new stage buttons
		createTestProject1(projectName);
		File mySpfFile = new File("/mnt/sdcard/catroid/" + projectName + "/" + projectName + Consts.PROJECT_EXTENTION);

		solo.clickOnButton(1);
		solo.clickOnScreen(Values.SCREEN_WIDTH / 2, Values.SCREEN_HEIGHT / 2);
		solo.goBack();
		solo.clickOnButton(0);
		solo.clickInList(1);

		File mySpfFile2 = new File("/mnt/sdcard/catroid/" + projectName + "/" + projectName + Consts.PROJECT_EXTENTION);

		assertEquals("spf File changed!", mySpfFile.hashCode(), mySpfFile2.hashCode());
	}

	public void testPlayPauseHomeButton() throws IOException, InterruptedException {
		StorageHandler storageHandler = StorageHandler.getInstance();
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

		this.setUpSoundFile();

		this.createTestProjectWithSound();
		solo.clickOnButton(1);
		solo.clickOnScreen(Values.SCREEN_WIDTH / 2, Values.SCREEN_HEIGHT / 2);
		Thread.sleep(150);
		assertTrue("Media player is not playing", mediaPlayer.isPlaying());
	}

	public void testMediaPlayerPause() throws IOException, InterruptedException {
		MediaPlayer mediaPlayer = SoundManager.getInstance().getMediaPlayer();

		this.setUpSoundFile();

		this.createTestProjectWithSound();
		solo.clickOnButton(1);
		solo.clickOnScreen(Values.SCREEN_WIDTH / 2, Values.SCREEN_HEIGHT / 2);
		solo.pressMenuItem(1);
		Thread.sleep(1000);
		solo.pressMenuItem(1);
		Thread.sleep(50);
		assertTrue("Media player is not playing after pause", mediaPlayer.isPlaying());
	}

	public void testMediaPlayerNotPlayerAfterPause() throws IOException, InterruptedException {
		MediaPlayer mediaPlayer = SoundManager.getInstance().getMediaPlayer();

		this.setUpSoundFile();

		this.createTestProjectWithSound();
		solo.clickOnButton(1);
		solo.pressMenuItem(1);
		Thread.sleep(1000);
		solo.pressMenuItem(1);
		assertFalse("Media Player is playing", mediaPlayer.isPlaying());
	}

	private void setUpSoundFile() throws IOException {
		// Note: File needs to be copied as MediaPlayer has no access to resources
		BufferedInputStream inputStream = new BufferedInputStream(getInstrumentation().getContext().getResources()
				.openRawResource(SOUND_FILE_ID));
		soundFile = File.createTempFile("audioTest", ".mp3");
		BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(soundFile), 1024);

		byte[] buffer = new byte[1024];
		int length = 0;
		while ((length = inputStream.read(buffer)) > 0) {
			outputStream.write(buffer, 0, length);
		}
		inputStream.close();
		outputStream.flush();
		outputStream.close();
	}

	//	public void testCanvas() throws IOException, InterruptedException{
	//
	//		final String checksum = "79ee0e009eddc798007708b64d2b22d5a09319ec";
	//
	//		createTestProject1(projectName);
	//		solo.clickOnButton(1);
	//
	//		Bitmap bitmap = Bitmap.createBitmap(500, 500, Bitmap.Config.ARGB_8888);
	//		Canvas singleUseCanvas = new Canvas(bitmap);
	//
	//		singleUseCanvas.setBitmap(bitmap);
	//
	//		ArrayList<Sprite> sprites = (ArrayList<Sprite>) ProjectManager.getInstance().getCurrentProject().getSpriteList();
	//		java.util.Collections.sort(sprites);
	//		Thread.sleep(3000);
	//		for (Sprite sprite : sprites) {
	//			if(!sprite.isVisible()){
	//				continue;
	//			}
	//			if (sprite.getCostume().getBitmap() != null) {
	//				Costume tempCostume = sprite.getCostume();
	//				singleUseCanvas.drawBitmap(tempCostume.getBitmap(), tempCostume.getDrawPositionX(), tempCostume.getDrawPositionY(), null);
	//			}
	//		}
	//
	//		final String imagePath = "/sdcard/catroid/" + this.projectName + "/images/temp.png";
	//		File testImage = new File(imagePath);
	//
	//		try {
	//			FileOutputStream out = new FileOutputStream(testImage);
	//			bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
	//		} catch (Exception e) {
	//			e.printStackTrace();
	//		}
	//
	//		String checksumNew = StorageHandler.getInstance().getMD5Checksum(testImage);
	//
	//		assertEquals("The checksum of the 'screenshot' is wrong", checksum, checksumNew);
	//	}
	//
	//	public void testCanvas2() throws IOException, InterruptedException{
	//		final String checksum = "d15e1df97307ca568aa3129df430f71f1f6f31d0";
	//
	//		createTestProject3(projectName);
	//		solo.clickOnButton(1);
	//
	//		Thread.sleep(1000);
	//		solo.clickOnScreen(Values.SCREEN_WIDTH / 2, Values.SCREEN_HEIGHT / 2);
	//
	//		Bitmap bitmap = Bitmap.createBitmap(500, 500, Bitmap.Config.ARGB_8888);
	//		Canvas singleUseCanvas = new Canvas(bitmap);
	//
	//		singleUseCanvas.setBitmap(bitmap);
	//
	//		ArrayList<Sprite> sprites = (ArrayList<Sprite>) ProjectManager.getInstance().getCurrentProject().getSpriteList();
	//		java.util.Collections.sort(sprites);
	//		Thread.sleep(3000);
	//		for (Sprite sprite : sprites) {
	//			if(!sprite.isVisible()){
	//				continue;
	//			}
	//			if (sprite.getCostume().getBitmap() != null) {
	//				Costume tempCostume = sprite.getCostume();
	//				singleUseCanvas.drawBitmap(tempCostume.getBitmap(), tempCostume.getDrawPositionX(), tempCostume.getDrawPositionY(), null);
	//			}
	//		}
	//
	//		final String imagePath = "/sdcard/catroid/" + this.projectName + "/images/temp.png";
	//		File testImage = new File(imagePath);
	//
	//		try {
	//			FileOutputStream out = new FileOutputStream(testImage);
	//			bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
	//		} catch (Exception e) {
	//			e.printStackTrace();
	//		}
	//
	//		String checksumNew = StorageHandler.getInstance().getMD5Checksum(testImage);
	//
	//		assertEquals("The checksum of the 'screenshot' is wrong", checksum, checksumNew);
	//	}

	public void testClickOnHiddenSprite() throws IOException, InterruptedException {
		createTestProject4(projectName);
		solo.clickOnButton(1);
		Thread.sleep(500);
		solo.clickOnScreen(Values.SCREEN_WIDTH / 2, Values.SCREEN_HEIGHT / 2);
		Thread.sleep(1000);

		Sprite sprite = ProjectManager.getInstance().getCurrentProject().getSpriteList().get(1);
		assertEquals(100.0, sprite.getScale());
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

	public void createTestProject1(String projectName) throws IOException {
		StorageHandler storageHandler = StorageHandler.getInstance();

		Project project = new Project(getActivity(), projectName);
		storageHandler.saveProject(project);

		image1 = savePictureInProject(projectName, 4147, imageName1, IMAGE_FILE_ID);
		image2 = savePictureInProject(projectName, 4147, imageName2, IMAGE_FILE_ID2);

		Sprite firstSprite = new Sprite("sprite1");
		Script testScript = new Script("script1", firstSprite);
		Script touchScript = new Script("script2", firstSprite);
		touchScript.setTouchScript(true);

		SetCostumeBrick setCostumeBrick = new SetCostumeBrick(firstSprite);
		setCostumeBrick.setCostume(image1.getAbsolutePath());
		SetCostumeBrick setCostumeBrick2 = new SetCostumeBrick(firstSprite);
		setCostumeBrick2.setCostume(image2.getAbsolutePath());

		project.addSprite(firstSprite);
		testScript.addBrick(setCostumeBrick);
		touchScript.addBrick(setCostumeBrick2);
		firstSprite.getScriptList().add(testScript);
		firstSprite.getScriptList().add(touchScript);

		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(firstSprite);
		ProjectManager.getInstance().setCurrentScript(testScript);

		storageHandler.saveProject(project);
	}

	public void createTestProject2(String projectName) throws IOException {
		StorageHandler storageHandler = StorageHandler.getInstance();

		Project project = new Project(getActivity(), projectName);
		storageHandler.saveProject(project);

		image1 = savePictureInProject(projectName, 4147, imageName1, IMAGE_FILE_ID);
		image2 = savePictureInProject(projectName, 4147, imageName2, IMAGE_FILE_ID2);

		Sprite firstSprite = new Sprite("sprite1");
		Script startScript = new Script("startscript", firstSprite);
		Script touchScript = new Script("script2", firstSprite);
		touchScript.setTouchScript(true);

		SetCostumeBrick setCostumeBrick = new SetCostumeBrick(firstSprite);
		setCostumeBrick.setCostume(image1.getAbsolutePath());
		SetCostumeBrick setCostumeBrick2 = new SetCostumeBrick(firstSprite);
		setCostumeBrick2.setCostume(image2.getAbsolutePath());
		ScaleCostumeBrick scaleCostumeBrick = new ScaleCostumeBrick(firstSprite, 50);
		ScaleCostumeBrick scaleCostumeBrick2 = new ScaleCostumeBrick(firstSprite, 100);
		WaitBrick waitBrick = new WaitBrick(firstSprite, 3000);
		PlaceAtBrick placeAt = new PlaceAtBrick(firstSprite, this.placeAt, this.placeAt);

		project.addSprite(firstSprite);
		startScript.addBrick(setCostumeBrick2);
		touchScript.addBrick(setCostumeBrick);
		touchScript.addBrick(scaleCostumeBrick);
		touchScript.addBrick(waitBrick);
		touchScript.addBrick(scaleCostumeBrick2);
		touchScript.addBrick(placeAt);
		firstSprite.getScriptList().add(startScript);
		firstSprite.getScriptList().add(touchScript);

		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(firstSprite);

		storageHandler.saveProject(project);
	}

	public void createTestProject3(String projectName) throws IOException {
		StorageHandler storageHandler = StorageHandler.getInstance();

		Project project = new Project(getActivity(), projectName);
		storageHandler.saveProject(project);

		image1 = savePictureInProject(projectName, 4147, imageName1, IMAGE_FILE_ID);
		image2 = savePictureInProject(projectName, 4147, imageName2, IMAGE_FILE_ID2);

		// sprite1 --------------------------------
		Sprite firstSprite = new Sprite("sprite1");
		Script startScript1 = new Script("start1", firstSprite);
		Script touchScript1 = new Script("script1", firstSprite);
		touchScript1.setTouchScript(true);
		// creating bricks:
		SetCostumeBrick setCostumeBrick = new SetCostumeBrick(firstSprite);
		setCostumeBrick.setCostume(image1.getAbsolutePath());
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
		setCostumeBrick2.setCostume(image2.getAbsolutePath());
		// adding bricks:
		startScript2.addBrick(setCostumeBrick2);
		touchScript2.addBrick(new ScaleCostumeBrick(secondSprite, 200));

		secondSprite.getScriptList().add(startScript2);
		secondSprite.getScriptList().add(touchScript2);

		// ---------------------------------------
		project.addSprite(firstSprite);
		project.addSprite(secondSprite);

		ProjectManager.getInstance().setProject(project);

		storageHandler.saveProject(project);
	}

	public void createTestProject4(String projectName) throws IOException {
		StorageHandler storageHandler = StorageHandler.getInstance();

		Project project = new Project(getActivity(), projectName);
		storageHandler.saveProject(project);

		image1 = savePictureInProject(projectName, 4147, imageName1, IMAGE_FILE_ID);

		Sprite firstSprite = new Sprite("sprite1");
		Script startScript = new Script("startscript", firstSprite);
		Script touchScript = new Script("touchscript", firstSprite);
		touchScript.setTouchScript(true);

		SetCostumeBrick setCostumeBrick = new SetCostumeBrick(firstSprite);
		setCostumeBrick.setCostume(image1.getAbsolutePath());

		ScaleCostumeBrick scaleCostumeBrick = new ScaleCostumeBrick(firstSprite, 50);
		HideBrick hideBrick = new HideBrick(firstSprite);

		project.addSprite(firstSprite);
		startScript.addBrick(setCostumeBrick);
		startScript.addBrick(hideBrick);
		touchScript.addBrick(scaleCostumeBrick);
		firstSprite.getScriptList().add(startScript);
		firstSprite.getScriptList().add(touchScript);

		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(firstSprite);

		storageHandler.saveProject(project);
	}

	public void createTestProjectWithSound() throws IOException {
		StorageHandler storageHandler = StorageHandler.getInstance();

		Project project = new Project(getActivity(), projectName);
		storageHandler.saveProject(project);

		image1 = savePictureInProject(projectName, 4147, imageName1, IMAGE_FILE_ID);

		Sprite firstSprite = new Sprite("sprite1");
		Script startScript = new Script("startscript", firstSprite);
		Script touchScript = new Script("touchscript", firstSprite);
		touchScript.setTouchScript(true);

		SetCostumeBrick setCostumeBrick = new SetCostumeBrick(firstSprite);
		setCostumeBrick.setCostume(image1.getAbsolutePath());

		//		SoundInfo soundInfo = new SoundInfo();
		//		soundInfo.setId(5);
		//		soundInfo.setTitle("whatever");
		//		soundInfo.setPath(this.soundFile.getAbsolutePath());

		PlaySoundBrick playSoundBrick = new PlaySoundBrick(firstSprite);
		playSoundBrick.setPathToSoundfile(soundFile.getAbsolutePath());

		ScaleCostumeBrick scaleCostumeBrick = new ScaleCostumeBrick(firstSprite, 50);

		project.addSprite(firstSprite);
		startScript.addBrick(setCostumeBrick);
		touchScript.addBrick(scaleCostumeBrick);
		touchScript.addBrick(playSoundBrick);

		firstSprite.getScriptList().add(startScript);
		firstSprite.getScriptList().add(touchScript);

		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(firstSprite);

		storageHandler.saveProject(project);
	}

	public File savePictureInProject(String project, int fileSize, String name, int fileID) throws IOException {

		// final int fileSize = 4147;
		final String imagePath = "/sdcard/catroid/" + project + "/images/" + name;
		File testImage = new File(imagePath);
		if (!testImage.exists()) {
			testImage.createNewFile();
		}
		InputStream in = getInstrumentation().getContext().getResources().openRawResource(fileID);
		OutputStream out = new BufferedOutputStream(new FileOutputStream(testImage), fileSize);
		byte[] buffer = new byte[fileSize];
		int length = 0;
		while ((length = in.read(buffer)) > 0) {
			out.write(buffer, 0, length);
		}

		in.close();
		out.flush();
		out.close();

		BitmapFactory.Options o = new BitmapFactory.Options();
		o.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(imagePath, o);

		if (name.equalsIgnoreCase(imageName1)) {
			image1Width = o.outWidth;
			image1Height = o.outHeight;
		} else {
			image2Width = o.outWidth;
			image2Height = o.outHeight;
		}

		return testImage;
	}

}

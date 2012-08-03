package at.tugraz.ist.catroid.test.xml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import android.test.AndroidTestCase;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.common.Constants;
import at.tugraz.ist.catroid.common.CostumeData;
import at.tugraz.ist.catroid.common.SoundInfo;
import at.tugraz.ist.catroid.content.Project;
import at.tugraz.ist.catroid.content.Script;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.content.StartScript;
import at.tugraz.ist.catroid.content.bricks.ComeToFrontBrick;
import at.tugraz.ist.catroid.content.bricks.HideBrick;
import at.tugraz.ist.catroid.content.bricks.LoopEndBrick;
import at.tugraz.ist.catroid.content.bricks.PlaceAtBrick;
import at.tugraz.ist.catroid.content.bricks.PlaySoundBrick;
import at.tugraz.ist.catroid.content.bricks.PointToBrick;
import at.tugraz.ist.catroid.content.bricks.RepeatBrick;
import at.tugraz.ist.catroid.content.bricks.SetCostumeBrick;
import at.tugraz.ist.catroid.content.bricks.SetSizeToBrick;
import at.tugraz.ist.catroid.content.bricks.ShowBrick;
import at.tugraz.ist.catroid.test.utils.TestUtils;
import at.tugraz.ist.catroid.utils.UtilFile;
import at.tugraz.ist.catroid.utils.Utils;
import at.tugraz.ist.catroid.xml.FullParser;
import at.tugraz.ist.catroid.xml.ParseException;
import at.tugraz.ist.catroid.xml.serializer.SerializeException;
import at.tugraz.ist.catroid.xml.serializer.XmlSerializer;

public class SerializerTest extends AndroidTestCase {

	//	@Override
	//	public void tearDown() {
	//		TestUtils.clearProject(getContext().getString(R.string.default_project_name));
	//		TestUtils.clearProject("testSerializeProject");
	//	}
	//
	@Override
	public void setUp() {
		File projectFile = new File(Constants.DEFAULT_ROOT + "/"
				+ getContext().getString(R.string.default_project_name));

		if (projectFile.exists()) {
			UtilFile.deleteDirectory(projectFile);
		}

	}

	public void testSerializingToXml() {
		XmlSerializer serializer = new XmlSerializer();
		int xPosition = 457;
		int yPosition = 598;
		double size = 0.8;

		Project project = new Project(getContext(), "testSerializeProject");
		Sprite firstSprite = new Sprite("first");
		Sprite secondSprite = new Sprite("second");
		Sprite thirdSprite = new Sprite("third");
		Sprite fourthSprite = new Sprite("fourth");
		Script testScript = new StartScript(firstSprite);
		Script otherScript = new StartScript(secondSprite);
		HideBrick hideBrick = new HideBrick(firstSprite);
		ShowBrick showBrick = new ShowBrick(firstSprite);
		SetSizeToBrick setSizeToBrick = new SetSizeToBrick(secondSprite, size);
		ComeToFrontBrick comeToFrontBrick = new ComeToFrontBrick(firstSprite);
		PlaceAtBrick placeAtBrick = new PlaceAtBrick(secondSprite, xPosition, yPosition);

		// adding Bricks: ----------------
		testScript.addBrick(hideBrick);
		testScript.addBrick(showBrick);
		testScript.addBrick(setSizeToBrick);
		testScript.addBrick(comeToFrontBrick);

		otherScript.addBrick(placeAtBrick); // secondSprite
		otherScript.setPaused(true);
		// -------------------------------

		firstSprite.addScript(testScript);
		secondSprite.addScript(otherScript);

		project.addSprite(firstSprite);
		project.addSprite(secondSprite);
		project.addSprite(thirdSprite);
		project.addSprite(fourthSprite);

		String projectDirectoryName = Utils.buildProjectPath("test__" + project.getName());
		File projectDirectory = new File(projectDirectoryName);
		try {

			if (!(projectDirectory.exists() && projectDirectory.isDirectory() && projectDirectory.canWrite())) {
				projectDirectory.mkdir();

			}
			serializer.toXml(project, Utils.buildPath(projectDirectoryName, Constants.PROJECTCODE_NAME));
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (SerializeException e) {
			e.printStackTrace();
		}

		Project loadedProject = null;
		if (projectDirectory.exists() && projectDirectory.isDirectory() && projectDirectory.canWrite()) {

			try {
				InputStream projectFileStream = new FileInputStream(Utils.buildPath(projectDirectory.getAbsolutePath(),
						Constants.PROJECTCODE_NAME));
				FullParser parser = new FullParser();
				loadedProject = parser.parseSpritesWithProject(projectFileStream);
			} catch (ParseException e) {

				e.printStackTrace();
			} catch (FileNotFoundException e) {

				e.printStackTrace();
			}

		}

		ArrayList<Sprite> preSpriteList = (ArrayList<Sprite>) project.getSpriteList();
		ArrayList<Sprite> postSpriteList = (ArrayList<Sprite>) loadedProject.getSpriteList();

		// Test sprite names:
		assertEquals("First sprite does not match after deserialization", preSpriteList.get(0).getName(),
				postSpriteList.get(0).getName());
		assertEquals("Second sprite does not match after deserialization", preSpriteList.get(1).getName(),
				postSpriteList.get(1).getName());
		assertEquals("Third sprite does not match after deserialization", preSpriteList.get(2).getName(),
				postSpriteList.get(2).getName());
		assertEquals("Fourth sprite does not match after deserialization", preSpriteList.get(3).getName(),
				postSpriteList.get(3).getName());
		assertEquals("Fifth sprite does not match after deserialization", preSpriteList.get(4).getName(),
				postSpriteList.get(4).getName());

		// Test project name:
		assertEquals("Title missmatch after deserialization", project.getName(), loadedProject.getName());

		// Test random brick values
		int actualXPosition = (Integer) TestUtils.getPrivateField("xPosition", (postSpriteList.get(2).getScript(0)
				.getBrickList().get(0)), false);
		int actualYPosition = (Integer) TestUtils.getPrivateField("yPosition", (postSpriteList.get(2).getScript(0)
				.getBrickList().get(0)), false);

		double actualSize = (Double) TestUtils.getPrivateField("size", (postSpriteList.get(1).getScript(0)
				.getBrickList().get(2)), false);

		assertEquals("Size was not deserialized right", size, actualSize);
		assertEquals("XPosition was not deserialized right", xPosition, actualXPosition);
		assertEquals("YPosition was not deserialized right", yPosition, actualYPosition);

		assertFalse("paused should not be set in script", preSpriteList.get(1).getScript(0).isPaused());

		// Test version codes and names
		final int preVersionCode = (Integer) TestUtils.getPrivateField("catroidVersionCode", project, false);
		final int postVersionCode = (Integer) TestUtils.getPrivateField("catroidVersionCode", loadedProject, false);
		assertEquals("Version codes are not equal", preVersionCode, postVersionCode);

		final String preVersionName = (String) TestUtils.getPrivateField("catroidVersionName", project, false);
		final String postVersionName = (String) TestUtils.getPrivateField("catroidVersionName", loadedProject, false);
		assertEquals("Version names are not equal", preVersionName, postVersionName);

	}

	public void testReferenceSerializing() {
		Sprite testSprite = new Sprite("test");
		Sprite pointedSprite = new Sprite("pointed");

		CostumeData referenceCostume = new CostumeData();
		referenceCostume.setCostumeFilename("testfileName");
		referenceCostume.setCostumeName("testName");

		SoundInfo referencedSound = new SoundInfo();
		referencedSound.setSoundFileName("soundFile");
		referencedSound.setTitle("SongTitle");

		RepeatBrick repeatBrick = new RepeatBrick(testSprite, 4);
		LoopEndBrick loopEndBrick = new LoopEndBrick(testSprite, repeatBrick);
		repeatBrick.setLoopEndBrick(loopEndBrick);

		SetCostumeBrick costumeBrick = new SetCostumeBrick(testSprite);
		costumeBrick.setCostume(referenceCostume);

		PlaySoundBrick soundBrick = new PlaySoundBrick(testSprite);
		soundBrick.setSoundInfo(referencedSound);

		PointToBrick pointBrick = new PointToBrick(testSprite, pointedSprite);

		Script testScript = new StartScript(testSprite);
		Script otherScript = new StartScript(pointedSprite);
		HideBrick hideBrick = new HideBrick(pointedSprite);
		ShowBrick showBrick = new ShowBrick(pointedSprite);
		testScript.addBrick(repeatBrick);
		testScript.addBrick(costumeBrick);
		testScript.addBrick(soundBrick);
		testScript.addBrick(loopEndBrick);
		testScript.addBrick(pointBrick);
		testSprite.addScript(testScript);
		try {
			Field costumeField = Sprite.class.getDeclaredField("costumeDataList");
			Field soundField = Sprite.class.getDeclaredField("soundList");
			List<CostumeData> costumeList = new ArrayList<CostumeData>();
			List<SoundInfo> soundList = new ArrayList<SoundInfo>();
			costumeList.add(referenceCostume);
			costumeField.setAccessible(true);
			costumeField.set(testSprite, costumeList);
			soundList.add(referencedSound);
			soundField.setAccessible(true);
			soundField.set(testSprite, soundList);
		} catch (SecurityException e1) {

			e1.printStackTrace();
		} catch (NoSuchFieldException e1) {

			e1.printStackTrace();
		} catch (IllegalArgumentException e) {

			e.printStackTrace();
		} catch (IllegalAccessException e) {

			e.printStackTrace();
		}
		otherScript.addBrick(hideBrick);
		otherScript.addBrick(showBrick);
		pointedSprite.addScript(otherScript);

		Project testProject = new Project(getContext(), "referenceProject");
		testProject.addSprite(testSprite);
		testProject.addSprite(pointedSprite);

		XmlSerializer serializer = new XmlSerializer();

		String projectDirectoryName = Utils.buildProjectPath("test__" + testProject.getName());
		File projectDirectory = new File(projectDirectoryName);

		if (!(projectDirectory.exists() && projectDirectory.isDirectory() && projectDirectory.canWrite())) {
			projectDirectory.mkdir();

		}
		try {
			serializer.toXml(testProject, Utils.buildPath(projectDirectoryName, Constants.PROJECTCODE_NAME));
		} catch (SerializeException e) {
			fail("unexpected SerilizeException");
			e.printStackTrace();
		}

		Project loadedProject = null;
		if (projectDirectory.exists() && projectDirectory.isDirectory() && projectDirectory.canWrite()) {

			try {
				InputStream projectFileStream = new FileInputStream(Utils.buildPath(projectDirectory.getAbsolutePath(),
						Constants.PROJECTCODE_NAME));
				FullParser parser = new FullParser();
				loadedProject = parser.parseSpritesWithProject(projectFileStream);
			} catch (ParseException e) {

				e.printStackTrace();
			} catch (FileNotFoundException e) {

				e.printStackTrace();
			}

		}
		assertNotNull("loaded project is null", loadedProject);
		Sprite loadedFirstSprite = loadedProject.getSpriteList().get(1);
		RepeatBrick loadedRepeatBrick = (RepeatBrick) loadedFirstSprite.getScript(0).getBrick(0);
		LoopEndBrick referenceLoopEndBrick = loadedRepeatBrick.getLoopEndBrick();
		LoopEndBrick loadedLoopEndBrick = (LoopEndBrick) loadedFirstSprite.getScript(0).getBrick(3);
		assertEquals("LoopEndBrick not referenced right", loadedLoopEndBrick, referenceLoopEndBrick);

		CostumeData loadedCostume = loadedFirstSprite.getCostumeDataList().get(0);
		assertNotNull("Costume not in sprite costumeList", loadedCostume);
		SetCostumeBrick loadedCostumeBrick = (SetCostumeBrick) loadedFirstSprite.getScript(0).getBrick(1);
		CostumeData brickReferencedCostumeData = (CostumeData) TestUtils.getPrivateField("costumeData",
				loadedCostumeBrick, false);
		assertEquals("Costume data referencing wrong", loadedCostume, brickReferencedCostumeData);

		SoundInfo loadedSound = loadedFirstSprite.getSoundList().get(0);
		PlaySoundBrick loadedPlaySoundBrick = (PlaySoundBrick) loadedFirstSprite.getScript(0).getBrick(2);
		SoundInfo brickReferenceSoundInfo = (SoundInfo) TestUtils.getPrivateField("soundInfo", loadedPlaySoundBrick,
				false);
		assertEquals("Sound Info referencing wrong", loadedSound, brickReferenceSoundInfo);

		PointToBrick loadedPointBrick = (PointToBrick) loadedFirstSprite.getScript(0).getBrick(4);
		Sprite referencedSprite = (Sprite) TestUtils.getPrivateField("pointedSprite", loadedPointBrick, false);
		assertEquals("SpriteReferencing wrong", loadedProject.getSpriteList().get(2), referencedSprite);

	}
}

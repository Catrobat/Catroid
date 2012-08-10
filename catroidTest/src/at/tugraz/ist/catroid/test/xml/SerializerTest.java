package at.tugraz.ist.catroid.test.xml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.test.InstrumentationTestCase;
import android.util.Log;
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
import at.tugraz.ist.catroid.content.bricks.WhenStartedBrick;
import at.tugraz.ist.catroid.stage.NativeAppActivity;
import at.tugraz.ist.catroid.test.utils.TestUtils;
import at.tugraz.ist.catroid.utils.UtilFile;
import at.tugraz.ist.catroid.utils.Utils;
import at.tugraz.ist.catroid.xml.FullParser;
import at.tugraz.ist.catroid.xml.ParseException;
import at.tugraz.ist.catroid.xml.serializer.SerializeException;
import at.tugraz.ist.catroid.xml.serializer.XmlSerializer;

public class SerializerTest extends InstrumentationTestCase {
	Context androidContext;
	private static final String XMLSCHEMA_URL = "http://catroidtestserver.ist.tugraz.at/xmlSchema/catrobatXmlSchemaNew.xsd";

	//	@Override
	//	public void tearDown() {
	//		TestUtils.clearProject(getContext().getString(R.string.default_project_name));
	//		TestUtils.clearProject("testSerializeProject");
	//	}
	//
	@Override
	protected void tearDown() throws Exception {
		androidContext = null;
		NativeAppActivity.setContext(androidContext);
	}

	@Override
	public void setUp() {
		//		File projectFile = new File(Constants.DEFAULT_ROOT + "/"
		//				+ getInstrumentation().getContext().getString(R.string.default_project_name));
		//
		//		if (projectFile.exists()) {
		//			UtilFile.deleteDirectory(projectFile);
		//		}

		androidContext = getInstrumentation().getContext();
		NativeAppActivity.setContext(androidContext);
	}

	public void testSerializingToXml() {
		XmlSerializer serializer = new XmlSerializer();
		int xPosition = 457;
		int yPosition = 598;
		double size = 0.8;

		Project project = new Project();
		project.setName("testSerializeProject");
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
		//		assertEquals("Fifth sprite does not match after deserialization", preSpriteList.get(4).getName(),
		//				postSpriteList.get(4).getName());

		// Test project name:
		assertEquals("Title missmatch after deserialization", project.getName(), loadedProject.getName());

		// Test random brick values
		int actualXPosition = (Integer) TestUtils.getPrivateField("xPosition", (postSpriteList.get(1).getScript(0)
				.getBrickList().get(0)), false);
		int actualYPosition = (Integer) TestUtils.getPrivateField("yPosition", (postSpriteList.get(1).getScript(0)
				.getBrickList().get(0)), false);

		double actualSize = (Double) TestUtils.getPrivateField("size", (postSpriteList.get(0).getScript(0)
				.getBrickList().get(2)), false);

		assertEquals("Size was not deserialized right", size, actualSize);
		assertEquals("XPosition was not deserialized right", xPosition, actualXPosition);
		assertEquals("YPosition was not deserialized right", yPosition, actualYPosition);

		assertFalse("paused should not be set in script", preSpriteList.get(0).getScript(0).isPaused());

		//		// Test version codes and names
		//		final int preVersionCode = (Integer) TestUtils.getPrivateField("catroidVersionCode", project, false);
		//		final int postVersionCode = (Integer) TestUtils.getPrivateField("catroidVersionCode", loadedProject, false);
		//		assertEquals("Version codes are not equal", preVersionCode, postVersionCode);
		//
		//		final String preVersionName = (String) TestUtils.getPrivateField("catroidVersionName", project, false);
		//		final String postVersionName = (String) TestUtils.getPrivateField("catroidVersionName", loadedProject, false);
		//		assertEquals("Version names are not equal", preVersionName, postVersionName);
		UtilFile.deleteDirectory(projectDirectory);
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
		Script testScriptReferenced = new StartScript(testSprite);

		WhenStartedBrick whenStartedBrick = new WhenStartedBrick(testSprite, testScript);

		HideBrick hideBrick = new HideBrick(pointedSprite);
		ShowBrick showBrick = new ShowBrick(pointedSprite);
		testScript.addBrick(repeatBrick);
		testScript.addBrick(costumeBrick);
		testScript.addBrick(soundBrick);
		testScript.addBrick(loopEndBrick);
		testScript.addBrick(pointBrick);
		testScriptReferenced.addBrick(whenStartedBrick);
		testSprite.addScript(testScript);
		testSprite.addScript(testScriptReferenced);
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

		Project testProject = new Project();
		testProject.setName("testReferenceSerializerProject");
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
		Sprite loadedFirstSprite = loadedProject.getSpriteList().get(0);
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
		assertEquals("SpriteReferencing wrong", loadedProject.getSpriteList().get(1), referencedSprite);

		WhenStartedBrick loadedScriptBrick = (WhenStartedBrick) loadedFirstSprite.getScript(1).getBrick(0);
		StartScript referencedScript = (StartScript) TestUtils.getPrivateField("script", loadedScriptBrick, false);
		assertEquals("Script referencing of bricks wrong", loadedFirstSprite.getScript(0), referencedScript);
		UtilFile.deleteDirectory(projectDirectory);
	}

	public void testSerializePerformanceTest() {
		FullParser parser = new FullParser();
		Project bigProject = null;
		try {
			bigProject = parser.fullParser("test_aquarium_project.xml");
		} catch (ParseException e) {
			fail("Unexpected ParseException");
			e.printStackTrace();
		}

		XmlSerializer serializer = new XmlSerializer();

		String bigProjectDirectoryName = Utils.buildProjectPath("test_1_" + bigProject.getName());
		File bigProjectDirectory = new File(bigProjectDirectoryName);

		if (!(bigProjectDirectory.exists() && bigProjectDirectory.isDirectory() && bigProjectDirectory.canWrite())) {
			bigProjectDirectory.mkdir();

		}
		try {
			long starTime = System.currentTimeMillis();
			serializer.toXml(bigProject, Utils.buildPath(bigProjectDirectoryName, Constants.PROJECTCODE_NAME));
			long endTime = System.currentTimeMillis();
			long duration = endTime - starTime;
			Log.i("SerializerTest", "big project duration is " + duration + " ms");
		} catch (SerializeException e) {
			fail("Unexpected exception");
			e.printStackTrace();
		}
		Project loadedBigProject = null;
		try {
			parser = null;
			parser = new FullParser();
			InputStream bigProjectFileStream = new FileInputStream(Utils.buildPath(
					bigProjectDirectory.getAbsolutePath(), Constants.PROJECTCODE_NAME));
			loadedBigProject = parser.parseSpritesWithProject(bigProjectFileStream);
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		assertNotNull("big project null", loadedBigProject);
		assertEquals("number of sprites wrong", 11, loadedBigProject.getSpriteList().size());
		UtilFile.deleteDirectory(bigProjectDirectory);
	}

	private static class ProjectWithVersionCode extends Project {
		static final long serialVersionUID = 1L;
		private final int mCatroidVersionCode;

		public ProjectWithVersionCode(String name, int catroidVersionCode) {
			super(null, name);
			mCatroidVersionCode = catroidVersionCode;
		}

		@Override
		public int getCatroidVersionCode() {
			return mCatroidVersionCode;
		}
	}

	public void testSerializingChildClassProject() {
		Project project = new ProjectWithVersionCode("versionProject", 123);
		Sprite firstSprite = new Sprite("cat");
		Script testScript = new StartScript(firstSprite);

		firstSprite.addScript(testScript);
		project.addSprite(firstSprite);

		XmlSerializer serializer = new XmlSerializer();
		String projectDirectoryName = Utils.buildProjectPath("test_" + project.getName());
		File projectDirectory = new File(projectDirectoryName);

		if (!(projectDirectory.exists() && projectDirectory.isDirectory() && projectDirectory.canWrite())) {
			projectDirectory.mkdir();

		}
		try {
			serializer.toXml(project, Utils.buildPath(projectDirectoryName, Constants.PROJECTCODE_NAME));
		} catch (SerializeException e) {
			e.printStackTrace();
		}
		Project testProject = null;
		try {
			FullParser parser = new FullParser();
			InputStream projectFileStream = new FileInputStream(Utils.buildPath(projectDirectory.getAbsolutePath(),
					Constants.PROJECTCODE_NAME));

			testProject = parser.parseSpritesWithProject(projectFileStream);

		} catch (FileNotFoundException e) {

			e.printStackTrace();
		} catch (ParseException e) {

			e.printStackTrace();
		}

		assertNotNull("testproject is null", testProject);
		UtilFile.deleteDirectory(projectDirectory);

	}

	public void testvaliidateXML() {
		FullParser parser = new FullParser();
		Project project = null;
		try {
			project = parser.fullParser("test_standard_project_new_version.xml");
		} catch (ParseException e) {
			fail("Unexpected ParseException");
			e.printStackTrace();
		}
		XmlSerializer serializer = new XmlSerializer();
		String projectDirectoryName = Utils.buildProjectPath("test_" + project.getName());
		File projectDirectory = new File(projectDirectoryName);

		if (!(projectDirectory.exists() && projectDirectory.isDirectory() && projectDirectory.canWrite())) {
			projectDirectory.mkdir();

		}
		try {
			serializer.toXml(project, Utils.buildPath(projectDirectoryName, Constants.PROJECTCODE_NAME));
		} catch (SerializeException e) {
			e.printStackTrace();
		}
		UtilFile.deleteDirectory(projectDirectory);
		//SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

		//		URL schemaUrl;
		//		Schema schema = null;
		//		try {
		//			schemaUrl = new URL(XMLSCHEMA_URL);
		//		//	schema = factory.newSchema(schemaUrl);
		//		} catch (MalformedURLException e) {
		//			e.printStackTrace();
		//		} catch (SAXException e) {
		//			e.printStackTrace();
		//		}
		//
		//		Validator schemaValidator = schema.newValidator();
		//
		//		File xmlDirectory = projectDirectory;
		//		File[] xmlFilesToValidate = xmlDirectory.listFiles();
		//
		//		File currentXMLFile = null;
		//		try {
		//			for (File xmlFile : xmlFilesToValidate) {
		//				currentXMLFile = xmlFile;
		//				Source source = new StreamSource(currentXMLFile);
		//				schemaValidator.validate(source);
		//			}
		//		} catch (SAXException ex) {
		//			ex.printStackTrace();
		//			assertFalse(currentXMLFile + " is not valid because: " + ex.getMessage(), true);
		//		} catch (IOException e) {
		//			e.printStackTrace();
		//		}
	}
}

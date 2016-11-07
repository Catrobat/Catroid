/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2016 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * An additional term exception under section 7 of the GNU Affero
 * General Public License, version 3, is available at
 * http://developer.catrobat.org/license_additional_term
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.test.io;

import android.test.InstrumentationTestCase;
import android.util.Log;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.DefaultProjectHandler;
import org.catrobat.catroid.content.LegoNXTSetting;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Setting;
import org.catrobat.catroid.content.SingleSprite;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.BrickBaseType;
import org.catrobat.catroid.content.bricks.ComeToFrontBrick;
import org.catrobat.catroid.content.bricks.FormulaBrick;
import org.catrobat.catroid.content.bricks.HideBrick;
import org.catrobat.catroid.content.bricks.LegoNxtMotorMoveBrick;
import org.catrobat.catroid.content.bricks.PlaceAtBrick;
import org.catrobat.catroid.content.bricks.SetSizeToBrick;
import org.catrobat.catroid.content.bricks.ShowBrick;
import org.catrobat.catroid.content.bricks.SpeakBrick;
import org.catrobat.catroid.devices.mindstorms.nxt.sensors.NXTSensor;
import org.catrobat.catroid.drone.DroneBrickFactory;
import org.catrobat.catroid.exceptions.CompatibilityProjectException;
import org.catrobat.catroid.exceptions.LoadingProjectException;
import org.catrobat.catroid.exceptions.OutdatedVersionProjectException;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.FormulaElement;
import org.catrobat.catroid.formulaeditor.InterpretationException;
import org.catrobat.catroid.formulaeditor.Sensors;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.test.utils.Reflection;
import org.catrobat.catroid.test.utils.TestUtils;
import org.catrobat.catroid.ui.SettingsActivity;
import org.catrobat.catroid.ui.controller.BackPackListManager;
import org.catrobat.catroid.uitest.util.UiTestUtils;
import org.catrobat.catroid.utils.UtilFile;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static org.catrobat.catroid.common.Constants.PROJECTCODE_NAME;
import static org.catrobat.catroid.common.Constants.PROJECTCODE_NAME_TMP;
import static org.catrobat.catroid.common.Constants.PROJECTPERMISSIONS_NAME;
import static org.catrobat.catroid.utils.Utils.buildPath;
import static org.catrobat.catroid.utils.Utils.buildProjectPath;

public class StorageHandlerTest extends InstrumentationTestCase {
	private final StorageHandler storageHandler;
	private final String projectName = TestUtils.DEFAULT_TEST_PROJECT_NAME;
	private final String backpackJsonValid = "backpack.json";
	private final String backpackJsonInvalid = "backpack_invalid.json";
	private final String backpackFilePath = buildPath(Constants.DEFAULT_ROOT, Constants.BACKPACK_DIRECTORY,
			StorageHandler.BACKPACK_FILENAME);
	private static final int SET_SPEED_INITIALLY = -70;
	private static final int DEFAULT_MOVE_TIME_IN_MILLISECONDS = 2000;
	private static final int DEFAULT_MOVE_POWER_IN_PERCENT = 20;

	public StorageHandlerTest() throws IOException {
		storageHandler = StorageHandler.getInstance();
	}

	@Override
	public void setUp() throws Exception {
		DefaultProjectHandler.createAndSaveDefaultProject(getInstrumentation().getTargetContext());
		super.setUp();
//		currentProject = ProjectManager.getInstance().getCurrentProject();
	}

	@Override
	public void tearDown() throws Exception {
//		ProjectManager.getInstance().setProject(currentProject);
		TestUtils.deleteTestProjects();
		super.tearDown();
	}

	public void testSerializeProject() {
		final int xPosition = 457;
		final int yPosition = 598;
		final float size = 0.8f;

		Project project = new Project(getInstrumentation().getTargetContext(), projectName);
		Sprite firstSprite = new SingleSprite("first");
		Sprite secondSprite = new SingleSprite("second");
		Sprite thirdSprite = new SingleSprite("third");
		Sprite fourthSprite = new SingleSprite("fourth");
		Script testScript = new StartScript();
		Script otherScript = new StartScript();
		HideBrick hideBrick = new HideBrick();
		ShowBrick showBrick = new ShowBrick();
		SetSizeToBrick setSizeToBrick = new SetSizeToBrick(size);
		ComeToFrontBrick comeToFrontBrick = new ComeToFrontBrick();
		PlaceAtBrick placeAtBrick = new PlaceAtBrick(xPosition, yPosition);

		testScript.addBrick(hideBrick);
		testScript.addBrick(showBrick);
		testScript.addBrick(setSizeToBrick);
		testScript.addBrick(comeToFrontBrick);

		otherScript.addBrick(placeAtBrick);

		firstSprite.addScript(testScript);
		secondSprite.addScript(otherScript);

		project.getDefaultScene().addSprite(firstSprite);
		project.getDefaultScene().addSprite(secondSprite);
		project.getDefaultScene().addSprite(thirdSprite);
		project.getDefaultScene().addSprite(fourthSprite);

		storageHandler.saveProject(project);

		Project loadedProject = storageHandler.loadProject(projectName, getInstrumentation().getContext());

		Scene preScene = project.getDefaultScene();
		Scene postScene = loadedProject.getDefaultScene();

		ArrayList<Sprite> preSpriteList = (ArrayList<Sprite>) project.getDefaultScene().getSpriteList();
		ArrayList<Sprite> postSpriteList = (ArrayList<Sprite>) loadedProject.getDefaultScene().getSpriteList();

		//Test scene name:
		assertEquals("Scene does not match after deserialization", preScene.getName(), postScene.getName());

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
		Formula actualXPosition = ((FormulaBrick) postSpriteList.get(2).getScript(0).getBrickList().get(0))
				.getFormulaWithBrickField(Brick.BrickField.X_POSITION);
		Formula actualYPosition = ((FormulaBrick) postSpriteList.get(2).getScript(0).getBrickList().get(0))
				.getFormulaWithBrickField(Brick.BrickField.Y_POSITION);

		Formula actualSize = ((FormulaBrick) postSpriteList.get(1).getScript(0).getBrickList().get(2))
				.getFormulaWithBrickField(Brick.BrickField.SIZE);

		assertEquals("Size was not deserialized right", size, interpretFormula(actualSize, null));
		assertEquals("XPosition was not deserialized right", xPosition,
				interpretFormula(actualXPosition, null).intValue());
		assertEquals("YPosition was not deserialized right", yPosition,
				interpretFormula(actualYPosition, null).intValue());

		// Test version codes and names
		//		final int preVersionCode = (Integer) TestUtils.getPrivateField("catroidVersionCode", project, false);
		//		final int postVersionCode = (Integer) TestUtils.getPrivateField("catroidVersionCode", loadedProject, false);
		//		assertEquals("Version codes are not equal", preVersionCode, postVersionCode);
		//
		//		final String preVersionName = (String) TestUtils.getPrivateField("catroidVersionName", project, false);
		//		final String postVersionName = (String) TestUtils.getPrivateField("catroidVersionName", loadedProject, false);
		//		assertEquals("Version names are not equal", preVersionName, postVersionName);
	}

	public void testSanityCheck() throws IOException {
		final int xPosition = 457;
		final int yPosition = 598;
		final float size = 0.8f;

		final Project project = new Project(getInstrumentation().getTargetContext(), projectName);
		Sprite firstSprite = new SingleSprite("first");
		Sprite secondSprite = new SingleSprite("second");
		Sprite thirdSprite = new SingleSprite("third");
		Sprite fourthSprite = new SingleSprite("fourth");
		Script testScript = new StartScript();
		Script otherScript = new StartScript();
		HideBrick hideBrick = new HideBrick();
		ShowBrick showBrick = new ShowBrick();
		SetSizeToBrick setSizeToBrick = new SetSizeToBrick(size);
		ComeToFrontBrick comeToFrontBrick = new ComeToFrontBrick();
		PlaceAtBrick placeAtBrick = new PlaceAtBrick(xPosition, yPosition);

		testScript.addBrick(hideBrick);
		testScript.addBrick(showBrick);
		testScript.addBrick(setSizeToBrick);
		testScript.addBrick(comeToFrontBrick);

		otherScript.addBrick(placeAtBrick);

		firstSprite.addScript(testScript);
		secondSprite.addScript(otherScript);

		project.getDefaultScene().addSprite(firstSprite);
		project.getDefaultScene().addSprite(secondSprite);
		project.getDefaultScene().addSprite(thirdSprite);
		project.getDefaultScene().addSprite(fourthSprite);

		File tmpCodeFile = new File(buildProjectPath(project.getName()), PROJECTCODE_NAME_TMP);
		File currentCodeFile = new File(buildProjectPath(project.getName()), PROJECTCODE_NAME);
		assertFalse(tmpCodeFile.getName() + " exists!", tmpCodeFile.exists());
		assertFalse(currentCodeFile.getName() + " exists!", currentCodeFile.exists());

		storageHandler.saveProject(project);

		assertTrue(currentCodeFile.getName() + " was not created!", currentCodeFile.exists());
		assertTrue(PROJECTCODE_NAME + " is empty!", currentCodeFile.length() > 0);

		// simulate 1st Option: tmp_code.xml exists but code.xml doesn't exist --> saveProject process will restore from tmp_code.xml
		if (!tmpCodeFile.createNewFile()) {
			fail("Could not create tmp file");
		}
		UtilFile.copyFile(tmpCodeFile, currentCodeFile);
		String currentCodeFileXml = Files.toString(currentCodeFile, Charsets.UTF_8);
		assertTrue("Could not delete " + currentCodeFile.getName(), currentCodeFile.delete());

		storageHandler.saveProject(project);

		assertTrue(currentCodeFile.getName() + " was not created!", currentCodeFile.exists());
		assertTrue(PROJECTCODE_NAME + " is empty!", currentCodeFile.length() > 0);
		assertTrue("Sanity Check Failed. New Code File is not equal with tmp file.", currentCodeFileXml.equals(Files.toString(currentCodeFile, Charsets.UTF_8)));

		// simulate 2nd Option: tmp_code.xml and code.xml exist --> saveProject process will discard tmp_code.xml and use code.xml
		if (!tmpCodeFile.createNewFile()) {
			fail("Could not create tmp file");
		}

		storageHandler.saveProject(project);

		assertFalse("Sanity Check Failed. tmp file was not discarded.", tmpCodeFile.exists());
	}

	private Float interpretFormula(Formula formula, Sprite sprite) {
		try {
			return formula.interpretFloat(sprite);
		} catch (InterpretationException interpretationException) {
			Log.d(getClass().getSimpleName(), "Formula interpretation for Formula failed.", interpretationException);
		}
		return Float.NaN;
	}

	public void testGetRequiredResources() {
		int resources = generateMultiplePermissionsProject().getRequiredResources();
		assertEquals("Sum over required resources not matching", Brick.ARDRONE_SUPPORT
				| Brick.FACE_DETECTION
				| Brick.BLUETOOTH_LEGO_NXT
				| Brick.TEXT_TO_SPEECH, resources);
	}

	public void testWritePermissionFile() throws IOException {
		Project project = generateMultiplePermissionsProject();
		ProjectManager.getInstance().setProject(project);
		StorageHandler.getInstance().saveProject(project);

		File permissionsFile = new File(buildProjectPath(project.getName()), PROJECTPERMISSIONS_NAME);
		assertTrue("File containing the permissions could not be written", permissionsFile.exists());

		//only for assertions. Add future permission; Vibration and LED not activated
		Set<String> permissions = new HashSet<String>();
		permissions.add(Constants.ARDRONE_SUPPORT);
		permissions.add(Constants.BLUETOOTH_LEGO_NXT);
		permissions.add(Constants.TEXT_TO_SPEECH);
		permissions.add(Constants.FACE_DETECTION);

		BufferedReader reader = new BufferedReader(new FileReader(permissionsFile));
		String line;
		while ((line = reader.readLine()) != null) {
			assertTrue("Wrong permission in File found", permissions.contains(line));
		}
	}

	public void testSerializeSettings() throws CompatibilityProjectException, OutdatedVersionProjectException, LoadingProjectException {

		NXTSensor.Sensor[] sensorMapping = new NXTSensor.Sensor[] {
				NXTSensor.Sensor.TOUCH, NXTSensor.Sensor.SOUND,
				NXTSensor.Sensor.LIGHT_INACTIVE, NXTSensor.Sensor.ULTRASONIC
		};

		Reflection.setPrivateField(ProjectManager.getInstance(), "asynchronousTask", false);

		Project project = generateMultiplePermissionsProject();
		ProjectManager.getInstance().setProject(project);

		String projectName = project.getName();
		SettingsActivity.setLegoMindstormsNXTSensorMapping(getInstrumentation().getTargetContext(), sensorMapping);

		ProjectManager.getInstance().saveProject(getInstrumentation().getTargetContext());
		Setting setting = project.getSettings().get(0);

		assertTrue("Wrong setting type, LegoNXT setting expected", setting instanceof LegoNXTSetting);

		LegoNXTSetting nxtSetting = (LegoNXTSetting) setting;
		NXTSensor.Sensor[] actualSensorMapping = nxtSetting.getSensorMapping();

		assertEquals("Wrong numer of sensors", 4, actualSensorMapping.length);

		assertEquals("Wrong sensor mapping for touch sensor", sensorMapping[0], actualSensorMapping[0]);
		assertEquals("Wrong sensor mapping for sound sensor", sensorMapping[1], actualSensorMapping[1]);
		assertEquals("Wrong sensor mapping for light sensor", sensorMapping[2], actualSensorMapping[2]);
		assertEquals("Wrong sensor mapping for ultrasonic sensor", sensorMapping[3], actualSensorMapping[3]);

		NXTSensor.Sensor[] changedSensorMapping = sensorMapping.clone();
		changedSensorMapping[0] = NXTSensor.Sensor.LIGHT_ACTIVE;

		SettingsActivity.setLegoMindstormsNXTSensorMapping(getInstrumentation().getTargetContext(), changedSensorMapping);

		ProjectManager.getInstance().setProject(null);
		ProjectManager.getInstance().loadProject(projectName, getInstrumentation().getTargetContext());

		actualSensorMapping = SettingsActivity.getLegoMindstormsNXTSensorMapping(getInstrumentation().getTargetContext());

		assertEquals("Wrong numer of sensors", 4, actualSensorMapping.length);

		assertEquals("Wrong sensor mapping for touch sensor, settings not correctly loaded from project",
				sensorMapping[0], actualSensorMapping[0]);
		assertEquals("Wrong sensor mapping for sound sensor", sensorMapping[1], actualSensorMapping[1]);
		assertEquals("Wrong sensor mapping for light sensor", sensorMapping[2], actualSensorMapping[2]);
		assertEquals("Wrong sensor mapping for ultrasonic sensor", sensorMapping[3], actualSensorMapping[3]);

		project = ProjectManager.getInstance().getCurrentProject();

		setting = project.getSettings().get(0);
		nxtSetting = (LegoNXTSetting) setting;

		assertTrue("Wrong setting type, LegoNXT setting expected", setting instanceof LegoNXTSetting);

		actualSensorMapping = nxtSetting.getSensorMapping();

		assertEquals("Wrong numer of sensors", 4, actualSensorMapping.length);

		assertEquals("Wrong sensor mapping for touch sensor", sensorMapping[0], actualSensorMapping[0]);
		assertEquals("Wrong sensor mapping for sound sensor", sensorMapping[1], actualSensorMapping[1]);
		assertEquals("Wrong sensor mapping for light sensor", sensorMapping[2], actualSensorMapping[2]);
		assertEquals("Wrong sensor mapping for ultrasonic sensor", sensorMapping[3], actualSensorMapping[3]);
	}

	public void testDeserializeInvalidBackpackFile() throws IOException {
		File backPackFile = loadBackpackFile(backpackJsonInvalid);

		BackPackListManager.getInstance().loadBackpack();
		TestUtils.sleep(1000);

		assertTrue("Backpacked items loaded despite file is invalid!", BackPackListManager.getInstance().getBackpack()
				.backpackedScripts.isEmpty());
		assertFalse("Backpack.json should be deleted!", backPackFile.exists());
	}

	public void testDeserializeValidBackpackFile() throws IOException {
		File backPackFile = loadBackpackFile(backpackJsonValid);

		BackPackListManager.getInstance().loadBackpack();
		TestUtils.sleep(1000);

		assertFalse("Backpacked sprites not loaded!", BackPackListManager.getInstance().getBackpack().backpackedSprites.isEmpty());
		assertFalse("Backpacked scripts not loaded!", BackPackListManager.getInstance().getBackpack().hiddenBackpackedScripts.isEmpty());
		assertFalse("Backpacked looks not loaded!", BackPackListManager.getInstance().getBackpack().hiddenBackpackedLooks.isEmpty());
		assertFalse("Backpacked sounds not loaded!", BackPackListManager.getInstance().getBackpack().hiddenBackpackedSounds.isEmpty());
		assertTrue("Backpack.json should not be deleted!", backPackFile.exists());
	}

	private File loadBackpackFile(String jsonName) throws IOException {
		UiTestUtils.clearBackPack(true);
		InputStream inputStream = getInstrumentation().getContext().getResources().getAssets().open(jsonName);
		File backPackFile = new File(backpackFilePath);
		assertFalse("Backpack.json should not exist!", backPackFile.exists());

		byte[] buffer = new byte[inputStream.available()];
		inputStream.read(buffer);

		File targetFile = new File(backpackFilePath);
		OutputStream outStream = new FileOutputStream(targetFile);
		outStream.write(buffer);
		assertTrue("Backpack.json should exist!", backPackFile.exists());
		assertTrue("Backpacked items not deleted!", BackPackListManager.getInstance().getBackpack()
				.backpackedScripts.isEmpty());
		return backPackFile;
	}

	private Project generateMultiplePermissionsProject() {
		final Project project = new Project(getInstrumentation().getTargetContext(), projectName);
		Sprite firstSprite = new SingleSprite("first");
		Sprite secondSprite = new SingleSprite("second");
		Script testScript = new StartScript();
		Script otherScript = new StartScript();
		HideBrick hideBrick = new HideBrick();
		ShowBrick showBrick = new ShowBrick();
		SpeakBrick speakBrick = new SpeakBrick("");
		LegoNxtMotorMoveBrick motorBrick = new LegoNxtMotorMoveBrick(LegoNxtMotorMoveBrick.Motor.MOTOR_A, SET_SPEED_INITIALLY);
		SetSizeToBrick setSizeToBrick = new SetSizeToBrick(new Formula(new FormulaElement(FormulaElement.ElementType.SENSOR,
				Sensors.FACE_SIZE.name(), null)));
		BrickBaseType moveBrick = DroneBrickFactory.getInstanceOfDroneBrick(DroneBrickFactory.DroneBricks.DRONE_MOVE_FORWARD_BRICK,
				DEFAULT_MOVE_TIME_IN_MILLISECONDS, DEFAULT_MOVE_POWER_IN_PERCENT);

		testScript.addBrick(hideBrick);
		testScript.addBrick(showBrick);
		testScript.addBrick(speakBrick);
		testScript.addBrick(motorBrick);
		otherScript.addBrick(setSizeToBrick);
		otherScript.addBrick(moveBrick);

		firstSprite.addScript(testScript);
		secondSprite.addScript(otherScript);

		project.getDefaultScene().addSprite(firstSprite);
		project.getDefaultScene().addSprite(secondSprite);

		return project;
	}
}

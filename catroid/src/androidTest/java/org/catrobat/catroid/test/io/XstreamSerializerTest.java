/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2018 The Catrobat Team
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
import org.catrobat.catroid.drone.ardrone.DroneBrickFactory;
import org.catrobat.catroid.exceptions.CompatibilityProjectException;
import org.catrobat.catroid.exceptions.LoadingProjectException;
import org.catrobat.catroid.exceptions.OutdatedVersionProjectException;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.FormulaElement;
import org.catrobat.catroid.formulaeditor.InterpretationException;
import org.catrobat.catroid.formulaeditor.Sensors;
import org.catrobat.catroid.io.StorageOperations;
import org.catrobat.catroid.io.XstreamSerializer;
import org.catrobat.catroid.test.utils.TestUtils;
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static org.catrobat.catroid.common.Constants.CODE_XML_FILE_NAME;
import static org.catrobat.catroid.common.Constants.PERMISSIONS_FILE_NAME;
import static org.catrobat.catroid.common.Constants.TMP_CODE_XML_FILE_NAME;
import static org.catrobat.catroid.utils.PathBuilder.buildProjectPath;

public class XstreamSerializerTest extends InstrumentationTestCase {

	private final XstreamSerializer storageHandler;
	private final String projectName = "testProject";

	private Project currentProjectBuffer;

	private static final int SET_SPEED_INITIALLY = -70;
	private static final int DEFAULT_MOVE_TIME_IN_MILLISECONDS = 2000;
	private static final int DEFAULT_MOVE_POWER_IN_PERCENT = 20;

	public XstreamSerializerTest() {
		storageHandler = XstreamSerializer.getInstance();
	}

	@Override
	public void setUp() throws Exception {
		TestUtils.deleteProjects(projectName);
		DefaultProjectHandler.createAndSaveDefaultProject(getInstrumentation().getTargetContext());

		currentProjectBuffer = ProjectManager.getInstance().getCurrentProject();
		super.setUp();
	}

	@Override
	public void tearDown() throws Exception {
		ProjectManager.getInstance().setProject(currentProjectBuffer);

		TestUtils.deleteProjects(projectName);
		super.tearDown();
	}

	public void testSerializeProject() throws Exception {
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

		assertEquals(preScene.getName(), postScene.getName());

		assertEquals(preSpriteList.get(0).getName(), postSpriteList.get(0).getName());
		assertEquals(preSpriteList.get(1).getName(), postSpriteList.get(1).getName());
		assertEquals(preSpriteList.get(2).getName(), postSpriteList.get(2).getName());
		assertEquals(preSpriteList.get(3).getName(), postSpriteList.get(3).getName());
		assertEquals(preSpriteList.get(4).getName(), postSpriteList.get(4).getName());

		assertEquals(project.getName(), loadedProject.getName());

		Formula actualXPosition = ((FormulaBrick) postSpriteList.get(2).getScript(0).getBrickList().get(0))
				.getFormulaWithBrickField(Brick.BrickField.X_POSITION);

		Formula actualYPosition = ((FormulaBrick) postSpriteList.get(2).getScript(0).getBrickList().get(0))
				.getFormulaWithBrickField(Brick.BrickField.Y_POSITION);

		Formula actualSize = ((FormulaBrick) postSpriteList.get(1).getScript(0).getBrickList().get(2))
				.getFormulaWithBrickField(Brick.BrickField.SIZE);

		assertEquals(size, interpretFormula(actualSize, null));
		assertEquals(xPosition, interpretFormula(actualXPosition, null).intValue());
		assertEquals(yPosition, interpretFormula(actualYPosition, null).intValue());
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

		File tmpCodeFile = new File(buildProjectPath(project.getName()), TMP_CODE_XML_FILE_NAME);
		File currentCodeFile = new File(buildProjectPath(project.getName()), CODE_XML_FILE_NAME);

		assertFalse(tmpCodeFile.exists());
		assertFalse(currentCodeFile.exists());

		storageHandler.saveProject(project);

		assertTrue(currentCodeFile.exists());
		assertTrue(currentCodeFile.length() > 0);

		// simulate 1st Option: tmp_code.xml exists but code.xml doesn't exist
		// --> saveProject process will restore from tmp_code.xml
		if (!tmpCodeFile.createNewFile()) {
			fail("Could not create tmp file");
		}

		StorageOperations.transferData(currentCodeFile, tmpCodeFile);
		String currentCodeFileXml = Files.toString(currentCodeFile, Charsets.UTF_8);

		assertTrue(currentCodeFile.delete());

		storageHandler.saveProject(project);

		assertTrue(currentCodeFile.exists());
		assertTrue(currentCodeFile.length() > 0);
		assertTrue(currentCodeFileXml.equals(Files.toString(currentCodeFile, Charsets.UTF_8)));

		// simulate 2nd Option: tmp_code.xml and code.xml exist
		// --> saveProject process will discard tmp_code.xml and use code.xml
		if (!tmpCodeFile.createNewFile()) {
			fail("Could not create tmp file");
		}

		storageHandler.saveProject(project);

		assertFalse(tmpCodeFile.exists());
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
		assertEquals(Brick.ARDRONE_SUPPORT
				| Brick.FACE_DETECTION
				| Brick.BLUETOOTH_LEGO_NXT
				| Brick.TEXT_TO_SPEECH, resources);
	}

	public void testWritePermissionFile() throws IOException {
		Project project = generateMultiplePermissionsProject();
		ProjectManager.getInstance().setProject(project);
		XstreamSerializer.getInstance().saveProject(project);

		File permissionsFile = new File(buildProjectPath(project.getName()), PERMISSIONS_FILE_NAME);
		assertTrue(permissionsFile.exists());

		//only for assertions. Add future permission; Vibration and LED not activated
		Set<String> permissions = new HashSet<>();
		permissions.add(Constants.ARDRONE_SUPPORT);
		permissions.add(Constants.BLUETOOTH_LEGO_NXT);
		permissions.add(Constants.TEXT_TO_SPEECH);
		permissions.add(Constants.FACE_DETECTION);

		BufferedReader reader = new BufferedReader(new FileReader(permissionsFile));
		String line;
		while ((line = reader.readLine()) != null) {
			assertTrue(permissions.contains(line));
		}
	}

	public void testSerializeSettings() throws CompatibilityProjectException, OutdatedVersionProjectException,
			LoadingProjectException {
		NXTSensor.Sensor[] sensorMapping = new NXTSensor.Sensor[] {
				NXTSensor.Sensor.TOUCH, NXTSensor.Sensor.SOUND,
				NXTSensor.Sensor.LIGHT_INACTIVE, NXTSensor.Sensor.ULTRASONIC
		};

		Project project = generateMultiplePermissionsProject();
		ProjectManager.getInstance().setProject(project);

		String projectName = project.getName();
		SettingsFragment.setLegoMindstormsNXTSensorMapping(getInstrumentation().getTargetContext(), sensorMapping);

		ProjectManager.getInstance().saveProject(getInstrumentation().getTargetContext());
		Setting setting = project.getSettings().get(0);

		assertTrue(setting instanceof LegoNXTSetting);

		LegoNXTSetting nxtSetting = (LegoNXTSetting) setting;
		NXTSensor.Sensor[] actualSensorMapping = nxtSetting.getSensorMapping();

		assertEquals(4, actualSensorMapping.length);

		assertEquals(sensorMapping[0], actualSensorMapping[0]);
		assertEquals(sensorMapping[1], actualSensorMapping[1]);
		assertEquals(sensorMapping[2], actualSensorMapping[2]);
		assertEquals(sensorMapping[3], actualSensorMapping[3]);

		NXTSensor.Sensor[] changedSensorMapping = sensorMapping.clone();
		changedSensorMapping[0] = NXTSensor.Sensor.LIGHT_ACTIVE;

		SettingsFragment.setLegoMindstormsNXTSensorMapping(getInstrumentation().getTargetContext(), changedSensorMapping);

		ProjectManager.getInstance().setProject(null);
		ProjectManager.getInstance().loadProject(projectName, getInstrumentation().getTargetContext());

		actualSensorMapping = SettingsFragment.getLegoNXTSensorMapping(getInstrumentation().getTargetContext());

		assertEquals(4, actualSensorMapping.length);

		assertEquals(sensorMapping[0], actualSensorMapping[0]);
		assertEquals(sensorMapping[1], actualSensorMapping[1]);
		assertEquals(sensorMapping[2], actualSensorMapping[2]);
		assertEquals(sensorMapping[3], actualSensorMapping[3]);

		project = ProjectManager.getInstance().getCurrentProject();

		setting = project.getSettings().get(0);
		nxtSetting = (LegoNXTSetting) setting;

		assertTrue(setting instanceof LegoNXTSetting);

		actualSensorMapping = nxtSetting.getSensorMapping();

		assertEquals(4, actualSensorMapping.length);

		assertEquals(sensorMapping[0], actualSensorMapping[0]);
		assertEquals(sensorMapping[1], actualSensorMapping[1]);
		assertEquals(sensorMapping[2], actualSensorMapping[2]);
		assertEquals(sensorMapping[3], actualSensorMapping[3]);
	}

	private Project generateMultiplePermissionsProject() {
		Project project = new Project(getInstrumentation().getTargetContext(), projectName);
		Sprite firstSprite = new SingleSprite("first");
		Sprite secondSprite = new SingleSprite("second");
		Script testScript = new StartScript();
		Script otherScript = new StartScript();
		HideBrick hideBrick = new HideBrick();
		ShowBrick showBrick = new ShowBrick();
		SpeakBrick speakBrick = new SpeakBrick("");
		LegoNxtMotorMoveBrick motorBrick = new LegoNxtMotorMoveBrick(
				LegoNxtMotorMoveBrick.Motor.MOTOR_A, SET_SPEED_INITIALLY);

		SetSizeToBrick setSizeToBrick = new SetSizeToBrick(
				new Formula(
						new FormulaElement(FormulaElement.ElementType.SENSOR, Sensors.FACE_SIZE.name(), null)));

		BrickBaseType moveBrick = DroneBrickFactory.getInstanceOfDroneBrick(
				DroneBrickFactory.DroneBricks.DRONE_MOVE_FORWARD_BRICK,
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

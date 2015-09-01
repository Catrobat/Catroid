/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2015 The Catrobat Team
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
package org.catrobat.catroid.io;

import android.util.Log;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.reflection.PureJavaReflectionProvider;

import org.catrobat.catroid.content.BroadcastScript;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.WhenScript;
import org.catrobat.catroid.content.bricks.Brick.BrickField;
import org.catrobat.catroid.content.bricks.BroadcastBrick;
import org.catrobat.catroid.content.bricks.BroadcastReceiverBrick;
import org.catrobat.catroid.content.bricks.BroadcastWaitBrick;
import org.catrobat.catroid.content.bricks.ChangeBrightnessByNBrick;
import org.catrobat.catroid.content.bricks.ChangeSizeByNBrick;
import org.catrobat.catroid.content.bricks.ChangeTransparencyByNBrick;
import org.catrobat.catroid.content.bricks.ChangeVariableBrick;
import org.catrobat.catroid.content.bricks.ChangeVolumeByNBrick;
import org.catrobat.catroid.content.bricks.ChangeXByNBrick;
import org.catrobat.catroid.content.bricks.ChangeYByNBrick;
import org.catrobat.catroid.content.bricks.ClearGraphicEffectBrick;
import org.catrobat.catroid.content.bricks.ComeToFrontBrick;
import org.catrobat.catroid.content.bricks.DroneFlipBrick;
import org.catrobat.catroid.content.bricks.DroneLandBrick;
import org.catrobat.catroid.content.bricks.DroneMoveBackwardBrick;
import org.catrobat.catroid.content.bricks.DroneMoveDownBrick;
import org.catrobat.catroid.content.bricks.DroneMoveForwardBrick;
import org.catrobat.catroid.content.bricks.DroneMoveLeftBrick;
import org.catrobat.catroid.content.bricks.DroneMoveRightBrick;
import org.catrobat.catroid.content.bricks.DroneMoveUpBrick;
import org.catrobat.catroid.content.bricks.DronePlayLedAnimationBrick;
import org.catrobat.catroid.content.bricks.DroneTakeOffBrick;
import org.catrobat.catroid.content.bricks.ForeverBrick;
import org.catrobat.catroid.content.bricks.GlideToBrick;
import org.catrobat.catroid.content.bricks.GoNStepsBackBrick;
import org.catrobat.catroid.content.bricks.HideBrick;
import org.catrobat.catroid.content.bricks.HideTextBrick;
import org.catrobat.catroid.content.bricks.IfLogicBeginBrick;
import org.catrobat.catroid.content.bricks.IfLogicElseBrick;
import org.catrobat.catroid.content.bricks.IfLogicEndBrick;
import org.catrobat.catroid.content.bricks.IfOnEdgeBounceBrick;
import org.catrobat.catroid.content.bricks.LedOffBrick;
import org.catrobat.catroid.content.bricks.LedOnBrick;
import org.catrobat.catroid.content.bricks.LegoNxtMotorMoveBrick;
import org.catrobat.catroid.content.bricks.LegoNxtMotorStopBrick;
import org.catrobat.catroid.content.bricks.LegoNxtMotorTurnAngleBrick;
import org.catrobat.catroid.content.bricks.LegoNxtPlayToneBrick;
import org.catrobat.catroid.content.bricks.LoopEndBrick;
import org.catrobat.catroid.content.bricks.LoopEndlessBrick;
import org.catrobat.catroid.content.bricks.MoveNStepsBrick;
import org.catrobat.catroid.content.bricks.NextLookBrick;
import org.catrobat.catroid.content.bricks.NoteBrick;
import org.catrobat.catroid.content.bricks.PhiroIfLogicBeginBrick;
import org.catrobat.catroid.content.bricks.PhiroMotorMoveBackwardBrick;
import org.catrobat.catroid.content.bricks.PhiroMotorMoveForwardBrick;
import org.catrobat.catroid.content.bricks.PhiroMotorStopBrick;
import org.catrobat.catroid.content.bricks.PhiroPlayToneBrick;
import org.catrobat.catroid.content.bricks.PhiroRGBLightBrick;
import org.catrobat.catroid.content.bricks.PlaceAtBrick;
import org.catrobat.catroid.content.bricks.PlaySoundBrick;
import org.catrobat.catroid.content.bricks.PointInDirectionBrick;
import org.catrobat.catroid.content.bricks.PointToBrick;
import org.catrobat.catroid.content.bricks.RepeatBrick;
import org.catrobat.catroid.content.bricks.SetBrightnessBrick;
import org.catrobat.catroid.content.bricks.SetLookBrick;
import org.catrobat.catroid.content.bricks.SetSizeToBrick;
import org.catrobat.catroid.content.bricks.SetTransparencyBrick;
import org.catrobat.catroid.content.bricks.SetVariableBrick;
import org.catrobat.catroid.content.bricks.SetVolumeToBrick;
import org.catrobat.catroid.content.bricks.SetXBrick;
import org.catrobat.catroid.content.bricks.SetYBrick;
import org.catrobat.catroid.content.bricks.ShowBrick;
import org.catrobat.catroid.content.bricks.ShowTextBrick;
import org.catrobat.catroid.content.bricks.SpeakBrick;
import org.catrobat.catroid.content.bricks.StopAllSoundsBrick;
import org.catrobat.catroid.content.bricks.TurnLeftBrick;
import org.catrobat.catroid.content.bricks.TurnRightBrick;
import org.catrobat.catroid.content.bricks.VibrationBrick;
import org.catrobat.catroid.content.bricks.WaitBrick;
import org.catrobat.catroid.content.bricks.WhenBrick;
import org.catrobat.catroid.content.bricks.WhenStartedBrick;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

public class XStreamToSupportCatrobatLanguageVersion096AndBefore extends XStream {

	private static final String TAG = XStreamToSupportCatrobatLanguageVersion096AndBefore.class.getSimpleName();

	private HashMap<String, BrickInfo> brickInfoMap;
	private HashMap<String, String> scriptInfoMap;

	public XStreamToSupportCatrobatLanguageVersion096AndBefore(PureJavaReflectionProvider reflectionProvider) {
		super(reflectionProvider);
	}

	public Object getProjectFromXML(File file) {
		Object parsedObject;
		try {
			parsedObject = super.fromXML(file);
		} catch (ConversionException exception) {
			Log.e(TAG, "Conversion error " + exception.getLocalizedMessage());
			modifyXMLToSupportUnknownFields(file);
			parsedObject = super.fromXML(file);
		}
		return parsedObject;
	}

	private void initializeBrickInfoMap() {
		if (brickInfoMap != null) {
			return;
		}

		brickInfoMap = new HashMap<String, BrickInfo>();

		BrickInfo brickInfo = new BrickInfo(BroadcastBrick.class.getSimpleName());
		brickInfoMap.put("broadcastBrick", brickInfo);

		brickInfo = new BrickInfo(BroadcastReceiverBrick.class.getSimpleName());
		brickInfoMap.put("broadcastReceiverBrick", brickInfo);

		brickInfo = new BrickInfo(BroadcastWaitBrick.class.getSimpleName());
		brickInfoMap.put("broadcastWaitBrick", brickInfo);

		brickInfo = new BrickInfo(ChangeBrightnessByNBrick.class.getSimpleName());
		brickInfo.addBrickFieldToMap("changeBrightness", BrickField.BRIGHTNESS_CHANGE);
		brickInfoMap.put("changeBrightnessByNBrick", brickInfo);

		brickInfo = new BrickInfo(ChangeTransparencyByNBrick.class.getSimpleName());
		brickInfo.addBrickFieldToMap("changeTransparency", BrickField.TRANSPARENCY_CHANGE);
		brickInfoMap.put("changeTransparencyByNBrick", brickInfo);

		brickInfo = new BrickInfo(ChangeSizeByNBrick.class.getSimpleName());
		brickInfo.addBrickFieldToMap("size", BrickField.SIZE_CHANGE);
		brickInfoMap.put("changeSizeByNBrick", brickInfo);

		brickInfo = new BrickInfo(ChangeVariableBrick.class.getSimpleName());
		brickInfo.addBrickFieldToMap("variableFormula", BrickField.VARIABLE_CHANGE);
		brickInfoMap.put("changeVariableBrick", brickInfo);

		brickInfo = new BrickInfo(ChangeVolumeByNBrick.class.getSimpleName());
		brickInfo.addBrickFieldToMap("volume", BrickField.VOLUME_CHANGE);
		brickInfoMap.put("changeVolumeByNBrick", brickInfo);

		brickInfo = new BrickInfo(ChangeXByNBrick.class.getSimpleName());
		brickInfo.addBrickFieldToMap("xMovement", BrickField.X_POSITION_CHANGE);
		brickInfoMap.put("changeXByNBrick", brickInfo);

		brickInfo = new BrickInfo(ChangeYByNBrick.class.getSimpleName());
		brickInfo.addBrickFieldToMap("yMovement", BrickField.Y_POSITION_CHANGE);
		brickInfoMap.put("changeYByNBrick", brickInfo);

		brickInfo = new BrickInfo(ClearGraphicEffectBrick.class.getSimpleName());
		brickInfoMap.put("clearGraphicEffectBrick", brickInfo);

		brickInfo = new BrickInfo(ComeToFrontBrick.class.getSimpleName());
		brickInfoMap.put("comeToFrontBrick", brickInfo);

		brickInfo = new BrickInfo(ForeverBrick.class.getSimpleName());
		brickInfoMap.put("foreverBrick", brickInfo);

		brickInfo = new BrickInfo(GlideToBrick.class.getSimpleName());
		brickInfo.addBrickFieldToMap("xDestination", BrickField.X_DESTINATION);
		brickInfo.addBrickFieldToMap("yDestination", BrickField.Y_DESTINATION);
		brickInfo.addBrickFieldToMap("durationInSeconds", BrickField.DURATION_IN_SECONDS);
		brickInfoMap.put("glideToBrick", brickInfo);

		brickInfo = new BrickInfo(GoNStepsBackBrick.class.getSimpleName());
		brickInfo.addBrickFieldToMap("steps", BrickField.STEPS);
		brickInfoMap.put("goNStepsBackBrick", brickInfo);

		brickInfo = new BrickInfo(HideBrick.class.getSimpleName());
		brickInfoMap.put("hideBrick", brickInfo);

		brickInfo = new BrickInfo(IfLogicBeginBrick.class.getSimpleName());
		brickInfo.addBrickFieldToMap("ifCondition", BrickField.IF_CONDITION);
		brickInfoMap.put("ifLogicBeginBrick", brickInfo);

		brickInfo = new BrickInfo(IfLogicElseBrick.class.getSimpleName());
		brickInfoMap.put("ifLogicElseBrick", brickInfo);

		brickInfo = new BrickInfo(IfLogicEndBrick.class.getSimpleName());
		brickInfoMap.put("ifLogicEndBrick", brickInfo);

		brickInfo = new BrickInfo(IfOnEdgeBounceBrick.class.getSimpleName());
		brickInfoMap.put("ifOnEdgeBounceBrick", brickInfo);

		brickInfo = new BrickInfo(LedOffBrick.class.getSimpleName());
		brickInfoMap.put("ledOffBrick", brickInfo);

		brickInfo = new BrickInfo(LedOnBrick.class.getSimpleName());
		brickInfoMap.put("ledOnBrick", brickInfo);

		brickInfo = new BrickInfo(LegoNxtMotorMoveBrick.class.getSimpleName());
		brickInfo.addBrickFieldToMap("speed", BrickField.LEGO_NXT_SPEED);
		brickInfoMap.put("legoNxtMotorMoveBrick", brickInfo);

		brickInfo = new BrickInfo(LegoNxtMotorStopBrick.class.getSimpleName());
		brickInfoMap.put("legoNxtMotorStopBrick", brickInfo);

		brickInfo = new BrickInfo(LegoNxtMotorTurnAngleBrick.class.getSimpleName());
		brickInfo.addBrickFieldToMap("degrees", BrickField.LEGO_NXT_DEGREES);
		brickInfoMap.put("legoNxtMotorTurnAngleBrick", brickInfo);

		brickInfo = new BrickInfo(LegoNxtPlayToneBrick.class.getSimpleName());
		brickInfo.addBrickFieldToMap("frequency", BrickField.LEGO_NXT_FREQUENCY);
		brickInfo.addBrickFieldToMap("durationInSeconds", BrickField.LEGO_NXT_DURATION_IN_SECONDS);
		brickInfoMap.put("legoNxtPlayToneBrick", brickInfo);

		brickInfo = new BrickInfo(PhiroMotorMoveForwardBrick.class.getSimpleName());
		brickInfo.addBrickFieldToMap("speed", BrickField.PHIRO_SPEED);
		brickInfoMap.put("phiroMotorMoveForwardBrick", brickInfo);

		brickInfo = new BrickInfo(PhiroMotorMoveBackwardBrick.class.getSimpleName());
		brickInfo.addBrickFieldToMap("speed", BrickField.PHIRO_SPEED);
		brickInfoMap.put("phiroMotorMoveBackwardBrick", brickInfo);

		brickInfo = new BrickInfo(PhiroMotorStopBrick.class.getSimpleName());
		brickInfoMap.put("phiroMotorStopBrick", brickInfo);

		brickInfo = new BrickInfo(PhiroPlayToneBrick.class.getSimpleName());
		brickInfo.addBrickFieldToMap("durationInSeconds", BrickField.PHIRO_DURATION_IN_SECONDS);
		brickInfoMap.put("phiroPlayToneBrick", brickInfo);

		brickInfo = new BrickInfo(PhiroRGBLightBrick.class.getSimpleName());
		brickInfo.addBrickFieldToMap("light", BrickField.PHIRO_LIGHT_RED);
		brickInfo.addBrickFieldToMap("light", BrickField.PHIRO_LIGHT_GREEN);
		brickInfo.addBrickFieldToMap("light", BrickField.PHIRO_LIGHT_BLUE);
		brickInfoMap.put("phiroRGBLightBrick", brickInfo);

		brickInfo = new BrickInfo(PhiroIfLogicBeginBrick.class.getSimpleName());
		brickInfo.addBrickFieldToMap("ifPhiroSensorCondition", BrickField.IF_PHIRO_SENSOR_CONDITION);
		brickInfoMap.put("phiroSensorBrick", brickInfo);

		brickInfo = new BrickInfo(IfLogicElseBrick.class.getSimpleName());
		brickInfoMap.put("phiroSensorElseBrick", brickInfo);

		brickInfo = new BrickInfo(IfLogicEndBrick.class.getSimpleName());
		brickInfoMap.put("phiroSensorEndBrick", brickInfo);

		brickInfo = new BrickInfo(LoopEndBrick.class.getSimpleName());
		brickInfoMap.put("loopEndBrick", brickInfo);

		brickInfo = new BrickInfo(LoopEndlessBrick.class.getSimpleName());
		brickInfoMap.put("loopEndlessBrick", brickInfo);

		brickInfo = new BrickInfo(MoveNStepsBrick.class.getSimpleName());
		brickInfo.addBrickFieldToMap("steps", BrickField.STEPS);
		brickInfoMap.put("moveNStepsBrick", brickInfo);

		brickInfo = new BrickInfo(NextLookBrick.class.getSimpleName());
		brickInfoMap.put("nextLookBrick", brickInfo);

		brickInfo = new BrickInfo(NoteBrick.class.getSimpleName());
		brickInfo.addBrickFieldToMap("note", BrickField.NOTE);
		brickInfoMap.put("noteBrick", brickInfo);

		brickInfo = new BrickInfo(PlaceAtBrick.class.getSimpleName());
		brickInfo.addBrickFieldToMap("xPosition", BrickField.X_POSITION);
		brickInfo.addBrickFieldToMap("yPosition", BrickField.Y_POSITION);
		brickInfoMap.put("placeAtBrick", brickInfo);

		brickInfo = new BrickInfo(PlaySoundBrick.class.getSimpleName());
		brickInfoMap.put("playSoundBrick", brickInfo);

		brickInfo = new BrickInfo(PointInDirectionBrick.class.getSimpleName());
		brickInfo.addBrickFieldToMap("degrees", BrickField.DEGREES);
		brickInfoMap.put("pointInDirectionBrick", brickInfo);

		brickInfo = new BrickInfo(PointToBrick.class.getSimpleName());
		brickInfoMap.put("pointToBrick", brickInfo);

		brickInfo = new BrickInfo(RepeatBrick.class.getSimpleName());
		brickInfo.addBrickFieldToMap("timesToRepeat", BrickField.TIMES_TO_REPEAT);
		brickInfoMap.put("repeatBrick", brickInfo);

		brickInfo = new BrickInfo(SetBrightnessBrick.class.getSimpleName());
		brickInfo.addBrickFieldToMap("brightness", BrickField.BRIGHTNESS);
		brickInfoMap.put("setBrightnessBrick", brickInfo);

		brickInfo = new BrickInfo(SetTransparencyBrick.class.getSimpleName());
		brickInfo.addBrickFieldToMap("transparency", BrickField.TRANSPARENCY);
		brickInfoMap.put("setTransparencyBrick", brickInfo);

		brickInfo = new BrickInfo(SetLookBrick.class.getSimpleName());
		brickInfoMap.put("setLookBrick", brickInfo);

		brickInfo = new BrickInfo(SetSizeToBrick.class.getSimpleName());
		brickInfo.addBrickFieldToMap("size", BrickField.SIZE);
		brickInfoMap.put("setSizeToBrick", brickInfo);

		brickInfo = new BrickInfo(SetVariableBrick.class.getSimpleName());
		brickInfo.addBrickFieldToMap("variableFormula", BrickField.VARIABLE);
		brickInfoMap.put("setVariableBrick", brickInfo);

		brickInfo = new BrickInfo(SetVolumeToBrick.class.getSimpleName());
		brickInfo.addBrickFieldToMap("volume", BrickField.VOLUME);
		brickInfoMap.put("setVolumeToBrick", brickInfo);

		brickInfo = new BrickInfo(SetXBrick.class.getSimpleName());
		brickInfo.addBrickFieldToMap("xPosition", BrickField.X_POSITION);
		brickInfoMap.put("setXBrick", brickInfo);

		brickInfo = new BrickInfo(SetYBrick.class.getSimpleName());
		brickInfo.addBrickFieldToMap("yPosition", BrickField.Y_POSITION);
		brickInfoMap.put("setYBrick", brickInfo);

		brickInfo = new BrickInfo(ShowBrick.class.getSimpleName());
		brickInfoMap.put("showBrick", brickInfo);

		brickInfo = new BrickInfo(SpeakBrick.class.getSimpleName());
		brickInfo.addBrickFieldToMap("text", BrickField.SPEAK);
		brickInfoMap.put("speakBrick", brickInfo);

		brickInfo = new BrickInfo(StopAllSoundsBrick.class.getSimpleName());
		brickInfoMap.put("whenBrick", brickInfo);

		brickInfo = new BrickInfo(TurnLeftBrick.class.getSimpleName());
		brickInfo.addBrickFieldToMap("degrees", BrickField.TURN_LEFT_DEGREES);
		brickInfoMap.put("turnLeftBrick", brickInfo);

		brickInfo = new BrickInfo(TurnRightBrick.class.getSimpleName());
		brickInfo.addBrickFieldToMap("degrees", BrickField.TURN_RIGHT_DEGREES);
		brickInfoMap.put("turnRightBrick", brickInfo);

		brickInfo = new BrickInfo(VibrationBrick.class.getSimpleName());
		brickInfoMap.put("vibrationBrick", brickInfo);

		brickInfo = new BrickInfo(WaitBrick.class.getSimpleName());
		brickInfo.addBrickFieldToMap("timeToWaitInSeconds", BrickField.TIME_TO_WAIT_IN_SECONDS);
		brickInfoMap.put("waitBrick", brickInfo);

		brickInfo = new BrickInfo(WhenBrick.class.getSimpleName());
		brickInfoMap.put("stopAllSoundsBrick", brickInfo);

		brickInfo = new BrickInfo(WhenStartedBrick.class.getSimpleName());
		brickInfoMap.put("whenStartedBrick", brickInfo);

		brickInfo = new BrickInfo(DronePlayLedAnimationBrick.class.getSimpleName());
		brickInfoMap.put("dronePlayLedAnimationBrick", brickInfo);

		brickInfo = new BrickInfo(DroneFlipBrick.class.getSimpleName());
		brickInfoMap.put("droneFlipBrick", brickInfo);

		brickInfo = new BrickInfo(DroneTakeOffBrick.class.getSimpleName());
		brickInfoMap.put("droneTakeOffBrick", brickInfo);

		brickInfo = new BrickInfo(DroneLandBrick.class.getSimpleName());
		brickInfoMap.put("droneTakeOffBrick", brickInfo);

		brickInfo = new BrickInfo(DroneMoveForwardBrick.class.getSimpleName());
		brickInfo.addBrickFieldToMap("timeToFlyInSeconds", BrickField.DRONE_TIME_TO_FLY_IN_SECONDS);
		brickInfo.addBrickFieldToMap("powerInPercent", BrickField.DRONE_POWER_IN_PERCENT);
		brickInfoMap.put("droneMoveForwardBrick", brickInfo);

		brickInfo = new BrickInfo(DroneMoveBackwardBrick.class.getSimpleName());
		brickInfo.addBrickFieldToMap("timeToFlyInSeconds", BrickField.DRONE_TIME_TO_FLY_IN_SECONDS);
		brickInfo.addBrickFieldToMap("powerInPercent", BrickField.DRONE_POWER_IN_PERCENT);
		brickInfoMap.put("droneMoveBackwardBrick", brickInfo);

		brickInfo = new BrickInfo(DroneMoveUpBrick.class.getSimpleName());
		brickInfo.addBrickFieldToMap("timeToFlyInSeconds", BrickField.DRONE_TIME_TO_FLY_IN_SECONDS);
		brickInfo.addBrickFieldToMap("powerInPercent", BrickField.DRONE_POWER_IN_PERCENT);
		brickInfoMap.put("droneMoveUpBrick", brickInfo);

		brickInfo = new BrickInfo(DroneMoveDownBrick.class.getSimpleName());
		brickInfo.addBrickFieldToMap("timeToFlyInSeconds", BrickField.DRONE_TIME_TO_FLY_IN_SECONDS);
		brickInfo.addBrickFieldToMap("powerInPercent", BrickField.DRONE_POWER_IN_PERCENT);
		brickInfoMap.put("droneMoveDownBrick", brickInfo);

		brickInfo = new BrickInfo(DroneMoveLeftBrick.class.getSimpleName());
		brickInfo.addBrickFieldToMap("timeToFlyInSeconds", BrickField.DRONE_TIME_TO_FLY_IN_SECONDS);
		brickInfo.addBrickFieldToMap("powerInPercent", BrickField.DRONE_POWER_IN_PERCENT);
		brickInfoMap.put("droneMoveLeftBrick", brickInfo);

		brickInfo = new BrickInfo(DroneMoveRightBrick.class.getSimpleName());
		brickInfo.addBrickFieldToMap("timeToFlyInSeconds", BrickField.DRONE_TIME_TO_FLY_IN_SECONDS);
		brickInfo.addBrickFieldToMap("powerInPercent", BrickField.DRONE_POWER_IN_PERCENT);
		brickInfoMap.put("droneMoveRightBrick", brickInfo);

		brickInfo = new BrickInfo(ShowTextBrick.class.getSimpleName());
		brickInfoMap.put("showTextBrick", brickInfo);

		brickInfo = new BrickInfo(HideTextBrick.class.getSimpleName());
		brickInfoMap.put("hideTextBrick", brickInfo);
	}

	private void initializeScriptInfoMap() {
		if (scriptInfoMap != null) {
			return;
		}

		scriptInfoMap = new HashMap<String, String>();
		scriptInfoMap.put("startScript", StartScript.class.getSimpleName());
		scriptInfoMap.put("whenScript", WhenScript.class.getSimpleName());
		scriptInfoMap.put("broadcastScript", BroadcastScript.class.getSimpleName());
	}

	private void modifyXMLToSupportUnknownFields(File file) {
		initializeScriptInfoMap();
		initializeBrickInfoMap();
		Document originalDocument = getDocument(file);
		if (originalDocument != null) {
			updateLegoNXTFields(originalDocument);

			convertChildNodeToAttribute(originalDocument, "lookList", "name");
			convertChildNodeToAttribute(originalDocument, "object", "name");

			deleteChildNodeByName(originalDocument, "scriptList", "object");
			deleteChildNodeByName(originalDocument, "brickList", "object");

			modifyScriptLists(originalDocument);
			modifyBrickLists(originalDocument);
			modifyVariables(originalDocument);
			checkReferences(originalDocument.getDocumentElement());

			saveDocument(originalDocument, file);
		}
	}

	private void updateLegoNXTFields(Document originalDocument) {

		final String oldDriveMotors = "MOTOR_A_C";
		final String newDriveMotors = "MOTOR_B_C";

		final String oldMotorMoveBrickName = "legoNxtMotorActionBrick";
		final String newMotorMoveBrickName = "legoNxtMotorMoveBrick";

		NodeList motors = originalDocument.getElementsByTagName("motor");
		for (int i = 0; i < motors.getLength(); i++) {
			Node motor = motors.item(i);
			if (motor.getTextContent().equals(oldDriveMotors)) {
				motor.setTextContent(newDriveMotors);
			}
		}

		NodeList motorMoveBricks = originalDocument.getElementsByTagName(oldMotorMoveBrickName);
		for (int i = 0; i < motorMoveBricks.getLength(); i++) {
			Node motorMoveBrick = motorMoveBricks.item(i);
			originalDocument.renameNode(motorMoveBrick, motorMoveBrick.getNamespaceURI(), newMotorMoveBrickName);
		}
	}

	private void modifyVariables(Document originalDocument) {

		try {
			Node variableNode = originalDocument.getElementsByTagName("variables").item(0);
			String variableNodeNamespaceURI = variableNode.getNamespaceURI();
			originalDocument.renameNode(variableNode, variableNodeNamespaceURI, "data");
		} catch (Exception exception) {
			Log.e(TAG, "Failed to modify variables tag", exception);
		}
	}

	private Document getDocument(File file) {
		try {
			Transformer serializer = TransformerFactory.newInstance().newTransformer();
			serializer.setOutputProperty(OutputKeys.INDENT, "yes");
			serializer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(file);
			normalizeWhiteSpaces(doc);
			return doc;
		} catch (Exception exception) {
			Log.e(TAG, "Failed to parse file to a Document", exception);
		}
		return null;
	}

	private void saveDocument(Document doc, File file) {
		try {
			Transformer serializer = TransformerFactory.newInstance().newTransformer();
			serializer.setOutputProperty(OutputKeys.INDENT, "yes");
			serializer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(file.getPath());
			serializer.transform(source, result);
		} catch (Exception exception) {
			Log.e(TAG, "Failed to save document to file", exception);
		}
	}

	private void normalizeWhiteSpaces(Document document) throws XPathExpressionException {
		XPath xPath = XPathFactory.newInstance().newXPath();
		NodeList emptyTextNodeList = (NodeList) xPath.evaluate("//text()[normalize-space(.)='']", document,
				XPathConstants.NODESET);

		for (int index = 0; index < emptyTextNodeList.getLength(); ++index) {
			Node emptyTextNode = emptyTextNodeList.item(index);
			emptyTextNode.getParentNode().removeChild(emptyTextNode);
		}
	}

	private Element findNodeByName(Node parentNode, String nodeName) {
		NodeList childNodes = parentNode.getChildNodes();
		if (childNodes != null) {
			for (int i = 0; i < childNodes.getLength(); i++) {
				if (childNodes.item(i).getNodeName().equals(nodeName)) {
					return (Element) childNodes.item(i);
				}
			}
		}
		return null;
	}

	private void deleteChildNodeByName(Node parentNode, String childNodeName) {
		Node node = findNodeByName(parentNode, childNodeName);
		if (node != null) {
			parentNode.removeChild(node);
		}
	}

	private void deleteChildNodeByName(Document doc, String listNodeName, String childNodeName) {
		NodeList nodeList = doc.getElementsByTagName(listNodeName);
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			if (node.hasChildNodes()) {
				NodeList childNodes = node.getChildNodes();
				for (int j = 0; j < childNodes.getLength(); j++) {
					deleteChildNodeByName(childNodes.item(j), childNodeName);
				}
			}
		}
	}

	private void copyAttributesIfNeeded(Node sourceNode, Element destinationNode) {
		if (sourceNode.getNodeName().equals("loopEndlessBrick") || sourceNode.getNodeName().equals("loopEndBrick")
				|| sourceNode.getNodeName().equals("ifLogicElseBrick")
				|| sourceNode.getNodeName().equals("ifLogicEndBrick")) {
			return;
		}
		NamedNodeMap namedNodeMap = sourceNode.getAttributes();
		for (int i = 0; i < namedNodeMap.getLength(); i++) {
			Attr node = (Attr) namedNodeMap.item(i);
			destinationNode.setAttributeNS(node.getNamespaceURI(), node.getName(), node.getValue());
		}
	}

	private void convertChildNodeToAttribute(Document originalDocument, String parentNodeName, String childNodeName) {
		NodeList nodeList = originalDocument.getElementsByTagName(parentNodeName);
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			Node childNode = findNodeByName(node, childNodeName);
			if (childNode != null && node instanceof Element) {
				Element elem = (Element) node;
				elem.setAttribute(childNodeName, childNode.getTextContent());
				node.removeChild(childNode);
			}
		}
	}

	private void modifyScriptLists(Document originalDocument) {
		NodeList scriptListNodeList = originalDocument.getElementsByTagName("scriptList");
		for (int i = 0; i < scriptListNodeList.getLength(); i++) {
			Node scriptListNode = scriptListNodeList.item(i);
			if (scriptListNode.hasChildNodes()) {
				NodeList scriptListChildNodes = scriptListNode.getChildNodes();
				for (int j = 0; j < scriptListChildNodes.getLength(); j++) {
					Node scriptNode = scriptListChildNodes.item(j);
					Element newScriptNode = originalDocument.createElement("script");

					String scriptName = scriptInfoMap.get(scriptNode.getNodeName());
					if (scriptName != null) {
						newScriptNode.setAttribute("type", scriptName);
						copyAttributesIfNeeded(scriptNode, newScriptNode);

						if (scriptNode.hasChildNodes()) {
							NodeList scriptNodeChildList = scriptNode.getChildNodes();
							for (int k = 0; k < scriptNodeChildList.getLength(); k++) {
								newScriptNode.appendChild(scriptNodeChildList.item(k));
							}
						}

						scriptListNode.replaceChild(newScriptNode, scriptNode);
					} else {
						Log.e(TAG, scriptNode.getNodeName() + " script cannot be converted to new structure");
					}
				}
			}
		}
	}

	private void modifyBrickLists(Document originalDocument) {
		NodeList brickListNodeList = originalDocument.getElementsByTagName("brickList");
		for (int i = 0; i < brickListNodeList.getLength(); i++) {
			Node brickListNode = brickListNodeList.item(i);
			if (brickListNode.hasChildNodes()) {
				NodeList brickListChildNodes = brickListNode.getChildNodes();
				for (int j = 0; j < brickListChildNodes.getLength(); j++) {
					Node brickNode = brickListChildNodes.item(j);
					Element newBrickNode = originalDocument.createElement("brick");

					if (brickNode.getNodeName().equals("setGhostEffectBrick")) {
						originalDocument.renameNode(brickNode, brickNode.getNamespaceURI(), "setTransparencyBrick");
					}
					if (brickNode.getNodeName().equals("changeGhostEffectByNBrick")) {
						originalDocument.renameNode(brickNode, brickNode.getNamespaceURI(), "changeTransparencyByNBrick");
					}

					BrickInfo brickInfo = brickInfoMap.get(brickNode.getNodeName());
					if (brickInfo != null) {
						newBrickNode.setAttribute("type", brickInfo.brickClassName);
						copyAttributesIfNeeded(brickNode, newBrickNode);

						if (brickNode.hasChildNodes()) {
							NodeList brickChildNodes = brickNode.getChildNodes();
							for (int k = 0; k < brickChildNodes.getLength(); k++) {
								Element brickChild = (Element) brickChildNodes.item(k);

								if (brickChild.getNodeName().equals("changeGhostEffect")) {
									originalDocument.renameNode(brickChild, brickChild.getNamespaceURI(), "changeTransparency");
								}

								if (brickInfo.getBrickFieldForOldFieldName(brickChild.getNodeName()) != null) {
									handleFormulaNode(originalDocument, brickInfo, newBrickNode, brickChild);
								} else if (brickChild.getNodeName().equals("userVariable")) {
									handleUserVariableNode(newBrickNode, brickChild);
								} else if (brickChild.getNodeName().equals("loopEndBrick")
										|| brickChild.getNodeName().equals("ifElseBrick")
										|| brickChild.getNodeName().equals("ifEndBrick")) {
									continue;
								} else {
									newBrickNode.appendChild(brickChild);
								}
							}
						}
						brickListNode.replaceChild(newBrickNode, brickNode);
					} else {
						Log.e(TAG, brickNode.getNodeName() + " brick cannot be converted to new structure");
					}
				}
			}
		}
	}

	private void handleFormulaNode(Document doc, BrickInfo brickInfo, Element newParentNode, Element oldNode) {
		Node formulaListNode = findNodeByName(newParentNode, "formulaList");
		if (formulaListNode == null) {
			formulaListNode = doc.createElement("formulaList");
			newParentNode.appendChild(formulaListNode);
		}

		Element formulaNode = findNodeByName(oldNode, "formulaTree");
		if (formulaNode == null) {
			formulaNode = doc.createElement("formula");
		} else {
			doc.renameNode(formulaNode, formulaNode.getNamespaceURI(), "formula");
		}
		String category = brickInfo.getBrickFieldForOldFieldName(oldNode.getNodeName()).toString();
		formulaNode.setAttribute("category", category);
		if (category.equals("SPEAK") || category.equals("NOTE")) {
			Element type = doc.createElement("type");
			type.setTextContent("STRING");
			formulaNode.appendChild(type);

			Element value = doc.createElement("value");
			String textContent = oldNode.getFirstChild().getTextContent();
			value.setTextContent(textContent);
			formulaNode.appendChild(value);
		}
		formulaListNode.appendChild(formulaNode);
	}

	private void handleUserVariableNode(Element parentNode, Element userVariableNode) {
		if (!userVariableNode.hasAttribute("reference")) {
			Node nameNode = findNodeByName(userVariableNode, "name");
			if (nameNode != null) {
				String userVariable = nameNode.getTextContent();
				userVariableNode.removeChild(nameNode);
				userVariableNode.setTextContent(userVariable);
			}
		}
		parentNode.appendChild(userVariableNode);
	}

	private void checkReferences(Element node) {
		if (node.hasAttribute("reference")) {
			node.setAttribute("reference", getValidReference(node, node.getAttribute("reference")));
		}

		NodeList childNodes = node.getChildNodes();
		if (childNodes != null) {
			for (int i = 0; i < childNodes.getLength(); i++) {
				Node childNode = childNodes.item(i);
				if (childNode instanceof Element) {
					checkReferences((Element) childNode);
				}
			}
		}
	}

	private String getValidReference(Node brickNode, String reference) {
		String[] parts = reference.split("/");
		Node node = brickNode;
		for (int i = 0; i < parts.length; i++) {
			if (parts[i].equals("..")) {
				node = node.getParentNode();
			} else {
				int position = 0;
				String nodeName = parts[i];
				if (parts[i].endsWith("]")) {
					nodeName = parts[i].substring(0, parts[i].indexOf('['));
					position = Integer.parseInt(parts[i].substring(parts[i].indexOf('[') + 1, parts[i].indexOf(']'))) - 1;
				}

				int occurrence = 0;
				NodeList childNodes = node.getChildNodes();
				for (int j = 0; j < childNodes.getLength(); j++) {
					Element childNode = (Element) childNodes.item(j);

					if (childNode.getNodeName().equals(nodeName)) {
						if (occurrence == position) {
							node = childNode;
							break;
						} else {
							occurrence++;
						}
					} else if (childNode.getNodeName().equals("script") && childNode.getAttribute("type")
							.equals(scriptInfoMap.get(nodeName))) {
						if (occurrence == position) {
							parts[i] = "script[" + (j + 1) + "]";
							node = childNode;
							break;
						} else {
							occurrence++;
						}
					} else if (childNode.getNodeName().equals("brick") && childNode.getAttribute("type")
							.equals(brickInfoMap.get(nodeName).getBrickClassName())) {
						if (occurrence == position) {
							parts[i] = "brick[" + (j + 1) + "]";
							node = childNode;
							break;
						} else {
							occurrence++;
						}
					}
				}
			}
		}

		return generateReference(parts);
	}

	private String generateReference(String[] referenceParts) {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < referenceParts.length; i++) {
			builder.append(referenceParts[i]);
			if (i != referenceParts.length - 1) {
				builder.append('/');
			}
		}
		return builder.toString();
	}

	private class BrickInfo {
		private String brickClassName;
		private HashMap<String, BrickField> brickFieldMap;

		BrickInfo(String brickClassName) {
			this.brickClassName = brickClassName;
		}

		void addBrickFieldToMap(String oldFiledName, BrickField brickField) {
			if (brickFieldMap == null) {
				brickFieldMap = new HashMap<String, BrickField>();
			}
			brickFieldMap.put(oldFiledName, brickField);
		}

		BrickField getBrickFieldForOldFieldName(String oldFiledName) {
			if (brickFieldMap != null) {
				return brickFieldMap.get(oldFiledName);
			}
			return null;
		}

		String getBrickClassName() {
			return brickClassName;
		}
	}
}

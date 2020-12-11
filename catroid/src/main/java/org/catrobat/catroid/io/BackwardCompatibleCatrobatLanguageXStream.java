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
package org.catrobat.catroid.io;

import android.util.Log;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.reflection.PureJavaReflectionProvider;

import org.catrobat.catroid.content.BroadcastScript;
import org.catrobat.catroid.content.RaspiInterruptScript;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.UserDefinedScript;
import org.catrobat.catroid.content.WhenBackgroundChangesScript;
import org.catrobat.catroid.content.WhenBounceOffScript;
import org.catrobat.catroid.content.WhenConditionScript;
import org.catrobat.catroid.content.WhenGamepadButtonScript;
import org.catrobat.catroid.content.WhenNfcScript;
import org.catrobat.catroid.content.WhenScript;
import org.catrobat.catroid.content.WhenTouchDownScript;
import org.catrobat.catroid.content.bricks.ArduinoSendDigitalValueBrick;
import org.catrobat.catroid.content.bricks.ArduinoSendPWMValueBrick;
import org.catrobat.catroid.content.bricks.AskBrick;
import org.catrobat.catroid.content.bricks.AskSpeechBrick;
import org.catrobat.catroid.content.bricks.Brick.BrickField;
import org.catrobat.catroid.content.bricks.BroadcastBrick;
import org.catrobat.catroid.content.bricks.BroadcastReceiverBrick;
import org.catrobat.catroid.content.bricks.BroadcastWaitBrick;
import org.catrobat.catroid.content.bricks.CameraBrick;
import org.catrobat.catroid.content.bricks.ChangeBrightnessByNBrick;
import org.catrobat.catroid.content.bricks.ChangeColorByNBrick;
import org.catrobat.catroid.content.bricks.ChangeSizeByNBrick;
import org.catrobat.catroid.content.bricks.ChangeTempoByNBrick;
import org.catrobat.catroid.content.bricks.ChangeTransparencyByNBrick;
import org.catrobat.catroid.content.bricks.ChangeVariableBrick;
import org.catrobat.catroid.content.bricks.ChangeVolumeByNBrick;
import org.catrobat.catroid.content.bricks.ChangeXByNBrick;
import org.catrobat.catroid.content.bricks.ChangeYByNBrick;
import org.catrobat.catroid.content.bricks.ChooseCameraBrick;
import org.catrobat.catroid.content.bricks.ClearBackgroundBrick;
import org.catrobat.catroid.content.bricks.ClearGraphicEffectBrick;
import org.catrobat.catroid.content.bricks.CloneBrick;
import org.catrobat.catroid.content.bricks.ComeToFrontBrick;
import org.catrobat.catroid.content.bricks.DeleteThisCloneBrick;
import org.catrobat.catroid.content.bricks.DroneEmergencyBrick;
import org.catrobat.catroid.content.bricks.DroneFlipBrick;
import org.catrobat.catroid.content.bricks.DroneMoveBackwardBrick;
import org.catrobat.catroid.content.bricks.DroneMoveDownBrick;
import org.catrobat.catroid.content.bricks.DroneMoveForwardBrick;
import org.catrobat.catroid.content.bricks.DroneMoveLeftBrick;
import org.catrobat.catroid.content.bricks.DroneMoveRightBrick;
import org.catrobat.catroid.content.bricks.DroneMoveUpBrick;
import org.catrobat.catroid.content.bricks.DronePlayLedAnimationBrick;
import org.catrobat.catroid.content.bricks.DroneSwitchCameraBrick;
import org.catrobat.catroid.content.bricks.DroneTakeOffLandBrick;
import org.catrobat.catroid.content.bricks.DroneTurnLeftBrick;
import org.catrobat.catroid.content.bricks.DroneTurnRightBrick;
import org.catrobat.catroid.content.bricks.FlashBrick;
import org.catrobat.catroid.content.bricks.ForeverBrick;
import org.catrobat.catroid.content.bricks.GlideToBrick;
import org.catrobat.catroid.content.bricks.GoNStepsBackBrick;
import org.catrobat.catroid.content.bricks.GoToBrick;
import org.catrobat.catroid.content.bricks.HideBrick;
import org.catrobat.catroid.content.bricks.HideTextBrick;
import org.catrobat.catroid.content.bricks.IfLogicBeginBrick;
import org.catrobat.catroid.content.bricks.IfLogicElseBrick;
import org.catrobat.catroid.content.bricks.IfLogicEndBrick;
import org.catrobat.catroid.content.bricks.IfOnEdgeBounceBrick;
import org.catrobat.catroid.content.bricks.IfThenLogicBeginBrick;
import org.catrobat.catroid.content.bricks.IfThenLogicEndBrick;
import org.catrobat.catroid.content.bricks.LegoNxtMotorMoveBrick;
import org.catrobat.catroid.content.bricks.LegoNxtMotorStopBrick;
import org.catrobat.catroid.content.bricks.LegoNxtMotorTurnAngleBrick;
import org.catrobat.catroid.content.bricks.LegoNxtPlayToneBrick;
import org.catrobat.catroid.content.bricks.LoopEndBrick;
import org.catrobat.catroid.content.bricks.LoopEndlessBrick;
import org.catrobat.catroid.content.bricks.MoveNStepsBrick;
import org.catrobat.catroid.content.bricks.NextLookBrick;
import org.catrobat.catroid.content.bricks.NoteBrick;
import org.catrobat.catroid.content.bricks.OpenUrlBrick;
import org.catrobat.catroid.content.bricks.PenDownBrick;
import org.catrobat.catroid.content.bricks.PenUpBrick;
import org.catrobat.catroid.content.bricks.PhiroIfLogicBeginBrick;
import org.catrobat.catroid.content.bricks.PhiroMotorMoveBackwardBrick;
import org.catrobat.catroid.content.bricks.PhiroMotorMoveForwardBrick;
import org.catrobat.catroid.content.bricks.PhiroMotorStopBrick;
import org.catrobat.catroid.content.bricks.PhiroPlayToneBrick;
import org.catrobat.catroid.content.bricks.PhiroRGBLightBrick;
import org.catrobat.catroid.content.bricks.PlaceAtBrick;
import org.catrobat.catroid.content.bricks.PlayDrumForBeatsBrick;
import org.catrobat.catroid.content.bricks.PlayNoteForBeatsBrick;
import org.catrobat.catroid.content.bricks.PlaySoundAndWaitBrick;
import org.catrobat.catroid.content.bricks.PlaySoundBrick;
import org.catrobat.catroid.content.bricks.PointInDirectionBrick;
import org.catrobat.catroid.content.bricks.PointToBrick;
import org.catrobat.catroid.content.bricks.PreviousLookBrick;
import org.catrobat.catroid.content.bricks.RaspiIfLogicBeginBrick;
import org.catrobat.catroid.content.bricks.RaspiPwmBrick;
import org.catrobat.catroid.content.bricks.RaspiSendDigitalValueBrick;
import org.catrobat.catroid.content.bricks.RepeatBrick;
import org.catrobat.catroid.content.bricks.ResetTimerBrick;
import org.catrobat.catroid.content.bricks.SayBubbleBrick;
import org.catrobat.catroid.content.bricks.SayForBubbleBrick;
import org.catrobat.catroid.content.bricks.SceneStartBrick;
import org.catrobat.catroid.content.bricks.SceneTransitionBrick;
import org.catrobat.catroid.content.bricks.SetBackgroundAndWaitBrick;
import org.catrobat.catroid.content.bricks.SetBackgroundBrick;
import org.catrobat.catroid.content.bricks.SetBounceBrick;
import org.catrobat.catroid.content.bricks.SetBrightnessBrick;
import org.catrobat.catroid.content.bricks.SetColorBrick;
import org.catrobat.catroid.content.bricks.SetFrictionBrick;
import org.catrobat.catroid.content.bricks.SetGravityBrick;
import org.catrobat.catroid.content.bricks.SetListeningLanguageBrick;
import org.catrobat.catroid.content.bricks.SetLookBrick;
import org.catrobat.catroid.content.bricks.SetMassBrick;
import org.catrobat.catroid.content.bricks.SetNfcTagBrick;
import org.catrobat.catroid.content.bricks.SetPenColorBrick;
import org.catrobat.catroid.content.bricks.SetPenSizeBrick;
import org.catrobat.catroid.content.bricks.SetPhysicsObjectTypeBrick;
import org.catrobat.catroid.content.bricks.SetRotationStyleBrick;
import org.catrobat.catroid.content.bricks.SetSizeToBrick;
import org.catrobat.catroid.content.bricks.SetTempoBrick;
import org.catrobat.catroid.content.bricks.SetTextBrick;
import org.catrobat.catroid.content.bricks.SetTransparencyBrick;
import org.catrobat.catroid.content.bricks.SetVariableBrick;
import org.catrobat.catroid.content.bricks.SetVelocityBrick;
import org.catrobat.catroid.content.bricks.SetVolumeToBrick;
import org.catrobat.catroid.content.bricks.SetXBrick;
import org.catrobat.catroid.content.bricks.SetYBrick;
import org.catrobat.catroid.content.bricks.SewUpBrick;
import org.catrobat.catroid.content.bricks.ShowBrick;
import org.catrobat.catroid.content.bricks.ShowTextBrick;
import org.catrobat.catroid.content.bricks.SpeakAndWaitBrick;
import org.catrobat.catroid.content.bricks.SpeakBrick;
import org.catrobat.catroid.content.bricks.StampBrick;
import org.catrobat.catroid.content.bricks.StartListeningBrick;
import org.catrobat.catroid.content.bricks.StopAllSoundsBrick;
import org.catrobat.catroid.content.bricks.StopScriptBrick;
import org.catrobat.catroid.content.bricks.ThinkBubbleBrick;
import org.catrobat.catroid.content.bricks.ThinkForBubbleBrick;
import org.catrobat.catroid.content.bricks.TurnLeftBrick;
import org.catrobat.catroid.content.bricks.TurnLeftSpeedBrick;
import org.catrobat.catroid.content.bricks.TurnRightBrick;
import org.catrobat.catroid.content.bricks.TurnRightSpeedBrick;
import org.catrobat.catroid.content.bricks.UserDefinedBrick;
import org.catrobat.catroid.content.bricks.VibrationBrick;
import org.catrobat.catroid.content.bricks.WaitBrick;
import org.catrobat.catroid.content.bricks.WhenBounceOffBrick;
import org.catrobat.catroid.content.bricks.WhenBrick;
import org.catrobat.catroid.content.bricks.WhenClonedBrick;
import org.catrobat.catroid.content.bricks.WhenConditionBrick;
import org.catrobat.catroid.content.bricks.WhenGamepadButtonBrick;
import org.catrobat.catroid.content.bricks.WhenNfcBrick;
import org.catrobat.catroid.content.bricks.WhenStartedBrick;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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

public class BackwardCompatibleCatrobatLanguageXStream extends XStream {

	private static final String TAG = BackwardCompatibleCatrobatLanguageXStream.class.getSimpleName();

	private HashMap<String, BrickInfo> brickInfoMap;
	private HashMap<String, String> scriptInfoMap;

	public BackwardCompatibleCatrobatLanguageXStream(PureJavaReflectionProvider reflectionProvider) {
		super(reflectionProvider);
	}

	public Object getProjectFromXML(File file) {
		Object parsedObject;
		try {
			parsedObject = super.fromXML(file);
		} catch (ConversionException exception) {
			Log.d(TAG, "Conversion error " + exception.getLocalizedMessage());
			modifyXMLToSupportUnknownFields(file);
			parsedObject = super.fromXML(file);
		}
		return parsedObject;
	}

	private void initializeBrickInfoMap() {
		if (brickInfoMap != null) {
			return;
		}

		brickInfoMap = new HashMap<>();

		BrickInfo brickInfo = new BrickInfo(AskBrick.class.getSimpleName());
		brickInfo.addBrickFieldToMap("askQuestion", BrickField.ASK_QUESTION);
		brickInfoMap.put("askBrick", brickInfo);

		brickInfo = new BrickInfo(AskSpeechBrick.class.getSimpleName());
		brickInfo.addBrickFieldToMap("askQuestionSpoken", BrickField.ASK_SPEECH_QUESTION);
		brickInfoMap.put("askSpeechBrick", brickInfo);

		brickInfo = new BrickInfo(StartListeningBrick.class.getSimpleName());
		brickInfoMap.put("startListeningBrick", brickInfo);

		brickInfo = new BrickInfo(SetListeningLanguageBrick.class.getSimpleName());
		brickInfoMap.put("setSpeechLanguageBrick", brickInfo);

		brickInfo = new BrickInfo(BroadcastBrick.class.getSimpleName());
		brickInfoMap.put("broadcastBrick", brickInfo);

		brickInfo = new BrickInfo(BroadcastReceiverBrick.class.getSimpleName());
		brickInfoMap.put("broadcastReceiverBrick", brickInfo);

		brickInfo = new BrickInfo(BroadcastWaitBrick.class.getSimpleName());
		brickInfoMap.put("broadcastWaitBrick", brickInfo);

		brickInfo = new BrickInfo(ChangeBrightnessByNBrick.class.getSimpleName());
		brickInfo.addBrickFieldToMap("changeBrightness", BrickField.BRIGHTNESS_CHANGE);
		brickInfoMap.put("changeBrightnessByNBrick", brickInfo);

		brickInfo = new BrickInfo(ChangeColorByNBrick.class.getSimpleName());
		brickInfo.addBrickFieldToMap("changeColor", BrickField.COLOR_CHANGE);
		brickInfoMap.put("changeColorByNBrick", brickInfo);

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

		brickInfo = new BrickInfo(ResetTimerBrick.class.getSimpleName());
		brickInfoMap.put("resetTimerBrick", brickInfo);

		brickInfo = new BrickInfo(OpenUrlBrick.class.getSimpleName());
		brickInfoMap.put("OpenUrlBrick", brickInfo);

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

		brickInfo = new BrickInfo(IfThenLogicBeginBrick.class.getSimpleName());
		brickInfo.addBrickFieldToMap("ifCondition", BrickField.IF_CONDITION);
		brickInfoMap.put("ifThenLogicBeginBrick", brickInfo);

		brickInfo = new BrickInfo(IfThenLogicEndBrick.class.getSimpleName());
		brickInfoMap.put("ifThenLogicEndBrick", brickInfo);

		brickInfo = new BrickInfo(IfOnEdgeBounceBrick.class.getSimpleName());
		brickInfoMap.put("ifOnEdgeBounceBrick", brickInfo);

		brickInfo = new BrickInfo(FlashBrick.class.getSimpleName());
		brickInfoMap.put("flashBrick", brickInfo);

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
		brickInfoMap.put("phiroSensorBrick", brickInfo);

		brickInfo = new BrickInfo(IfLogicElseBrick.class.getSimpleName());
		brickInfoMap.put("phiroSensorElseBrick", brickInfo);

		brickInfo = new BrickInfo(IfLogicEndBrick.class.getSimpleName());
		brickInfoMap.put("phiroSensorEndBrick", brickInfo);

		brickInfo = new BrickInfo(ArduinoSendDigitalValueBrick.class.getSimpleName());
		brickInfo.addBrickFieldToMap("digitalPinNumber", BrickField.ARDUINO_DIGITAL_PIN_NUMBER);
		brickInfo.addBrickFieldToMap("digitalPinValue", BrickField.ARDUINO_DIGITAL_PIN_VALUE);
		brickInfoMap.put("arduinoSendDigitalValueBrick", brickInfo);

		brickInfo = new BrickInfo(ArduinoSendPWMValueBrick.class.getSimpleName());
		brickInfo.addBrickFieldToMap("pwmPinNumber", BrickField.ARDUINO_ANALOG_PIN_NUMBER);
		brickInfo.addBrickFieldToMap("pwmPinValue", BrickField.ARDUINO_ANALOG_PIN_VALUE);
		brickInfoMap.put("arduinoSendPWMValueBrick", brickInfo);

		brickInfo = new BrickInfo(RaspiSendDigitalValueBrick.class.getSimpleName());
		brickInfo.addBrickFieldToMap("digitalPinNumber", BrickField.RASPI_DIGITAL_PIN_NUMBER);
		brickInfo.addBrickFieldToMap("digitalPinValue", BrickField.RASPI_DIGITAL_PIN_VALUE);
		brickInfoMap.put("raspiSendDigitalValueBrick", brickInfo);

		brickInfo = new BrickInfo(RaspiPwmBrick.class.getSimpleName());
		brickInfo.addBrickFieldToMap("digitalPinNumber", BrickField.RASPI_DIGITAL_PIN_NUMBER);
		brickInfo.addBrickFieldToMap("pwmFrequency", BrickField.RASPI_PWM_FREQUENCY);
		brickInfo.addBrickFieldToMap("pwmPercentage", BrickField.RASPI_PWM_PERCENTAGE);
		brickInfoMap.put("raspiPwmBrick", brickInfo);

		brickInfo = new BrickInfo(RaspiIfLogicBeginBrick.class.getSimpleName());
		brickInfo.addBrickFieldToMap("digitalPinNumber", BrickField.IF_CONDITION);
		brickInfoMap.put("raspiIfLogicBeginBrick", brickInfo);

		brickInfo = new BrickInfo(UserDefinedBrick.class.getSimpleName());
		brickInfoMap.put("userDefinedBrick", brickInfo);

		brickInfo = new BrickInfo(LoopEndBrick.class.getSimpleName());
		brickInfoMap.put("loopEndBrick", brickInfo);

		brickInfo = new BrickInfo(LoopEndlessBrick.class.getSimpleName());
		brickInfoMap.put("loopEndlessBrick", brickInfo);

		brickInfo = new BrickInfo(MoveNStepsBrick.class.getSimpleName());
		brickInfo.addBrickFieldToMap("steps", BrickField.STEPS);
		brickInfoMap.put("moveNStepsBrick", brickInfo);

		brickInfo = new BrickInfo(NextLookBrick.class.getSimpleName());
		brickInfoMap.put("nextLookBrick", brickInfo);

		brickInfo = new BrickInfo(PreviousLookBrick.class.getSimpleName());
		brickInfoMap.put("previousLookBrick", brickInfo);

		brickInfo = new BrickInfo(NoteBrick.class.getSimpleName());
		brickInfo.addBrickFieldToMap("note", BrickField.NOTE);
		brickInfoMap.put("noteBrick", brickInfo);

		brickInfo = new BrickInfo(PlaceAtBrick.class.getSimpleName());
		brickInfo.addBrickFieldToMap("xPosition", BrickField.X_POSITION);
		brickInfo.addBrickFieldToMap("yPosition", BrickField.Y_POSITION);
		brickInfoMap.put("placeAtBrick", brickInfo);

		brickInfo = new BrickInfo(GoToBrick.class.getSimpleName());
		brickInfoMap.put("goToBrick", brickInfo);

		brickInfo = new BrickInfo(PlayDrumForBeatsBrick.class.getSimpleName());
		brickInfoMap.put("playDrumForBeatsBrick", brickInfo);

		brickInfo = new BrickInfo(PlaySoundBrick.class.getSimpleName());
		brickInfoMap.put("playSoundBrick", brickInfo);

		brickInfo = new BrickInfo(PlaySoundAndWaitBrick.class.getSimpleName());
		brickInfoMap.put("playSoundAndWaitBrick", brickInfo);

		brickInfo = new BrickInfo(SetTempoBrick.class.getSimpleName());
		brickInfoMap.put("setTempoBrick", brickInfo);

		brickInfo = new BrickInfo(ChangeTempoByNBrick.class.getSimpleName());
		brickInfoMap.put("changeTempoByNBrick", brickInfo);

		brickInfo = new BrickInfo(PlayNoteForBeatsBrick.class.getSimpleName());
		brickInfoMap.put("playNoteForBeatsBrick", brickInfo);

		brickInfo = new BrickInfo(PointInDirectionBrick.class.getSimpleName());
		brickInfo.addBrickFieldToMap("degrees", BrickField.DEGREES);
		brickInfoMap.put("pointInDirectionBrick", brickInfo);

		brickInfo = new BrickInfo(PointToBrick.class.getSimpleName());
		brickInfoMap.put("pointToBrick", brickInfo);

		brickInfo = new BrickInfo(RepeatBrick.class.getSimpleName());
		brickInfo.addBrickFieldToMap("timesToRepeat", BrickField.TIMES_TO_REPEAT);
		brickInfoMap.put("repeatBrick", brickInfo);

		brickInfo = new BrickInfo(SceneTransitionBrick.class.getSimpleName());
		brickInfoMap.put("sceneTransitionBrick", brickInfo);

		brickInfo = new BrickInfo(SceneStartBrick.class.getSimpleName());
		brickInfoMap.put("sceneStartBrick", brickInfo);

		brickInfo = new BrickInfo(SetBrightnessBrick.class.getSimpleName());
		brickInfo.addBrickFieldToMap("brightness", BrickField.BRIGHTNESS);
		brickInfoMap.put("setBrightnessBrick", brickInfo);

		brickInfo = new BrickInfo(SetColorBrick.class.getSimpleName());
		brickInfo.addBrickFieldToMap("color", BrickField.COLOR);
		brickInfoMap.put("setColorBrick", brickInfo);

		brickInfo = new BrickInfo(SetTransparencyBrick.class.getSimpleName());
		brickInfo.addBrickFieldToMap("transparency", BrickField.TRANSPARENCY);
		brickInfoMap.put("setTransparencyBrick", brickInfo);

		brickInfo = new BrickInfo(SetBackgroundBrick.class.getSimpleName());
		brickInfoMap.put("setBackgroundBrick", brickInfo);

		brickInfo = new BrickInfo(SetBackgroundAndWaitBrick.class.getSimpleName());
		brickInfoMap.put("setBackgroundAndWaitBrick", brickInfo);

		brickInfo = new BrickInfo(SetLookBrick.class.getSimpleName());
		brickInfoMap.put("setLookBrick", brickInfo);

		brickInfo = new BrickInfo(SetRotationStyleBrick.class.getSimpleName());
		brickInfoMap.put("setRotationStyleBrick", brickInfo);

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

		brickInfo = new BrickInfo(ThinkBubbleBrick.class.getSimpleName());
		brickInfo.addBrickFieldToMap("text", BrickField.STRING);
		brickInfoMap.put("thinkBubbleBrick", brickInfo);

		brickInfo = new BrickInfo(SayBubbleBrick.class.getSimpleName());
		brickInfo.addBrickFieldToMap("text", BrickField.STRING);
		brickInfoMap.put("sayBubbleBrick", brickInfo);

		brickInfo = new BrickInfo(ThinkForBubbleBrick.class.getSimpleName());
		brickInfo.addBrickFieldToMap("text", BrickField.STRING);
		brickInfo.addBrickFieldToMap("durationInSeconds", BrickField.DURATION_IN_SECONDS);
		brickInfoMap.put("thinkForBubbleBrick", brickInfo);

		brickInfo = new BrickInfo(SayForBubbleBrick.class.getSimpleName());
		brickInfo.addBrickFieldToMap("text", BrickField.STRING);
		brickInfo.addBrickFieldToMap("durationInSeconds", BrickField.DURATION_IN_SECONDS);
		brickInfoMap.put("sayForBubbleBrick", brickInfo);

		brickInfo = new BrickInfo(SpeakAndWaitBrick.class.getSimpleName());
		brickInfo.addBrickFieldToMap("text", BrickField.SPEAK);
		brickInfoMap.put("speakAndWaitBrick", brickInfo);

		brickInfo = new BrickInfo(WhenBrick.class.getSimpleName());
		brickInfoMap.put("whenBrick", brickInfo);

		brickInfo = new BrickInfo(WhenConditionBrick.class.getSimpleName());
		brickInfoMap.put("whenConditionBrick", brickInfo);

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

		brickInfo = new BrickInfo(StopAllSoundsBrick.class.getSimpleName());
		brickInfoMap.put("stopAllSoundsBrick", brickInfo);

		brickInfo = new BrickInfo(WhenStartedBrick.class.getSimpleName());
		brickInfoMap.put("whenStartedBrick", brickInfo);

		brickInfo = new BrickInfo(StopScriptBrick.class.getSimpleName());
		brickInfoMap.put("stopScriptBrick", brickInfo);

		brickInfo = new BrickInfo(WhenNfcBrick.class.getSimpleName());
		brickInfoMap.put("whenNfcBrick", brickInfo);

		brickInfo = new BrickInfo(SetNfcTagBrick.class.getSimpleName());
		brickInfoMap.put("setNfcTagBrick", brickInfo);

		brickInfo = new BrickInfo(WhenClonedBrick.class.getSimpleName());
		brickInfoMap.put("whenClonedBrick", brickInfo);

		brickInfo = new BrickInfo(CloneBrick.class.getSimpleName());
		brickInfoMap.put("cloneBrick", brickInfo);

		brickInfo = new BrickInfo(DeleteThisCloneBrick.class.getSimpleName());
		brickInfoMap.put("deleteThisCloneBrick", brickInfo);

		brickInfo = new BrickInfo(DronePlayLedAnimationBrick.class.getSimpleName());
		brickInfoMap.put("dronePlayLedAnimationBrick", brickInfo);

		brickInfo = new BrickInfo(DroneEmergencyBrick.class.getSimpleName());
		brickInfoMap.put("droneGoEmergencyBrick", brickInfo);

		brickInfo = new BrickInfo(DroneTakeOffLandBrick.class.getSimpleName());
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

		brickInfo = new BrickInfo(DroneTurnLeftBrick.class.getSimpleName());
		brickInfo.addBrickFieldToMap("timeToFlyInSeconds", BrickField.DRONE_TIME_TO_FLY_IN_SECONDS);
		brickInfo.addBrickFieldToMap("powerInPercent", BrickField.DRONE_POWER_IN_PERCENT);
		brickInfoMap.put("droneTurnLeftBrick", brickInfo);

		brickInfo = new BrickInfo(DroneTurnRightBrick.class.getSimpleName());
		brickInfo.addBrickFieldToMap("timeToFlyInSeconds", BrickField.DRONE_TIME_TO_FLY_IN_SECONDS);
		brickInfo.addBrickFieldToMap("powerInPercent", BrickField.DRONE_POWER_IN_PERCENT);
		brickInfoMap.put("droneTurnRightBrick", brickInfo);

		brickInfo = new BrickInfo(DroneSwitchCameraBrick.class.getSimpleName());
		brickInfoMap.put("droneSwitchCameraBrick", brickInfo);

		brickInfo = new BrickInfo(DroneEmergencyBrick.class.getSimpleName());
		brickInfoMap.put("droneEmergencyBrick", brickInfo);

		brickInfo = new BrickInfo(DroneFlipBrick.class.getSimpleName());
		brickInfoMap.put("droneFlipBrick", brickInfo);

		brickInfo = new BrickInfo(SetTextBrick.class.getSimpleName());
		brickInfo.addBrickFieldToMap("xDestination", BrickField.X_DESTINATION);
		brickInfo.addBrickFieldToMap("yDestination", BrickField.Y_DESTINATION);
		brickInfo.addBrickFieldToMap("string", BrickField.STRING);
		brickInfoMap.put("setTextBrick", brickInfo);

		brickInfo = new BrickInfo(ShowTextBrick.class.getSimpleName());
		brickInfoMap.put("showTextBrick", brickInfo);

		brickInfo = new BrickInfo(HideTextBrick.class.getSimpleName());
		brickInfoMap.put("hideTextBrick", brickInfo);

		brickInfo = new BrickInfo(CameraBrick.class.getSimpleName());
		brickInfoMap.put("videoBrick", brickInfo);

		brickInfo = new BrickInfo(ChooseCameraBrick.class.getSimpleName());
		brickInfoMap.put("chooseCameraBrick", brickInfo);

		brickInfo = new BrickInfo(WhenBounceOffBrick.class.getSimpleName());
		brickInfoMap.put("collisionReceiverBrick", brickInfo);

		brickInfo = new BrickInfo(SetBounceBrick.class.getSimpleName());
		brickInfo.addBrickFieldToMap("bounceFactor", BrickField.PHYSICS_BOUNCE_FACTOR);
		brickInfoMap.put("setBounceBrick", brickInfo);

		brickInfo = new BrickInfo(SetFrictionBrick.class.getSimpleName());
		brickInfo.addBrickFieldToMap("friction", BrickField.PHYSICS_FRICTION);
		brickInfoMap.put("setFrictionBrick", brickInfo);

		brickInfo = new BrickInfo(SetGravityBrick.class.getSimpleName());
		brickInfo.addBrickFieldToMap("gravityX", BrickField.PHYSICS_GRAVITY_X);
		brickInfo.addBrickFieldToMap("gravityY", BrickField.PHYSICS_GRAVITY_Y);
		brickInfoMap.put("setGravityBrick", brickInfo);

		brickInfo = new BrickInfo(SetMassBrick.class.getSimpleName());
		brickInfo.addBrickFieldToMap("mass", BrickField.PHYSICS_MASS);
		brickInfoMap.put("setMassBrick", brickInfo);

		brickInfo = new BrickInfo(SetPhysicsObjectTypeBrick.class.getSimpleName());
		brickInfoMap.put("setPhysicsObjectTypeBrick", brickInfo);

		brickInfo = new BrickInfo(SetVelocityBrick.class.getSimpleName());
		brickInfo.addBrickFieldToMap("velocityX", BrickField.PHYSICS_VELOCITY_X);
		brickInfo.addBrickFieldToMap("velocityY", BrickField.PHYSICS_VELOCITY_Y);
		brickInfoMap.put("setVelocityBrick", brickInfo);

		brickInfo = new BrickInfo(TurnLeftSpeedBrick.class.getSimpleName());
		brickInfo.addBrickFieldToMap("turnLeftSpeed", BrickField.PHYSICS_TURN_LEFT_SPEED);
		brickInfoMap.put("turnLeftSpeedBrick", brickInfo);

		brickInfo = new BrickInfo(TurnRightSpeedBrick.class.getSimpleName());
		brickInfo.addBrickFieldToMap("turnRightSpeed", BrickField.PHYSICS_TURN_RIGHT_SPEED);
		brickInfoMap.put("turnRightSpeedBrick", brickInfo);

		brickInfo = new BrickInfo(PenDownBrick.class.getSimpleName());
		brickInfoMap.put("penDownBrick", brickInfo);

		brickInfo = new BrickInfo(PenUpBrick.class.getSimpleName());
		brickInfoMap.put("penUpBrick", brickInfo);

		brickInfo = new BrickInfo(StampBrick.class.getSimpleName());
		brickInfoMap.put("stampBrick", brickInfo);

		brickInfo = new BrickInfo(ClearBackgroundBrick.class.getSimpleName());
		brickInfoMap.put("clearBackgroundBrick", brickInfo);

		brickInfo = new BrickInfo(SetPenSizeBrick.class.getSimpleName());
		brickInfo.addBrickFieldToMap("penSize", BrickField.PEN_SIZE);
		brickInfoMap.put("setPenSizeBrick", brickInfo);

		brickInfo = new BrickInfo(SetPenColorBrick.class.getSimpleName());
		brickInfo.addBrickFieldToMap("penColor", BrickField.PEN_COLOR_RED);
		brickInfo.addBrickFieldToMap("penColor", BrickField.PEN_COLOR_GREEN);
		brickInfo.addBrickFieldToMap("penColor", BrickField.PEN_COLOR_BLUE);
		brickInfoMap.put("setPenColorBrick", brickInfo);

		brickInfo = new BrickInfo(WhenGamepadButtonBrick.class.getSimpleName());
		brickInfoMap.put("whenGamepadButtonBrick", brickInfo);

		brickInfo = new BrickInfo(SewUpBrick.class.getSimpleName());
		brickInfoMap.put("sewUpBrick", brickInfo);
	}

	private void initializeScriptInfoMap() {
		if (scriptInfoMap != null) {
			return;
		}

		scriptInfoMap = new HashMap<>();
		scriptInfoMap.put("startScript", StartScript.class.getSimpleName());
		scriptInfoMap.put("whenScript", WhenScript.class.getSimpleName());
		scriptInfoMap.put("whenConditionScript", WhenConditionScript.class.getSimpleName());
		scriptInfoMap.put("whenBackgroundChangesScript", WhenBackgroundChangesScript.class.getSimpleName());
		scriptInfoMap.put("broadcastScript", BroadcastScript.class.getSimpleName());
		scriptInfoMap.put("raspiInterruptScript", RaspiInterruptScript.class.getSimpleName());
		scriptInfoMap.put("whenNfcScript", WhenNfcScript.class.getSimpleName());
		scriptInfoMap.put("collisionScript", WhenBounceOffScript.class.getSimpleName());
		scriptInfoMap.put("whenTouchDownScript", WhenTouchDownScript.class.getSimpleName());
		scriptInfoMap.put("whenGamepadButtonScript", WhenGamepadButtonScript.class.getSimpleName());
		scriptInfoMap.put("userDefinedScript", UserDefinedScript.class.getSimpleName());
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
			deleteChildNodeByName(originalDocument.getElementsByTagName("header").item(0), "isPhiroProProject");
			deleteChildNodeByName(originalDocument, "brickList", "inUserBrick");

			renameScriptChildNodeByName(originalDocument, "CollisionScript", "receivedMessage",
					"spriteToBounceOffName");
			renameScriptChildNodeByName(originalDocument, "CollisionScript", "spriteToCollideWithName",
					"spriteToBounceOffName");
			renameScriptType(originalDocument, "CollisionScript", WhenBounceOffScript.class.getSimpleName());
			modifyScriptLists(originalDocument);
			modifyBrickLists(originalDocument);
			modifyVariables(originalDocument);
			checkReferences(originalDocument.getDocumentElement());

			saveDocument(originalDocument, file);
		}
	}

	private void renameScriptType(Document doc, String oldTypeName, String newTypeName) {
		for (Node script : getScriptsOfType(doc, oldTypeName)) {
			if (script instanceof Element) {
				((Element) script).setAttribute("type", newTypeName);
			}
		}
	}

	private void renameScriptChildNodeByName(Document originalDocument, String scriptName, String oldChildNodeName,
			String newChildNodeName) {
		for (Node script : getScriptsOfType(originalDocument, scriptName)) {
			Element message = findNodeByName(script, oldChildNodeName);
			if (message != null) {
				originalDocument.renameNode(message, null, newChildNodeName);
			}
		}
	}

	private List<Node> getScriptsOfType(Document doc, String type) {
		NodeList scripts = doc.getElementsByTagName("script");
		return getElementsFilteredByAttribute(scripts, "type", type);
	}

	private List<Node> getElementsFilteredByAttribute(NodeList unfiltered, String attributeName, String
			filterAttributeValue) {
		List<Node> filtered = new ArrayList<>();
		for (int i = 0; i < unfiltered.getLength(); i++) {
			Node node = unfiltered.item(i);
			String attributeValue = getAttributeOfNode(attributeName, node);
			if (attributeValue != null && attributeValue.equals(filterAttributeValue)) {
				filtered.add(node);
			}
		}
		return filtered;
	}

	private String getAttributeOfNode(String attributeName, Node node) {
		NamedNodeMap attributes = node.getAttributes();
		if (attributes != null) {
			return attributes.getNamedItem(attributeName).getNodeValue();
		}
		return null;
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
		Node variableNode = originalDocument.getElementsByTagName("variables").item(0);
		if (variableNode != null) {
			String variableNodeNamespaceURI = variableNode.getNamespaceURI();
			originalDocument.renameNode(variableNode, variableNodeNamespaceURI, "data");
		} else {
			Log.e(TAG, "XML-Update: No variables to modify.");
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
						Log.e(TAG, scriptNode.getNodeName() + ": Found no scripts to convert to new structure.\"");
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
						Log.e(TAG, brickNode.getNodeName() + ": Found no bricks to convert to new structure.");
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
				brickFieldMap = new HashMap<>();
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

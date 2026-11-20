/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2025 The Catrobat Team
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
package org.catrobat.catroid.test;

import android.util.Log;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.ui.fragment.CategoryBricksFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import androidx.test.platform.app.InstrumentationRegistry;
import dalvik.system.DexFile;

import static org.junit.Assert.assertEquals;
import static org.junit.Assume.assumeTrue;

@RunWith(Parameterized.class)
public class BricksHelpUrlTest {
	public static final String TAG = BricksHelpUrlTest.class.getSimpleName();
	public static Map<String, String> brickToGermanHelpUrlMapping;
	public static Map<String, String> brickToEnglishHelpUrlMapping;

	static {
		brickToGermanHelpUrlMapping = new HashMap<>();
		brickToEnglishHelpUrlMapping = new HashMap<>();
		// Ereignisse
		brickToGermanHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.WhenConditionBrick",
				"https://catrobat.org/docs/brick-dokumentation-de/ereignisse/wenn-wahr-wird");
		brickToGermanHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.WhenBrick",
				"https://catrobat.org/docs/brick-dokumentation-de/ereignisse/wenn-angetippt");
		brickToGermanHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.WhenTouchDownBrick",
				"https://catrobat.org/docs/brick-dokumentation-de/ereignisse/wenn-der-bildschirm-beruhrt-wird");
		brickToGermanHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.WhenBackgroundChangesBrick",
				"https://catrobat.org/docs/brick-dokumentation-de/ereignisse/wenn-der-hintergrund-wechselt-zu");
		brickToGermanHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.BroadcastReceiverBrick",
				"https://catrobat.org/docs/brick-dokumentation-de/ereignisse/wenn-du-empfangst");
		brickToGermanHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.WhenClonedBrick",
				"https://catrobat.org/docs/brick-dokumentation-de/ereignisse/als-klon-starte");
		brickToGermanHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.WhenStartedBrick",
				"https://catrobat.org/docs/brick-dokumentation-de/ereignisse/wenn-szene-startet");
		// Event
		brickToEnglishHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.WhenConditionBrick",
				"https://catrobat.org/docs/brickdocumentation/event/when-1");
		brickToEnglishHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.WhenBrick",
				"https://catrobat.org/docs/brickdocumentation/event/when-tapped");
		brickToEnglishHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.WhenTouchDownBrick",
				"https://catrobat.org/docs/brickdocumentation/event/when-stage-is-tapped");
		brickToEnglishHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.WhenBackgroundChangesBrick",
				"https://catrobat.org/docs/brickdocumentation/event/when-background-changes-to");
		brickToEnglishHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.BroadcastReceiverBrick",
				"https://catrobat.org/docs/brickdocumentation/event/when-you-receive");
		brickToEnglishHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.WhenClonedBrick",
				"https://catrobat.org/docs/brickdocumentation/event/when-you-start-as-a-clone");
		brickToEnglishHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.WhenStartedBrick",
				"https://catrobat.org/docs/brickdocumentation/event/when-scene-starts");
		// Stuerung
		brickToGermanHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.BroadcastBrick",
				"https://catrobat.org/docs/brick-dokumentation-de/steuerung/verschicke-an-alle");
		brickToGermanHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.BroadcastWaitBrick",
				"https://catrobat.org/docs/brick-dokumentation-de/steuerung/verschicke-und-warte");
		brickToGermanHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.SceneTransitionBrick",
				"https://catrobat.org/docs/brick-dokumentation-de/steuerung/szene-fortsetzen");
		brickToGermanHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.CloneBrick",
				"https://catrobat.org/docs/brick-dokumentation-de/steuerung/erzeuge-klon-von");
		brickToGermanHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.DeleteThisCloneBrick",
				"https://catrobat.org/docs/brick-dokumentation-de/steuerung/losche-diesen-klon");
		brickToGermanHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.ExitStageBrick",
				"https://catrobat.org/docs/brick-dokumentation-de/steuerung/stufe-beenden");
		brickToGermanHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.ForItemInUserListBrick",
				"https://catrobat.org/docs/brick-dokumentation-de/steuerung/fur-jeden-wert-von-im");
		brickToGermanHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.ForVariableFromToBrick",
				"https://catrobat.org/docs/brick-dokumentation-de/steuerung/fur-werte-von-auf-im");
		brickToGermanHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.ForeverBrick",
				"https://catrobat.org/docs/brick-dokumentation-de/steuerung/wiederhole"
						+ "-fortlaufend");
		brickToGermanHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.IfLogicBeginBrick",
				"https://catrobat.org/docs/brick-dokumentation-de/steuerung/wenn-wahr-ist-dann"
						+ "-sonst");
		brickToGermanHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.IfThenLogicBeginBrick",
				"https://catrobat.org/docs/brick-dokumentation-de/steuerung/wenn-wahr-ist-dann");
		brickToGermanHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.NoteBrick",
				"https://catrobat.org/docs/brick-dokumentation-de/steuerung/notiz");
		brickToGermanHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.RepeatBrick",
				"https://catrobat.org/docs/brick-dokumentation-de/steuerung/wiederhole-mal");
		brickToGermanHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.RepeatUntilBrick",
				"https://catrobat.org/docs/brick-dokumentation-de/steuerung/wiederhole-bis-wahr-ist");
		brickToGermanHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.SceneStartBrick",
				"https://catrobat.org/docs/brick-dokumentation-de/steuerung/beginne-szene");
		brickToGermanHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.StopScriptBrick",
				"https://catrobat.org/docs/brick-dokumentation-de/steuerung/stoppe");
		brickToGermanHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.WaitBrick",
				"https://catrobat.org/docs/brick-dokumentation-de/steuerung/warte-sekunde");
		brickToGermanHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.WaitUntilBrick",
				"https://catrobat.org/docs/brick-dokumentation-de/steuerung/warte-bis-wahr-ist");
		brickToGermanHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.WaitTillIdleBrick",
				"https://catrobat.org/docs/brick-dokumentation-de/steuerung/warte-bis-alle-anderen-skripte-gestoppt");
		// Control
		brickToEnglishHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.BroadcastBrick",
				"https://catrobat.org/docs/brickdocumentation/control/broadcast");
		brickToEnglishHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.BroadcastWaitBrick",
				"https://catrobat.org/docs/brickdocumentation/control/broadcast-and-wait");
		brickToEnglishHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.SceneTransitionBrick",
				"https://catrobat.org/docs/brickdocumentation/control/continue-scene");
		brickToEnglishHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.CloneBrick",
				"https://catrobat.org/docs/brickdocumentation/control/create-clone-of");
		brickToEnglishHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.DeleteThisCloneBrick",
				"https://catrobat.org/docs/brickdocumentation/control/delete-this-clone");
		brickToEnglishHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.ExitStageBrick",
				"https://catrobat.org/docs/brickdocumentation/control/finish-stage");
		brickToEnglishHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.ForItemInUserListBrick",
				"https://catrobat.org/docs/brickdocumentation/control/for-each-value-from-in");
		brickToEnglishHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.ForVariableFromToBrick",
				"https://catrobat.org/docs/brickdocumentation/control/for-values-from-1-to-10-in");
		brickToEnglishHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.ForeverBrick",
				"https://catrobat.org/docs/brickdocumentation/control/forever");
		brickToEnglishHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.IfLogicBeginBrick",
				"https://catrobat.org/docs/brickdocumentation/control/if-1");
		brickToEnglishHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.IfThenLogicBeginBrick",
				"https://catrobat.org/docs/brickdocumentation/control/if-1-2");
		brickToEnglishHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.NoteBrick",
				"https://catrobat.org/docs/brickdocumentation/control/note-add-comment-here");
		brickToEnglishHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.RepeatBrick",
				"https://catrobat.org/docs/brickdocumentation/control/repeat-10-times");
		brickToEnglishHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.RepeatUntilBrick",
				"https://catrobat.org/docs/brickdocumentation/control/repeat-until-1");
		brickToEnglishHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.SceneStartBrick",
				"https://catrobat.org/docs/brickdocumentation/control/start-scene");
		brickToEnglishHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.StopScriptBrick",
				"https://catrobat.org/docs/brickdocumentation/control/stop-this-script");
		brickToEnglishHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.WaitBrick",
				"https://catrobat.org/docs/brickdocumentation/control/wait-1-second");
		brickToEnglishHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.WaitUntilBrick",
				"https://catrobat.org/docs/brickdocumentation/control/wait-until-1");
		brickToEnglishHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.WaitTillIdleBrick",
				"https://catrobat.org/docs/brickdocumentation/control/wait-until-all-other-scripts-have-stopped");
		// Bewegung
		brickToGermanHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.ChangeXByNBrick",
				"https://catrobat.org/docs/brick-dokumentation-de/bewegung/andere-x-um");
		brickToGermanHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.ChangeYByNBrick",
				"https://catrobat.org/docs/brick-dokumentation-de/bewegung/andere-y-um");
		brickToGermanHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.GlideToBrick",
				"https://catrobat.org/docs/brick-dokumentation-de/bewegung/gleite-sekunde-zu-x-y");
		brickToGermanHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.GoNStepsBackBrick",
				"https://catrobat.org/docs/brick-dokumentation-de/bewegung/gehe-nach-hinten-ebene");
		brickToGermanHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.GoToBrick",
				"https://catrobat.org/docs/brick-dokumentation-de/bewegung/gehe-zu");
		brickToGermanHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.ComeToFrontBrick",
				"https://catrobat.org/docs/brick-dokumentation-de/bewegung/gehe-nach-vorne");
		brickToGermanHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.IfOnEdgeBounceBrick",
				"https://catrobat.org/docs/brick-dokumentation-de/bewegung/pralle-vom-rand-ab");
		brickToGermanHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.MoveNStepsBrick",
				"https://catrobat.org/docs/brick-dokumentation-de/bewegung/verschiebe-um-schritte");
		brickToGermanHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.PlaceAtBrick",
				"https://catrobat.org/docs/brick-dokumentation-de/bewegung/setze-an-position-x-y");
		brickToGermanHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.PointInDirectionBrick",
				"https://catrobat.org/docs/brick-dokumentation-de/bewegung/zeige-in-richtung");
		brickToGermanHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.PointToBrick",
				"https://catrobat.org/docs/brick-dokumentation-de/bewegung/zeige-auf");
		brickToGermanHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.SetRotationStyleBrick",
				"https://catrobat.org/docs/brick-dokumentation-de/bewegung/setze-rotations-stil");
		brickToGermanHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.SetVelocityBrick",
				"https://catrobat.org/docs/brick-dokumentation-de/bewegung/setze-geschwindigkeit-auf-x-y-schritt-sekunde");
		brickToGermanHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.SetXBrick",
				"https://catrobat.org/docs/brick-dokumentation-de/bewegung/setze-x-auf");
		brickToGermanHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.SetYBrick",
				"https://catrobat.org/docs/brick-dokumentation-de/bewegung/setze-y-auf");
		brickToGermanHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.SetPhysicsObjectTypeBrick",
				"https://catrobat.org/docs/brick-dokumentation-de/bewegung/setze-bewegungstyp-auf");
		brickToGermanHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.TurnLeftSpeedBrick",
				"https://catrobat.org/docs/brick-dokumentation-de/bewegung/nach-links-drehen-grad-sekunde");
		brickToGermanHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.TurnRightSpeedBrick",
				"https://catrobat.org/docs/brick-dokumentation-de/bewegung/nach-rechts-drehen-grad-sekunde");
		brickToGermanHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.TurnLeftBrick",
				"https://catrobat.org/docs/brick-dokumentation-de/bewegung/drehe-links-grad");
		brickToGermanHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.TurnRightBrick",
				"https://catrobat.org/docs/brick-dokumentation-de/bewegung/drehe-rechts-grad");
		brickToGermanHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.VibrationBrick",
				"https://catrobat.org/docs/brick-dokumentation-de/bewegung/vibriere-fur-sekunde");
		brickToGermanHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.WhenBounceOffBrick",
				"https://catrobat.org/docs/brick-dokumentation-de/bewegung/wenn-du-abprallst-von");
		brickToGermanHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.SetBounceBrick",
				"https://catrobat.org/docs/brick-dokumentation-de/bewegung/setze-aufprallfaktor-auf");
		brickToGermanHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.SetFrictionBrick",
				"https://catrobat.org/docs/brick-dokumentation-de/bewegung/setze-gravitation-fur-alle-figuren-und-objekte");
		brickToGermanHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.SetMassBrick",
				"https://catrobat.org/docs/brick-dokumentation-de/bewegung/setze-masse-auf-kilogramm");
		// Motion
		brickToEnglishHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.ChangeXByNBrick",
				"https://catrobat.org/docs/brickdocumentation/motion/change-x-by");
		brickToEnglishHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.ChangeYByNBrick",
				"https://catrobat.org/docs/brickdocumentation/motion/change-y-by");
		brickToEnglishHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.GlideToBrick",
				"https://catrobat.org/docs/brickdocumentation/motion/glide-second-to");
		brickToEnglishHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.GoNStepsBackBrick",
				"https://catrobat.org/docs/brickdocumentation/motion/go-back-layer");
		brickToEnglishHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.GoToBrick",
				"https://catrobat.org/docs/brickdocumentation/motion/go-to");
		brickToEnglishHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.ComeToFrontBrick",
				"https://catrobat.org/docs/brickdocumentation/motion/go-to-front");
		brickToEnglishHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.IfOnEdgeBounceBrick",
				"https://catrobat.org/docs/brickdocumentation/motion/if-on-edge-bounce");
		brickToEnglishHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.MoveNStepsBrick",
				"https://catrobat.org/docs/brickdocumentation/motion/move-steps");
		brickToEnglishHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.PlaceAtBrick",
				"https://catrobat.org/docs/brickdocumentation/motion/place-at");
		brickToEnglishHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.PointInDirectionBrick",
				"https://catrobat.org/docs/brickdocumentation/motion/point-in-direction-degrees");
		brickToEnglishHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.PointToBrick",
				"https://catrobat.org/docs/brickdocumentation/motion/point-towards");
		brickToEnglishHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.SetRotationStyleBrick",
				"https://catrobat.org/docs/brickdocumentation/motion/set-rotation-style");
		brickToEnglishHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.SetVelocityBrick",
				"https://catrobat.org/docs/brickdocumentation/motion/set-velocity-to");
		brickToEnglishHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.SetXBrick",
				"https://catrobat.org/docs/brickdocumentation/motion/set-x-to");
		brickToEnglishHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.SetYBrick",
				"https://catrobat.org/docs/brickdocumentation/motion/set-y-to");
		brickToEnglishHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.SetPhysicsObjectTypeBrick",
				"https://catrobat.org/docs/brickdocumentation/motion/set-your-motion-type-to");
		brickToEnglishHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.TurnLeftSpeedBrick",
				"https://catrobat.org/docs/brickdocumentation/motion/spin-left-degrees-second");
		brickToEnglishHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.TurnRightSpeedBrick",
				"https://catrobat.org/docs/brickdocumentation/motion/spin-right-degrees-second");
		brickToEnglishHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.TurnLeftBrick",
				"https://catrobat.org/docs/brickdocumentation/motion/turn-left-degrees");
		brickToEnglishHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.TurnRightBrick",
				"https://catrobat.org/docs/brickdocumentation/motion/turn-right-degrees");
		brickToEnglishHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.VibrationBrick",
				"https://catrobat.org/docs/brickdocumentation/motion/vibrate-for-second");
		brickToEnglishHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.WhenBounceOffBrick",
				"https://catrobat.org/docs/brickdocumentation/motion/when-you-bounce-off");
		brickToEnglishHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.SetBounceBrick",
				"https://catrobat.org/docs/brickdocumentation/motion/set-bounce-factor-to");
		brickToEnglishHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.SetGravityBrick",
				"https://catrobat.org/docs/brickdocumentation/motion/set-gravity-for-all-actors-and-objects-to");
		brickToEnglishHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.SetMassBrick",
				"https://catrobat.org/docs/brickdocumentation/motion/set-mass-to-kilogram");
		// Klang
		brickToGermanHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.ChangeVolumeByNBrick",
				"https://catrobat.org/docs/brick-dokumentation-de/klang/andere-lautstarke-um");
		brickToGermanHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.PlayDrumForBeatsBrick",
				"https://catrobat.org/docs/brick-dokumentation-de/klang/trommel-abspielen-fur-schlage");
		brickToGermanHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.PlayNoteForBeatsBrick",
				"https://catrobat.org/docs/brick-dokumentation-de/klang/musiknote-abspielen-fur-sekunden");
		brickToGermanHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.SetInstrumentBrick",
				"https://catrobat.org/docs/brick-dokumentation-de/klang/setze-instrument-auf");
		brickToGermanHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.SetVolumeToBrick",
				"https://catrobat.org/docs/brick-dokumentation-de/klang/setze-lautstarke-auf");
		brickToGermanHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.PlaySoundBrick",
				"https://catrobat.org/docs/brick-dokumentation-de/klang/klang-starten");
		brickToGermanHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.PlaySoundAndWaitBrick",
				"https://catrobat.org/docs/brick-dokumentation-de/klang/starte-klang-und-warte");
		brickToGermanHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.StopSoundBrick",
				"https://catrobat.org/docs/brick-dokumentation-de/klang/stoppe-klang");
		brickToGermanHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.StopAllSoundsBrick",
				"https://catrobat.org/docs/brick-dokumentation-de/klang/alle-klange-stoppen");
		brickToGermanHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.ChangeTempoByNBrick",
				"https://catrobat.org/docs/brick-dokumentation-de/klang/setze-tempo-auf");
		brickToGermanHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.PauseForBeatsBrick",
				"https://catrobat.org/docs/brick-dokumentation-de/klang/pausiere-fur-schlage");
		brickToGermanHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.SetTempoBrick",
				"https://catrobat.org/docs/brick-dokumentation-de/klang/setze-tempo-auf");
		// sound
		brickToEnglishHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.ChangeVolumeByNBrick",
				"https://catrobat.org/docs/brickdocumentation/sound/change-volume-by");
		brickToEnglishHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.PlayDrumForBeatsBrick",
				"https://catrobat.org/docs/brickdocumentation/sound/play-drum-for-beats");
		brickToEnglishHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.PlayNoteForBeatsBrick",
				"https://catrobat.org/docs/brickdocumentation/sound/play-note-for");
		brickToEnglishHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.SetInstrumentBrick",
				"https://catrobat.org/docs/brickdocumentation/sound/set-instrument-to");
		brickToEnglishHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.SetVolumeToBrick",
				"https://catrobat.org/docs/brickdocumentation/sound/set-volume-to");
		brickToEnglishHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.PlaySoundBrick",
				"https://catrobat.org/docs/brickdocumentation/sound/start-sound");
		brickToEnglishHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.PlaySoundAndWaitBrick",
				"https://catrobat.org/docs/brickdocumentation/sound/start-sound-and-wait");
		brickToEnglishHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.StopSoundBrick",
				"https://catrobat.org/docs/brickdocumentation/sound/stop-sound");
		brickToEnglishHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.StopAllSoundsBrick",
				"https://catrobat.org/docs/brickdocumentation/sound/stop-all-sounds");
		brickToEnglishHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.ChangeTempoByNBrick",
				"https://catrobat.org/docs/brickdocumentation/sound/change-tempo-by");
		brickToEnglishHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.PauseForBeatsBrick",
				"https://catrobat.org/docs/brickdocumentation/sound/pause-for-beats");
		brickToEnglishHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.SetTempoBrick",
				"https://catrobat.org/docs/brickdocumentation/sound/set-tempo-to");
		// Aussehn
		brickToGermanHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.AskBrick",
				"https://catrobat.org/docs/brick-dokumentation-de/aussehen/frage-und-speichere-das-eingegebene-in");
		brickToGermanHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.SetCameraFocusPointBrick",
				"https://catrobat.org/docs/brick-dokumentation-de/aussehen/fokuspunkt-werden-mit-horizontal-und-vertikale-flexibilitat");
		brickToGermanHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.ChangeBrightnessByNBrick",
				"https://catrobat.org/docs/brick-dokumentation-de/aussehen/andere-helligkeit-um");
		brickToGermanHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.ChangeColorByNBrick",
				"https://catrobat.org/docs/brick-dokumentation-de/aussehen/andere-farbe-um");
		brickToGermanHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.ChangeSizeByNBrick",
				"https://catrobat.org/docs/brick-dokumentation-de/aussehen/andere-grose-um");
		brickToGermanHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.ChangeTransparencyByNBrick",
				"https://catrobat.org/docs/brick-dokumentation-de/aussehen/andere-transparenz-um");
		brickToGermanHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.ClearGraphicEffectBrick",
				"https://catrobat.org/docs/brick-dokumentation-de/aussehen/grafikeffekt-zurucksetzen");
		brickToGermanHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.CopyLookBrick",
				"https://catrobat.org/docs/brick-dokumentation-de/aussehen/look-kopieren-und-namen-geben");
		brickToGermanHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.DeleteLookBrick",
				"https://catrobat.org/docs/brick-dokumentation-de/aussehen/losche-aussehen");
		brickToGermanHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.EditLookBrick",
				"https://catrobat.org/docs/brick-dokumentation-de/aussehen/aussehen-bearbeiten");
		brickToGermanHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.FadeParticleEffectBrick",
				"https://catrobat.org/docs/brick-dokumentation-de/aussehen/blende-partikeleffekt");
		brickToGermanHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.LookRequestBrick",
				"https://catrobat.org/docs/brick-dokumentation-de/aussehen/bild-abrufen-von");
		brickToGermanHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.HideBrick",
				"https://catrobat.org/docs/brick-dokumentation-de/aussehen/verbergen");
		brickToGermanHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.NextLookBrick",
				"https://catrobat.org/docs/brick-dokumentation-de/aussehen/nachstes-aussehen");
		brickToGermanHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.PaintNewLookBrick",
				"https://catrobat.org/docs/brick-dokumentation-de/aussehen/neuen-look-malen");
		brickToGermanHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.PreviousLookBrick",
				"https://catrobat.org/docs/brick-dokumentation-de/aussehen/voriges-aussehen");
		brickToGermanHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.SayBubbleBrick",
				"https://catrobat.org/docs/brick-dokumentation-de/aussehen/sage");
		brickToGermanHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.SayForBubbleBrick",
				"https://catrobat.org/docs/brick-dokumentation-de/aussehen/sage-fur-sekunde");
		brickToGermanHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.SetBackgroundBrick",
				"https://catrobat.org/docs/brick-dokumentation-de/aussehen/setze-hintergrund");
		brickToGermanHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.SetBackgroundAndWaitBrick",
				"https://catrobat.org/docs/brick-dokumentation-de/aussehen/setze-hintergrund-und-warte");
		brickToGermanHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.SetBackgroundByIndexBrick",
				"https://catrobat.org/docs/brick-dokumentation-de/aussehen/setze-hintergrund-auf-nummer");
		brickToGermanHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.SetBrightnessBrick",
				"https://catrobat.org/docs/brick-dokumentation-de/aussehen/setze-helligkeit-auf");
		brickToGermanHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.SetColorBrick",
				"https://catrobat.org/docs/brick-dokumentation-de/aussehen/setze-farbe-auf");
		brickToGermanHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.SetParticleColorBrick",
				"https://catrobat.org/docs/brick-dokumentation-de/aussehen/setze-partikelfarbe-auf");
		brickToGermanHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.SetSizeToBrick",
				"https://catrobat.org/docs/brick-dokumentation-de/aussehen/setze-grose-auf");
		brickToGermanHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.SetTransparencyBrick",
				"https://catrobat.org/docs/brick-dokumentation-de/aussehen/setze-transparenz-auf");
		brickToGermanHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.ShowBrick",
				"https://catrobat.org/docs/brick-dokumentation-de/aussehen/anzeigen");
		brickToGermanHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.SetLookBrick",
				"https://catrobat.org/docs/brick-dokumentation-de/aussehen/setze-aussehen");
		brickToGermanHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.ThinkForBubbleBrick",
				"https://catrobat.org/docs/brick-dokumentation-de/aussehen/denke-fur-sekunde");
		brickToGermanHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.SetLookByIndexBrick",
				"https://catrobat.org/docs/brick-dokumentation-de/aussehen/setze-aussehen-auf-nummer");
		brickToGermanHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.ThinkBubbleBrick",
				"https://catrobat.org/docs/brick-dokumentation-de/aussehen/denke");
		brickToGermanHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.CameraBrick",
				"https://catrobat.org/docs/brick-dokumentation-de/aussehen/schalte-kamera");
		brickToGermanHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.ParticleEffectAdditivityBrick",
				"https://catrobat.org/docs/brick-dokumentation-de/aussehen/schalte-blitz");
		brickToGermanHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.FlashBrick",
				"https://catrobat.org/docs/brick-dokumentation-de/aussehen/schalte-partikeleffekt-additivitat");
		brickToGermanHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.ChooseCameraBrick",
				"https://catrobat.org/docs/brick-dokumentation-de/aussehen/verwende-kamera");
		brickToGermanHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.SetBackgroundByIndexAndWaitBrick",
				"https://catrobat.org/docs/brick-dokumentation-de/aussehen/setze-hintergrund-auf-nummer-und-warte");
		// Looks
		brickToEnglishHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.AskBrick",
				"https://catrobat.org/docs/brickdocumentation/looks/ask-and-store-written-answer"
						+ "-in");
		brickToEnglishHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.SetCameraFocusPointBrick",
				"https://catrobat.org/docs/brickdocumentation/looks/become-focus-point-with-horizontal-and-vertical-flexibility");
		brickToEnglishHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.ChangeBrightnessByNBrick",
				"https://catrobat.org/docs/brickdocumentation/looks/change-brightness-by");
		brickToEnglishHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.ChangeColorByNBrick",
				"https://catrobat.org/docs/brickdocumentation/looks/change-colour-by");
		brickToEnglishHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.ChangeSizeByNBrick",
				"https://catrobat.org/docs/brickdocumentation/looks/change-size-by");
		brickToEnglishHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.ChangeTransparencyByNBrick",
				"https://catrobat.org/docs/brickdocumentation/looks/change-transparency-by");
		brickToEnglishHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.ClearGraphicEffectBrick",
				"https://catrobat.org/docs/brickdocumentation/looks/clear-graphic-effects");
		brickToEnglishHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.CopyLookBrick",
				"https://catrobat.org/docs/brickdocumentation/looks/copy-look-and-name-it");
		brickToEnglishHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.DeleteLookBrick",
				"https://catrobat.org/docs/brickdocumentation/looks/delete-look");
		brickToEnglishHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.EditLookBrick",
				"https://catrobat.org/docs/brickdocumentation/looks/edit-look");
		brickToEnglishHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.FadeParticleEffectBrick",
				"https://catrobat.org/docs/brickdocumentation/looks/fade-particle-effect");
		brickToEnglishHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.LookRequestBrick",
				"https://catrobat.org/docs/brickdocumentation/looks/get-image-from-and-use-as-current-look");
		brickToEnglishHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.HideBrick",
				"https://catrobat.org/docs/brickdocumentation/looks/hide");
		brickToEnglishHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.NextLookBrick",
				"https://catrobat.org/docs/brickdocumentation/looks/next-look");
		brickToEnglishHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.PaintNewLookBrick",
				"https://catrobat.org/docs/brickdocumentation/looks/paint-new-look");
		brickToEnglishHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.PreviousLookBrick",
				"https://catrobat.org/docs/brickdocumentation/looks/previous-look");
		brickToEnglishHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.SayBubbleBrick",
				"https://catrobat.org/docs/brickdocumentation/looks/say");
		brickToEnglishHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.SayForBubbleBrick",
				"https://catrobat.org/docs/brickdocumentation/looks/say-for-second");
		brickToEnglishHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.SetBackgroundBrick",
				"https://catrobat.org/docs/brickdocumentation/looks/set-background");
		brickToEnglishHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.SetBackgroundAndWaitBrick",
				"https://catrobat.org/docs/brickdocumentation/looks/set-background-and-wait");
		brickToEnglishHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.SetBackgroundByIndexBrick",
				"https://catrobat.org/docs/brickdocumentation/looks/set-background-to-number");
		brickToEnglishHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.SetBrightnessBrick",
				"https://catrobat.org/docs/brickdocumentation/looks/set-brightness-to");
		brickToEnglishHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.SetColorBrick",
				"https://catrobat.org/docs/brickdocumentation/looks/set-colour-to");
		brickToEnglishHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.SetParticleColorBrick",
				"https://catrobat.org/docs/brickdocumentation/looks/set-particle-color-to");
		brickToEnglishHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.SetSizeToBrick",
				"https://catrobat.org/docs/brickdocumentation/looks/set-size-to");
		brickToEnglishHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.SetTransparencyBrick",
				"https://catrobat.org/docs/brickdocumentation/looks/set-transparency-to");
		brickToEnglishHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.ShowBrick",
				"https://catrobat.org/docs/brickdocumentation/looks/show");
		brickToEnglishHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.SetLookBrick",
				"https://catrobat.org/docs/brickdocumentation/looks/switch-to-look");
		brickToEnglishHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.SetLookByIndexBrick",
				"https://catrobat.org/docs/brickdocumentation/looks/switch-to-look-with-number");
		brickToEnglishHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.ThinkForBubbleBrick",
				"https://catrobat.org/docs/brickdocumentation/looks/think-for-second");
		brickToEnglishHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.ThinkBubbleBrick",
				"https://catrobat.org/docs/brickdocumentation/looks/think");
		brickToEnglishHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.CameraBrick",
				"https://catrobat.org/docs/brickdocumentation/looks/turn-camera");
		brickToEnglishHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.ParticleEffectAdditivityBrick",
				"https://catrobat.org/docs/brickdocumentation/looks/turn-particle-effect-additivity");
		brickToEnglishHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.ChooseCameraBrick",
				"https://catrobat.org/docs/brickdocumentation/looks/use-camera");
		brickToEnglishHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.SetBackgroundByIndexAndWaitBrick",
				"https://catrobat.org/docs/brickdocumentation/looks/set-background-to-number-and-wait");
		// Malstift
		brickToGermanHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.ClearBackgroundBrick",
				"https://catrobat.org/docs/brick-dokumentation-de/malstift/wische-malspur-weg");
		brickToGermanHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.PenDownBrick",
				"https://catrobat.org/docs/brick-dokumentation-de/malstift/schalte-stift-ein");
		brickToGermanHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.PenUpBrick",
				"https://catrobat.org/docs/brick-dokumentation-de/malstift/schalte-stift-aus");
		brickToGermanHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.SetPenSizeBrick",
				"https://catrobat.org/docs/brick-dokumentation-de/malstift/setze-stiftdicke-auf");
		brickToGermanHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.StampBrick",
				"https://catrobat.org/docs/brick-dokumentation-de/malstift/stempel");
		brickToGermanHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.SetPenColorBrick",
				"https://catrobat.org/docs/brick-dokumentation-de/malstift/setze-stiftfarbe-auf-rot-grun-blau");
		// Pen
		brickToEnglishHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.ClearBackgroundBrick",
				"https://catrobat.org/docs/brickdocumentation/pen/clear");
		brickToEnglishHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.PenDownBrick",
				"https://catrobat.org/docs/brickdocumentation/pen/pen-down");
		brickToEnglishHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.PenUpBrick",
				"https://catrobat.org/docs/brickdocumentation/pen/pen-up");
		brickToEnglishHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.SetPenSizeBrick",
				"https://catrobat.org/docs/brickdocumentation/pen/set-pen-size-to");
		brickToEnglishHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.StampBrick",
				"https://catrobat.org/docs/brickdocumentation/pen/stamp");
		brickToEnglishHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.SetPenColorBrick",
				"https://catrobat.org/docs/brickdocumentation/pen/set-pen-colour-to-red-green-blue");
		// Daten
		brickToGermanHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.AddItemToUserListBrick",
				"https://catrobat.org/docs/brick-dokumentation-de/daten/hinzufugen-zur-liste");
		brickToGermanHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.ChangeVariableBrick",
				"https://catrobat.org/docs/brick-dokumentation-de/daten/andere-variable-um");
		brickToGermanHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.ClearUserListBrick",
				"https://catrobat.org/docs/brick-dokumentation-de/daten/alle-elemente-aus-der-liste-loschen");
		brickToGermanHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.DeleteItemOfUserListBrick",
				"https://catrobat.org/docs/brick-dokumentation-de/daten/elemente-aus-liste-loschen-auf-position");
		brickToGermanHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.HideTextBrick",
				"https://catrobat.org/docs/brick-dokumentation-de/daten/verstecke-variable");
		brickToGermanHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.InsertItemIntoUserListBrick",
				"https://catrobat.org/docs/brick-dokumentation-de/daten/einfugen-in-liste-auf-position");
		brickToGermanHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.ReadVariableFromFileBrick",
				"https://catrobat.org/docs/brick-dokumentation-de/daten/lese-variable-aus-datei-und");
		brickToGermanHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.ReadListFromDeviceBrick",
				"https://catrobat.org/docs/brick-dokumentation-de/daten/liste-von-gerat-lesen");
		brickToGermanHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.ReadVariableFromDeviceBrick",
				"https://catrobat.org/docs/brick-dokumentation-de/daten/variable-von-gerat-lesen");
		brickToGermanHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.ReplaceItemInUserListBrick",
				"https://catrobat.org/docs/brick-dokumentation-de/daten/elemente-in-liste-ersetzen-auf-position-im");
		brickToGermanHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.WebRequestBrick",
				"https://catrobat.org/docs/brick-dokumentation-de/daten/webanfrage-senden-an-und-speichere-antwort-in");
		brickToGermanHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.SetVariableBrick",
				"https://catrobat.org/docs/brick-dokumentation-de/daten/setze-variable-auf");
		brickToGermanHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.ShowTextBrick",
				"https://catrobat.org/docs/brick-dokumentation-de/daten/zeige-variable-bei-x-y");
		brickToGermanHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.ShowTextColorSizeAlignmentBrick",
				"https://catrobat.org/docs/brick-dokumentation-de/daten/zeige-variable-bei-x-y-grose-farbe");
		brickToGermanHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.StoreCSVIntoUserListBrick",
				"https://catrobat.org/docs/brick-dokumentation-de/daten/speichere-spalte-von-den-durch-kommas-getrennten-werten-in-liste");
		brickToGermanHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.WriteVariableToFileBrick",
				"https://catrobat.org/docs/brick-dokumentation-de/daten/schreibe-variable-zur-datei");
		brickToGermanHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.WriteListOnDeviceBrick",
				"https://catrobat.org/docs/brick-dokumentation-de/daten/liste-auf-gerat-speichern");
		brickToGermanHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.WriteVariableOnDeviceBrick",
				"https://catrobat.org/docs/brick-dokumentation-de/daten/variable-auf-gerat-schreiben");
		// Data
		brickToEnglishHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.AddItemToUserListBrick",
				"https://catrobat.org/docs/brickdocumentation/data/add-to-list");
		brickToEnglishHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.ChangeVariableBrick",
				"https://catrobat.org/docs/brickdocumentation/data/change-variable-by");
		brickToEnglishHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.ClearUserListBrick",
				"https://catrobat.org/docs/brickdocumentation/data/delete-all-items-from-list");
		brickToEnglishHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.DeleteItemOfUserListBrick",
				"https://catrobat.org/docs/brickdocumentation/data/delete-item-from-list-at-position");
		brickToEnglishHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.HideTextBrick",
				"https://catrobat.org/docs/brickdocumentation/data/hide-variable");
		brickToEnglishHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.InsertItemIntoUserListBrick",
				"https://catrobat.org/docs/brickdocumentation/data/insert-into-list-at-position");
		brickToEnglishHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.ReadVariableFromFileBrick",
				"https://catrobat.org/docs/brickdocumentation/data/read-variable-from-file-and");
		brickToEnglishHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.ReadListFromDeviceBrick",
				"https://catrobat.org/docs/brickdocumentation/data/read-list-from-device");
		brickToEnglishHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.ReadVariableFromDeviceBrick",
				"https://catrobat.org/docs/brickdocumentation/data/read-variable-from-device");
		brickToEnglishHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.ReplaceItemInUserListBrick",
				"https://catrobat.org/docs/brickdocumentation/data/replace-item-in-list-at-position-with");
		brickToEnglishHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.WebRequestBrick",
				"https://catrobat.org/docs/brickdocumentation/data/send-web-request-to-and-store-answer-in");
		brickToEnglishHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.SetVariableBrick",
				"https://catrobat.org/docs/brickdocumentation/data/set-variable-to");
		brickToEnglishHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.ShowTextBrick",
				"https://catrobat.org/docs/brickdocumentation/data/Show variable … at X:… Y:…");
		brickToEnglishHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.ShowTextColorSizeAlignmentBrick",
				"https://catrobat.org/docs/brickdocumentation/data/show-variable-at-x-y-size-colour-aligned");
		brickToEnglishHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.StoreCSVIntoUserListBrick",
				"https://catrobat.org/docs/brickdocumentation/data/store-column-of-the-comma-separated-values");
		brickToEnglishHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.WriteVariableToFileBrick",
				"https://catrobat.org/docs/brickdocumentation/data/write-variable-to-file");
		brickToEnglishHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.WriteListOnDeviceBrick",
				"https://catrobat.org/docs/brickdocumentation/data/write-list-on-device");
		brickToEnglishHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.WriteVariableOnDeviceBrick",
				"https://catrobat.org/docs/brickdocumentation/data/write-variable-on-device");
		// Sticken
		brickToGermanHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.SetThreadColorBrick",
				"https://catrobat.org/docs/brick-dokumentation-de/sticken/setze-auf-farbe");
		brickToGermanHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.ZigZagStitchBrick",
				"https://catrobat.org/docs/brick-dokumentation-de/sticken/starte-zickzack-stich-mit-lange-und-breite");
		brickToGermanHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.RunningStitchBrick",
				"https://catrobat.org/docs/brick-dokumentation-de/sticken/starte-laufstich-mit-lange");
		brickToGermanHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.TripleStitchBrick",
				"https://catrobat.org/docs/brick-dokumentation-de/sticken/starte-dreifachstich-mit-lange");
		brickToGermanHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.SewUpBrick",
				"https://catrobat.org/docs/brick-dokumentation-de/sticken/vernahen");
		brickToGermanHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.StopRunningStitchBrick",
				"https://catrobat.org/docs/brick-dokumentation-de/sticken/beende-aktuellen-stich");
		brickToGermanHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.WriteEmbroideryToFileBrick",
				"https://catrobat.org/docs/brick-dokumentation-de/sticken/schreibe-stickdaten-in-die-datei");
		brickToGermanHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.StitchBrick",
				"https://catrobat.org/docs/brick-dokumentation-de/sticken/stich");
		// Embroidery
		brickToEnglishHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.SetThreadColorBrick",
				"https://catrobat.org/docs/brickdocumentation/embroidery/set-thread-color-to");
		brickToEnglishHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.ZigZagStitchBrick",
				"https://catrobat.org/docs/brickdocumentation/embroidery/start-zigzag-stitch-with-lenght-and-width");
		brickToEnglishHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.RunningStitchBrick",
				"https://catrobat.org/docs/brickdocumentation/embroidery/start-running-stitch-with-length");
		brickToEnglishHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.TripleStitchBrick",
				"https://catrobat.org/docs/brickdocumentation/embroidery/start-triple-stitch-with-length");
		brickToEnglishHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.SewUpBrick",
				"https://catrobat.org/docs/brickdocumentation/embroidery/sew-up");
		brickToEnglishHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.WriteEmbroideryToFileBrick",
				"https://catrobat.org/docs/brickdocumentation/embroidery/write-embroidery-data-to-file");
		brickToEnglishHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.StitchBrick",
				"https://catrobat.org/docs/brickdocumentation/embroidery/stitch");
		// Plotten
		brickToGermanHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.SavePlotBrick",
				"https://catrobat.org/docs/brick-dokumentation-de/plotten/plot-als-svg-speichern");
		brickToGermanHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.StartPlotBrick",
				"https://catrobat.org/docs/brick-dokumentation-de/plotten/plotten-starten");
		brickToGermanHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.StopPlotBrick",
				"https://catrobat.org/docs/brick-dokumentation-de/plotten/plotten-beenden");
		// Plot
		brickToEnglishHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.SavePlotBrick",
				"https://catrobat.org/docs/brickdocumentation/plot/save-plot");
		brickToEnglishHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.StartPlotBrick",
				"https://catrobat.org/docs/brickdocumentation/plot/start-to-plot");
		brickToEnglishHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.StopPlotBrick",
				"https://catrobat.org/docs/brickdocumentation/plot/stop-to-plot");
	}

	@Parameterized.Parameters(name = "{0}")
	public static Iterable<Object[]> data() {
		List<Object[]> parameters = new ArrayList<>();
		Set<Class> brickClasses = getAllBrickClasses();

		brickClasses = removeAbstractClasses(brickClasses);
		brickClasses = removeInnerClasses(brickClasses);
		brickClasses = removeEndBrick(brickClasses);
		for (Class<?> brickClazz : brickClasses) {
			parameters.add(new Object[] {brickClazz.getName(), brickClazz});
		}

		return parameters;
	}

	@Parameterized.Parameter
	public String simpleName;

	@Parameterized.Parameter(1)
	public Class brickClass;

	private static Set<Class> getAllBrickClasses() {
		ArrayList<Class> classes = new ArrayList<>();
		try {
			String packageCodePath =
					InstrumentationRegistry.getInstrumentation().getTargetContext().getPackageCodePath();
			DexFile dexFile = new DexFile(packageCodePath);
			for (Enumeration<String> iter = dexFile.entries(); iter.hasMoreElements(); ) {
				String className = iter.nextElement();
				if (className.contains("org.catrobat.catroid.content.bricks") && className.endsWith(
						"Brick")) {
					classes.add(Class.forName(className));
				}
			}
		} catch (Exception e) {
			Log.e(TAG, e.getMessage(), e);
		}

		return new HashSet<>(classes);
	}

	@Before
	public void setUp() {
		ProjectManager.getInstance().setCurrentProject(
				new Project(InstrumentationRegistry.getInstrumentation().getTargetContext(), "empty"));
	}

	@Test
	public void testGermanBrickHelpUrl() throws IllegalAccessException, InstantiationException {
		assumeTrue(brickToGermanHelpUrlMapping.containsKey(simpleName));
		Brick brick = (Brick) brickClass.newInstance();
		int category = new CategoryBricksFactory().getBrickCategory(brick, false,
				InstrumentationRegistry.getInstrumentation().getTargetContext());
		String brickHelpUrl = brick.getHelpUrl(category, "de");
		assertEquals(brickToGermanHelpUrlMapping.get(simpleName), brickHelpUrl);
	}

	@Test
	public void testEnglishBrickHelpUrl() throws IllegalAccessException, InstantiationException {
		assumeTrue(brickToEnglishHelpUrlMapping.containsKey(simpleName));
		Brick brick = (Brick) brickClass.newInstance();
		int category = new CategoryBricksFactory().getBrickCategory(brick, false,
				InstrumentationRegistry.getInstrumentation().getTargetContext());
		String brickHelpUrl = brick.getHelpUrl(category, "en");
		assertEquals(brickToEnglishHelpUrlMapping.get(simpleName), brickHelpUrl);
	}

	private static Set<Class> removeAbstractClasses(Set<Class> classes) {
		Set<Class> filtered = new HashSet<>();

		for (Class clazz : classes) {
			boolean isAbstract = Modifier.isAbstract(clazz.getModifiers());
			if (!isAbstract) {
				filtered.add(clazz);
			}
		}
		return filtered;
	}

	private static Set<Class> removeInnerClasses(Set<Class> classes) {
		Set<Class> filtered = new HashSet<>();

		for (Class clazz : classes) {
			boolean isInnerClass = clazz.getEnclosingClass() != null;
			if (!isInnerClass) {
				filtered.add(clazz);
			}
		}
		return filtered;
	}

	private static Set<Class> removeEndBrick(Set<Class> classes) {
		Set<Class> filtered = new HashSet<>();

		for (Class clazz : classes) {
			if (!clazz.getName().contains("EndBrick")) {
				filtered.add(clazz);
			}
		}
		return filtered;
	}
}

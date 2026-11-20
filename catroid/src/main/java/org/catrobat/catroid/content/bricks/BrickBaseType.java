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
package org.catrobat.catroid.content.bricks;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Spinner;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.ui.recyclerview.fragment.ScriptFragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import java.util.HashMap;
import java.util.Map;
import java.util.Locale;


import androidx.annotation.CallSuper;
import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public abstract class BrickBaseType implements Brick {

	private static final long serialVersionUID = 1L;

	public transient View view;
	private transient CheckBox checkbox;

	protected transient Brick parent;

	protected boolean commentedOut;

	protected UUID brickId = UUID.randomUUID();

	@Override
	public boolean isCommentedOut() {
		return commentedOut;
	}

	@Override
	public void setCommentedOut(boolean commentedOut) {
		this.commentedOut = commentedOut;
	}

	@Nullable
	@Override
	public CheckBox getCheckBox() {
		return checkbox;
	}

	@Override
	public Brick clone() throws CloneNotSupportedException {
		BrickBaseType clone = (BrickBaseType) super.clone();
		clone.view = null;
		clone.checkbox = null;
		clone.parent = null;
		clone.commentedOut = commentedOut;
		clone.brickId = UUID.randomUUID();
		return clone;
	}

	@Override
	public void addRequiredResources(final ResourcesSet requiredResourcesSet) {
	}

	@LayoutRes
	public abstract int getViewResource();

	@CallSuper
	@Override
	public View getView(Context context) {
		view = LayoutInflater.from(context).inflate(getViewResource(), null, false);
		checkbox = view.findViewById(R.id.brick_checkbox);
		return view;
	}

	@Override
	public View getPrototypeView(Context context) {
		View view = getView(context);
		disableSpinners(view);
		return view;
	}

	public void disableSpinners() {
		disableSpinners(view);
	}

	private void disableSpinners(View view) {
		if (view instanceof Spinner) {
			view.setEnabled(false);
			view.setClickable(false);
			view.setFocusable(false);
		}
		if (view instanceof ViewGroup) {
			ViewGroup parent = (ViewGroup) view;
			for (int i = 0; i < parent.getChildCount(); i++) {
				disableSpinners(parent.getChildAt(i));
			}
		}
	}

	@Override
	public boolean consistsOfMultipleParts() {
		return false;
	}

	@Override
	public List<Brick> getAllParts() {
		return Collections.singletonList(this);
	}

	@Override
	public void addToFlatList(List<Brick> bricks) {
		bricks.add(this);
	}

	@Override
	public Script getScript() {
		return getParent().getScript();
	}

	@Override
	public int getPositionInScript() {
		if (getParent() instanceof ScriptBrick) {
			return getScript().getBrickList().indexOf(this);
		}
		return getParent().getPositionInScript();
	}

	@Override
	public Brick getParent() {
		return parent;
	}

	@Override
	public void setParent(Brick parent) {
		this.parent = parent;
	}

	@Override
	public List<Brick> getDragAndDropTargetList() {
		return getParent().getDragAndDropTargetList();
	}

	@Override
	public int getPositionInDragAndDropTargetList() {
		return getDragAndDropTargetList().indexOf(this);
	}

	@Override
	public boolean removeChild(Brick brick) {
		return false;
	}

	public boolean hasHelpPage() {
		return true;
	}

	void notifyDataSetChanged(AppCompatActivity activity) {
		ScriptFragment parentFragment = (ScriptFragment) activity
				.getSupportFragmentManager().findFragmentByTag(ScriptFragment.TAG);
		if (parentFragment != null) {
			parentFragment.notifyDataSetChanged();
		}
	}

	private static final Map<Integer, String> CATEGORY_DE = new HashMap<>();
	private static final Map<Integer, String> CATEGORY_EN = new HashMap<>();

	private static final Map<String, String> BRICK_DE = new HashMap<>();
	private static final Map<String, String> BRICK_EN = new HashMap<>();

	static {

		// -------------------- Ereignisse || Event -------------------

		CATEGORY_DE.put(R.string.category_event, "/ereignisse");
		CATEGORY_EN.put(R.string.category_event, "/event");

		// When scene starts
		BRICK_DE.put("WhenStartedBrick", "/wenn-szene-startet");
		BRICK_EN.put("WhenStartedBrick", "/when-scene-starts");
		// When tapped
		BRICK_DE.put("WhenBrick", "/wenn-angetippt");
		BRICK_EN.put("WhenBrick", "/when-tapped");
		// When background changes to
		BRICK_DE.put("WhenBackgroundChangesBrick", "/wenn-der-hintergrund-wechselt-zu");
		BRICK_EN.put("WhenBackgroundChangesBrick", "/when-background-changes-to");
		// When stage is tapped
		BRICK_DE.put("WhenTouchDownBrick", "/wenn-der-bildschirm-beruhrt-wird");
		BRICK_EN.put("WhenTouchDownBrick", "/event/when-stage-is-tapped");
		// When(...) becomes true
		BRICK_DE.put("WhenConditionBrick", "/wenn-wahr-wird");
		BRICK_EN.put("WhenConditionBrick", "/when-1");
		// When you start as a clone
		BRICK_DE.put("WhenClonedBrick", "/als-klon-starte");
		BRICK_EN.put("WhenClonedBrick", "/when-you-start-as-a-clone");
		// When you receive
		BRICK_DE.put("BroadcastReceiverBrick", "/wenn-du-empfangst");
		BRICK_EN.put("BroadcastReceiverBrick", "/when-you-receive");

		// -------------------- Steuerung || Control -------------------

		CATEGORY_DE.put(R.string.category_control, "/steuerung");
		CATEGORY_EN.put(R.string.category_control, "/control");
		// Broadcast
		BRICK_DE.put("BroadcastBrick", "/verschicke-an-alle");
		BRICK_EN.put("BroadcastBrick", "/broadcast");
		// Broadcast and wait
		BRICK_DE.put("BroadcastWaitBrick", "/verschicke-und-warte");
		BRICK_EN.put("BroadcastWaitBrick", "/broadcast-and-wait");
		// Continue scene
		BRICK_DE.put("SceneTransitionBrick", "/szene-fortsetzen");
		BRICK_EN.put("SceneTransitionBrick", "/continue-scene");
		// Create clone of
		BRICK_DE.put("CloneBrick", "/erzeuge-klon-von");
		BRICK_EN.put("CloneBrick", "/create-clone-of");
		// Delete this clone
		BRICK_DE.put("DeleteThisCloneBrick", "/losche-diesen-klon");
		BRICK_EN.put("DeleteThisCloneBrick", "/delete-this-clone");
		// Finish stage
		BRICK_DE.put("ExitStageBrick", "/stufe-beenden");
		BRICK_EN.put("ExitStageBrick", "/finish-stage");
		// For each value from .. in ..
		BRICK_DE.put("ForItemInUserListBrick", "/fur-jeden-wert-von-im");
		BRICK_EN.put("ForItemInUserListBrick", "/for-each-value-from-in");
		// For values from 1 to 10 in
		BRICK_DE.put("ForVariableFromToBrick", "/fur-werte-von-auf-im");
		BRICK_EN.put("ForVariableFromToBrick", "/for-values-from-1-to-10-in");
		// Forever
		BRICK_DE.put("ForeverBrick", "/wiederhole-fortlaufend");
		BRICK_EN.put("ForeverBrick", "/forever");
		// If … Then … Else …
		BRICK_DE.put("IfLogicBeginBrick", "/wenn-wahr-ist-dann-sonst");
		BRICK_EN.put("IfLogicBeginBrick", "/if-1");
		// If(…) is true then
		BRICK_DE.put("IfThenLogicBeginBrick", "/wenn-wahr-ist-dann");
		BRICK_EN.put("IfThenLogicBeginBrick", "/if-1-2");
		// Note
		BRICK_DE.put("NoteBrick", "/notiz");
		BRICK_EN.put("NoteBrick", "/note-add-comment-here");
		// Repeat … times
		BRICK_DE.put("RepeatBrick", "/wiederhole-mal");
		BRICK_EN.put("RepeatBrick", "/repeat-10-times");
		// Repeat … times
		BRICK_DE.put("RepeatUntilBrick", "/wiederhole-bis-wahr-ist");
		BRICK_EN.put("RepeatUntilBrick", "/repeat-until-1");
		// Start scene
		BRICK_DE.put("SceneStartBrick", "/beginne-szene");
		BRICK_EN.put("SceneStartBrick", "/start-scene");
		// Stop
		BRICK_DE.put("StopScriptBrick", "/stoppe");
		BRICK_EN.put("StopScriptBrick", "/stop-this-script");
		// Wait
		BRICK_DE.put("WaitBrick", "/warte-sekunde");
		BRICK_EN.put("WaitBrick", "/wait-1-second");
		// Wait until … is true
		BRICK_DE.put("WaitUntilBrick", "/warte-bis-wahr-ist");
		BRICK_EN.put("WaitUntilBrick", "/wait-until-1");
		// Wait until all other scripts have stopped
		BRICK_DE.put("WaitTillIdleBrick", "/warte-bis-alle-anderen-skripte-gestoppt");
		BRICK_EN.put("WaitTillIdleBrick", "/wait-until-all-other-scripts-have-stopped");


		// -------------------- Bewegung || Motion -------------------

		CATEGORY_DE.put(R.string.category_motion, "/bewegung");
		CATEGORY_EN.put(R.string.category_motion, "/motion");

		// Change X by
		BRICK_DE.put("ChangeXByNBrick", "/andere-x-um");
		BRICK_EN.put("ChangeXByNBrick", "/change-x-by");
		// Change Y by
		BRICK_DE.put("ChangeYByNBrick", "/andere-y-um");
		BRICK_EN.put("ChangeYByNBrick", "/change-y-by");
		// Glide … second to
		BRICK_DE.put("GlideToBrick", "/gleite-sekunde-zu-x-y");
		BRICK_EN.put("GlideToBrick", "/glide-second-to");
		// Go back … layer
		BRICK_DE.put("GoNStepsBackBrick", "/gehe-nach-hinten-ebene");
		BRICK_EN.put("GoNStepsBackBrick", "/go-back-layer");
		// Go to
		BRICK_DE.put("GoToBrick", "/gehe-zu");
		BRICK_EN.put("GoToBrick", "/go-to");
		// Go to front
		BRICK_DE.put("ComeToFrontBrick", "/gehe-nach-vorne");
		BRICK_EN.put("ComeToFrontBrick", "/go-to-front");
		// If on edge, bounce
		BRICK_DE.put("IfOnEdgeBounceBrick", "/pralle-vom-rand-ab");
		BRICK_EN.put("IfOnEdgeBounceBrick", "/if-on-edge-bounce");
		// Move … steps
		BRICK_DE.put("MoveNStepsBrick", "/verschiebe-um-schritte");
		BRICK_EN.put("MoveNStepsBrick", "/move-steps");
		// Place at
		BRICK_DE.put("PlaceAtBrick", "/setze-an-position-x-y");
		BRICK_EN.put("PlaceAtBrick", "/place-at");
		// Point in direction … degrees
		BRICK_DE.put("PointInDirectionBrick", "/zeige-in-richtung");
		BRICK_EN.put("PointInDirectionBrick", "/point-in-direction-degrees");
		// Point towards
		BRICK_DE.put("PointToBrick", "/zeige-auf");
		BRICK_EN.put("PointToBrick", "/point-towards");
		// Set rotation style
		BRICK_DE.put("SetRotationStyleBrick", "/setze-rotations-stil");
		BRICK_EN.put("SetRotationStyleBrick", "/set-rotation-style");
		// Set velocity to
		BRICK_DE.put("SetVelocityBrick", "/setze-geschwindigkeit-auf-x-y-schritt-sekunde");
		BRICK_EN.put("SetVelocityBrick", "/set-velocity-to");
		// Set X to
		BRICK_DE.put("SetXBrick", "/setze-x-auf");
		BRICK_EN.put("SetXBrick", "/set-x-to");
		// Set Y to
		BRICK_DE.put("SetYBrick", "/setze-y-auf");
		BRICK_EN.put("SetYBrick", "/set-y-to");
		// Set your motion type to …
		BRICK_DE.put("SetPhysicsObjectTypeBrick", "/setze-bewegungstyp-auf");
		BRICK_EN.put("SetPhysicsObjectTypeBrick", "/set-your-motion-type-to");
		// Spin left … degrees/second
		BRICK_DE.put("TurnLeftSpeedBrick", "/nach-links-drehen-grad-sekunde");
		BRICK_EN.put("TurnLeftSpeedBrick", "/spin-left-degrees-second");
		// Spin right … degrees/second
		BRICK_DE.put("TurnRightSpeedBrick", "/nach-rechts-drehen-grad-sekunde");
		BRICK_EN.put("TurnRightSpeedBrick", "/spin-right-degrees-second");
		// Turn left … degrees
		BRICK_DE.put("TurnLeftBrick", "/drehe-links-grad");
		BRICK_EN.put("TurnLeftBrick", "/turn-left-degrees");
		// Turn right … degrees
		BRICK_DE.put("TurnRightBrick", "/drehe-rechts-grad");
		BRICK_EN.put("TurnRightBrick", "/turn-right-degrees");
		// Vibrate for … second
		BRICK_DE.put("VibrationBrick", "/vibriere-fur-sekunde");
		BRICK_EN.put("VibrationBrick", "/vibrate-for-second");
		// When you bounce off …
		BRICK_DE.put("WhenBounceOffBrick", "/wenn-du-abprallst-von");
		BRICK_EN.put("WhenBounceOffBrick", "/when-you-bounce-off");
		// Set bounce factor to …%
		BRICK_DE.put("SetBounceBrick", "/setze-aufprallfaktor-auf");
		BRICK_EN.put("SetBounceBrick", "/set-bounce-factor-to");
		// Set friction to …%
		BRICK_DE.put("SetFrictionBrick", "/setze-reibung-auf");
		BRICK_EN.put("SetFrictionBrick", "/set-friction-to");
		// Set gravity for all actors and objects to …
		BRICK_DE.put("SetGravityBrick", "/setze-gravitation-fur-alle-figuren-und-objekte");
		BRICK_EN.put("SetGravityBrick", "/set-gravity-for-all-actors-and-objects-to");
		// Set mass to … kilogram
		BRICK_DE.put("SetMassBrick", "/setze-masse-auf-kilogramm");
		BRICK_EN.put("SetMassBrick", "/set-mass-to-kilogram");


		// -------------------- Klang || Sound  -------------------
		CATEGORY_DE.put(R.string.category_sound, "/klang");
		CATEGORY_EN.put(R.string.category_sound, "/sound");
		// Change volume by …
		BRICK_DE.put("ChangeVolumeByNBrick", "/andere-lautstarke-um");
		BRICK_EN.put("ChangeVolumeByNBrick", "/change-volume-by");
		// Play drum … for … beats
		BRICK_DE.put("PlayDrumForBeatsBrick", "/trommel-abspielen-fur-schlage");
		BRICK_EN.put("PlayDrumForBeatsBrick", "/play-drum-for-beats");
		// Play note … for … beats
		BRICK_DE.put("PlayNoteForBeatsBrick", "/musiknote-abspielen-fur-sekunden");
		BRICK_EN.put("PlayNoteForBeatsBrick", "/play-note-for");
		// Set instrument to
		BRICK_DE.put("SetInstrumentBrick", "/setze-instrument-auf");
		BRICK_EN.put("SetInstrumentBrick", "/set-instrument-to");
		// Set volume to …%
		BRICK_DE.put("SetVolumeToBrick", "/setze-lautstarke-auf");
		BRICK_EN.put("SetVolumeToBrick", "/set-volume-to");
		// Start sound
		BRICK_DE.put("PlaySoundBrick", "/klang-starten");
		BRICK_EN.put("PlaySoundBrick", "/start-sound");
		// Start sound and wait
		BRICK_DE.put("PlaySoundAndWaitBrick", "/starte-klang-und-warte");
		BRICK_EN.put("PlaySoundAndWaitBrick", "/start-sound-and-wait");
		// Stop sound
		BRICK_DE.put("StopSoundBrick", "/stoppe-klang");
		BRICK_EN.put("StopSoundBrick", "/stop-sound");
		// Stop all sounds
		BRICK_DE.put("StopAllSoundsBrick", "/alle-klange-stoppen");
		BRICK_EN.put("StopAllSoundsBrick", "/stop-all-sounds");
		// Change tempo by …
		BRICK_DE.put("ChangeTempoByNBrick", "/setze-tempo-auf");
		BRICK_EN.put("ChangeTempoByNBrick", "/change-tempo-by");
		// Pause for … beats
		BRICK_DE.put("PauseForBeatsBrick", "/pausiere-fur-schlage");
		BRICK_EN.put("PauseForBeatsBrick", "/pause-for-beats");
		// Set tempo to …
		BRICK_DE.put("SetTempoBrick", "/setze-tempo-auf");
		BRICK_EN.put("SetTempoBrick", "/set-tempo-to");


		// -------------------- Aussehen || Looks -------------------
		CATEGORY_DE.put(R.string.category_looks, "/aussehen");
		CATEGORY_EN.put(R.string.category_looks, "/looks");
		// Ask … and store written answer in …
		BRICK_DE.put("AskBrick", "/frage-und-speichere-das-eingegebene-in");
		BRICK_EN.put("AskBrick", "/ask-and-store-written-answer-in");
		// Become focus point with …% horizontal and …% vertical flexibility
		BRICK_DE.put("SetCameraFocusPointBrick",
				"/fokuspunkt-werden-mit-horizontal-und-vertikale-flexibilitat");
		BRICK_EN.put("SetCameraFocusPointBrick",
				"/become-focus-point-with-horizontal-and-vertical-flexibility");
		// Change brightness by …
		BRICK_DE.put("ChangeBrightnessByNBrick", "/andere-helligkeit-um");
		BRICK_EN.put("ChangeBrightnessByNBrick", "/change-brightness-by");
		// Change colour by …
		BRICK_DE.put("ChangeColorByNBrick", "/andere-farbe-um");
		BRICK_EN.put("ChangeColorByNBrick", "/change-colour-by");
		// Change size by …
		BRICK_DE.put("ChangeSizeByNBrick", "/andere-grose-um");
		BRICK_EN.put("ChangeSizeByNBrick", "/change-size-by");
		// Change transparency by …
		BRICK_DE.put("ChangeTransparencyByNBrick", "/andere-transparenz-um");
		BRICK_EN.put("ChangeTransparencyByNBrick", "/change-transparency-by");
		// Clear graphic effects
		BRICK_DE.put("ClearGraphicEffectBrick", "/grafikeffekt-zurucksetzen");
		BRICK_EN.put("ClearGraphicEffectBrick", "/clear-graphic-effects");
		// Copy look and name it …
		BRICK_DE.put("CopyLookBrick", "/look-kopieren-und-namen-geben");
		BRICK_EN.put("CopyLookBrick", "/copy-look-and-name-it");
		// Delete look
		BRICK_DE.put("DeleteLookBrick", "/losche-aussehen");
		BRICK_EN.put("DeleteLookBrick", "/delete-look");
		// Edit look
		BRICK_DE.put("EditLookBrick", "/aussehen-bearbeiten");
		BRICK_EN.put("EditLookBrick", "/edit-look");
		// Fade particle effect
		BRICK_DE.put("FadeParticleEffectBrick", "/blende-partikeleffekt");
		BRICK_EN.put("FadeParticleEffectBrick", "/fade-particle-effect");
		// Get image from … and use as current look
		BRICK_DE.put("LookRequestBrick", "/bild-abrufen-von");
		BRICK_EN.put("LookRequestBrick", "/get-image-from-and-use-as-current-look");
		// Hide
		BRICK_DE.put("HideBrick", "/verbergen");
		BRICK_EN.put("HideBrick", "/hide");
		// Next look
		BRICK_DE.put("NextLookBrick", "/nachstes-aussehen");
		BRICK_EN.put("NextLookBrick", "/next-look");
		// Paint new look …
		BRICK_DE.put("PaintNewLookBrick", "/neuen-look-malen");
		BRICK_EN.put("PaintNewLookBrick", "/paint-new-look");
		// Previous look
		BRICK_DE.put("PreviousLookBrick", "/voriges-aussehen");
		BRICK_EN.put("PreviousLookBrick", "/previous-look");
		// Say …
		BRICK_DE.put("SayBubbleBrick", "/sage");
		BRICK_EN.put("SayBubbleBrick", "/say");
		// Say … for .. second
		BRICK_DE.put("SayForBubbleBrick", "/sage-fur-sekunde");
		BRICK_EN.put("SayForBubbleBrick", "/say-for-second");
		// Set background
		BRICK_DE.put("SetBackgroundBrick", "/setze-hintergrund");
		BRICK_EN.put("SetBackgroundBrick", "/set-background");
		// Set background and wait
		BRICK_DE.put("SetBackgroundAndWaitBrick", "/setze-hintergrund-und-warte");
		BRICK_EN.put("SetBackgroundAndWaitBrick", "/set-background-and-wait");
		// Set background to number …
		BRICK_DE.put("SetBackgroundByIndexBrick", "/setze-hintergrund-auf-nummer");
		BRICK_EN.put("SetBackgroundByIndexBrick", "/set-background-to-number");
		// Set brightness to …%
		BRICK_DE.put("SetBrightnessBrick", "/setze-helligkeit-auf");
		BRICK_EN.put("SetBrightnessBrick", "/set-brightness-to");
		// Set colour to …
		BRICK_DE.put("SetColorBrick", "/setze-farbe-auf");
		BRICK_EN.put("SetColorBrick", "/set-colour-to");
		// Set particle color to …
		BRICK_DE.put("SetParticleColorBrick", "/setze-partikelfarbe-auf");
		BRICK_EN.put("SetParticleColorBrick", "/set-particle-color-to");
		// Set size to …%
		BRICK_DE.put("SetSizeToBrick", "/setze-grose-auf");
		BRICK_EN.put("SetSizeToBrick", "/set-size-to");
		// Set transparency to …
		BRICK_DE.put("SetTransparencyBrick", "/setze-transparenz-auf");
		BRICK_EN.put("SetTransparencyBrick", "/set-transparency-to");
		// Show
		BRICK_DE.put("ShowBrick", "/anzeigen");
		BRICK_EN.put("ShowBrick", "/show");
		// Switch to look
		BRICK_DE.put("SetLookBrick", "/setze-aussehen");
		BRICK_EN.put("SetLookBrick", "/switch-to-look");
		// Switch to look with number …
		BRICK_DE.put("SetLookByIndexBrick", "/setze-aussehen-auf-nummer");
		BRICK_EN.put("SetLookByIndexBrick", "/switch-to-look-with-number");
		// Think .. for … second
		BRICK_DE.put("ThinkForBubbleBrick", "/denke-fur-sekunde");
		BRICK_EN.put("ThinkForBubbleBrick", "/think-for-second");
		// Think …
		BRICK_DE.put("ThinkBubbleBrick", "/denke");
		BRICK_EN.put("ThinkBubbleBrick", "/think");
		// Turn camera
		BRICK_DE.put("CameraBrick", "/schalte-kamera");
		BRICK_EN.put("CameraBrick", "/turn-camera");
		// Turn flashlight
		BRICK_DE.put("FlashBrick", "/schalte-blitz");
		BRICK_EN.put("FlashBrick", "/turn-flashlight");
		// Turn particle effect additivity
		BRICK_DE.put("ParticleEffectAdditivityBrick", "/schalte-partikeleffekt-additivitat");
		BRICK_EN.put("ParticleEffectAdditivityBrick", "/turn-particle-effect-additivity");
		// Use camera
		BRICK_DE.put("ChooseCameraBrick", "/verwende-kamera");
		BRICK_EN.put("ChooseCameraBrick", "/use-camera");
		// Set background to number … and wait
		BRICK_DE.put("SetBackgroundByIndexAndWaitBrick", "/setze-hintergrund-auf-nummer-und-warte");
		BRICK_EN.put("SetBackgroundByIndexAndWaitBrick", "/set-background-to-number-and-wait");


		// -------------------- Malsstifte || Pen -------------------
		CATEGORY_DE.put(R.string.category_pen, "/malstift");
		CATEGORY_EN.put(R.string.category_pen, "/pen");
		// Clear
		BRICK_DE.put("ClearBackgroundBrick", "/wische-malspur-weg");
		BRICK_EN.put("ClearBackgroundBrick", "/clear");
		// Pen down
		BRICK_DE.put("PenDownBrick", "/schalte-stift-ein");
		BRICK_EN.put("PenDownBrick", "/pen-down");
		// Pen up
		BRICK_DE.put("PenUpBrick", "/schalte-stift-aus");
		BRICK_EN.put("PenUpBrick", "/pen-up");
		// Set pen size to …
		BRICK_DE.put("SetPenSizeBrick", "/setze-stiftdicke-auf");
		BRICK_EN.put("SetPenSizeBrick", "/set-pen-size-to");
		// Stamp
		BRICK_DE.put("StampBrick", "/stempel");
		BRICK_EN.put("StampBrick", "/stamp");
		// Set pen colour to red .. green .. blue
		BRICK_DE.put("SetPenColorBrick", "/setze-stiftfarbe-auf-rot-grun-blau");
		BRICK_EN.put("SetPenColorBrick", "/set-pen-colour-to-red-green-blue");

		// -------------------- Daten || Data -------------------
		CATEGORY_DE.put(R.string.category_data, "/daten");
		CATEGORY_EN.put(R.string.category_data, "/data");
		// Add … to list …
		BRICK_DE.put("AddItemToUserListBrick", "/hinzufugen-zur-liste");
		BRICK_EN.put("AddItemToUserListBrick", "/add-to-list");
		// Change variable … by …
		BRICK_DE.put("ChangeVariableBrick", "/andere-variable-um");
		BRICK_EN.put("ChangeVariableBrick", "/change-variable-by");
		// Delete all items from list …
		BRICK_DE.put("ClearUserListBrick", "/alle-elemente-aus-der-liste-loschen");
		BRICK_EN.put("ClearUserListBrick", "/delete-all-items-from-list");
		// Delete item from list … at position …
		 BRICK_DE.put("DeleteItemOfUserListBrick", "/elemente-aus-liste-loschen-auf-position");
		 BRICK_EN.put("DeleteItemOfUserListBrick", "/delete-item-from-list-at-position");
		// Hide variable …
		BRICK_DE.put("HideTextBrick", "/verstecke-variable");
		BRICK_EN.put("HideTextBrick", "/hide-variable");
		// Insert … into list … at position …
		BRICK_DE.put("InsertItemIntoUserListBrick", "/einfugen-in-liste-auf-position");
		BRICK_EN.put("InsertItemIntoUserListBrick", "/insert-into-list-at-position");
		// Read variable … from file … AND …
		BRICK_DE.put("ReadVariableFromFileBrick", "/lese-variable-aus-datei-und");
		BRICK_EN.put("ReadVariableFromFileBrick", "/read-variable-from-file-and");
		// Read list from device …
		BRICK_DE.put("ReadListFromDeviceBrick", "/liste-von-gerat-lesen");
		BRICK_EN.put("ReadListFromDeviceBrick", "/read-list-from-device");
		// Read variable from device …
		BRICK_DE.put("ReadVariableFromDeviceBrick", "/variable-von-gerat-lesen");
		BRICK_EN.put("ReadVariableFromDeviceBrick", "/read-variable-from-device");
		// Replace item in list … at position … with …
		BRICK_DE.put("ReplaceItemInUserListBrick", "/elemente-in-liste-ersetzen-auf-position-im");
		BRICK_EN.put("ReplaceItemInUserListBrick", "/replace-item-in-list-at-position-with");
		// Send web request to … and store answer in …
		BRICK_DE.put("WebRequestBrick", "/webanfrage-senden-an-und-speichere-antwort-in");
		BRICK_EN.put("WebRequestBrick", "/send-web-request-to-and-store-answer-in");
		// Set variable … to …
		BRICK_DE.put("SetVariableBrick", "/setze-variable-auf");
		BRICK_EN.put("SetVariableBrick", "/set-variable-to");
		// Show variable … at X:… Y:…
		BRICK_DE.put("ShowTextBrick", "/zeige-variable-bei-x-y");
		BRICK_EN.put("ShowTextBrick", "/Show variable … at X:… Y:…");
		// Show variable … at X:… Y:… size:.. colour:… aligned…
		BRICK_DE.put("ShowTextColorSizeAlignmentBrick", "/zeige-variable-bei-x-y-grose-farbe"
				+ "-ausrichtung");
		BRICK_EN.put("ShowTextColorSizeAlignmentBrick", "/show-variable-at-x-y-size-colour-aligned");
		// Store column … of the comma-separated values …
		BRICK_DE.put("StoreCSVIntoUserListBrick", "/speichere-spalte-von-den-durch-kommas"
				+ "-getrennten-werten-in-liste");
		BRICK_EN.put("StoreCSVIntoUserListBrick", "/store-column-of-the-comma-separated-values");
		// Write variable … to file …
		BRICK_DE.put("WriteVariableToFileBrick", "/schreibe-variable-zur-datei");
		BRICK_EN.put("WriteVariableToFileBrick", "/write-variable-to-file");
		// Write list on device …
		BRICK_DE.put("WriteListOnDeviceBrick", "/liste-auf-gerat-speichern");
		BRICK_EN.put("WriteListOnDeviceBrick", "/write-list-on-device");
		// Write variable on device …
		BRICK_DE.put("WriteVariableOnDeviceBrick", "/variable-auf-gerat-schreiben");
		BRICK_EN.put("WriteVariableOnDeviceBrick", "/write-variable-on-device");


		// -------------------- Gerät || Device -------------------
		CATEGORY_DE.put(R.string.category_device, "/gerat");
		CATEGORY_EN.put(R.string.category_device, "/device");

		// "No single bricks are in wiki, it is just for fallbacks"


		// -------------------- Deine Bausteine || YourBricks -------------------
		CATEGORY_DE.put(R.string.category_user_bricks, "/deine-bausteine");
		CATEGORY_EN.put(R.string.category_user_bricks, "/yourbricks");

		// "No single bricks are in wiki, it is just for fallbacks"


		// -------------------- Sticken || Embroidery -------------------
		CATEGORY_DE.put(R.string.category_embroidery, "/sticken");
		CATEGORY_EN.put(R.string.category_embroidery, "/embroidery");
		// Set thread color to …
		BRICK_DE.put("SetThreadColorBrick", "/setze-auf-farbe");
		BRICK_EN.put("SetThreadColorBrick", "/set-thread-color-to");
		// Start zigzag stitch with lenght … and width …
		BRICK_DE.put("ZigZagStitchBrick", "/starte-zickzack-stich-mit-lange-und-breite");
		BRICK_EN.put("ZigZagStitchBrick", "/start-zigzag-stitch-with-lenght-and-width");
		// Start running stitch with length …
		BRICK_DE.put("RunningStitchBrick", "/starte-laufstich-mit-lange");
		BRICK_EN.put("RunningStitchBrick", "/start-running-stitch-with-length");
		// Start triple stitch with length …
		BRICK_DE.put("TripleStitchBrick", "/starte-dreifachstich-mit-lange");
		BRICK_EN.put("TripleStitchBrick", "/start-triple-stitch-with-length");
		// Sew up
		BRICK_DE.put("SewUpBrick", "/vernahen");
		BRICK_EN.put("SewUpBrick", "/sew-up");
		// Stop current stitch
		BRICK_DE.put("StopRunningStitchBrick", "/beende-aktuellen-stich");
		BRICK_EN.put("StopRunningStitchBrick", "/stop-current-stitch");
		// Write embroidery data to file …
		BRICK_DE.put("WriteEmbroideryToFileBrick", "/schreibe-stickdaten-in-die-datei");
		BRICK_EN.put("WriteEmbroideryToFileBrick", "/write-embroidery-data-to-file");
		// Stitch
		BRICK_DE.put("StitchBrick", "/stich");
		BRICK_EN.put("StitchBrick", "/stitch");


		// -------------------- Plotten || Plot -------------------
		CATEGORY_DE.put(R.string.category_plot, "/plotten");
		CATEGORY_EN.put(R.string.category_plot, "/plot");

		// Save plot as SVG
		BRICK_DE.put("SavePlotBrick", "/plot-als-svg-speichern");
		BRICK_EN.put("SavePlotBrick", "/save-plot");
		// Start to plot
		BRICK_DE.put("StartPlotBrick", "/plotten-starten");
		BRICK_EN.put("StartPlotBrick", "/start-to-plot");
		// Stop to plot
		BRICK_DE.put("StopPlotBrick", "/plotten-beenden");
		BRICK_EN.put("StopPlotBrick", "/stop-to-plot");

	}

	public String getHelpUrl(int category, String language) {
		String brickName = this.getClass().getSimpleName();

		String baseUrl;
		String categoryUrl;
		String brickUrl;
		if (language.equalsIgnoreCase("de")) {
			baseUrl = "https://catrobat.org/docs/brick-dokumentation-de";
			categoryUrl = CATEGORY_DE.getOrDefault(category, "");
			brickUrl = BRICK_DE.getOrDefault(brickName, "");
		} else {
			baseUrl = "https://catrobat.org/docs/brickdocumentation";
			categoryUrl = CATEGORY_EN.getOrDefault(category, "");
			brickUrl = BRICK_EN.getOrDefault(brickName, "");
		}

		return baseUrl + categoryUrl + brickUrl;
	}

	protected String getPositionInformation() {
		int position = -1;
		String scriptName = "unknown";
		if (getParent() != null) {
			position = getPositionInScript();
			scriptName = getScript().getClass().getSimpleName();
		}
		position += 2;
		return "Brick at position " + position + "\nin \"" + scriptName + "/";
	}

	@Override
	public UUID getBrickID() {
		return brickId;
	}

	@Override
	public List<Brick> findBricksInNestedBricks(List<UUID> brickIds) {
		if (!(this instanceof CompositeBrick)) {
			return null;
		}

		List<Brick> foundBricks = new ArrayList<>();
		CompositeBrick compositeBrick = (CompositeBrick) this;

		for (Brick brick : compositeBrick.getNestedBricks()) {
			if (brickIds.contains(brick.getBrickID())) {
				foundBricks.add(brick);
			} else if (brick instanceof CompositeBrick) {
				List<Brick> tmpBricks = brick.findBricksInNestedBricks(brickIds);
				if (tmpBricks != null) {
					return tmpBricks;
				}
			}

			if (brickIds.size() == foundBricks.size()) {
				break;
			}
		}

		if (foundBricks.size() == 0 && compositeBrick.hasSecondaryList()) {
			for (Brick brick : compositeBrick.getSecondaryNestedBricks()) {
				if (brickIds.contains(brick.getBrickID())) {
					foundBricks.add(brick);
				} else if (brick instanceof CompositeBrick) {
					List<Brick> tmpBricks = brick.findBricksInNestedBricks(brickIds);
					if (tmpBricks != null) {
						return tmpBricks;
					}
				}

				if (brickIds.size() == foundBricks.size()) {
					break;
				}
			}
		}

		if (foundBricks.size() > 0) {
			return foundBricks;
		}
		return null;
	}

	@Override
	public boolean addBrickInNestedBrick(UUID parentBrickId, int subStackIndex, List<Brick> bricksToAdd) {
		if (!(this instanceof CompositeBrick)) {
			return false;
		}

		CompositeBrick compositeBrick = (CompositeBrick) this;

		if (getBrickID().equals(parentBrickId)) {
			if (subStackIndex == 0) {
				compositeBrick.getNestedBricks().addAll(0, bricksToAdd);
				return true;
			} else if (subStackIndex == 1 && compositeBrick.hasSecondaryList()) {
				compositeBrick.getSecondaryNestedBricks().addAll(0, bricksToAdd);
				return true;
			}
		}

		int index = 0;

		for (Brick brick : compositeBrick.getNestedBricks()) {
			++index;
			if (subStackIndex == -1
					&& brick.getBrickID().equals(parentBrickId)) {
				compositeBrick.getNestedBricks().addAll(index, bricksToAdd);
			} else if (brick instanceof CompositeBrick
					&& brick.addBrickInNestedBrick(parentBrickId, subStackIndex, bricksToAdd)) {
				return true;
			}
		}

		if (!compositeBrick.hasSecondaryList()) {
			return false;
		}

		index = 0;
		for (Brick brick : compositeBrick.getSecondaryNestedBricks()) {
			++index;
			if (subStackIndex == -1
					&& brick.getBrickID().equals(parentBrickId)) {
				compositeBrick.getSecondaryNestedBricks().addAll(index, bricksToAdd);
				return true;
			} else if (brick instanceof CompositeBrick
					&& brick.addBrickInNestedBrick(parentBrickId, subStackIndex, bricksToAdd)) {
				return true;
			}
		}
		return false;
	}
}

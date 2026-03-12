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

		CATEGORY_DE.put(R.string.category_event, "ereignisse/");
		CATEGORY_EN.put(R.string.category_event, "event/");

		// When scene starts
		BRICK_DE.put("WhenStartedBrick", "wenn-szene-startet");
		BRICK_EN.put("WhenStartedBrick", "when-scene-starts");
		// When tapped
		BRICK_DE.put("WhenBrick", "wenn-angetippt");
		BRICK_EN.put("WhenBrick", "when-tapped");
		// When background changes to
		BRICK_DE.put("WhenBackgroundChangesBrick", "wenn-der-hintergrund-wechselt-zu");
		BRICK_EN.put("WhenBackgroundChangesBrick", "when-background-changes-to");
		// When stage is tapped
		BRICK_DE.put("WhenTouchDownBrick", "wenn-der-bildschirm-beruhrt-wird");
		BRICK_EN.put("WhenTouchDownBrick", "event/when-stage-is-tapped");
		// When(...) becomes true
		BRICK_DE.put("WhenConditionBrick", "wenn-wahr-wird");
		BRICK_EN.put("WhenConditionBrick", "when-1");
		// When you start as a clone
		BRICK_DE.put("WhenClonedBrick", "als-klon-starte");
		BRICK_EN.put("WhenClonedBrick", "when-you-start-as-a-clone");
		// When you receive
		BRICK_DE.put("BroadcastReceiverBrick", "wenn-du-empfangst");
		BRICK_EN.put("BroadcastReceiverBrick", "when-you-receive");

		// -------------------- Steuerung || Control -------------------

		CATEGORY_DE.put(R.string.category_control, "steuerung/");
		CATEGORY_EN.put(R.string.category_control, "control/");

		// Broadcast
		BRICK_DE.put("BroadcastBrick", "verschicke-an-alle");
		BRICK_EN.put("BroadcastBrick", "broadcast");
		// Broadcast and wait
		BRICK_DE.put("BroadCastWaitBrick", "verschicke-und-warte"); // TODO - error
		BRICK_EN.put("BroadCastWaitBrick", "broadcast-and-wait"); // TODO - error
		// Continue scene
		BRICK_DE.put("SceneTransitionBrick", "szene-fortsetzen");
		BRICK_EN.put("SceneTransitionBrick", "continue-scene");
		// Create clone of
		BRICK_DE.put("CloneBrick", "erzeuge-klon-von");
		BRICK_EN.put("CloneBrick", "create-clone-of");
		// Delete this clone
		BRICK_DE.put("DeleteThisCloneBrick", "losche-diesen-klon");
		BRICK_EN.put("DeleteThisCloneBrick", "delete-this-clone");
		// Finish stage
		BRICK_DE.put("ExitStageBrick", "stufe-beenden");
		BRICK_EN.put("ExitStageBrick", "finish-stage");
		// For each value from .. in ..
		BRICK_DE.put("FromItemInUserListBrick", "fur-jeden-wert-von-im"); // TODO - error
		BRICK_EN.put("FromItemInUserListBrick", "for-each-value-from-in"); // TODO - error
		// For values from 1 to 10 in
		BRICK_DE.put("ForVariableFromToBrick", "fur-werte-von-auf-im");
		BRICK_EN.put("ForVariableFromToBrick", "for-values-from-1-to-10-in");
		// Forever
		BRICK_DE.put("ForeverBrick", "wiederhole-fortlaufend");
		BRICK_EN.put("ForeverBrick", "forever");
		// If … Then … Else …
		BRICK_DE.put("IfLogicBeginBrick", "wenn-wahr-ist-dann-sonst");
		BRICK_EN.put("IfLogicBeginBrick", "if-1");
		// If(…) is true then
		BRICK_DE.put("IfThenLogicBeginBrick", "wenn-wahr-ist-dann");
		BRICK_EN.put("IfThenLogicBeginBrick", "if-1-2");
		// Note
		BRICK_DE.put("NoteBrick", "notiz");
		BRICK_EN.put("NoteBrick", "note-add-comment-here");
		// Repeat … times
		BRICK_DE.put("RepeatBrick", "wiederhole-mal");
		BRICK_EN.put("RepeatBrick", "repeat-10-times");
		// Repeat … times
		BRICK_DE.put("RepeatUntilBrick", "wiederhole-bis-wahr-ist");
		BRICK_EN.put("RepeatUntilBrick", "repeat-until-1");
		// Start scene
		BRICK_DE.put("SceneStartBrick", "beginne-szene");
		BRICK_EN.put("SceneStartBrick", "start-scene");
		// Stop
		BRICK_DE.put("StopScriptBrick", "stoppe");
		BRICK_EN.put("StopScriptBrick", "stop-this-script");
		// Wait
		BRICK_DE.put("WaitBrick", "warte-sekunde");
		BRICK_EN.put("WaitBrick", "wait-1-second");
		// Wait until … is true
		BRICK_DE.put("WaitUntilBrick", "warte-bis-wahr-ist");
		BRICK_EN.put("WaitUntilBrick", "wait-until-1");
		// Wait until all other scripts have stopped
		BRICK_DE.put("WaitUntilIdleBrick", "warte-bis-alle-anderen-skripte-gestoppt"); // TODO - error
		BRICK_EN.put("WaitUntilIdleBrick", "wait-until-all-other-scripts-have-stopped"); // TODO - error

		// -------------------- Bewegung || Motion -------------------

		CATEGORY_DE.put(R.string.category_motion, "bewegung/");
		CATEGORY_EN.put(R.string.category_motion, "motion/");

		// Change X by
		BRICK_DE.put("ChangeXByNBrick", "andere-x-um");
		BRICK_EN.put("ChangeXByNBrick", "change-x-by");
		// Change Y by
		BRICK_DE.put("ChangeYByNBrick", "andere-y-um");
		BRICK_EN.put("ChangeYByNBrick", "change-y-by");
		// Glide … second to
		BRICK_DE.put("GlideToBrick", "gleite-sekunde-zu-x-y");
		BRICK_EN.put("GlideToBrick", "glide-second-to");
		// Go back … layer
		BRICK_DE.put("GlideToBrick", "gehe-nach-hinten-ebene");
		BRICK_EN.put("GlideToBrick", "go-back-layer");
		// Go back … layer
		BRICK_DE.put("GoToBrick", "gehe-zu");
		BRICK_EN.put("GoToBrick", "go-to");



		// -------------------- Klang || Sound  -------------------
		CATEGORY_DE.put(R.string.category_sound, "klang/");
		CATEGORY_EN.put(R.string.category_sound, "sound/");

		// -------------------- Aussehen || Looks -------------------
		CATEGORY_DE.put(R.string.category_looks, "aussehen/");
		CATEGORY_EN.put(R.string.category_looks, "looks/");

		// -------------------- Malsstifte || Pen -------------------
		CATEGORY_DE.put(R.string.category_pen, "malstift/");
		CATEGORY_EN.put(R.string.category_pen, "pen/");

		// -------------------- Daten || Data -------------------
		CATEGORY_DE.put(R.string.category_data, "daten/");
		CATEGORY_EN.put(R.string.category_data, "data/");

		// -------------------- Gerät || Device -------------------
		CATEGORY_DE.put(R.string.category_device, "gerat/");
		CATEGORY_EN.put(R.string.category_device, "device/");

		// -------------------- Deine Bausteine || YourBricks -------------------
		CATEGORY_DE.put(R.string.category_user_bricks, "deine-bausteine/");
		CATEGORY_EN.put(R.string.category_user_bricks, "yourbricks/");

		// -------------------- Sticken || Embroidery -------------------
		CATEGORY_DE.put(R.string.category_embroidery, "sticken/");
		CATEGORY_EN.put(R.string.category_embroidery, "embroidery/");

		// -------------------- Plotten || Plot -------------------
		CATEGORY_DE.put(R.string.category_plot, "plotten/");
		CATEGORY_EN.put(R.string.category_plot, "plot/");

		//  Missing Brick Documentation

		// TODO WhenClonedBrick
		// TODO WhenNfcBrick
		// TODO PhiroIfLogicBeginBrick
		// TODO SetNfcTagBrick
		// TODO TapATBrick
		// TODO TapForBrick
	}

	public String getHelpUrl(int category, String language) {
		String brickName = this.getClass().getSimpleName();

		String baseUrl;
		String categoryUrl;
		String brickUrl;
		if (language.equalsIgnoreCase("de")) {
			baseUrl = "https://catrobat.org/docs/brick-dokumentation-de/";
			categoryUrl = CATEGORY_DE.getOrDefault(category, "");
			brickUrl = BRICK_DE.getOrDefault(brickName, "");
		} else {
			baseUrl = "https://catrobat.org/docs/brickdocumentation/";
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

/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2022 The Catrobat Team
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

package org.catrobat.catroid.ui.recyclerview.controller;

import android.util.Log;

import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.CompositeBrick;
import org.catrobat.catroid.content.bricks.IfLogicBeginBrick;
import org.catrobat.catroid.content.bricks.PhiroIfLogicBeginBrick;
import org.catrobat.catroid.content.bricks.ScriptBrick;
import org.catrobat.catroid.content.bricks.UserDefinedBrick;
import org.catrobat.catroid.content.bricks.UserDefinedReceiverBrick;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;

public final class BrickController {

	private static final String TAG = BrickController.class.getSimpleName();

	public void copy(@NonNull List<Brick> bricksToCopy, Sprite parent) {
		for (Brick brick : bricksToCopy) {
			Script script = brick.getScript();

			if (brick instanceof ScriptBrick) {
				try {
					parent.addScript(script.clone());
				} catch (CloneNotSupportedException e) {
					Log.e(TAG, Log.getStackTraceString(e));
				}
			} else {
				try {
					if (!bricksToCopy.contains(brick.getParent())) {
						Brick copyBrick = brick.clone();
						if (copyBrick instanceof CompositeBrick) {
							removeUnselectedBricksInCompositeBricks((CompositeBrick) copyBrick,
									(CompositeBrick) brick);
						}
						script.addBrick(copyBrick);
					}
				} catch (CloneNotSupportedException e) {
					Log.e(TAG, Log.getStackTraceString(e));
				}
			}
		}
	}

	private void removeUnselectedBricksInCompositeBricks(CompositeBrick copyBrick,
			CompositeBrick referenceBrick) {
		int copyCounter = 0;
		for (int i = 0; i < referenceBrick.getNestedBricks().size(); i++) {
			if (referenceBrick.getNestedBricks().get(i).getCheckBox() != null) {
				if (referenceBrick.getNestedBricks().get(i).getCheckBox().isChecked()) {
					if (referenceBrick.getNestedBricks().get(i) instanceof CompositeBrick) {
						removeUnselectedBricksInCompositeBricks((CompositeBrick) copyBrick.getNestedBricks().get(copyCounter),
								(CompositeBrick) referenceBrick.getNestedBricks().get(i));
					}
					copyCounter++;
				} else {
					copyBrick.getNestedBricks().remove(copyCounter);
				}
			}
		}
		copyCounter = 0;
		if (referenceBrick.hasSecondaryList()) {
			for (int i = 0; i < referenceBrick.getSecondaryNestedBricks().size(); i++) {
				if (referenceBrick.getSecondaryNestedBricks().get(i).getCheckBox() != null) {
					if (referenceBrick.getSecondaryNestedBricks().get(i).getCheckBox().isChecked()) {
						if (referenceBrick.getNestedBricks().get(i) instanceof CompositeBrick) {
							removeUnselectedBricksInCompositeBricks((CompositeBrick) copyBrick.getSecondaryNestedBricks().get(copyCounter),
									(CompositeBrick) referenceBrick.getSecondaryNestedBricks().get(i));
						}
						copyCounter++;
					} else {
						copyBrick.getSecondaryNestedBricks().remove(copyCounter);
					}
				}
			}
		}
	}

	public void delete(@NonNull List<Brick> bricksToDelete, Sprite parent) {
		for (Brick brick : bricksToDelete) {
			Script script = brick.getScript();
			if (brick instanceof UserDefinedReceiverBrick) {
				UserDefinedBrick userDefinedBrick = ((UserDefinedReceiverBrick) brick).getUserDefinedBrick();
				parent.removeUserDefinedBrick(userDefinedBrick);
			}

			if (brick instanceof ScriptBrick) {
				parent.removeScript(script);
			} else {
				if (!bricksToDelete.contains(brick.getParent())) {
					if (brick instanceof CompositeBrick) {
						List<Brick> unselectedBricks = getAllUnSelectedChildBricksOfCompositeBrick((CompositeBrick) brick);
						addUnselectedBricksToNextUnselectedParentBrick(unselectedBricks, brick);
					}
					script.removeBrick(brick);
				}
			}
		}
	}

	private List<Brick> getAllUnSelectedChildBricksOfCompositeBrick(CompositeBrick compositeBrick) {
		List<Brick> unselectedBricks = new ArrayList<>();
		for (Brick childBrick : compositeBrick.getNestedBricks()) {
			if (childBrick.getCheckBox().isChecked()) {
				if (childBrick instanceof CompositeBrick) {
					unselectedBricks.addAll(getAllUnSelectedChildBricksOfCompositeBrick((CompositeBrick) childBrick));
				}
			} else {
				unselectedBricks.add(childBrick);
			}
		}
		if (compositeBrick.hasSecondaryList()) {
			for (Brick childBrick : compositeBrick.getSecondaryNestedBricks()) {
				if (childBrick.getCheckBox().isChecked()) {
					if (childBrick instanceof CompositeBrick) {
						unselectedBricks.addAll(getAllUnSelectedChildBricksOfCompositeBrick((CompositeBrick) childBrick));
					}
				} else {
					unselectedBricks.add(childBrick);
				}
			}
		}
		return unselectedBricks;
	}

	private void addUnselectedBricksToNextUnselectedParentBrick(List<Brick> unselectedBricks,
			Brick lastSelectedBrick) {
		Brick parentBrick = lastSelectedBrick.getParent();
		if (parentBrick instanceof ScriptBrick) {
			int pos =
					parentBrick.getScript().getBrickList().indexOf(lastSelectedBrick);
			parentBrick.getScript().getBrickList().addAll(pos,
					unselectedBricks);
		} else if (parentBrick instanceof CompositeBrick) {
			int pos =
					((CompositeBrick) parentBrick).getNestedBricks().indexOf(lastSelectedBrick);
			((CompositeBrick) parentBrick).getNestedBricks().addAll(pos,
					unselectedBricks);
		} else if ((parentBrick instanceof IfLogicBeginBrick.ElseBrick)) {
			int pos = ((IfLogicBeginBrick) parentBrick.getParent())
					.getSecondaryNestedBricks().indexOf(lastSelectedBrick);
			((IfLogicBeginBrick) parentBrick.getParent())
					.getSecondaryNestedBricks().addAll(pos, unselectedBricks);
		} else if ((parentBrick instanceof PhiroIfLogicBeginBrick.ElseBrick)) {
			int pos = ((PhiroIfLogicBeginBrick) parentBrick.getParent())
					.getSecondaryNestedBricks().indexOf(lastSelectedBrick);
			((PhiroIfLogicBeginBrick) parentBrick.getParent())
					.getSecondaryNestedBricks().addAll(pos, unselectedBricks);
		}
	}
}

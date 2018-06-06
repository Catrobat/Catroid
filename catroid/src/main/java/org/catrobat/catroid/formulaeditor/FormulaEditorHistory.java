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
package org.catrobat.catroid.formulaeditor;


import org.catrobat.catroid.content.bricks.Brick;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class FormulaEditorHistory {

	private static final int MAXIMUM_HISTORY_LENGTH = 32;
	private Stack<State> undoStack;
	private Stack<State>redoStack;
	private State current;
	private boolean hasUnsavedChanges = false;
	Map<Brick.BrickField,InternFormulaState> initialStates;


	public FormulaEditorHistory(State state) {
		undoStack = new Stack<State>();
		redoStack = new Stack<State>();
		initialStates = new HashMap<Brick.BrickField,InternFormulaState>();
		current = state;
	}

	public void push(State state) {

		if (current != null && current.equals(state)) {
			return;
		}
		if (current != null) {
			undoStack.push(current);
		}
		if(!initialStates.containsKey(current.brickField)) {
			initialStates.put(current.brickField,current.internFormulaState);
		}
		current = state;
		redoStack.clear();
		hasUnsavedChanges = true;
		if (undoStack.size() > MAXIMUM_HISTORY_LENGTH) {
			undoStack.removeElementAt(0);
		}
	}

	public State backward() {
		redoStack.push(current);
		hasUnsavedChanges = true;
		if (!undoStack.empty() ) {
			current = undoStack.pop();
		}
		return current;
	}

	public State forward() {
		undoStack.push(current);
		hasUnsavedChanges = true;
		if (!redoStack.empty()) {
			current = redoStack.pop();
		}
		return current;
	}

	public void updateCurrentSelection(InternFormulaTokenSelection internFormulaTokenSelection) {
		current.internFormulaState.setSelection(internFormulaTokenSelection);
	}

	public void init(State state) {
		current = state;
	}

	public void clear() {
		undoStack.clear();
		redoStack.clear();
		initialStates.clear();
		current = null;
		hasUnsavedChanges = false;
	}

	public void updateCurrentCursor(int cursorPosition) {
		current.internFormulaState.setExternCursorPosition(cursorPosition);
	}

	public boolean undoIsPossible() {
		return !undoStack.empty();
	}

	public boolean redoIsPossible() {
		return !redoStack.empty();
	}

	public boolean hasUnsavedChanges() {
		return hasUnsavedChanges;
	}

	public Brick.BrickField getCurrentBrickField() {
		return current.brickField;
	}

	public Brick.BrickField getPeekStackUndo() {
		return undoStack.peek().brickField;
	}

	public Brick.BrickField getPeekStackRedo() {
		return redoStack.peek().brickField;
	}

	public Map<Brick.BrickField, InternFormulaState> getInitialValues() {
		return initialStates;
	}

	public void changesSaved() {
		hasUnsavedChanges = false;
	}
}

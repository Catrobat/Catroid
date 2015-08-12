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
package org.catrobat.catroid.formulaeditor;

import java.util.Stack;

public class FormulaEditorHistory {

	private static final int MAXIMUM_HISTORY_LENGTH = 32;
	private Stack<InternFormulaState> undoStack = null;
	private Stack<InternFormulaState> redoStack = null;
	private InternFormulaState current = null;
	private boolean hasUnsavedChanges = false;

	public FormulaEditorHistory(InternFormulaState internFormulaState) {
		current = internFormulaState;
		undoStack = new Stack<InternFormulaState>();
		redoStack = new Stack<InternFormulaState>();
	}

	public void push(InternFormulaState internFormulaState) {

		if (current != null && current.equals(internFormulaState)) {
			return;
		}
		if (current != null) {
			undoStack.push(current);
		}
		current = internFormulaState;
		redoStack.clear();
		hasUnsavedChanges = true;
		if (undoStack.size() > MAXIMUM_HISTORY_LENGTH) {
			undoStack.removeElementAt(0);
		}
	}

	public InternFormulaState backward() {
		redoStack.push(current);
		hasUnsavedChanges = true;
		if (!undoStack.empty()) {
			current = undoStack.pop();
		}
		return current;
	}

	public InternFormulaState forward() {
		undoStack.push(current);
		hasUnsavedChanges = true;
		if (!redoStack.empty()) {
			current = redoStack.pop();
		}
		return current;
	}

	public void updateCurrentSelection(InternFormulaTokenSelection internFormulaTokenSelection) {
		current.setSelection(internFormulaTokenSelection);
	}

	public void init(InternFormulaState internFormulaState) {
		current = internFormulaState;
	}

	public void clear() {
		undoStack.clear();
		redoStack.clear();
		current = null;
		hasUnsavedChanges = false;
	}

	public void updateCurrentCursor(int cursorPosition) {
		current.setExternCursorPosition(cursorPosition);
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

	public void changesSaved() {
		hasUnsavedChanges = false;
	}
}

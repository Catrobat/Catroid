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

package org.catrobat.catroid.content;

import org.catrobat.catroid.content.commands.MediaCommand;

import java.util.Stack;

public abstract class MediaHistory {
	protected Stack<MediaCommand> redoStack = new Stack<>();
	protected Stack<MediaCommand> undoStack = new Stack<>();

	public void undo() {
		MediaCommand command = undoStack.pop();
		command.undo();
		redoStack.push(command);
	}

	public void redo() {
		MediaCommand command = redoStack.pop();
		command.execute();
		undoStack.push(command);
	}

	public void add(MediaCommand command) {
		undoStack.push(command);
		redoStack.clear();
	}

	public void update() {
		for (MediaCommand command : undoStack) {
			command.update();
		}

		for (MediaCommand command : redoStack) {
			command.update();
		}
	}

	public boolean isUndoable() {
		return !undoStack.empty();
	}

	public boolean isRedoable() {
		return !redoStack.empty();
	}
}

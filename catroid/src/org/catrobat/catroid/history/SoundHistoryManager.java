/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2013 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://developer.catrobat.org/license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *  
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.history;

import org.catrobat.catroid.commands.Command;

import java.util.EmptyStackException;
import java.util.Stack;

public class SoundHistoryManager {
	private static final SoundHistoryManager INSTANCE = new SoundHistoryManager();

	private Stack<Command> undoStack, redoStack;

	private SoundHistoryManager() {
		undoStack = new Stack<Command>();
		redoStack = new Stack<Command>();
	}

	public static SoundHistoryManager getInstance() {
		return INSTANCE;
	}

	public void executeCommand(Command command) {
		command.execute();
		undoStack.push(command);
	}

	public void undo() {
		try {
			Command prevCommand = undoStack.pop();
			prevCommand.undo();
			redoStack.push(prevCommand);
		} catch (EmptyStackException ex) {
		}
	}

	public void redo() {
		try {
			Command nextCommand = redoStack.pop();
			nextCommand.execute();
			undoStack.push(nextCommand);
		} catch (EmptyStackException ex) {
		}
	}

}

/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2014 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
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
package org.catrobat.catroid.test.cucumber.util;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.BrickBaseType;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.List;

public class PrintBrick extends BrickBaseType {
	private static final long serialVersionUID = 1L;
	private String text;
	private OutputStream outputStream;

	public PrintBrick(Sprite sprite, String text) {
		this.outputStream = System.//
		out;
		this.sprite = sprite;
		this.text = text;
	}

	public PrintBrick() {
		this.outputStream = System.//
		out;
	}

	public void setOutputStream(OutputStream outputStream) {
		this.outputStream = outputStream;
	}

	@Override
	public int getRequiredResources() {
		return NO_RESOURCES;
	}

	@Override
	public Brick copyBrickForSprite(Sprite sprite, Script script) {
		PrintBrick copyBrick = (PrintBrick) clone();
		copyBrick.sprite = sprite;
		return copyBrick;
	}

	@Override
	public Brick clone() {
		PrintBrick brick = new PrintBrick(sprite, text);
		brick.outputStream = this.outputStream;
		return brick;
	}

	@Override
	public List<SequenceAction> addActionToSequence(SequenceAction sequence) {
		sequence.addAction(new PrintAction(text, outputStream));
		return null;
	}

	private class PrintAction extends Action {
		private String text;
		private PrintStream printStream;

		PrintAction(String text, OutputStream outputStream) {
			this.text = text;
			this.printStream = new PrintStream(outputStream);
		}

		@Override
		public boolean act(float v) {
			printStream.print(text);
			return true;
		}
	}
}

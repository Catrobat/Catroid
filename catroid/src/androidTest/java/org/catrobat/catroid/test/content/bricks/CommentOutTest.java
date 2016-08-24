/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2016 The Catrobat Team
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
package org.catrobat.catroid.test.content.bricks;

import android.test.AndroidTestCase;

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import org.catrobat.catroid.content.ActionFactory;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.WaitBrick;

public class CommentOutTest extends AndroidTestCase {
	private Sprite sprite;
	private StartScript script;
	private SequenceAction sequence;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		sprite = new Sprite("testSprite");
		script = new StartScript();
		sequence = ActionFactory.sequence();
	}

	public void testCommentOutSimple() {

		script.addBrick(new WaitBrick(1));
		script.addBrick(new WaitBrick(1));
		script.addBrick(new WaitBrick(1));

		Brick disabledBrick = new WaitBrick(1);
		disabledBrick.setCommentedOut(true);
		script.addBrick(disabledBrick);

		script.run(sprite, sequence);

		assertEquals("action of disabled brick should not be in sequence:", sequence.getActions().size, 3);
	}

	public void testCommentOutScript() {
		script.addBrick(new WaitBrick(1));
		script.addBrick(new WaitBrick(1));

		script.setCommentedOut(true);

		script.run(sprite, sequence);

		assertEquals("no action of a disabled script should be in the sequence:", sequence.getActions().size, 0);
	}
}


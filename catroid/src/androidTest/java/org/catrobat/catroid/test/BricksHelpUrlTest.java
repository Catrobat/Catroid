/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2026 The Catrobat Team
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

import org.catrobat.catroid.content.bricks.BrickDocumentation;
import org.catrobat.catroid.content.bricks.WhenGamepadButtonBrick;
import org.catrobat.catroid.content.bricks.WhenStartedBrick;
import org.catrobat.catroid.content.bricks.WaitBrick;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class BricksHelpUrlTest {
	@Test
	public void linksToTheArticleForTheBrick() {
		assertEquals("https://catrobat.org/docs/brickdocumentation/control/wait-1-second/",
				BrickDocumentation.getUrl(WaitBrick.class));
		assertEquals("https://catrobat.org/docs/brickdocumentation/event/when-scene-starts/",
				BrickDocumentation.getUrl(WhenStartedBrick.class));
	}

	@Test
	public void undocumentedBrickHasNoLink() {
		assertNull(BrickDocumentation.getUrl(WhenGamepadButtonBrick.class));
	}
}

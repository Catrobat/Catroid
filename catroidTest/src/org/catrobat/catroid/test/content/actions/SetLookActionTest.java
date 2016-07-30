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
package org.catrobat.catroid.test.content.actions;

import android.test.InstrumentationTestCase;

import com.badlogic.gdx.scenes.scene2d.Action;

import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.content.ActionFactory;
import org.catrobat.catroid.content.Sprite;

import java.io.File;

public class SetLookActionTest extends InstrumentationTestCase {

	private File testImage;

	@Override
	protected void setUp() throws Exception {
		final String imagePath = Constants.DEFAULT_ROOT + "/testImage.png";
		testImage = new File(imagePath);
	}

	public void testSetLook() {

		Sprite sprite = new Sprite("new sprite");
		LookData lookData = new LookData();
		lookData.setLookFilename(testImage.getName());
		lookData.setLookName("testImage");
		sprite.getLookDataList().add(lookData);

		ActionFactory factory = sprite.getActionFactory();
		Action action = factory.createSetLookAction(sprite, lookData);
		action.act(1.0f);
		assertNotNull("current Look is null", sprite.look);
	}
}

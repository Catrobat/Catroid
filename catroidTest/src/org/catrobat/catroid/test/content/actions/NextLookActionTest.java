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

public class NextLookActionTest extends InstrumentationTestCase {

	private File testImage;

	@Override
	protected void setUp() throws Exception {

		final String imagePath = Constants.DEFAULT_ROOT + "/testImage.png";
		testImage = new File(imagePath);
	}

	public void testNextLook() {

		Sprite sprite = new Sprite("cat");

		LookData lookData1 = new LookData();
		lookData1.setLookFilename(testImage.getName());
		lookData1.setLookName("testImage1");
		sprite.getLookDataList().add(lookData1);

		LookData lookData2 = new LookData();
		lookData2.setLookFilename(testImage.getName());
		lookData2.setLookName("testImage2");
		sprite.getLookDataList().add(lookData2);

		LookData lookData3 = new LookData();
		lookData3.setLookFilename(testImage.getName());
		lookData3.setLookName("testImage3");
		sprite.getLookDataList().add(lookData3);

		ActionFactory factory = sprite.getActionFactory();
		Action setLookAction = factory.createSetLookAction(sprite, lookData2);
		Action nextLookAction = factory.createNextLookAction(sprite);
		Action nextLookAction2 = factory.createNextLookAction(sprite);

		setLookAction.act(1.0f);
		nextLookAction.act(1.0f);

		assertEquals("Look is not next look", lookData3, sprite.look.getLookData());

		nextLookAction2.act(1.0f);

		assertEquals("Look is not first look", lookData1, sprite.look.getLookData());
	}

	public void testLookGalleryNull() {

		Sprite sprite = new Sprite("cat");
		ActionFactory factory = sprite.getActionFactory();
		Action nextLookAction = factory.createNextLookAction(sprite);
		nextLookAction.act(1.0f);

		assertEquals("Look is not null", null, sprite.look.getLookData());
	}

	public void testLookGalleryWithOneAndNoLook() {
		Sprite sprite = new Sprite("cat");

		LookData lookData1 = new LookData();
		lookData1.setLookFilename(testImage.getName());
		lookData1.setLookName("testImage1");
		sprite.getLookDataList().add(lookData1);

		ActionFactory factory = sprite.getActionFactory();
		Action nextLookAction1 = factory.createNextLookAction(sprite);

		nextLookAction1.act(1.0f);

		assertNull("No Custume should be set.", sprite.look.getLookData());

		Action setLookAction = factory.createSetLookAction(sprite, lookData1);
		Action nextLookAction2 = factory.createNextLookAction(sprite);

		setLookAction.act(1.0f);
		nextLookAction2.act(1.0f);

		assertEquals("Wrong look after executing NextLookBrick with just one look", lookData1,
				sprite.look.getLookData());
	}
}

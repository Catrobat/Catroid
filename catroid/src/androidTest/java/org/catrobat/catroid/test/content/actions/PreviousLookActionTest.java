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
package org.catrobat.catroid.test.content.actions;

import android.support.test.runner.AndroidJUnit4;

import com.badlogic.gdx.scenes.scene2d.Action;

import org.catrobat.catroid.common.FlavoredConstants;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.content.ActionFactory;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.NextLookAction;
import org.catrobat.catroid.content.actions.PreviousLookAction;
import org.catrobat.catroid.content.actions.SetLookAction;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;

@RunWith(AndroidJUnit4.class)
public class PreviousLookActionTest {

	private File testImage;
	private Sprite sprite;
	private ActionFactory actionFactory;

	@Before
	public void setUp() throws Exception {
		testImage = new File(FlavoredConstants.DEFAULT_ROOT_DIRECTORY, "testImage.png");
		sprite = new Sprite("cat");
		actionFactory = sprite.getActionFactory();
	}

	@Test
	public void testPreviousLook() {
		LookData lookData1 = new LookData();
		lookData1.setFile(testImage);
		lookData1.setName("testImage1");
		sprite.getLookList().add(lookData1);

		LookData lookData2 = new LookData();
		lookData2.setFile(testImage);
		lookData2.setName("testImage2");
		sprite.getLookList().add(lookData2);

		SetLookAction setLookAction = (SetLookAction) actionFactory.createSetLookAction(sprite, lookData1);
		NextLookAction nextLookAction = (NextLookAction) actionFactory.createNextLookAction(sprite);
		PreviousLookAction previousLookAction = (PreviousLookAction) actionFactory.createPreviousLookAction(sprite);

		setLookAction.act(1.0f);
		nextLookAction.act(1.0f);

		assertEquals(lookData2, sprite.look.getLookData());

		previousLookAction.act(1.0f);

		assertEquals(lookData1, sprite.look.getLookData());
	}

	@Test
	public void testLastLook() {
		LookData lookData1 = new LookData();
		lookData1.setFile(testImage);
		lookData1.setName("testImage1");
		sprite.getLookList().add(lookData1);

		LookData lookData2 = new LookData();
		lookData2.setFile(testImage);
		lookData2.setName("testImage2");
		sprite.getLookList().add(lookData2);

		LookData lookData3 = new LookData();
		lookData3.setFile(testImage);
		lookData3.setName("testImage3");
		sprite.getLookList().add(lookData3);

		Action setLookAction = actionFactory.createSetLookAction(sprite, lookData1);
		Action previousLookAction = actionFactory.createPreviousLookAction(sprite);

		setLookAction.act(1.0f);
		previousLookAction.act(1.0f);

		assertEquals(lookData3.getName(), sprite.look.getLookData().getName());
	}

	@Test
	public void testLookGalleryNull() {
		Action previousLookAction = actionFactory.createPreviousLookAction(sprite);
		previousLookAction.act(1.0f);

		assertNull(sprite.look.getLookData());
	}

	@Test
	public void testLookGalleryWithOneLook() {
		LookData lookData1 = new LookData();
		lookData1.setFile(testImage);
		lookData1.setName("testImage1");
		sprite.getLookList().add(lookData1);

		Action setLookAction = actionFactory.createSetLookAction(sprite, lookData1);
		Action previousLookAction = actionFactory.createPreviousLookAction(sprite);

		setLookAction.act(1.0f);
		previousLookAction.act(1.0f);

		assertEquals(lookData1.getName(), sprite.look.getLookData().getName());
	}

	@Test
	public void testPreviousLookWithNoLookSet() {
		Action previousLookAction = actionFactory.createPreviousLookAction(sprite);

		LookData lookData1 = new LookData();
		lookData1.setFile(testImage);
		lookData1.setName("testImage1");
		sprite.getLookList().add(lookData1);

		previousLookAction.act(1.0f);

		assertNull(sprite.look.getLookData());
	}
}

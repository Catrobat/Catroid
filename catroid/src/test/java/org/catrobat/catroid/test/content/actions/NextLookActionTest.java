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

import com.badlogic.gdx.scenes.scene2d.Action;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.common.ScreenValues;
import org.catrobat.catroid.content.ActionFactory;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.SingleSprite;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.test.MockUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;

import java.io.File;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;

@RunWith(JUnit4.class)
public class NextLookActionTest {

	@Before
	public void setUp() throws Exception {
		createProject();
	}

	@Test
	public void testNextLook() {
		Sprite sprite = new SingleSprite("cat");

		LookData lookData1 = new LookData();
		lookData1.setFile(Mockito.mock(File.class));
		lookData1.setName("testImage1");
		sprite.getLookList().add(lookData1);

		LookData lookData2 = new LookData();
		lookData2.setFile(Mockito.mock(File.class));
		lookData2.setName("testImage2");
		sprite.getLookList().add(lookData2);

		ActionFactory factory = sprite.getActionFactory();
		Action setLookAction = factory.createSetLookAction(sprite, lookData1);
		Action nextLookAction = factory.createNextLookAction(sprite);

		setLookAction.act(1.0f);
		nextLookAction.act(1.0f);

		assertEquals(lookData2, sprite.look.getLookData());
	}

	@Test
	public void testLastLook() {
		Sprite sprite = new SingleSprite("cat");

		LookData lookData1 = new LookData();
		lookData1.setFile(Mockito.mock(File.class));
		lookData1.setName("testImage1");
		sprite.getLookList().add(lookData1);

		LookData lookData2 = new LookData();
		lookData2.setFile(Mockito.mock(File.class));
		lookData2.setName("testImage2");
		sprite.getLookList().add(lookData2);

		LookData lookData3 = new LookData();
		lookData3.setFile(Mockito.mock(File.class));
		lookData3.setName("testImage3");
		sprite.getLookList().add(lookData3);

		ActionFactory factory = sprite.getActionFactory();
		Action setLookAction = factory.createSetLookAction(sprite, lookData3);
		Action nextLookAction = factory.createNextLookAction(sprite);

		setLookAction.act(1.0f);
		nextLookAction.act(1.0f);

		assertEquals(lookData1, sprite.look.getLookData());
	}

	@Test
	public void testLookGalleryNull() {

		Sprite sprite = new SingleSprite("cat");
		ActionFactory factory = sprite.getActionFactory();
		Action nextLookAction = factory.createNextLookAction(sprite);
		nextLookAction.act(1.0f);

		assertNull(sprite.look.getLookData());
	}

	@Test
	public void testLookGalleryWithOneLook() {
		Sprite sprite = new SingleSprite("cat");

		LookData lookData1 = new LookData();
		lookData1.setFile(Mockito.mock(File.class));
		lookData1.setName("testImage1");
		sprite.getLookList().add(lookData1);

		ActionFactory factory = sprite.getActionFactory();
		Action setLookAction = factory.createSetLookAction(sprite, lookData1);
		Action nextLookAction = factory.createNextLookAction(sprite);

		setLookAction.act(1.0f);
		nextLookAction.act(1.0f);

		assertEquals(lookData1, sprite.look.getLookData());
	}

	@Test
	public void testNextLookWithNoLookSet() {

		Sprite sprite = new SingleSprite("cat");

		ActionFactory factory = sprite.getActionFactory();
		Action nextLookAction = factory.createNextLookAction(sprite);

		LookData lookData1 = new LookData();
		lookData1.setFile(Mockito.mock(File.class));
		lookData1.setName("testImage1");
		sprite.getLookList().add(lookData1);

		nextLookAction.act(1.0f);

		assertNull(sprite.look.getLookData());
	}

	private void createProject() {
		Project project = new Project(MockUtil.mockContextForProject(), getClass().getSimpleName());
		Sprite firstSprite = new SingleSprite("cat");
		Script testScript = new StartScript();

		firstSprite.addScript(testScript);
		project.getDefaultScene().addSprite(firstSprite);

		ProjectManager.getInstance().setCurrentProject(project);
		ProjectManager.getInstance().setCurrentSprite(firstSprite);

		ScreenValues.SCREEN_HEIGHT = 200;
		ScreenValues.SCREEN_WIDTH = 200;
	}
}

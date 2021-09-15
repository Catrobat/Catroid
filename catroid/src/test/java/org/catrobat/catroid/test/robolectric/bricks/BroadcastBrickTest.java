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

package org.catrobat.catroid.test.robolectric.bricks;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Build;
import android.view.View;
import android.widget.Spinner;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Nameable;
import org.catrobat.catroid.content.BroadcastScript;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.bricks.BroadcastBrick;
import org.catrobat.catroid.content.bricks.BroadcastMessageBrick;
import org.catrobat.catroid.content.bricks.BroadcastReceiverBrick;
import org.catrobat.catroid.content.bricks.BroadcastWaitBrick;
import org.catrobat.catroid.ui.SpriteActivity;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.ParameterizedRobolectricTestRunner;
import org.robolectric.Robolectric;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;

import java.util.Arrays;
import java.util.Collection;

import androidx.appcompat.app.AppCompatActivity;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

@RunWith(ParameterizedRobolectricTestRunner.class)
@Config(sdk = {Build.VERSION_CODES.P})
public class BroadcastBrickTest {

	private static final String INITAL_MESSAGE = "initialMessage";
	private int spinnerId = R.id.brick_broadcast_spinner;

	private AppCompatActivity activity;

	@ParameterizedRobolectricTestRunner.Parameters(name = "{0}")
	public static Collection<Object[]> data() {
		return Arrays.asList(new Object[][]{
				{BroadcastBrick.class.getSimpleName(), new BroadcastBrick(INITAL_MESSAGE)},
				{BroadcastWaitBrick.class.getSimpleName(), new BroadcastWaitBrick(INITAL_MESSAGE)},
				{BroadcastReceiverBrick.class.getSimpleName(), new BroadcastReceiverBrick(new BroadcastScript(INITAL_MESSAGE))},
		});
	}

	@SuppressWarnings("PMD.UnusedPrivateField")
	public String name;
	private BroadcastMessageBrick broadcastBrick;

	public BroadcastBrickTest(String name, BroadcastMessageBrick broadcastBrick) {
		this.name = name;
		this.broadcastBrick = broadcastBrick;
	}

	@Before
	public void setUp() throws Exception {
		ActivityController<SpriteActivity> activityController = Robolectric.buildActivity(SpriteActivity.class);
		activity = activityController.get();

		createProject(activity);

		assertEquals("initialMessage", ((Nameable) getBrickSpinner().getSelectedItem()).getName());
	}

	@After
	public void tearDown() {
		ProjectManager.getInstance().resetProjectManager();
	}

	private Spinner getBrickSpinner() {
		View brickView = broadcastBrick.getView(activity);
		assertNotNull(brickView);

		Spinner brickSpinner = (Spinner) brickView.findViewById(spinnerId);
		assertNotNull(brickSpinner);

		return brickSpinner;
	}

	@Test
	public void testBroadcastAddNewMessage() {
		assertEquals("initialMessage", ((Nameable) getBrickSpinner().getSelectedItem()).getName());
		broadcastBrick.getOkButtonListener(activity).onPositiveButtonClick(Mockito.mock(DialogInterface.class),
				"newMessage");
		assertEquals("newMessage", ((Nameable) getBrickSpinner().getSelectedItem()).getName());
	}

	@Test
	public void testBroadcastCancelNewMessage() {
		assertEquals("initialMessage", ((Nameable) getBrickSpinner().getSelectedItem()).getName());
		broadcastBrick.getCanceledListener().onCancel(Mockito.mock(DialogInterface.class));
		assertEquals("initialMessage", ((Nameable) getBrickSpinner().getSelectedItem()).getName());
	}

	@Test
	public void testBroadcastNegativeButtonNewMessage() {
		assertEquals("initialMessage", ((Nameable) getBrickSpinner().getSelectedItem()).getName());
		int anyInt = 0;
		broadcastBrick.getNegativeButtonListener().onClick(Mockito.mock(DialogInterface.class), anyInt);
		assertEquals("initialMessage", ((Nameable) getBrickSpinner().getSelectedItem()).getName());
	}

	public void createProject(Activity activity) {
		Project project = new Project(activity, getClass().getSimpleName());
		Sprite sprite = new Sprite("testSprite");
		project.getDefaultScene().addSprite(sprite);
		ProjectManager.getInstance().setCurrentProject(project);
		ProjectManager.getInstance().setCurrentSprite(sprite);
		ProjectManager.getInstance().setCurrentlyEditedScene(project.getDefaultScene());
		project.getBroadcastMessageContainer().addBroadcastMessage("unusedMessage");
		project.getBroadcastMessageContainer().addBroadcastMessage("initialMessage");
	}
}

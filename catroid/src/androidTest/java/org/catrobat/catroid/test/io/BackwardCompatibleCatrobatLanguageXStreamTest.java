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
package org.catrobat.catroid.test.io;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.catrobat.catroid.common.FlavoredConstants;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.io.StorageOperations;
import org.catrobat.catroid.io.XstreamSerializer;
import org.catrobat.catroid.io.ZipArchiver;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

@RunWith(AndroidJUnit4.class)
public class BackwardCompatibleCatrobatLanguageXStreamTest {

	private static final String ZIP_FILENAME_FALLING_BALLS = "Falling_balls.catrobat";
	private static final String ZIP_FILENAME_COLOR_LEANER_BALLOONS = "Color_Learner_-_Balloons.catrobat";
	private static final String ZIP_FILENAME_PONG_STARTER = "Pong_Starter.catrobat";
	private static final String ZIP_FILENAME_WHIP = "Whip.catrobat";
	private static final String ZIP_FILENAME_AIR_FIGHT = "Air_fight_0.5.catrobat";
	private static final String ZIP_FILENAME_XRAY_PHONE = "X-Ray_phone.catrobat";
	private static final String ZIP_FILENAME_ALL_BRICKS = "All_Bricks.catrobat";
	private static final String ZIP_FILENAME_NOTE_AND_SPEAK_BRICK = "Note_And_Speak_Brick.catrobat";
	private static final String ZIP_FILENAME_GHOST_EFFECT_BRICKS = "Ghost_Effect_Bricks.catrobat";
	private static final String ZIP_FILENAME_LEGO_NXT = "old-lego-nxt.catrobat";

	private static final String PROJECT_NAME_FALLING_BALLS = "falling balls";
	private static final String PROJECT_NAME_COLOR_LEANER_BALLOONS = "color learner - balloons";
	private static final String PROJECT_NAME_PONG_STARTER = "pong starter";
	private static final String PROJECT_NAME_WHIP = "whip";
	private static final String PROJECT_NAME_AIR_FIGHT = "air fight 0.5";
	private static final String PROJECT_NAME_XRAY_PHONE = "x-ray phone";
	private static final String PROJECT_NAME_ALL_BRICKS = "all bricks";
	private static final String PROJECT_NAME_NOTE_AND_SPEAK_BRICK = "noteandspeakbrick";
	private static final String PROJECT_NAME_GHOST_EFFECT_BRICKS = "ghosteffectbricks";
	private static final String PROJECT_NAME_LEGO_NXT = "oldlegonxt";

	@Test
	public void testLoadingProjectsOfCatrobatLanguageVersion08() throws Exception {
		copyProjectFromAssets(ZIP_FILENAME_FALLING_BALLS, PROJECT_NAME_FALLING_BALLS);
		copyProjectFromAssets(ZIP_FILENAME_COLOR_LEANER_BALLOONS, PROJECT_NAME_COLOR_LEANER_BALLOONS);

		Project fallingBallsProject = XstreamSerializer.getInstance()
				.loadProject(PROJECT_NAME_FALLING_BALLS, InstrumentationRegistry.getTargetContext());

		assertNotNull(fallingBallsProject);
		assertEquals(PROJECT_NAME_FALLING_BALLS, fallingBallsProject.getName().toLowerCase(Locale.getDefault()));

		Project colorLeanerBalloonsProject = XstreamSerializer.getInstance()
				.loadProject(PROJECT_NAME_COLOR_LEANER_BALLOONS, InstrumentationRegistry.getTargetContext());

		assertNotNull(colorLeanerBalloonsProject);
		assertEquals(PROJECT_NAME_COLOR_LEANER_BALLOONS, colorLeanerBalloonsProject.getName().toLowerCase(Locale.getDefault()));

		StorageOperations.deleteDir(new File(FlavoredConstants.DEFAULT_ROOT_DIRECTORY, PROJECT_NAME_FALLING_BALLS));
		StorageOperations.deleteDir(new File(FlavoredConstants.DEFAULT_ROOT_DIRECTORY, PROJECT_NAME_COLOR_LEANER_BALLOONS));
	}

	@Test
	public void testLoadingProjectsOfCatrobatLanguageVersion09() throws Exception {
		copyProjectFromAssets(ZIP_FILENAME_PONG_STARTER, PROJECT_NAME_PONG_STARTER);
		copyProjectFromAssets(ZIP_FILENAME_WHIP, PROJECT_NAME_WHIP);

		Project pongStarterProject = XstreamSerializer.getInstance()
				.loadProject(PROJECT_NAME_PONG_STARTER, InstrumentationRegistry.getTargetContext());

		assertNotNull(pongStarterProject);
		assertEquals(PROJECT_NAME_PONG_STARTER, pongStarterProject.getName().toLowerCase(Locale.getDefault()));

		Project whipProject = XstreamSerializer.getInstance()
				.loadProject(PROJECT_NAME_WHIP, InstrumentationRegistry.getTargetContext());

		assertNotNull(whipProject);
		assertEquals(PROJECT_NAME_WHIP, whipProject.getName().toLowerCase(Locale.getDefault()));

		StorageOperations.deleteDir(new File(FlavoredConstants.DEFAULT_ROOT_DIRECTORY, PROJECT_NAME_PONG_STARTER));
		StorageOperations.deleteDir(new File(FlavoredConstants.DEFAULT_ROOT_DIRECTORY, PROJECT_NAME_WHIP));
	}

	@Test
	public void testLoadingProjectsOfCatrobatLanguageVersion091() throws Exception {
		copyProjectFromAssets(ZIP_FILENAME_AIR_FIGHT, PROJECT_NAME_AIR_FIGHT);
		copyProjectFromAssets(ZIP_FILENAME_XRAY_PHONE, PROJECT_NAME_XRAY_PHONE);
		copyProjectFromAssets(ZIP_FILENAME_ALL_BRICKS, PROJECT_NAME_ALL_BRICKS);

		Project airFightProject = XstreamSerializer.getInstance().loadProject(PROJECT_NAME_AIR_FIGHT,
				InstrumentationRegistry.getTargetContext());

		assertNotNull(airFightProject);
		assertEquals(PROJECT_NAME_AIR_FIGHT, airFightProject.getName().toLowerCase(Locale.getDefault()));

		Project xRayPhoneProject = XstreamSerializer.getInstance().loadProject(PROJECT_NAME_XRAY_PHONE,
				InstrumentationRegistry.getTargetContext());

		assertNotNull(xRayPhoneProject);
		assertEquals(PROJECT_NAME_XRAY_PHONE, xRayPhoneProject.getName().toLowerCase(Locale.getDefault()));

		Project allBricksProject = XstreamSerializer.getInstance().loadProject(PROJECT_NAME_ALL_BRICKS,
				InstrumentationRegistry.getTargetContext());

		assertNotNull(allBricksProject);
		assertEquals(PROJECT_NAME_ALL_BRICKS, allBricksProject.getName().toLowerCase(Locale.getDefault()));

		StorageOperations.deleteDir(new File(FlavoredConstants.DEFAULT_ROOT_DIRECTORY, PROJECT_NAME_AIR_FIGHT));
		StorageOperations.deleteDir(new File(FlavoredConstants.DEFAULT_ROOT_DIRECTORY, PROJECT_NAME_XRAY_PHONE));
		StorageOperations.deleteDir(new File(FlavoredConstants.DEFAULT_ROOT_DIRECTORY, PROJECT_NAME_ALL_BRICKS));
	}

	@Test
	public void testLoadingProjectsOfCatrobatLanguageVersion092() throws Exception {
		copyProjectFromAssets(ZIP_FILENAME_NOTE_AND_SPEAK_BRICK, PROJECT_NAME_NOTE_AND_SPEAK_BRICK);

		Project noteAndSpeakBrickProject = XstreamSerializer.getInstance()
				.loadProject(PROJECT_NAME_NOTE_AND_SPEAK_BRICK, InstrumentationRegistry.getTargetContext());

		assertNotNull(noteAndSpeakBrickProject);
		assertEquals(PROJECT_NAME_NOTE_AND_SPEAK_BRICK, noteAndSpeakBrickProject.getName().toLowerCase(Locale.getDefault()));

		StorageOperations.deleteDir(new File(FlavoredConstants.DEFAULT_ROOT_DIRECTORY, PROJECT_NAME_NOTE_AND_SPEAK_BRICK));
	}

	@Test
	public void testLoadingProjectsOfCatrobatLanguageVersion095() throws Exception {
		copyProjectFromAssets(ZIP_FILENAME_GHOST_EFFECT_BRICKS, PROJECT_NAME_GHOST_EFFECT_BRICKS);

		Project ghostBricksProject = XstreamSerializer.getInstance()
				.loadProject(PROJECT_NAME_GHOST_EFFECT_BRICKS, InstrumentationRegistry.getTargetContext());

		assertNotNull(ghostBricksProject);
		assertEquals(PROJECT_NAME_GHOST_EFFECT_BRICKS, ghostBricksProject.getName().toLowerCase(Locale.getDefault()));

		StorageOperations.deleteDir(new File(FlavoredConstants.DEFAULT_ROOT_DIRECTORY, PROJECT_NAME_GHOST_EFFECT_BRICKS));
	}

	@Test
	public void testLoadingLegoNxtProjectsOfCatrobatLanguageVersion092() throws Exception {
		copyProjectFromAssets(ZIP_FILENAME_LEGO_NXT, PROJECT_NAME_LEGO_NXT);

		Project legoProject = XstreamSerializer.getInstance()
				.loadProject(PROJECT_NAME_LEGO_NXT, InstrumentationRegistry.getTargetContext());

		assertNotNull(legoProject);
		assertEquals(PROJECT_NAME_LEGO_NXT, legoProject.getName().toLowerCase(Locale.getDefault()));

		StorageOperations.deleteDir(new File(FlavoredConstants.DEFAULT_ROOT_DIRECTORY, PROJECT_NAME_LEGO_NXT));
	}

	private void copyProjectFromAssets(String assetName, String projectName) throws IOException {
		InputStream inputStream = InstrumentationRegistry.getContext().getAssets().open(assetName);
		new ZipArchiver().unzip(inputStream, new File(FlavoredConstants.DEFAULT_ROOT_DIRECTORY, projectName));
	}
}

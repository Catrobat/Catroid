/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2022 The Catrobat Team
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
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.widget.ListAdapter;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.SetXBrick;
import org.catrobat.catroid.ui.SpriteActivity;
import org.catrobat.catroid.ui.fragment.BrickCategoryFragment;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.fragment.app.Fragment;

import static junit.framework.Assert.assertEquals;
import static junit.framework.TestCase.assertNotNull;

import static org.catrobat.catroid.ui.settingsfragments.SettingsFragment.SETTINGS_MINDSTORMS_EV3_BRICKS_CHECKBOX_PREFERENCE;
import static org.catrobat.catroid.ui.settingsfragments.SettingsFragment.SETTINGS_MINDSTORMS_NXT_BRICKS_CHECKBOX_PREFERENCE;
import static org.catrobat.catroid.ui.settingsfragments.SettingsFragment.SETTINGS_SHOW_ARDUINO_BRICKS;
import static org.catrobat.catroid.ui.settingsfragments.SettingsFragment.SETTINGS_SHOW_EMBROIDERY_BRICKS_CHECKBOX_PREFERENCE;
import static org.catrobat.catroid.ui.settingsfragments.SettingsFragment.SETTINGS_SHOW_JUMPING_SUMO_BRICKS;
import static org.catrobat.catroid.ui.settingsfragments.SettingsFragment.SETTINGS_SHOW_NFC_BRICKS;
import static org.catrobat.catroid.ui.settingsfragments.SettingsFragment.SETTINGS_SHOW_PARROT_AR_DRONE_BRICKS;
import static org.catrobat.catroid.ui.settingsfragments.SettingsFragment.SETTINGS_SHOW_PHIRO_BRICKS_CHECKBOX_PREFERENCE;
import static org.catrobat.catroid.ui.settingsfragments.SettingsFragment.SETTINGS_SHOW_RASPI_BRICKS;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = {Build.VERSION_CODES.P})
public class BrickAddCategoryTest {

	private SpriteActivity activity;
	private BrickCategoryFragment brickCategoryFragment;

	private final List<String> allPeripheralCategories = new ArrayList<>(Arrays.asList(SETTINGS_MINDSTORMS_NXT_BRICKS_CHECKBOX_PREFERENCE,
			SETTINGS_MINDSTORMS_EV3_BRICKS_CHECKBOX_PREFERENCE, SETTINGS_SHOW_PARROT_AR_DRONE_BRICKS,
			SETTINGS_SHOW_PHIRO_BRICKS_CHECKBOX_PREFERENCE,
			SETTINGS_SHOW_ARDUINO_BRICKS, SETTINGS_SHOW_RASPI_BRICKS, SETTINGS_SHOW_NFC_BRICKS,
			SETTINGS_SHOW_EMBROIDERY_BRICKS_CHECKBOX_PREFERENCE, SETTINGS_SHOW_JUMPING_SUMO_BRICKS));
	private final List<String> enabledByThisTestPeripheralCategories = new ArrayList<>();

	@Before
	public void setUp() throws Exception {

		ActivityController<SpriteActivity> activityController = Robolectric.buildActivity(SpriteActivity.class);
		activity = activityController.get();
		createProject(activity);
		activityController.create().resume();

		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(activity);

		for (String category : allPeripheralCategories) {
			boolean categoryEnabled = sharedPreferences.getBoolean(category, false);
			if (!categoryEnabled) {
				sharedPreferences.edit().putBoolean(category, true).commit();
				enabledByThisTestPeripheralCategories.add(category);
			}
		}

		brickCategoryFragment = new BrickCategoryFragment();

		Fragment scriptFragment = activity.getSupportFragmentManager().findFragmentById(R.id.fragment_container);
		assertNotNull(scriptFragment);

		scriptFragment.getFragmentManager().beginTransaction()
				.add(R.id.fragment_container, brickCategoryFragment, BrickCategoryFragment.BRICK_CATEGORY_FRAGMENT_TAG)
				.addToBackStack(BrickCategoryFragment.BRICK_CATEGORY_FRAGMENT_TAG)
				.commit();

		shadowOf(Looper.getMainLooper()).idle();
	}

	public void createProject(Activity activity) {
		Project project = new Project(activity, getClass().getSimpleName());
		Sprite sprite = new Sprite("testSprite");
		Script script = new StartScript();
		script.addBrick(new SetXBrick());
		sprite.addScript(script);
		project.getDefaultScene().addSprite(sprite);
		ProjectManager.getInstance().setCurrentProject(project);
		ProjectManager.getInstance().setCurrentSprite(sprite);
		ProjectManager.getInstance().setCurrentlyEditedScene(project.getDefaultScene());
	}

	@After
	public void tearDown() {
		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(activity);
		for (String category : enabledByThisTestPeripheralCategories) {
			sharedPreferences.edit().putBoolean(category, false).commit();
		}
		enabledByThisTestPeripheralCategories.clear();
	}

	@Test
	public void categoriesTest() {
		List<String> expectedCategories = translateAll(Arrays.asList(
				R.string.category_recently_used,
				R.string.category_embroidery,
				R.string.category_lego_nxt,
				R.string.category_lego_ev3,
				R.string.category_drone,
				R.string.category_jumping_sumo,
				R.string.category_arduino,
				R.string.category_raspi,
				R.string.category_phiro,
				R.string.category_event,
				R.string.category_control,
				R.string.category_motion,
				R.string.category_sound,
				R.string.category_looks,
				R.string.category_pen,
				R.string.category_data,
				R.string.category_device,
				R.string.category_user_bricks,
				R.string.category_assertions));

		ListAdapter listAdapter = brickCategoryFragment.getListAdapter();
		List<String> categoryNames = new ArrayList<>();
		for (int index = 0; index < listAdapter.getCount(); index++) {
			categoryNames.add((String) listAdapter.getItem(index));
		}
		assertEquals(expectedCategories, categoryNames);
	}

	private List<String> translateAll(List<Integer> stringIds) {
		List<String> translated = new ArrayList<>();
		for (Integer stringId : stringIds) {
			translated.add(activity.getString(stringId));
		}
		return translated;
	}
}

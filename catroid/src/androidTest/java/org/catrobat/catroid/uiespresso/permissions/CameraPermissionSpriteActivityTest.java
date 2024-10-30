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

package org.catrobat.catroid.uiespresso.permissions;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;

import org.catrobat.catroid.R;
import org.catrobat.catroid.testsuites.annotations.Cat;
import org.catrobat.catroid.testsuites.annotations.Level;
import org.catrobat.catroid.ui.SpriteActivity;
import org.catrobat.catroid.uiespresso.util.UiTestUtils;
import org.catrobat.catroid.uiespresso.util.rules.FragmentActivityTestRule;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.List;

import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiSelector;

import static android.Manifest.permission.CAMERA;

import static junit.framework.Assert.assertTrue;

import static org.catrobat.catroid.uiespresso.ui.fragment.rvutils.RecyclerViewInteractionWrapper.onRecyclerView;
import static org.catrobat.catroid.uiespresso.util.matchers.BundleMatchers.bundleHasMatchingString;
import static org.hamcrest.core.AllOf.allOf;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasExtras;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasPackage;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class CameraPermissionSpriteActivityTest {

	private Matcher permissionIntentMatcher;
	private Matcher chooserIntentMatcher;
	private UiDevice device;

	@Rule
	public FragmentActivityTestRule<SpriteActivity> baseActivityTestRule = new
			FragmentActivityTestRule<>(SpriteActivity.class, SpriteActivity.EXTRA_FRAGMENT_POSITION, SpriteActivity.FRAGMENT_LOOKS);

	@Before
	public void setUp() throws Exception {
		UiTestUtils.createDefaultTestProject("CameraPermissionSpriteActivityTest");
		device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
		baseActivityTestRule.launchActivity();

		Intents.init();

		final String[] requiredPermission = new String[]{CAMERA};

		permissionIntentMatcher = allOf(
				hasPackage("com.google.android.packageinstaller"),
				hasAction("android.content.pm.action.REQUEST_PERMISSIONS"),
				containsPermissionRequest(Arrays.asList(requiredPermission)));

//		// How to Mock a permission denied result:
//		final int[] denyPermission1 = new int[]{-1};
//		Intent premissionResultData = new Intent();
//		final int[] denyPermission = new int[requiredPermission.length];
//		Arrays.fill(denyPermission, PackageManager.PERMISSION_DENIED);
//		premissionResultData.putExtra("android.content.pm.extra.REQUEST_PERMISSIONS_NAMES", requiredPermission);
//		premissionResultData.putExtra("android.content.pm.extra.REQUEST_PERMISSIONS_RESULTS", denyPermission1);
//		Instrumentation.ActivityResult permissionResult =
//				new Instrumentation.ActivityResult(Activity.RESULT_OK, premissionResultData);
//
//		intending(permissionIntentMatcher).respondWith(permissionResult);

		chooserIntentMatcher = allOf(hasAction("android.intent.action.CHOOSER"),
				hasExtras(bundleHasMatchingString("android.intent.extra.TITLE", "Select Photo")));

		Intent chooserResultData = new Intent();
		Instrumentation.ActivityResult chooserResult =
				new Instrumentation.ActivityResult(Activity.RESULT_CANCELED, chooserResultData);

		intending(chooserIntentMatcher).respondWith(chooserResult);
	}

	@After
	public void tearDown() throws Exception {
		baseActivityTestRule.finishActivity();
		Intents.release();
	}

	@Category({Cat.AppUi.class, Level.Smoke.class, Cat.Educational.class})
	@Test
	public void testSpriteFromCameraPermission() throws UiObjectNotFoundException {
		onView(withId(R.id.button_add))
				.perform(click());

		onView(withId(R.id.dialog_new_look_camera))
				.perform(click());

		intended(permissionIntentMatcher);

		UiObject allowPermissions = device.findObject(new UiSelector().text("ALLOW"));
		assertTrue(allowPermissions.exists());
		allowPermissions.click();

		intended(chooserIntentMatcher);

		onRecyclerView().checkHasNumberOfItems(0);
	}

	public static Matcher<Intent> containsPermissionRequest(final List<String> expectedPermissions) {
		return new TypeSafeMatcher<Intent>() {
			@Override
			public boolean matchesSafely(final Intent intent) {
				String[] intentPermissions = intent.getStringArrayExtra("android.content.pm.extra.REQUEST_PERMISSIONS_NAMES");
				if (intentPermissions == null || intentPermissions.length != expectedPermissions.size()) {
					return false;
				}
				List<String> intentPermissionsList = Arrays.asList(intentPermissions);
				for (String expectedPermission : expectedPermissions) {
					if (!intentPermissionsList.contains(expectedPermission)) {
						return false;
					}
				}
				return true;
			}

			@Override
			public void describeTo(Description description) {
				description.appendText("expected Intent with permissions");
			}
		};
	}
}

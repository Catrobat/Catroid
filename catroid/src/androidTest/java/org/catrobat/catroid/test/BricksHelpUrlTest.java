/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2025 The Catrobat Team
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

import android.util.Log;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.ui.fragment.CategoryBricksFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import androidx.test.platform.app.InstrumentationRegistry;
import dalvik.system.DexFile;

import static org.junit.Assert.assertEquals;
import static org.junit.Assume.assumeTrue;

@RunWith(Parameterized.class)
public class BricksHelpUrlTest {
	public static final String TAG = BricksHelpUrlTest.class.getSimpleName();
	public static Map<String, String> brickToGermanHelpUrlMapping;
	public static Map<String, String> brickToEnglishHelpUrlMapping;

	static {
		brickToGermanHelpUrlMapping = new HashMap<>();
		brickToEnglishHelpUrlMapping = new HashMap<>();

		brickToGermanHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.WhenConditionBrick",
				"https://catrobat.org/docs/brick-dokumentation-de/ereignisse/wenn-wahr-wird");
		brickToGermanHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.WhenBrick",
				"https://catrobat.org/docs/brick-dokumentation-de/ereignisse/wenn-angetippt");
		brickToGermanHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.WhenTouchDownBrick",
				"https://catrobat.org/docs/brick-dokumentation-de/ereignisse/wenn-der-bildschirm-beruhrt-wird");
		brickToGermanHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.WhenBackgroundChangesBrick",
				"https://catrobat.org/docs/brick-dokumentation-de/ereignisse/wenn-der-hintergrund-wechselt-zu");
		brickToGermanHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.BroadcastReceiverBrick",
				"https://catrobat.org/docs/brick-dokumentation-de/ereignisse/wenn-du-empfangst");
		brickToGermanHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.WhenClonedBrick",
				"https://catrobat.org/docs/brick-dokumentation-de/ereignisse/als-klon-starte");
		brickToGermanHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.WhenStartedBrick",
				"https://catrobat.org/docs/brick-dokumentation-de/ereignisse/wenn-szene-startet");

		brickToEnglishHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.WhenConditionBrick",
				"https://catrobat.org/docs/brickdocumentation/event/when-1");
		brickToEnglishHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.WhenBrick",
				"https://catrobat.org/docs/brickdocumentation/event/when-tapped");
		brickToEnglishHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.WhenTouchDownBrick",
				"https://catrobat.org/docs/brickdocumentation/event/when-stage-is-tapped");
		brickToEnglishHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.WhenBackgroundChangesBrick",
				"https://catrobat.org/docs/brickdocumentation/event/when-background-changes-to");
		brickToEnglishHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.BroadcastReceiverBrick",
				"https://catrobat.org/docs/brickdocumentation/event/when-you-receive");
		brickToEnglishHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.WhenClonedBrick",
				"https://catrobat.org/docs/brickdocumentation/event/when-you-start-as-a-clone");
		brickToEnglishHelpUrlMapping.put(
				"org.catrobat.catroid.content.bricks.WhenStartedBrick",
				"https://catrobat.org/docs/brickdocumentation/event/when-scene-starts");
	}

	@Parameterized.Parameters(name = "{0}")
	public static Iterable<Object[]> data() {
		List<Object[]> parameters = new ArrayList<>();
		Set<Class> brickClasses = getAllBrickClasses();

		brickClasses = removeAbstractClasses(brickClasses);
		brickClasses = removeInnerClasses(brickClasses);
		brickClasses = removeEndBrick(brickClasses);
		for (Class<?> brickClazz : brickClasses) {
			parameters.add(new Object[] {brickClazz.getName(), brickClazz});
		}

		return parameters;
	}

	@Parameterized.Parameter
	public String simpleName;

	@Parameterized.Parameter(1)
	public Class brickClass;

	private static Set<Class> getAllBrickClasses() {
		ArrayList<Class> classes = new ArrayList<>();
		try {
			String packageCodePath =
					InstrumentationRegistry.getInstrumentation().getTargetContext().getPackageCodePath();
			DexFile dexFile = new DexFile(packageCodePath);
			for (Enumeration<String> iter = dexFile.entries(); iter.hasMoreElements(); ) {
				String className = iter.nextElement();
				if (className.contains("org.catrobat.catroid.content.bricks") && className.endsWith(
						"Brick")) {
					classes.add(Class.forName(className));
				}
			}
		} catch (Exception e) {
			Log.e(TAG, e.getMessage(), e);
		}

		return new HashSet<>(classes);
	}

	@Before
	public void setUp() {
		ProjectManager.getInstance().setCurrentProject(
				new Project(InstrumentationRegistry.getInstrumentation().getTargetContext(), "empty"));
	}

	@Test
	public void testGermanBrickHelpUrl() throws IllegalAccessException, InstantiationException {
		assumeTrue(brickToGermanHelpUrlMapping.containsKey(simpleName));
		Brick brick = (Brick) brickClass.newInstance();
		int category = new CategoryBricksFactory().getBrickCategory(brick, false,
				InstrumentationRegistry.getInstrumentation().getTargetContext());
		String brickHelpUrl = brick.getHelpUrl(category, "de");
		assertEquals(brickToGermanHelpUrlMapping.get(simpleName), brickHelpUrl);
	}

	@Test
	public void testEnglishBrickHelpUrl() throws IllegalAccessException, InstantiationException {
		assumeTrue(brickToEnglishHelpUrlMapping.containsKey(simpleName));
		Brick brick = (Brick) brickClass.newInstance();
		int category = new CategoryBricksFactory().getBrickCategory(brick, false,
				InstrumentationRegistry.getInstrumentation().getTargetContext());
		String brickHelpUrl = brick.getHelpUrl(category, "en");
		assertEquals(brickToEnglishHelpUrlMapping.get(simpleName), brickHelpUrl);
	}

	private static Set<Class> removeAbstractClasses(Set<Class> classes) {
		Set<Class> filtered = new HashSet<>();

		for (Class clazz : classes) {
			boolean isAbstract = Modifier.isAbstract(clazz.getModifiers());
			if (!isAbstract) {
				filtered.add(clazz);
			}
		}
		return filtered;
	}

	private static Set<Class> removeInnerClasses(Set<Class> classes) {
		Set<Class> filtered = new HashSet<>();

		for (Class clazz : classes) {
			boolean isInnerClass = clazz.getEnclosingClass() != null;
			if (!isInnerClass) {
				filtered.add(clazz);
			}
		}
		return filtered;
	}

	private static Set<Class> removeEndBrick(Set<Class> classes) {
		Set<Class> filtered = new HashSet<>();

		for (Class clazz : classes) {
			if (!clazz.getName().contains("EndBrick")) {
				filtered.add(clazz);
			}
		}
		return filtered;
	}
}

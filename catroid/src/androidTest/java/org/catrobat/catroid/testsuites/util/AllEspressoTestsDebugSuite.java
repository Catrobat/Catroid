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

package org.catrobat.catroid.testsuites.util;

import org.catrobat.catroid.uiespresso.formulaeditor.FormulaEditorMovementPropertiesTest;
import org.catrobat.catroid.uiespresso.intents.looks.gallery.LookFromGalleryIntentDismissTest;
import org.catrobat.catroid.uiespresso.intents.looks.gallery.LookFromGalleryIntentTest;
import org.catrobat.catroid.uiespresso.intents.looks.gallery.SpriteFromGalleryIntentDismissTest;
import org.catrobat.catroid.uiespresso.intents.looks.gallery.SpriteFromGalleryIntentTest;
import org.catrobat.catroid.uiespresso.intents.looks.paintroid.PocketPaintEditLookIntentTest;
import org.catrobat.catroid.uiespresso.intents.looks.paintroid.PocketPaintNewLookDiscardIntentTest;
import org.catrobat.catroid.uiespresso.intents.looks.paintroid.PocketPaintNewLookIntentTest;
import org.catrobat.catroid.uiespresso.intents.looks.paintroid.PocketPaintNewSpriteDiscardIntentTest;
import org.catrobat.catroid.uiespresso.intents.looks.paintroid.PocketPaintNewSpriteIntentTest;
import org.catrobat.catroid.uiespresso.intents.sounds.gallery.SoundFromGalleryIntentDismissTest;
import org.catrobat.catroid.uiespresso.intents.sounds.gallery.SoundFromGalleryIntentTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
		FormulaEditorMovementPropertiesTest.class,
		LookFromGalleryIntentDismissTest.class,
		LookFromGalleryIntentTest.class,
		SpriteFromGalleryIntentDismissTest.class,
		SpriteFromGalleryIntentTest.class,
		PocketPaintEditLookIntentTest.class,
		PocketPaintNewLookDiscardIntentTest.class,
		PocketPaintNewLookIntentTest.class,
		PocketPaintNewSpriteDiscardIntentTest.class,
		PocketPaintNewSpriteIntentTest.class,
		SoundFromGalleryIntentDismissTest.class,
		SoundFromGalleryIntentTest.class
})
public class AllEspressoTestsDebugSuite {
}

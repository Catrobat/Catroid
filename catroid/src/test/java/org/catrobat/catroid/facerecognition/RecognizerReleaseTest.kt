/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2026 The Catrobat Team
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
package org.catrobat.catroid.facerecognition

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * The face models are released in the background when the user leaves the
 * stage. Found on the phone: a program started right after that got the
 * models before the release ran, and the release then took them away from
 * it: face names stayed Unknown and training failed. The models are
 * released only if nobody has taken them since the user left.
 */
class RecognizerReleaseTest {

    @Test
    fun theModelsAreReleasedWhenNobodyTookThemSinceLeaving() {
        val left = Recognizer.acquisitionMark()

        assertTrue(Recognizer.releaseIfUnusedSince(left))
    }

    @Test
    fun theModelsStayWhenAProgramTookThemAfterLeaving() {
        val left = Recognizer.acquisitionMark()
        Recognizer.noteAcquiredForTest()

        assertFalse(Recognizer.releaseIfUnusedSince(left))
    }
}

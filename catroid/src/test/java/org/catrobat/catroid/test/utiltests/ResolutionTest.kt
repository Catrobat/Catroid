/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2021 The Catrobat Team
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

package org.catrobat.catroid.test.utiltests

import org.catrobat.catroid.utils.Resolution
import org.hamcrest.Matchers.`is`
import org.junit.Assert.assertThat
import org.junit.Test

class ResolutionTest {

    @Test
    fun testResizeToFitBiggerAspectRatio() {
        val screen = Resolution(1280, 720)
        val scaledResolution = Resolution(640, 480).resizeToFit(screen)

        assertThat(scaledResolution.width, `is`(960))
        assertThat(scaledResolution.height, `is`(720))
        assertThat(scaledResolution.offsetX, `is`(160))
        assertThat(scaledResolution.offsetY, `is`(0))
    }

    @Test
    fun testResizeToFitSmallerAspectRatio() {
        val screen = Resolution(640, 480)
        val scaledResolution = Resolution(1280, 720).resizeToFit(screen)

        assertThat(scaledResolution.width, `is`(640))
        assertThat(scaledResolution.height, `is`(360))
        assertThat(scaledResolution.offsetX, `is`(0))
        assertThat(scaledResolution.offsetY, `is`(60))
    }

    @Test
    fun testResizeToFitSameAspectRatio() {
        val screen = Resolution(640, 480)
        val scaledResolution = Resolution(1280, 960).resizeToFit(screen)

        assertThat(scaledResolution.width, `is`(640))
        assertThat(scaledResolution.height, `is`(480))
        assertThat(scaledResolution.offsetX, `is`(0))
        assertThat(scaledResolution.offsetY, `is`(0))
    }

    @Test
    fun testFlipFromLandscapeToPortrait() {
        val portraitResolution = Resolution(960, 1280)
        val flippedResolution = Resolution(640, 480).flipToFit(portraitResolution)

        assertThat(flippedResolution.width, `is`(480))
        assertThat(flippedResolution.height, `is`(640))
        assertThat(flippedResolution.offsetX, `is`(0))
        assertThat(flippedResolution.offsetY, `is`(0))
    }

    @Test
    fun testFlipFromPortraitToLandscape() {
        val landscapeResolution = Resolution(1280, 960)
        val flippedResolution = Resolution(480, 640).flipToFit(landscapeResolution)

        assertThat(flippedResolution.width, `is`(640))
        assertThat(flippedResolution.height, `is`(480))
        assertThat(flippedResolution.offsetX, `is`(0))
        assertThat(flippedResolution.offsetY, `is`(0))
    }

    @Test
    fun testNoFlipInLandscape() {
        val landscapeResolution = Resolution(1280, 960)
        val flippedResolution = Resolution(640, 480).flipToFit(landscapeResolution)

        assertThat(flippedResolution.width, `is`(640))
        assertThat(flippedResolution.height, `is`(480))
        assertThat(flippedResolution.offsetX, `is`(0))
        assertThat(flippedResolution.offsetY, `is`(0))
    }

    @Test
    fun testNoFlipInPortrait() {
        val portraitResolution = Resolution(960, 1280)
        val flippedResolution = Resolution(480, 640).flipToFit(portraitResolution)

        assertThat(flippedResolution.width, `is`(480))
        assertThat(flippedResolution.height, `is`(640))
        assertThat(flippedResolution.offsetX, `is`(0))
        assertThat(flippedResolution.offsetY, `is`(0))
    }

    @Test
    fun testFlippedTwice() {
        val landscape = Resolution(640, 480)
        val portrait = Resolution(960, 1280)

        val doubleFlipped = landscape
            .flipToFit(portrait)
            .flipToFit(landscape)

        assertThat(doubleFlipped.width, `is`(landscape.width))
        assertThat(doubleFlipped.height, `is`(landscape.height))
        assertThat(doubleFlipped.offsetX, `is`(landscape.offsetX))
        assertThat(doubleFlipped.offsetY, `is`(landscape.offsetY))
    }

    @Test
    fun testThatAspectRatioRemainsUnchangedAfterResize() {
        val screen = Resolution(640, 480)
        val scaledResolution = Resolution(1280, 720).resizeToFit(screen)

        assertThat(scaledResolution.aspectRatio(), `is`(1280.0f / 720.0f))
    }

    @Test
    fun testSameSize() {
        val resolution1 = Resolution(1280, 720)
        val resolution2 = Resolution(1280, 720)

        assertThat(resolution1.sameRatioOrMeasurements(resolution2), `is`(true))
    }

    @Test
    fun testSameAspectRatio() {
        val resolution1 = Resolution(1280, 720)
        val resolution2 = Resolution(640, 360)

        assertThat(resolution1.sameRatioOrMeasurements(resolution2), `is`(true))
    }

    @Test
    fun testDifferentResolutions() {
        val resolution1 = Resolution(1280, 720)
        val resolution2 = Resolution(1024, 768)

        assertThat(resolution1.sameRatioOrMeasurements(resolution2), `is`(false))
    }
}

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

package org.catrobat.catroid.test.robolectric.bricks

import android.app.Activity
import android.os.Build
import android.view.MotionEvent
import android.widget.FrameLayout
import android.widget.Spinner
import org.catrobat.catroid.common.Nameable
import org.catrobat.catroid.content.bricks.brickspinner.BrickSpinner
import org.catrobat.catroid.content.bricks.brickspinner.NewOption
import org.catrobat.catroid.content.bricks.brickspinner.StringOption
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.never
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class BrickSpinnerSingleNewOptionTest {
    private lateinit var spinner: Spinner
    private lateinit var parent: FrameLayout
    private lateinit var listener: BrickSpinner.OnItemSelectedListener<Nameable>

    @Before
    fun setUp() {
        val activity = Robolectric.buildActivity(Activity::class.java).create().get()
        parent = FrameLayout(activity)
        spinner = Spinner(activity)
        spinner.id = SPINNER_ID
        parent.addView(spinner)
        @Suppress("UNCHECKED_CAST")
        listener = mock(BrickSpinner.OnItemSelectedListener::class.java)
            as BrickSpinner.OnItemSelectedListener<Nameable>
    }

    @Test
    fun tapOnlyNewOption() {
        createBrickSpinner(NewOption("new…"))

        tapSpinner()

        verify(listener, times(1)).onNewOptionSelected(SPINNER_ID)
    }

    @Test
    fun tapMoreThanNewOption() {
        createBrickSpinner(NewOption("new…"), StringOption("someItem"))

        tapSpinner()

        verify(listener, never()).onNewOptionSelected(SPINNER_ID)
    }

    @Test
    fun tapAfterItemAdded() {
        val brickSpinner = createBrickSpinner(NewOption("new…"))
        brickSpinner.add(StringOption("someVariable"))

        tapSpinner()

        verify(listener, never()).onNewOptionSelected(SPINNER_ID)
    }

    private fun createBrickSpinner(vararg items: Nameable): BrickSpinner<Nameable> {
        val brickSpinner = BrickSpinner<Nameable>(SPINNER_ID, parent, items.toMutableList())
        brickSpinner.setOnItemSelectedListener(listener)
        return brickSpinner
    }

    private fun tapSpinner() {
        spinner.dispatchTouchEvent(MotionEvent.obtain(0L, 0L, MotionEvent.ACTION_DOWN, 0f, 0f, 0))
        spinner.dispatchTouchEvent(MotionEvent.obtain(0L, 0L, MotionEvent.ACTION_UP, 0f, 0f, 0))
    }

    companion object {
        private const val SPINNER_ID = 12345
    }
}

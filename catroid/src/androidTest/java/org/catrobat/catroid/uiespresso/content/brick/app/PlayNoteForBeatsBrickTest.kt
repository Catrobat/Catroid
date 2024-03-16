/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2023 The Catrobat Team
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
package org.catrobat.catroid.uiespresso.content.brick.app

import android.content.Context
import android.graphics.drawable.PaintDrawable
import android.view.MotionEvent
import android.view.View
import android.widget.TableLayout
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.MotionEvents
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withTagValue
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.StartScript
import org.catrobat.catroid.R
import org.catrobat.catroid.content.bricks.PlayNoteForBeatsBrick
import org.catrobat.catroid.pocketmusic.note.NoteName
import org.catrobat.catroid.pocketmusic.ui.NotePickerPianoWhiteKeysRowView
import org.catrobat.catroid.pocketmusic.ui.NotePickerPianoWhiteKeysRowView.Companion.PIANO_WHITE_KEY_TAG
import org.catrobat.catroid.pocketmusic.ui.NotePickerPianoWhiteKeysView.Companion.DEFAULT_OCTAVE_COUNT
import org.catrobat.catroid.ui.SpriteActivity
import org.catrobat.catroid.uiespresso.content.brick.utils.BrickDataInteractionWrapper
import org.catrobat.catroid.uiespresso.util.rules.FragmentActivityTestRule
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers
import org.hamcrest.TypeSafeMatcher
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.java.KoinJavaComponent

@RunWith(AndroidJUnit4::class)
class PlayNoteForBeatsBrickTest {

    private lateinit var sprite: Sprite
    private lateinit var script: StartScript
    private val projectName = "PlayNoteForBeatsBrickTest"
    private val projectManager by KoinJavaComponent.inject(ProjectManager::class.java)

    @get:Rule
    var baseActivityTestRule = FragmentActivityTestRule(
        SpriteActivity::class.java,
        SpriteActivity.EXTRA_FRAGMENT_POSITION,
        SpriteActivity.FRAGMENT_SCRIPTS
    )

    @Before
    @Throws(Exception::class)
    fun setUp() {
        createProjectAndSprite()
        baseActivityTestRule.launchActivity()
        clickOnBrick()
        chooseEditFormulaDialogOption()
    }

    @Test
    fun checkCurrentNoteNameIsDisplayed() {
        onView(withId(R.id.rotated_toolbar_title))
            .check(matches(withText(ACTIVE_NOTE_ON_START.prettyPrintName)))
    }

    @Test
    fun checkCurrentNoteIsHighlighted() {
        @ColorInt val neutralColor = ContextCompat.getColor(
            context,
            R.color.solid_white
        )
        @ColorInt val highlightedColor = ContextCompat.getColor(
            context,
            R.color.turquoise_play_line
        )
        onView(withId(R.id.musicdroid_piano_notepickerWhiteKeyView))
            .check(
                matches(
                    checkKeyColors(
                        NoteName.C4,
                        highlightedColor,
                        neutralColor
                    )
                )
            )
    }

    @Test
    fun checkBlackAndWhiteKeysAreShown() {
        val halfTonesPerOctave = 5
        val wholeTonesPerOctave = 7
        onView(withId(R.id.musicdroid_piano_notepickerView))
            .check(matches(isDisplayed()))
            .check(matches(withNumberOfTableItems(DEFAULT_OCTAVE_COUNT * (halfTonesPerOctave + wholeTonesPerOctave))))
        onView(withId(R.id.musicdroid_piano_notepickerWhiteKeyView))
            .check(matches(isDisplayed()))
            .check(matches(withNumberOfTableItems(DEFAULT_OCTAVE_COUNT * wholeTonesPerOctave)))
    }

    @Test
    fun checkScrollToKeyByBigPianoScroll() {
        onView(withTagValue(Matchers.`is`(PIANO_WHITE_KEY_TAG + ACTIVE_NOTE_ON_START.name)))
            .check(matches(isDisplayed()))
        onView(withTagValue(Matchers.`is`(PIANO_WHITE_KEY_TAG + "C7")))
            .check(matches(Matchers.not<View>(isDisplayed())))
        onView(withTagValue(Matchers.`is`(PIANO_WHITE_KEY_TAG + "C7")))
            .perform(ViewActions.scrollTo())
        onView(withTagValue(Matchers.`is`(PIANO_WHITE_KEY_TAG + ACTIVE_NOTE_ON_START.name)))
            .check(matches(Matchers.not<View>(isDisplayed())))
        onView(withTagValue(Matchers.`is`(PIANO_WHITE_KEY_TAG + "C7")))
            .check(matches(isDisplayed()))
    }

    @Test
    fun checkScrollToKeyByMiniPianoScroll() {
        onView(withTagValue(Matchers.`is`(PIANO_WHITE_KEY_TAG + ACTIVE_NOTE_ON_START.name)))
            .check(matches(isDisplayed()))
        onView(withTagValue(Matchers.`is`(PIANO_WHITE_KEY_TAG + "C7")))
            .check(matches(Matchers.not<View>(isDisplayed())))
        onView(withId(R.id.musicdroid_miniPianoPreview)).perform(touchOnVerticalBottom())
        onView(withTagValue(Matchers.`is`(PIANO_WHITE_KEY_TAG + ACTIVE_NOTE_ON_START.name)))
            .check(matches(Matchers.not<View>(isDisplayed())))
        onView(withTagValue(Matchers.`is`(PIANO_WHITE_KEY_TAG + "C7")))
            .check(matches(isDisplayed()))
    }

    private fun createProjectAndSprite() {
        val project = Project(ApplicationProvider.getApplicationContext(), projectName)
        val playNoteForBeatsBrick = PlayNoteForBeatsBrick(ACTIVE_NOTE_ON_START.midi, 8)
        sprite = Sprite("testSprite")
        script = StartScript()
        script.addBrick(playNoteForBeatsBrick)
        sprite.addScript(script)
        project.defaultScene.addSprite(sprite)
        projectManager.currentProject = project
        projectManager.currentSprite = sprite
        projectManager.currentlyEditedScene = project.defaultScene
    }

    private val context: Context
        get() = baseActivityTestRule.activity

    private fun clickOnBrick() {
        BrickDataInteractionWrapper.onBrickAtPosition(1)
            .onFormulaTextField(R.id.brick_play_note_for_beats_note_edit_text)
            .perform(ViewActions.click())
    }

    private fun chooseEditFormulaDialogOption() {
        onView(withText(R.string.brick_context_dialog_pick_note))
            .perform(ViewActions.click())
    }

    private fun withNumberOfTableItems(numberOfItems: Int): Matcher<View> {
        return object : TypeSafeMatcher<View>() {
            override fun matchesSafely(item: View): Boolean {
                if (item is TableLayout) {
                    val tableLayout: TableLayout = item
                    return tableLayout.childCount == numberOfItems
                }
                return false
            }

            override fun describeTo(description: Description) {
                description.appendText("TableLayout with row count: $numberOfItems")
            }
        }
    }

    private fun touchOnVerticalBottom(): ViewAction {
        return object : ViewAction {

            override fun getConstraints(): Matcher<View> {
                return isDisplayed()
            }

            override fun getDescription(): String = "Sends a touch event to the vertical bottom of a view."

            override fun perform(uiController: UiController, view: View) {
                val currentLocation = IntArray(2)
                view.getLocationOnScreen(currentLocation)
                val safetyOffset = 50
                val coordinates = floatArrayOf(
                    (currentLocation[0] + safetyOffset).toFloat(),
                    (
                        currentLocation[1] + view.measuredHeight - safetyOffset).toFloat()
                )
                val precision = floatArrayOf(1f, 1f)
                val down: MotionEvent =
                    MotionEvents.sendDown(uiController, coordinates, precision).down
                uiController.loopMainThreadForAtLeast(200)
                MotionEvents.sendUp(uiController, down, coordinates)
            }
        }
    }

    @Suppress("NestedBlockDepth")
    private fun checkKeyColors(
        note: NoteName,
        @ColorInt highlightedColor: Int,
        @ColorInt neutralColor: Int
    ): Matcher<View> {
        return object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description) {
                description.appendText("Piano key " + note.name + " has color #" + highlightedColor)
            }

            private fun hasColoredPaintDrawable(@ColorInt color: Int, view: View): Boolean {
                val backgroundDrawable = view.background
                if (backgroundDrawable is PaintDrawable) {
                    val backgroundColor: Int = backgroundDrawable.paint.color
                    return backgroundColor == color
                }
                return false
            }

            override fun matchesSafely(item: View): Boolean {
                if (item is TableLayout) {
                    val tableLayout: TableLayout = item
                    val rowCount: Int = tableLayout.childCount
                    for (i in 0 until rowCount) {
                        val childView: View = tableLayout.getChildAt(i)
                        if (childView is NotePickerPianoWhiteKeysRowView) {
                            val row: NotePickerPianoWhiteKeysRowView =
                                childView
                            return if (row.noteMidi != note.midi) {
                                hasColoredPaintDrawable(neutralColor, row.whiteButton)
                            } else hasColoredPaintDrawable(highlightedColor, row.whiteButton)
                        }
                    }
                }
                return false
            }
        }
    }

    companion object {
        private val ACTIVE_NOTE_ON_START: NoteName = NoteName.C4
    }
}

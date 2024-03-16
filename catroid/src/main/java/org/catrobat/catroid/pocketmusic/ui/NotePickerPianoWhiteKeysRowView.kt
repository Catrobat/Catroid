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
package org.catrobat.catroid.pocketmusic.ui

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Typeface
import android.graphics.drawable.PaintDrawable
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.View.OnClickListener
import android.widget.LinearLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.annotation.VisibleForTesting
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import org.catrobat.catroid.R
import org.catrobat.catroid.common.ScreenValues
import org.catrobat.catroid.pocketmusic.note.NoteName
import java.util.Objects

@SuppressLint("ViewConstructor")
class NotePickerPianoWhiteKeysRowView(context: Context?, noteName: NoteName) : TableRow(context),
    OnClickListener {
    private var parentHeight = -1
    private val note: NoteName
    @VisibleForTesting
    lateinit var whiteButton: LinearLayout

    init {
        parentHeight = ScreenValues.SCREEN_HEIGHT
        note = noteName
        initializeRow()
    }

    fun resetActiveNote() {
        DrawableCompat.setTintList(whiteButton.background, null)
    }

    fun setActiveNote() {
        val tintedColor = ContextCompat.getColor(context, R.color.turquoise_play_line)
        val tintedDrawable = DrawableCompat.wrap(whiteButton.background)
        DrawableCompat.setTint(tintedDrawable, tintedColor)
        whiteButton.background = tintedDrawable
    }

    fun duplicateOnClickAction(
        notePickerView: NotePickerView
    ) {
        whiteButton.setOnClickListener(OnClickListener {
            notePickerView.disableAllNotes()
            notePickerView.selectedNote = note.midi
            notePickerView.onNoteChanged()
            setActiveNote()
        })
    }

    val noteMidi: Int
        get() = note.midi

    private fun initializeRow() {
        val margin = resources.getDimensionPixelSize(R.dimen.pocketmusic_trackrow_margin)
        val totalMarginWidth = margin * NUMBER_OF_MARGINS
        val usableWidth = parentHeight - totalMarginWidth
        val keyHeight =
            (usableWidth / NUMBER_OF_KEYS * Objects.requireNonNull(note.whiteButtonWidthFactor)).toInt()
        val params = LayoutParams(0, keyHeight, 1.0f)
        params.bottomMargin = margin
        params.topMargin = margin
        whiteButton = LinearLayout(context)
        whiteButton.gravity = Gravity.CENTER_VERTICAL
        whiteButton.background = createBackgroundDrawableWithRadius(
            context,
            R.color.solid_white
        )
        whiteButton.tag = PIANO_WHITE_KEY_TAG + note.name

        if (shouldShowNoteNameLabel()) {
            whiteButton.addView(createStyledWhiteButtonText(note.name))
        }
        addView(whiteButton, params)
    }

    private fun createStyledWhiteButtonText(text: String): TextView {
        val buttonText = TextView(context)
        buttonText.text = text
        buttonText.setTextColor(ContextCompat.getColor(context, R.color.solid_black))
        val padding =
            resources.getDimensionPixelSize(R.dimen.pocketmusic_pianoWhiteKeyRowView_nameLabelPadding)
        buttonText.setPadding(0, 0, 0, padding)
        buttonText.rotation = BUTTON_TEXT_ROTATION
        buttonText.typeface = Typeface.defaultFromStyle(Typeface.BOLD)
        return buttonText
    }

    private fun createBackgroundDrawableWithRadius(
        context: Context,
        backgroundColor: Int
    ): PaintDrawable {

        val backgroundDrawable = PaintDrawable(ContextCompat.getColor(context, backgroundColor))
        val cornerRadii = floatArrayOf(
            TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, CORNER_RADIUS,
                resources.displayMetrics
            ),
            TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, CORNER_RADIUS,
                resources.displayMetrics
            ),
            0f,
            0f,
            0f,
            0f,
            TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, CORNER_RADIUS,
                resources.displayMetrics
            ),
            TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, CORNER_RADIUS,
                resources.displayMetrics
            )
        )
        backgroundDrawable.setCornerRadii(cornerRadii)

        return backgroundDrawable
    }

    override fun onClick(view: View) {
        val notePickerView = view.findViewById<NotePickerView>(R.id.musicdroid_piano_notepickerView)
        notePickerView?.selectedNote = note.midi
    }

    private fun shouldShowNoteNameLabel() = note.baseNoteName == 'C' && note.isWholeNote

    companion object {
        const val NUMBER_OF_KEYS = 12
        const val PIANO_WHITE_KEY_TAG = "PianoWhiteKeyButton"
        private const val NUMBER_OF_MARGINS = NUMBER_OF_KEYS
        private const val BUTTON_TEXT_ROTATION = 90f
        private const val CORNER_RADIUS = 10f
    }
}

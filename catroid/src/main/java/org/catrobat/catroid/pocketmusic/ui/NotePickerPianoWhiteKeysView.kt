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

import android.content.Context
import android.util.AttributeSet
import android.widget.TableLayout
import org.catrobat.catroid.R
import org.catrobat.catroid.pocketmusic.note.NoteName

class NotePickerPianoWhiteKeysView(context: Context, attrs: AttributeSet) : TableLayout(context, attrs) {
    private val rowViews: MutableList<NotePickerPianoWhiteKeysRowView> = ArrayList()
    private var rowCount = 0
    private var baseMidiValue = NotePickerView.NO_BASE_MIDI_VALUE

    init {
        isScrollContainer = true
        isClickable = true
        readStyleParameters(context, attrs)
        initializeRows()
    }

    fun duplicateOnClickAction(notePickerView: NotePickerView) {
        for (rowView in rowViews) {
            rowView.duplicateOnClickAction(notePickerView)
        }
    }

    fun resetAllActiveNotes() {
        for (rowView in rowViews) {
            rowView.resetActiveNote()
        }
    }

    fun setActiveNoteByMidi(midi: Int) {
        for (rowView in rowViews) {
            if (rowView.noteMidi == midi) {
                rowView.setActiveNote()
                break
            }
        }
    }

    private fun initializeRows() {
        if (rowViews.isNotEmpty()) {
            removeAllViews()
            rowViews.clear()
        }
        val params = LayoutParams(LayoutParams.MATCH_PARENT, 0, 1.0f)
        for (i in 0 until rowCount) {
            val noteName = NoteName.getNoteNameFromMidiValue(getMidiValueForRow(i))
            val isBlackRow = noteName.isSigned
            if (isBlackRow) continue
            val rowView = NotePickerPianoWhiteKeysRowView(context, noteName)
            rowViews.add(rowView)
            addView(rowView, params)
        }
    }

    private fun readStyleParameters(context: Context, attributeSet: AttributeSet) {
        val styledAttributes = context.obtainStyledAttributes(
            attributeSet,
            R.styleable.NotePickerPianoWhiteKeysView
        )
        try {
            baseMidiValue = styledAttributes.getInteger(
                R.styleable.NotePickerPianoWhiteKeysView_whiteKeyViewBaseMidiValue,
                NotePickerView.NO_BASE_MIDI_VALUE
            )

            rowCount = styledAttributes.getInteger(
                R.styleable.NotePickerView_notePickerOctaveCount, DEFAULT_OCTAVE_COUNT
            ) * NoteName.NOTES_PER_OCTAVE

            weightSum = rowCount.toFloat()
        } finally {
            styledAttributes.recycle()
        }
    }

    private fun getMidiValueForRow(i: Int): Int {
        return if (baseMidiValue != NotePickerView.NO_BASE_MIDI_VALUE) {
            baseMidiValue + i
        } else TrackRowView.getMidiValueForRow(
            i
        )
    }

    companion object {
        @JvmField
		var DEFAULT_OCTAVE_COUNT = 7
    }
}
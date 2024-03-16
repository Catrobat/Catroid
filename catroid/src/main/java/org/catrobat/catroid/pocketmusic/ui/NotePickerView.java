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

package org.catrobat.catroid.pocketmusic.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.TableLayout;

import org.catrobat.catroid.R;
import org.catrobat.catroid.pocketmusic.note.MusicalBeat;
import org.catrobat.catroid.pocketmusic.note.MusicalInstrument;
import org.catrobat.catroid.pocketmusic.note.MusicalKey;
import org.catrobat.catroid.pocketmusic.note.NoteLength;
import org.catrobat.catroid.pocketmusic.note.NoteName;
import org.catrobat.catroid.pocketmusic.note.trackgrid.GridRow;
import org.catrobat.catroid.pocketmusic.note.trackgrid.TrackGrid;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import static org.catrobat.catroid.pocketmusic.ui.NotePickerPianoWhiteKeysView.DEFAULT_OCTAVE_COUNT;

public class NotePickerView extends TableLayout {
	private NotePickerView.OnNoteChangedListener listener;
	private int selectedNote;
	private int initialNote;

	private List<TrackRowView> trackRowViews = new ArrayList<>();
	private TrackGrid trackGrid;
	private int tactPosition = 0;

	private int baseMidiValue = NO_BASE_MIDI_VALUE;

	private int rowCount;

	public static final int NO_BASE_MIDI_VALUE = -1;

	public NotePickerView(Context context, AttributeSet attrs) {
		this(context, attrs, new TrackGrid(MusicalKey.VIOLIN, MusicalInstrument.ACCORDION, MusicalBeat.BEAT_4_4, new
				ArrayList<GridRow>()));
		this.readStyleParameters(context, attrs);
	}

	public NotePickerView(Context context, AttributeSet attrs, TrackGrid trackGrid) {
		super(context, attrs);
		this.readStyleParameters(context, attrs);
		setStretchAllColumns(false);
		setScrollContainer(true);
		setClickable(true);
		this.trackGrid = trackGrid;
		initializeRows();
	}

	private void initializeRows() {
		if (!trackRowViews.isEmpty()) {
			removeAllViews();
			trackRowViews.clear();
		}
		TableLayout.LayoutParams params = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, 0, 1.0f);
		for (int i = 0; i < rowCount; i++) {
			NoteName noteName = NoteName.getNoteNameFromMidiValue(getMidiValueForRow(i));
			boolean isBlackRow = noteName.isSigned();
			TrackRowView trackRowView = new TrackRowView(getContext(), trackGrid.getBeat(), isBlackRow,
					this, noteName, 1);
			trackGrid.updateGridRowPosition(noteName, 0, NoteLength.QUARTER, 0, false);
			trackRowView.setTactPosition(tactPosition, trackGrid.getGridRowForNoteName(noteName));
			trackRowViews.add(trackRowView);
			addView(trackRowViews.get(i), params);
		}
	}

	public void disableAllNotes() {
		for (int i = 0; i < rowCount; i++) {
			trackRowViews.get(i).disableOwnNotes();
		}
	}

	public void updateGridRowPosition(NoteName noteName, int columnIndex, NoteLength noteLength, boolean toggled) {
		trackGrid.updateGridRowPosition(noteName, columnIndex, noteLength, tactPosition, toggled);
	}

	public void setOnNoteChangedListener(NotePickerView.OnNoteChangedListener listener) {
		this.listener = listener;
	}

	public interface OnNoteChangedListener {
		void noteChanged(int color);
	}

	public void onNoteChanged() {
		if (listener != null) {
			listener.noteChanged(getSelectedNote());
		}
	}

	public int getSelectedNote() {
		return selectedNote;
	}

	public void setSelectedNote(int note) {
		updateTrackRowViews(note);
		selectedNote = note;
	}

	public void setInitialNote(int initialNote) {
		updateTrackRowViews(initialNote);
		this.initialNote = initialNote;
	}

	public @Nullable NoteView getNoteViewForMidi(int midi) {
		for (int i = 0; i < rowCount; i++) {
			int tempNote = getMidiValueForRow(i);
			if (tempNote == midi) {
				return trackRowViews.get(i).getNoteViews().get(0);
			}
		}
		return null;
	}

	private void updateTrackRowViews(int noteToBeSet) {
		for (int i = 0; i < rowCount; i++) {
			int tempNote = getMidiValueForRow(i);
			if (tempNote == noteToBeSet) {
				trackRowViews.get(i).getNoteViews().get(0).setNoteActive(true, true);
				break;
			}
		}
	}

	private int getMidiValueForRow(int i) {
		if (baseMidiValue != NO_BASE_MIDI_VALUE) {
			return baseMidiValue + i;
		}
		return TrackRowView.getMidiValueForRow(i);
	}

	private void readStyleParameters(Context context, AttributeSet attributeSet) {
		TypedArray styledAttributes = context.obtainStyledAttributes(attributeSet,
				R.styleable.NotePickerView);
		try {
			baseMidiValue = styledAttributes.getInteger(
					R.styleable.NotePickerView_notePickerViewBaseMidiValue, NO_BASE_MIDI_VALUE);

			rowCount = styledAttributes.getInteger(
					R.styleable.NotePickerView_notePickerOctaveCount, DEFAULT_OCTAVE_COUNT) * NoteName.NOTES_PER_OCTAVE;
			setWeightSum(rowCount);
		} finally {
			styledAttributes.recycle();
		}
	}

	public int getInitialNote() {
		return initialNote;
	}

	public int getRowCount() {
		return rowCount;
	}
}

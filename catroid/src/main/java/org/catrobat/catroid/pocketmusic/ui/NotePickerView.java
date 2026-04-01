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
import android.util.AttributeSet;
import android.widget.TableLayout;

import org.catrobat.catroid.pocketmusic.note.MusicalBeat;
import org.catrobat.catroid.pocketmusic.note.MusicalInstrument;
import org.catrobat.catroid.pocketmusic.note.MusicalKey;
import org.catrobat.catroid.pocketmusic.note.NoteLength;
import org.catrobat.catroid.pocketmusic.note.NoteName;
import org.catrobat.catroid.pocketmusic.note.trackgrid.GridRow;
import org.catrobat.catroid.pocketmusic.note.trackgrid.TrackGrid;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NotePickerView extends TableLayout {
	private NotePickerView.OnNoteChangedListener listener;
	private int selectedNote;
	private int initialNote;

	public static final int ROW_COUNT = 13;
	private List<TrackRowView> trackRowViews = new ArrayList<>(ROW_COUNT);
	private TrackGrid trackGrid;
	private int tactPosition = 0;

	public NotePickerView(Context context, AttributeSet attrs) {
		this(context, attrs, new TrackGrid(MusicalKey.VIOLIN, MusicalInstrument.ACCORDION, MusicalBeat.BEAT_4_4, new
				ArrayList<GridRow>()));
	}

	public NotePickerView(Context context, AttributeSet attrs, TrackGrid trackGrid) {
		super(context, attrs);
		setStretchAllColumns(true);
		setClickable(true);
		this.trackGrid = trackGrid;
		initializeRows();
		setWeightSum(ROW_COUNT);
	}

	private void initializeRows() {
		if (!trackRowViews.isEmpty()) {
			removeAllViews();
			trackRowViews.clear();
		}
		TableLayout.LayoutParams params = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, 0, 1.0f);
		for (int i = 0; i < ROW_COUNT; i++) {
			boolean isBlackRow = Arrays.binarySearch(TrackView.BLACK_KEY_INDICES, i) > -1;
			NoteName noteName = NoteName.getNoteNameFromMidiValue(TrackRowView.getMidiValueForRow(i));
			TrackRowView trackRowView = new TrackRowView(getContext(), trackGrid.getBeat(), isBlackRow,
					this, noteName, 1);
			trackGrid.updateGridRowPosition(noteName, 0, NoteLength.QUARTER, 0, false);
			trackRowView.setTactPosition(tactPosition, trackGrid.getGridRowForNoteName(noteName));
			trackRowViews.add(trackRowView);
			addView(trackRowViews.get(i), params);
		}
	}

	public void disableAllNotes() {
		for (int i = 0; i < ROW_COUNT; i++) {
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

	private void updateTrackRowViews(int noteToBeSet) {
		for (int i = 0; i < ROW_COUNT; i++) {
			int tempNote = TrackRowView.getMidiValueForRow(i);
			if (tempNote == noteToBeSet) {
				trackRowViews.get(i).getNoteViews().get(0).setNoteActive(true, true);
				break;
			}
		}
	}

	public int getInitialNote() {
		return initialNote;
	}
}

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
import android.widget.TableRow;

import org.catrobat.catroid.R;
import org.catrobat.catroid.pocketmusic.note.MusicalBeat;
import org.catrobat.catroid.pocketmusic.note.NoteLength;
import org.catrobat.catroid.pocketmusic.note.NoteName;
import org.catrobat.catroid.pocketmusic.note.trackgrid.GridRow;
import org.catrobat.catroid.pocketmusic.note.trackgrid.GridRowPosition;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import androidx.core.content.ContextCompat;

public class TrackRowView extends TableRow {

	public static final int INITIAL_QUARTER_COUNT = 4;
	public static int quarterCount;

	private int tactPosition = 0;
	private final MusicalBeat beat;
	private List<NoteView> noteViews;
	private boolean isBlackRow;
	private GridRow gridRow;
	private TrackView trackView = null;
	private NotePickerView notePickerView = null;
	public boolean allowOnlySingleNote = false;
	private NoteName noteName;

	public TrackRowView(Context context) {
		this(context, MusicalBeat.BEAT_4_4, false, NoteName.DEFAULT_NOTE_NAME, null);
	}

	public TrackRowView(Context context, MusicalBeat beat, boolean isBlackRow, NoteName noteName, TrackView trackView) {
		super(context);
		this.beat = beat;
		this.noteName = noteName;
		this.trackView = trackView;
		this.quarterCount = INITIAL_QUARTER_COUNT;
		noteViews = new ArrayList<>(INITIAL_QUARTER_COUNT);
		this.setBlackRow(isBlackRow);
		initializeRow();
		setWeightSum(INITIAL_QUARTER_COUNT);
		updateGridRow();
	}

	public TrackRowView(Context context, MusicalBeat beat, boolean isBlackRow,
			NotePickerView notePickerView, NoteName noteName, int quarterCount) {
		super(context);
		this.beat = beat;
		this.noteName = noteName;
		this.notePickerView = notePickerView;
		this.quarterCount = quarterCount;
		noteViews = new ArrayList<>(quarterCount);
		allowOnlySingleNote = true;
		this.setBlackRow(isBlackRow);
		initializeRow();
		setWeightSum(quarterCount);
		updateGridRow();
	}

	public void setTactPosition(int tactPosition, GridRow gridRow) {
		this.gridRow = gridRow;
		this.tactPosition = tactPosition;
		refreshNoteViews();
	}

	private void refreshNoteViews() {
		final int whiteKeyColor;
		final int blackKeyColor;
		if (tactPosition % 2 == 0) {
			whiteKeyColor = ContextCompat.getColor(getContext(), R.color.pocketmusic_odd_bright);
			blackKeyColor = ContextCompat.getColor(getContext(), R.color.pocketmusic_odd_dusk);
		} else {
			whiteKeyColor = ContextCompat.getColor(getContext(), R.color.pocketmusic_even_bright);
			blackKeyColor = ContextCompat.getColor(getContext(), R.color.pocketmusic_even_dusk);
		}
		for (NoteView noteView : noteViews) {
			noteView.setNoteActive(false, false);
			if (isBlackRow) {
				noteView.setBackgroundColor(blackKeyColor);
			} else {
				noteView.setBackgroundColor(whiteKeyColor);
			}
		}
		updateGridRow();
	}

	public void updateGridRow() {
		if (gridRow == null || gridRow.getGridRowPositions().size() == 0) {
			return;
		}
		List<GridRowPosition> gridRowTact = getGridRowsForCurrentTact();
		if (gridRowTact != null) {
			for (int i = 0; i < gridRowTact.size(); i++) {
				GridRowPosition position = gridRowTact.get(i);
				setNoteForGridRowPosition(position);
			}
		}
	}

	private void setNoteForGridRowPosition(GridRowPosition position) {
		if (position != null) {
			BigDecimal divident = new BigDecimal(position.getNoteLength().toMilliseconds(1));
			BigDecimal divisor = new BigDecimal(beat.getNoteLength().toMilliseconds(1));
			long length = divident.divide(divisor, BigDecimal.ROUND_HALF_UP).longValue();
			for (int j = position.getColumnStartIndex(); j < position.getColumnStartIndex() + length; j++) {
				noteViews.get(j).setNoteActive(true, false);
			}
		}
	}

	private void initializeRow() {
		LayoutParams params = new LayoutParams(0, LayoutParams.MATCH_PARENT, 1.0f);
		params.leftMargin = params.topMargin = params.rightMargin = params.bottomMargin = getResources()
				.getDimensionPixelSize(R.dimen.pocketmusic_trackrow_margin);

		for (int i = 0; i < quarterCount; i++) {
			noteViews.add(new NoteView(getContext(), this, i));
			addView(noteViews.get(i), params);
		}
	}

	public void updateGridRowPosition(int columnIndex, NoteLength noteLength, boolean toggled) {
		if (trackView != null) {
			trackView.updateGridRowPosition(noteName, columnIndex, noteLength, toggled);
		} else if (notePickerView != null) {
			notePickerView.updateGridRowPosition(noteName, columnIndex, noteLength, toggled);
		}
	}

	private List<GridRowPosition> getGridRowsForCurrentTact() {
		return gridRow.gridRowForTact(tactPosition);
	}

	public List<NoteView> getNoteViews() {
		return noteViews;
	}

	public int getTactCount() {
		return quarterCount;
	}

	public void setBlackRow(boolean blackRow) {
		isBlackRow = blackRow;
	}

	public void alertNoteChanged() {
		if (notePickerView != null) {
			notePickerView.disableAllNotes();
			notePickerView.setSelectedNote(gridRow.getNoteName().getMidi());
			notePickerView.onNoteChanged();
		}
	}

	public void disableOwnNotes() {
		for (NoteView noteview : noteViews) {
			if (noteview.isToggled()) {
				noteview.setNoteActive(false, true);
			}
		}
	}

	public static int getMidiValueForRow(int row) {
		return row * TrackView.HIGHEST_MIDI / TrackView.ROW_COUNT + TrackView.HIGHEST_MIDI / TrackView.ROW_COUNT;
	}
}

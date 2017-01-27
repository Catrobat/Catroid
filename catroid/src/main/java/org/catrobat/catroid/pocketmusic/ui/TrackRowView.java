/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2017 The Catrobat Team
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
import android.support.v4.content.ContextCompat;
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

public class TrackRowView extends TableRow {

	public static final int QUARTER_COUNT = 4;
	private int tactPosition = 0;
	private final MusicalBeat beat;
	private List<NoteView> noteViews = new ArrayList<>(QUARTER_COUNT);
	private boolean isBlackRow;
	private GridRow gridRow;
	private TrackView trackView;
	private NoteName noteName;

	public TrackRowView(Context context) {
		this(context, MusicalBeat.BEAT_4_4, false, NoteName.C1, null);
	}

	public TrackRowView(Context context, MusicalBeat beat, boolean isBlackRow, NoteName noteName, TrackView trackView) {
		super(context);
		this.beat = beat;
		this.noteName = noteName;
		this.trackView = trackView;
		this.setBlackRow(isBlackRow);
		initializeRow();
		setWeightSum(QUARTER_COUNT);
		updateGridRow();
	}

	public void setTactPosition(int tactPosition, GridRow gridRow) {
		this.gridRow = gridRow;
		this.tactPosition = tactPosition;
		refreshNoteViews();
	}

	private void refreshNoteViews() {
		for (NoteView noteView : noteViews) {
			noteView.setNoteActive(false, false);
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
		int noteColor;
		if (isBlackRow) {
			noteColor = ContextCompat.getColor(getContext(), R.color.light_grey);
		} else {
			noteColor = ContextCompat.getColor(getContext(), R.color.white);
		}
		for (int i = 0; i < QUARTER_COUNT; i++) {
			noteViews.add(new NoteView(getContext(), noteColor, this, i));
			addView(noteViews.get(i), params);
		}
	}

	public void updateGridRowPosition(int columnIndex, NoteLength noteLength, boolean toggled) {
		trackView.updateGridRowPosition(noteName, columnIndex, noteLength, toggled);
	}

	private List<GridRowPosition> getGridRowsForCurrentTact() {
		return gridRow.gridRowForTact(tactPosition);
	}

	public List<NoteView> getNoteViews() {
		return noteViews;
	}

	public int getTactCount() {
		return QUARTER_COUNT;
	}

	public void setBlackRow(boolean blackRow) {
		isBlackRow = blackRow;
	}
}

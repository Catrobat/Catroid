/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2016 The Catrobat Team
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
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.TableLayout;

import org.catrobat.catroid.R;
import org.catrobat.catroid.pocketmusic.note.MusicalBeat;
import org.catrobat.catroid.pocketmusic.note.MusicalInstrument;
import org.catrobat.catroid.pocketmusic.note.MusicalKey;
import org.catrobat.catroid.pocketmusic.note.NoteLength;
import org.catrobat.catroid.pocketmusic.note.NoteName;
import org.catrobat.catroid.pocketmusic.note.Track;
import org.catrobat.catroid.pocketmusic.note.trackgrid.GridRow;
import org.catrobat.catroid.pocketmusic.note.trackgrid.TrackGrid;
import org.catrobat.catroid.pocketmusic.note.trackgrid.TrackToTrackGridConverter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TrackView extends TableLayout {

	public static final int ROW_COUNT = 13;
	private List<TrackRowView> trackRowViews = new ArrayList<>(ROW_COUNT);
	private TrackGrid trackGrid;
	private static final int[] BLACK_KEY_INDICES = {
			1, 3, 6, 8, 10
	};

	public TrackView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setStretchAllColumns(true);
		setClickable(true);
		trackGrid = new TrackGrid(MusicalKey.VIOLIN, MusicalInstrument.ACCORDION, MusicalBeat.BEAT_4_4, new
				ArrayList<GridRow>());
		initializeRows();
		setWeightSum(ROW_COUNT);
	}

	private void initializeRows() {
		if (!trackRowViews.isEmpty()) {
			removeAllViews();
			trackRowViews.clear();
		}
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, 0, 1.0f);
		for (int i = 0; i < ROW_COUNT; i++) {
			boolean isBlackRow = Arrays.binarySearch(BLACK_KEY_INDICES, i) > -1;
			NoteName noteName = NoteName.getNoteNameFromMidiValue(NoteName.C1.getMidi() + i);
			trackRowViews.add(new TrackRowView(getContext(), trackGrid.getBeat(), isBlackRow, noteName,
					trackGrid.getGridRowForNoteName(noteName), this));
			addView(trackRowViews.get(i), params);
		}
	}

	public List<TrackRowView> getTrackRowViews() {
		return trackRowViews;
	}

	public void setTrack(Track track, int beatsPerMinute) {
		trackGrid = TrackToTrackGridConverter.convertTrackToTrackGrid(track, MusicalBeat.BEAT_4_4, beatsPerMinute);
		initializeRows();
	}

	public void updateGridRowPosition(NoteName noteName, int columnIndex, NoteLength noteLength, boolean toggled) {
		trackGrid.updateGridRowPosition(noteName, columnIndex, noteLength, toggled);
	}

	public TrackGrid getTrackGrid() {
		return trackGrid;
	}

	public void colorGridColumn(int column) {
		column = columnSanityCheck(column);

		for (int i = 0; i < ROW_COUNT; i++) {
			boolean isBlackRow = Arrays.binarySearch(BLACK_KEY_INDICES, i) > -1;
			int noteColorId;
			if (isBlackRow) {
				noteColorId = R.color.opaque_turquoise_play_line_dark;
			} else {
				noteColorId = R.color.opaque_turquoise_play_line;
			}
			trackRowViews.get(i).getChildAt(column).setBackgroundColor(ContextCompat.getColor(getContext(),
					noteColorId));
		}
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		return !isClickable() || super.onInterceptTouchEvent(ev);
	}

	public void clearColorGridColumn(int column) {
		column = columnSanityCheck(column);

		for (int i = 0; i < ROW_COUNT; i++) {
			boolean isBlackRow = Arrays.binarySearch(BLACK_KEY_INDICES, i) > -1;
			int noteColorId;
			if (isBlackRow) {
				noteColorId = R.color.light_grey;
			} else {
				noteColorId = R.color.white;
			}
			trackRowViews.get(i).getChildAt(column).setBackgroundColor(ContextCompat.getColor(getContext(),
					noteColorId));
		}
	}

	private int columnSanityCheck(int column) {
		int notesPerTact = trackRowViews.get(0).getChildCount();
		if (column >= notesPerTact) {
			column = notesPerTact - 1;
		}
		if (column < 0) {
			column = 0;
		}
		return column;
	}
}

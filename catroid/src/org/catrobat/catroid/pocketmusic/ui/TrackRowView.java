/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2015 The Catrobat Team
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

import java.util.ArrayList;
import java.util.List;

public class TrackRowView extends TableRow {

	private int tactCount = 4;
	private List<NoteView> noteViews = new ArrayList<>(tactCount);

	public TrackRowView(Context context) {
		super(context);
		initializeRow();
	}

	private void initializeRow() {
		for (int i = 0; i < tactCount; ++i) {
			noteViews.add(new NoteView(getContext()));
			LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			params.leftMargin = params.topMargin = params.rightMargin = params.bottomMargin = 2;
			addView(noteViews.get(i), params);
		}
	}

	public List<NoteView> getNoteViews() {
		return noteViews;
	}

	public int getTactCount() {
		return tactCount;
	}
}

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
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;

import org.catrobat.catroid.R;
import org.catrobat.catroid.pocketmusic.note.NoteLength;

public class NoteView extends ImageView implements View.OnClickListener {

	private static final int HIDDEN = 0;
	private static final int FULL_VISIBLE = 255;
	private final int horizontalIndexInGridRowPosition;
	private boolean toggled;
	private Drawable noteDrawable;
	private TrackRowView trackRowView;

	public NoteView(Context context) {
		this(context, null, 0);
	}

	public NoteView(Context context, TrackRowView trackRowView, int horizontalIndexInGridRowPosition) {
		super(context);
		setOnClickListener(this);
		setAdjustViewBounds(true);
		setScaleType(ScaleType.CENTER_INSIDE);
		initNoteDrawable();
		this.trackRowView = trackRowView;
		this.horizontalIndexInGridRowPosition = horizontalIndexInGridRowPosition;
	}

	private void initNoteDrawable() {
		noteDrawable = ContextCompat.getDrawable(getContext(), R.drawable.ic_pocketmusic_note_toggle);
		noteDrawable.setColorFilter(ContextCompat.getColor(getContext(), R.color.terms_of_use_text_color), PorterDuff.Mode.SRC_IN);
		noteDrawable.mutate();
		noteDrawable.setAlpha(HIDDEN);
		setImageDrawable(noteDrawable);
	}

	public void setNoteActive(boolean active, boolean updateData) {
		if (toggled != active) {
			toggled = active;
			showNote();
			if (updateData) {
				updateGridRow();
			}
		}
	}

	@Override
	public void onClick(View v) {
		toggled = !toggled;
		showNote();
		updateGridRow();
	}

	private void updateGridRow() {
		trackRowView.updateGridRowPosition(horizontalIndexInGridRowPosition, NoteLength.QUARTER, toggled);
	}

	private void showNote() {
		if (toggled) {
			noteDrawable.setAlpha(FULL_VISIBLE);
		} else {
			noteDrawable.setAlpha(HIDDEN);
		}
		invalidate();
	}

	public boolean isToggled() {
		return toggled;
	}
}

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

package org.catrobat.catroid.pocketmusic;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageButton;

import org.catrobat.catroid.R;
import org.catrobat.catroid.pocketmusic.note.NoteLength;
import org.catrobat.catroid.pocketmusic.ui.TrackView;

public class ScrollController {

	private static final int LAST_NOTE_IN_TRACK_VIEW = 4;

	private ObjectAnimator playLineAnimator;
	private final View playLine;
	private final ImageButton playButton;
	private final TrackView trackView;
	private final int beatsPerMinute;
	private int currentPlayLineIndex = 0;

	public ScrollController(ViewGroup pocketmusicMainLayout, int beatsPerMinute) {
		this.beatsPerMinute = beatsPerMinute;
		this.playLine = pocketmusicMainLayout.findViewById(R.id.pocketmusic_play_line);
		this.playButton = (ImageButton) pocketmusicMainLayout.findViewById(R.id.pocketmusic_play_button);
		this.trackView = (TrackView) pocketmusicMainLayout.findViewById(R.id.musicdroid_note_grid);
		init();
	}

	private void init() {
		initializePlayLine();
		initializeAnimator();
	}

	@SuppressWarnings("unused")
	private void setGlobalPlayPosition(float xPosition) {
		int buttonWidth = trackView.getWidth() / LAST_NOTE_IN_TRACK_VIEW;
		int newPlayLineIndex = (int) (xPosition / buttonWidth);

		playLine.setX(xPosition);

		if (newPlayLineIndex == 0 || newPlayLineIndex > currentPlayLineIndex) {
			currentPlayLineIndex = newPlayLineIndex;
			onNoteLengthOutRun(newPlayLineIndex);
		}
	}

	private void initializeAnimator() {
		final long singleButtonDuration = NoteLength.QUARTER.toMilliseconds(beatsPerMinute);
		playLineAnimator = ObjectAnimator.ofFloat(this, "globalPlayPosition", 0, trackView.getWidth());
		playLineAnimator.setDuration(singleButtonDuration * LAST_NOTE_IN_TRACK_VIEW);
		playLineAnimator.setInterpolator(new LinearInterpolator());

		playLineAnimator.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationStart(Animator animation) {
				playButton.setImageResource(R.drawable.ic_stop_24dp);
				playLine.setVisibility(View.VISIBLE);
				trackView.setClickable(false);
			}

			@Override
			public void onAnimationEnd(Animator animation) {
				playButton.setImageResource(R.drawable.ic_play);
				playLine.setVisibility(View.GONE);
				trackView.clearColorGridColumn(currentPlayLineIndex);
				trackView.setClickable(true);
				currentPlayLineIndex = 0;
			}
		});
	}

	private void initializePlayLine() {

		trackView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
			@Override
			public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
				initializeAnimator();
				trackView.removeOnLayoutChangeListener(this);
			}
		});

		playButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (playLineAnimator.isRunning()) {
					trackView.getTrackGrid().stopPlayback();
					playLineAnimator.cancel();
					playLineAnimator.setupStartValues();
				} else {
					playLineAnimator.start();
					trackView.getTrackGrid().startPlayback();
				}
			}
		});
	}

	private void onNoteLengthOutRun(int index) {
		if (index >= 0 & index < LAST_NOTE_IN_TRACK_VIEW) {
			trackView.colorGridColumn(index);
		}
		if (index > 0 && index <= LAST_NOTE_IN_TRACK_VIEW) {
			trackView.clearColorGridColumn(index - 1);
		}
	}
}

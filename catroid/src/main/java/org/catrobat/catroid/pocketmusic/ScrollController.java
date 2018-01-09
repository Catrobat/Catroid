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
import org.catrobat.catroid.pocketmusic.ui.PianoView;
import org.catrobat.catroid.pocketmusic.ui.TactScrollRecyclerView;
import org.catrobat.catroid.pocketmusic.ui.TrackRowView;

public class ScrollController {

	private static final int NUMBER_OF_NOTES_IN_TRACK_VIEW = 8;

	private ObjectAnimator scrollingAnimator;
	private final View playLine;
	private final PianoView pianoView;
	private final ImageButton playButton;
	private final TactScrollRecyclerView scrollingView;
	private final int beatsPerMinute;
	private int oldScrollPosition = 0;

	public ScrollController(ViewGroup pocketmusicMainLayout, TactScrollRecyclerView tactScrollRecyclerView, int beatsPerMinute) {
		this.beatsPerMinute = beatsPerMinute;
		this.scrollingView = tactScrollRecyclerView;
		this.playLine = pocketmusicMainLayout.findViewById(R.id.pocketmusic_play_line);
		this.playButton = (ImageButton) pocketmusicMainLayout.findViewById(R.id.pocketmusic_play_button);
		this.pianoView = (PianoView) pocketmusicMainLayout.findViewById(R.id.musicdroid_piano);
		init();
	}

	private void init() {
		initializePlayLine();
		initializeAnimator();
	}

	@SuppressWarnings("unused")
	private void setGlobalPlayPosition(int xPosition) {
		scrollingView.scrollBy(xPosition - oldScrollPosition, 0);
		oldScrollPosition = xPosition;
	}

	private void initializeAnimator() {
		final long singleButtonDuration = NoteLength.QUARTER.toMilliseconds(beatsPerMinute);
		final int singleButtonWidth = scrollingView.getMeasuredWidth() / NUMBER_OF_NOTES_IN_TRACK_VIEW;
		final int buttonsInTrack = scrollingView.getTrackGrid().getTactCount() * TrackRowView.QUARTER_COUNT;
		scrollingAnimator = ObjectAnimator.ofInt(this, "globalPlayPosition", singleButtonWidth * buttonsInTrack);

		scrollingAnimator.setDuration(singleButtonDuration * buttonsInTrack);
		scrollingAnimator.setInterpolator(new LinearInterpolator());

		scrollingAnimator.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationStart(Animator animation) {
				playButton.setImageResource(R.drawable.ic_stop_24dp);
				playLine.setVisibility(View.VISIBLE);
				scrollingView.setPlaying(true);
				scrollingView.smoothScrollToPosition(0);
				scrollingView.getTrackGrid().startPlayback(pianoView);
			}

			@Override
			public void onAnimationEnd(Animator animation) {
				playButton.setImageResource(R.drawable.ic_play);
				playLine.setVisibility(View.GONE);
				scrollingView.setPlaying(false);
				scrollingView.getTrackGrid().stopPlayback(pianoView);
				scrollingView.smoothScrollToPosition(0);
			}
		});
	}

	private void initializePlayLine() {

		scrollingView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
			@Override
			public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
				initializeAnimator();
				scrollingView.removeOnLayoutChangeListener(this);
			}
		});

		playButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (scrollingAnimator.isRunning()) {
					scrollingAnimator.cancel();
					scrollingAnimator.setupStartValues();
				} else {
					scrollingAnimator.start();
				}
			}
		});
	}
}

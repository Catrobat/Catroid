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
import android.media.Image;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageButton;

import org.catrobat.catroid.R;
import org.catrobat.catroid.pocketmusic.note.NoteLength;
import org.catrobat.catroid.pocketmusic.note.Project;
import org.catrobat.catroid.pocketmusic.ui.TactScrollRecyclerView;

public class ScrollController {

	private static final int LAST_NOTE_IN_TRACK_VIEW = 8;

	private ObjectAnimator playLineAnimator;
	private final View playLine;
	private final ImageButton playButton;
	private final ImageButton stopButton;
	private final TactScrollRecyclerView scrollingView;
	private final int beatsPerMinute;

	private boolean playbackOnPause;
	private long starPlaybackTime;

	public ScrollController(ViewGroup pocketmusicMainLayout, TactScrollRecyclerView tactScrollRecyclerView, int beatsPerMinute) {
		this.beatsPerMinute = beatsPerMinute;
		this.scrollingView = tactScrollRecyclerView;
		this.playLine = pocketmusicMainLayout.findViewById(R.id.pocketmusic_play_line);
		this.playButton = (ImageButton) pocketmusicMainLayout.findViewById(R.id.pocketmusic_play_button);
		this.stopButton = (ImageButton) pocketmusicMainLayout.findViewById(R.id.pocketmusic_stop_button);
		this.playbackOnPause = false;
		this.starPlaybackTime = 0;


		init();
	}

	private void init() {
		stopButton.setVisibility(View.GONE);
		initializePlayLine();
		initializeAnimator();
	}

	@SuppressWarnings("unused")
	private void setGlobalPlayPosition(float xPosition) {
		int buttonWidth = scrollingView.getWidth() / LAST_NOTE_IN_TRACK_VIEW;
		int newPlayLineIndex = (int) (xPosition / buttonWidth);

		playLine.setX(xPosition);
	}

	private void initializeAnimator() {
		final long singleButtonDuration = NoteLength.QUARTER.toMilliseconds(beatsPerMinute);
		playLineAnimator = ObjectAnimator.ofFloat(this, "globalPlayPosition", 0, scrollingView.getWidth());
		playLineAnimator.setDuration(singleButtonDuration * LAST_NOTE_IN_TRACK_VIEW);
		playLineAnimator.setInterpolator(new LinearInterpolator());


		playLineAnimator.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationStart(Animator animation) {
				playLine.setVisibility(View.VISIBLE);
				scrollingView.setPlaying(true);
			}

			@Override
			public void onAnimationEnd(Animator animation) {
				playButton.setImageResource(R.drawable.ic_play);
				if (playbackOnPause)
				{
					stopButton.setVisibility(View.VISIBLE);
					playLine.setVisibility(View.VISIBLE);

				}
				else
				{
					starPlaybackTime = 0;
					stopButton.setVisibility(View.GONE);
					playLine.setVisibility(View.GONE);

				}
				scrollingView.setPlaying(false);
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

				if (playLineAnimator.isRunning()) {

					long playLength = NoteLength.QUARTER.toMilliseconds(Project.DEFAULT_BEATS_PER_MINUTE);
					long mod = 0;
					long currentPlayTime = playLineAnimator.getCurrentPlayTime();
					if ( currentPlayTime > playLength)
					{
						mod = currentPlayTime % playLength;
						starPlaybackTime = playLineAnimator.getCurrentPlayTime() - mod;
					}
					else
					{
						starPlaybackTime = 0;
					}

					playbackOnPause = true;
					playButton.setImageResource(R.drawable.ic_play);
					scrollingView.getTrackGrid().stopPlayback();
					playLineAnimator.cancel();
					playLineAnimator.setupStartValues();

				} else {
					playbackOnPause = false;

					playButton.setImageResource(R.drawable.ic_pause_dark);
					stopButton.setVisibility(View.VISIBLE);

					playLineAnimator.setCurrentPlayTime(starPlaybackTime);
					playLineAnimator.start();
					scrollingView.getTrackGrid().startPlayback(starPlaybackTime);
				}
			}
		});

		stopButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				starPlaybackTime = 0;
				stopButton.setVisibility(View.GONE);
				playbackOnPause = false;
				if (playLineAnimator.isRunning()) {
					scrollingView.getTrackGrid().stopPlayback();
					playLineAnimator.cancel();
					playLineAnimator.setupStartValues();
				}

			}
		});
	}
}

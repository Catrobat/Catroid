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
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import org.catrobat.catroid.pocketmusic.TactViewHolder;
import org.catrobat.catroid.pocketmusic.note.MusicalBeat;
import org.catrobat.catroid.pocketmusic.note.MusicalInstrument;
import org.catrobat.catroid.pocketmusic.note.MusicalKey;
import org.catrobat.catroid.pocketmusic.note.Track;
import org.catrobat.catroid.pocketmusic.note.trackgrid.GridRow;
import org.catrobat.catroid.pocketmusic.note.trackgrid.TrackGrid;
import org.catrobat.catroid.pocketmusic.note.trackgrid.TrackToTrackGridConverter;

import java.util.ArrayList;

public class TactScrollRecyclerView extends RecyclerView {

	private static final int TACTS_PER_SCREEN = 2;
	private static final int MINIMUM_TACT_COUNT = 3;
	private static final int ALWAYS_ONE_ADDITIONAL_TACT = 1;
	private TrackGrid trackGrid;
	private ViewGroup.LayoutParams tactViewParams = new ViewGroup.LayoutParams(0, 0);
	private boolean isPlaying;
	private int tactCount;
	private TactSnapper tactSnapper;

	public TactScrollRecyclerView(Context context) {
		this(context, null);
	}

	public TactScrollRecyclerView(Context context, @Nullable AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public TactScrollRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
		setAdapter(new TactAdapter());
		setHorizontalScrollBarEnabled(true);
		setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
		trackGrid = new TrackGrid(MusicalKey.VIOLIN, MusicalInstrument.ACCORDION, MusicalBeat.BEAT_4_4, new
				ArrayList<GridRow>());

		tactSnapper = new TactSnapper();
		addOnScrollListener(tactSnapper);
	}

	@Override
	public boolean onTouchEvent(MotionEvent e) {
		tactSnapper.setScrollStartedByUser(true);
		return super.onTouchEvent(e);
	}

	public void setTrack(Track track, int beatsPerMinute) {
		this.trackGrid = TrackToTrackGridConverter.convertTrackToTrackGrid(track, MusicalBeat.BEAT_4_4, beatsPerMinute);
		tactCount = Math.max(trackGrid.getTactCount() + ALWAYS_ONE_ADDITIONAL_TACT, MINIMUM_TACT_COUNT);
	}

	public TrackGrid getTrackGrid() {
		return trackGrid;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		return isPlaying || super.onInterceptTouchEvent(ev);
	}

	public boolean isPlaying() {
		return isPlaying;
	}

	public void setPlaying(boolean playing) {
		isPlaying = playing;
	}

	@Override
	protected void onMeasure(int widthSpec, int heightSpec) {
		tactViewParams.width = MeasureSpec.getSize(widthSpec) / TACTS_PER_SCREEN;
		tactViewParams.height = MeasureSpec.getSize(heightSpec);
		super.onMeasure(widthSpec, heightSpec);
	}

	private class TactAdapter extends RecyclerView.Adapter<TactViewHolder> {

		@Override
		public TactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
			TrackView trackView = new TrackView(getContext(), trackGrid);
			trackView.setLayoutParams(tactViewParams);
			return new TactViewHolder(trackView);
		}

		@Override
		public void onBindViewHolder(TactViewHolder holder, int position) {
			TrackView trackView = (TrackView) holder.itemView;
			trackView.updateDataForTactPosition(position);
		}

		@Override
		public int getItemCount() {
			return Math.max(tactCount, MINIMUM_TACT_COUNT);
		}
	}

	class TactSnapper extends OnScrollListener {
		private boolean scrollStartedByUser = false;

		@Override
		public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
			if (newState == RecyclerView.SCROLL_STATE_IDLE && scrollStartedByUser) {
				if (recyclerView.getChildCount() != 0) {
					RecyclerView.ViewHolder holder = getViewHolderAtScreenCenter();
					if (holder != null) {
						recyclerView.smoothScrollBy((int) holder.itemView.getX(), 0);
					}
				}
				scrollStartedByUser = false;
			}
			super.onScrollStateChanged(recyclerView, newState);
		}

		public void setScrollStartedByUser(boolean scrollStartedByUser) {
			this.scrollStartedByUser = scrollStartedByUser;
		}

		TactViewHolder getViewHolderAtScreenCenter() {
			int midX = getWidth() / 4;
			int midY = getHeight() / 2;
			View view = findChildViewUnder(midX, midY);

			TactViewHolder tactViewHolder = null;
			try {
				tactViewHolder = (TactViewHolder) getChildViewHolder(view);
			} catch (NullPointerException e) {
				Log.w(getClass().getSimpleName(), "Warning: Tact not found for centering");
			}

			return tactViewHolder;
		}
	}
}

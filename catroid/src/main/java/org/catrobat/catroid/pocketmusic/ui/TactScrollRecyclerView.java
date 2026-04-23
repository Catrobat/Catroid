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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import org.catrobat.catroid.R;
import org.catrobat.catroid.pocketmusic.TactViewHolder;
import org.catrobat.catroid.pocketmusic.fastscroller.SectionTitleProvider;
import org.catrobat.catroid.pocketmusic.note.MusicalBeat;
import org.catrobat.catroid.pocketmusic.note.MusicalInstrument;
import org.catrobat.catroid.pocketmusic.note.MusicalKey;
import org.catrobat.catroid.pocketmusic.note.Track;
import org.catrobat.catroid.pocketmusic.note.trackgrid.GridRow;
import org.catrobat.catroid.pocketmusic.note.trackgrid.TrackGrid;
import org.catrobat.catroid.pocketmusic.note.trackgrid.TrackToTrackGridConverter;

import java.util.ArrayList;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class TactScrollRecyclerView extends RecyclerView {

	private static final int TACTS_PER_SCREEN = 2;
	private static final int TACT_VIEW_TYPE = 100;
	private static final int PLUS_VIEW_TYPE = 101;
	private static final int EMPTY_END_VIEW_TYPE = 102;

	private TrackGrid trackGrid;
	private ViewGroup.LayoutParams tactViewParams = new ViewGroup.LayoutParams(0, 0);
	private boolean isPlaying;
	private int tactCount;
	private TactSnapper tactSnapper;
	private AnimatorUpdateCallback animatorUpdateCallback;

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

	public void setUpdateAnimatorCallback(AnimatorUpdateCallback callback) {
		animatorUpdateCallback = callback;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent e) {
		return e.getActionMasked() != MotionEvent.ACTION_SCROLL && isPlaying || super.onInterceptTouchEvent(e);
	}

	@Override
	public boolean onTouchEvent(MotionEvent e) {
		if (!isPlaying()) {
			tactSnapper.setScrollStartedByUser(true);
			return super.onTouchEvent(e);
		}
		return false;
	}

	public void setTrack(Track track, int beatsPerMinute) {
		this.trackGrid = TrackToTrackGridConverter.convertTrackToTrackGrid(track, MusicalBeat.BEAT_4_4,
				beatsPerMinute);
		tactCount = Math.max(trackGrid.getTactCount(), TACTS_PER_SCREEN);
	}

	public TrackGrid getTrackGrid() {
		return trackGrid;
	}

	public boolean isPlaying() {
		return isPlaying;
	}

	public void setPlaying(boolean playing) {
		isPlaying = playing;
		getAdapter().notifyDataSetChanged();
	}

	public int getTactCount() {
		return tactCount;
	}

	public int getNotesPerScreen() {
		return TACTS_PER_SCREEN * trackGrid.getBeat().getTopNumber();
	}

	@Override
	protected void onMeasure(int widthSpec, int heightSpec) {
		if (MeasureSpec.getMode(widthSpec) != MeasureSpec.UNSPECIFIED
				&& MeasureSpec.getMode(heightSpec) != MeasureSpec.UNSPECIFIED) {
			tactViewParams.width = MeasureSpec.getSize(widthSpec) / TACTS_PER_SCREEN;
			tactViewParams.height = MeasureSpec.getSize(heightSpec);
			animatorUpdateCallback.updateCallback(tactCount, getMeasuredWidth());
		}
		for (int i = 0; i < getChildCount(); i++) {
			getChildAt(i).getLayoutParams().width = tactViewParams.width;
			getChildAt(i).getLayoutParams().height = tactViewParams.height;
		}
		super.onMeasure(widthSpec, heightSpec);
	}

	public int getTactViewWidth() {
		return tactViewParams.width;
	}

	private class TactAdapter extends RecyclerView.Adapter<TactViewHolder> implements SectionTitleProvider {

		private static final int PLUS_BUTTON_ON_END = 1;
		private final OnClickListener addTactClickListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				tactCount++;
				notifyDataSetChanged();
			}
		};

		@Override
		public TactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
			View tactContent;
			switch (viewType) {
				case PLUS_VIEW_TYPE:
					tactContent = LayoutInflater.from(parent.getContext()).inflate(R.layout.pocketmusic_add_tact_button,
							parent, false);
					tactContent.setLayoutParams(tactViewParams);
					tactContent.setOnClickListener(addTactClickListener);
					break;
				case EMPTY_END_VIEW_TYPE:
					tactContent = LayoutInflater.from(parent.getContext()).inflate(R.layout.pocketmusic_empty_view,
							parent, false);
					tactContent.setLayoutParams(tactViewParams);
					break;
				default:
					tactContent = new TrackView(getContext(), trackGrid);
					tactContent.setLayoutParams(tactViewParams);
					animatorUpdateCallback.updateCallback(tactCount, getMeasuredWidth());
					break;
			}
			TactViewHolder tactViewHolder = new TactViewHolder(tactContent);
			tactViewHolder.setIsRecyclable(false);
			return tactViewHolder;
		}

		@Override
		public void onBindViewHolder(TactViewHolder holder, int position) {
			if (getItemViewType(position) == TACT_VIEW_TYPE) {
				TrackView trackView = (TrackView) holder.itemView;
				trackView.updateDataForTactPosition(position);
			}
		}

		@Override
		public int getItemViewType(int position) {
			if (isPlaying) {
				if (position >= getItemCount() - TACTS_PER_SCREEN) {
					return EMPTY_END_VIEW_TYPE;
				}
				return TACT_VIEW_TYPE;
			}
			if (position == getItemCount() - 1) {
				return PLUS_VIEW_TYPE;
			} else {
				return TACT_VIEW_TYPE;
			}
		}

		@Override
		public int getItemCount() {
			if (isPlaying) {
				return Math.max(tactCount, TACTS_PER_SCREEN) + TACTS_PER_SCREEN;
			}
			return Math.max(tactCount, TACTS_PER_SCREEN) + PLUS_BUTTON_ON_END;
		}

		@Override
		public String getSectionTitle(int position) {
			if (this.getItemCount() - PLUS_BUTTON_ON_END == position) {
				return "+";
			}
			return Integer.toString(position + 1);
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
				super.onScrollStateChanged(recyclerView, newState);
			}
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

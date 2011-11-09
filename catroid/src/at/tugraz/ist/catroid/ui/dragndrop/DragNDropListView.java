/*
 * Copyright (C) 2010 Draggable and Droppable ListView Project
 *
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010  Catroid development team 
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */
package at.tugraz.ist.catroid.ui.dragndrop;

import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import at.tugraz.ist.catroid.R;

public class DragNDropListView extends ExpandableListView {

	private ImageView dragView;
	private WindowManager windowManager;
	private WindowManager.LayoutParams windowParams;
	private int dragPosition; // which item is being dragged
	private int firstDragPosition; // where was the dragged item originally
	private int dragPoint; // at what offset inside the item did the user grab it
	private int coordOffset; // the difference between screen coordinates and coordinates in this view
	private DragListener dragListener;
	private DropListener dropListener;
	private RemoveListener removeListener;
	private int upperBound;
	private int lowerBound;
	private int height;
	private GestureDetector gestureDetector;
	private static final int FLING = 0;
	private static final int SLIDE = 1;
	private int removeMode = -1;
	private Rect tempRect = new Rect();
	private Bitmap dragBitmap;
	private final int theTouchSlop;
	private Context context;
	private ImageView trash;
	private int trashWidth;
	private int trashHeight;
	private int screenWidth;
	private HashMap<Integer, Integer> itemHeightMap = new HashMap<Integer, Integer>();

	public DragNDropListView(Context context, AttributeSet attrs) {
		super(context, attrs);

		DisplayMetrics displayMetrics = new DisplayMetrics();
		((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		screenWidth = displayMetrics.widthPixels;

		removeMode = SLIDE;
		this.context = context;
		theTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
	}

	public void setTrashView(ImageView trashView) {
		trash = trashView;
		android.view.ViewGroup.LayoutParams params = trash.getLayoutParams();
		trashWidth = params.width;
		trashHeight = params.height;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
		if (removeListener != null && gestureDetector == null) {
			if (removeMode == FLING) {
				gestureDetector = new GestureDetector(getContext(), new SimpleOnGestureListener() {
					@Override
					public boolean onFling(MotionEvent event1, MotionEvent event2, float velocityX, float velocityY) {
						if (dragView != null) {
							if (velocityX > 1000) {
								Rect rect = tempRect;
								dragView.getDrawingRect(rect);
								if (event2.getX() > rect.right * 2 / 3) {
									// fast fling right with release near the right edge of the screen
									stopDragging();
									removeListener.remove(convertPosition(firstDragPosition));
									unExpandViews(true);
								}
							}
							// flinging while dragging should have no effect
							return true;
						}
						return false;
					}
				});
			}
		}
		if (dragListener != null || dropListener != null) {
			switch (motionEvent.getAction()) {
				case MotionEvent.ACTION_DOWN:
					int x = (int) motionEvent.getX();
					int y = (int) motionEvent.getY();
					int itemPosition = pointToPosition(x, y);
					if (itemPosition == AdapterView.INVALID_POSITION) {
						break;
					}
					ViewGroup item = (ViewGroup) getChildAt(itemPosition - getFirstVisiblePosition());
					View dragger = item.findViewById(R.id.grabber);
					if (dragger == null) {
						return super.onInterceptTouchEvent(motionEvent);
					}
					dragPoint = y - item.getTop();
					coordOffset = ((int) motionEvent.getRawY()) - y;
					Rect rect = tempRect;
					dragger.getDrawingRect(rect);
					// The dragger icon itself is quite small, so pretend the touch area is bigger
					if (x < rect.right * 2) {
						item.setDrawingCacheEnabled(true);
						// Create a copy of the drawing cache so that it does not get recycled
						// by the framework when the list tries to clean up memory
						Bitmap bitmap = Bitmap.createBitmap(item.getDrawingCache());
						startDragging(bitmap, y);
						dragPosition = itemPosition;
						firstDragPosition = dragPosition;
						height = getHeight();
						int touchSlop = theTouchSlop;
						upperBound = Math.min(y - touchSlop, height / 3);
						lowerBound = Math.max(y + touchSlop, height * 2 / 3);
						return true;
					}
					dragView = null;
					break;
			}
		}
		return super.onInterceptTouchEvent(motionEvent);
	}

	/*
	 * pointToPosition() doesn't consider invisible views, but we
	 * need to, so implement a slightly different version.
	 */
	private int myPointToPosition(int x, int y) {
		Rect frame = tempRect;
		for (int i = getChildCount() - 1; i >= 0; i--) {
			final View child = getChildAt(i);
			child.getHitRect(frame);
			if (frame.contains(x, y)) {
				return getFirstVisiblePosition() + i;
			}
		}
		return INVALID_POSITION;
	}

	private int getItemForPosition(int y) {
		int adjustedY = y - dragPoint - 32;
		int position = myPointToPosition(0, adjustedY);
		if (position >= 0) {
			if (position <= firstDragPosition) {
				position += 1;
			}
		} else if (adjustedY < 0) {
			position = 0;
		}
		return position;
	}

	private void adjustScrollBounds(int y) {
		if (y >= height / 3) {
			upperBound = height / 3;
		}
		if (y <= height * 2 / 3) {
			lowerBound = height * 2 / 3;
		}
	}

	/*
	 * Restore size and visibility for all listitems
	 */
	private void unExpandViews(boolean deletion) {

		int firstPosition = getFirstVisiblePosition();
		for (int i = 0;; i++) {
			View view = getChildAt(i);
			if (view == null) {
				if (deletion) {
					// HACK force update of mItemCount
					int position = firstPosition;
					int y = getChildAt(0).getTop();
					setAdapter(getExpandableListAdapter());
					setSelectionFromTop(position, y);
					// end hack
					expandGroup(getExpandableListAdapter().getGroupCount() - 1);
				}
				layoutChildren(); // force children to be recreated where needed
				view = getChildAt(i);
				if (view == null) {
					break;
				}
			}
			ViewGroup.LayoutParams params = view.getLayoutParams();
			fillHeightMap(firstPosition + i, view.getHeight());
			params.height = itemHeightMap.get(firstPosition + i);
			view.setLayoutParams(params);
			view.setVisibility(View.VISIBLE);
		}
	}

	/*
	 * Adjust visibility and size to make it appear as though
	 * an item is being dragged around and other items are making
	 * room for it:
	 * If dropping the item would result in it still being in the
	 * same place, then make the dragged listitem's size normal,
	 * but make the item invisible.
	 * Otherwise, if the dragged listitem is still on screen, make
	 * it as small as possible and expand the item below the insert
	 * point.
	 * If the dragged item is not on screen, only expand the item
	 * below the current insertpoint.
	 */
	private void doExpansion() {
		int childnum = dragPosition - getFirstVisiblePosition();
		if (dragPosition > firstDragPosition) {
			//childnum++;
		}

		View first = getChildAt(firstDragPosition - getFirstVisiblePosition());

		for (int i = 0;; i++) {
			View view = getChildAt(i);
			if (view == null) {
				break;
			}
			int positionInMap = getFirstVisiblePosition() + i;
			fillHeightMap(positionInMap, view.getHeight());

			int height = itemHeightMap.get(positionInMap);
			int visibility = View.VISIBLE;
			if (view.equals(first)) {
				// processing the item that is being dragged
				if (dragPosition == firstDragPosition) {
					// hovering over the original location
					visibility = View.INVISIBLE;
				} else {
					// not hovering over it
					height = 1;
				}
			} else if (i == childnum) {
				if (dragPosition < getCount()) {
					height += itemHeightMap.get(firstDragPosition);
				}
			}
			ViewGroup.LayoutParams params = view.getLayoutParams();
			params.height = height;
			view.setLayoutParams(params);
			view.setVisibility(visibility);
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if (gestureDetector != null) {
			gestureDetector.onTouchEvent(ev);
		}
		if ((dragListener != null || dropListener != null) && dragView != null) {
			int action = ev.getAction();
			switch (action) {
				case MotionEvent.ACTION_UP:
				case MotionEvent.ACTION_CANCEL:
					Rect rect = tempRect;
					dragView.getDrawingRect(rect);
					stopDragging();

					ViewGroup.LayoutParams layoutParams = trash.getLayoutParams();
					layoutParams.width = trashWidth;
					layoutParams.height = trashHeight;
					trash.setLayoutParams(layoutParams);
					trash.setVisibility(View.GONE);

					if (removeMode == SLIDE && ev.getX() > rect.right * 3 / 4) {
						if (removeListener != null) {
							removeListener.remove(convertPosition(firstDragPosition));
						}
						unExpandViews(true);
					} else {
						if (dropListener != null && dragPosition >= 0 && dragPosition < getCount()) {
							dropListener.drop(convertPosition(firstDragPosition), convertPosition(dragPosition));
						}
						unExpandViews(false);
					}
					itemHeightMap.clear();
					break;

				case MotionEvent.ACTION_DOWN:
					trash.setVisibility(View.VISIBLE);
					Animation animation = AnimationUtils.loadAnimation(context, R.anim.trash_in);
					trash.startAnimation(animation);
				case MotionEvent.ACTION_MOVE:
					int x = (int) ev.getX();
					int y = (int) ev.getY();
					dragView(x, y);
					int itemPosition = getItemForPosition(y);
					if (itemPosition >= 0) {
						if (action == MotionEvent.ACTION_DOWN || itemPosition != dragPosition) {
							if (dragListener != null) {
								dragListener.drag(dragPosition, itemPosition);
							}
							dragPosition = itemPosition;
							doExpansion();
						}
						int speed = 0;
						adjustScrollBounds(y);
						if (y > lowerBound) {
							// scroll the list up a bit
							speed = y > (height + lowerBound) / 2 ? 16 : 4;
						} else if (y < upperBound) {
							// scroll the list down a bit
							speed = y < upperBound / 2 ? -16 : -4;
						}
						if (speed != 0) {
							int ref = pointToPosition(0, height / 2);
							if (ref == AdapterView.INVALID_POSITION) {
								//we hit a divider or an invisible view, check somewhere else
								ref = pointToPosition(0, height / 2 + getDividerHeight() + 64);
							}
							View view = getChildAt(ref - getFirstVisiblePosition());
							if (view != null) {
								int topPosition = view.getTop();
								setSelectionFromTop(ref, topPosition - speed);
							}
						}
					}
					break;
			}
			return true;
		}
		return super.onTouchEvent(ev);
	}

	private void startDragging(Bitmap bitmap, int y) {
		stopDragging();
		windowParams = new WindowManager.LayoutParams();
		windowParams.gravity = Gravity.TOP | Gravity.LEFT;
		windowParams.x = 0;
		windowParams.y = y - dragPoint + coordOffset;

		windowParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
		windowParams.width = screenWidth - trashWidth + 2;//WindowManager.LayoutParams.WRAP_CONTENT;
		windowParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
				| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
				| WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
		windowParams.format = PixelFormat.TRANSLUCENT;
		windowParams.windowAnimations = 0;

		ImageView imageView = new ImageView(getContext());
		int backgroundColor = Color.parseColor("#e0103010");
		imageView.setBackgroundColor(backgroundColor);
		imageView.setImageBitmap(bitmap);
		dragBitmap = bitmap;

		windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
		windowManager.addView(imageView, windowParams);
		dragView = imageView;
	}

	private void dragView(int x, int y) {
		if (removeMode == SLIDE) {
			float alpha = 1.0f;
			android.view.ViewGroup.LayoutParams layoutParams = trash.getLayoutParams();

			if (x > 100 && x < screenWidth - 100) {
				alpha = ((float) (screenWidth - x)) / (screenWidth - 100);
				float rate = 1 - alpha;
				layoutParams.width = (int) (trashWidth * (1 + rate));
				layoutParams.height = (int) (trashHeight * (1 + rate));
				trash.setLayoutParams(layoutParams);
				windowParams.alpha = alpha;
				windowParams.width = screenWidth - layoutParams.width + 2;
			}

		}
		windowParams.y = y - dragPoint + coordOffset;
		windowManager.updateViewLayout(dragView, windowParams);
	}

	private void stopDragging() {
		if (dragView != null) {
			dragView.setVisibility(GONE);
			WindowManager windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
			windowManager.removeView(dragView);
			dragView.setImageDrawable(null);
			dragView = null;
		}
		if (dragBitmap != null) {
			dragBitmap.recycle();
			dragBitmap = null;
		}
	}

	private void fillHeightMap(int position, int height) {
		if (!itemHeightMap.containsKey(position)) {
			itemHeightMap.put(position, height);
		}
	}

	private int convertPosition(int originalPosition) {
		int convertedPosition = originalPosition - getExpandableListAdapter().getGroupCount();
		if (convertedPosition < 0) {
			convertedPosition = 0;
		}
		return convertedPosition;
	}

	public void setOnDragListener(DragListener l) {
		dragListener = l;
	}

	public void setOnDropListener(DropListener l) {
		dropListener = l;
	}

	public void setOnRemoveListener(RemoveListener l) {
		removeListener = l;
	}

	public interface DragListener {
		void drag(int from, int to);
	}

	public interface DropListener {
		void drop(int from, int to);
	}

	public interface RemoveListener {
		void remove(int which);
	}
}
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
	private int dragPos; // which item is being dragged
	private int firstDragPos; // where was the dragged item originally
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

		DisplayMetrics dm = new DisplayMetrics();
		((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
		screenWidth = dm.widthPixels;

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
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		if (removeListener != null && gestureDetector == null) {
			if (removeMode == FLING) {
				gestureDetector = new GestureDetector(getContext(), new SimpleOnGestureListener() {
					@Override
					public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
							float velocityY) {
						if (dragView != null) {
							if (velocityX > 1000) {
								Rect r = tempRect;
								dragView.getDrawingRect(r);
								if (e2.getX() > r.right * 2 / 3) {
									// fast fling right with release near the right edge of the screen
									stopDragging();
									removeListener.remove(convertPosition(firstDragPos));
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
			switch (ev.getAction()) {
				case MotionEvent.ACTION_DOWN:
					int x = (int) ev.getX();
					int y = (int) ev.getY();
					int itemnum = pointToPosition(x, y);
					if (itemnum == AdapterView.INVALID_POSITION) {
						break;
					}
					ViewGroup item = (ViewGroup) getChildAt(itemnum - getFirstVisiblePosition());
					View dragger = item.findViewById(R.id.grabber);
					if (dragger == null) {
						return super.onInterceptTouchEvent(ev);
					}
					dragPoint = y - item.getTop();
					coordOffset = ((int) ev.getRawY()) - y;
					Rect r = tempRect;
					dragger.getDrawingRect(r);
					// The dragger icon itself is quite small, so pretend the touch area is bigger
					if (x < r.right * 2) {
						item.setDrawingCacheEnabled(true);
						// Create a copy of the drawing cache so that it does not get recycled
						// by the framework when the list tries to clean up memory
						Bitmap bitmap = Bitmap.createBitmap(item.getDrawingCache());
						startDragging(bitmap, y);
						dragPos = itemnum;
						firstDragPos = dragPos;
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
		return super.onInterceptTouchEvent(ev);
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
		int adjustedy = y - dragPoint - 32;
		int pos = myPointToPosition(0, adjustedy);
		if (pos >= 0) {
			if (pos <= firstDragPos) {
				pos += 1;
			}
		} else if (adjustedy < 0) {
			pos = 0;
		}
		return pos;
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

		int firstpos = getFirstVisiblePosition();
		for (int i = 0;; i++) {
			View v = getChildAt(i);
			if (v == null) {
				if (deletion) {
					// HACK force update of mItemCount
					int position = firstpos;
					int y = getChildAt(0).getTop();
					setAdapter(getExpandableListAdapter());
					setSelectionFromTop(position, y);
					// end hack
					expandGroup(getExpandableListAdapter().getGroupCount() - 1);
				}
				layoutChildren(); // force children to be recreated where needed
				v = getChildAt(i);
				if (v == null) {
					break;
				}
			}
			ViewGroup.LayoutParams params = v.getLayoutParams();
			fillHeightMap(firstpos + i, v.getHeight());
			params.height = itemHeightMap.get(firstpos + i);
			v.setLayoutParams(params);
			v.setVisibility(View.VISIBLE);
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
		int childnum = dragPos - getFirstVisiblePosition();
		if (dragPos > firstDragPos) {
			//childnum++;
		}

		View first = getChildAt(firstDragPos - getFirstVisiblePosition());

		for (int i = 0;; i++) {
			View vv = getChildAt(i);
			if (vv == null) {
				break;
			}
			int mappos = getFirstVisiblePosition() + i;
			fillHeightMap(mappos, vv.getHeight());

			int height = itemHeightMap.get(mappos);
			int visibility = View.VISIBLE;
			if (vv.equals(first)) {
				// processing the item that is being dragged
				if (dragPos == firstDragPos) {
					// hovering over the original location
					visibility = View.INVISIBLE;
				} else {
					// not hovering over it
					height = 1;
				}
			} else if (i == childnum) {
				if (dragPos < getCount()) {
					height += itemHeightMap.get(firstDragPos);
				}
			}
			ViewGroup.LayoutParams params = vv.getLayoutParams();
			params.height = height;
			vv.setLayoutParams(params);
			vv.setVisibility(visibility);
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
					Rect r = tempRect;
					dragView.getDrawingRect(r);
					stopDragging();

					android.view.ViewGroup.LayoutParams params = trash.getLayoutParams();
					params.width = trashWidth;
					params.height = trashHeight;
					trash.setLayoutParams(params);
					trash.setVisibility(View.GONE);

					if (removeMode == SLIDE && ev.getX() > r.right * 3 / 4) {
						if (removeListener != null) {
							removeListener.remove(convertPosition(firstDragPos));
						}
						unExpandViews(true);
					} else {
						if (dropListener != null && dragPos >= 0 && dragPos < getCount()) {
							dropListener.drop(convertPosition(firstDragPos), convertPosition(dragPos));
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
					int itemnum = getItemForPosition(y);
					if (itemnum >= 0) {
						if (action == MotionEvent.ACTION_DOWN || itemnum != dragPos) {
							if (dragListener != null) {
								dragListener.drag(dragPos, itemnum);
							}
							dragPos = itemnum;
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
							View v = getChildAt(ref - getFirstVisiblePosition());
							if (v != null) {
								int pos = v.getTop();
								setSelectionFromTop(ref, pos - speed);
							}
						}
					}
					break;
			}
			return true;
		}
		return super.onTouchEvent(ev);
	}

	private void startDragging(Bitmap bm, int y) {
		stopDragging();
		windowParams = new WindowManager.LayoutParams();
		windowParams.gravity = Gravity.TOP | Gravity.LEFT;
		windowParams.x = 0;
		windowParams.y = y - dragPoint + coordOffset;

		windowParams.height = android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
		windowParams.width = screenWidth - trashWidth + 2;//WindowManager.LayoutParams.WRAP_CONTENT;
		windowParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
				| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
				| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
				| WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
		windowParams.format = PixelFormat.TRANSLUCENT;
		windowParams.windowAnimations = 0;

		ImageView v = new ImageView(getContext());
		int backGroundColor = Color.parseColor("#e0103010");
		v.setBackgroundColor(backGroundColor);
		v.setImageBitmap(bm);
		dragBitmap = bm;

		windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
		windowManager.addView(v, windowParams);
		dragView = v;
	}

	private void dragView(int x, int y) {
		if (removeMode == SLIDE) {
			float alpha = 1.0f;
			android.view.ViewGroup.LayoutParams params = trash.getLayoutParams();

			if (x > 100 && x < screenWidth - 100) {
				alpha = ((float) (screenWidth - x)) / (screenWidth - 100);
				float rate = 1 - alpha;
				params.width = (int) (trashWidth * (1 + rate));
				params.height = (int) (trashHeight * (1 + rate));
				trash.setLayoutParams(params);
				windowParams.alpha = alpha;
				windowParams.width = screenWidth - params.width + 2;
			}

		}
		windowParams.y = y - dragPoint + coordOffset;
		windowManager.updateViewLayout(dragView, windowParams);
	}

	private void stopDragging() {
		if (dragView != null) {
			dragView.setVisibility(GONE);
			WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
			wm.removeView(dragView);
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
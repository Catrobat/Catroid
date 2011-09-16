/**
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
 */
package at.tugraz.ist.catroid.ui.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ExpandableListView;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.content.BroadcastScript;
import at.tugraz.ist.catroid.content.Script;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.content.StartScript;
import at.tugraz.ist.catroid.content.TapScript;
import at.tugraz.ist.catroid.content.WhenScript;
import at.tugraz.ist.catroid.content.bricks.Brick;
import at.tugraz.ist.catroid.content.bricks.BroadcastReceiverBrick;
import at.tugraz.ist.catroid.content.bricks.IfStartedBrick;
import at.tugraz.ist.catroid.content.bricks.IfTouchedBrick;
import at.tugraz.ist.catroid.content.bricks.LoopBeginBrick;
import at.tugraz.ist.catroid.content.bricks.LoopEndBrick;
import at.tugraz.ist.catroid.content.bricks.WhenBrick;
import at.tugraz.ist.catroid.ui.dragndrop.DragAndDropListView;
import at.tugraz.ist.catroid.ui.dragndrop.DragAndDropListener;

public class BrickAdapter extends BaseAdapter implements DragAndDropListener {

	public static final int FOCUS_BLOCK_DESCENDANTS = 2;

	private Context context;
	private Sprite sprite;
	private BrickListAnimation brickListAnimation;
	private boolean animateChildren;
	private int dragTargetPosition;
	private Brick draggedBrick;
	private OnLongClickListener longClickListener;
	private View insertionView;

	public BrickAdapter(Context context, Sprite sprite, DragAndDropListView listView) {
		this.context = context;
		this.sprite = sprite;
		brickListAnimation = new BrickListAnimation(this, listView);
		longClickListener = listView;
		insertionView = View.inflate(context, R.layout.brick_insert, null);
	}

	public void drag(int from, int to) {
		/*
		 * int childFrom = Math.max(0, from - getGroupCount());
		 * int childTo = Math.max(0, to - getGroupCount());
		 * childFrom = Math.min(childFrom, getChildCountFromLastGroup() - 1);
		 * childTo = Math.min(childTo, getChildCountFromLastGroup() - 1);
		 * 
		 * if (draggedBrick == null) {
		 * draggedBrick = getChild(getCurrentGroup(), childFrom);
		 * notifyDataSetChanged();
		 * }
		 * 
		 * ArrayList<Brick> brickList = getBrickList();
		 * 
		 * if (draggedBrick instanceof LoopBeginBrick) {
		 * LoopEndBrick loopEndBrick = ((LoopBeginBrick) draggedBrick).getLoopEndBrick();
		 * 
		 * if (childTo >= brickList.indexOf(loopEndBrick) || childFrom >= brickList.indexOf(loopEndBrick)) {
		 * return;
		 * }
		 * } else if (draggedBrick instanceof LoopEndBrick) {
		 * LoopBeginBrick loopBeginBrick = ((LoopEndBrick) draggedBrick).getLoopBeginBrick();
		 * 
		 * if (childTo <= brickList.indexOf(loopBeginBrick) || childFrom <= brickList.indexOf(loopBeginBrick)) {
		 * return;
		 * }
		 * }
		 * 
		 * dragTargetPosition = childTo;
		 * 
		 * if (childFrom != childTo) {
		 * Brick removedBrick = getBrickList().remove(childFrom);
		 * sprite.getScript(getCurrentGroup()).addBrick(childTo, removedBrick);
		 * notifyDataSetChanged();
		 * }
		 */

		System.out.println("BrickAdapter.drag() from: " + from);
		System.out.println("BrickAdapter.drag() to: " + to);

		int childFrom = ExpandableListView.getPackedPositionChild(from);
		int groupFrom = ExpandableListView.getPackedPositionGroup(from);

		int childTo = ExpandableListView.getPackedPositionChild(to);
		int groupTo = ExpandableListView.getPackedPositionGroup(to);

		//		int childTo;
		//		int groupTo;

		if (ExpandableListView.getPackedPositionType(to) == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
			childTo = ExpandableListView.getPackedPositionChild(to);
			groupTo = ExpandableListView.getPackedPositionGroup(to);

			if (draggedBrick == null && childFrom != -1) {
				//				draggedBrick = getChild(groupFrom, childFrom);
				notifyDataSetChanged();
			}

			ArrayList<Brick> brickList = sprite.getScript(groupFrom).getBrickList();

			if (draggedBrick instanceof LoopBeginBrick) {
				LoopEndBrick loopEndBrick = ((LoopBeginBrick) draggedBrick).getLoopEndBrick();

				if (childTo >= brickList.indexOf(loopEndBrick) || childFrom >= brickList.indexOf(loopEndBrick)) {
					return;
				}
			} else if (draggedBrick instanceof LoopEndBrick) {
				LoopBeginBrick loopBeginBrick = ((LoopEndBrick) draggedBrick).getLoopBeginBrick();

				if (childTo <= brickList.indexOf(loopBeginBrick) || childFrom <= brickList.indexOf(loopBeginBrick)) {
					return;
				}
			}

			dragTargetPosition = childTo;

			if (from != to) {
				sprite.getScript(groupFrom).removeBrick(draggedBrick);
				sprite.getScript(groupTo).addBrick(childTo, draggedBrick);
				notifyDataSetChanged();
			}

		}

	}

	public void drop(int to) {
		draggedBrick = null;
		notifyDataSetChanged();
	}

	public void remove(int index) {

		//		ArrayList<Brick> brickList = getBrickList();
		if (draggedBrick instanceof LoopBeginBrick) {
			LoopBeginBrick loopBeginBrick = (LoopBeginBrick) draggedBrick;
			//			brickList.remove(loopBeginBrick.getLoopEndBrick());
		} else if (draggedBrick instanceof LoopEndBrick) {
			LoopEndBrick loopEndBrick = (LoopEndBrick) draggedBrick;
			//			brickList.remove(loopEndBrick.getLoopBeginBrick());
		}

		//		brickList.remove(draggedBrick);
		draggedBrick = null;
		notifyDataSetChanged();
	}

	public OnLongClickListener getOnLongClickListener() {
		return longClickListener;
	}

	public int getCount() {

		int count = 0;
		for (int i = 0; i < sprite.getScriptCount(); i++) {
			count += getBrickCount(i) + 1;
		}
		return count;
	}

	public Object getItem(int element) {

		int count = 0;
		while (element > getBrickCount(count)) {
			element -= getBrickCount(count) + 1;
			count++;
		}
		if (element == 0) {
			return sprite.getScript(count);
		} else {
			//			HP_DEBUG
			Log.d("Test", "BrickNr: " + element + " Script: " + count);
			return sprite.getScript(count).getBrick(element - 1);
		}
	}

	private int getBrickCount(int scriptIndex) {
		return sprite.getScript(scriptIndex).getBrickList().size();
	}

	public long getItemId(int index) {
		return index;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		// if group kein longclicklistener

		if (getItem(position) instanceof Brick) {
			Brick brick = (Brick) getItem(position);
			View currentBrickView = brick.getView(context, position, this);
			currentBrickView.setOnLongClickListener(longClickListener);
			return currentBrickView;
		} else {
			View view = null;

			if (getItem(position) instanceof TapScript) {
				view = new IfTouchedBrick(sprite, (Script) getItem(position)).getView(context, position, this);
			} else if (getItem(position) instanceof BroadcastScript) {
				view = new BroadcastReceiverBrick(sprite, (BroadcastScript) getItem(position)).getView(context,
						position, this);
			} else if (getItem(position) instanceof StartScript) {
				view = new IfStartedBrick(sprite, (Script) getItem(position)).getView(context, position, this);
			} else if (getItem(position) instanceof WhenScript) {
				view = new WhenBrick(sprite, (WhenScript) getItem(position)).getView(context, position, this);
			}
			return view;
		}
	}

	private int getScriptId(int index) {
		int count = 0;
		while (index > getBrickCount(count)) {
			index -= getBrickCount(count) + 1;
			count++;
		}
		return count;
	}

	private boolean isBrick(int index) {
		Object obj = getItem(index);
		if (obj instanceof Brick) {
			return true;
		} else {
			return false;
		}
	}

}

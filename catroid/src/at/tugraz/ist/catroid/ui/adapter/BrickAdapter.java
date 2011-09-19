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
import at.tugraz.ist.catroid.ProjectManager;
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

		int scriptFrom = getScriptId(from);
		int scriptTo = getScriptId(to);

		if (isBrick(to)) {

			//			Log.d("Test", "BrickAdapter.drag() from: " + from);
			//			Log.d("Test", "BrickAdapter.drag() to: " + to);
			//			Log.d("Test", "BrickAdapter.drag() scriptPosition: " + getScriptPosition(to, scriptTo));

			if (draggedBrick == null) {
				if (isBrick(from)) {
					draggedBrick = (Brick) getItem(from);
				} else {
					Log.d("Test", "BrickAdapter.drag() from: childFrom was Script not Brick!!!");
				}
				notifyDataSetChanged();
			}

			ArrayList<Brick> brickList = sprite.getScript(getScriptId(from)).getBrickList();
			if (draggedBrick instanceof LoopBeginBrick) {
				LoopEndBrick loopEndBrick = ((LoopBeginBrick) draggedBrick).getLoopEndBrick();

				if (getScriptPosition(to, scriptTo) >= brickList.indexOf(loopEndBrick)
						|| getScriptPosition(from, scriptFrom) >= brickList.indexOf(loopEndBrick)) {
					return;
				}
			} else if (draggedBrick instanceof LoopEndBrick) {
				LoopBeginBrick loopBeginBrick = ((LoopEndBrick) draggedBrick).getLoopBeginBrick();

				if (getScriptPosition(to, scriptTo) <= brickList.indexOf(loopBeginBrick)
						|| getScriptPosition(from, scriptFrom) <= brickList.indexOf(loopBeginBrick)) {
					return;
				}
			}

			dragTargetPosition = to;

			if (from != to) {
				sprite.getScript(scriptFrom).removeBrick(draggedBrick);

				sprite.getScript(scriptTo).addBrick(getScriptPosition(to, scriptTo), draggedBrick);
			}

		} else { //isScript(to) == true

			if (from < to) {
				sprite.getScript(scriptFrom).removeBrick(draggedBrick);
				sprite.getScript(getScriptId(to)).addBrick(0, draggedBrick);
			} else if (from > to && to > 0) {
				sprite.getScript(scriptFrom).removeBrick(draggedBrick);
				Log.d("BrickAdapter", "getScriptId(to) - 1: " + (getScriptId(to) - 1));
				sprite.getScript(getScriptId(to) - 1).addBrick(
						sprite.getScript(getScriptId(to) - 1).getBrickList().size(), draggedBrick);
			}
		}

		ProjectManager.getInstance().setCurrentScript(sprite.getScript(getScriptId(to)));

		notifyDataSetChanged();

	}

	public void drop(int to) {
		draggedBrick = null;
		notifyDataSetChanged();
	}

	public void remove(int index) {

		if (draggedBrick instanceof LoopBeginBrick) {
			LoopBeginBrick loopBeginBrick = (LoopBeginBrick) draggedBrick;
			sprite.getScript(getScriptId(index)).removeBrick(loopBeginBrick.getLoopEndBrick());

		} else if (draggedBrick instanceof LoopEndBrick) {
			LoopEndBrick loopEndBrick = (LoopEndBrick) draggedBrick;
			sprite.getScript(getScriptId(index)).removeBrick(loopEndBrick.getLoopBeginBrick());
		}

		sprite.getScript(getScriptId(index)).removeBrick(draggedBrick);

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

	private int getScriptPosition(int index, int script) {

		int scriptCount = 0;

		while (scriptCount < script) {
			index -= sprite.getScript(scriptCount).getBrickList().size() + 1;
			scriptCount++;
		}

		return --index;
	}

}

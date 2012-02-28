/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010-2011 The Catroid Team
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://www.catroid.org/catroid_license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *   
 *  You should have received a copy of the GNU Affero General Public License
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
import at.tugraz.ist.catroid.content.WhenScript;
import at.tugraz.ist.catroid.content.bricks.Brick;
import at.tugraz.ist.catroid.content.bricks.BroadcastReceiverBrick;
import at.tugraz.ist.catroid.content.bricks.LoopBeginBrick;
import at.tugraz.ist.catroid.content.bricks.LoopEndBrick;
import at.tugraz.ist.catroid.content.bricks.WhenBrick;
import at.tugraz.ist.catroid.content.bricks.WhenStartedBrick;
import at.tugraz.ist.catroid.ui.dragndrop.DragAndDropListView;
import at.tugraz.ist.catroid.ui.dragndrop.DragAndDropListener;

public class BrickAdapter extends BaseAdapter implements DragAndDropListener {

	private Context context;
	private Sprite sprite;
	private int dragTargetPosition;
	private Brick draggedBrick;
	private OnLongClickListener longClickListener;
	private View insertionView;
	private int currentScriptPosition;

	public BrickAdapter(Context context, Sprite sprite, DragAndDropListView listView) {
		this.context = context;
		this.sprite = sprite;
		longClickListener = listView;
		insertionView = View.inflate(context, R.layout.brick_insert, null);
	}

	public void drag(int from, int to) {

		int scriptFrom = getScriptId(from);
		int scriptTo = getScriptId(to);

		if (isBrick(to)) {

			if (draggedBrick == null) {
				if (isBrick(from)) {
					draggedBrick = (Brick) getItem(from);
				} else {
					Log.d("Warning", "BrickAdapter.drag() from was Script not Brick. should not happen!!!");
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
			if (to != 0) {
				dragTargetPosition = to;
			} else {
				dragTargetPosition = 1;
			}

			if (from != to) {
				sprite.getScript(scriptFrom).removeBrick(draggedBrick);

				sprite.getScript(scriptTo).addBrick(getScriptPosition(to, scriptTo), draggedBrick);
			}

		} else {
			if (to != 0) {
				dragTargetPosition = to;
			} else {
				dragTargetPosition = 1;
			}
			if (from < to) {
				sprite.getScript(getScriptId(to)).addBrick(0, draggedBrick);
				sprite.getScript(scriptFrom).removeBrick(draggedBrick);
				if (currentScriptPosition != 0 && from < currentScriptPosition) {
					currentScriptPosition--;
				}
			} else if (from > to && to > 0) {
				sprite.getScript(getScriptId(to) - 1).addBrick(
						sprite.getScript(getScriptId(to) - 1).getBrickList().size(), draggedBrick);
				sprite.getScript(scriptFrom).removeBrick(draggedBrick);
				if (!(currentScriptPosition < to)) {
					currentScriptPosition++;
				}
			}
		}

		notifyDataSetChanged();

	}

	public void drop(int to) {
		draggedBrick = null;
		notifyDataSetChanged();
	}

	public void remove(int index) {

		if (index < 0) {
			index = getBrickPosition();
		}

		if (index < currentScriptPosition) {
			currentScriptPosition--;
		}

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
			return sprite.getScript(count).getBrick(element - 1);
		}
	}

	public int getBrickCount(int scriptIndex) {
		return sprite.getScript(scriptIndex).getBrickList().size();
	}

	public long getItemId(int index) {
		return index;
	}

	public View getView(int position, View convertView, ViewGroup parent) {

		if (getItem(position) instanceof Brick) {
			Brick brick = (Brick) getItem(position);
			View currentBrickView = brick.getView(context, position, this);

			if (draggedBrick != null && dragTargetPosition == position) {
				return insertionView;
			}

			//Hack!!!
			//if wrapper isn't used the longClick event won't be triggered
			ViewGroup wrapper = (ViewGroup) View.inflate(context, R.layout.brick_wrapper, null);

			if (currentBrickView.getParent() != null) {
				((ViewGroup) currentBrickView.getParent()).removeView(currentBrickView);
			}

			if (draggedBrick != null && dragTargetPosition == 0) {
				return null;
			}

			wrapper.addView(currentBrickView);
			wrapper.setOnLongClickListener(longClickListener);
			return wrapper;

		} else {

			View view = null;

			if (getItem(position) instanceof BroadcastScript) {
				view = new BroadcastReceiverBrick(sprite, (BroadcastScript) getItem(position)).getView(context,
						position, this);
			} else if (getItem(position) instanceof StartScript) {
				view = new WhenStartedBrick(sprite, (Script) getItem(position)).getView(context, position, this);
			} else if (getItem(position) instanceof WhenScript) {
				view = new WhenBrick(sprite, (WhenScript) getItem(position)).getView(context, position, this);
			}

			if (position == currentScriptPosition) {
				view.setBackgroundResource(R.drawable.brick_touched_current);
			}
			return view;
		}
	}

	public int getScriptId(int index) {
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

	public int getScriptCount() {
		return sprite.getScriptCount();
	}

	public void setTouchedScript(int index) {
		if (!(index == currentScriptPosition)) {
			if (!(index < 0)) {
				if (!isBrick(index)) {
					if (draggedBrick == null) {
						ProjectManager.getInstance().setCurrentScript(sprite.getScript(getScriptId(index)));
						setCurrentScriptPosition(index);
						notifyDataSetChanged();
					}
				}
			}
		}
	}

	public void setCurrentScriptPosition(int position) {
		currentScriptPosition = position;
	}

	public int getSpriteSize() {
		return sprite.getScriptCount();
	}

	private int getBrickPosition() {
		int brickCount = 0;

		for (int count = 0; count < sprite.getScriptCount(); count++) {
			brickCount += sprite.getScript(count).getBrickList().size() + 1;
		}

		return brickCount - 1;
	}

	// Just for Testing
	public int getChildCountFromLastGroup() {
		return getBrickCount(getScriptCount() - 1);
	}

	public int getGroupCount() {
		return getScriptCount();
	}

	public Brick getChild(int groupPosition, int childPosition) {

		return (Brick) getItem(getScriptId(groupPosition) + (childPosition + 1));
	}

}

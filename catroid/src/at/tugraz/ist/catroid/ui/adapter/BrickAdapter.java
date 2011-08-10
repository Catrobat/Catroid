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

/**
 * @author DANIEL
 *
 */

import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupClickListener;
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

public class BrickAdapter extends BaseExpandableListAdapter implements DragAndDropListener, OnGroupClickListener {

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

	public Brick getChild(int groupPosition, int childPosition) {
		return sprite.getScript(groupPosition).getBrickList().get(childPosition);
	}

	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView,
			ViewGroup parent) {
		Brick brick = getChild(groupPosition, childPosition);

		if (draggedBrick != null && (dragTargetPosition == childPosition)) {
			return insertionView;
		}

		View currentBrickView = brick.getView(context, childPosition, this);

		if (animateChildren) {
			brickListAnimation.doExpandAnimation(currentBrickView, childPosition);
		}
		//Hack!!!
		//if wrapper isn't used the longClick event won't be triggered
		ViewGroup wrapper = (ViewGroup) View.inflate(context, R.layout.construction_brick_wrapper, null);
		
		if (currentBrickView.getParent() != null) {
			((ViewGroup) currentBrickView.getParent()).removeView(currentBrickView);
		}
		wrapper.addView(currentBrickView);
		wrapper.setOnLongClickListener(longClickListener);
		return wrapper;
	}

	public int getChildrenCount(int groupPosition) {
		return sprite.getScript(groupPosition).getBrickList().size();
	}

	public Script getGroup(int groupPosition) {
		return sprite.getScript(groupPosition);
	}

	public int getGroupCount() {
		return sprite.getNumberOfScripts();
	}

	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
		View view = null;
		if (getGroup(groupPosition) instanceof TapScript) {
			view = new IfTouchedBrick(sprite, getGroup(groupPosition)).getView(context, groupPosition, this);
		} else if (getGroup(groupPosition) instanceof BroadcastScript) {
			view = new BroadcastReceiverBrick(sprite, (BroadcastScript) getGroup(groupPosition)).getView(context,
					groupPosition, this);
		} else {
			view = new IfStartedBrick(sprite, getGroup(groupPosition)).getView(context, groupPosition, this);
		}

		return view;
	}

	public boolean hasStableIds() {
		return false;
	}

	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return false;
	}

	public void drag(int from, int to) {

		int childFrom = Math.max(0, from - getGroupCount());
		int childTo = Math.max(0, to - getGroupCount());
		childFrom = Math.min(childFrom, getChildCountFromLastGroup() - 1);
		childTo = Math.min(childTo, getChildCountFromLastGroup() - 1);

		if (draggedBrick == null) {
			draggedBrick = getChild(getCurrentGroup(), childFrom);
			notifyDataSetChanged();
		}

		ArrayList<Brick> brickList = getBrickList();

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

		if (childFrom != childTo) {
			Brick removedBrick = getBrickList().remove(childFrom);
			sprite.getScript(getCurrentGroup()).addBrick(childTo, removedBrick);
			notifyDataSetChanged();
		}

	}

	public void drop(int from, int to) {
		draggedBrick = null;
		notifyDataSetChanged();
	}

	public void remove(int index) {
		ArrayList<Brick> brickList = getBrickList();
		if (draggedBrick instanceof LoopBeginBrick) {
			LoopBeginBrick loopBeginBrick = (LoopBeginBrick) draggedBrick;
			brickList.remove(loopBeginBrick.getLoopEndBrick());
		} else if (draggedBrick instanceof LoopEndBrick) {
			LoopEndBrick loopEndBrick = (LoopEndBrick) draggedBrick;
			brickList.remove(loopEndBrick.getLoopBeginBrick());
		}

		brickList.remove(draggedBrick);
		draggedBrick = null;
		notifyDataSetChanged();
	}

	public boolean onGroupClick(final ExpandableListView parent, View v, final int groupPosition, long id) {
		if (groupPosition == getCurrentGroup()) {
			return false;
		}

		animateChildren = true;
		brickListAnimation.doClickOnGroupAnimate(getGroupCount(), groupPosition);
		return true;
	}

	public void doReordering(ExpandableListView parent, int groupPosition) {
		for (int i = 0; i < getGroupCount(); ++i) {
			parent.collapseGroup(i);
		}
		Script currentScript = sprite.getScript(groupPosition);
		int lastScriptIndex = sprite.getNumberOfScripts() - 1;
		Script lastScript = sprite.getScript(lastScriptIndex);
		boolean scriptDeleted = sprite.removeScript(currentScript);
		if (scriptDeleted) {
			sprite.addScript(currentScript);
			sprite.removeScript(lastScript);
			sprite.addScript(groupPosition, lastScript);
		}

		ProjectManager.getInstance().setCurrentScript(currentScript);

		notifyDataSetChanged();
		parent.expandGroup(getCurrentGroup());
	}

	public void setAnimateChildren(boolean animateChildren) {
		this.animateChildren = animateChildren;
	}

	public int getChildCountFromLastGroup() {
		return getChildrenCount(getCurrentGroup());
	}

	public OnLongClickListener getOnLongClickListener() {
		return longClickListener;
	}

	private ArrayList<Brick> getBrickList() {
		return sprite.getScript(getCurrentGroup()).getBrickList();
	}

	private int getCurrentGroup() {
		return getGroupCount() - 1;
	}
}

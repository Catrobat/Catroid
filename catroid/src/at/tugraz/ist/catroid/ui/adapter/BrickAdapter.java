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
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupClickListener;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.content.Script;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.content.TapScript;
import at.tugraz.ist.catroid.content.bricks.Brick;
import at.tugraz.ist.catroid.content.bricks.IfStartedBrick;
import at.tugraz.ist.catroid.content.bricks.IfTouchedBrick;
import at.tugraz.ist.catroid.content.bricks.PlaySoundBrick;
import at.tugraz.ist.catroid.content.bricks.SetCostumeBrick;
import at.tugraz.ist.catroid.io.StorageHandler;
import at.tugraz.ist.catroid.ui.dragndrop.DragNDropListView;
import at.tugraz.ist.catroid.ui.dragndrop.DragNDropListView.DropListener;
import at.tugraz.ist.catroid.ui.dragndrop.DragNDropListView.RemoveListener;

public class BrickAdapter extends BaseExpandableListAdapter implements DropListener, RemoveListener,
		OnGroupClickListener {

	private Context context;
	private Sprite sprite;
	private BrickListAnimation brickListAnimation;
	private boolean animateChildren;

	public BrickAdapter(Context context, Sprite sprite, DragNDropListView listView) {
		this.context = context;
		this.sprite = sprite;
		brickListAnimation = new BrickListAnimation(this, listView);
	}

	public Brick getChild(int groupPosition, int childPosition) {
		return sprite.getScriptList().get(groupPosition).getBrickList().get(childPosition);
	}

	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView,
			ViewGroup parent) {
		Brick brick = getChild(groupPosition, childPosition);

		View currentBrickView = brick.getView(context, childPosition, this);
		if (!animateChildren) {
			return currentBrickView;
		}
		brickListAnimation.doExpandAnimation(currentBrickView, childPosition);

		return currentBrickView;
	}

	public int getChildrenCount(int groupPosition) {
		return sprite.getScriptList().get(groupPosition).getBrickList().size();
	}

	public Script getGroup(int groupPosition) {
		return sprite.getScriptList().get(groupPosition);
	}

	public int getGroupCount() {
		return sprite.getScriptList().size();
	}

	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
		View view;
		if (getGroup(groupPosition) instanceof TapScript) {
			view = new IfTouchedBrick(sprite, getGroup(groupPosition)).getPrototypeView(context);
		} else {
			view = new IfStartedBrick(sprite, getGroup(groupPosition)).getPrototypeView(context);
		}
		return view;
	}

	public boolean hasStableIds() {
		return false;
	}

	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return false;
	}

	public void drop(int from, int to) {
		if (from == to) {
			return;
		}
		ArrayList<Brick> brickList = sprite.getScriptList().get(getGroupCount() - 1).getBrickList();
		Brick removedBrick = brickList.remove(from);
		brickList.add(to, removedBrick);
		notifyDataSetChanged();
	}

	public void remove(int which) {
		ArrayList<Brick> brickList = sprite.getScriptList().get(getGroupCount() - 1).getBrickList();

		if (brickList.get(which) instanceof PlaySoundBrick) {
			PlaySoundBrick toDelete = (PlaySoundBrick) brickList.get(which);
			String pathToSoundFile = toDelete.getPathToSoundFile();
			if (pathToSoundFile != null) {
				StorageHandler.getInstance().deleteFile(pathToSoundFile);
			}

		} else if (brickList.get(which) instanceof SetCostumeBrick) {
			SetCostumeBrick toDelete = (SetCostumeBrick) brickList.get(which);
			String imagePath = toDelete.getImagePath();
			if (imagePath != null) {
				StorageHandler.getInstance().deleteFile(imagePath);
			}
		}

		brickList.remove(which);
		notifyDataSetChanged();
	}

	public boolean onGroupClick(final ExpandableListView parent, View v, final int groupPosition, long id) {
		if (groupPosition == getGroupCount() - 1) {
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
		Script currentScript = sprite.getScriptList().get(groupPosition);
		int lastScriptIndex = sprite.getScriptList().size() - 1;
		Script lastScript = sprite.getScriptList().get(lastScriptIndex);
		boolean scriptDeleted = sprite.getScriptList().remove(currentScript);
		if (scriptDeleted) {
			sprite.getScriptList().add(currentScript);
			sprite.getScriptList().remove(lastScript);
			sprite.getScriptList().add(groupPosition, lastScript);
		}

		ProjectManager.getInstance().setCurrentScript(currentScript);

		notifyDataSetChanged();
		parent.expandGroup(getGroupCount() - 1);
	}

	public void setAnimateChildren(boolean animateChildren) {
		this.animateChildren = animateChildren;
	}

	public int getChildCountFromLastGroup() {
		return getChildrenCount(getGroupCount() - 1);
	}

}

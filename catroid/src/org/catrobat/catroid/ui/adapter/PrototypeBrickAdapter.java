/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2015 The Catrobat Team
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
package org.catrobat.catroid.ui.adapter;

/**
 * @author DENISE, DANIEL
 */

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.UserBrick;
import org.catrobat.catroid.ui.BrickView;
import org.catrobat.catroid.ui.CheckableLinearLayout;
import org.catrobat.catroid.ui.bricks.BrickViewProvider;

import java.util.ArrayList;
import java.util.List;


public class PrototypeBrickAdapter extends BaseAdapter implements CheckableLinearLayout.OnCheckedChangeListener {

	private final List<Brick> brickList = new ArrayList<Brick>();
	private final List<Class<? extends Brick>> viewTypes = new ArrayList<Class<? extends Brick>>();
	private OnBrickCheckedListener onBrickCheckedListener;
	private BrickViewProvider brickViewProvider;
	private boolean useSelection;

	public PrototypeBrickAdapter(Context context, List<Brick> brickList) {
		this.brickList.clear();
		this.brickList.addAll(brickList);

		for (Brick brick : brickList) {
			if (!viewTypes.contains(brick.getClass())) {
				viewTypes.add(brick.getClass());
			}
		}
		brickViewProvider = new BrickViewProvider(context);
		brickViewProvider.setPrototypeLayout(true);
	}

	public void addBrickToList(Brick brick) {
		brickList.add(brick);
		if (!viewTypes.contains(brick.getClass())) {
			viewTypes.add(brick.getClass());
		}
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return brickList.size();
	}

	@Override
	public Brick getItem(int position) {
		return brickList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public int getViewTypeCount() {
		/*Can't have a viewTypeCount < 1*/
		return viewTypes.isEmpty() ? /*display empty view in this case*/ 1 : viewTypes.size();
	}

	@Override
	public int getItemViewType(int position) {
		Brick brick = brickList.get(position);
		Class<? extends Brick> brickClass = brick.getClass();
		return viewTypes.indexOf(brickClass);
	}

	public List<Brick> getBrickList() {
		return brickList;
	}

	public void removeUserBrick(Brick brick) {
		brickList.remove(brick);
		UserBrick deleteThisBrick = (UserBrick) brick;
		ProjectManager.getInstance().getCurrentSprite().removeUserBrick(deleteThisBrick);

		notifyDataSetChanged();
	}

	public void enableSelection(boolean enableSelection) {
		useSelection = enableSelection;
	}

	@Override
	public void onCheckedChanged(View checkableView, boolean isChecked) {
		if (onBrickCheckedListener != null) {
			onBrickCheckedListener.onBrickChecked();
		}
	}

	public void setOnBrickCheckedListener(OnBrickCheckedListener listener) {
		onBrickCheckedListener = listener;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		final Brick brick = brickList.get(position);
		BrickView view = brickViewProvider.createView(brick, parent);
		if (useSelection) {
			view.addMode(BrickView.Mode.SELECTION);
			view.setOnCheckedChangeListener(this);
		} else {
			view.removeMode(BrickView.Mode.SELECTION);
		}
		return view;
	}

	public interface OnBrickCheckedListener {
		void onBrickChecked();
	}
}

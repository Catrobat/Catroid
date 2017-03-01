/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2016 The Catrobat Team
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

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.BrickViewProvider;
import org.catrobat.catroid.content.bricks.UserBrick;
import org.catrobat.catroid.ui.controller.BackPackListManager;
import org.catrobat.catroid.ui.fragment.UserBrickFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class UserBrickAdapter extends BrickBaseAdapter implements ActionModeActivityAdapterInterface {

	private BrickAdapter brickAdapter;
	private int selectMode;

	public UserBrickAdapter(UserBrickFragment userBrickFragment, BrickAdapter brickAdapter) {
		this.context = userBrickFragment.getActivity();
		this.userBrickFragment = userBrickFragment;
		this.brickAdapter = brickAdapter;
		initBrickList();
	}

	public void initBrickList() {
		brickList = new ArrayList<>();
		for (Brick brick : getPrototypeUserBricks()) {
			brickList.add(brick);
			brick.setBrickAdapter(this);
		}
	}

	public Context getContext() {
		return context;
	}

	@Override
	public int getCount() {
		return brickList.size();
	}

	@Override
	public Object getItem(int position) {
		return brickList.get(position);
	}

	@Override
	public long getItemId(int i) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup viewGroup) {
		Brick brick = brickList.get(position);
		return brick.getView(context, 0, this);
	}

	@Override
	public void setShowDetails(boolean showDetails) {
		//not supported
	}

	@Override
	public boolean getShowDetails() {
		//not supported
		return false;
	}

	@Override
	public int getSelectMode() {
		return selectMode;
	}

	@Override
	public void setSelectMode(int mode) {
		selectMode = mode;
	}

	@Override
	public int getAmountOfCheckedItems() {
		return getCheckedBricks().size();
	}

	@Override
	public Set<Integer> getCheckedItems() {
		return null;
	}

	@Override
	public void clearCheckedItems() {
		actionMode = ActionModeEnum.NO_ACTION;
		checkedBricks.clear();
		enableAllBricks();
		notifyDataSetChanged();
	}

	private List<UserBrick> getPrototypeUserBricks() {
		return ProjectManager.getInstance().getCurrentSprite().getUserBrickList();
	}

	private void enableAllBricks() {
		unCheckAllItems();
		for (Brick brick : brickList) {
			BrickViewProvider.setCheckboxVisibility(brick, View.GONE);
			BrickViewProvider.setAlphaForBrick(brick, BrickViewProvider.ALPHA_FULL);
		}
	}

	public void checkAllItems() {
		for (Brick brick : brickList) {
			setCheckbox(brick, true);
			handleCheck(brick, true);
		}
	}

	private void unCheckAllItems() {
		for (Brick brick : brickList) {
			setCheckbox(brick, false);
			handleCheck(brick, false);
		}
	}

	public void handleCheck(Brick brick, boolean checked) {
		if (checked) {
			addElementToCheckedBricks(brick);
		} else {
			checkedBricks.remove(brick);
		}

		if (userBrickFragment.getActionModeActive()) {
			userBrickFragment.updateActionModeTitle();
		}
	}

	public void onDestroyActionModeBackPack() {
		actionMode = ActionModeEnum.NO_ACTION;
		List<String> backPackedUserBrickGroups = BackPackListManager.getInstance().getBackPackedUserBrickGroups();
		showNewGroupBackPackDialog(backPackedUserBrickGroups, true);
	}

	public void deleteBrick(UserBrick brick) {
		List<UserBrick> userBricks = ProjectManager.getInstance().getCurrentSprite().getUserBrickList();
		userBricks.remove(brick);
		ProjectManager.getInstance().getCurrentSprite().removeUserBrick(brick);

		initBrickList();
		notifyDataSetChanged();
		brickAdapter.initBrickList();
		brickAdapter.notifyDataSetChanged();
	}
}

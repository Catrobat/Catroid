/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2020 The Catrobat Team
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

package org.catrobat.catroid.content.bricks;

import android.content.Context;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.TextView;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ScriptSequenceAction;
import org.catrobat.catroid.ui.BrickLayout;
import org.catrobat.catroid.ui.fragment.AddUserDataToUserDefinedBrickFragment;
import org.catrobat.catroid.ui.recyclerview.util.UniqueNameProvider;
import org.catrobat.catroid.userbrick.UserDefinedBrickData;
import org.catrobat.catroid.userbrick.UserDefinedBrickInput;
import org.catrobat.catroid.userbrick.UserDefinedBrickLabel;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

public class UserDefinedBrick extends BrickBaseType {
	private static final long serialVersionUID = 1L;

	public static final String USER_BRICK_BUNDLE_ARGUMENT = "user_brick";
	public static final String ADD_INPUT_OR_LABEL_BUNDLE_ARGUMENT = "addInputOrLabel";
	public static final boolean INPUT = true;
	public static final boolean LABEL = false;

	@XStreamAlias("userDefinedBrickDataList")
	private List<UserDefinedBrickData> userDefinedBrickDataList;

	@XStreamAlias("userDefinedBrickID")
	private UUID userDefinedBrickID;

	private transient BrickLayout userDefinedBrickLayout;
	public transient TextView currentUserDefinedDataTextView;

	public UserDefinedBrick() {
		userDefinedBrickDataList = new ArrayList<>();
		userDefinedBrickID = UUID.randomUUID();
	}

	public UserDefinedBrick(List<UserDefinedBrickData> userBrickDataList) {
		this.userDefinedBrickDataList = userBrickDataList;
		this.userDefinedBrickID = UUID.randomUUID();
	}

	public UserDefinedBrick(UserDefinedBrick userDefinedBrick) {
		copyUserDefinedDataList(userDefinedBrick);
		this.userDefinedBrickID = UUID.randomUUID();
	}

	private void copyUserDefinedDataList(UserDefinedBrick userDefinedBrick) {
		this.userDefinedBrickDataList = new ArrayList<>();
		for (UserDefinedBrickData data : userDefinedBrick.getUserDefinedBrickDataList()) {
			if (data instanceof UserDefinedBrickInput) {
				userDefinedBrickDataList.add(new UserDefinedBrickInput((UserDefinedBrickInput) data));
			} else {
				userDefinedBrickDataList.add(new UserDefinedBrickLabel((UserDefinedBrickLabel) data));
			}
		}
	}

	public UUID getUserDefinedBrickID() {
		return userDefinedBrickID;
	}

	public void addLabel(String label) {
		removeLastLabel();
		userDefinedBrickDataList.add(new UserDefinedBrickLabel(label));
	}

	public void removeLastLabel() {
		if (lastContentIsLabel()) {
			userDefinedBrickDataList.remove(userDefinedBrickDataList.size() - 1);
		}
	}

	public void addInput(String input) {
		userDefinedBrickDataList.add(new UserDefinedBrickInput(input));
	}

	public boolean isEmpty() {
		return userDefinedBrickDataList.isEmpty();
	}

	public List<UserDefinedBrickData> getUserDefinedBrickDataList() {
		return this.userDefinedBrickDataList;
	}

	public List<String> getUserDataList(boolean isInput) {
		List<String> userDataList = new ArrayList<>();
		if (isInput) {
			for (UserDefinedBrickData userDefinedBrickData : userDefinedBrickDataList) {
				if (userDefinedBrickData instanceof UserDefinedBrickInput) {
					userDataList.add(((UserDefinedBrickInput) userDefinedBrickData).getInput());
				}
			}
		} else {
			for (UserDefinedBrickData userDefinedBrickData : userDefinedBrickDataList) {
				if (userDefinedBrickData instanceof UserDefinedBrickLabel) {
					userDataList.add(((UserDefinedBrickLabel) userDefinedBrickData).getLabel());
				}
			}
		}
		return userDataList;
	}

	private boolean lastContentIsLabel() {
		if (userDefinedBrickDataList.isEmpty()) {
			return false;
		}
		return userDefinedBrickDataList.get(userDefinedBrickDataList.size() - 1) instanceof UserDefinedBrickLabel;
	}

	public boolean isUserDefinedBrickDataEqual(Brick brick) {
		if (!(brick instanceof UserDefinedBrick)) {
			return false;
		}
		UserDefinedBrick other = (UserDefinedBrick) brick;
		if (userDefinedBrickDataList.size() != other.userDefinedBrickDataList.size()) {
			return false;
		}
		for (int dataIndex = 0; dataIndex < userDefinedBrickDataList.size(); dataIndex++) {
			UserDefinedBrickData thisData = userDefinedBrickDataList.get(dataIndex);
			UserDefinedBrickData otherData = other.userDefinedBrickDataList.get(dataIndex);

			if (!thisData.getClass().equals(otherData.getClass())) {
				return false;
			}
			if (thisData instanceof UserDefinedBrickLabel && !thisData.equals(otherData)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public Brick clone() throws CloneNotSupportedException {
		UserDefinedBrick clone = (UserDefinedBrick) super.clone();
		clone.copyUserDefinedDataList(this);
		clone.userDefinedBrickID = this.getUserDefinedBrickID();
		return clone;
	}

	@Override
	public View getView(Context context) {
		super.getView(context);
		userDefinedBrickLayout = view.findViewById(R.id.brick_user_brick);
		boolean isAddInputFragment = false;
		boolean isAddLabelFragment = false;

		Fragment currentFragment =
				((FragmentActivity) context).getSupportFragmentManager().findFragmentById(R.id.fragment_container);
		if (currentFragment instanceof AddUserDataToUserDefinedBrickFragment) {
			isAddInputFragment = ((AddUserDataToUserDefinedBrickFragment) currentFragment).isAddInput();
			isAddLabelFragment = !isAddInputFragment;
		}

		for (UserDefinedBrickData userBrickData : userDefinedBrickDataList) {
			if (userBrickData instanceof UserDefinedBrickInput) {
				addTextViewForUserData(context, ((UserDefinedBrickInput) userBrickData).getInput(),
						INPUT);
			}
			if (userBrickData instanceof UserDefinedBrickLabel) {
				addTextViewForUserData(context, ((UserDefinedBrickLabel) userBrickData).getLabel(),
						LABEL);
			}
		}
		if (isAddInputFragment) {
			String defaultText =
					new UniqueNameProvider().getUniqueName(context.getResources().getString(R.string.brick_user_defined_default_input_name), getUserDataList(INPUT));
			addTextViewForUserData(context, defaultText, INPUT);
		}
		if (isAddLabelFragment && !lastContentIsLabel()) {
			String defaultText =
					new UniqueNameProvider().getUniqueName(context.getResources().getString(R.string.brick_user_defined_default_label), getUserDataList(LABEL));
			addTextViewForUserData(context, defaultText, LABEL);
		}

		return view;
	}

	private void addTextViewForUserData(Context context, String text, boolean isInput) {

		TextView userDataTextView;
		if (isInput) {
			userDataTextView = new TextView(new ContextThemeWrapper(context, R.style.BrickEditText));
		} else {
			userDataTextView = new TextView(new ContextThemeWrapper(context, R.style.BrickText));
		}
		userDataTextView.setText(text);
		currentUserDefinedDataTextView = userDataTextView;
		userDefinedBrickLayout.addView(userDataTextView);
	}

	@Override
	public int getViewResource() {
		return R.layout.brick_user_brick;
	}

	@Override
	public void addActionToSequence(Sprite sprite, ScriptSequenceAction sequence) {
	}
}

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
import android.text.method.ScrollingMovementMethod;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Nameable;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ScriptSequenceAction;
import org.catrobat.catroid.content.bricks.brickspinner.StringOption;
import org.catrobat.catroid.ui.BrickLayout;
import org.catrobat.catroid.ui.fragment.AddUserDataToUserBrickFragment;
import org.catrobat.catroid.ui.recyclerview.util.UniqueNameProvider;
import org.catrobat.catroid.userbrick.UserBrickData;
import org.catrobat.catroid.userbrick.UserBrickInput;
import org.catrobat.catroid.userbrick.UserBrickLabel;

import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

public class UserDefinedBrick extends BrickBaseType {
	public static final String USER_BRICK_BUNDLE_ARGUMENT = "user_brick";
	public static final String ADD_INPUT_OR_LABEL_BUNDLE_ARGUMENT = "addInputOrLabel";
	public static final boolean INPUT = true;
	public static final boolean LABEL = false;

	private ScrollView scrollbar;
	private List<UserBrickData> userBrickDataList;
	private BrickLayout userBrickContentLayout;
	public TextView currentUserDataEditText;

	public UserDefinedBrick() {
		userBrickDataList = new ArrayList<>();
	}

	public void addLabel(Nameable label) {
		if (lastContentIsLabel()) {
			userBrickDataList.remove(userBrickDataList.size() - 1);
		}
		userBrickDataList.add(new UserBrickLabel(label));
	}

	public void addInput(Nameable input) {
		userBrickDataList.add(new UserBrickInput(input));
	}

	public List<Nameable> getUserDataList(boolean inputOrLabel) {
		List<Nameable> userDataList = new ArrayList<>();
		if (inputOrLabel) {
			for (UserBrickData userBrickData : userBrickDataList) {
				if (userBrickData instanceof UserBrickInput) {
					userDataList.add(((UserBrickInput) userBrickData).getInput());
				}
			}
		} else {
			for (UserBrickData userBrickData : userBrickDataList) {
				if (userBrickData instanceof UserBrickLabel) {
					userDataList.add(((UserBrickLabel) userBrickData).getLabel());
				}
			}
		}
		return userDataList;
	}

	private boolean lastContentIsLabel() {
		if (userBrickDataList.isEmpty()) {
			return false;
		}
		return userBrickDataList.get(userBrickDataList.size() - 1) instanceof UserBrickLabel;
	}

	@Override
	public View getView(Context context) {
		super.getView(context);
		userBrickContentLayout = view.findViewById(R.id.brick_user_brick);
		scrollbar = view.findViewById(R.id.user_brick_scrollbar);

		boolean isAddInputFragment = currentFragmentIsAddInput(context);

		boolean isAddLabelFragment = currentFragmentIsAddLabel(context);

		if (userBrickDataList.isEmpty()) {
			if (isAddInputFragment) {
				String defaultText = new UniqueNameProvider().getUniqueNameInNameables(context.getResources().getString(R.string.brick_user_defined_default_input_name), getUserDataList(INPUT));
				addTextViewForUserData(context, new StringOption(defaultText), INPUT);
			}
			if (isAddLabelFragment) {
				String defaultText = new UniqueNameProvider().getUniqueNameInNameables(context.getResources().getString(R.string.brick_user_defined_default_label), getUserDataList(LABEL));
				addTextViewForUserData(context, new StringOption(defaultText), LABEL);
			}
		} else {
			for (UserBrickData userBrickData : userBrickDataList) {
				if (userBrickData instanceof UserBrickInput) {
					addTextViewForUserData(context, ((UserBrickInput) userBrickData).getInput(),
							INPUT);
				}
				if (userBrickData instanceof UserBrickLabel) {
					addTextViewForUserData(context, ((UserBrickLabel) userBrickData).getLabel(),
							LABEL);
				}
			}
			if (isAddInputFragment) {
				String defaultText = new UniqueNameProvider().getUniqueNameInNameables(context.getResources().getString(R.string.brick_user_defined_default_input_name), getUserDataList(INPUT));
				addTextViewForUserData(context, new StringOption(defaultText), INPUT);
			}
			if (isAddLabelFragment && !lastContentIsLabel()) {
				String defaultText = new UniqueNameProvider().getUniqueNameInNameables(context.getResources().getString(R.string.brick_user_defined_default_label), getUserDataList(LABEL));
				addTextViewForUserData(context, new StringOption(defaultText), LABEL);
			}
		}

		scrollbar.post(this::scrollToBottom);

		return view;
	}

	private void addTextViewForUserData(Context context, Nameable text, boolean isInputOrLabel) {

		TextView userDataTextView;
		if (isInputOrLabel) {
			userDataTextView = new TextView(new ContextThemeWrapper(context, R.style.MultilineBrickEditText));
		} else {
			userDataTextView = new TextView(new ContextThemeWrapper(context, R.style.MultilineBrickText));
		}
		userDataTextView.setMovementMethod(new ScrollingMovementMethod());
		userDataTextView.setText(text.getName());
		currentUserDataEditText = userDataTextView;
		userBrickContentLayout.addView(userDataTextView);
	}

	public void scrollToBottom() {
		scrollbar.scrollTo(0,
				scrollbar.getChildAt(scrollbar.getChildCount() - 1).getBottom());
	}

	private boolean currentFragmentIsAddInput(Context context) {
		Fragment currentFragment =
				((FragmentActivity) context).getSupportFragmentManager().findFragmentById(R.id.fragment_container);
		if (currentFragment instanceof AddUserDataToUserBrickFragment) {
			AddUserDataToUserBrickFragment fragment = (AddUserDataToUserBrickFragment) currentFragment;
			return fragment.getIsAddInputOrLabel() == INPUT;
		}
		return false;
	}

	private boolean currentFragmentIsAddLabel(Context context) {
		Fragment currentFragment =
				((FragmentActivity) context).getSupportFragmentManager().findFragmentById(R.id.fragment_container);
		if (currentFragment instanceof AddUserDataToUserBrickFragment) {
			AddUserDataToUserBrickFragment fragment = (AddUserDataToUserBrickFragment) currentFragment;
			return fragment.getIsAddInputOrLabel() == LABEL;
		}
		return false;
	}

	@Override
	public int getViewResource() {
		return R.layout.brick_user_brick;
	}

	@Override
	public void addActionToSequence(Sprite sprite, ScriptSequenceAction sequence) {
	}
}

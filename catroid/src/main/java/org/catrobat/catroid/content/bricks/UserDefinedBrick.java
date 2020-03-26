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
import org.catrobat.catroid.ui.fragment.AddInputToUserBrickFragment;
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

	private ScrollView scrollbar;
	private List<UserBrickData> userBrickDataList;
	private BrickLayout userBrickContentLayout;
	public TextView currentInputEditText;

	public UserDefinedBrick() {
		userBrickDataList = new ArrayList<>();
	}

	public void addLabel(String label) {
		userBrickDataList.add(new UserBrickLabel(label));
	}

	public void addInput(Nameable input) {
		if (lastContentIsInput()) {
			userBrickDataList.remove(userBrickDataList.size() - 1);
		}
		userBrickDataList.add(new UserBrickInput(input));
	}

	public List<Nameable> getInputList() {
		List<Nameable> inputList = new ArrayList<>();
		for (UserBrickData userBrickData : userBrickDataList) {
			if (userBrickData instanceof UserBrickInput) {
				inputList.add(((UserBrickInput) userBrickData).getInput());
			}
		}
		return inputList;
	}

	private boolean lastContentIsInput() {
		if (userBrickDataList.isEmpty()) {
			return false;
		}
		return userBrickDataList.get(userBrickDataList.size() - 1) instanceof UserBrickInput;
	}

	@Override
	public View getView(Context context) {
		super.getView(context);
		userBrickContentLayout = view.findViewById(R.id.brick_user_brick);
		scrollbar = view.findViewById(R.id.user_brick_scrollbar);

		boolean isAddInputFragment = currentFragmentIsAddInput(context);

		if (userBrickDataList.isEmpty()) {
			if (isAddInputFragment) {
				String defaultText =
						new UniqueNameProvider().getUniqueNameInNameables(context.getResources().getString(R.string.brick_user_defined_default_input_name),
								getInputList());
				addTextViewForInput(context, new StringOption(defaultText));
			}
		} else {
			for (UserBrickData userBrickData : userBrickDataList) {
				if (userBrickData instanceof UserBrickInput) {
					addTextViewForInput(context, ((UserBrickInput) userBrickData).getInput());
				} else {
					addTextViewForLabel(context, ((UserBrickLabel) userBrickData).getLabel());
				}
			}
			if (isAddInputFragment && !lastContentIsInput()) {
				String defaultText =
						new UniqueNameProvider().getUniqueNameInNameables(context.getResources().getString(R.string.brick_user_defined_default_input_name),
								getInputList());
				addTextViewForInput(context, new StringOption(defaultText));
			}
		}

		scrollbar.post(this::scrollToBottom);

		return view;
	}

	private void addTextViewForInput(Context context, Nameable text) {
		TextView inputTextView = new TextView(new ContextThemeWrapper(context,
				R.style.MultilineBrickEditText));
		inputTextView.setMovementMethod(new ScrollingMovementMethod());
		inputTextView.setText(text.getName());
		currentInputEditText = inputTextView;
		userBrickContentLayout.addView(inputTextView);
	}

	private void addTextViewForLabel(Context context, String text) {
		TextView labelTextView = new TextView(new ContextThemeWrapper(context,
				R.style.BrickText));
		labelTextView.setText(text);
		userBrickContentLayout.addView(labelTextView);
	}

	public void scrollToBottom() {
		scrollbar.scrollTo(0,
				scrollbar.getChildAt(scrollbar.getChildCount() - 1).getBottom());
	}

	private boolean currentFragmentIsAddInput(Context context) {
		Fragment currentFragment =
				((FragmentActivity) context).getSupportFragmentManager().findFragmentById(R.id.fragment_container);
		return currentFragment instanceof AddInputToUserBrickFragment;
	}

	@Override
	public int getViewResource() {
		return R.layout.brick_user_brick;
	}

	@Override
	public void addActionToSequence(Sprite sprite, ScriptSequenceAction sequence) {
	}
}

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
import android.view.View;
import android.widget.LinearLayout;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.BrickValues;
import org.catrobat.catroid.common.Nameable;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.UserDefinedScript;
import org.catrobat.catroid.content.actions.ScriptSequenceAction;
import org.catrobat.catroid.content.bricks.brickspinner.BrickSpinner;
import org.catrobat.catroid.content.bricks.brickspinner.StringOption;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;

public class UserDefinedReceiverBrick extends ScriptBrickBaseType implements BrickSpinner.OnItemSelectedListener<StringOption> {

	private static final long serialVersionUID = 1L;

	private UserDefinedScript userDefinedScript;
	private LinearLayout userBrickSpace;
	private Brick userDefinedBrick;

	public int spinnerSelection;

	public UserDefinedReceiverBrick(UserDefinedScript userDefinedScript) {
		this.spinnerSelection = userDefinedScript.getScreenRefresh() ? BrickValues.USER_DEFINED_BRICK_WITH_SCREEN_REFRESH
				: BrickValues.USER_DEFINED_BRICK_WITHOUT_SCREEN_REFRESH;
		this.userDefinedScript = userDefinedScript;
	}

	public UserDefinedReceiverBrick(UserDefinedBrick userDefinedBrick) {
		userDefinedScript = new UserDefinedScript(userDefinedBrick.getUserDefinedBrickID());
		userDefinedScript.setScriptBrick(this);
		this.userDefinedBrick = userDefinedBrick;
	}

	@VisibleForTesting
	public UserDefinedReceiverBrick() {
		this.userDefinedScript = new UserDefinedScript();
	}

	public UserDefinedBrick getUserDefinedBrick() {
		return (UserDefinedBrick) userDefinedBrick;
	}

	@Override
	public Script getScript() {
		return userDefinedScript;
	}

	@Override
	public Brick clone() throws CloneNotSupportedException {
		UserDefinedReceiverBrick clone = (UserDefinedReceiverBrick) super.clone();
		clone.userDefinedScript = (UserDefinedScript) userDefinedScript.clone();
		clone.userDefinedScript.setScriptBrick(clone);
		if (this.userDefinedBrick != null) {
			clone.userDefinedBrick = this.userDefinedBrick.clone();
		}
		return clone;
	}

	@Override
	public View getView(Context context) {
		super.getView(context);
		userBrickSpace = view.findViewById(R.id.user_brick_space);
		if (userDefinedBrick == null) {
			Sprite currentSprite = ProjectManager.getInstance().getCurrentSprite();
			userDefinedBrick = currentSprite.getUserDefinedBrickByID(userDefinedScript.getUserDefinedBrickID());
		}
		if (userDefinedBrick != null) {
			userBrickSpace.addView(userDefinedBrick.getView(context));
		}
		setUpSpinner(context);
		return view;
	}

	private void setUpSpinner(Context context) {
		List<Nameable> items = new ArrayList<>();
		items.add(new StringOption(context.getString(R.string.brick_user_defined_with_screen_refreshing)));
		items.add(new StringOption(context.getString(R.string.brick_user_defined_without_screen_refreshing)));

		BrickSpinner<StringOption> spinner =
				new BrickSpinner<>(R.id.brick_set_screen_refresh_spinner, view, R.layout.userdefined_brick_spinner_item, items);
		spinner.setSpinnerFontColor(context, R.color.dark_blue);
		spinner.setOnItemSelectedListener(this);

		if (spinnerSelection == BrickValues.USER_DEFINED_BRICK_WITH_SCREEN_REFRESH) {
			spinner.setSelection(0);
		}

		if (spinnerSelection == BrickValues.USER_DEFINED_BRICK_WITHOUT_SCREEN_REFRESH) {
			spinner.setSelection(1);
		}
	}

	@Override
	public int getViewResource() {
		return R.layout.brick_user_defined_script;
	}

	@Override
	public void addActionToSequence(Sprite sprite, ScriptSequenceAction sequence) {
	}

	@Override
	public void onNewOptionSelected(Integer spinnerId) {
	}

	@Override
	public void onEditOptionSelected(Integer spinnerId) {
	}

	@Override
	public void onStringOptionSelected(Integer spinnerId, String string) {
		Context context = view.getContext();

		if (string.equals(context.getString(R.string.brick_user_defined_with_screen_refreshing))) {
			spinnerSelection = BrickValues.USER_DEFINED_BRICK_WITH_SCREEN_REFRESH;
		}
		if (string.equals(context.getString(R.string.brick_user_defined_without_screen_refreshing))) {
			spinnerSelection = BrickValues.USER_DEFINED_BRICK_WITHOUT_SCREEN_REFRESH;
		}
		userDefinedScript.setScreenRefresh(spinnerSelection == BrickValues.USER_DEFINED_BRICK_WITH_SCREEN_REFRESH);
	}

	@Override
	public void onItemSelected(Integer spinnerId, @Nullable StringOption item) {
	}
}

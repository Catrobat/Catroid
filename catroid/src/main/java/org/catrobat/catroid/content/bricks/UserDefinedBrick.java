/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2021 The Catrobat Team
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

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.thoughtworks.xstream.annotations.XStreamAlias;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ScriptSequenceAction;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.ui.BrickLayout;
import org.catrobat.catroid.ui.fragment.AddUserDataToUserDefinedBrickFragment;
import org.catrobat.catroid.ui.fragment.FormulaEditorFragment;
import org.catrobat.catroid.ui.recyclerview.util.UniqueNameProvider;
import org.catrobat.catroid.userbrick.InputFormulaField;
import org.catrobat.catroid.userbrick.UserDefinedBrickData;
import org.catrobat.catroid.userbrick.UserDefinedBrickData.UserDefinedBrickDataType;
import org.catrobat.catroid.userbrick.UserDefinedBrickInput;
import org.catrobat.catroid.userbrick.UserDefinedBrickLabel;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import androidx.annotation.VisibleForTesting;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import static org.catrobat.catroid.userbrick.UserDefinedBrickData.UserDefinedBrickDataType.INPUT;

public class UserDefinedBrick extends FormulaBrick {
	private static final long serialVersionUID = 1L;

	public static final String USER_BRICK_BUNDLE_ARGUMENT = "user_brick";
	public static final String ADD_INPUT_OR_LABEL_BUNDLE_ARGUMENT = "addInputOrLabel";

	@XStreamAlias("userDefinedBrickDataList")
	private List<UserDefinedBrickData> userDefinedBrickDataList;

	@XStreamAlias("userDefinedBrickID")
	private UUID userDefinedBrickID;

	@XStreamAlias("callingBrick")
	private boolean isCallingBrick;

	private transient BrickLayout userDefinedBrickLayout;
	public transient TextView currentUserDefinedDataTextView;
	public transient BiMap<FormulaField, TextView> formulaFieldToTextViewMap = HashBiMap.create(2);
	public transient int horizontalSpacing;

	public UserDefinedBrick() {
		userDefinedBrickDataList = new ArrayList<>();
		userDefinedBrickID = UUID.randomUUID();
	}

	public UserDefinedBrick(UserDefinedBrick userDefinedBrick) {
		copyUserDefinedDataList(userDefinedBrick);
		this.userDefinedBrickID = userDefinedBrick.getUserDefinedBrickID();
		this.isCallingBrick = userDefinedBrick.isCallingBrick;
	}

	@VisibleForTesting
	public UserDefinedBrick(List<UserDefinedBrickData> userBrickDataList) {
		this.userDefinedBrickDataList = userBrickDataList;
	}

	@Override
	public Brick clone() throws CloneNotSupportedException {
		UserDefinedBrick clone = (UserDefinedBrick) super.clone();
		clone.userDefinedBrickID = this.getUserDefinedBrickID();
		clone.isCallingBrick = this.isCallingBrick;
		return clone;
	}

	private void copyUserDefinedDataList(UserDefinedBrick userDefinedBrick) {
		this.userDefinedBrickDataList = new ArrayList<>();
		for (UserDefinedBrickData data : userDefinedBrick.getUserDefinedBrickDataList()) {
			if (data instanceof UserDefinedBrickInput) {
				UserDefinedBrickInput oldInput = (UserDefinedBrickInput) data;
				UserDefinedBrickInput newInput = new UserDefinedBrickInput(oldInput);
				userDefinedBrickDataList.add(newInput);
				if (userDefinedBrick.isCallingBrick) {
					copyFormulaOfInput(oldInput, newInput);
				}
			} else {
				userDefinedBrickDataList.add(new UserDefinedBrickLabel((UserDefinedBrickLabel) data));
			}
		}
	}

	private void copyFormulaOfInput(UserDefinedBrickInput oldInput, UserDefinedBrickInput newInput) {
		FormulaField formulaField = oldInput.getInputFormulaField();
		Formula formula = formulaMap.get(formulaField);
		formulaMap.remove(formulaField);
		formulaMap.putIfAbsent(newInput.getInputFormulaField(), formula);
	}

	public void setCallingBrick(boolean callingBrick) {
		isCallingBrick = callingBrick;
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

	public List<UserDefinedBrickInput> getUserDefinedBrickInputs() {
		List<UserDefinedBrickInput> userDefinedBrickInputs = new ArrayList<>();
		for (UserDefinedBrickData userDefinedBrickData : userDefinedBrickDataList) {
			if (userDefinedBrickData.isInput()) {
				userDefinedBrickInputs.add((UserDefinedBrickInput) userDefinedBrickData);
			}
		}
		return userDefinedBrickInputs;
	}

	public boolean containsInputs() {
		for (UserDefinedBrickData userDefinedBrickData : userDefinedBrickDataList) {
			if (userDefinedBrickData.isInput()) {
				return true;
			}
		}
		return false;
	}

	public List<String> getUserDataListAsStrings(UserDefinedBrickDataType dataType) {
		List<String> userDataList = new ArrayList<>();
		for (UserDefinedBrickData userDefinedBrickData : userDefinedBrickDataList) {
			if (userDefinedBrickData.getType() == dataType) {
				userDataList.add(userDefinedBrickData.getName());
			}
		}
		return userDataList;
	}

	private boolean lastContentIsLabel() {
		if (userDefinedBrickDataList.isEmpty()) {
			return false;
		}
		return userDefinedBrickDataList.get(userDefinedBrickDataList.size() - 1).isLabel();
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
	public View getView(Context context) {
		super.getView(context);
		userDefinedBrickLayout = view.findViewById(R.id.brick_user_brick);
		horizontalSpacing =
				context.getResources().getDimensionPixelOffset(R.dimen.material_design_spacing_small);

		TextView textView = null;
		for (UserDefinedBrickData userDefinedBrickData : userDefinedBrickDataList) {
			if (userDefinedBrickData.isInput()) {
				textView = new TextView(context, null, 0, R.style.BrickEditText);
			} else {
				textView = new TextView(new ContextThemeWrapper(context, R.style.BrickText));
			}
			textView.setText(userDefinedBrickData.getName());
			if (userDefinedBrickData.isInput() && isCallingBrick) {
				InputFormulaField formulaField = ((UserDefinedBrickInput) userDefinedBrickData).getInputFormulaField();
				addAllowedBrickField(formulaField, textView);
				textView.setText(getFormulaWithBrickField(formulaField).getTrimmedFormulaString(context));
			}
			userDefinedBrickLayout.addView(textView);
			if (userDefinedBrickData.isInput()) {
				((BrickLayout.LayoutParams) textView.getLayoutParams()).setEditText(true);
			}
			((BrickLayout.LayoutParams) textView.getLayoutParams()).setHorizontalSpacing(horizontalSpacing);
		}

		Fragment currentFragment = ((FragmentActivity) context).getSupportFragmentManager().findFragmentById(R.id.fragment_container);
		if (currentFragment instanceof AddUserDataToUserDefinedBrickFragment) {
			UserDefinedBrickDataType dataTypeToAdd = ((AddUserDataToUserDefinedBrickFragment) currentFragment).getDataTypeToAdd();
			addTextViewForUserData(context, textView, dataTypeToAdd);
		}

		return view;
	}

	private void addTextViewForUserData(Context context, TextView textView, UserDefinedBrickDataType dataType) {
		String text = getDefaultText(context, dataType);
		if (dataType == INPUT) {
			textView = new TextView(context, null, 0, R.style.BrickEditText);
			userDefinedBrickLayout.addView(textView);
			((BrickLayout.LayoutParams) textView.getLayoutParams()).setEditText(true);
		} else {
			if (lastContentIsLabel()) {
				text = userDefinedBrickDataList.get(userDefinedBrickDataList.size() - 1).getName();
			} else {
				textView = new TextView(new ContextThemeWrapper(context, R.style.BrickText));
				userDefinedBrickLayout.addView(textView);
			}
		}
		((BrickLayout.LayoutParams) textView.getLayoutParams()).setHorizontalSpacing(horizontalSpacing);
		textView.setText(text);
		currentUserDefinedDataTextView = textView;
	}

	private String getDefaultText(Context context, UserDefinedBrickDataType dataTypeToAdd) {

		UniqueNameProvider uniqueNameProvider = new UniqueNameProvider();

		String defaultText;
		if (dataTypeToAdd == INPUT) {
			defaultText = context.getResources().getString(R.string.brick_user_defined_default_input_name);
		} else {
			defaultText = context.getResources().getString(R.string.brick_user_defined_default_label);
		}
		defaultText = uniqueNameProvider.getUniqueName(defaultText, getUserDataListAsStrings(dataTypeToAdd));
		return defaultText;
	}

	protected void addAllowedBrickField(FormulaField formulaField, TextView textView) {
		formulaMap.putIfAbsent(formulaField, new Formula(0));
		formulaFieldToTextViewMap.put(formulaField, textView);
	}

	private void updateUserDefinedBrickDataValues() {
		for (UserDefinedBrickData userDefinedBrickData : userDefinedBrickDataList) {
			if (userDefinedBrickData.isInput()) {
				UserDefinedBrickInput input = (UserDefinedBrickInput) userDefinedBrickData;
				input.setValue(getFormulaWithBrickField(input.getInputFormulaField()));
			}
		}
	}

	@Override
	public boolean hasEditableFormulaField() {
		return containsInputs();
	}

	@Override
	public void setClickListeners() {
		for (BiMap.Entry<FormulaField, TextView> entry : formulaFieldToTextViewMap.entrySet()) {
			TextView brickFieldView = entry.getValue();
			brickFieldView.setOnClickListener(this);
		}
	}

	@Override
	public void showFormulaEditorToEditFormula(View view) {
		if (formulaFieldToTextViewMap.inverse().containsKey(view)) {
			FormulaEditorFragment.showFragment(view.getContext(), this, getBrickFieldFromTextView((TextView) view));
		} else {
			FormulaEditorFragment.showFragment(view.getContext(), this, getDefaultBrickField());
		}
	}

	@Override
	public int getViewResource() {
		return R.layout.brick_user_brick;
	}

	public Brick.FormulaField getBrickFieldFromTextView(TextView view) {
		return formulaFieldToTextViewMap.inverse().get(view);
	}

	@Override
	public TextView getTextView(FormulaField formulaField) {
		return formulaFieldToTextViewMap.get(formulaField);
	}

	@Override
	public void addActionToSequence(Sprite sprite, ScriptSequenceAction sequence) {
		updateUserDefinedBrickDataValues();
		sequence.addAction(sprite.getActionFactory().createUserBrickAction(sprite, sequence,
				getUserDefinedBrickInputs(), userDefinedBrickID));
	}
}

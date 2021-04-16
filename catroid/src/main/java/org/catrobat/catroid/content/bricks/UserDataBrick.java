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

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.RadioButton;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Nameable;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.bricks.brickspinner.BrickSpinner;
import org.catrobat.catroid.content.bricks.brickspinner.NewOption;
import org.catrobat.catroid.formulaeditor.UserData;
import org.catrobat.catroid.formulaeditor.UserList;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.ui.UiUtils;
import org.catrobat.catroid.ui.recyclerview.dialog.TextInputDialog;
import org.catrobat.catroid.ui.recyclerview.dialog.textwatcher.DuplicateInputTextWatcher;
import org.catrobat.catroid.ui.recyclerview.fragment.ScriptFragment;
import org.catrobat.catroid.ui.recyclerview.util.UniqueNameProvider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public abstract class UserDataBrick extends FormulaBrick implements BrickSpinner.OnItemSelectedListener<UserData> {

	public transient BiMap<BrickData, Integer> brickDataToTextViewIdMap = HashBiMap.create(2);

	protected UserDataHashMap userDataList = new UserDataHashMap();

	private transient HashMap<BrickData, BrickSpinner<UserData>> spinnerMap = new HashMap<>();

	@Override
	public Brick clone() throws CloneNotSupportedException {
		UserDataBrick clone = (UserDataBrick) super.clone();
		clone.userDataList = userDataList.clone();
		clone.spinnerMap = new HashMap<>();
		return clone;
	}

	public UserList getUserListWithBrickData(BrickData brickData) {
		if (userDataList.containsKey(brickData)) {
			UserData result = userDataList.get(brickData);
			if (result instanceof UserList) {
				return (UserList) result;
			} else {
				return null;
			}
		} else {
			throw new IllegalArgumentException("Incompatible Brick data: " + this.getClass().getSimpleName()
					+ " does not have BrickField." + brickData.toString());
		}
	}

	public UserVariable getUserVariableWithBrickData(BrickData brickData) {
		if (userDataList.containsKey(brickData)) {
			UserData result = userDataList.get(brickData);
			if (result instanceof UserVariable) {
				return (UserVariable) result;
			} else {
				return null;
			}
		} else {
			throw new IllegalArgumentException("Incompatible Brick data: " + this.getClass().getSimpleName()
					+ " does not have BrickField." + brickData.toString());
		}
	}

	public HashMap<BrickData, UserData> getUserDataMap() {
		return userDataList;
	}

	protected void addAllowedBrickData(BrickData brickData, int textViewResourceId) {
		if (!userDataList.containsKey(brickData)) {
			userDataList.put(brickData, null);
		}
		brickDataToTextViewIdMap.put(brickData, textViewResourceId);
	}

	public Brick.BrickData getBrickDataFromTextViewId(int textViewId) {
		return brickDataToTextViewIdMap.inverse().get(textViewId);
	}

	@Override
	public View getView(Context context) {
		super.getView(context);

		Sprite sprite = ProjectManager.getInstance().getCurrentSprite();

		List<Nameable> lists = new ArrayList<>();
		lists.add(new NewOption(context.getString(R.string.new_option)));
		lists.addAll(sprite.getUserLists());
		lists.addAll(ProjectManager.getInstance().getCurrentProject().getUserLists());

		List<Nameable> variables = new ArrayList<>();
		variables.add(new NewOption(context.getString(R.string.new_option)));
		variables.addAll(sprite.getUserVariables());
		variables.addAll(ProjectManager.getInstance().getCurrentProject().getUserVariables());

		for (Map.Entry<BrickData, UserData> entry : userDataList.entrySet()) {
			Integer spinnerid = brickDataToTextViewIdMap.get(entry.getKey());
			BrickSpinner<UserData> spinner;

			if (Brick.BrickData.isUserList(entry.getKey())) {
				spinner = new BrickSpinner<>(spinnerid, view, lists);
			} else {
				spinner = new BrickSpinner<>(spinnerid, view, variables);
			}

			spinner.setOnItemSelectedListener(this);
			spinner.setSelection(entry.getValue());
			spinnerMap.put(entry.getKey(), spinner);
		}

		return view;
	}

	@Override
	public void onNewOptionSelected(Integer spinnerId) {
		final AppCompatActivity activity = UiUtils.getActivityFromView(view);
		if (activity == null) {
			return;
		}

		final Project currentProject = ProjectManager.getInstance().getCurrentProject();
		final Sprite currentSprite = ProjectManager.getInstance().getCurrentSprite();
		BrickData brickData = getBrickDataFromTextViewId(spinnerId);
		int placeholder;
		int title;
		if (Brick.BrickData.isUserList(getBrickDataFromTextViewId(spinnerId))) {
			placeholder = R.string.default_list_name;
			title = R.string.formula_editor_list_dialog_title;
		} else {
			placeholder = R.string.default_variable_name;
			title = R.string.formula_editor_variable_dialog_title;
		}
		TextInputDialog.Builder builder = new TextInputDialog.Builder(activity);
		UniqueNameProvider uniqueNameProvider = builder.createUniqueNameProvider(placeholder);
		builder.setHint(activity.getString(R.string.data_label))
				.setTextWatcher(new DuplicateInputTextWatcher<>(spinnerMap.get(brickData).getItems()))
				.setText(uniqueNameProvider.getUniqueName(activity.getString(placeholder), null))
				.setPositiveButton(activity.getString(R.string.ok), new TextInputDialog.OnClickListener() {
					@Override
					public void onPositiveButtonClick(DialogInterface dialog, String textInput) {

						RadioButton addToProjectListsRadioButton = ((Dialog) dialog).findViewById(R.id.global);
						boolean addToProjectData = addToProjectListsRadioButton.isChecked();
						boolean isUserList = BrickData.isUserList(brickData);
						UserData userData;
						if (isUserList) {
							userData = new UserList(textInput);
							if (addToProjectData) {
								currentProject.addUserList((UserList) userData);
							} else {
								currentSprite.addUserList((UserList) userData);
							}
						} else {
							userData = new UserVariable(textInput);
							if (addToProjectData) {
								currentProject.addUserVariable((UserVariable) userData);
							} else {
								currentSprite.addUserVariable((UserVariable) userData);
							}
						}

						for (Map.Entry<BrickData, BrickSpinner<UserData>> entry
								: spinnerMap.entrySet()) {
							if (BrickData.isUserList(entry.getKey()) == isUserList) {
								entry.getValue().add(userData);
							}
						}

						spinnerMap.get(brickData).setSelection(userData);

						ScriptFragment parentFragment = (ScriptFragment) activity
								.getSupportFragmentManager().findFragmentByTag(ScriptFragment.TAG);
						if (parentFragment != null) {
							parentFragment.notifyDataSetChanged();
						}
					}
				});

		builder.setTitle(title)
				.setView(R.layout.dialog_new_user_data)
				.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						spinnerMap.get(brickData).setSelection(userDataList.get(brickData));
					}
				})
				.setOnCancelListener(new DialogInterface.OnCancelListener() {
					@Override
					public void onCancel(DialogInterface dialog) {
						spinnerMap.get(brickData).setSelection(userDataList.get(brickData));
					}
				})
				.show();
	}

	@Override
	public void onEditOptionSelected(Integer spinnerId) {
	}

	@Override
	public void onStringOptionSelected(Integer spinnerId, String string) {
	}

	@Override
	public void onItemSelected(Integer spinnerId, @Nullable UserData item) {
		userDataList.put(getBrickDataFromTextViewId(spinnerId), item);
	}
}

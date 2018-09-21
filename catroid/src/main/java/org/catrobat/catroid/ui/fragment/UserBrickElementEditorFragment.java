/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2018 The Catrobat Team
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
package org.catrobat.catroid.ui.fragment;

import android.app.ActionBar;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.bricks.UserBrick;
import org.catrobat.catroid.content.bricks.UserScriptDefinitionBrick;
import org.catrobat.catroid.content.bricks.UserScriptDefinitionBrickElement;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.formulaeditor.datacontainer.DataContainer;
import org.catrobat.catroid.ui.BottomBar;
import org.catrobat.catroid.ui.BrickLayout;
import org.catrobat.catroid.ui.DragAndDropBrickLayoutListener;
import org.catrobat.catroid.ui.DragNDropBrickLayout;
import org.catrobat.catroid.ui.LineBreakListener;
import org.catrobat.catroid.ui.UiUtils;
import org.catrobat.catroid.ui.dialogs.UserBrickEditElementDialog;

import java.util.ArrayList;
import java.util.List;

public class UserBrickElementEditorFragment extends Fragment implements
		OnKeyListener,
		DragAndDropBrickLayoutListener,
		UserBrickEditElementDialog.DialogListener,
		LineBreakListener {

	public static final String BRICK_DATA_EDITOR_FRAGMENT_TAG = "brick_data_editor_fragment";
	private static final String BRICK_BUNDLE_ARGUMENT = "current_brick";
	private Context context;
	private UserScriptDefinitionBrick currentBrick;
	private int indexOfCurrentlyEditedElement;
	private LinearLayout editorBrickSpace;
	private View brickView;
	private View fragmentView;

	public UserBrickElementEditorFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		currentBrick = (UserScriptDefinitionBrick) getArguments().getSerializable(BRICK_BUNDLE_ARGUMENT);
	}

	public static void showFragment(View view, UserScriptDefinitionBrick brick) {
		AppCompatActivity activity = UiUtils.getActivityFromView(view);
		if (activity == null) {
			return;
		}

		FragmentManager fragmentManager = activity.getSupportFragmentManager();

		UserBrickElementEditorFragment dataEditorFragment =
				(UserBrickElementEditorFragment) fragmentManager.findFragmentByTag(BRICK_DATA_EDITOR_FRAGMENT_TAG);

		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

		fragmentTransaction.addToBackStack(null);

		if (dataEditorFragment == null) {
			dataEditorFragment = new UserBrickElementEditorFragment();
			Bundle bundle = new Bundle();
			bundle.putSerializable(BRICK_BUNDLE_ARGUMENT, brick);
			dataEditorFragment.setArguments(bundle);

			fragmentTransaction.add(R.id.fragment_container, dataEditorFragment, BRICK_DATA_EDITOR_FRAGMENT_TAG);
			fragmentTransaction.hide(fragmentManager.findFragmentByTag(ScriptFragment.TAG));
			fragmentTransaction.show(dataEditorFragment);
			BottomBar.hideBottomBar(activity);
		} else if (dataEditorFragment.isHidden()) {
			dataEditorFragment.updateBrickView();
			fragmentTransaction.hide(fragmentManager.findFragmentByTag(ScriptFragment.TAG));
			fragmentTransaction.show(dataEditorFragment);
			BottomBar.hideBottomBar(activity);
		}
		fragmentTransaction.commit();
	}

	private void onUserDismiss() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		fragmentView = inflater.inflate(R.layout.fragment_brick_data_editor, container, false);
		fragmentView.setFocusableInTouchMode(true);
		fragmentView.requestFocus();

		context = getActivity();
		brickView = View.inflate(context, R.layout.brick_user_editable, null);

		updateBrickView();

		editorBrickSpace = fragmentView.findViewById(R.id.brick_data_editor_brick_space);

		editorBrickSpace.addView(brickView);

		ListView buttonList = fragmentView.findViewById(R.id.button_list);

		buttonList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Resources resources = getResources();

				String[] actions = resources.getStringArray(R.array.data_editor_buttons);

				String action = actions[position];

				if (action.equals(resources.getString(R.string.add_text))) {
					addTextDialog();
				}
				if (action.equals(resources.getString(R.string.add_variable))) {
					addVariableDialog();
				}
				if (action.equals(resources.getString(R.string.add_line_break))) {
					addLineBreak();
				}
				if (action.equals(resources.getString(R.string.close))) {
					onUserDismiss();
				}
			}
		});

		return fragmentView;
	}

	public void addTextDialog() {
		int indexOfNewText = currentBrick.addUIText("");
		editElementDialog("", false, R.string.add_text, R.string.text_hint);
		indexOfCurrentlyEditedElement = indexOfNewText;
		updateBrickView();
	}

	public void addLineBreak() {
		currentBrick.addUILineBreak();
		updateBrickView();
	}

	public void addVariableDialog() {
		String variableName = getString(R.string.new_user_brick_variable);
		int indexOfNewVariableText = currentBrick.addUILocalizedVariable(variableName);
		editElementDialog(variableName, false, R.string.add_variable, R.string.variable_hint);
		indexOfCurrentlyEditedElement = indexOfNewVariableText;
		updateBrickView();
	}

	public void editElementDialog(CharSequence text, boolean editMode, int title, int defaultText) {
		DataContainer dataContainer = ProjectManager.getInstance().getCurrentlyEditedScene().getDataContainer();
		Sprite currentSprite = ProjectManager.getInstance().getCurrentSprite();
		UserBrick currentUserBrick = ProjectManager.getInstance().getCurrentUserBrick();
		List<UserVariable> spriteVariables = dataContainer.getSpriteUserVariables(currentSprite);
		List<UserVariable> globalVariables = dataContainer.getProjectUserVariables();
		List<UserVariable> userBrickVariables = dataContainer.getUserBrickUserVariables(currentUserBrick);

		ArrayList<String> takenVariables = new ArrayList<>();
		for (UserVariable variable : userBrickVariables) {
			takenVariables.add(variable.getName());
		}
		for (UserVariable variable : spriteVariables) {
			takenVariables.add(variable.getName());
		}
		for (UserVariable variable : globalVariables) {
			takenVariables.add(variable.getName());
		}

		UserBrickEditElementDialog dialog = new UserBrickEditElementDialog();
		dialog.addDialogListener(this);
		dialog.show(getFragmentManager(),
				UserBrickEditElementDialog.DIALOG_FRAGMENT_TAG);

		UserBrickEditElementDialog.setTakenVariables(takenVariables);
		UserBrickEditElementDialog.setTitle(title);
		UserBrickEditElementDialog.setText(text);
		UserBrickEditElementDialog.setHintText(defaultText);
		UserBrickEditElementDialog.setEditMode(editMode);
		dialog.setUserBrickElementEditorFragment(this);
	}

	@Override
	public void onFinishDialog(CharSequence text, boolean editMode) {
		UserScriptDefinitionBrickElement element = currentBrick.getUserScriptDefinitionBrickElements().get(indexOfCurrentlyEditedElement);
		if (element != null) {
			if (text != null) {
				String oldString = element.getText();
				String newString = text.toString();
				currentBrick.renameUIElement(element, oldString, newString, getActivity());
			} else if (element.getText().isEmpty()) {
				currentBrick.getUserScriptDefinitionBrickElements().remove(element);
			}
		}
		updateUserBrickParameters(currentBrick);
		updateBrickView();
	}

	private void updateUserBrickParameters(UserScriptDefinitionBrick definitionBrick) {
		Sprite sprite = ProjectManager.getInstance().getCurrentSprite();
		List<UserBrick> userBricks = sprite.getUserBricksByDefinitionBrick(definitionBrick, true, true);
		for (UserBrick userBrick : userBricks) {
			userBrick.updateUserBrickParametersAndVariables();
		}
	}

	@Override
	public void reorder(int from, int to) {
		currentBrick.reorderUIData(from, to);
		updateBrickView();
	}

	@Override
	public void click(int id) {
		UserScriptDefinitionBrickElement element = currentBrick.getUserScriptDefinitionBrickElements().get(id);
		if (element != null && !element.isLineBreak()) {
			int title = element.isVariable() ? R.string.edit_variable : R.string.edit_text;
			int defaultText = element.isVariable() ? R.string.variable_hint : R.string.text_hint;
			editElementDialog(element.getText(), true, title, defaultText);
			indexOfCurrentlyEditedElement = id;
		}
	}

	private void deleteButtonClicked(View theView) {
		DragNDropBrickLayout layout = brickView.findViewById(R.id.brick_user_flow_layout);
		int found = -1;
		for (int i = 0; i < layout.getChildCount(); i++) {
			if (layout.getChildAt(i) == theView) {
				found = i;
			}
		}
		if (found > -1) {
			currentBrick.removeDataAt(found, theView.getContext());
			updateUserBrickParameters(currentBrick);
			updateBrickView();
		}
	}

	public void updateBrickView() {
		Context context = brickView.getContext();

		DragNDropBrickLayout layout = brickView.findViewById(R.id.brick_user_flow_layout);
		layout.setListener(this);

		if (layout.getChildCount() > 0) {
			layout.removeAllViews();
		}

		for (UserScriptDefinitionBrickElement element : currentBrick.getUserScriptDefinitionBrickElements()) {
			View dataView;
			if (element.isLineBreak()) {
				dataView = View.inflate(context, R.layout.brick_user_data_line_break, null);
			} else {
				if (element.isVariable()) {
					dataView = View.inflate(context, R.layout.brick_user_data_variable, null);
				} else {
					dataView = View.inflate(context, R.layout.brick_user_data_text, null);
				}
			}

			TextView textView = dataView.findViewById(R.id.text_view);

			if (textView != null) {
				textView.setText(element.getText());
			}
			Button button = dataView.findViewById(R.id.button);
			button.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					deleteButtonClicked((View) view.getParent());
				}
			});

			layout.addView(dataView);

			if (element.isLineBreak()) {
				BrickLayout.LayoutParams params = (BrickLayout.LayoutParams) dataView.getLayoutParams();
				params.setNewLine(true);
			}

			layout.registerLineBreakListener(this);
		}
	}

	@Override
	public void setBreaks(List<Integer> breaks) {
		for (UserScriptDefinitionBrickElement data : currentBrick.getUserScriptDefinitionBrickElements()) {
			data.setNewLineHint(false);
		}
		for (int breakIndex : breaks) {
			currentBrick.getUserScriptDefinitionBrickElements().get(breakIndex).setNewLineHint(true);
		}
	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		for (int index = 0; index < menu.size(); index++) {
			menu.getItem(index).setVisible(false);
		}

		getActivity().getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		getActivity().getActionBar().setDisplayShowTitleEnabled(true);

		super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onKey(View view, int keyCode, KeyEvent event) {
		switch (keyCode) {
			case KeyEvent.KEYCODE_BACK:
				onUserDismiss();
				return true;
		}
		return false;
	}

	public void decreaseIndexOfCurrentlyEditedElement() {
		indexOfCurrentlyEditedElement--;
	}
}

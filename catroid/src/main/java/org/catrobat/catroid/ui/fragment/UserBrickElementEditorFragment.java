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
import android.widget.LinearLayout;
import android.widget.ListView;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.bricks.UserScriptDefinitionBrick;
import org.catrobat.catroid.content.bricks.UserScriptDefinitionBrickElement;
import org.catrobat.catroid.ui.BottomBar;
import org.catrobat.catroid.ui.LineBreakListener;
import org.catrobat.catroid.ui.UiUtils;
import org.catrobat.catroid.ui.dialogs.UserBrickEditElementDialog;

import java.util.List;

public class UserBrickElementEditorFragment extends Fragment implements
		OnKeyListener,
		UserBrickEditElementDialog.DialogListener,
		LineBreakListener {

	public static final String BRICK_DATA_EDITOR_FRAGMENT_TAG = "brick_data_editor_fragment";
	private static final String BRICK_BUNDLE_ARGUMENT = "current_brick";
	private UserScriptDefinitionBrick currentBrick;
	private int indexOfCurrentlyEditedElement;

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

		View fragmentView = inflater.inflate(R.layout.fragment_brick_data_editor, container, false);
		fragmentView.setFocusableInTouchMode(true);
		fragmentView.requestFocus();

		Context context = getActivity();
		View brickView = View.inflate(context, R.layout.brick_user_editable, null);

		updateBrickView();

		LinearLayout editorBrickSpace = fragmentView.findViewById(R.id.brick_data_editor_brick_space);

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
		UserBrickEditElementDialog dialog = new UserBrickEditElementDialog();
		dialog.addDialogListener(this);
		dialog.show(getFragmentManager(),
				UserBrickEditElementDialog.DIALOG_FRAGMENT_TAG);

		UserBrickEditElementDialog.setTitle(title);
		UserBrickEditElementDialog.setText(text);
		UserBrickEditElementDialog.setHintText(defaultText);
		UserBrickEditElementDialog.setEditMode(editMode);
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
		updateBrickView();
	}

	public void updateBrickView() {
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

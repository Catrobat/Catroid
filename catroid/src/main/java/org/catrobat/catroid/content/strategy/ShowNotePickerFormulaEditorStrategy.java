/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2022 The Catrobat Team
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

package org.catrobat.catroid.content.strategy;

import android.view.View;

import org.catrobat.catroid.R;
import org.catrobat.catroid.pocketmusic.ui.NotePickerDialog;
import org.catrobat.catroid.ui.UiUtils;
import org.catrobat.catroid.ui.recyclerview.fragment.ScriptFragment;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import static androidx.fragment.app.DialogFragment.STYLE_NORMAL;

public class ShowNotePickerFormulaEditorStrategy implements ShowFormulaEditorStrategy {
	private static final int OPTION_PICK_NOTE = 0;
	private static final int OPTION_FORMULA_EDIT_BRICK = 1;

	@Override
	public void showFormulaEditorToEditFormula(View view, Callback callback) {
		if (isViewInScriptFragment(view)) {
			showSelectEditDialog(view, callback);
		} else {
			callback.showFormulaEditor(view);
		}
	}

	private boolean isViewInScriptFragment(View view) {
		AppCompatActivity activity = UiUtils.getActivityFromView(view);
		if (activity == null) {
			return false;
		}

		FragmentManager supportFragmentManager = activity.getSupportFragmentManager();
		Fragment currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container);

		return currentFragment instanceof ScriptFragment;
	}

	private void showSelectEditDialog(View view, Callback callback) {
		new AlertDialog.Builder(view.getContext())
				.setItems(R.array.brick_select_note_picker,
						(dialog, which) -> switchSelectEditDialogOption(callback, view, which))
				.show();
	}

	private void switchSelectEditDialogOption(Callback callback, View view, int which) {
		switch (which) {
			case OPTION_PICK_NOTE:
				AppCompatActivity activity = UiUtils.getActivityFromView(view);
				if (activity == null) {
					return;
				}
				FragmentManager fragmentManager = activity.getSupportFragmentManager();
				if (fragmentManager.isStateSaved()) {
					return;
				}
				showNotePicker(callback, fragmentManager);
				break;
			case OPTION_FORMULA_EDIT_BRICK:
				callback.showFormulaEditor(view);
				break;
			default:
				throw new IllegalArgumentException();
		}
	}

	private void showNotePicker(Callback callback, FragmentManager fragmentManager) {
		int currentNote = callback.getValue();
		NotePickerDialog dialog = NotePickerDialog.newInstance(currentNote);
		dialog.addOnNotePickedListener(callback::setValue);
		dialog.setStyle(STYLE_NORMAL, R.style.AlertDialogWithTitle);
		dialog.show(fragmentManager, null);
	}
}

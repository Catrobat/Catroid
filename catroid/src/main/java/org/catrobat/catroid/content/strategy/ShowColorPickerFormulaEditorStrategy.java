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

package org.catrobat.catroid.content.strategy;

import android.graphics.Bitmap;
import android.view.View;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.ui.UiUtils;
import org.catrobat.catroid.ui.fragment.FormulaEditorFragment;
import org.catrobat.catroid.ui.recyclerview.fragment.ScriptFragment;
import org.catrobat.catroid.utils.ProjectManagerExtensionsKt;
import org.catrobat.paintroid.colorpicker.ColorPickerDialog;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public class ShowColorPickerFormulaEditorStrategy implements ShowFormulaEditorStrategy {
	private static final int OPTION_PICK_COLOR = 0;
	private static final int OPTION_FORMULA_EDIT_BRICK = 1;

	@Override
	public void showFormulaEditorToEditFormula(View view, Callback callback) {
		if (isInCorrectFragment(view, callback)) {
			showSelectEditDialog(view, callback);
		} else {
			callback.showFormulaEditor(view);
		}
	}

	private boolean isInCorrectFragment(View view, Callback callback) {
		AppCompatActivity activity = UiUtils.getActivityFromView(view);
		if (activity == null) {
			return false;
		}

		FragmentManager supportFragmentManager = activity.getSupportFragmentManager();
		Fragment currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container);

		if (currentFragment instanceof FormulaEditorFragment) {
			callback.showFormulaEditor(view);
		}

		return currentFragment instanceof ScriptFragment || currentFragment instanceof FormulaEditorFragment;
	}

	private void showSelectEditDialog(View view, Callback callback) {
		new AlertDialog.Builder(view.getContext())
				.setItems(R.array.brick_select_color_picker,
						(dialog, which) -> switchSelectEditDialogOption(callback, view, which))
				.show();
	}

	private void switchSelectEditDialogOption(Callback callback, View view, int which) {
		switch (which) {
			case OPTION_PICK_COLOR:
				AppCompatActivity activity = UiUtils.getActivityFromView(view);
				if (activity == null) {
					return;
				}
				FragmentManager fragmentManager = activity.getSupportFragmentManager();
				if (fragmentManager.isStateSaved()) {
					return;
				}
				showColorPicker(callback, fragmentManager);
				break;
			case OPTION_FORMULA_EDIT_BRICK:
				callback.showFormulaEditor(view);
				break;
			default:
				throw new IllegalArgumentException();
		}
	}

	private void showColorPicker(Callback callback, FragmentManager fragmentManager) {
		int currentColor = callback.getValue();
		ColorPickerDialog dialog = ColorPickerDialog.Companion.newInstance(currentColor, true,
				true);
		Bitmap projectBitmap = ProjectManagerExtensionsKt
				.getProjectBitmap(ProjectManager.getInstance());
		dialog.setBitmap(projectBitmap);
		dialog.addOnColorPickedListener(callback::setValue);
		dialog.show(fragmentManager, null);
	}
}

/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2017 The Catrobat Team
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
package org.catrobat.catroid.ui.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.utils.DynamicTextSizeArrayAdapter;

import java.util.ArrayList;
import java.util.List;

public class FormulaEditorChooseSpriteDialog extends DialogFragment {

	private static final String TAG = FormulaEditorChooseSpriteDialog.class.getSimpleName();
	private DialogInterface.OnDismissListener onDismissListener;
	private boolean success = false;
	private Spinner spinnerOne;

	public static FormulaEditorChooseSpriteDialog newInstance() {
		return new FormulaEditorChooseSpriteDialog();
	}

	public void showDialog(Fragment fragment) {
		show(fragment.getActivity().getFragmentManager(), TAG);
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		View dialogView = View.inflate(getActivity(), R.layout.dialog_formulaeditor_choose_sprite, null);
		//View dialogView = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_formulaeditor_choose_sprite, );
		setUpSpinner(dialogView);

		final AlertDialog chooseSpriteDialog = new AlertDialog.Builder(getActivity()).setView(dialogView)
				.setTitle(ProjectManager.getInstance().getCurrentSprite().getName())
				.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				}).setPositiveButton(R.string.ok, null).create();
		chooseSpriteDialog.setCanceledOnTouchOutside(true);

		chooseSpriteDialog.setOnShowListener(new DialogInterface.OnShowListener() {
			@Override
			public void onShow(DialogInterface dialog) {
				Button ok = chooseSpriteDialog.getButton(AlertDialog.BUTTON_POSITIVE);
				ok.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						chooseSpriteDialog.dismiss();
						success = true;
					}
				});
			}
		});

		return chooseSpriteDialog;
	}

	public void setOnDismissListener(DialogInterface.OnDismissListener onDismissListener) {
		this.onDismissListener = onDismissListener;
	}

	@Override
	public void onDismiss(final DialogInterface dialog) {
		super.onDismiss(dialog);
		if (onDismissListener != null) {
			onDismissListener.onDismiss(dialog);
		}
	}

	private void setUpSpinner(View dialogView) {
		spinnerOne = (Spinner) dialogView.findViewById(R.id.formula_editor_choose_sprite_spinner_one);
		List<String> spriteNames = new ArrayList<>();
		for (Sprite sprite : ProjectManager.getInstance().getCurrentScene().getSpriteList()) {
			if (sprite.getName().compareTo(getActivity().getString(R.string.background)) != 0
					&& sprite.getName().compareTo(ProjectManager.getInstance().getCurrentSprite().getName()) != 0) {
				spriteNames.add(sprite.getName());
			}
		}
		DynamicTextSizeArrayAdapter<String> adapterOne = new DynamicTextSizeArrayAdapter<String>(getActivity(),
				android.R.layout.simple_spinner_item, spriteNames);

		adapterOne.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerOne.setAdapter(adapterOne);
	}

	public boolean getSuccessStatus() {
		return success;
	}

	public String getSprite() {
		return String.valueOf(spinnerOne.getSelectedItem());
	}
}

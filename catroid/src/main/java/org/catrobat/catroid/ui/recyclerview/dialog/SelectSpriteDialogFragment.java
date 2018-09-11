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
package org.catrobat.catroid.ui.recyclerview.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Sprite;

import java.util.ArrayList;
import java.util.List;

public class SelectSpriteDialogFragment extends DialogFragment {

	public static final String TAG = SelectSpriteDialogFragment.class.getSimpleName();

	private SelectSpriteListener listener;
	private List<Sprite> selectableSprites;
	private Spinner spinner;

	public SelectSpriteDialogFragment() {
	}

	public SelectSpriteDialogFragment(SelectSpriteListener listener, List<Sprite> selectableSprites) {
		this.listener = listener;
		this.selectableSprites = selectableSprites;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		boolean isRestoringPreviouslyDestroyedActivity = savedInstanceState != null;
		if (isRestoringPreviouslyDestroyedActivity) {
			dismiss();
		}
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		View view = View.inflate(getActivity(), R.layout.dialog_formulaeditor_choose_sprite, null);

		spinner = view.findViewById(R.id.spinner);

		ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
				android.R.layout.simple_spinner_item, getSelectableSpriteNames(selectableSprites));

		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);

		return new AlertDialog.Builder(getActivity())
				.setTitle(ProjectManager.getInstance().getCurrentSprite().getName())
				.setView(view)
				.setNegativeButton(R.string.cancel, null)
				.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						onPositiveButtonClick();
					}
				})
				.create();
	}

	private void onPositiveButtonClick() {
		Sprite selectedSprite = selectableSprites.get(spinner.getSelectedItemPosition());
		listener.onSpriteSelected(selectedSprite);
	}

	private List<String> getSelectableSpriteNames(List<Sprite> selectableSprites) {
		List<String> spriteNames = new ArrayList<>();
		for (Sprite sprite : selectableSprites) {
			spriteNames.add(sprite.getName());
		}
		return spriteNames;
	}

	public interface SelectSpriteListener {

		void onSpriteSelected(Sprite selectedSprite);
	}
}

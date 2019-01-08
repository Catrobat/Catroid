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
package org.catrobat.catroid.content.bricks;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.VisualPlacementListener;
import org.catrobat.catroid.content.actions.ScriptSequenceAction;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.ui.SpriteActivity;
import org.catrobat.catroid.ui.UiUtils;
import org.catrobat.catroid.ui.fragment.FormulaEditorFragment;
import org.catrobat.catroid.ui.fragment.ScriptFragment;

import java.util.List;

public class PlaceAtBrick extends FormulaBrick implements VisualPlacementListener {

	private static final long serialVersionUID = 1L;

	public PlaceAtBrick() {
		addAllowedBrickField(BrickField.X_POSITION, R.id.brick_place_at_edit_text_x);
		addAllowedBrickField(BrickField.Y_POSITION, R.id.brick_place_at_edit_text_y);
	}

	@Override
	protected BrickField getDefaultBrickField() {
		return BrickField.X_POSITION;
	}

	public PlaceAtBrick(int xPositionValue, int yPositionValue) {
		this(new Formula(xPositionValue), new Formula(yPositionValue));
	}

	public PlaceAtBrick(Formula xPosition, Formula yPosition) {
		this();
		setFormulaWithBrickField(BrickField.X_POSITION, xPosition);
		setFormulaWithBrickField(BrickField.Y_POSITION, yPosition);
	}

	@Override
	public void onClick(final View view) {
		android.support.v4.app.FragmentManager fragmentManager = ((AppCompatActivity) view.getContext()).getSupportFragmentManager();
		Fragment scriptFragment = fragmentManager.findFragmentByTag(ScriptFragment.TAG);
		if (scriptFragment != null && scriptFragment.isVisible() && (view.getId() == R.id.brick_place_at_edit_text_x || view.getId() == R.id.brick_place_at_edit_text_y)) {
			AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
			String[] optionStrings = {view.getContext().getString(R.string.brick_place_at_option_place_visually),
					view.getContext().getString(R.string.brick_context_dialog_formula_edit_brick)};
			builder.setItems(optionStrings, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					switch (which) {
						case 0:
							FormulaEditorFragment.visualPlacement(view.getContext());
							AppCompatActivity activity = UiUtils.getActivityFromView(view);
							if (!(activity instanceof SpriteActivity)) {
								return;
							}
							((SpriteActivity) activity).registerOnCoordinatesChanges(PlaceAtBrick.this);
							break;
						case 1:
							showFormulaEditorToEditFormula(view);
					}
				}
			});
			AlertDialog dialog = builder.create();
			dialog.show();
		} else {
			super.onClick(view);
		}
	}

	@Override
	public void onPrototypeViewCreated() {
		super.onPrototypeViewCreated();
		TextView xEditText = view.findViewById(R.id.brick_place_at_edit_text_x);
		TextView yEditText = view.findViewById(R.id.brick_place_at_edit_text_y);
		xEditText.setOnClickListener(null);
		yEditText.setOnClickListener(null);
	}

	@Override
	public View getView(Context context) {
		super.getView(context);
		TextView xEditText = view.findViewById(R.id.brick_place_at_edit_text_x);
		TextView yEditText = view.findViewById(R.id.brick_place_at_edit_text_y);
		xEditText.setOnClickListener(this);
		yEditText.setOnClickListener(this);
		return view;
	}

	@Override
	public int getViewResource() {
		return R.layout.brick_place_at;
	}

	@Override
	public List<ScriptSequenceAction> addActionToSequence(Sprite sprite, ScriptSequenceAction sequence) {
		sequence.addAction(sprite.getActionFactory().createPlaceAtAction(sprite,
				getFormulaWithBrickField(BrickField.X_POSITION),
				getFormulaWithBrickField(BrickField.Y_POSITION)));
		return null;
	}

	@Override
	public void saveNewCoordinates(int x, int y) {
		setFormulaWithBrickField(BrickField.X_POSITION, new Formula(x));
		setFormulaWithBrickField(BrickField.Y_POSITION, new Formula(y));
	}
}

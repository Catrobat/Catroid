/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010-2011 The Catroid Team
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://www.catroid.org/catroid_license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *   
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package at.tugraz.ist.catroid.content.bricks;

import android.content.Context;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.ui.ScriptTabActivity;
import at.tugraz.ist.catroid.ui.dialogs.BrickTextDialog;

public class ChangeGhostEffectByNBrick implements Brick, OnClickListener {
	private static final long serialVersionUID = 1L;
	private double changeGhostEffect;
	private Sprite sprite;

	private transient View view;

	public ChangeGhostEffectByNBrick() {

	}

	public ChangeGhostEffectByNBrick(Sprite sprite, double changeGhostEffect) {
		this.sprite = sprite;
		this.changeGhostEffect = changeGhostEffect;
	}

	@Override
	public int getRequiredResources() {
		return NO_RESOURCES;
	}

	@Override
	public void execute() {
		sprite.costume.changeAlphaValueBy((float) this.changeGhostEffect / -100);
	}

	@Override
	public Sprite getSprite() {
		return this.sprite;
	}

	public double getChangeGhostEffect() {
		return changeGhostEffect;
	}

	@Override
	public View getView(Context context, int brickId, BaseAdapter adapter) {

		view = View.inflate(context, R.layout.brick_change_ghost_effect, null);

		TextView textX = (TextView) view.findViewById(R.id.brick_change_ghost_effect_text_view);
		EditText editX = (EditText) view.findViewById(R.id.brick_change_ghost_effect_edit_text);
		editX.setText(String.valueOf(changeGhostEffect));

		textX.setVisibility(View.GONE);
		editX.setVisibility(View.VISIBLE);
		editX.setOnClickListener(this);

		return view;
	}

	@Override
	public View getPrototypeView(Context context) {
		return View.inflate(context, R.layout.brick_change_ghost_effect, null);
	}

	@Override
	public Brick clone() {
		return new ChangeGhostEffectByNBrick(getSprite(), getChangeGhostEffect());
	}

	@Override
	public void onClick(View view) {
		ScriptTabActivity activity = (ScriptTabActivity) view.getContext();

		BrickTextDialog editDialog = new BrickTextDialog() {
			@Override
			protected void initialize() {
				input.setText(String.valueOf(changeGhostEffect));
				input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL
						| InputType.TYPE_NUMBER_FLAG_SIGNED);
				input.setSelectAllOnFocus(true);
			}

			@Override
			protected boolean handleOkButton() {
				try {
					changeGhostEffect = Double.parseDouble(input.getText().toString());
				} catch (NumberFormatException exception) {
					Toast.makeText(getActivity(), R.string.error_no_number_entered, Toast.LENGTH_SHORT).show();
				}

				return true;
			}
		};

		editDialog.show(activity.getSupportFragmentManager(), "dialog_change_ghost_effect_brick");
	}
}

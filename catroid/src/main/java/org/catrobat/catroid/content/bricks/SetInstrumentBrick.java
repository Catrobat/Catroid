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

package org.catrobat.catroid.content.bricks;

import android.content.Context;
import android.view.View;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Nameable;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ScriptSequenceAction;
import org.catrobat.catroid.content.bricks.brickspinner.BrickSpinner;
import org.catrobat.catroid.content.bricks.brickspinner.PickableMusicalInstrument;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;

public class SetInstrumentBrick extends BrickBaseType implements BrickSpinner.OnItemSelectedListener<PickableMusicalInstrument> {

	public PickableMusicalInstrument instrumentSelection = PickableMusicalInstrument.values()[0];

	@Override
	public int getViewResource() {
		return R.layout.brick_set_instrument;
	}

	protected int getSpinnerId() {
		return R.id.set_instrument_spinner;
	}

	@Override
	public View getView(Context context) {
		super.getView(context);
		List<Nameable> items = new ArrayList<>();

		for (PickableMusicalInstrument instrument : PickableMusicalInstrument.values()) {
			instrument.setName(instrument.getString(context));
			items.add(instrument);
		}

		BrickSpinner<PickableMusicalInstrument> spinner = new BrickSpinner<>(R.id.set_instrument_spinner, view, items);
		spinner.setSelection(PickableMusicalInstrument.getIndexByValue(instrumentSelection.getValue()));
		spinner.setOnItemSelectedListener(this);

		return view;
	}

	@Override
	public void addActionToSequence(Sprite sprite, ScriptSequenceAction sequence) {
		sequence.addAction(sprite.getActionFactory().createSetInstrumentAction(instrumentSelection));
	}

	@Override
	public void onNewOptionSelected(Integer spinnerId) {
	}

	@Override
	public void onEditOptionSelected(Integer spinnerId) {
	}

	@Override
	public void onStringOptionSelected(Integer spinnerId, String string) {
	}

	@Override
	public void onItemSelected(Integer spinnerId, @Nullable PickableMusicalInstrument item) {
		if (item != null) {
			instrumentSelection = item;
		}
	}
}

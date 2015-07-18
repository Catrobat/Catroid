/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2015 The Catrobat Team
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
import android.widget.BaseAdapter;

import org.catrobat.catroid.formulaeditor.Formula;

import java.util.Iterator;

public abstract class FormulaBrick extends BrickBaseType implements View.OnClickListener {

	private ConcurrentFormulaHashMap formulaMap;

	public Formula getFormulaWithBrickField(BrickField brickField) throws IllegalArgumentException {
		if (formulaMap != null && formulaMap.containsKey(brickField)) {
			return formulaMap.get(brickField);
		} else {
			throw new IllegalArgumentException("Incompatible Brick Field : " + brickField.toString());
		}
	}

	public void setFormulaWithBrickField(BrickField brickField, Formula formula) throws IllegalArgumentException {
		if (formulaMap != null && formulaMap.containsKey(brickField)) {
			formulaMap.replace(brickField, formula);
		} else {
			throw new IllegalArgumentException("Incompatible Brick Field : " + brickField.toString());
		}
	}

	protected void addAllowedBrickField(BrickField brickField) {
		if (formulaMap == null) {
			formulaMap = new ConcurrentFormulaHashMap();
		}
		formulaMap.putIfAbsent(brickField, new Formula(0));
	}

	@Override
	public Brick clone() throws CloneNotSupportedException {
		FormulaBrick clonedBrick = (FormulaBrick) super.clone();
		clonedBrick.formulaMap = this.formulaMap.clone();
		return clonedBrick;
	}

	public Formula getFormula() {
		if (formulaMap == null) {
			return null;
		}

		Iterator<BrickField> brickFieldIterator = formulaMap.keySet().iterator();
		if (brickFieldIterator.hasNext()) {
			return formulaMap.get(brickFieldIterator.next());
		} else {
			return null;
		}
	}

	@Override
	public void onClick(View view) {
		if (checkbox.getVisibility() == View.VISIBLE) {
			return;
		}
		showFormulaEditorToEditFormula(view);
	}

	public View getCustomView(Context context, int brickId, BaseAdapter baseAdapter) {
		return null;
	}

	public abstract void showFormulaEditorToEditFormula(View view);
}

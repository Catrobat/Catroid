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

package org.catrobat.catroid.userbrick;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.UserData;

import java.io.Serializable;
import java.util.UUID;

import static org.catrobat.catroid.userbrick.UserDefinedBrickData.UserDefinedBrickDataType.INPUT;

@XStreamAlias("userDefinedBrickInput")
public class UserDefinedBrickInput extends UserDefinedBrickData implements Serializable,
		UserData<Formula> {

	@XStreamAlias("input")
	private InputFormulaField name;
	private transient Formula value;
	private int initialIndex = -1;

	public UserDefinedBrickInput(String input) {
		this.name = new InputFormulaField(input);
		this.type = INPUT;
		this.value = new Formula(0);
	}

	public UserDefinedBrickInput(UserDefinedBrickInput userDefinedBrickInput) {
		this.name = userDefinedBrickInput.name;
		this.type = INPUT;
		this.value = userDefinedBrickInput.value;
	}

	@Override
	public String getName() {
		return this.name.toString();
	}

	@Override
	public void setName(String name) {
	}

	public int getInitialIndex() {
		return initialIndex;
	}

	public void setInitialIndex(int initialIndex) {
		this.initialIndex = initialIndex;
	}

	public InputFormulaField getInputFormulaField() {
		return this.name;
	}

	@Override
	public Formula getValue() {
		if (this.value == null) {
			this.value = new Formula(0);
		}
		return this.value;
	}

	@Override
	public void setValue(Formula value) {
		this.value = value;
	}

	@Override
	public void reset() {
		this.value = new Formula(0);
	}

	@Override
	public UUID getDeviceKey() {
		return UUID.nameUUIDFromBytes(this.name.toString().getBytes());
	}
}


/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2020 The Catrobat Team
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

import org.catrobat.catroid.formulaeditor.UserData;

import java.io.Serializable;
import java.util.UUID;

import static org.catrobat.catroid.userbrick.UserDefinedBrickData.UserDefinedBrickDataType.INPUT;

@XStreamAlias("userDefinedBrickInput")
public class UserDefinedBrickInput extends UserDefinedBrickData implements Serializable,
		UserData<Object> {

	@XStreamAlias("input")
	private InputFormulaField input;
	private transient Object value;

	public UserDefinedBrickInput(String input) {
		this.input = new InputFormulaField(input);
		this.type = INPUT;
		this.value = 0d;
	}

	public UserDefinedBrickInput(UserDefinedBrickInput userDefinedBrickInput) {
		this.input = userDefinedBrickInput.input;
		this.type = INPUT;
		this.value = userDefinedBrickInput.value;
	}

	@Override
	public String getName() {
		return this.input.toString();
	}

	@Override
	public void setName(String name) {
	}

	public InputFormulaField getInputFormulaField() {
		return this.input;
	}

	@Override
	public Object getValue() {
		if (this.value == null) {
			this.value = 0d;
		}
		return this.value;
	}

	@Override
	public void setValue(Object value) {
		this.value = value;
	}

	@Override
	public void reset() {
		this.value = 0d;
	}

	@Override
	public UUID getDeviceKey() {
		return UUID.nameUUIDFromBytes(this.input.toString().getBytes());
	}
}


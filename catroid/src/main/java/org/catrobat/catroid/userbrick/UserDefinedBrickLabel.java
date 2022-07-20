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

import java.io.Serializable;

import androidx.annotation.Nullable;

import static org.catrobat.catroid.userbrick.UserDefinedBrickData.UserDefinedBrickDataType.LABEL;

@XStreamAlias("userDefinedBrickLabel")
public class UserDefinedBrickLabel extends UserDefinedBrickData implements Serializable {

	@XStreamAlias("label")
	private String label;

	public UserDefinedBrickLabel(String label) {
		this.label = label;
		this.type = LABEL;
	}

	public UserDefinedBrickLabel(UserDefinedBrickLabel userDefinedBrickLabel) {
		this.label = userDefinedBrickLabel.label;
		this.type = LABEL;
	}

	@Override
	public String getName() {
		return this.label;
	}
	@Override
	public boolean equals(@Nullable Object obj) {
		if (obj instanceof UserDefinedBrickLabel) {
			UserDefinedBrickLabel other = (UserDefinedBrickLabel) obj;
			return this.label.equals(other.label);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return this.label.hashCode();
	}
}

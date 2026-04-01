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
package org.catrobat.catroid.content.eventids;

import com.google.common.base.Objects;

public class RaspiEventId extends EventId {
	private final String pin;
	private final String eventValue;

	public RaspiEventId(String pin, String eventValue) {
		this.pin = pin;
		this.eventValue = eventValue;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof RaspiEventId)) {
			return false;
		}
		if (!super.equals(o)) {
			return false;
		}
		RaspiEventId that = (RaspiEventId) o;
		return Objects.equal(pin, that.pin)
				&& Objects.equal(eventValue, that.eventValue);
	}

	@Override
	public int hashCode() {
		int result = pin != null ? pin.hashCode() : 0;
		result = 31 * result + (eventValue != null ? eventValue.hashCode() : 0);
		return result;
	}
}

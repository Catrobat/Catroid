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

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import androidx.annotation.IntDef;

public class EventId {
	@Retention(RetentionPolicy.SOURCE)
	@IntDef({TAP, TAP_BACKGROUND, START, START_AS_CLONE, ANY_NFC, OTHER})
	public @interface EventType {
	}

	public static final int OTHER = 0;
	public static final int TAP = 1;
	public static final int TAP_BACKGROUND = 2;
	public static final int START = 3;
	public static final int START_AS_CLONE = 4;
	public static final int ANY_NFC = 5;

	@EventType
	private final int type;

	public EventId(@EventType int type) {
		this.type = type;
	}

	protected EventId() {
		this.type = OTHER;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof EventId)) {
			return false;
		}

		EventId eventId = (EventId) o;

		return type == eventId.type;
	}

	@Override
	public int hashCode() {
		return type;
	}
}

/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2017 The Catrobat Team
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

package org.catrobat.catroid.scratchconverter.protocol.command;

import org.json.JSONObject;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public abstract class Command {

	public enum Type {
		AUTHENTICATE(0),
		RETRIEVE_INFO(1),
		SCHEDULE_JOB(2),
		CANCEL_DOWNLOAD(3);

		private final int typeID;

		Type(final int typeID) {
			this.typeID = typeID;
		}

		public int getTypeID() {
			return typeID;
		}
	}

	public enum ArgumentType {
		CLIENT_ID("clientID"),
		JOB_ID("jobID"),
		FORCE("force"),
		VERBOSE("verbose");
		private final String rawValue;

		ArgumentType(final String rawValue) {
			this.rawValue = rawValue;
		}

		@Override
		public String toString() {
			return rawValue;
		}
	}

	private final Type type;
	private final Map<ArgumentType, Object> arguments;

	public Command(Type type) {
		this.type = type;
		this.arguments = new EnumMap<>(ArgumentType.class);
	}

	public void addArgument(ArgumentType type, Object value) {
		arguments.put(type, value);
	}

	public JSONObject toJson() {
		final Map<String, Object> args = new HashMap<>();
		for (Map.Entry<ArgumentType, Object> entry : arguments.entrySet()) {
			args.put(entry.getKey().toString(), entry.getValue());
		}
		final Map<String, Object> payloadMap = new HashMap<>();
		payloadMap.put("cmd", type.getTypeID());
		payloadMap.put("args", args);
		return new JSONObject(payloadMap);
	}

	public Type getType() {
		return type;
	}
}

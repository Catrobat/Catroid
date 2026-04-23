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

package org.catrobat.catroid.io;

import android.util.Log;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.io.File;
import java.lang.reflect.Type;

public class BackpackInterfaceSerializerAndDeserializer<T> implements JsonSerializer<T>,
		JsonDeserializer<T> {

	private static final String TAG = BackpackInterfaceSerializerAndDeserializer.class.getSimpleName();

	private static final String TYPE = "type";
	private static final String PROPERTY = "properties";

	File file;

	public BackpackInterfaceSerializerAndDeserializer(File file) {
		this.file = file;
	}

	@Override
	public JsonElement serialize(T object, Type interfaceType, JsonSerializationContext context) {
		JsonObject jsonObject = new JsonObject();
		String packageName = object.getClass().getPackage().getName();
		String className = object.getClass().getSimpleName();
		jsonObject.add(TYPE, new JsonPrimitive(packageName + '.' + className));
		jsonObject.add(PROPERTY, context.serialize(object, object.getClass()));
		return jsonObject;
	}

	@Override
	public T deserialize(JsonElement json, Type interfaceType, JsonDeserializationContext context) {
		JsonObject jsonObject = json.getAsJsonObject();
		String type = jsonObject.get(TYPE).getAsString();
		JsonElement element = jsonObject.get(PROPERTY);

		Class classToDeserialize;
		try {
			classToDeserialize = Class.forName(type);
		} catch (ClassNotFoundException classNotFoundException) {
			Log.e(TAG, "Could not deserialize backpacked element: " + type);
			file.delete();
			return null;
		}
		return context.deserialize(element, classToDeserialize);
	}
}

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

package org.catrobat.catroid.io;

import android.util.Log;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import org.catrobat.catroid.content.bricks.Brick;

import java.lang.reflect.Type;

public class BackpackBrickSerializerAndDeserializer implements JsonSerializer<Brick>, JsonDeserializer<Brick> {

	private static final String TAG = BackpackBrickSerializerAndDeserializer.class.getSimpleName();

	private static final String TYPE = "bricktype";
	private static final String PROPERTY = "properties";

	@Override
	public JsonElement serialize(Brick brick, Type typeOfSrc, JsonSerializationContext context) {
		JsonObject jsonObject = new JsonObject();
		String packageName = brick.getClass().getPackage().getName();
		String className = brick.getClass().getSimpleName();
		jsonObject.add(TYPE, new JsonPrimitive(packageName + '.' + className));
		jsonObject.add(PROPERTY, context.serialize(brick, brick.getClass()));
		return jsonObject;
	}

	@Override
	public Brick deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
		JsonObject jsonObject = json.getAsJsonObject();
		String type = jsonObject.get(TYPE).getAsString();
		JsonElement element = jsonObject.get(PROPERTY);

		Class classToDeserialize;
		try {
			classToDeserialize = Class.forName(type);
		} catch (ClassNotFoundException classNotFoundException) {
			Log.e(TAG, "Could not deserialize backpacked brick element: " + type);
			StorageHandler.getInstance().deleteBackpackFile();
			return null;
		}
		return context.deserialize(element, classToDeserialize);
	}
}

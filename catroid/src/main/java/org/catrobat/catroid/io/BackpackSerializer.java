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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.catrobat.catroid.common.Backpack;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.userbrick.UserDefinedBrickData;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public final class BackpackSerializer {

	private static final String TAG = BackpackSerializer.class.getSimpleName();
	private final File backpackFile;

	private Gson backpackGson;

	public BackpackSerializer(File backpackFile) {
		this.backpackFile = backpackFile;
		GsonBuilder gsonBuilder = new GsonBuilder().enableComplexMapKeySerialization().setPrettyPrinting();
		gsonBuilder.registerTypeAdapter(Script.class,
				new BackpackScriptSerializerAndDeserializer());
		gsonBuilder.registerTypeAdapter(Brick.class,
				new BackpackInterfaceSerializerAndDeserializer(backpackFile));
		gsonBuilder.registerTypeAdapter(UserDefinedBrickData.class,
				new BackpackInterfaceSerializerAndDeserializer(backpackFile));
		gsonBuilder.registerTypeAdapter(Brick.FormulaField.class,
				new BackpackFormulaFieldSerializerAndDeserializer(backpackFile));
		backpackGson = gsonBuilder.create();
	}

	public boolean saveBackpack(Backpack backpack) {
		FileWriter writer = null;
		String json = backpackGson.toJson(backpack);

		try {
			backpackFile.createNewFile();
			writer = new FileWriter(backpackFile);
			writer.write(json);
			return true;
		} catch (IOException e) {
			Log.e(TAG, Log.getStackTraceString(e));
			return false;
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
					Log.e(TAG, "Cannot close Buffered Writer", e);
				}
			}
		}
	}

	public Backpack loadBackpack() {
		if (!backpackFile.exists()) {
			return new Backpack();
		}

		try {
			BufferedReader bufferedReader = new BufferedReader(new FileReader(backpackFile));
			return backpackGson.fromJson(bufferedReader, Backpack.class);
		} catch (Exception e) {
			if (!(e instanceof FileNotFoundException)) {
				backpackFile.delete();
			}
			Log.e(TAG, "Cannot load Backpack. Creating new Backpack File.", e);
			return new Backpack();
		}
	}
}

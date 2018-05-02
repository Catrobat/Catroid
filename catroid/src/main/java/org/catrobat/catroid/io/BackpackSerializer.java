/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2018 The Catrobat Team
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
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import org.catrobat.catroid.common.Backpack;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.bricks.Brick;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import static org.catrobat.catroid.common.Constants.BACKPACK_DIRECTORY;
import static org.catrobat.catroid.common.Constants.BACKPACK_FILE;
import static org.catrobat.catroid.common.Constants.BACKPACK_IMAGE_DIRECTORY;
import static org.catrobat.catroid.common.Constants.BACKPACK_SCENE_DIRECTORY;
import static org.catrobat.catroid.common.Constants.BACKPACK_SOUND_DIRECTORY;
import static org.catrobat.catroid.common.Constants.DEFAULT_ROOT_DIRECTORY;

public final class BackpackSerializer {

	private static final String TAG = BackpackSerializer.class.getSimpleName();
	private static final BackpackSerializer INSTANCE = new BackpackSerializer();

	private Gson backpackGson;

	public static BackpackSerializer getInstance() {
		return INSTANCE;
	}

	private BackpackSerializer() {
		GsonBuilder gsonBuilder = new GsonBuilder().enableComplexMapKeySerialization().setPrettyPrinting();
		gsonBuilder.registerTypeAdapter(Script.class, new BackpackScriptSerializerAndDeserializer());
		gsonBuilder.registerTypeAdapter(Brick.class, new BackpackBrickSerializerAndDeserializer());
		backpackGson = gsonBuilder.create();

		DEFAULT_ROOT_DIRECTORY.mkdir();
		BACKPACK_DIRECTORY.mkdir();
		BACKPACK_SCENE_DIRECTORY.mkdir();
		BACKPACK_IMAGE_DIRECTORY.mkdir();
		BACKPACK_SOUND_DIRECTORY.mkdir();
	}

	public boolean saveBackpack(Backpack backpack) {
		FileWriter writer = null;
		String json = backpackGson.toJson(backpack);

		try {
			BACKPACK_FILE.createNewFile();
			writer = new FileWriter(BACKPACK_FILE);
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
		if (!BACKPACK_FILE.exists()) {
			return new Backpack();
		}

		try {
			BufferedReader bufferedReader = new BufferedReader(new FileReader(BACKPACK_FILE));
			return backpackGson.fromJson(bufferedReader, Backpack.class);
		} catch (FileNotFoundException e) {
			Log.e(TAG, "FileNotFoundException: Could not create buffered Writer with file: "
					+ BACKPACK_FILE.getAbsolutePath());
			return new Backpack();
		} catch (JsonSyntaxException | JsonIOException jsonException) {
			Log.e(TAG, "Cannot load Backpack. Creating new Backpack File.", jsonException);
			BACKPACK_FILE.delete();
			return new Backpack();
		}
	}
}

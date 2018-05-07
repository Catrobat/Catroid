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
package org.catrobat.catroid.common;

import android.support.annotation.NonNull;

import org.catrobat.catroid.io.StorageOperations;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

public class SoundInfo implements Serializable, Comparable<SoundInfo>, Cloneable {

	private static final long serialVersionUID = 1L;
	private static final String TAG = SoundInfo.class.getSimpleName();

	private String name;
	private String fileName;

	private transient File file;

	public SoundInfo() {
	}

	public SoundInfo(String name, @NonNull File file) {
		this.name = name;
		this.file = file;
		fileName = file.getName();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFileName() {
		return fileName;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
		fileName = file.getName();
	}

	@SuppressWarnings("MethodDoesntCallSuperMethod")
	@Override
	public SoundInfo clone() {
		try {
			return new SoundInfo(name, StorageOperations.duplicateFile(file));
		} catch (IOException e) {
			throw new RuntimeException(TAG + ": Could not copy file: " + file.getAbsolutePath());
		}
	}

	@SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
	@Override
	public boolean equals(Object obj) {
		return file.equals(((SoundInfo) obj).file);
	}

	@Override
	public int hashCode() {
		return file.hashCode();
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public int compareTo(@NonNull SoundInfo soundInfo) {
		return name.compareTo(soundInfo.name);
	}
}

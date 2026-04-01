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
package org.catrobat.catroid.common;

import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

import org.catrobat.catroid.io.StorageOperations;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

import androidx.annotation.NonNull;

public class SoundInfo implements Cloneable, Nameable, Serializable {

	private static final long serialVersionUID = 1L;
	private static final String TAG = SoundInfo.class.getSimpleName();

	@XStreamAsAttribute
	private String name;
	@XStreamAsAttribute
	private String fileName;

	private transient File file;

	boolean midiFile;

	public SoundInfo() {
	}

	public SoundInfo(String name, @NonNull File file) {
		this.name = name;
		this.file = file;
		fileName = file.getName();
		midiFile = false;
	}

	public SoundInfo(String name, @NonNull File file, boolean midiFile) {
		this.name = name;
		this.file = file;
		fileName = file.getName();
		this.midiFile = midiFile;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getXstreamFileName() {
		if (file != null) {
			throw new IllegalStateException("This should be used only to deserialize the Object."
					+ " You should use @getFile() instead.");
		}
		return fileName;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
		fileName = file.getName();
	}

	public boolean isMidiFile() {
		return midiFile;
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
}

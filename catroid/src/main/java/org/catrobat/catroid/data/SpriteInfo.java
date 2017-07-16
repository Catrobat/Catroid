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

package org.catrobat.catroid.data;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;

import org.catrobat.catroid.data.brick.Brick;
import org.catrobat.catroid.gui.adapter.ListItem;
import org.catrobat.catroid.storage.DirectoryPathInfo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SpriteInfo implements ListItem {

	private String name;
	private DirectoryPathInfo pathInfo;

	private List<LookInfo> looks = new ArrayList<>();
	private List<SoundInfo> sounds = new ArrayList<>();
	private List<Brick> bricks = new ArrayList<>();

	private transient RoundedBitmapDrawable thumbnail;

	public SpriteInfo(String name, DirectoryPathInfo pathInfo) {
		this.name = name;
		this.pathInfo = pathInfo;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	public DirectoryPathInfo getDirectoryInfo() {
		return pathInfo;
	}

	public List<LookInfo> getLooks() {
		return looks;
	}

	public List<SoundInfo> getSounds() {
		return sounds;
	}

	public List<Brick> getBricks() {
		return bricks;
	}

	@Override
	public void createThumbnail() {
		if (looks.isEmpty()) {
			Bitmap bitmap = Bitmap.createBitmap(THUMBNAIL_WIDTH, THUMBNAIL_HEIGHT, BITMAP_CONFIG);
			bitmap.eraseColor(Color.GRAY);

			thumbnail = RoundedBitmapDrawableFactory.create(Resources.getSystem(), bitmap);
			thumbnail.setCircular(true);
		} else {
			thumbnail = (RoundedBitmapDrawable) looks.get(0).getThumbnail();
		}
	}

	@Override
	public Drawable getThumbnail() {
		createThumbnail();
		return thumbnail;
	}

	@Override
	public SpriteInfo clone() throws CloneNotSupportedException {
		SpriteInfo clone = new SpriteInfo(name, new DirectoryPathInfo(pathInfo.getParent(), pathInfo.getRelativePath()));

		for (LookInfo look : looks) {
			clone.addLook(look.clone());
		}

		for (SoundInfo sound : sounds) {
			clone.addSound(sound.clone());
		}

		for (Brick brick : bricks) {
			clone.addBrick(brick.clone());
		}

		return clone;
	}

	@Override
	public void copyResourcesToDirectory(DirectoryPathInfo directoryPathInfo) throws IOException {
		pathInfo = directoryPathInfo;

		for (LookInfo look : looks) {
			look.copyResourcesToDirectory(directoryPathInfo);
		}

		for (SoundInfo sound : sounds) {
			sound.copyResourcesToDirectory(directoryPathInfo);
		}
	}

	@Override
	public void removeResources() throws IOException {
		for (LookInfo look : looks) {
			look.removeResources();
		}

		for (SoundInfo sound : sounds) {
			sound.removeResources();
		}
	}

	public void addLook(LookInfo look) {
		looks.add(look);
	}

	public void addSound(SoundInfo sound) {
		sounds.add(sound);
	}

	public void addBrick(Brick brick) {
		bricks.add(brick);
	}
}

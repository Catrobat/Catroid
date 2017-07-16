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

import org.catrobat.catroid.gui.adapter.ListItem;
import org.catrobat.catroid.storage.DirectoryPathInfo;
import org.catrobat.catroid.storage.StorageManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ProjectInfo implements ListItem {

	public static final String TAG = ProjectInfo.class.getSimpleName();

	private String name;
	private List<SceneInfo> scenes = new ArrayList<>();

	private DirectoryPathInfo pathInfo;
	private transient RoundedBitmapDrawable thumbnail;

	public ProjectInfo(String name) throws IOException {
		this.name = name;
		pathInfo = StorageManager.mkDir(StorageManager.getProjectsDirectory(), name);
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	public List<SceneInfo> getScenes() {
		return scenes;
	}

	public DirectoryPathInfo getDirectoryInfo() {
		return pathInfo;
	}

	@Override
	public void createThumbnail() {
		Bitmap bitmap = Bitmap.createBitmap(THUMBNAIL_WIDTH, THUMBNAIL_HEIGHT, BITMAP_CONFIG);
		bitmap.eraseColor(Color.GRAY);

		thumbnail = RoundedBitmapDrawableFactory.create(Resources.getSystem(), bitmap);
		thumbnail.setCircular(true);
	}

	@Override
	public Drawable getThumbnail() {
		if (thumbnail == null) {
			createThumbnail();
		}
		return thumbnail;
	}

	@Override
	public ProjectInfo clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException("Cannot clone Project");
	}

	@Override
	public void copyResourcesToDirectory(DirectoryPathInfo directoryPathInfo) throws IOException {
		pathInfo = directoryPathInfo;

		for (SceneInfo scene : scenes) {
			scene.copyResourcesToDirectory(directoryPathInfo);
		}
	}

	@Override
	public void removeResources() throws IOException {
		StorageManager.clearDirectory(pathInfo);
		StorageManager.deleteFile(pathInfo);
	}

	public void addScene(SceneInfo scene) {
		scenes.add(scene);
	}

	public SceneInfo getSceneByName(String name) {
		for (SceneInfo scene : scenes) {
			if (scene.getName().equals(name)) {
				return scene;
			}
		}

		return null;
	}
}

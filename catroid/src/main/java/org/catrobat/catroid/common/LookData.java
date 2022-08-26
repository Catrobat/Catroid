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

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.io.StorageOperations;
import org.catrobat.catroid.sensing.CollisionInformation;
import org.catrobat.catroid.utils.ImageEditing;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;

public class LookData implements Cloneable, Nameable, Serializable {

	private static final long serialVersionUID = 1L;
	private static final String TAG = LookData.class.getSimpleName();

	private static final transient int THUMBNAIL_WIDTH = 150;
	private static final transient int THUMBNAIL_HEIGHT = 150;

	@XStreamAsAttribute
	protected String name;
	@XStreamAsAttribute
	protected String fileName;

	protected transient File file;

	private transient Bitmap thumbnailBitmap;

	protected transient Integer width;
	protected transient Integer height;

	protected transient Pixmap pixmap = null;
	transient TextureRegion textureRegion = null;

	private transient CollisionInformation collisionInformation = null;

	private boolean valid = true;

	private boolean isWebRequest = false;

	public LookData() {
	}

	public LookData(String name, @NonNull File file) {
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

	public boolean getIsWebRequest() {
		return isWebRequest;
	}

	public void setIsWebRequest(Boolean isWebRequest) {
		this.isWebRequest = isWebRequest;
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

	public void addRequiredResources(final Brick.ResourcesSet requiredResourcesSet) {
	}

	public void draw(Batch batch, float alpha) {
	}

	public void dispose() {
		if (pixmap != null) {
			pixmap.dispose();
			pixmap = null;
		}
		if (textureRegion != null) {
			textureRegion.getTexture().dispose();
			textureRegion = null;
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}

		if (obj == null) {
			return false;
		}

		if (!(obj instanceof LookData)) {
			return false;
		}

		LookData lookData = (LookData) obj;
		return lookData.file.equals(this.file);
	}

	@SuppressWarnings("MethodDoesntCallSuperMethod")
	@Override
	public LookData clone() {
		try {
			return new LookData(name, StorageOperations.duplicateFile(file));
		} catch (IOException e) {
			throw new RuntimeException(TAG + ": Could not copy file: " + file.getAbsolutePath());
		}
	}

	@Override
	public int hashCode() {
		return file.hashCode() + super.hashCode();
	}

	public TextureRegion getTextureRegion() {
		if (textureRegion == null) {
			textureRegion = new TextureRegion(new Texture(getPixmap()));
		}
		return textureRegion;
	}

	@VisibleForTesting
	public void setTextureRegion(TextureRegion textureRegion) {
		this.textureRegion = textureRegion;
	}

	public Pixmap getPixmap() {
		if (pixmap == null) {
			try {
				pixmap = new Pixmap(Gdx.files.absolute(file.getAbsolutePath()));
			} catch (GdxRuntimeException gdxRuntimeException) {
				Log.e(TAG, Log.getStackTraceString(gdxRuntimeException));
				if (gdxRuntimeException.getMessage().startsWith("Couldn't load file:")) {
					pixmap = new Pixmap(1, 1, Pixmap.Format.Alpha);
				}
			} catch (NullPointerException nullPointerException) {
				Log.e(TAG, Log.getStackTraceString(nullPointerException));
			}
		}
		return pixmap;
	}

	@VisibleForTesting
	public void setPixmap(Pixmap pixmap) {
		this.pixmap = pixmap;
	}

	public Bitmap getThumbnailBitmap() {
		if (thumbnailBitmap == null && file != null) {
			thumbnailBitmap = ImageEditing.getScaledBitmapFromPath(file.getAbsolutePath(),
					THUMBNAIL_WIDTH,
					THUMBNAIL_HEIGHT,
					ImageEditing.ResizeType.STAY_IN_RECTANGLE_WITH_SAME_ASPECT_RATIO, false);
		}
		return thumbnailBitmap;
	}

	public void invalidateThumbnailBitmap() {
		thumbnailBitmap = null;
	}

	public int[] getMeasure() {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(file.getAbsolutePath(), options);
		width = options.outWidth;
		height = options.outHeight;

		return new int[] {width, height};
	}

	public void clearCollisionInformation() {
		collisionInformation = null;
	}

	public CollisionInformation getCollisionInformation() {
		if (collisionInformation == null) {
			collisionInformation = new CollisionInformation(this);
		}
		return collisionInformation;
	}

	public String getImageMimeType() {
		String pathName = file.getAbsolutePath();
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(pathName, options);
		return options.outMimeType;
	}

	public boolean isValid() {
		return valid;
	}

	public void invalidate() {
		valid = false;
	}
}

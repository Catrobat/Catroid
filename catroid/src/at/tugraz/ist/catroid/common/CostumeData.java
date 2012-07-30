/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010-2011 The Catroid Team
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://www.catroid.org/catroid_license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *   
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package at.tugraz.ist.catroid.common;

import java.util.concurrent.Semaphore;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.utils.ImageEditing;
import at.tugraz.ist.catroid.utils.Utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class CostumeData {

	private String name;
	private String fileName;
	private transient Bitmap thumbnailBitmap;
	private transient Integer width;
	private transient Integer height;
	private transient static final int THUMBNAIL_WIDTH = 150;
	private transient static final int THUMBNAIL_HEIGHT = 150;
	private transient Pixmap pixmap = null;
	private transient Pixmap originalPixmap = null;
	private transient TextureRegion region = null;
	private Semaphore pixmapLock = new Semaphore(1);
	private Semaphore originalPixmapLock = new Semaphore(1);
	private Semaphore textureRegionLock = new Semaphore(1);

	public void adjustBrightness(float brightnessValue) {
		Pixmap currentPixmap = getOriginalPixmap();
		Pixmap newPixmap = new Pixmap(currentPixmap.getWidth(), currentPixmap.getHeight(), currentPixmap.getFormat());
		newPixmap.drawPixmap(currentPixmap, 0, 0);
		for (int y = 0; y < currentPixmap.getHeight(); y++) {
			for (int x = 0; x < currentPixmap.getWidth(); x++) {
				int pixel = currentPixmap.getPixel(x, y);
				int r = (int) (((pixel >> 24) & 0xff) + (255 * (brightnessValue - 1)));
				int g = (int) (((pixel >> 16) & 0xff) + (255 * (brightnessValue - 1)));
				int b = (int) (((pixel >> 8) & 0xff) + (255 * (brightnessValue - 1)));
				int a = pixel & 0xff;

				if (r > 255) {
					r = 255;
				} else if (r < 0) {
					r = 0;
				}
				if (g > 255) {
					g = 255;
				} else if (g < 0) {
					g = 0;
				}
				if (b > 255) {
					b = 255;
				} else if (b < 0) {
					b = 0;
				}

				newPixmap.setColor(r / 255f, g / 255f, b / 255f, a / 255f);
				newPixmap.drawPixel(x, y);
			}
		}
		setPixmap(newPixmap);
		setTextureRegion();

	}

	public TextureRegion getTextureRegion() {

		if (region == null) {
			textureRegionLock.acquireUninterruptibly();
			region = new TextureRegion(new Texture(getPixmap()));
			textureRegionLock.release();
		}
		return region;
	}

	public synchronized void setTextureRegion() {
		textureRegionLock.acquireUninterruptibly();
		Texture texture = new Texture(getPixmap());
		this.region = new TextureRegion(texture);
		textureRegionLock.release();
	}

	public Pixmap getPixmap() {

		if (pixmap == null) {
			pixmapLock.acquireUninterruptibly();
			pixmap = new Pixmap(Gdx.files.absolute(getAbsolutePath()));
			pixmapLock.release();
		}
		return pixmap;
	}

	public synchronized void setPixmap(Pixmap pixmap) {
		pixmapLock.acquireUninterruptibly();
		this.pixmap = pixmap;
		pixmapLock.release();
	}

	public Pixmap getOriginalPixmap() {

		if (originalPixmap == null) {
			originalPixmapLock.acquireUninterruptibly();
			originalPixmap = new Pixmap(Gdx.files.absolute(getAbsolutePath()));
			originalPixmapLock.release();
		}
		return originalPixmap;

	}

	public String getAbsolutePath() {
		if (fileName != null) {
			return Utils.buildPath(getPathToImageDirectory(), fileName);
		} else {
			return null;
		}
	}

	public String getInternalPath() {
		if (fileName != null) {
			return Constants.IMAGE_DIRECTORY + "/" + fileName;
		} else {
			return null;
		}
	}

	public String getCostumeName() {
		return name;
	}

	public void setCostumeName(String name) {
		this.name = name;
	}

	public void setCostumeFilename(String fileName) {
		this.fileName = fileName;
	}

	public String getCostumeFileName() {
		return fileName;
	}

	public String getChecksum() {
		if (fileName == null) {
			return null;
		}
		return fileName.substring(0, 32);
	}

	private String getPathToImageDirectory() {
		return Utils.buildPath(Utils.buildProjectPath(ProjectManager.getInstance().getCurrentProject().getName()),
				Constants.IMAGE_DIRECTORY);
	}

	public Bitmap getThumbnailBitmap() {
		if (thumbnailBitmap == null) {
			thumbnailBitmap = ImageEditing.getScaledBitmapFromPath(getAbsolutePath(), THUMBNAIL_HEIGHT,
					THUMBNAIL_WIDTH, false);
		}
		return thumbnailBitmap;
	}

	public void resetThumbnailBitmap() {
		thumbnailBitmap = null;
	}

	public int[] getResolution() {
		if (width != null && height != null) {
			return new int[] { width, height };
		}
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(getAbsolutePath(), options);
		width = options.outWidth;
		height = options.outHeight;

		return new int[] { width, height };
	}

	@Override
	public String toString() {
		return name;
	}
}

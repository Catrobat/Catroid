/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010  Catroid development team 
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package at.tugraz.ist.catroid.stage;

import java.util.Collection;
import java.util.HashMap;
import java.util.Vector;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * @author jib218
 * 
 */
public class TextureHandler {
	private static TextureHandler textureHandler;
	private Vector<String> imagePaths = new Vector<String>();
	private HashMap<String, TextureRegion> textureMap = new HashMap<String, TextureRegion>();

	private TextureHandler() {
	}

	public synchronized static TextureHandler getInstance() {
		if (textureHandler == null) {
			textureHandler = new TextureHandler();
		}
		return textureHandler;
	}

	public synchronized void addImage(String absoluteImagePath) {
		if (!imagePaths.contains(absoluteImagePath)) {
			imagePaths.add(absoluteImagePath);
		}
	}

	public synchronized void deleteImage(String absoluteImagePath) {
		if (imagePaths.contains(absoluteImagePath)) {
			imagePaths.remove(absoluteImagePath);
		}
	}

	public synchronized void loadTextures() {
		for (String imagePath : imagePaths) {
			Texture texture = new Texture(Gdx.files.absolute(imagePath));
			textureMap.put(imagePath, new TextureRegion(texture));
		}
	}

	public TextureRegion getTexture(String absoluteImagePath) {
		return textureMap.get(absoluteImagePath);
	}

	public synchronized void clear() {
		imagePaths.clear();
		Collection<TextureRegion> textures = textureMap.values();
		for (TextureRegion texture : textures) {
			texture.getTexture().dispose();
		}
		textureMap.clear();
	}
}

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
package at.tugraz.ist.catroid.common;

import java.util.Collection;
import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * @author Johannes Iber
 * 
 */
public class TextureRegionContainer {
	private class TextureInfo {
		public int usage = 1;
		public TextureRegion textureRegion = null;

		public TextureInfo(TextureRegion textureRegion) {
			this.textureRegion = textureRegion;
		}
	}

	public TextureRegionContainer() {
	}

	private HashMap<String, TextureInfo> textureInfoMap = new HashMap<String, TextureInfo>();

	public synchronized TextureRegion getTextureRegion(String currentAbsolutePath, String newAbsolutePath) {
		if (!currentAbsolutePath.equals("") && textureInfoMap.containsKey(currentAbsolutePath)) {
			TextureInfo textureInfo = textureInfoMap.get(currentAbsolutePath);
			textureInfo.usage--;
			if (textureInfo.usage < 1) {
				if (textureInfo.textureRegion != null && textureInfo.textureRegion.getTexture() != null) {
					textureInfo.textureRegion.getTexture().dispose();
				}
				textureInfoMap.remove(currentAbsolutePath);
			}
		}
		if (newAbsolutePath.equals("")) {
			return new TextureRegion();
		}
		if (textureInfoMap.containsKey(newAbsolutePath)) {
			TextureInfo textureInfo = textureInfoMap.get(newAbsolutePath);
			textureInfo.usage++;
			return textureInfo.textureRegion;
		} else {
			Texture tex = new Texture(Gdx.files.absolute(newAbsolutePath));
			TextureRegion textureRegion = new TextureRegion(tex);
			textureInfoMap.put(newAbsolutePath, new TextureInfo(textureRegion));
			return textureRegion;
		}
	}

	public synchronized void clear() {
		Collection<TextureInfo> textures = textureInfoMap.values();
		for (TextureInfo texture : textures) {
			texture.textureRegion.getTexture().dispose();
		}
		textureInfoMap.clear();
	}
}

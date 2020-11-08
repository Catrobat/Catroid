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

import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.converters.reflection.ReflectionConverter;
import com.thoughtworks.xstream.converters.reflection.ReflectionProvider;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.Mapper;

import org.catrobat.catroid.content.Sprite;

public class XStreamSpriteConverter extends ReflectionConverter {

	private static final String TAG = XStreamSpriteConverter.class.getSimpleName();
	private static final String SPRITES_PACKAGE_NAME = "org.catrobat.catroid.content";
	private static final String TYPE = "type";

	public XStreamSpriteConverter(Mapper mapper, ReflectionProvider reflectionProvider) {
		super(mapper, reflectionProvider);
	}

	@Override
	public boolean canConvert(Class type) {
		return Sprite.class.isAssignableFrom(type);
	}

	@Override
	protected void doMarshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
		writer.addAttribute(TYPE, source.getClass().getSimpleName());
		super.doMarshal(source, writer, context);
	}

	@Override
	public Object doUnmarshal(Object result, HierarchicalStreamReader reader, UnmarshallingContext context) {
		String type = reader.getAttribute(TYPE);
		if (type != null) {
			try {
				if (type.equals("SingleSprite")) {
					type = "Sprite";
				}
				Class cls = Class.forName(SPRITES_PACKAGE_NAME + "." + type);
				Sprite sprite = (Sprite) reflectionProvider.newInstance(cls);
				return super.doUnmarshal(sprite, reader, context);
			} catch (ClassNotFoundException exception) {
				Log.e(TAG, "Sprite class not found : " + result.toString(), exception);
			}
		} else {
			Sprite sprite = (Sprite) reflectionProvider.newInstance(Sprite.class);
			return super.doUnmarshal(sprite, reader, context);
		}
		return super.doUnmarshal(result, reader, context);
	}
}

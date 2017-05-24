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
package org.catrobat.catroid.io;

import android.util.Log;

import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.converters.reflection.ReflectionConverter;
import com.thoughtworks.xstream.converters.reflection.ReflectionProvider;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.Mapper;

import org.catrobat.catroid.content.bricks.Brick;

public class XStreamBrickConverter extends ReflectionConverter {

	private static final String TAG = XStreamBrickConverter.class.getSimpleName();
	private static final String[] BRICKS_PACKAGE_NAMES = {"org.catrobat.catroid.content.bricks",
			"org.catrobat.catroid.physics.content.bricks"};
	private static final String TYPE = "type";

	public XStreamBrickConverter(Mapper mapper, ReflectionProvider reflectionProvider) {
		super(mapper, reflectionProvider);
	}

	@Override
	public boolean canConvert(Class type) {
		return Brick.class.isAssignableFrom(type);
	}

	@Override
	protected void doMarshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
		writer.addAttribute(TYPE, source.getClass().getSimpleName());
		super.doMarshal(source, writer, context);
	}

	@Override
	public Object doUnmarshal(Object result, HierarchicalStreamReader reader, UnmarshallingContext context) {

		String type = reader.getAttribute(TYPE);
		for (int index = 0; index < BRICKS_PACKAGE_NAMES.length; index++) {
			try {
				Class cls = Class.forName(BRICKS_PACKAGE_NAMES[index] + "." + type);
				Brick brick = (Brick) reflectionProvider.newInstance(cls);
				return super.doUnmarshal(brick, reader, context);
			} catch (ClassNotFoundException exception) {
				Log.d(TAG, "Brick " + type + " not found in " + BRICKS_PACKAGE_NAMES[index]);
			}
		}
		Log.i(TAG, "Brick " + type + " not found in packages");
		return super.doUnmarshal(result, reader, context);
	}
}

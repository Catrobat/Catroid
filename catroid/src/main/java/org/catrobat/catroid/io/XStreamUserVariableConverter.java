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
package org.catrobat.catroid.io;

import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.converters.reflection.ReflectionProvider;
import com.thoughtworks.xstream.converters.reflection.SerializableConverter;
import com.thoughtworks.xstream.core.ClassLoaderReference;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.Mapper;

import org.catrobat.catroid.formulaeditor.UserVariable;

public class XStreamUserVariableConverter extends SerializableConverter {

	private static final String TYPE = "type";

	public XStreamUserVariableConverter(Mapper mapper, ReflectionProvider reflectionProvider, ClassLoaderReference classLoaderReference) {
		super(mapper, reflectionProvider, classLoaderReference);
	}

	@Override
	public boolean canConvert(Class type) {
		return type.equals(UserVariable.class);
	}

	@Override
	public void doMarshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
		writer.addAttribute(TYPE, source.getClass().getSimpleName());
		super.doMarshal(source, writer, context);
	}

	@Override
	public Object doUnmarshal(Object result, HierarchicalStreamReader reader, UnmarshallingContext context) {
		String type = reader.getAttribute(TYPE);
		if (type != null) {
			return super.doUnmarshal(result, reader, context);
		}
		return new UserVariable(reader.getValue());
	}
}

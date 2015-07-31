/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2015 The Catrobat Team
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
package org.catrobat.catroid.test.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public final class Reflection {

	// Suppress default constructor for noninstantiability
	private Reflection() {
		throw new AssertionError();
	}

	public static Object getPrivateField(Object object, String fieldName) {
		if (object == null) {
			throw new IllegalArgumentException("Object is null");
		}

		return Reflection.getPrivateField(object.getClass(), object, fieldName);
	}

	public static Object getPrivateField(Class<?> clazz, String fieldName) {
		return Reflection.getPrivateField(clazz, null, fieldName);
	}

	public static Object getPrivateField(Class<?> clazz, Object object, String fieldName) {
		try {
			Field field = clazz.getDeclaredField(fieldName);
			field.setAccessible(true);
			return field.get(object);
		} catch (Exception exception) {
			throw new RuntimeException(exception);
		}
	}

	public static void setPrivateField(Object object, String fieldName, Object value) {
		if (object == null) {
			throw new IllegalArgumentException("Object is null");
		}

		Reflection.setPrivateField(object.getClass(), object, fieldName, value);
	}

	public static void setPrivateField(Class<?> fieldOwnerType, String fieldName, Object value) {
		Reflection.setPrivateField(fieldOwnerType, null, fieldName, value);
	}

	public static void setPrivateField(Class<?> fieldOwnerType, Object object, String fieldName, Object value) {
		try {
			Field field = fieldOwnerType.getDeclaredField(fieldName);
			field.setAccessible(true);
			field.set(object, value);
		} catch (Exception exception) {
			throw new RuntimeException(exception);
		}
	}

	public static class Parameter {
		private final Class<?> type;
		private final Object value;

		public Parameter(Object value) {
			this(value.getClass(), value);
		}

		public Parameter(Class<?> type, Object value) {
			this.type = type;
			this.value = value;
		}
	}

	public static class ParameterList {
		private Class<?>[] types;
		private Object[] values;

		public ParameterList(Object... parameters) {
			Parameter[] parameterList = new Parameter[parameters.length];
			for (int index = 0; index < parameters.length; index++) {
				Object parameter = parameters[index];

				if (parameter == null) {
					throw new RuntimeException("Parameter " + index + " is null");
				} else if (parameter instanceof Parameter) {
					parameterList[index] = (Parameter) parameter;
				} else {
					parameterList[index] = new Parameter(getParameterType(parameter), parameter);
				}
			}
			splitParametersToTypesAndValues(parameterList);
		}

		private Class<?> getParameterType(Object object) {
			Class<?> objectClass = object.getClass();

			if (objectClass == Boolean.class) {
				objectClass = boolean.class;
			} else if (objectClass == Byte.class) {
				objectClass = byte.class;
			} else if (objectClass == Character.class) {
				objectClass = char.class;
			} else if (objectClass == Double.class) {
				objectClass = double.class;
			} else if (objectClass == Float.class) {
				objectClass = float.class;
			} else if (objectClass == Integer.class) {
				objectClass = int.class;
			} else if (objectClass == Long.class) {
				objectClass = long.class;
			} else if (objectClass == Short.class) {
				objectClass = short.class;
			}

			return objectClass;
		}

		public ParameterList(Parameter... parameters) {
			splitParametersToTypesAndValues(parameters);
		}

		private void splitParametersToTypesAndValues(Parameter... parameters) {
			this.types = new Class<?>[parameters.length];
			this.values = new Object[parameters.length];

			for (int index = 0; index < values.length; index++) {
				Parameter parameter = parameters[index];
				this.types[index] = parameter.type;
				this.values[index] = parameter.value;
			}
		}
	}

	public static Object invokeMethod(Object object, String methodName) {
		return invokeMethod(object, methodName, new ParameterList());
	}

	public static Object invokeMethod(Object object, String methodName, ParameterList parameterList) {
		if (object == null) {
			throw new IllegalArgumentException("Object is null");
		}

		return Reflection.invokeMethod(object.getClass(), object, methodName, parameterList);
	}

	public static Object invokeMethod(Class<?> clazz, String methodName) {
		return invokeMethod(clazz, methodName, new ParameterList());
	}

	public static Object invokeMethod(Class<?> clazz, String methodName, ParameterList parameterList) {
		return Reflection.invokeMethod(clazz, null, methodName, parameterList);
	}

	public static Object invokeMethod(Class<?> clazz, Object object, String methodName) {
		return invokeMethod(clazz, object, methodName, new ParameterList());
	}

	public static Object invokeMethod(Class<?> clazz, Object object, String methodName, ParameterList parameterList) {
		try {
			Method method = clazz.getDeclaredMethod(methodName, parameterList.types);
			method.setAccessible(true);
			return method.invoke(object, parameterList.values);
		} catch (Exception exception) {
			throw new RuntimeException(exception);
		}
	}
}

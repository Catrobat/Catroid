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
package org.catrobat.catroid.test.utiltests.reflection;

import org.catrobat.catroid.test.utils.Reflection;
import org.catrobat.catroid.test.utils.Reflection.Parameter;
import org.catrobat.catroid.test.utils.Reflection.ParameterList;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class ReflectionTest {

	@Test
	public void testPrivateFieldGettersAndSetters() throws Exception {
		char secretChar = (Character) Reflection.getPrivateField(SubClass.class, "SECRET_STATIC_CHAR");
		assertEquals(SubClass.SECRET_STATIC_CHAR, secretChar);

		String secretString = (String) Reflection.getPrivateField(new SubClass(), "secretString");
		assertEquals(new SubClass().secretString, secretString);

		int secretInteger = (Integer) Reflection.getPrivateField(new SubClass(), "SECRET_INTEGER");
		assertEquals(new SubClass().SECRET_INTEGER, secretInteger);

		float secretFloat = (Float) Reflection.getPrivateField(SuperClass.class, new SubClass(), "SECRET_FLOAT");
		assertEquals(new SuperClass().SECRET_FLOAT, secretFloat);

		byte secretByte = (Byte) Reflection.getPrivateField(new SuperClass(), "secretByte");
		assertEquals(new SuperClass().secretByte, secretByte);

		char newSecretChar = 'n';
		Reflection.setPrivateField(SubClass.class, "SECRET_STATIC_CHAR", newSecretChar);
		secretChar = (Character) Reflection.getPrivateField(SubClass.class, "SECRET_STATIC_CHAR");
		assertEquals(newSecretChar, secretChar);

		SubClass sub = new SubClass();
		String newSecretString = "This is a new secret string!";
		Reflection.setPrivateField(sub, "secretString", newSecretString);
		secretString = (String) Reflection.getPrivateField(sub, "secretString");
		assertEquals(newSecretString, secretString);

		int newSecretInteger = 128;
		Reflection.setPrivateField(sub, "SECRET_INTEGER", newSecretInteger);
		secretInteger = (Integer) Reflection.getPrivateField(sub, "SECRET_INTEGER");
		assertEquals(newSecretInteger, secretInteger);

		float newSecretFloat = -5.4f;
		Reflection.setPrivateField(SuperClass.class, sub, "SECRET_FLOAT", newSecretFloat);
		secretFloat = (Float) Reflection.getPrivateField(SuperClass.class, sub, "SECRET_FLOAT");
		assertEquals(newSecretFloat, secretFloat);
	}

	@Test
	public void testInvokeMethodForObjects() throws Exception {
		InvokeMethodClass invokeMethodObject = new InvokeMethodClass();

		String returnValue = (String) Reflection.invokeMethod(invokeMethodObject, "methodWithoutParameters");
		assertEquals("Called methodWithoutParameters!", returnValue);

		String parameter1 = "first parameter";
		String parameter2 = "second parameter";
		returnValue = (String) Reflection.invokeMethod(invokeMethodObject, "methodWithParameters", new ParameterList(
				parameter1, parameter2));
		assertEquals(parameter1 + parameter2, returnValue);

		returnValue = (String) Reflection.invokeMethod(invokeMethodObject, "methodWithParameters", new ParameterList(
				new Parameter(String.class, null), parameter2));
		assertEquals(null + parameter2, returnValue);

		InvokeMethodClass.calledVoidMethod = false;
		Object voidReturnValue = Reflection.invokeMethod(invokeMethodObject, "voidMethod");
		assertTrue(InvokeMethodClass.calledVoidMethod);
		assertNull(voidReturnValue);

		String superClassMethodReturnValue = (String) Reflection.invokeMethod(Object.class, invokeMethodObject,
				"toString");
		assertNotNull(superClassMethodReturnValue);
	}

	@Test
	public void testInvokeMethodForClasses() throws Exception {
		String returnValue = (String) Reflection.invokeMethod(InvokeMethodClass.class, "staticMethodWithoutParameters");
		assertEquals("Called staticMethodWithoutParameters!", returnValue);

		String parameter1 = "first parameter";
		String parameter2 = "second parameter";
		returnValue = (String) Reflection.invokeMethod(InvokeMethodClass.class, "staticMethodWithParameters",
				new ParameterList(parameter1, parameter2));
		assertEquals(parameter1 + parameter2, returnValue);

		parameter1 = null;
		returnValue = (String) Reflection.invokeMethod(InvokeMethodClass.class, "staticMethodWithParameters",
				new ParameterList(new Parameter(String.class, parameter1), parameter2));
		assertEquals(parameter1 + parameter2, returnValue);

		InvokeMethodClass.calledVoidMethod = false;
		Object voidReturnValue = Reflection.invokeMethod(InvokeMethodClass.class, "staticVoidMethod");
		assertTrue(InvokeMethodClass.calledVoidMethod);
		assertNull(voidReturnValue);
	}

	@Test
	public void testInvokeMethodWithAutoBoxingParameter() throws Exception {
		InvokeMethodClass invokeMethodObject = new InvokeMethodClass();

		float returnValue = (Float) Reflection.invokeMethod(invokeMethodObject, "methodWithPrimitiveParameter",
				new ParameterList(3.14f));
		assertEquals(returnValue, 1.0f);

		Float floatObject = Float.valueOf(1.234f);
		returnValue = (Float) Reflection.invokeMethod(invokeMethodObject, "methodWithPrimitiveParameter",
				new ParameterList(floatObject));
		assertEquals(returnValue, 1.0f);

		returnValue = (Float) Reflection.invokeMethod(invokeMethodObject, "methodWithWrappedPrimitiveParameter",
				new ParameterList(new Parameter(Float.class, Float.valueOf(3.14f))));
		assertEquals(returnValue, -1.0f);
	}

	@Test
	public void testConvertObjectsIntoPrimitives() throws Exception {
		ParameterList parameterList = new ParameterList(Boolean.TRUE, Byte.valueOf((byte) 1),
				Character.valueOf('c'), Double.valueOf(1.0), Float.valueOf(1.0f), Integer.valueOf(1), Long.valueOf(1L),
				Short.valueOf((short) 1));

		Class<?>[] primitiveObjectsClass = (Class<?>[]) Reflection.getPrivateField(parameterList, "types");
		Class<?>[] expectedPrimitiveObjectsClasses = new Class<?>[] {boolean.class, byte.class, char.class,
				double.class, float.class, int.class, long.class, short.class};
		assertTrue(Arrays.deepEquals(expectedPrimitiveObjectsClasses, primitiveObjectsClass));
	}

	private static class InvokeMethodClass {
		private static boolean calledVoidMethod = false;

		@SuppressWarnings("unused")
		private static String staticMethodWithoutParameters() {
			return "Called staticMethodWithoutParameters!";
		}

		@SuppressWarnings("unused")
		private static String staticMethodWithParameters(String param1, String param2) {
			return param1 + param2;
		}

		@SuppressWarnings("unused")
		private static void staticVoidMethod() {
			calledVoidMethod = true;
		}

		@SuppressWarnings("unused")
		private String methodWithoutParameters() {
			return "Called methodWithoutParameters!";
		}

		@SuppressWarnings("unused")
		private String methodWithParameters(String param1, String param2) {
			return param1 + param2;
		}

		@SuppressWarnings("unused")
		private void voidMethod() {
			calledVoidMethod = true;
		}

		@SuppressWarnings("unused")
		private float methodWithPrimitiveParameter(float parameter) {
			return 1.0f;
		}

		@SuppressWarnings("unused")
		private float methodWithWrappedPrimitiveParameter(Float parameter) {
			return -1.0f;
		}
	}

	private class SuperClass {
		// CHECKSTYLE DISABLE MemberNameCheck FOR 2 LINES
		private final float SECRET_FLOAT = 3.1415f;
		protected byte secretByte = 32;
	}

	private class SubClass extends SuperClass {
		// CHECKSTYLE DISABLE MemberNameCheck FOR 3 LINES
		private static final char SECRET_STATIC_CHAR = 'c';
		private final int SECRET_INTEGER = 42;
		private String secretString = "This is a secret string!";
	}
}

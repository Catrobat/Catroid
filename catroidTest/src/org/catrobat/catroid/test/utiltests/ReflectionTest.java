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
package org.catrobat.catroid.test.utiltests;

import android.test.AndroidTestCase;

import org.catrobat.catroid.test.utils.Reflection;
import org.catrobat.catroid.test.utils.Reflection.Parameter;
import org.catrobat.catroid.test.utils.Reflection.ParameterList;

import java.util.Arrays;

public class ReflectionTest extends AndroidTestCase {

	public void testPrivateFieldGettersAndSetters() {
		char secretChar = (Character) Reflection.getPrivateField(SubClass.class, "SECRET_STATIC_CHAR");
		assertEquals("Getting private static field failed!", SubClass.SECRET_STATIC_CHAR, secretChar);

		String secretString = (String) Reflection.getPrivateField(new SubClass(), "secretString");
		assertEquals("Getting private String failed!", new SubClass().secretString, secretString);

		int secretInteger = (Integer) Reflection.getPrivateField(new SubClass(), "SECRET_INTEGER");
		assertEquals("Getting private Integer failed!", new SubClass().SECRET_INTEGER, secretInteger);

		float secretFloat = (Float) Reflection.getPrivateField(SuperClass.class, new SubClass(), "SECRET_FLOAT");
		assertEquals("Getting private Float from super class failed!", new SuperClass().SECRET_FLOAT, secretFloat);

		byte secretByte = (Byte) Reflection.getPrivateField(new SuperClass(), "secretByte");
		assertEquals("Getting private Float from super class failed!", new SuperClass().secretByte, secretByte);

		char newSecretChar = 'n';
		Reflection.setPrivateField(SubClass.class, "SECRET_STATIC_CHAR", newSecretChar);
		secretChar = (Character) Reflection.getPrivateField(SubClass.class, "SECRET_STATIC_CHAR");
		assertEquals("Setting private static field failed!", newSecretChar, secretChar);

		SubClass sub = new SubClass();
		String newSecretString = "This is a new secret string!";
		Reflection.setPrivateField(sub, "secretString", newSecretString);
		secretString = (String) Reflection.getPrivateField(sub, "secretString");
		assertEquals("Setting private String failed!", newSecretString, secretString);

		int newSecretInteger = 128;
		Reflection.setPrivateField(sub, "SECRET_INTEGER", newSecretInteger);
		secretInteger = (Integer) Reflection.getPrivateField(sub, "SECRET_INTEGER");
		assertEquals("Setting private Integer failed!", newSecretInteger, secretInteger);

		float newSecretFloat = -5.4f;
		Reflection.setPrivateField(SuperClass.class, sub, "SECRET_FLOAT", newSecretFloat);
		secretFloat = (Float) Reflection.getPrivateField(SuperClass.class, sub, "SECRET_FLOAT");
		assertEquals("Setting private Float from super class failed!", newSecretFloat, secretFloat);
	}

	public void testPrivateFieldWithNullObject() {
		Object nullObject = null;
		try {
			Reflection.getPrivateField(nullObject, "nullObjectsDontHaveFields");
			fail("Getting private field of null object didn't cause an IllegalArgumentException");
		} catch (IllegalArgumentException expected) {
		}

		try {
			Reflection.getPrivateField(SubClass.class, nullObject, "SECRET_INTEGER");
			fail("Getting private field of null object didn't cause an IllegalArgumentException");
		} catch (Exception exception) {
			assertEquals("Wrong exception has been thrown", exception.getCause().getClass(), NullPointerException.class);
		}

		try {
			Reflection.setPrivateField(nullObject, "nullObjectsDontHaveFields", null);
			fail("Setting private field of null object didn't cause an IllegalArgumentException");
		} catch (IllegalArgumentException expected) {
		}

		try {
			Reflection.setPrivateField(SubClass.class, nullObject, "SECRET_INTEGER", 123);
			fail("Setting private field of null object didn't cause an IllegalArgumentException");
		} catch (Exception exception) {
			assertEquals("Wrong exception has been thrown", exception.getCause().getClass(), NullPointerException.class);
		}
	}

	public void testPrivateFieldWithWrongParameters() {
		try {
			Reflection.getPrivateField(SuperClass.class, new SubClass(), "secretString");
			fail("Secret string is only located in SubClass but also found in SuperClass");
		} catch (RuntimeException runtimeException) {
			assertEquals("Wrong exception has been thrown", runtimeException.getCause().getClass(),
					NoSuchFieldException.class);
		}

		try {
			Reflection.getPrivateField(SubClass.class, new SuperClass(), "secretString");
			fail("SuperClass object isn't a sub class of SubClass");
		} catch (RuntimeException runtimeException) {
			assertEquals("Wrong exception has been thrown", runtimeException.getCause().getClass(),
					IllegalArgumentException.class);
		}

		try {
			Reflection.getPrivateField(SubClass.class, null, "secretString");
			fail("SubClass has a static member 'secretString'");
		} catch (RuntimeException runtimeException) {
			assertEquals("Wrong exception has been thrown", runtimeException.getCause().getClass(),
					NullPointerException.class);
		}

		try {
			Reflection.setPrivateField(SuperClass.class, new SubClass(), "secretString", "Secret string");
			fail("Secret string is only located in SubClass but also found in SuperClass");
		} catch (RuntimeException runtimeException) {
			assertEquals("Wrong exception has been thrown", runtimeException.getCause().getClass(),
					NoSuchFieldException.class);
		}

		try {
			Reflection.setPrivateField(SubClass.class, new SuperClass(), "secretString", "Secret string");
			fail("SuperClass object is a sub class of SubClass but shouldn't");
		} catch (RuntimeException runtimeException) {
			assertEquals("Wrong exception has been thrown", runtimeException.getCause().getClass(),
					IllegalArgumentException.class);
		}

		try {
			Reflection.setPrivateField(SubClass.class, null, "secretString", "Secret string");
			fail("SubClass has a static member 'secretString'");
		} catch (RuntimeException runtimeException) {
			assertEquals("Wrong exception has been thrown", runtimeException.getCause().getClass(),
					NullPointerException.class);
		}
	}

	public void testInvokeMethodForObjects() {
		InvokeMethodClass invokeMethodObject = new InvokeMethodClass();

		String returnValue = (String) Reflection.invokeMethod(invokeMethodObject, "methodWithoutParameters");
		assertEquals("Wrong return value", "Called methodWithoutParameters!", returnValue);

		String parameter1 = "first parameter";
		String parameter2 = "second parameter";
		returnValue = (String) Reflection.invokeMethod(invokeMethodObject, "methodWithParameters", new ParameterList(
				parameter1, parameter2));
		assertEquals("Wrong return value", parameter1 + parameter2, returnValue);

		returnValue = (String) Reflection.invokeMethod(invokeMethodObject, "methodWithParameters", new ParameterList(
				new Parameter(String.class, null), parameter2));
		assertEquals("Wrong return value", null + parameter2, returnValue);

		InvokeMethodClass.calledVoidMethod = false;
		Object voidReturnValue = Reflection.invokeMethod(invokeMethodObject, "voidMethod");
		assertTrue("Void method hasn't been called", InvokeMethodClass.calledVoidMethod);
		assertNull("Void method returned a non-null value", voidReturnValue);

		String superClassMethodReturnValue = (String) Reflection.invokeMethod(Object.class, invokeMethodObject,
				"toString");
		assertNotNull("toString method returned null", superClassMethodReturnValue);
	}

	public void testInvokeMethodForClasses() {
		String returnValue = (String) Reflection.invokeMethod(InvokeMethodClass.class, "staticMethodWithoutParameters");
		assertEquals("Wrong return value", "Called staticMethodWithoutParameters!", returnValue);

		String parameter1 = "first parameter";
		String parameter2 = "second parameter";
		returnValue = (String) Reflection.invokeMethod(InvokeMethodClass.class, "staticMethodWithParameters",
				new ParameterList(parameter1, parameter2));
		assertEquals("Wrong return value", parameter1 + parameter2, returnValue);

		parameter1 = null;
		returnValue = (String) Reflection.invokeMethod(InvokeMethodClass.class, "staticMethodWithParameters",
				new ParameterList(new Parameter(String.class, parameter1), parameter2));
		assertEquals("Wrong return value", parameter1 + parameter2, returnValue);

		InvokeMethodClass.calledVoidMethod = false;
		Object voidReturnValue = Reflection.invokeMethod(InvokeMethodClass.class, "staticVoidMethod");
		assertTrue("Void method hasn't been called", InvokeMethodClass.calledVoidMethod);
		assertNull("Void method returned a non-null value", voidReturnValue);
	}

	public void testInvokeMethodWithAutoBoxingParameter() {
		InvokeMethodClass invokeMethodObject = new InvokeMethodClass();

		float returnValue = (Float) Reflection.invokeMethod(invokeMethodObject, "methodWithPrimitiveParameter",
				new ParameterList(3.14f));
		assertEquals("Method with primitive float parameter hasn't been called", returnValue, 1.0f);

		Float floatObject = Float.valueOf(1.234f);
		returnValue = (Float) Reflection.invokeMethod(invokeMethodObject, "methodWithPrimitiveParameter",
				new ParameterList(floatObject));
		assertEquals("Method with float object parameter hasn't been converted into primitive", returnValue, 1.0f);

		returnValue = (Float) Reflection.invokeMethod(invokeMethodObject, "methodWithWrappedPrimitiveParameter",
				new ParameterList(new Parameter(Float.class, Float.valueOf(3.14f))));
		assertEquals("Method with float object parameter hasn't been called", returnValue, -1.0f);
	}

	public void testConvertObjectsIntoPrimitives() {
		ParameterList parameterList = new ParameterList(Boolean.TRUE, Byte.valueOf((byte) 1),
				Character.valueOf('c'), Double.valueOf(1.0), Float.valueOf(1.0f), Integer.valueOf(1), Long.valueOf(1L),
				Short.valueOf((short) 1));

		Class<?>[] primitiveObjectsClass = (Class<?>[]) Reflection.getPrivateField(parameterList, "types");
		Class<?>[] expectedPrimitiveObjectsClasses = new Class<?>[] { boolean.class, byte.class, char.class,
				double.class, float.class, int.class, long.class, short.class };
		assertTrue("Not all object classes are converted into primitve classes",
				Arrays.deepEquals(expectedPrimitiveObjectsClasses, primitiveObjectsClass));
	}

	public void testInvokeMethodWithNullObject() {
		Object nullObject = null;
		try {
			Reflection.invokeMethod(nullObject, "nullObjectsDontHaveMethods");
			fail("Invoking method of a null object didn't cause an IllegalArgumentException");
		} catch (IllegalArgumentException expected) {
		}

		try {
			Reflection.invokeMethod(nullObject, "nullObjectsDontHaveMethods", new ParameterList("text"));
			fail("Invoking method of a null object didn't cause an IllegalArgumentException");
		} catch (IllegalArgumentException expected) {
		}

		try {
			Reflection.invokeMethod(InvokeMethodClass.class, nullObject, "voidMethod");
			fail("Invoking method of a null object didn't cause an IllegalArgumentException");
		} catch (RuntimeException runtimeException) {
			assertEquals("Wrong exception has been thrown", runtimeException.getCause().getClass(),
					NullPointerException.class);
		}
	}

	public void testInvokeMethodWithWrongParameters() {
		InvokeMethodClass invokeMethodObject = new InvokeMethodClass();

		try {
			Reflection.invokeMethod(String.class, Integer.valueOf(1), "toString");
			fail("Integer is a sub class of String");
		} catch (RuntimeException runtimeException) {
			assertEquals("Wrong exception has been thrown", runtimeException.getCause().getClass(),
					IllegalArgumentException.class);
		}

		String parameter1 = "String";
		String parameter2 = null;
		try {
			Reflection.invokeMethod(invokeMethodObject, "methodWithParameters", new ParameterList(parameter1,
					parameter2));
			fail("Found not existing method signature");
		} catch (RuntimeException expected) {
		}
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

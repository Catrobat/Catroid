/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2013 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://developer.catrobat.org/license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *  
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.test.utiltests;

import java.util.Arrays;

import org.catrobat.catroid.test.utils.Reflection;

import android.test.AndroidTestCase;

public class ReflectionTest extends AndroidTestCase {

	public void testPrivateFieldUtils() {
		char secretChar = (Character) Reflection.getPrivateField(SubClass.class, "SECRET_STATIC_CHAR");
		assertEquals("Getting private static field failed!", 'c', secretChar);

		String secretString = (String) Reflection.getPrivateField(new SubClass(), "SECRET_STRING");
		assertEquals("Getting private String failed!", "This is a secret string!", secretString);

		int secretInteger = (Integer) Reflection.getPrivateField(new SubClass(), "SECRET_INTEGER");
		assertEquals("Getting private Integer failed!", 42, secretInteger);

		float secretFloat = (Float) Reflection.getPrivateField(SuperClass.class, new SubClass(), "SECRET_FLOAT");
		assertEquals("Getting private Float from super class failed!", 3.1415f, secretFloat);

		char newSecretChar = 'n';
		Reflection.setPrivateField(SubClass.class, "SECRET_STATIC_CHAR", newSecretChar);
		secretChar = (Character) Reflection.getPrivateField(SubClass.class, "SECRET_STATIC_CHAR");
		assertEquals("Getting private static field failed!", newSecretChar, secretChar);

		SubClass sub = new SubClass();
		String newSecretString = "This is a new secret string!";
		Reflection.setPrivateField(sub, "SECRET_STRING", newSecretString);
		secretString = (String) Reflection.getPrivateField(sub, "SECRET_STRING");
		assertEquals("Getting private String failed!", newSecretString, secretString);

		int newSecretInteger = 128;
		Reflection.setPrivateField(sub, "SECRET_INTEGER", newSecretInteger);
		secretInteger = (Integer) Reflection.getPrivateField(sub, "SECRET_INTEGER");
		assertEquals("Getting private Integer failed!", newSecretInteger, secretInteger);

		float newSecretFloat = -5.4f;
		Reflection.setPrivateField(SuperClass.class, sub, "SECRET_FLOAT", newSecretFloat);
		secretFloat = (Float) Reflection.getPrivateField(SuperClass.class, sub, "SECRET_FLOAT");
		assertEquals("Getting private Float from super class failed!", newSecretFloat, secretFloat);
	}

	public void testPrivateFieldUtilsWithNullObject() {
		Object nullObject = null;
		try {
			Reflection.getPrivateField(nullObject, "nullObjectsDontHaveFields");
			fail("Getting private field of null object didn't cause an IllegalArgumentException");
		} catch (IllegalArgumentException illegalArgumentException) {
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
		} catch (IllegalArgumentException illegalArgumentException) {
		}

		try {
			Reflection.setPrivateField(SubClass.class, nullObject, "SECRET_INTEGER", 123);
			fail("Setting private field of null object didn't cause an IllegalArgumentException");
		} catch (Exception exception) {
			assertEquals("Wrong exception has been thrown", exception.getCause().getClass(), NullPointerException.class);
		}
	}

	public void testPrivateFieldUtilsWithWrongParameters() {
		try {
			Reflection.getPrivateField(SuperClass.class, new SubClass(), "SECRET_STRING");
			fail("Secret string is only located in SubClass but also found in SuperClass");
		} catch (RuntimeException runtimeException) {
			assertEquals("Wrong exception has been thrown", runtimeException.getCause().getClass(),
					NoSuchFieldException.class);
		}

		try {
			Reflection.getPrivateField(SubClass.class, new SuperClass(), "SECRET_STRING");
			fail("SuperClass object is a sub class of SubClass but shouldn't");
		} catch (RuntimeException runtimeException) {
			assertEquals("Wrong exception has been thrown", runtimeException.getCause().getClass(),
					IllegalArgumentException.class);
		}

		try {
			Reflection.getPrivateField(SubClass.class, null, "SECRET_STRING");
			fail("SubClass has a static member 'SECRET_STRING'");
		} catch (RuntimeException runtimeException) {
			assertEquals("Wrong exception has been thrown", runtimeException.getCause().getClass(),
					NullPointerException.class);
		}

		try {
			Reflection.setPrivateField(SuperClass.class, new SubClass(), "SECRET_STRING", "Secret string");
			fail("Secret string is only located in SubClass but also found in SuperClass");
		} catch (RuntimeException runtimeException) {
			assertEquals("Wrong exception has been thrown", runtimeException.getCause().getClass(),
					NoSuchFieldException.class);
		}

		try {
			Reflection.setPrivateField(SubClass.class, new SuperClass(), "SECRET_STRING", "Secret string");
			fail("SuperClass object is a sub class of SubClass but shouldn't");
		} catch (RuntimeException runtimeException) {
			assertEquals("Wrong exception has been thrown", runtimeException.getCause().getClass(),
					IllegalArgumentException.class);
		}

		try {
			Reflection.setPrivateField(SubClass.class, null, "SECRET_STRING", "Secret string");
			fail("SubClass has a static member 'SECRET_STRING'");
		} catch (RuntimeException runtimeException) {
			assertEquals("Wrong exception has been thrown", runtimeException.getCause().getClass(),
					NullPointerException.class);
		}
	}

	private class SuperClass {
		@SuppressWarnings("unused")
		private final float SECRET_FLOAT = 3.1415f;
	}

	private class SubClass extends SuperClass {
		@SuppressWarnings("unused")
		private final static char SECRET_STATIC_CHAR = 'c';
		@SuppressWarnings("unused")
		private final int SECRET_INTEGER = 42;
		@SuppressWarnings("unused")
		private String SECRET_STRING = "This is a secret string!";
	}

	public void testInvokeMethodForObjects() {
		String returnValue = (String) Reflection.invokeMethod(new InvokeMethodTestClass(),
				"testMethodWithoutParameters");
		assertEquals("Calling private method without parameters failed!", "Called testMethodWithoutParameters!",
				returnValue);

		String parameter1 = "first parameter";
		String parameter2 = "second parameter";
		returnValue = (String) Reflection.invokeMethod(new InvokeMethodTestClass(), "testMethodWithParameters",
				parameter1, parameter2);
		assertEquals("Wrong return value", parameter1 + parameter2, returnValue);

		parameter1 = "New first parameter";
		returnValue = (String) Reflection.invokeMethod(new InvokeMethodTestClass(), "testMethodWithParameters",
				new Class<?>[] { String.class, String.class }, new Object[] { parameter1, parameter2 });
		assertEquals("Wrong return value", parameter1 + parameter2, returnValue);

		InvokeMethodTestClass testClass = new InvokeMethodTestClass();
		assertFalse("Already called void method", testClass.calledVoidMethod);
		returnValue = (String) Reflection.invokeMethod(testClass, "testVoidMethod");
		assertTrue("Void method hasn't been called", testClass.calledVoidMethod);
		assertNull("Void method returned a non-null value", returnValue);
	}

	public void testInvokeMethodForClasses() {
		String returnValue = (String) Reflection.invokeMethod(new InvokeMethodTestClass(),
				"testStaticMethodWithoutParameters");
		assertEquals("Calling private static method without parameters failed!",
				"Called testStaticMethodWithoutParameters!", returnValue);

		String parameter1 = "First parameter for static method";
		String parameter2 = "Second parameter for static mehtod";
		returnValue = (String) Reflection.invokeMethod(new InvokeMethodTestClass(), "testStaticMethodWithParameters",
				parameter1, parameter2);
		assertEquals("Wrong return value", parameter1 + parameter2, returnValue);

		parameter1 = "New first parameter for another static method";
		returnValue = (String) Reflection.invokeMethod(new InvokeMethodTestClass(), "testStaticMethodWithParameters",
				new Class<?>[] { String.class, String.class }, new Object[] { parameter1, parameter2 });
		assertEquals("Wrong return value", parameter1 + parameter2, returnValue);
	}

	public void testInvokeMethodForPrimitivesVersusNonPrimitves() {
		InvokeMethodTestClass testClass = new InvokeMethodTestClass();
		float returnFloat = (Float) Reflection.invokeMethod(testClass, "testPrimitiveParameterMethod", 0.0f);
		assertEquals("Method with primitive float parameter hasn't been called", returnFloat, 1.0f);

		Class<?>[] arguments = new Class[] { Float.class };
		Object[] parameters = new Object[] { new Float(0.0f) };
		returnFloat = (Float) Reflection.invokeMethod(testClass, "testObjectParameterMethod", arguments, parameters);
		assertEquals("Method with float object parameter hasn't been called", returnFloat, -1.0f);
	}

	public void testConvertObjectsIntoPrimitives() {
		Object[] primitiveObjects = new Object[] { new Boolean(true), new Byte((byte) 1), new Character('c'),
				new Double(1.0), new Float(1.0f), new Integer(1), new Long(1l), new Short((short) 1) };

		Class<?>[] primitiveObjectsClass = Reflection.getParameterTypes(primitiveObjects);
		Class<?>[] expectedPrimitiveObjectsClasses = new Class<?>[] { boolean.class, byte.class, char.class,
				double.class, float.class, int.class, long.class, short.class };
		assertTrue("Not all object classes are converted into primitve classes",
				Arrays.deepEquals(expectedPrimitiveObjectsClasses, primitiveObjectsClass));
	}

	public void testInvokeMethodWithNullObject() {
		Object nullObject = null;
		try {
			Reflection.invokeMethod(nullObject, "nullObjectsDontHaveMethods");
			fail("Invoking method of an null object didn't cause an IllegalArgumentException");
		} catch (IllegalArgumentException illegalArgumentException) {
		}

		try {
			Reflection.invokeMethod(nullObject, "nullObjectsDontHaveMethods", new Class<?>[] { Object.class },
					new Object[] { new Object() });
			fail("Invoking method of an null object didn't cause an IllegalArgumentException");
		} catch (IllegalArgumentException illegalArgumentException) {
		}

		try {
			Reflection.invokeMethod(String.class, nullObject, "toString");
			fail("Class string has a static method 'toString' but shouldn't");
		} catch (Exception exception) {
			assertEquals("Wrong exception has been thrown", exception.getCause().getClass(), NullPointerException.class);
		}
	}

	public void testInvokeMethodWithWrongParameters() {
		try {
			Reflection.invokeMethod(String.class, new Integer(1), "toString");
			fail("Integer is an sub class of String");
		} catch (RuntimeException runtimeException) {
			assertEquals("Wrong exception has been thrown", runtimeException.getCause().getClass(),
					IllegalArgumentException.class);
		}
	}

	private static class InvokeMethodTestClass {
		protected boolean calledVoidMethod = false;

		@SuppressWarnings("unused")
		private String testMethodWithoutParameters() {
			return "Called testMethodWithoutParameters!";
		};

		@SuppressWarnings("unused")
		private static String testStaticMethodWithoutParameters() {
			return "Called testStaticMethodWithoutParameters!";
		}

		@SuppressWarnings("unused")
		private String testMethodWithParameters(String param1, String param2) {
			return param1 + param2;
		};

		@SuppressWarnings("unused")
		private static String testStaticMethodWithParameters(String param1, String param2) {
			return param1 + param2;
		}

		@SuppressWarnings("unused")
		private void testVoidMethod() {
			calledVoidMethod = true;
		}

		@SuppressWarnings("unused")
		private float testPrimitiveParameterMethod(float primitiveParameter) {
			return 1.0f;
		}

		@SuppressWarnings("unused")
		private float testObjectParameterMethod(Float primitiveParameter) {
			return -1.0f;
		}
	}
}

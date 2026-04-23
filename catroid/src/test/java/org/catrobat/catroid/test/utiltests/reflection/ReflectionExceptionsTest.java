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
import org.catrobat.catroid.test.utils.Reflection.ParameterList;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class ReflectionExceptionsTest {

	@Rule
	public final ExpectedException exception = ExpectedException.none();

	@Test
	public void testInvokeMethodWithNullObject1() throws Exception {
		Object nullObject = null;
		exception.expect(IllegalArgumentException.class);
		Reflection.invokeMethod(nullObject, "nullObjectsDontHaveMethods");
	}

	@Test
	public void testInvokeMethodWithNullObject2() throws Exception {
		Object nullObject = null;
		exception.expect(IllegalArgumentException.class);
		Reflection.invokeMethod(nullObject, "nullObjectsDontHaveMethods", new ParameterList("text"));
	}

	@Test
	public void testInvokeMethodWithNullObject3() throws Exception {
		Object nullObject = null;
		exception.expect(NullPointerException.class);
		Reflection.invokeMethod(InvokeMethodClass.class, nullObject, "voidMethod");
	}

	@Test
	public void testInvokeMethodWithWrongParameters1() throws Exception {
		exception.expect(IllegalArgumentException.class);
		Reflection.invokeMethod(String.class, Integer.valueOf(1), "toString");
	}

	@Test
	public void testInvokeMethodWithWrongParameters2() throws Exception {
		InvokeMethodClass invokeMethodObject = new InvokeMethodClass();
		String parameter1 = "String";
		String parameter2 = null;
		exception.expect(RuntimeException.class);
		Reflection.invokeMethod(invokeMethodObject, "methodWithParameters", new ParameterList(parameter1,
					parameter2));
	}

	@Test
	public void testPrivateFieldWithNullObject1() throws Exception {
		Object nullObject = null;
		exception.expect(IllegalArgumentException.class);
		Reflection.getPrivateField(nullObject, "nullObjectsDontHaveFields");
	}

	@Test
	public void testPrivateFieldWithNullObject2() throws Exception {
		Object nullObject = null;
		exception.expect(NullPointerException.class);
		Reflection.getPrivateField(SubClass.class, nullObject, "SECRET_INTEGER");
	}

	@Test
	public void testPrivateFieldWithNullObject3() throws Exception {
		Object nullObject = null;
		exception.expect(IllegalArgumentException.class);
		Reflection.setPrivateField(nullObject, "nullObjectsDontHaveFields", null);
	}

	@Test
	public void testPrivateFieldWithNullObject4() throws Exception {
		Object nullObject = null;
		exception.expect(NullPointerException.class);
		Reflection.setPrivateField(SubClass.class, nullObject, "SECRET_INTEGER", 123);
	}

	@Test
	public void testPrivateFieldWithWrongParameters1() throws Exception {
		exception.expect(NoSuchFieldException.class);
		Reflection.getPrivateField(SuperClass.class, new SubClass(), "secretString");
	}

	@Test
	public void testPrivateFieldWithWrongParameters2() throws Exception {
		exception.expect(IllegalArgumentException.class);
		Reflection.getPrivateField(SubClass.class, new SuperClass(), "secretString");
	}

	@Test
	public void testPrivateFieldWithWrongParameters3() throws Exception {
		exception.expect(NullPointerException.class);
		Reflection.getPrivateField(SubClass.class, null, "secretString");
	}

	@Test
	public void testPrivateFieldWithWrongParameters4() throws Exception {
		exception.expect(NoSuchFieldException.class);
		Reflection.setPrivateField(SuperClass.class, new SubClass(), "secretString", "Secret string");
	}

	@Test
	public void testPrivateFieldWithWrongParameters5() throws Exception {
		exception.expect(IllegalArgumentException.class);
		Reflection.setPrivateField(SubClass.class, new SuperClass(), "secretString", "Secret string");
	}

	@Test
	public void testPrivateFieldWithWrongParameters6() throws Exception {
		exception.expect(NullPointerException.class);
		Reflection.setPrivateField(SubClass.class, null, "secretString", "Secret string");
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

	@SuppressWarnings("PMD.UnusedPrivateField")
	private class SuperClass {
		// CHECKSTYLE DISABLE MemberNameCheck FOR 2 LINES
		private final float SECRET_FLOAT = 3.1415f;
		protected byte secretByte = 32;
	}

	@SuppressWarnings("PMD.UnusedPrivateField")
	private class SubClass extends SuperClass {
		// CHECKSTYLE DISABLE MemberNameCheck FOR 3 LINES
		private static final char SECRET_STATIC_CHAR = 'c';
		private final int SECRET_INTEGER = 42;
		private String secretString = "This is a secret string!";
	}
}

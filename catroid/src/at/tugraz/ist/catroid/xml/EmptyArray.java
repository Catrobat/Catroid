/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010-2011 The Catroid Team
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://www.catroid.org/catroid_license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *   
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package at.tugraz.ist.catroid.xml;

public final class EmptyArray {
	private EmptyArray() {
	}

	public static final boolean[] BOOLEAN = new boolean[0];
	public static final byte[] BYTE = new byte[0];
	public static final char[] CHAR = new char[0];
	public static final double[] DOUBLE = new double[0];
	public static final int[] INT = new int[0];

	public static final Class<?>[] CLASS = new Class[0];
	public static final Object[] OBJECT = new Object[0];
	public static final String[] STRING = new String[0];
	public static final Throwable[] THROWABLE = new Throwable[0];
	public static final StackTraceElement[] STACK_TRACE_ELEMENT = new StackTraceElement[0];
}
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
package at.tugraz.ist.catroid.nativetest.stage;

import android.test.ActivityInstrumentationTestCase2;
import at.tugraz.ist.catroid.stage.NativeAppActivity;

import com.jayway.android.robotium.solo.Solo;

public class NativeAppActivityTest extends ActivityInstrumentationTestCase2<NativeAppActivity> {

	private Solo solo;

	public NativeAppActivityTest() {
		super("at.tugraz.ist.catroid", NativeAppActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		solo = new Solo(getInstrumentation(), getActivity());
		super.setUp();
	}

	public void testNativeAppActivity() {
		assertTrue("NativeAppActivity is not showing!", solo.getCurrentActivity() instanceof NativeAppActivity);
	}
}

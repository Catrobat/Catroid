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
package org.catrobat.catroid.test.content.actions;

import android.location.Location;

import com.parrot.freeflight.service.DroneControlService;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
//import powermock stuff
//import android location class to mock

//import the location helper from the app project we want to test

//these two annotations are required by Powermock
@RunWith(org.powermock.modules.junit4.PowerMockRunner.class)
@PrepareForTest(Location.class)
public class DroneTestTest {

	private Object ojb;

	@Test
	public void IsSupportedPosition_Position1_ReturnTrue() {
		DroneControlService locationHelper = new DroneControlService();

		// power mock call to mock the entire static 
		//class with default implementations
		//Lorg.ob

		ojb = new Object();
		//mockStatic(DroneProxy.class);
		//
		//		//using the Mockito API plugin we mock the method of choice 
		//		//and force it to return the specific values for the condition
		//		//we want to test. Note this could be some business rule.
		//		when(Location.convert("latitude")).thenReturn(10.0);
		//		when(Location.convert("longitude")).thenReturn(39.0);

	}

	public void testSourceTestFix() {
		junit.framework.Assert.assertNotNull(ojb);
	}
}
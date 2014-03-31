package org.catrobat.catroid.test.drone;

import android.test.InstrumentationTestCase;

import com.parrot.freeflight.drone.DroneProxy;

import org.mockito.Mockito;

public class DemoTestklasse extends InstrumentationTestCase {

	public void testDemo() {
		DroneProxy droneMock = Mockito.mock(DroneProxy.class);

		droneMock.doFlip();

		// was the method called once?
		Mockito.verify(droneMock, Mockito.atLeast(1)).doFlip();
	}
}

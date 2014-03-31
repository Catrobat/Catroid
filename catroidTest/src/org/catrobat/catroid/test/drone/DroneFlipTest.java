package org.catrobat.catroid.test.drone;

import android.test.InstrumentationTestCase;

import com.parrot.freeflight.service.DroneControlService;

import org.catrobat.catroid.content.actions.DroneFlipAction;
import org.catrobat.catroid.content.actions.ExtendedActions;
import org.catrobat.catroid.drone.DroneServiceWrapper;
import org.mockito.Mockito;

public class DroneFlipTest extends InstrumentationTestCase {

	public void testFlipCalled() {
		DroneFlipAction action = ExtendedActions.droneFlip();
		DroneControlService dcs = Mockito.mock(DroneControlService.class);
		DroneServiceWrapper.getInstance().setDroneService(dcs);

		action.act(0.0f);

		// was the method called once?
		Mockito.verify(dcs, Mockito.atLeast(1)).doLeftFlip();

	}
}

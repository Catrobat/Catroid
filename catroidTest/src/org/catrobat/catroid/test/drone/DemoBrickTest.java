package org.catrobat.catroid.test.drone;

import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.powermock.api.easymock.PowerMock.verifyAll;

import android.test.InstrumentationTestCase;

import org.catrobat.catroid.content.bricks.DroneTakeOffBrick;
import org.junit.Test;

public class DemoBrickTest extends InstrumentationTestCase {

	@Test
	public void testSomething() throws Exception {
		DroneTakeOffBrick drone = new DroneTakeOffBrick(null);

		replayAll();
		assertEquals("", 0x20, drone.getRequiredResources());
		verifyAll();
	}
}

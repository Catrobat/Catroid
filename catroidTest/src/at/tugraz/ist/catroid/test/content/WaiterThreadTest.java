package at.tugraz.ist.catroid.test.content;

import android.test.AndroidTestCase;
import at.tugraz.ist.catroid.stage.WaiterThread;

public class WaiterThreadTest extends AndroidTestCase {
	public void testWait() {
		WaiterThread waiterThread = new WaiterThread(null, 100);
		assertFalse("WaiterThread is alive before it was started", waiterThread.isAlive());
		waiterThread.start();
		assertTrue("WaiterThread is not alive after start", waiterThread.isAlive());
	}
	
	public void testInterrupt() {
		WaiterThread waiterThread = new WaiterThread(null, 100);
		waiterThread.start();
		waiterThread.interrupt();
		assertTrue("WaiterThread is not interrupted after interrupt", waiterThread.isInterrupted());
	}
}

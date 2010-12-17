package at.tugraz.ist.catroid.test.content;

import android.test.AndroidTestCase;
import at.tugraz.ist.catroid.stage.BrickWait;

public class BrickWaitTest extends AndroidTestCase {
	public void testWait() {
		BrickWait brickWait = new BrickWait(100);
		assertFalse("BrickWait is waiting before it was started", brickWait.isWaiting());
		brickWait.start();
		assertTrue("BrickWait isn't waiting after it was started", brickWait.isWaiting());
	}
	
	public void testPauseAndResume() {
		BrickWait brickWait = new BrickWait(100);
		brickWait.start();
		try {
			Thread.sleep(50);
		} catch (InterruptedException e) {
			fail("Test case was interrupted during sleep");
		}
		brickWait.pause();
		assertFalse("BrickWait is waiting after it was paused", brickWait.isWaiting());

		brickWait.start();
		assertTrue("BrickWait is not waiting after it was resumed", brickWait.isWaiting());
		try {
			Thread.sleep(60);
		} catch (InterruptedException e) {
			fail("Test case was interrupted during sleep");
		}
		assertFalse("BrickWait is still waiting after it should have finished", brickWait.isWaiting());
	}
}

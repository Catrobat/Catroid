package org.catrobat.catroid.test.ui;

import java.util.concurrent.locks.Lock;

import org.catrobat.catroid.test.utils.Reflection;
import org.catrobat.catroid.ui.ViewSwitchLock;

import android.test.AndroidTestCase;

public class ViewSwitchLockTest extends AndroidTestCase {

	public void testViewSwitchLock() {
		Lock viewSwitchLock = new ViewSwitchLock();
		assertFalse("Wrong init value", (Boolean) Reflection.getPrivateField(viewSwitchLock, "locked"));

		boolean returnValue = viewSwitchLock.tryLock();
		assertTrue("ViewSwitch already locked", returnValue);
		assertTrue("Wrong locked member value", (Boolean) Reflection.getPrivateField(viewSwitchLock, "locked"));

		viewSwitchLock.unlock();
		assertFalse("ViewSwitch hasn't been unlocked", (Boolean) Reflection.getPrivateField(viewSwitchLock, "locked"));

		viewSwitchLock = new ViewSwitchLock();
		returnValue = viewSwitchLock.tryLock();
		assertTrue("ViewSwitch already locked", returnValue);
		assertTrue("Wrong locked member value", (Boolean) Reflection.getPrivateField(viewSwitchLock, "locked"));

		returnValue = viewSwitchLock.tryLock();
		assertFalse("ViewSwitch has been relocked", returnValue);
		assertTrue("Wrong locked member value", (Boolean) Reflection.getPrivateField(viewSwitchLock, "locked"));

		viewSwitchLock.unlock();
		assertFalse("ViewSwitch hasn't been unlocked", (Boolean) Reflection.getPrivateField(viewSwitchLock, "locked"));
	}

	public void testUnsupportedMethods() {
		Lock viewSwitchLock = new ViewSwitchLock();

		try {
			viewSwitchLock.lock();
			fail("Method is supported");
		} catch (UnsupportedOperationException unsupportedOperationException) {
			// Expected behavior
		}

		try {
			viewSwitchLock.lockInterruptibly();
			fail("Method is supported");
		} catch (UnsupportedOperationException unsupportedOperationException) {
			// Expected behavior
		} catch (Exception exception) {
			fail("An unexcpected excpetion occured");
		}

		try {
			viewSwitchLock.newCondition();
			fail("Method is supported");
		} catch (UnsupportedOperationException unsupportedOperationException) {
			// Expected behavior
		}

		try {
			viewSwitchLock.tryLock(1l, null);
			fail("Method is supported");
		} catch (UnsupportedOperationException unsupportedOperationException) {
			// Expected behavior
		} catch (Exception exception) {
			fail("An unexcpected excpetion occured");
		}
	}
}

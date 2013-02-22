package org.catrobat.catroid.test.ui;

import org.catrobat.catroid.test.utils.Reflection;
import org.catrobat.catroid.ui.ViewSwitchLock;

import android.test.AndroidTestCase;

public class ViewSwitchLockTest extends AndroidTestCase {

	public void testViewSwitchLock() {
		ViewSwitchLock viewSwitchLock = new ViewSwitchLock();
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
}

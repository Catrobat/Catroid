/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2015 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * An additional term exception under section 7 of the GNU Affero
 * General Public License, version 3, is available at
 * http://developer.catrobat.org/license_additional_term
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.test.ui;

import android.test.AndroidTestCase;

import org.catrobat.catroid.test.utils.Reflection;
import org.catrobat.catroid.ui.ViewSwitchLock;

import java.util.concurrent.locks.Lock;

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

	public void testViewSwitchAutoUnlock() throws InterruptedException {
		Lock viewSwitchLock = new ViewSwitchLock();
		long timeout = (Long) Reflection.getPrivateField(ViewSwitchLock.class, "UNLOCK_TIMEOUT");

		viewSwitchLock.tryLock();
		assertTrue("ViewSwitch lock isn't locked", (Boolean) Reflection.getPrivateField(viewSwitchLock, "locked"));
		Thread.sleep(timeout + 50);
		assertFalse("ViewSwitch lock didn't unlock automatically",
				(Boolean) Reflection.getPrivateField(viewSwitchLock, "locked"));
	}

	public void testDefaultSettings() {
		assertEquals("Wrong default setting", 200L, Reflection.getPrivateField(ViewSwitchLock.class, "UNLOCK_TIMEOUT"));
	}

	public void testUnsupportedMethods() {
		Lock viewSwitchLock = new ViewSwitchLock();

		try {
			viewSwitchLock.lock();
			fail("Method is supported");
		} catch (UnsupportedOperationException expected) {
		}

		try {
			viewSwitchLock.lockInterruptibly();
			fail("Method is supported");
		} catch (UnsupportedOperationException expected) {
		} catch (Exception exception) {
			fail("An unexcpected excpetion occured");
		}

		try {
			viewSwitchLock.newCondition();
			fail("Method is supported");
		} catch (UnsupportedOperationException expected) {
		}

		try {
			viewSwitchLock.tryLock(1L, null);
			fail("Method is supported");
		} catch (UnsupportedOperationException expected) {
		} catch (Exception exception) {
			fail("An unexcpected excpetion occured");
		}
	}
}

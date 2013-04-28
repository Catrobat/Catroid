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
package org.catrobat.catroid.ui;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class ViewSwitchLock implements Lock {
	private final static long UNLOCK_TIMEOUT = 200;
	private boolean locked = false;

	@Override
	public synchronized boolean tryLock() {
		if (locked) {
			return false;
		}

		locked = true;
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					Thread.sleep(UNLOCK_TIMEOUT);
				} catch (InterruptedException interruptedException) {
					interruptedException.printStackTrace();
				}
				ViewSwitchLock.this.unlock();
			}
		}).start();

		return true;
	}

	@Override
	public synchronized void unlock() {
		locked = false;
	}

	@Override
	public void lock() {
		throw new UnsupportedOperationException("Unsupported Method");
	}

	@Override
	public void lockInterruptibly() throws InterruptedException {
		throw new UnsupportedOperationException("Unsupported Method");
	}

	@Override
	public Condition newCondition() {
		throw new UnsupportedOperationException("Unsupported Method");
	}

	@Override
	public boolean tryLock(long arg0, TimeUnit arg1) throws InterruptedException {
		throw new UnsupportedOperationException("Unsupported Method");
	}
}

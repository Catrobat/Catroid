/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2016 The Catrobat Team
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
package org.catrobat.catroid.utils.idlingresources;

import android.support.annotation.Nullable;
import android.support.test.espresso.IdlingResource;

import java.util.concurrent.atomic.AtomicBoolean;

public class BooleanInitiallyBusyIdlingResource implements IdlingResource {
	@Nullable
	private volatile ResourceCallback callback;
	private AtomicBoolean isIdleNow = new AtomicBoolean(false);

	@Override
	public String getName() {
		return BooleanInitiallyBusyIdlingResource.class.getName();
	}

	@Override
	public boolean isIdleNow() {
		return isIdleNow.get();
	}

	@Override
	public void registerIdleTransitionCallback(ResourceCallback callback) {
		this.callback = callback;
	}

	public void setIdleState(boolean isIdleNow) {
		this.isIdleNow.set(isIdleNow);
		if (isIdleNow && callback != null) {
			callback.onTransitionToIdle();
		}
	}
}

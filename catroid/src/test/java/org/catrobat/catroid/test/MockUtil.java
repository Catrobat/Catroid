/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2018 The Catrobat Team
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

package org.catrobat.catroid.test;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.ScreenValues;
import org.mockito.Mockito;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

public final class MockUtil {
	private MockUtil() {
		throw new AssertionError();
	}

	public static Context mockContextForProject() throws RuntimeException {
		Context contextMock = Mockito.mock(Context.class);
		PackageManager packageManagerMock = Mockito.mock(PackageManager.class);
		when(contextMock.getPackageManager()).thenReturn(packageManagerMock);
		when(contextMock.getPackageName()).thenReturn("testStubPackage");
		when(contextMock.getString(R.string.background)).thenReturn("Background");
		PackageInfo packageInfoStub = new PackageInfo();
		packageInfoStub.versionName = "testStub";
		try {
			when(packageManagerMock.getPackageInfo(any(String.class), anyInt())).thenReturn(packageInfoStub);
		} catch (PackageManager.NameNotFoundException e) {
			throw new RuntimeException(e);
		}
		ScreenValues.setToDefaultScreenSize();

		StaticSingletonInitializer.initializeStaticSingletonMethodsWith(contextMock);

		return contextMock;
	}
}

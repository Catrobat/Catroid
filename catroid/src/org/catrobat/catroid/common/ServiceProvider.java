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
package org.catrobat.catroid.common;

import android.util.Log;

import org.catrobat.catroid.bluetooth.BluetoothDeviceServiceImpl;

import java.util.HashMap;

public final class ServiceProvider {

	public static final String TAG = ServiceProvider.class.getSimpleName();

	private ServiceProvider() {
	}

	private static HashMap<Class<? extends CatroidService>, CatroidService> services = new HashMap<Class<? extends CatroidService>, CatroidService>();

	public static synchronized <T extends CatroidService, S extends CatroidService> void registerService(Class<T> serviceType, S serviceInstance) {
		if (services.put(serviceType, serviceInstance) != null) {
			Log.w(TAG, "Service '" + serviceType.getSimpleName() + "' got overwritten!");
		}
	}

	public static synchronized <T extends CatroidService> T getService(Class<T> serviceType) {
		CatroidService serviceInstance = services.get(serviceType);
		if (serviceInstance != null) {
			return (T) serviceInstance;
		} else {
			serviceInstance = createCommonService(serviceType);
			if (serviceInstance != null) {
				return (T) serviceInstance;
			}
		}

		Log.w(TAG, "No Service '" + serviceType.getSimpleName() + "' is registered!");

		return null;
	}

	public static synchronized <T extends CatroidService> void unregisterService(Class<T> serviceType) {
		if (services.remove(serviceType) == null) {
			Log.w(TAG, "Unregister Service: Service '" + serviceType.getSimpleName() + "' is not registered!");
		}
	}

	private static CatroidService createCommonService(Class<? extends CatroidService> serviceType) {

		CatroidService service = null;

		if (serviceType == CatroidService.BLUETOOTH_DEVICE_SERVICE) {
			service = new BluetoothDeviceServiceImpl();
		}

//		example for further common services
//		if (serviceType == CatrobatService.STORAGE_HANDLER) {
//			service = new StorageHandler();
//		}

		if (service != null) {
			registerService(serviceType, service);
		}

		return service;
	}
}

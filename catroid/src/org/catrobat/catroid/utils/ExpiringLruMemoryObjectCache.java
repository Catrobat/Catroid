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

package org.catrobat.catroid.utils;

import android.util.LruCache;

import org.catrobat.catroid.common.Constants;

public class ExpiringLruMemoryObjectCache<V> extends ExpiringLruMemoryCache<String, V> {

	private static long EXPIRE_TIME = Constants.MEMORY_OBJECT_CACHE_EXPIRE_TIME;
	private static int CACHE_SIZE = Constants.MEMORY_OBJECT_CACHE_MAX_SIZE;

	private static ExpiringLruMemoryObjectCache instance = null;

	private ExpiringLruMemoryObjectCache(final long expireTime, final LruCache<String, V> lruCache,
			final ClockInterface clock) {
		super(expireTime, lruCache, clock);
	}

	final public static <V> ExpiringLruMemoryObjectCache<V> getInstance() {
		if (instance == null) {
			// do it in a thread safe way
			synchronized (ExpiringLruMemoryObjectCache.class) {
				if (instance == null) {
					instance = new ExpiringLruMemoryObjectCache<>(EXPIRE_TIME, new LruCache<String, V>(CACHE_SIZE) {
						@Override
						protected void entryRemoved(boolean evicted, String key, V oldValue, V newValue) {
							instance.removeExpiryTime(key);
						}

						@Override
						protected int sizeOf(String key, V value) {
							return 1;
						}
					}, null);
				}
			}
		}
		return instance;
	}
}

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
// based on: https://gist.github.com/christopherperry/7383019 by ZenMasterChris

package org.catrobat.catroid.utils;

import android.os.SystemClock;
import android.util.LruCache;

import java.util.HashMap;
import java.util.Map;

public class ExpiringLruMemoryCache<K, V> {

	private final long expireTime;
	private final LruCache<K, V> memoryCache;
	private final Map<K, Long> expirationTimes;
	private ClockInterface clock;

	public interface ClockInterface {
		long elapsedRealTime();
	}

	public ExpiringLruMemoryCache(final long expireTime, final LruCache<K, V> lruCache,
			final ClockInterface clock) {
		this.expireTime = expireTime;
		this.expirationTimes = new HashMap<>();
		this.memoryCache = lruCache;
		this.clock = clock;

		if (clock == null) {
			this.clock = new ClockInterface() {
				@Override
				public long elapsedRealTime() {
					return SystemClock.elapsedRealtime();
				}
			};
		}
	}

	public synchronized V get(K key) {
		V value = memoryCache.get(key);
		if (value != null && clock.elapsedRealTime() >= getExpiryTime(key)) {
			remove(key);
			return null;
		}
		return value;
	}

	public synchronized V put(K key, V value) {
		V oldValue = memoryCache.put(key, value);
		expirationTimes.put(key, clock.elapsedRealTime() + expireTime);
		return oldValue;
	}

	public long getExpiryTime(K key) {
		Long time = expirationTimes.get(key);
		if (time == null) {
			return 0;
		}
		return time;
	}

	public void removeExpiryTime(K key) {
		expirationTimes.remove(key);
	}

	public V remove(K key) {
		return memoryCache.remove(key);
	}

	public Map<K, V> snapshot() {
		return memoryCache.snapshot();
	}

	public int createCount() {
		return memoryCache.createCount();
	}

	public void evictAll() {
		memoryCache.evictAll();
	}

	public int evictionCount() {
		return memoryCache.evictionCount();
	}

	public int hitCount() {
		return memoryCache.hitCount();
	}

	public int maxSize() {
		return memoryCache.maxSize();
	}

	public int missCount() {
		return memoryCache.missCount();
	}

	public int putCount() {
		return memoryCache.putCount();
	}

	public int size() {
		return memoryCache.size();
	}
}

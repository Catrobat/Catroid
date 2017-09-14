/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2017 The Catrobat Team
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

package org.catrobat.catroid.test.utils;

import android.os.SystemClock;
import android.test.InstrumentationTestCase;
import android.util.LruCache;

import org.catrobat.catroid.utils.ExpiringLruMemoryCache;
import org.catrobat.catroid.utils.ExpiringLruMemoryObjectCache;

import java.lang.reflect.Constructor;

public final class ExpiringLruMemoryCacheTest extends InstrumentationTestCase {

	private static final class TestClock implements ExpiringLruMemoryCache.ClockInterface {
		public long elapsedTime = 0;

		@Override
		public long elapsedRealTime() {
			return (elapsedTime != 0) ? elapsedTime : SystemClock.elapsedRealtime();
		}
	}

	private static final long EXPIRE_TIME = 300; // 300ms (in ms)

	private ExpiringLruMemoryObjectCache<String> textCache;
	private TestClock testClock;

	public ExpiringLruMemoryCacheTest() {
		super();
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		testClock = new TestClock();
		int maxNumOfEntries = 2;      // number of entries

		// this bypasses the getInstance() singleton method
		Class[] constructorArgs = new Class[] {Long.TYPE, LruCache.class, ExpiringLruMemoryCache.ClockInterface.class};
		Constructor<ExpiringLruMemoryObjectCache> textCacheConstructor = ExpiringLruMemoryObjectCache.class.getDeclaredConstructor(constructorArgs);
		textCacheConstructor.setAccessible(true);

		// use introspection for accessing private constructor
		LruCache<String, String> lruTextCache = new LruCache<String, String>(maxNumOfEntries) {
			@Override
			protected void entryRemoved(boolean evicted, String key, String oldValue, String newValue) {
				textCache.removeExpiryTime(key);
			}

			@Override
			protected int sizeOf(String key, String value) {
				return 1;
			}
		};

		textCache = (ExpiringLruMemoryObjectCache<String>) textCacheConstructor.newInstance(Long.valueOf(EXPIRE_TIME),
				lruTextCache, testClock);
	}

	//----------------------------------------------------------------------------------------------
	// text cache tests
	//----------------------------------------------------------------------------------------------
	public void testFetchingNotExistingKeyShouldFail() {
		final String key = "a";
		assertNull("Fetching a not existing cache-entry must return null", textCache.get(key));
	}

	public void testFetchingExpiryTimeForNotExistingKeyShouldReturnZero() {
		final String key = "a";
		assertEquals("getExpiryTime() should return zero for non existing entries!", 0, textCache.getExpiryTime(key));
	}

	public void testShouldReturnTextForNonExpiredText() {
		final String key = "a";
		final String value = "A";

		textCache.put(key, value);
		String resultA = textCache.get(key);
		assertNotNull("Cache must NOT return null for non-expired entry", resultA);
		assertEquals("Cache entry differs from original value", value, resultA);
	}

	public void testShouldReturnNullForExpiredText() {
		final String key = "a";
		final String value = "A";

		testClock.elapsedTime = SystemClock.elapsedRealtime();
		textCache.put(key, value);
		testClock.elapsedTime += EXPIRE_TIME + 100; // simulate wait until key gets expired!
		assertNull("Cache must return null for expired entries", textCache.get(key));
	}

	public void testRemoveNonExpiredText() {
		final String key = "a";
		final String value = "A";

		textCache.put(key, value);
		assertNotNull("remove() must return removed entry but returned null", textCache.remove(key));
		assertNull("Remove entry still available!", textCache.get(key));
	}

	public void testExpiryTimeForNonExpiredText() {
		final String key = "a";
		final String value = "A";

		testClock.elapsedTime = SystemClock.elapsedRealtime();
		textCache.put(key, value);

		final long expiryTime = textCache.getExpiryTime(key);
		assertTrue("Key does not exist or is not valid any more!", expiryTime != 0);
		assertEquals("Actual expiry-time differs from expected time", testClock.elapsedTime + EXPIRE_TIME, expiryTime);
	}

	public void testAccessingTextShouldNotIncreaseExpiryTime() {
		final String key = "a";
		final String value = "A";

		testClock.elapsedTime = SystemClock.elapsedRealtime();
		textCache.put(key, value); // create key
		final long initialExpiryTime = textCache.getExpiryTime(key);
		assertTrue("Key does not exist or is not valid any more!", initialExpiryTime != 0);

		testClock.elapsedTime += EXPIRE_TIME - 100; // simulate wait 200ms! key should remain valid!
		assertNotNull("Entry not available any more but expiry-time not exhausted", textCache.get(key));
		assertEquals("Key does not exist or expiry time changed unexpectedly!",
				initialExpiryTime, textCache.getExpiryTime(key));

		testClock.elapsedTime += EXPIRE_TIME - 100; // simulate wait another 200ms!
		assertNull("Entry should not be valid/available any more!", textCache.get(key));
		assertEquals("Key has not been removed!", 0, textCache.getExpiryTime(key));
	}

	public void testRemovingExpiryTimeOfText() {
		final String key = "a";
		final String value = "A";

		textCache.put(key, value);
		textCache.removeExpiryTime(key);
		assertEquals("Expiry-time should have been invalidated but is not 0!", 0, textCache.getExpiryTime(key));
	}

	public void testRemovingExpiryTimeOfTextShouldRemoveCacheEntry() {
		final String key = "a";
		final String value = "A";

		textCache.put(key, value);
		textCache.removeExpiryTime(key);
		assertNull("Entry with invalidated expiry-time still available!", textCache.get(key));
	}

	public void testExceedingMaxSizeShouldRemoveLeastRecentlyUsedTextEntryAndRemoveExpiryTime() {
		final String keyA = "a";
		final String valueA = "A";
		final String keyB = "b";
		final String valueB = "B";
		final String keyC = "c";
		final String valueC = "C";

		textCache.put(keyA, valueA);
		textCache.put(keyB, valueB);
		final long expiryTimeB = textCache.getExpiryTime(keyB);

		// we are at 2, which is our maximum
		// => let's access "b" multiple times and never "use" "a"
		textCache.get(keyB);
		textCache.get(keyB);
		textCache.get(keyB);

		// now add another, which should evict "a"
		textCache.put(keyC, valueC);
		assertNotNull("Replaced entry still available, but should have been overwritten!", textCache.get(keyC));
		assertTrue("Expiry time of replaced entry not updated!", textCache.getExpiryTime(keyC) != 0);

		assertNotNull("Not-replaced entry has been removed unexpectedly!", textCache.get(keyB));
		assertEquals("Expiry-time of not-replaced entry has been unexpectedly updated!",
				expiryTimeB, textCache.getExpiryTime(keyB));

		assertNull("LRU algorithm did not removed least recently used entry", textCache.get(keyA));
		assertEquals("LRU algorithm did not invalidate expiry time of least recently used entry",
				0, textCache.getExpiryTime(keyA));
	}
}

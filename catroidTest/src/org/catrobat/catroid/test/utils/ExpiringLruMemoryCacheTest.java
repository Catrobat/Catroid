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

package org.catrobat.catroid.test.utils;

import android.graphics.Bitmap;
import android.os.SystemClock;
import android.test.InstrumentationTestCase;
import android.util.LruCache;

import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.uitest.util.UiTestUtils;
import org.catrobat.catroid.utils.ExpiringLruMemoryCache;
import org.catrobat.catroid.utils.ExpiringLruMemoryImageCache;
import org.catrobat.catroid.utils.ExpiringLruMemoryObjectCache;
import org.catrobat.catroid.utils.ImageEditing;

import java.io.File;
import java.lang.reflect.Constructor;

public final class ExpiringLruMemoryCacheTest extends InstrumentationTestCase {

    final private static class BitmapData {
        public Bitmap bitmap;
        public int width, height;
        public BitmapData(Bitmap bitmap, int width, int height) {
            this.bitmap = bitmap;
            this.width = width;
            this.height = height;
        }
    }

    final private static class TestClock implements ExpiringLruMemoryCache.ClockInterface {
        public long elapsedTime = 0;

        @Override
        public long elapsedRealtime() {
            return (elapsedTime != 0) ? elapsedTime : SystemClock.elapsedRealtime();
        }
    }

    final private static long EXPIRE_TIME = 300; // 300ms (in ms)

    private ExpiringLruMemoryObjectCache<String> textCache;
    private ExpiringLruMemoryImageCache imageCache;
    private BitmapData[] images;
    private TestClock testClock;

    public ExpiringLruMemoryCacheTest() {
        super();
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        testClock = new TestClock();
        int maxSize = 2;      // number of entries

        // this bypasses the getInstance() singleton method
        Class[] constructorArgs = new Class[] { Long.TYPE, LruCache.class, ExpiringLruMemoryCache.ClockInterface.class };
        Constructor<ExpiringLruMemoryObjectCache> textCacheConstructor = ExpiringLruMemoryObjectCache.class.getDeclaredConstructor(constructorArgs);
        textCacheConstructor.setAccessible(true);
        // use introspection for accessing private constructor
        // TODO: pass mocked LruCache instead!
        textCache = (ExpiringLruMemoryObjectCache<String>) textCacheConstructor.newInstance(new Long(EXPIRE_TIME), new LruCache<String, String>(maxSize) {
            @Override
            protected void entryRemoved(boolean evicted, String key, String oldValue, String newValue) {
                textCache.removeExpiryTime(key);
            }

            @Override
            protected int sizeOf(String key, String value) { return 1; }
        }, testClock);

        String tempFilePath = Constants.DEFAULT_ROOT + "/testFile.png";
        File file = UiTestUtils.createTestMediaFile(tempFilePath,
                org.catrobat.catroid.test.R.drawable.catroid_banzai,
                getInstrumentation().getContext());
        int width = 127;
        int height = 150;
        Bitmap bitmap = ImageEditing.getScaledBitmapFromPath(file.getAbsolutePath(), width, height,
                ImageEditing.ResizeType.STAY_IN_RECTANGLE_WITH_SAME_ASPECT_RATIO, true);
        BitmapData bitmapData = new BitmapData(bitmap, width, height);
        images = new BitmapData[] { bitmapData, bitmapData, bitmapData };
        maxSize = (2 * bitmap.getByteCount() / 1024); // allocate memory for exactly 2 bitmap files (in KB)
        Constructor<ExpiringLruMemoryImageCache> imageCacheConstructor = ExpiringLruMemoryImageCache.class.getDeclaredConstructor(constructorArgs);
        imageCacheConstructor.setAccessible(true);
        // use introspection for accessing private constructor
        // TODO: pass mocked LruCache instead!
        imageCache = imageCacheConstructor.newInstance(new Long(EXPIRE_TIME), new LruCache<String, Bitmap>(maxSize) {
            @Override
            protected void entryRemoved(boolean evicted, String key, Bitmap oldValue, Bitmap newValue) {
                imageCache.removeExpiryTime(key);
            }

            @Override
            protected int sizeOf(String key, Bitmap bitmap) { return bitmap.getByteCount() / 1024; }
        }, testClock);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    //----------------------------------------------------------------------------------------------
    // text cache tests
    //----------------------------------------------------------------------------------------------
    public void testFetchingNotExistingKeyShouldFail() {
        final String key = "a";
        assertNull(textCache.get(key));
    }

    public void testFetchingExpiryTimeForNotExistingKeyShouldReturnZero() {
        final String key = "a";
        assertEquals(0, textCache.getExpiryTime(key));
    }

    public void testShouldReturnTextForNonExpiredText() {
        final String key = "a";
        final String value = "A";

        textCache.put(key, value);
        String resultA = textCache.get(key);
        assertNotNull(resultA);
        assertEquals(value, resultA);
    }

    public void testShouldReturnNullForExpiredText() {
        final String key = "a";
        final String value = "A";

        testClock.elapsedTime = SystemClock.elapsedRealtime();
        textCache.put(key, value);
        testClock.elapsedTime += EXPIRE_TIME + 100; // simulate wait until key gets expired!
        assertNull(textCache.get(key));
    }

    public void testRemoveNonExpiredText() {
        final String key = "a";
        final String value = "A";

        textCache.put(key, value);
        assertNotNull(value);
        assertNotNull(textCache.remove(key));
        assertNull(textCache.get(key));
    }

    public void testExpiryTimeForNonExpiredText() {
        final String key = "a";
        final String value = "A";

        testClock.elapsedTime = SystemClock.elapsedRealtime();
        textCache.put(key, value);

        final long expiryTime = textCache.getExpiryTime(key);
        assertTrue("Key does not exist or is not valid any more!", expiryTime != 0);
        assertEquals(testClock.elapsedTime + EXPIRE_TIME, expiryTime);
    }

    public void testAccessingTextShouldNotIncreaseExpiryTime() {
        final String key = "a";
        final String value = "A";

        testClock.elapsedTime = SystemClock.elapsedRealtime();
        textCache.put(key, value); // create key
        final long initialExpiryTime = textCache.getExpiryTime(key);
        assertTrue("Key does not exist or is not valid any more!", initialExpiryTime != 0);

        testClock.elapsedTime += EXPIRE_TIME - 100; // simulate wait 200ms! key should remain valid!
        assertNotNull(textCache.get(key));
        assertEquals("Key does not exist or expiry time changed unexpectedly!",
                initialExpiryTime, textCache.getExpiryTime(key));

        testClock.elapsedTime += EXPIRE_TIME - 100; // simulate wait another 200ms!
        assertNull(textCache.get(key)); // now, the key should not be valid/available any more!
        assertEquals("Key has not been removed!", 0, textCache.getExpiryTime(key));
    }

    public void testRemovingExpiryTimeOfText() {
        final String key = "a";
        final String value = "A";

        textCache.put(key, value);
        textCache.removeExpiryTime(key);
        assertEquals(0, textCache.getExpiryTime(key));
    }

    public void testRemovingExpiryTimeOfTextShouldRemoveCacheEntry() {
        final String key = "a";
        final String value = "A";

        textCache.put(key, value);
        textCache.removeExpiryTime(key);
        assertNull(textCache.get(key));
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
        assertNotNull(textCache.get(keyC));
        assertTrue(textCache.getExpiryTime(keyC) != 0);
        assertNotNull(textCache.get(keyB));
        assertEquals(expiryTimeB, textCache.getExpiryTime(keyB));

        assertNull(textCache.get(keyA));
        assertEquals(0, textCache.getExpiryTime(keyA));
    }

    //----------------------------------------------------------------------------------------------
    // image cache tests
    //----------------------------------------------------------------------------------------------
    public void testFetchingNotExistingImageKeyShouldFail() {
        final String key = "a";
        assertNull(imageCache.get(key));
    }

    public void testFetchingImageExpiryTimeForNotExistingKeyShouldReturnZero() {
        final String key = "a";
        assertEquals(0, imageCache.getExpiryTime(key));
    }

    public void testShouldReturnImageForNonExpiredImage() {
        final String key = "a";
        final Bitmap value = images[0].bitmap;

        imageCache.put(key, value);
        Bitmap resultA = imageCache.get(key);
        assertNotNull(resultA);
        assertEquals(images[0].height, resultA.getHeight());
        assertEquals(images[0].width, resultA.getWidth());
    }

    public void testShouldReturnNullForExpiredImage() {
        final String key = "a";
        final Bitmap value = images[0].bitmap;

        testClock.elapsedTime = SystemClock.elapsedRealtime();
        imageCache.put(key, value);
        testClock.elapsedTime += EXPIRE_TIME + 100; // simulate wait until key gets expired!
        assertNull(imageCache.get(key));
    }

    public void testRemoveNonExpiredImage() {
        final String key = "a";
        final Bitmap value = images[0].bitmap;

        imageCache.put(key, value);
        assertNotNull(value);
        assertNotNull(imageCache.remove(key));
        assertNull(imageCache.get(key));
    }

    public void testExpiryTimeForNonExpiredImage() {
        final String key = "a";
        final Bitmap value = images[0].bitmap;

        testClock.elapsedTime = SystemClock.elapsedRealtime();
        imageCache.put(key, value);

        final long expiryTime = imageCache.getExpiryTime(key);
        assertTrue("Key does not exist or is not valid any more!", expiryTime != 0);
        assertEquals(testClock.elapsedTime + EXPIRE_TIME, expiryTime);
    }

    public void testAccessingImageShouldNotIncreaseExpiryTime() {
        final String key = "a";
        final Bitmap value = images[0].bitmap;

        testClock.elapsedTime = SystemClock.elapsedRealtime();
        imageCache.put(key, value); // create key
        final long initialExpiryTime = imageCache.getExpiryTime(key);
        assertTrue("Key does not exist or is not valid any more!", initialExpiryTime != 0);

        testClock.elapsedTime += EXPIRE_TIME - 100; // simulate wait 200ms! key should remain valid!
        assertNotNull(imageCache.get(key));
        assertEquals("Key does not exist or expiry time changed unexpectedly!",
                initialExpiryTime, imageCache.getExpiryTime(key));

        testClock.elapsedTime += EXPIRE_TIME - 100; // wait another 200ms
        assertNull(imageCache.get(key)); // now, the key should not be available any more!
        assertEquals("Key has not been removed!", 0, imageCache.getExpiryTime(key));
    }

    public void testRemovingExpiryTimeOfImage() {
        final String key = "a";
        final Bitmap value = images[0].bitmap;

        imageCache.put(key, value);
        imageCache.removeExpiryTime(key);
        assertEquals(0, imageCache.getExpiryTime(key));
    }

    public void testRemovingExpiryTimeOfImageShouldRemoveCacheEntry() {
        final String key = "a";
        final Bitmap value = images[0].bitmap;

        imageCache.put(key, value);
        imageCache.removeExpiryTime(key);
        assertNull(imageCache.get(key));
    }

    public void testExceedingMaxSizeShouldEvictLeastRecentlyUsedImageEntryAndRemoveExpiryCacheImage() {
        final String keyA = "a";
        final Bitmap valueA = images[0].bitmap;
        final String keyB = "b";
        final Bitmap valueB = images[1].bitmap;
        final String keyC = "c";
        final Bitmap valueC = images[2].bitmap;

        imageCache.put(keyA, valueA);
        imageCache.put(keyB, valueB);
        final long expiryTimeB = imageCache.getExpiryTime(keyB);

        // we are at 2, which is our maximum
        // => let's access "b" multiple times and never "use" "a"
        imageCache.get(keyB);
        imageCache.get(keyB);
        imageCache.get(keyB);

        // now add another, which should evict "a"
        imageCache.put(keyC, valueC);
        assertNotNull(imageCache.get(keyC));
        assertTrue(imageCache.getExpiryTime(keyC) != 0);
        assertNotNull(imageCache.get(keyB));
        assertEquals(expiryTimeB, imageCache.getExpiryTime(keyB));

        assertNull(imageCache.get(keyA));
        assertEquals(0, imageCache.getExpiryTime(keyA));
    }

}

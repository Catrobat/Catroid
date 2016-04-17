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
import android.test.InstrumentationTestCase;

import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.uitest.util.UiTestUtils;
import org.catrobat.catroid.utils.ExpiringLruMemoryImageCache;
import org.catrobat.catroid.utils.ExpiringLruMemoryObjectCache;
import org.catrobat.catroid.utils.ImageEditing;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

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

    private ExpiringLruMemoryObjectCache<String> textCache;
    private ExpiringLruMemoryImageCache imageCache;
    private BitmapData[] images;

    public ExpiringLruMemoryCacheTest() {
        super();
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        // this bypasses the getInstance() singleton method
        // and uses introspection in order to access private constructor (for testing purposes only!)
        int maxSize = 2;      // number of entries
        int expireTime = 300; // 500ms (in ms)
        Class[] constructorArgs = new Class[] { Long.TYPE, Integer.TYPE };

        Constructor<ExpiringLruMemoryObjectCache> textCacheConstructor = ExpiringLruMemoryObjectCache.class.getDeclaredConstructor(constructorArgs);
        textCacheConstructor.setAccessible(true);
        textCache = (ExpiringLruMemoryObjectCache<String>) textCacheConstructor.newInstance(new Long(expireTime), new Integer(maxSize));
        Field instanceField = ExpiringLruMemoryObjectCache.class.getDeclaredField("instance");
        instanceField.setAccessible(true);
        if (instanceField.getType() == ExpiringLruMemoryObjectCache.class) {
            instanceField.set(null, textCache);
        }

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
        expireTime = 300;   // 500ms (in ms)
        Constructor<ExpiringLruMemoryImageCache> imageCacheConstructor = ExpiringLruMemoryImageCache.class.getDeclaredConstructor(constructorArgs);
        imageCacheConstructor.setAccessible(true);
        imageCache = imageCacheConstructor.newInstance(new Long(expireTime), new Integer(maxSize));
        instanceField = ExpiringLruMemoryImageCache.class.getDeclaredField("instance");
        instanceField.setAccessible(true);
        if (instanceField.getType() == ExpiringLruMemoryImageCache.class) {
            instanceField.set(null, imageCache);
        }
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    //----------------------------------------------------------------------------------------------
    // text cache tests
    //----------------------------------------------------------------------------------------------
    public void testShouldReturnTextForNonExpiredText() {
        textCache.put("z", "Z");
        String resultA = textCache.get("z");
        assertNotNull(resultA);
        assertEquals(resultA, "Z");
    }

    public void testShouldReturnNullForExpiredText() {
        textCache.put("a", "A");

        try {
            Thread.sleep(400); // let both keys get expired => wait for more than a half second!
        } catch (InterruptedException e) {}

        assertNull(textCache.get("a"));
    }

    public void testAccessingTextShouldNotIncreaseExpireTime() {
        textCache.put("a", "A"); // create key

        try {
            Thread.sleep(200); // wait a bit but not too long! key should not get expired!
        } catch (InterruptedException e) {}

        assertNotNull(textCache.get("a"));

        try {
            Thread.sleep(200); // after 400ms (in total) the key should not be available any more!
        } catch (InterruptedException e) {}

        assertNull(textCache.get("a"));
    }

    public void testRemovingCachedTextShouldRemoveExpiryCacheEntry() {
        textCache.put("a", "A");
        textCache.removeExpiryTime("a");
        assertTrue(textCache.getExpiryTime("a") == 0);
    }

    public void testExceedingMaxSizeShouldEvictLeastRecentlyUsedTextEntryAndRemoveExpiryCacheEntry() {
        textCache.put("a", "A");
        textCache.put("b", "B");

        // we are at 2, which is our maximum
        // => let's access "b" multiple times and never "use" "a"
        textCache.get("b");
        textCache.get("b");
        textCache.get("b");

        // now add another, which should evict "a"
        textCache.put("c", "C");
        assertNotNull(textCache.get("c"));
        assertTrue(textCache.getExpiryTime("c") != 0);

        assertNull(textCache.get("a"));
        assertTrue(textCache.getExpiryTime("a") == 0);
    }

    //----------------------------------------------------------------------------------------------
    // image cache tests
    //----------------------------------------------------------------------------------------------
    public void testShouldReturnImageForNonExpiredImage() {
        imageCache.put("a", images[0].bitmap);
        Bitmap resultA = imageCache.get("a");
        assertNotNull(resultA);
        assertEquals(resultA.getHeight(), images[0].height);
        assertEquals(resultA.getWidth(), images[0].width);
    }

    public void testShouldReturnNullForExpiredImage() {
        imageCache.put("a", images[0].bitmap);

        try {
            Thread.sleep(400); // let both keys get expired => wait for more than a half second!
        } catch (InterruptedException e) {}

        assertNull(imageCache.get("a"));
    }

    public void testAccessingImageShouldNotIncreaseExpireTime() {
        imageCache.put("a", images[0].bitmap); // create key

        try {
            Thread.sleep(200); // wait a bit but not too long! key should not get expired!
        } catch (InterruptedException e) {}

        assertNotNull(imageCache.get("a"));

        try {
            Thread.sleep(200); // after 600ms (in total) the key should not be available any more!
        } catch (InterruptedException e) {}

        assertNull(imageCache.get("a"));
    }

    public void testRemovingCachedImageShouldRemoveExpiryCacheImage() {
        imageCache.put("a", images[0].bitmap);
        imageCache.removeExpiryTime("a");
        assertTrue(imageCache.getExpiryTime("a") == 0);
    }

    public void testExceedingMaxSizeShouldEvictLeastRecentlyUsedImageEntryAndRemoveExpiryCacheImage() {
        imageCache.put("a", images[0].bitmap);
        imageCache.put("b", images[1].bitmap);

        // we are at 2, which is our maximum
        // => let's access "b" multiple times and never "use" "a"
        imageCache.get("b");
        imageCache.get("b");
        imageCache.get("b");

        // now add another, which should evict "a"
        imageCache.put("c", images[2].bitmap);
        assertNotNull(imageCache.get("c"));
        assertTrue(imageCache.getExpiryTime("c") != 0);

        assertNull(imageCache.get("a"));
        assertTrue(imageCache.getExpiryTime("a") == 0);
    }

}

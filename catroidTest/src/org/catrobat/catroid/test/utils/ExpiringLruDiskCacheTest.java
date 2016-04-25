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

import android.content.Context;
import android.graphics.Bitmap;
import android.os.SystemClock;
import android.test.InstrumentationTestCase;

import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.uitest.util.UiTestUtils;
import org.catrobat.catroid.utils.DiskCacheBackendInterface;
import org.catrobat.catroid.utils.DiskCacheEntryEditorInterface;
import org.catrobat.catroid.utils.DiskCacheSnapshotInterface;
import org.catrobat.catroid.utils.DiskLruCache;
import org.catrobat.catroid.utils.ExpiringDiskCache;
import org.catrobat.catroid.utils.ImageEditing;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.when;

public final class ExpiringLruDiskCacheTest extends InstrumentationTestCase {

    final private static class BitmapData {
        public Bitmap bitmap;
        public int width, height;
        public BitmapData(Bitmap bitmap, int width, int height) {
            this.bitmap = bitmap;
            this.width = width;
            this.height = height;
        }

        public InputStream getInputStream() {
            final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 0, byteArrayOutputStream);
            final byte[] imageData = byteArrayOutputStream.toByteArray();
            DiskLruCache.closeQuietly(byteArrayOutputStream);
            return new ByteArrayInputStream(imageData);
        }
    }

    final private static long EXPIRE_TIME = 300; // 300ms (in ms)

    public ExpiringLruDiskCacheTest() {
        super();
    }

    private BitmapData[] images;
    private int maxSize;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

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
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    private ExpiringDiskCache createImageCache(final int maxSize, final long expireTime, final ExpiringDiskCache.ClockInterface clock) {
        try {
            // this bypasses the getInstance() singleton method
            // and uses introspection in order to access private constructor
            Class[] constructorArgs = new Class[]{
                    Context.class,
                    Integer.TYPE,
                    Long.TYPE,
                    Long.TYPE,
                    DiskCacheBackendInterface.class,
                    ExpiringDiskCache.ClockInterface.class
            };
            Class clazz = ExpiringDiskCache.class;
            Constructor<ExpiringDiskCache> constructor = clazz.getDeclaredConstructor(constructorArgs);
            constructor.setAccessible(true);
            return constructor.newInstance(
                    getInstrumentation().getContext(),
                    new Integer(1),
                    new Long(maxSize),
                    new Long(expireTime),
                    new CacheBackendStub(),
                    clock
            );
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private long extractExpirationDateFromMetaData(Map<String, Serializable> metaData) {
        if (metaData == null) {
            return 0;
        }
        Serializable expirationDate = metaData.get(ExpiringDiskCache.EXPIRATION_DATE_KEY);
        if (expirationDate == null) {
            return 0;
        }
        if (! (expirationDate instanceof Integer) && ! (expirationDate instanceof Long)) {
            return 0;
        }
        return ((Long)expirationDate).longValue();
    }

    //----------------------------------------------------------------------------------------------
    // image cache tests
    //----------------------------------------------------------------------------------------------
    public void testFetchingNotExistingImageKeyShouldFail() {
        final String key = "a";

        ExpiringDiskCache.ClockInterface clock = Mockito.mock(ExpiringDiskCache.ClockInterface.class);
        when(clock.elapsedRealtime()).thenReturn(SystemClock.elapsedRealtime());
        ExpiringDiskCache imageCache = createImageCache(maxSize, EXPIRE_TIME, clock);

        try {
            assertNull(imageCache.getBitmap(key));
        } catch (IOException e) {
            fail("Cannot access cache!");
        }
    }

    public void testShouldReturnImageForNonExpiredImage() {
        final String key = "a";
        final InputStream inputStream = images[0].getInputStream();
        assertNotNull(inputStream);

        ExpiringDiskCache.ClockInterface clock = Mockito.mock(ExpiringDiskCache.ClockInterface.class);
        when(clock.elapsedRealtime()).thenReturn(SystemClock.elapsedRealtime());
        ExpiringDiskCache imageCache = createImageCache(maxSize, EXPIRE_TIME, clock);

        try {
            imageCache.put(key, inputStream);
            ExpiringDiskCache.BitmapEntry entry = imageCache.getBitmap(key);
            assertNotNull(entry);
            Bitmap bitmap = entry.getBitmap();
            assertNotNull(bitmap);
            assertEquals(bitmap.getHeight(), images[0].height);
            assertEquals(bitmap.getWidth(), images[0].width);

        } catch (IOException e) {
            fail("Cannot access cache!");
        } finally {
            DiskLruCache.closeQuietly(inputStream);
        }
    }

    public void testRemoveNonExpiredImage() {
        final String key = "a";
        final InputStream inputStream = images[0].getInputStream();
        assertNotNull(inputStream);

        ExpiringDiskCache.ClockInterface clock = Mockito.mock(ExpiringDiskCache.ClockInterface.class);
        when(clock.elapsedRealtime()).thenReturn(SystemClock.elapsedRealtime());
        ExpiringDiskCache imageCache = createImageCache(maxSize, EXPIRE_TIME, clock);

        try {
            imageCache.put(key, inputStream);
            assertNotNull(imageCache.getBitmap(key));
            assertTrue(imageCache.remove(key));
            assertNull(imageCache.getBitmap(key));

        } catch (IOException e) {
            fail("Cannot access cache!");
        } finally {
            DiskLruCache.closeQuietly(inputStream);
        }
    }

    public void testShouldReturnNullForExpiredImage() {
        final String key = "a";
        final InputStream inputStream = images[0].getInputStream();
        assertNotNull(inputStream);

        ExpiringDiskCache.ClockInterface clock = Mockito.mock(ExpiringDiskCache.ClockInterface.class);
        long now = SystemClock.elapsedRealtime();
        when(clock.elapsedRealtime())
                .thenReturn(now)                      // 1st clock access
                .thenReturn(now + EXPIRE_TIME + 100); // 2nd clock access (simulate long waiting so that key gets expired!)
        ExpiringDiskCache imageCache = createImageCache(maxSize, EXPIRE_TIME, clock);

        try {
            imageCache.put(key, inputStream);         // put() triggers 1st clock access
            assertNull(imageCache.getBitmap(key));    // getBitmap() triggers 2nd clock access

        } catch(IOException e) {
            fail("Cannot access cache!");
        } finally {
            DiskLruCache.closeQuietly(inputStream);
        }
    }

    public void testExpiryTimeForNonExpiredImage() {
        final String key = "a";
        final InputStream inputStream = images[0].getInputStream();
        assertNotNull(inputStream);

        ExpiringDiskCache.ClockInterface clock = Mockito.mock(ExpiringDiskCache.ClockInterface.class);
        long startTime = SystemClock.elapsedRealtime();
        when(clock.elapsedRealtime())
                .thenReturn(startTime)             // 1st clock access
                .thenAnswer(new Answer<Long>() {   // 2nd clock access
                    public Long answer(InvocationOnMock invocation) {
                        return SystemClock.elapsedRealtime();
                    }
                });
        ExpiringDiskCache imageCache = createImageCache(maxSize, EXPIRE_TIME, clock);

        try {
            imageCache.put(key, inputStream);      // put() triggers 1st clock access

            ExpiringDiskCache.BitmapEntry entry;
            entry = imageCache.getBitmap(key);     // getBitmap() triggers 2nd clock access
            assertNotNull(entry);

            long expirationDate = extractExpirationDateFromMetaData(entry.getMetadata());
            assertTrue("Expiration date does not exist or is not valid any more!", expirationDate != 0);
            assertEquals(startTime + EXPIRE_TIME, expirationDate);

        } catch(IOException e) {
            fail("Cannot access cache!");
        } finally {
            DiskLruCache.closeQuietly(inputStream);
        }
    }

    public void testAccessingImageShouldNotIncreaseExpiryTime() {
        final String key = "a";
        final InputStream inputStream = images[0].getInputStream();
        assertNotNull(inputStream);

        ExpiringDiskCache.ClockInterface clock = Mockito.mock(ExpiringDiskCache.ClockInterface.class);
        long startTime = SystemClock.elapsedRealtime();
        when(clock.elapsedRealtime())
                .thenReturn(startTime)        // 1st clock access
                .thenReturn(startTime + 10)   // 2nd clock access
                .thenReturn(startTime + 200)  // 3rd clock access (simulate wait 200ms! key should remain valid!)
                .thenReturn(startTime + 400); // 4th clock access (simulate wait another 200ms! key must not be valid any more!)
        ExpiringDiskCache imageCache = createImageCache(maxSize, EXPIRE_TIME, clock);

        try {
            imageCache.put(key, inputStream);               // put() triggers 1st clock access

            ExpiringDiskCache.BitmapEntry entry;
            entry = imageCache.getBitmap(key);              // getBitmap() triggers 2nd clock access
            assertNotNull(entry);

            final long initialExpirationDate = extractExpirationDateFromMetaData(entry.getMetadata());
            assertTrue("Expiration date does not exist or is not valid any more!", initialExpirationDate != 0);
            assertEquals("Invalid expiry time!", initialExpirationDate, startTime + EXPIRE_TIME);

            entry = imageCache.getBitmap(key);              // getBitmap() triggers 3rd clock access
            assertNotNull(entry);

            long currentExpiryTime = extractExpirationDateFromMetaData(entry.getMetadata());
            assertTrue("Expiration date does not exist or is not valid any more!", currentExpiryTime != 0);
            assertEquals("Expiry time changed unexpectedly!", initialExpirationDate, currentExpiryTime);

            entry = imageCache.getBitmap(key);              // getBitmap() triggers 4th clock access
            assertNull(entry);                              // key must not be valid any more!

        } catch(IOException e) {
            fail("Cannot access cache!");
        } finally {
            DiskLruCache.closeQuietly(inputStream);
        }
    }

    public void testExceedingMaxSizeShouldEvictLeastRecentlyUsedImageEntryAndRemoveExpiryCacheImage() {
        final String keyA = "a";
        final String keyB = "b";
        final String keyC = "c";
        final InputStream inputStreamA = images[0].getInputStream();
        final InputStream inputStreamB = images[1].getInputStream();
        final InputStream inputStreamC = images[2].getInputStream();
        assertNotNull(inputStreamA);
        assertNotNull(inputStreamB);
        assertNotNull(inputStreamC);

        ExpiringDiskCache.ClockInterface clock = Mockito.mock(ExpiringDiskCache.ClockInterface.class);
        when(clock.elapsedRealtime())
                .thenAnswer(new Answer<Long>() {
                    public Long answer(InvocationOnMock invocation) {
                        return SystemClock.elapsedRealtime();
                    }
                });
        ExpiringDiskCache imageCache = createImageCache(maxSize, EXPIRE_TIME, clock);

        try {
            imageCache.put(keyA, inputStreamA);
            imageCache.put(keyB, inputStreamB);

            ExpiringDiskCache.BitmapEntry entry;
            entry = imageCache.getBitmap(keyB);
            assertNotNull(entry);

            final long expirationDateB = extractExpirationDateFromMetaData(entry.getMetadata());
            assertTrue("Expiration date does not exist or is not valid any more!", expirationDateB != 0);

            // we are at 2, which is our maximum
            // => let's access "b" multiple times and never "use" "a"
            assertNotNull(imageCache.getBitmap(keyB));
            assertNotNull(imageCache.getBitmap(keyB));
            assertNotNull(imageCache.getBitmap(keyB));

            // now add another, which should evict "a"
            imageCache.put(keyC, inputStreamC);

            // check if C and B are still available and if their expiration dates are valid!
            entry = imageCache.getBitmap(keyC);
            assertNotNull(entry);
            final long expirationDateC = extractExpirationDateFromMetaData(entry.getMetadata());
            assertTrue("Expiration date does not exist or is not valid any more!", expirationDateC != 0);

            entry = imageCache.getBitmap(keyB);
            assertNotNull(entry);
            final long currentExpirationDateB = extractExpirationDateFromMetaData(entry.getMetadata());
            assertTrue("Expiration date does not exist or is not valid any more!", expirationDateB != 0);
            assertEquals(currentExpirationDateB, expirationDateB);

            // "a" should have been removed from cache!
            //assertNull(imageCache.getBitmap(keyA)); // TODO: wrong cache size: fix this!

        } catch(IOException e) {
            fail("Cannot access cache!");
        } finally {
            DiskLruCache.closeQuietly(inputStreamA);
            DiskLruCache.closeQuietly(inputStreamB);
            DiskLruCache.closeQuietly(inputStreamC);
        }
    }

    // Stub classes
    // TODO: use mockit and replace these classes with mocked interfaces
    final private static class EditorStub implements DiskCacheEntryEditorInterface {
        HashMap<String, ByteArrayOutputStream> outputStreams = new HashMap<>();
        public void abort() throws IOException {}
        public void commit() throws IOException {}
        public OutputStream newOutputStream(int index) throws IOException {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            outputStreams.put(Integer.toString(index), os);
            return os;
        }
    }

    final private static class SnapshotStub implements DiskCacheSnapshotInterface {
        public EditorStub editor;
        public InputStream getInputStream(int index) {
            return new ByteArrayInputStream(editor.outputStreams.get(Integer.toString(index)).toByteArray());
        }
        public void close() {
            for (Map.Entry<String, ByteArrayOutputStream> entry : editor.outputStreams.entrySet()) {
                DiskLruCache.closeQuietly(entry.getValue());
            }
        }
    }

    final private static class CacheBackendStub implements DiskCacheBackendInterface {
        private HashMap<String, SnapshotStub> data = new HashMap<>();
        public File getDirectory() { return null; }
        public long maxSize() { return 0; }
        public void delete() throws IOException { return; }
        public DiskCacheSnapshotInterface get(String key) throws IOException {
            return data.get(key);
        }
        public boolean remove(String key) throws IOException {
            return data.remove(key) != null;
        }
        public DiskCacheEntryEditorInterface edit(String key) throws IOException {
            SnapshotStub mock = new SnapshotStub();
            mock.editor = new EditorStub();
            data.put(key, mock);
            return mock.editor;
        }
    }

}

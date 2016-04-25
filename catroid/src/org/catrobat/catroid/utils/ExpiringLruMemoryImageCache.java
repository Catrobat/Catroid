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

import android.graphics.Bitmap;
import android.util.LruCache;

import org.catrobat.catroid.common.Constants;

final public class ExpiringLruMemoryImageCache extends ExpiringLruMemoryCache<String, Bitmap> {

    private final static long EXPIRE_TIME = Constants.MEMORY_IMAGE_CACHE_EXPIRE_TIME;
    private final static int AVAILABLE_MEMORY = (int) (Runtime.getRuntime().maxMemory() / 1024);
    private final static int CACHE_SIZE = AVAILABLE_MEMORY / Constants.MEMORY_IMAGE_CACHE_ALLOCATED_FRACTION_OF_TOTAL_AVAILABLE_MEMORY;

    private static ExpiringLruMemoryImageCache instance = null;

    private ExpiringLruMemoryImageCache(final long expireTime, final LruCache<String, Bitmap> lruCache,
                                        final ClockInterface clock) {
        super(expireTime, lruCache, clock);
    }

    final public static ExpiringLruMemoryImageCache getInstance() {
        if (instance == null) {
            // do it in a thread safe way
            synchronized (ExpiringLruMemoryImageCache.class) {
                if (instance == null) {
                    instance = new ExpiringLruMemoryImageCache(EXPIRE_TIME, new LruCache<String, Bitmap>(CACHE_SIZE) {
                        @Override
                        protected void entryRemoved(boolean evicted, String key, Bitmap oldValue, Bitmap newValue) {
                            instance.removeExpiryTime(key);
                        }

                        @Override
                        protected int sizeOf(String key, Bitmap bitmap) {
                            return bitmap.getByteCount() / 1024;
                        }
                    }, null);
                }
            }
        }
        return instance;
    }

}

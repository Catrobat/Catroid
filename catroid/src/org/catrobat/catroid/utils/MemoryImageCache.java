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

public class MemoryImageCache {

    private LruCache<String, Bitmap> memoryCache;

    public MemoryImageCache() {
        final int maxAvailableVirtualMachineMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxAvailableVirtualMachineMemory / 8; // use 1/8th of available memory
        memoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getByteCount() / 1024;
            }
        };
    }

    public void put(String key, Bitmap bitmap) {
        if (get(key) == null) {
            memoryCache.put(key, bitmap);
        }
    }

    public Bitmap get(String key) {
        return memoryCache.get(key);
    }

}

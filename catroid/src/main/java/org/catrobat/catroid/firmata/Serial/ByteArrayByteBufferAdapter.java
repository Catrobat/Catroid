/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2023 The Catrobat Team
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

package org.catrobat.catroid.firmata.Serial;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Adapts byte[] to be used as IByteBuffer
 */
public class ByteArrayByteBufferAdapter implements IByteBuffer {

    private byte[] array;

    public ByteArrayByteBufferAdapter(byte[] array) {
        this.array = array;
        reset();
    }

    private void reset() {
        readIndex.set(0);
        writeIndex.set(0);
    }

    private AtomicInteger readIndex = new AtomicInteger();
    private AtomicInteger writeIndex = new AtomicInteger();
    
    public void add(byte value) {
        array[writeIndex.getAndIncrement()] = value;
    }

    public byte get() {
        if (size() <= 0)
            return -1;

        byte outcomingByte = array[readIndex.getAndIncrement()];

        // if reached written count
        if (readIndex == writeIndex) {
            reset();
        }

        return outcomingByte;
    }

    public void clear() {
        reset();
    }

    public int size() {
        return writeIndex.get() - readIndex.get();
    }
}

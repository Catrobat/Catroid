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

package org.catrobat.catroid.pocketmusic.mididriver;

public class NativeMidiDriver {
	private OnMidiStartListener listener;

	public NativeMidiDriver() {
	}

	public void start() {
		if (!init()) {
			return;
		}
		if (listener != null) {
			listener.onMidiStart();
		}
	}

	public void queueEvent(byte[] event) {
		write(event);
	}

	public void stop() {
		shutdown();
	}

	public void setOnMidiStartListener(OnMidiStartListener l) {
		listener = l;
	}

	public interface OnMidiStartListener {
		void onMidiStart();
	}

	private native boolean init();

	public native int[] config();

	public native boolean write(byte[] a);

	private native boolean shutdown();

	static {
		System.loadLibrary("midi");
	}
}

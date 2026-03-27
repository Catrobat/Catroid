/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2025 The Catrobat Team
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

package org.catrobat.catroid.formulaeditor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public final class FunctionCallManager {
	private static final FunctionCallManager INSTANCE = new FunctionCallManager();
	private static final long TIMEOUT_SECONDS = 30;

	private final AtomicLong callIdCounter = new AtomicLong(0);
	private final Map<Long, CallResult> pendingCalls = new ConcurrentHashMap<>();

	public static FunctionCallManager getInstance() {
		return INSTANCE;
	}

	public long createCall() {
		long id = callIdCounter.incrementAndGet();
		pendingCalls.put(id, new CallResult());
		return id;
	}

	public Object waitForResult(long callId) {
		CallResult result = pendingCalls.get(callId);
		if (result == null) {
			return 0.0;
		}

		try {
			if (result.latch.await(TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
				return result.value != null ? result.value : 0.0;
			}
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		} finally {
			pendingCalls.remove(callId);
		}
		return 0.0;
	}

	public void setResult(long callId, Object value) {
		CallResult result = pendingCalls.get(callId);
		if (result != null) {
			result.value = value;
			result.latch.countDown();
		}
	}

	private static class CallResult {
		final CountDownLatch latch = new CountDownLatch(1);
		volatile Object value;
	}

	private FunctionCallManager() {
	}
}

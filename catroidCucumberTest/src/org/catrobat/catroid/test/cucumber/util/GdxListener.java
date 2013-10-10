package org.catrobat.catroid.test.cucumber.util;

import com.badlogic.gdx.ApplicationListener;

public final class GdxListener implements ApplicationListener {
	private final Object mWaitLock = new Object();
	private boolean mRenderWasCalled = false;

	public void waitForStageToRender(long timeout) throws InterruptedException {
		synchronized (mWaitLock) {
			if (!mRenderWasCalled)
				mWaitLock.wait(timeout);
		}
	}

	@Override
	public void render() {
		synchronized (mWaitLock) {
			mRenderWasCalled = true;
			mWaitLock.notify();
		}
	}

	@Override
	public void create() {
	}

	@Override
	public void resize(int i, int i2) {
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void dispose() {
	}
}

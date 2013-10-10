package org.catrobat.catroid.test.cucumber.util;

import com.badlogic.gdx.scenes.scene2d.Action;

public final class CallbackAction extends Action {
	private final transient CallbackBrick.BrickCallback mCallback;

	public CallbackAction(CallbackBrick.BrickCallback callback) {
		mCallback = callback;
	}

	@Override
	public boolean act(float delta) {
		mCallback.onCallback();
		return true;
	}
}

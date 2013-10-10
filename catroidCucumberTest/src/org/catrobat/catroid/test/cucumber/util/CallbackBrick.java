package org.catrobat.catroid.test.cucumber.util;

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.bricks.ShowBrick;

import java.util.List;

public final class CallbackBrick extends ShowBrick {
	private final transient BrickCallback mCallback;

	public CallbackBrick(Sprite sprite, BrickCallback callback) {
		CallbackBrick.this.sprite = sprite;
		mCallback = callback;
	}

	@Override
	public List<SequenceAction> addActionToSequence(SequenceAction sequence) {
		sequence.addAction(new CallbackAction(mCallback));
		return null;
	}

	public interface BrickCallback {
		public void onCallback();
	}
}

package at.tugraz.ist.catroid.content.brick;

import at.tugraz.ist.catroid.content.entities.PrimitiveWrapper;
import at.tugraz.ist.catroid.content.sprite.Sprite;
import at.tugraz.ist.catroid.exception.InterruptedRuntimeException;

public class WaitBrickBase implements BrickBase {
	private static final long serialVersionUID = 1L;
    protected PrimitiveWrapper<Integer> timeToWaitInMilliseconds;
    
	public WaitBrickBase(int timeToWaitInMilliseconds) {
		this.timeToWaitInMilliseconds = new PrimitiveWrapper<Integer>(timeToWaitInMilliseconds);
	}

	public void execute() {
		long startTime = 0;
		try {
			startTime = System.currentTimeMillis();
			Thread.sleep(timeToWaitInMilliseconds.getValue());
		} catch (InterruptedException e) {
			timeToWaitInMilliseconds.setValue(timeToWaitInMilliseconds.getValue() - (int)(System.currentTimeMillis() - startTime));
			throw new InterruptedRuntimeException("WaitBrick was interrupted", e);
		}
	}

	public Sprite getSprite() {
		return null;
	}

	public long getWaitTime() {
		return timeToWaitInMilliseconds.getValue();
	}
}

package at.tugraz.ist.catroid.content.brick;

import at.tugraz.ist.catroid.content.entities.PrimitiveWrapper;
import at.tugraz.ist.catroid.content.sprite.Sprite;

public class WaitBrickBase implements BrickBase {
	private static final long serialVersionUID = 1L;
    protected PrimitiveWrapper<Long> timeToWaitInMilliseconds;
    
	public WaitBrickBase(long timeToWaitInMilliseconds) {
		this.timeToWaitInMilliseconds = new PrimitiveWrapper<Long>(timeToWaitInMilliseconds);
	}

	public void execute() {
		long startTime = 0;
		try {
			startTime = System.currentTimeMillis();
			Thread.sleep(timeToWaitInMilliseconds.getValue());
		} catch (InterruptedException e) {
			timeToWaitInMilliseconds.setValue(timeToWaitInMilliseconds.getValue() - (System.currentTimeMillis() - startTime));
			throw new RuntimeException("WaitBrick was interrupted", e);
		}
	}

	public Sprite getSprite() {
		return null;
	}

	public long getWaitTime() {
		return timeToWaitInMilliseconds.getValue();
	}
}

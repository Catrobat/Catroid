package at.tugraz.ist.catroid.content.brick;

import android.util.Log;
import at.tugraz.ist.catroid.content.sprite.Sprite;

public class WaitBrickBase implements BrickBase {
	private static final long serialVersionUID = 1L;
    protected long timeToWaitInMilliseconds;
    
	public WaitBrickBase(long timeToWaitInMilliseconds) {
		this.timeToWaitInMilliseconds = timeToWaitInMilliseconds;
	}

	public void execute() {
		long startTime = 0;
		try {
			Log.d("WaitBrick ", "Starting to wait for " + this.timeToWaitInMilliseconds);
			startTime = System.currentTimeMillis();
			Thread.sleep(timeToWaitInMilliseconds);
		} catch (InterruptedException e) {
			Log.d("WaitBrick ", "Interrupted at " + System.currentTimeMillis());
			timeToWaitInMilliseconds -= System.currentTimeMillis() - startTime;
			Log.d("WaitBrick ", "remainingWaitingTime is " + timeToWaitInMilliseconds);
		}
		
	}

	public Sprite getSprite() {
		return null;
	}

}

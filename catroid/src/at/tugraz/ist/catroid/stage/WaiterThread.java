package at.tugraz.ist.catroid.stage;

import android.util.Log;

public class WaiterThread extends Thread {
	private long timeToWaitInMilliseconds;
	private long startTime;
	private boolean isWaiting = false;
	BrickWait owner;
	
	public WaiterThread(BrickWait owner, long timeToWaitInMilliseconds) {
		this.owner = owner;
		this.timeToWaitInMilliseconds = timeToWaitInMilliseconds;
	}
	
	public boolean isWaiting() {
		return isWaiting;
	}

	public void run() {
		isWaiting = true;
		try {
			startTime = System.currentTimeMillis();
			Log.d("WaiterThread", "Started waiting at " + startTime);
			Thread.sleep(timeToWaitInMilliseconds);
		} catch (InterruptedException e) {
			long interruptedTime = System.currentTimeMillis();
			Log.d("WaiterThread", "Interrupted at " + interruptedTime);
			long alreadyWaited = interruptedTime - startTime;
			timeToWaitInMilliseconds -= alreadyWaited;
		}
		Log.d("WaiterThread", "Done waiting at " + System.currentTimeMillis());
		isWaiting = false;
		owner.done();
	}

}

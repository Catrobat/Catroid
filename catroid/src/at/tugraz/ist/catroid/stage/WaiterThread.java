package at.tugraz.ist.catroid.stage;

import android.util.Log;

public class WaiterThread extends Thread {
	private long timeToWaitInMilliseconds;
	private long startTime;
	BrickWait owner;

	public WaiterThread(BrickWait owner, long timeToWaitInMilliseconds) {
		this.owner = owner;
		this.timeToWaitInMilliseconds = timeToWaitInMilliseconds;
	}

	public void run() {
		try {
			startTime = System.currentTimeMillis();
			Log.d("WaiterThread", "Started waiting at " + startTime);
			Thread.sleep(timeToWaitInMilliseconds);
		} catch (InterruptedException e) {
			// TODO: This catch block isn't reached, even after calling interrupt(). Find out why!
			long interruptedTime = System.currentTimeMillis();
			Log.d("WaiterThread", "Interrupted at " + interruptedTime);
			long alreadyWaited = interruptedTime - startTime;
			timeToWaitInMilliseconds -= alreadyWaited;
			if (owner != null)
				owner.threadInterrupted((int) timeToWaitInMilliseconds);
			return;
		}
		Log.d("WaiterThread", "Done waiting at " + System.currentTimeMillis());
		if (owner != null)
			owner.done();
	}

}

package at.tugraz.ist.catroid.stage;

import java.util.Observable;

public class BrickWait extends Observable {
	private WaiterThread waiterThread;
	private boolean isWaiting;

	public BrickWait(int timeToWaitInMilliseconds) {
		waiterThread = new WaiterThread(this, timeToWaitInMilliseconds);
		isWaiting = false;
	}

	public void start() {
		isWaiting = true;
		if(!waiterThread.isAlive())
			waiterThread.start();
	}

	public void pause() {
		isWaiting = false;
		waiterThread.interrupt();
	}

	public boolean isWaiting() {
		return isWaiting;
	}

	public void threadInterrupted(int timeLeftToWaitInMilliseconds) {
		waiterThread = new WaiterThread(this, timeLeftToWaitInMilliseconds);
	}

	public void done() {
		isWaiting = false;
		setChanged();
		notifyObservers();
	}
}

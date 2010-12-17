package at.tugraz.ist.catroid.stage;

import java.util.Observable;

public class BrickWait extends Observable {
	private WaiterThread waiterThread;

	public BrickWait(int timeToWaitInMilliseconds) {
		waiterThread = new WaiterThread(this, timeToWaitInMilliseconds);
	}

	public void start() {
		waiterThread.start();
	}
	
	public void pause() {
		waiterThread.interrupt();
	}

	public boolean isWaiting() {
		return waiterThread.isAlive();
	}

	public void done() {
		setChanged();
		notifyObservers();
	}
}

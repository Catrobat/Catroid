package org.catrobat.catroid.devices.raspberrypi;

import android.os.AsyncTask;
import android.util.Log;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

/**
 * Created by patri on 13.12.2015.
 */
public class RaspberryPiImpl implements RaspberryPi {

	private String host;
	private int port;
	private Boolean connected;

	private RPiSocketConnection connection;

	public RaspberryPiImpl() {
		connection = new RPiSocketConnection();
	}

	public Boolean connect(String host, int port) {
		this.host = host;
		this.port = port;

		try {
			new HandleConnection().execute().get(20000, TimeUnit.MILLISECONDS);
		} catch (Exception e) {
			Log.e(getClass().getSimpleName(), "RPi connecting took too long" + e);
			return false;
		}

		return connected;
	}

	public RPiSocketConnection getConnection() {
		return connection;
	}

	public void setPin(int GPIO, Boolean pinValue) {
		if (connected) {
			//new asyncSetPin().execute(new Integer(GPIO), pinValue);
		}
	}

	public void disconnect() {
		if (connected) {
			new asyncDisconnect().execute();
		}
	}

	private class asyncSetPin extends AsyncTask<Object, Void, Integer> {
		protected Integer doInBackground(Object... args) {
			int progress = 0;

			try {
				//connection.setPin(40,true);
			} catch (Exception e) {
				e.printStackTrace();
				progress = 1;
			}
			return progress;
		}

		protected void onPostExecute(Integer integers) {
			if (integers == 1) {
				//TODO: Handle connection error!
			}
		}
	}

	private class HandleConnection extends AsyncTask<String, Void, Integer> {
		protected Integer doInBackground(String... args) {
			int progress;

			try {
				connection.connect(host, port);

				for (Integer pin : RaspberryPiService.getInstance().getPinInterrupts()) {
					connection.activatePinInterrupt(pin);
				}

				progress = 0;
			} catch (UnknownHostException e) {
				progress = 1;
			} catch (ConnectException e) {
				progress = 2;
			} catch (SocketTimeoutException e) {
				progress = 3;
			} catch (Exception e) {
				e.printStackTrace();
				progress = 4;
			}
			return progress;
		}

		protected void onPostExecute(Integer integers) {
			if (integers == 1) {
				//TODO: Host not found
			} else if (integers == 2) {
				//TODO: Could not connect
			} else if (integers == 3) {
				//TODO: Socket Timeout
			} else if (integers == 4) {
				//TODO: Unhandled error
			} else {
				connected = true;
			}
		}
	}

	private class asyncDisconnect extends AsyncTask<String, Void, Integer> {
		protected Integer doInBackground(String... args) {
			int progress = 0;

			try {
				connection.disconnect();
			} catch (Exception e) {
				e.printStackTrace();
				progress = 1;
			}
			return progress;
		}

		protected void onPostExecute(Integer integers) {
			if (integers == 1) {
				//TODO: Connection Error
			}
		}
	}
}

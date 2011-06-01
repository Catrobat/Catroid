/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010  Catroid development team
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package at.tugraz.ist.catroid.stage;

import java.io.IOException;

import lejos.nxt.Motor;
import lejos.nxt.Sound;
import lejos.nxt.remote.NXTCommand;
import lejos.pc.comm.NXTConnector;
import android.os.Looper;
import android.util.Log;
import at.tugraz.ist.catroid.stage.LeJOSDroid.CONN_TYPE;

public class TachoCount extends Thread {
	protected static final String TAG = "TachoCount";
	NXTConnector conn;

	public TachoCount() {

	}

	public void closeConnection() {

		try {
			//Log.d(TAG, "TachoCount run loop finished and closing");

			conn.getNXTComm().close();
		} catch (Exception e) {
		} finally {

			conn = null;
		}

	}

	@Override
	public void run() {
		setName(TAG + " thread");
		Looper.prepare();

		conn = LeJOSDroid.connect(CONN_TYPE.LEGO_LCP);
		NXTCommand.getSingleton().setNXTComm(conn.getNXTComm());

		Motor.A.backward();
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			Log.e(TAG, "Thread.sleep error", e);
		}
		Motor.A.stop();
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			Log.e(TAG, "Thread.sleep error", e);
		}
		Motor.A.forward();
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			Log.e(TAG, "Thread.sleep error", e);
		}
		Motor.A.stop();
		//Motor.C.rotate(-500);
		//int x=0;
		//ColorSensorHT ht= new ColorSensorHT(SensorPort.S1);
		//x=ht.getColorID();
		//LeJOSDroid.sendMessageToUIThread("T.A:" + Motor.A.getTachoCount());
		//LeJOSDroid.sendMessageToUIThread("T.A:" + Motor.A.getTachoCount() + " -- " + x);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			Log.e(TAG, "Thread.sleep error", e);
		}
		//LeJOSDroid.sendMessageToUIThread("");
		Sound.playTone(1000, 1000);

		if (conn != null) {
			try {
				conn.close();
			} catch (IOException e) {
				Log.e(TAG, "Error closing connection", e);
			}
		}
		closeConnection();
		Looper.loop();
		Looper.myLooper().quit();
		LeJOSDroid.displayToastOnUIThread("Tacho Count finished it's run");
	}

}
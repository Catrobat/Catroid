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
package at.tugraz.ist.catroid.content.bricks;

import java.io.IOException;

import lejos.nxt.Motor;
import lejos.nxt.remote.NXTCommand;
import lejos.pc.comm.NXTConnector;
import android.content.Context;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.BaseExpandableListAdapter;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.content.bricks.LeJOSDroid.CONN_TYPE;

public class MotorOn implements Brick {
	private static final long serialVersionUID = 1L;
	private Sprite sprite;
	private NXTConnector conn;

	public MotorOn(Sprite sprite) {
		this.sprite = sprite;
	}

	public void execute() {
		Looper.prepare();
		Log.i("nxt", "before lejosdroid.connect");
		conn = LeJOSDroid.connect(CONN_TYPE.LEGO_LCP);
		Log.i("nxt", "after lejosdroid.connect");
		Log.i("nxt", "before nxtcommand.getsingleton");
		NXTCommand.getSingleton().setNXTComm(conn.getNXTComm());
		Log.i("nxt", "after nxtcommand.getsingleton");
		Log.i("nxt", "before motor.A.Forward");
		Motor.A.forward();
		Log.i("nxt", "after motor.A.Forward");
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Motor.A.flt();
		if (conn != null) {
			try {
				conn.close();
			} catch (IOException e) {
				Log.e("nxt", "Error closing connection", e);
			}
		}
		closeConnection();
		Looper.loop();
		Looper.myLooper().quit();
	}

	public Sprite getSprite() {
		return this.sprite;
	}

	public View getView(Context context, int brickId, BaseExpandableListAdapter adapter) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		return inflater.inflate(R.layout.construction_brick_motoron, null);
	}

	@Override
	public Brick clone() {
		return new MotorOn(getSprite());
	}

	public View getPrototypeView(Context context) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		return inflater.inflate(R.layout.brick_motoron, null);
	}

	public void closeConnection() {
		try {
			conn.getNXTComm().close();
		} catch (Exception e) {
		} finally {

			conn = null;
		}

	}
}

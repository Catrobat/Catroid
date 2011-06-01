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
import java.util.ArrayList;
import java.util.List;

import lejos.nxt.remote.NXTCommand;
import lejos.pc.comm.NXTConnector;
import android.os.Looper;
import android.util.Log;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.content.Script;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.content.bricks.Brick;
import at.tugraz.ist.catroid.stage.LeJOSDroid.CONN_TYPE;

/**
 * @author oussama
 * 
 */
public class BTthread extends Thread {
	private NXTConnector conn;
	//	private ArrayAdapter<Sprite> adapter;
	//	private ArrayList<Sprite> adapterSpriteList;
	protected static final String TAG = "TachoCount";
	protected ArrayList<Sprite> spriteList;
	protected List<Script> scriptList;
	protected ArrayList<Brick> brickList;

	@Override
	public void run() {
		Log.i("r", "start BTthread");
		setName(TAG + " thread");
		Looper.prepare();
		conn = LeJOSDroid.connect(CONN_TYPE.LEGO_LCP);
		NXTCommand.getSingleton().setNXTComm(conn.getNXTComm());
		Log.i("r", "start play sound file");
		//Sound.playSoundFile("Hello.rso");
		Log.i("r", "end play sound file");
		scriptList = new ArrayList<Script>();
		spriteList = (ArrayList<Sprite>) ProjectManager.getInstance().getCurrentProject().getSpriteList();
		for (Sprite sprite : spriteList) {
			scriptList = sprite.getScriptList();
			for (Script script : scriptList) {
				brickList = script.getBrickList();
				for (Brick b : brickList) {
					sleep();
					b.execute();
				}
			}
		}
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

	public void sleep() {
		try {
			Thread.sleep(500);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
}

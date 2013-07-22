/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2013 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://developer.catrobat.org/license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *  
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.facedetection;

import java.util.LinkedList;
import java.util.List;

import android.util.Log;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

public class FaceDetection {

	private static FaceDetection instance;

	private final List<SequenceAction> actions = new LinkedList<SequenceAction>();

	public static FaceDetection getInstance() {
		if (instance == null) {
			instance = new FaceDetection();
		}
		return instance;
	}

	private FaceDetection() {
		new Thread() {
			@Override
			public void run() {
				while (true) {
					try {
						Thread.sleep(10000);
						onFaceDetected();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			}

		}.start();
	}

	public void addWhenFaceDetectedAction(SequenceAction action) {
		actions.add(action);
	}

	private void onFaceDetected() {
		Log.d("Andy", "Face detected");
		for (Action action : actions) {
			action.restart();
			Log.d("Andy", action + " asdasd detected");
		}
	}
}

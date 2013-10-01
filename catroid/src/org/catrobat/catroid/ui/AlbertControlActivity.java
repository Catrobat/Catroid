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
package org.catrobat.catroid.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import org.catrobat.catroid.R;

public class AlbertControlActivity extends Activity {

	//for Albert-Robot-Test
	private byte[] buffer = new byte[22];

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_robot_albert);

	}

	public void forward(View view) {

		buffer[0] = (byte) 0xAA;
		buffer[1] = (byte) 0x55;
		buffer[2] = (byte) 20;
		buffer[3] = (byte) 6;
		buffer[4] = (byte) 0x11;
		buffer[5] = (byte) 0;
		buffer[6] = (byte) 0;
		buffer[7] = (byte) 0xFF;
		buffer[8] = (byte) 100; //Left motor
		buffer[9] = (byte) 100; //Right motor
		buffer[10] = (byte) 0; //Buzzer
		buffer[11] = (byte) 0; //Left LED Red
		buffer[12] = (byte) 0; //Left LED Green
		buffer[13] = (byte) 0; //Left LED Blue
		buffer[14] = (byte) 0; //Right LED Red
		buffer[15] = (byte) 0; //Right LED Green
		buffer[16] = (byte) 0; //Right LED Blue
		buffer[17] = (byte) 0; //Front-LED 0...1
		buffer[18] = (byte) 0; //Reserved
		buffer[19] = (byte) 0; //Body-LED 0...255
		buffer[20] = (byte) 0x0D;
		buffer[21] = (byte) 0x0A;
		//mCommandService.write(buffer);
	}

	public void stop(View view) {

		buffer[0] = (byte) 0xAA;
		buffer[1] = (byte) 0x55;
		buffer[2] = (byte) 20;
		buffer[3] = (byte) 6;
		buffer[4] = (byte) 0x11;
		buffer[5] = (byte) 0;
		buffer[6] = (byte) 0;
		buffer[7] = (byte) 0xFF;
		buffer[8] = (byte) 0; //Left motor
		buffer[9] = (byte) 0; //Right motor
		buffer[10] = (byte) 0; //Buzzer
		buffer[11] = (byte) 0; //Left LED Red
		buffer[12] = (byte) 0; //Left LED Green
		buffer[13] = (byte) 0; //Left LED Blue
		buffer[14] = (byte) 0; //Right LED Red
		buffer[15] = (byte) 0; //Right LED Green
		buffer[16] = (byte) 0; //Right LED Blue
		buffer[17] = (byte) 0; //Front-LED 0...1
		buffer[18] = (byte) 0; //Reserved
		buffer[19] = (byte) 0; //Body-LED 0...255
		buffer[20] = (byte) 0x0D;
		buffer[21] = (byte) 0x0A;
		//mCommandService.write(buffer);
	}

	public void backward(View view) {

		buffer[0] = (byte) 0xAA;
		buffer[1] = (byte) 0x55;
		buffer[2] = (byte) 20;
		buffer[3] = (byte) 6;
		buffer[4] = (byte) 0x11;
		buffer[5] = (byte) 0;
		buffer[6] = (byte) 0;
		buffer[7] = (byte) 0xFF;
		buffer[8] = (byte) -100; //Left motor
		buffer[9] = (byte) -100; //Right motor
		buffer[10] = (byte) 0; //Buzzer
		buffer[11] = (byte) 0; //Left LED Red
		buffer[12] = (byte) 0; //Left LED Green
		buffer[13] = (byte) 0; //Left LED Blue
		buffer[14] = (byte) 0; //Right LED Red
		buffer[15] = (byte) 0; //Right LED Green
		buffer[16] = (byte) 0; //Right LED Blue
		buffer[17] = (byte) 0; //Front-LED 0...1
		buffer[18] = (byte) 0; //Reserved
		buffer[19] = (byte) 0; //Body-LED 0...255
		buffer[20] = (byte) 0x0D;
		buffer[21] = (byte) 0x0A;
		//mCommandService.write(buffer);
	}

	public void left(View view) {

		buffer[0] = (byte) 0xAA;
		buffer[1] = (byte) 0x55;
		buffer[2] = (byte) 20;
		buffer[3] = (byte) 6;
		buffer[4] = (byte) 0x11;
		buffer[5] = (byte) 0;
		buffer[6] = (byte) 0;
		buffer[7] = (byte) 0xFF;
		buffer[8] = (byte) 50; //Left motor
		buffer[9] = (byte) 0; //Right motor
		buffer[10] = (byte) 0; //Buzzer
		buffer[11] = (byte) 0; //Left LED Red
		buffer[12] = (byte) 0; //Left LED Green
		buffer[13] = (byte) 0; //Left LED Blue
		buffer[14] = (byte) 0; //Right LED Red
		buffer[15] = (byte) 0; //Right LED Green
		buffer[16] = (byte) 0; //Right LED Blue
		buffer[17] = (byte) 0; //Front-LED 0...1
		buffer[18] = (byte) 0; //Reserved
		buffer[19] = (byte) 0; //Body-LED 0...255
		buffer[20] = (byte) 0x0D;
		buffer[21] = (byte) 0x0A;
		//mCommandService.write(buffer);
	}

	public void right(View view) {

		buffer[0] = (byte) 0xAA;
		buffer[1] = (byte) 0x55;
		buffer[2] = (byte) 20;
		buffer[3] = (byte) 6;
		buffer[4] = (byte) 0x11;
		buffer[5] = (byte) 0;
		buffer[6] = (byte) 0;
		buffer[7] = (byte) 0xFF;
		buffer[8] = (byte) 0; //Left motor
		buffer[9] = (byte) 50; //Right motor
		buffer[10] = (byte) 0; //Buzzer
		buffer[11] = (byte) 0; //Left LED Red
		buffer[12] = (byte) 0; //Left LED Green
		buffer[13] = (byte) 0; //Left LED Blue
		buffer[14] = (byte) 0; //Right LED Red
		buffer[15] = (byte) 0; //Right LED Green
		buffer[16] = (byte) 0; //Right LED Blue
		buffer[17] = (byte) 0; //Front-LED 0...1
		buffer[18] = (byte) 0; //Reserved
		buffer[19] = (byte) 0; //Body-LED 0...255
		buffer[20] = (byte) 0x0D;
		buffer[21] = (byte) 0x0A;
		//mCommandService.write(buffer);
	}

	public void eyecolor(View view) {

		buffer[0] = (byte) 0xAA;
		buffer[1] = (byte) 0x55;
		buffer[2] = (byte) 20;
		buffer[3] = (byte) 6;
		buffer[4] = (byte) 0x11;
		buffer[5] = (byte) 0;
		buffer[6] = (byte) 0;
		buffer[7] = (byte) 0xFF;
		buffer[8] = (byte) 0; //Left motor
		buffer[9] = (byte) 0; //Right motor
		buffer[10] = (byte) 0; //Buzzer
		buffer[11] = (byte) 255; //Left LED Red
		buffer[12] = (byte) 0; //Left LED Green
		buffer[13] = (byte) 0; //Left LED Blue
		buffer[14] = (byte) 255; //Right LED Red
		buffer[15] = (byte) 0; //Right LED Green
		buffer[16] = (byte) 0; //Right LED Blue
		buffer[17] = (byte) 0; //Front-LED 0...1
		buffer[18] = (byte) 0; //Reserved
		buffer[19] = (byte) 0; //Body-LED 0...255
		buffer[20] = (byte) 0x0D;
		buffer[21] = (byte) 0x0A;
		//mCommandService.write(buffer);
	}

}

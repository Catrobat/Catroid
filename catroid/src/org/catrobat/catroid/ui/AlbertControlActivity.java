package org.catrobat.catroid.ui;

import org.catrobat.catroid.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

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

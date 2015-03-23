package org.catrobat.catroid.devices.arduino.common.firmata.writer;

import android.util.Log;

import org.catrobat.catroid.devices.arduino.common.firmata.FormatHelper;
import org.catrobat.catroid.devices.arduino.common.firmata.message.DigitalMessage;
import org.catrobat.catroid.devices.arduino.common.firmata.serial.ISerial;
import org.catrobat.catroid.devices.arduino.common.firmata.serial.SerialException;

import static org.catrobat.catroid.devices.arduino.common.firmata.BytesHelper.*;

/**
 * MessageWriter for DigitalMessage
 */
public class DigitalMessageWriter implements IMessageWriter<DigitalMessage> {

    public static final int COMMAND = 0x90;

    public void write(DigitalMessage message, ISerial serial) throws SerialException {
//        serial.write(COMMAND | ENCODE_CHANNEL(message.getPort()));
//        serial.write(LSB(message.getValue()));
//        serial.write(MSB(message.getValue()));

		int portNumber = (message.getPort() >> 3) & 0x0F;
		int out;
		if (message.getValue() == 0)
			out = 0 & ~(1 << (message.getPort() & 0x07));
		else
			out = (1 << (message.getPort() & 0x07));

		serial.write(COMMAND | portNumber);
		serial.write(out & 0x7F);
		serial.write(out >> 7);

		FormatHelper f = new FormatHelper();

		Log.d("DigitalMessag Writer", f.formatBinary(COMMAND | portNumber));
		Log.d("DigitalMessag Writer", f.formatBinary(out & 0x7F));
		Log.d("DigitalMessag Writer", f.formatBinary(out >> 7));

		Log.d("DigitalMessag Writer", f.formatBinary(COMMAND | ENCODE_CHANNEL(message.getPort())));
		Log.d("DigitalMessag Writer", f.formatBinary(LSB(message.getValue())));
		Log.d("DigitalMessag Writer", f.formatBinary(MSB(message.getValue())));
    }
}

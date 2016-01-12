package org.catrobat.catroid.devices.raspberrypi;

import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by patrick on 14.12.15.
 */
public class RaspberryPiService {
	public RPiSocketConnection connection = null;

	private static RaspberryPiService instance;

	// 0 = small GPIO, 1 = big GPIO, 2 = ComputeModule
	private Map<String, Integer> GpioVersionMap = new HashMap<String, Integer>();

	private Set<Integer> pinInterrupts = null;

	public static RaspberryPiService getInstance() {
		if (instance == null) {
			instance = new RaspberryPiService();
		}
		return instance;
	}

	private RaspberryPiService() {
		initGpioVersionMap();
		pinInterrupts = new HashSet<Integer>();
	}

	public void addPinInterrupt(int pin) {
		pinInterrupts.add(pin);
	}

	public boolean connect(String host, int port) {

		RaspberryPiImpl rpi;
		try {
			rpi = new RaspberryPiImpl();
			rpi.connect(host, port);
			connection = rpi.getConnection();
		} catch (Exception e) {
			Log.e(getClass().getSimpleName(), "connecting to " + host + ":" + port + " failed" + e);
			return false;
		}

		if (rpi.getConnection().isConnected()) {
			return true;
		}

		return false;
	}

	public void disconnect() {
		if (connection != null) {

			try {
				connection.disconnect();
				connection = null;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		pinInterrupts.clear();
	}

	public ArrayList<Integer> getGpioList(String revision) {
		int version = GpioVersionMap.get(revision);
		ArrayList<Integer> available_GPIOs = new ArrayList<Integer>();
		if (version == 0) { // small GPIO
			available_GPIOs.add(3);
			available_GPIOs.add(5);
			available_GPIOs.add(7);
			available_GPIOs.add(8);
			available_GPIOs.add(10);
			available_GPIOs.add(11);
			available_GPIOs.add(12);
			available_GPIOs.add(13);
			available_GPIOs.add(15);
			available_GPIOs.add(16);
			available_GPIOs.add(18);
			available_GPIOs.add(19);
			available_GPIOs.add(21);
			available_GPIOs.add(22);
			available_GPIOs.add(23);
			available_GPIOs.add(24);
			available_GPIOs.add(26);
		} else if (version == 1) {
			available_GPIOs.add(3);
			available_GPIOs.add(5);
			available_GPIOs.add(7);
			available_GPIOs.add(8);
			available_GPIOs.add(10);
			available_GPIOs.add(11);
			available_GPIOs.add(12);
			available_GPIOs.add(13);
			available_GPIOs.add(15);
			available_GPIOs.add(16);
			available_GPIOs.add(18);
			available_GPIOs.add(19);
			available_GPIOs.add(21);
			available_GPIOs.add(22);
			available_GPIOs.add(23);
			available_GPIOs.add(24);
			available_GPIOs.add(26);
			available_GPIOs.add(29);
			available_GPIOs.add(31);
			available_GPIOs.add(32);
			available_GPIOs.add(33);
			available_GPIOs.add(35);
			available_GPIOs.add(36);
			available_GPIOs.add(37);
			available_GPIOs.add(38);
			available_GPIOs.add(40);
		} else { // legacy mode, try to support if we don't know the version
			// TODO: Support Compute Module
			available_GPIOs.add(3);
			available_GPIOs.add(5);
			available_GPIOs.add(7);
			available_GPIOs.add(8);
			available_GPIOs.add(10);
			available_GPIOs.add(11);
			available_GPIOs.add(12);
			available_GPIOs.add(13);
			available_GPIOs.add(15);
			available_GPIOs.add(16);
			available_GPIOs.add(18);
			available_GPIOs.add(19);
			available_GPIOs.add(21);
			available_GPIOs.add(22);
			available_GPIOs.add(23);
			available_GPIOs.add(24);
			available_GPIOs.add(26);
		}

		return available_GPIOs;
	}

	private void initGpioVersionMap() {
		GpioVersionMap.put("a01041", 1);
		GpioVersionMap.put("a21041", 1);
		GpioVersionMap.put("0013", 1);
		GpioVersionMap.put("0012", 1);
		GpioVersionMap.put("0011", 2);
		GpioVersionMap.put("0010", 1);
		GpioVersionMap.put("000f", 0);
		GpioVersionMap.put("000e", 0);
		GpioVersionMap.put("000d", 0);
		GpioVersionMap.put("0009", 0);
		GpioVersionMap.put("0008", 0);
		GpioVersionMap.put("0007", 0);
		GpioVersionMap.put("0006", 0);
		GpioVersionMap.put("0005", 0);
		GpioVersionMap.put("0004", 0);
		GpioVersionMap.put("0003", 0);
		GpioVersionMap.put("0002", 0);
		GpioVersionMap.put("Beta", 0);
	}

	public Set<Integer> getPinInterrupts() {
		return pinInterrupts;
	}
}

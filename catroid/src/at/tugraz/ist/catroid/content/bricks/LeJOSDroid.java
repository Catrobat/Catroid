package at.tugraz.ist.catroid.content.bricks;

import lejos.pc.comm.NXTComm;
import lejos.pc.comm.NXTCommLogListener;
import lejos.pc.comm.NXTConnector;
import android.util.Log;

public class LeJOSDroid {

	public static enum CONN_TYPE {
		LEJOS_PACKET, LEGO_LCP
	}

	private final static String TAG = "LeJOSDroid";

	public static NXTConnector connect(final CONN_TYPE connection_type) {
		Log.d(TAG, " about to add LEJOS listener ");

		NXTConnector conn = new NXTConnector();
		conn.setDebug(true);
		conn.addLogListener(new NXTCommLogListener() {

			public void logEvent(String arg0) {
				Log.e(TAG + " NXJ log:", arg0);
			}

			public void logEvent(Throwable arg0) {
				Log.e(TAG + " NXJ log:", arg0.getMessage(), arg0);
			}
		});

		switch (connection_type) {
			case LEGO_LCP:
				conn.connectTo("btspp://NXT", NXTComm.LCP);
				break;
			case LEJOS_PACKET:
				conn.connectTo("btspp://");
				break;
		}

		return conn;
	}

}

// note: as we do not need the applet and there were errors, I commented all out


//import java.io.PrintStream;
//import java.util.Hashtable;
//import javax.swing.JApplet;
//
//public class ScratchApplet extends JApplet {
//	LContext lc;
//
//	// hook so virtual sensor board can set sensor values:
//	public static void setSensorValue(int index, int value) {
//		if ((index < 0) || (index > 15)) return;
//		PlayerPrims.sensorValues[index] = value;
//	}
//
//	public static int getSensorValue(int index) {
//		if ((index < 0) || (index > 15)) return 0;
//		return PlayerPrims.sensorValues[index];
//	}
//
//	public void init() {
//		String codeBase = getCodeBase().toString();
//		String proj = getParameter("project");
//		String projURL = (proj != null) ? projURL = codeBase + proj : null;
//		String autostart = getParameter("autostart");
//
//		boolean startFlag = true;
//		if (autostart != null) {
//			if (autostart.equalsIgnoreCase("false")) startFlag = false;
//			if (autostart.equalsIgnoreCase("no")) startFlag = false;
//		}
//			
//		// Note: Java runs the init() and destroy() as separate threads; sometimes they can get out of order
//		try {Thread.sleep(50); } catch (InterruptedException e) {};  // this gives previous destroy() a chance to run first
//		lc = PlayerPrims.startup(codeBase, projURL, getContentPane(), startFlag);
//		lc.tyo = System.out; //Note: change from MIT-code: added cast to PrintStream
//	}
//
//	public void destroy() { PlayerPrims.shutdown(lc); }
//
//}

//class LContext {
//	static final boolean isApplet = true;
//
//	Hashtable oblist = new Hashtable();
//	Hashtable props = new Hashtable();
//	MapList iline;
//	Symbol cfun, ufun;
//	Object ufunresult, juststop = new Object();
//	boolean mustOutput, timeToStop;
//	int priority = 0;
//	Object[] locals;
//	String errormessage;
//	String codeBase;
//	String projectURL;
//	PlayerCanvas canvas;
//	Sprite who;
//	Thread logoThread;
//	PrintStream tyo;
//	boolean autostart;
//}

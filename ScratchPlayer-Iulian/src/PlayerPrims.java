import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.MediaTracker;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.AccessControlException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Soundbank;
import javax.sound.midi.Synthesizer;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;
import javax.swing.JPanel;

class PlayerPrims extends Primitives {
	static Synthesizer midiSynth;
	static boolean midiSynthInitialized = false;
	static int[] sensorValues = {0, 0, 0, 1000, 1000, 1000, 1000, 1000, 0, 0, 0, 0, 0, 0, 0, 0};

	static final String[] primlist = {
		"redrawall", "0",
		"redraw", "0",
		"drawrect", "4",
		"stage", "0",
		"setstage", "1",
		"sprites", "0",
		"setsprites", "1",
		"readprojfile", "1",
		"readprojurl", "0",
		"applet?", "0",
		"string?", "1",
		"sprite?", "1",
		"color?", "1",
		"mouseX", "0",
		"mouseY", "0",
		"mouseIsDown", "0",
		"mouseClick", "0",
		"keystroke", "0",
		"keydown?", "1",
		"clearkeys", "0",
		"midisetinstrument", "2",
		"midinoteon", "3",
		"midinoteoff", "2",
		"midicontrol", "3",
		"updatePenTrails", "0",
		"soundLevel", "0",
		"jpegDecode", "1",
		"memTotal", "0",
		"memFree", "0",
		"gc", "0",
		"clearall", "0",
		"requestFocus", "0",
		"setMessage", "1",
		"getSensorValue", "1",
		"autostart?", "0",
		"quoted?", "1",
		"unquote", "1",
	};

	public String[] primlist() { return primlist; }

	public Object dispatch(int offset, Object[] args, LContext lc) {
		switch (offset) {
			case 0: lc.canvas.redraw_all(); return null;
			case 1: lc.canvas.redraw_invalid(); return null;
			case 2: return prim_drawrect(args[0], args[1], args[2], args[3], lc);
			case 3: return ((lc.canvas.stage == null) ? (Object) new Object[0] : lc.canvas.stage);
			case 4: return prim_setstage(args[0], lc);
			case 5: return lc.canvas.sprites;
			case 6: lc.canvas.sprites = (Object[]) args[0]; return null;
			case 7: return prim_readprojfile(args[0], lc);
			case 8: return prim_readprojurl(lc);
			case 9: return new Boolean(lc.isApplet);
			case 10: return new Boolean((args[0] instanceof String) || (args[0] instanceof Symbol));
			case 11: return new Boolean(args[0] instanceof Sprite);
			case 12: return new Boolean(args[0] instanceof Color);
			case 13: return new Double(lc.canvas.mouseX);
			case 14: return new Double(lc.canvas.mouseY);
			case 15: return new Boolean(lc.canvas.mouseIsDown);
			case 16: return prim_mouseclick(lc);
			case 17: return prim_keystroke(lc);
			case 18: return prim_keydown(args[0], lc);
			case 19: lc.canvas.clearkeys(); return null;
			case 20: return prim_midisetinstrument(args[0], args[1], lc);
			case 21: return prim_midinoteon(args[0], args[1], args[2], lc);
			case 22: return prim_midinoteoff(args[0], args[1], lc);
			case 23: return prim_midicontrol(args[0], args[1], args[2], lc);
			case 24: lc.canvas.updatePenTrails(); return null;
			case 25: return new Double(lc.canvas.soundLevel());
			case 26: return prim_jpegDecode(args[0], lc);
			case 27: return new Double(Runtime.getRuntime().totalMemory());
			case 28: return new Double(Runtime.getRuntime().freeMemory());
			case 29: Runtime.getRuntime().gc(); return null;
			case 30: lc.canvas.clearall(lc); return null;
			case 31: lc.canvas.requestFocus(); return null;
			case 32: lc.canvas.setMessage(Logo.aString(args[0], lc)); return null;
			case 33: return prim_getSensorValue(args[0], lc);
			case 34: return new Boolean(lc.autostart);
			case 35: return new Boolean(args[0] instanceof QuotedSymbol);
			case 36: return ((QuotedSymbol) args[0]).sym;
		}
		return null;
	}

	static final String[] primclasses = {
		"SystemPrims", "MathPrims", "ControlPrims",
		"DefiningPrims", "WordListPrims", "FilePrims",
		"PlayerPrims", "SpritePrims"};

	static synchronized LContext startup(String codeBase, String projectURL, Container container, boolean startFlag) {
			LContext lc = new LContext();
			Logo.setupPrims(primclasses, lc);
			lc.codeBase = codeBase;
			lc.projectURL = projectURL;
			lc.autostart = startFlag;
			PlayerCanvas newC = new PlayerCanvas();
			newC.lc = lc;
			lc.canvas = newC;

			Skin.readSkin(newC);
			SoundPlayer.startPlayer();
			container.add(newC, BorderLayout.CENTER);
			LogoCommandRunner.startLogoThread("load \"startup startup", lc);
			return lc;
	}

	static synchronized void shutdown(LContext lc) {
		if (lc != null) {
			LogoCommandRunner.stopLogoThread(lc);
			lc.canvas.clearall(lc); // stops all sounds, too
		}
		SoundPlayer.stopSoundsForApplet(lc);
		sensorValues = new int[16];
		for (int i = 3; i < 8; i++) sensorValues[i] = 1000;  // button and sensors A-D
	}

	static void stopMIDINotes() {
		if (PlayerPrims.midiSynth == null) return;
		MidiChannel channels[] = PlayerPrims.midiSynth.getChannels();
		for (int i = 0; i < channels.length; i++) {
			if (channels[i] != null) {
				channels[i].allNotesOff();
				channels[i].programChange(0);	// set back to piano
			}
		}
	}

	Object prim_drawrect(Object arg1, Object arg2, Object arg3, Object arg4, LContext lc) {
		int x = Logo.anInt(arg1, lc);
		int y = Logo.anInt(arg2, lc);
		int w = Logo.anInt(arg3, lc);
		int h = Logo.anInt(arg4, lc);
		lc.canvas.drawRect(x, y, w, h);
		return null;
	}

	Object prim_setstage(Object arg1, LContext lc) {
		if (!(arg1 instanceof Sprite)) return null;
		Sprite newStage = (Sprite) arg1;
		newStage.x = newStage.y = 0;
		lc.canvas.stage = newStage;
		return null;
	}

	Object prim_readprojfile(Object arg1, LContext lc) {
		FileInputStream f;
		ObjReader reader;
		Object[] empty = new Object[0];
		Object[][] objTable;

		lc.canvas.startLoading();
		try {
			f = new FileInputStream(arg1.toString());
		} catch (FileNotFoundException e) {
			Logo.error("File not found: " + arg1, lc);
			return empty;
		}

		lc.canvas.loadingProgress(0.5);
		try {
			reader = new ObjReader(f);
			objTable = reader.readObjects(lc);
			f.close();
		} catch (IOException e) {
			e.printStackTrace();
			return empty;
		}
		lc.canvas.stopLoading();
		return objTable;
	}

	Object prim_readprojurl(LContext lc) {
		Object[] empty = new Object[0];
		URL url;
		Object[][] objTable;

		if (lc.projectURL == null) return empty;

		try { url = new URL(lc.projectURL); }
		catch (java.net.MalformedURLException e) {
			Logo.error("Bad project URL: " + lc.projectURL, lc);
			return empty;
		}

		try {
			lc.canvas.startLoading();
			URLConnection urlConn = url.openConnection();
			urlConn.connect();
			int contentLength = urlConn.getContentLength();
			byte[] buf = new byte[contentLength];
			InputStream in = urlConn.getInputStream();
			int n = 0, i = 0;

			while ((n >= 0) && (i < contentLength)) {
				n = in.read(buf, i, contentLength - i);
				if (n > 0) {
					i += n;
					lc.canvas.loadingProgress((double) i / contentLength);
				} else {
					try {Thread.sleep(100); } catch (InterruptedException e) {};
				}
			}
			in.close();

			ObjReader reader = new ObjReader(new ByteArrayInputStream(buf));
			objTable = reader.readObjects(lc);
		} catch (IOException e) {
			Logo.error("Problem reading project from URL: " + lc.projectURL, lc);
			return empty;
		}
		lc.canvas.stopLoading();
		return objTable;
	}

	Object prim_mouseclick(LContext lc) {
		if (lc.canvas.mouseclicks.isEmpty()) return new Object[0];
		else return lc.canvas.mouseclicks.remove(0);
	}

	Object prim_keystroke(LContext lc) {
		if (lc.canvas.keystrokes.isEmpty()) return new Object[0];
		else return lc.canvas.keystrokes.remove(0);
	}

	Object prim_keydown(Object arg1, LContext lc) {
		int keyindex = Logo.anInt(arg1, lc);
		if (keyindex > 255) return new Boolean(false);
		return new Boolean(lc.canvas.keydown[keyindex]);
	}

	Object prim_midisetinstrument(Object arg1, Object arg2, LContext lc) {
		init_midi(lc);
		if (midiSynth == null) return null;
		int chan = (Logo.anInt(arg1, lc) - 1) & 15;
		int instr = (Logo.anInt(arg2, lc) - 1) & 127;
		(midiSynth.getChannels()[chan]).programChange(instr);
		return null;
	}

	Object prim_midinoteon(Object arg1, Object arg2, Object arg3, LContext lc) {
		init_midi(lc);
		if (midiSynth == null) return null;
		int chan = (Logo.anInt(arg1, lc) - 1) & 15;
		int key = Logo.anInt(arg2, lc) & 127;
		int vel = Logo.anInt(arg3, lc) & 127;
		(midiSynth.getChannels()[chan]).noteOn(key, vel);
		return null;
	}

	Object prim_midinoteoff(Object arg1, Object arg2, LContext lc) {
		init_midi(lc);
		if (midiSynth == null) return null;
		int chan = (Logo.anInt(arg1, lc) - 1) & 15;
		int key = Logo.anInt(arg2, lc) & 127;
		(midiSynth.getChannels()[chan]).noteOff(key);
		return null;
	}

	Object prim_midicontrol(Object arg1, Object arg2, Object arg3, LContext lc) {
		init_midi(lc);
		if (midiSynth == null) return null;
		int chan = (Logo.anInt(arg1, lc) - 1) & 15;
		int cntl = Logo.anInt(arg2, lc) & 127;
		int val = Logo.anInt(arg3, lc) & 127;
		(midiSynth.getChannels()[chan]).controlChange(cntl, val);
		return null;
	}

	void init_midi(LContext lc) {
		// try to open the MIDI synth
		if (midiSynthInitialized) return;
		midiSynthInitialized = true;
		try {
			midiSynth = MidiSystem.getSynthesizer();
			midiSynth.open();
			if (midiSynth.getDefaultSoundbank() == null) {
				lc.canvas.setMessage("Reading sound bank from server. Please wait...");
				URL url = (lc.isApplet) ?
					new URL(lc.codeBase + "soundbank.gm") :
					new URL("http://web.media.mit.edu/~jmaloney/player/soundbank.gm");
				Soundbank sb = MidiSystem.getSoundbank(url);
				if (sb != null) {
					midiSynth.loadAllInstruments(sb);
					lc.canvas.setMessage("");
				} else {
					midiSynth.close();
					midiSynth = null;
				}
			}
		} catch (MidiUnavailableException e) {
			e.printStackTrace();
			midiSynth = null;
		} catch (MalformedURLException e) {
			e.printStackTrace();
			midiSynth = null;
		} catch (InvalidMidiDataException e) {
			e.printStackTrace();
			midiSynth = null;
		} catch (IOException e) {
			e.printStackTrace();
			midiSynth = null;
		} catch (java.security.AccessControlException e) {
			e.printStackTrace();
			midiSynth = null;
		}
		if (midiSynth != null) {
			MidiChannel channels[] = PlayerPrims.midiSynth.getChannels();
			for (int i = 0; i < channels.length; i++) {
				if (channels[i] != null) channels[i].programChange(0);	// set instrument to piano
			}
		} else {
			lc.canvas.setMessage("No soundbank; note & drum commands disabled.");
		}
	}

	Object prim_jpegDecode(Object arg1, LContext lc) {
		if (!(arg1 instanceof byte[])) return null;
		byte[] data = (byte[]) arg1;
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Image img = toolkit.createImage(data);

		// wait for conversion to complete so we know width and height
		MediaTracker mediaTracker = new MediaTracker(lc.canvas);
		mediaTracker.addImage(img, 0);
		try { mediaTracker.waitForID(0); } catch (InterruptedException ie) { }
		if (img == null) return null;

		// convert Image into a BufferedImage
		int w = img.getWidth(null);
		int h = img.getHeight(null);
		BufferedImage outImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		Graphics g = outImg.getGraphics();
		g.drawImage(img, 0, 0, w, h, null);
		g.dispose();
		return outImg;
	}

	Object prim_getSensorValue(Object arg1, LContext lc) {
		int i = Logo.anInt(arg1, lc) - 1;
		if ((i < 0) || (i > 15)) return new Double(123.0);
		return new Double((double) sensorValues[i] / 10.0);
	}
}

// Holder for Images used by the UI.
class Skin {
	static boolean skinInitialized;

	static BufferedImage askBubbleFrame;
	static BufferedImage askPointerL;
	static BufferedImage askPointerR;
	static BufferedImage bubbleFrame;
	static BufferedImage goButton;
	static BufferedImage goButtonOver;
	static BufferedImage promptCheckButton;
	static BufferedImage sliderKnob;
	static BufferedImage sliderSlot;
	static BufferedImage stopButton;
	static BufferedImage stopButtonOver;
	static BufferedImage talkPointerL;
	static BufferedImage talkPointerR;
	static BufferedImage thinkPointerL;
	static BufferedImage thinkPointerR;
	static BufferedImage watcherOuterFrame;
	static BufferedImage listWatcherOuterFrame;
	static BufferedImage listWatcherOuterFrameError;
	static BufferedImage vScrollFrame;
	static BufferedImage vScrollSlider;

	static synchronized void readSkin(Component c) {
		if (skinInitialized) return;
		Skin.askBubbleFrame = readImage("askBubbleFrame.gif", c);
		Skin.askPointerL = readImage("askBubblePointer.gif", c);
		Skin.askPointerR = flipImage(askPointerL);
		Skin.bubbleFrame = readImage("talkBubbleFrame.gif", c);
		Skin.goButton = readImage("goButton.gif", c);
		Skin.goButtonOver = readImage("goButtonOver.gif", c);
		Skin.promptCheckButton = readImage("promptCheckButton.png", c);
		Skin.sliderKnob = readImage("sliderKnob.gif", c);
		Skin.sliderSlot = readImage("sliderSlot.gif", c);
		Skin.stopButton = readImage("stopButton.gif", c);
		Skin.stopButtonOver = readImage("stopButtonOver.gif", c);
		Skin.talkPointerL = readImage("talkBubbleTalkPointer.gif", c);
		Skin.talkPointerR = flipImage(talkPointerL);
		Skin.thinkPointerL = readImage("talkBubbleThinkPointer.gif", c);
		Skin.thinkPointerR = flipImage(thinkPointerL);
		Skin.watcherOuterFrame = readImage("watcherOuterFrame.png", c);
		Skin.listWatcherOuterFrame = readImage("listWacherOuterFrame.png", c);
		Skin.listWatcherOuterFrameError = readImage("listWacherOuterFrameError.png", c);
		Skin.vScrollFrame = readImage("vScrollFrame.png", c);
		Skin.vScrollSlider = readImage("vScrollSlider.png", c);
		skinInitialized = true;
	}

	static BufferedImage readImage(String s, Component c) {
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Image img = toolkit.createImage(c.getClass().getResource("skin/" + s));
		MediaTracker mediaTracker = new MediaTracker(c);
		mediaTracker.addImage(img, 0);
		try {
			mediaTracker.waitForID(0);
		} catch (InterruptedException e) {}

		// convert Image into a BufferedImage
		int w = img.getWidth(null);
		int h = img.getHeight(null);
		BufferedImage bufImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		Graphics g = bufImg.getGraphics();
		g.drawImage(img, 0, 0, w, h, null);
		g.dispose();
		return bufImg;
	}

	static BufferedImage flipImage(BufferedImage img) {
		int imgW = img.getWidth(null);
		int imgH = img.getHeight(null);
		BufferedImage out = new BufferedImage(imgW, imgH, BufferedImage.TYPE_INT_ARGB);
		Graphics g = out.getGraphics();
		g.drawImage(img, imgW, 0, 0, imgH, 0, 0, imgW, imgH, null);
		g.dispose();
		return out;
	}
}

// Implements the UI, including input events and incremental redraw via and offscreen buffer.
class PlayerCanvas extends JPanel
	implements KeyListener, MouseListener, MouseMotionListener {

	static final String versionString = "experimental v37";

	static final int width = 482;
	static final int height = 387;
	static final int topBarHeight = 26;
	static final int goButtonX = 418;
	static final int stopButtonX = 451;
	static final int buttonY = 4;
	static final Color BLACK = new Color(0, 0, 0);
	static final Color WHITE = new Color(255, 255, 255);

	static final int soundInputBufSize = 50000;  // about half a second @ 44100 16-bit samples/sec
	static TargetDataLine soundInputLine;
	byte[] soundInputBuf = new byte[soundInputBufSize];
	int soundLevel = 0;

	boolean overGoButton = false;
	boolean overStopButton = false;

	String message = "";
	boolean isLoading = true;
	double loadingFraction = 0.2;

	LContext lc;
	Sprite stage;
	Object[] sprites = new Object[0];
	BufferedImage offscreen, penTrails;
	Rectangle invalrect = new Rectangle();

	int mouseX, mouseY;
	boolean mouseIsDown = false;
	int mouseDownX, mouseDownY;
	Drawable mouseDragTarget;
	int	mouseDragXOffset;
	int	mouseDragYOffset;
	boolean reportClickOnMouseUp;
	Vector mouseclicks = new Vector();
	boolean[] keydown = new boolean[256];
	Vector keystrokes = new Vector();

	AskPrompter askPrompt = null;
	String lastAnswer = "";

	PlayerCanvas() {
		setLayout(null);
		addKeyListener(this);
		addMouseListener(this);
		addMouseMotionListener(this);
	}

	public void addNotify() {
		super.addNotify();
		offscreen = (BufferedImage) createImage(width, height);
		offscreen.getRaster(); // force this image to non-accellerated; prevents flashing and only 2-3% slower
		Graphics offg = offscreen.getGraphics();
		offg.setColor(WHITE);
		offg.fillRect(0, 0, width, height);
		offg.dispose();
		if (!LContext.isApplet) openSoundInput();
		repaint();
	}

	public Dimension getMinimumSize() { return new Dimension(width, height); }
	public Dimension getPreferredSize() { return new Dimension(width, height); }

	public synchronized void paintComponent(Graphics g) {
		g.drawImage(offscreen, 0, 0, width, height, this);
	}

	void clearall(LContext lc) {
		stage = null;
		sprites = new Object[0];
		askPrompt = null;
		lastAnswer = "";
		penTrails = null;
		SoundPlayer.stopSoundsForApplet(lc);
		soundLevel = 0;
		lc.props = new Hashtable(); // clear obsolete sprites from property list hashtable
		Runtime.getRuntime().gc();
		clearkeys();
		mouseclicks = new Vector();
		mouseIsDown = false;
		mouseDragTarget = null;
	}

	void setMessage(String s) {
		message = s;
		redraw_all();
	}

	synchronized void inval(Rectangle r) {
		if (invalrect.isEmpty()) invalrect = new Rectangle(r);
		else invalrect = invalrect.union(r);
	}
	
	void invalAll() { inval(new Rectangle(0, 0, width, height)); }

	void redraw_all() { redraw(new Rectangle(0, 0, width, height), false); }
	void redraw_invalid() { redraw(invalrect, false); }

	synchronized void redraw(Rectangle r, boolean showInvalidRect) {
		Graphics offg = offscreen.getGraphics();
		offg.setClip(r.x, r.y, r.width, r.height);
		offg.setColor(WHITE);
		offg.fillRect(0, 0, width, height);
		if (isLoading) {
			drawProgressBar(offg);
		} else {
			if (stage != null) { stage.setStageOffset(); stage.paint(offg); }
			if (penTrails != null) offg.drawImage(penTrails, 0, 0, width, height, null);
			for (int i = sprites.length - 1; i >= 0; i--) {
				Drawable obj = (Drawable) sprites[i];
				if (obj.isShowing() && r.intersects(obj.fullRect())) obj.paint(offg);
			}
			for (int i = sprites.length - 1; i >= 0; i--) {
				Drawable obj = (Drawable) sprites[i];
				if (obj.isShowing() && r.intersects(obj.fullRect())) obj.paintBubble(offg);
			}
			if (askPrompt != null) askPrompt.paint(offg);
		}
		drawBorder(offg);
		if (showInvalidRect) {
			offg.setColor(new Color(200, 0, 0));
			offg.fillRect(r.x, r.y, r.width, r.height);
		}
		offg.dispose();
		repaint(r);
		invalrect = new Rectangle();
	}

	void drawBorder(Graphics g) {
		g.setColor(new Color(130, 130, 130));
		g.fillRect(0, 0, width, 25);

		g.setColor(BLACK);
		g.fillRect(0, 0, width, 1);
		g.fillRect(0, 0, 1, height);
		g.fillRect(0, height - 1, width, 1);
		g.fillRect(width - 1, 0, 1, height);
		g.fillRect(0, 25, width, 1);
		
		g.drawImage(
			overGoButton ? Skin.goButtonOver : Skin.goButton,
			goButtonX, buttonY, null);
		g.drawImage(
			overStopButton ? Skin.stopButtonOver : Skin.stopButton,
			stopButtonX, buttonY, null);

		g.setColor(WHITE);
		g.setFont(new Font("SansSerif", Font.PLAIN, 8));
		g.drawString(versionString, 5, 11);
		if (message.length() > 0) {
			g.setFont(new Font("SansSerif", Font.BOLD, 13));
			g.setColor(new Color(250, 250, 0));
			g.drawString(message, 70, 17);
		}
	}

	void drawProgressBar(Graphics g) {
		g.setColor(BLACK);
		g.setFont(new Font("SansSerif", Font.BOLD, 24));
		g.drawString("Loading...", (width / 2) - 53, (height / 2) - 20);

		int x = (width / 2) - 150;
		int y = height / 2;
		g.fillRect(x, y, 300, 1);
		g.fillRect(x, y, 1, 29);
		g.fillRect(x, y + 28, 300, 1);
		g.fillRect(x + 299, y, 1, 29);

		g.setColor(new Color(10, 10, 200));
		g.fillRect(x + 2, y + 2, (int) (296.0 * Math.min(loadingFraction, 1.0)), 25);
		
		drawBorder(g);
	}

	BufferedImage drawAreaWithoutSprite(Rectangle r, Sprite skipSprite) {
		BufferedImage img = new BufferedImage(r.width, r.height, BufferedImage.TYPE_INT_ARGB);
		Graphics g = img.getGraphics();
		g.setColor(WHITE);
		g.fillRect(0, 0, r.width, r.height);
		g = g.create(-r.x, -r.y, r.width, r.height);  // set translation
		g.setClip(r.x, r.y, r.width, r.height);
		if (stage != null) { stage.setStageOffset(); stage.paint(g); }
		if (penTrails != null) g.drawImage(penTrails, 0, 0, width, height, null);
		for (int i = sprites.length - 1; i >= 0; i--) {
			Drawable obj = (Drawable) sprites[i];
			if ((obj != skipSprite) && obj.isShowing() && r.intersects(obj.rect())) obj.paint(g);
		}
		g.dispose();
		return img;
	}

	void drawRect(int x, int y, int w, int h) {
		Graphics offg = offscreen.getGraphics();
		offg.setColor(BLACK);
		offg.fillRect(x, y, w, h);
		offg.dispose();
		repaint();
	}

	void startLoading() {
		isLoading = true;
		loadingFraction = 0;
		redraw_all();
	}

	void stopLoading() {
		loadingFraction = 1;
		redraw_all();
		loadingFraction = 0;
		isLoading = false;
	}

	void loadingProgress(double f) {
		loadingFraction = f;
		redraw_all();
	}

	boolean logoIsRunning() {
		return lc.logoThread != null;
	}

	void updatePenTrails() {
		for (int i = sprites.length - 1; i >= 0; i--) {
			if (sprites[i] instanceof Sprite) {
				Sprite s = (Sprite) sprites[i];
				if (s.penDown) updatePenTrailForSprite(s);
			}
		}
	}

	void updatePenTrailForSprite(Sprite s) {
		if (penTrails == null) createPenTrails();
		int newPenX = Sprite.originX + (int) s.x;
		int newPenY = Sprite.originY - (int) s.y;
		if (s.lastPenX == -1000000.0) {  // pen was just put down
			s.lastPenX = newPenX;
			s.lastPenY = newPenY;
		} else {
			if ((s.lastPenX == newPenX) && (s.lastPenY == newPenY)) return;  // sprite has not moved
		}
		Graphics2D g = penTrails.createGraphics();
		g.setColor(s.penColor);
		g.setStroke(new BasicStroke(s.penSize, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
		g.drawLine(s.lastPenX, s.lastPenY, newPenX, newPenY);
		g.dispose();

		Rectangle r = new Rectangle(s.lastPenX, s.lastPenY, 0, 0);
		r.add(newPenX, newPenY);
		r.grow(s.penSize, s.penSize);
		inval(r);

		s.lastPenX = newPenX;
		s.lastPenY = newPenY;
	}

	void stampCostume(Sprite s) {
		if (penTrails == null) createPenTrails();
		Graphics2D g = penTrails.createGraphics();
		if (s.filterChanged) s.applyFilters();
		g.drawImage(s.outImage(), s.screenX(), s.screenY(), null);
		g.dispose();
		s.inval();
	}

	void createPenTrails() {
		penTrails = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		penTrails.getRaster(); // force this image to non-accellerated
	}

	void clearPenTrails() {
		if (penTrails != null) penTrails.flush();
		penTrails = null;
		inval(new Rectangle(0, 0, width, height));
	}

	public void mouseEntered(MouseEvent e) { requestFocus(); mouseDragOrMove(e); }
	public void mouseExited(MouseEvent e) { updateMouseXY(e); }
	public void mousePressed(MouseEvent e) { mouseDown(e); }
	public void mouseReleased(MouseEvent e) { mouseUp(e); }
	public void mouseClicked(MouseEvent e) { updateMouseXY(e); }
	public void mouseDragged(MouseEvent e) { mouseDragOrMove(e); }
	public void mouseMoved(MouseEvent e) { mouseDragOrMove(e); }

	void mouseDown(MouseEvent e) {
		updateMouseXY(e);
		requestFocus();
		if (inGoButton(e)) {
			doStopButton();
			LogoCommandRunner.startLogoThread("greenflag", lc);
			return;
		}
		if (inStopButton(e)) {
			doStopButton();
			LogoCommandRunner.startLogoThread("interact", lc);
			return;
		}
		mouseIsDown = true;
		mouseDownX = e.getX();
		mouseDownY = e.getY();
		mouseDragTarget = findDragTarget(e.getX(), e.getY());
		mouseDragXOffset = mouseDragYOffset = 0;
		reportClickOnMouseUp = true;
		if (mouseDragTarget instanceof Sprite) {			// mouse down on sprite, could be click or drag
			Sprite s = (Sprite) mouseDragTarget;
			if (s.isDraggable) {
				mouseDragXOffset = s.screenX() - e.getX();
				mouseDragYOffset = s.screenY() - e.getY();
				moveSpriteToFront(s);
			} else {
				mouseDragTarget = null;
			}
		}
		if (mouseDragTarget instanceof ListWatcher) {
			ListWatcher lw = (ListWatcher) mouseDragTarget;
			lw.mouseDown(e.getX(), e.getY());				// record mouse down value for scroll offset
		}
		if (askPrompt != null) {
			boolean val = askPrompt.mouseDown(e.getX(), e.getY(), this);
			if (val) return;
		}
		if (mouseDragTarget == null) {
			reportClick();  // mouse down on stage or locked sprite; report click it immediately
		}
	}

	void mouseDragOrMove(MouseEvent e) {
		updateMouseXY(e);
		if ((e.getX() != mouseDownX) || (e.getY() != mouseDownY)) {
			reportClickOnMouseUp = false;
		}
		if (mouseDragTarget != null) {
			mouseDragTarget.dragTo(e.getX() + mouseDragXOffset, e.getY() + mouseDragYOffset);
		} else {
			boolean oldGo = overGoButton;
			boolean oldStop = overStopButton;
			overGoButton = inGoButton(e);
			overStopButton = inStopButton(e);
			if ((oldGo != overGoButton) || (oldStop != overStopButton)) {
				inval(new Rectangle(0, 0, width, topBarHeight));
				redraw_invalid();
			}
		}
	}

	void mouseUp(MouseEvent e) {
		updateMouseXY(e);
		if (reportClickOnMouseUp) {
			if (mouseDragTarget instanceof Watcher) {
				((Watcher) mouseDragTarget).click(e.getX(), e.getY());
			} else {
				reportClick();
			}
		}
		mouseDragTarget = null;
		mouseIsDown = false;
	}

	void reportClick() {
		Object[] mouseDownPoint = new Object[2];
		mouseDownPoint[0] = new Double(mouseDownX - Sprite.originX);
		mouseDownPoint[1] = new Double(Sprite.originY - mouseDownY);
		mouseclicks.addElement(mouseDownPoint);
		reportClickOnMouseUp = false;
	}

	void updateMouseXY(MouseEvent e) {
		mouseX = e.getX() - Sprite.originX;
		mouseY = Sprite.originY - e.getY();
	}

	boolean inGoButton(MouseEvent e) {
		if (e.getY() >= topBarHeight) return false;
		int x = e.getX();
		return (x >= goButtonX) && (x <= (goButtonX + Skin.goButton.getWidth(null)));
	}

	boolean inStopButton(MouseEvent e) {
		if (e.getY() >= topBarHeight) return false;
		int x = e.getX();
		return (x >= stopButtonX) && (x <= (stopButtonX + Skin.stopButton.getWidth(null)));
	}

	void doStopButton() {
		SoundPlayer.stopSoundsForApplet(lc);
		LogoCommandRunner.stopLogoThread(lc);
		(new LogoCommandRunner("stopAll", lc, true)).run();
		clearkeys();
		mouseclicks = new Vector();
		mouseIsDown = false;
		mouseDragTarget = null;
		redraw_all();
	}

	Drawable findDragTarget(int mouseX, int mouseY) {
		for (int i = 0; i < sprites.length; i++) {
			if (sprites[i] instanceof Watcher) {
				Watcher w = (Watcher) sprites[i];
				if (w.inSlider(mouseX, mouseY)) return w;
			}
			if (sprites[i] instanceof ListWatcher) {
				ListWatcher w = (ListWatcher) sprites[i];
				if (w.inScrollbar(mouseX, mouseY)) return w;
			}
			if (sprites[i] instanceof Sprite) {
				Sprite s = (Sprite) sprites[i];
				if (s.isShowing() && s.containsPoint(mouseX, mouseY)) return s;
			}
		}
		return null;
	}

	void moveSpriteToFront(Sprite s) {
		int index = -1;
		for (int i = 0; i < sprites.length; i++) {
			if (sprites[i] == s) index = i;
		}
		if (index < 0) return; // obj is not in sprites list
		Object tmp = sprites[index];
		for (int i = index; i > 0; i--) sprites[i] = sprites[i-1];
		sprites[0] = tmp;
		s.inval();
	}

	public void keyPressed(KeyEvent e) {
		int k = keyCodeFor(e);
		keydown[k] = true;
		if ((k == 10) || ((k >= 28) && (k <= 31))) keystrokes.addElement(new Double(k));
	}

	public void keyReleased(KeyEvent e) {
		int k = keyCodeFor(e);
		keydown[k] = false;
	}

	public void keyTyped(KeyEvent e) {
		int k = e.getKeyChar();

		if (askPrompt != null) {
			askPrompt.handleKeystroke(k, this);
			return;
		}

		if ((k >= 65) && (k <= 90)) k = k + 32;  // convert letters to lower case
		keystrokes.addElement(new Double(k));
	}

	int keyCodeFor(KeyEvent e) {
		int code = e.getKeyCode();
		if (code == 10) return 10;  // enter
		if (code == 37) return 28;  // left-arrow
		if (code == 38) return 30;  // up-arrow
		if (code == 39) return 29;  // right-arrow
		if (code == 40) return 31;  // down-arrow
		if ((code >= 65) && (code <= 90)) return code + 32;  // convert letters to lower case
		return Math.min(code, 255);
	}

	void clearkeys() {
		for (int i = 0; i < keydown.length; i++) keydown[i] = false;
		keystrokes = new Vector();
	}

	int soundLevel() {
		if (soundInputLine == null) return 0;

		int byteCount = soundInputLine.available();
		if (byteCount == 0) return soundLevel;
		byteCount = soundInputLine.read(soundInputBuf, 0, byteCount);

		int max = 0;
		for (int i = 0; i < (byteCount / 2); i++) {
			int sample = (soundInputBuf[2 * i] << 8) + soundInputBuf[(2 * i) + 1];
			if (sample >= 32768) sample = 65536 - sample;  // if negative, use absolute value
			if (sample > max) max = sample;
		}
		soundLevel = max / 327;  // scale to [0..100]
		return soundLevel;
	}

	void openSoundInput() {
		if (soundInputLine != null) soundInputLine.close();
		AudioFormat audioFormat = new AudioFormat(44100.0F, 16, 1, true, true);  // PCM, signed, little-endian
		DataLine.Info info = new DataLine.Info(TargetDataLine.class, audioFormat);
		try {
			soundInputLine = (TargetDataLine) AudioSystem.getLine(info);
			soundInputLine.open(audioFormat, soundInputBufSize);
			soundInputLine.start();
		} catch (LineUnavailableException e) {
			soundInputLine = null;
		}
	}

	void showAskPrompt(String promptString) {
		askPrompt = new AskPrompter(promptString);
		invalAll();
	}

	void hideAskPrompt() {
		if (askPrompt != null) lastAnswer = askPrompt.answerString;
		askPrompt = null;
		invalAll();
	}

	boolean askPromptShowing() {
		return askPrompt != null;
	}
}

// SoundPlayer manages playing sounds. Since there is only one, global SoundPlayer, all functions are implemented as static methods.
class SoundPlayer implements Runnable {

	static Vector activeSounds = new Vector();
	static Thread sndThread;

	static synchronized Object startSound(Object arg, Sprite sprite, LContext lc) {
		if (!(arg instanceof ScratchSound)) {
			Logo.error("argument of startSound must be a ScratchSound", lc);
			return new Object[0];
		}
		Object[] snds = activeSounds.toArray();
		for (int i = 0; i < snds.length; i++) {
			PlayingSound thisSnd = (PlayingSound) snds[i];
			if ((thisSnd.snd == (ScratchSound) arg) && (thisSnd.sprite == sprite)) {
				// sound is already playing for this sprite; stop it
				thisSnd.closeLine();
				activeSounds.remove(thisSnd);
			}
		}
		PlayingSound snd = new PlayingSound((ScratchSound) arg, sprite);
		snd.startPlaying(lc);
		activeSounds.add(snd);
		return snd;
	}

	static synchronized boolean isSoundPlaying(Object arg) {
		if (!(arg instanceof PlayingSound)) return false;
		return ((PlayingSound) arg).isPlaying();
	}

	static synchronized void stopSound(Object arg) {
		if (!(arg instanceof PlayingSound)) return;
		((PlayingSound) arg).closeLine();
		activeSounds.remove(arg);
	}

	static synchronized void stopSoundsForApplet(LContext lc) {
		PlayerPrims.stopMIDINotes();
		Vector stillActive = new Vector();
		for (Iterator iter = activeSounds.iterator(); iter.hasNext(); ) {
			PlayingSound playingSnd = (PlayingSound) iter.next();
			if (playingSnd.owner == lc) {
				playingSnd.closeLine();
			} else {
				stillActive.addElement(playingSnd);
			}
		}
		activeSounds = stillActive;
	}

	static synchronized void updateActiveSounds() {
		Vector stillActive = new Vector();
		for (Iterator iter = activeSounds.iterator(); iter.hasNext(); ) {
			PlayingSound snd = (PlayingSound) iter.next();
			if (snd.isPlaying()) {
				snd.writeSomeSamples();
				stillActive.addElement(snd);
			} else {
				snd.closeLine();
			}
		}
		activeSounds = stillActive;
	}

	static synchronized void startPlayer() {
		sndThread = new Thread(new SoundPlayer(), "SoundPlayer");
		sndThread.setPriority(Thread.MAX_PRIORITY);
		sndThread.start();
	}

	public void run() {
		Thread thisThread = Thread.currentThread();
		while (sndThread == thisThread) {
			SoundPlayer.updateActiveSounds();
			try { Thread.sleep(10); } catch (InterruptedException e) {};
		}
	}
}

// PlayingSound represents a sound that is playing.
class PlayingSound {

	LContext owner;
	ScratchSound snd;
	Sprite sprite;
	SourceDataLine line;
	int i;

	PlayingSound(ScratchSound snd, Sprite sprite) {
		this.snd = snd;
		this.sprite = sprite;
	}

	void startPlaying(LContext lc) {
		owner = lc;
		i = 0;
		if (line == null) openLine();
		writeSomeSamples();
	}

	boolean isPlaying() {
		if (line == null) return false;
		return line.getFramePosition() < (snd.samples.length / 2);
	}

	void writeSomeSamples() {
		if (line == null) return;
		int bytesToWrite = Math.min(line.available(), (snd.samples.length - i));
		if (bytesToWrite > 0) {
			if (!line.isRunning()) line.start();  // restart if needed
			int bytesWritten = line.write(snd.samples, i, bytesToWrite);
			i += bytesWritten;
		}
	}

	void openLine() {
		try {
			AudioFormat format = new AudioFormat((float) snd.rate, 16, 1, true, true);  // 16-bit, mono, signed, big-endian
			line = (SourceDataLine) AudioSystem.getLine(new DataLine.Info(SourceDataLine.class, format));
			line.open(format);
		} catch (LineUnavailableException e) {
			line = null;
			return;
		}
		line.start();
	}

	void closeLine() {
		if (line != null) {
			line.close();
			line = null;
		}
	}
}

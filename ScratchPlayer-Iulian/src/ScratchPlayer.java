import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.Font;
import java.awt.Insets;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Hashtable;

import javax.swing.Action;
import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.Keymap;

public class ScratchPlayer {
	public static void main(String[] args) {
		PlayerFrame playerFrame = new PlayerFrame();
	}
}

class PlayerFrame extends JFrame {
	PlayerFrame() {
		super("ScratchPlayer");
		setResizable(false);
		setLocation(380, 20);
		setVisible(true);
	}

	public void addNotify() {
		LContext lc;

		super.addNotify();
		lc = PlayerPrims.startup(null, null, getContentPane(), true);
		LogoConsole logoconsole = new LogoConsole("JavaLogo");
		logoconsole.init(lc);
		lc.tyo.println("Welcome to Logo!");
		pack();
	}
}

class LogoConsole extends JFrame {

	JPanel buttons;
	Listener cc;

	LogoConsole(String s) { super(s); }

	void init(LContext lc) {
		JPanel lcpanel = new JPanel();
		lcpanel.setLayout(new BorderLayout());
		getContentPane().add(lcpanel, BorderLayout.CENTER);

		cc = new Listener(20, 50);
		JScrollPane scrollpane = new JScrollPane(
				cc,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		lcpanel.add(scrollpane, BorderLayout.CENTER);
		cc.setFont(new Font("Courier", Font.BOLD, 12));
		cc.setMargin(new Insets(0, 5, 0, 0));
		cc.setWrapStyleWord(true);
		cc.setLineWrap(true);
		initAppleKeys(cc);
		pack();
		setVisible(true);
		addWindowListener(new ToplevelWindowListener());
		cc.addKeyListener(new TEKeyListener());
		cc.lc = lc;
		cc.lc.tyo = new PrintWriter(new TEStream(cc), true);
	}

	public void initAppleKeys(JTextArea text) {
		// define the actions
		DefaultEditorKit.CutAction cutaction = new DefaultEditorKit.CutAction();
		DefaultEditorKit.CopyAction copyaction = new DefaultEditorKit.CopyAction();
		DefaultEditorKit.PasteAction pasteaction = new DefaultEditorKit.PasteAction();
		SelectAllAction selectallaction = new SelectAllAction();

		// add the keymap to object and map various keys
		Keymap keymap = text.getKeymap();
		KeyStroke apple_x = KeyStroke.getKeyStroke(KeyEvent.VK_X, java.awt.event.InputEvent.META_MASK);
		KeyStroke apple_c = KeyStroke.getKeyStroke(KeyEvent.VK_C, java.awt.event.InputEvent.META_MASK);
		KeyStroke apple_v = KeyStroke.getKeyStroke(KeyEvent.VK_V, java.awt.event.InputEvent.META_MASK);
		KeyStroke apple_a = KeyStroke.getKeyStroke(KeyEvent.VK_A, java.awt.event.InputEvent.META_MASK);
		keymap.addActionForKeyStroke(apple_x, cutaction);
		keymap.addActionForKeyStroke(apple_c, copyaction);
		keymap.addActionForKeyStroke(apple_v, pasteaction);
		keymap.addActionForKeyStroke(apple_a, selectallaction);
		text.setKeymap(keymap);
	}
}

class ToplevelWindowListener extends WindowAdapter {
	public void windowClosing(WindowEvent e) { System.exit(0); }
}

class TEKeyListener extends KeyAdapter {

	public void keyPressed(KeyEvent e) {
		Listener cc = (Listener) e.getComponent();
		char key = e.getKeyChar();
		int code = e.getKeyCode();
		cc.lc.timeToStop = true;
		SoundPlayer.stopSoundsForApplet(cc.lc);
		if (key=='\u0001') { cc.selectAll(); e.consume(); return; }	// ctrl-A = select all
		if (key== '\n' && e.getKeyCode() == 10) { cc.handlecr(); e.consume(); return; } 	// patch: don't interpret ctrl-C as a return
		}
}

//--------------- for Mac Key handling -----------------
class SelectAllAction extends AbstractAction implements Action {
	public void actionPerformed(ActionEvent e) {
		JTextArea text = (JTextArea) e.getSource();
		text.selectAll();
	}
}

class Listener extends JTextArea {

	LContext lc;
	String file, dir;

	Listener(int h, int w) { super(h, w); };

	void handlecr() {
		String s = getText();
		int sol = findStartOfLine(s, getCaretPosition());
		int eol = findEndOfLine(s, sol);
		if (eol == s.length()) append("\n");
		setCaretPosition(eol + 1);
		LogoCommandRunner.startLogoThread(s.substring(sol, eol), lc);
	}

	int findStartOfLine(String s, int i) {
		int val = s.lastIndexOf(10, i - 1);
		if (val < 0) return 0;
		return val + 1;
	}

	int findEndOfLine(String s, int i) {
		int val = s.indexOf('\n', i);
		if (val < 0) return s.length();
		return val;
	}
}

class TEStream extends OutputStream {

	JTextArea te;
	String buffer = "";

	public TEStream(JTextArea te) { this.te = te; }

	public void write(int n) {
		if (n == 10) buffer += '\n';
		else if (n == 13) return;
		else buffer += (char) n;
	}

	public void flush() {
		int pos = te.getCaretPosition();
		te.insert(buffer, pos);
		te.setCaretPosition(pos + buffer.length());
		buffer = "";
	}
}

class LContext {
	static final boolean isApplet = false;

	Hashtable oblist = new Hashtable();
	Hashtable props = new Hashtable();
	MapList iline;
	Symbol cfun, ufun;
	Object ufunresult, juststop = new Object();
	boolean mustOutput, timeToStop;
	int priority = 0;
	Object[] locals;
	String errormessage;
	String codeBase;
	String projectURL;
	PlayerCanvas canvas;
	Sprite who;
	Thread logoThread;
	PrintWriter tyo;
	boolean autostart;
}

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Toolkit;
import java.lang.Integer;
import java.lang.Math;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Vector;

class SpritePrims extends Primitives {

	static final String[] primlist = {
		"who", "0",
		"talkto", "1",
		"xpos", "0",
		"ypos", "0",
		"setx", "1",
		"sety", "1",
		"%left", "0",		// %left, %right, %top, and %bottom are in screen coordinates (origin at top-left)
		"%top", "0",
		"%right", "0",
		"%bottom", "0",
		"%setleft", "1",
		"%settop", "1",
		"%w", "0",
		"%h", "0",
		"costume", "0",
		"setcostume", "3",
		"%scale", "0",
		"setscale", "1",
		"heading", "0",
		"setheading", "1",
		"rotationstyle", "0",
		"setrotationstyle", "1",
		"show", "0",
		"hide", "0",
		"changed", "0",
		"containsPoint?", "2",
		"alpha", "0",
		"setalpha", "1",
		"color", "0",
		"setcolor", "1",
		"brightness", "0",
		"setbrightness", "1",
		"fisheye", "0",
		"setfisheye", "1",
		"whirl", "0",
		"setwhirl", "1",
		"mosaic", "0",
		"setmosaic", "1",
		"pixelate", "0",
		"setpixelate", "1",
		"beep", "0",
		"startSound", "1",
		"isSoundPlaying?", "1",
		"stopSound", "1",
		"stopAllSounds", "0",
		"setPenDown", "1",
		"setPenColor", "1",
		"penSize", "0",
		"setPenSize", "1",
		"penHue", "0",
		"setPenHue", "1",
		"penShade", "0",
		"setPenShade", "1",
		"clearPenTrails", "0",
		"stampCostume", "0",
		"newcolor", "3",
		"touchingSprite?", "1",
		"touchingColor?", "1",
		"colorTouchingColor?", "2",
		"isShowing", "1",
		"talkbubble", "1",
		"thinkbubble", "1",
		"updateBubble", "0",
		"watcher?", "1",
		"setWatcherXY", "3",
		"setWatcherColorAndLabel", "3",
		"setWatcherSliderMinMax", "3",
		"setWatcherMode", "2",
		"setWatcherText", "2",
		"isDraggable", "0",
		"setDraggable", "1",
		"showWatcher", "1",
		"hideWatcher", "1",
		"listContents", "1",
		"hasKey", "2",
		"listWatcher?", "1",
		"newListWatcher", "0",
		"setListWatcherXY", "3",
		"setListWatcherWidthHeight", "3",
		"setListWatcherLabel", "2",
		"setListWatcherList", "2",
		"highlightListWatcherIndex", "2",
		"clearListWatcherHighlights", "1",
		"askbubble", "1",
		"showAskPrompt", "1",
		"hideAskPrompt", "0",
		"askPromptShowing?", "0",
		"lastAnswer", "0",
		"isVisible", "1",
		"newVarWatcher", "1",
		"watcherX", "1",
		"watcherY", "1",
	};

	public String[] primlist() { return primlist; }

	public Object dispatch(int offset, Object[] args, LContext lc) {
		Sprite who = lc.who;

		switch (offset) {
			case 0: return ((who == null) ? (Object) new Object[0] : (Object) who);
			case 1: lc.who = (args[0] instanceof Sprite) ? (Sprite) args[0] : null; return null;
			case 2: return new Double((who == null) ? 0.0 : who.x);
			case 3: return new Double((who == null) ? 0.0 : who.y);
			case 4: who.x = Logo.aDouble(args[0], lc); return null;
			case 5: who.y = Logo.aDouble(args[0], lc); return null;
			case 6: return new Double(who.screenX());
			case 7: return new Double(who.screenY());
			case 8: return new Double(who.screenX() + who.outImage().getWidth(null));
			case 9: return new Double(who.screenY() + who.outImage().getHeight(null));
			case 10: who.setscreenX(Logo.aDouble(args[0], lc)); return null;
			case 11: who.setscreenY(Logo.aDouble(args[0], lc)); return null;
			case 12: return new Double(who.outImage().getWidth(null));
			case 13: return new Double(who.outImage().getHeight(null));
			case 14: return who.costume;
			case 15: who.setcostume(args[0], args[1], args[2], lc); who.costumeChanged(); return null;
			case 16: return new Double(who.scale);
			case 17: who.setscale(args[0], lc); return null;
			case 18: return prim_heading(lc);
			case 19: who.rotationDegrees = Logo.aDouble(args[0], lc) % 360; who.costumeChanged(); return null;
			case 20: return new Double(who.rotationstyle);
			case 21: who.rotationstyle = Logo.anInt(args[0], lc); who.costumeChanged(); return null;
			case 22: who.show(); return null;
			case 23: who.hide(); return null;
			case 24: who.inval(); return null;
			case 25: return prim_containsPoint(args[0], args[1], lc);
			case 26: return new Double(who.alpha);
			case 27: who.setalpha(args[0], lc); return null;
			case 28: return new Double(who.color);
			case 29: who.color = Logo.aDouble(args[0], lc); who.filterChanged = true; return null;
			case 30: return new Double(who.brightness);
			case 31: who.brightness = Logo.aDouble(args[0], lc); who.filterChanged = true; return null;
			case 32: return new Double(who.fisheye);
			case 33: who.fisheye = Logo.aDouble(args[0], lc); who.filterChanged = true; return null;
			case 34: return new Double(who.whirl);
			case 35: who.whirl = Logo.aDouble(args[0], lc); who.filterChanged = true; return null;
			case 36: return new Double(who.mosaic);
			case 37: who.mosaic = Logo.aDouble(args[0], lc); who.filterChanged = true; return null;
			case 38: return new Double(who.pixelate);
			case 39: who.pixelate = Logo.aDouble(args[0], lc); who.filterChanged = true; return null;
			case 40: Toolkit.getDefaultToolkit().beep(); return null;
			case 41: return SoundPlayer.startSound(args[0], who, lc);
			case 42: return new Boolean(SoundPlayer.isSoundPlaying(args[0]));
			case 43: SoundPlayer.stopSound(args[0]); return null;
			case 44: SoundPlayer.stopSoundsForApplet(lc); return null;
			case 45: who.setPenDown(Logo.aBoolean(args[0], lc)); return null;
			case 46: if (args[0] instanceof Color) who.setPenColor((Color) args[0]); return null;
			case 47: return new Double(who.penSize);
			case 48: who.penSize = Logo.anInt(args[0], lc); return null;
			case 49: return new Double(who.penHue);
			case 50: who.setPenHue(Logo.aDouble(args[0], lc)); return null;
			case 51: return new Double(who.penShade);
			case 52: who.setPenShade(Logo.aDouble(args[0], lc)); return null;
			case 53: lc.canvas.clearPenTrails(); return null;
			case 54: lc.canvas.stampCostume(who); return null;
			case 55: return prim_newcolor(args[0], args[1], args[2], lc);
			case 56: return new Boolean(who.touchingSprite(args[0], lc));
			case 57: return new Boolean(who.touchingColor(args[0], lc));
			case 58: return new Boolean(who.colorTouchingColor(args[0], args[1], lc));
			case 59: return new Boolean((args[0] instanceof Sprite) && ((Sprite) args[0]).isShowing());
			case 60: who.talkbubble(args[0], false, true, lc); return null;
			case 61: who.talkbubble(args[0], false, false, lc); return null;
			case 62: who.updateBubble(); return null;
			case 63: return new Boolean(args[0] instanceof Watcher);
			case 64: prim_setWatcherXY(args[0], args[1], args[2], lc); return null;
			case 65: prim_setWatcherColorAndLabel(args[0], args[1], args[2], lc); return null;
			case 66: prim_setWatcherSliderMinMax(args[0], args[1], args[2], lc); return null;
			case 67: prim_setWatcherMode(args[0], args[1], lc); return null;
			case 68: prim_setWatcherText(args[0], args[1], lc); return null;
			case 69: return new Boolean(who.isDraggable);
			case 70: who.isDraggable = Logo.aBoolean(args[0], lc); return null;
			case 71: ((Watcher) args[0]).show(); return null;
			case 72: ((Watcher) args[0]).hide(); return null;
			case 73: return prim_listContents(args[0], lc);
			case 74: return prim_hasKey(args[0], args[1], lc);
			case 75: return new Boolean(args[0] instanceof ListWatcher);
			case 76: return prim_newListWatcher(lc);
			case 77: prim_setListWatcherXY(args[0], args[1], args[2], lc); return null;
			case 78: prim_setListWatcherWidthHeight(args[0], args[1], args[2], lc); return null;
			case 79: prim_setListWatcherLabel(args[0], args[1], lc); return null;
			case 80: prim_setListWatcherList(args[0], args[1], lc); return null;
			case 81: prim_highlightListWatcherIndex(args[0], args[1], lc); return null;
			case 82: prim_clearListWatcherHighlights(args[0], lc); return null;
			case 83: who.talkbubble(args[0], true, true, lc); return null;
			case 84: lc.canvas.showAskPrompt(Logo.aString(args[0], lc)); return null;
			case 85: lc.canvas.hideAskPrompt(); return null;
			case 86: return new Boolean(lc.canvas.askPromptShowing());
			case 87: return lc.canvas.lastAnswer;
			case 88: return new Boolean((args[0] instanceof Sprite) && ((Sprite) args[0]).isVisible());
			case 89: return prim_newVarWatcher(args[0], lc);
			case 90: return prim_watcherX(args[0], lc);
			case 91: return prim_watcherY(args[0], lc);
		}
		return null;
	}

	Object prim_heading(LContext lc) {
		double degrees = lc.who.rotationDegrees % 360;
		if (degrees > 180.0) degrees = degrees - 360.0;
		return new Double(degrees);
	}

	Object prim_containsPoint(Object arg1, Object arg2, LContext lc) {
		if (lc.who == null) return new Boolean(false);
		int screenX = Logo.anInt(arg1, lc) + Sprite.originX;
		int screenY = Sprite.originY - Logo.anInt(arg2, lc);
		return new Boolean(lc.who.containsPoint(screenX, screenY));
	}

	Color prim_newcolor(Object arg1, Object arg2, Object arg3, LContext lc) {
		int r = Logo.anInt(arg1, lc);
		int g = Logo.anInt(arg2, lc);
		int b = Logo.anInt(arg3, lc);
		return new Color(r, g, b);
	}

	void prim_setWatcherXY(Object arg1, Object arg2, Object arg3, LContext lc) {
		if (!(arg1 instanceof Watcher)) return;
		Watcher w = (Watcher) arg1;
		w.inval();
		w.box.x = Logo.anInt(arg2, lc);
		w.box.y = Logo.anInt(arg3, lc);
		w.inval();
	}

	void prim_setWatcherColorAndLabel(Object arg1, Object arg2, Object arg3, LContext lc) {
		if (!(arg1 instanceof Watcher)) return;
		Watcher w = (Watcher) arg1;
		w.inval();
		w.readout.color = (Color) arg2;
		w.label = (String) arg3;
		w.inval();
	}

	void prim_setWatcherSliderMinMax(Object arg1, Object arg2, Object arg3, LContext lc) {
		if (!(arg1 instanceof Watcher)) return;
		Watcher w = (Watcher) arg1;
		w.sliderMin = Logo.aDouble(arg2, lc);
		w.sliderMax = Logo.aDouble(arg3, lc);
		w.isDiscrete =
			((Math.round(w.sliderMin) == w.sliderMin) &&
			 (Math.round(w.sliderMax) == w.sliderMax));
	}

	void prim_setWatcherMode(Object arg1, Object arg2, LContext lc) {
		if (!(arg1 instanceof Watcher)) return;
		Watcher w = (Watcher) arg1;
		int mode = Logo.anInt(arg2, lc);
		w.inval();
		w.mode = Math.max(0, Math.min(mode, 3));
		w.inval();
	}

	void prim_setWatcherText(Object arg1, Object arg2, LContext lc) {
		if (!(arg1 instanceof Watcher)) return;
		Watcher w = (Watcher) arg1;
		String s = Logo.prs(arg2);
		if (arg2 instanceof Double) {
			double n = ((Double) arg2).doubleValue();
			double absN = Math.abs(n);
			DecimalFormat formater = new DecimalFormat("0.#");
			if (absN < 1.0) formater = new DecimalFormat("0.######");
			if ((absN < 0.00001) || (absN >= 1000000)) formater = new DecimalFormat("0.###E0");
			if (absN == 0.0) formater = new DecimalFormat("0.#");
			s = formater.format(n);
		}
		if (s.equals(w.readout.contents)) return;
		w.inval();
		w.readout.contents = s;
		w.inval();
	}

	Object prim_listContents(Object arg1, LContext lc) {
		if (!(arg1 instanceof Object [])) {
			Logo.error("argument must be a list", lc);
			return "";
		}
		Object[] list = (Object[]) arg1;
		if (list.length == 0) return "";
		StringBuffer buf = new StringBuffer(1000);

		// if all list elements are single-character strings, don't insert spaces
		boolean insertSpace = false;
		for (int i = 0; i < list.length; i++) {
			Object el = list[i];
			if (!(el instanceof String)) el = Logo.prs(el);
			if (((String) el).length() > 1) insertSpace = true;
		}

		for (int i = 0; i < list.length; i++) {
			Object el = list[i];
			if (!(el instanceof String)) el = Logo.prs(el);
			buf.append(el);
			if (insertSpace) buf.append(" ");
		}
		if (insertSpace) buf.deleteCharAt(buf.length() - 1);  // remove trailing space
		return buf.toString();
	}

	Object prim_hasKey(Object arg1, Object arg2, LContext lc) {
		Hashtable plist = (Hashtable) lc.props.get(arg1);
		if (plist == null) return new Boolean(false);
		return new Boolean(plist.containsKey(arg2));
	}

	Object prim_newListWatcher(LContext lc) {
		ListWatcher w = new ListWatcher(lc);
		Object[] newSprites = new Object[lc.canvas.sprites.length + 1];
		for (int i = 0; i < lc.canvas.sprites.length; i++) {
			newSprites[i] = lc.canvas.sprites[i];
		}
		newSprites[newSprites.length - 1] = w;
		lc.canvas.sprites = newSprites;
		return w;
	}

	void prim_setListWatcherXY(Object arg1, Object arg2, Object arg3, LContext lc) {
		if (!(arg1 instanceof ListWatcher)) return;
		ListWatcher w = (ListWatcher) arg1;
		w.inval();
		w.box.x = Logo.anInt(arg2, lc);
		w.box.y = Logo.anInt(arg3, lc);
		w.inval();
	}

	void prim_setListWatcherWidthHeight(Object arg1, Object arg2, Object arg3, LContext lc) {
		if (!(arg1 instanceof ListWatcher)) return;
		ListWatcher w = (ListWatcher) arg1;
		w.inval();
		w.box.w = Logo.anInt(arg2, lc);
		w.box.h = Logo.anInt(arg3, lc);
		w.scrollBarHeight = w.box.h - ListWatcher.TOP_MARGIN - ListWatcher.BOTTOM_MARGIN;
		w.pane.w = w.box.w;
		w.inval();
	}

	void prim_setListWatcherLabel(Object arg1, Object arg2, LContext lc) {
		if (!(arg1 instanceof ListWatcher)) return;
		ListWatcher w = (ListWatcher) arg1;
		w.inval();
		w.listTitle = Logo.aString(arg2, lc);
		w.inval();
	}

	void prim_setListWatcherList(Object arg1, Object arg2, LContext lc) {
		if (!(arg1 instanceof ListWatcher)) return;
		if (!(arg2 instanceof Object[])) return;
		ListWatcher w = (ListWatcher) arg1;
		w.setList((Object[]) arg2);
		w.inval();
	}

	void prim_highlightListWatcherIndex(Object arg1, Object arg2, LContext lc) {
		if (!(arg1 instanceof ListWatcher)) return;
		ListWatcher w = (ListWatcher) arg1;
		w.highlightIndex(Logo.anInt(arg2, lc));
		w.inval();
	}

	void prim_clearListWatcherHighlights(Object arg1, LContext lc) {
		if (!(arg1 instanceof ListWatcher)) return;
		ListWatcher w = (ListWatcher) arg1;
		w.clearHighlights();
		w.inval();
	}

	Watcher prim_newVarWatcher(Object arg1, LContext lc) {
		return new Watcher(lc);
	}

	Object prim_watcherX(Object arg1, LContext lc) {
		if ((arg1 instanceof Watcher)) return new Double(((Watcher) arg1).box.x);
		if ((arg1 instanceof ListWatcher)) return new Double(((ListWatcher) arg1).box.x);
		return new Double(0);
	}

	Object prim_watcherY(Object arg1, LContext lc) {
		if ((arg1 instanceof Watcher)) return new Double(((Watcher) arg1).box.y);
		if ((arg1 instanceof ListWatcher)) return new Double(((ListWatcher) arg1).box.y);
		return new Double(0);
	}
}

interface Drawable {
	boolean isShowing();
	Rectangle rect();
	Rectangle fullRect();
	void paint(Graphics g);
	void paintBubble(Graphics g);
	void dragTo(int mouseX, int mouseY);
	void mouseDown(int mouseX, int mouseY);
}

class Sprite implements Drawable {
	static final int originX = PlayerCanvas.width / 2;
	static final int originY = ((PlayerCanvas.height - PlayerCanvas.topBarHeight) / 2) + PlayerCanvas.topBarHeight;

	PlayerCanvas canvas;
	BufferedImage costume, rotatedCostume, filteredCostume, tempImage;
	double x, y;
	boolean isShowing = true;
	boolean isDraggable = false;
	double alpha = 1.0;
	double scale = 1;
	double rotationDegrees = 90;
	int rotationstyle;						// 0 - normal, 1 - flip, 2 - none
	int rotationX, rotationY;			// costume's rotation center
	int offsetX, offsetY;					// offset when rotated
	Bubble bubble;
	boolean penDown;
	int lastPenX, lastPenY;
	Color penColor = new Color(0, 0, 255);
	int penSize = 1;							// range 0-500
	double penHue;								// range 0-200
	double penShade;							// range 0-100

	// image effects
	boolean filterChanged = false;
	double color;
	double brightness;
	double fisheye;
	double whirl;
	double mosaic;
	double pixelate;
	ImageFilter imageFilter = new ImageFilter();

	Sprite(LContext lc) {
		setPenColor(penColor);
		if (lc != null) canvas = lc.canvas;
	}

	int screenX() { return originX + (int)(x - offsetX); }
	int screenY() { return originY + (int)(-y - offsetY); }
	void setscreenX(double newX) { x = newX + offsetX - originX; }
	void setscreenY(double newY) { y = -(newY + offsetY - originY); }

	public void mouseDown(int mouseX, int mouseY) { }

	void setStageOffset() {
		// used to ensure stage is centered
		x = y = 0.0;
		offsetX = costume.getWidth(null) / 2;
		offsetY = costume.getHeight(null) / 2;
	}

	public void dragTo(int mouseX, int mouseY) {
		inval();
		setscreenX(mouseX);
		setscreenY(mouseY);
		updateBubble();
		inval();
	}

	boolean containsPoint(int pointX, int pointY) {
		BufferedImage img = outImage();
		int left = screenX();
		int top = screenY();
		int w = img.getWidth(null);
		int h = img.getHeight(null);
		if ((pointX < left) || (pointX >= (left + w)) ||
				(pointY < top) || (pointY >= (top + h))) return false; // outside bounding box
		int rgb = img.getRGB(pointX - left, pointY - top);
		return (rgb & 0xFF000000) != 0;
	}

	boolean touchingSprite(Object arg1, LContext lc) {
		if (!(arg1 instanceof Sprite)) {
			Logo.error("argument must be a Sprite", lc);
			return false;
		}
		Sprite s = (Sprite) arg1;
		Rectangle r = rect().intersection(s.rect());  // rectangle containing overlap
		if ((r.width <= 0) || (r.height <= 0)) return false;

		BufferedImage img1 = outImage();
		BufferedImage img2 = s.outImage();

		int startX1 = r.x - screenX();
		int startY1 = r.y - screenY();
		int startX2 = r.x - s.screenX();
		int y2 = r.y - s.screenY();
		for (int y1 = startY1; y1 < (startY1 + r.height); y1++) {
			int x2 = startX2;
			for (int x1 = startX1; x1 < (startX1 + r.width); x1++) {
				int pix1 = img1.getRGB(x1, y1);
				int pix2 = img2.getRGB(x2, y2);
				if (((pix1 & 0xFF000000) != 0) && ((pix2 & 0xFF000000) != 0)) return true;
				x2++;
			}
			y2++;
		}
		return false;
	}

	boolean touchingColor(Object arg1, LContext lc) {
		if (!(arg1 instanceof Color)) {
			Logo.error("argument of touchingColor? must be a Color", lc);
			return false;
		}
		int c = ((Color) arg1).getRGB() | 0xFF000000;
		Rectangle r = rect();
		BufferedImage myImg = outImage();
		BufferedImage worldImg = lc.canvas.drawAreaWithoutSprite(r, this);

		for (int y = 0; y < r.height; y++) {
			for (int x = 0; x < r.width; x++) {
				if ((myImg.getRGB(x, y) & 0xFF000000) != 0) {
					if (colorsMatch(worldImg.getRGB(x, y), c)) {
						worldImg.flush();
						return true;
					}
				}
			}
		}
		worldImg.flush();
		return false;
	}

	boolean colorTouchingColor(Object arg1, Object arg2, LContext lc) {
		if (!(arg1 instanceof Color) || !(arg2 instanceof Color)) {
			Logo.error("the arguments of colorTouchingColor? must be Colors", lc);
			return false;
		}
		int c1 = ((Color) arg1).getRGB() | 0xFF000000;
		int c2 = ((Color) arg2).getRGB() | 0xFF000000;
		Rectangle r = rect();
		BufferedImage img1 = outImage();
		BufferedImage img2 = lc.canvas.drawAreaWithoutSprite(r, this);

		for (int y = 0; y < r.height; y++) {
			for (int x = 0; x < r.width; x++) {
				if (colorsMatch(img1.getRGB(x, y), c1) && colorsMatch(img2.getRGB(x, y), c2)) {
					img2.flush();
					return true;
				}
			}
		}
		img2.flush();
		return false;
	}

	boolean colorsMatch(int c1, int c2) {
		if ((c1 & 0xFF000000) != (c2 & 0xFF000000)) return false;
		if (((c1 >> 16) & 0xF8) != ((c2 >> 16) & 0xF8)) return false;
		if (((c1 >>  8) & 0xF8) != ((c2 >>  8) & 0xF8)) return false;
		if (((c1 & 0xFFFF00) == 0) && ((c2 & 0xFFFF00) == 0)) {  // both colors have zero R and G
			if (((c1 & 0xFF) <= 8) && ((c2 & 0xFF) <= 8)) return true;  // both colors considered black
		}
		if ((c1 & 0xF8) != (c2 & 0xF8)) return false;
		return true;
	}

	void setalpha(Object arg1, LContext lc) {
		double newAlpha = Logo.aDouble(arg1, lc);
		if (newAlpha < 0.0) newAlpha = -newAlpha;
		if (newAlpha > 1.0) newAlpha = 1.0;
		alpha = newAlpha;
		inval();
	}

	void setcostume(Object arg1, Object arg2, Object arg3, LContext lc) {
		if (!(arg1 instanceof BufferedImage)) return;
		rotationX = Logo.anInt(arg2, lc);
		rotationY = Logo.anInt(arg3, lc);
		if (costume != null) inval();
		costume = (BufferedImage) arg1;
		rotateAndScale();
		inval();
	}

	void costumeChanged() {
		inval();
		rotateAndScale();
		inval();
	}

	void setscale(Object arg1, LContext lc) {
		double newScale = Logo.aDouble(arg1, lc);
		double minW = Math.min(costume.getWidth(null), 10);
		double minH = Math.min(costume.getHeight(null), 10);
		double minScale = Math.max(minW / 480.0, minH / 360.0);
		double maxScale = Math.min(480.0 / costume.getWidth(null), 360.0 / costume.getHeight(null));
		scale = Math.min(Math.max(newScale, minScale), maxScale);
		costumeChanged();
	}

	void rotateAndScale() {
		double degrees;
		int w, h;

		filterChanged = true;
		degrees = (rotationstyle == 0) ? rotationDegrees : 90;

		if ((rotatedCostume != null) && (rotatedCostume != costume)) rotatedCostume.flush();
		if ((scale == 1) && (rotationDegrees == 90)) {
			rotatedCostume = costume;
			offsetX = rotationX;
			offsetY = rotationY;
			return;
		}

		w = costume.getWidth(null);
		h = costume.getHeight(null);

		double radians = Math.toRadians(degrees - 90);
		AffineTransform transform = AffineTransform.getRotateInstance(radians, (w / 2), (h / 2));
		transform.scale(scale, scale);
		AffineTransformOp op = new AffineTransformOp(transform, AffineTransformOp.TYPE_BILINEAR);

		Rectangle2D.Float outRect = (Rectangle2D.Float) op.getBounds2D((BufferedImage) costume);
		float offx = -outRect.x;
		float offy = -outRect.y;
		transform = AffineTransform.getRotateInstance(radians, (w / 2) + offx, (h / 2) + offy);
		transform.translate(offx, offy);
		transform.scale(scale, scale);
		op = new AffineTransformOp(transform, AffineTransformOp.TYPE_BILINEAR);
		rotatedCostume = op.filter((BufferedImage) costume, null);

		// compute rotation offset
		transform = AffineTransform.getRotateInstance(radians, 0, 0);
		transform.scale(scale, scale);
		Point2D newRotationOffset = transform.transform(new Point2D.Double(rotationX - (w / 2), rotationY - (h / 2)), null);

		offsetX = (int) (newRotationOffset.getX() + (rotatedCostume.getWidth(null) / 2));
		offsetY = (int) (newRotationOffset.getY() + (rotatedCostume.getHeight(null) / 2));

		if (rotationstyle == 1) { // flip
			double positiveDegrees = rotationDegrees < 0 ? rotationDegrees + 360 : rotationDegrees;
			if (positiveDegrees <= 180) return; // not pointing left, so don't flip

			int newW = rotatedCostume.getWidth(null);
			transform = AffineTransform.getScaleInstance(-1, 1);
			transform.translate(-newW, 0);
			op = new AffineTransformOp(transform, AffineTransformOp.TYPE_BILINEAR);
			rotatedCostume = op.filter(rotatedCostume, null);

			offsetX = (int) ((newW / 2) - (scale * (rotationX - (w / 2))));
			offsetY = (int) (scale * rotationY);
		}
	}

	void show() { isShowing = true; inval(); }
	void hide() { isShowing = false; inval(); }
	public boolean isShowing() { return isShowing; }
	public boolean isVisible() { return isShowing && (alpha > 0.0); }

	public Rectangle rect() {
		BufferedImage img = outImage();
		if (img == null) {
			return new Rectangle(screenX(), screenY(), 600, 600);
		}
		return new Rectangle(screenX(), screenY(), img.getWidth(null), img.getHeight(null));
	}

	public Rectangle fullRect() { // union of sprite rect() and it's bubble rect(), if sprite has a bubble
		Rectangle r = rect();
		if (bubble != null) r = r.union(bubble.rect());
		return r;
	}

	void inval() { canvas.inval(fullRect()); }

	public void paint(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		if (filterChanged) applyFilters();
		if (alpha != 1.0) {
			Composite oldComposite = g2.getComposite();
			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) alpha));
			g2.drawImage(outImage(), screenX(), screenY(), null);
			g2.setComposite(oldComposite);
		} else {
			g2.drawImage(outImage(), screenX(), screenY(), null);
		}
	}

	public void paintBubble(Graphics g) {
		if (bubble != null) bubble.paint(g);
	}

	void talkbubble(Object arg, boolean askFlag, boolean talkFlag, LContext lc) {
		String s = (arg instanceof String) ? (String) arg : Logo.prs(arg);
		inval();
		bubble = null;
		if (s.length() == 0) return;  // remove bubble
		bubble = new Bubble();
		if (askFlag) bubble.beAskBubble();
		if (!talkFlag) bubble.beThinkBubble(true);
		bubble.setContents(s);
		if ((rotationDegrees >= 0) && (rotationDegrees <= 180)) bubble.pointLeft = true;
		updateBubble();
	}

	void updateBubble() {
		int extra = 3;  // extra x space
		int screenRight = PlayerCanvas.width - extra;  // subtract extra and border pixels
		if (bubble == null) return;
		inval();

		// decide which side of the sprite the bubble should be on
		Rectangle r = rect();  // this sprite's screen rectangle
		boolean bubbleOnRight = bubble.pointLeft;
		int[] insets = bubbleInsets();
		if (bubbleOnRight && ((r.x + r.width - insets[1] + bubble.w + extra) > screenRight)) bubbleOnRight = false;
		if (!bubbleOnRight && ((r.x + insets[0] - bubble.w - extra) < 0)) bubbleOnRight = true;

		if (bubbleOnRight) {
			bubble.pointLeft = true;
			bubble.x = r.x + r.width - insets[1] + extra;
		} else {
			bubble.pointLeft = false;
			bubble.x = r.x + insets[0] - bubble.w - extra;
		}

		// make sure bubble stays on screen
		if ((bubble.x + bubble.w) > screenRight) bubble.x = screenRight - bubble.w;
		if (bubble.x < extra) bubble.x = extra;
		bubble.y = Math.max(r.y - bubble.h - 12, 25 + extra);
		if ((bubble.y + bubble.h) > PlayerCanvas.height) {
			bubble.y = PlayerCanvas.height - bubble.h;
		}
		inval();
	}

	int[] bubbleInsets() {
		BufferedImage img = outImage();
		int w = img.getWidth();
		int h = img.getHeight();
		int left = w;
		int right = w;
		int startY = -1;  // first row with at least one opaque pixel; if < 0 then all rows so far have been entirely transparent

		for (int y = 0 ; y < h; y++) {
			boolean opaqueSeen = false;
			int alpha;
			for (int inset = 0; inset < (Math.max(left, right)); inset++) {
				alpha = img.getRGB(inset, y) & 0xFF000000;
				if ((alpha != 0) && (inset < left)) { left = inset; opaqueSeen = true; }
				alpha = img.getRGB(w - inset - 1, y) & 0xFF000000;
				if ((alpha != 0) && (inset < right)) { right = inset; opaqueSeen = true; }
			}
			if (startY < 0) {
				if (opaqueSeen) startY = y;
			} else {
				if (y >= (startY + 10)) break;  // exit loop
			}
		}
		int[] result = new int[2];
		result[0] = left;
		result[1] = right;
		return result;
	}

	void setPenDown(boolean newPenDown) {
		if (newPenDown == penDown) return; // no change
		if (newPenDown) lastPenX = lastPenY = -1000000; // putting pen down
		canvas.updatePenTrailForSprite(this);
		penDown = newPenDown;
	}

	void setPenColor(Color c) {
		float[] hsb = Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), null);
		penColor = c;
		penHue = 200.0 * hsb[0];
		float saturation = hsb[1];
		float brightness = hsb[2];
		if (brightness == 1.0) {
			penShade = 50.0 + (50.0 * (1.0 - saturation));
		} else {
			penShade = 50.0 * brightness;
		}
	}

	void setPenHue(double newHue) {
		penHue = newHue % 200.0;
		if (penHue < 0.0) penHue = 200.0 + penHue;
		setPenShade(penShade);
	}

	void setPenShade(double newShade) {
		penShade = newShade % 200.0;
		if (penShade < 0.0) penShade = 200.0 + penShade;
		float normalizedShade = (float) ((penShade > 100.0) ? 200.0 - penShade : penShade);
		if (normalizedShade <= 50.0) {
			float b = (normalizedShade + 10.0F) / 60.0F;
			penColor = new Color(Color.HSBtoRGB((float) (penHue / 200.0), 1.0F, b));
		} else {
			float s = ((100.0F - normalizedShade) + 10.0F) / 60.0F;
			penColor = new Color(Color.HSBtoRGB((float) (penHue / 200.0), s, 1.0F));
		}
	}

	BufferedImage outImage() {
		if (filteredCostume != null) return filteredCostume;
		if (rotatedCostume != null) return rotatedCostume;
		return costume;
	}

	void applyFilters() {
		if (!filtersActive()) {
			filteredCostume = null;
			filterChanged = false;
			return;
		}
		imageFilter.setSourceImage((rotatedCostume != null) ? rotatedCostume : costume);
		if (color != 0.0) imageFilter.applyHueShift((int) color);
		if (brightness != 0.0) imageFilter.applyBrightnessShift((int) brightness);
		if (whirl != 0.0) imageFilter.applyWhirl(whirl);
		if (fisheye != 0.0) imageFilter.applyFisheye(fisheye);
		if (Math.abs(pixelate) >= 5.0) imageFilter.applyPixelate(pixelate);
		if (Math.abs(mosaic) >= 5.0) imageFilter.applyMosaic(mosaic);
		filteredCostume = imageFilter.filteredImage;
		filterChanged = false;
	}

	boolean filtersActive() {
		if ((color != 0.0) ||
				(brightness != 0.0) ||
				(fisheye != 0.0) ||
				(whirl != 0.0) ||
				(Math.abs(mosaic) >= 5.0) ||
				(Math.abs(pixelate) >= 5.0)) {
			return true;
		}
		return false;
	}
}

class StretchyBox implements Drawable {
	int x, y;
	int w = 100, h = 75;

	BufferedImage cornerTL;
	BufferedImage cornerTR;
	BufferedImage cornerBL;
	BufferedImage cornerBR;
	BufferedImage edgeTop;
	BufferedImage edgeBottom;
	BufferedImage edgeLeft;
	BufferedImage edgeRight;
	Color fillColor;

	void setFrameImage(BufferedImage img) {
		int w = img.getWidth(null);
		int h = img.getHeight(null);
		int halfW = w / 2;
		int halfH = h / 2;
		cornerTL = img.getSubimage(0, 0, halfW, halfH);
		cornerTR = img.getSubimage(w - halfW, 0, halfW, halfH);
		cornerBL = img.getSubimage(0, h - halfH, halfW, halfH);
		cornerBR = img.getSubimage(w - halfW, h - halfH, halfW, halfH);
		edgeTop = img.getSubimage(halfW, 0, 1, halfH);
		edgeBottom = img.getSubimage(halfW, h - halfH, 1, halfH);
		edgeLeft = img.getSubimage(0, halfH, halfW, 1);
		edgeRight = img.getSubimage(w - halfW, halfH, halfW, 1);
		fillColor = new Color(img.getRGB(halfW, halfH));
	}

	public boolean isShowing() { return true; }
	public Rectangle rect() { return new Rectangle(x, y, w, h); }
	public Rectangle fullRect() { return rect(); }

	public void paint(Graphics g) {
		if (cornerTL == null) { // no forms
			g.setColor(new Color(100, 100, 250));
			g.fillRect(x, y, w, h);
			return;
		}
		int imgW = cornerTL.getWidth(null);
		int imgH = cornerTL.getHeight(null);
		g.setColor(fillColor);
		g.fillRect(x + imgW, y + imgH, w - (2 * imgW), h - (2 * imgH));
		for (int x1 = x + imgW; x1 < x + w - imgW; x1++) {
			g.drawImage(edgeTop, x1, y, null);
			g.drawImage(edgeBottom, x1, y + h - imgH, null);
		}
		for (int y1 = y + imgH; y1 < y + h - imgH; y1++) {
			g.drawImage(edgeLeft, x, y1, null);
			g.drawImage(edgeRight, x + w - imgW, y1, null);
		}
		g.drawImage(cornerTL, x, y, null);
		g.drawImage(cornerTR, x + w - imgW, y, null);
		g.drawImage(cornerBL, x, y + h - imgH, null);
		g.drawImage(cornerBR, x + w - imgW, y + h - imgH, null);
	}

	public void paintBubble(Graphics g) { }
	public void dragTo(int mouseX, int mouseY) { }
	public void mouseDown(int mouseX, int mouseY) { }
}

class Bubble extends StretchyBox {

	boolean pointLeft = false;
	BufferedImage leftPointer;
	BufferedImage rightPointer;
	int fontSize = 13;
	Font font = new Font("Verdana", Font.BOLD, fontSize);
	FontRenderContext renderContext;
	int wrapWidth = 135;
	String contents;
	String[] lines;
	int[] xOffsets;

	Bubble() {
		renderContext = Skin.bubbleFrame.createGraphics().getFontRenderContext();
		setFrameImage(Skin.bubbleFrame);
		beThinkBubble(false);
	}

	void beThinkBubble(boolean think) {
		if (think) {
			leftPointer = Skin.thinkPointerL;
			rightPointer = Skin.thinkPointerR;
		} else {
			leftPointer = Skin.talkPointerL;
			rightPointer = Skin.talkPointerR;
		}
	}

	void beAskBubble() {
		renderContext = Skin.askBubbleFrame.createGraphics().getFontRenderContext();
		setFrameImage(Skin.askBubbleFrame);
		leftPointer = Skin.askPointerL;
		rightPointer = Skin.askPointerR;
	}

	void setContents(String s) {
		contents = s;
		Vector newLines = new Vector();
		int start = 0;
		while (start < s.length()) {
			int end = findLineEnd(s, start);
			newLines.addElement(s.substring(start, end));
			start = end;
		}

		// collect lines and compute maximum width
		lines = new String[newLines.size()];
		w = 65;
		for (int i = 0; i < lines.length; i++) {
			lines[i] = (String) newLines.get(i);
			w = Math.max(w, widthOf(lines[i]) + 15);
		}

		// compute xOffsets for centering
		xOffsets = new int[lines.length];
		for (int i = 0; i < lines.length; i++) {
			xOffsets[i] = (w - widthOf(lines[i])) / 2;
		}

		h = (lines.length * (fontSize + 2)) + 19;
	}

	int findLineEnd(String s, int start) {
		int end = start + 1;

		// find the character that hits wrap boundary
		while ((end < s.length()) && (widthOf(s.substring(start, end + 1)) < wrapWidth)) end++;

		if (end == s.length()) return end;  // string ended before wrapping
		if (widthOf(s.substring(start, end + 1)) < wrapWidth) return end + 1;  // string ended before wrapping

		// back up to last space (but include at least one character)
		int endIfNoSpace = end + 1;
		while (end > start + 1) {
			if ((end < s.length()) && (s.charAt(end) == ' ')) return end + 1;
			end--;
		}
		return endIfNoSpace;
	}

	int widthOf(String s) {
		if (s.length() == 0) return 0;
		return (int) new TextLayout(s, font, renderContext).getAdvance();
	}

	public Rectangle rect() { return new Rectangle(x, y, w, h + leftPointer.getHeight(null)); }

	public void paint(Graphics g) {
		super.paint(g);
		if (pointLeft) {
			g.drawImage(leftPointer, x + 7, y + h - 3, null);
		} else {
			g.drawImage(rightPointer, x - 9 + w - rightPointer.getWidth(null), y + h - 3, null);
		}

		g.setColor(new Color(0, 0, 0));
		g.setFont(font);
		int lineY = y + fontSize + 8;
		for (int i = 0; i < lines.length; i++) {
			g.drawString(lines[i], x + xOffsets[i], lineY);
			lineY += fontSize + 2;
		}
	}
}

class AskPrompter extends StretchyBox {
	int fontSize = 11;
	Font font = new Font("Verdana", Font.BOLD, fontSize);
	Font answerFont = new Font("Verdana", Font.PLAIN, 13);
	FontRenderContext renderContext;
	String promptString = "";
	String answerString = "";
	int lineX;
	int lineY;

	AskPrompter(String promptString) {
		renderContext = Skin.askBubbleFrame.createGraphics().getFontRenderContext();
		setFrameImage(Skin.askBubbleFrame);
		setPrompt(promptString);
	}

	void setPrompt(String s) {
		int lineCount = (s.length() == 0) ? 1 : 2;
		promptString = s;
		x = 15;
		w = 452;
		h = (lineCount * (fontSize + 4)) + 22;
		y = 378 - h;
	}

	public Rectangle rect() { return new Rectangle(x, y, w, h); }

	public void paint(Graphics g) {
		super.paint(g);
		Graphics2D g2d = (Graphics2D) g;

		lineX = x + 12;
		lineY = y + fontSize + 14;

		// question text
		g2d.setColor(new Color(0, 0, 0));
		g2d.setFont(font);
		if (promptString.length() > 0) {
			g2d.drawString(promptString, lineX - 2, lineY - 8);
			lineY += fontSize + 4;
		}

		// typein field
		g2d.setStroke(new BasicStroke(2));
		g2d.setColor(new Color(213, 213, 213));
		g2d.drawLine(lineX - 4, lineY - fontSize - 5, lineX - 4 + w - 38, lineY - fontSize - 5);
		g2d.drawLine(lineX - 4, lineY - fontSize - 5, lineX - 4, lineY - fontSize - 5 + fontSize +9);
		g2d.setColor(new Color(242, 242, 242));
		g2d.fillRect(lineX - 3, lineY - fontSize - 4, w - 39, fontSize + 8);

		// answer text
		Shape oldClip = g.getClip();
		g.setClip(lineX - 3, lineY - fontSize - 4, w - 39, fontSize + 8);
		g2d.setColor(new Color(0, 0, 0));
		g2d.setFont(answerFont);
		g2d.drawString(answerString, lineX, lineY - 1);
		g.setClip(oldClip);

		// check button
		g2d.drawImage(Skin.promptCheckButton, null, lineX + w - 38, lineY - fontSize - 6);
	}

	public boolean mouseDown(int mouseX, int mouseY, PlayerCanvas canvas) {
		// this is only triggered if mouse goes down over button
		if ((mouseX > (lineX + w - 38)) &&
			(mouseX < (lineX + w - 38 + 20)) &&
			(mouseY > (lineY - fontSize - 6)) &&
			(mouseY < (lineY - fontSize - 6 + 20))) {
				canvas.hideAskPrompt();
				return true;
		}
		return false;
	}

	public void handleKeystroke(int k, PlayerCanvas canvas) {
		canvas.inval(rect());
		if ((k == 3) || (k == 10)) {
			canvas.hideAskPrompt();
			return;
		}
		if ((k == 8) || (k == 127)) {
			if (answerString.length() > 0) answerString = answerString.substring(0, answerString.length() - 1);
			return;
		}

		char buf[] = new char[1];
		buf[0] = (char) k;
		if (k >= 32) answerString += new String(buf);
	}
}

class Watcher implements Drawable {

	static final Color black = new Color(0, 0, 0);
	static final Font labelFont = new Font("Verdana", Font.BOLD, 10);

	static final int NORMAL_MODE = 1;
	static final int SLIDER_MODE = 2;
	static final int LARGE_MODE = 3;

	PlayerCanvas canvas;
	StretchyBox box;
	WatcherReadout readout;
	StretchyBox slider;
	String label = "x";
	int mode = 1;  // 1 - normal, 2 - slider, 3 - big
	double sliderMin = 0;
	double sliderMax = 100;
	boolean isDiscrete = false;
	boolean isShowing = true;

	Watcher(LContext lc) {
		if (lc != null) canvas = lc.canvas;
		box = new StretchyBox();
		box.setFrameImage(Skin.watcherOuterFrame);
		box.x = 50;
		box.y = 50;
		readout = new WatcherReadout(false);
		readout.color = new Color(74, 107, 214);
		slider = new StretchyBox();
		slider.setFrameImage(Skin.sliderSlot);
		slider.h = 5;
	}

	void show() { isShowing = true; inval(); }
	void hide() { isShowing = false; inval(); }
	public boolean isShowing() { return isShowing; }

	public Rectangle rect() { return new Rectangle(box.x, box.y, box.w, box.h); }
	public Rectangle fullRect() { return rect(); }
	void inval() { canvas.inval(rect()); }

	public void paint(Graphics g) {
		int labelW = labelWidth(g);
		readout.beLarge(mode == LARGE_MODE);
		readout.adjustWidth(g);
		box.w = labelW + readout.w + 17;
		box.h = (mode == NORMAL_MODE) ? 21 : 31;

		if (mode == LARGE_MODE) {  // large readout
				readout.x = box.x;
				readout.y = box.y;
				readout.paint(g);
				return;
		}

		box.paint(g);
		drawLabel(g);
		readout.x = box.x + labelW + 12;
		readout.y = box.y + 3;
		readout.paint(g);
		if (mode == SLIDER_MODE) drawSlider(g);
	}

	public void paintBubble(Graphics g) { }
	public void mouseDown(int mouseX, int mouseY) { }

	void drawLabel(Graphics g) {
		g.setColor(black);
		g.setFont(labelFont);
		g.drawString(label, box.x + 6, box.y + 14);
	}

	int labelWidth(Graphics g) {
		if (label.length() == 0) return 0;
		TextLayout tLayout = new TextLayout(label, labelFont, ((Graphics2D) g).getFontRenderContext());
		return (int) tLayout.getBounds().getBounds().getWidth();
	}

	void drawSlider(Graphics g) {
		slider.x = box.x + 6;
		slider.y = box.y + 21;
		slider.w = box.w - 12;
		slider.paint(g);
		int xOffset = (int) Math.round((slider.w - 8) * ((getSliderValue() - sliderMin) / (sliderMax - sliderMin)));
		xOffset = Math.max(0, Math.min(xOffset, slider.w - 8));
		g.drawImage(Skin.sliderKnob, slider.x + xOffset - 1, slider.y - 3, null);
	}

	boolean inSlider(int x, int y) {
		if (mode != SLIDER_MODE) return false;
		if ((y < (slider.y - 1)) || (y > (slider.y + slider.h + 4))) return false;
		if ((x < (slider.x - 4)) || (x > (slider.x + slider.w + 5))) return false;
		return true;
	}

	public void dragTo(int mouseX, int mouseY) {
		double xOffset = mouseX - box.x - 10;
		setSliderValue(((xOffset * (sliderMax - sliderMin)) / (slider.w - 8)) + sliderMin);
	}

	void click(int mouseX, int mouseY) {
		double sliderVal = getSliderValue();
		int knobX = slider.x + (int) Math.round((slider.w - 8) * ((sliderVal - sliderMin) / (sliderMax - sliderMin))) + 5;
		int sign = (mouseX < knobX) ? -1 : 1;
		if (isDiscrete) {
			setSliderValue(sliderVal + sign);
		} else {
			setSliderValue(sliderVal + (sign * ((sliderMax - sliderMin) / 100.0)));
		}
	}

	double getSliderValue() {
		String param = Logo.prs(getObjProp(this, "param"));
		Hashtable vars = getVarsTable();
		if ((param == null) || (vars == null)) return (sliderMin + sliderMax) / 2;
		Object varValue = vars.get(Logo.aSymbol(param, canvas.lc));
		return (varValue instanceof Number) ? ((Number) varValue).doubleValue() : (sliderMin + sliderMax) / 2;
	}

	void setSliderValue(double newValue) {
		double sliderVal = isDiscrete ? Math.round(newValue) : newValue;
		sliderVal = Math.min(sliderMax, Math.max(sliderMin, sliderVal));

		String param = Logo.prs(getObjProp(this, "param"));
		Hashtable vars = getVarsTable();
		if ((param == null) || (vars == null)) return;
		vars.put(Logo.aSymbol(param, canvas.lc), new Double(sliderVal));
		inval();
	}

	Hashtable getVarsTable() {
		String op = Logo.prs(getObjProp(this, "op"));
		String param = Logo.prs(getObjProp(this, "param"));
		Object target = getObjProp(this, "target");
		if ((target == null) || (!op.equals("getVar:"))) return null;

		// variables are kept in a hash table associated with a unique object
		// that is stored in the "vars" property of the target sprite
		Object varsObj = getObjProp(target, "vars");
		if (varsObj == null) return null;
		return (Hashtable) canvas.lc.props.get(varsObj);
	}

	Object getObjProp(Object obj, String propName) {
		Hashtable plist = (Hashtable) canvas.lc.props.get(obj);
		if (plist == null) return null;
		return plist.get(Logo.aSymbol(propName, canvas.lc));
	}
}

class WatcherReadout {

	int x, y;
	int w = 40, h = 14;
	Color color = new Color(100, 60, 20);
	String contents = "123";
	boolean isLarge = false;

	static final Font smallFont = new Font("Verdana", Font.BOLD, 10);
	static final Font bigFont = new Font("Verdana", Font.BOLD, 14);

	WatcherReadout(boolean b) { beLarge(b); }

	void beLarge(boolean b) {
		if (isLarge == b) return; // no change
		isLarge = b;
		w = isLarge ? 50 : 40;
		h = isLarge ? 23 : 14;
	}

	void adjustWidth(Graphics g) {
		Font f = isLarge ? bigFont : smallFont;
		w = Math.max(w, stringWidth(contents, f, g) + 12);
	}

	void paint(Graphics g) {
		g.setColor(Color.WHITE);
		g.fillRect(x + 2, y, w - 4, 1);
		g.fillRect(x + 2, y + h - 1, w - 4, 1);
		g.fillRect(x, y + 2, 1, h - 4);
		g.fillRect(x + w - 1, y + 2, 1, h - 4);
		g.fillRect(x + 1, y + 1, 1, 1);
		g.fillRect(x + w - 2, y + 1, 1, 1);
		g.fillRect(x + 1, y + h - 2, 1, 1);
		g.fillRect(x + w - 2, y + h - 2, 1, 1);

		g.setColor(darker(darker(color)));
		g.fillRect(x + 2, y + 1, w - 4, 1);
		g.fillRect(x + 1, y + 2, 1, h - 4);

		g.setColor(darker(color));
		g.fillRect(x + 2, y + 2, w - 4, 1);
		g.fillRect(x + 2, y + h - 2, w - 4, 1);
		g.fillRect(x + 2, y + 2, 1, h - 3);
		g.fillRect(x + w - 2, y + 2, 1, h - 4);

		g.setColor(color);
		g.fillRect(x + 3, y + 3, w - 5, h - 5);

		Font f = isLarge ? bigFont : smallFont;
		int yOffset = isLarge ? 17 : 11;
		g.setColor(Color.WHITE);
		g.setFont(f);
		g.drawString(contents, x + ((w - stringWidth(contents, f, g)) / 2) - 1, y + yOffset);
	}

	public static int stringWidth(String s, Font font, Graphics g) {
		if (s.length() == 0) return 0;
		TextLayout tLayout = new TextLayout(s, font, ((Graphics2D) g).getFontRenderContext());
		return (int) tLayout.getBounds().getBounds().getWidth();
	}

	public static Color darker(Color c) {
		double f = 0.8333;
		return new Color((int)(f * c.getRed()), (int)(f * c.getGreen()), (int)(f * c.getBlue()));
	}
}

class ListWatcher implements Drawable {

	public static final Font LABEL_FONT = new Font("Verdana", Font.BOLD, 10);
	public static final Font LABEL_FONT_SMALL = new Font("Verdana", Font.PLAIN, 10);
	public static final Font CELL_NUM_FONT = new Font("Verdana", Font.PLAIN, 9);
	public static final int TOP_MARGIN = 23;	// should really take into account height of list title string
	public static final int BOTTOM_MARGIN = 20;	// should really take into account height of 'length: 0' string
	public static final int LEFT_MARGIN = 5;
	public static final int RIGHT_MARGIN = 5;

	LContext lc;
	PlayerCanvas canvas;
	StretchyBox box;
	ListWatcherPane pane;
	ListWatcherScrollBar scrollBar;
	String listTitle = "listTitle";
	Object[] list = null;
	ArrayList highlightedIndices = new ArrayList();
	int mouseOffset = 0;
	int paneHeight = 0;
	int scroll = 0;
	int scrollForIndex = 0;
	int scrollBarHeight = 0;
	float scrollRatio = 0;
	boolean isShowing = true;
	boolean useScrollForIndex = false;

	ListWatcher(LContext inputLC) {
		if (inputLC != null) {
			canvas = inputLC.canvas;
			lc = inputLC;
		}
		box = new StretchyBox();
		box.setFrameImage(Skin.listWatcherOuterFrame);
		pane = new ListWatcherPane(this);
	}

	public void setList(Object[] inputList) {
		list = inputList;
		pane.setList(list);
		paneHeight = pane.totalHeight;
		initScrollBar();
	}

	public void initScrollBar() {
		scrollBarHeight = box.h - TOP_MARGIN - BOTTOM_MARGIN;
		scrollRatio = (float) ((float) paneHeight/(float) scrollBarHeight);
		if(scrollRatio < 1.0) {
			// don't need a scrollbar
			scrollRatio = 0;
			scrollBar = null;
			return;
		} else if(scrollBar == null) {
			// need a scrollbar
			scrollBar = new ListWatcherScrollBar();
		}
		scrollBar.nubBox.h = (int) (((float) scrollBarHeight) / scrollRatio);
		// minimum size for the scrollbar nub
		if (scrollBar.nubBox.h < 23) {
			scrollBar.nubBox.h = 23;
			scrollRatio = (float) ((float) paneHeight/ ((float) scrollBarHeight - 23.0));
		}
	}

	void show() { isShowing = true; inval(); }
	void hide() { isShowing = false; inval(); }
	public boolean isShowing() { return isShowing; }

	public Rectangle rect() { return new Rectangle(box.x, box.y, box.w, box.h); }
	public Rectangle fullRect() { return rect(); }
	void inval() { canvas.inval(rect()); }
	public void paintBubble(Graphics g) { }

	public void paint(Graphics g) {
		//paint background frame
		box.paint(g);

		//paint title
		g.setColor(Color.BLACK);
		g.setFont(LABEL_FONT);
		g.drawString(listTitle, box.x + ((box.w - WatcherReadout.stringWidth(listTitle, LABEL_FONT, g)) / 2), box.y + TOP_MARGIN - 8);

		//paint scrollbar
		if(scrollBar != null) {
			scrollBar.paint(g,
				box.x + box.w,					// bar right edge
				box.y + TOP_MARGIN,				// bar top edge
				scrollBarHeight,				// bar height
				box.y + TOP_MARGIN + scroll);	// scroll value
		}

		// paint scrollpane
		Graphics g2 = g.create();
		pane.paint(g2,
			box.x + LEFT_MARGIN,				// pane left edge
			useScrollForIndex ?
				scrollForIndex :
				(int) (scroll*scrollRatio),	// scroll value
			box.y + TOP_MARGIN,					// pane top clip
			scrollBarHeight);					// pane clip height

		// paint length label
		g.setColor(Color.BLACK);
		g.setFont(LABEL_FONT_SMALL);
		String contents = "length: " + list.length;
		g.drawString(contents, box.x + ((box.w - WatcherReadout.stringWidth(contents, LABEL_FONT_SMALL, g)) / 2), box.y + box.h - 5);
	}

	boolean inScrollbar(int x, int y) {
		if(scrollBar != null) {
			if ((x < (box.x + box.w - ListWatcherScrollBar.SCROLLBAR_WIDTH)) || (x > (box.x + box.w))) return false;
			if ((y < (box.y + TOP_MARGIN + scroll)) || (y > (box.y + TOP_MARGIN + scroll + scrollBar.nubBox.h))) return false;
			return true;
		} else {
			return false;
		}
	}

	public void mouseDown(int mouseX, int mouseY) {
		if(scrollBar != null) {
			mouseOffset = mouseY - (box.y + TOP_MARGIN) - scroll;
		}
	}

	public void dragTo(int mouseX, int mouseY) {
		if(scrollBar != null) {
			setScroll(mouseY - mouseOffset - (box.y + TOP_MARGIN));
		}
	}

	void setScroll(int s) {
		if(scrollBar != null) {
			if (s < 0) {
				scroll = 0;
			} else if (s > scrollBarHeight - scrollBar.nubBox.h) {
				scroll = scrollBarHeight - scrollBar.nubBox.h;
			} else {
				scroll = s;
			}
			useScrollForIndex = false;
			inval();
		}
	}

	void setScrollForHighlightIndex(int index) {
		if(scrollBar != null) {
			scrollForIndex = pane.getYPositionAtIndex(index) - (scrollBarHeight/2);
			useScrollForIndex = true;
			// need this for the scroll bar to update
			int s = (int) (scrollForIndex / scrollRatio);
			if (s < 0) {
				scroll = 0;
			} else if (s > scrollBarHeight - scrollBar.nubBox.h) {
				scroll = scrollBarHeight - scrollBar.nubBox.h;
			} else {
				scroll = s;
			}
			inval();
		}
	}

	void highlightIndex(int index) {
		if (index < 1 || index > list.length){
			// draw red border if out of range
			box.setFrameImage(Skin.listWatcherOuterFrameError);
		} else {
			// scroll to index if in range
			setScrollForHighlightIndex(index);
			highlightedIndices.add(new Integer(index));
			box.setFrameImage(Skin.listWatcherOuterFrame);
		}
	}

	void clearHighlights() {
		highlightedIndices = new ArrayList();
		box.setFrameImage(Skin.listWatcherOuterFrame);
	}
}

class ListWatcherPane {

	static final int CELL_MARGIN = 20;
	static final int CELL_WIDTH = 42;
	static final int CELL_HEIGHT = 21;

	Object[] list;
	ArrayList cells;
	ListWatcher ownerListWatcher;
	int w = 0;
	int maxIndexWidth = 0;
	int totalHeight = 0;

	ListWatcherPane(ListWatcher lw) {
		cells = new ArrayList();
		ownerListWatcher = lw;
	}

	public void setList(Object[] inputList){
		list = inputList;
		cells = new ArrayList();
		totalHeight = 0;
		maxIndexWidth = maxIndexWidth(Skin.bubbleFrame.createGraphics());
		for(int i = 0; i < inputList.length; i++) {
			ListWatcherCell lwc = new ListWatcherCell(list[i].toString(),
				w - ListWatcherScrollBar.SCROLLBAR_WIDTH - ListWatcher.LEFT_MARGIN - maxIndexWidth - 5);
			cells.add(lwc);
			totalHeight += lwc.h;
		}
	}

	public void paint(Graphics g, int x, int scroll, int clipY, int h) {

		// starting y position limits
		int y = clipY - scroll;
		if (y > clipY || scroll == 0 || totalHeight < h) {
			y = clipY;
		} else if (y < clipY - (totalHeight - h)) {
			y = clipY - (totalHeight - h);
		}

		// clip drawing bounds
		g.setClip(x, clipY, w - ListWatcherScrollBar.SCROLLBAR_WIDTH - ListWatcher.LEFT_MARGIN, h);

		// draw indices and cells
		g.setFont(ListWatcher.CELL_NUM_FONT);
		if (list == null) return;
		for (int i = 0; i < cells.size(); i++) {
			// draw the cell
			ListWatcherCell lwc = (ListWatcherCell) cells.get(i);
			lwc.x = x + maxIndexWidth + 3;
			lwc.y = y + 2;
			lwc.paint(g);

			// draw the index
			if(ownerListWatcher.highlightedIndices.contains(new Integer(i + 1))){
				g.setColor(Color.WHITE);
			} else {
				g.setColor(new Color(60, 60, 60));
			}
			String is = Integer.toString(i + 1);
			g.drawString(is, x + (maxIndexWidth - WatcherReadout.stringWidth(is, ListWatcher.CELL_NUM_FONT, g)) / 2 , y + (int) ((float) lwc.h/ (float) 2) + 5);

			y += lwc.h;
		}
	}

	public int getYPositionAtIndex(int index) {
		if(cells.size() > 0) {
			int h = 0;
			for (int i = 0; i < index; i++) {
				h += ((ListWatcherCell) cells.get(i)).h;
			}
			return h;
		} else {
			return 0;
		}
	}

	int maxIndexWidth(Graphics g) {
		double w = 0;
		for(int i = 1; i< list.length + 1; i++) {
			w = Math.max((double) w, (double) WatcherReadout.stringWidth(Integer.toString(i), ListWatcher.LABEL_FONT, g));
		}
		return (int) w;
	}
}

class ListWatcherScrollBar {

	public static final int SCROLLBAR_WIDTH = 20;
	StretchyBox frameBox;
	StretchyBox nubBox;

	ListWatcherScrollBar() {
		frameBox = new StretchyBox();
		frameBox.setFrameImage(Skin.vScrollFrame);
		nubBox = new StretchyBox();
		nubBox.setFrameImage(Skin.vScrollSlider);
	}

	public void paint(Graphics g, int topRightX, int y, int h, int scroll) {
		frameBox.x = topRightX - SCROLLBAR_WIDTH;
		frameBox.y = y;
		frameBox.w = SCROLLBAR_WIDTH - 4;
		frameBox.h = h + 5;
		frameBox.paint(g);

		nubBox.x = topRightX - SCROLLBAR_WIDTH + 2;
		nubBox.y = scroll + 2;
		nubBox.w = 12;
		if (nubBox.h < 23) {nubBox.h = 23;}
		nubBox.paint(g);
	}
}

class ListWatcherCell {

	Color CELL_COLOR = new Color(218, 77, 17);
	String contents;
	AttributedCharacterIterator ci;
	AttributedString as;
	LineBreakMeasurer lineMeasurer;
	FontRenderContext renderContext;
	BufferedImage contentsImage;
	int w;
	int h;
	int x;
	int y;

	ListWatcherCell(String contents, int width) {
		this.contents = contents;
		w = width;
		x = 0;
		y = 0;
		as = new AttributedString(contents);
		as.addAttribute(TextAttribute.FONT, ListWatcher.LABEL_FONT);
		ci = as.getIterator();
		Graphics g = Skin.bubbleFrame.createGraphics();
		renderContext = ((Graphics2D) g).getFontRenderContext();

		// calculate height and draw contents onto image
		h = 4;
		lineMeasurer = new LineBreakMeasurer(ci, renderContext);
		lineMeasurer.setPosition(ci.getBeginIndex());
		while (lineMeasurer.getPosition() < ci.getEndIndex()) {
			TextLayout layout = lineMeasurer.nextLayout(w-10);
			h += layout.getAscent();
			h += layout.getDescent() + layout.getLeading();
		}

		// draw and store the contents
		contentsImage = new BufferedImage(w,h,BufferedImage.TYPE_4BYTE_ABGR);
		Graphics gc = contentsImage.getGraphics();
		gc.setColor(Color.WHITE);
		Graphics2D g2d = (Graphics2D) gc;
		int drawPosY = y + 2;
		lineMeasurer.setPosition(ci.getBeginIndex());
		while (lineMeasurer.getPosition() < ci.getEndIndex()) {
			TextLayout layout = lineMeasurer.nextLayout(w-10);
			drawPosY += layout.getAscent();
			layout.draw(g2d, x + 6, drawPosY);
			drawPosY += layout.getDescent() + layout.getLeading();
		}
	}

	public void paint(Graphics g) {

		// draw the cell background
		g.setColor(Color.WHITE);
		g.fillRect(x + 2, y, w - 4, 1);
		g.fillRect(x + 2, y + h - 1, w - 4, 1);
		g.fillRect(x, y + 2, 1, h - 4);
		g.fillRect(x + w - 1, y + 2, 1, h - 4);
		g.fillRect(x + 1, y + 1, 1, 1);
		g.fillRect(x + w - 2, y + 1, 1, 1);
		g.fillRect(x + 1, y + h - 2, 1, 1);
		g.fillRect(x + w - 2, y + h - 2, 1, 1);

		g.setColor(WatcherReadout.darker(WatcherReadout.darker(CELL_COLOR)));
		g.fillRect(x + 2, y + 1, w - 4, 1);
		g.fillRect(x + 1, y + 2, 1, h - 4);

		g.setColor(WatcherReadout.darker(CELL_COLOR));
		g.fillRect(x + 2, y + 2, w - 4, 1);
		g.fillRect(x + 2, y + h - 2, w - 4, 1);
		g.fillRect(x + 2, y + 2, 1, h - 3);
		g.fillRect(x + w - 2, y + 2, 1, h - 4);

		g.setColor(CELL_COLOR);
		g.fillRect(x + 3, y + 3, w - 5, h - 5);

		// draw the cell contents
		g.drawImage(contentsImage, x, y, new Color(0,0,0,1), null);
	}
}

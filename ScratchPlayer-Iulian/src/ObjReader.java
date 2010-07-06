import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.IndexColorModel;
import java.awt.image.MemoryImageSource;
import java.awt.image.PixelGrabber;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.Boolean;
import java.util.Hashtable;
import java.util.Vector;

/* Reader for the serialized object format used to store Scratch projects.

	The result of readObjects() is a table of objects. There are three
	kinds of object records, determined by the 8-bit classID at the
	start of the record:
		a. fixed-format class records (<classID><...data...>), Class ID's 0 to 98
		b. an object reference (an object table index to the target object), ClassID = 99
		c. user-class records (<classID><class version #>[<field>]*), ClassID's 100-255

	The process of reading in the object table is:
		a. read all objects
		b. build Java image and sound objects
		c. dereference object references (Ref objects) in the fields list of
		   user-classes and in fixed-format collection objects.

	Typically, the client will then scan the processed object table to extract
	Sprites, their costumes and sounds, and their scripts.
*/

class ObjReader {
	DataInputStream s;
	Object[][] objTable;

	static final int ObjectRefID = 99;
	static final Object[] empty = new Object[0];
	static final Canvas canvas = new Canvas(); // used by MediaTracker when decoding JPEG images

	ObjReader(InputStream in) {
		s = new DataInputStream(in);
	}

	// Return the object table containing the contents of this project (stage, sprites, scripts, etc.)
	Object[][] readObjects(LContext lc) throws IOException {
		readInfo();
		readObjTable(lc);
		return objTable;
	}

	// Return a Hashtable containing info about the project (e.g. history, thumbnail, etc.)
	Hashtable readInfo() throws IOException {
		byte[] buf = new byte[10];
		s.read(buf);
		if (!(("ScratchV01".equals(new String(buf))) || ("ScratchV02".equals(new String(buf))))) {
			throw new IOException();  // not a Scratch project file
		}

		int infoBytes = s.readInt();
		readObjTable(null);

		Object[] keysAndValues = (Object[]) objTable[0][0];
		Hashtable result = new Hashtable(keysAndValues.length);
		for (int i = 0; i < keysAndValues.length - 1; i += 2) {
			result.put(keysAndValues[i], keysAndValues[i+1]);
		}
		return result;
	}

	void readObjTable(LContext lc) throws IOException {
		byte[] buf = new byte[4];

		// object store header
		s.read(buf);
		if (!"ObjS".equals(new String(buf)) || (s.readByte() != 1)) throw new IOException();
		s.read(buf);
		if (!"Stch".equals(new String(buf)) || (s.readByte() != 1)) throw new IOException();

		int objCount = s.readInt();

		objTable = new Object[objCount][];
		for (int i = 0; i < objCount; i++) {
			objTable[i] = readObj();
		}
		createSpritesAndWatchers(lc);
		buildImagesAndSounds();
		fixSounds();
		resolveReferences();
		uncompressMedia();
	}

	Object[] readObj() throws IOException {
		Object[] result;

		int classID = s.readUnsignedByte();
		if (classID < ObjectRefID) {
			result = new Object[2];
			result[0] = readFixedFormat(classID);
			result[1] = new Integer(classID);
		} else {
			int classVersion = s.readUnsignedByte();
			int fieldCount = s.readUnsignedByte();
			result = new Object[3 + fieldCount];
			result[0] = empty;  // placeholder for the resulting Java object, if any
			result[1] = new Integer(classID);
			result[2] = new Integer(classVersion);
			for (int i = 3; i < result.length; i++) { result[i] = readField(); }
		}
		return result;
	}

	Object readField() throws IOException {
		int classID = s.readUnsignedByte();
		if (classID == ObjectRefID) {  // Ref: read a 24-bit object table index
			int i = (s.readUnsignedByte()) << 16;
			i += (s.readUnsignedByte()) << 8;
			i += s.readUnsignedByte();
			return new Ref(i);
		}
		return readFixedFormat(classID);
	}

	// this table maps betweeen characters 128-255 in MacRoman to their nearest equivalents in ISO-8859-1
	static final byte[] macRomanToISOLatin = {
		-60, -59, -57, -55, -47, -42, -36, -31, -32, -30, -28, -29, -27, -25, -23, -24,
		-22, -21, -19, -20, -18, -17, -15, -13, -14, -12, -10, -11, -6, -7, -5, -4,
		-122, -80, -94, -93, -89, -107, -74, -33, -82, -87, -103, -76, -88, -128, -58, -40,
		-127, -79, -118, -115, -91, -75, -114, -113, -112, -102, -99, -86, -70, -98, -26, -8,
		-65, -95, -84, -90, -125, -83, -78, -85, -69, -123, -96, -64, -61, -43, -116, -100,
		-106, -105, -109, -108, -111, -110, -9, -77, -1, -97, -71, -92, -117, -101, -68, -67,
		-121, -73, -126, -124, -119, -62, -54, -63, -53, -56, -51, -50, -49, -52, -45, -44,
		-66, -46, -38, -37, -39, -48, -120, -104, -81, -41, -35, -34, -72, -16, -3, -2};

	Object readFixedFormat(int classID) throws IOException {
		int count, x, y;
		byte[] buf;
		Object[] objList;

		switch(classID) {
		case 1: return empty;
		case 2: return Boolean.TRUE;
		case 3: return Boolean.FALSE;
		case 4: return new Integer(s.readInt());
		case 5: return new Integer(s.readShort());
		case 6:
		case 7:
			double num = 0.0;
			double multiplier = 1.0;
			count = s.readShort();
			for (int i = 0; i < count; i++) {
				int digit = s.readUnsignedByte();
				num += multiplier * digit;
				multiplier *= 256.0;
			}
			if (classID == 7) num = -num;
			return new Double(num);
		case 8:
			return new Double(s.readDouble());
		case 9:
		case 10:
			count = s.readInt();
			s.read(buf = new byte[count]);
			for (int i = 0; i < count; i++) {
				if (buf[i] < 0) buf[i] = macRomanToISOLatin[buf[i] + 128];
			}
			try {
				return new String(buf, "ISO-8859-1");
			} catch (UnsupportedEncodingException e) {
				return new String(buf);
			}
		case 11:
			count = s.readInt();
			s.read(buf = new byte[count]);
			return buf;
		case 12:
			count = s.readInt();
			s.read(buf = new byte[2 * count]);
			return buf;
		case 13:
			count = s.readInt();
			int[] ints = new int[count];
			for (int i = 0; i < ints.length; i++) ints[i] = s.readInt();
			return ints;
		case 14:
			count = s.readInt();
			s.read(buf = new byte[count]);
			try {
				return new String(buf, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				return new String(buf);
			}
		case 20:
		case 21:
		case 22:
		case 23:
			count = s.readInt();
			objList = new Object[count];
			for (int i = 0; i < objList.length; i++) objList[i] = readField();
			return objList;
		case 24:
		case 25:
			count = s.readInt();
			objList = new Object[2 * count];
			for (int i = 0; i < objList.length; i++) objList[i] = readField();
			return objList;
		case 30:
		case 31:
			int rgb = s.readInt();  // 10 bit each for RGB
			int alpha = 255;
			if (classID == 31) alpha = s.readUnsignedByte();
			return new Color(
				(rgb >> 22) & 0xFF,
				(rgb >> 12) & 0xFF,
				(rgb >>  2) & 0xFF,
				alpha);
		case 32: // point
			objList = new Object[2];
			objList[0] = readField();
			objList[1] = readField();
			return objList;
		case 33:  // rectangle
			objList = new Object[4];
			objList[0] = readField();
			objList[1] = readField();
			objList[2] = readField();
			objList[3] = readField();
			return objList;
		case 34:
		case 35:
			// Squeak image; collect the fields for later conversion to a Java Image
			Object[] fields = new Object[6];
			for (int i = 0; i < 5; i++) fields[i] = readField();
			// width, height, depth, offset, bits
			if (classID == 35) fields[5] = readField();  // ColorForm's color map
			return fields;
		}
		System.out.println("Unknown fixed-format class " + classID);
		throw new IOException();
	}

	void createSpritesAndWatchers(LContext lc) {
		// create an empty object for each sprite/stage/watcher; filled in by Logo code later
		// this allows intersprite references to be fixed by resolveReferences()

		for (int i = 0; i < objTable.length; i++) {
			Object[] entry = objTable[i];
			int classID = ((Number) entry[1]).intValue();
			if ((classID == 124) || (classID == 125)) entry[0] = new Sprite(lc);
			if (classID == 155) {
				entry[0] = new Watcher(lc);
				// if the slider max or min is a Squeak Float the slider is continuous
				// encode this case by ensuring that at least one end of the range has
				// a non-zero fractional part (since, in Logo, all numbers are Doubles)
				if (((Number) entry[2]).intValue() > 3) {
					Number sliderMin = (Number) entry[23];
					Number sliderMax = (Number) entry[24];
					if (floatWithoutFraction(sliderMin) || floatWithoutFraction(sliderMax)) {
						entry[24] = new Double(sliderMax.doubleValue() + 0.00000001);
					}
				}
			}
		}
	}

	boolean floatWithoutFraction(Object obj) {
		if (!(obj instanceof Double)) return false;
		double n = ((Double) obj).doubleValue();
		return Math.round(n) == n;
	}

	void resolveReferences() throws IOException {
		for (int i = 0; i < objTable.length; i++) {
			int classID = ((Number) objTable[i][1]).intValue();

			if ((classID >= 20) && (classID <= 29)) { // collection
				Object [] list = (Object[]) objTable[i][0];
				for (int j = 0; j < list.length; j++) {
					Object el = list[j];
					if (el instanceof Ref) {
						list[j] = deRef((Ref) el);
					}
				}
			}
			if (classID > ObjectRefID) { // user-defined
				for (int j = 3; j < objTable[i].length; j++) {
					Object el = objTable[i][j];
					if (el instanceof Ref) {
						objTable[i][j] = deRef((Ref) el);
					}
				}
			}
		}
	}

	Object deRef(Ref ref) {
		Object [] entry = objTable[ref.index];
		return ((entry[0] == null) || (entry[0] == empty)) ? entry : entry[0];
	}

	void buildImagesAndSounds() throws IOException {
		for (int i = 0; i < objTable.length; i++) {
			int classID = ((Number) objTable[i][1]).intValue();

			if ((classID == 34) || (classID == 35)) {  // image
				Object[] fields = (Object []) objTable[i][0];
				int w = ((Number) fields[0]).intValue();
				int h = ((Number) fields[1]).intValue();
				int depth = ((Number) fields[2]).intValue();
				int[] raster = decodePixels(objTable[((Ref) fields[4]).index][0]);
				MemoryImageSource imageSrc = null;

				objTable[i][0] = empty;  // default value if we can't convert to Java

				if (depth <= 8) {
					IndexColorModel colormap;
					if (fields[5] != null) {
						Object[] colors = (Object []) objTable[((Ref) fields[5]).index][0];
						colormap = customColorMap(depth, colors);
					} else {
					  colormap = squeakColorMap(depth);
					}
					imageSrc = new MemoryImageSource(w, h, colormap, rasterToByteRaster(raster, w, h, depth), 0, w);
				}
				if (depth == 16) {
					imageSrc = new MemoryImageSource(w, h, raster16to32(raster, w, h), 0, w);
				}
				if (depth == 32) {
					imageSrc = new MemoryImageSource(w, h, rasterAddAlphaTo32(raster), 0, w);
				}
				if (imageSrc != null) {
					int[] pixels32 = new int[w * h];
					PixelGrabber grabber = new PixelGrabber(imageSrc, 0, 0, w, h, pixels32, 0, w);
					try { grabber.grabPixels(); } catch (InterruptedException e) {}
					BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
					img.getRaster().setDataElements(0, 0, w, h, pixels32);
					objTable[i][0] = img;
				}
			}

			if (classID == 109) {  // sound
				Object[] sndBufEntry = objTable[((Ref) objTable[i][6]).index];
				objTable[i][0] = new ScratchSound(((Number) objTable[i][7]).intValue(), (byte []) sndBufEntry[0]);
			}
		}
	}

	int [] decodePixels(Object data) throws IOException {
		/*  Decompress Squeak pixel data run-encoded as a byte[]. (If the argument
			is an int[] then it is unencoded pixel words; just return it.)
			The run encoding is a sequence of pairs, {N D}*, where N is the
			run-length * 4 with a 2-bit data code in the least significant
			two bits. D, the data for the run, depends on the data code...
				0	skip N words, D is absent (not currently used, I believe)
				1	N copies of a word with all 4 bytes = D (1 byte)
				2	N copies of word D (4 bytes)
				3	D is N words of literal data (4N bytes)
		*/
		if (data instanceof int[]) return (int[]) data;

		DataInputStream s = new DataInputStream(new ByteArrayInputStream((byte []) data));
		int n = decodeInt(s);
		int [] result = new int[n];
		int i = 0;

		while ((s.available() > 0) & (i < n)) {
			int runLengthAndCode = decodeInt(s);
			int runLength = runLengthAndCode >> 2;
			int code = runLengthAndCode & 3;
			int b, w;

			switch (code) {
			case 0:
				i += runLength;
				break;
			case 1:
				b = s.readUnsignedByte();
				w = (b << 24) | (b << 16) | (b << 8) | b;
				for (int j = 0; j < runLength; j++) result[i++] = w;
				break;
			case 2:
				w = s.readInt();
				for (int j = 0; j < runLength; j++) result[i++] = w;
				break;
			case 3:
				for (int j = 0; j < runLength; j++) {
					w = (s.readUnsignedByte()) << 24;
					w |= (s.readUnsignedByte()) << 16;
					w |= (s.readUnsignedByte()) << 8;
					w |= s.readUnsignedByte();
					result[i++] = w;
				}
				break;
			}
		}
		return result;
	}

	int decodeInt(DataInputStream s) throws IOException {
		/*  Decode an integer as follows...
			  0-223		0-223
			  224-254   (0-30)*256 + next byte (0-7935)
			  255		next 4 bytes as big-endian integer
		*/

		int count = s.readUnsignedByte();
		if (count <= 223) return count;
		if (count <= 254) return ((count - 224) * 256) + (s.readUnsignedByte());
		return s.readInt();
	}

	int[]  rasterAddAlphaTo32(int[] raster32)  {
		// Add alpha to all non-zero pixel values of the given 32-bit raster
		for (int i = 0; i < raster32.length; i++) {
		  int pix = raster32[i];
			if (pix != 0) raster32[i] = 0xFF000000 | pix;
		}
		return raster32;
	}

	int[] raster16to32(int[] raster16, int w, int h)  {
		// Convert from 16-bit to 32-bit direct color
		int shift, pix, r, g, b, pix32;
		int[] raster32 = new int[w * h];
		int span = (w + 1) / 2;
		for (int y = 0; y < h; y++) {
			shift = 16;
			for (int x = 0; x < w; x++) {
				pix = ((raster16[(y * span) + (x / 2)]) >> shift) & 0xFFFF;
				r = ((pix >> 10) & 0x1F) << 3;
				g = ((pix >> 5) & 0x1F) << 3;
				b = (pix & 0x1F) << 3;
				pix32 = ((r + g + b) == 0) ? 0 : (0xFF000000 | (r << 16) | (g << 8) | b);
				raster32[(y * w) + x] = pix32;
				shift = (shift == 16) ? 0 : 16;
			}
		}
		return raster32;
	}

	byte[] rasterToByteRaster(int [] raster, int w, int h, int depth) {
		byte[] byteRaster = new byte[w * h];
		int span = raster.length / h;
		int mask = (1 << depth) - 1;
		int pixelsPerWord = 32 / depth;

		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				int word = raster[(y * span) + (x / pixelsPerWord)];
				int shift = depth * (pixelsPerWord - (x % pixelsPerWord) - 1);
				byteRaster[(y * w) + x] = (byte) ((word >> shift) & mask);
			}
		}
		return byteRaster;
	}

  // Squeak's color map for image depths <= 8; use for images without a custom color map
	static final byte[] squeakColors = {
		-1, -1, -1, 0, 0, 0, -1, -1, -1, -128, -128, -128, -1, 0, 0, 0, -1, 0, 0, 0, -1, 0, -1, -1,
		-1, -1, 0, -1, 0, -1, 32, 32, 32, 64, 64, 64, 96, 96, 96, -97, -97, -97, -65, -65, -65, -33, -33, -33,
		8, 8, 8, 16, 16, 16, 24, 24, 24, 40, 40, 40, 48, 48, 48, 56, 56, 56, 72, 72, 72, 80, 80, 80,
		88, 88, 88, 104, 104, 104, 112, 112, 112, 120, 120, 120, -121, -121, -121, -113, -113, -113, -105, -105, -105, -89, -89, -89,
		-81, -81, -81, -73, -73, -73, -57, -57, -57, -49, -49, -49, -41, -41, -41, -25, -25, -25, -17, -17, -17, -9, -9, -9,
		0, 0, 0, 0, 51, 0, 0, 102, 0, 0, -103, 0, 0, -52, 0, 0, -1, 0, 0, 0, 51, 0, 51, 51,
		0, 102, 51, 0, -103, 51, 0, -52, 51, 0, -1, 51, 0, 0, 102, 0, 51, 102, 0, 102, 102, 0, -103, 102,
		0, -52, 102, 0, -1, 102, 0, 0, -103, 0, 51, -103, 0, 102, -103, 0, -103, -103, 0, -52, -103, 0, -1, -103,
		0, 0, -52, 0, 51, -52, 0, 102, -52, 0, -103, -52, 0, -52, -52, 0, -1, -52, 0, 0, -1, 0, 51, -1,
		0, 102, -1, 0, -103, -1, 0, -52, -1, 0, -1, -1, 51, 0, 0, 51, 51, 0, 51, 102, 0, 51, -103, 0,
		51, -52, 0, 51, -1, 0, 51, 0, 51, 51, 51, 51, 51, 102, 51, 51, -103, 51, 51, -52, 51, 51, -1, 51,
		51, 0, 102, 51, 51, 102, 51, 102, 102, 51, -103, 102, 51, -52, 102, 51, -1, 102, 51, 0, -103, 51, 51, -103,
		51, 102, -103, 51, -103, -103, 51, -52, -103, 51, -1, -103, 51, 0, -52, 51, 51, -52, 51, 102, -52, 51, -103, -52,
		51, -52, -52, 51, -1, -52, 51, 0, -1, 51, 51, -1, 51, 102, -1, 51, -103, -1, 51, -52, -1, 51, -1, -1,
		102, 0, 0, 102, 51, 0, 102, 102, 0, 102, -103, 0, 102, -52, 0, 102, -1, 0, 102, 0, 51, 102, 51, 51,
		102, 102, 51, 102, -103, 51, 102, -52, 51, 102, -1, 51, 102, 0, 102, 102, 51, 102, 102, 102, 102, 102, -103, 102,
		102, -52, 102, 102, -1, 102, 102, 0, -103, 102, 51, -103, 102, 102, -103, 102, -103, -103, 102, -52, -103, 102, -1, -103,
		102, 0, -52, 102, 51, -52, 102, 102, -52, 102, -103, -52, 102, -52, -52, 102, -1, -52, 102, 0, -1, 102, 51, -1,
		102, 102, -1, 102, -103, -1, 102, -52, -1, 102, -1, -1, -103, 0, 0, -103, 51, 0, -103, 102, 0, -103, -103, 0,
		-103, -52, 0, -103, -1, 0, -103, 0, 51, -103, 51, 51, -103, 102, 51, -103, -103, 51, -103, -52, 51, -103, -1, 51,
		-103, 0, 102, -103, 51, 102, -103, 102, 102, -103, -103, 102, -103, -52, 102, -103, -1, 102, -103, 0, -103, -103, 51, -103,
		-103, 102, -103, -103, -103, -103, -103, -52, -103, -103, -1, -103, -103, 0, -52, -103, 51, -52, -103, 102, -52, -103, -103, -52,
		-103, -52, -52, -103, -1, -52, -103, 0, -1, -103, 51, -1, -103, 102, -1, -103, -103, -1, -103, -52, -1, -103, -1, -1,
		-52, 0, 0, -52, 51, 0, -52, 102, 0, -52, -103, 0, -52, -52, 0, -52, -1, 0, -52, 0, 51, -52, 51, 51,
		-52, 102, 51, -52, -103, 51, -52, -52, 51, -52, -1, 51, -52, 0, 102, -52, 51, 102, -52, 102, 102, -52, -103, 102,
		-52, -52, 102, -52, -1, 102, -52, 0, -103, -52, 51, -103, -52, 102, -103, -52, -103, -103, -52, -52, -103, -52, -1, -103,
		-52, 0, -52, -52, 51, -52, -52, 102, -52, -52, -103, -52, -52, -52, -52, -52, -1, -52, -52, 0, -1, -52, 51, -1,
		-52, 102, -1, -52, -103, -1, -52, -52, -1, -52, -1, -1, -1, 0, 0, -1, 51, 0, -1, 102, 0, -1, -103, 0,
		-1, -52, 0, -1, -1, 0, -1, 0, 51, -1, 51, 51, -1, 102, 51, -1, -103, 51, -1, -52, 51, -1, -1, 51,
		-1, 0, 102, -1, 51, 102, -1, 102, 102, -1, -103, 102, -1, -52, 102, -1, -1, 102, -1, 0, -103, -1, 51, -103,
		-1, 102, -103, -1, -103, -103, -1, -52, -103, -1, -1, -103, -1, 0, -52, -1, 51, -52, -1, 102, -52, -1, -103, -52,
		-1, -52, -52, -1, -1, -52, -1, 0, -1, -1, 51, -1, -1, 102, -1, -1, -103, -1, -1, -52, -1, -1, -1, -1
	};

	IndexColorModel squeakColorMap(int depth) {
	  int transparentIndex = (depth == 1) ? -1 : 0;  // zero is the transparent index except for depth = 1
		return new IndexColorModel(depth, 256, squeakColors, 0, false, transparentIndex);
	}

	IndexColorModel customColorMap(int depth, Object[] colors) {
		byte[] cmap = new byte[4 * colors.length];
		int next = 0;
		for (int i = 0; i < colors.length; i++) {
			Color c = (Color) objTable[((Ref) colors[i]).index][0];
			cmap[next++] = (byte) c.getRed();
			cmap[next++] = (byte) c.getGreen();
			cmap[next++] = (byte) c.getBlue();
			cmap[next++] = (byte) c.getAlpha();
		}
		return new IndexColorModel(depth, colors.length, cmap, 0, true, 0);
	}

	void fixSounds() {
		boolean needsReversal = false;

		for (int i = 0; i < objTable.length; i++) {
			int classID = ((Number) objTable[i][1]).intValue();
			if (classID == 109) {  // sound
				if (((ScratchSound) objTable[i][0]).isByteReversed()) needsReversal = true;
			}
		}
		if (!needsReversal) return;  // byte reversal not needed

		for (int i = 0; i < objTable.length; i++) {
			int classID = ((Number) objTable[i][1]).intValue();
			if (classID == 109) {  // sound
				((ScratchSound) objTable[i][0]).reverseBytes();
			}
		}
	}

	void uncompressMedia() {
		for (int i = 0; i < objTable.length; i++) {
			Object[] entry = objTable[i];
			int classID = ((Number) entry[1]).intValue();
			int version = -1;
			if (classID >= 100) version = ((Number) entry[2]).intValue();

			if ((classID == 162) && (version >= 4)) {  // image media
				if (entry[7] instanceof byte[]) {
						BufferedImage img = jpegDecode((byte[]) entry[7]);
						if (img != null) {
							if (entry[4] instanceof Image) ((Image) entry[4]).flush();
							entry[4] = img;
							entry[7] = empty;
						}
				}
				if (entry[8] instanceof BufferedImage) {
						entry[4] = entry[8];
						entry[8] = empty;
				}
			}
			if ((classID == 164) && (version >= 2)) {  // sound media
				if (entry[9] instanceof byte[]) {
					int sampleRate = ((Number) entry[7]).intValue();
					int bitsPerSample = ((Number) entry[8]).intValue();
					byte[] data = (byte[]) entry[9];
					int sampleCount = ((data.length * 8) + (bitsPerSample - 1)) / bitsPerSample;
					int[] samples = (new ADPCMDecoder(data, bitsPerSample)).decode(sampleCount);
					entry[4] = new ScratchSound(sampleRate, samples);
					entry[7] = entry[8] = entry[9] = empty;
				}
			}
		}
	}

	void canonicalizeMedia() {
		Vector images = new Vector(100);
		Vector sounds = new Vector(100);

		for (int i = 0; i < objTable.length; i++) {
			Object[] entry = objTable[i];
			int classID = ((Number) entry[1]).intValue();

			if (classID == 162) {  // image media
				BufferedImage img = (BufferedImage) entry[4];
				// not yet complete: should search images for matching image and use it if found
			}
			if (classID == 164) {  // sound media
				ScratchSound snd = (ScratchSound) entry[4];
				// not yet complete: should search sounds for matching sound and use it if found
			}
		}
	}

	BufferedImage jpegDecode(byte[] jpegData) {
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Image img = toolkit.createImage(jpegData);

		// wait for conversion to complete so we know width and height
		MediaTracker mediaTracker = new MediaTracker(canvas);
		mediaTracker.addImage(img, 0);
		try { mediaTracker.waitForID(0); } catch (InterruptedException ie) { }
		if (img == null) return null;

		// convert Image into a BufferedImage
		int w = img.getWidth(null);
		int h = img.getHeight(null);
		BufferedImage bufferedImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		Graphics g = bufferedImg.getGraphics();
		g.drawImage(img, 0, 0, w, h, null);
		g.dispose();
		img.flush();
		return bufferedImg;
	}

	void printit(Object x) {
		if ((x instanceof Object[]) && (((Object [])x).length == 0)) {
			System.out.print(" []");
			return;
		}
		if (x instanceof BufferedImage) {
			BufferedImage img = (BufferedImage) x;
			System.out.print(" BufferedImage_" + x.hashCode() + "(" + img.getWidth(null) + "x" + img.getHeight(null) + ")");
			return;
		}
		System.out.print(" " + x);
	}
}

class Ref {
	int index;

	Ref(int i) {
		index = i - 1;  // Java array indices are zero-based
	}

	public String toString() {
		return "Ref(" + index + ")";
	}
}

class ScratchSound {
	int rate;
	byte[] samples;

	ScratchSound(int samplingRate, byte[] sampleBuffer) {
		rate = samplingRate;
		samples = sampleBuffer;
	}

	ScratchSound(int samplingRate, int[] intSamples) {
		rate = samplingRate;
		samples = new byte[2 * intSamples.length];
		int dst = 0;
		for (int i = 0; i < intSamples.length; i++) {  // convert to byte[], big-endian
			samples[dst++] = (byte) ((intSamples[i] >> 8) & 0xFF);
			samples[dst++] = (byte) (intSamples[i] & 0xFF);
		}
	}

	public String toString() {
		double secs = ((100 * samples.length) / (2 * rate)) / 100.0;
		return "ScratchSound(" + secs + ", " + rate + ")";
	}

	// Note: Due to a bug in Scratch, some projects were uploaded with byte-reversed sounds. One way
	// to detect such projects to is to detect a byte-reversed version of a standard sound (meow or pap).
	// While not sure-fire, since the standards sounds may have been deleted, this should work for most projects.
	boolean isByteReversed() {
		if (samples.length < 100) return false;
		return matches(reversedMeow) || matches(reversedPop);
	}

	boolean matches(byte[] buf) {
		for (int i = 0; i < 100; i++) {
			if (samples[i] != buf[i]) return false;
		}
		return true;
	}

	static final  byte[] reversedMeow = {
		33, 0, 58, 0, 21, 0, -32, -1, -34, -1, 5, 0, -34, -1, -11, -1, -59, -1, -94, -1, -87, -1, -108, -1, 109, -1, 120,
		-1, 91, -1, 76, -1, 66, -1, 20, -1, -19, -2, -28, -2, -38, -2, -126, -2, 57, -2, 5, -2, 41, -2, -115, -2, 16, -1,
		70, -1, -47, -1, 109, 0, 84, 1, 47, 2, -56, 2, -100, 2, -92, 2, -66, 2, -13, 2, 33, 3, 109, 3, -2, 2, 65, 2,
		75, 2, 105, 2, 49, 2, 13, 2, 78, 1, 17, 1, -86, 0, 112, 0, -82, -1};

	static final byte[] reversedPop = {
		-43, 0, 3, 3, -67, 7, 114, 13, -17, 21, 83, 29, 60, 35, -101, 36, -30, 32, -85, 22, 115, 6, 85, -15, 95, -38, 96,
		-60, -115, -77, 105, -87, -110, -88, -27, -79, 71, -59, 65, -31, -13, 2, -19, 38, -47, 71, 74, 97, 125, 111, 35,
		112, 10, 97, -50, 68, -22, 29, 42, -13, 15, -55, 106, -89, -3, -111, 37, -115, -18, -104, 57, -75, -24, -35, -30, 12,
		-99, 58, 68, 96, 112, 118, -75, 121, -94, 104, -40, 69, 103, 23, 74, -28, 108, -75, 122, -110, -34, -127, 107, -122};

	void reverseBytes() {
		for (int i = 0; i < samples.length - 1; i += 2) {
			byte tmp = samples[i];
			samples[i] = samples[i+1];
			samples[i+1] = tmp;
		}
	}
}

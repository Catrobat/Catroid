package com.scratchPlayer.app.objectReader;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Bitmap.Config;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

//import java.awt.image.IndexColorModel;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Hashtable;


public class ProjectFile {
	private boolean fileCorrect = true;
	private final int headerLength = 10;
	private final String correctHeader = "ScratchV";
	private String header = null;
	private DataInputStream fileStream = null;
	Object[][] objTable;
	Object[][] headerObjTable;
	Object[][] stageObjTable;
	static final Object[] empty = new Object[0];

	static final byte[] macRomanToISOLatin = { -60, -59, -57, -55, -47, -42,
			-36, -31, -32, -30, -28, -29, -27, -25, -23, -24, -22, -21, -19,
			-20, -18, -17, -15, -13, -14, -12, -10, -11, -6, -7, -5, -4, -122,
			-80, -94, -93, -89, -107, -74, -33, -82, -87, -103, -76, -88, -128,
			-58, -40, -127, -79, -118, -115, -91, -75, -114, -113, -112, -102,
			-99, -86, -70, -98, -26, -8, -65, -95, -84, -90, -125, -83, -78,
			-85, -69, -123, -96, -64, -61, -43, -116, -100, -106, -105, -109,
			-108, -111, -110, -9, -77, -1, -97, -71, -92, -117, -101, -68, -67,
			-121, -73, -126, -124, -119, -62, -54, -63, -53, -56, -51, -50,
			-49, -52, -45, -44, -66, -46, -38, -37, -39, -48, -120, -104, -81,
			-41, -35, -34, -72, -16, -3, -2 };
	static final byte[] squeakColors = { -1, -1, -1, 0, 0, 0, -1, -1, -1, -128, -128, -128, -1, 0, 0, 0, -1, 0, 0, 0, -1, 0, -1, -1, -1, -1, 0, -1, 0, -1, 32, 32, 32, 64, 64, 64, 96, 96, 96, -97, -97, -97, -65, -65, -65, -33, -33, -33, 8, 8, 8, 16, 16, 16, 24, 24, 24, 40, 40, 40, 48, 48, 48, 56, 56, 56, 72, 72, 72, 80, 80, 80, 88, 88, 88, 104, 104, 104, 112, 112, 112, 120, 120, 120, -121, -121, -121, -113, -113, -113, -105, -105, -105, -89, -89, -89, -81, -81, -81, -73, -73, -73, -57, -57, -57, -49, -49, -49, -41, -41, -41, -25, -25, -25, -17, -17, -17, -9, -9, -9, 0, 0, 0, 0, 51, 0, 0, 102, 0, 0, -103, 0, 0, -52, 0, 0, -1, 0, 0, 0, 51, 0, 51, 51, 0, 102, 51, 0, -103, 51, 0, -52, 51, 0, -1, 51, 0, 0, 102, 0, 51, 102, 0, 102, 102, 0, -103, 102, 0, -52, 102, 0, -1, 102, 0, 0, -103, 0, 51, -103, 0, 102, -103, 0, -103, -103, 0, -52, -103, 0, -1, -103, 0, 0, -52, 0, 51, -52, 0, 102, -52, 0, -103, -52, 0, -52, -52, 0, -1, -52, 0, 0, -1, 0, 51, -1, 0, 102, -1, 0, -103, -1, 0, -52, -1, 0, -1, -1, 51, 0, 0, 51, 51, 0, 51, 102, 0, 51, -103, 0, 51, -52, 0, 51, -1, 0, 51, 0, 51, 51, 51, 51, 51, 102, 51, 51, -103, 51, 51, -52, 51, 51, -1, 51, 51, 0, 102, 51, 51, 102, 51, 102, 102, 51, -103, 102, 51, -52, 102, 51, -1, 102, 51, 0, -103, 51, 51, -103, 51, 102, -103, 51, -103, -103, 51, -52, -103, 51, -1, -103, 51, 0, -52, 51, 51, -52, 51, 102, -52, 51, -103, -52, 51, -52, -52, 51, -1, -52, 51, 0, -1, 51, 51, -1, 51, 102, -1, 51, -103, -1, 51, -52, -1, 51, -1, -1, 102, 0, 0, 102, 51, 0, 102, 102, 0, 102, -103, 0, 102, -52, 0, 102, -1, 0, 102, 0, 51, 102, 51, 51, 102, 102, 51, 102, -103, 51, 102, -52, 51, 102, -1, 51, 102, 0, 102, 102, 51, 102, 102, 102, 102, 102, -103, 102, 102, -52, 102, 102, -1, 102, 102, 0, -103, 102, 51, -103, 102, 102, -103, 102, -103, -103, 102, -52, -103, 102, -1, -103, 102, 0, -52, 102, 51, -52, 102, 102, -52, 102, -103, -52, 102, -52, -52, 102, -1, -52, 102, 0, -1, 102, 51, -1, 102, 102, -1, 102, -103, -1, 102, -52, -1, 102, -1, -1, -103, 0, 0, -103, 51, 0, -103, 102, 0, -103, -103, 0, -103, -52, 0, -103, -1, 0, -103, 0, 51, -103, 51, 51, -103, 102, 51, -103, -103, 51, -103, -52, 51, -103, -1, 51, -103, 0, 102, -103, 51, 102, -103, 102, 102, -103, -103, 102, -103, -52, 102, -103, -1, 102, -103, 0, -103, -103, 51, -103, -103, 102, -103, -103, -103, -103, -103, -52, -103, -103, -1, -103, -103, 0, -52, -103, 51, -52, -103, 102, -52, -103, -103, -52, -103, -52, -52, -103, -1, -52, -103, 0, -1, -103, 51, -1, -103, 102, -1, -103, -103, -1, -103, -52, -1, -103, -1, -1, -52, 0, 0, -52, 51, 0, -52, 102, 0, -52, -103, 0, -52, -52, 0, -52, -1, 0, -52, 0, 51, -52, 51, 51, -52, 102, 51, -52, -103, 51, -52, -52, 51, -52, -1, 51, -52, 0, 102, -52, 51, 102, -52, 102, 102, -52, -103, 102, -52, -52, 102, -52, -1, 102, -52, 0, -103, -52, 51, -103, -52, 102, -103, -52, -103, -103, -52, -52, -103, -52, -1, -103, -52, 0, -52, -52, 51, -52, -52, 102, -52, -52, -103, -52, -52, -52, -52, -52, -1, -52, -52, 0, -1, -52, 51, -1, -52, 102, -1, -52, -103, -1, -52, -52, -1, -52, -1, -1, -1, 0, 0, -1, 51, 0, -1, 102, 0, -1, -103, 0, -1, -52, 0, -1, -1, 0, -1, 0, 51, -1, 51, 51, -1, 102, 51, -1, -103, 51, -1, -52, 51, -1, -1, 51, -1, 0, 102, -1, 51, 102, -1, 102, 102, -1, -103, 102, -1, -52, 102, -1, -1, 102, -1, 0, -103, -1, 51, -103, -1, 102, -103, -1, -103, -103, -1, -52, -103, -1, -1, -103, -1, 0, -52, -1, 51, -52, -1, 102, -52, -1, -103, -52, -1, -52, -52, -1, -1, -52, -1, 0, -1, -1, 51, -1, -1, 102, -1, -1, -103, -1, -1, -52, -1, -1, -1, -1 };


	@SuppressWarnings("unchecked")
	Hashtable headerHashTable = null;
	Hashtable stageHashTable = null;

	
	public ProjectFile(File file) throws IOException {

		// try to open file
		// try {
		fileStream = new DataInputStream(new FileInputStream(file));
		// } catch (FileNotFoundException e) {
		// fileCorrect = false;
		// }

		// read header an check if header is correct
		// try {
		if (fileStream.available() >= headerLength) {
			byte[] tmpHeader = new byte[headerLength];
			fileStream.read(tmpHeader);

			header = new String(tmpHeader);
			// }
			// } catch (IOException e) {
			// fileCorrect = false;
			// }

			if (!header.contains(correctHeader)) {
				fileCorrect = false;
			}

			// read infoSize
			// try {
			@SuppressWarnings("unused")
			int infoSize = fileStream.readInt();
			// } catch (IOException e) {
			// fileCorrect = false;
			// }

			// try {
			headerHashTable = readObjects();
			stageHashTable = readObjects();
			// } catch (IOException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }
		}
		int ksjksjd = 2;
	}

	public boolean isCorrect() {
		return fileCorrect;
	}

	public String getInfo(String key) {
		return (String) headerHashTable.get(key);
	}

	@SuppressWarnings("unchecked")
	Hashtable readObjects() throws IOException {

		readObjTable();

		Object[] arrayOfObject = (Object[]) objTable[0][0];
		Hashtable localHashtable = new Hashtable(arrayOfObject.length);
		for (int j = 0; j < arrayOfObject.length - 1; j += 2) {
			localHashtable.put(arrayOfObject[j], arrayOfObject[(j + 1)]);
		}

		return localHashtable;
	}

	// reads the objTabel if section == 0 header else Stage
	void readObjTable() throws IOException {
		byte[] arrayOfByte = new byte[4];

		fileStream.read(arrayOfByte);
		if ((!"ObjS".equals(new String(arrayOfByte)))
				|| (fileStream.readByte() != 1))
			throw new IOException();
		fileStream.read(arrayOfByte);
		if ((!"Stch".equals(new String(arrayOfByte)))
				|| (fileStream.readByte() != 1))
			throw new IOException();

		int i = fileStream.readInt();

		objTable = new Object[i][];
		for (int j = 0; j < i; ++j) {
			objTable[j] = readObj();

		}

		int nochwasda = fileStream.available();

		// resolveReferences();
		// todo
		// createSpritesAndWatchers(paramLContext);
		// buildImagesAndSounds();
		// fixSounds();
		// uncompressMedia();
		resolveReferences();
	}

	// read a single object
	Object[] readObj() throws IOException {
		int i = fileStream.readUnsignedByte();
		Object[] arrayOfObject;
		if (i < 99) {
			arrayOfObject = new Object[2];
			arrayOfObject[0] = readFixedFormat(i);
			arrayOfObject[1] = new Integer(i);
		} else {
			int j = fileStream.readUnsignedByte();
			int k = fileStream.readUnsignedByte();
			arrayOfObject = new Object[3 + k];
			arrayOfObject[0] = empty;
			arrayOfObject[1] = new Integer(i);
			arrayOfObject[2] = new Integer(j);
			for (int l = 3; l < arrayOfObject.length; ++l)
				arrayOfObject[l] = readField();
		}
		return arrayOfObject;
	}

	// reads objects for fixed formats ( int, double, string,...)
	Object readFixedFormat(int paramInt) throws IOException {
		int i;
		int j;
		int k;
		byte[] arrayOfByte;
		Object[] arrayOfObject1;
		int l;
		switch (paramInt) {
		case 1:
			return empty;
		case 2:
			return Boolean.TRUE;
		case 3:
			return Boolean.FALSE;
		case 4:
			return new Integer(fileStream.readInt());
		case 5:
			return new Integer(fileStream.readShort());
		case 6:
		case 7:
			double d1 = 0.0D;
			double d2 = 1.0D;
			i = fileStream.readShort();
			for (j = 0; j < i; ++j) {
				k = fileStream.readUnsignedByte();
				d1 += d2 * k;
				d2 *= 256.0D;
			}
			if (paramInt == 7)
				d1 = -d1;
			return new Double(d1);
		case 8:
			return new Double(fileStream.readDouble());
		case 9:
		case 10:
			i = fileStream.readInt();
			fileStream.read(arrayOfByte = new byte[i]);
			for (j = 0; j < i; ++j) {
				if (arrayOfByte[j] >= 0)
					continue;
				arrayOfByte[j] = macRomanToISOLatin[(arrayOfByte[j] + 128)];
			}
			try {
				return new String(arrayOfByte, "ISO-8859-1");
			} catch (UnsupportedEncodingException localUnsupportedEncodingException1) {
				return new String(arrayOfByte);
			}
		case 11:
			i = fileStream.readInt();
			fileStream.read(arrayOfByte = new byte[i]);
			return arrayOfByte;
		case 12:
			i = fileStream.readInt();
			fileStream.read(arrayOfByte = new byte[2 * i]);
			return arrayOfByte;
		case 13:
			i = fileStream.readInt();
			int[] arrayOfInt = new int[i];
			for (k = 0; k < arrayOfInt.length; ++k)
				arrayOfInt[k] = fileStream.readInt();
			return arrayOfInt;
		case 14:
			i = fileStream.readInt();
			fileStream.read(arrayOfByte = new byte[i]);
			try {
				return new String(arrayOfByte, "UTF-8");
			} catch (UnsupportedEncodingException localUnsupportedEncodingException2) {
				return new String(arrayOfByte);
			}
		case 20:
		case 21:
		case 22:
		case 23:
			i = fileStream.readInt();
			arrayOfObject1 = new Object[i];
			for (l = 0; l < arrayOfObject1.length; ++l)
				arrayOfObject1[l] = readField();
			return arrayOfObject1;
		case 24:
		case 25:
			i = fileStream.readInt();
			arrayOfObject1 = new Object[2 * i];
			for (l = 0; l < arrayOfObject1.length; ++l)
				arrayOfObject1[l] = readField();
			return arrayOfObject1;
		case 30:
		case 31:
			l = fileStream.readInt();
			int i1 = 255;
			if (paramInt == 31)
				i1 = fileStream.readUnsignedByte();
			return Color
					.argb(i1, l >> 22 & 0xFF, l >> 12 & 0xFF, l >> 2 & 0xFF); // changed
			// to
			// AndroidColor
		case 32:
			arrayOfObject1 = new Object[2];
			arrayOfObject1[0] = readField();
			arrayOfObject1[1] = readField();
			return arrayOfObject1;
		case 33:
			arrayOfObject1 = new Object[4];
			arrayOfObject1[0] = readField();
			arrayOfObject1[1] = readField();
			arrayOfObject1[2] = readField();
			arrayOfObject1[3] = readField();
			return arrayOfObject1;
		case 34:
		case 35:
			Object[] arrayOfObject2 = new Object[6];
			for (int i2 = 0; i2 < 5; ++i2)
				arrayOfObject2[i2] = readField();

			if (paramInt == 35)
				arrayOfObject2[5] = readField();
			return arrayOfObject2;
		case 15:
		case 16:
		case 17:
		case 18:
		case 19:
		case 26:
		case 27:
		case 28:
		case 29:
		}
		System.out.println("Unknown fixed-format class " + paramInt);
		throw new IOException();
	}

	Object readField() throws IOException {
		int i = fileStream.readUnsignedByte();
		if (i == 99) {
			int j = fileStream.readUnsignedByte() << 16;
			j += (fileStream.readUnsignedByte() << 8);
			j += fileStream.readUnsignedByte();
			return new Ref(j);
		}
		return readFixedFormat(i);
	}

	// resolves references for hashtable
	void resolveReferences() throws IOException {
		for (int i = 0; i < objTable.length; ++i) {
			int j = ((Number) objTable[i][1]).intValue();

			if ((j >= 20) && (j <= 29)) {
				Object[] arrayOfObject = (Object[]) objTable[i][0];
				for (int l = 0; l < arrayOfObject.length; ++l) {
					Object localObject2 = arrayOfObject[l];
					if (localObject2 instanceof Ref) {
						arrayOfObject[l] = deRef(objTable, (Ref) localObject2);
					}
				}
			}
			if (j > 99)
				for (int k = 3; k < objTable[i].length; ++k) {
					Object localObject1 = objTable[i][k];
					if (localObject1 instanceof Ref)
						objTable[i][k] = deRef(objTable, (Ref) localObject1);
				}
		}
	}

	Object deRef(Object[][] objTable, Ref paramRef) {
		Object[] arrayOfObject = objTable[paramRef.index];
		return ((arrayOfObject[0] == null) || (arrayOfObject[0] == empty)) ? arrayOfObject
				: arrayOfObject[0];
	}

	// converts Images and sounds from ProjectFile to Drawables no priority on
	// sound now
	void buildImagesAndSounds(Object[][] objTable) throws IOException {
		for (int i = 0; i < objTable.length; ++i) {
			int j = ((Number) objTable[i][1]).intValue();
			Object[] arrayOfObject;
			if ((j == 34) || (j == 35)) {
				arrayOfObject = (Object[]) objTable[i][0];
				int k = ((Number) arrayOfObject[0]).intValue(); // is image
				// width
				int l = ((Number) arrayOfObject[1]).intValue(); // is image
				// height
				int i1 = ((Number) arrayOfObject[2]).intValue(); // is num of
				// color
				// bits
				int[] arrayOfInt = decodePixels(objTable[((Ref) arrayOfObject[4]).index][0]);
				Bitmap localMemoryImageSource = null;

				objTable[i][0] = empty;
				Object localObject2;
				Object localObject1;
				 if (i1 <= 8) {
				 if (arrayOfObject[5] != null) {
				 localObject2 = (Object[]) this.objTable[((Ref)arrayOfObject[5]).index][0];
				 localObject1 = customColorMap(i1, (Object[])localObject2);
				 } else {
				 localObject1 = squeakColorMap(i1);
				 }
				 localMemoryImageSource = Bitmap.createBitmap(k, l, Config.ALPHA_8);
				 
				 //localMemoryImageSource = new Bitmap(k, l,
				 //localObject1, rasterToByteRaster(
				 //arrayOfInt, k, l, i1), 0, k);
				 }
				 if (i1 == 16) {
				 localMemoryImageSource = Bitmap.createBitmap(raster16to32(arrayOfInt, k, l),k, l, Config.ALPHA_8);
				 //localMemoryImageSource = new Bitmap(k, l,
				 //raster16to32(arrayOfInt, k, l), 0, k);
				 }
				 if (i1 == 32) {
			     localMemoryImageSource = Bitmap.createBitmap(rasterAddAlphaTo32(arrayOfInt),k, l, Config.ALPHA_8);
				// localMemoryImageSource = new MemoryImageSource(k, l,
				// rasterAddAlphaTo32(arrayOfInt), 0, k);
				 }
				 if (localMemoryImageSource != null) {
				 localObject1 = new int[k * l];
				// localObject2 = new PixelGrabber(localMemoryImageSource, 0,
				// 0, k, l, localObject1, 0, k);
				// try {
				// ((PixelGrabber) localObject2).grabPixels();
				// } catch (InterruptedException localInterruptedException) {
				// }
				 
				 BitmapDrawable localBufferedImage = new BitmapDrawable(localMemoryImageSource
				 );
				// localBufferedImage.getRaster().setDataElements(0, 0, k, l,
				// localObject1);
				// this.objTable[i][0] = localBufferedImage;
				 }
			}
            //Sounds
			// if (j == 109) {
			// arrayOfObject = this.objTable[((Ref) this.objTable[i][6]).index];
			// this.objTable[i][0] = new ScratchSound(
			// ((Number) this.objTable[i][7]).intValue(),
			// (byte[]) arrayOfObject[0]);
			// }
		}

	}
//Needed


	int[] decodePixels(Object paramObject) throws IOException {
		if (paramObject instanceof int[])
			return (int[]) paramObject;

		DataInputStream localDataInputStream = new DataInputStream(
				new ByteArrayInputStream((byte[]) paramObject));
		int i = decodeInt(localDataInputStream);
		int[] arrayOfInt = new int[i];
		int j = 0;

		while ((((localDataInputStream.available() > 0) ? 1 : 0) & ((j < i) ? 1
				: 0)) != 0) {
			int k = decodeInt(localDataInputStream);
			int l = k >> 2;
			int i1 = k & 0x3;
			int i3;
			int i4;
			switch (i1) {
			case 0:
				j += l;
				break;
			case 1:
				int i2 = localDataInputStream.readUnsignedByte();
				i3 = i2 << 24 | i2 << 16 | i2 << 8 | i2;
				for (i4 = 0;; ++i4) {
					if (i4 >= l)
						break;// label288;
					arrayOfInt[(j++)] = i3;
				}

			case 2:
				i3 = localDataInputStream.readInt();
				for (i4 = 0;; ++i4) {
					if (i4 >= l)
						break;// label288;
					// label288: arrayOfInt[(j++)] = i3;
				}

			case 3:
				for (i4 = 0; i4 < l; ++i4) {
					i3 = localDataInputStream.readUnsignedByte() << 24;
					i3 |= localDataInputStream.readUnsignedByte() << 16;
					i3 |= localDataInputStream.readUnsignedByte() << 8;
					i3 |= localDataInputStream.readUnsignedByte();
					arrayOfInt[(j++)] = i3;
				}
			}
		}

		return arrayOfInt;
	}

	int decodeInt(DataInputStream paramDataInputStream) throws IOException {
		int i = paramDataInputStream.readUnsignedByte();
		if (i <= 223)
			return i;
		if (i <= 254)
			return (i - 224) * 256 + paramDataInputStream.readUnsignedByte();
		return paramDataInputStream.readInt();
	}
	IndexColorModel squeakColorMap(int paramInt)
	  {
	    int i = (paramInt == 1) ? -1 : 0;
	    return new IndexColorModel(paramInt, 256, squeakColors, 0, false, i);
	  }
	IndexColorModel customColorMap(int paramInt, Object[] paramArrayOfObject) {
		    byte[] arrayOfByte = new byte[4 * paramArrayOfObject.length];
		    int i = 0;
		    for (int j = 0; j < paramArrayOfObject.length; ++j) {
		      MyColor localColor = (MyColor)this.objTable[((Ref)paramArrayOfObject[j]).index][0];
		      arrayOfByte[(i++)] = (byte)localColor.getRed();
		      arrayOfByte[(i++)] = (byte)localColor.getGreen();
		      arrayOfByte[(i++)] = (byte)localColor.getBlue();
		      arrayOfByte[(i++)] = (byte)localColor.getAlpha();
		    }
		    return new IndexColorModel(paramInt, paramArrayOfObject.length, arrayOfByte, 0, true, 0);
	  }
	  byte[] rasterToByteRaster(int[] paramArrayOfInt, int paramInt1, int paramInt2, int paramInt3) {
		    byte[] arrayOfByte = new byte[paramInt1 * paramInt2];
		    int i = paramArrayOfInt.length / paramInt2;
		    int j = (1 << paramInt3) - 1;
		    int k = 32 / paramInt3;

		    for (int l = 0; l < paramInt2; ++l) {
		      for (int i1 = 0; i1 < paramInt1; ++i1) {
		        int i2 = paramArrayOfInt[(l * i + i1 / k)];
		        int i3 = paramInt3 * (k - i1 % k - 1);
		        arrayOfByte[(l * paramInt1 + i1)] = (byte)(i2 >> i3 & j);
		      }
		    }
		    return arrayOfByte;
		  }
	  int[] raster16to32(int[] paramArrayOfInt, int paramInt1, int paramInt2)
	  {
	    int[] arrayOfInt = new int[paramInt1 * paramInt2];
	    int i3 = (paramInt1 + 1) / 2;
	    for (int i4 = 0; i4 < paramInt2; ++i4) {
	      int i = 16;
	      for (int i5 = 0; i5 < paramInt1; ++i5) {
	        int j = paramArrayOfInt[(i4 * i3 + i5 / 2)] >> i & 0xFFFF;
	        int k = (j >> 10 & 0x1F) << 3;
	        int l = (j >> 5 & 0x1F) << 3;
	        int i1 = (j & 0x1F) << 3;
	        int i2 = (k + l + i1 == 0) ? 0 : 0xFF000000 | k << 16 | l << 8 | i1;
	        arrayOfInt[(i4 * paramInt1 + i5)] = i2;
	        i = (i == 16) ? 0 : 16;
	      }
	    }
	    return arrayOfInt;
	  }
	  int[] rasterAddAlphaTo32(int[] paramArrayOfInt)
	  {
	    for (int i = 0; i < paramArrayOfInt.length; ++i) {
	      int j = paramArrayOfInt[i];
	      if (j == 0) continue; paramArrayOfInt[i] = (0xFF000000 | j);
	    }
	    return paramArrayOfInt;
	  }



}
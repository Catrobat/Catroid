package com.scratchPlayer.app.objectReader;

import android.graphics.Bitmap;
import android.graphics.Rect;
import java.nio.ByteBuffer;


public class StForm {
	
  public int width;
  public int height;
  public int depth;
  public int offset;
  public MyColor[] privateColor;  //Can initially be an ObjRefRecord
  public ByteBuffer bits;  //Can initially be an ObjRefRecord
  ByteBuffer outArray; //Initialize
  Bitmap result =  Bitmap.createBitmap(width, height, android.graphics.Bitmap.Config.ALPHA_8);
  static protected MyColor[] CONST_defaultColorTable= {new MyColor((float)0xFFFFFF, (float)0), 
	                                                   new MyColor((float)0x000000, (float)0),
	                                                   new MyColor((float)0xFFFFFF, (float)0),
	                                                   new MyColor((float)0x808080, (float)0)};  
  
  
  StForm(int width_, int height_, int depth_, int offset_, MyColor[] privateColor_){
	  width = width_;
	  height = height_;
	  depth = depth_;
	  offset = offset_;
	  privateColor = privateColor_;	  
  }
  
  public Bitmap createBitmap() {
	  
      ByteBuffer bytebuffer = bits;
      int length  = readDecodedInt(bytebuffer);


      int k  = 0;
      try{
      while ( (k < length) && (bytebuffer.capacity() > 0) ) {
          int word = readDecodedInt(bytebuffer);
          int runLength = word >> 2;
          int dataCode = word & 0x03;
          switch (dataCode) {
              case 0: break;
              case 1: putNextByteOf_into_times(bytebuffer,outArray,runLength); break;
              case 2: putNextIntOf_into_times(bytebuffer,outArray,runLength); break;
              case 3: putIntsFrom_into_times(bytebuffer,outArray,runLength); break;
          }
          k += runLength;
      }}
      catch(Exception e){}

      //MLF: We might be able to do both of these together -- pixels and decompression
      //var testHeight : Number = Math.min(height, Math.floor(outArray.length / width / 4));

      Rect rect = new Rect(0,0,width,height);
      if (depth == 32) {
    	  result.setPixels(Buffer_to_intArray(outArray), 0, 0, 0, 0, rect.width(), rect.height());
      } else if (depth < 8) {
          writePixelsFrom_to_rect_depth_colorTable(outArray, result, rect, depth, privateColor);
      } else {
          //
      }
      
      return result;
  }

  protected int readDecodedInt(ByteBuffer array) {
	  try{
      int first = array.get();
      if (first < 224) {
          return first;
      } else if (first < 255) {
          int result  = array.get();
          result += (first - 224) * 256;
          return result;
      } else {
          return (int)array.get();
      }}
	  catch(Exception e){return 0;}
  } 
  protected void putNextByteOf_into_times(ByteBuffer input, ByteBuffer output, int runLength){
	  try{
      int data = input.getInt();
      int fullData = data << 24 | data << 16 | data << 8 | data;
      for (int i=0; i<runLength; i++) {
          output.putInt(fullData);
      }}catch(Exception e){}
  }
  
  protected void putNextIntOf_into_times(ByteBuffer input, ByteBuffer output, int runLength) {
	  try{
      int data = input.getInt();
      for (int i=0; i<runLength; i++) {
          output.putInt(data);
      }}catch(Exception e){}
  }
  
  
  protected void putIntsFrom_into_times(ByteBuffer input, ByteBuffer output, int runLength){
	  try{
      for (int i=0; i<runLength; i++) {
          int data = input.getInt();
          output.putInt(data);
      }}catch(Exception e){}
  }
  
  //Input: Array of Pixel; Bitmap is our picture; rect only for size; ColorTable is our ColorTable
  
  protected void writePixelsFrom_to_rect_depth_colorTable(ByteBuffer input , Bitmap bitmap, Rect rect, int depth, MyColor[] colorTable) {
      int x = 0;
      int y = 0;
      int width = rect.width();
      int shifts = (int)Math.floor((8/depth));
      int mask = (0x01 << depth) - 1;

      int partial = (width % shifts);
      if (partial != 0) width += (shifts - partial); 

      if (colorTable == null) {
          colorTable = CONST_defaultColorTable;
      } else {
          colorTable = fixColorTable(colorTable);
      }
      try{
      while (input.capacity()>0) {
          int nextByte= input.get();
          for (int shiftIndex = 0; shiftIndex<shifts; shiftIndex++) {
              int index = nextByte & mask;
              MyColor color = colorTable[index];
              int rgb = (int)color.getRgb();
              int alpha = (int)color.getAlpha();

              if (alpha != 0) {
                  rgb = alpha << 24 + rgb;
                  //ToDo Check what setPixel32 is and check this function
                  bitmap.setPixel(x + (shifts - shiftIndex - 1), y, rgb);
              } else {
                  bitmap.setPixel(x + (shifts - shiftIndex - 1), y, rgb);
              }
              nextByte = nextByte >> depth;
          }
          x += shifts;
          if (x >= width) {
              x=0;
              y++;
          }
      }
      if (input.capacity() > 0) {
          //("Had extra bytes: "+input.available()); TODO can do putput but i think not really needed
      }}catch(Exception e){}
  }

  //There are too many bits in the Smalltalk colors, so we need to strip some of them:
  protected MyColor[] fixColorTable(MyColor[] colorTable) {
      MyColor[] result = new MyColor[colorTable.length];
      for (int i=0; i<colorTable.length;i++) {
    	  MyColor myColors = colorTable[i];
          int eachRgb = (int)myColors.getRgb();
          int outRgb  =  ( ( (eachRgb >> 22) & 0xff) << 16) + ( ( (eachRgb >> 12) & 0xff ) << 8 ) + ( ( (eachRgb >> 2) & 0xff ) );
          result[i].setRgb(outRgb);
      }
      return result;
      
  }
  //Needed to convert the ByteBuffer to an IntArray to create the Bitmap
  private int[] Buffer_to_intArray(ByteBuffer outArray2) {
		int[] pixelMap = new int[outArray.capacity()];
		int counter =0;
	    while(outArray2.capacity() > 0)
		{
			int pixel = outArray2.getInt();
			pixelMap[counter] = pixel;
		
		}
	    int[] pixelMapOut = new int[counter];
	    int j=0;
	    for(int i= counter; i>=0; i--)
	    {
	    	pixelMapOut[j] = pixelMap[i];
	    }
	    
		return pixelMapOut;
	}


}

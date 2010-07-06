package com.scratchPlayer.app.objectReader;

public class IndexColorModel {
  private int bits;
  private int size;
  private byte[] cmap;
  private int startmap;
  private boolean hasalpha;
  private int trans;  
	
  IndexColorModel(int bit, int siz, byte[] map, int begin, boolean alpha, int tran)
  {
	  bits = bit;
	  size =siz;
	  cmap =map;
	  begin =startmap;
	  hasalpha =alpha;
	  trans =tran;
  }
}

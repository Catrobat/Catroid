package at.tugraz.ist.catroid.io;

import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.inputmethodservice.Keyboard.Key;
import android.inputmethodservice.Keyboard.Row;

public class CatKey extends Key {

	public CatKey(Resources res, Row parent, int x, int y, XmlResourceParser parser) {
		super(res, parent, x, y, parser);
	}

	//	@Override
	//	public boolean isInside(int x, int y) {
	//		//Closing Keyboard
	//		return super.isInside(x, codes[0] == Keyboard.KEYCODE_CANCEL ? y - 10 : y);
	//	}

}

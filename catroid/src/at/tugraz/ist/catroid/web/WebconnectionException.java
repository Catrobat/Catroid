package at.tugraz.ist.catroid.web;

public class WebconnectionException extends Exception {

	private static final long serialVersionUID = -6911428763559513678L;
	private int mHttpResultCode;
	
	
	public WebconnectionException(int httpResultCode) {
		mHttpResultCode = httpResultCode;
	}

	public int getmHttpResultCode() {
		return mHttpResultCode;
	}
	
	
}

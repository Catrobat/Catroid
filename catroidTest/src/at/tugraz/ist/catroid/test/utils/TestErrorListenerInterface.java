package at.tugraz.ist.catroid.test.utils;

import at.tugraz.ist.catroid.utils.ErrorListenerInterface;

public class TestErrorListenerInterface implements ErrorListenerInterface {
	public String errorMessage;

	public void showErrorDialog(String errorMessage) {
		this.errorMessage = errorMessage;
	}
}

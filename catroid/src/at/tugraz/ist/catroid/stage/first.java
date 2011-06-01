package at.tugraz.ist.catroid.stage;

import android.app.Activity;
import android.os.Bundle;
import at.tugraz.ist.catroid.R;

public class first extends Activity {
	/** Called when the activity is first created. */
	BTthread bt;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.first);

	}

	@Override
	public void onStart() {
		super.onStart();
		bt = new BTthread();
		bt.start();

	}

}
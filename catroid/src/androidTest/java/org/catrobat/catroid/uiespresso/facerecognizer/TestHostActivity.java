package org.catrobat.catroid.uiespresso.facerecognizer;

import android.app.Activity;
import android.os.Bundle;
import android.widget.FrameLayout;

/**
 * An empty activity for the face training UI tests to hang dialogs on.
 *
 * The real host is StageActivity, which needs a loaded Catroid project and a
 * running libGDX stage. The dialogs themselves only need a window, so this stands
 * in for it and keeps the tests fast and independent of project loading.
 *
 * Declare it in app/src/androidTest/AndroidManifest.xml:
 *
 * <activity
 *     android:name="org.catrobat.catroid.uitest.facerecognizer.TestHostActivity"
 *     android:exported="false"
 *     android:theme="@style/Theme.AppCompat" />
 */
public class TestHostActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(new FrameLayout(this));
	}
}

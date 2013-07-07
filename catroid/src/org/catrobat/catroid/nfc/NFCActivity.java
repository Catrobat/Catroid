package org.catrobat.catroid.nfc;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class NFCActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Context context = getApplicationContext();
		Intent intent = new Intent(context, NfcReadIntentService.class);
		intent.putExtra(NfcReadIntentService.NFC_INTENT, getIntent());
		context.startService(intent);
		finish();
	}
}

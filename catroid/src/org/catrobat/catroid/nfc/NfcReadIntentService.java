package org.catrobat.catroid.nfc;

import android.app.IntentService;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.util.Log;

public class NfcReadIntentService extends IntentService {
	private static final String LOG_TAG_NFC = "NFC";
	public static String NFC_INTENT = "NFC_INTENT";

	public NfcReadIntentService() {
		super("NfcReadIntentService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		intent = (Intent) intent.getExtras().get(NFC_INTENT);

		Log.d(LOG_TAG_NFC, "handling following intent in service: " + intent.getAction());

		if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {

			Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

			byte[] byteId = tag.getId();

			int uid = Integer.parseInt(byteId.toString());

			NfcManager.getInstance().setUid(uid);
		}

	}
}

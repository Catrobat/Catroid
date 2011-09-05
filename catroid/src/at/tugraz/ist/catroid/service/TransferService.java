package at.tugraz.ist.catroid.service;

import java.util.LinkedList;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import at.tugraz.ist.catroid.service.requests.BillingRequest;
import at.tugraz.ist.catroid.service.requests.UploadRequest;

public class TransferService extends Service implements ServiceConnection {

	private final static String TAG = TransferService.class.getSimpleName();
	private static LinkedList<BillingRequest> pendingRequests = new LinkedList<BillingRequest>();

	//private ServiceNotificationManager notifyManager;
	private TransferService transferService = null;
	private Binder binder = new LocalBinder();

	@Override
	public IBinder onBind(Intent intent) {
		Log.d(TAG, "onServiceConnected address of transfer Service: " + ((LocalBinder) binder).getService());
		return binder;
	}

	public boolean bindToMarketBillingService() {
		Log.i(TAG, "binding to TransferService");
		boolean bindResult = bindService(new Intent(this, TransferService.class), this, // ServiceConnection.
				Context.BIND_AUTO_CREATE);

		if (bindResult) {
			Log.d(TAG, "Bind ok");
			return true;
		} else {
			Log.e(TAG, "Could not bind to service.");
		}

		return false;
	}

	public void uploadRequest(String projectName, String projectDescription, String projectPath, String token) {
		BillingRequest request = new UploadRequest(transferService, projectName, projectDescription, projectPath, token);
		request.runRequest();
	}

	private void runPendingRequests() {
		Log.d(TAG, "run pending requests");

		Log.d(TAG, "Pending requests count: " + pendingRequests.size());

		BillingRequest request;
		while ((request = pendingRequests.peek()) != null) {
			if (request.runIfConnected()) {
				pendingRequests.remove();

			} else {
				Log.i(TAG, "The service crashed, so restart it");
				bindToMarketBillingService();
				return;
			}
		}

		//Log.i(TAG, "Stop the service");
		//stopSelf();
	}

	//	public static void updateStatus(String edition_id, int page, int page_count) {
	//		synchronized (TransferService.class) {
	//			//instance.notifyManager.updateNotification(edition_id, page, page_count);
	//
	//		}
	//	}
	//
	//	public void downloadFaild() {
	//		//instance.notifyManager.cancleDownloads();
	//		transferService.stopSelf();
	//	}
	//
	//	public void downloadFinished(String edition_id, boolean is_preview) {
	//		synchronized (TransferService.class) {
	//			//instance.notifyManager.finishNotification(edition_id);
	//
	//			transferService.stopSelf();
	//
	//		}
	//	}

	@Override
	public void onDestroy() {
		//threadExecutor.shutdown();
		Log.d(TAG, "service sucessfully shutdown...");
	}

	public void onServiceConnected(ComponentName name, IBinder service) {
		transferService = ((LocalBinder) service).getService();
		Log.d(TAG, "onServiceConnected address of transfer Service: " + transferService);
		runPendingRequests();
	}

	public void onServiceDisconnected(ComponentName name) {
		transferService = null;
	}

	public static void setPendingRequests(LinkedList<BillingRequest> pendingRequests) {
		TransferService.pendingRequests = pendingRequests;
	}

	public static LinkedList<BillingRequest> getPendingRequests() {
		return pendingRequests;
	}

	public TransferService getTransferService() {
		return transferService;
	}

	class LocalBinder extends Binder {
		TransferService getService() {
			return TransferService.this;
		}
	}
}

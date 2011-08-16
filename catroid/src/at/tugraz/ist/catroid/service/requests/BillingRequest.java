/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010  Catroid development team
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package at.tugraz.ist.catroid.service.requests;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.util.Log;
import at.tugraz.ist.catroid.service.TransferService;
import at.tugraz.ist.catroid.web.WebconnectionException;

abstract public class BillingRequest {

	private final static String TAG = BillingRequest.class.getName();

	private static ExecutorService threadExecutor;

	protected TransferService myBillingService;
	private long requestID;

	public BillingRequest(TransferService transferService) {
		myBillingService = transferService;
		threadExecutor = Executors.newFixedThreadPool(1);
	}

	abstract long run() throws WebconnectionException;

	/**
	 * Run the request, starting the connection if necessary.
	 * 
	 * @return true if the request was executed or queued; false if there
	 *         was an error starting the connection
	 */
	public boolean runRequest() {
		if (runIfConnected()) {
			return true;
		}

		if (myBillingService.bindToMarketBillingService()) {
			// Add a pending request to run when the service is connected.
			Log.d(TAG, "Service not binded. Add to pending requests");
			TransferService.getPendingRequests().add(this);
			return true;
		}
		return false;
	}

	/**
	 * Try running the request directly if the service is already connected.
	 * 
	 * @return true if the request ran successfully; false if the service
	 *         is not connected or there was an error when trying to use it
	 */

	public boolean runIfConnected() {
		Log.d(TAG, getClass().getSimpleName());

		if (myBillingService.getTransferService() != null) {
			threadExecutor.execute(new Runnable() {
				public void run() {
					try {
						requestID = BillingRequest.this.run();
						Log.d(TAG, "request id: " + requestID);

						//				if (requestID >= 0) {
						//					TransferService.getSentRequests().put(requestID, this);
						//				}
					} catch (WebconnectionException e) {

					}
					//TODO: stop Service if executer is empty
				}
			});
			return true;
		}
		return false;
	}

}
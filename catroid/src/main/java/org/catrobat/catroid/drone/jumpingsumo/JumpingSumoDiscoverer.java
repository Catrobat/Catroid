/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2017 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * An additional term exception under section 7 of the GNU Affero
 * General Public License, version 3, is available at
 * http://developer.catrobat.org/license_additional_term
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.drone.jumpingsumo;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;

import com.parrot.arsdk.ardiscovery.ARDiscoveryDeviceService;
import com.parrot.arsdk.ardiscovery.ARDiscoveryService;
import com.parrot.arsdk.ardiscovery.receivers.ARDiscoveryServicesDevicesListUpdatedReceiver;
import com.parrot.arsdk.ardiscovery.receivers.ARDiscoveryServicesDevicesListUpdatedReceiverDelegate;

import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;

import com.parrot.arsdk.ardiscovery.ARDiscoveryDeviceNetService;
import com.parrot.arsdk.ardiscovery.ARDiscoveryDeviceService;
import com.parrot.arsdk.arutils.ARUtilsException;
import com.parrot.arsdk.arutils.ARUtilsFtpConnection;
import com.parrot.arsdk.arutils.ARUtilsManager;

import java.util.ArrayList;
import java.util.List;

import static org.catrobat.catroid.CatroidApplication.getAppContext;

public class JumpingSumoDiscoverer {
	private static final String TAG = JumpingSumoDiscoverer.class.getSimpleName();

	public interface Listener {
		/**
		 * Called when the list of seen drones is updated
		 * Called in the main thread
		 * @param dronesList list of ARDiscoveryDeviceService which represents all available drones
		 * Content of this list respect the drone types given in startDiscovery
		 */
		void onDronesListUpdated(List<ARDiscoveryDeviceService> dronesList);
	}

	public interface ListenerPicture {
		/**
		 * Called when the pic Count changes
		 * Called in the main thread
		 * @param pictureCount the count of Pictures
		 */
		void onPictureCount(int pictureCount);

		/**
		 * Called before medias will be downloaded
		 * Called in the main thread
		 * @param matchingMedias the number of medias that will be downloaded
		 */
		void onMatchingMediasFound(int matchingMedias);

		/**
		 * Called each time the progress of a download changes
		 * Called in the main thread
		 * @param mediaName the name of the media
		 * @param progress the progress of its download (from 0 to 100)
		 */
		void onDownloadProgressed(String mediaName, int progress);

		/**
		 * Called when a media download has ended
		 * Called in the main thread
		 * @param mediaName the name of the media
		 */
		void onDownloadComplete(String mediaName);
	}

	private final List<Listener> listeners;
	private final List<ListenerPicture> listenerPictures;
	private final Handler handler = new Handler(getAppContext().getMainLooper());
	private final Context context;
	private ARDiscoveryService ardiscoveryService;
	private ServiceConnection ardiscoveryServiceConnection;
	private final ARDiscoveryServicesDevicesListUpdatedReceiver ardiscoveryServicesDevicesListUpdatedReceiver;
	private final List<ARDiscoveryDeviceService> matchingDrones;
	private SDCardModule sdcardModule;
	private ARUtilsManager ftplistModule;
	private ARUtilsManager ftpqueueManager;
	private static final int DEVICE_PORT = 21;

	private boolean startDiscoveryAfterConnection = true;

	public JumpingSumoDiscoverer(Context contextDrone) {
		context = contextDrone;
		listeners = new ArrayList<>();
		matchingDrones = new ArrayList<>();
		ardiscoveryServicesDevicesListUpdatedReceiver = new ARDiscoveryServicesDevicesListUpdatedReceiver(discoveryListener);
		listenerPictures = new ArrayList<>();
	}

	/*
	* Add a listener
	* All callbacks of the interface Listener will be called within this function
	* Should be called in the main thread
	* @param listener an object that implements the {@link Listener} interface
	*/
	public void addListener(Listener listener) {
		listeners.add(listener);
		notifyServiceDiscovered(matchingDrones);
	}

	public void addListenerPicture(ListenerPicture listenerPicture) {
		listenerPictures.add(listenerPicture);
	}

	/**
	 * remove a listener from the listener list
	 * @param listener an object that implements the {@link Listener} interface
	 */

	public void removeListener(Listener listener) {
		listeners.remove(listener);
	}

	/**
	 * Setup the drone discoverer
	 * Should be called before starting discovering
	 */
	public void setup() {
		// registerReceivers
		LocalBroadcastManager localBroadcastMgr = LocalBroadcastManager.getInstance(context);
		localBroadcastMgr.registerReceiver(ardiscoveryServicesDevicesListUpdatedReceiver,
				new IntentFilter(ARDiscoveryService.kARDiscoveryServiceNotificationServicesDevicesListUpdated));

		if (ardiscoveryServiceConnection == null) {
			ardiscoveryServiceConnection = new ServiceConnection() {
				@Override
				public void onServiceConnected(ComponentName name, IBinder service) {
					ardiscoveryService = ((ARDiscoveryService.LocalBinder) service).getService();
					if (startDiscoveryAfterConnection) {
						startDiscovering();
						startDiscoveryAfterConnection = false;
					}
				}

				@Override
				public void onServiceDisconnected(ComponentName name) {
					ardiscoveryService = null;
				}
			};
		}

		if (ardiscoveryService == null) {
			Intent i = new Intent(context, ARDiscoveryService.class);
			context.bindService(i, ardiscoveryServiceConnection, Context.BIND_AUTO_CREATE);
		}
	}

	/**
	 * Cleanup the object
	 * Should be called when the object is not used anymore
	 */
	public void cleanup() {
		stopDiscovering();
		if (ardiscoveryService != null) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					ardiscoveryService.stop();
					context.unbindService(ardiscoveryServiceConnection);
					ardiscoveryService = null;
				}
			}).start();
		}

		// unregister receivers
		LocalBroadcastManager localBroadcastMgr = LocalBroadcastManager.getInstance(context);
		localBroadcastMgr.unregisterReceiver(ardiscoveryServicesDevicesListUpdatedReceiver);
	}

	/**
	 * Start discovering Parrot drones
	 * For Wifi drones, the device should be on the drone's network
	 * When drones will be discovered, you will be notified through {@link Listener#onDronesListUpdated(List)}
	 */
	public void startDiscovering() {
		if (ardiscoveryService != null) {
			discoveryListener.onServicesDevicesListUpdated();
			ardiscoveryService.start();
			startDiscoveryAfterConnection = false;
		} else {
			startDiscoveryAfterConnection = true;
		}
	}
	/**
	 * Stop discovering Parrot drones
	 */
	public void stopDiscovering() {
		if (ardiscoveryService != null) {
			ardiscoveryService.stop();
		}
		startDiscoveryAfterConnection = false;
	}

	private void notifyServiceDiscovered(List<ARDiscoveryDeviceService> dronesList) {
		List<Listener> listenersCpy = new ArrayList<>(listeners);
		for (Listener listener : listenersCpy) {
			listener.onDronesListUpdated(dronesList);
		}
	}

	private final ARDiscoveryServicesDevicesListUpdatedReceiverDelegate discoveryListener =
			new ARDiscoveryServicesDevicesListUpdatedReceiverDelegate() {
				@Override
				public void onServicesDevicesListUpdated() {
					if (ardiscoveryService != null) {
						matchingDrones.clear();
						List<ARDiscoveryDeviceService> deviceList = ardiscoveryService.getDeviceServicesArray();
						if (deviceList != null) {
							for (ARDiscoveryDeviceService service : deviceList) {
								matchingDrones.add(service);
							}
						}
						notifyServiceDiscovered(matchingDrones);
					}
				}
			};

	public void getInfoDevice(@NonNull ARDiscoveryDeviceService deviceService) {
		try {
			ftplistModule = new ARUtilsManager();
			ftpqueueManager = new ARUtilsManager();
			String productIP = ((ARDiscoveryDeviceNetService) (deviceService.getDevice())).getIp();

			ftplistModule.initWifiFtp(productIP, DEVICE_PORT, ARUtilsFtpConnection.FTP_ANONYMOUS, "");
			ftpqueueManager.initWifiFtp(productIP, DEVICE_PORT, ARUtilsFtpConnection.FTP_ANONYMOUS, "");

			sdcardModule = new SDCardModule(ftplistModule, ftpqueueManager);
			sdcardModule.addListener(sdcardModulelistener);
			notifyPictureCount(sdcardModule.getPictureCount());
		} catch (ARUtilsException e) {
			Log.e(TAG, "Exception", e);
		}
	}

	public void download() {
		sdcardModule.getallFlightMedias();
	}

	public void notifyPic() {
		notifyPictureCount(sdcardModule.getPictureCount());
	}

	public void onDeleteFile(String mediaName) {
		sdcardModule.deleteLastReceivedPic(mediaName);
		notifyPictureCount(sdcardModule.getPictureCount());
	}

	private void notifyPictureCount(int pictureCount) {
		List<ListenerPicture> listenersCpy = new ArrayList<>(listenerPictures);
		for (ListenerPicture listener : listenersCpy) {
			listener.onPictureCount(pictureCount);
		}
	}

	private void notifyMatchingMediasFound(int matchingMedias) {
		List<ListenerPicture> listenersCpy = new ArrayList<>(listenerPictures);
		for (ListenerPicture listener : listenersCpy) {
			listener.onMatchingMediasFound(matchingMedias);
		}
	}

	private void notifyDownloadProgressed(String mediaName, int progress) {
		List<ListenerPicture> listenersCpy = new ArrayList<>(listenerPictures);
		for (ListenerPicture listener : listenersCpy) {
			listener.onDownloadProgressed(mediaName, progress);
		}
	}

	private void notifyDownloadComplete(String mediaName) {
		List<ListenerPicture> listenersCpy = new ArrayList<>(listenerPictures);
		for (ListenerPicture listener : listenersCpy) {
			listener.onDownloadComplete(mediaName);
		}
	}

	private final SDCardModule.Listener sdcardModulelistener = new SDCardModule.Listener() {
		@Override
		public void onMatchingMediasFound(final int matchingMedias) {
			handler.post(new Runnable() {
				@Override
				public void run() {
					notifyMatchingMediasFound(matchingMedias);
				}
			});
		}

		@Override
		public void onDownloadProgressed(final String mediaName, final int progress) {
			handler.post(new Runnable() {
				@Override
				public void run() {
					notifyDownloadProgressed(mediaName, progress);
				}
			});
		}

		@Override
		public void onDownloadComplete(final String mediaName) {
			handler.post(new Runnable() {
				@Override
				public void run() {
					notifyDownloadComplete(mediaName);
				}
			});
			onDeleteFile(mediaName);
		}
	};
}

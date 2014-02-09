package com.parrot.freeflight.utils;

import android.app.Activity;
import android.media.CamcorderProfile;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;

import com.parrot.freeflight.drone.DroneProxy.EVideoRecorderCapability;

public class DeviceCapabilitiesUtils {

	/**
	 * Determine maximum video resolution that device supports.
	 * 
	 * @return VIDEO_720P if device supports 720p video recording, <br>
	 *         VIDEO_360P if device supports 360p video recording, and <br>
	 *         UNSUPPORTED if it supports less than 360p video recording.
	 *         <p>
	 *         If it is impossible to determine video recording frame size we
	 *         assume that device support VIDEO_720p.
	 */
	static public EVideoRecorderCapability getMaxSupportedVideoRes()
	{
		  EVideoRecorderCapability videoResolution = EVideoRecorderCapability.VIDEO_720P;
		  CamcorderProfile profile = null;
		  
		  profile = CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH);

		  if (profile != null) {
			    if (profile.videoFrameHeight >= 720) {
			    	videoResolution = EVideoRecorderCapability.VIDEO_720P;
			    } else if (profile.videoFrameHeight >= 360) {
			    	videoResolution = EVideoRecorderCapability.VIDEO_360P;
			    } else if (profile.videoFrameHeight < 360) {
			    	videoResolution = EVideoRecorderCapability.NOT_SUPPORTED;
			    }
		    }
		    
	    return videoResolution;
	}
	
	/**
	 * Dumps display resolution in dips to the log with tag "Display"
	 * @param context - instance of Activity.
	 */
	static public void dumpScreenSizeInDips(Activity context)
	{
	    Display display = context.getWindowManager().getDefaultDisplay();
	    DisplayMetrics outMetrics = new DisplayMetrics ();
	    display.getMetrics(outMetrics);

	    float density  = context.getResources().getDisplayMetrics().density;
	    float dpHeight = outMetrics.heightPixels / density;
	    float dpWidth  = outMetrics.widthPixels / density;
	    
	    Log.i("Display", "" + dpHeight + "dp x " + dpWidth + "dp" + " density: " + density);    
	}
	
}

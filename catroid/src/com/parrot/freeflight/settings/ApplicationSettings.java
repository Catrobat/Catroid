package com.parrot.freeflight.settings;

import java.util.Hashtable;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Build;
import android.util.Log;

public class ApplicationSettings 
{
    public enum ControlMode {
        NORMAL_MODE,    //Two analogue joysticks
        ACCELERO_MODE,  //One accelero joystick and one analogue
        ACE_MODE        //Single joystick (accelero + analogue)
    };
    
    public static final long MEMORY_USAGE = 0;
	public static final int INTERFACE_OPACITY_MIN = 0;
	public static final int INTERFACE_OPACITY_MAX = 100;
	public static final int DEFAULT_INTERFACE_OPACITY = 50;
	private static final boolean DEFAULT_MAGNETO_ENABLED = false;
	public static final boolean DEFAULT_LOOPING_ENABLED = false;
	private static final int DEFAULT_CONTROL_MODE = ControlMode.ACCELERO_MODE.ordinal();

	private static final String NAME = "Preferences";
	private static final String TAG = "ApplicationSettings";
	
	public enum EAppSettingProperty {
	    FIRST_LAUNCH_PROP        ("first_launch"),
	    LEFT_HANDED_PROP         ("left_handed"),
	    FORCE_COMBINED_CTRL_PROP ("force_combined_control"),
	    CONTROL_MODE_PROP        ("control_mode"),
	    INTERFACE_OPACITY_PROP   ("interface_opacity"),
	    MAGNETO_ENABLED_PROP     ("magneto_enabled"),
	    LOOPING_ENABLED_PROP     ("looping_enabled"),
	    ASK_FOR_GPS              ("ask_for_gps");
	    
	    private final String propname;

        private EAppSettingProperty(final String propname) {
	        this.propname = propname;
	    }
        
        @Override
        public String toString() {
            return propname;
        }
	}
	
	// Static preferences
	private static final String[] GPU_BLACK_LIST_GPU_FROYO = {"Adreno"};

	private Map<Integer, String[]> blacklist;
	
	// Runtime preferences
	private boolean useOpenGL; 
	
	// Persistent preferences
	private SharedPreferences prefs;
	

	public ApplicationSettings(Context context)
	{
		this.prefs = context.getSharedPreferences(NAME, 0);
		
		this.blacklist = new Hashtable<Integer, String[]>();
        this.blacklist.put(Build.VERSION_CODES.FROYO, GPU_BLACK_LIST_GPU_FROYO);
	}


    @SuppressLint("NewApi")
	protected void applyChanges(Editor editor) 
	{
	    if (Build.VERSION.SDK_INT >= 9) {
	        editor.apply();
	    } else {
	        if (!editor.commit()) {
	            Log.w(TAG, "Can't save properties. Can't commit.");
            }
	    }
	}
	
    
    /**
     * Is not used anymore
     * @return
     */
    @Deprecated
	public boolean isUseOpenGL() 
	{
		return useOpenGL;
	}

    /**
     * Is not used anymore
     * @param useOpenGL
     */
    @Deprecated
	public void setUseOpenGL(boolean useOpenGL) 
	{
		this.useOpenGL = useOpenGL;
	}


    /**
     * Returns true if left handed mode should be used, false otherwise.
     * @return
     */
	public boolean isLeftHanded() 
	{
	    return getProperty(EAppSettingProperty.LEFT_HANDED_PROP, false);
	}


	/**
	 * Configures the left handed mode
	 * @param leftHanded
	 */
	public void setLeftHanded(boolean leftHanded) 
	{
		saveProperty(EAppSettingProperty.LEFT_HANDED_PROP, leftHanded);
	}


	/**
	 * Returns control mode.
	 * @return EASY_MODE, ACCELERO_MODE or ACE_MODE>
	 * EASY_MODE stands for two analogue onscreen joysticks.
	 * ACCELERO_MODE stands for one accelero joystick and one analogue.
	 * ACE_MODE stands for single joystick that uses G sensor in order to control roll and pitch, and thumb
	 * controls the gaz and yaw.
	 */
	public ControlMode getControlMode() 
	{
	    return ControlMode.values()[getProperty(EAppSettingProperty.CONTROL_MODE_PROP, DEFAULT_CONTROL_MODE)];
	}

	/**
	 * Sets the control mode.
	 * @param controlMode EASY_MODE, ACCELERO_MODE or ACE_MODE><p>
	 * <li>EASY_MODE stands for two analogue onscreen joysticks.</li>
	 * <li>ACCELERO_MODE stands for one accelero joystick and one analogue.</li>
	 * <li>ACE_MODE stands for single joystick that uses G sensor in order to control roll and pitch, and thumb
	 * controls the gaz and yaw.</li>
	 */
	public void setControlMode(ControlMode controlMode) 
	{
		saveProperty(EAppSettingProperty.CONTROL_MODE_PROP, controlMode.ordinal());
	}
	
	
	/**
	 * Returns GPU renderer names which are blacklisted
	 * @param androidVersion one of the Build.VERSION_CODES value.
	 * @return String[] that contains GPU renderer names if there are some for the specified Android version.
	 */
	public String[] getGpuBlackList(int androidVersion)
	{
		if (blacklist.containsKey(androidVersion)) {
			return blacklist.get(androidVersion);
		}
		
		return null;
	}
	
	
	/**
	 * Configures absolute control mode.
	 * @param enabled - if set to true - AR.Drone will use compass to fly relative to the user.
	 */
	public void setAbsoluteControlEnabled(boolean enabled)
	{
		saveProperty(EAppSettingProperty.MAGNETO_ENABLED_PROP, enabled);
	}
	
	
	/**
	 * Returns true if absolute control mode is enabled, false otherwise.
	 * @return
	 */
	public boolean isAbsoluteControlEnabled()
	{
	    return getProperty(EAppSettingProperty.MAGNETO_ENABLED_PROP, DEFAULT_MAGNETO_ENABLED);
	}

	
	/**
	 * @return Returns interface opacity in percents [0..100]
	 */
	public int getInterfaceOpacity()
	{
		return getProperty(EAppSettingProperty.INTERFACE_OPACITY_PROP, DEFAULT_INTERFACE_OPACITY);
	}
	
	
	/**
	 * Configures interface opacity
	 * @param opacity
	 */
	public void setInterfaceOpacity(int opacity)
	{
		if (opacity < INTERFACE_OPACITY_MIN || opacity > INTERFACE_OPACITY_MAX) {
			throw new IllegalArgumentException();
		}
		
		saveProperty(EAppSettingProperty.INTERFACE_OPACITY_PROP, opacity);
	}


	public boolean isFirstLaunch() 
	{
		return getProperty(EAppSettingProperty.FIRST_LAUNCH_PROP, true);
	}
	
	/**
	 * @return true is ability to make flips is enabled, false otherwise.
	 */
	public boolean isFlipEnabled()
	{
	    return getProperty(EAppSettingProperty.LOOPING_ENABLED_PROP, DEFAULT_LOOPING_ENABLED);
	}
	
	/**
	 * Configures ability of the Drone to make flips.
	 * @param enabled - true to enable, false to disable.
	 */
	public void setFlipEnabled(boolean enabled)
	{
	    saveProperty(EAppSettingProperty.LOOPING_ENABLED_PROP, enabled);
	}
	
	
	public void setFirstLaunch(boolean firstLaunch)
	{
	    saveProperty(EAppSettingProperty.FIRST_LAUNCH_PROP, firstLaunch);
	}


	@Deprecated
	public void setForceCombinedControl(boolean forceCombinedControl) 
	{
		saveProperty(EAppSettingProperty.FORCE_COMBINED_CTRL_PROP, forceCombinedControl);
	}
	
	
   @Deprecated
	public boolean isCombinedControlForced()
	{
       return getProperty(EAppSettingProperty.FORCE_COMBINED_CTRL_PROP, false);
	}
	
	
	public void setAskForGPS(boolean show)
	{
	    saveProperty(EAppSettingProperty.ASK_FOR_GPS, show);
	}
	
	
	public boolean isAskForGPS()
	{
	    return getProperty(EAppSettingProperty.ASK_FOR_GPS, true);
	}
	
  
    protected void saveProperty(EAppSettingProperty property, boolean value)
    {
        Editor editor = prefs.edit();
        editor.putBoolean(property.toString(), value);
        applyChanges(editor);
    }
    
    
    protected void saveProperty(EAppSettingProperty property, int value)
    {
        Editor editor = prefs.edit();
        editor.putInt(property.toString(), value);
        applyChanges(editor);
    }
    
    
    protected boolean getProperty(EAppSettingProperty property, boolean defValue)
    {
        return prefs.getBoolean(property.toString(), defValue);
    }
    
    
    protected int getProperty(EAppSettingProperty property, int defValue)
    {
        return prefs.getInt(property.toString(), defValue);
    }
}

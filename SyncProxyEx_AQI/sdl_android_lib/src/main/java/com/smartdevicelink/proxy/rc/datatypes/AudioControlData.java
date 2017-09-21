package com.smartdevicelink.proxy.rc.datatypes;

import java.util.Hashtable;

import com.smartdevicelink.proxy.rc.enums.ModuleType;
import com.smartdevicelink.proxy.rc.enums.RadioBand;
import com.smartdevicelink.proxy.rc.enums.RadioState;
import com.smartdevicelink.proxy.rpc.enums.PrimaryAudioSource;
import com.smartdevicelink.util.DebugTool;

public class AudioControlData extends ControlData{


	public static final String KEY_SOURCE_PRIMARY		= "source";
	public static final String KEY_VOLUME_INTEGER 		= "volume";

	/**
	 * Constructs a newly allocated RadioControlData object
	 */
	public AudioControlData() { 
		this.setInteriorDataType(ModuleType.AUDIO);
	}

	/**
	 * Constructs a newly allocated RadioControlData object indicated by the Hashtable parameter
	 * @param hash The Hashtable to use
	 */      
	public AudioControlData(Hashtable<String, Object> hash) {
		super(hash);
		this.setInteriorDataType(ModuleType.AUDIO);
	}

	/**
	 * Volume from 0 to 100 percent
	 * @return
	 */
	public Integer getVolumeInteger(){
		return (Integer) store.get(KEY_VOLUME_INTEGER);
	}

	public void setVolumeInteger(Integer volumeInt){
		if (volumeInt!=null) {
			store.put(KEY_VOLUME_INTEGER, volumeInt);
		} else {
			store.remove(KEY_VOLUME_INTEGER);
		}
	}
	
	/**
	 * The fractional part of the frequency for 101.7 is 7
	 * @return
	 */
	public PrimaryAudioSource getAudioSource(){
		 Object obj = store.get(KEY_SOURCE_PRIMARY);
		 if (obj instanceof PrimaryAudioSource) {
	            return (PrimaryAudioSource) obj;
	        } else if (obj instanceof String) {
	        	PrimaryAudioSource source = null;
	            try {
	            	source = PrimaryAudioSource.valueOf((String) obj);
	            } catch (Exception e) {
	                DebugTool.logError("Failed to parse " + getClass().getSimpleName() + "." + KEY_SOURCE_PRIMARY, e);
	            }
	            return source;
	        }
		 return null;
	}

	public void setAudioSource(PrimaryAudioSource audioSource){
		if (audioSource!=null) {
			store.put(KEY_SOURCE_PRIMARY, audioSource);
		} else {
			store.remove(KEY_SOURCE_PRIMARY);
		}
	}
	//TODO add a get/set frequency with float
}

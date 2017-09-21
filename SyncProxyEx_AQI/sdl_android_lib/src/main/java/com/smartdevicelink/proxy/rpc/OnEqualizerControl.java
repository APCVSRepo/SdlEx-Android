package com.smartdevicelink.proxy.rpc;

import com.smartdevicelink.protocol.enums.FunctionID;
import com.smartdevicelink.proxy.RPCNotification;
import com.smartdevicelink.proxy.rpc.enums.AudioStreamingState;
import com.smartdevicelink.proxy.rpc.enums.HMILevel;
import com.smartdevicelink.proxy.rpc.enums.SystemContext;

import java.util.Dictionary;
import java.util.Hashtable;

public class OnEqualizerControl extends RPCNotification {

    public static int MAX_SETTING = 100;
    public static int MIN_SETTING = 0;

    public static final String KEY_TREBLE = "TREBLE";
    public static final String KEY_MID = "MID";
    public static final String KEY_BASS = "BASS";
	/*Constructs a newly allocated OnEqualizerControl object
	*/
    public OnEqualizerControl() {
//        super(FunctionID.ON_EQUALIZER_CONTROL.toString()); //FIXME: liuchong
        super("");
    }

    public OnEqualizerControl(Hashtable<String, Object> hash) {
        super(hash);
    }

    public int getTreble() {
        if (parameters == null || parameters.get(KEY_TREBLE) == null)
            return (MIN_SETTING + MAX_SETTING) / 2;
        int treble = (int)parameters.get(KEY_TREBLE);
        treble = boundSettingRange(treble);
        return treble;
    }

    public int getMid() {
        if (parameters == null || parameters.get(KEY_MID) == null)
            return (MIN_SETTING + MAX_SETTING) / 2;
        int mid = (int)parameters.get(KEY_MID);
        mid = boundSettingRange(mid);
        return mid;
    }

    public int getBass() {
        if (parameters == null || parameters.get(KEY_BASS) == null)
            return (MIN_SETTING + MAX_SETTING) / 2;
        int bass = (int)parameters.get(KEY_BASS);
        bass = boundSettingRange(bass);
        return bass;
    }

    int boundSettingRange(int setting){
        if (setting < MIN_SETTING)
            return MIN_SETTING;
        if (setting > MAX_SETTING)
            return MAX_SETTING;
        return setting;
    }
}

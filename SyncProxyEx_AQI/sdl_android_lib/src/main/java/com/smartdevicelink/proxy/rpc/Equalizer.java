package com.smartdevicelink.proxy.rpc;

import com.smartdevicelink.protocol.enums.FunctionID;
import com.smartdevicelink.proxy.RPCRequest;
import com.smartdevicelink.proxy.rpc.enums.ControlButtonPress;

import java.util.Hashtable;

public class Equalizer extends RPCRequest {

    public static final String KEY_TREBLE = "TREBLE";
    public static final String KEY_MID = "MID";
    public static final String KEY_BASS = "BASS";


	public Equalizer() {
//        super(FunctionID.EQUALIZER.toString()); //FIXME: liuchong
        super("");
    }



    public Equalizer(Hashtable<String, Object> hash) {
        super(hash);
    }

    public int getTreble() {
        int treble = (int)parameters.get(KEY_TREBLE);
        return treble;
    }
    public void setTreble(int treble) {
        parameters.put(KEY_TREBLE, treble);
    }

    public int getMid() {
        int mid = (int)parameters.get(KEY_MID);
        return mid;
    }
    public void setMid(int mid) {  parameters.put(KEY_MID, mid);  }

    public int getBass() {
        int bass = (int)parameters.get(KEY_BASS);
        return bass;
    }
    public void setBass(int bass) {
        parameters.put(KEY_BASS, bass);
    }

}

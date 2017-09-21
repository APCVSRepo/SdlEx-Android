package com.smartdevicelink.proxy.rpc;

import java.util.Hashtable;

import com.smartdevicelink.protocol.enums.FunctionID;
import com.smartdevicelink.proxy.RPCNotification;
import com.smartdevicelink.proxy.rpc.enums.ComponentVolumeStatus;
import com.smartdevicelink.proxy.rpc.enums.PRNDL;
import com.smartdevicelink.proxy.rpc.enums.VehicleDataEventStatus;
import com.smartdevicelink.proxy.rpc.enums.WiperStatus;
import com.smartdevicelink.util.DebugTool;
import com.smartdevicelink.util.SdlDataTypeConverter;

public class OnClimateInfo extends RPCNotification {
	public static final String POWER = "power";
	public static final String TEMP = "temp";
	public static final String BLOWERSPEED = "blowerSpeed";
	public static final String DUAL = "dual";
	public static final String DUALTEMP = "dualTemp";
	public static final String RECIRC = "recirculate";
	
	
	

    public OnClimateInfo() {
//        super(FunctionID.ON_CLIMATE_CONTROL); //FIXME: liuchong
        super("");
    }
    public OnClimateInfo(Hashtable<String, Object> hash) {
        super(hash);
    }
    
    public Integer getPower() {
    	return (Integer) parameters.get(POWER);
    }

    public Integer getBlowerSpeed() {
    	return (Integer) parameters.get(BLOWERSPEED);
    }

    public Integer getRecirc() {
    	return (Integer) parameters.get(RECIRC);
    }
}

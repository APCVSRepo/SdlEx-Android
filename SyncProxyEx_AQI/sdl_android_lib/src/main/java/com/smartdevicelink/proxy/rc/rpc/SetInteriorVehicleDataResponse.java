package com.smartdevicelink.proxy.rc.rpc;

import java.util.Hashtable;

import com.smartdevicelink.protocol.enums.FunctionID;
import com.smartdevicelink.proxy.RPCResponse;
import com.smartdevicelink.proxy.rc.datatypes.ModuleData;

public class SetInteriorVehicleDataResponse extends RPCResponse {

	public static final String KEY_MODULE_DATA = "moduleData";

	/**
	*Constructs a newly allocated SetInteriorVehicleDataResponse object
	*/ 	
    public SetInteriorVehicleDataResponse() {
        super(FunctionID.SET_INTERIOR_VEHICLE_DATA.toString());
    }
    /**
    *<p>Constructs a newly allocated SetInteriorVehicleDataResponse object indicated by the Hashtable parameter</p>
    *@param hash The Hashtable to use
    */    
    public SetInteriorVehicleDataResponse(Hashtable<String, Object> hash) {
        super(hash);
    }
    
	public ModuleData getModuleData(){
		return (ModuleData) store.get(KEY_MODULE_DATA);
	}

	public void setModuleData(ModuleData moduleData){
		if (moduleData!=null) {
			parameters.put(KEY_MODULE_DATA, moduleData);
		} else {
			parameters.remove(KEY_MODULE_DATA);
		}
	}
	
}

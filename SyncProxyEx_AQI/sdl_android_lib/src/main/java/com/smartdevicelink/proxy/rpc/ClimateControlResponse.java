package com.smartdevicelink.proxy.rpc;

import com.smartdevicelink.protocol.enums.FunctionID;
import com.smartdevicelink.proxy.RPCResponse;

import java.util.Hashtable;

public class ClimateControlResponse extends RPCResponse {
    public ClimateControlResponse() {
//        super(FunctionID.CLIMATECONTROL); //FIXME: liuchong
        super("");
    }

    public ClimateControlResponse(Hashtable<String, Object> hash) {
        super(hash);
    }
}
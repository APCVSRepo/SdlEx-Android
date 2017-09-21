package com.smartdevicelink.proxy.rpc;

import com.smartdevicelink.protocol.enums.FunctionID;
import com.smartdevicelink.proxy.RPCResponse;

import java.util.Hashtable;


public class EqualizerResponse extends RPCResponse {

    public EqualizerResponse() {
//        super(FunctionID.EQUALIZER.toString()); //FIXME: liuchong
        super("");
    }

    public EqualizerResponse(Hashtable<String, Object> hash) {
        super(hash);
    }

}
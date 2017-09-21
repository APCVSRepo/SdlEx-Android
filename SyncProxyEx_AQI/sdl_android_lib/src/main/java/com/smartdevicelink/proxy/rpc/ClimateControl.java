package com.smartdevicelink.proxy.rpc;

import com.smartdevicelink.protocol.enums.FunctionID;
import com.smartdevicelink.proxy.RPCRequest;
import com.smartdevicelink.util.DebugTool;

import java.util.Hashtable;
import java.util.List;

/**
 * <p>
 * This class will add a command to the application's Command Menu
 * </p>
 * <p>
 * <b>Note:</b> A command will be added to the end of the list of elements in
 * the Command Menu under the following conditions:
 * </p>
 * <ul>
 * <li>When a Command is added with no MenuParams value provided</li>
 * <li>When a MenuParams value is provided with a MenuParam.position value
 * greater than or equal to the number of menu items currently defined in the
 * menu specified by the MenuParam.parentID value</li>
 * </ul>
 * <br/>
 * <p>
 * The set of choices which the application builds using AddCommand can be a
 * mixture of:
 * </p>
 * <ul>
 * <li>Choices having only VR synonym definitions, but no MenuParams definitions
 * </li>
 * <li>Choices having only MenuParams definitions, but no VR synonym definitions
 * </li>
 * <li>Choices having both MenuParams and VR synonym definitions</li>
 * </ul>
 * <p>
 * <b>HMILevel needs to be FULL, LIMITED or BACKGROUD</b>
 * </p>
 *
 * @since SmartDeviceLink 1.0
 * @see com.smartdevicelink.proxy.rpc.DeleteCommand
 * @see com.smartdevicelink.proxy.rpc.AddSubMenu
 * @see com.smartdevicelink.proxy.rpc.DeleteSubMenu
 */

public class ClimateControl extends RPCRequest {
    public static final String POWER = "power";
    public static final String TEMP = "temp";
    public static final String BLOWERSPEED = "blowerSpeed";
    public static final String DUAL = "dual";
    public static final String DUALTEMP = "dualTemp";
    public static final String RECIRC = "recirculate";
    public static final String BUTTONPRESS = "buttonPress";
    public static final String GETDATA = "getData";

    /**
     * Constructs a new AddCommand object
     */
    public ClimateControl() {
//        super(FunctionID.CLIMATECONTROL); //FIXME: liuchong
        super("");
    }


    public ClimateControl(Hashtable<String, Object> hash) {
        super(hash);
    }

    public void setRecirculate(Integer recirc) {

        if (recirc != null) {
            if(recirc < 0) recirc = 0;
            if(recirc > 1) recirc = 1;
            parameters.put(RECIRC, recirc);
        } else {
            parameters.remove(RECIRC);
        }
    }

    public void setBlowerSpeed(Integer blowerSpeed) {

        if (blowerSpeed != null) {
            if(blowerSpeed < 0) blowerSpeed = 0;
            if(blowerSpeed > 6) blowerSpeed = 6;
            parameters.put(BLOWERSPEED, blowerSpeed);
        } else {
            parameters.remove(BLOWERSPEED);
        }
    }

    public void setPushButton(Integer pushButton) {

        if (pushButton != null) {
            parameters.put(BUTTONPRESS, pushButton);
        } else {
            parameters.remove(BUTTONPRESS);
        }
    }

    public void setGetData(Integer getData){
        if (getData != null) {
            parameters.put(GETDATA, getData);
        } else {
            parameters.remove(GETDATA);
        }
    }
}
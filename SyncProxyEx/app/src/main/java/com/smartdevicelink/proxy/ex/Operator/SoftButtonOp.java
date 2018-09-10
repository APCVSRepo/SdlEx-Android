package com.smartdevicelink.proxy.ex.Operator;

import com.smartdevicelink.exception.SdlException;
import com.smartdevicelink.proxy.SdlProxyALM;
import com.smartdevicelink.proxy.rpc.Show;
import com.smartdevicelink.proxy.rpc.SoftButton;

import java.util.List;

/**
 * Created by leon on 16/9/27.
 */
public class SoftButtonOp extends MultiOperator
{
	private List<SoftButton> softButtons;

	public SoftButtonOp(List<SoftButton> softButtons, int correlationID)
	{
		this.softButtons = softButtons;
		this.correlationID = correlationID;

		updateFileInfo(softButtons.iterator());
	}

	@Override
	public boolean execute(SdlProxyALM proxy) throws SdlException
	{
		if (ready())
		{
			Show show = new Show();
			show.setCorrelationID(correlationID);
			show.setSoftButtons(softButtons);
			proxy.sendRPCRequest(show);
			return true;
		}
		else
		{
			proxy.putfile(fileInfo.name, fileInfo.type, persistent, fileInfo.data, correlationID);
			return false;
		}

	}

}

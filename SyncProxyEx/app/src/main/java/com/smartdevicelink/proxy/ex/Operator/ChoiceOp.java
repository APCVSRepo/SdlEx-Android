package com.smartdevicelink.proxy.ex.Operator;

import com.smartdevicelink.exception.SdlException;
import com.smartdevicelink.proxy.SdlProxyALM;
import com.smartdevicelink.proxy.rpc.Choice;

import java.util.Vector;

/**
 * Created by leon on 16/9/27.
 */
public class ChoiceOp extends MultiOperator
{
	private Vector<Choice> choices;
	private int choiceSetID;

	public ChoiceOp(Vector<Choice> choices, int choiceSetID, int correlationID)
	{
		this.choices = choices;
		this.choiceSetID = choiceSetID;
		this.correlationID = correlationID;

		updateFileInfo(choices.iterator());
	}

	@Override
	public boolean execute(SdlProxyALM proxy) throws SdlException
	{
		if (ready())
		{
			proxy.createInteractionChoiceSet(choices, choiceSetID, correlationID);
			return true;
		}
		else
		{
			proxy.putfile(fileInfo.name, fileInfo.type, persistent, fileInfo.data, correlationID);
			return false;
		}
	}

}

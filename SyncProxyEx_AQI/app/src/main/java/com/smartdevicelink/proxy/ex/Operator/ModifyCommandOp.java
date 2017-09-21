package com.smartdevicelink.proxy.ex.Operator;

import android.content.Context;

import com.smartdevicelink.exception.SdlException;
import com.smartdevicelink.proxy.SdlProxyALM;
import com.smartdevicelink.proxy.rpc.enums.FileType;

import java.io.InputStream;
import java.util.Vector;

/**
 * Created by leon on 16/9/30.
 */
public class ModifyCommandOp extends Operator
{
	private AddCommandOp addCommand;
	private int delCommandID;

	public ModifyCommandOp(Integer delCommandID, Integer commandID, String menuText, Integer parentID, Integer position, Vector<String> vrCommands,
						   String imagePath, FileType fileType, Integer correlationID)
	{
		addCommand = new AddCommandOp(commandID, menuText, parentID, position, vrCommands, imagePath, fileType, correlationID);
		this.delCommandID = delCommandID;
		this.correlationID = correlationID;
		this.prepareTotal = 1;
		this.prepareComplete = 0;
	}

	public ModifyCommandOp(Integer delCommandID, Integer commandID, String menuText, Integer parentID, Integer position, Vector<String> vrCommands,
						   int resID, Context ctx, FileType fileType, Integer correlationID)
	{
		addCommand = new AddCommandOp(commandID, menuText, parentID, position, vrCommands, resID, ctx, fileType, correlationID);
		this.delCommandID = delCommandID;
		this.correlationID = correlationID;
		this.prepareTotal = 1;
		this.prepareComplete = 0;
	}

	public ModifyCommandOp(Integer delCommandID, Integer commandID, String menuText, Integer parentID, Integer position, Vector<String> vrCommands,
						   InputStream is, FileType fileType, Integer correlationID)
	{
		addCommand = new AddCommandOp(commandID, menuText, parentID, position, vrCommands, is, fileType, correlationID);
		this.delCommandID = delCommandID;
		this.correlationID = correlationID;
		this.prepareTotal = 1;
		this.prepareComplete = 0;
	}

	public ModifyCommandOp(Integer delCommandID, Integer commandID, String menuText, Integer parentID, Integer position, Vector<String> vrCommands,
						   Integer correlationID)
	{
		// no image
		addCommand = new AddCommandOp(commandID, menuText, parentID, position, vrCommands, correlationID);
		this.delCommandID = delCommandID;
		this.correlationID = correlationID;
		this.prepareTotal = 1;
		this.prepareComplete = 0;
	}

	@Override
	public boolean execute(SdlProxyALM proxy) throws SdlException
	{
		if (ready())
		{
			return addCommand.execute(proxy);
		}
		else
		{
			proxy.deleteCommand(delCommandID, correlationID);
			return false;
		}
	}

	@Override
	public void readyOnce()
	{
		if (!ready())
			++prepareComplete;
		else
			addCommand.readyOnce();
	}
}

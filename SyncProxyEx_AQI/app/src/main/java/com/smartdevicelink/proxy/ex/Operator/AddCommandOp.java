package com.smartdevicelink.proxy.ex.Operator;

import android.content.Context;

import com.smartdevicelink.exception.SdlException;
import com.smartdevicelink.proxy.SdlProxyALM;
import com.smartdevicelink.proxy.rpc.enums.FileType;
import com.smartdevicelink.proxy.rpc.enums.ImageType;

import java.io.InputStream;
import java.util.Vector;

/**
 * Created by leon on 16/9/27.
 */
public class AddCommandOp extends SingleOperator
{
	private int commandID;
	private String menuText;
	private int parentID;
	private int position;
	private Vector<String> vrCommands;

	public AddCommandOp(Integer commandID, String menuText, Integer parentID, Integer position, Vector<String> vrCommands,
						String imagePath, FileType fileType, Integer correlationID)
	{
		updateFileInfo(imagePath, fileType);
		this.commandID = commandID;
		this.menuText = menuText;
		this.parentID = parentID;
		this.position = position;
		this.vrCommands = vrCommands;
		this.correlationID = correlationID;
	}

	public AddCommandOp(Integer commandID, String menuText, Integer parentID, Integer position, Vector<String> vrCommands,
						int resID, Context ctx, FileType fileType, Integer correlationID)
	{
		updateFileInfo(resID, ctx, fileType);
		this.commandID = commandID;
		this.menuText = menuText;
		this.parentID = parentID;
		this.position = position;
		this.vrCommands = vrCommands;
		this.correlationID = correlationID;
	}

	public AddCommandOp(Integer commandID, String menuText, Integer parentID, Integer position, Vector<String> vrCommands,
						InputStream is, FileType fileType, Integer correlationID)
	{
		updateFileInfo(is, fileType);
		this.commandID = commandID;
		this.menuText = menuText;
		this.parentID = parentID;
		this.position = position;
		this.vrCommands = vrCommands;
		this.correlationID = correlationID;
	}

	public AddCommandOp(Integer commandID, String menuText, Integer parentID, Integer position, Vector<String> vrCommands,
						Integer correlationID)
	{
		// no image
		this.prepareTotal = 0;
		this.fileInfo = null;

		this.commandID = commandID;
		this.menuText = menuText;
		this.parentID = parentID;
		this.position = position;
		this.vrCommands = vrCommands;
		this.correlationID = correlationID;
	}

	@Override
	public boolean execute(SdlProxyALM proxy) throws SdlException
	{
		if (ready())
		{
			if (fileInfo != null)
				proxy.addCommand(commandID, menuText, parentID, position,
								 vrCommands, fileInfo.name, ImageType.DYNAMIC, correlationID);
			else
				proxy.addCommand(commandID, menuText, parentID, position,
								 vrCommands, correlationID);
			return true;
		}
		else
		{
			proxy.putfile(fileInfo.name, fileInfo.type, false, fileInfo.data, correlationID);
			return false;
		}

	}


}

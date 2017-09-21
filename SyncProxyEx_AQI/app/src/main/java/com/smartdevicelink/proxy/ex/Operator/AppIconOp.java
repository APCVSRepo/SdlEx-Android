package com.smartdevicelink.proxy.ex.Operator;

import android.content.Context;

import com.smartdevicelink.exception.SdlException;
import com.smartdevicelink.proxy.SdlProxyALM;
import com.smartdevicelink.proxy.rpc.enums.FileType;

import java.io.InputStream;

/**
 * Created by leon on 16/9/28.
 */
public class AppIconOp extends SingleOperator
{
	public AppIconOp(String imagePath, FileType fileType, int correlationID)
	{
		updateFileInfo(imagePath, fileType);
		this.correlationID = correlationID;
	}

	public AppIconOp(int resID, Context ctx, FileType fileType, int correlationID)
	{
		updateFileInfo(resID, ctx, fileType);
		this.correlationID = correlationID;
	}

	public AppIconOp(InputStream is, FileType fileType, Integer correlationID)
	{
		updateFileInfo(is, fileType);
		this.correlationID = correlationID;
	}

	@Override
	public boolean execute(SdlProxyALM proxy) throws SdlException
	{
		if (ready())
		{
			proxy.setappicon(fileInfo.name, correlationID);
			return true;
		}
		else
		{
			proxy.putfile(fileInfo.name, fileInfo.type, false, fileInfo.data, correlationID);
			return false;
		}
	}


}

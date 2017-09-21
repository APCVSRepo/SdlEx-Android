package com.smartdevicelink.proxy.ex.Operator;


import android.content.Context;

import com.smartdevicelink.proxy.ex.Utils;
import com.smartdevicelink.proxy.rpc.enums.FileType;

import java.io.InputStream;

/**
 * Created by leon on 16/9/29.
 */
public abstract class SingleOperator extends Operator
{

	public void readyOnce()
	{
		fileSet.add(fileInfo.name);
		++prepareComplete;
	}

	protected void updateFileInfo(String imagePath, FileType fileType)
	{
		fileInfo = Utils.decodeFile(imagePath, fileType);
		if (fileSet.contains(fileInfo.name))
			this.prepareTotal = 0;
		else
			this.prepareTotal = 1;
	}

	protected void updateFileInfo(int resID, Context ctx, FileType fileType)
	{
		fileInfo = Utils.decodeFile(resID, ctx, fileType);
		if (fileSet.contains(fileInfo.name))
			this.prepareTotal = 0;
		else
			this.prepareTotal = 1;
	}

	protected void updateFileInfo(InputStream is, FileType fileType)
	{
		fileInfo = Utils.decodeFile(is, fileType);
		if (fileSet.contains(fileInfo.name))
			this.prepareTotal = 0;
		else
			this.prepareTotal = 1;
	}
}

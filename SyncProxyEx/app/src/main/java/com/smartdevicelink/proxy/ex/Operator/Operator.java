package com.smartdevicelink.proxy.ex.Operator;

import com.smartdevicelink.exception.SdlException;
import com.smartdevicelink.proxy.SdlProxyALM;
import com.smartdevicelink.proxy.ex.Utils;

import java.util.HashSet;
import java.util.List;

/**
 * Created by leon on 16/9/27.
 *
 */
public abstract class Operator
{
	protected int prepareTotal;
	protected int prepareComplete;
	protected int correlationID;
	protected Utils.FileInfo fileInfo;
	protected static HashSet<String> fileSet = new HashSet<>();
	protected static boolean persistent = false;

	public static void clear()
	{
		fileSet.clear();
	}

	public static void setImageList(List<String> fileName)
	{
		fileSet = new HashSet<>(fileName);
	}

	public static HashSet<String> getImageList()
	{
		return fileSet;
	}

	public static void setPersistent(boolean persistentMode)
	{
		persistent = persistentMode;
	}


	protected Operator()
	{
		prepareTotal = 0;
		prepareComplete = 0;
		correlationID = 0;
	}

	protected boolean ready()
	{
		return prepareTotal == prepareComplete;
	}

	public int getCorrelationID()
	{
		return correlationID;
	}

	// return false: not finish yet; return true: all success!
	public abstract boolean execute(SdlProxyALM proxy) throws SdlException;
	public abstract void readyOnce();


}

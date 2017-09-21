package com.smartdevicelink.proxy.ex.RPCStructEx;

import android.content.Context;

import com.smartdevicelink.proxy.rpc.SoftButton;
import com.smartdevicelink.proxy.rpc.enums.FileType;

import java.io.InputStream;


/**
 * Created by leon on 16/1/6.
 */
public class SoftButtonEx extends SoftButton implements IImageProvider
{
	private IImageProvider mProvider;

	public SoftButtonEx()
	{
		mProvider = new RPCImageProvider(this, this.getClass());
	}

	public void setImage(int resID, Context ctx, FileType type)
	{
		mProvider.setImage(resID, ctx, type);
	}

	public void setImage(String path, FileType type)
	{
		mProvider.setImage(path, type);
	}

	public void setImage(InputStream is, FileType type)
	{
		mProvider.setImage(is, type);
	}

	public FileType getFileType()
	{
		return mProvider.getFileType();
	}

	public byte[] getImageData()
	{
		return mProvider.getImageData();
	}

	public String getImageName()
	{
		return mProvider.getImageName();
	}
}

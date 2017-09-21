package com.smartdevicelink.proxy.ex.RPCStructEx;

import android.content.Context;

import com.smartdevicelink.proxy.rpc.Choice;
import com.smartdevicelink.proxy.rpc.enums.FileType;

import java.io.InputStream;

/**
 * Created by leon on 16/3/2.
 */
public class ChoiceEx extends Choice implements IImageProvider
{
	private IImageProvider mProvider;

	public ChoiceEx()
	{
		mProvider = new RPCImageProvider(this, this.getClass());
	}

	@Override
	public void setImage(int resID, Context ctx, FileType type)
	{
		mProvider.setImage(resID, ctx, type);
	}

	@Override
	public void setImage(String path, FileType type)
	{
		mProvider.setImage(path, type);
	}

	@Override
	public void setImage(InputStream is, FileType type)
	{
		mProvider.setImage(is, type);
	}

	@Override
	public FileType getFileType()
	{
		return mProvider.getFileType();
	}

	@Override
	public byte[] getImageData()
	{
		return mProvider.getImageData();
	}

	@Override
	public String getImageName()
	{
		return mProvider.getImageName();
	}
}

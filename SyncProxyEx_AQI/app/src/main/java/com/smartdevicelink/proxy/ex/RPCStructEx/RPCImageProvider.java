package com.smartdevicelink.proxy.ex.RPCStructEx;

import android.content.Context;

import com.smartdevicelink.proxy.ex.Utils;
import com.smartdevicelink.proxy.rpc.Image;
import com.smartdevicelink.proxy.rpc.enums.FileType;
import com.smartdevicelink.proxy.rpc.enums.ImageType;

import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by leon on 16/3/2.
 */
public class RPCImageProvider implements IImageProvider
{
	private Utils.FileInfo mFileInfo;
	private Object mObj;
	private Class mCls;

	public RPCImageProvider(Object obj, Class cls)
	{
		mObj = obj;
		mCls = cls;
	}

	public void setImage(int resID, Context ctx, FileType type)
	{
		mFileInfo = Utils.decodeFile(resID, ctx, type);
		setImageRPC(mFileInfo.name);
	}

	public void setImage(String path, FileType type)
	{
		mFileInfo = Utils.decodeFile(path, type);
		setImageRPC(mFileInfo.name);
	}

	public void setImage(InputStream is, FileType type)
	{
		mFileInfo = Utils.decodeFile(is, type);
		setImageRPC(mFileInfo.name);
	}

	public FileType getFileType()
	{
		if(mFileInfo == null)
			return null;
		else
			return mFileInfo.type;
	}

	public byte[] getImageData()
	{
		if(mFileInfo == null)
			return null;
		else
			return mFileInfo.data;
	}

	public String getImageName()
	{
		if(mFileInfo == null)
			return null;
		else
			return mFileInfo.name;
	}


	private void setImageRPC(String imageName)
	{
		Image img = new Image();
		img.setImageType(ImageType.DYNAMIC);
		img.setValue(imageName);

		try
		{
			Method method = mCls.getMethod("setImage", Image.class);
			method.invoke(mObj, img);
		}
		catch (NoSuchMethodException e)
		{
			e.printStackTrace();
		}
		catch (InvocationTargetException e)
		{
			e.printStackTrace();
		}
		catch (IllegalAccessException e)
		{
			e.printStackTrace();
		}

		//obj.setImage(img);

	}
}

package com.smartdevicelink.proxy.ex.RPCStructEx;

import android.content.Context;

import com.smartdevicelink.proxy.ex.Utils;
import com.smartdevicelink.proxy.rpc.enums.FileType;
import java.io.InputStream;


/**
 * Created by leon on 16/3/2.
 */
public interface IImageProvider
{
	public void setImage(int resID, Context ctx, FileType type);

	public void setImage(String path, FileType type);

	public void setImage(InputStream is, FileType type);

	public FileType getFileType();

	public byte[] getImageData();

	public String getImageName();

}

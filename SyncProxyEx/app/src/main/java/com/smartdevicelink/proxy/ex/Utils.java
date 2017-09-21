package com.smartdevicelink.proxy.ex;

import android.content.Context;

import com.smartdevicelink.proxy.rpc.enums.FileType;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by leon on 16/1/7.
 */
public class Utils
{
	public static final String[] DEVICE_NAME =
			{
					"SYNC",
					"Ford Fiesta",
					"Ford Focus",
					"Ford Fusion",
					"Ford C-Max",
					"Ford Taurus",
					"Ford Mustang",
					"Ford Ecosport",
					"Ford Escape",
					"Ford Edge",
					"Ford Flex",
					"Ford Explorer",
					"Ford Expedition",
					"Ford Ranger",
					"Ford F150",
					"Ford F250",
					"Ford F350",
					"Ford F450",
					"Ford F550",
					"Ford Transit Connect",
					"Ford Transit",
					"Ford E150",
					"Ford E350",
					"Ford F650",
					"Ford F750",
					"Lincoln MKZ",
					"Lincoln MKS",
					"Lincoln MKC",
					"Lincoln MKX",
					"Lincoln  MKT",
					"Lincoln Navigator",
					"Ford Ka",
					"Ford Fiesta",
					"Ford Transit Courier",
					"Ford B-Max",
					"Ford Grand C-Max",
					"Ford Mondeo",
					"Ford Kuga",
					"Ford S-Max",
					"Ford Galaxy",
					"Ford Figo",
					"Ford Escort",
					"Ford Falcon",
					"Ford Everest",
					"Ford Territory",
					"Ford Raptor",
					"Lincoln Continental",
					"Ford GT",
			};


	public static class FileInfo
	{
		public String name;
		public FileType type;
		public byte[] data;
	}

	private static byte[] inputStream2Byte(InputStream is)
	{
		if (is == null)
			return null;

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buf = new byte[1024 * 32];
		int rc;

		try
		{
			while ((rc = is.read(buf, 0, buf.length)) > 0)
			{
				baos.write(buf, 0, rc);
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return baos.toByteArray();
	}

	private static String getMD5Checksum(byte[] data)
	{
		String result = "";
		byte[] digest = null;

		try
		{
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			md5.update(data);
			digest = md5.digest();
		}
		catch (NoSuchAlgorithmException e)
		{
			e.printStackTrace();
		}

		for (int i=0; i<digest.length; i++)
		{
			result += Integer.toString((digest[i] & 0xff) + 0x100, 16).substring(1);
		}
		return result;
	}

	private static String getFileNameByMD5(byte[] data)
	{
		if (data != null)
			return getMD5Checksum(data).substring(0, 27); 	// more than 28 characters cannot be supported
		else
			return null;
	}

	private static FileInputStream path2InputStream(String path)
	{
		FileInputStream fis = null;
		try
		{
			fis = new FileInputStream(path);
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}

		return fis;
	}

	public static FileInfo decodeFile(int resID, Context ctx, FileType type)
	{
		InputStream is = ctx.getResources().openRawResource(resID);
		return decodeFile(is, type);
	}

	public static FileInfo decodeFile(String path, FileType type)
	{
		return decodeFile(Utils.path2InputStream(path), type);
	}

	public static FileInfo decodeFile(InputStream is, FileType type)
	{
		FileInfo fi = new FileInfo();
		fi.type = type;
		fi.data = Utils.inputStream2Byte(is);
		fi.name = Utils.getFileNameByMD5(fi.data);

		return fi;
	}
}

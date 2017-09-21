package com.smartdevicelink.proxy.ex.Operator;

import com.smartdevicelink.proxy.ex.RPCStructEx.IImageProvider;
import com.smartdevicelink.proxy.ex.Utils;

import java.util.Iterator;
import java.util.Vector;

/**
 * Created by leon on 16/9/29.
 */
public abstract class MultiOperator extends Operator
{
	private Vector<Utils.FileInfo> fileInfoVec;

	public MultiOperator()
	{
		fileInfoVec = new Vector<>();
	}

	@Override
	public void readyOnce()
	{
		fileSet.add(fileInfo.name);

		if (++prepareComplete < prepareTotal)
			fileInfo = fileInfoVec.get(prepareComplete);
	}

	// updateFileInfo object if need putFile or not
	protected void updateFileInfo(Iterator iter)
	{
		prepareTotal = 0;
		while (iter.hasNext())
		{
			Object obj = iter.next();
			if (obj instanceof IImageProvider)
			{
				IImageProvider imageProvider = (IImageProvider)obj;
				Utils.FileInfo fi = new Utils.FileInfo();
				fi.name = imageProvider.getImageName();
				fi.data = imageProvider.getImageData();
				fi.type = imageProvider.getFileType();

				if (fi.name == null || fi.data == null || fi.type == null)
					continue;

				// not allow repeat in a batch
				boolean exist = false;
				for (Utils.FileInfo one : fileInfoVec)
				{
					if (one.name.equals(fi.name))
					{
						exist = true;
						break;
					}
				}

				if (!fileSet.contains(fi.name) && !exist)
				{
					fileInfoVec.add(fi);
					++prepareTotal;
				}
			}
		}

		if (prepareTotal > 0)
			fileInfo = fileInfoVec.get(0);
	}
}

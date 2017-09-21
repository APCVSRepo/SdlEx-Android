package com.smartdevicelink.proxy.ex.Operator;

import android.content.Context;

import com.smartdevicelink.exception.SdlException;
import com.smartdevicelink.proxy.SdlProxyALM;
import com.smartdevicelink.proxy.rpc.Image;
import com.smartdevicelink.proxy.rpc.Show;
import com.smartdevicelink.proxy.rpc.enums.FileType;
import com.smartdevicelink.proxy.rpc.enums.ImageType;

import java.io.InputStream;

/**
 * Created by leon on 16/9/27.
 */
public class ShowOp extends SingleOperator
{
	private ShowType showType;

	public enum ShowType
	{
		IMAGE_MAIN,
		IMAGE_SECONDARY,
	}


	public ShowOp(String imagePath, FileType fileType, ShowOp.ShowType showType, Integer correlationID)
	{
		updateFileInfo(imagePath, fileType);
		this.showType = showType;
		this.correlationID = correlationID;
	}

	public ShowOp(int resID, Context ctx, FileType fileType, ShowOp.ShowType showType, Integer correlationID)
	{
		updateFileInfo(resID, ctx, fileType);
		this.showType = showType;
		this.correlationID = correlationID;
	}

	public ShowOp(InputStream is, FileType fileType, ShowOp.ShowType showType, Integer correlationID)
	{
		updateFileInfo(is, fileType);
		this.showType = showType;
		this.correlationID = correlationID;
	}

	@Override
	public boolean execute(SdlProxyALM proxy) throws SdlException
	{
		if (ready())
		{
			Image image = new Image();
			image.setImageType(ImageType.DYNAMIC);
			image.setValue(fileInfo.name);

			Show show = new Show();
			show.setCorrelationID(correlationID);
			if (showType == ShowType.IMAGE_MAIN)
				show.setGraphic(image);
			else
				show.setSecondaryGraphic(image);
			proxy.sendRPCRequest(show);

			return true;
		}
		else
		{
			proxy.putfile(fileInfo.name, fileInfo.type, false, fileInfo.data, correlationID);
			return false;
		}


	}

}

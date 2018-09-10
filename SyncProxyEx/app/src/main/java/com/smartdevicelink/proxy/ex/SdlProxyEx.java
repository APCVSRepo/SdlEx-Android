package com.smartdevicelink.proxy.ex;

import android.content.Context;

import com.smartdevicelink.exception.SdlException;
import com.smartdevicelink.proxy.SdlProxyALM;
import com.smartdevicelink.proxy.ex.Operator.AppIconOp;
import com.smartdevicelink.proxy.ex.Operator.ModifyCommandOp;
import com.smartdevicelink.proxy.ex.Operator.Operator;
import com.smartdevicelink.proxy.ex.Operator.ChoiceOp;
import com.smartdevicelink.proxy.ex.Operator.AddCommandOp;
import com.smartdevicelink.proxy.ex.Operator.ShowOp;
import com.smartdevicelink.proxy.ex.Operator.SoftButtonOp;
import com.smartdevicelink.proxy.rpc.Choice;
import com.smartdevicelink.proxy.rpc.SoftButton;
import com.smartdevicelink.proxy.rpc.enums.FileType;
import com.smartdevicelink.proxy.rpc.enums.Language;
import com.smartdevicelink.transport.BaseTransportConfig;

import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * Created by leon on 15/1/9.
 */
public class SdlProxyEx extends SdlProxyALM
{
	private static final int INTERVAL_TIME_WORD = 250;
	private static final int INTERVAL_TIME_SENTENCE = 1500;
	private class SpeakInfo
	{
		public String text;
		public Boolean interrupt;
		public Integer correlationID;

		SpeakInfo(String text, Boolean interrupt, Integer correlationID)
		{
			this.text = text;
			this.interrupt = interrupt;
			this.correlationID = correlationID;
		}
	}

	private ArrayBlockingQueue<SpeakInfo> mSpeakInfoQ = new ArrayBlockingQueue<>(4096);

	private Operator mCurOperator = null;
	private ArrayBlockingQueue<Operator> mOperatorQ = new ArrayBlockingQueue<>(4096);

	private Context mContext = null;
	private ProxyListenerALMEx mProxyListener = null;

	public SdlProxyEx(IProxyListenerALMEx listener, String appName, Boolean isMediaApp, String appID,
					  Context context, Integer iconID, FileType iconFileType,
					  BaseTransportConfig baseTransportConfig) throws SdlException
	{
		super(ProxyListenerALMEx.getInstance(),
			  appName, isMediaApp, Language.ZH_CN, Language.ZH_CN, appID, baseTransportConfig);

		mProxyListener = ProxyListenerALMEx.getInstance();
		mProxyListener.setParam(this, listener, iconID, iconFileType);

		mUploadFileThread.start();
		mSpeakThread.start();
		mContext = context;
	}

	public SdlProxyEx(IProxyListenerALMEx listener, String appName, Boolean isMediaApp, String appID,
					  Context context, String iconPath, FileType iconFileType,
					  BaseTransportConfig baseTransportConfig) throws SdlException
	{
		super(ProxyListenerALMEx.getInstance(),
			  appName, isMediaApp, Language.ZH_CN, Language.ZH_CN, appID, baseTransportConfig);

		mProxyListener = ProxyListenerALMEx.getInstance();
		mProxyListener.setParam(this, listener, iconPath, iconFileType);

		mUploadFileThread.start();
		mSpeakThread.start();
		mContext = context;
	}


	@Override
	public void dispose() throws SdlException
	{
		mCurOperator = null;
		mContext = null;
		mOperatorQ.clear();
		Operator.clear();

		mProxyListener.onDispose();
		super.dispose();
	}

	//================================ UPLOAD OPERATOR ================================
	private Thread mUploadFileThread = new Thread()
	{
		@Override
		public void run()
		{
			super.run();

			while (true)
			{
				try
				{
					mCurOperator = mOperatorQ.take();
					if (mCurOperator.execute(SdlProxyEx.this))
						continue;

					synchronized (mUploadFileThread)
					{
						try
						{
							wait();
						}
						catch (InterruptedException e)
						{
							e.printStackTrace();
						}
					}

					mCurOperator = null;
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
					return;
				}
				catch (SdlException e)
				{
					e.printStackTrace();
				}

			}
		}
	};


	private Thread mSpeakThread = new Thread()
	{
		@Override
		public void run()
		{
			super.run();

			while (true)
			{
				try
				{
					SpeakInfo si = mSpeakInfoQ.take();
					speak(si.text, si.correlationID);

					int delay = 0;
					if (!si.interrupt)
						delay = si.text.length() * INTERVAL_TIME_WORD + INTERVAL_TIME_SENTENCE;

					sleep(delay);
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
					return;
				}
				catch (SdlException e)
				{
					e.printStackTrace();
				}
			}
		}
	};


	//===============================RPC callback=======================================
	void onRPCResponse(boolean success) throws SdlException
	{
		if (success)
		{
			mCurOperator.readyOnce();
			if (!mCurOperator.execute(this))
				return;
		}

		synchronized (mUploadFileThread)
		{
			mUploadFileThread.notify();
		}
	}

	boolean isOperator(int correlationID)
	{
		if (mCurOperator == null)
			return false;
		else
			return correlationID == mCurOperator.getCorrelationID();
	}

	void updateImageList(List<String> fileName)
	{
		Operator.setImageList(fileName);
	}

	//=======================================INTERFACE=======================================//
	public static boolean isSdlDevice(String deviceName)
	{
		for (String one : Utils.DEVICE_NAME)
		{
			if (one.equals(deviceName))
				return true;
		}
		return false;
	}


	//	public void clearSoftButton() throws SdlException
	//	{
	//		mSoftButtons = null;
	//		mSoftButtonImageUpload = 0;
	//
	//		Show rpc = new Show();
	//		rpc.setSoftButtons(null);
	//		sendRPCRequest(rpc);
	//	}

	public HashSet<String> getImageList()
	{
		return Operator.getImageList();
	}

	public void setPersistentMode(boolean persistentMode)
	{
		Operator.setPersistent(persistentMode);
	}

	// Support SoftButtonEx
	public boolean showSoftButton(List<SoftButton> softButtons, int correlationID) throws SdlException
	{
		// store in Q
		SoftButtonOp param = new SoftButtonOp(softButtons, correlationID);
		mOperatorQ.add(param);
		return true;
	}

	// Support ChoiceEx
	public boolean createInteractionChoiceSetEx(Vector<Choice> choices, Integer choiceSetID, int correlationID) throws SdlException
	{
		// store in Q
		ChoiceOp param = new ChoiceOp(choices, choiceSetID, correlationID);
		mOperatorQ.add(param);
		return true;
	}

	public boolean addCommandEx(Integer commandID, String menuText, Integer parentID, Integer position, Vector<String> vrCommands,
								String imagePath, FileType fileType, Integer correlationID) throws SdlException
	{
		AddCommandOp param = new AddCommandOp(commandID, menuText, parentID, position, vrCommands, imagePath, fileType, correlationID);
		mOperatorQ.add(param);
		return true;
	}

	public boolean addCommandEx(Integer commandID, String menuText, Integer parentID, Integer position, Vector<String> vrCommands,
								int resID, FileType fileType, Integer correlationID) throws SdlException
	{
		AddCommandOp
				param = new AddCommandOp(commandID, menuText, parentID, position, vrCommands, resID, mContext, fileType, correlationID);
		mOperatorQ.add(param);
		return true;
	}

	public boolean addCommandEx(Integer commandID, String menuText, Integer parentID, Integer position, Vector<String> vrCommands,
								InputStream is, FileType fileType, Integer correlationID) throws SdlException
	{
		AddCommandOp param = new AddCommandOp(commandID, menuText, parentID, position, vrCommands, is, fileType, correlationID);
		mOperatorQ.add(param);
		return true;
	}


	public boolean modifyCommandEx(Integer delCommandID, Integer commandID, String menuText, Integer parentID, Integer position, Vector<String> vrCommands,
								   Integer correlationID) throws SdlException
	{
		ModifyCommandOp param = new ModifyCommandOp(delCommandID, commandID, menuText, parentID, position, vrCommands, correlationID);
		mOperatorQ.add(param);
		return true;
	}

	public boolean modifyCommandEx(Integer delCommandID, Integer commandID, String menuText, Integer parentID, Integer position, Vector<String> vrCommands,
								   String imagePath, FileType fileType, Integer correlationID) throws SdlException
	{
		ModifyCommandOp param = new ModifyCommandOp(delCommandID, commandID, menuText, parentID, position, vrCommands, imagePath, fileType, correlationID);
		mOperatorQ.add(param);
		return true;
	}

	public boolean modifyCommandEx(Integer delCommandID, Integer commandID, String menuText, Integer parentID, Integer position, Vector<String> vrCommands,
								   int resID, FileType fileType, Integer correlationID) throws SdlException
	{
		ModifyCommandOp param = new ModifyCommandOp(delCommandID, commandID, menuText, parentID, position, vrCommands, resID, mContext, fileType, correlationID);
		mOperatorQ.add(param);
		return true;
	}

	public boolean modifyCommandEx(Integer delCommandID, Integer commandID, String menuText, Integer parentID, Integer position, Vector<String> vrCommands,
								   InputStream is, FileType fileType, Integer correlationID) throws SdlException
	{
		ModifyCommandOp param = new ModifyCommandOp(delCommandID, commandID, menuText, parentID, position, vrCommands, is, fileType, correlationID);
		mOperatorQ.add(param);
		return true;
	}

	public boolean showImg(String imagePath, FileType fileType, ShowOp.ShowType showType, Integer correlationID) throws SdlException
	{
		ShowOp sp = new ShowOp(imagePath, fileType, showType, correlationID);
		mOperatorQ.add(sp);
		return true;
	}

	public boolean showImg(int resID, FileType fileType, ShowOp.ShowType showType, Integer correlationID) throws SdlException
	{
		ShowOp sp = new ShowOp(resID, mContext, fileType, showType, correlationID);
		mOperatorQ.add(sp);
		return true;
	}

	public boolean showImg(InputStream is, FileType fileType, ShowOp.ShowType showType, Integer correlationID) throws SdlException
	{
		ShowOp sp = new ShowOp(is, fileType, showType, correlationID);
		mOperatorQ.add(sp);
		return true;
	}

	public boolean showAppIcon(String imagePath, FileType fileType, Integer correlationID) throws                                                      SdlException
	{
		AppIconOp aip = new AppIconOp(imagePath, fileType, correlationID);
		mOperatorQ.add(aip);
		return true;
	}

	public boolean showAppIcon(int resID, FileType fileType, Integer correlationID) throws SdlException
	{
		AppIconOp aip = new AppIconOp(resID, mContext, fileType, correlationID);
		mOperatorQ.add(aip);
		return true;
	}

	public boolean showAppIcon(InputStream is, FileType fileType, Integer correlationID) throws SdlException
	{
		AppIconOp aip = new AppIconOp(is, fileType, correlationID);
		mOperatorQ.add(aip);
		return true;
	}

	public boolean speakEx(String text, Boolean interrupt, Integer correlationID) throws SdlException
	{
		SpeakInfo si = new SpeakInfo(text, interrupt, correlationID);
		mSpeakInfoQ.add(si);
		return true;
	}

}

package com.smartdevicelink.proxy.ex;


import com.smartdevicelink.exception.SdlException;
import com.smartdevicelink.proxy.callbacks.OnServiceEnded;
import com.smartdevicelink.proxy.callbacks.OnServiceNACKed;
import com.smartdevicelink.proxy.rpc.AddCommandResponse;
import com.smartdevicelink.proxy.rpc.AddSubMenuResponse;
import com.smartdevicelink.proxy.rpc.AlertManeuverResponse;
import com.smartdevicelink.proxy.rpc.AlertResponse;
import com.smartdevicelink.proxy.rpc.ButtonPressResponse;
import com.smartdevicelink.proxy.rpc.ChangeRegistrationResponse;
import com.smartdevicelink.proxy.rpc.CreateInteractionChoiceSetResponse;
import com.smartdevicelink.proxy.rpc.DeleteCommandResponse;
import com.smartdevicelink.proxy.rpc.DeleteFileResponse;
import com.smartdevicelink.proxy.rpc.DeleteInteractionChoiceSetResponse;
import com.smartdevicelink.proxy.rpc.DeleteSubMenuResponse;
import com.smartdevicelink.proxy.rpc.DiagnosticMessageResponse;
import com.smartdevicelink.proxy.rpc.DialNumberResponse;
import com.smartdevicelink.proxy.rpc.EndAudioPassThruResponse;
import com.smartdevicelink.proxy.rpc.GenericResponse;
import com.smartdevicelink.proxy.rpc.GetDTCsResponse;
import com.smartdevicelink.proxy.rpc.GetInteriorVehicleDataResponse;
import com.smartdevicelink.proxy.rpc.GetSystemCapabilityResponse;
import com.smartdevicelink.proxy.rpc.GetVehicleDataResponse;
import com.smartdevicelink.proxy.rpc.GetWayPointsResponse;
import com.smartdevicelink.proxy.rpc.ListFilesResponse;
import com.smartdevicelink.proxy.rpc.OnAudioPassThru;
import com.smartdevicelink.proxy.rpc.OnButtonEvent;
import com.smartdevicelink.proxy.rpc.OnButtonPress;
import com.smartdevicelink.proxy.rpc.OnCommand;
import com.smartdevicelink.proxy.rpc.OnDriverDistraction;
import com.smartdevicelink.proxy.rpc.OnHMIStatus;
import com.smartdevicelink.proxy.rpc.OnHashChange;
import com.smartdevicelink.proxy.rpc.OnInteriorVehicleData;
import com.smartdevicelink.proxy.rpc.OnKeyboardInput;
import com.smartdevicelink.proxy.rpc.OnLanguageChange;
import com.smartdevicelink.proxy.rpc.OnLockScreenStatus;
import com.smartdevicelink.proxy.rpc.OnPermissionsChange;
import com.smartdevicelink.proxy.rpc.OnStreamRPC;
import com.smartdevicelink.proxy.rpc.OnSystemRequest;
import com.smartdevicelink.proxy.rpc.OnTBTClientState;
import com.smartdevicelink.proxy.rpc.OnTouchEvent;
import com.smartdevicelink.proxy.rpc.OnVehicleData;
import com.smartdevicelink.proxy.rpc.OnWayPointChange;
import com.smartdevicelink.proxy.rpc.PerformAudioPassThruResponse;
import com.smartdevicelink.proxy.rpc.PerformInteractionResponse;
import com.smartdevicelink.proxy.rpc.PutFileResponse;
import com.smartdevicelink.proxy.rpc.ReadDIDResponse;
import com.smartdevicelink.proxy.rpc.ResetGlobalPropertiesResponse;
import com.smartdevicelink.proxy.rpc.ScrollableMessageResponse;
import com.smartdevicelink.proxy.rpc.SendHapticDataResponse;
import com.smartdevicelink.proxy.rpc.SendLocationResponse;
import com.smartdevicelink.proxy.rpc.SetAppIconResponse;
import com.smartdevicelink.proxy.rpc.SetDisplayLayoutResponse;
import com.smartdevicelink.proxy.rpc.SetGlobalPropertiesResponse;
import com.smartdevicelink.proxy.rpc.SetInteriorVehicleDataResponse;
import com.smartdevicelink.proxy.rpc.SetMediaClockTimerResponse;
import com.smartdevicelink.proxy.rpc.ShowConstantTbtResponse;
import com.smartdevicelink.proxy.rpc.ShowResponse;
import com.smartdevicelink.proxy.rpc.SliderResponse;
import com.smartdevicelink.proxy.rpc.SpeakResponse;
import com.smartdevicelink.proxy.rpc.StreamRPCResponse;
import com.smartdevicelink.proxy.rpc.SubscribeButtonResponse;
import com.smartdevicelink.proxy.rpc.SubscribeVehicleDataResponse;
import com.smartdevicelink.proxy.rpc.SubscribeWayPointsResponse;
import com.smartdevicelink.proxy.rpc.SystemRequestResponse;
import com.smartdevicelink.proxy.rpc.UnsubscribeButtonResponse;
import com.smartdevicelink.proxy.rpc.UnsubscribeVehicleDataResponse;
import com.smartdevicelink.proxy.rpc.UnsubscribeWayPointsResponse;
import com.smartdevicelink.proxy.rpc.UpdateTurnListResponse;
import com.smartdevicelink.proxy.rpc.enums.FileType;
import com.smartdevicelink.proxy.rpc.enums.HMILevel;
import com.smartdevicelink.proxy.rpc.enums.SdlDisconnectedReason;

/**
 * Created by leon on 15/1/19.
 */
public class ProxyListenerALMEx implements IProxyListenerALMEx
{
	private static final int COID_SHOWDEFAULTICON = 64200;
	private static final int COID_LISTIMAGEFILE = 64201;

	private boolean mFirstRun = false;
	private HMILevel mLastHmiLevel = HMILevel.HMI_NONE;
	private Object mIcon = null;		// iconID or iconPath
	private FileType mIconFileType = null;

	private SdlProxyEx mProxy = null;
	private IProxyListenerALMEx mForwardListener = null;

	private static ProxyListenerALMEx mInstance = null;
	public static ProxyListenerALMEx getInstance()
	{
		if (mInstance == null)
		{
			mInstance = new ProxyListenerALMEx();
		}
		return mInstance;
	}

	protected ProxyListenerALMEx()
	{

	}

	public void setParam(SdlProxyEx proxy, IProxyListenerALMEx forwardListener,
						 Object icon, FileType iconFileType)
	{
		mProxy = proxy;
		mForwardListener = forwardListener;
		mIcon = icon;
		mIconFileType = iconFileType;
		mFirstRun = false;
		mLastHmiLevel = HMILevel.HMI_NONE;
	}

	public void onDispose()
	{
		mFirstRun = false;
		mLastHmiLevel = HMILevel.HMI_NONE;
	}

	@Override
	public void onSDLResume()
	{
		// Used for synchronization from object, i.e. song list update(add, delete and etc.)
		if (mForwardListener != null)
			mForwardListener.onSDLResume();
	}

	@Override
	public void onPerformInteractionResponse(PerformInteractionResponse response)
	{
		if (mForwardListener != null)
			mForwardListener.onPerformInteractionResponse(response);
	}


	@Override
	public void onOnHMIStatus(OnHMIStatus status)
	{
		// Set icon
		if (!mFirstRun && mIcon != null && mIconFileType != null)
		{
			mFirstRun = true;

			try
			{
				if (mIcon instanceof String)
					mProxy.showAppIcon((String)mIcon, mIconFileType, COID_SHOWDEFAULTICON);
				else
					mProxy.showAppIcon((Integer)mIcon, mIconFileType, COID_SHOWDEFAULTICON);

				mProxy.listfiles(64201);
			}
			catch (SdlException e)
			{
				e.printStackTrace();
			}
		}

		// HMI_NONE -> HMI_FULL
		if (mLastHmiLevel == HMILevel.HMI_NONE && status.getHmiLevel() == HMILevel.HMI_FULL)
		{
			onSDLResume();
		}
		mLastHmiLevel = status.getHmiLevel();

		if (mForwardListener != null)
			mForwardListener.onOnHMIStatus(status);
	}

	@Override
	public void onProxyClosed(String s, Exception e, SdlDisconnectedReason reason)
	{
		if (mForwardListener != null)
			mForwardListener.onProxyClosed(s, e, reason);
	}

	@Override
	public void onServiceEnded(OnServiceEnded end)
	{
		if (mForwardListener != null)
			mForwardListener.onServiceEnded(end);
	}

	@Override
	public void onServiceNACKed(OnServiceNACKed ack)
	{
		if (mForwardListener != null)
			mForwardListener.onServiceNACKed(ack);
	}

	@Override
	public void onOnStreamRPC(OnStreamRPC rpc)
	{
		if (mForwardListener != null)
			mForwardListener.onOnStreamRPC(rpc);
	}

	@Override
	public void onStreamRPCResponse(StreamRPCResponse response)
	{
		if (mForwardListener != null)
			mForwardListener.onStreamRPCResponse(response);
	}

	@Override
	public void onError(String s, Exception e)
	{
		if (mForwardListener != null)
			mForwardListener.onError(s, e);
	}

	@Override
	public void onGenericResponse(GenericResponse response)
	{
		if (mForwardListener != null)
			mForwardListener.onGenericResponse(response);
	}

	@Override
	public void onOnCommand(OnCommand command)
	{
		if (mForwardListener != null)
			mForwardListener.onOnCommand(command);
	}

	@Override
	public void onAddCommandResponse(AddCommandResponse response)
	{
		if (mForwardListener != null)
			mForwardListener.onAddCommandResponse(response);
	}

	@Override
	public void onAddSubMenuResponse(AddSubMenuResponse response)
	{
		if (mForwardListener != null)
			mForwardListener.onAddSubMenuResponse(response);
	}

	@Override
	public void onCreateInteractionChoiceSetResponse(CreateInteractionChoiceSetResponse response)
	{
		if (mForwardListener != null)
			mForwardListener.onCreateInteractionChoiceSetResponse(response);
	}

	@Override
	public void onAlertResponse(AlertResponse response)
	{
		if (mForwardListener != null)
			mForwardListener.onAlertResponse(response);
	}

	@Override
	public void onDeleteCommandResponse(DeleteCommandResponse response)
	{
		boolean success = response.getSuccess();

		if (mProxy.isOperator(response.getCorrelationID()))
		{
			try
			{
				mProxy.onRPCResponse(success);
			}
			catch (SdlException e)
			{
				e.printStackTrace();
			}
		}
		else
		{
			if (mForwardListener != null)
				mForwardListener.onDeleteCommandResponse(response);
		}
	}

	@Override
	public void onDeleteInteractionChoiceSetResponse(DeleteInteractionChoiceSetResponse response)
	{
		if (mForwardListener != null)
			mForwardListener.onDeleteInteractionChoiceSetResponse(response);
	}

	@Override
	public void onDeleteSubMenuResponse(DeleteSubMenuResponse response)
	{
		if (mForwardListener != null)
			mForwardListener.onDeleteSubMenuResponse(response);
	}

	@Override
	public void onResetGlobalPropertiesResponse(ResetGlobalPropertiesResponse response)
	{
		if (mForwardListener != null)
			mForwardListener.onResetGlobalPropertiesResponse(response);
	}

	@Override
	public void onSetGlobalPropertiesResponse(SetGlobalPropertiesResponse response)
	{
		if (mForwardListener != null)
			mForwardListener.onSetGlobalPropertiesResponse(response);
	}

	@Override
	public void onSetMediaClockTimerResponse(SetMediaClockTimerResponse response)
	{
		if (mForwardListener != null)
			mForwardListener.onSetMediaClockTimerResponse(response);
	}

	@Override
	public void onShowResponse(ShowResponse response)
	{
		if (mForwardListener != null)
			mForwardListener.onShowResponse(response);
	}

	@Override
	public void onSpeakResponse(SpeakResponse response)
	{
		if (mForwardListener != null)
			mForwardListener.onSpeakResponse(response);
	}

	@Override
	public void onOnButtonEvent(OnButtonEvent event)
	{
		if (mForwardListener != null)
			mForwardListener.onOnButtonEvent(event);
	}

	@Override
	public void onOnButtonPress(OnButtonPress press)
	{
		if (mForwardListener != null)
			mForwardListener.onOnButtonPress(press);
	}

	@Override
	public void onSubscribeButtonResponse(SubscribeButtonResponse response)
	{
		if (mForwardListener != null)
			mForwardListener.onSubscribeButtonResponse(response);
	}

	@Override
	public void onUnsubscribeButtonResponse(UnsubscribeButtonResponse response)
	{
		if (mForwardListener != null)
			mForwardListener.onUnsubscribeButtonResponse(response);
	}

	@Override
	public void onOnPermissionsChange(OnPermissionsChange change)
	{
		if (mForwardListener != null)
			mForwardListener.onOnPermissionsChange(change);
	}

	@Override
	public void onSubscribeVehicleDataResponse(SubscribeVehicleDataResponse response)
	{
		if (mForwardListener != null)
			mForwardListener.onSubscribeVehicleDataResponse(response);
	}

	@Override
	public void onUnsubscribeVehicleDataResponse(UnsubscribeVehicleDataResponse response)
	{
		if (mForwardListener != null)
			mForwardListener.onUnsubscribeVehicleDataResponse(response);
	}

	@Override
	public void onGetVehicleDataResponse(GetVehicleDataResponse response)
	{
		if (mForwardListener != null)
			mForwardListener.onGetVehicleDataResponse(response);
	}

	@Override
	public void onOnVehicleData(OnVehicleData data)
	{
		if (mForwardListener != null)
			mForwardListener.onOnVehicleData(data);
	}

	@Override
	public void onPerformAudioPassThruResponse(PerformAudioPassThruResponse response)
	{
		if (mForwardListener != null)
			mForwardListener.onPerformAudioPassThruResponse(response);
	}

	@Override
	public void onEndAudioPassThruResponse(EndAudioPassThruResponse response)
	{
		if (mForwardListener != null)
			mForwardListener.onEndAudioPassThruResponse(response);
	}

	@Override
	public void onOnAudioPassThru(OnAudioPassThru apt)
	{
		if (mForwardListener != null)
			mForwardListener.onOnAudioPassThru(apt);
	}

	@Override
	public void onPutFileResponse(PutFileResponse response)
	{
		boolean success = response.getSuccess();

		if (mProxy.isOperator(response.getCorrelationID()))
		{
			try
			{
				mProxy.onRPCResponse(success);
			}
			catch (SdlException e)
			{
				e.printStackTrace();
			}
		}
		else
		{
			if (mForwardListener != null)
				mForwardListener.onPutFileResponse(response);
		}

	}

	@Override
	public void onDeleteFileResponse(DeleteFileResponse response)
	{
		if (mForwardListener != null)
			mForwardListener.onDeleteFileResponse(response);
	}

	@Override
	public void onListFilesResponse(ListFilesResponse response)
	{
		if (response.getCorrelationID() == COID_LISTIMAGEFILE)
		{
			mProxy.updateImageList(response.getFilenames());
			return;
		}

		if (mForwardListener != null)
			mForwardListener.onListFilesResponse(response);
	}

	@Override
	public void onSetAppIconResponse(SetAppIconResponse response)
	{
		if (mForwardListener != null)
			mForwardListener.onSetAppIconResponse(response);
	}

	@Override
	public void onScrollableMessageResponse(ScrollableMessageResponse response)
	{
		if (mForwardListener != null)
			mForwardListener.onScrollableMessageResponse(response);
	}

	@Override
	public void onChangeRegistrationResponse(ChangeRegistrationResponse response)
	{
		if (mForwardListener != null)
			mForwardListener.onChangeRegistrationResponse(response);
	}

	@Override
	public void onSetDisplayLayoutResponse(SetDisplayLayoutResponse response)
	{
		if (mForwardListener != null)
			mForwardListener.onSetDisplayLayoutResponse(response);
	}

	@Override
	public void onOnLanguageChange(OnLanguageChange change)
	{
		if (mForwardListener != null)
			mForwardListener.onOnLanguageChange(change);
	}

	@Override
	public void onOnHashChange(OnHashChange change)
	{
		if (mForwardListener != null)
			mForwardListener.onOnHashChange(change);
	}

	@Override
	public void onSliderResponse(SliderResponse response)
	{
		if (mForwardListener != null)
			mForwardListener.onSliderResponse(response);
	}

	@Override
	public void onOnDriverDistraction(OnDriverDistraction dt)
	{
		if (mForwardListener != null)
			mForwardListener.onOnDriverDistraction(dt);
	}

	@Override
	public void onOnTBTClientState(OnTBTClientState state)
	{
		if (mForwardListener != null)
			mForwardListener.onOnTBTClientState(state);
	}

	@Override
	public void onOnSystemRequest(OnSystemRequest request)
	{
		if (mForwardListener != null)
			mForwardListener.onOnSystemRequest(request);
	}

	@Override
	public void onSystemRequestResponse(SystemRequestResponse response)
	{
		if (mForwardListener != null)
			mForwardListener.onSystemRequestResponse(response);
	}

	@Override
	public void onOnKeyboardInput(OnKeyboardInput input)
	{
		if (mForwardListener != null)
			mForwardListener.onOnKeyboardInput(input);
	}

	@Override
	public void onOnTouchEvent(OnTouchEvent event)
	{
		if (mForwardListener != null)
			mForwardListener.onOnTouchEvent(event);
	}

	@Override
	public void onDiagnosticMessageResponse(DiagnosticMessageResponse response)
	{
		if (mForwardListener != null)
			mForwardListener.onDiagnosticMessageResponse(response);
	}

	@Override
	public void onReadDIDResponse(ReadDIDResponse response)
	{
		if (mForwardListener != null)
			mForwardListener.onReadDIDResponse(response);
	}

	@Override
	public void onGetDTCsResponse(GetDTCsResponse response)
	{
		if (mForwardListener != null)
			mForwardListener.onGetDTCsResponse(response);
	}

	@Override
	public void onOnLockScreenNotification(OnLockScreenStatus status)
	{
		if (mForwardListener != null)
			mForwardListener.onOnLockScreenNotification(status);
	}

	@Override
	public void onDialNumberResponse(DialNumberResponse response)
	{
		if (mForwardListener != null)
			mForwardListener.onDialNumberResponse(response);
	}

	@Override
	public void onSendLocationResponse(SendLocationResponse response)
	{
		if (mForwardListener != null)
			mForwardListener.onSendLocationResponse(response);
	}

	@Override
	public void onShowConstantTbtResponse(ShowConstantTbtResponse response)
	{
		if (mForwardListener != null)
			mForwardListener.onShowConstantTbtResponse(response);
	}

	@Override
	public void onAlertManeuverResponse(AlertManeuverResponse response)
	{
		if (mForwardListener != null)
			mForwardListener.onAlertManeuverResponse(response);
	}

	@Override
	public void onUpdateTurnListResponse(UpdateTurnListResponse response)
	{
		if (mForwardListener != null)
			mForwardListener.onUpdateTurnListResponse(response);
	}

	@Override
	public void onServiceDataACK(int i)
	{
		if (mForwardListener != null)
			mForwardListener.onServiceDataACK(i);
	}

	@Override
	public void onGetWayPointsResponse(GetWayPointsResponse getWayPointsResponse)
	{

	}

	@Override
	public void onSubscribeWayPointsResponse(SubscribeWayPointsResponse subscribeWayPointsResponse)
	{

	}

	@Override
	public void onUnsubscribeWayPointsResponse(UnsubscribeWayPointsResponse unsubscribeWayPointsResponse)
	{

	}

	@Override
	public void onOnWayPointChange(OnWayPointChange onWayPointChange)
	{

	}

	@Override
	public void onGetSystemCapabilityResponse(GetSystemCapabilityResponse getSystemCapabilityResponse)
	{

	}

	@Override
	public void onGetInteriorVehicleDataResponse(GetInteriorVehicleDataResponse getInteriorVehicleDataResponse)
	{

	}

	@Override
	public void onButtonPressResponse(ButtonPressResponse buttonPressResponse)
	{

	}

	@Override
	public void onSetInteriorVehicleDataResponse(SetInteriorVehicleDataResponse setInteriorVehicleDataResponse)
	{

	}

	@Override
	public void onOnInteriorVehicleData(OnInteriorVehicleData onInteriorVehicleData)
	{

	}

	@Override
	public void onSendHapticDataResponse(SendHapticDataResponse sendHapticDataResponse)
	{

	}


}

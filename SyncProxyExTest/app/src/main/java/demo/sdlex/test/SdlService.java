package demo.sdlex.test;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.smartdevicelink.exception.SdlException;
import com.smartdevicelink.proxy.LockScreenManager;
import com.smartdevicelink.proxy.callbacks.OnServiceEnded;
import com.smartdevicelink.proxy.callbacks.OnServiceNACKed;
import com.smartdevicelink.proxy.ex.IProxyListenerALMEx;
import com.smartdevicelink.proxy.ex.Operator.ShowOp;
import com.smartdevicelink.proxy.ex.RPCStructEx.ChoiceEx;
import com.smartdevicelink.proxy.ex.RPCStructEx.SoftButtonEx;
import com.smartdevicelink.proxy.ex.SdlProxyEx;
import com.smartdevicelink.proxy.rpc.AddCommandResponse;
import com.smartdevicelink.proxy.rpc.AddSubMenuResponse;
import com.smartdevicelink.proxy.rpc.AlertManeuverResponse;
import com.smartdevicelink.proxy.rpc.AlertResponse;
import com.smartdevicelink.proxy.rpc.ChangeRegistrationResponse;
import com.smartdevicelink.proxy.rpc.Choice;
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
import com.smartdevicelink.proxy.rpc.SendLocation;
import com.smartdevicelink.proxy.rpc.SendLocationResponse;
import com.smartdevicelink.proxy.rpc.SetAppIconResponse;
import com.smartdevicelink.proxy.rpc.SetDisplayLayoutResponse;
import com.smartdevicelink.proxy.rpc.SetGlobalPropertiesResponse;
import com.smartdevicelink.proxy.rpc.SetMediaClockTimer;
import com.smartdevicelink.proxy.rpc.SetMediaClockTimerResponse;
import com.smartdevicelink.proxy.rpc.ShowConstantTbtResponse;
import com.smartdevicelink.proxy.rpc.ShowResponse;
import com.smartdevicelink.proxy.rpc.SliderResponse;
import com.smartdevicelink.proxy.rpc.SoftButton;
import com.smartdevicelink.proxy.rpc.SpeakResponse;
import com.smartdevicelink.proxy.rpc.StartTime;
import com.smartdevicelink.proxy.rpc.StreamRPCResponse;
import com.smartdevicelink.proxy.rpc.SubscribeButtonResponse;
import com.smartdevicelink.proxy.rpc.SubscribeVehicleDataResponse;
import com.smartdevicelink.proxy.rpc.SubscribeWayPointsResponse;
import com.smartdevicelink.proxy.rpc.SystemRequestResponse;
import com.smartdevicelink.proxy.rpc.UnsubscribeButtonResponse;
import com.smartdevicelink.proxy.rpc.UnsubscribeVehicleDataResponse;
import com.smartdevicelink.proxy.rpc.UnsubscribeWayPointsResponse;
import com.smartdevicelink.proxy.rpc.UpdateTurnListResponse;
import com.smartdevicelink.proxy.rpc.enums.ButtonName;
import com.smartdevicelink.proxy.rpc.enums.FileType;
import com.smartdevicelink.proxy.rpc.enums.InteractionMode;
import com.smartdevicelink.proxy.rpc.enums.SdlDisconnectedReason;
import com.smartdevicelink.proxy.rpc.enums.SoftButtonType;
import com.smartdevicelink.proxy.rpc.enums.SystemAction;
import com.smartdevicelink.proxy.rpc.enums.UpdateMode;
import com.smartdevicelink.transport.BaseTransportConfig;
import com.smartdevicelink.transport.MultiplexTransportConfig;
import com.smartdevicelink.transport.TransportConstants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import static android.provider.UserDictionary.Words.APP_ID;

// InteractionChoiceSet Service
public class SdlService extends Service implements IProxyListenerALMEx
{
	public static final int ID_CMD_BEGIN = 1;
	public static final int ID_CMD_END = 101;

	private static final int ID_BTN_PHONE = 1001;
	private static final int ID_BTN_NAVIGATION = 1002;
	private static final int ID_BTN_NAVIGATION_DIRECT = 1003;
	private static final int CSID_PERFORM_INTERACTION = 1101;
	private static final int CSID_NAVIGATION = 1102;
	private static final int COID_PERFORM_INTERACTION = 1201;
	private static final int COID_NAVIGATION = 1202;
	private static final String PHONE_NO = "10086";

	private SdlProxyEx mProxy = null;
	public int mCmdCount = 0;
	private long mLastTime = 0;
	private int mCorrelationID = 0;
	private boolean mChoiceSetHasCreated = false;
	private boolean mNavChoiceSetHasCreated = false;



	// start applink if connect to ford vehicle
	public static void updateSDLService(final Context ctx)
	{
		BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
		int state = adapter.getProfileConnectionState(BluetoothProfile.A2DP);
		if (state == BluetoothProfile.STATE_CONNECTED)
		{
			adapter.getProfileProxy(ctx, new BluetoothProfile.ServiceListener() {
				@Override
				public void onServiceConnected(int i, BluetoothProfile bluetoothProfile)
				{
					List<BluetoothDevice> devices = bluetoothProfile.getConnectedDevices();
					for (BluetoothDevice one : devices)
					{
						if (SdlProxyEx.isSdlDevice(one.getName()))
						{
							// start service
							Intent intent = new Intent(ctx, SdlService.class);
							ctx.startService(intent);
							break;
						}

					}
				}

				@Override
				public void onServiceDisconnected(int i)
				{
					Intent intent = new Intent(ctx, SdlService.class);
					ctx.stopService(intent);
				}
			}, BluetoothProfile.A2DP);
		}
	}

	private void startProxy(boolean forceConnect, Intent intent)
	{
		if (mProxy == null)
		{
			try
			{
				//BaseTransportConfig btc = new BTTransportConfig();
				BaseTransportConfig btc = new MultiplexTransportConfig(this, APP_ID,
																	   MultiplexTransportConfig.FLAG_MULTI_SECURITY_OFF);

				mProxy = new SdlProxyEx(this, "SyncProxyTester", true, "584421907",
										this, R.drawable.ic_launcher, FileType.GRAPHIC_PNG, btc);
			}
			catch (SdlException e)
			{
				e.printStackTrace();
				if (mProxy == null)
					stopSelf();
			}
		}
		else if (forceConnect)
		{
			mProxy.forceOnConnected();
		}
	}


	private void disposeProxy()
	{
		if (mProxy != null)
		{
			try
			{
				mProxy.dispose();
				mProxy = null;
			}
			catch (SdlException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}


	public class DemoBinder extends Binder
	{
		public void MediaMusicDemo()
		{
			SdlService.this.mediaMusicDemo();
		}

		public void NonMediaMsgDemo(String layout)
		{
			SdlService.this.nonMediaMsgDemo(layout);
		}

		public void NonMeidaNaviDemo()
		{
			SdlService.this.nonMediaMsgNavi();
		}

		public void SetAppIcon()
		{
			SdlService.this.setAppIcon();

		}

		public void PerformInteraction()
		{
			SdlService.this.performInteraction();
		}

		public void DidaTexi()
		{
			SdlService.this.didaTexi();
		}

		public void DidaTexiCall()
		{
			SdlService.this.didaTexiCall();
		}

		public void DidaTexiNavi()
		{
			SdlService.this.didaTexiNavi();
		}
	}

	private void showBase()
	{
		try
		{
			mProxy.subscribeButton(ButtonName.OK, mCorrelationID++);
			mProxy.subscribeButton(ButtonName.SEEKLEFT, mCorrelationID++);
			mProxy.subscribeButton(ButtonName.SEEKRIGHT, mCorrelationID++);


			mProxy.show("第一行", "第二行", "第三行", "第四行", null, null, null, null, null, null, null, mCorrelationID++);
			mProxy.showImg(R.drawable.focus_rs, FileType.GRAPHIC_JPEG, ShowOp.ShowType.IMAGE_MAIN, mCorrelationID++);

			SoftButtonEx sb1 = new SoftButtonEx();
			sb1.setSoftButtonID(0);
			sb1.setText("本地");
			sb1.setType(SoftButtonType.SBT_BOTH);
			sb1.setSystemAction(SystemAction.DEFAULT_ACTION);
			sb1.setImage(R.drawable.local, this, FileType.GRAPHIC_PNG);

			SoftButtonEx sb2 = new SoftButtonEx();
			sb2.setSoftButtonID(1);
			sb2.setText("收藏");
			sb2.setType(SoftButtonType.SBT_BOTH);
			sb2.setSystemAction(SystemAction.DEFAULT_ACTION);
			sb2.setImage(R.drawable.favorite, this, FileType.GRAPHIC_PNG);

			SoftButtonEx sb3 = new SoftButtonEx();
			sb3.setSoftButtonID(2);
			sb3.setText("热门");
			sb3.setType(SoftButtonType.SBT_BOTH);
			sb3.setSystemAction(SystemAction.DEFAULT_ACTION);
			sb3.setImage(R.drawable.hot, this, FileType.GRAPHIC_PNG);

			SoftButtonEx sb4 = new SoftButtonEx();
			sb4.setSoftButtonID(3);
			sb4.setText("音乐");
			sb4.setType(SoftButtonType.SBT_BOTH);
			sb4.setSystemAction(SystemAction.DEFAULT_ACTION);
			sb4.setImage(R.drawable.music, this, FileType.GRAPHIC_PNG);

			SoftButtonEx sb5 = new SoftButtonEx();
			sb5.setSoftButtonID(4);
			sb5.setText("经典");
			sb5.setType(SoftButtonType.SBT_TEXT);
			sb5.setIsHighlighted(true);
			sb5.setSystemAction(SystemAction.DEFAULT_ACTION);
//			sb5.setImage(R.drawable.classic, this, FileType.GRAPHIC_PNG);


			SoftButton sb6 = new SoftButton();
			sb6.setSoftButtonID(5);
			sb6.setText("金属");
			sb6.setType(SoftButtonType.SBT_TEXT);
			sb6.setSystemAction(SystemAction.DEFAULT_ACTION);
//			sb6.setImage(R.drawable.metal, this, FileType.GRAPHIC_PNG);

			List<SoftButton> btns = new ArrayList<SoftButton>();
			btns.add(sb1);
			btns.add(sb2);
			btns.add(sb3);
			btns.add(sb4);
			btns.add(sb5);
			btns.add(sb6);

			mProxy.getSoftButtonCapabilities().clear();
			mProxy.showSoftButton(btns, mCorrelationID++);


			new Thread()
			{

				@Override
				public void run()
				{
					super.run();

					// ClockTimer
					StartTime start = new StartTime();
					start.setHours(0);
					start.setMinutes(0);
					start.setSeconds(0);

					StartTime end = new StartTime();
					end.setHours(1);
					end.setMinutes(20);
					end.setSeconds(30);

					SetMediaClockTimer smct = new SetMediaClockTimer();
					smct.setStartTime(start);
					smct.setEndTime(end);
					smct.setUpdateMode(UpdateMode.COUNTUP);
					smct.setCorrelationID(mCorrelationID++);

					try
					{
						Thread.sleep(1000);
						mProxy.sendRPCRequest(smct);

						Thread.sleep(1200);
						mProxy.pauseMediaClockTimer(mCorrelationID++);
					}
					catch (InterruptedException e)
					{
						e.printStackTrace();
					}
					catch (SdlException e)
					{
						e.printStackTrace();
					}
				}
			}.start();


		}
		catch(SdlException e)
		{
			e.printStackTrace();
		}
	}

	private void navigation()
	{
		if (!mNavChoiceSetHasCreated)
		{
			final int PAGE_SIZE = 4;
			final String CHOICE_NAME = "目的地";
			int choiceID = 0;

			Vector<Choice> vec = new Vector<Choice>();
			for (int j = 0; j < PAGE_SIZE; j++)
			{
				String name = String.format("%s%d", CHOICE_NAME, j + PAGE_SIZE + 1);
				Choice c = new Choice();
				c.setChoiceID(choiceID++);   //ID_CHOICE[j+i*PAGE_SIZE]
				c.setMenuName(name);
				c.setVrCommands(new Vector<String>(Arrays.asList(name)));
				vec.add(c);
			}


			try
			{
				mProxy.createInteractionChoiceSet(vec, CSID_NAVIGATION, COID_NAVIGATION);
			}
			catch (SdlException e)
			{
				e.printStackTrace();
			}
		}
		else
		{
			showNavPerformInteraction();
		}

	}

	private void navigationDirect()
	{
		SendLocation sendlocation = new SendLocation();
		sendlocation.setLocationDescription("陆家嘴环路东城路");
		sendlocation.setLocationName("陆家嘴");
		sendlocation.setPhoneNumber(PHONE_NO);
		//			sendlocation.setAddressLines(list);
		sendlocation.setLongitudeDegrees(121.5625);
		sendlocation.setLatitudeDegrees(31.2080);
		sendlocation.setCorrelationID(8000);
		try
		{
			mProxy.sendRPCRequest(sendlocation);
		}
		catch (SdlException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void showNavPerformInteraction()
	{
		final String initPrompt = "请说一个目的地";
		final String display = "请说出一个目的地";
		try
		{
			mProxy.performInteraction(initPrompt, display, CSID_NAVIGATION, null, null, InteractionMode.MANUAL_ONLY,
									  15000, COID_NAVIGATION);
		}
		catch (SdlException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}


	private void mediaMusicDemo()
	{
		try
		{
			showBase();
			mProxy.setdisplaylayout("MEDIA", mCorrelationID++);
		}
		catch(SdlException e)
		{
			e.printStackTrace();
		}
	}


	private void nonMediaMsgDemo(String layout)
	{
		try
		{
			showBase();
			mProxy.setdisplaylayout(layout, mCorrelationID++);
		}
		catch (SdlException e)
		{
			e.printStackTrace();
		}
	}

	private void nonMediaMsgNavi()
	{
		try
		{
//			mProxy.modifyCommandEx(100, 100, "hello100", 0, 1, null, R.drawable.ic_ford, FileType.GRAPHIC_PNG, mCorrelationID++);
//			mProxy.modifyCommandEx(200, 200, "hello200", 0, 2, null, R.drawable.navi_sim, FileType.GRAPHIC_PNG, mCorrelationID++);
//			mProxy.modifyCommandEx(300, 300, "hello300", 0, 3, null, R.drawable.ic_ford, FileType.GRAPHIC_PNG, mCorrelationID++);
//			mProxy.modifyCommandEx(400, 400, "hello400", 0, 4, null, R.drawable.navi_sim, FileType.GRAPHIC_PNG, mCorrelationID++);
//			mProxy.modifyCommandEx(500, 500, "hello500", 0, 5, null, mCorrelationID++);

			mProxy.setdisplaylayout("LARGE_GRAPHIC_ONLY", mCorrelationID++);
//			mProxy.setdisplaylayout("DOUBLE_GRAPHIC_WITH_SOFTBUTTONS", mCorrelationID++);
			mProxy.showImg(R.drawable.navi_sim, FileType.GRAPHIC_PNG, ShowOp.ShowType.IMAGE_MAIN, mCorrelationID++);
		}
		catch (SdlException e)
		{
			e.printStackTrace();
		}

	}

	private void setAppIcon()
	{
		try
		{
			mProxy.showAppIcon(R.drawable.icon, FileType.GRAPHIC_PNG, mCorrelationID++);

			mProxy.addCommandEx(100, "hello1", 0, 1, null, R.drawable.icon, FileType.GRAPHIC_PNG, mCorrelationID++);
			mProxy.addCommandEx(200, "hello2", 0, 2, null, R.drawable.focus_rs, FileType.GRAPHIC_JPEG, mCorrelationID++);
			mProxy.addCommandEx(300, "hello3", 0, 3, null, R.drawable.dida_lady, FileType.GRAPHIC_JPEG, mCorrelationID++);
			mProxy.addCommandEx(400, "hello4", 0, 4, null, R.drawable.icon, FileType.GRAPHIC_PNG,
								mCorrelationID++);
			mProxy.addCommandEx(500, "hello5", 0, 5, null, R.drawable.local, FileType.GRAPHIC_PNG,
								mCorrelationID++);

//			mProxy.addCommand(id, Vector<String>, correlationID);

		}
		catch (SdlException e)
		{
			e.printStackTrace();
		}
	}

	private void performInteraction()
	{
		if (!mChoiceSetHasCreated)
		{
			final String NON_IMG = "non-image";
			final String IMG_FORD = "ford";
			final String IMG_FOUCS_RS = "focus_rs";
			final String IMG_MUSIC = "music";

			Choice c1 = new Choice();
			c1.setChoiceID(1);
			c1.setMenuName(NON_IMG);
			c1.setVrCommands(new Vector<String>(Arrays.asList(NON_IMG)));

			ChoiceEx c2 = new ChoiceEx();
			c2.setChoiceID(2);
			c2.setMenuName(IMG_FORD);
			c2.setVrCommands(new Vector<String>(Arrays.asList(IMG_FORD)));
			c2.setImage(R.drawable.ic_ford, this, FileType.GRAPHIC_PNG);

			ChoiceEx c3 = new ChoiceEx();
			c3.setChoiceID(3);
			c3.setMenuName(IMG_FOUCS_RS);
			c3.setVrCommands(new Vector<String>(Arrays.asList(IMG_FOUCS_RS)));
			c3.setImage(R.drawable.focus_rs, this, FileType.GRAPHIC_JPEG);

			ChoiceEx c4 = new ChoiceEx();
			c4.setChoiceID(4);
			c4.setMenuName(IMG_MUSIC);
			c4.setVrCommands(new Vector<String>(Arrays.asList(IMG_MUSIC)));
			c4.setImage(R.drawable.music, this, FileType.GRAPHIC_PNG);

			Vector<Choice> choiceSet = new Vector<Choice>();
			choiceSet.add(c1);
			choiceSet.add(c2);
			choiceSet.add(c3);
			choiceSet.add(c4);

			try
			{
				mProxy.createInteractionChoiceSetEx(choiceSet, CSID_PERFORM_INTERACTION, COID_PERFORM_INTERACTION);
			}
			catch (SdlException e)
			{
				e.printStackTrace();
			}
		}
		else
		{
			showPerformInteraction();
		}
	}

	private void showPerformInteraction()
	{
		try
		{
			mProxy.performInteraction(null, "Perform Interaction", CSID_PERFORM_INTERACTION, null, null, InteractionMode.MANUAL_ONLY,
									  15000, COID_PERFORM_INTERACTION);
		}
		catch (SdlException e)
		{
			e.printStackTrace();
		}
	}


	private void didaTexi()
	{
		try
		{
			mProxy.show("起点：陆家嘴环路东城路(0.5KM)", "终点：南京西路石门一路(7.3KM)", "出发时间：18:00（±15'）", null, null, null, null, null,
						null, null, null, mCorrelationID++);
			mProxy.showImg(R.drawable.dida_lady, FileType.GRAPHIC_JPEG, ShowOp.ShowType.IMAGE_MAIN, mCorrelationID++);


			SoftButtonEx sb1 = new SoftButtonEx();
			sb1.setSoftButtonID(ID_BTN_PHONE);
			sb1.setText("电话");
			sb1.setType(SoftButtonType.SBT_BOTH);
			sb1.setSystemAction(SystemAction.DEFAULT_ACTION);
			sb1.setImage(R.drawable.phone, this, FileType.GRAPHIC_PNG);

			SoftButtonEx sb2 = new SoftButtonEx();
			sb2.setSoftButtonID(ID_BTN_NAVIGATION);
			sb2.setText("导航");
			sb2.setType(SoftButtonType.SBT_BOTH);
			sb2.setSystemAction(SystemAction.DEFAULT_ACTION);
			sb2.setImage(R.drawable.navigation, this, FileType.GRAPHIC_PNG);

			List<SoftButton> btns = new ArrayList<SoftButton>();
			btns.add(sb1);
			btns.add(sb2);

			mProxy.getSoftButtonCapabilities().clear();
			mProxy.showSoftButton(btns, mCorrelationID++);
			mProxy.setdisplaylayout("TEXT_AND_SOFTBUTTONS_WITH_GRAPHIC", mCorrelationID++);

		}
		catch(SdlException e)
		{
			e.printStackTrace();
		}
	}

	private void didaTexiCall()
	{
		SoftButton sb1 = new SoftButton();
		sb1.setSoftButtonID(ID_BTN_PHONE);
		sb1.setText("是");
		sb1.setType(SoftButtonType.SBT_TEXT);
		sb1.setSystemAction(SystemAction.DEFAULT_ACTION);
		sb1.setIsHighlighted(true);

		SoftButton sb2 = new SoftButton();
		sb2.setSoftButtonID(1);
		sb2.setText("否");
		sb2.setType(SoftButtonType.SBT_TEXT);
		sb2.setSystemAction(SystemAction.DEFAULT_ACTION);

		Vector<SoftButton> softButtons = new Vector<SoftButton>();
		softButtons.add(sb1);
		softButtons.add(sb2);

		try
		{
			mProxy.alert("当前已接近目的地，是否拨打王小姐的电话？", "接近目的地", "是否拨打王小姐电话？",
						 PHONE_NO, true, 10000, softButtons, 0);
		}
		catch (SdlException e)
		{
			e.printStackTrace();
		}
	}

	private void didaTexiNavi()
	{
		SoftButton sb1 = new SoftButton();
		sb1.setSoftButtonID(ID_BTN_NAVIGATION_DIRECT);
		sb1.setText("是");
		sb1.setType(SoftButtonType.SBT_TEXT);
		sb1.setSystemAction(SystemAction.DEFAULT_ACTION);
		sb1.setIsHighlighted(true);

		SoftButton sb2 = new SoftButton();
		sb2.setSoftButtonID(1);
		sb2.setText("否");
		sb2.setType(SoftButtonType.SBT_TEXT);
		sb2.setSystemAction(SystemAction.DEFAULT_ACTION);

		Vector<SoftButton> softButtons = new Vector<SoftButton>();
		softButtons.add(sb1);
		softButtons.add(sb2);

		try
		{
			mProxy.alert("乘客王小姐将在陆家嘴环路东城路上车，是否开启导航？", "王小姐", "上车地点：陆家嘴环路东城路",
						 "是否开启导航？", true, 10000, softButtons, 0);
		}
		catch (SdlException e)
		{
			e.printStackTrace();
		}
	}

	public SdlService()
	{
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		boolean forced = intent !=null && intent.getBooleanExtra(TransportConstants.FORCE_TRANSPORT_CONNECTED, false);
		startProxy(forced, intent);

		return START_STICKY;
	}

	@Override
	public void onCreate()
	{
		super.onCreate();
	}

	@Override
	public void onDestroy()
	{
		disposeProxy();
		super.onDestroy();
	}


	@Override
	public IBinder onBind(Intent intent) {
		return new DemoBinder();
	}

	@Override
	public void onSDLResume()
	{

	}

	@Override
	public void onOnHMIStatus(OnHMIStatus status)
	{
		switch (status.getSystemContext())
		{
		case SYSCTXT_MAIN:
			break;
		case SYSCTXT_VRSESSION:
			break;
		case SYSCTXT_MENU:
			break;
		default:
			return;
		}

		switch (status.getAudioStreamingState())
		{
		case AUDIBLE:
			// play audio if applicable
			Log.d("FordAppLinkService", "AUDIBLE");
			break;
		case NOT_AUDIBLE:
			// pause/stop/mute audio if applicable
			Log.d("FordAppLinkService", "NOT AUDIBLE");
			break;
		default:
			return;
		}

		switch (status.getHmiLevel())
		{
		case HMI_FULL:
			if (status.getFirstRun())
			{


			}
			break;
		case HMI_LIMITED:
			break;
		case HMI_BACKGROUND:
			break;
		case HMI_NONE:
			break;
		default:
			return;
		}
	}

	@Override
	public void onProxyClosed(String s, Exception e, SdlDisconnectedReason sdlDisconnectedReason)
	{
		int n = 0;
		n++;
	}

	@Override
	public void onServiceEnded(OnServiceEnded onServiceEnded)
	{

	}

	@Override
	public void onServiceNACKed(OnServiceNACKed onServiceNACKed)
	{

	}

	@Override
	public void onOnStreamRPC(OnStreamRPC onStreamRPC)
	{

	}

	@Override
	public void onStreamRPCResponse(StreamRPCResponse streamRPCResponse)
	{

	}

	@Override
	public void onError(String s, Exception e)
	{

	}

	@Override
	public void onGenericResponse(GenericResponse genericResponse)
	{

	}

	@Override
	public void onOnCommand(OnCommand onCommand)
	{
		try
		{
			mProxy.alert("Alert", "第"+onCommand.getCmdID()+"个", true,5000,0);
		}
		catch (SdlException e)
		{
			e.printStackTrace();
		}
	}


	@Override
	public void onAddCommandResponse(AddCommandResponse response)
	{
		if (++mCmdCount >= ID_CMD_END - ID_CMD_BEGIN)
		{
			long cost = System.currentTimeMillis() - mLastTime;
			try
			{
				mProxy.alert("Alert", "耗时：" + cost/1000.0 + "秒", true, 10000,0);
			}
			catch (SdlException e)
			{
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onAddSubMenuResponse(AddSubMenuResponse addSubMenuResponse)
	{

	}

	@Override
	public void onCreateInteractionChoiceSetResponse(
			CreateInteractionChoiceSetResponse response)
	{
			if (response.getSuccess())
			{
				switch (response.getCorrelationID())
				{
				case COID_PERFORM_INTERACTION:
					mChoiceSetHasCreated = true;
					showPerformInteraction();
					break;
				case COID_NAVIGATION:
					mNavChoiceSetHasCreated = true;
					showNavPerformInteraction();
					break;
				default:
					break;
				}
			}


	}

	@Override
	public void onAlertResponse(AlertResponse alertResponse)
	{

	}

	@Override
	public void onDeleteCommandResponse(DeleteCommandResponse deleteCommandResponse)
	{
	}

	@Override
	public void onDeleteInteractionChoiceSetResponse(
			DeleteInteractionChoiceSetResponse deleteInteractionChoiceSetResponse)
	{

	}

	@Override
	public void onDeleteSubMenuResponse(DeleteSubMenuResponse deleteSubMenuResponse)
	{

	}

	@Override
	public void onPerformInteractionResponse(PerformInteractionResponse response)
	{
		String result = response.getResultCode().toString();
		String info = response.getInfo();
		switch (response.getCorrelationID())
		{
		case COID_NAVIGATION:
			if (response.getSuccess())
				navigationDirect();
			break;
		default:
			break;
		}

	}

	@Override
	public void onResetGlobalPropertiesResponse(ResetGlobalPropertiesResponse resetGlobalPropertiesResponse)
	{

	}

	@Override
	public void onSetGlobalPropertiesResponse(SetGlobalPropertiesResponse setGlobalPropertiesResponse)
	{

	}




	@Override
	public void onSetMediaClockTimerResponse(SetMediaClockTimerResponse response)
	{
		Log.d("test", "response: " + response.getSuccess() + ", resultCode:" + response.getResultCode().toString());
	}

	@Override
	public void onShowResponse(ShowResponse showResponse)
	{
	}

	@Override
	public void onSpeakResponse(SpeakResponse speakResponse)
	{

	}

	@Override
	public void onOnButtonEvent(OnButtonEvent onButtonEvent)
	{

	}

	@Override
	public void onOnButtonPress(OnButtonPress notification)
	{
		switch (notification.getButtonName())
		{
		case CUSTOM_BUTTON:
			switch (notification.getCustomButtonName())
			{
			case ID_BTN_PHONE:
				Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + PHONE_NO));
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
				break;
			case ID_BTN_NAVIGATION:
				navigation();
				break;
			case ID_BTN_NAVIGATION_DIRECT:
				navigationDirect();
				break;
			default:
				break;
			}
			break;
		case OK:
			try
			{
				mProxy.alert("", "empty string", "", false, 10000, null, 1000);
			}
			catch (SdlException e)
			{
				e.printStackTrace();
			}
			break;
		case SEEKLEFT:
			try
			{
				mProxy.alert(null, "null string", null, false, 10000, null, 1000);
			}
			catch (SdlException e)
			{
				e.printStackTrace();
			}
			break;
		case SEEKRIGHT:
			break;
		default:
			break;
		}
	}

	@Override
	public void onSubscribeButtonResponse(SubscribeButtonResponse notification)
	{

	}

	@Override
	public void onUnsubscribeButtonResponse(UnsubscribeButtonResponse unsubscribeButtonResponse)
	{

	}

	@Override
	public void onOnPermissionsChange(OnPermissionsChange onPermissionsChange)
	{

	}

	@Override
	public void onSubscribeVehicleDataResponse(SubscribeVehicleDataResponse subscribeVehicleDataResponse)
	{

	}

	@Override
	public void onUnsubscribeVehicleDataResponse(UnsubscribeVehicleDataResponse unsubscribeVehicleDataResponse)
	{

	}

	@Override
	public void onGetVehicleDataResponse(GetVehicleDataResponse response)
	{


	}

	@Override
	public void onOnVehicleData(OnVehicleData onVehicleData)
	{

	}

	@Override
	public void onPerformAudioPassThruResponse(PerformAudioPassThruResponse response)
	{



	}

	@Override
	public void onEndAudioPassThruResponse(EndAudioPassThruResponse endAudioPassThruResponse)
	{

	}

	@Override
	public void onOnAudioPassThru(OnAudioPassThru apt)
	{
		//byte[] data = apt.getAPTData();

	}

	@Override
	public void onPutFileResponse(PutFileResponse putFileResponse)
	{
		//putFileResponse.getSpaceAvailable();
	}

	@Override
	public void onDeleteFileResponse(DeleteFileResponse deleteFileResponse)
	{

	}

	@Override
	public void onListFilesResponse(ListFilesResponse listFilesResponse)
	{

	}

	@Override
	public void onSetAppIconResponse(SetAppIconResponse setAppIconResponse)
	{

	}

	@Override
	public void onScrollableMessageResponse(ScrollableMessageResponse scrollableMessageResponse)
	{

	}

	@Override
	public void onChangeRegistrationResponse(ChangeRegistrationResponse changeRegistrationResponse)
	{

	}

	@Override
	public void onSetDisplayLayoutResponse(SetDisplayLayoutResponse setDisplayLayoutResponse)
	{

	}

	@Override
	public void onOnLanguageChange(OnLanguageChange onLanguageChange)
	{

	}

	@Override
	public void onOnHashChange(OnHashChange onHashChange)
	{

	}

	@Override
	public void onSliderResponse(SliderResponse sliderResponse)
	{

	}

	@Override
	public void onOnDriverDistraction(OnDriverDistraction onDriverDistraction)
	{

	}

	@Override
	public void onOnTBTClientState(OnTBTClientState onTBTClientState)
	{

	}

	@Override
	public void onOnSystemRequest(OnSystemRequest onSystemRequest)
	{

	}

	@Override
	public void onSystemRequestResponse(SystemRequestResponse systemRequestResponse)
	{

	}

	@Override
	public void onOnKeyboardInput(OnKeyboardInput onKeyboardInput)
	{

	}

	@Override
	public void onOnTouchEvent(OnTouchEvent onTouchEvent)
	{

	}

	@Override
	public void onDiagnosticMessageResponse(DiagnosticMessageResponse diagnosticMessageResponse)
	{

	}

	@Override
	public void onReadDIDResponse(ReadDIDResponse readDIDResponse)
	{

	}

	@Override
	public void onGetDTCsResponse(GetDTCsResponse getDTCsResponse)
	{

	}

	@Override
	public void onOnLockScreenNotification(OnLockScreenStatus onLockScreenStatus)
	{
		Log.d("ttt", "DD:" + onLockScreenStatus.getDriverDistractionStatus());
		Log.d("ttt", "HMI Level:" + onLockScreenStatus.getHMILevel());
		Log.d("ttt", "Show Lock Screen:" + onLockScreenStatus.getShowLockScreen());
		Log.d("ttt", "Get User Selected:" + onLockScreenStatus.getUserSelected());
		Log.d("ttt", "=============================================");
	}

	@Override
	public void onDialNumberResponse(DialNumberResponse dialNumberResponse)
	{

	}

	@Override
	public void onSendLocationResponse(SendLocationResponse sendLocationResponse)
	{

	}

	@Override
	public void onShowConstantTbtResponse(ShowConstantTbtResponse showConstantTbtResponse)
	{

	}

	@Override
	public void onAlertManeuverResponse(AlertManeuverResponse alertManeuverResponse)
	{

	}

	@Override
	public void onUpdateTurnListResponse(UpdateTurnListResponse updateTurnListResponse)
	{

	}

	@Override
	public void onServiceDataACK(int i)
	{

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


}

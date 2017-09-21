package demo.sdlex.test;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;


public class MainActivity extends Activity implements View.OnClickListener, DialogInterface.OnClickListener
{
	private Button mBtnMediaMusic = null;
	private Button mBtnNonMediaMsg = null;
	private Button mBtnNonMediaNavi = null;
	private Button mBtnAppIcon = null;
	private Button mBtnDidaTexi = null;
	private Button mBtnPerformInteraction = null;
	private SdlService.DemoBinder mDemoBinder = null;

	private AlertDialog.Builder mNonMediaDlgBuilder = null;
	private AlertDialog.Builder mDidaDlgBuilder = null;
	private AlertDialog mDlgNonMedia = null;
	private AlertDialog mDlgDida = null;

	private final static String[] DISPLAY_LAYOUT_MENU =
			{
					"NON_MEDIA",
					"TILES_WITH_GRAPHIC",
					"TEXT_AND_SOFTBUTTONS_WITH_GRAPHIC",
					"TEXTBUTTONS_WITH_GRAPHIC",
			};

	private final static String[] DIDA_MENU =
			{
					"模拟接单，导航",
					"模拟接近目的地时，打电话",
			};

	private ServiceConnection mSrvConn = new ServiceConnection()
	{

		@Override
		public void onServiceConnected(ComponentName name, IBinder service)
		{
			mDemoBinder = (SdlService.DemoBinder)service;
		}

		@Override
		public void onServiceDisconnected(ComponentName name)
		{
			mDemoBinder = null;
		}
	};



	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);


		mBtnMediaMusic = (Button)findViewById(R.id.btn_media_music);
		mBtnNonMediaMsg = (Button)findViewById(R.id.btn_non_media_msg);
		mBtnNonMediaNavi = (Button)findViewById(R.id.btn_non_media_navi);
		mBtnAppIcon = (Button)findViewById(R.id.btn_app_icon);
		mBtnDidaTexi = (Button)findViewById(R.id.btn_dida_texi);
		mBtnPerformInteraction = (Button)findViewById(R.id.btn_perform_interaction);

		mBtnMediaMusic.setOnClickListener(this);
		mBtnNonMediaMsg.setOnClickListener(this);
		mBtnNonMediaNavi.setOnClickListener(this);
		mBtnAppIcon.setOnClickListener(this);
		mBtnDidaTexi.setOnClickListener(this);
		mBtnPerformInteraction.setOnClickListener(this);

		mNonMediaDlgBuilder = new AlertDialog.Builder(this);
		mNonMediaDlgBuilder.setTitle("请选择Layout");
		mNonMediaDlgBuilder.setItems(DISPLAY_LAYOUT_MENU, this);
		mNonMediaDlgBuilder.setNegativeButton("取消", null);

		mDidaDlgBuilder = new AlertDialog.Builder(this);
		mDidaDlgBuilder.setTitle("请选择要模拟的内容");
		mDidaDlgBuilder.setItems(DIDA_MENU, this);
		mDidaDlgBuilder.setNegativeButton("取消", null);

		MySdlBroadcastReceiver.queryForConnectedService(this);

		Intent intent = new Intent(this, SdlService.class);
		bindService(intent, mSrvConn, BIND_AUTO_CREATE);

//		SyncProxyService.updateSDLService(this);
	}

	@Override
	protected void onDestroy()
	{
		// TODO Auto-generated method stub
		unbindService(mSrvConn);

		super.onDestroy();

	}

	@Override
	public void onClick(View view)
	{

		switch (view.getId())
		{
		case R.id.btn_media_music:
			if (mDemoBinder != null)
			{
				mDemoBinder.MediaMusicDemo();
			}
			break;
		case R.id.btn_non_media_msg:
			mDlgNonMedia = mNonMediaDlgBuilder.show();
			break;
		case R.id.btn_non_media_navi:
			if (mDemoBinder != null)
			{
				mDemoBinder.NonMeidaNaviDemo();
			}
			break;
		case R.id.btn_app_icon:
			if (mDemoBinder != null)
			{
				mDemoBinder.SetAppIcon();
			}
			break;
		case R.id.btn_perform_interaction:
			if (mDemoBinder != null)
			{
				mDemoBinder.PerformInteraction();
			}
			break;
		case R.id.btn_dida_texi:
			if (mDemoBinder != null)
			{
				mDemoBinder.DidaTexi();
				mDlgDida = mDidaDlgBuilder.show();
			}
		default:
			break;
		}
	}

	@Override
	public void onClick(DialogInterface dlg, int i)
	{
		if (mDemoBinder != null)
		{
			if (dlg == mDlgNonMedia)
			{
				mDemoBinder.NonMediaMsgDemo(DISPLAY_LAYOUT_MENU[i]);
			}
			else if (dlg == mDlgDida)
			{
				switch(i)
				{
				case 0:
					mDemoBinder.DidaTexiNavi();
					break;
				case 1:
					mDemoBinder.DidaTexiCall();
					break;
				default:
					break;
				}
			}
		}
	}
}


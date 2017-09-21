package demo.sdlex.test;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;

import com.smartdevicelink.transport.SdlBroadcastReceiver;
import com.smartdevicelink.transport.SdlRouterService;

/**
 * Created by leon on 2017/5/22.
 */

public class MySdlBroadcastReceiver extends SdlBroadcastReceiver
{
	@Override
	public void onSdlEnabled(Context context, Intent intent)
	{
		intent.setClass(context, SdlService.class);
		context.startService(intent);
	}


	@Override
	public Class<? extends SdlRouterService> defineLocalSdlRouterClass()
	{
		return demo.sdlex.test.MySdlRouterService.class;
	}


	@Override
	public void onReceive(Context context, Intent intent)
	{
		super.onReceive(context, intent);

		// TODO: Your code here
		// final BluetoothDevice bluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
		if (intent.getAction() != null)
		{
			if (intent.getAction().compareTo(BluetoothDevice.ACTION_ACL_CONNECTED) == 0)
			{
				Intent i = new Intent(context, SdlService.class);
				context.startService(i);
			}
			else if (intent.getAction().equals(BluetoothAdapter.ACTION_STATE_CHANGED))
			{
				if ((intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1) == (BluetoothAdapter.STATE_TURNING_OFF)))
				{
					Intent i = new Intent(context, SdlService.class);
					context.stopService(i);
				}
			}

		}

	}

}

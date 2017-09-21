package lib.smartdevicelink.com.layout;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

/**
 * Created by leon on 16/10/8.
 */
public class SDLLayoutWeather
{
	// INNER CLASS
	public enum WeatherType
	{
		UNKNOWN,		//未知
		SUNNY,			//晴天
		CLOUDY,			//多云
		OVERCAST,		//阴
		DRIZZLE,		//小雨
		RAIN,			//中雨
		DOWNPOUR,		//大雨
		RAIN_STORM,		//暴雨
		THUNDER_STORM,	//雷雨
		SHOWER,			//阵雨
		LIGHT_SNOW,		//小雪
		SNOW,			//中雪
		HEAVY_SNOW,		//大雪
		BLIZZARD,		//暴风雪
		FOG,			//雾
		HAZE,			//霾/薄雾
		FROST,			//霜冻
		SLEET,			//雨夹雪
		TYPHOON,		//台风
		SANDSTORM,		//沙尘暴
		BREEZY,			//微风
	}

	public static class WeatherInfo
	{
		public WeatherType type;
//		public String condition;
		public String humidity;
		public String temperature;
		public String realFeel;
		public String pressure;
		public String tips;
		public String windDir;
		public String windLevel;
	}

	// SINGLETON
	private static SDLLayoutWeather mInstance = null;
	public static SDLLayoutWeather getInstance(Context ctx)
	{
		if (mInstance == null)
		{
			mInstance = new SDLLayoutWeather();
			mInstance.init(ctx);
		}
		return mInstance;
	}

	protected SDLLayoutWeather()
	{

	}


	// MEMBER
	private Bitmap mBarBMP;
	private Context mCtx;
	private Paint mTempPaint;
	private Paint mWeatherPaint;
	private HashMap<WeatherType, String> mWeatherNameMap;
	private HashMap<WeatherType, Bitmap> mWeatherLogoMap;
	private WeatherInfo mLastWeatherInfo = null;
	private int mLastInnerAQI = 0;
	private int mLastOuterAQI = 0;



	private void init(Context ctx)
	{
		mCtx = ctx;

		mTempPaint = new Paint();
		mTempPaint.setTextAlign(Paint.Align.LEFT);
		mTempPaint.setColor(Color.LTGRAY);
		mTempPaint.setTextSize(30f);

		mWeatherPaint = new Paint();
		mWeatherPaint.setTextAlign(Paint.Align.LEFT);
		mWeatherPaint.setColor(Color.DKGRAY);
		mWeatherPaint.setTextSize(24f);

		mWeatherLogoMap = new HashMap<>();

		mWeatherNameMap = new HashMap<>();
		mWeatherNameMap.put(WeatherType.UNKNOWN, "");
		mWeatherNameMap.put(WeatherType.SUNNY, "晴天");
		mWeatherNameMap.put(WeatherType.CLOUDY, "多云");
		mWeatherNameMap.put(WeatherType.OVERCAST, "阴");
		mWeatherNameMap.put(WeatherType.DRIZZLE, "小雨");
		mWeatherNameMap.put(WeatherType.RAIN, "中雨");
		mWeatherNameMap.put(WeatherType.DOWNPOUR, "大雨");
		mWeatherNameMap.put(WeatherType.RAIN_STORM, "暴雨");
		mWeatherNameMap.put(WeatherType.THUNDER_STORM, "雷雨");
		mWeatherNameMap.put(WeatherType.SHOWER, "阵雨");
		mWeatherNameMap.put(WeatherType.LIGHT_SNOW, "小雪");
		mWeatherNameMap.put(WeatherType.SNOW, "中雪");
		mWeatherNameMap.put(WeatherType.HEAVY_SNOW, "大雪");
		mWeatherNameMap.put(WeatherType.BLIZZARD, "暴风雪");
		mWeatherNameMap.put(WeatherType.FOG, "雾");
		mWeatherNameMap.put(WeatherType.HAZE, "霾");
		mWeatherNameMap.put(WeatherType.FROST, "霜冻");
		mWeatherNameMap.put(WeatherType.SLEET, "雨夹雪");
		mWeatherNameMap.put(WeatherType.TYPHOON, "台风");
		mWeatherNameMap.put(WeatherType.SANDSTORM, "沙尘暴");
		mWeatherNameMap.put(WeatherType.BREEZY, "微风");
	}

	private byte[] read(int id) throws IOException
	{
		Resources res = mCtx.getResources();
		InputStream ios = res.openRawResource(id);
		int count = 0;
		while (ios.read() != -1)
		{
			++count;
		}

		ios.reset();
		byte[] buffer = new byte[count];

		try
		{
			if (ios.read(buffer) == -1)
			{
				throw new IOException("EOF reached while trying to read the whole file");
			}
		}
		finally
		{
			try
			{
				if (ios != null)
					ios.close();
			}
			catch (IOException e)
			{
				Log.e("read", e.getMessage());
			}
		}

		return buffer;
	}

	private Bitmap loadBMP(int res)
	{
		byte[] data = null;
		try
		{
			data = read(res);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return BitmapFactory.decodeByteArray(data, 0, data.length);
	}

	private InputStream getAQILayoutImage()
	{
		if (mLastInnerAQI < 0)
			mLastInnerAQI = 0;

		if (mLastOuterAQI < 0)
			mLastOuterAQI = 0;

		final int layoutWidth = 770;
		final int layoutHeight = 250;
		final int barWidth = mBarBMP.getWidth();
		final int barHeight = mBarBMP.getHeight();
		final int barX = layoutWidth - barWidth;			//Bar right align
		final int barY = layoutHeight - barHeight - 20;
		final int innerX = Float.valueOf((Math.min(mLastInnerAQI, 500f) / 500f * barWidth)).intValue() + barX;
		final int outerX = Float.valueOf((Math.min(mLastOuterAQI, 500f) / 500f * barWidth)).intValue() + barX;
		final int innerColor = mBarBMP.getPixel(innerX, 0);
		final int outerColor = mBarBMP.getPixel(outerX, 0);
		final int innerMarkY = barY - 5;
		final int outerMarkY = barY + barHeight + 15;

		final Paint innerPaint = new Paint();
		innerPaint.setTextAlign(Paint.Align.LEFT);
		innerPaint.setColor(innerColor);
		innerPaint.setTextSize(18f);

		final Paint outerPaint = new Paint();
		outerPaint.setTextAlign(Paint.Align.LEFT);
		outerPaint.setColor(outerColor);
		outerPaint.setTextSize(18f);

		Bitmap bmp = Bitmap.createBitmap(layoutWidth, layoutHeight, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bmp);
		canvas.drawBitmap(mBarBMP, barX, barY, null);
		canvas.drawText("车内空气", 0, innerMarkY, innerPaint);
		canvas.drawText("车外空气", 0, outerMarkY, outerPaint);

		if (mLastInnerAQI < 400)
			canvas.drawText("| " + Integer.toString(mLastInnerAQI), innerX, innerMarkY, innerPaint);
		else
			canvas.drawText(Integer.toString(mLastInnerAQI) + " |", innerX, innerMarkY, innerPaint);

		if (mLastOuterAQI < 400)
			canvas.drawText("| " + Integer.toString(mLastOuterAQI), outerX, outerMarkY, outerPaint);
		else
			canvas.drawText(Integer.toString(mLastOuterAQI) + " |", outerX, outerMarkY, outerPaint);

		// weather info
		if (mLastWeatherInfo != null)
		{
			if (mWeatherLogoMap.keySet().contains(mLastWeatherInfo.type))
				canvas.drawBitmap(mWeatherLogoMap.get(mLastWeatherInfo.type), 0f, 0f, null);

			canvas.drawText(mLastWeatherInfo.temperature + "℃", 35, 145, mTempPaint);
			canvas.drawText(mWeatherNameMap.get(mLastWeatherInfo.type), 140, 30, mTempPaint);
			canvas.drawText(mLastWeatherInfo.tips, 400, 30, mWeatherPaint);
			canvas.drawText("湿度：" + mLastWeatherInfo.humidity + "%", 400, 60, mWeatherPaint);
			canvas.drawText("体感温度：" + mLastWeatherInfo.realFeel + "℃", 400, 90, mWeatherPaint);
			canvas.drawText("气压：" + mLastWeatherInfo.pressure + " bar", 400, 120, mWeatherPaint);
			canvas.drawText("风向：" + mLastWeatherInfo.windDir + "，风力：" + mLastWeatherInfo.windLevel + "级", 400, 150,
							mWeatherPaint);
		}

		// convert to inputstream
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bmp.compress(Bitmap.CompressFormat.PNG, 100, baos);
		return new ByteArrayInputStream(baos.toByteArray());
	}


	// INTERFACE
	public void setPaint(Paint tempPaint, Paint weatherPaint)
	{
		mTempPaint = tempPaint;
		mWeatherPaint = weatherPaint;
	}

	public void setWeaterLogo(WeatherType type, int resID)
	{
		if (mWeatherLogoMap.keySet().contains(type))
			mWeatherLogoMap.remove(type);
		mWeatherLogoMap.put(type, loadBMP(resID));
	}

	public void setAQIBar(int resID)
	{
		mBarBMP = loadBMP(resID);
	}

	public InputStream updateInnerAQI(int aqi)
	{
		mLastInnerAQI = aqi;
		return getAQILayoutImage();
	}

	public InputStream updateOuterAQI(int aqi)
	{
		mLastOuterAQI = aqi;
		return getAQILayoutImage();
	}

	public InputStream updateAQI(int innerAQI, int outerAQI)
	{
		mLastInnerAQI = innerAQI;
		mLastOuterAQI = outerAQI;

		return getAQILayoutImage();
	}

	public InputStream updateWeatherInfo(WeatherInfo info)
	{
		mLastWeatherInfo = info;
		return getAQILayoutImage();
	}
}



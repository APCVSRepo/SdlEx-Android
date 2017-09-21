package lib.smartdevicelink.com.layout;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

/**
 * Created by leon on 2016/11/11.
 */
public class SDLLayoutDida
{
	// SINGLETON
	private static SDLLayoutDida mInstance = null;
	public static SDLLayoutDida getInstance(Context ctx)
	{
		if (mInstance == null)
		{
			mInstance = new SDLLayoutDida();
			mInstance.init(ctx);
		}
		return mInstance;
	}

	protected SDLLayoutDida()
	{

	}

	// MEMBER
	private static final int LAYOUT_WIDTH = 770;
	private static final int LAYOUT_HEIGHT = 210;
	private static final int IMG_WIDTH = LAYOUT_HEIGHT;
	private static final int IMG_HEIGHT = LAYOUT_HEIGHT;
	private static final int LINE_COUNT = 4;
	private static final float LINE_FONT_SIZE = 34f;

	private Paint[] mLinePaint = new Paint[LINE_COUNT];
	private String[] mLine = new String[LINE_COUNT];
	private int[] mLineHeight = new int[LINE_COUNT];
	private int mLineSpace = 0;
	private Bitmap mBmp;
	private Context mCtx;


	private void init(Context ctx)
	{
		mCtx = ctx;

		for (int i=0; i<LINE_COUNT; i++)
		{
			mLinePaint[i] = new Paint();
			mLinePaint[i].setTextAlign(Paint.Align.LEFT);
			mLinePaint[i].setColor(Color.DKGRAY);
			mLinePaint[i].setTextSize(LINE_FONT_SIZE);
			mLinePaint[i].setAntiAlias(true);
		}
	}

	private int getLineSpace()
	{
		float heightSum = 0f;
		for (int i=0; i<LINE_COUNT; i++)
		{
			Paint.FontMetrics fontMetrics = mLinePaint[i].getFontMetrics();
			mLineHeight[i] = (int)(fontMetrics.bottom - fontMetrics.top);
			heightSum += mLineHeight[i];
		}

		return (int)((LAYOUT_HEIGHT - heightSum) / mLinePaint.length);
	}

	private static Bitmap resizeImg(Bitmap bmp, int destWidth, int destHeight)
	{
		float scaleWidth = ((float) destWidth) / bmp.getWidth();
		float scaleHeight = ((float) destHeight) / bmp.getHeight();
		Matrix mx = new Matrix();

		float scale = scaleWidth < scaleHeight ? scaleWidth : scaleHeight;
		mx.postScale(scale, scale);

		return Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), mx, true);
	}

	private InputStream getImage()
	{
		final int imgX = LAYOUT_WIDTH - LAYOUT_HEIGHT;
		final int imgY = 0;
		final int lineSpace = getLineSpace();
		int txtX = 0;
		int txtY = 0;

		Bitmap bmp = Bitmap.createBitmap(LAYOUT_WIDTH, LAYOUT_HEIGHT, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bmp);

		if (mBmp != null)
		{
			Bitmap bmpInner = resizeImg(mBmp, IMG_WIDTH, IMG_HEIGHT);
			final int bmpInnerWidth = bmpInner.getWidth();
			final int bmpInnerHeight = bmpInner.getHeight();
			canvas.drawBitmap(bmpInner,
							  bmpInnerWidth < IMG_WIDTH ? imgX + (IMG_WIDTH - bmpInnerWidth) / 2 : imgX,
							  bmpInnerHeight < IMG_HEIGHT ? imgY + (IMG_HEIGHT - bmpInnerHeight) / 2 : imgY,
							  null);
		}


		for (int i=0; i<LINE_COUNT; i++)
		{
			txtY += mLineHeight[i];
			canvas.drawText(mLine[i]==null ? "" : mLine[i], txtX, txtY, mLinePaint[i]);
			txtY += lineSpace;

//			canvas.drawText(mLine1, txtX, txtY += fontHeight, mLinePaint);
//			canvas.drawText(mLine2, txtX, txtY += fontHeight + interval, mLinePaint);
//			canvas.drawText(mLine3, txtX, txtY += fontHeight + interval, mLinePaint);
//			canvas.drawText(mLine4, txtX, txtY += fontHeight + interval, mLinePaint);
		}


		// convert to inputstream
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bmp.compress(Bitmap.CompressFormat.PNG, 100, baos);
		return new ByteArrayInputStream(baos.toByteArray());
	}


	// INTERFACE
	public InputStream updateLayout(String line1, String line2, String line3, String line4, Bitmap bmp)
	{
		mLine[0] = line1;
		mLine[1] = line2;
		mLine[2] = line3;
		mLine[3] = line4;
		mBmp = bmp;

		return getImage();
	}

	// i start from 0;
	public void setLinePaint(int i, Paint paint)
	{
		if (i >= 0 && i < LINE_COUNT && paint != null)
		{
			mLinePaint[i] = paint;
		}
	}

	// i start from 0;
	public InputStream updateLayoutLine(int i, String txt)
	{
		if (i >= 0 && i < LINE_COUNT)
		{
			mLine[i] = txt;
			return getImage();
		}
		return null;
	}

	public InputStream updateLayoutImage(Bitmap bmp)
	{
		mBmp = bmp;
		return getImage();
	}



}

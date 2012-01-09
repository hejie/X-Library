package x.ui;

import java.util.Timer;
import java.util.TimerTask;

import x.lib.AsyncHttpClient;
import x.lib.AsyncHttpResponse;
import x.lib.Debug;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.ImageView;

/**
 * @brief A custom ImageView class that allows support for remote image loading
 */
public class XUIImageView extends ImageView
{
	private Context mContext;
	
	/**
	 * Default constructor
	 * @param context
	 */
	public XUIImageView(Context context)
	{
		super(context);
		mContext = context;
	}
	
	/**
	 * Default constructor
	 * @param context
	 * @param attrs
	 */
	public XUIImageView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		mContext = context;
	}
	
	/**
	 * Sets the image to a resource located on the internet
	 * @param url The Uri to the image
	 */
	public void setImage(Uri url)
	{
		setImage(url.toString());
	}
	
	/**
	 * Sets the image to a resource located on the internet
	 * @param url The URL to the image
	 */
	public void setImage(String url)
	{	
		final ScaleType scale = getScaleType();
						
		final AnimationDrawable loader = new AnimationDrawable();
		loader.addFrame(getResources().getDrawable(R.drawable.image_loading_1), 50);
		loader.addFrame(getResources().getDrawable(R.drawable.image_loading_2), 50);
		loader.addFrame(getResources().getDrawable(R.drawable.image_loading_3), 50);
		loader.addFrame(getResources().getDrawable(R.drawable.image_loading_4), 50);
		loader.addFrame(getResources().getDrawable(R.drawable.image_loading_5), 50);
		loader.addFrame(getResources().getDrawable(R.drawable.image_loading_6), 50);
		loader.addFrame(getResources().getDrawable(R.drawable.image_loading_7), 50);
		loader.addFrame(getResources().getDrawable(R.drawable.image_loading_8), 50);	
		
		setImageResource(R.drawable.image_loading_1);						
		setScaleType(ScaleType.CENTER_INSIDE);
		
		final Handler h = new Handler();
		final Runnable r = new Runnable()
		{			
			int index = 0;
			public void run()
			{
				index = index > 7 ? 0 : index;
				setImageDrawable(loader.getFrame(index++));
				h.postDelayed(this, 100);
			}
		};
		h.postDelayed(r, 100);
				
		AsyncHttpClient downloader = new AsyncHttpClient();
		downloader.getImage(url, new AsyncHttpResponse()
		{
			@Override
			public void onSuccess(Object response)
			{				
				h.removeCallbacks(r);
				
				Bitmap b = (Bitmap)response;
				setScaleType(scale);
				setImageBitmap(b);
			}
			
			@Override
			public void onFailure(int responseCode, String responseMessage)
			{
				Debug.out(responseCode + " " + responseMessage);
			}
		});
	}
}
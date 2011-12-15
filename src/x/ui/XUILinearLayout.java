package x.ui;

import java.util.jar.Attributes;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

/**
 * Extention to the standard LinearLayout but adds a few useful methods such as child spacing
 */
public class XUILinearLayout extends LinearLayout
{
	private Context mContext;
	private int mSpacing = 0;
	
	/**
	 * Default constructor
	 * @param context
	 */
	public XUILinearLayout(Context context)
	{
		super(context);
		mContext = context;
	}
	
	/**
	 * Default constructor
	 * @param context
	 * @param attrs
	 */
	public XUILinearLayout(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		mContext = context;
		
		TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.XUILinearLayout);	
		mSpacing = attributes.getDimensionPixelSize(R.styleable.XUILinearLayout_childSpacing, 0);
		
		attributes.recycle();
	}		
	
	/**
	 * Sets the child spacing in the layout
	 * @param newSpacing The new spacing in px
	 */
	public void setChildSpacing(int newSpacing)
	{
		mSpacing = newSpacing;
		
		requestLayout();
	}
	
	/**
	 * Gets the child spacing in the layout
	 * @return The child spacing in px
	 */
	public int getChildSpacing()
	{
		return mSpacing;
	}
	
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b)
	{	
		super.onLayout(changed, l, t, r, b);
		
		if (changed)
		{
			int childCount = getChildCount();
			int orientation = getOrientation();
			for (int index = 1; index < childCount; index++)
			{
				View child = getChildAt(index);
				
				switch (orientation)
				{
					//	Margin goes left
					case LinearLayout.HORIZONTAL:
					{
						((LinearLayout.LayoutParams)child.getLayoutParams()).leftMargin += mSpacing;					
					}
					
					//	Margin goes top
					case LinearLayout.VERTICAL:
					{						
						((LinearLayout.LayoutParams)child.getLayoutParams()).topMargin += mSpacing;
					}
				}
			}
			
			requestLayout();
		}
	}
}

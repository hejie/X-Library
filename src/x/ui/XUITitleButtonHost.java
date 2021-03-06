/**
 * @brief x ui is the library which includes the commonly used views in 3 Sided Cube Android applications
 * 
 * @author Callum Taylor
**/
package x.ui;

import java.util.Collection;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/**
 * @brief The view container for XUITitleButton
 * 
 * Use in XML
 * @code
 * <x.ui.XUITitleButtonHost
 *     android:layout_width="wrap_content"
 *     android:layout_height="wrap_content"
 *     android:id="@+id/button_container"
 * />
 * @endcode
 * 
 * Adding buttons in code
 * @code
 * XUITitleButtonHost bHost = (XUITitleButtonHost)findViewById(R.id.button_container);
 * bHost.addButton(new XUITitleButton(this, R.drawable.button_compose));
 * bHost.addButton(new XUITitleButton(this, R.drawable.button_search));
 * @endcode
 */
public class XUITitleButtonHost extends LinearLayout
{
	private int totalChildren = 0;
	
	/**
	 * Default Constructor
	 * @param context The application's context
	 */
	public XUITitleButtonHost(Context context)
	{
		super(context);
	}
	
	/**
	 * Default constructor
	 * @param context The context of the application/activity
	 * @param attrs The attribute set gathered from the XML
	 */
	public XUITitleButtonHost(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}
	
	/**
	 * Adds a button to the container
	 * @param button The button to add
	 */
	public void addButton(XUITitleButton button)
	{ 
		this.addView(button);
		
		totalChildren = this.getChildCount();
	}
	
	/**
	 * Adds an array of buttons to the container
	 * @param button The buttons to add 
	 */
	public void addButtons(Collection<XUITitleButton> button)
	{
		addButtons((XUITitleButton[])button.toArray(new XUITitleButton[button.size()]));
	}
	
	/**
	 * Adds an array of buttons to the container
	 * @param button The buttons to add 
	 */
	public void addButtons(XUITitleButton... button)
	{
		for (XUITitleButton b : button)
		{
			if (b != null)
			{
				this.addView(b);
			}
		}
		
		totalChildren = this.getChildCount();
	}
	
	/**
	 * Removes all buttons and puts the new set of buttons in
	 * @param button The buttons to set
	 */
	public void setButtons(XUITitleButton... button)
	{
		this.removeAllViews();
		
		for (XUITitleButton b : button)
		{
			if (b != null)
			{
				this.addView(b);
			}
		}
		
		totalChildren = this.getChildCount();
	}
	
	/**
	 * Removes a button at a certain index
	 * @param index The index of the unwanted button
	 */
	public void removeButton(int index)
	{
		this.removeViewAt(index);
	}
	
	/**
	 * Removes a certain button
	 * @param button The button reference to remove
	 */
	public void removeButton(XUITitleButton button)
	{
		this.removeView(button);
	}
	
	/**
	 * Removes all the buttons from the view
	 */
	public void removeAllButtons()
	{
		this.removeAllViews();
	}

	/**
	 * Is called when the view is being layed out
	 * @param changed If the view has changed or not
	 * @param l The left coordinate
	 * @param t The top coordinate
	 * @param r The right coordinate
	 * @param b The bottom coordinate
	 */
	@Override protected void onLayout(boolean changed, int l, int t, int r, int b)
	{	
		super.onLayout(changed, l, t, r, b);
	}
}

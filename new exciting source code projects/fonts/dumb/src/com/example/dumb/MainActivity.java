package com.example.dumb;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.widget.TextView;

public class MainActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		final TextView textView = (TextView) findViewById(R.id.textView1);
		textView.setBackgroundResource(R.drawable.variantoption_mail_to);
		
		
		textView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Animation fadeOut = new AlphaAnimation(1, 0);
				fadeOut.setInterpolator(new AccelerateInterpolator()); //and this
				fadeOut.setStartOffset(1000);
				fadeOut.setDuration(1000);
				
				AnimationSet animation = new AnimationSet(false); //change to false
				
				animation.addAnimation(fadeOut);
				arg0.setAnimation(animation);
				arg0.startAnimation(animation);
				
			}
		});
		
		
//		
//		LinearLayout ll = (LinearLayout) findViewById(R.id.buy_bottom_screen);
//		ll.setBackgroundResource(R.drawable.variantoption_mail_to);
		
//		ArrayList<Integer> dumbIntList = new ArrayList<Integer>();
//		dumbIntList.
		
//	    CustomFontTextView textView = (CustomFontTextView) findViewById(R.id.textView1);
//	    String value = "heavy metal rules";
//	    textView.setTypefaceOfCustomFont(Typeface.defaultFromStyle(Typeface.BOLD));
	   
		
		// textView.setTypeface(Typeface.DEFAULT_BOLD);
	    //savedInstanceStatetext.setText(value);
	    //text.setMovementMethod(LinkMovementMethod.getInstance());
	    //Linkify.addLinks(text, Linkify.ALL);

	}

}
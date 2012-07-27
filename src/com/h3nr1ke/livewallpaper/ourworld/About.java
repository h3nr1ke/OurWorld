package com.h3nr1ke.livewallpaper.ourworld;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;

public class About extends Activity implements OnClickListener {

	private TextView textView;
	private Button button;
	private AdView adView;

	public void onClick(View v) {
		if (v.getId() == R.id.about_ok) {
			this.finish();
		}

	}

	public void onDestroy() {
		// Destroy the AdView.
		adView.destroy();

		super.onDestroy();
	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);

		// do some replaces
		String desc = getString(R.string.about_description);
		desc = desc.replace("@app_name@", getString(R.string.app_name));
		desc = desc.replace("@apps@",
				"<a href=\"market://search?q=pub:h3nr1ke\">h3nr1ke</a>");
		desc = desc.replace("\n", "<br />");
		desc = desc.replace("@apps_art@",
				"<a href=\"http://www.bazaardesigns.com\">www.bazaardesigns.com</a>");

		String title = getString(R.string.about_category);

		title = title.replace("@app_name@", getString(R.string.app_name));

		button = (Button) findViewById(R.id.about_ok);
		button.setOnClickListener(this);

		textView = (TextView) findViewById(R.id.about_view);
		textView.setText(Html.fromHtml(desc));
		textView.setMovementMethod(LinkMovementMethod.getInstance());

		// include the ad
		LinearLayout adContainer = (LinearLayout) findViewById(R.id.adViewLayout);
		// Create the adView
		adView = new AdView(this, AdSize.BANNER, Constantes.AD_ID);

		// include the ad inside a linear layout in the main.xml
		adContainer.addView(adView);

		// create the ad request conf
		AdRequest request = new AdRequest();

		//request.setTesting(true);

		// load the ad
		adView.loadAd(request);
	}
}
package com.h3nr1ke.livewallpaper.ourworld;

import java.util.Map;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;

public class Settings extends PreferenceActivity implements
		SharedPreferences.OnSharedPreferenceChangeListener {

	private AdView adView;
	
	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.adlayout); //set the contentview. On the layout, you need a listview with the id: @android:id/list

		//include the ad
		if (Constantes.ADD_AD) {
			// include the ad
			LinearLayout adContainer = (LinearLayout) findViewById(R.id.ad_view);
			// Create the adView
			adView = new AdView(this, AdSize.BANNER, Constantes.AD_ID);

			// include the ad inside a linear layout in the main.xml
			adContainer.addView(adView);

			// create the ad request conf
			AdRequest request = new AdRequest();
			if (Constantes.AD_TEST) {
				for (String td : Constantes.AD_TEST_DEVICE) {
					request.addTestDevice(td);
				}

				request.setTesting(true);
			}

			// load the ad
			adView.loadAd(request);
		}
		
		addPreferencesFromResource(R.xml.wallpaper_settings);
		
		populatePreferencesDesc();
		
		getPreferenceManager().getSharedPreferences()
				.registerOnSharedPreferenceChangeListener(this);
		getPreferenceManager().setSharedPreferencesName(OurWorld.SHARED_PREFS_NAME);
		


	}
	public void tst(String str) {
		Toast.makeText(Settings.this, str, Toast.LENGTH_LONG).show();
	}
	
	public void populatePreferencesDesc(){
		// Set up initial values for all list preferences
		Map<String, ?> sharedPreferencesMap = getPreferenceScreen()
				.getSharedPreferences().getAll();
		Preference pref;
		ListPreference listPref;
		for (Map.Entry<String, ?> entry : sharedPreferencesMap.entrySet()) {
			pref = findPreference(entry.getKey());
			if (pref instanceof ListPreference) {
				listPref = (ListPreference) pref;
				//tst(listPref.getEntry().toString());
				CharSequence[] mPositions = listPref.getEntries();
				int index = Integer.valueOf(listPref.getPreferenceManager()
						.getSharedPreferences().getString(entry.getKey(), "1"));
				
				if((entry.getKey() == "horizontal_option") && (index < 10)){
					index = 20;
				}
				((ListPreference) pref).setValue(index+"");
				
				if (index >=10){
					index = index /10;
				}
				pref.setSummary(mPositions[index - 1]);

			} else if (pref instanceof SeekBarDialogPreference) {
				SeekBarDialogPreference seek = (SeekBarDialogPreference) pref;
				pref.setSummary(String.valueOf(seek.getPreferenceManager()
						.getSharedPreferences().getInt(entry.getKey(), 1)) + "x");
			}
		}

	};

	@Override
	protected void onResume() {
		super.onResume();
		populatePreferencesDesc();
		// Set up a listener whenever a key changes
		getPreferenceScreen().getSharedPreferences()
				.registerOnSharedPreferenceChangeListener(this);

	}

	@Override
	protected void onDestroy() {
		getPreferenceManager().getSharedPreferences()
				.unregisterOnSharedPreferenceChangeListener(this);
		super.onDestroy();
	}

	// @Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

		Preference pref = findPreference(key);

		if (pref instanceof ListPreference) {
			ListPreference listPref = (ListPreference) pref;
			pref.setSummary(listPref.getEntry());
		} else if (pref instanceof SeekBarDialogPreference) {
			pref.setSummary(String.valueOf(sharedPreferences.getInt(key, 1))+"x");
		}

	}

}

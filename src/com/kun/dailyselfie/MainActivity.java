package com.kun.dailyselfie;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;

import com.kun.dailyselfie.OnePictureFragment.EmptyListListener;
import com.kun.dailyselfie.PicturesFragment.ListSelectionListener;

public class MainActivity extends Activity implements ListSelectionListener, EmptyListListener {
	private static final String CURRENT_PIC_KEY = "CURRENT_PIC_ID";
	private static final String IS_ONEPICTURE_FRAGMENT_KEY = "IS_ONEPICTURE_FRAGMENT";
	
	private static final long INITIAL_ALARM_DELAY = 1 * 20 * 1000L;
	protected static final long JITTER = 5000L;
	
	private FragmentManager mFragmentManager;
	
	private PicturesFragment picturesFragment = new PicturesFragment();
	private OnePictureFragment onePicFragment = new OnePictureFragment();
	
	//to handle the configuration change
	private int mCurrentPicId=0;
	private boolean mIsOnePictureFragment = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// Get a reference to the FragmentManager
		mFragmentManager = getFragmentManager();

		// Start a new FragmentTransaction
		FragmentTransaction fragmentTransaction = mFragmentManager
				.beginTransaction();

		//if configuration has been changed
		if (savedInstanceState != null){
//			mIsOnePictureFragment = savedInstanceState.getBoolean(IS_ONEPICTURE_FRAGMENT_KEY);
//			mCurrentPicId = savedInstanceState.getInt(CURRENT_PIC_KEY);
//			
//			if (mIsOnePictureFragment && !onePicFragment.isAdded()){
//				fragmentTransaction.add(R.id.fragment_container, onePicFragment);				
//			}
			
		}else{		
			// Add the fragment of pictures to the layout
			if (!onePicFragment.isAdded() && !picturesFragment.isAdded()){
				fragmentTransaction.add(R.id.fragment_container, picturesFragment);
			}
		}
		
		// Commit the FragmentTransaction
		fragmentTransaction.commit();
		
		AlarmManager mAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
		Intent mNotificationIntent = new Intent(this,AlarmNotificationReceiver.class);
		PendingIntent mNotificationPendingIntent = PendingIntent.getBroadcast(this, 0, mNotificationIntent, 0);
		
		mAlarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
				System.currentTimeMillis() + INITIAL_ALARM_DELAY, INITIAL_ALARM_DELAY,
				mNotificationPendingIntent);
		
	}
	
	@Override
	protected void onStart() {		
		super.onStart();
		if (mIsOnePictureFragment){
			onePicFragment.showImage(mCurrentPicId);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onListSelection(int imgPathId) {
		
		if (!onePicFragment.isAdded()) {
			
			// Start a new FragmentTransaction
			FragmentTransaction fragmentTransaction = mFragmentManager
					.beginTransaction();
			
			//if (picturesFragment.isAdded()){
				fragmentTransaction.remove(picturesFragment);
			//}
			
			// Add the OnePictureFragment to the layout
			fragmentTransaction.add(R.id.fragment_container, onePicFragment);
			
			// Add this FragmentTransaction to the backstack
			fragmentTransaction.addToBackStack(null);
			
			// Commit the FragmentTransaction
			fragmentTransaction.commit();
			
			// Force Android to execute the committed FragmentTransaction
			mFragmentManager.executePendingTransactions();
		}
		onePicFragment.showImage(imgPathId);
		mCurrentPicId = imgPathId;
		mIsOnePictureFragment = true;
	}
	
	@Override
	public void removeFragment(Fragment oneFragment){
		
		if (oneFragment.isAdded()) {
			
			// Start a new FragmentTransaction
			FragmentTransaction fragmentTransaction = mFragmentManager
					.beginTransaction();			
			
			fragmentTransaction.remove(oneFragment);			
			
			// Add the main fragment, always available
			fragmentTransaction.add(R.id.fragment_container, picturesFragment);
			
			// Add this FragmentTransaction to the backstack
			fragmentTransaction.addToBackStack(null);
			
			// Commit the FragmentTransaction
			fragmentTransaction.commit();
			
			// Force Android to execute the committed FragmentTransaction
			mFragmentManager.executePendingTransactions();
			
			mIsOnePictureFragment = false;
		}
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		
		outState.putInt(CURRENT_PIC_KEY, mCurrentPicId);
		outState.putBoolean(IS_ONEPICTURE_FRAGMENT_KEY, mIsOnePictureFragment);
		
	}
	

}

package com.nutomic.zertman.test;

import android.content.pm.ActivityInfo;
import android.test.ActivityInstrumentationTestCase2;
import android.test.ActivityUnitTestCase;
import android.test.suitebuilder.annotation.SmallTest;
import android.util.Log;

import com.nutomic.zertman.MainActivity;

public class MainActivityTest extends ActivityInstrumentationTestCase2<MainActivity> {

	private static final String TAG = "MainActivityTest";

	private MainActivity mActivity;

	public MainActivityTest() {
		super(MainActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		setActivityInitialTouchMode(false);

		mActivity = getActivity();
		getInstrumentation().waitForIdleSync();
	}

	@SmallTest
	public void testOrientation() {
		try {
			mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			Thread.sleep(50);
			assertTrue(true);
			mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			Thread.sleep(50);
			assertTrue(true);
		}
		catch (InterruptedException e) {
			Log.w(TAG, "Sleep failed", e);
		}
	}

}

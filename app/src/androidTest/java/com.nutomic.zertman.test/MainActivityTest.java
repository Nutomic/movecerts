package com.nutomic.zertman.test;

import android.test.ActivityUnitTestCase;
import android.test.suitebuilder.annotation.SmallTest;

import com.nutomic.zertman.MainActivity;

public class MainActivityTest extends ActivityUnitTestCase<MainActivity> {

	public MainActivityTest() {
		super(MainActivity.class);
	}

	@SmallTest
	public void testBlah() {
		// TODO: test for showing ListView with all cert files
	}

}
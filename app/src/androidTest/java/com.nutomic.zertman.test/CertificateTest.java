package com.nutomic.zertman.test;

import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.MediumTest;
import android.test.suitebuilder.annotation.SmallTest;

import com.nutomic.zertman.Certificate;

public class CertificateTest extends AndroidTestCase {

	Certificate a1 = new Certificate("a", true);
	Certificate a2 = new Certificate("a", false);
	Certificate b1 = new Certificate("b", true);
	Certificate b2 = new Certificate("b", false);

	@SmallTest
	public void testEquals() {
		assertNotSame(a1, a2);
		assertNotSame(a2, b1);
		assertNotSame(a1, b1);
		assertNotSame(a2, b2);
	}

	@SmallTest
	public void testGetFile() {
		assertNotNull(a1.getFile());
		assertNotNull(a2.getFile());
	}
}
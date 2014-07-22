package com.nutomic.zertman.test;

import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.MediumTest;
import android.test.suitebuilder.annotation.SmallTest;

import com.nutomic.zertman.Certificate;
import com.nutomic.zertman.MovedCertificatesStorage;

import java.util.List;

/**
 * NOTE: This test works on the app's actual data.
 */
public class MovedCertificatesStorageTest extends AndroidTestCase {

	private MovedCertificatesStorage mMovedCertificatesStorage;

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		mMovedCertificatesStorage = new MovedCertificatesStorage(getContext());
		assertTrue(mMovedCertificatesStorage.list().isEmpty());
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();

		mMovedCertificatesStorage.close();
	}

	@SmallTest
	public void testInvalidDelete() {
		assertFalse(mMovedCertificatesStorage.delete(new Certificate("cert", true)));
	}

	@SmallTest
	public void testDuplicateInsert() {
		Certificate c = new Certificate("dupe", true);
		assertTrue(mMovedCertificatesStorage.insert(c));
		assertFalse(mMovedCertificatesStorage.insert(c));
		assertTrue(mMovedCertificatesStorage.delete(c));
	}

	@MediumTest
	public void testStorage() {
		Certificate c1 = new Certificate("first", true);
		Certificate c2 = new Certificate("second", true);
		assertTrue(mMovedCertificatesStorage.insert(c1));
		assertTrue(mMovedCertificatesStorage.insert(c2));
		List<Certificate> list = mMovedCertificatesStorage.list();
		assertEquals(list.get(0), c1);
		assertEquals(list.get(1), c2);
		assertTrue(mMovedCertificatesStorage.delete(c1));
		assertTrue(mMovedCertificatesStorage.delete(c2));
		assertTrue(mMovedCertificatesStorage.list().isEmpty());
	}

}

package com.nutomic.zertman.test;

import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.SmallTest;
import android.widget.TextView;

import com.nutomic.zertman.Certificate;
import com.nutomic.zertman.CertificateAdapter;

import java.util.ArrayList;

public class CertificateAdapterTest extends AndroidTestCase {

	private CertificateAdapter mCertificateAdapter;

	private ArrayList<Certificate> mTestCertificates = new ArrayList<Certificate>();

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		mCertificateAdapter = new CertificateAdapter(getContext());
		mTestCertificates.add(new Certificate("first", false));
		mTestCertificates.add(new Certificate("second", true));
		mCertificateAdapter.addAll(mTestCertificates);
	}

	@SmallTest
	public void testAdapter() {
		for (int i = 0; i < mTestCertificates.size(); i++) {
			TextView tv1 = (TextView) mCertificateAdapter.getView(i, null, null)
					.findViewById(android.R.id.text1);
			assertTrue(tv1.getText().equals(mTestCertificates.get(i).getFile().getName()));
		}
	}

	@SmallTest
	public void testColors() {
		assertNotSame(mCertificateAdapter.getView(0, null, null).getBackground(),
				mCertificateAdapter.getView(1, null, null).getBackground());
	}

}

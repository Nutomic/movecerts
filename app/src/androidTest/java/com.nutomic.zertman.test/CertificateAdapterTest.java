package com.nutomic.zertman.test;

import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.SmallTest;
import android.widget.TextView;

import com.nutomic.zertman.*;
import com.nutomic.zertman.R;

import java.util.ArrayList;

public class CertificateAdapterTest extends AndroidTestCase {

	private CertificateAdapter mCertificateAdapter;

	private ArrayList<Certificate> mTestCertificates = new ArrayList<Certificate>();

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		CertificateManager cm = new CertificateManager();
		MovedCertificatesStorage mv = new MovedCertificatesStorage(getContext());
		mCertificateAdapter = new CertificateAdapter(getContext(), cm, mv);
		mTestCertificates.add(new Certificate("first", false));
		mTestCertificates.add(new Certificate("second", true));
		mCertificateAdapter.addAll(mTestCertificates);
	}

	@SmallTest
	public void testAdapter() {
		for (int i = 0; i < mTestCertificates.size(); i++) {
			TextView tv1 = (TextView) mCertificateAdapter.getView(i, null, null)
					.findViewById(R.id.title);
			assertTrue(tv1.getText().equals(mTestCertificates.get(i).getFile().getName()));
		}
	}

}

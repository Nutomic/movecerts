package com.nutomic.zertman;

import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ListView;


public class MainActivity extends ListActivity {

	private ListView mListView;

	private CertificateAdapter mCertificateAdapter;

	private CertificateManager mCertificateManager;

	/**
	 * Sets up ListView showing all certificates.
	 */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

	    mListView = getListView();
	    mCertificateAdapter = new CertificateAdapter(this);
	    mCertificateManager = new CertificateManager();
	    mCertificateAdapter.addAll(mCertificateManager.getCertificates(false));
	    mCertificateAdapter.addAll(mCertificateManager.getCertificates(true));
	    mListView.setAdapter(mCertificateAdapter);
    }

}

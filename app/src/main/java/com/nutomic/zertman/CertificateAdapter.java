package com.nutomic.zertman;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

/**
 * List Adapter for {@link Certificate}.
 */
public class CertificateAdapter extends ArrayAdapter<Certificate> {

	private CertificateManager mCertificateManager;

	private MovedCertificatesStorage mMovedCertificatesStorage;

	public CertificateAdapter(Context context, CertificateManager certificateManager,
	                          MovedCertificatesStorage movedCertificatesStorage) {
		super(context, 0);
		mCertificateManager = certificateManager;
		mMovedCertificatesStorage = movedCertificatesStorage;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) getContext()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.certificate_list_item, parent, false);
		}

		final Certificate cert = getItem(position);
		TextView title = (TextView) convertView.findViewById(R.id.title);
		title.setText(cert.getFile().getName());
		Button button = (Button) convertView.findViewById(R.id.button);
		int colorRes;
		if (cert.isSystemCertificate()) {
			button.setText(R.string.delete);
			button.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					mCertificateManager.deleteCertificate(cert);
					mMovedCertificatesStorage.delete(cert);
				}
			});
			colorRes = R.color.background_system_certificate;
		}
		else {
			button.setText(R.string.move_to_system);
			button.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					mCertificateManager.moveCertificateToSystem(cert);
					mMovedCertificatesStorage.insert(cert);
				}
			});
			colorRes = R.color.background_user_certificate;
		}
		convertView.setBackgroundColor(getContext().getResources().getColor(colorRes));
		return convertView;
	}

}
